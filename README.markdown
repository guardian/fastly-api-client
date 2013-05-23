An asynchronous Scala client for Fastly's [API](http://www.fastly.com/docs/api)
=============================================================================

Dependencies
------------

    resolvers += "Guardian Github Releases" at "http://moschops.github.com/mvn/releases"

    libraryDependencies += "com.gu" %% "fastlyapiclient" % "0.2.2"


Configuring the client
----------------------

You can either create a new instance yourself,

    val fastlyApiClient = FastlyAPIClient("my-fastly-api-key", "my-service-id", Some(asyncHttpClientConfig))

or use the FastlyCredentials trait. If you use this trait, you'll need to create a file in either (in order of lookup)

    ~/.fastlyapiclientcconfig
    /etc/fastly/fastlyapiclientcconfig

with the following values:

    apiKey=[your API key]
    serviceId=[your service id]

If you wish to override the default [AsyncHttpClientConfig](http://asynchttpclient.github.io/async-http-client/apidocs/com/ning/http/client/AsyncHttpClientConfig.Builder.html) and/or the location of the credentials file,

    fastlyCredentialLocations = Seq(new File("some-other-credentials-file"))
    asyncHttpClientConfig = new AsyncHttpClientConfig.Builder().set....build()

Set these variables *before* you call any methods if you wish to override the defaults.

Examples
--------
All methods return a ListenableFuture[Response] call *future.get* if you want to be synchronous and wait for the response.
Or, to be asynchronous, pass an optional AsyncHandler to any method, e.g.

    def purge(url: String, ..., handler: Option[AsyncHandler[Response]] = None): ListenableFuture[Response] = {...

To clone a previous version

    fastlyApiClient.versionClone(versionToClone)
    // you can get the newly cloned version number from the response body, or...
    fastlyApiClient.latestVersionNumber

To overwrite a VCL file

    fastlyApiClient.vclUpdate(Map(name -> vcl), version)

To deploy/activate a version

    fastlyApiClient.versionActivate(version)

To purge

    fastlyApiClient.purge(someUrl)

To retrieve datacenter stats

    fastlyApiClient.datacenterStats(startDatetime, endDatetime)
