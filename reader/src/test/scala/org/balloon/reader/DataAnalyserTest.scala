package org.balloon.reader

import java.time.LocalDateTime

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{GraphDSL, Merge, Source}
import akka.stream.{ActorMaterializer, Graph, SourceShape}
import akka.testkit.TestKit
import org.balloon.data.observatory.{Other, _}
import org.balloon.data.temperature._
import org.balloon.data.utils.TimeStamp
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSuiteLike, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class DataAnalyserTest extends TestKit(ActorSystem("DataDeserializerTestSystem")) with FunSuiteLike with Matchers {
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val timestamp: LocalDateTime = TimeStamp.now
  private val observations = List(
    Australia(timestamp, Celsius(20)),
    UnitedStates(timestamp, Fahrenheit(50)),
    France(timestamp, Kelvin(283)),
    Other("PL", timestamp, Kelvin(0)),
    Other("DE", timestamp, Kelvin(283))
  )

  val source = Source(observations)

  val graph: Graph[SourceShape[ObservatoryData], NotUsed] = GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._
    val merge = b.add(Merge[ObservatoryData](1))
    source ~> merge
    SourceShape(merge.out)
  }

  val dataAnalyser = DataAnalyser(graph)

  test("filter source") {
    val l: List[ObservatoryData] = Await.result(dataAnalyser.filter(_.is[Australia]), 2 seconds)
    l should equal(List(Australia(timestamp, Celsius(20))))
  }

  test("number of observations") {
    val n: Int = Await.result(dataAnalyser.numOfObservations[Australia](), 2 seconds)
    n should equal(1)
  }

  test("number of observations with predicate") {
    val n: Int = Await.result(dataAnalyser.numOfObservations[Other](_.observatoryName == "PL"), 2 seconds)
    n should equal(1)
  }

  test("0 if no observations") {
    val n: Int = Await.result(dataAnalyser.numOfObservations[Other](_.observatoryName == "GB"), 2 seconds)
    n should equal(0)
  }

  test("minimum temperature") {
    val t: Option[Temperature] = Await.result(dataAnalyser.minimumTemperature, 2 seconds)
    t should not be None
    t.get should be(Kelvin(0))
  }

  test("maximum temperature") {
    val t: Option[Temperature] = Await.result(dataAnalyser.maximumTemperature, 2 seconds)
    t should not be None
    t.get should be(Celsius(20))
  }

  test("mean temperature") {
    val sum = observations.map(_.temperature.to[Celsius]).fold(EmptyTemperature.asInstanceOf[Temperature])(_ + _).value
    val mean = Celsius(sum / observations.length)
    val t: Option[Temperature] = Await.result(dataAnalyser.meanTemperature[Celsius], 2 seconds)
    t should not be None
    t.get should equal(mean)
  }

}
