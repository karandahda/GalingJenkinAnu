package basic

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class FixDurationLoadSimulation extends Simulation{
  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")

  def getAllVideoGames() = {
    exec(
      http("Get All video Games")
        .get("videogames")
        .check(status.is(200))
    )
  }


  def getSpecificVideoGame()={
    exec(http("Get Specific Video Game")
      .get("videogames/2")
      .check(status.in(200,210)))
  }

  val scn = scenario("Fixed Duration simulation")
    .forever(){
      exec(getAllVideoGames())
        .pause(5)
        .exec(getSpecificVideoGame())
        .pause(5)
        .exec(getAllVideoGames())
    }

  setUp(
    scn.inject(
      nothingFor(5),
      atOnceUsers(10),
      rampUsers(50) during (30 seconds)
    ).protocols(httpConf.inferHtmlResources())
  ).maxDuration(1 minute)

}
