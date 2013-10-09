package com.gu.fastly.api

import org.joda.time.DateTime
import org.scalatest.{Matchers, FeatureSpec}
import dispatch._
import concurrent.Await
import concurrent.duration._

class FastlyApiClientTest extends FeatureSpec with Matchers with FastlyCredentials {

  val client = FastlyApiClient(apiKey, serviceId)

  implicit class EnrichedFuture[A](future: Future[A]) {
    def get = Await.result(future, 5 seconds)
  }

  feature("stats") {
    scenario("stats") {
      val response = client.stats(
        from = DateTime.now.minusMinutes(1),
        to = DateTime.now,
        by = By.minute
      ).get

      assert(response.getStatusCode === 200)
    }

    scenario("stats with field filter") {
      val response = client.statsWithFieldFilter(
        from = DateTime.now.minusMinutes(1),
        to = DateTime.now,
        by = By.minute,
        field = "hit_ratio"
      ).get
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    scenario("aggregate") {
      val response = client.statsAggregate(
        from = DateTime.now.minusMinutes(1),
        to = DateTime.now,
        by = By.minute
      ).get
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    scenario("service") {
      val response = client.statsForService(
        from = DateTime.now.minusMinutes(1),
        to = DateTime.now,
        by = By.minute,
        serviceId = client.serviceId
      ).get
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    scenario("service with field filter") {
      val response = client.statsForServiceWithFieldFilter(
        from = DateTime.now.minusMinutes(1),
        to = DateTime.now,
        by = By.minute,
        serviceId = client.serviceId,
        field = "hit_ratio"
      ).get
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    scenario("usage") {
      val response = client.statsUsage(
        from = DateTime.now.minusHours(1),
        to = DateTime.now,
        by = By.minute
      ).get
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    scenario("usage by grouped by service") {
      val response = client.statsUsageGroupedByService(
        from = DateTime.now.minusHours(1),
        to = DateTime.now,
        by = By.minute
      ).get
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    scenario("regions") {
      val response = client.stats(
        from = DateTime.now.minusMinutes(1),
        to = DateTime.now,
        by = By.minute,
        region = Region.all
      ).get
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    scenario("stats region list") {
      val response = client.statsRegions().get
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }
  }

  feature("Servcie") {
    scenario("list") {
      val response = client.serviceList().get
      //            println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }
  }

}