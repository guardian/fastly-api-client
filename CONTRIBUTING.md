## How to contribute and perform a release

You can find usful notes on releasing to maven [here](https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide
)

### Creating a branch for the next release (if it doesn't already exist)
* Look at the [master branch](https://github.com/guardian/fastly-api-client/tree/master) to find the current release number
* Create a branch for the next release, e.g. 0.2.2
* Increment the release number in [build.sbt](https://github.com/guardian/fastly-api-client/blob/master/build.sbt)

### Contributing
* Switch to your own feature branch based off the next release (do not branch from master)
* Write tests
* Test the new release locally using

```
    sbt publishLocal
```

* Raise a pull request (to merge into the release branch, e.g 0.2.2)

### Publishing to maven

* Publish to [maven](http://search.maven.org/#browse|948553587) with the following (you will need a key and the password),

```
    sbt publishSigned
```

* Log into [sonatype](https://oss.sonatype.org/index.html)
* [Close and release](https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide#SonatypeOSSMavenRepositoryUsageGuide-8a.ReleaseIt) the new version
* Wait for up to two hours for it to appear
* Test the release using a real project which has the library as a dependency (you can do this using **sbt publishLocal**)
* Merge the release branch into master
* Create a [release on github](https://github.com/guardian/fastly-api-client/releases) to document the changes within the release
* Create the next release branch

