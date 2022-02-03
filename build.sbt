name := "AnimeTool"

version := "0.1"

scalaVersion := "2.13.6"

libraryDependencies += "io.github.vincenzopalazzo" % "material-ui-swing" % "1.1.2"

libraryDependencies += "com.github.jiconfont" % "jiconfont-font_awesome" % "4.7.0.1"

//import NativePackagerHelper._

enablePlugins(JavaAppPackaging)
enablePlugins(UniversalPlugin)
enablePlugins(JDKPackagerPlugin)
//enablePlugins(LauncherJarPlugin)

mainClass in Compile := Some("Main")

(antPackagerTasks in JDKPackager) := Some(file("C:\\Program Files\\Java\\jdk1.8.0_321\\lib\\ant-javafx.jar"))

//mappings in Universal := {
//  // universalMappings: Seq[(File,String)]
//  val universalMappings = (mappings in Universal).value
//  val fatJar = (assembly in Compile).value
//
//  // removing means filtering
//  // notice the "!" - it means NOT, so only keep those that do NOT have a name ending with "jar"
//  val filtered = universalMappings filter {
//    case (file, name) =>  ! name.endsWith(".jar")
//  }
//
//  // add the fat jar to our sequence of things that we've filtered
//  filtered :+ (fatJar -> ("lib/" + fatJar.getName))
//}