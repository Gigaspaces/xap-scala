package org.openspaces.scala.repl

import org.junit.Test
import scala.tools.nsc.Settings
import org.junit.Before
import com.j_spaces.kernel.SystemProperties
import org.junit.Ignore

//@Ignore
class GigaSpaceScalaReplTest {

  @Before
  def before() {
    sys.props += "org.apache.commons.logging.Log" -> "org.apache.commons.logging.impl.Jdk14Logger"
    sys.props += SystemProperties.JINI_LUS_GROUPS -> "dank"
  }
  
  @Test
  def test() {
    new GigaSpaceScalaReplLoop process new Settings
  }
  
}