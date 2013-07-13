An asynchronous Scala client for Fastly's [API](http://www.fastly.com/docs/api)
===============================================================================

Used to update/deploy/query Fastly and query their stats API.

[http://www.fastly.com/docs/api](http://www.fastly.com/docs/api)
[http://www.fastly.com/docs/stats](http://www.fastly.com/docs/stats)


Dependencies
------------

    resolvers += "Guardian Github Releases" at "http://guardian.github.com/maven/repo-releases"

    libraryDependencies += "com.gu" %% "fastlyapiclient" % "0.3.0"


Configuring the client
----------------------

Instantiate the client

    val client = FastlyAPIClient("my-fastly-api-key", "my-service-id")
    val client = FastlyAPIClient("my-fastly-api-key", "my-service-id", config = Some(asyncHttpClientConfig), proxy = Some(proxyToAccessTheWorld))


Examples
--------

All methods return a ListenableFuture[Response] call *future.get* if you want to be synchronous and wait for the response.
Or, to be asynchronous, pass an optional AsyncHandler to any method, e.g.

    def purge(url: String, ..., handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {...

This client uses the [HTTP Asyc Client](https://github.com/AsyncHttpClient/async-http-client), have a look there for examples on creating handlers/configuring the client config.

Datacenter stats:

    fastlyApiClient.stats(startDatetime, endDatetime, By.minute)
    fastlyApiClient.stats(startDatetime, endDatetime, By.minute, region = Region.usa)
    fastlyApiClient.stats(startDatetime, endDatetime, By.minute, region = Region.usa, handler = myHandler)
