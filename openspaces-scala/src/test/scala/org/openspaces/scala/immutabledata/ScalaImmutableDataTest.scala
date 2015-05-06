package org.openspaces.scala.immutabledata

import org.openspaces.core.SpaceMetadataException
import org.openspaces.scala.common._
import org.junit.Test
import org.junit.Assert

class ScalaImmutableDataTest extends StartNewGigaSpace {

  @Test
  def testSimple() = {
    val written = ScalaImmutableDataClass1("myId", "myName")
    val template = ScalaImmutableDataClass1(name=null,id=null)
    gigaSpace.write(written)
    val read = gigaSpace.read(template)
    Assert.assertEquals(written, read)
  }
  
  @Test
  def testDefaultConstructorValues() = {
    val written = ScalaImmutableDataClassDefaultValues("myId")
    val template = ScalaImmutableDataClassDefaultValues()
    gigaSpace.write(written)
    val read = gigaSpace.read(template)
    Assert.assertEquals(written, read)
  }
  
  @Test
  def testPrimitiveNullValue() = {
    val written = ScalaImmutableDataClassNullValues("myId")
    val template = ScalaImmutableDataClassNullValues()
    gigaSpace.write(written)
    val read = gigaSpace.read(template)
    Assert.assertEquals(written, read)
  }
  
  @Test
  def testExcludedProperties() = {
    val written = ScalaImmutableDataClassExcludedProperties("myId", "myName")
    val template = ScalaImmutableDataClassExcludedProperties(excludedInt = 100, excludedString = "string")
    gigaSpace.write(written)
    val read = gigaSpace.read(template)
    Assert.assertEquals(written.copy(excludedInt = 0, excludedString = null), read)
  }

  @Test
  def testInheritance1() = {
    val written = ScalaImmutableDataClassInheritanceChild1("myId", "myName")
    val template = ScalaImmutableDataClassInheritanceChild1()
    gigaSpace.write(written)
    val read = gigaSpace.read(template)
    Assert.assertEquals(written, read)
  }

  @Test
  def testInvalidImmutables() = {
    val written = ScalaDataClass("id1")
    val newId = "newId"
    written.id = newId
    Assert.assertEquals(newId, written.id)
  }

  @Test(expected = classOf[SpaceMetadataException])
  def testIncorrectImmutable() {
    val written = ScalaDataClass2("id2", "ScalaDataClass2")
    gigaSpace.write(written)
  }

}