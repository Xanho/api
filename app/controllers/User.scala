package controllers

import controllers.helpers.CRUDController
import models.Users

class User extends CRUDController[Users, User] {

  /**
    * @inheritdoc
    */
  val resourceCollection =
    Users

  /**
    * Authenticates a user, and if successful, responds with a JWT
    */
  def login =
    ???

}
