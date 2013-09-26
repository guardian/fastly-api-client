Fastly API client
=================

An asynchronous Scala client for Fastly's [API](http://www.fastly.com/docs/api) used to deploy to or update your Fastly config, as well as query their stats API.

[http://www.fastly.com/docs/api](http://www.fastly.com/docs/api)

[http://www.fastly.com/docs/stats](http://www.fastly.com/docs/stats)

[http://search.maven.org/#browse|948553587](Hosted on maven central)

SBT
---

    libraryDependencies += "com.gu" %% "fastly-api-client" % "0.1.0"


Configuring the client
----------------------

Use the default AsyncHttpClientConfig

    val client = FastlyAPIClient("my-fastly-api-key", "my-service-id")

Or define your own AsyncHttpClientConfig

    val client = FastlyAPIClient("my-fastly-api-key", "my-service-id", config = Some(asyncHttpClientConfig))

Set a proxy if needed,

    val client = FastlyAPIClient("my-fastly-api-key", "my-service-id", config = Some(asyncHttpClientConfig), proxy = Some(proxyToAccessTheWorld))

Asynchronous calls
------------------

All methods return a Future[Response] call *future.get* if you want to be synchronous and wait for the response.
Or, to be asynchronous, pass an optional AsyncHandler to any method e.g.

    val future = client.purge(url, handler = myHandler)

This client uses the [HTTP Asyc Client](https://github.com/AsyncHttpClient/async-http-client), have a look there for examples on creating handlers/configuring the client config.

Examples
========

Purging
-------
    val future = client.purge(url, handler = myHandler)
    val future = client.purgeStatus(purgeId, handler = myHandler)

Deploying
---------

    client.versions(...) // find the active version
    client.versionClone(...) // clone the active version
    client.vclDelete(...) // delete all the VCL files ready for the new ones
    client.vclUpload(...) // upload you new VCL files
    client.vclSetAsMain(...) // define the main VCL file
    client.versionValidate(...) // validate the cloned version
    client.versionActivate(...) // active the cloned version

Datacenter stats
----------------
    val future = client.stats(startDatetime, endDatetime, By.minute)
    val future = client.stats(startDatetime, endDatetime, By.hour, region = Region.usa)
    val future = client.stats(startDatetime, endDatetime, By.day, region = Region.all, handler = myHandler)
