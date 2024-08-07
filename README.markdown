Fastly API client
=================

An asynchronous Scala client for [Fastly's API](http://docs.fastly.com/api) used to deploy and update configs, decache objects and query the stats API.

[![fastly-api-client Scala version support](https://index.scala-lang.org/guardian/fastly-api-client/fastly-api-client/latest-by-scala-version.svg?platform=jvm)](https://index.scala-lang.org/guardian/fastly-api-client/fastly-api-client)

[![Release](https://github.com/guardian/fastly-api-client/actions/workflows/release.yml/badge.svg)](https://github.com/guardian/fastly-api-client/actions/workflows/release.yml)

## Installation

### SBT

    libraryDependencies += "com.gu" %% "fastly-api-client" % "latest version"

## Configuring the client

Use the default AsyncHttpClientConfig,

    val client = FastlyApiClient("my-fastly-api-key", "my-service-id")

Or define your own AsyncHttpClientConfig,

    val client = FastlyApiClient("my-fastly-api-key",
                    "my-service-id",
                    config = Some(asyncHttpClientConfig))

This client uses the [HTTP Async Client](https://github.com/AsyncHttpClient/async-http-client), have a look there for configuring your own AsyncHttpClientConfig. This is what [Dispatch Reboot](https://github.com/dispatch/reboot) uses under the hood, too.

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

This is the way Fastly recommend performing releases.

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

# Running the tests
You'll need a config file at ~/.config/fastly/fastlyApiClientTest with these values:

    serviceId=<ID of any fastly service>
    apiKey=<api key with read-only access to above service>

You may wish to create a service just for this purpose.


# Publishing a new release

This repo uses [`gha-scala-library-release-workflow`](https://github.com/guardian/gha-scala-library-release-workflow)
to automate publishing releases (both full & preview releases) - see
[**Making a Release**](https://github.com/guardian/gha-scala-library-release-workflow/blob/main/docs/making-a-release.md).
