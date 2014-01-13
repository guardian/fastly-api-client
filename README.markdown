Fastly API client
=================

An asynchronous Scala client for [Fastly's API](http://docs.fastly.com/api), used to deploy and update configs, decache objects and query the stats API

[Released to maven central](http://search.maven.org/#browse|948553587)

## Installation

### SBT

    libraryDependencies += "com.gu" %% "fastly-api-client" % "0.2.0"

### Maven
   
    <dependency>
        <groupId>com.gu</groupId>
        <artifactId>fastly-api-client_2.10</artifactId>
        <version>0.2.0</version>
    </dependency>


## Configuring the client

Use the default AsyncHttpClientConfig,

    val client = FastlyApiClient("my-fastly-api-key", "my-service-id")

Or define your own AsyncHttpClientConfig,

    val client = FastlyApiClient("my-fastly-api-key",
                    "my-service-id",
                    config = Some(asyncHttpClientConfig))

This client uses the [HTTP Async Client](https://github.com/AsyncHttpClient/async-http-client), have a look there for configuring your own AsyncHttpClientConfig. Don't worry, this is what [Dispatch Reboot](https://github.com/dispatch/reboot) uses too.

Set a proxy if needed,

    val client = FastlyApiClient("my-fastly-api-key",
                    "my-service-id",
                    proxy = Some(proxyToAccessTheWorld))


## Asynchronous calls

All methods return a scala.concurrent.Future[Response]

If you want to block, you must use the Await.result construct.

## Examples

### Purging

    client.purge(url)


### Deploying

This is the way Fastly recommended.

    client.versionList(...) // find the active version
    client.versionClone(...) // clone the active version
    client.vclDelete(...) // delete all the VCL files ready for the new ones
    client.vclUpload(...) // upload you new VCL files
    client.vclSetAsMain(...) // define the main VCL file
    client.versionValidate(...) // validate the cloned version
    client.versionActivate(...) // activate the cloned version


### Datacenter stats

    client.stats(startDatetime, endDatetime, By.minute)
    client.stats(startDatetime, endDatetime, By.hour, region = Region.usa)
    client.stats(startDatetime,
                        endDatetime,
                        By.day,
                        region = Region.all)

## Release notes for 0.2.0

* Now returns scala.concurrent.Future[Response] from all methods
* No longer need to pass a handler to your method calls because the Future[Response] replaces this need
* Removed call to purgeStatus method as this is no longer supported by Fastly
* Using [Scala's String interpolation] (http://docs.scala-lang.org/overviews/core/string-interpolation.html)
* Removed comments that point to HTML docs, as they are out of date
