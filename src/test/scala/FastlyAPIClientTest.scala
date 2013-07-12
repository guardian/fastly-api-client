import com.gu.{By, Region, FastlyAPIClient}

import org.joda.time.DateTime
import org.scalatest.FeatureSpec
import org.scalatest.matchers._


class FastlyAPIClientTest extends FeatureSpec with ShouldMatchers with FastlyCredentials {

  val client = FastlyAPIClient(apiKey, serviceId)

  feature("Stats") {
    scenario("usage") {
      val response = client.statsUsage().get
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    scenario("stats to and from") {
      val response = client.stats(
        from = DateTime.now.minusMinutes(1),
        to = DateTime.now,
        by = By.minute
      ).get
//      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    scenario("stats by") {
      val response = client.stats(
        from = DateTime.now.minusHours(1),
        to = DateTime.now,
        by = By.minute
      ).get
      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    scenario("stats region") {
      val response = client.stats(
        from = DateTime.now.minusMinutes(5),
        to = DateTime.now,
        by = By.minute,
        region = Region.europe
      ).get
      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    scenario("stats region list") {
      val response = client.statsRegionList().get
      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }
  }
}
