package org.isolema.domain.service

import scalaz._
import Scalaz._
import Kleisli._
import \/._
import org.isolema.domain._
import org.isolema.domain.model.AnagramModel
import org.isolema.domain.repository.AnagramaRepository

trait AnagramaService {
  type AnagramaOperation = Kleisli[Valid, AnagramaRepository, List[AnagramModel]]

  def getAnamgramas(): AnagramaOperation
}

class AnagramaServiceImpl extends AnagramaService {
  def getAnamgramas(): AnagramaOperation = kleisli[Valid, AnagramaRepository, List[AnagramModel]] { (repo: AnagramaRepository) ⇒
    repo.getAnamgramas() match {
      case \/-(rlist) ⇒ rlist.right
      case -\/(_)     ⇒ NonEmptyList("No result").left
    }
  }
}

object AnagramaService extends AnagramaServiceImpl