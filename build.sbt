scalaVersion in ThisBuild := "2.12.1"
crossScalaVersions in ThisBuild := Seq("2.11.8", "2.12.1")

import ReleaseTransformations._

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(action = Command.process("publishSigned", _)),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
  pushChanges
)

val oauth2 = project.in(file("."))
    .settings(
        name := "OAuth2 for Akka",
        normalizedName := "akka-oauth2",
        libraryDependencies ++= Seq(
            "com.typesafe.akka" %% "akka-actor" % "2.4.12",
            "com.typesafe.akka" %% "akka-http" % "10.0.1",
            "eu.tilk" %% "scala-jwt" % "0.0.1-SNAPSHOT"
        ),
        organization := "eu.tilk",
        version := "0.0.1-SNAPSHOT",
        licenses += ("LGPL 3.0", url("https://opensource.org/licenses/LGPL-3.0")),
        scmInfo := Some(ScmInfo(
            url("https://github.com/tilk/akka-oauth2"),
            "scm:git:git@github.com:tilk/akka-oauth2.git",
            Some("scm:git:git@github.com:tilk/akka-oauth2.git"))),
        publishTo := {
          val nexus = "https://oss.sonatype.org/"
          if (isSnapshot.value)
            Some("snapshots" at nexus + "content/repositories/snapshots")
          else
            Some("releases" at nexus + "service/local/staging/deploy/maven2")
        },
        publishMavenStyle := true,
        pomExtra := (
          <developers>
            <developer>
              <id>tilk</id>
              <name>Marek Materzok</name>
              <url>https://github.com/tilk/</url>
            </developer>
          </developers>
        )
    )

