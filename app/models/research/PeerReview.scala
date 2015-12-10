package models.research

import java.util.UUID

import models.Helpers.{Columns, ForeignKeys}
import models.helpers.Ownable
import slick.driver.MySQLDriver.api._

/**
  * Represents a student or peer's peerReview of a [[Project]]
  * @param id The peer review's ID
  * @param ownerId The peer review owner's ID
  * @param projectDraftId The ID of the draft being peer reviewd
  * @param content The body of the peer review
  */
case class PeerReview(id: UUID,
                      ownerId: UUID,
                      projectDraftId: UUID,
                      content: String) extends Ownable

/**
  * A [[slick.profile.RelationalTableComponent.Table]] for [[PeerReview]]s
  * @param tag @see [[slick.lifted.Tag]]
  */
class PeerReviews(tag: Tag)
  extends Table[PeerReview](tag, "peer_reviews")
  with Columns.Id[PeerReview]
  with Columns.OwnerId[PeerReview]
  with ForeignKeys.Owner[PeerReview] {

  /**
    * @see [[PeerReview.content]]
    */
  def content =
    column[String]("content")

  /**
    * @see [[PeerReviews.projectDraftId]]
    */
  def projectDraftId =
    column[UUID]("project_draft_id")

  /**
    * @inheritdoc
    */
  def * =
    (id, ownerId, projectDraftId, content).<>(PeerReview.tupled, PeerReview.unapply)

  /**
    * Foreign key for [[PeerReviews.projectDraftId]]
    */
  def projectDraft =
    foreignKey("fk_project_draft", projectDraftId, tableQueries.projectDrafts)(_.id)
}
