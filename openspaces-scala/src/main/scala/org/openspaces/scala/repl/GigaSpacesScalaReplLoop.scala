package org.openspaces.scala.repl

import scala.tools.nsc.Properties
import scala.tools.nsc.Settings
import scala.tools.nsc.interpreter.ILoop
import java.io.File
import org.openspaces.scala.util.Utils
import com.j_spaces.kernel.Environment

class GigaSpacesScalaReplLoop extends ILoop {
  
  override def process(settings: Settings): Boolean = {
    echo("Initializing... This may take a few seconds.")
    super.process(settings)
  }
  
  override def closeInterpreter() {
    runCustomShutdownCode()
    super.closeInterpreter()
  }
  
  override def printWelcome() {
    import Properties._
    val welcomeMsg =
     """|Welcome to Scala %s (%s, Java %s).
        |Type in expressions to have them evaluated.
        |Type :help for more information.
        |Please enjoy the predefined 'admin' val.""".
    stripMargin.format(versionString, javaVmName, javaVersion)
    echo(welcomeMsg)
  }
  
  override def prompt = "\nxap> " 
    
  override def postInitialization() {
    super.postInitialization()
    addCustomImports()
    runCustomInitializationCode()
  }
  
  private def addCustomImports() {
    val importsPathProp = "org.os.scala.repl.imports"
    val importsPathDefault = s"${Environment.getHomeDirectory()}/tools/scala/conf/repl-imports.conf"
    val importsFile = new File(Properties.propOrElse(importsPathProp, importsPathDefault))
    if (importsFile.isFile()) {
      Utils.withCloseable(io.Source.fromFile(importsFile)) { imports =>
        imports.getLines().foreach { imp => if (!imp.isEmpty()) intp.quietImport(imp) }
      }
    }
  }
  
  private def runCustomInitializationCode() {
    val initCodePathProp = "org.os.scala.repl.initcode"
    val initCodePathDefault = s"${Environment.getHomeDirectory()}/tools/scala/conf/init-code.scala"
    runCustomCode(initCodePathProp, initCodePathDefault)    
  }
  
  private def runCustomShutdownCode() {
    val shutdownCodePathProp = "org.os.scala.repl.shutdowncode"
    val shutdownCodePathDefault = s"${Environment.getHomeDirectory()}/tools/scala/conf/shutdown-code.scala"
    runCustomCode(shutdownCodePathProp, shutdownCodePathDefault)    
  }
  
  private def runCustomCode(propPath: String, defaultPath: String) {
    val codeFile = new File(Properties.propOrElse(propPath, defaultPath))
    if (codeFile.isFile()) {
      Utils.withCloseable(io.Source.fromFile(codeFile)) { codeSource =>
        val code = codeSource.getLines.mkString(Properties.lineSeparator)
        intp.quietRun(code)
      }
    }     
  }
  
}

object GigaSpacesScalaRepl {
  
  def main(args: Array[String]) {
    new GigaSpacesScalaReplLoop process(args)
  }
  
}

