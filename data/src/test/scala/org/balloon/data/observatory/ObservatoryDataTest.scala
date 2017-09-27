package org.balloon.data.observatory

import java.time.LocalDateTime

import org.balloon.data.temperature._
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
    val data: ObservatoryData = Australia(LocalDateTime.now(),Coordinates(Kilometers(10), Kilometers(5)), tempMock)

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
      data.serialize should equal(s"${timestamp.format(TimeStamp.pattern)}|10.0,5.0|10|AU")
    }

    "from" in {
      val l: List[ObservatoryData] = List(
        Australia(TimeStamp.now, Coordinates(Kilometers(10), Kilometers(5)), Celsius(31)),
        UnitedStates(TimeStamp.now, Coordinates(Miles(10), Miles(5)),Fahrenheit(300)),
        France(TimeStamp.now, Coordinates(Meters(10), Meters(5)), Kelvin(300)),
        Other("PL", TimeStamp.now, Coordinates(Kilometers(10), Kilometers(5)), Kelvin(293))
      )

      l.map(d => d.serialize).map(line => ObservatoryData.from(line)) should equal(l.map(Some(_)))
    }
  }

}
