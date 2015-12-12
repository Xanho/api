package models.school

import java.util.UUID

import models.Helpers.{Columns, ForeignKeys}
import models.helpers.OptionallyOwnable
import slick.driver.MySQLDriver.api._
import system.helpers.SlickHelper._

/**
  * Represents a microdegree or unit, which covers a specific area
  * @param id The microdegree's ID
  * @param name The microdegree's name or title
  * @param ownerId The optional [[models.User]] owner of this microdegree.  If [[None]], then
  *                this microdegree is considered to be owned by the public.
  */
case class Microdegree(id: UUID,
                       name: String,
                       ownerId: Option[UUID]) extends OptionallyOwnable

/**
  * A [[slick.profile.RelationalTableComponent.Table]] for [[Microdegree]]s
  * @param tag @see [[slick.lifted.Tag]]
  */
class Microdegrees(tag: Tag)
  extends Table[Microdegree](tag, "microdegrees")
  with Columns.Id[Microdegree]
  with Columns.Name[Microdegree]
  with Columns.OptionalOwnerId[Microdegree]
  with ForeignKeys.OptionalOwner[Microdegree] {

  /**
    * @see [[slick.profile.RelationalTableComponent.Table.*]]
    */
  def * =
    (id, name, ownerId).<>(Microdegree.tupled, Microdegree.unapply)

}

/**
  * Represents a revision to a [[Microdegree]]
  * @param id The revision's ID
  * @param revisionNumber The revision number
  * @param microdegreeId @see [[Microdegree.id]]
  * @param proposalId @see [[MicrodegreeRevisionProposal.id]]
  */
case class MicrodegreeRevision(id: UUID,
                               revisionNumber: Int,
                               microdegreeId: UUID,
                               proposalId: UUID) {

  /**
    * The parent [[Microdegree]] of this revision
    */
  lazy val microdegree: Microdegree =
    microdegreeId.fk[Microdegrees, Microdegree](tableQueries.microdegrees)

  /**
    * The [[MicrodegreeRevisionProposal]] used for this revision
    */
  lazy val microdegreeRevisionProposal: MicrodegreeRevisionProposal =
    proposalId.fk[MicrodegreeRevisionProposals, MicrodegreeRevisionProposal](tableQueries.microdegreeRevisionProposals)

}

/**
  * A [[slick.profile.RelationalTableComponent.Table]] for [[MicrodegreeRevision]]s
  * @param tag @see [[slick.lifted.Tag]]
  */
class MicrodegreeRevisions(tag: Tag)
  extends Table[MicrodegreeRevision](tag, "microdegree_revisions")
  with Columns.Id[MicrodegreeRevision]
  with Columns.RevisionNumber[MicrodegreeRevision]
  with Columns.MicrodegreeId[MicrodegreeRevision]
  with Columns.ProposalId[MicrodegreeRevision]
  with ForeignKeys.Microdegree[MicrodegreeRevision] {

  /**
    * @inheritdoc
    */
  def * =
    (id, revisionNumber, microdegreeId, proposalId).<>(MicrodegreeRevision.tupled, MicrodegreeRevision.unapply)

  /**
    * The proposal used in this revision
    */
  def proposal =
    foreignKey("fk_proposal", proposalId, tableQueries.microdegreeRevisionProposals)(_.id)
}

/**
  * Represents a proposed [[MicrodegreeRevision]] to a [[Microdegree]]
  * @param id The Microdegree Revision Proposal's ID
  * @param microdegreeId @see [[Microdegree.id]]
  * @param newRevisionNumber The next/target revision number.  @see [[MicrodegreeRevision.revisionNumber]]
  *                          For example, if the currently active revision number is 19, then the newRevisionNumber would be 20
  * @param content The microdegree's content and materials
  */
case class MicrodegreeRevisionProposal(id: UUID,
                                       microdegreeId: UUID,
                                       newRevisionNumber: Int,
                                       content: String) {

  /**
    * The parent [[Microdegree]] of this revision
    */
  lazy val microdegree: Microdegree =
    microdegreeId.fk[Microdegrees, Microdegree](tableQueries.microdegrees)

}

/**
  * A [[slick.profile.RelationalTableComponent.Table]] for [[MicrodegreeRevisionProposal]]s
  * @param tag @see [[slick.lifted.Tag]]
  */
class MicrodegreeRevisionProposals(tag: Tag)
  extends Table[MicrodegreeRevisionProposal](tag, "microdegree_revision_proposals")
  with Columns.Id[MicrodegreeRevisionProposal]
  with Columns.AuthorId[MicrodegreeRevisionProposal]
  with Columns.MicrodegreeId[MicrodegreeRevisionProposal]
  with Columns.NewRevisionNumber[MicrodegreeRevisionProposal]
  with ForeignKeys.Author[MicrodegreeRevisionProposal]
  with ForeignKeys.Microdegree[MicrodegreeRevisionProposal] {

  /**
    * @see [[MicrodegreeRevisionProposal.content]]
    */
  def content =
    column[String]("content")

  /**
    * @inheritdoc
    */
  def * =
    (id, microdegreeId, newRevisionNumber, content).<>(MicrodegreeRevisionProposal.tupled, MicrodegreeRevisionProposal.unapply)

}

/**
  * Represents a Topic Requirement for completion of a Microdegree
  * @param id The requirement ID
  * @param microdegreeRevisionProposalId @see [[MicrodegreeRevisionProposal.id]]
  * @param topicId @see [[Topic.id]]
  * @param minimumRevision @see [[TopicRevision.revisionNumber]]
  * @param maximumRevision @see [[TopicRevision.revisionNumber]]
  */
case class TopicRequirement(id: UUID,
                            microdegreeRevisionProposalId: UUID,
                            topicId: UUID,
                            minimumRevision: Option[Int],
                            maximumRevision: Option[Int]) {

  /**
    * The [[MicrodegreeRevisionProposal]] in which this requirement is used
    */
  lazy val microdegreeRevisionProposal: MicrodegreeRevisionProposal =
    microdegreeRevisionProposalId.fk[MicrodegreeRevisionProposals, MicrodegreeRevisionProposal](tableQueries.microdegreeRevisionProposals)

  /**
    * The [[Topic]] used for this revision
    */
  lazy val topic: Topic =
    topicId.fk[Topics, Topic](tableQueries.topics)

}

/**
  * A [[slick.profile.RelationalTableComponent.Table]] for [[TopicRequirement]]s
  * @param tag @see [[slick.lifted.Tag]]
  */
class TopicRequirements(tag: Tag)
  extends Table[TopicRequirement](tag, "topic_requirement")
  with Columns.Id[TopicRequirement]
  with Columns.ProposalId[TopicRequirement]
  with Columns.TopicId[TopicRequirement]
  with ForeignKeys.Topic[TopicRequirement] {

  /**
    * @see [[TopicRequirement.minimumRevision]]
    */
  def minimumRevision =
    column[Option[Int]]("minimum_revision")

  /**
    * @see [[TopicRequirement.maximumRevision]]
    */
  def maximumRevision =
    column[Option[Int]]("maximum_revision")

  /**
    * @inheritdoc
    * @return
    */
  def * =
    (id, proposalId, topicId, minimumRevision, maximumRevision).<>(TopicRequirement.tupled, TopicRequirement.unapply)

  /**
    * The proposal containing this requirement
    */
  def proposal =
    foreignKey("fk_proposal", proposalId, tableQueries.microdegreeRevisionProposals)(_.id)
}