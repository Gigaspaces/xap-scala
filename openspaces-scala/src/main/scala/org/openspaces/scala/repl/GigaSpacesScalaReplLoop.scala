/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openspaces.scala.repl

import scala.tools.nsc.util._
import scala.tools.nsc.{GenericRunnerSettings, Properties, Settings}
import scala.tools.nsc.interpreter._
import java.io.{BufferedReader, File}
import org.openspaces.scala.util.{ImportReader, Utils}
import com.j_spaces.kernel.Environment

/**
 * An extension of [[scala.tools.nsc.interpreter.ILoop]] (The scala REPL).
 * 
 * @since 9.6
 * @author Dan Kilman
 */
class GigaSpacesScalaReplLoop(in0: Option[BufferedReader],
                              override protected val out: JPrintWriter) extends ILoop(in0, out) {

  def this() = this(None, new JPrintWriter(Console.out, true))
  
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

  override def loadFiles(settings: Settings) {
    super.loadFiles(settings)
    addCustomImports()
    runCustomInitializationCode()
  }
  
  private def addCustomImports() {
    // This way of running imports ensures the import code will be run after interpreter is fully initialized.
    // Further code executions can be run using intp.quiteRun method.
    def run() = {
      val importsPathProp = "org.os.scala.repl.imports"
      val importsPathDefault = s"${Environment.getHomeDirectory()}/tools/scala/conf/repl-imports.conf"
      val importsPath = Properties.propOrElse(importsPathProp, importsPathDefault)
      withFile(importsPath) { file =>
        savingReader {
          savingReplayStack {
            file applyReader { reader =>
              in = new ImportReader(reader, out)
              loop()
            }
          }
        }
      }
    }

    intp.beQuietDuring {
      run()
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

/**
 * Entry point for the XAP enhanced scala REPL.
 * 
 * @since 9.6
 * @author Dan Kilman
 */
object GigaSpacesScalaRepl {
  
  def main(args: Array[String]) {
    val settings = new GenericRunnerSettings(Console.println)
    val defaultArgs = List("-usejavacp")
    settings.processArguments(defaultArgs ++ args.iterator.toList, true)

    new GigaSpacesScalaReplLoop process settings
  }

  def run(code: String, sets: Settings = new Settings): String = {
    // Used during tests. This is implemented in the same way as in ILoop.
    import java.io.{BufferedReader, StringReader, OutputStreamWriter}

    stringFromStream { ostream =>
      Console.withOut(ostream) {
        val input    = new BufferedReader(new StringReader(code))
        val output   = new JPrintWriter(new OutputStreamWriter(ostream), true)
        val repl     = new GigaSpacesScalaReplLoop(Some(input), output)

        repl process sets
      }
    }
  }
}

