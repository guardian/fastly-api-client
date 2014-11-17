import SonatypeKeys._

name := "fastly-api-client"

organization := "com.gu"

scalaVersion := "2.11.4"

crossScalaVersions := Seq("2.10.4", "2.11.4")

libraryDependencies ++= Seq(
    "com.ning" % "async-http-client" % "1.8.14",
    "joda-time" % "joda-time" % "2.5",
    "org.joda" % "joda-convert" % "1.7",
    "org.scalatest" %% "scalatest" % "2.2.2" % "test",
    "com.typesafe" % "config" % "1.2.1" % "test"
)

scalacOptions in ThisBuild ++= Seq("-deprecation", "-feature", "-language:postfixOps")

publishMavenStyle := true

publishTo in ThisBuild <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at s"${nexus}content/repositories/snapshots")
  else
    Some("releases"  at s"${nexus}service/local/staging/deploy/maven2")
}

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

sonatypeSettings

releaseSettings

ReleaseKeys.crossBuild := true

ReleaseKeys.publishArtifactsAction := PgpKeys.publishSigned.value
