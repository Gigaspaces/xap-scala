package org.openspaces.remoting.scripting

import org.junit.Test
import org.openspaces.core.GigaSpace
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import javax.annotation.Resource
import com.gigaspaces.annotation.pojo.SpaceId
import scala.beans.BeanProperty
import org.junit.Assert
import org.junit.Before
import org.openspaces.scala.common.ScalaDataClass

@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(value = Array("/org/openspaces/remoting/scripting/scala-scripting-context.xml"))
class ScalaLocalScriptExecutorTest {

  @Resource var asyncScriptingExecutor: ScriptingExecutor[_] = _
  @Resource var executorScriptingExecutor: ScriptingExecutor[_] = _ 
  @Resource var gigaSpace: GigaSpace = _
  
  @Before
  def before() {
    gigaSpace.clear(null)
  }
  
  @Test
  def basicTest() {
    
    gigaSpace.write(ScalaDataClass("123123123"))
    
    val userCode = """
      val templateCount = gigaSpace.count(template) // 1
      val fooLength = foo.length // 5
      val arrayLength = arrayOfStrings.length // 3
      val sumOfStringLengthsInArrayOfString = arrayOfStrings.map(_.length).sum // 11
      val typedSetOfInts = setOfInts.asInstanceOf[Set[Int]]
      val setSize = typedSetOfInts.size // 5
      val setElementsSum = typedSetOfInts.sum // 15
      val sum = templateCount + fooLength + bar + arrayLength + sumOfStringLengthsInArrayOfString + setSize + setElementsSum
      sum
    """
    
    val script = new ScalaTypedStaticScript("name", "scala", userCode)
      .parameter("foo", "a"*5, classOf[String])
      .parameter("bar", 3, classOf[Int])
      .parameter("template", new ScalaDataClass(), classOf[ScalaDataClass])
      .parameter("arrayOfStrings", Array("one", "two", "three"), classOf[Array[String]])
      .parameter("setOfInts", Set(1,2,3,4,5), classOf[Set[Int]])
      
    val result = executorScriptingExecutor.execute(script)
    
    Assert.assertTrue(result.isInstanceOf[Int])
    Assert.assertEquals(1+5+3+11+5+15+3, result.asInstanceOf[Int])
    
  }
  
  @Test
  def scriptCacheTest() {
    
    gigaSpace.write(ScalaDataClass("123123123"))
    
    val script = new ScalaTypedStaticScript("name2", "scala", "gigaSpace.count(null) + foo.size + bar*100")
      .parameterType("foo", classOf[String])
      .parameterType("bar", classOf[Int])
      
    for (i <- 1 to 10000) {
      val rand = (i % 5) + 1
      val result = executorScriptingExecutor.execute(script.parameter("foo", "a"*rand).parameter("bar", rand))
      Assert.assertEquals(1 + rand + rand*100, result)
    }
    
  }
  
  @Test
  def scriptConcurrentTest() {
    gigaSpace.write(ScalaDataClass("123123123"))
    val script = new ScalaTypedStaticScript("name3", "scala", "gigaSpace.count(null)")
    val parCalc = (1 to 100).toList.par.map { i =>
       println(executorScriptingExecutor.execute(script))
    }
  }
  
}