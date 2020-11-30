import scoverage.{Coverage, ScoverageSbtPlugin}

name := """akka-chat"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.2"

val akkaVersion = "2.3.4"

libraryDependencies ++= Seq(
  "org.webjars" % "bootstrap" % "2.3.1",
  "org.seleniumhq.selenium" % "selenium-java" % "2.44.0" % "test",
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
)

ScoverageSbtPlugin.autoImport.coverageMinimumCoverage := 100
