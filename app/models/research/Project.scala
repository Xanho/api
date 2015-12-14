package models.research

import java.util.UUID

import models.Helpers.{Columns, ForeignKeys}
import models.helpers.Ownable
import _root_.play.api.libs.json.{JsObject, JsValue, Json, Writes}
import slick.driver.MySQLDriver.api._
import system.helpers.{PropertyValidators, Resource, ResourceCollection, SlickHelper}
import _root_.play.api.libs.json._
import _root_.play.api.libs.functional.syntax._
/**
  * Represents a Research Project, which consists of a series of drafts
  * @param id The project's ID
  * @param ownerId The project owner's ID
  */
case class Project(id: UUID,
                   ownerId: UUID) extends Ownable with Resource

/**
  * A [[slick.profile.RelationalTableComponent.Table]] for [[Project]]s
  * @param tag @see [[slick.lifted.Tag]]
  */
class Projects(tag: Tag)
  extends Table[Project](tag, "projects")
  with Columns.Id[Project]
  with Columns.OwnerId[Project]
  with ForeignKeys.Owner[Project] {

  /**
    * @inheritdoc
    */
  def * =
    (id, ownerId).<>(Project.tupled, Project.unapply)

}

object Projects extends ResourceCollection[Projects, Project] {

  /**
    * @inheritdoc
    */
  val tableQuery =
    TableQuery[Projects]

  /**
    * @inheritdoc
    */
  implicit val writes =
    new Writes[Project] {
      def writes(o: Project) =
        Json.obj(
          "id" -> o.id,
          "ownerId" -> o.ownerId
        )
    }

  /**
    * @inheritdoc
    */
  val validaters =
    Set(("ownerId", true, Set(PropertyValidators.uuid4 _)))

  /**
    * @inheritdoc
    * @param fields The arguments to create a [[Project]]
    * @return An optional [[Project]]
    */
  def create(fields: Map[String, JsValue]): Option[Project] = {
    val uuid =
      system.helpers.uuid

    if (
      SlickHelper.queryResult(
        tableQueries.projects +=
          Project(
            uuid,
            fields("ownerId").as[UUID]
          )
      ) > 0
    )
      SlickHelper.optionalFindById[Projects, Project](tableQuery, uuid)
    else
      None
  }

  /**
    * @inheritdoc
    * @param id @see [[Project.id]]
    * @param arguments A key-value argument pair
    * @return true if successful, false otherwise
    */
  def update(id: UUID,
             arguments: Map[String, JsValue]): Boolean =
    read(id)
      .map(row =>
        SlickHelper.queryResult(
          tableQuery
            .filter(_.id === id)
            .update(
              row.copy(
                id,
                arguments.get("ownerId")
                  .fold(row.ownerId)(_.as[UUID])
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
      .fold(false)(uid =>
        resourceId
          .fold(false)(
            read(_)
              .fold(false)(_.ownerId == uid)
          )
      )

  /**
    * @inheritdoc
    */
  def canDelete(resourceId: Option[UUID],
                userId: Option[UUID],
                data: JsObject = Json.obj()): Boolean =
    userId
      .fold(false)(uid =>
        resourceId
          .fold(false)(
            read(_)
              .fold(false)(_.ownerId == uid)
          )
      )

  /**
    * @inheritdoc
    */
  def canModify(resourceId: Option[UUID],
                userId: Option[UUID],
                data: JsObject = Json.obj()): Boolean =
    userId
      .fold(false)(uid =>
        resourceId
          .fold(false)(
            read(_)
              .fold(false)(_.ownerId == uid)
          )
      )

  /**
    * @inheritdoc
    */
  def canCreate(resourceId: Option[UUID] = None,
                userId: Option[UUID],
                data: JsObject = Json.obj()): Boolean =
    userId.nonEmpty

}