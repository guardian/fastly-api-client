package com.gu.fastly.api

import org.joda.time.DateTime
import scala.language.implicitConversions
import dispatch._
import com.ning.http.client.{AsyncHttpClient, Response, AsyncHttpClientConfig}
import com.ning.http.client.providers.netty.{NettyAsyncHttpProvider, NettyConnectionsPool}
import scala.concurrent.ExecutionContext.Implicits.global

// http://www.fastly.com/docs/api
// http://www.fastly.com/docs/stats
case class FastlyApiClient(apiKey: String, serviceId: String, config: Option[AsyncHttpClientConfig] = None) {
  import HttpMethods._

  private val fastlyAPIURL = "https://api.fastly.com"
  private val commonHeaders = Map("X-Fastly-Key" -> apiKey, "Accept" -> "application/json")

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
    vcl.map({ case (name, file) =>
      val apiUrl = "%s/service/%s/version/%d/vcl/%s".format(fastlyAPIURL, serviceId, version, name)
      AsyncHttpExecutor.execute(
        apiUrl,
        PUT,
        headers = commonHeaders ++ Map("Content-Type" -> "application/x-www-form-urlencoded"),
        parameters = Map("content" -> file, "name" -> name)
      )
    }).toList
  }

  // http://docs.fastly.com/docs/api#purge_3
  def purge(url: String, extraHeaders: Map[String, String] = Map()): Future[Response] = {
    val apiUrl = "%s/purge/%s".format(fastlyAPIURL, url.stripPrefix("http://").stripPrefix("https://"))
    AsyncHttpExecutor.execute(apiUrl, POST, headers = Map("X-Fastly-Key" -> apiKey) ++ extraHeaders)
  }

  // http://docs.fastly.com/docs/api#purge_4
  def purgeStatus(purgeId: String): Future[Response] = {
    val apiUrl = "%s/purge".format(fastlyAPIURL)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders ++ Map("Accept" -> "*/*"), parameters = Map("id" -> purgeId))
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
    private lazy val client = {
      val conf = config getOrElse new AsyncHttpClientConfig.Builder()
        .setAllowPoolingConnection(true)
        .setMaximumConnectionsTotal(50)
        .setMaxRequestRetry(3)
        .setRequestTimeoutInMs(20000)
        .build()

      val connectionPool = new NettyConnectionsPool(new NettyAsyncHttpProvider(conf))
      new AsyncHttpClient(new AsyncHttpClientConfig.Builder(conf).setConnectionsPool(connectionPool).build)
    }

    private lazy val Http = dispatch.Http(client)

    def close() = Http.client.close()

    def execute(apiUrl: String,
                method: HttpMethods.Value = GET,
                headers: Map[String, String] = Map(),
                parameters: Map[String, String] = Map()): Future[Response] = {
      val withHeaders = headers.foldLeft(url(apiUrl)) {
        case (url, (k, v)) => url.addHeader(k, v)
      }

      val withParameters = if (method == GET) {
        withHeaders <<? parameters
      } else parameters.foldLeft(withHeaders) {
        case (req, (k, v)) => req.addParameter(k, v)
      }

      val req = method match {
        case GET => withParameters.GET
        case POST => withParameters.POST
        case PUT => withParameters.PUT
        case DELETE => withParameters.DELETE
      }

      headers.get("Host").foreach(req.setVirtualHost)

      Http(req OK as.Response(identity))
    }
  }
}

object HttpMethods extends Enumeration {
  val GET, PUT, POST, DELETE = Value
}

// constants for the stats API
object By extends Enumeration {
  val minute, hour, day = Value
}

object Region extends Enumeration {
  val all, usa, europe, ausnz, apac = Value
}
