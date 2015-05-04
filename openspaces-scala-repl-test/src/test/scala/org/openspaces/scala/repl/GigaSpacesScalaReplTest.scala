package org.openspaces.scala.repl

import org.junit.{Assert, Test}

import scala.tools.nsc.GenericRunnerSettings

class GigaSpacesScalaReplTest {

  def replOutputFor(input: String) {
    val settings = new GenericRunnerSettings(Console.println)
    settings.usejavacp.value = true
    settings.Yreplsync.value = true

    GigaSpacesScalaRepl.run(input, settings)
  }

  @Test
  def emptyRun() = {
    val output = replOutputFor("")
    Assert.assertEquals((), output)
  }

  @Test
  def runWithNewInitScala() = {
    System.setProperty("org.os.scala.repl.newinitstyle", "true")
    try {
      val output = replOutputFor("")
      Assert.assertEquals((), output)
    } finally {
      System.clearProperty("org.os.scala.repl.newinitstyle")
    }
  }

}
