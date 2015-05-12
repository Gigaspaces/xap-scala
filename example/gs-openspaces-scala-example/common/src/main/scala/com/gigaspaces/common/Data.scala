package com.gigaspaces.common

import org.openspaces.scala.core.aliases.annotation._

import scala.beans.{BooleanBeanProperty, BeanProperty}

case class Data (
  @BeanProperty @SpaceId(autoGenerate = true) var id: String = null,
  @BeanProperty @SpaceRouting @SpaceProperty(nullValue = "-1") var `type`: Long = -1,
  @BeanProperty var rawData: String = null,
  @BeanProperty var data: String = null,
  @BooleanBeanProperty var processed: Boolean = false) extends scala.Serializable {

  def this() = this(null, -1, null, null, false)

  def this(`type`: Long, rawData: String) = this(null, `type`, rawData, null, false)

  override def toString: String = {
    return "id[" + id + "] type[" + `type` + "] rawData[" + rawData + "] data[" + data + "] processed[" + processed + "]"
  }
}




case class ImmutableData @SpaceClassConstructor() (
  @BeanProperty
  @SpaceId(autoGenerate = true)
  id: String = null,

  @BeanProperty
  @SpaceRouting
  @SpaceProperty(nullValue = "-1")
  `type`: Long = -1,

  @BeanProperty
  rawData: String = null,

  @BeanProperty
  data: String = null,

  @BooleanBeanProperty
  processed: Boolean = false) extends scala.Serializable {

  def this(`type`: Long, rawData: String) = this(null, `type`, rawData, null, false)

  override def toString: String = {
    return "id[" + id + "] type[" + `type` + "] rawData[" + rawData + "] data[" + data + "] processed[" + processed + "]"
  }
}