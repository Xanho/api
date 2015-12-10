package models.research

import java.util.UUID

import models.Helpers.{Columns, ForeignKeys}
import slick.driver.MySQLDriver.api._

import models.helpers.Ownable

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
                    content: String) extends Ownable

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
