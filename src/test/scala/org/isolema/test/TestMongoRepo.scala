package org.isolema.test

import org.scalatest.FlatSpec
import org.isolema.domain.repository.MongoRepository
import org.scalatest.Matchers


class TestMongoRepo extends FlatSpec with Matchers {
  
  "MongoRepository" should "connect to mongo" in {
    MongoRepository.client should not be (null)
  }
  
  it should "get database" in {
    MongoRepository.database should not be (null)
  }
  
   it should "get collection" in {
    MongoRepository.hashIsomorphismsCol should not be (null)
  }
   
   "MongoRepository.findWordsLike alco" should "return 96 matching words" in {
     val result =  MongoRepository.findWordsLike("alco")
     result.isLeft should be (false)
     result.isRight should be (true)
     for { res <- result } {
       res should have size 96
     }
   }
   it should "return 2 matching words" in {
     val result =  MongoRepository.findWordsLike("romeria")
     result.isLeft should be (false)
     result.isRight should be (true)
     for { res <- result } {
       res should have size 2
     }
   }
   
}