package org.balloon.reader

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{Flow, Source}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import org.balloon.data.observatory.{Observatory, ObservatoryData}
import org.balloon.data.temperature.Temperature

import scala.concurrent.Future
import scala.reflect.runtime.universe._

case class DataAnalyser(data: Graph[SourceShape[ObservatoryData], NotUsed])(implicit system: ActorSystem, materializer: ActorMaterializer) {
  import system.dispatcher

  private val source = Source.fromGraph(data)

  def filter(f: ObservatoryData => Boolean): Future[List[ObservatoryData]] =
    Source.fromGraph(data).filter(f).runFold(List[ObservatoryData]())((a, b) => b :: a)

  def numOfObservations[T <: Observatory[T] : TypeTag](f: ObservatoryData => Boolean = _ => true): Future[Int] = {
    val filter = Flow[ObservatoryData].filter(_.is[T]).filter(f)
    source.via(filter).runFold(0)((a, _) => a + 1)
  }

  def minimumTemperature: Future[Option[Temperature]] = {
    source.via(mapTemperature).runFold(Option.empty[Temperature]){(t1, t2) =>
      t1 match {
        case None => Some(t2)
        case Some(t) => t match {
          case i if (i == t2) || (i < t2) => t1
          case i if i > t2 => Some(t2)
        }
      }
    }
  }

  def maximumTemperature: Future[Option[Temperature]] = {
    source.via(mapTemperature).runFold(Option.empty[Temperature]){(t1, t2) =>
      t1 match {
        case None => Some(t2)
        case Some(t) => t match {
          case i if (i == t2) || (i > t2) => t1
          case i if i < t2 => Some(t2)
        }
      }
    }
  }

  private def mapTemperature = {
    Flow[ObservatoryData].mapAsync(2)(d => Future {
      d.temperature
    })
  }
}
