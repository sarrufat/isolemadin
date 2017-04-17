package org.isolema.test

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.isolema.domain.model.HashedWord
import org.bson.types.ObjectId
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

object Util {
  def renderWord(pre: String, mid: String, suf: String): String = pre + mid + suf
}

class Functions extends FlatSpec with Matchers {
  "getFixes" should "decompose bebible by ible" in {
    val hw = HashedWord(new ObjectId("58b870337a74b9010f395a66"), "bebible", "0102031", 1, "bebible")
    val res = hw.getPreMidSuf("ible")(Util.renderWord)
    res should be(hw.word)
  }
  it should "decompose adáraga by ara" in {
    val hw = HashedWord(new ObjectId("58b870337a74b9010f393103"), "adáraga", "0102030", 21, "adaraga")
    val res = hw.getPreMidSuf("ara")(Util.renderWord)
    res should be(hw.word)
  }

  it should "decompose atalayero by atal" in {
    val hw = HashedWord(new ObjectId("58b870337a74b9010f395080"), "atalayero", "010203456", 20, "atalayero")
    val res = hw.getPreMidSuf("atal")(Util.renderWord)
    res should be(hw.word)
  }

  "ConfigApp" should "get properties" in {
    val conf = ConfigFactory.load();
    conf.getString("isolemadin.mongoConnection") should be("mongodb://li518-4.members.linode.com:27017")
  }
}