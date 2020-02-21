package basic

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class RuntimeParameters extends Simulation{
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
      .pause(1)
    }

  setUp(
    scn.inject(
      nothingFor(5),
      rampUsers(1) during (1 seconds)
    ).protocols(httpConf.inferHtmlResources())
    ).maxDuration(20 seconds)

}
