
import java.util.UUID

import slick.driver.MySQLDriver.api._

// Note: These package object imports are aliased to disambiguate calls to their corresponding tableQueries
import models.{research => researchPO, school => schoolPO}

package object models {

  /**
    * Container object with references to other packages' table queries
    */
  object tableQueries {

    /**
      * @see [[researchPO.tableQueries]]
      */
    val research =
      researchPO.tableQueries

    /**
      * @see [[schoolPO.tableQueries]]
      */
    val school =
      schoolPO.tableQueries

    /**
      * The [[TableQuery]] for [[Users]]
      */
    val users: TableQuery[Users] =
      TableQuery[Users]

  }

  /**
    * Shared DB reference
    */
  def db =
    Database.forConfig("default")

  /**
    * Support methods for Models
    */
  object Helpers {

    /**
      * Generates a stringified new UUID v4
      * @return A [[String]]
      */
    def uuid4: String =
      java.util.UUID.randomUUID().toString

    /**
      * Commonly used column definitions inside of a [[Table]]
      */
    object Columns {

      /**
        * An (UU)ID Column
        * @tparam T Type bound on [[Table]]
        */
      trait Id[T] {
        self: Table[T] =>

        def id =
          column[UUID]("id", O.PrimaryKey, O.Default[UUID](java.util.UUID.randomUUID()))
      }

      /**
        * A Name Column
        * @tparam T Type bound on [[Table]]
        */
      trait Name[T] {
        self: Table[T] =>

        def name =
          column[String]("name")
      }

      /**
        * An Optional Owner ID Column
        * @tparam T Type bound on [[Table]]
        */
      trait OwnerId[T] {
        self: Table[T] =>

        def ownerId =
          column[UUID]("owner_id")
      }

      /**
        * An Optional Owner ID Column
        * @tparam T Type bound on [[Table]]
        */
      trait OptionalOwnerId[T] {
        self: Table[T] =>

        def ownerId =
          column[Option[UUID]]("owner_id")
      }

      /**
        * An Author ID Column
        * @tparam T Type bound on [[Table]]
        */
      trait AuthorId[T] {
        self: Table[T] =>

        def authorId =
          column[UUID]("author_id")
      }

      /**
        * A Revision Number Column
        * @tparam T Type bound on [[Table]]
        */
      trait RevisionNumber[T] {
        self: Table[T] =>

        def revisionNumber =
          column[Int]("revision_number")
      }

      /**
        * A Target/New Revision Number Column
        * @tparam T Type bound on [[Table]]
        */
      trait NewRevisionNumber[T] {
        self: Table[T] =>

        def newRevisionNumber =
          column[Int]("new_revision_number")
      }

      /**
        * A Topic ID Column
        * @tparam T Type bound on [[Table]]
        */
      trait TopicId[T] {
        self: Table[T] =>

        def topicId =
          column[UUID]("topic_id")
      }

      /**
        * A Microdegree ID Column
        * @tparam T Type bound on [[Table]]
        */
      trait MicrodegreeId[T] {
        self: Table[T] =>

        def microdegreeId =
          column[UUID]("microdegree_id")
      }

      /**
        * A Proposal ID Column
        * @tparam T Type bound on [[Table]]
        */
      trait ProposalId[T] {
        self: Table[T] =>

        def proposalId =
          column[UUID]("proposal_id")
      }

    }

    /**
      * Commonly used foreign key definitions inside of a [[Table]]
      */
    object ForeignKeys {

      /**
        * Represents a FK to an optional owner of something
        * @tparam T Type bound on [[Table]]
        */
      trait Owner[T] {
        self: Table[T] with Columns.OwnerId[T] =>

        def owner =
          foreignKey("fk_owner", ownerId, tableQueries.users)(_.id)
      }

      /**
        * Represents a FK to an optional owner of something
        * @tparam T Type bound on [[Table]]
        */
      trait OptionalOwner[T] {
        self: Table[T] with Columns.OptionalOwnerId[T] =>

        def owner =
          foreignKey("fk_owner", ownerId, tableQueries.users)(_.id.?)
      }

      /**
        * Represents a FK to an author of something
        * @tparam T Type bound on [[Table]]
        */
      trait Author[T] {
        self: Table[T] with Columns.AuthorId[T] =>

        def author =
          foreignKey("fk_author", authorId, tableQueries.users)(_.id)
      }

      /**
        * Represents a FK to a topic
        * @tparam T Type bound on [[Table]]
        */
      trait Topic[T] {
        self: Table[T] with Columns.TopicId[T] =>

        def topic =
          foreignKey("fk_topic", topicId, tableQueries.school.topics)(_.id)
      }

      /**
        * Represents a FK to a [[Microdegree]]
        * @tparam T Type bound on [[Table]]
        */
      trait Microdegree[T] {
        self: Table[T] with Columns.MicrodegreeId[T] =>

        def topic =
          foreignKey("fk_microdegree", microdegreeId, tableQueries.school.microdegrees)(_.id)
      }

    }

  }

}
