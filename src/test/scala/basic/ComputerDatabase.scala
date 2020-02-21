package basic

import io.gatling.http.Predef._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import scala.concurrent.duration._

class ComputerDatabase extends Simulation{

  //http definition
  val httpConfig = http.baseUrl("http://computer-database.gatling.io")
    .header("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36")
  //scenario definition

  def launchComputerDatabaseApp(): ChainBuilder ={
    exec(http("00_Launch")
      .get("/")
      .check(status.is(200)))
      .pause(2)
  }
  val scn = scenario("Computer DB - New computer Creation")

    .exec(launchComputerDatabaseApp())


  //Load scenario definition
  setUp(
    scn.inject(
      rampConcurrentUsers(0) to (2) during (10),
      constantConcurrentUsers(2) during (30 seconds)
      // rampUsersPerSec(1) to(10) during(15)
    ).protocols(httpConfig.inferHtmlResources()))

}