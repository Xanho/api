package system.helpers

import java.util.UUID

import models.User
import play.api.http.ContentTypes
import play.api.libs.json._
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
  * Represents a [[Request]] that contains an optional user ID and the data of the request
  * @param userId @see [[User.id]]
  * @param request The base request
  * @param data A combination of all of the path, query, body, form, and header parameters
  * @tparam A @see [[Request#A]]
  */
case class RichRequest[A](userId: Option[UUID],
                          request: Request[A],
                          data: JsObject) extends WrappedRequest[A](request)

/**
  * Represents an [[Action]] that requires [[Resource]] authorization
  * @param resourceId @see [[Resource.id]]
  * @param authorizers A set of functions which determine if the user can access the resource
  * @param acceptedContentTypes A set of [[ContentTypes]] that the particular route accepts
  * @param pathParameters Any additional path parameters for the request
  */
case class Authorized(resourceId: Option[UUID],
                      authorizers: Set[(Option[UUID], Option[UUID], JsObject) => Boolean],
                      acceptedContentTypes: Set[String] = Set(),
                      pathParameters: Map[String, Any] = Map())(block: RichRequest => Result) extends ActionBuilder[Request] {

  /**
    *
    * @inheritdoc
    * Ensures that for all of the [[authorizers]], the (optional) is authorized to access the [[resourceId]].
    * If so, continues on with a [[RichRequest]] and refreshed the JWT Session Token
    * @param request The incoming request
    * @param block Transformer from the request to a result
    * @tparam A @see [[Request#A]]
    * @return A [[Future]] [[Result]]
    */
  def invokeBlock[A](request: Request[A],
                     block: Request[A] => Future[Result]): Future[Result] = {
    val userId: Option[UUID] =
      request.jwtSession.getAs[JsObject]("user")
        .flatMap((u: JsObject) => (u \ "id").asOpt[UUID])

    val data =
      parseBody(request.body) ++
        JsObject(request.queryString map (kv => kv._1 -> Json.toJson(kv._2.mkString))) ++
        JsObject(pathParameters map (kv => kv._1 -> Json.toJson(kv._2))) ++
        JsObject(request.headers.toMap.map(kv => kv._1 -> Json.toJson(kv._2.mkString)))

    Try(authorizers forall (authorizer => authorizer(userId, resourceId, data))) match {
      case Success(true) =>
        block(RichRequest(userId, request, data))
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
      case ContentTypes.FORM =>
        Try(JsObject(body.asInstanceOf[Map[String, String]].map(kv => kv._1 -> Json.toJson(kv._2))))
          .getOrElse(Json.obj())
      case ContentTypes.JSON =>
        Try(body.asInstanceOf[JsObject])
          .getOrElse(Json.obj())
      case _ =>
        Json.obj()
    }).fold(Json.obj())(_ deepMerge _)

}
