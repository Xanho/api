package controllers

import _root_.play.api.libs.json.Json
import _root_.play.api.mvc._

class Application extends Controller {

  def index =
    Action {
      Ok(Json.obj())
    }

}
