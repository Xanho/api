package controllers

import controllers.helpers.CRUDController
import models.Users

class User extends CRUDController {

  /**
    * @inheritdoc
    */
  val resourceCollection =
    Users

  /**
    * Authenticates a user, and if successful, responds with a JWT Token
    */
  def login =
    ???

}
