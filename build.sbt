import sbtversionpolicy.withsbtrelease.ReleaseVersion.fromAggregatedAssessedCompatibilityWithLatestRelease

name := "fastly-api-client"

organization := "com.gu"

scalaVersion := "3.3.3"

crossScalaVersions := Seq(scalaVersion.value, "2.12.19", "2.13.14")

libraryDependencies ++= Seq(
    "org.asynchttpclient" % "async-http-client" % "2.12.3" exclude("io.netty", "netty-codec"),
    "io.netty" % "netty-codec" % "4.1.112.Final",
    "joda-time" % "joda-time" % "2.12.7",
    "org.joda" % "joda-convert" % "2.2.3",
    "org.scala-lang.modules" %% "scala-collection-compat" % "2.12.0",
    "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    "com.typesafe" % "config" % "1.4.3" % Test
)

val sonatypeReleaseSettings = Seq(
  licenses := Seq("Apache V2" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")),
  releaseVersion := fromAggregatedAssessedCompatibilityWithLatestRelease().value,
  releaseCrossBuild := true,
)

ThisBuild / scalacOptions ++= Seq("-deprecation", "-feature", "-language:postfixOps", "-release:11") ++
  (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, _)) => Seq("-Xsource:3") // flags only needed in Scala 2
    case _ => Seq.empty
  })

enablePlugins(BuildInfoPlugin)
buildInfoKeys := Seq[BuildInfoKey](name, version)
buildInfoPackage := "com.gu.fastly.api"

import ReleaseTransformations._

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  setNextVersion,
  commitNextVersion,
)
