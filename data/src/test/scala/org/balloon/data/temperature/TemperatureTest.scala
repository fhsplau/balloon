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

  "Can add" - {
    "Celsius to other" in {
      val t1 = Celsius(10)
      val t2 = Fahrenheit(50)

      (t1 + t2) should equal(Celsius(20))
    }

    "Kelvin to other" in {
      val t1 = Kelvin(283)
      val t2 = Celsius(10)

      (t1 + t2) should equal(Kelvin(292))
    }

    "Fahrenheit to other" in {
      val t1 = Fahrenheit(50)
      val t2 = Celsius(10)

      (t1 + t2) should equal(Fahrenheit(68))
    }
  }

  "Temperature is" - {
    "greater than temperature" - {
      "Celsius" in {
        val t1 = Celsius(20)
        val t2 = Kelvin(283)

        (t1 > t2) should be(true)
      }

      "Kelvin" in {
        val t1 = Kelvin(293)
        val t2 = Celsius(10)

        (t1 > t2) should be(true)
      }

      "Fahrenheit" in {
        val t1 = Fahrenheit(60)
        val t2 = Celsius(10)

        (t1 > t2) should be(true)
      }
    }

    "smaller than" in {
      val t1 = Celsius(10)
      val t2 = Kelvin(293)

      (t1 < t2) should be(true)
    }

    "not greater than" in {
      val t1 = Celsius(20)
      val t2 = Kelvin(283)

      (t2 > t1) should be(false)
    }

    "equal" in {
      (Celsius(10) == Kelvin(283)) should be(true)
    }

    "not equal" in {
      (Celsius(20) == Kelvin(273)) should be(false)
    }


  }
}
