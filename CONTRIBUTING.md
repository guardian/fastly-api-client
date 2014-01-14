## How to contribute and perform a release

* Look at the [master branch](https://github.com/guardian/fastly-api-client/tree/master) to find the current release number
* Create a branch for the next version number (if it doesn’t exist already)
* Switch to your own feature branch based off the latest release, do not develop against master
* Test what you can
* Don't forget to increment the release number in [build.sbt](https://github.com/guardian/fastly-api-client/blob/master/build.sbt)
* Think about using a snapshot to test your changes
* Test the new release locally with,

    sbt clean
    sbt publishLocal

* Raise a pull request and get it reviewed, do not merge your own pull request
* Publish to [maven](http://search.maven.org/#browse|948553587) with the following (you will need a key and the password),

    sbt publishSigned

* Log into [sonatype](https://oss.sonatype.org/index.html)
* [Close and then release](https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide#SonatypeOSSMavenRepositoryUsageGuide-8a.ReleaseIt) the new version
* Wait for up to three hours for it to appear
* Test the release using a real project which has the library as a dependency
* Merge the release branch into master
* Create the next release branch
* Create a [release on github](https://github.com/guardian/fastly-api-client/releases)

## Further notes on releasing to maven
https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide

