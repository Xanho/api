package models.school

import java.util.UUID

import models.Helpers.{Columns, ForeignKeys}
import models._
import models.helpers.OptionallyOwnable
import slick.driver.MySQLDriver.api._

/**
  * Represents a topic or unit, which covers a specific area
  * @param id The topic's ID
  * @param name The topic's name or title
  * @param ownerId The optional [[models.User]] owner of this topic.  If [[None]], then
  *                this topic is considered to be owned by the public.
  */
case class Topic(id: UUID,
                 name: String,
                 ownerId: Option[UUID]) extends OptionallyOwnable

/**
  * A [[slick.profile.RelationalTableComponent.Table]] for [[Topic]]s
  * @param tag @see [[slick.lifted.Tag]]
  */
class Topics(tag: Tag)
  extends Table[Topic](tag, "topics")
  with Columns.Id[Topic]
  with Columns.Name[Topic]
  with Columns.OwnerId[Topic]
  with ForeignKeys.Owner[Topic] {

  /**
    * @see [[slick.profile.RelationalTableComponent.Table.*]]
    */
  def * =
    (id, name, ownerId).<>(Topic.tupled, Topic.unapply)

}

/**
  * Represents a revision to a [[Topic]]
  * @param id The revision's ID
  * @param revisionNumber The revision number
  * @param topicId @see [[Topic.id]]
  * @param proposalId @see [[TopicRevisionProposal.id]]
  */
case class TopicRevision(id: UUID,
                         revisionNumber: Int,
                         topicId: UUID,
                         proposalId: UUID)

/**
  * A [[slick.profile.RelationalTableComponent.Table]] for [[TopicRevision]]s
  * @param tag @see [[slick.lifted.Tag]]
  */
class TopicRevisions(tag: Tag)
  extends Table[TopicRevision](tag, "topic_revisions")
  with Columns.Id[TopicRevision]
  with Columns.RevisionNumber[TopicRevision]
  with Columns.TopicId[TopicRevision]
  with Columns.ProposalId[TopicRevision]
  with ForeignKeys.Topic[TopicRevision] {

  /**
    * @inheritdoc
    */
  def * =
    (id, revisionNumber, topicId, proposalId).<>(TopicRevision.tupled, TopicRevision.unapply)

  /**
    * Foreign Key to a [[TopicRevisionProposal]]
    */
  def proposal =
    foreignKey("fk_proposal", proposalId, topicRevisionProposals)(_.id)
}

/**
  * Represents a proposed [[TopicRevision]] to a [[Topic]]
  * @param id The Topic Revision Proposal's ID
  * @param topicId @see [[Topic.id]]
  * @param newRevisionNumber The next/target revision number.  @see [[TopicRevision.revisionNumber]]
  *                          For example, if the currently active revision number is 19, then the newRevisionNumber would be 20
  * @param content The topic's content and materials
  */
case class TopicRevisionProposal(id: UUID,
                                 topicId: UUID,
                                 newRevisionNumber: Int,
                                 content: String)

/**
  * A [[slick.profile.RelationalTableComponent.Table]] for [[TopicRevisionProposal]]s
  * @param tag @see [[slick.lifted.Tag]]
  */
class TopicRevisionProposals(tag: Tag)
  extends Table[TopicRevisionProposal](tag, "topic_revision_proposals")
  with Columns.Id[TopicRevisionProposal]
  with Columns.AuthorId[TopicRevisionProposal]
  with Columns.TopicId[TopicRevisionProposal]
  with Columns.NewRevisionNumber[TopicRevisionProposal]
  with ForeignKeys.Author[TopicRevisionProposal]
  with ForeignKeys.Topic[TopicRevisionProposal] {

  /**
    * @see [[TopicRevisionProposal.content]]
    */
  def content =
    column[String]("content")

  /**
    * @inheritdoc
    */
  def * =
    (id, topicId, newRevisionNumber, content).<>(TopicRevisionProposal.tupled, TopicRevisionProposal.unapply)

}