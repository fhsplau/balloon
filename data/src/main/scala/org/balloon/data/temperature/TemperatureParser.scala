package org.balloon.data.temperature

import org.balloon.data.observatory._

import scala.util.{Success, Try}

trait TemperatureParser[O <: Observatory[O]] {
  def parse(s: String): Option[Temperature]
}

object TemperatureParser {
  def parse[O <: Observatory[O]](v: String)(implicit tp: TemperatureParser[O]): Option[Temperature] = tp.parse(v)

  implicit val australiaTemp: TemperatureParser[Australia] = (s: String) => tryToParse(Celsius(s.toInt))

  implicit val usTemp: TemperatureParser[UnitedStates] = (s: String) => tryToParse(Fahrenheit(s.toInt))

  implicit val franceTemp: TemperatureParser[France] = (s: String) => tryToParse(Kelvin(s.toInt))

  implicit val other: TemperatureParser[Other] = (s: String) => tryToParse(Kelvin(s.toInt))

  private def tryToParse(to: => Temperature): Option[Temperature] = Try(to) match {
    case Success(t) => Some(t)
    case _ => None
  }
}
