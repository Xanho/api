package controllers.school

import controllers.helpers.CRUDController
import models.school

class TopicRevision extends CRUDController[school.TopicRevisions, school.TopicRevision] {

  /**
    * @inheritdoc
    */
  def resourceCollection =
    school.TopicRevisions

}
