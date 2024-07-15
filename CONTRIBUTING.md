## How to contribute and publish to maven


### Contributing
* Fork the repository
* Create your own feature branch based off main
* Raise a pull request
* You can test your changes locally by using the following command

```
    sbt publishLocal
```

### Publishing to maven

[Guardian doc on publishing to maven and sonatype](https://docs.google.com/document/d/1rNXjoZDqZMsQblOVXPAIIOMWuwUKe3KzTCttuqS7AcY/edit#)

Merge the pull request before publishing to maven and then publish from the main branch.

You can find useful notes on releasing to maven [here](http://central.sonatype.org/pages/ossrh-guide.html)

You can find useful notes on configuring sbt to publish to maven [here](http://www.scala-sbt.org/release/docs/Community/Using-Sonatype.html)


* Execute the release command in sbt this will automate updating the version number and publish to maven central
```
   sbt release
```

* Wait for up to two hours for it to appear
* Test the release using a real project which has the library as a dependency (you can do this using **sbt publishLocal**)
* Create a [release on github](https://github.com/guardian/fastly-api-client/releases) to document the changes within the release
