package org.balloon.data.observatory

import java.time.LocalDateTime

import org.balloon.data.temperature.{Celsius, Fahrenheit, Kelvin, Temperature}
import org.balloon.data.utils.TimeStamp
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.scalatest.junit.JUnitRunner
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, FreeSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class ObservatoryDataTest extends FreeSpec with Matchers with MockitoSugar with BeforeAndAfter {
  val tempMock: Temperature = mock[Temperature]

  before {
    Mockito.reset(tempMock)
  }

  "Data" - {
    val timestamp = TimeStamp.now
    val data: ObservatoryData = Australia(LocalDateTime.now(), tempMock)

    "is from Australia" in {
      data.is[Australia] should be(true)
    }

    "is not from France" in {
      data.is[France] should be(false)
    }

    "can be copied" - {
      "without changes" in {
        data.copy().temperature should be(tempMock)
      }

      "with changed temp" in {
        val tempMock2 = mock[Temperature]
        data.copy(tempMock2).temperature should be(tempMock2)
      }
    }

    "can be serialized" in {
      Mockito.when(tempMock.value).thenReturn(10)
      data.serialize should equal(s"${timestamp.format(TimeStamp.pattern)}|10|AU")
    }

    "from" in {
      val l: List[ObservatoryData] = List(
        Australia(TimeStamp.now, Celsius(31)),
        UnitedStates(TimeStamp.now, Fahrenheit(300)),
        France(TimeStamp.now, Kelvin(300)),
        Other("PL", TimeStamp.now, Kelvin(293))
      )

      l.map(d => d.serialize).map(line => ObservatoryData.from(line)) should equal(l.map(Some(_)))
    }
  }

}
