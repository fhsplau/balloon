import org.balloon.data.observatory.{Australia, France, Other, UnitedStates}

object Main {
  def main(args: Array[String]): Unit = {
    Australia
    Other
    UnitedStates
    France
    val imports: List[String] = List(
      "import org.balloon.data.temperature.{Celsius, Fahrenheit, Kelvin}",
      "import org.balloon.data.temperature.{Kilometers, Miles, Meters}",
      "import org.balloon.data.observatory.{Australia, France, Other, UnitedStates}",
      "import org.balloon.reader.DataReader",
      "import akka.actor.ActorSystem",
      "import akka.stream.ActorMaterializer",
      "import org.balloon.generator.Generator",
      "import java.nio.file.Paths",
      "import scala.concurrent.{ExecutionContext, Future}",
      "implicit val system = ActorSystem(\"balloon\")",
      "implicit val materializer = ActorMaterializer()",
      "implicit val ec: ExecutionContext = system.dispatcher"
    )
    ammonite.Main(predefCode = s"repl.frontEnd() = ammonite.repl.FrontEnd.JLineUnix\n${imports.mkString("\n")}").run()
  }

}
