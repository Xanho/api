package controllers.research

import controllers.helpers.CRUDController
import models.research

class PeerReview extends CRUDController[research.PeerReviews, research.PeerReview] {

  /**
    * @inheritdoc
    */
  def resourceCollection =
    research.PeerReviews

}
