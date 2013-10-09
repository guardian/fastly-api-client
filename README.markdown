Fastly API client
=================

An asynchronous Scala client for [Fastly's API](http://www.fastly.com/docs/api), used to deploy and update configs, decache objects and query the stats API

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

Set a proxy in the AsyncHttpClientConfig if needed

## Asynchronous calls

All methods return a [Scala Future](http://docs.scala-lang.org/overviews/core/futures.html).

This client uses the [HTTP Async Client](https://github.com/AsyncHttpClient/async-http-client), have a look there for examples on configuring your own AsyncHttpClientConfig.


## Examples

### Purging

    client.purge(url)
    client.purgeStatus(purgeId)


### Deploying

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
