package controllers.school

import controllers.helpers.CRUDController
import models.school

class TopicRequirement extends CRUDController[school.TopicRequirements, school.TopicRequirement] {

  /**
    * @inheritdoc
    */
  def resourceCollection =
    school.TopicRequirements

}
