package org.openspaces.remoting.scripting

import scala.beans.BeanProperty
import java.util.{Map => JMap}
import java.io.ObjectOutput
import java.io.ObjectInput
import java.io.Externalizable
import scala.collection.JavaConversions.mapAsScalaMap
import com.gigaspaces.internal.io.IOUtils
import com.gigaspaces.internal.utils.ObjectUtils
import com.j_spaces.kernel.ClassLoaderHelper
import org.openspaces.remoting.RemoteResultReducer
import scala.annotation.varargs

trait ScalaTypedScript extends TypedScript with Externalizable {
  
  @BeanProperty val parameterTypes: JMap[String, Class[_]] = new java.util.HashMap[String, Class[_]]()

  def parameterType(name: String, staticType: Class[_]): this.type = { parameterTypes.put(name, staticType); this }
  
  abstract override def writeExternal(out: ObjectOutput) {
    super.writeExternal(out)
    writeParameterTypesMap(out)
  }
  abstract override def readExternal(in: ObjectInput) {
    super.readExternal(in)
    readParameterTypesMap(in)
  }
  protected def writeParameterTypesMap(out: ObjectOutput) {
    if (parameterTypes eq null) {
      out.writeBoolean(false)
    } else {
      out.writeBoolean(true)
      out.writeShort(parameterTypes.size)
      parameterTypes.foreach { case(paramName, paramType) =>
        IOUtils.writeString(out, paramName)
        IOUtils.writeString(out, paramType.getName())
      }
    }
  }
  protected def readParameterTypesMap(in: ObjectInput) {
    if (in.readBoolean()) {
      val size = in.readShort()
      for (i <- 0 until size) {
        val paramName = IOUtils.readString(in)
        val paramTypeName = IOUtils.readString(in)
        val paramType = 
          if (ObjectUtils.isPrimitive(paramTypeName)) 
            ObjectUtils.getPrimitive(paramTypeName)
          else
            ClassLoaderHelper.loadClass(paramTypeName, false /* localOnly */);
        parameterTypes.put(paramName, paramType)
      }
    }
  }
  
}

class ScalaTypedStaticScript(name: String, scriptType: String, code: String) 
  extends StaticScript(name, scriptType, code)
  with ScalaTypedScript {

  def this() = this(null, null, null)
  
  override def parameter(name: String, value: Any) = { super.parameter(name, value); this }
  override def name(name: String) = { super.name(name); this }
  override def script(script: String) = { super.script(script); this }
  override def `type`(`type`: String) = { super.`type`(`type`); this }
  override def cache(shouldCache: Boolean) = { super.cache(shouldCache); this }
  override def routing(routing: Any) = { super.routing(routing); this }
  override def broadcast[T, Y](reducer: RemoteResultReducer[T, Y]) = { super.broadcast(reducer); this }
  
  def parameter(name: String, value: Any, staticType: Class[_]) = {
    super.parameter(name, value)
    parameterType(name, staticType)
  }
}

class ScalaTypedStaticResourceScript(name: String, scriptType: String, resourceLocation: String) 
  extends StaticResourceScript(name, scriptType, resourceLocation)
  with ScalaTypedScript {
  
  override def parameter(name: String, value: Any) = { super.parameter(name, value); this }
  override def name(name: String) = { super.name(name); this }
  override def script(script: String) = { super.script(script); this }
  override def `type`(`type`: String) = { super.`type`(`type`); this }
  override def cache(shouldCache: Boolean) = { super.cache(shouldCache); this }
  override def routing(routing: Any) = { super.routing(routing); this }
  override def broadcast[T, Y](reducer: RemoteResultReducer[T, Y]) = { super.broadcast(reducer); this }
  
  def parameter(name: String, value: Any, staticType: Class[_]) = {
    super.parameter(name, value)
    parameterType(name, staticType)
  }
}

class ScalaTypedResourceLazyLoadingScript(name: String, scriptType: String, resourceLocation: String) 
  extends ResourceLazyLoadingScript(name, scriptType, resourceLocation)
  with ScalaTypedScript {
  
  def this() = this(null, null, null)
  
  override def parameter(name: String, value: Any) = { super.parameter(name, value); this }
  override def name(name: String) = { super.name(name); this }
  override def script(script: String) = { super.script(script); this }
  override def `type`(`type`: String) = { super.`type`(`type`); this }
  override def cache(shouldCache: Boolean) = { super.cache(shouldCache); this }
  override def routing(routing: Any) = { super.routing(routing); this }
  override def broadcast[T, Y](reducer: RemoteResultReducer[T, Y]) = { super.broadcast(reducer); this }
  
  def parameter(name: String, value: Any, staticType: Class[_]) = { 
    super.parameter(name, value)
    parameterType(name, staticType)
  }
}

