// give the user a nice default project!
lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "$organization$",
      scalaVersion := "2.11.8"
    )),
    name := "$name$",
    version := "0.0.1",
    sparkVersion := "$sparkVersion$",
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    sparkComponents := Seq("core", "sql", "catalyst", "mllib", "hive", "streaming-kinesis-asl"),
    parallelExecution in Test := false,
    fork := true,
    coverageHighlighting := true,
    javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled"),
    libraryDependencies ++= Seq(
      // Test your code PLEASE!!!
      "org.scalatest" %% "scalatest" % "3.0.1" % "test",
      "org.scalacheck" %% "scalacheck" % "1.13.4" % "test",
      "org.apache.hadoop" % "hadoop-aws"    % "2.7.3",
      "com.holdenkarau" %% "spark-testing-base" % "$sparkVersion$_$sparkTestingbaseRelease$" % "test").map(_.
excludeAll(
    ExclusionRule(organization = "commons-code"),
    ExclusionRule(organization = "joda-time"),
    ExclusionRule(organization = "commons-beanutils"),
    ExclusionRule(organization = "mime-types")
)),
    scalacOptions ++= Seq("-deprecation", "-unchecked"),
    pomIncludeRepository := { x => false },
    resolvers ++= Seq(
      "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/",
      "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
      "Second Typesafe repo" at "http://repo.typesafe.com/typesafe/maven-releases/",
      Resolver.sonatypeRepo("public")
    ),
    // publish settings
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    }
  )

lazy val intellijRunner = project.in(file("intellijRunner")).dependsOn(RootProject(file("."))).settings(
  spIgnoreProvided := false
).disablePlugins(sbtassembly.AssemblyPlugin)
