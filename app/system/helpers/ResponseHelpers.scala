package system.helpers

import play.api.libs.json.{JsObject, Json}

object ResponseHelpers {

  /**
   * Creates a message Json Object
   * @param message Response message
   * @param code Optional numeric error code (defaults to 0)
   * @return
   */
  def message(message: String,
              code: Int = 0): JsObject =
    Json.obj("message" -> message, "errorCode" -> code)

  /**
   * Creates an invalidFields Json Object,
   * which contains a list of fields that were invalid upon form input
   * @param fieldNames The names of the fields which were invalid
   * @return
   */
  def invalidFields(fieldNames: Traversable[String]): JsObject =
    Json.obj("invalidFields" -> fieldNames)

}
