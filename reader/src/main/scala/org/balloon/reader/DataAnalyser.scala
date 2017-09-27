package org.balloon.reader

import java.nio.file.Path

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{FileIO, Flow, GraphDSL, Keep, RunnableGraph, Sink, Source, Zip}
import akka.util.ByteString
import org.balloon.data.observatory.{Observatory, ObservatoryData}
import org.balloon.data.temperature.{Celsius, Temperature, TemperatureScale}

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
    source.via(mapTemperature).runFold(Option.empty[Temperature]) { (t1, t2) =>
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
    source.via(mapTemperature).runFold(Option.empty[Temperature]) { (t1, t2) =>
      t1 match {
        case None => Some(t2)
        case Some(t) => t match {
          case i if (i == t2) || (i > t2) => t1
          case i if i < t2 => Some(t2)
        }
      }
    }
  }

  def meanTemperature[T <: TemperatureScale : TypeTag]: Future[Option[Temperature]] = {
    val toCelsius = Flow[Temperature].map(_.to[Celsius])
    val sum: Flow[Temperature, Option[Temperature], NotUsed] = Flow[Temperature].fold(Option.empty[Temperature]) { (t1, t2) =>
      t1 match {
        case None => Some(t2)
        case Some(t) => Some(t + t2)
      }
    }

    val count: Flow[Temperature, Int, NotUsed] = Flow[Temperature].fold(0)((a, _) => a + 1)

    val celsiusSource = source.via(mapTemperature).via(toCelsius)

    val graph = Source.fromGraph(GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._
      val zip = b.add(Zip[Option[Temperature], Int])

      celsiusSource ~> sum ~> zip.in0
      celsiusSource ~> count ~> zip.in1

      SourceShape(zip.out)
    })

    graph.map(m => if (m._1.isEmpty) None else Some(Celsius(m._1.get.value / m._2).to[T])).runWith(Sink.head)
  }

  def save(filename: Path, m: ObservatoryData => ObservatoryData): Future[IOResult] = {
    val writer = Flow[String].map(s => ByteString(s + "\n")).toMat(FileIO.toPath(filename))(Keep.right)
    val serialize = Flow[ObservatoryData].map(d =>
      s"${d.timestamp}|${d.coordinates.x.value}${d.coordinates.x.shortName},${d.coordinates.y.value}${d.coordinates.x.shortName}|${d.temperature.value}${d.temperature.shortName}|${d.observatoryName}")
    val mapper = Flow[ObservatoryData].map(m)

    source.via(mapper).via(serialize).runWith(writer)
  }

  private def mapTemperature = {
    Flow[ObservatoryData].mapAsync(2)(d => Future {
      d.temperature
    })
  }
}
