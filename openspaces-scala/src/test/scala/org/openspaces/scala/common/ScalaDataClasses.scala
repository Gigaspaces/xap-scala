package org.openspaces.scala.common

import scala.beans.BeanProperty
import org.openspaces.scala.core.aliases.annotation._

// The following 2 ScalaDataClasses show that without SpaceClassConstructor is is not possible
// to construct immutable space objects - class is either mutable or it cannot be written to a space.

case class ScalaDataClass(@SpaceId @BeanProperty var id: String) extends Serializable {
  def this() = this(null)
}

case class ScalaDataClass2(@SpaceId @BeanProperty val id: String,
                           @BeanProperty val name: String) extends Serializable {
  def this() = this(null, null)
}

// Below are examples of correct scala immutable data classes - all of them use SpaceClassConstructor.

case class ScalaImmutableDataClass1 @SpaceClassConstructor() (
  
  @BeanProperty
  @SpaceId 
  id: String,
  
  @BeanProperty
  name: String
  
)

case class ScalaImmutableDataClassDefaultValues @SpaceClassConstructor()(
  
  @BeanProperty
  @SpaceId
  id: String = null,
  
  @BeanProperty
  name: String = null
  
)

case class ScalaImmutableDataClassNullValues @SpaceClassConstructor()(
    
  @BeanProperty
  @SpaceId
  id: String = null,
  
  @BeanProperty
  @SpaceProperty(nullValue = "-1")
  number: Int = -1
                                                
)

case class ScalaImmutableDataClassExcludedProperties @SpaceClassConstructor()(
                                             
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

class ScalaImmutableDataClassInheritanceParent @SpaceClassConstructor()(

  @BeanProperty
  @SpaceId
  val id: String,

  @BeanProperty
  val name: String
  
) 

case class ScalaImmutableDataClassInheritanceChild1 @SpaceClassConstructor()(
  override val id: String = null,
  override val name: String = null
) extends ScalaImmutableDataClassInheritanceParent(id, name) 
