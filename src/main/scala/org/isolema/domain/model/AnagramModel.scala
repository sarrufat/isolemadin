package org.isolema.domain.model

import org.bson.types.ObjectId
import org.mongodb.scala.Document
import org.mongodb.scala.bson._

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._


trait AnagramaElemen {
  def word: String
  def isocode: String
  def saoWord: String
}

trait AnagramModel {
  def _id: ObjectId
  def anagramas: List[AnagramaElemen]
}

object AnagramaElemen {
  def apply(doc: Document): AnagramaElemen = {
    new AnagramaElemen {
      val word = doc.getString("word")
      val isocode = doc.getString("isocode")
      val saoWord = doc.getString("saoWord")
    }
  }
}

object AnagramModel {
  def apply(doc: Document): AnagramModel = {
    new AnagramModel {
      val _id = doc.getObjectId("_id")
      val anagramas: List[AnagramaElemen] = {
        val option =  doc.get[BsonArray]("words").map ( arr => arr.getValues() ).map {  values => 
          values.map { v =>  AnagramaElemen(v.asDocument())}.toList
        }
        option.getOrElse(List())
      }
    }
  }
}