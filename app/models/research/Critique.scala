package models.research

import java.util.UUID

import models.Helpers.{Columns, ForeignKeys}
import models.helpers.Ownable
import slick.driver.MySQLDriver.api._
import system.helpers._
import system.helpers.SlickHelper._

/**
  * Represents a student or peer's critique of a [[Project]]
  * @param id The critique's ID
  * @param ownerId The critique owner's ID
  * @param projectDraftId The ID of the draft being critiqued
  * @param content The body of the critique
  */
case class Critique(id: UUID,
                    ownerId: UUID,
                    projectDraftId: UUID,
                    content: String) extends Ownable with Resource {

  /**
    * The [[ProjectDraft]] being critiqued
    */
  lazy val projectDraft: ProjectDraft =
    projectDraftId.fk[ProjectDrafts, ProjectDraft](tableQueries.projectDrafts)

  /**
    * @inheritdoc
    */
  def canRead(userId: Option[UUID]): Boolean =
    userId.fold(false)(_ == ownerId) || userId.fold(false)(_ == projectDraft.project.ownerId)

  /**
    * @inheritdoc
    */
  def canDelete(userId: Option[UUID]): Boolean =
    false

  /**
    * @inheritdoc
    */
  def canModify(userId: Option[UUID]): Boolean =
    false
}

object Critiques extends ResourceCollection {

  /**
    * @inheritdoc
    */
  def canRead(userId: Option[UUID],
              resources: Resource*): Boolean =
    ???

  /**
    * @inheritdoc
    */
  def canDelete(userId: Option[UUID],
                resources: Resource*): Boolean =
    ???

  /**
    * @inheritdoc
    */
  def canCreate(userId: Option[UUID]): Boolean =
    ???

  /**
    * @inheritdoc
    */
  def canModify(userId: Option[UUID],
                resources: Resource*): Boolean =
    ???
}

/**
  * A [[slick.profile.RelationalTableComponent.Table]] for [[Critique]]s
  * @param tag @see [[slick.lifted.Tag]]
  */
class Critiques(tag: Tag)
  extends Table[Critique](tag, "critiques")
  with Columns.Id[Critique]
  with Columns.OwnerId[Critique]
  with ForeignKeys.Owner[Critique] {

  /**
    * @see [[Critique.content]]
    */
  def content =
    column[String]("content")

  /**
    * @see [[Critique.projectDraftId]]
    */
  def projectDraftId =
    column[UUID]("project_draft_id")

  /**
    * @inheritdoc
    */
  def * =
    (id, ownerId, projectDraftId, content).<>(Critique.tupled, Critique.unapply)

  /**
    * Foreign key for [[Critique.projectDraftId]]
    */
  def projectDraft =
    foreignKey("fk_project_draft", projectDraftId, tableQueries.projectDrafts)(_.id)
}
