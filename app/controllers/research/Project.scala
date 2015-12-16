package controllers.research

import controllers.helpers.CRUDController
import models.research

class Project extends CRUDController[research.Projects, research.Project] {

  /**
    * @inheritdoc
    */
  def resourceCollection =
    research.Projects

}
