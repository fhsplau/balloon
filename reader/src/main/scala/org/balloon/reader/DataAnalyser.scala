package org.balloon.reader

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Source}
import akka.stream.{ActorMaterializer, Graph, SourceShape}
import org.balloon.data.observatory.{Observatory, ObservatoryData}

import scala.concurrent.Future
import scala.reflect.runtime.universe._

case class DataAnalyser(data: Graph[SourceShape[ObservatoryData], NotUsed])(implicit system: ActorSystem, materializer: ActorMaterializer) {

  def filter(f: ObservatoryData => Boolean): Future[List[ObservatoryData]] =
    Source.fromGraph(data).filter(f).runFold(List[ObservatoryData]())((a, b) => b :: a)

  def numOfObservations[T <: Observatory[T] : TypeTag](f: ObservatoryData => Boolean = _ => true): Future[Int] = {
    val source = Source.fromGraph(data)
    val filter = Flow[ObservatoryData].filter(_.is[T]).filter(f)
    source.via(filter).runFold(0)((a, _) => a + 1)
  }

}
