import ReleaseTransformations._

organization := "com.thesamet"

name := "sbt-protoc"

scalacOptions := Seq("-deprecation", "-unchecked", "-Xlint", "-Yno-adapted-args")

scalacOptions += "-target:jvm-1.6"

libraryDependencies ++= Seq(
  "com.github.os72" % "protoc-jar" % "3.1.0.1",
  "com.trueaccord.scalapb" %% "protoc-bridge" % "0.2.5-JAVA6"
)

resolvers ++= Seq(
  "cm" at "http://maven.codemettle.com/repository/internal",
  "cm/snaps" at "http://maven.codemettle.com/repository/snapshots"
)

publishMavenStyle := true

credentials += {
  def file = "credentials-" + (if (isSnapshot.value) "snapshots" else "internal")

  Credentials(Path.userHome / ".m2" / file)
}

publishTo := {
  def path = "/repository/" + (if (isSnapshot.value) "snapshots" else "internal")

  Some("CodeMettle Maven" at s"http://maven.codemettle.com$path")
}

sbtPlugin := true

ScriptedPlugin.scriptedSettings

scriptedBufferLog := false

scriptedLaunchOpts <+= version( x => s"-Dplugin.version=${x}" )

// Release
releasePublishArtifactsAction := PgpKeys.publishSigned.value

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(action = Command.process("publishSigned", _), enableCrossBuild = true),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _), enableCrossBuild = true),
  pushChanges
)

