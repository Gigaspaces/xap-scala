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
  def testIsRun() = {
    val input = ""
    val output = replOutputFor(input)

    Assert.assertEquals((), output)
  }

}
