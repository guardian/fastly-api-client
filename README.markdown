Fastly API client
=================

An asynchronous Scala (and Java interoperable) client for [Fastly's API](http://www.fastly.com/docs/api) used to deploy configs, update configs, decache objects and query the stats API.

[http://www.fastly.com/docs/api](http://www.fastly.com/docs/api)

[http://www.fastly.com/docs/stats](http://www.fastly.com/docs/stats)

[Released to maven central](http://search.maven.org/#browse|948553587)

## Installation

### SBT

    libraryDependencies += "com.gu" %% "fastly-api-client" % "0.1.2"

### Maven
   
    <dependency>
        <groupId>com.gu</groupId>
        <artifactId>fastly-api-client_2.10</artifactId>
        <version>0.1.2</version>
    </dependency>


## Configuring the client

Use the default AsyncHttpClientConfig,

    val client = FastlyAPIClient("my-fastly-api-key", "my-service-id")

Or define your own AsyncHttpClientConfig,

    val client = FastlyAPIClient("my-fastly-api-key",
                    "my-service-id",
                    config = Some(asyncHttpClientConfig))

Set a proxy if needed,

    val client = FastlyAPIClient("my-fastly-api-key",
                    "my-service-id",
                    proxy = Some(proxyToAccessTheWorld))


## Asynchronous calls

All methods return a Future[Response] call *future.get* if you want to be synchronous and wait for the response.
Or, to be asynchronous, pass an optional AsyncHandler to any method e.g.

    client.purge(url, handler = myHandler)

This client uses the [HTTP Async Client](https://github.com/AsyncHttpClient/async-http-client), have a look there for examples on creating a new AsyncHandler or configuring your own AsyncHttpClientConfig.


## Examples

### Purging

    client.purge(url, handler = myHandler)
    client.purgeStatus(purgeId, handler = myHandler)


### Deploying

    client.versions(...) // find the active version
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
                        region = Region.all,
                        handler = myHandler)
