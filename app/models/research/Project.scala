package models.research

import java.util.UUID

import models.Helpers.{Columns, ForeignKeys}
import models.helpers.Ownable
import slick.driver.MySQLDriver.api._

/**
  * Represents a Research Project, which consists of a series of drafts
  * @param id The project's ID
  * @param ownerId The project owner's ID
  */
case class Project(id: UUID,
                   ownerId: UUID) extends Ownable

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

/**
  * Represents a draft for a [[Project]]
  * @param id The draft's ID
  * @param revisionNumber The revision number
  * @param content The content body of the draft
  */
case class ProjectDraft(id: UUID,
                        revisionNumber: Int,
                        content: String)

/**
  * A [[slick.profile.RelationalTableComponent.Table]] for [[ProjectDraft]]s
  * @param tag @see [[slick.lifted.Tag]]
  */
class ProjectDrafts(tag: Tag)
  extends Table[ProjectDraft](tag, "project_drafts")
  with Columns.Id[ProjectDraft]
  with Columns.RevisionNumber[ProjectDraft] {

  /**
    * @see [[ProjectDraft.content]]
    */
  def content =
    column[String]("content")

  /**
    * @inheritdoc
    */
  def * =
    (id, revisionNumber, content).<>(ProjectDraft.tupled, ProjectDraft.unapply)

}
