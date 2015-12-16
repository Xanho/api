package controllers.school

import controllers.helpers.CRUDController
import models.school

class MicrodegreeRevision extends CRUDController[school.MicrodegreeRevisions, school.MicrodegreeRevision] {

  /**
    * @inheritdoc
    */
  def resourceCollection =
    school.MicrodegreeRevisions

}
