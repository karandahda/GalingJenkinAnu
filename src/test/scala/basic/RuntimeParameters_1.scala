package basic

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class RuntimeParameters_1 extends Simulation{

  private def getProperty(propertyName: String, defaultValue: String): String ={
    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }
  def userCount: Int = getProperty("USERS", "5").toInt
  def rampDuration: Int = getProperty("RAMPUP_DURATION", "10").toInt
  def testDuration: Int = getProperty("DURATION", "60").toInt

  before {
    println(s"Running test with ${userCount} users")
    println(s"Ramping users over ${rampDuration} seconds")
    println(s"Total test duration ${testDuration} seconds")
  }
  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")

  def getAllVideoGames() = {
    exec(
      http("Get All video Games")
        .get("videogames")
        .check(status.is(200))
    )
  }

  val scn = scenario("Fixed Duration simulation")
    .forever(){
      exec(getAllVideoGames())
      .pause(1)
    }

  setUp(
    scn.inject(
      nothingFor(5),
     rampUsers(userCount) during (rampDuration seconds)
    ).protocols(httpConf.inferHtmlResources())
  ).maxDuration(testDuration seconds)

}
