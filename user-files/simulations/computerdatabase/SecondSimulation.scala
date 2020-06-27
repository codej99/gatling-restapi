
package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class SecondSimulation extends Simulation {
  val httpProtocol = http.baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7,id;q=0.6,th;q=0.5,zh-TW;q=0.4,zh;q=0.3")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Safari/537.36")

  val feeder = csv("input-datas.csv").queue
  val postId = 0

  val scn = scenario("SecondSimulation")
    .feed(feeder)
    .exec(http("게시판 보기")
        .get("/v1/board/freeboard/posts"))
    .exec(http("게시판에 글 작성하기")
        .post("/v1/board/freeboard/post")
        .headers(Map("X-AUTH-TOKEN" -> "${x-auth-token}"))
        .formParamMap(Map("author" -> "${author}","title" -> "${title}", "content" -> "${content}"))
        .check(status.is(200))
        .check(jsonPath("$.data['postId']").saveAs("postId")))
    .exec(http("작성한 글 확인하기")
        .get("/v1/board/post/${postId}"))

  setUp(
    scn.inject(
      rampUsers(10) during (5 seconds)
    ).protocols(httpProtocol)
  )
}