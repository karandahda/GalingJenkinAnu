package basic
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class CsvFeederToCustomFeeder extends Simulation{

  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")
   // .proxy(Proxy("localhost",8888))


  //val csvFeeder = csv("data/gameCsvFile.csv").circular
  var idNumbers = (1 to 10).iterator
  val customFeeder = Iterator.continually(Map("gameId" -> idNumbers.next()))

  def getSpecificVideoGame(): ChainBuilder ={
    repeat(10){
      feed(customFeeder)
        .exec(http("get Specific Video game")
          .get("videogames/${gameId}")
          .check(status.is(200)))

        .pause(2)
    }
  }

  val scn = scenario("CSV Feeder Test")
    .exec(getSpecificVideoGame())


  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)

}