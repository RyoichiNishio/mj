
name := "mahjong"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
	"io.spray" % "spray-json_2.11" % "1.3.2",
	"com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
	"ch.qos.logback" % "logback-classic" % "1.1.3",
	"org.scala-lang.modules" %% "scala-xml" % "1.0.5"
)

// META-INF discarding
assemblyMergeStrategy in assembly := {
	case PathList("javax", "servlet", xs@_*) => MergeStrategy.first
	case PathList(ps@_*) if ps.last endsWith ".properties" => MergeStrategy.first
	case PathList(ps@_*) if ps.last endsWith ".xml" => MergeStrategy.first
	case PathList(ps@_*) if ps.last endsWith ".types" => MergeStrategy.first
	case PathList(ps@_*) if ps.last endsWith ".class" => MergeStrategy.first
	case "application.conf" => MergeStrategy.concat
	case "unwanted.txt" => MergeStrategy.discard
	case x =>
		val oldStrategy = (assemblyMergeStrategy in assembly).value
		oldStrategy(x)
}


assemblyJarName := s"${name.value}-${version.value}.jar"

// Add provided libraries to classpath when `sbt run` and `sbt run-main` are executed
run in Compile <<= Defaults.runTask(fullClasspath in Compile, mainClass in(Compile, run), runner in(Compile, run))
runMain in Compile <<= Defaults.runMainTask(fullClasspath in Compile, runner in(Compile, run))