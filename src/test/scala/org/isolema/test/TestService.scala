package org.isolema.test

import org.scalatest.Matchers
import org.scalatest.FlatSpec
import org.isolema.domain.repository.MongoRepository


class TestService extends FlatSpec with Matchers {
  "HashedWordService.getWordLike" should "return proper results" in {
    val result = org.isolema.domain.HashedWordService.getWordLike("romería", true)(MongoRepository)
    result.isRight should be(true)
    for ( res <-result) res should have size(2)
  }
}