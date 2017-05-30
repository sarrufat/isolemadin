package org.isolema.util

object HashIsomorphism {
  def hashingWord(word: String): String = {
    val wwacc = MapOrtographicAccent.mapword(word)
    var letters = Seq[Char]()
    for (ch ← wwacc; if (!letters.contains(ch)))
      letters = letters :+ ch
    wwacc.map { ch ⇒ ('0' + letters.indexWhere(_ == ch)).toChar }
  }
  def decomposeWordByCode(pword: String) = {
    val word = MapOrtographicAccent.mapword(pword)
    val codeZip = word.zipWithIndex
    val result = for (cz ← codeZip) yield {
      if (word.drop(cz._2 + 1).contains(cz._1) || word.take(cz._2).contains(cz._1)) word.charAt(cz._2)
      else '_'
    }
    result.mkString
  }
}

object MapOrtographicAccent {
  private val mapChar = Map('á' -> 'a', 'é' -> 'e', 'í' -> 'i', 'ó' -> 'o', 'ú' -> 'u')
  def mapword(word: String) = word.map { ch =>
    mapChar get ch match {
      case Some(c) => c
      case None    => ch
    }
  }
}