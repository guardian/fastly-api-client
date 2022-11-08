
name := "fastly-api-client"

organization := "com.gu"

scalaVersion := "2.13.10"

crossScalaVersions := Seq(scalaVersion.value, "2.12.17")

libraryDependencies ++= Seq(
    "org.asynchttpclient" % "async-http-client" % "2.12.3",
    "joda-time" % "joda-time" % "2.12.1",
    "org.joda" % "joda-convert" % "2.2.2",
    "org.scalatest" %% "scalatest" % "3.2.14" % Test,
    "com.typesafe" % "config" % "1.4.2" % Test
)

ThisBuild / scalacOptions ++= Seq("-deprecation", "-feature", "-language:postfixOps")

enablePlugins(BuildInfoPlugin)
buildInfoKeys := Seq[BuildInfoKey](name, version)
buildInfoPackage := "com.gu.fastly.api"

publishMavenStyle := true

publishTo := sonatypePublishToBundle.value

Test / publishArtifact := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/guardian/fastly-api-client</url>
  <licenses>
    <license>
      <name>Apache License, Version 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:guardian/fastly-api-client.git</url>
    <connection>scm:git:git@github.com:guardian/fastly-api-client.git</connection>
  </scm>
  <developers>
    <developer>
      <id>obrienm</id>
      <name>Matthew O'Brien</name>
      <url>http://www.theguardian.com</url>
    </developer>
  </developers>
)


import ReleaseTransformations._

releaseCrossBuild := true

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  // For non cross-build projects, use releaseStepCommand("publishSigned")
  releaseStepCommandAndRemaining("+publishSigned"),
  releaseStepCommand("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)