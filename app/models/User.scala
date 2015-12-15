package models

import java.util.UUID

import com.github.t3hnar.bcrypt._
import models.Helpers.Columns
import _root_.play.api.libs.json.{JsObject, JsValue, Json, Writes}
import slick.driver.MySQLDriver.api._
import system.helpers.{Resource, PropertyValidators, ResourceCollection, SlickHelper}

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
                boxcode: String) extends Resource

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
    (id, firstName, lastName, email, boxcode).<>(User.tupled, User.unapply)

}

object Users extends ResourceCollection[Users, User] {

  /**
    * @inheritdoc
    */
  val tableQuery =
    TableQuery[Users]

  /**
    * @inheritdoc
    */
  implicit val writes: Writes[User] =
    new Writes[User] {
      def writes(o: User) =
        Json.obj(
          "id" -> o.id,
          "firstName" -> o.firstName,
          "lastName" -> o.lastName,
          "email" -> o.email
        )
    }

  /**
    * @inheritdoc
    */
  val validaters =
    Set(
      ("firstName", true, Set(PropertyValidators.name _)),
      ("lastName", true, Set(PropertyValidators.name _)),
      ("email", true, Set(PropertyValidators.email _)),
      ("password", true, Set(PropertyValidators.password _))
    )

  /**
    * @inheritdoc
    * @param uuid The UUID to use in the creation
    * @param arguments A map containing values to be updated
    * @return A new [[User]]
    */
  def creator(uuid: UUID,
              arguments: Map[String, JsValue]) =
    User(
      uuid,
      arguments("firstName").as[String],
      arguments("lastName").as[String],
      arguments("email").as[String],
      arguments("boxcode").as[String]
    )

  /**
    * @inheritdoc
    * @param row A [[User]]
    * @param arguments A map containing values to be updated
    * @return A new [[User]]
    */
  def updater(row: User,
              arguments: Map[String, JsValue]) =
    row.copy(
      row.id,
      arguments.get("firstName")
        .fold(row.firstName)(_.as[String]),
      arguments.get("lastName")
        .fold(row.firstName)(_.as[String]),
      arguments.get("email")
        .fold(row.firstName)(_.as[String]),
      arguments.get("boxcode")
        .fold(row.firstName)(_.as[String])
    )
  
  /**
    * @inheritdoc
    */
  def canRead(resourceId: Option[UUID],
              userId: Option[UUID],
              data: JsObject = Json.obj()): Boolean =
    userId
      .fold(false)(_ == resourceId)

  /**
    * @inheritdoc
    */
  def canDelete(resourceId: Option[UUID],
                userId: Option[UUID],
                data: JsObject = Json.obj()): Boolean =
    false

  /**
    * @inheritdoc
    */
  def canModify(resourceId: Option[UUID],
                userId: Option[UUID],
                data: JsObject = Json.obj()): Boolean =
    userId
      .fold(false)(_ == resourceId)

  /**
    * @inheritdoc
    */
  def canCreate(resourceId: Option[UUID] = None,
                userId: Option[UUID],
                data: JsObject = Json.obj()): Boolean =
    userId.isEmpty

}