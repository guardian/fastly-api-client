import sbtversionpolicy.withsbtrelease.ReleaseVersion

name := "fastly-api-client"

organization := "com.gu"

scalaVersion := "3.3.4"

crossScalaVersions := Seq(scalaVersion.value, "2.12.20", "2.13.15")

libraryDependencies ++= Seq(
    "org.asynchttpclient" % "async-http-client" % "3.0.2",
    "org.scala-lang.modules" %% "scala-collection-compat" % "2.12.0",
    "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    "com.typesafe" % "config" % "1.4.3" % Test
)

scalacOptions ++= Seq("-deprecation", "-feature", "-language:postfixOps", "-release:11") ++
  (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, _)) => Seq("-Xsource:3") // flags only needed in Scala 2
    case _ => Seq.empty
  })

enablePlugins(BuildInfoPlugin)
buildInfoKeys := Seq[BuildInfoKey](name, version)
buildInfoPackage := "com.gu.fastly.api"

import ReleaseTransformations._

licenses := Seq(License.Apache2)
releaseVersion := ReleaseVersion.fromAssessedCompatibilityWithLatestRelease().value
releaseCrossBuild := true

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  setNextVersion,
  commitNextVersion,
)
