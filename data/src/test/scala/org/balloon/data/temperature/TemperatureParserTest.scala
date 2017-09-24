package org.balloon.data.temperature

import org.balloon.data.observatory.{Australia, France, Other, UnitedStates}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FreeSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class TemperatureParserTest extends FreeSpec with Matchers with MockitoSugar {
  val correctTemperature = "10"
  val incorrectTemperature = "s"
  "Can parse" - {
    "temperature string" - {
      "to temperature in" - {

        "Australia" in {
          TemperatureParser.parse[Australia](correctTemperature) should equal(Some(Celsius(10)))
        }

        "United States" in {
          TemperatureParser.parse[UnitedStates](correctTemperature) should equal(Some(Fahrenheit(10)))
        }

        "France" in {
          TemperatureParser.parse[France](correctTemperature) should equal(Some(Kelvin(10)))
        }

        "Other" in {
          TemperatureParser.parse[Other](correctTemperature) should equal(Some(Kelvin(10)))
        }
      }
    }
  }

  "Can't parse incorrect string" - {
    "to temperature in" - {
      "Australia" in {
        TemperatureParser.parse[Australia](incorrectTemperature) should be(None)
      }

      "United States" in {
        TemperatureParser.parse[UnitedStates](incorrectTemperature) should be(None)
      }

      "France" in {
        TemperatureParser.parse[France](incorrectTemperature) should be(None)
      }

      "Other" in {
        TemperatureParser.parse[Other](incorrectTemperature) should be(None)
      }
    }
  }

}
