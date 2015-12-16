package controllers.research

import controllers.helpers.CRUDController
import models.research

class ProjectDraft extends CRUDController[research.ProjectDrafts, research.ProjectDraft] {

  /**
    * @inheritdoc
    */
  def resourceCollection =
    research.ProjectDrafts

}
