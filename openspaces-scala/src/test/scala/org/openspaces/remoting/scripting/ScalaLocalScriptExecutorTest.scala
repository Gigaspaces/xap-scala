package org.openspaces.remoting.scripting

import java.util.concurrent.TimeUnit

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.openspaces.core.GigaSpace
import org.openspaces.scala.common.ScalaDataClass
import org.springframework.test.context.ContextConfiguration
import javax.annotation.Resource
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.openspaces.scala.common.ScalaImmutableDataClass1

@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(value = Array("/org/openspaces/remoting/scripting/scala-scripting-context.xml"))
class ScalaLocalScriptExecutorTest {

  @Resource var scriptingExecutorImpl: DefaultScriptingExecutor = _
  @Resource var executorScriptingExecutor: ScriptingExecutor[_] = _
  @Resource var gigaSpace: GigaSpace = _
  val spaceObject = ScalaImmutableDataClass1("123123123", "theName")
  
  @Before
  def before() {
    gigaSpace.clear(null)
  }
  
  @Test
  def basicTest() {
    gigaSpace.write(spaceObject)
    
    val userCode = """
      import org.openspaces.scala.common.ScalaImmutableDataClass1
      
      val templateCount = gigaSpace.count(template) // 1
      val fooLength = foo.length // 5
      val arrayLength = arrayOfStrings.length // 3
      val sumOfStringLengthsInArrayOfString = arrayOfStrings.map(_.length).sum // 11
      val typedSetOfInts = setOfInts.asInstanceOf[Set[Int]]
      val setSize = typedSetOfInts.size // 5
      val setElementsSum = typedSetOfInts.sum // 15
      
      val wrapper = gigaSpace.predicate
      val predCount = wrapper.count { dataClass: ScalaImmutableDataClass1 =>
        dataClass.name == "theName" || dataClass.name == "otherName"
      }
      
      val predRead = wrapper.read { dataClass: ScalaImmutableDataClass1 =>
        dataClass.name == "theName" || dataClass.name == "otherName"
      }
      
      val sum = predRead.name.length + predCount + templateCount + fooLength + bar + arrayLength + sumOfStringLengthsInArrayOfString + setSize + setElementsSum
      sum
    """
    
    val script = new ScalaTypedStaticScript("name", "scala", userCode)
      .parameter("foo", "a"*5)
      .parameter("bar", 3)
      .parameter("template", new ScalaImmutableDataClass1(null, null))
      .parameter("arrayOfStrings", Array("one", "two", "three"))
      .parameter("setOfInts", Set(1,2,3,4,5), classOf[Set[_]])
      
    val result = executorScriptingExecutor.execute(script)
    
    Assert.assertTrue(result.isInstanceOf[Int])
    Assert.assertEquals(spaceObject.name.length+1+1+5+3+11+5+15+3, result.asInstanceOf[Int])
    
  }
  
  @Test
  def scriptCacheTest() {
    
    gigaSpace.write(ScalaDataClass("123123123"))
    
    val script = new ScalaTypedStaticScript("name2", "scala", "gigaSpace.count(null) + foo.size + bar*100")
      
    // simply run this many iterations. without caching, this will throw OutOfMemory: PermGen...
    for (i <- 1 to 10000) {
      val rand = (i % 5) + 1
      val result = executorScriptingExecutor.execute(script.parameter("foo", "a"*rand).parameter("bar", rand))
      Assert.assertEquals(1 + rand + rand*100, result)
    }
    
  }
  
  // The shared dynamic compilation instance is not thread safe
  // Without synchonization on it, this test will fail
  @Test
  def scriptConcurrentTest() {
    gigaSpace.write(ScalaDataClass("123123123"))
    val script = new ScalaTypedStaticScript("name3", "scala", "gigaSpace.count(null)")
    val parCalc = (1 to 100).toList.par.map { i =>
       executorScriptingExecutor.execute(script)
    }
  }
  
  @Test
  def globalParameterTypesTest() {
    val jMap = new java.util.HashMap[String, Class[_]]()
    jMap.put("setType", classOf[Set[_]])
    scriptingExecutorImpl.setParameterTypes(jMap)
    val script = new ScalaTypedStaticScript("globalParameterTestScript", "scala", "setType.size")
        .parameter("setType", Set(1,2,3,4))
    val result = executorScriptingExecutor.execute(script)
    Assert.assertEquals(4, result)
  }

  @Test(expected = classOf[ScriptExecutionException])
  def errorScriptTest() {
    val script = new ScalaTypedStaticScript("errorScriptTest", "scala", "val x = 30 / 0")
    executorScriptingExecutor.execute(script)
  }

  @Test(expected = classOf[ScriptCompilationException])
  def syntaxErrorTest() {
    val script = new ScalaTypedStaticScript("syntaxErrorTest", "scala", "val x = 30 30")
    executorScriptingExecutor.execute(script)
  }

  @Test(expected = classOf[ScriptCompilationException])
  def missingParameterTest() {
    val script = new ScalaTypedStaticScript("missingParameterTest", "scala", "val x = a")
    val result = executorScriptingExecutor.execute(script)
  }

  @Test
  def asyncTest() {
    gigaSpace.write(spaceObject)

    val userCode = """
      import org.openspaces.scala.common.ScalaImmutableDataClass1
      gigaSpace.count(new ScalaImmutableDataClass1(null, null))
    """

    val script = new ScalaTypedStaticScript("asyncTest", "scala", userCode)
    val result = executorScriptingExecutor.asyncExecute(script).get(10, TimeUnit.SECONDS)

    Assert.assertTrue(result.isInstanceOf[Int])
    Assert.assertEquals(1, result.asInstanceOf[Int])
  }

  @Test
  def resourceScriptTest(): Unit = {
    gigaSpace.write(spaceObject)

    val scriptPath = "/org/openspaces/remoting/scripting/simple_script.scala"

    val script = new ScalaTypedStaticResourceScript("resourceScriptTest", "scala", scriptPath)
    val result = executorScriptingExecutor.execute(script)

    Assert.assertTrue(result.isInstanceOf[Int])
    Assert.assertEquals(1, result.asInstanceOf[Int])
  }

  @Test
  def lazyResourceScriptTest(): Unit = {
    gigaSpace.write(spaceObject)

    val scriptPath = "/org/openspaces/remoting/scripting/simple_script.scala"

    val script = new ScalaTypedResourceLazyLoadingScript("lazyResourceScriptTest", "scala", scriptPath)
    val result = executorScriptingExecutor.execute(script)

    Assert.assertTrue(result.isInstanceOf[Int])
    Assert.assertEquals(1, result.asInstanceOf[Int])
  }

}