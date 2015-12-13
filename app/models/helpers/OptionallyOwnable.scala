package models.helpers

import java.util.UUID

import models.{tableQueries, _}
import system.helpers.SlickHelper._

/**
  * An entity which can be owned by a [[models.User]]
  */
trait OptionallyOwnable {

  /**
    * @see [[models.Helpers.Columns.OptionalOwnerId]]
    */
  def ownerId: Option[UUID]

  /**
    * The optional [[User]] who owns this entity
    */
  lazy val owner: Option[User] =
    ownerId map (_.fk[Users, User](tableQueries.users))

}

/**
  * An entity which must be owned by a [[models.User]]
  */
trait Ownable {

  /**
    * @see [[models.Helpers.Columns.OptionalOwnerId]]
    */
  def ownerId: UUID

  /**
    * The [[User]] who owns this entity
    */
  lazy val owner: User =
    ownerId.fk[Users, User](tableQueries.users)

}