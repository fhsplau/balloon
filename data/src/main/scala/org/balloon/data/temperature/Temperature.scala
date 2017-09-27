package org.balloon.data.temperature

import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._

trait TemperatureScale

trait Temperature {
  val value: Int

  def to[T <: TemperatureScale : TypeTag]: Temperature

  def +[T <: Temperature : TypeTag](that: T): Temperature

  def >[T <: Temperature : TypeTag](that: T): Boolean

  def <[T <: Temperature : TypeTag](that: T): Boolean = that > this

  def ==[T <: Temperature : TypeTag](that: T): Boolean = !((this > that) || (this < that))
}

//Only for testing
object EmptyTemperature extends Temperature {
  override lazy val value = throw new Exception("Empty temperature")

  override def to[T <: TemperatureScale : universe.TypeTag]: Temperature = this

  override def +[T <: Temperature : universe.TypeTag](that: T): Temperature = that

  override def >[T <: Temperature : universe.TypeTag](that: T): Boolean = false
}

case class Celsius(value: Int) extends TemperatureScale with Temperature {
  override def to[T <: TemperatureScale : universe.TypeTag]: Temperature = typeOf[T] match {
    case t if t =:= typeOf[Celsius] => this
    case t if t =:= typeOf[Kelvin] => Kelvin(Math.round(value + 273.15).toInt)
    case t if t =:= typeOf[Fahrenheit] => Fahrenheit(Math.round((value * 1.8) + 32).toInt)
  }

  override def +[T <: Temperature : universe.TypeTag](that: T): Temperature =
    Celsius(that.to[Celsius].value + value)

  override def >[T <: Temperature : universe.TypeTag](that: T): Boolean = value > that.to[Celsius].value
}

case class Kelvin(value: Int) extends TemperatureScale with Temperature {
  override def to[T <: TemperatureScale : universe.TypeTag]: Temperature = typeOf[T] match {
    case t if t =:= typeOf[Kelvin] => this
    case t if t =:= typeOf[Celsius] => Celsius(Math.round(value - 273.15).toInt)
    case t if t =:= typeOf[Fahrenheit] => Fahrenheit(Math.round(((value * 9) / 5) - 459.67).toInt)
  }

  override def +[T <: Temperature : universe.TypeTag](that: T): Temperature =
    Kelvin((that.to[Kelvin].value + value - 273.15).toInt)

  override def >[T <: Temperature : universe.TypeTag](that: T): Boolean = this.to[Celsius] > that.to[Celsius]
}

case class Fahrenheit(value: Int) extends TemperatureScale with Temperature {
  override def to[T <: TemperatureScale : universe.TypeTag]: Temperature = typeOf[T] match {
    case t if t =:= typeOf[Fahrenheit] => this
    case t if t =:= typeOf[Celsius] => Celsius(Math.round((value - 32) / 1.8).toInt)
    case t if t =:= typeOf[Kelvin] => Kelvin(Math.round(((value + 459.67) * 5) / 9).toInt)
  }

  override def +[T <: Temperature : universe.TypeTag](that: T): Temperature =
    (this.to[Celsius] + that.to[Celsius]).to[Fahrenheit]

  override def >[T <: Temperature : universe.TypeTag](that: T): Boolean = this.to[Celsius] > that.to[Celsius]
}
