
package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class FirstSimulation extends Simulation {
  val httpProtocol = http.baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7,id;q=0.6,th;q=0.5,zh-TW;q=0.4,zh;q=0.3")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Safari/537.36")

  val authHeaders = Map(
    "X-AUTH-TOKEN" -> "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sImlhdCI6MTU5MzE1Nzc0MywiZXhwIjoxNTkzMjQ0MTQzfQ.GH32pWlt8TB6yUdAtg8So0vDKituyNi0r1YJ9j7RXtM")

  val formParam = Map(
    "author" -> "아무게",
    "title" -> "첫인사드립니다.",
    "content" -> "안녕하세요.아무게입니다.잘부탁드립니다."
  )

  val postId = 0

  val scn = scenario("FirstSimulation")
    .exec(http("게시판 보기")
        .get("/v1/board/freeboard/posts"))
    .pause(2)
    .exec(http("게시판에 글 작성하기")
        .post("/v1/board/freeboard/post")
        .headers(authHeaders)
        .formParamMap(formParam)
        .check(status.is(200))
        .check(jsonPath("$.data['postId']").saveAs("postId")))
    .exec(http("작성한 글 확인하기")
        .get("/v1/board/post/${postId}"))

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}