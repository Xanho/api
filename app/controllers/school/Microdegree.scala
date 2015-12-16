package controllers.school

import controllers.helpers.CRUDController
import models.school

class Microdegree extends CRUDController[school.Microdegrees, school.Microdegree] {

  /**
    * @inheritdoc
    */
  def resourceCollection =
    school.Microdegrees

}
