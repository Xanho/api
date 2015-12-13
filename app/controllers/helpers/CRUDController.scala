package controllers.helpers

import java.util.UUID

import play.api.http.ContentTypes
import play.api.libs.json.{Json, JsValue, JsObject}
import play.api.mvc.{Result, Controller, Action}
import play.api.mvc.Results._
import system.helpers.PropertyValidators.PropertyErrorCodes
import system.helpers.{ResponseHelpers, Authorized, ResourceCollection}


trait CRUDController extends Controller {

  def resourceCollection: ResourceCollection

  /**
    * An [[Action]] to create a new resource in the [[resourceCollection]]
    * @return An [[Action]]
    */
  def create =
    Authorized(None, Set(resourceCollection.canCreate), Set(ContentTypes.JSON))(request =>
      CRUDResults.create(resourceCollection, request.data)
    )

  /**
    * An [[Action]] to read the resource with the provided ID in [[resourceCollection]]
    * @param uuid @see [[system.helpers.Resource.id]]
    * @return An [[Action]]
    */
  def read(uuid: UUID) =
    Authorized(Some(uuid), Set(resourceCollection.canRead))(_ =>
      CRUDResults.read(uuid, resourceCollection)
    )

  /**
    * An [[Action]] to update the resource with the provided ID in [[resourceCollection]]
    * @param uuid @see [[system.helpers.Resource.id]]
    * @return An [[Action]]
    */
  def update(uuid: UUID) =
    Authorized(Some(uuid), Set(resourceCollection.canModify), Set(ContentTypes.JSON))(request =>
      CRUDResults.update(uuid, resourceCollection, request.data)
    )

  /**
    * An [[Action]] to delete the resource with the provided ID in [[resourceCollection]]
    * @param uuid @see [[system.helpers.Resource.id]]
    * @return An [[Action]]
    */
  def delete(uuid: UUID) =
    Authorized(Some(uuid), Set(resourceCollection.canDelete))(_ =>
      CRUDResults.delete(uuid, resourceCollection)
    )


}

object CRUDResults {

  /**
    * Creates a resource in the provided [[ResourceCollection]]
    * @param resourceCollection The [[ResourceCollection]] in which the new object is created
    * @param data A [[JsObject]] containing the data to use in the creation
    * @return [[Ok]] or [[InternalServerError]] or [[BadRequest]]
    */
  def create(resourceCollection: ResourceCollection,
             data: JsObject): Result =
    resourceCollection.validateArguments(data.as[Map[String, JsValue]]) match {
      case m if m.isEmpty =>
        resourceCollection.create(data.as[Map[String, JsValue]])
          .fold(InternalServerError(ResponseHelpers.message("Something broke.")))(item =>
            Ok(Json.toJson(item)(resourceCollection.writes))
          )
      case m =>
        BadRequest(ResponseHelpers.invalidFields(m))
    }

  /**
    * Attempts to read the resource with the given uuid in the [[ResourceCollection]]
    * @param uuid @see [[system.helpers.Resource.id]]
    * @param resourceCollection The [[ResourceCollection]] to search in
    * @return [[Ok]] or [[NotFound]]
    */
  def read(uuid: UUID,
           resourceCollection: ResourceCollection): Result =
    resourceCollection.read(uuid)
      .fold(NotFound(ResponseHelpers.message("The resource with ID %s could not be found." format uuid)))(item =>
        Ok(Json.toJson(item)(resourceCollection.writes))
      )

  /**
    * Updates a resource with the given uuid in the [[ResourceCollection]]
    * @param uuid @see [[system.helpers.Resource.id]]
    * @param resourceCollection The [[ResourceCollection]] to search/update in
    * @param data A [[JsObject]] containing the data to use in the update
    * @return [[Accepted]] or [[InternalServerError]] or [[BadRequest]]
    */
  def update(uuid: UUID,
             resourceCollection: ResourceCollection,
             data: JsObject): Result =
    resourceCollection.validateArguments(data.as[Map[String, JsValue]]) filterNot (_._2 == PropertyErrorCodes.NO_VALUE) match {
      case m if m.isEmpty =>
        if(resourceCollection.update(uuid, data.as[Map[String, JsValue]]))
          Accepted
        else
          InternalServerError(ResponseHelpers.message("Something broke."))
      case m =>
        BadRequest(ResponseHelpers.invalidFields(m))
    }

  /**
    * Deletes the resource with the given uuid in the [[ResourceCollection]]
    * @param uuid @see [[system.helpers.Resource.id]]
    * @param resourceCollection The [[ResourceCollection]] to search/delete in
    * @return [[NoContent]] or [[NotFound]]
    */
  def delete(uuid: UUID,
             resourceCollection: ResourceCollection): Result =
    if(resourceCollection.delete(uuid))
      NoContent
    else
      NotFound(ResponseHelpers.message("The resource with ID %s could not be found." format uuid))

}
