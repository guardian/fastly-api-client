package com.gu

import com.ning.http.client._
import java.util.Date

case class FastlyAPIClient(apiKey: String, serviceId: String, config: Option[AsyncHttpClientConfig] = None) {

  private val fastlyAPIURL = "https://api.fastly.com"
  private val commonHeaders = Map("X-Fastly-Key" -> apiKey, "Accept" -> "application/json")

  /**
   * Fastly endpoints
   */
  def vclUpload(vcl: String, id: String, name: String, version: Int, handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val url = "%s/service/%s/version/%d/vcl".format(fastlyAPIURL, serviceId, version)
    AsyncHttpExecutor.post(
      url,
      headers = commonHeaders ++ Map("Content-Type" -> "application/x-www-form-urlencoded"),
      parameters = Map("content" -> vcl, "name" -> name, "id" -> id),
      handler = handler
    )
  }

  def vclUpdate(vcl: Map[String, String], version: Int, handler: Option[AsyncHandler[Response]] = None): List[ListenableFuture[Response]] = {
    vcl.map({
      case (name, file) => {
        val url = "%s/service/%s/version/%d/vcl/%s".format(fastlyAPIURL, serviceId, version, name)
        AsyncHttpExecutor.put(
          url,
          headers = commonHeaders ++ Map("Content-Type" -> "application/x-www-form-urlencoded"),
          parameters = Map("content" -> file, "name" -> name),
          handler = handler
        )
      }
    }).toList
  }

  def purge(url: String, extraHeaders: Map[String, String] = Map(), handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val clearCacheUrl = String.format("https://app.fastly.com/purge/%s", url.stripPrefix("http://"))
    AsyncHttpExecutor.post(clearCacheUrl, headers = Map("X-Fastly-Key" -> apiKey) ++ extraHeaders, handler = handler)
  }

  def purgeStatus(purgeId: String, handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val url = fastlyAPIURL + "/purge"
    AsyncHttpExecutor.get(url, headers = commonHeaders ++ Map("Accept" -> "*/*"), parameters = Map("id" -> purgeId), handler = handler)
  }

  def versions(handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val url = "%s/service/%s/version".format(fastlyAPIURL, serviceId)
    AsyncHttpExecutor.get(url, headers = commonHeaders, handler = handler)
  }

  def versionClone(version: Int, handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val url = "%s/service/%s/version/%d/clone".format(fastlyAPIURL, serviceId, version)
    AsyncHttpExecutor.put(url, headers = commonHeaders ++ Map("Content-Type" -> "application/x-www-form-urlencoded"), handler = handler)
  }

  def versionActivate(version: Int, handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val url = "%s/service/%s/version/%d/activate".format(fastlyAPIURL, serviceId, version)
    AsyncHttpExecutor.put(url, headers = commonHeaders, handler = handler)
  }

  def versionCreate(handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val url = "%s/service/%s/version".format(fastlyAPIURL, serviceId)
    AsyncHttpExecutor.post(url, headers = commonHeaders, handler = handler)
  }

  def vclSetAsMain(version: Int, name: String, handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val url = "%s/service/%s/version/%d/vcl/%s/main".format(fastlyAPIURL, serviceId, version, name)
    AsyncHttpExecutor.put(url, headers = commonHeaders, handler = handler)
  }

  def vclList(version: Int, handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val url = "%s/service/%s/version/%d/vcl".format(fastlyAPIURL, serviceId, version)
    AsyncHttpExecutor.get(url, headers = commonHeaders, handler = handler)
  }

  def vclDelete(version: Int, name: String, handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val url = "%s/service/%s/version/%s/vcl/%s".format(fastlyAPIURL, serviceId, version, name)
    AsyncHttpExecutor.delete(url, headers = commonHeaders, handler = handler)
  }

  def backendCreate(version: Int, id: String, address: String, port: Int, handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val url = "%s/service/%s/version/%d/backend".format(fastlyAPIURL, serviceId, version)
    val params = Map("ipv4" -> address, "version" -> version.toString, "id" -> id, "port" -> port.toString, "service" -> serviceId)
    AsyncHttpExecutor.post(url, headers = commonHeaders, parameters = params, handler = handler)
  }

  def backends(version: Int, handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    val url = "%s/service/%s/version/%d/backend".format(fastlyAPIURL, serviceId, version)
    AsyncHttpExecutor.get(url, headers = commonHeaders, handler = handler)
  }

  def stats(start: Date, end: Date, handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {
    def toUnixTimeStamp(date: Date): Long = date.getTime / 1000L
    val apiUrl = "https://app.fastly.com/service/%s/stats/summary?fields=all&start_time=%d&end_time=%d".format(serviceId, toUnixTimeStamp(start), toUnixTimeStamp(end))
    AsyncHttpExecutor.get(apiUrl, headers = commonHeaders, handler = handler)
  }

  /**
   * Useful methods
   * Be warned! May be *wildly* inefficient! as I do not want to pull in more dependencies, e.g. Lift Json.
   */
  def latestVersionNumber(handler: Option[AsyncHandler[Response]] = None): Int = {
    val body = versions(handler).get.getResponseBody
    val versionRe = """number"\s*:(\d+)""".r
    val versionNumbers = versionRe.findAllIn(body).matchData.toList.map(_.subgroups).flatten
    versionNumbers.sortWith(_.toInt > _.toInt).head.toInt
  }

  private object AsyncHttpExecutor {

    lazy val defaultConfig = new AsyncHttpClientConfig.Builder()
      .setAllowPoolingConnection(true)
      .setMaximumConnectionsTotal(50)
      .setMaxRequestRetry(3)
      .setRequestTimeoutInMs(20000)
      .build()

    lazy val client = new AsyncHttpClient(config.getOrElse(defaultConfig))

    def get(apiUrl: String,
            headers: Map[String, String] = Map(),
            parameters: Map[String, String] = Map(),
            handler: Option[AsyncHandler[Response]]): ListenableFuture[Response] = {
      execute(apiUrl, "GET", headers, parameters, handler)
    }

    def put(apiUrl: String,
            headers: Map[String, String] = Map(),
            parameters: Map[String, String] = Map(),
            handler: Option[AsyncHandler[Response]]): ListenableFuture[Response] = {
      execute(apiUrl, "PUT", headers, parameters, handler)
    }

    def post(apiUrl: String,
             headers: Map[String, String] = Map(),
             parameters: Map[String, String] = Map(),
             handler: Option[AsyncHandler[Response]]): ListenableFuture[Response] = {
      execute(apiUrl, "POST", headers, parameters, handler)
    }

    def delete(apiUrl: String,
               headers: Map[String, String] = Map(),
               parameters: Map[String, String] = Map(),
               handler: Option[AsyncHandler[Response]]): ListenableFuture[Response] = {
      execute(apiUrl, "DELETE", headers, parameters, handler)
    }

    private def execute(apiUrl: String,
                        method: String,
                        headers: Map[String, String],
                        parameters: Map[String, String],
                        handler: Option[AsyncHandler[Response]]): ListenableFuture[Response] = {
      val request = method match {
        case "POST" => client.preparePost(apiUrl)
        case "PUT" => client.preparePut(apiUrl)
        case "DELETE" => client.prepareDelete(apiUrl)
        case _ => client.prepareGet(apiUrl)
      }
      build(request, headers, parameters)
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
      request.setParameters(parameters)

      if (headers.get("Host").isDefined) request.setVirtualHost(headers.get("Host").get)
    }
  }

}
