package controllers.school

import controllers.helpers.CRUDController
import models.school

class MicrodegreeRevisionProposal extends CRUDController[school.MicrodegreeRevisionProposals, school.MicrodegreeRevisionProposal] {

  /**
    * @inheritdoc
    */
  def resourceCollection =
    school.MicrodegreeRevisionProposals

}
