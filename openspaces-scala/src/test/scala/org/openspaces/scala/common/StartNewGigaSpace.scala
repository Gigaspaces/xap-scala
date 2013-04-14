package org.openspaces.scala.common

import org.openspaces.core.GigaSpace
import org.openspaces.core.GigaSpaceConfigurer
import org.openspaces.core.space.UrlSpaceConfigurer
import org.junit.After
import org.junit.Before

trait StartNewGigaSpace {

  var configurer: UrlSpaceConfigurer = _
  var gigaSpace: GigaSpace = _
  
  @Before 
  def before() {
    configurer = new UrlSpaceConfigurer("/./testSpace")
    gigaSpace = new GigaSpaceConfigurer(configurer).create()
  }
  
  @After
  def after() {
    if (configurer != null) {
      configurer.destroy()
    }
  } 
  
}