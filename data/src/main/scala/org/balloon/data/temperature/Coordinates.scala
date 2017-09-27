package org.balloon.data.temperature

import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._

trait DistanceUnit

trait Distance {
  val value: Double
  val shortName: String

  def to[D <: DistanceUnit : TypeTag]: Distance
}

case class Kilometers(value: Double) extends Distance with DistanceUnit {
  override val shortName = "KM"

  override def to[D <: DistanceUnit : universe.TypeTag]: Distance = typeOf[D] match {
    case t if t =:= typeOf[Kilometers] => this
    case t if t =:= typeOf[Meters] => Meters(this.value / 1000)
    case t if t =:= typeOf[Miles] => Miles(value * 0.6213)
  }
}

case class Miles(value: Double) extends Distance with DistanceUnit {
  override val shortName = "M"

  override def to[D <: DistanceUnit : universe.TypeTag]: Distance = typeOf[D] match {
    case t if t =:= typeOf[Miles] => this
    case t if t =:= typeOf[Meters] => Meters(value * 1.609 * 1000)
    case t if t =:= typeOf[Kilometers] => Kilometers(value * 1.609)
  }
}

case class Meters(value: Double) extends Distance with DistanceUnit {
  override val shortName = "m"

  override def to[D <: DistanceUnit : universe.TypeTag]: Distance = typeOf[D] match {
    case t if t =:= typeOf[Meters] => this
    case t if t =:= typeOf[Kilometers] => Kilometers(value / 1000)
    case t if t =:= typeOf[Miles] => Miles((value / 1000) * 0.6213)
  }
}

case class Coordinates(x: Distance, y: Distance) {
  def to[T <: DistanceUnit: TypeTag]: Coordinates = Coordinates(x.to[T], y.to[T])
}
