package org.balloon.generator.connection

import org.balloon.data.observatory.{Australia, France, Other, UnitedStates}
import org.balloon.generator.simulator.BalloonSimulator
import org.scalatest.{FreeSpec, Matchers}

import scala.concurrent.ExecutionContext

class ConnectionTest extends FreeSpec with Matchers {
  implicit val ex: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  "Connection to" - {
    "AU returns BalloonSimulator[Australia]" in {
      Connection.to("AU", 1).isInstanceOf[BalloonSimulator[Australia]] should be(true)
    }

    "US returns BalloonSimulator[UnitedStates]" in {
      Connection.to("US", 1).isInstanceOf[BalloonSimulator[UnitedStates]] should be(true)
    }

    "FR returns BalloonSimulator[France]" in {
      Connection.to("FR", 1).isInstanceOf[BalloonSimulator[France]] should be(true)
    }

    "PL returns BalloonSimulator[Other]" in {
      Connection.to("PL", 1).isInstanceOf[BalloonSimulator[Other]] should be(true)
    }
  }

}
