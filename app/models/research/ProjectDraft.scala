package models.research

import java.util.UUID

import models.Helpers.Columns
import slick.driver.MySQLDriver.api._
import system.helpers.SlickHelper._


/**
  * Represents a draft for a [[Project]]
  * @param id The draft's ID
  * @param revisionNumber The revision number
  * @param projectId The [[Project.id]] to which this draft belongs
  * @param content The content body of the draft
  */
case class ProjectDraft(id: UUID,
                        revisionNumber: Int,
                        projectId: UUID,
                        content: String) {

  /**
    * The [[Project]] to which this draft belongs
    */
  lazy val project: Project =
    projectId.fk[Projects, Project](tableQueries.projects)

}

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
    * @see [[ProjectDraft.projectId]]
    */
  def projectId =
    column[UUID]("project_id")

  /**
    * @inheritdoc
    */
  def * =
    (id, revisionNumber, projectId, content).<>(ProjectDraft.tupled, ProjectDraft.unapply)

  /**
    * Foreign key to the [[Project]]
    */
  def project =
    foreignKey("fk_project", projectId, tableQueries.projects)(_.id)

}
