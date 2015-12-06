import sbt.Keys._
import sbt.Project.projectToRef

version := "1.0"

lazy val root =
  (project in file("."))
    .enablePlugins(PlayScala)
    .settings(
      name := Settings.name,
      scalaVersion := Settings.versions.scala,
      scalacOptions ++= Settings.scalacOptions,
      libraryDependencies ++= Settings.dependencies.library,
      resolvers ++= Settings.resolvers,
      organization := Settings.organization
    )

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator