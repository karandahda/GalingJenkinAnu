package com.simulations.gatling

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.util.Random

class ComputerDBAddRemove extends Simulation {

  val httpProtocol = http
    .baseUrl("http://computer-database.gatling.io")
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
    "Origin" -> "http://computer-database.gatling.io",
    "Upgrade-Insecure-Requests" -> "1")

  var idNumbers = (11 to 20).iterator
  var rnd = new Random()
  var now = LocalDate.now()
  def randomString(length: Int) = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  def getPastRandomDate(startDate: LocalDate,random: Random): String = {
    startDate.minusDays(random.nextInt(30)).format(pattern)
  }
  def getFutureRandomDate(startDate: LocalDate,random: Random): String = {
    startDate.plusDays(random.nextInt(30)).format(pattern)

  }
  val customFeeder = Iterator.continually(Map(
    "laptopName" -> ("Laptop-" + randomString(5)),
    "introducedDate" ->  getPastRandomDate(now,rnd),
    "decommissionedDate" ->  getFutureRandomDate(now,rnd),
  ))

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

    .feed(customFeeder)
    .exec(http("02_AddNewComputer")
      .post("/computers")
      .headers(headers_7)
      .formParam("name", "${laptopName}")
      .formParam("introduced", "${introducedDate}")
      .formParam("discontinued", "${decommissionedDate}")
      .formParam("company", "1")
      .resources(http("request_8")
        .get("/assets/stylesheets/bootstrap.min.css"),
        http("request_9")
          .get("/assets/stylesheets/main.css"))
      .check(status.in(200,201, 202, 302, 304))
      .check(status.not(404)))
    .exec{session => println(session("${laptopName}").as[String]);session}
    //.check(currentLocationRegex("*computers*")))
    .pause(10)

    .exec(http("03_SearchComputer")
      .get("/computers?f=${laptopName}")
      .headers(headers_0)
      .resources(http("request_11")
        .get("/assets/stylesheets/bootstrap.min.css"),
        http("request_12")
          .get("/assets/stylesheets/main.css"))
      .check(status.in(200,201, 202, 302, 304))
      .check(status.not(404))
   // failed .check(css("h1:contains('computers found')").exists)
   //failed .check(substring("${laptopName}").find.exists)
    // failed .check(substring("${laptopName}"))
            .check(regex("<a href=\"/computers/(.*)\">${laptopName}</a>").find.saveAs("ComputerId")))
  //     .check(bodyString.saveAs("BODY")))
//    .exec{   session => println(session("BODY").as[String])
//        session
//    }
    .exec {session => println(session);session}
    .exec{session => println(session("ComputerId").as[String])
      session}
    .exec{session => println(session("laptopName").as[String])
        session}
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

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
    .disablePauses

//  setUp(
//    scn.inject(
//      rampConcurrentUsers(0) to (2) during (10),
//      constantConcurrentUsers(2) during (30 seconds)
//      // rampUsersPerSec(1) to(10) during(15)
//    ).protocols(httpProtocol.inferHtmlResources()))

}