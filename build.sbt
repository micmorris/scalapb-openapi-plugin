val Scala213 = "2.13.2"

val Scala212 = "2.12.10"

ThisBuild / organization := "com.example"

ThisBuild / scalaVersion := Scala213

lazy val core = (projectMatrix in file("core"))
  .defaultAxes()
  .settings(
    name := "scalapb-openapi-plugin-core"
  )
  .jvmPlatform(scalaVersions = Seq(Scala212, Scala213))

lazy val codeGen = (projectMatrix in file("code-gen"))
  .enablePlugins(BuildInfoPlugin)
  .defaultAxes()
  .settings(
     buildInfoKeys := Seq[BuildInfoKey](name, organization, version, scalaVersion, sbtVersion),
     buildInfoPackage := "scalapb_openapi.compiler",
     libraryDependencies ++= Seq(
       "com.thesamet.scalapb" %% "compilerplugin" % scalapb.compiler.Version.scalapbVersion,
       "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion,
     )
  )
  .jvmPlatform(scalaVersions = Seq(Scala212, Scala213))

lazy val codeGenJVM212 = codeGen.jvm(Scala212)

lazy val protocGenScalapbopenapiplugin = protocGenProject("protoc-gen-scalapb-openapi-plugin", codeGenJVM212)
  .settings(
    Compile / mainClass := Some("scalapb_openapi.compiler.CodeGenerator"),
    scalaVersion := Scala212
  )

lazy val e2e = (projectMatrix in file("e2e"))
  .dependsOn(core)
  .enablePlugins(LocalCodeGenPlugin)
  .defaultAxes()
  .settings(
    skip in publish := true,
    codeGenClasspath := (codeGenJVM212 / Compile / fullClasspath).value,
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.7.9" % Test
    ),
    testFrameworks += new TestFramework("munit.Framework"),
    PB.targets in Compile := Seq(
      scalapb.gen() -> (sourceManaged in Compile).value / "scalapb",
      genModule("scalapb_openapi.compiler.CodeGenerator$") -> (sourceManaged in Compile).value / "scalapb"
    )
  )
  .jvmPlatform(scalaVersions = Seq(Scala212, Scala213))

lazy val root: Project =
  project
    .in(file("."))
    .settings(
      publishArtifact := false,
      publish := {},
      publishLocal := {}
    )
    .aggregate(protocGenScalapbopenapiplugin.agg)
    .aggregate(
      codeGen.projectRefs ++
      core.projectRefs: _*
    )
