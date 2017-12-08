mainClass in (Compile, run) := Some("$organization$.$name$.KinesisExample")

test in assembly := {}

version := "$version$"

sparkVersion := "$sparkVersion$"


assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case x => MergeStrategy.first
}

lazy val testDependencies = Seq(
      // Test your code PLEASE!!!
      "org.scalatest"     %% "scalatest"             % "3.0.1"       % "test",
      "org.scalacheck"    %% "scalacheck"            % "1.13.4"      % "test",
      "com.holdenkarau"   %% "spark-testing-base"    % "2.2.0_0.7.2" % "test").map(_.
excludeAll(
    ExclusionRule(organization = "commons-code"),
    ExclusionRule(organization = "joda-time"),
    ExclusionRule(organization = "commons-beanutils"),
    ExclusionRule(organization = "mime-types")
))

lazy val otherDependencies = Seq(
  "com.amazonaws"     %  "aws-java-sdk"         % "1.7.4" ,
  "com.typesafe"      %  "config"               % "1.3.1" ,
  "org.apache.hadoop" %  "hadoop-aws"           % "2.7.3" ,
  "mysql"             %  "mysql-connector-java" % "5.1.12",
  "org.scalaj"        %% "scalaj-http"          % "2.3.0" ,
  "org.jfarcand"      %  "wcs"                  % "1.5"
)
lazy val sparkComponentReqs =  Seq("core", "sql", "catalyst", "mllib", "hive", "streaming-kinesis-asl")
lazy val commonSettings = Seq(
      mainClass in (Compile, run) := Some("com.example.cmnkine.KinesisExample"),
      organization := "$organization$",
      scalaVersion := "2.11.8",

    javacOptions    ++= Seq("-source", "1.8", "-target", "1.8"),

    spIgnoreProvided := false,
    parallelExecution in Test := false,
    fork := true,
    coverageHighlighting := true,
    javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled"),

    scalacOptions ++= Seq("-deprecation", "-unchecked"),
    pomIncludeRepository := { x => false },
    resolvers ++= Seq(
      "sonatype-releases"    at "https://oss.sonatype.org/content/repositories/releases/",
      "Typesafe repository"  at "http://repo.typesafe.com/typesafe/releases/",
      "Second Typesafe repo" at "http://repo.typesafe.com/typesafe/maven-releases/",
      Resolver.sonatypeRepo("public")
    )
)
// give the user a nice default project!
lazy val root = (project in file(".")).
  settings(
    name := "$name$",
    commonSettings,
    sparkComponents := sparkComponentReqs,
       libraryDependencies ++= otherDependencies ++ testDependencies,
    // publish settings
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    })
// https://github.com/JetBrains/intellij-scala/wiki/%5BSBT%5D-How-to-use-provided-libraries-in-run-configurations
lazy val intellijRunner = project.in(file(".")).settings(
  spIgnoreProvided := true,
  name := "intellijRunner_$name$",
 commonSettings,
 libraryDependencies ++=  (sparkComponentReqs.map { component =>
            "org.apache.spark" %% s"spark-$component" % sparkVersion.value
}.toSeq)
)
  //.disablePlugins(sbtassembly.AssemblyPlugin)
