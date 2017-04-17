package org.isolema.domain.repository

import org.mongodb.scala.MongoClient
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.model.Filters._
import scalaz._
import scalaz.\/._
import scalaz.Scalaz._
import scala.concurrent.Await
import scala.concurrent.duration._
import org.mongodb.scala.ScalaObservable
import org.isolema.domain.model.HWordT
import org.isolema.domain.model.HashedWord
import com.typesafe.config.ConfigFactory

trait IsolemaRepository {
  def findWordsLike(like: String): \/[NonEmptyList[String], List[HWordT]]
}

object MongoRepository extends IsolemaRepository {
  private val conf = ConfigFactory.load();
  private val connectionStr = conf.getString("isolemadin.mongoConnection")
  val client: MongoClient = MongoClient(connectionStr)

  val database: MongoDatabase = client.getDatabase("isomorphic")

  val hashIsomorphismsCol = database.getCollection("hashIsomorphisms")

  def findWordsLike(like: String): \/[NonEmptyList[String], List[HWordT]] = {
    val result = hashIsomorphismsCol.find(regex("saoWord", like))
    //    result.subscribe(new Observer[Document] {
    //      override def onNext(result: Document): Unit = {}
    //      override def onError(e: Throwable): Unit = {}
    //      override def onComplete(): Unit = {}
    //    })
    val futureRes = Await.result(result.toFuture(), 20 seconds)
    val resSeq = futureRes.map { doc =>
      HashedWord(doc.getObjectId("_id"), doc.getString("word"), doc.getString("isocode"), doc.getInteger("isoCount"), doc.getString("saoWord"))
    }
    resSeq.toList.right
  }
}