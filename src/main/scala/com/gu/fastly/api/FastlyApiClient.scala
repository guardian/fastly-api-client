package com.gu.fastly.api

import com.ning.http.client._
import org.joda.time.DateTime
import scala.concurrent.{Promise, Future}
import scala.language.implicitConversions
import scala.util.Success

// http://www.fastly.com/docs/api
// http://www.fastly.com/docs/stats
case class FastlyApiClient(apiKey: String, serviceId: String, config: Option[AsyncHttpClientConfig] = None, proxyServer: Option[ProxyServer] = None) {

  private val fastlyAPIURL = "https://api.fastly.com"
  private val commonHeaders = Map("X-Fastly-Key" -> apiKey, "Accept" -> "application/json")

  sealed trait HttpMethod
  object GET extends HttpMethod
  object POST extends HttpMethod
  object PUT extends HttpMethod
  object DELETE extends HttpMethod

  // http://docs.fastly.com/docs/api#vcl_7
  def vclUpload(version: Int, vcl: String, id: String, name: String): Future[Response] = {
    val apiUrl = "%s/service/%s/version/%d/vcl".format(fastlyAPIURL, serviceId, version)
    AsyncHttpExecutor.execute(
      apiUrl,
      POST,
      headers = commonHeaders ++ Map("Content-Type" -> "application/x-www-form-urlencoded"),
      parameters = Map("content" -> vcl, "name" -> name, "id" -> id)
    )
  }

  // http://docs.fastly.com/docs/api#vcl_10
  def vclUpdate(version: Int, vcl: Map[String, String]): List[Future[Response]] = {
    vcl.map({
      case (name, file) => {
        val apiUrl = "%s/service/%s/version/%d/vcl/%s".format(fastlyAPIURL, serviceId, version, name)
        AsyncHttpExecutor.execute(
          apiUrl,
          PUT,
          headers = commonHeaders ++ Map("Content-Type" -> "application/x-www-form-urlencoded"),
          parameters = Map("content" -> file, "name" -> name)
        )
      }
    }).toList
  }

  // http://docs.fastly.com/docs/api#purge_3
  def purge(url: String, extraHeaders: Map[String, String] = Map.empty): Future[Response] = {
    val apiUrl = "%s/purge/%s".format(fastlyAPIURL, url.stripPrefix("http://").stripPrefix("https://"))
    AsyncHttpExecutor.execute(apiUrl, POST, headers = Map("X-Fastly-Key" -> apiKey) ++ extraHeaders)
  }

  // http://docs.fastly.com/docs/api#version_2
  def versionCreate(): Future[Response] = {
    val apiUrl = "%s/service/%s/version".format(fastlyAPIURL, serviceId)
    AsyncHttpExecutor.execute(apiUrl, PUT, headers = commonHeaders)
  }

  // http://docs.fastly.com/docs/api#version_3
  def versionList(): Future[Response] = {
    val apiUrl = "%s/service/%s/version".format(fastlyAPIURL, serviceId)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders)
  }

  // http://docs.fastly.com/docs/api#version_5
  def versionActivate(version: Int): Future[Response] = {
    val apiUrl = "%s/service/%s/version/%d/activate".format(fastlyAPIURL, serviceId, version)
    AsyncHttpExecutor.execute(apiUrl, PUT, headers = commonHeaders)
  }

  // http://docs.fastly.com/docs/api#version_7
  def versionClone(version: Int): Future[Response] = {
    val apiUrl = "%s/service/%s/version/%d/clone".format(fastlyAPIURL, serviceId, version)
    AsyncHttpExecutor.execute(apiUrl, PUT, headers = commonHeaders ++ Map("Content-Type" -> "application/x-www-form-urlencoded"))
  }

  // http://docs.fastly.com/docs/api#version_8
  def versionValidate(version: Int): Future[Response] = {
    val apiUrl = "%s/service/%s/version/%s/validate".format(fastlyAPIURL, serviceId, version)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders)
  }

  // http://docs.fastly.com/docs/api#vcl_9
  def vclSetAsMain(version: Int, name: String): Future[Response] = {
    val apiUrl = "%s/service/%s/version/%d/vcl/%s/main".format(fastlyAPIURL, serviceId, version, name)
    AsyncHttpExecutor.execute(apiUrl, PUT, headers = commonHeaders)
  }

  // http://docs.fastly.com/docs/api#vcl_1
  def vclList(version: Int): Future[Response] = {
    val apiUrl = "%s/service/%s/version/%d/vcl".format(fastlyAPIURL, serviceId, version)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders)
  }

  // http://docs.fastly.com/docs/api#vcl_8
  def vclDelete(version: Int, name: String): Future[Response] = {
    val apiUrl = "%s/service/%s/version/%s/vcl/%s".format(fastlyAPIURL, serviceId, version, name)
    AsyncHttpExecutor.execute(apiUrl, DELETE, headers = commonHeaders)
  }

  // http://docs.fastly.com/docs/api#backend_1
  def backendCheckAll(version: Int): Future[Response] = {
    val apiUrl = "%s/service/%s/version/%d/backend/check_all".format(fastlyAPIURL, serviceId, version)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders)
  }

  // http://docs.fastly.com/docs/api#backend_3
  def backendCreate(version: Int, id: String, address: String, port: Int): Future[Response] = {
    val apiUrl = "%s/service/%s/version/%d/backend".format(fastlyAPIURL, serviceId, version)
    val params = Map("ipv4" -> address, "version" -> version.toString, "id" -> id, "port" -> port.toString, "service" -> serviceId)
    AsyncHttpExecutor.execute(apiUrl, POST, headers = commonHeaders, parameters = params)
  }

  // http://docs.fastly.com/docs/api#backend_4
  def backendList(version: Int): Future[Response] = {
    val apiUrl = "%s/service/%s/version/%d/backend".format(fastlyAPIURL, serviceId, version)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders)
  }

  // http://docs.fastly.com/docs/api#service_1
  def serviceList(): Future[Response] = {
    val apiUrl = "%s/service".format(fastlyAPIURL)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders)
  }

  // http://docs.fastly.com/docs/stats#Range
  // http://docs.fastly.com/docs/stats#Sample
  // http://docs.fastly.com/docs/stats#Region
  def stats(from: DateTime, to: DateTime, by: By.Value, region: Region.Value = Region.all): Future[Response] = {
    val apiUrl = "%s/stats".format(fastlyAPIURL)
    val params = statsParams(from, to, by, region)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders, parameters = params)
  }

  // http://docs.fastly.com/docs/stats#Fields
  def statsWithFieldFilter(from: DateTime, to: DateTime, by: By.Value, region: Region.Value = Region.all, field: String): Future[Response] = {
    val apiUrl = "%s/stats/field/%s".format(fastlyAPIURL, field)
    val params = statsParams(from, to, by, region)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders, parameters = params)
  }

  // http://docs.fastly.com/docs/stats
  def statsAggregate(from: DateTime, to: DateTime, by: By.Value, region: Region.Value = Region.all): Future[Response] = {
    val apiUrl = "%s/stats/aggregate".format(fastlyAPIURL)
    val params = statsParams(from, to, by, region)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders, parameters = params)
  }

  // http://docs.fastly.com/docs/stats
  def statsForService(from: DateTime, to: DateTime, by: By.Value, region: Region.Value = Region.all, serviceId: String): Future[Response] = {
    val apiUrl = "%s/stats/service/%s".format(fastlyAPIURL, serviceId)
    val params = statsParams(from, to, by, region)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders, parameters = params)
  }

  // http://docs.fastly.com/docs/stats
  def statsForServiceWithFieldFilter(from: DateTime, to: DateTime, by: By.Value, region: Region.Value = Region.all, serviceId: String, field: String): Future[Response] = {
    val apiUrl = "%s/stats/service/%s/field/%s".format(fastlyAPIURL, serviceId, field)
    val params = statsParams(from, to, by, region)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders, parameters = params)
  }

  // http://docs.fastly.com/docs/stats
  def statsUsage(from: DateTime, to: DateTime, by: By.Value, region: Region.Value = Region.all): Future[Response] = {
    val apiUrl = "%s/stats/usage".format(fastlyAPIURL)
    val params = statsParams(from, to, by, region)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders, parameters = params)
  }

  // http://docs.fastly.com/docs/stats
  def statsUsageGroupedByService(from: DateTime, to: DateTime, by: By.Value, region: Region.Value = Region.all): Future[Response] = {
    val apiUrl = "%s/stats/usage_by_service".format(fastlyAPIURL)
    val params = statsParams(from, to, by, region)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders, parameters = params)
  }

  // http://docs.fastly.com/docs/stats
  def statsRegions(): Future[Response] = {
    val apiUrl = "%s/stats/regions".format(fastlyAPIURL)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders)
  }

  private def statsParams(from: DateTime, to: DateTime, by: By.Value, region: Region.Value = Region.all): Map[String, String] = {
    def millis(date: DateTime): String = (date.getMillis / 1000).toString
    Map[String, String]("from" -> millis(from), "to" -> millis(to), "by" -> by.toString, "region" -> region.toString)
  }

  def closeConnectionPool() = AsyncHttpExecutor.close()

  private object AsyncHttpExecutor {

    private lazy val defaultConfig = new AsyncHttpClientConfig.Builder()
      .setAllowPoolingConnection(true)
      .setMaximumConnectionsTotal(50)
      .setMaxRequestRetry(3)
      .setRequestTimeoutInMs(20000)
      .build()

    private lazy val client = new AsyncHttpClient(config.getOrElse(defaultConfig))

    def close() = client.close()

    def execute(apiUrl: String,
                method: HttpMethod = GET,
                headers: Map[String, String] = Map.empty,
                parameters: Map[String, String] = Map.empty) : Future[Response] = {
      val request = method match {
        case POST => client.preparePost(apiUrl)
        case PUT => client.preparePut(apiUrl)
        case DELETE => client.prepareDelete(apiUrl)
        case GET => client.prepareGet(apiUrl)
      }
      build(request, headers, parameters)

      proxyServer.map {
        ps => request.setProxyServer(ps)
      }

      val p = Promise[Response]()
      val handler = new AsyncCompletionHandler[Unit] {
        def onCompleted(response: Response) = p.complete(Success(response))
      }
      request.execute(handler)
      p.future
    }

    private def build(request: AsyncHttpClient#BoundRequestBuilder, headers: Map[String, String], parameters: Map[String, String] = Map.empty) = {

      implicit def mapToFluentCaseInsensitiveStringsMap(headers: Map[String, String]): FluentCaseInsensitiveStringsMap = {
        val fluentCaseInsensitiveStringsMap = new FluentCaseInsensitiveStringsMap()
        headers.foreach({
          case (name: String, value: String) => fluentCaseInsensitiveStringsMap.add(name, value)
        })
        fluentCaseInsensitiveStringsMap
      }

      implicit def mapToFluentStringsMap(parameters: Map[String, String]): FluentStringsMap = {
        val fluentStringsMap = new FluentStringsMap()
        parameters.foreach({
          case (name: String, value: String) => fluentStringsMap.add(name, value)
        })
        fluentStringsMap
      }

      request.setHeaders(headers)

      if (request.build().getMethod == "GET") {
        request.setQueryParameters(parameters)
      } else {
        request.setParameters(parameters)
      }

      headers.get("Host").map( h => request.setVirtualHost(h))
    }
  }

}

// constants for the stats API
object By extends Enumeration {
  val minute, hour, day = Value
}

object Region extends Enumeration {
  val all, usa, europe, ausnz, apac = Value
}
