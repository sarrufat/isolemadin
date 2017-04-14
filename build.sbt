name := "isolemadin"

version := "0.1.0"

organization := "arrufat.org"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("–unchecked", "-deprecation")


resolvers += Resolver.sonatypeRepo("releases")

resolvers += "Scaladin Snapshots" at "http://henrikerola.github.io/repository/snapshots/"


libraryDependencies ++= {
  	Seq(
  	    "org.scalatest" % "scalatest_2.11" % "3.0.1" % "test",
  	    "org.vaadin.addons" %% "scaladin" % "3.2-SNAPSHOT",
  		"com.vaadin" % "vaadin-server" % "7.5.10",
  		"com.vaadin" % "vaadin-client-compiled" % "7.5.10",
  		"com.vaadin" % "vaadin-themes" % "7.5.10"
  	)
}





