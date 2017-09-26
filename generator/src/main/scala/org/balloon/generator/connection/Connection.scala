package org.balloon.generator.connection

import akka.NotUsed
import akka.stream.scaladsl.Source
import org.balloon.data.observatory._
import org.balloon.data.temperature.{Celsius, Fahrenheit, Kelvin}
import org.balloon.data.utils.TimeStamp
import org.balloon.generator.simulator.BalloonSimulator

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

trait ConnectionMaker[L <: Observatory[L]] {
  def getSource(n: Int, ob: String)(implicit ec: ExecutionContext): Source[ObservatoryData, NotUsed]
}

object ConnectionMaker {
  private val r = Random

  private def temperature = Celsius(r.nextInt(50))

  implicit val australiaConnection: ConnectionMaker[Australia] = new ConnectionMaker[Australia] {
    override def getSource(n: Int, ob: String)(implicit ec: ExecutionContext): Source[ObservatoryData, NotUsed] =
      Source(1 to n).mapAsync(2)(_ => Future(Australia(TimeStamp.now, temperature.to[Celsius])))
  }
  implicit val usConnection: ConnectionMaker[UnitedStates] = new ConnectionMaker[UnitedStates] {
    override def getSource(n: Int, ob: String)(implicit ec: ExecutionContext): Source[ObservatoryData, NotUsed] =
      Source(1 to n).mapAsync(2)(_ => Future(UnitedStates(TimeStamp.now, temperature.to[Fahrenheit])))
  }
  implicit val franceConnection: ConnectionMaker[France] = new ConnectionMaker[France] {
    override def getSource(n: Int, ob: String)(implicit ec: ExecutionContext): Source[ObservatoryData, NotUsed] =
      Source(1 to n).mapAsync(2)(_ => Future(UnitedStates(TimeStamp.now, temperature.to[Kelvin])))
  }
  implicit val otherConnection: ConnectionMaker[Other] = new ConnectionMaker[Other] {
    override def getSource(n: Int, ob: String)(implicit ec: ExecutionContext): Source[ObservatoryData, NotUsed] =
      Source(1 to n).mapAsync(2)(_ => Future(Other(ob, TimeStamp.now, temperature.to[Kelvin])))
  }
}

trait Connection {
  def channel: Source[ObservatoryData, NotUsed]
}

object Connection {
  def to(observatory: String, samples: Int)(implicit ec: ExecutionContext): Connection = observatory match {
    case "AU" => BalloonSimulator[Australia](observatory, samples)
    case "FR" => BalloonSimulator[France](observatory, samples)
    case "US" => BalloonSimulator[UnitedStates](observatory, samples)
    case _ => BalloonSimulator[Other](observatory, samples)
  }
}
