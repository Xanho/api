package system.helpers

import java.util.UUID

import _root_.play.api.http.ContentTypes
import _root_.play.api.libs.json._
import _root_.play.api.mvc.Codec._
import _root_.play.api.mvc.Results._
import _root_.play.api.mvc._
import models.User
import pdi.jwt._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
  * Represents a [[Request]] that contains an optional user ID and the data of the request
  * @param userId @see [[User.id]]
  * @param request The base request
  * @param data A combination of all of the path, query, body, form, and header parameters
  * @tparam A @see [[Request#A]]
  */
case class ParsedRequest[A](userId: Option[UUID],
                            request: Request[A],
                            data: JsObject) extends WrappedRequest[A](request)

trait Secured {

  def Authorized(resourceId: Option[UUID],
                 authorizers: Set[(Option[UUID], Option[UUID], JsObject) => Boolean] = Set(),
                 acceptedContentTypes: Set[String] = Set(),
                 pathParameters: Map[String, JsValue] = Map()) =
    AuthorizedAction(resourceId, authorizers, pathParameters)

}

/**
  * Represents an [[Action]] that requires [[Resource]] authorization
  * @param resourceId @see [[Resource.id]]
  * @param authorizers A set of functions which determine if the user can access the resource
  * @param pathParameters Any additional path parameters for the request
  */
case class AuthorizedAction(resourceId: Option[UUID],
                          authorizers: Set[(Option[UUID], Option[UUID], JsObject) => Boolean] = Set(),
                          pathParameters: Map[String, JsValue] = Map()) extends ActionBuilder[ParsedRequest] {

  import JwtPlayImplicits._


  /**
    *
    * @inheritdoc
    * Ensures that for all of the [[authorizers]], the (optional) user is authorized to access the [[resourceId]].
    * If so, continues on with a [[ParsedRequest]] and refreshed the JWT Session Token
    * @param request The incoming request
    * @param block Transformer from the request to a result
    * @tparam A @see [[Request#A]]
    * @return A [[Future]]: [[Unauthorized]] or [[InternalServerError]] or [[]]
    */
  def invokeBlock[A](request: Request[A],
                     block: ParsedRequest[A] => Future[Result]) = {
    val userId: Option[UUID] =
      request.jwtSession.getAs[JsObject]("user")
        .flatMap((u: JsObject) => (u \ "id").asOpt[UUID])

    val data =
      (Try(parseBody(request.body.asInstanceOf[JsObject])) match {
        case Success(b) =>
          b
        case _ =>
          Try(parseBody(request.body.asInstanceOf[Map[String, Seq[String]]])) match {
            case Success(m) =>
              m
            case _ =>
              Json.obj()
          }
      }) ++
        JsObject(request.queryString map (kv => kv._1 -> Json.toJson(kv._2.mkString))) ++
        JsObject(pathParameters) ++
        JsObject(request.headers.toMap.map(kv => kv._1 -> Json.toJson(kv._2.mkString))) ++
        Json.obj("userId" -> userId)

    Try(authorizers forall (authorizer => authorizer(userId, resourceId, data))) match {
      case Success(true) =>
        block(ParsedRequest(userId, request, data))
          .map(_.refreshJwtSession(request))
      case Success(false) =>
        Future.successful(Unauthorized(ResponseHelpers.message("You are not authorized to access this resource.")))
      case Failure(error) =>
        Future.successful(InternalServerError(ResponseHelpers.message("Something broke.")))
    }

  }

  def parseBody(body: JsValue) =
    body match {
      case j: JsObject =>
        j
      case _ =>
        Json.obj()
    }

  def parseBody(body: Map[String, Seq[String]]) =
    JsObject(body.map(kv => kv._1 -> Json.toJson(kv._2)))

}

object JwtPlayImplicits extends JwtJsonImplicits with JwtPlayImplicits
