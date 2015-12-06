import sbt.Keys._

lazy val root =
  (project in file("."))
    .enablePlugins(PlayScala)
    .settings(
      name := Settings.name,
      version := Settings.versions.APPLICATION,
      scalaVersion := Settings.versions.SCALA,
      scalacOptions ++= Settings.scalacOptions,
      libraryDependencies ++= Settings.dependencies.library,
      resolvers ++= Settings.resolvers,
      organization := Settings.organization,
      routesGenerator := InjectedRoutesGenerator
    )