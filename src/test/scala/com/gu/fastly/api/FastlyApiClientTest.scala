package com.gu.fastly.api

import com.typesafe.config.{ConfigException, ConfigFactory}
import org.joda.time.DateTime
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.must.Matchers

import java.io.File
import scala.concurrent.Await
import scala.concurrent.duration._

class FastlyApiClientTest extends AnyFeatureSpec with Matchers {


  lazy val apiKey = sys.env.getOrElse("API_KEY", conf.getString("apiKey"))
  lazy val serviceId = sys.env.getOrElse("SERVICE_ID", conf.getString("serviceId"))

  lazy val client = FastlyApiClient(apiKey, serviceId)

  Feature("stats") {

    Scenario("stats") {
      val response = Await.result(client.stats(
        from = DateTime.now.minusMinutes(1),
        to = DateTime.now,
        by = By.minute
      ), 5.seconds)
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    Scenario("stats with field filter") {
      val response = Await.result(client.statsWithFieldFilter(
        from = DateTime.now.minusMinutes(1),
        to = DateTime.now,
        by = By.minute,
        field = "hit_ratio"
      ), 5.seconds)
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    Scenario("aggregate") {
      val response = Await.result(client.statsAggregate(
        from = DateTime.now.minusMinutes(1),
        to = DateTime.now,
        by = By.minute
      ), 5.seconds)
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    Scenario("service") {
      val response = Await.result(client.statsForService(
        from = DateTime.now.minusMinutes(1),
        to = DateTime.now,
        by = By.minute,
        serviceId = client.serviceId
      ), 5.seconds)
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    Scenario("service with field filter") {
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

    Scenario("usage") {
      val response = Await.result(client.statsUsage(
        from = DateTime.now.minusHours(1),
        to = DateTime.now,
        by = By.minute
      ), 5.seconds)
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    Scenario("usage by grouped by service") {
      val response = Await.result(client.statsUsageGroupedByService(
        from = DateTime.now.minusHours(1),
        to = DateTime.now,
        by = By.minute
      ), 5.seconds)
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    Scenario("regions") {
      val response = Await.result(client.stats(
        from = DateTime.now.minusMinutes(1),
        to = DateTime.now,
        by = By.minute,
        region = Region.all
      ), 5.seconds)
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }

    Scenario("stats region list") {
      val response = Await.result(client.statsRegions(), 5.seconds)
      //      println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }
  }

  Feature("Service") {
    Scenario("list") {
      val response = Await.result(client.serviceList(), 5.seconds)
      //            println(response.getResponseBody)
      assert(response.getStatusCode === 200)
    }
  }

  Feature("Package") {
    Scenario("upload package") {
      try {
        val path = conf.getString("packagePath")
        // Make sure this version isn't locked, or else the Fastly API will return a 422 response
        val versionId = conf.getInt("packageVersionId")
        val `package` = new File(path)
        val response = Await.result(client.packageUpload(client.serviceId, versionId, `package`), 5.seconds)
        //      println(response.getResponseBody)
        assert(response.getStatusCode === 200)
      }
      catch {
        case _: ConfigException.Missing =>
          cancel("packagePath and/or packageVersionId are missing from your config file")
      }
    }
  }

  lazy val conf = ConfigFactory.parseFile(new File(
    System.getProperty("user.home") + "/.config/fastly/fastlyApiClientTest")
  )
}
