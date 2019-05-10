
name := "fastly-api-client"

organization := "com.gu"

scalaVersion := "2.12.2"

crossScalaVersions := Seq(scalaVersion.value, "2.11.9")

libraryDependencies ++= Seq(
    "com.ning" % "async-http-client" % "1.9.40",
    "joda-time" % "joda-time" % "2.5",
    "org.joda" % "joda-convert" % "1.7",
    "org.scalatest" %% "scalatest" % "3.0.3" % "test",
    "com.typesafe" % "config" % "1.2.1" % "test"
)

scalacOptions in ThisBuild ++= Seq("-deprecation", "-feature", "-language:postfixOps")

enablePlugins(BuildInfoPlugin)
buildInfoKeys := Seq[BuildInfoKey](name, version)
buildInfoPackage := "com.gu.fastly.api"

publishMavenStyle := true

publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)

publishArtifact in Test := false

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
  ReleaseStep(action = Command.process("publishSigned", _), enableCrossBuild = true),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _), enableCrossBuild = true),
  pushChanges
)