package system.helpers

import java.util.UUID

import models.Helpers.Columns
import play.api.libs.json._
import slick.driver.MySQLDriver.api._
import system.helpers.ResponseHelpers.PropertyErrorCodes

/**
  * Represents a specific, identifiable application resource.
  */
trait Resource {

  /**
    * All resources must have a unique identifier
    * @return a UUID
    */
  def id: UUID

}

/**
  * Represents a container or collection of [[Resource]]s.  In essence, this allows requesting authorization
  * on a particular resource in the collection.
  * @tparam R The [[Resource]] of this collection
  * @tparam T Type bound used for the TableQuery to ensure the table has an ID column
  */
trait ResourceCollection[R <: Resource, T <: Table[R] with Columns.Id[R]] {

  /**
    * This collection's [[TableQuery]]
    * @return A [[TableQuery]]
    */
  def tableQuery: TableQuery[T]

  /**
    * An implicit writer to convert this [[R]] to JSON
    * @return A [[Writes]] [[R]]
    */
  implicit def writes: Writes[R]

  /**
    * Checks the provided arguments, and validates the necessary properties
    * @param arguments The arguments to be validated
    * @return A invalid property mapping from a property name to an error status
    */
  def validateArguments(arguments: Map[String, Any]): Map[String, Int]

  /**
    * Creates a [[R]] with the given arguments
    * @param arguments A key-value argument pair
    * @return An optional [[R]]
    */
  def create(arguments: Map[String, Any]): Option[R]

  /**
    * Retrieves the [[R]] with the given ID
    * @param id @see [[Resource.id]]
    * @return An optional [[R]] if one is found
    */
  def read(id: UUID): Option[R] =
    SlickHelper.optionalFindById(tableQuery, id)

  /**
    * Deletes the [[system.helpers.Resource]] with the given [[system.helpers.Resource.id]]
    * @param id @see [[system.helpers.Resource.id]]
    * @return true if successful, false otherwise
    */
  def delete(id: UUID): Boolean =
    SlickHelper.queryResult((tableQuery filter (_.id === id)).delete) > 0

  /**
    * Updates the [[R]] with the given ID, to the given arguments
    * @param id @see [[Resource.id]]
    * @param arguments A key-value argument pair
    * @return true if successful, false otherwise
    */
  def update(id: UUID,
             arguments: Map[String, Any]): Boolean

  /**
    * Dictates if the user with the given ID is allowed READ access to the resource with the given ID
    * @param resourceId @see [[Resource.id]]
    * @param userId @see [[models.User.id]]
    * @param data The JSON object provided by the user containing data about the resource
    * @return true if authorized, false if unauthorized
    */
  def canRead(resourceId: Option[UUID],
              userId: Option[UUID],
              data: JsObject = Json.obj()): Boolean

  /**
    * Dictates if the user with the given ID is allowed MODIFY access to the resource with the given ID
    * @param resourceId @see [[Resource.id]]
    * @param userId @see [[models.User.id]]
    * @param data The JSON object provided by the user containing data about the resource
    * @return true if authorized, false if unauthorized
    */
  def canModify(resourceId: Option[UUID],
                userId: Option[UUID],
                data: JsObject = Json.obj()): Boolean

  /**
    * Dictates if the user with the given ID is allowed DELETE access to the resource with the given ID
    * @param resourceId @see [[Resource.id]]
    * @param userId @see [[models.User.id]]
    * @param data The JSON object provided by the user containing data about the resource
    * @return true if authorized, false if unauthorized
    */
  def canDelete(resourceId: Option[UUID],
                userId: Option[UUID],
                data: JsObject = Json.obj()): Boolean

  /**
    * Dictates if the user with the given ID is allowed CREATE access in this collection
    * @param userId @see [[models.User.id]]
    * @param data The JSON object provided by the user containing data about the resource
    * @return true if authorized, false if unauthorized
    */
  def canCreate(resourceId: Option[UUID],
                userId: Option[UUID],
                data: JsObject = Json.obj()): Boolean

}

object PropertyValidators {

  /**
    * Validates the given key against the given arguments, using the given set of rules
    * @param key The key in the arguments map
    * @param arguments The list of arguments containing the key
    * @param required Dictates if the property is required
    * @param rules The rules to match against the argument
    * @return An optional error code
    */
  def validate(key: String,
               arguments: Map[String, JsValue],
               required: Boolean,
               rules: (JsValue => Option[Int])*): Option[Int] =
    arguments.get(key)
      .fold(
        if(required)
          Some(PropertyErrorCodes.NO_VALUE)
        else
          None
      )(a => rules.foldLeft[Option[Int]](None){
        case (None, rule: (JsValue => Option[Int])) =>
          rule(a)
        case (Some(x), _) =>
          Some(x)
      })

  /**
    * Validates a name field
    * @param s The given input
    * @return An optional error code
    */
  def name(s: JsValue): Option[Int] =
    s match {
      case str: JsString =>
        str.value match {
          case namePattern(_*) =>
            if(str.value.length < 2)
              Some(PropertyErrorCodes.TOO_SHORT)
            else if(str.value.length > 20)
              Some(PropertyErrorCodes.TOO_LONG)
            else
              None
          case _ =>
            Some(PropertyErrorCodes.INVALID_CHARACTERS)
        }
      case _ =>
        Some(PropertyErrorCodes.INVALID_TYPE)
    }

  /**
    * Validates an email address
    * @param s The given input
    * @return An optional error code
    */
  def email(s: JsValue): Option[Int] =
    s match {
      case str: JsString =>
        str.value match {
          case emailPattern(_*) =>
            None
          case _ =>
            Some(PropertyErrorCodes.INVALID_EMAIL)
        }
      case _ =>
        Some(PropertyErrorCodes.INVALID_TYPE)
    }

  /**
    * Validates a password field
    * @param s The given input
    * @return An optional error code
    */
  def password(s: JsValue): Option[Int] =
    s match {
      case str: JsString =>
        if((nonAlphaNumeric findAllIn str.value).isEmpty || (numeric findAllIn str.value).isEmpty)
          Some(PropertyErrorCodes.NOT_COMPLEX_ENOUGH)
        else if(str.value.length < 7)
          Some(PropertyErrorCodes.TOO_SHORT)
        else if(str.value.length > 200)
          Some(PropertyErrorCodes.TOO_LONG)
        else
          None
      case _ =>
        Some(PropertyErrorCodes.INVALID_TYPE)
    }

  private val emailPattern =
    """\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}\b""".r

  private val namePattern =
    """[A-Za-z-]*""".r

  private val nonAlphaNumeric =
    """([^A-Za-z0-9])""".r

  private val numeric =
    """([0-9])""".r

  object PropertyErrorCodes {

    val NO_VALUE = 0
    val INVALID_TYPE = 1
    val TOO_SHORT = 2
    val TOO_LONG = 3
    val INVALID_EMAIL = 4
    val INVALID_CHARACTERS = 5
    val NOT_COMPLEX_ENOUGH = 6
  }

}
