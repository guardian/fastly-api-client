## How to contribute and publish to maven


### Contributing
* Fork the repository
* Create your own feature branch based off master
* Raise a pull request
* You can test your changes locally by using the following command

```
    sbt publishLocal
```

### Publishing to maven

You can find useful notes on releasing to maven [here](http://central.sonatype.org/pages/ossrh-guide.html)

You can find useful notes on configuring sbt to publish to maven [here](http://www.scala-sbt.org/release/docs/Community/Using-Sonatype.html)


* This is how you publish to [maven](http://search.maven.org/#browse|948553587) - you will need a key and a password,

```
    sbt publishSigned
    sbt sonatypeRelease
```

* Wait for up to two hours for it to appear
* Test the release using a real project which has the library as a dependency (you can do this using **sbt publishLocal**)
* Create a [release on github](https://github.com/guardian/fastly-api-client/releases) to document the changes within the release
