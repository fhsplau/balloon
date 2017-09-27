package org.balloon.data.observatory.deserializer

import java.time.LocalDateTime

import org.balloon.data.observatory._
import org.balloon.data.temperature._
import org.balloon.data.utils.TimeStamp

import scala.util.{Success, Try}

case class RawData(timestamp: String, coordinates: String, temperature: String, observatory: String)

object RawData {

  def from(line: String): Option[RawData] = {
    val s: Array[String] = line.split("\\|")
    if (s.length == 4) s match {
      case Array(timestamp, coordinates, temperature, observatory) =>
        Some(RawData(timestamp, coordinates, temperature, observatory))
    }
    else None
  }

}

trait DataDeserializer[T <: ObservatoryData] {
  def deserialize(data: RawData): Option[T]
}

object DataDeserializer {
  def deserialize[T <: ObservatoryData](data: RawData)(implicit dd: DataDeserializer[T]): Option[T] = dd.deserialize(data)

  implicit val australiaData: DataDeserializer[Australia] = (rawData: RawData) => {
    val c: Coordinates = getCoordinates(rawData.coordinates, (x, y) => Coordinates(Kilometers(x), Kilometers(y)))
    getData[Australia](rawData)(rt => TemperatureParser.parse[Australia](rt))((_, ts, t) => Australia(ts, c, t))
  }

  implicit val usData: DataDeserializer[UnitedStates] = (rawData: RawData) => {
    val c: Coordinates = getCoordinates(rawData.coordinates, (x, y) => Coordinates(Miles(x), Miles(y)))
    getData[UnitedStates](rawData)(rt => TemperatureParser.parse[UnitedStates](rt))((_, ts, t) => UnitedStates(ts, c, t))
  }

  implicit val franceData: DataDeserializer[France] = (rawData: RawData) => {
    val c: Coordinates = getCoordinates(rawData.coordinates, (x, y) => Coordinates(Meters(x), Meters(y)))
    getData[France](rawData)(rt => TemperatureParser.parse[France](rt))((_, ts, t) => France(ts, c, t))
  }

  implicit val otherData: DataDeserializer[Other] = (rawData: RawData) => {
    val c: Coordinates = getCoordinates(rawData.coordinates, (x, y) => Coordinates(Kilometers(x), Kilometers(y)))
    getData[Other](rawData)(rt => TemperatureParser.parse[Other](rt))((ob, ts, t) => Other(ob, ts, c, t))
  }

  private def getTimeStamp: String => Option[LocalDateTime] = s => Try(LocalDateTime.parse(s, TimeStamp.pattern)) match {
    case Success(time) => Some(time)
    case _ => None
  }

  private def getCoordinates(raw: String, f: (Double, Double) => Coordinates): Coordinates =
    raw.split(",") match {
      case Array(x, y) => f(x.toDouble, y.toDouble)
    }

  private def getData[D <: ObservatoryData](rawData: RawData)
                                           (temperatureParser: String => Option[Temperature])
                                           (result: (String, LocalDateTime, Temperature) => D): Option[D] = {
    temperatureParser(rawData.temperature) match {
      case Some(temperature) => getTimeStamp(rawData.timestamp) match {
        case Some(timestamp) => Some(result(rawData.observatory, timestamp, temperature))
        case _ => None
      }
      case _ => None
    }
  }


}
