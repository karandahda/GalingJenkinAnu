package basic

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
class RampUserCodeSimulation extends Simulation{
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

  val scn = scenario("my basic simlation")
    .exec(getAllVideoGames())
    .pause(2)
    .exec(getSpecificVideoGame())
    .pause(2)
    .exec(getAllVideoGames())

  setUp(
    scn.inject(
      nothingFor(5),
     // constantUsersPerSec(10) during (10 seconds)
      rampUsersPerSec(1) to (5) during(20 seconds)
    ).protocols(httpConf.inferHtmlResources())
  )

}
