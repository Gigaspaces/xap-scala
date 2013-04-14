package org.openspaces.scala.common

import scala.beans.BeanProperty
import org.openspaces.scala.core.aliases.annotation._
import com.gigaspaces.annotation.pojo.SpaceClass
import com.gigaspaces.annotation.pojo.SpaceClass.IncludeProperties

case class ScalaDataClass(@SpaceId @BeanProperty var id: String) extends Serializable {
  def this() = this(null)
}

case class ScalaDataClass2(@SpaceId @BeanProperty val id: String,
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

@SpaceClass(includeProperties = IncludeProperties.CONSTRUCTOR)
case class ScalaImmutableDataClass1(
  
  @BeanProperty
  @SpaceId 
  id: String,
  
  @BeanProperty
  name: String
  
)

@SpaceClass(includeProperties = IncludeProperties.CONSTRUCTOR)
case class ScalaImmutableDataClassDefaultValues(
  
  @BeanProperty
  @SpaceId
  id: String = null,
  
  @BeanProperty
  name: String = null
  
)

@SpaceClass(includeProperties = IncludeProperties.CONSTRUCTOR)
case class ScalaImmutableDataClassNullValues(
    
  @BeanProperty
  @SpaceId
  id: String = null,
  
  @BeanProperty
  @SpaceProperty(nullValue = "-1")
  number: Int = -1
                                                
)

@SpaceClass(includeProperties = IncludeProperties.CONSTRUCTOR)
case class ScalaImmutableDataClassExcludedProperties(
                                             
  @BeanProperty
  @SpaceId
  id: String = null,
  
  @BeanProperty
  name: String = null,
  
  @BeanProperty
  @SpaceExclude
  excludedInt: Int = -15,
  
  @BeanProperty
  @SpaceExclude
  excludedString: String = "hurray!"
                                             
)

@SpaceClass(includeProperties = IncludeProperties.CONSTRUCTOR)
class ScalaImmutableDataClassInheritanceParent(

  @BeanProperty
  @SpaceId
  val id: String,

  @BeanProperty
  val name: String
  
) 

@SpaceClass(includeProperties = IncludeProperties.CONSTRUCTOR)
case class ScalaImmutableDataClassInheritanceChild1(
  override val id: String = null,
  override val name: String = null
) extends ScalaImmutableDataClassInheritanceParent(id, name) 
