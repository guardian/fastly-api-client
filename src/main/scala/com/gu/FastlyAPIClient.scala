package com.gu

import com.ning.http.client._
import org.joda.time.DateTime
import java.util.concurrent.Future

// http://www.fastly.com/docs/api
// http://www.fastly.com/docs/stats
case class FastlyAPIClient(apiKey: String, serviceId: String, config: Option[AsyncHttpClientConfig] = None, proxyServer: Option[ProxyServer] = None) {

  private val fastlyAPIURL = "https://api.fastly.com"
  private val commonHeaders = Map("X-Fastly-Key" -> apiKey, "Accept" -> "application/json")

  private val GET = "GET"
  private val POST = "POST"
  private val PUT = "PUT"
  private val DELETE = "DELETE"

  // http://docs.fastly.com/docs/api#vcl_7
  def vclUpload(version: Int, vcl: String, id: String, name: String, handler: Option[AsyncHandler[Response]] = None): Future[Response] = {
    val apiUrl = "%s/service/%s/version/%d/vcl".format(fastlyAPIURL, serviceId, version)
    AsyncHttpExecutor.execute(
      apiUrl,
      POST,
      headers = commonHeaders ++ Map("Content-Type" -> "application/x-www-form-urlencoded"),
      parameters = Map("content" -> vcl, "name" -> name, "id" -> id),
      handler = handler
    )
  }

  // http://docs.fastly.com/docs/api#vcl_10
  def vclUpdate(version: Int, vcl: Map[String, String], handler: Option[AsyncHandler[Response]] = None): List[Future[Response]] = {
    vcl.map({
      case (name, file) => {
        val apiUrl = "%s/service/%s/version/%d/vcl/%s".format(fastlyAPIURL, serviceId, version, name)
        AsyncHttpExecutor.execute(
          apiUrl,
          PUT,
          headers = commonHeaders ++ Map("Content-Type" -> "application/x-www-form-urlencoded"),
          parameters = Map("content" -> file, "name" -> name),
          handler = handler
        )
      }
    }).toList
  }

  // http://docs.fastly.com/docs/api#purge_3
  def purge(url: String, extraHeaders: Map[String, String] = Map(), handler: Option[AsyncHandler[Response]] = None): Future[Response] = {
    val apiUrl = "%s/purge/%s".format(fastlyAPIURL, url.stripPrefix("http://").stripPrefix("https://"))
    AsyncHttpExecutor.execute(apiUrl, POST, headers = Map("X-Fastly-Key" -> apiKey) ++ extraHeaders, handler = handler)
  }

  // http://docs.fastly.com/docs/api#purge_4
  def purgeStatus(purgeId: String, handler: Option[AsyncHandler[Response]] = None): Future[Response] = {
    val apiUrl = "%s/purge".format(fastlyAPIURL)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders ++ Map("Accept" -> "*/*"), parameters = Map("id" -> purgeId), handler = handler)
  }

  // http://docs.fastly.com/docs/api#version_2
  def versionCreate(handler: Option[AsyncHandler[Response]] = None): Future[Response] = {
    val apiUrl = "%s/service/%s/version".format(fastlyAPIURL, serviceId)
    AsyncHttpExecutor.execute(apiUrl, PUT, headers = commonHeaders, handler = handler)
  }

  // http://docs.fastly.com/docs/api#version_3
  def versionList(handler: Option[AsyncHandler[Response]] = None): Future[Response] = {
    val apiUrl = "%s/service/%s/version".format(fastlyAPIURL, serviceId)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders, handler = handler)
  }

  // http://docs.fastly.com/docs/api#version_5
  def versionActivate(version: Int, handler: Option[AsyncHandler[Response]] = None): Future[Response] = {
    val apiUrl = "%s/service/%s/version/%d/activate".format(fastlyAPIURL, serviceId, version)
    AsyncHttpExecutor.execute(apiUrl, PUT, headers = commonHeaders, handler = handler)
  }

  // http://docs.fastly.com/docs/api#version_7
  def versionClone(version: Int, handler: Option[AsyncHandler[Response]] = None): Future[Response] = {
    val apiUrl = "%s/service/%s/version/%d/clone".format(fastlyAPIURL, serviceId, version)
    AsyncHttpExecutor.execute(apiUrl, PUT, headers = commonHeaders ++ Map("Content-Type" -> "application/x-www-form-urlencoded"), handler = handler)
  }

  // http://docs.fastly.com/docs/api#version_8
  def versionValidate(version: Int, handler: Option[AsyncHandler[Response]] = None): Future[Response] = {
    val apiUrl = "%s/service/%s/version/%s/validate".format(fastlyAPIURL, serviceId, version)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders, handler = handler)
  }

  // http://docs.fastly.com/docs/api#vcl_9
  def vclSetAsMain(version: Int, name: String, handler: Option[AsyncHandler[Response]] = None): Future[Response] = {
    val apiUrl = "%s/service/%s/version/%d/vcl/%s/main".format(fastlyAPIURL, serviceId, version, name)
    AsyncHttpExecutor.execute(apiUrl, PUT, headers = commonHeaders, handler = handler)
  }

  // http://docs.fastly.com/docs/api#vcl_1
  def vclList(version: Int, handler: Option[AsyncHandler[Response]] = None): Future[Response] = {
    val apiUrl = "%s/service/%s/version/%d/vcl".format(fastlyAPIURL, serviceId, version)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders, handler = handler)
  }

  // http://docs.fastly.com/docs/api#vcl_8
  def vclDelete(version: Int, name: String, handler: Option[AsyncHandler[Response]] = None): Future[Response] = {
    val apiUrl = "%s/service/%s/version/%s/vcl/%s".format(fastlyAPIURL, serviceId, version, name)
    AsyncHttpExecutor.execute(apiUrl, DELETE, headers = commonHeaders, handler = handler)
  }

  // http://docs.fastly.com/docs/api#backend_1
  def backendCheckAll(version: Int, handler: Option[AsyncHandler[Response]] = None): Future[Response] = {
    val apiUrl = "%s/service/%s/version/%d/backend/check_all".format(fastlyAPIURL, serviceId, version)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders, handler = handler)
  }

  // http://docs.fastly.com/docs/api#backend_3
  def backendCreate(version: Int, id: String, address: String, port: Int, handler: Option[AsyncHandler[Response]] = None): Future[Response] = {
    val apiUrl = "%s/service/%s/version/%d/backend".format(fastlyAPIURL, serviceId, version)
    val params = Map("ipv4" -> address, "version" -> version.toString, "id" -> id, "port" -> port.toString, "service" -> serviceId)
    AsyncHttpExecutor.execute(apiUrl, POST, headers = commonHeaders, parameters = params, handler = handler)
  }

  // http://docs.fastly.com/docs/api#backend_4
  def backendList(version: Int, handler: Option[AsyncHandler[Response]] = None): Future[Response] = {
    val apiUrl = "%s/service/%s/version/%d/backend".format(fastlyAPIURL, serviceId, version)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders, handler = handler)
  }

  // http://docs.fastly.com/docs/api#service_1
  def serviceList(handler: Option[AsyncHandler[Response]] = None): Future[Response] = {
    val apiUrl = "%s/service".format(fastlyAPIURL)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders, handler = handler)
  }

  // http://docs.fastly.com/docs/stats#Range
  // http://docs.fastly.com/docs/stats#Sample
  // http://docs.fastly.com/docs/stats#Region
  def stats(from: DateTime, to: DateTime, by: By.Value, region: Region.Value = Region.all, handler: Option[AsyncHandler[Response]] = None): Future[Response] = {
    val apiUrl = "%s/stats".format(fastlyAPIURL)
    val params = statsParams(from, to, by, region)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders, parameters = params, handler = handler)
  }

  // http://docs.fastly.com/docs/stats#Fields
  def statsWithFieldFilter(from: DateTime, to: DateTime, by: By.Value, region: Region.Value = Region.all, field: String, handler: Option[AsyncHandler[Response]] = None): Future[Response] = {
    val apiUrl = "%s/stats/field/%s".format(fastlyAPIURL, field)
    val params = statsParams(from, to, by, region)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders, parameters = params, handler = handler)
  }

  // http://docs.fastly.com/docs/stats
  def statsAggregate(from: DateTime, to: DateTime, by: By.Value, region: Region.Value = Region.all, handler: Option[AsyncHandler[Response]] = None): Future[Response] = {
    val apiUrl = "%s/stats/aggregate".format(fastlyAPIURL)
    val params = statsParams(from, to, by, region)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders, parameters = params, handler = handler)
  }

  // http://docs.fastly.com/docs/stats
  def statsForService(from: DateTime, to: DateTime, by: By.Value, region: Region.Value = Region.all, serviceId: String, handler: Option[AsyncHandler[Response]] = None): Future[Response] = {
    val apiUrl = "%s/stats/service/%s".format(fastlyAPIURL, serviceId)
    val params = statsParams(from, to, by, region)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders, parameters = params, handler = handler)
  }

  // http://docs.fastly.com/docs/stats
  def statsForServiceWithFieldFilter(from: DateTime, to: DateTime, by: By.Value, region: Region.Value = Region.all, serviceId: String, field: String, handler: Option[AsyncHandler[Response]] = None): Future[Response] = {
    val apiUrl = "%s/stats/service/%s/field/%s".format(fastlyAPIURL, serviceId, field)
    val params = statsParams(from, to, by, region)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders, parameters = params, handler = handler)
  }

  // http://docs.fastly.com/docs/stats
  def statsUsage(from: DateTime, to: DateTime, by: By.Value, region: Region.Value = Region.all, handler: Option[AsyncHandler[Response]] = None): Future[Response] = {
    val apiUrl = "%s/stats/usage".format(fastlyAPIURL)
    val params = statsParams(from, to, by, region)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders, parameters = params, handler = handler)
  }

  // http://docs.fastly.com/docs/stats
  def statsUsageGroupedByService(from: DateTime, to: DateTime, by: By.Value, region: Region.Value = Region.all, handler: Option[AsyncHandler[Response]] = None): Future[Response] = {
    val apiUrl = "%s/stats/usage_by_service".format(fastlyAPIURL)
    val params = statsParams(from, to, by, region)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders, parameters = params, handler = handler)
  }

  // http://docs.fastly.com/docs/stats
  def statsRegions(handler: Option[AsyncHandler[Response]] = None): Future[Response] = {
    val apiUrl = "%s/stats/regions".format(fastlyAPIURL)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders, handler = handler)
  }

  private def statsParams(from: DateTime, to: DateTime, by: By.Value, region: Region.Value = Region.all): Map[String, String] = {
    def millis(date: DateTime): String = (date.getMillis / 1000).toString
    Map[String, String]("from" -> millis(from), "to" -> millis(to), "by" -> by.toString, "region" -> region.toString)
  }

  def closeConnectionPool = AsyncHttpExecutor.close

  private object AsyncHttpExecutor {

    private lazy val defaultConfig = new AsyncHttpClientConfig.Builder()
      .setAllowPoolingConnection(true)
      .setMaximumConnectionsTotal(50)
      .setMaxRequestRetry(3)
      .setRequestTimeoutInMs(20000)
      .build()

    private lazy val client = new AsyncHttpClient(config.getOrElse(defaultConfig))

    def close = client.close()

    def execute(apiUrl: String,
                method: String = GET,
                headers: Map[String, String] = Map(),
                parameters: Map[String, String] = Map(),
                handler: Option[AsyncHandler[Response]]): Future[Response] = {
      val request = method match {
        case POST => client.preparePost(apiUrl)
        case PUT => client.preparePut(apiUrl)
        case DELETE => client.prepareDelete(apiUrl)
        case _ => client.prepareGet(apiUrl)
      }
      build(request, headers, parameters)

      proxyServer.map {
        ps => request.setProxyServer(ps)
      }

      if (handler.isDefined) {
        request.execute(handler.get)
      } else {
        request.execute
      }
    }

    private def build(request: AsyncHttpClient#BoundRequestBuilder, headers: Map[String, String], parameters: Map[String, String] = Map()) = {

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

      if (request.build().getMethod == GET) {
        request.setQueryParameters(parameters)
      } else {
        request.setParameters(parameters)
      }

      if (headers.get("Host").isDefined) request.setVirtualHost(headers.get("Host").get)
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
