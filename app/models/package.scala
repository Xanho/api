
import slick.driver.MySQLDriver.api._

package object models {

  /**
    * The [[TableQuery]] for [[Users]]
    */
  val users =
    TableQuery[Users]

  /**
    * The [[TableQuery]] for [[school.Topics]]
    */
  val topics =
    TableQuery[school.Topics]

  /**
    * The [[TableQuery]] for [[school.TopicRevisions]]
    */
  val topicRevisions =
    TableQuery[school.TopicRevisions]

  /**
    * The [[TableQuery]] for [[school.TopicRevisionProposals]]
    */
  val topicRevisionProposals =
    TableQuery[school.TopicRevisionProposals]

  /**
    * The [[TableQuery]] for [[school.Microdegrees]]
    */
  val microdegrees =
    TableQuery[school.Microdegrees]

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
          column[String]("id", O.PrimaryKey, O.Default[String](uuid4))
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
        * An Owner ID Column
        * @tparam T Type bound on [[Table]]
        */
      trait OwnerId[T] {
        self: Table[T] =>

        def ownerId =
          column[Option[String]]("owner_id")
      }

      /**
        * An Author ID Column
        * @tparam T Type bound on [[Table]]
        */
      trait AuthorId[T] {
        self: Table[T] =>

        def authorId =
          column[String]("author_id")
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
          column[String]("topic_id")
      }

      /**
        * A Proposal ID Column
        * @tparam T Type bound on [[Table]]
        */
      trait ProposalId[T] {
        self: Table[T] =>

        def proposalId =
          column[String]("proposal_id")
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
          foreignKey("fk_owner", ownerId, users)(_.id.?)
      }

      /**
        * Represents a FK to an author of something
        * @tparam T Type bound on [[Table]]
        */
      trait Author[T] {
        self: Table[T] with Columns.AuthorId[T] =>

        def author =
          foreignKey("fk_author", authorId, users)(_.id)
      }

      /**
        * Represents a FK to a topic
        * @tparam T Type bound on [[Table]]
        */
      trait Topic[T] {
        self: Table[T] with Columns.TopicId[T] =>

        def topic =
          foreignKey("fk_topic", topicId, topics)(_.id)
      }

    }

  }

}
