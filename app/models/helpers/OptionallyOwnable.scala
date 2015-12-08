package models.helpers

import java.util.UUID

import slick.driver.MySQLDriver.api._

import models._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * An entity which can be owned by a [[models.User]]
  */
trait OptionallyOwnable {

  /**
    * @see [[models.Helpers.Columns.OwnerId]]
    */
  def ownerId: Option[UUID]

  /**
    * The optional [[User]] who owns this entity
    */
  lazy val owner: Option[User] =
    ownerId map (oid => Await.result(db.run((users filter (_.id === oid)).result.head), Duration.Inf))

}

/**
  * An entity which must be owned by a [[models.User]]
  */
trait Ownable {

  /**
    * @see [[models.Helpers.Columns.OwnerId]]
    */
  def ownerId: UUID

  /**
    * The [[User]] who owns this entity
    */
  lazy val owner: User =
    Await.result(db.run((users filter (_.id === ownerId)).result.head), Duration.Inf)

}