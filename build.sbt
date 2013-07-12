name := "fastlyapiclient"

organization := "com.gu"

version := "0.3.0"

scalaVersion := "2.10.2"

crossScalaVersions := Seq("2.9.1", "2.9.2", "2.10.0")

libraryDependencies ++= Seq(
    "com.ning" % "async-http-client" % "1.7.11",
    "joda-time" % "joda-time" % "2.0",
    "org.joda" % "joda-convert" % "1.2",
    "org.scalatest" %% "scalatest" % "1.9.1" % "test"
)

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))
