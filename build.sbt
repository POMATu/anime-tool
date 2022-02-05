import com.typesafe.sbt.SbtNativePackager._

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

enablePlugins(JavaAppPackaging)
enablePlugins(UniversalPlugin)
enablePlugins(JDKPackagerPlugin)
//enablePlugins(LauncherJarPlugin)

mainClass in Compile := Some("Main")

// change java path to yours for exe installer
(antPackagerTasks in JDKPackager) := Some(file("C:\\Program Files\\Java\\jdk1.8.0_321\\lib\\ant-javafx.jar"))
    /*
Compile / packageBin / mappings += {
  (baseDirectory.value / "package") -> "package"
}
*/


// Build with
// sbt ;clean;jdkPackager:packageBin
// sbt ;clean;universal:packageBin