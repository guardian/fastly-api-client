package com.gu.fastly.api

import org.joda.time.DateTime
import org.scalatest.FeatureSpec
import org.scalatest.Matchers
import com.typesafe.config.ConfigFactory
import java.io.File
import scala.concurrent.Await
import scala.concurrent.duration._

class FastlyApiClientTest extends FeatureSpec with Matchers {

  lazy val client = FastlyApiClient(conf.getString("apiKey"), conf.getString("serviceId"))

  feature("stats") {

    scenario("stats") {
      val response = Await.result(client.stats(
        from = DateTime.now.minusMinutes(1),
        to = DateTime.now,
        by = By.minute
      ), 5.seconds)
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    scenario("stats with field filter") {
      val response = Await.result(client.statsWithFieldFilter(
        from = DateTime.now.minusMinutes(1),
        to = DateTime.now,
        by = By.minute,
        field = "hit_ratio"
      ), 5.seconds)
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    scenario("aggregate") {
      val response = Await.result(client.statsAggregate(
        from = DateTime.now.minusMinutes(1),
        to = DateTime.now,
        by = By.minute
      ), 5.seconds)
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    scenario("service") {
      val response = Await.result(client.statsForService(
        from = DateTime.now.minusMinutes(1),
        to = DateTime.now,
        by = By.minute,
        serviceId = client.serviceId
      ), 5.seconds)
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    scenario("service with field filter") {
      val response = Await.result(client.statsForServiceWithFieldFilter(
        from = DateTime.now.minusMinutes(1),
        to = DateTime.now,
        by = By.minute,
        serviceId = client.serviceId,
        field = "hit_ratio"
      ), 5.seconds)
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    scenario("usage") {
      val response = Await.result(client.statsUsage(
        from = DateTime.now.minusHours(1),
        to = DateTime.now,
        by = By.minute
      ), 5.seconds)
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    scenario("usage by grouped by service") {
      val response = Await.result(client.statsUsageGroupedByService(
        from = DateTime.now.minusHours(1),
        to = DateTime.now,
        by = By.minute
      ), 5.seconds)
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    scenario("regions") {
      val response = Await.result(client.stats(
        from = DateTime.now.minusMinutes(1),
        to = DateTime.now,
        by = By.minute,
        region = Region.all
      ), 5.seconds)
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    scenario("stats region list") {
      val response = Await.result(client.statsRegions(), 5.seconds)
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }
  }

  feature("Servcie") {
    scenario("list") {
      val response = Await.result(client.serviceList(), 5.seconds)
      //            println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }
  }

  lazy val conf = ConfigFactory.parseFile(new File(
    System.getProperty("user.home") + "/.fastlyapiclientcconfigbeta")
  )
}
