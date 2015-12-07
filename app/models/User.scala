package models

import java.util.UUID

import com.github.t3hnar.bcrypt._
import models.Helpers.Columns
import slick.driver.MySQLDriver.api._

/**
  * A Xanho User/Member
  * @param id The user's ID
  * @param firstName The user's first name
  * @param lastName The user's last name
  * @param email The user's email address
  * @param boxcode The user's boxcode
  */
case class User(id: UUID,
                firstName: String,
                lastName: String,
                email: String,
                boxcode: String)

object UserHelper {

  /**
    * Hashes a given raw boxcode using bcrypt
    * @param rawBoxcode The raw, plain-text boxcode
    * @return A bcrypt/hashed boxcode
    */
  def hashBoxcode(rawBoxcode: String): String =
    rawBoxcode.bcrypt
}

/**
  * A [[slick.profile.RelationalTableComponent.Table]] for [[User]]s
  * @param tag @see [[slick.lifted.Tag]]
  */
class Users(tag: Tag)
  extends Table[User](tag, "users")
  with Columns.Id[User] {

  /**
    * @see [[User.id]]
    */
  def firstName =
    column[String]("first_name")

  /**
    * @see [[User.id]]
    */
  def lastName =
    column[String]("last_name")

  /**
    * @see [[User.id]]
    */
  def email =
    column[String]("email")

  /**
    * @see [[User.id]]
    */
  def boxcode =
    column[String]("boxcode")

  /**
    * @see [[slick.profile.RelationalTableComponent.Table.*]]
    */
  def * =
    (id, firstName, lastName, email, boxcode) <>(User.tupled, User.unapply)

}