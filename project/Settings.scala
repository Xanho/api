import play.sbt.PlayImport._
import sbt._

object Settings {

  val name =
    "api"

  val version =
    "0.0.1"

  val scalacOptions =
    Seq(
      "-Xlint",
      "-unchecked",
      "-deprecation",
      "-feature"
    )

  val organization =
    "org.xanho"

  val resolvers =
    Seq("scalaz-bintray" at "http://dl.bintray.com/scalaz/releases")

  object versions {

    val scala =
      "2.11.7"

  }

  object dependencies {

    val library =
      Seq(
        jdbc,
        cache,
        ws,
        specs2 % Test
      )

  }
}