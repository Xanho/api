import play.sbt.PlayImport._
import sbt._

/**
  * Container for SBT or organizational configuration settings
  */
object Settings {

  /**
    * Application Name
    */
  val name =
    "xanho-api"

  /**
    * Scala compiler options
    */
  val scalacOptions =
    Seq(
      "-Xlint",
      "-unchecked",
      "-deprecation",
      "-feature"
    )

  /**
    * Package prefix/organization
    */
  val organization =
    "org.xanho"

  /**
    * Custom resolvers
    */
  val resolvers =
    Seq("scalaz-bintray" at "http://dl.bintray.com/scalaz/releases")

  /**
    * Versions
    */
  object versions {

    val APPLICATION =
      "0.0.1"

    val SCALA =
      "2.11.7"

    /**
      * Library dependency versions
      */
    object dependencies {

      val PLAY_SLICK =
        "1.1.1"

      val JWT_PLAY =
        "0.4.1"

      val MYSQL_CONNECTOR_JAVA =
        "5.1.36"

      val SCALA_BCRYPT =
        "2.4"

    }

  }

  /**
    * Project Dependencies
    */
  object dependencies {

    val library =
      Seq(
        cache,
        ws,
        specs2 % Test,
        "com.github.t3hnar" % "scala-bcrypt_2.10" % versions.dependencies.SCALA_BCRYPT,
        "mysql" % "mysql-connector-java" % versions.dependencies.MYSQL_CONNECTOR_JAVA,
        "com.pauldijou" %% "jwt-play" % versions.dependencies.JWT_PLAY,
        "com.typesafe.play" %% "play-slick" % versions.dependencies.PLAY_SLICK,
        "com.typesafe.play" %% "play-slick-evolutions" % versions.dependencies.PLAY_SLICK
      )

  }

}