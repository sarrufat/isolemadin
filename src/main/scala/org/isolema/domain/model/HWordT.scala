package org.isolema.domain.model

import org.bson.types.ObjectId

// Base contract of HashedWord

sealed trait HWordT {
  def _id: ObjectId
  // word with spelling accents
  def word: String
  // Isomorphic code
  def isocode: String
  // numbers of words -1 sharing the same isocode 
  def isoCount: Int
  // Without spelling accents
  def saoWord: String

  def getPreMidSuf(search: String)(render: (String, String, String) => String): String

  def decomposeWordByOccur() = {
    val codeZip = saoWord.zipWithIndex
    val result = for (cz <- codeZip) yield {
      if (saoWord.drop(cz._2 + 1).contains(cz._1) || saoWord.take(cz._2).contains(cz._1)) saoWord.charAt(cz._2)
      else '_'
    }
    result.mkString
  }
}

final case class HashedWord(_id: ObjectId, word: String, isocode: String, isoCount: Int, saoWord: String) extends HWordT {
  def getPreMidSuf(search: String)(render: (String, String, String) => String): String = {
    val idx = saoWord.indexOfSlice(search)
    val idxm = idx + search.length()
    render(word.slice(0, idx), word.slice(idx, idxm), word.slice(idxm, word.length()))
  }
}
