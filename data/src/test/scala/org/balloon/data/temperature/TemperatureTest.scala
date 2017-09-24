package org.balloon.data.temperature

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FreeSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class TemperatureTest extends FreeSpec with Matchers with BeforeAndAfter {

  "Can convert" - {
    "Celsius" - {
      val temperature: Temperature = Celsius(10)

      "to Celsius" in {
        temperature.to[Celsius] should equal(temperature)
      }

      "to Kelvin" in {
        temperature.to[Kelvin] should equal(Kelvin(283))
      }

      "to Fahrenheit" in {
        temperature.to[Fahrenheit] should equal(Fahrenheit(50))
      }
    }

    "Kelvin" - {
      val temperature: Temperature = Kelvin(283)

      "to Kelvin" in {
        temperature.to[Kelvin] should equal(temperature)
      }

      "to Celsius" in {
        temperature.to[Celsius] should equal(Celsius(10))
      }

      "to Fahrenheit" in {
        temperature.to[Fahrenheit] should equal(Fahrenheit(49))
      }
    }

    "Fahrenheit" - {
      val temperature = Fahrenheit(50)

      "to Fahrenheit" in {
        temperature.to[Fahrenheit] should equal(temperature)
      }

      "to Celsius" in {
        temperature.to[Celsius] should equal(Celsius(10))
      }

      "to Kelvin" in {
        temperature.to[Kelvin] should equal(Kelvin(283))
      }
    }
  }
}
