package controllers.school

import controllers.helpers.CRUDController
import models.school

class Topic extends CRUDController[school.Topics, school.Topic] {

  /**
    * @inheritdoc
    */
  def resourceCollection =
    school.Topics

}
