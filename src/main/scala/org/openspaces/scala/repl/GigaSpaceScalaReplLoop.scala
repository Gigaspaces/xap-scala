package org.openspaces.scala.repl

import scala.tools.nsc.interpreter.ILoop
import scala.tools.nsc.Settings
import scala.tools.nsc.Properties
import scala.tools.nsc.interpreter.NamedParam
import org.openspaces.admin.AdminFactory
import scala.tools.nsc.interpreter.NamedParamClass
import org.openspaces.admin.Admin

class GigaSpaceScalaReplLoop extends ILoop {
  
  private var admin: Admin = _
  
  override def process(settings: Settings): Boolean = {
    settings.usejavacp.value = true
    settings.Yreplsync.value = true
    echo("initializing...")
    super.process(settings)
  }
  
  override def closeInterpreter() {
    if (admin ne null) {
      admin.close()
      admin = null
    }
    super.closeInterpreter()
  }
  
  override def printWelcome() {
    import Properties._
    val welcomeMsg =
     """|Welcome to Scala %s (%s, Java %s).
        |Type in expressions to have them evaluated.
        |Type :help for more information.
        |Please enjoy the predefined `admin` val.""" .
    stripMargin.format(versionString, javaVmName, javaVersion)
    echo(welcomeMsg)
  }
  
  override def prompt = "\nxap> " 
    
  override def postInitialization() {
    super.postInitialization()
    addCustomImports()
    addCustomBindings()
  }
  
  private def addCustomImports() {
    intp.quietImport(classOf[com.gigaspaces.document.SpaceDocument].getName())
  }
  
  private def addCustomBindings() {
    // create and bind admin
    admin = new AdminFactory().create()
    intp.quietBind(new NamedParamClass("admin", admin.getClass().getName(), admin))
  }
  
}

object GigaSpaceScalaRepl {
  
  def main(args: Array[String]) {
    
    new GigaSpaceScalaReplLoop process new Settings
    
  }
  
}

