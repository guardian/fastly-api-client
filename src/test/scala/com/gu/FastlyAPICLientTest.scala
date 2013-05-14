package com.gu

import org.scalatest.FeatureSpec
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.ShouldMatchers
import net.liftweb.json._
import com.ning.http.client.{Response, ListenableFuture}
import org.joda.time.{DateTimeZone, DateTime}


class FastlyAPICLientTest extends FeatureSpec with BeforeAndAfter with ShouldMatchers with FastlyCredentials {

  fastlyCredentialLocations = Seq(new java.io.File(System.getProperty("user.home") + "/.fastlyapiclientcconfigTEST"))

  feature("VCL") {
    implicit val formats = DefaultFormats
    case class VCL(main: Boolean, version: Int, name: String, service_id: String, content: String)

    scenario("should list") {
      val version = fastlyApiClient.latestVersionNumber()
      val response = fastlyApiClient.vclList(version).get
      response.getStatusCode should equal(200)
      try {
        parse(response.getResponseBody).extract[List[VCL]]
      } catch {
        case e: Exception => fail(e)
        case _ => //
      }
    }

    scenario("Update") {
      val version = fastlyApiClient.latestVersionNumber()
      val testVclFile = scala.io.Source.fromFile("./test-do-not-delete.vcl").mkString
      val files = Map("test-do-not-delete.vcl" -> testVclFile)
      fastlyApiClient.vclUpdate(files, version).foreach {
        lfresponse: ListenableFuture[Response] => {
          try {
            parse(lfresponse.get.getResponseBody).extract[VCL]
          } catch {
            case e: Exception => fail(e)
            case _ => //
          }
        }
      }
    }
  }

  feature("Stats") {
    scenario("should get stats") {
      val from = new DateTime(DateTimeZone.UTC).minusHours(1)
      val to = new DateTime(DateTimeZone.UTC)

      val response = fastlyApiClient.stats(from.toDate, to.toDate).get
      response.getStatusCode should equal(200)
    }
  }

}
