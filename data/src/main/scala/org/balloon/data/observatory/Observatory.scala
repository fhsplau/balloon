package org.balloon.data.observatory

import java.time.LocalDateTime

import org.balloon.data.observatory.deserializer.{DataDeserializer, RawData}
import org.balloon.data.temperature.{Coordinates, Temperature}
import org.balloon.data.utils.TimeStamp

import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._

trait Observatory[T <: Observatory[T]] {
  val observatoryName: String

  def is[O <: Observatory[O] : TypeTag]: Boolean
}

trait ObservatoryData {
  val observatoryName: String

  val temperature: Temperature

  val timestamp: LocalDateTime

  val coordinates: Coordinates

  def is[O <: Observatory[O] : TypeTag]: Boolean

  def copy(temperature: Temperature = this.temperature, c: Coordinates = this.coordinates): ObservatoryData

  def serialize: String = s"${timestamp.format(TimeStamp.pattern)}|${coordinates.x.value},${coordinates.y.value}|${temperature.value}|$observatoryName"
}

case class Australia(timestamp: LocalDateTime, coordinates: Coordinates, temperature: Temperature) extends Observatory[Australia] with ObservatoryData {
  override val observatoryName: String = "AU"

  override def is[O <: Observatory[O] : universe.TypeTag]: Boolean = typeOf[Australia] =:= typeOf[O]

  override def copy(t: Temperature = this.temperature, c: Coordinates = this.coordinates): ObservatoryData = Australia(timestamp, c,t)
}

case class France(timestamp: LocalDateTime, coordinates: Coordinates, temperature: Temperature) extends Observatory[France] with ObservatoryData {
  override val observatoryName: String = "FR"

  override def is[O <: Observatory[O] : universe.TypeTag]: Boolean = typeOf[France] =:= typeOf[O]

  override def copy(t: Temperature = this.temperature, c: Coordinates = this.coordinates): ObservatoryData = France(timestamp, c,t)
}

case class UnitedStates(timestamp: LocalDateTime, coordinates: Coordinates, temperature: Temperature) extends Observatory[UnitedStates] with ObservatoryData {
  override val observatoryName: String = "US"

  override def is[O <: Observatory[O] : universe.TypeTag]: Boolean = typeOf[UnitedStates] =:= typeOf[O]

  override def copy(t: Temperature = this.temperature, c: Coordinates = this.coordinates): ObservatoryData = UnitedStates(timestamp, c,t)
}

case class Other(observatoryName: String, timestamp: LocalDateTime, coordinates: Coordinates, temperature: Temperature) extends Observatory[Other] with ObservatoryData {
  override def is[O <: Observatory[O] : universe.TypeTag]: Boolean = typeOf[Other] =:= typeOf[O]

  override def copy(t: Temperature = this.temperature, c: Coordinates = this.coordinates): ObservatoryData = Other(observatoryName, timestamp, c, t)
}

object ObservatoryData {
  def from(line: String): Option[ObservatoryData] = {
    RawData.from(line) match {
      case Some(rawData) => rawData.observatory match {
        case "AU" => DataDeserializer.deserialize[Australia](rawData)
        case "US" => DataDeserializer.deserialize[UnitedStates](rawData)
        case "FR" => DataDeserializer.deserialize[France](rawData)
        case _ => DataDeserializer.deserialize[Other](rawData)
      }
      case _ => None
    }
  }
}
