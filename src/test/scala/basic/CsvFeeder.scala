package basic
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class CsvFeeder extends Simulation {

  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")
  val csvFeeder = csv("data/gameCsvFile.csv").circular

  def getSpecificVideoGame(): ChainBuilder ={
    repeat(10){
      feed(csvFeeder)
        .exec(http("get Specific Video game")
            .get("videogames/${gameId}")
        .check(jsonPath("$.name").is("${gameName}"))
        .check(status.is(200)))
       .exec{session =>println(session("gameName").as[String]);session}
        .pause(2)
    }
  }

  val scn = scenario("CSV Feeder Test")
      .exec(getSpecificVideoGame())


  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)

}
