package system.helpers

import java.util.UUID

import models.helpers.Ownable

/**
  * Represents a specific, identifiable application resource.  This trait dictates that
  * its implementers define read, modify, and delete rules
  */
trait Resource {

  /**
    * All resources must have a unique identifier
    * @return a UUID
    */
  def id: UUID

  /**
    * Dictates if the user with the given ID is allowed READ access to this resource
    * @param userId @see [[models.User.id]]
    * @return true if authorized, false if unauthorized
    */
  def canRead(userId: Option[UUID]): Boolean

  /**
    * Dictates if the user with the given ID is allowed MODIFY access to this resource
    * @param userId @see [[models.User.id]]
    * @return true if authorized, false if unauthorized
    */
  def canModify(userId: Option[UUID]): Boolean

  /**
    * Dictates if the user with the given ID is allowed DELETE access to this resource
    * @param userId @see [[models.User.id]]
    * @return true if authorized, false if unauthorized
    */
  def canDelete(userId: Option[UUID]): Boolean

}

/**
  * Represents a [[Resource]] that is visible to the public
  */
trait PubliclyReadable {
  self: Resource =>

  /**
    * @inheritdoc
    */
  def canRead(id: Option[UUID]) =
    true

}

/**
  * Represents a [[Resource]] that can be modified by the public
  */
trait PubliclyModifiable {
  self: Resource =>

  /**
    * @inheritdoc
    */
  def canModify(id: Option[UUID]) =
    true
}

/**
  * Represents a [[Resource]] that can be deleted by the public
  */
trait PubliclyDeletable {
  self: Resource =>

  /**
    * @inheritdoc
    */
  def canDelete(id: Option[UUID]) =
    true
}

/**
  * Represents a [[Resource]] that can be viewed by its owner
  */
trait OwnerReadable {
  self: Resource with Ownable =>

  /**
    * @inheritdoc
    */
  def canRead(id: Option[UUID]) =
    id
      .fold(false)(_ == ownerId)

}

/**
  * Represents a [[Resource]] that can be modified by its owner
  */
trait OwnerModifiable {
  self: Resource with Ownable =>

  /**
    * @inheritdoc
    */
  def canModify(id: Option[UUID]) =
    id
      .fold(false)(_ == ownerId)

}

/**
  * Represents a [[Resource]] that can be deleted by its owner
  */
trait OwnerDeletable {
  self: Resource with Ownable =>

  /**
    * @inheritdoc
    */
  def canDelete(id: Option[UUID]) =
    id
      .fold(false)(_ == ownerId)

}

/**
  * Represents a container or collection of [[Resource]]s.  In essence, this allows requesting authorization
  * on a subset or the collection as a whole.
  */
trait ResourceCollection {

  /**
    * Dictates if the user with the given ID is allowed READ access to the provided resources
    * @param userId @see [[models.User.id]]
    * @param resources The resources of interest.  An empty list assumes
    *                  there is a request for ALL resources in this collection
    * @return true if authorized, false if unauthorized
    */
  def canRead(userId: Option[UUID],
              resources: Resource*): Boolean

  /**
    * Dictates if the user with the given ID is allowed MODIFY access to the provided resources
    * @param userId @see [[models.User.id]]
    * @param resources The resources of interest.  An empty list assumes
    *                  there is a request for ALL resources in this collection
    * @return true if authorized, false if unauthorized
    */
  def canModify(userId: Option[UUID],
                resources: Resource*): Boolean

  /**
    * Dictates if the user with the given ID is allowed DELETE access to the provided resources
    * @param userId @see [[models.User.id]]
    * @param resources The resources of interest.  An empty list assumes
    *                  there is a request for ALL resources in this collection
    * @return true if authorized, false if unauthorized
    */
  def canDelete(userId: Option[UUID],
                resources: Resource*): Boolean

  /**
    * Dictates if the user with the given ID is allowed CREATE access in this collection
    * @param userId @see [[models.User.id]]
    * @return true if authorized, false if unauthorized
    */
  def canCreate(userId: Option[UUID]): Boolean

}
