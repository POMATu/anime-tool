import java.io.{ByteArrayOutputStream, PrintWriter}
import java.util.spi.ToolProvider


name := "AnimeTool"

version := "2.0"

scalaVersion := "2.13.6"

libraryDependencies += "io.github.vincenzopalazzo" % "material-ui-swing" % "1.1.2"

libraryDependencies += "com.github.jiconfont" % "jiconfont-font_awesome" % "4.7.0.1"

libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.0"

libraryDependencies += "commons-io" % "commons-io" % "2.11.0"

//import NativePackagerHelper._

maintainer := "AnimeTool"
packageSummary := "AnimeTool"
packageDescription := "AnimeTool - an mpv frontend for watching chunked titles from different sources"

    /*
lazy val iconGlob = sys.props("os.name").toLowerCase match {
  case os if os.contains("mac") => "*.icns"
  case os if os.contains("win") => "*.ico"
  case _ => "*.png"
}

jdkAppIcon :=  (sourceDirectory.value ** iconGlob).getPaths.headOption.map(file)
*/
/*
jdkPackagerProperties := Map(
  "app.name" -> name.value,
  "app.version" -> version.value
)

jdkPackagerAppArgs := Seq(maintainer.value, packageSummary.value, packageDescription.value)
*/

//println(jdkAppIcon)

jdkPackagerType := "installer"

//javaFxVerbose := true

enablePlugins(JavaAppPackaging)
//enablePlugins(UniversalPlugin)
//enablePlugins(JDKPackagerPlugin)
//enablePlugins(JavaAppPackaging)
enablePlugins(JlinkPlugin)
//enablePlugins(LauncherJarPlugin)

mainClass in Compile := Some("Main")

//this allows us to run tools like jdeps and jlink from within the JVM
def runTool(name: String, arguments: Seq[String]): Either[String,String] = {
  val maybeTool: Option[ToolProvider] = {
    val _tool = ToolProvider.findFirst(name)
    if(_tool.isPresent) {
      Some(_tool.get())
    } else {
      None
    }
  }

  val result = for(tool <- maybeTool) yield {
    val stdOut = new ByteArrayOutputStream()
    val errOut = new ByteArrayOutputStream()
    tool.run(new PrintWriter(stdOut), new PrintWriter(errOut), arguments: _*)
    (new String(stdOut.toByteArray), new String(errOut.toByteArray))
  }

  result
    .toRight(s"Could not find tool $name in your java development environment")
    .flatMap{ case (ret,err) =>
      if(ret.contains("Error:") || err.nonEmpty) {
        Left(ret + err)
      } else {
        Right(ret -> "")
      }
    }
    .map(_._1)
}

//get the jvm module dependencies (such as java.base) from our application and imported libraries
val moduleDependencies = taskKey[Array[String]]("outputs the jdk module dependency information of our classpath")

moduleDependencies := {
  val logger = streams.value
  logger.log.info("getting module dependencies from jdeps...")

  val classPathValue = (dependencyClasspath in Runtime)
    .value
    .map(_.data.getAbsolutePath)

  val command = Seq("-recursive", "--list-deps") ++ classPathValue

  logger.log.info(s"jdeps ${command.mkString(" ")}")

  runTool("jdeps", command)
    .map(_
      .split('\n')
      .filter(!_.isEmpty)
      .map(_
        .filter(!_.isWhitespace)
        .split('/')
        .head
      )
      .distinct
      .filter(_ != "JDKremovedinternalAPI")
    )
    .fold(sys.error, mods => {
      logger.log.info("done generating module dependencies...")
      mods
    })
}

//this generates the minimized JRE for our application courtesy of jlink
val jlink = taskKey[File]("generates a java runtime for the project")

jlink := {
  val logger = streams.value

  logger.log.info("generating runtime with jlink...")

  val outputFile = target.value / s"${name.value}-runtime"

  if(outputFile.exists()) {
    logger.log.info("deleting already generated runtime")
    IO.delete(outputFile)
  }

  val modulesToAdd = Seq("--add-modules", moduleDependencies.value.mkString(","))

  val outputArgument = Seq("--output", outputFile.absolutePath)

  val command = Seq("--no-header-files","--no-man-pages","--compress=2","--strip-debug") ++ modulesToAdd ++ outputArgument

  logger.log.debug(s"command: jlink ${command.mkString(" ")}")

  runTool("jlink", command).map(_ => outputFile).fold(sys.error, identity)
}

//this tells sbt-native-packager to bundle our minimized JRE with our application
mappings in Universal ++= {
  val dir = jlink.value
  (dir.**(AllPassFilter) --- dir).pair(file => IO.relativize(dir.getParentFile, file))
}

//sbt-native-packager launch script modifications necessary for using the internal JRE to run the application
bashScriptExtraDefines ++= Seq(s"JAVA_HOME=$$app_home/../${jlink.value.getName}")


// change java path to yours for exe installer
//(antPackagerTasks in JDKPackager) := Some(file("C:\\Program Files\\Java\\jdk1.8.0_321\\lib\\ant-javafx.jar"))
    /*
Compile / packageBin / mappings += {
  (baseDirectory.value / "package") -> "package"
}
*/


// Build with
// sbt ;clean;jdkPackager:packageBin
// sbt ;clean;universal:packageBin