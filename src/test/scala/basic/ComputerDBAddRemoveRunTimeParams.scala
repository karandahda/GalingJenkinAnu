package basic

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class ComputerDBAddRemoveRunTimeParams extends Simulation {
//Properties
  private def getProperty(propertyName: String, defaultValue: String): String ={
    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }

  def userCount: Int = getProperty("USERS", "1").toInt
  def rampDuration: Int = getProperty("RAMPUP_DURATION", "10").toInt
  def testDuration: Int = getProperty("DURATION", "30").toInt
  def endPoint: String = getProperty("ENDPOINT","NotFound").toString



  val httpProtocol = http
    //.baseUrl("http://computer-database.gatling.io")
    .baseUrl(endPoint)
    .inferHtmlResources()
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.106 Safari/537.36")
    .silentResources
    // .silentUri(".*delete")

  val headers_0 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "Accept-Encoding" -> "gzip, deflate",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_2 = Map(
    "Accept" -> "text/css,*/*;q=0.1",
    "Accept-Encoding" -> "gzip, deflate",
    "Accept-Language" -> "en-US,en;q=0.9")

  val headers_3 = Map(
    "Accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
    "Accept-Encoding" -> "gzip, deflate",
    "Accept-Language" -> "en-US,en;q=0.9")

  val headers_7 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "Accept-Encoding" -> "gzip, deflate",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Origin" -> endPoint,
    "Upgrade-Insecure-Requests" -> "1")



  val scn = scenario("ComputerDBAddRemove")
    .exec(http("00_Launch_ComputerDB")
      .get("/computers")
      .headers(headers_0)
      .resources(http("00_L_HomePage")
        .get("/")
        .headers(headers_0),
        http("request_2")
          .get("/assets/stylesheets/main.css")
          .headers(headers_2),
        //.notSilent,
        http("request_3")
          .get("/favicon.ico")
          //.silent
          .headers(headers_3)
          .check(status.is(404))))
    .pause(10)
    .exec(http("01_StartCreatingNewComputer")
      .get("/computers/new")
      .headers(headers_0)
      .check(status.in(200,201, 202, 302, 304))
          .check(status.not(404))
      .resources(http("request_5")
        .get("/assets/stylesheets/bootstrap.min.css"),
        http("request_6")
          .get("/assets/stylesheets/main.css")))
    .pause(10)
    .exec(http("02_AddNewComputer")
      .post("/computers")
      .headers(headers_7)
      .formParam("name", "Acer")
      .formParam("introduced", "2018-01-01")
      .formParam("discontinued", "2020-01-01")
      .formParam("company", "1")
      .resources(http("request_8")
        .get("/assets/stylesheets/bootstrap.min.css"),
        http("request_9")
          .get("/assets/stylesheets/main.css"))
      .check(status.in(200,201, 202, 302, 304))
      .check(status.not(404)))
    //.check(currentLocationRegex("*computers*")))
    .pause(10)
    .exec(http("03_SearchComputer")
      .get("/computers?f=acer")
      .headers(headers_0)
      .resources(http("request_11")
        .get("/assets/stylesheets/bootstrap.min.css"),
        http("request_12")
          .get("/assets/stylesheets/main.css"))
      .check(status.in(200,201, 202, 302, 304))
      .check(status.not(404))
    .check(css("h1:contains('computers found')").exists)
    .check(substring("acer").find.exists)
     .check(substring("acer"))
       .check(regex("<a href=\"/computers/(.*)\">Acer</a>").find.saveAs("ComputerId")))
    // .check(bodyString.saveAs("BODY")))
   // .exec{
  //    session => println(session("BODY").as[String])
    //    session
   // }
     // .check(substring("acer").count.is(2)))
    .pause(5)
    .exec(http("04_OpenSearchedComputer")
      .get("/computers/${ComputerId}")
      .headers(headers_0)
      .resources(http("request_14")
        .get("/assets/stylesheets/bootstrap.min.css"),
        http("request_15")
          .get("/assets/stylesheets/main.css"))
      .check(status.in(200,201, 202, 302, 304))
      .check(status.not(404)))
    .pause(4)
    .exec(http("05_DeleteComputer")
      .post("/computers/${ComputerId}/delete")
      .headers(headers_7)
      .check(status.in(200,201, 202, 302, 304))
      .check(status.not(404)))

 // setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
  setUp(
    scn.inject(
      rampConcurrentUsers(0) to (userCount) during (rampDuration seconds),
      constantConcurrentUsers(userCount) during (testDuration seconds)
      // rampUsersPerSec(1) to(10) during(15)
    ).protocols(httpProtocol.inferHtmlResources()))
  // .uniformPauses(2)
  //mvn gatling:test -Dgatling.simulationClass=basic.ComputerDBAddRemoveRunTimeParams -DUSERS=2 -DRAMPUP_DURATION=5 -DDURATION=30 -DENDPOINT="http://computer-database.gatling.io"


}