package com.gu

import com.ning.http.client._
import org.joda.time.DateTime

// http://www.fastly.com/docs/stats
// TODO: can I set the proxyServer in the config instead, and thus delete it?
case class FastlyAPIClient(apiKey: String, serviceId: String, config: Option[AsyncHttpClientConfig] = None, proxyServer: Option[ProxyServer] = None) {

  private val fastlyAPIURL = "https://api.fastly.com"
  private val commonHeaders = Map("X-Fastly-Key" -> apiKey, "Accept" -> "application/json")

  private val GET = "GET"
  private val POST = "POST"
  private val PUT = "PUT"
  private val DELETE = "DELETE"

  def vclUpload(vcl: String, id: String, name: String, version: Int, handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val url = "%s/service/%s/version/%d/vcl".format(fastlyAPIURL, serviceId, version)
    AsyncHttpExecutor.execute(
      url,
      "POST",
      headers = commonHeaders ++ Map("Content-Type" -> "application/x-www-form-urlencoded"),
      parameters = Map("content" -> vcl, "name" -> name, "id" -> id),
      handler = handler
    )
  }

  def vclUpdate(vcl: Map[String, String], version: Int, handler: Option[AsyncHandler[Response]] = None): List[ListenableFuture[Response]] = {
    vcl.map({
      case (name, file) => {
        val url = "%s/service/%s/version/%d/vcl/%s".format(fastlyAPIURL, serviceId, version, name)
        AsyncHttpExecutor.execute(
          url,
          "PUT",
          headers = commonHeaders ++ Map("Content-Type" -> "application/x-www-form-urlencoded"),
          parameters = Map("content" -> file, "name" -> name),
          handler = handler
        )
      }
    }).toList
  }

  def purge(url: String, extraHeaders: Map[String, String] = Map(), handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val clearCacheUrl = String.format("https://app.fastly.com/purge/%s", url.stripPrefix("http://"))
    AsyncHttpExecutor.execute(clearCacheUrl, POST, headers = Map("X-Fastly-Key" -> apiKey) ++ extraHeaders, handler = handler)
  }

  def purgeStatus(purgeId: String, handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val url = fastlyAPIURL + "/purge"
    AsyncHttpExecutor.execute(url, headers = commonHeaders ++ Map("Accept" -> "*/*"), parameters = Map("id" -> purgeId), handler = handler)
  }

  def versions(handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val url = "%s/service/%s/version".format(fastlyAPIURL, serviceId)
    AsyncHttpExecutor.execute(url, headers = commonHeaders, handler = handler)
  }

  def versionClone(version: Int, handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val url = "%s/service/%s/version/%d/clone".format(fastlyAPIURL, serviceId, version)
    AsyncHttpExecutor.execute(url, PUT, headers = commonHeaders ++ Map("Content-Type" -> "application/x-www-form-urlencoded"), handler = handler)
  }

  def versionActivate(version: Int, handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val url = "%s/service/%s/version/%d/activate".format(fastlyAPIURL, serviceId, version)
    AsyncHttpExecutor.execute(url, PUT, headers = commonHeaders, handler = handler)
  }

  def versionCreate(handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val url = "%s/service/%s/version".format(fastlyAPIURL, serviceId)
    AsyncHttpExecutor.execute(url, PUT, headers = commonHeaders, handler = handler)
  }

  def vclSetAsMain(version: Int, name: String, handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val url = "%s/service/%s/version/%d/vcl/%s/main".format(fastlyAPIURL, serviceId, version, name)
    AsyncHttpExecutor.execute(url, PUT, headers = commonHeaders, handler = handler)
  }

  def vclList(version: Int, handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val url = "%s/service/%s/version/%d/vcl".format(fastlyAPIURL, serviceId, version)
    AsyncHttpExecutor.execute(url, headers = commonHeaders, handler = handler)
  }

  def vclDelete(version: Int, name: String, handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val url = "%s/service/%s/version/%s/vcl/%s".format(fastlyAPIURL, serviceId, version, name)
    AsyncHttpExecutor.execute(url, DELETE, headers = commonHeaders, handler = handler)
  }

  def backendCreate(version: Int, id: String, address: String, port: Int, handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val url = "%s/service/%s/version/%d/backend".format(fastlyAPIURL, serviceId, version)
    val params = Map("ipv4" -> address, "version" -> version.toString, "id" -> id, "port" -> port.toString, "service" -> serviceId)
    AsyncHttpExecutor.execute(url, POST, headers = commonHeaders, parameters = params, handler = handler)
  }

  def backend(version: Int, handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val url = "%s/service/%s/version/%d/backend".format(fastlyAPIURL, serviceId, version)
    AsyncHttpExecutor.execute(url, headers = commonHeaders, handler = handler)
  }

  def services(handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val url = "%s/service".format(fastlyAPIURL)
    AsyncHttpExecutor.execute(url, headers = commonHeaders, handler = handler)
  }

  def statsUsage(handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val apiUrl = "%s/stats/usage".format(fastlyAPIURL)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders, handler = handler)
  }

  def stats(from: DateTime, to: DateTime, by: By.Value, region: Region.Value = Region.all, handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    def millis(date: DateTime): String = (date.getMillis / 1000).toString
    val apiUrl = "%s/stats".format(fastlyAPIURL)
    val params = Map[String, String]("from" -> millis(from), "to" -> millis(to), "by" -> by.toString, "region" -> region.toString)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders, parameters = params, handler = handler)
  }

  def statsRegionList(handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val apiUrl = "%s/stats/regions".format(fastlyAPIURL)
    AsyncHttpExecutor.execute(apiUrl, headers = commonHeaders, handler = handler)
  }

  private object AsyncHttpExecutor {

    private lazy val defaultConfig = new AsyncHttpClientConfig.Builder()
      .setAllowPoolingConnection(true)
      .setMaximumConnectionsTotal(50)
      .setMaxRequestRetry(3)
      .setRequestTimeoutInMs(20000)
      .build()

    private lazy val client = new AsyncHttpClient(config.getOrElse(defaultConfig))

    def execute(apiUrl: String,
                method: String = GET,
                headers: Map[String, String] = Map(),
                parameters: Map[String, String] = Map(),
                handler: Option[AsyncHandler[Response]]): ListenableFuture[Response] = {
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

      println(request.build().getRawUrl)

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
