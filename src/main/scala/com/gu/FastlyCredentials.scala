package com.gu

import scala.Predef._
import scala.io._
import com.ning.http.client.AsyncHttpClientConfig
import java.io.File
import java.util.InvalidPropertiesFormatException

trait FastlyCredentials {

  var fastlyCredentialLocations: Seq[File] = Seq(
    new File(System.getProperty("user.home") + "/.fastlyapiclientcconfig"),
    new File("/etc/fastly/fastlyapiclientcconfig"))
  var asyncHttpClientConfig: AsyncHttpClientConfig = null

  final var apiKey: String = _
  final var serviceId: String = _

  lazy val fastlyApiClient: FastlyAPIClient = {
    fastlyCredentialLocations.foreach(location => if (location.exists()) parseCredentials(location))
    if (apiKey == null) throw new InvalidPropertiesFormatException("missing property apiKey")
    if (serviceId == null) throw new InvalidPropertiesFormatException("missing property serviceId")
    FastlyAPIClient(apiKey, serviceId, Option(asyncHttpClientConfig))
  }

  private def parseCredentials(credentialsLocation: File) {
    val file = Source.fromFile(credentialsLocation, "utf-8")
    file.getLines.foreach(
      line => {
        val propertyRegex = """^(\S+)=(\S+)$""".r
        line match {
          case propertyRegex(key, value) => {
            if (key.trim.equals("apiKey")) apiKey = value.trim
            if (key.trim.equals("serviceId")) serviceId = value.trim
          }
          case _ =>
        }
      })
  }
}
