package models.school

import models._
import models.Helpers._
import slick.driver.MySQLDriver.api._

/**
  * Represents a collection of topics, to represent a general field
  * @param id The microdegree's ID
  * @param name The microdegree's name or title
  * @param ownerId The optional [[models.User]] owner of this microdegree.  If [[None]], then
  *                this microdegree is considered to be owned by the public.
  */
case class Microdegree(id: String,
                       name: String,
                       ownerId: Option[String])

/**
  * A [[slick.profile.RelationalTableComponent.Table]] for [[Microdegree]]s
  * @param tag @see [[slick.lifted.Tag]]
  */
class Microdegrees(tag: Tag)
  extends Table[Microdegree](tag, "microdegrees")
  with Columns.Id[Microdegree]
  with Columns.Name[Microdegree]
  with Columns.OwnerId[Microdegree]
  with ForeignKeys.Owner[Microdegree] {

  /**
    * @inheritdoc
    */
  def * =
    (id, name, ownerId).<>(Microdegree.tupled, Microdegree.unapply)

}