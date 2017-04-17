package org.isolema.domain

import scala.util.Try
import org.bson.types.ObjectId

import scalaz._
import Scalaz._
import Kleisli._
import \/._
import org.isolema.domain.repository.IsolemaRepository
import org.isolema.domain.model.HWordT


/*
 *   HashedWordServiceModule with supported operations
 */
trait HashedWordService[HW] {
  type HWordOperation[A] = Kleisli[Valid,IsolemaRepository,A]
  def getWordLike(likestr:String) : HWordOperation[List[HW]]
}


class HashedWordServiceImpl extends HashedWordService[HWordT] {
    def getWordLike(likestr:String) = kleisli[Valid, IsolemaRepository, List[HWordT]] { (repo: IsolemaRepository) =>
      repo.findWordsLike(likestr) match {
        case \/-(rlist) => rlist.right
         case -\/(_) => NonEmptyList("Any result").left
      }
    }
}

// Singleton implementation
object HashedWordService extends HashedWordServiceImpl