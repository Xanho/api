package controllers.school

import controllers.helpers.CRUDController
import models.school

class TopicRevisionProposal extends CRUDController[school.TopicRevisionProposals, school.TopicRevisionProposal] {

  /**
    * @inheritdoc
    */
  def resourceCollection =
    school.TopicRevisionProposals

}
