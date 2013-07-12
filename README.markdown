An asynchronous Scala client for Fastly's [API](http://www.fastly.com/docs/api)
=============================================================================

Used to update/deploy/query Fastly and query the stats api.

Dependencies
------------

    resolvers += "Guardian Github Releases" at "http://guardian.github.com/maven/repo-releases"

    libraryDependencies += "com.gu" %% "fastlyapiclient" % "0.3.0"


Configuring the client
----------------------

Instantiate the client

    val fastlyApiClient = FastlyAPIClient("my-fastly-api-key", "my-service-id")
    val fastlyApiClient = FastlyAPIClient("my-fastly-api-key", "my-service-id", config = Some(asyncHttpClientConfig), proxy = Some(proxyToAccessTheWorld))


Examples
--------

All methods return a ListenableFuture[Response] call *future.get* if you want to be synchronous and wait for the response.
Or, to be asynchronous, pass an optional AsyncHandler to any method, e.g.

    def purge(url: String, ..., handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {...


Datacenter stats:

    fastlyApiClient.stats(startDatetime, endDatetime, By.minute)
    fastlyApiClient.stats(startDatetime, endDatetime, By.minute, regio = Region.usa)
    fastlyApiClient.stats(startDatetime, endDatetime, By.minute, region = Region.usa, handler = myHandler)

