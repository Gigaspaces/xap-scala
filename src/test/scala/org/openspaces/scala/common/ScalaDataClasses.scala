package org.openspaces.scala.common

import com.gigaspaces.annotation.pojo.SpaceId
import scala.beans.BeanProperty

case class ScalaDataClass(@SpaceId @BeanProperty var id: String) extends Serializable {
  def this() = this(null)
}

case class ScalaDataClass2(@SpaceId @BeanProperty var id: String,
                           @BeanProperty val name: String) extends Serializable {
  def this() = this(null, null)
}

class ScalaDataClass3(@SpaceId id: String, name: String) extends Serializable {
  def this() = this(null, null)

  private var _id: String = id
  private var _name: String = name

  def getId() = _id
  
  private def setId(id: String) = _id = id
  
  def getName() = _name
  
  private def setName(name: String) = _name = name
  
}