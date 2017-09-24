package org.balloon.data.observatory.deserializer

import java.time.LocalDateTime

import org.balloon.data.observatory._
import org.balloon.data.temperature.{Temperature, TemperatureParser}
import org.balloon.data.utils.TimeStamp

import scala.util.{Success, Try}

case class RawData(timestamp: String, temperature: String, observatory: String)

object RawData {

  def from(line: String): Option[RawData] = {
    val s: Array[String] = line.split("\\|")
    if(s.length == 3) s match {
      case Array(timestamp, temperature, observatory) => Some(RawData(timestamp, temperature, observatory))
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
    getData[Australia](rawData)(rt => TemperatureParser.parse[Australia](rt))((_, ts, t) => Australia(ts, t))
  }

  implicit val usData: DataDeserializer[UnitedStates] = (rawData: RawData) => {
    getData[UnitedStates](rawData)(rt => TemperatureParser.parse[UnitedStates](rt))((_, ts, t) => UnitedStates(ts, t))
  }

  implicit val franceData: DataDeserializer[France] = (rawData: RawData) => {
    getData[France](rawData)(rt => TemperatureParser.parse[France](rt))((_, ts, t) => France(ts, t))
  }

  implicit val otherData: DataDeserializer[Other] = (rawData: RawData) => {
    getData[Other](rawData)(rt => TemperatureParser.parse[Other](rt))((ob, ts, t) => Other(ob, ts, t))
  }

  private def getTimeStamp: String => Option[LocalDateTime] = s => Try(LocalDateTime.parse(s, TimeStamp.pattern)) match {
    case Success(time) => Some(time)
    case _ => None
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
