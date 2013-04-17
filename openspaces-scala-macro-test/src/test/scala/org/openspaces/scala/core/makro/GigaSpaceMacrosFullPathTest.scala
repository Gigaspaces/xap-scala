package org.openspaces.scala.core.makro

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.openspaces.core.GigaSpace
import org.openspaces.core.GigaSpaceConfigurer
import org.openspaces.core.space.UrlSpaceConfigurer
import org.openspaces.scala.core.ScalaGigaSpacesImplicits.ScalaEnhancedGigaSpaceWrapper
import org.openspaces.scala.core.aliases.annotation._

case class MacroTestDataClass(
  
  
                              
)

class GigaSpaceMacrosFullPathTest {

  var configurer: UrlSpaceConfigurer = _
  var gigaSpace: GigaSpace = _
  
//  @Before 
  def before() {
    configurer = new UrlSpaceConfigurer("/./testSpace")
    gigaSpace = new GigaSpaceConfigurer(configurer).create()
  }
  
//  @After
  def after() {
    if (configurer != null) {
      configurer.destroy()
    }
  } 
  
  @Test
  def test() {
    
//    val written = ScalaImmutableDataClassNullValues(id = "someId", number = 13)
//    
//    gigaSpace.write(ScalaImmutableDataClassNullValues(id = "someId", number = 13))
//    
//    val predicateSpace = gigaSpace.predicate
//    
//    val result = predicateSpace.read { data: ScalaImmutableDataClassNullValues =>
//      data.id == written.id
//    }
//    
//    Assert.assertEquals(written, result)
    
    
  }
  
}