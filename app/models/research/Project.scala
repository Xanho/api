package models.research

import java.util.UUID

import _root_.play.api.libs.json.{JsObject, JsValue, Writes, _}
import models.Helpers.{Columns, ForeignKeys}
import models.helpers.Ownable
import slick.driver.MySQLDriver.api._
import system.helpers.{PropertyValidators, Resource, ResourceCollection}

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
  implicit val writes: Writes[Project] =
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
    * @param uuid The UUID to use in the creation
    * @param arguments A map containing values to be updated
    * @return A new [[Project]]
    */
  def creator(uuid: UUID,
              arguments: Map[String, JsValue]) =
    Project(
      uuid,
      arguments("ownerId").as[UUID]
    )

  /**
    * @inheritdoc
    * @param row A [[Project]]
    * @param arguments A map containing values to be updated
    * @return A new [[Project]]
    */
  def updater(row: Project,
              arguments: Map[String, JsValue]) =
    row.copy(
      row.id,
      arguments.get("ownerId")
        .fold(row.ownerId)(_.as[UUID])
    )

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