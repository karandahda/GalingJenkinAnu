  package basic
  import io.gatling.core.Predef._
  import io.gatling.http.Predef._

  import scala.concurrent.duration.DurationInt

  class CheckResponseBodyAndExtract extends Simulation {

    val httpConf = http.baseUrl("http://localhost:8080/app/")
      .header("Accept", "application/json")

    val scn = scenario("Check Json Path")

      .exec(http("Get specific game")
        .get("videogames/1")
        .check(jsonPath("$.name").is("Resident Evil 4")))
      .pause(1)

      .exec(http("Get video Games")
        .get("videogames")
        .check(jsonPath("$[1].id").saveAs("gameId")))
      .pause(1)
      .exec {session => println(session);session}

      .exec(http("Get specific game")
        .get("videogames/${gameId}")
        .check(jsonPath("$.name").is("Gran Turismo 3"))
        .check(bodyString.saveAs("responseBody")))
      .exec{session =>println(session("responseBody").as[String]);session}
      .pause(1, 2)

    setUp(
      scn.inject(atOnceUsers(1))
    ).protocols(httpConf)

  }


