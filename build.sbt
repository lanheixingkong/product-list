name := """product-list"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "net.sf.barcode4j" % "barcode4j" % "2.0",
  "mysql" % "mysql-connector-java" % "5.1.25",
  "com.typesafe.play" %% "anorm" % "2.4.0",
  "org.squeryl" % "squeryl_2.9.1" % "0.9.5-7",
"org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0-RC1" % Test

)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
