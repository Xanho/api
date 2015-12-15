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

/**
  * Represents an [[Action]] that requires [[Resource]] authorization
  * @param resourceId @see [[Resource.id]]
  * @param authorizers A set of functions which determine if the user can access the resource
  * @param acceptedContentTypes A set of [[ContentTypes]] that the particular route accepts
  * @param pathParameters Any additional path parameters for the request
  */
case class Authorized[A1](resourceId: Option[UUID],
                          authorizers: Set[(Option[UUID], Option[UUID], JsObject) => Boolean],
                          acceptedContentTypes: Set[String] = Set(),
                          pathParameters: Map[String, JsValue] = Map())(block: ParsedRequest[A1] => Result) extends ActionBuilder[ParsedRequest] {

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
      parseBody(request.body) ++
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

  /**
    * Parses a [[Request.body]] by attempting to use each of provided [[acceptedContentTypes]]
    * @param body @see [[Request.body]]
    * @return A [[JsObject]] created using the body
    */
  def parseBody(body: Any) =
    (acceptedContentTypes map {
      case x if x == ContentTypes.FORM =>
        Try(JsObject(body.asInstanceOf[Map[String, String]].map(kv => kv._1 -> Json.toJson(kv._2))))
          .getOrElse(Json.obj())
      case x if x == ContentTypes.JSON =>
        Try(body.asInstanceOf[JsObject])
          .getOrElse(Json.obj())
      case _ =>
        Json.obj()
    }).fold(Json.obj())(_ deepMerge _)

}

object JwtPlayImplicits extends JwtJsonImplicits with JwtPlayImplicits
