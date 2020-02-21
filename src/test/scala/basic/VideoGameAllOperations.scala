package basic
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.util.Random

class VideoGameAllOperations extends Simulation{
  //Get all Games
  //Create New Game [use custome feeder]
  //get single game [created above]
  //call delete game [create above]
  //create method for each call
  //add pause time
  //add checks and assertions in each call
  //load scenario with run time paramters
  //print message to console after start and end

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

  //http
  val httpConfig = http.baseUrl("http://localhost:8080/app/")
    .header("accept","application/json")
    .silentResources
 // val csvFeeder = csv("data/gameCsvFile.csv").circular
 var idNumbers = (21 to 2000).iterator
  var rnd = new Random()
  var now = LocalDate.now()
  def randomString(length: Int) = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }
  val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  def getRandomDate(startDate: LocalDate,random: Random): String = {
    startDate.minusDays(random.nextInt(30)).format(pattern)
  }
  val customFeeder = Iterator.continually(Map(
    "gameId" -> idNumbers.next(),
    "name" -> ("Game - " + randomString(5)),
    "releaseDate" ->  getRandomDate(now,rnd),
    "reviewScore" -> rnd.nextInt(1000) ,
    "category" -> ("Category - " + randomString(6)) ,
    "rating" -> ("Rating - " + randomString(4))
  ))
  //methods
  def getAllVideoGames() = {
    exec(
      http("Get All video Games")
        .get("videogames")
        .check(status.is(200))
        .check(jsonPath("$[-1:].id").saveAs("lastGameId"))
    )
  }

  def createNewGame() = {

      feed(customFeeder)
        .exec(http("Create New Video Game")
          .post("videogames/")
          .body(StringBody(
            "{" +
              "\n\t\"id\": ${gameId}," +
              "\n\t\"name\": \"${name}\"," +
              "\n\t\"releaseDate\": \"${releaseDate}\"," +
              "\n\t\"reviewScore\": ${reviewScore}," +
              "\n\t\"category\": \"${category}\"," +
              "\n\t\"rating\": \"${rating}\"\n}")
          ).asJson
          .check(status.is(200))
          .check(bodyString.saveAs("responseBody"))
          .check(jsonPath("$.status").is("Record Added Successfully")))
        //.exec{session =>println(session("responseBody").as[String]);session}
        //.exec{session =>println(session("name").as[String]);session}
      //  .exec {session => println(session);session}
        .pause(2)
    }


  def getSpecificGame() = {
    exec(
      http("Get All video Games")
        .get("videogames/${gameId}")
        .check(status.is(200))
        .check(jsonPath("$[0].id").saveAs("gameId"))
        .check(jsonPath("$[0].id").is("${gameId}"))
    )
  }

    def deleteGame() = {
      exec(
        http("Delete Specific Video Game")
          .delete("videogames/${gameId}")
          .check(status.is(200))
          .check(jsonPath("$.status").is("Record Deleted Successfully"))
      )
    }

  val scn = scenario("Video Game - navigation")
    .forever() {
      exec(getAllVideoGames())
        .pause(3)
        .exec(createNewGame())
        .pause(3)
        .exec(getSpecificGame())
        .pause(3)
        .exec(deleteGame())
        .pause(3)
        .exec(getAllVideoGames())
    }

  setUp(

    scn.inject(
      nothingFor(5),
      rampUsersPerSec(userCount) to(rampDuration) during(testDuration)
    ).protocols(httpConfig.inferHtmlResources())
      )//mvn gatling:test -Dgatling.simulationClass=basic.VideoGameAllOperations -DUSERS=10 -DRAMPUP_DURATION=5 -DDURATION=30

  after {
    println(s"Ran test with ${userCount} users")
   // println(s"Ramping users over ${rampDuration} seconds")
    println(s"Total test duration was ${testDuration} seconds")
  }
}
