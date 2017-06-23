package org.isolema.domain.repository

import scala.concurrent.Await
import scala.concurrent.duration._

import org.isolema.domain.model.HWordT
import org.isolema.domain.model.HashedWord
import org.isolema.util.HashIsomorphism
import org.mongodb.scala.MongoClient
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.ScalaObservable
import org.mongodb.scala.model.Filters._

import com.typesafe.config.ConfigFactory

import scalaz._
import scalaz.Scalaz._
import org.isolema.util.MapOrtographicAccent

trait IsolemaRepository {
  def findWordsLike(like: String): \/[NonEmptyList[String], List[HWordT]]
  def getIsomorphisms(word: String): \/[NonEmptyList[String], List[HWordT]]
}

object MongoRepository extends IsolemaRepository {
  private val conf = ConfigFactory.load();
  private val connectionStr = conf.getString("isolemadin.mongoConnection")
  val client: MongoClient = MongoClient(connectionStr)

  val database: MongoDatabase = client.getDatabase("isomorphic")

  val hashIsomorphismsCol = database.getCollection("hashIsomorphisms")

  def findWordsLike(like: String): \/[NonEmptyList[String], List[HWordT]] = {
    val mappedLike = MapOrtographicAccent.mapword(like)
    val result = hashIsomorphismsCol.find(regex("saoWord", mappedLike))
    val futureRes = Await.result(result.toFuture(), 20 seconds)
    val resSeq = futureRes.map { doc ⇒
      HashedWord(doc.getObjectId("_id"), doc.getString("word"), doc.getString("isocode"), doc.getInteger("isoCount"), doc.getString("saoWord"), doc.getString("form"))
    }
    resSeq.toList.right
  }
  def getIsomorphisms(word: String): \/[NonEmptyList[String], List[HWordT]] = {
    val code = HashIsomorphism.hashingWord(word)
    val result = hashIsomorphismsCol.find(equal("isocode", code))
    val futureRes = Await.result(result.toFuture(), 20 seconds)
    val resSeq = futureRes.map { doc ⇒
      HashedWord(doc.getObjectId("_id"), doc.getString("word"), doc.getString("isocode"), doc.getInteger("isoCount"), doc.getString("saoWord"),  doc.getString("form"))
    }
    resSeq.toList.right
  }
}