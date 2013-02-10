package org.openspaces.remoting.scripting

import java.util.{Map => JMap}
import scala.collection.JavaConversions.mapAsScalaMap
import scala.collection.mutable.Map
import scala.reflect.ClassTag
import scala.reflect.runtime.universe
import scala.tools.reflect.ToolBox
import scala.tools.reflect.ToolBoxError
import com.gigaspaces.internal.utils.StringUtils
import scala.util.Properties

class ScalaLocalScriptExecutor extends AbstractLocalScriptExecutor[java.util.Map[String, Object] => Object] {

  val mirror = universe.runtimeMirror(getClass.getClassLoader)
  val toolbox = ToolBox(mirror).mkToolBox()
  val toolBoxLock = new Object
  
  protected def doCompile(script: Script): JMap[String, Object] => Object = {
    try {
      
        if (!script.isInstanceOf[ScalaTypedScript[_]]) {
          throw new IllegalArgumentException("script must be a typed scala script")
        }
        
        val typedScript = script.asInstanceOf[ScalaTypedScript[_]]
        
        val paramTypes = typedScript.getParameterTypes()
        
        if (paramTypes eq null) {
          throw new IllegalArgumentException("typed scala script must be configured with static binding types")
        }
        
        val wrappedUserCode = ScalaLocalScriptExecutor.wrapUserCode(typedScript.getScriptAsString(), paramTypes)

        toolBoxLock.synchronized {
            val parsedTree = toolbox.parse(wrappedUserCode)
            val compiledScriptHolder = toolbox.compile(parsedTree)
            compiledScriptHolder().asInstanceOf[JMap[String, Object] => Object]
        }
        
    } catch {
      case toolBoxError: ToolBoxError => throw new ScriptCompilationException("Failed compiling scala script", toolBoxError)
      case ex: Exception => throw new ScriptCompilationException("Failed compiling scala script", ex)
    }
  }
  
  def execute(
      script: Script, 
      compiledScript: JMap[String,Object] => Object, 
      parameters: JMap[String,Object]): Object = {
    try {
      compiledScript(parameters)
    } catch {
      case e: Exception => throw new ScriptExecutionException("Failed executing scala script", e)
    }
  }
  
  def close(compiledScript: JMap[String,Object] => Object) = Unit
  
  def isThreadSafe(): Boolean = false
  
}

object ScalaLocalScriptExecutor {

  val defaultValueMethodName = s"${classOf[ScalaLocalScriptExecutor].getName()}.defaultValue"
  
  def defaultValue[T: ClassTag]: T = {
    val classTag = scala.reflect.classTag[T]
    classTag.runtimeClass.toString match {
      case "void" => ().asInstanceOf[T]
      case "boolean" => false.asInstanceOf[T]
      case "byte" => (0: Byte).asInstanceOf[T]
      case "short" => (0: Short).asInstanceOf[T]
      case "char" => '\0'.asInstanceOf[T]
      case "int" => 0.asInstanceOf[T]
      case "long" => 0L.asInstanceOf[T]
      case "float" => 0.0F.asInstanceOf[T]
      case "double" => 0.0.asInstanceOf[T]
      case _ => null.asInstanceOf[T]
    }
  }
  
  private def toScalaTypeName(paramType: Class[_]): String = {
    if (paramType.isArray()) {
      s"Array[${toScalaTypeName(paramType.getComponentType())}]"
    } else {
      paramType.getName() match {
        case "void" => "Unit"
        case "boolean" => "Boolean"
        case "byte" => "Byte"
        case "short" => "Short"
        case "char" => "Char"
        case "int" => "Int"
        case "long" => "Long"
        case "float" => "Float"
        case "double" => "Double"
        case _ => paramType.getName() + createParametersStringIfNeeded(paramType)
      }
    }
  }
  
  private def createParametersStringIfNeeded(paramType: Class[_]): String = {
    if ((paramType.getTypeParameters() ne null) && paramType.getTypeParameters().length > 0) {
      val count = paramType.getTypeParameters().length
      List.fill(count)("_").mkString("[", ",", "]")
    } else {
       ""
    }
  }
  
  private def wrapUserCode(userCode: String, paramTypes: Map[String, Class[_]]): String = {
    s"""
    ${ 
      paramTypes.map({ case (name, paramType) => {
        val scalaTypeName = toScalaTypeName(paramType)
        s"var ${name}: ${scalaTypeName} = ${defaultValueMethodName}[${scalaTypeName}] "
      }}).mkString(Properties.lineSeparator) 
    }
    
    { params: java.util.Map[String, Object] =>

      ${ 
        paramTypes.map({ case(name, paramType) => {
          val scalaTypeName = toScalaTypeName(paramType)
          s"""${name} = params.get(\"${name}\").asInstanceOf[${scalaTypeName}]"""
        }}).mkString(Properties.lineSeparator) 
      }
    
      { 
        ${userCode} 
      }
    }
    """
  }

}
