package models

import java.util.UUID

import com.github.t3hnar.bcrypt._
import models.Helpers.Columns
import play.api.libs.json.{JsValue, Writes, JsObject, Json}
import slick.driver.MySQLDriver.api._
import system.helpers.PropertyValidators.PropertyErrorCodes
import system.helpers.ResponseHelpers.PropertyErrorCodes
import system.helpers.{PropertyValidators, ResourceCollection, SlickHelper}

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
    (id, firstName, lastName, email, boxcode).<>(User.tupled, User.unapply)

}

object Users extends ResourceCollection[User, Users] {

  /**
    * @inheritdoc
    */
  val tableQuery =
    TableQuery[Users]

  /**
    * @inheritdoc
    */
  implicit val writes =
    new Writes[User] {
      def writes(o: User) =
        Json.obj(
          "id" -> o.id,
          "firstName" -> o.firstName,
          "lastName" -> o.lastName,
          "email" -> o.email
        )
    }

  def validateArguments(arguments: Map[String, JsValue]) =
      Map(
        "firstName" -> PropertyValidators.validate("firstName", arguments, true, PropertyValidators.name),
        "lastName" -> PropertyValidators.validate("lastName", arguments, true, PropertyValidators.name),
        "email" -> PropertyValidators.validate("email", arguments, true, PropertyValidators.email),
        "password" -> PropertyValidators.validate("password", arguments, true, PropertyValidators.password)
      ) collect {
        case (key, Some(value)) =>
          key -> value
      }

  /**
    * @inheritdoc
    * @param fields The arguments to create a [[User]]
    * @return An optional [[User]]
    */
  def create(fields: Map[String, Any]): Option[User] = {
    val uuid =
      java.util.UUID.randomUUID()

    if (
      SlickHelper.queryResult(
        tableQueries.users +=
          User(
            uuid,
            fields("firstName").asInstanceOf[String],
            fields("lastName").asInstanceOf[String],
            fields("email").asInstanceOf[String],
            fields("boxcode").asInstanceOf[String]
          )
      ) > 0
    )
      SlickHelper.optionalFindById[Users, User](tableQuery, uuid)
    else
      None
  }

  /**
    * @inheritdoc
    * @param id @see [[User.id]]
    * @param arguments A key-value argument pair
    * @return true if successful, false otherwise
    */
  def update(id: UUID,
             arguments: Map[String, Any]): Boolean =
    read(id)
      .map(row =>
        SlickHelper.queryResult(
          tableQuery
            .filter(_.id === id)
            .update(
              row.copy(
                id,
                arguments.get("firstName")
                  .fold(row.firstName)(_.asInstanceOf[String]),
                arguments.get("lastName")
                  .fold(row.firstName)(_.asInstanceOf[String]),
                arguments.get("email")
                  .fold(row.firstName)(_.asInstanceOf[String]),
                arguments.get("boxcode")
                  .fold(row.firstName)(_.asInstanceOf[String])
              )
            )
        )
      )
      .fold(false)(_ > 0)

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