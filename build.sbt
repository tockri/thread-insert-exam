import sbt.Keys.javacOptions

name := """thread-insert-exam"""
organization := "nulab"

version := "1.0-SNAPSHOT"
val scalikejdbcVersion = "3.0.2"

lazy val root = (project in file(".")).enablePlugins(PlayScala).
  settings(
    libraryDependencies ++= Seq(
      jdbc,
      evolutions,
      "mysql" % "mysql-connector-java" % "5.1.24",
      "org.scalikejdbc" %% "scalikejdbc" % scalikejdbcVersion,
      "org.scalikejdbc" %% "scalikejdbc-config" % scalikejdbcVersion,
      "org.scalikejdbc" %% "scalikejdbc-play-dbapi-adapter" % "2.6.0-scalikejdbc-3.0",
      "org.scalatest" %% "scalatest" % "3.0.1" % "test",
      "ch.qos.logback" % "logback-classic" % "1.2.3" % "test"
    ),
    javacOptions ++= Seq("-encoding", "UTF-8")
  )

scalaVersion := "2.12.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "nulab.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "nulab.binders._"
