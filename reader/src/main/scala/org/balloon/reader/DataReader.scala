package org.balloon.reader

import java.nio.file.Path

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Balance, FileIO, Flow, Framing, GraphDSL, Merge, Source}
import akka.stream.{ActorMaterializer, IOResult, SourceShape}
import akka.util.ByteString
import org.balloon.data.observatory.ObservatoryData
import org.balloon.reader.stages.DataDeserializerStage

import scala.concurrent.Future


class DataReader(implicit system: ActorSystem, materializer: ActorMaterializer) {
  private val reader: Path => Source[String, Future[IOResult]] = { filename =>
    val lines = Flow[ByteString]
      .via(Framing.delimiter(ByteString(System.lineSeparator()), Int.MaxValue, allowTruncation = true))
      .map(bs => bs.utf8String)

    FileIO.fromPath(filename).via(lines)
  }

  def read(filename: Path): DataAnalyser = {
    val deserializers = (1 to 3).map(_ => new DataDeserializerStage)
    val input = reader(filename)

    val graph = GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._
      val balance = b.add(Balance[String](deserializers.length))
      val merge = b.add(Merge[ObservatoryData](deserializers.length))
      input ~> balance
      deserializers.foreach(deserializer => balance ~> deserializer ~> merge)
      SourceShape(merge.out)
    }

    DataAnalyser(graph)
  }
}
