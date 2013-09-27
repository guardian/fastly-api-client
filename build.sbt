name := "fastly-api-client"

organization := "com.gu"

version := "0.1.2-SNAPSHOT"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
    "com.ning" % "async-http-client" % "1.7.20",
    "joda-time" % "joda-time" % "2.0",
    "org.joda" % "joda-convert" % "1.2",
    "org.scalatest" %% "scalatest" % "1.9.1" % "test"
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

