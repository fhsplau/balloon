package org.balloon.generator.simulator

import akka.NotUsed
import akka.stream.scaladsl.Source
import org.balloon.data.observatory.{Observatory, ObservatoryData}
import org.balloon.generator.connection.{Connection, ConnectionMaker}

import scala.concurrent.ExecutionContext

case class BalloonSimulator[L <: Observatory[L]](observatory: String, samples: Int)
                                                (implicit s: ConnectionMaker[L], ex: ExecutionContext) extends Connection {
  override def channel: Source[ObservatoryData, NotUsed] = s.getSource(samples, observatory)
}
