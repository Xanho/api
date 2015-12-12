package system

import java.util.UUID

import models.Helpers.Columns
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

package object helpers {

  /**
    * Reference to the default Database, providing an easy interface to run queries
    */
  def db =
    DatabaseConfigProvider.get[JdbcProfile](play).db

  /**
    * Retrieves the current [[Play]] application
    */
  def play =
    Play.current

  /**
    * Retrieves a value from the application.conf
    * @param key The configuration key
    * @param default A default value if one can't be found
    * @return A string value
    */
  def config(key: String,
             default: String) =
    play.configuration
      .getString(key)
      .getOrElse(default)

  /**
    * Configuration helpers from the current [[play.configuration]] which attempt to read from application.conf,
    * with defaults in case they can't be found
    */
  object DefaultConfiguration {

    /**
      * The default time limit on a database query
      */
    def queryTimeout: Duration =
      Duration(config("queryTimeout", "30s"))

  }

  object SlickHelper {

    /**
      * Executes the given query/action, and awaits the result using the [[DefaultConfiguration.queryTimeout]]
      * @param a The action to run
      * @tparam R The expected return type
      * @return A [[R]]
      */
    def queryResult[R](a: slick.dbio.DBIOAction[R, slick.dbio.NoStream, scala.Nothing]): R =
      Await.result(db.run(a), DefaultConfiguration.queryTimeout)

    /**
      * Retrieves a foreign row from a foreign table corresponding to the given tableQuery
      * @param tableQuery The "foreign" table referenced by a column in the "local" or current table
      * @param localId The optional ID in the local table which references the ID in tableQuery
      * @tparam T Type bound used for the TableQuery to ensure the table has an ID column
      * @tparam E The type of the [[Table]]
      * @return An optional [[T#TableElementType]]
      */
    def fkResult[T <: Table[E] with Columns.Id[E], E](tableQuery: TableQuery[T],
                                                      localId: UUID): T#TableElementType =
      SlickHelper.queryResult((tableQuery filter (_.id === localId)).result.head)

    /**
      * @see [[fkResult]], except with an optional local ID
      * @param tableQuery The "foreign" table referenced by a column in the "local" or current table
      * @param localId The optional ID in the local table which references the ID in tableQuery
      * @tparam T Type bound used for the TableQuery to ensure the table has an ID column
      * @tparam E The type of the [[Table]]
      * @return An optional [[T#TableElementType]]
      */
    def optionFkResult[T <: Table[E] with Columns.Id[E], E](tableQuery: TableQuery[T],
                                                            localId: Option[UUID]): Option[T#TableElementType] =
      localId map (fkResult[T, E](tableQuery, _))

    /**
      * Helper class which allows for treating UUIDs as foreign key reference columns
      * @param uuid The local UUID which references a foreign table's column
      */
    implicit class FKUUID(uuid: UUID) {

      def fk[T <: Table[E] with Columns.Id[E], E](tableQuery: TableQuery[T]): T#TableElementType =
        fkResult[T, E](tableQuery, uuid)

    }

  }

}
