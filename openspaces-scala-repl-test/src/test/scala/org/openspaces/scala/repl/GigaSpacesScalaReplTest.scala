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

    println("*************************************************")
    println(output)
    Assert.assertEquals(0, 0)
  }

  @Test
  def anotherTestIsRunTwice() = {
    val input = ""
    val output = replOutputFor(input)

    val input2 = ""
    val output2 = replOutputFor(input2)

    println("*************************************************")
    println(output)
    Assert.assertEquals(0, 0)

    println("*************************************************")
    println(output2)
    Assert.assertEquals(0, 0)
  }
}
