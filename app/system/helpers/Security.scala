package system.helpers

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._

import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.json._

import pdi.jwt._

import models.{User, UserModel}
import models.UserModel._

/**
 * A class which wraps a request with a [[User]] object
 * @param user The [[User]] making the [[Request]]
 * @param request The [[Request]] being made
 * @tparam A
 */
class AuthenticatedRequest[A](val user: User,
                              request: Request[A]) extends WrappedRequest[A](request)

/**
 * Provides an access layer to [[AuthenticatedAction]] and [[AdminAction]]
 */
trait Secured {
  def Public = PublicAction
  def Authenticated = AuthenticatedAction
  def Admin = AdminAction
}



object PublicAction extends ActionBuilder[Request] {
  /**
   * @inheritdoc
   */
  def invokeBlock[A](request: Request[A],
                     block: Request[A] => Future[Result]) = {
    request.jwtSession.getAs[JsObject]("user")
      .flatMap((u: JsObject) => (u \ "id").asOpt[Int]) match {
      case Some(id: Int) =>
        UserModel.get(id) match {
          case Some(user: User) =>
            block(new AuthenticatedRequest(user, request)).map(_.refreshJwtSession(request))
          case _ =>
            block(request)
        }
      case _ =>
        block(request)
    }
  }

}

/**
 * An Action which retrieves the user from a [[JwtSession]], authenticates the action,
 * and if successful, completes the action
 */
object AuthenticatedAction extends ActionBuilder[AuthenticatedRequest] {
  /**
   * @inheritdoc
   */
  def invokeBlock[A](request: Request[A],
                     block: AuthenticatedRequest[A] => Future[Result]) = {
    request.jwtSession.getAs[JsObject]("user")
      .flatMap((u: JsObject) => (u \ "id").asOpt[Int]) match {
      case Some(id: Int) =>
        UserModel.get(id) match {
          case Some(user: User) =>
            block(new AuthenticatedRequest(user, request)).map(_.refreshJwtSession(request))
          case _ =>
            Future.successful(Unauthorized(ResponseHelpers.message("No user matched the claimed ID.")))
        }
      case _ =>
        Future.successful(Unauthorized(ResponseHelpers.message("No user was included in the token's claim.")))
    }
  }
}

/**
 * Similar to [[AuthenticatedAction]], except requires the user be an admin
 */
object AdminAction extends ActionBuilder[AuthenticatedRequest] {
  /**
   * @inheritdoc
   */
  def invokeBlock[A](request: Request[A],
                     block: AuthenticatedRequest[A] => Future[Result]) =
    request.jwtSession.getAs[JsObject]("user")
      .flatMap((u: JsObject) => (u \ "id").asOpt[Int]) match {
      case Some(id: Int) =>
        UserModel.get(id) match {
          case Some(user: User) if user.isAdmin =>
            block(new AuthenticatedRequest(user, request)).map(_.refreshJwtSession(request))
          case Some(User(_)) =>
            Future.successful(Forbidden(ResponseHelpers.message("You do not have authorization to complete this request")).refreshJwtSession(request))
          case _ =>
            Future.successful(Unauthorized(ResponseHelpers.message("No user matched the claimed ID.")))
        }
      case _ =>
        Future.successful(Unauthorized(ResponseHelpers.message("No user was included in the token's claim.")))
    }
}