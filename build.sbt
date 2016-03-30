name := """4df-play-java-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

resolvers ++= Seq(
  Resolver.sonatypeRepo("public"),
  Resolver.bintrayRepo("scalaz", "releases")
)

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "com.fdflib" % "4dflib" % "1.0.1-SNAPSHOT"
)