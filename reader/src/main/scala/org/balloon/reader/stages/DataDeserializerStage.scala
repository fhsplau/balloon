package org.balloon.reader.stages

import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import org.balloon.data.observatory.ObservatoryData

class DataDeserializerStage extends GraphStage[FlowShape[String, ObservatoryData]] {
  val input: Inlet[String] = Inlet[String]("line.in")
  val output: Outlet[ObservatoryData] = Outlet[ObservatoryData]("data.out")

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {
    setHandler(input, new InHandler {
      override def onPush(): Unit = {
        val line: String = grab(input)
        val data: Option[ObservatoryData] = ObservatoryData.from(line)
        if (data.isDefined) {
          push(output, data.get)
        } else {
          pull(input)
        }
      }
    })

    setHandler(output, new OutHandler {
      override def onPull(): Unit = pull(input)
    })
  }

  override def shape: FlowShape[String, ObservatoryData] = FlowShape(input, output)
}
