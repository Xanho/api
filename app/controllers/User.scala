package controllers

import models.Users
import play.api.http.ContentTypes
import play.api.libs.json.Json
import play.api.mvc._
import system.helpers.{ResponseHelpers, Authorized}

class User extends Controller {

  def create =
    Authorized(None, Set(Users.canCreate), Set(ContentTypes.JSON)) {
      request =>
        Users.create(request.data)
          .fold(ResponseHelpers.)
    }

}
