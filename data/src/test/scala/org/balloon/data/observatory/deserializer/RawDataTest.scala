package org.balloon.data.observatory.deserializer

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FreeSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class RawDataTest extends FreeSpec with Matchers {

  "Raw data" - {
    "returns" -{
      "raw data if line contains all the information" in {
        val line: String = "2017-10-29:13:21:45|10|AU"
        RawData.from(line) should be(Some(RawData("2017-10-29:13:21:45", "10", "AU")))
      }

      "None is line doesn't contains all the information" in {
        val line: String = "2017-10-29:13:21:45|10"
        RawData.from(line) should be(None)
      }
    }
  }

}
