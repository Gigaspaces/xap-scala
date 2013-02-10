package org.openspaces.remoting.scripting

import scala.beans.BeanProperty
import java.util.{Map => JMap}
import java.io.ObjectOutput
import java.io.ObjectInput
import java.io.Externalizable
import scala.collection.JavaConversions.mapAsScalaMap

trait ScalaTypedScript[T <: Script] extends TypedScript {
  
  self: T =>
  
  @BeanProperty var parameterTypes: JMap[String, Class[_]] = _

  def parameterType(name: String, staticType: Class[_]): T = {
    if (parameterTypes eq null) {
      parameterTypes = new java.util.HashMap[String, Class[_]]()
    }
    parameterTypes.put(name, staticType)
    this
  }
  
  def writeParameterTypesMap(out: ObjectOutput) {
    if (parameterTypes eq null) {
      out.writeBoolean(false)
    } else {
      out.writeBoolean(true)
      out.writeShort(parameterTypes.size)
      parameterTypes.foreach { case(key, value) =>
        out.writeUTF(key)
        out.writeObject(value)
      }
    }
  }
  
  def readParameterTypesMap(in: ObjectInput) {
    if (in.readBoolean()) {
      val size = in.readShort()
      parameterTypes = new java.util.HashMap[String, Class[_]]()
      for (i <- 0 until size) {
        val key = in.readUTF()
        val value = in.readObject().asInstanceOf[Class[_]]
        parameterTypes.put(key, value)
      }
    }
  }
  
}

class ScalaTypedStaticScript(name: String, scriptType: String, code: String) 
  extends StaticScript(name, scriptType, code)
  with ScalaTypedScript[ScalaTypedStaticScript] {
  
  def parameter(name: String, value: Any, staticType: Class[_]) = {
    super.parameter(name, value)
    parameterType(name, staticType)
  }
  
  override def writeExternal(out: ObjectOutput) {
    super.writeExternal(out)
    writeParameterTypesMap(out)
  }

  override def readExternal(in: ObjectInput) {
    super.readExternal(in)
    readParameterTypesMap(in)
  }
  
}

class ScalaTypedStaticResourceScript(name: String, scriptType: String, resourceLocation: String) 
  extends StaticResourceScript(name, scriptType, resourceLocation)
  with ScalaTypedScript[ScalaTypedStaticResourceScript] {
  
  def parameter(name: String, value: Any, staticType: Class[_]) = {
    super.parameter(name, value)
    parameterType(name, staticType)
  }
  
    override def writeExternal(out: ObjectOutput) {
    super.writeExternal(out)
    writeParameterTypesMap(out)
  }

  override def readExternal(in: ObjectInput) {
    super.readExternal(in)
    readParameterTypesMap(in)
  }
  
}

class ScalaTypedResourceLazyLoadingScript(name: String, scriptType: String, resourceLocation: String) 
  extends ResourceLazyLoadingScript(name, scriptType, resourceLocation)
  with ScalaTypedScript[ScalaTypedResourceLazyLoadingScript] {
  
  def parameter(name: String, value: Any, staticType: Class[_]) = {
    super.parameter(name, value)
    parameterType(name, staticType)
  }
  
  override def writeExternal(out: ObjectOutput) {
    super.writeExternal(out)
    writeParameterTypesMap(out)
  }

  override def readExternal(in: ObjectInput) {
    super.readExternal(in)
    readParameterTypesMap(in)
  }
  
}

