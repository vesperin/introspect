import sbt._
import Keys._
import sbtassembly.AssemblyPlugin.autoImport._


// Project name (artifact name in Maven)
name := "cue"

// organization name (e.g., the package name of the project)
organization := "com.vesperin"

version := "0.1"

// project description
description := "Concept assignment tool (Experimental)"

// Enables publishing to maven repo
publishMavenStyle := true

// Do not append Scala versions to the generated artifacts
crossPaths := false

scalaVersion := "2.11.7"

// This forbids including Scala related libraries into the dependency
autoScalaLibrary := false

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case PathList("about_files", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}


assemblyJarName in assembly:= "cue-0.1-SNAPSHOT.jar"

test in assembly := {}


// library dependencies. (organization name) % (project name) % (version)
libraryDependencies ++= Seq(
   "org.eclipse.jdt" % "org.eclipse.jdt.core" % "3.10.0",
    "com.google.guava" % "guava" % "19.0",
    "com.novocode" % "junit-interface" % "0.11" % "test",
    "junit" % "junit" % "4.12",
    "com.github.rvesse" % "airline" % "2.1.0",
    "com.google.code.gson" % "gson" % "2.7",
    "gov.nist.math" % "jama" % "1.0.3"
)

assemblyExcludedJars in assembly <<= (fullClasspath in assembly) map { cp =>
  val excludes = Set(
    "junit-4.12.jar",
    "hamcrest-core-1.3.jar",
    "junit-interface-0.11.jar",
    "guava-19.0.jar",
    "org.eclipse.core.commands-3.6.0.jar",
    "org.eclipse.core.contenttype-3.4.100.jar",
    "org.eclipse.core.expressions-3.4.300.jar",
    "org.eclipse.core.filesystem-1.3.100.jar",
    "org.eclipse.core.jobs-3.5.100.jar",
    "org.eclipse.core.resources-3.7.100.jar",
    "org.eclipse.equinox.app-1.3.100.jar",
    "org.eclipse.equinox.common-3.6.0.jar",
    "org.eclipse.equinox.preferences-3.4.1.jar",
    "org.eclipse.equinox.registry-3.5.101.jar",
    "org.eclipse.jdt.core-3.10.0.jar",
    "org.eclipse.osgi-3.7.1.jar",
    "org.eclipse.text-3.5.101.jar",
    "airline-io-2.1.0.jar",
    "airline-2.1.0.jar",
    "jama-1.0.3.jar"
  )

  cp filter { jar => excludes(jar.data.getName) }
}
