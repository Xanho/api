package controllers.research

import controllers.helpers.CRUDController
import models.research

class Critique extends CRUDController[research.Critiques, research.Critique] {

  /**
    * @inheritdoc
    */
  def resourceCollection =
    research.Critiques

}
