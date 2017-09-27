Prerequisite
-
- Java 8
- access to *-nix machine

Project Structure
-
This project contains four modules:

- client - integrated ammonite repl with the project
- generator - program which generates simulation data
- reader - program which reads and analyzes the data
- data - project contains all the data structures and types used in this project

Building
-
To build the project go to the main directory (`balloon_data`) and run the following command

`./gradlew clean build`

The command will build, test and create the distribution package(s).

In the folder `client\build\distributions` you can find `client.zip` or `client.tar` package. Unpack it.

The newly created directory will contain to sub directories `lib` and `bin`. Lib directory contains all the jar files which need to be loaded on runtime. Bin directory contains `client` script which, after execution, will start the client.


Running the client
-
`client` script will run the ammonite repl with all the necessary classes and objects pre-imported.

If you don't know what ammonite repl is check it out https://github.com/lihaoyi/Ammonite, it's really awesome.

Pre-imported stuff
-

 - org.balloon.data.temperature.{Celsius, Fahrenheit, Kelvin}
 - org.balloon.data.temperature.{Kilometers, Miles, Meters}
 - org.balloon.data.observatory.{Australia, France, Other, UnitedStates}
 - org.balloon.reader.DataReader
 - akka.actor.ActorSystem
 - akka.stream.ActorMaterializer
 - org.balloon.generator.Generator
 - java.nio.file.Paths
 - scala.concurrent.{ExecutionContext, Future}
 - val system = ActorSystem(\"balloon\")
 - val materializer = ActorMaterializer()
 - val ec: ExecutionContext = system.dispatcher

Generating balloon data
-
To generate the data first you need to create `Generator` first. Simply type the following command in repl.

`val generator = Generator()`

The command will create reusable generator. To generate data you need to run the generator.

`val f = generator.run(<number_of_lines>, Paths.get(<filename>))`

The above command returns future.

Reading data
-
To read the data you need to create reusable `DataReader` first. Type the following command in repl.

`val dataReader = new DataReader`

Next step is to read the data (virtually only) by running `read` command.

`val data = reader.read(Paths.get(<filename>))`

`read` function returns `DataAnalyser` object.

Analysing the data
-
To analyse your data use `DataAnalyser` object returned by previously run command. `DataAnalyser` has got six functions which I have listed below.

- `def filter(f: ObservatoryData => Boolean): Future[List[ObservatoryData]]`
- `def numOfObservations[T <: Observatory[T] : TypeTag](f: ObservatoryData => Boolean = _ => true): Future[Int]`
- `def minimumTemperature: Future[Option[Temperature]]`
- `def maximumTemperature: Future[Option[Temperature]]`
- `def meanTemperature[T <: TemperatureScale : TypeTag]: Future[Option[Temperature]]`
- `def save(filename: Path, m: ObservatoryData => ObservatoryData): Future[IOResult]`

As you can see every function returns future, so you can use `onComplete` method to be notified on completion or block with `Await` and wait for the result (Await was not pre-imported).

Saving data
-

To save data use the following command

`data.save(Paths.get("test"), d => d.copy(d.temperature.to[Celsius], d.coordinates.to[Kilometers]))`

The above command will map ([T] => [Celsius] and [C] => Kilometers) and save all the date read by the reader.

Exiting
-

To exit simply type `exit` in repl (probaly you will also need to use `Control-D`) 



