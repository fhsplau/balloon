package org.balloon.data.observatory.deserializer

import java.time.LocalDateTime

import org.balloon.data.observatory.{Australia, France, Other, UnitedStates}
import org.balloon.data.temperature._
import org.balloon.data.utils.TimeStamp
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.scalatest.junit.JUnitRunner
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, FreeSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class DataDeserializerTest extends FreeSpec with Matchers with MockitoSugar with BeforeAndAfter{

  "Can deserialize raw data" - {
    "to data from" - {
      val timestamp: LocalDateTime = LocalDateTime.parse("2014-12-31T13:44:45", TimeStamp.pattern)
      "Australia" in {
        val rawData: RawData = mock[RawData]
        Mockito.when(rawData.timestamp).thenReturn("2014-12-31T13:44:45")
        Mockito.when(rawData.temperature).thenReturn("10")
        Mockito.when(rawData.observatory).thenReturn("AU")
        Mockito.when(rawData.coordinates).thenReturn("10,5")
        DataDeserializer.deserialize[Australia](rawData) should equal(Some(Australia(timestamp, Coordinates(Kilometers(10), Kilometers(5)), Celsius(10))))
      }

      "United States" in {
        val rawData: RawData = mock[RawData]
        Mockito.when(rawData.timestamp).thenReturn("2014-12-31T13:44:45")
        Mockito.when(rawData.temperature).thenReturn("10")
        Mockito.when(rawData.observatory).thenReturn("US")
        Mockito.when(rawData.coordinates).thenReturn("10,5")
        DataDeserializer.deserialize[UnitedStates](rawData) should equal(Some(UnitedStates(timestamp, Coordinates(Miles(10), Miles(5)), Fahrenheit(10))))
      }

      "France" in {
        val rawData: RawData = mock[RawData]
        Mockito.when(rawData.timestamp).thenReturn("2014-12-31T13:44:45")
        Mockito.when(rawData.temperature).thenReturn("10")
        Mockito.when(rawData.observatory).thenReturn("FR")
        Mockito.when(rawData.coordinates).thenReturn("10,5")
        DataDeserializer.deserialize[France](rawData) should equal(Some(France(timestamp, Coordinates(Meters(10), Meters(5)), Kelvin(10))))
      }

      "Other" in {
        val rawData: RawData = mock[RawData]
        Mockito.when(rawData.timestamp).thenReturn("2014-12-31T13:44:45")
        Mockito.when(rawData.temperature).thenReturn("10")
        Mockito.when(rawData.observatory).thenReturn("PL")
        Mockito.when(rawData.coordinates).thenReturn("10,5")
        DataDeserializer.deserialize[Other](rawData) should equal(Some(Other("PL", timestamp, Coordinates(Kilometers(10), Kilometers(5)), Kelvin(10))))
      }
    }
  }

  "Can't deserialize" -{
    "when timestamp has wrong format" in {
      val rawData: RawData = mock[RawData]
      Mockito.when(rawData.timestamp).thenReturn("2014-12-31T13:44")
      Mockito.when(rawData.temperature).thenReturn("10")
      Mockito.when(rawData.observatory).thenReturn("AU")
      Mockito.when(rawData.coordinates).thenReturn("10,5")

      DataDeserializer.deserialize[Australia](rawData) should be(None)
    }

    "when temperature is incorrect" in {
      val rawData: RawData = mock[RawData]
      Mockito.when(rawData.timestamp).thenReturn("2014-12-31T13:44:45")
      Mockito.when(rawData.temperature).thenReturn("s")
      Mockito.when(rawData.observatory).thenReturn("AU")
      Mockito.when(rawData.coordinates).thenReturn("10,5")

      DataDeserializer.deserialize[Australia](rawData) should be(None)
    }

  }

}
