An asynchronous Scala client for Fastly's [API](http://www.fastly.com/docs/api) used to deploy to or update your Fastly config, as well as query their stats API.

[http://www.fastly.com/docs/api](http://www.fastly.com/docs/api)

[http://www.fastly.com/docs/stats](http://www.fastly.com/docs/stats)

[http://search.maven.org/#browse|948553587](Hosted on maven central)

SBT
---

    libraryDependencies += "com.gu" %% "fastly-api-client" % "0.1.0"


Configuring the client
----------------------

Instantiate the client

    val client = FastlyAPIClient("my-fastly-api-key", "my-service-id")
    val client = FastlyAPIClient("my-fastly-api-key", "my-service-id", config = Some(asyncHttpClientConfig), proxy = Some(proxyToAccessTheWorld))


Examples
--------

All methods return a Future[Response] call *future.get* if you want to be synchronous and wait for the response.
Or, to be asynchronous, pass an optional AsyncHandler to any method, e.g.

    val future = purge(url, myHandler)

This client uses the [HTTP Asyc Client](https://github.com/AsyncHttpClient/async-http-client), have a look there for examples on creating handlers/configuring the client config.

Datacenter stats:

    val future = fastlyApiClient.stats(startDatetime, endDatetime, By.minute)
    val future = fastlyApiClient.stats(startDatetime, endDatetime, By.minute, region = Region.usa)
    val future = fastlyApiClient.stats(startDatetime, endDatetime, By.minute, region = Region.usa, handler = myHandler)
