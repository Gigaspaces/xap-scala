package org.openspaces.scala.core

// I hate this. god damn requirements
object ScalaGigaSpacesTypeAliases {
  
  type G = org.openspaces.core.GigaSpace
  type S = com.j_spaces.core.IJSpace
  type Q[T] = com.gigaspaces.query.ISpaceQuery[T]
  type D = com.gigaspaces.document.SpaceDocument
  type GSM = org.openspaces.admin.gsm.GridServiceManager
  type GSC = org.openspaces.admin.gsc.GridServiceContainer
  type GSA = org.openspaces.admin.gsa.GridServiceAgent
  type ESM = org.openspaces.admin.esm.ElasticServiceManager
  type LUS = org.openspaces.admin.lus.LookupService
   
}
