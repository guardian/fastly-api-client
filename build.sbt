name := "fastlyapiclient"

organization := "guardian"

version := "0.2.2"

scalaVersion := "2.9.1"

crossScalaVersions := Seq("2.9.1", "2.9.2", "2.10.0")

libraryDependencies ++= Seq(
    "com.ning" % "async-http-client" % "1.7.11"//,
    //"org.scalatest" %% "scalatest" % "1.7.1" % "test",
    //"net.liftweb" %% "lift-json" % "2.4" % "test",
    //"joda-time" % "joda-time" % "2.0" % "test",
    //"org.joda" % "joda-convert" % "1.2" % "test"
    )

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))
