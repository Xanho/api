package system

import java.util.UUID

import models.Helpers.Columns
import models._
import play.api.Play
import slick.driver.MySQLDriver.api._
import slick.lifted.AbstractTable

import scala.concurrent.Await
import scala.concurrent.duration.Duration

package object helpers {

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
    def queryResult[R](a : slick.dbio.DBIOAction[R, slick.dbio.NoStream, scala.Nothing]): R =
      Await.result(db.run(a), DefaultConfiguration.queryTimeout)

    /**
      * Given a foreign [[TableQuery]] which implements [[Columns.Id]],
      * retrieves the foreign record with the given Local ID
      * @param tableQuery The "foreign" table referenced by a column in the "local" or current table
      * @param localId The ID in the local table which references the ID in tableQuery
      * @tparam E Type bound on a [[TableQuery]] requiring [[Columns.Id]]
      * @return An entity of the type of the row type of the tableQuery
      */
//    def fkResult[E <: Table[_] with Columns.Id[_]](tableQuery: TableQuery[E],
//    def fkResult[E](tableQuery: slick.driver.MySQLDriver.api.TableQuery[Table[E] with Columns.Id[E]],
//                                                      localId: UUID): Table[E]#TableElementType =
//      SlickHelper.queryResult((tableQuery filter (_.id === localId)).result.head)
    def fkResult[T <: Table[E] with Columns.Id[E], E](tableQuery: TableQuery[T],
                                                      localId: UUID): T#TableElementType =
      SlickHelper.queryResult((tableQuery filter (_.id === localId)).result.head)
//      SlickHelper.queryResult((tableQuery filter (_.id === localId)).result.head)
    /**
      * @see [[fkResult]], except with an optional local ID
      * @param tableQuery The "foreign" table referenced by a column in the "local" or current table
      * @param localId The optional ID in the local table which references the ID in tableQuery
      * @tparam E Type bound on a [[TableQuery]] requiring [[Columns.Id]]
      * @return An optional entity of the type of the row type of the tableQuery
      */
//    def optionFkResult[E](tableQuery: slick.driver.MySQLDriver.api.TableQuery[Table[E] with Columns.Id[E]],
//                          localId: Option[UUID]): Option[Table[E]#TableElementType] =
    def optionFkResult[T <: Table[E] with Columns.Id[E], E](tableQuery: TableQuery[T],
                                                            localId: Option[UUID]): Option[T#TableElementType] =
      localId map (lid => fkResult[T, E](tableQuery, lid))
//      localId map (fkResult(tableQuery, _))

    /**
      * Helper class which allows for treating UUIDs as foreign key reference columns
      * @param uuid The local UUID which references a foreign table's column
      */
    implicit class FKUUID(uuid: UUID) {

//      def fk[E](tableQuery: TableQuery[Table[E] with Columns.Id[E]]): Table[E]#TableElementType =
      def fk[T <: Table[E] with Columns.Id[E], E](tableQuery: TableQuery[T]): T#TableElementType =
        fkResult[T, E](tableQuery, uuid)
//      def fk[E <: Table[_] with Columns.Id[_]](tableQuery: TableQuery[E]): E#TableElementType =
//        fkResult(tableQuery, uuid)

    }

  }

}
