package org.openspaces.scala.core.aliases {
  
  package object misc {
    
    // Silly short names
    type G     = org.openspaces.core.GigaSpace
    type S     = com.j_spaces.core.IJSpace
    type Q[T]  = com.gigaspaces.query.ISpaceQuery[T]
    type D     = com.gigaspaces.document.SpaceDocument
    type GSM   = org.openspaces.admin.gsm.GridServiceManager
    type GSC   = org.openspaces.admin.gsc.GridServiceContainer
    type GSA   = org.openspaces.admin.gsa.GridServiceAgent
    type ESM   = org.openspaces.admin.esm.ElasticServiceManager
    type LUS   = org.openspaces.admin.lus.LookupService
    
  }
  
  package object annotation {
    
    import scala.annotation.meta.beanGetter
    import com.gigaspaces.annotation.pojo
    
    type SpaceClass                = pojo.SpaceClass
    
    // Enhance space annotations with @beanGetter property
    type SpaceDynamicProperties    = pojo.SpaceDynamicProperties @beanGetter
    type SpaceExclude              = pojo.SpaceExclude @beanGetter
    type SpaceFifoGroupingIndex    = pojo.SpaceFifoGroupingIndex @beanGetter
    type SpaceFifoGroupingProperty = pojo.SpaceFifoGroupingProperty @beanGetter
    type SpaceId                   = pojo.SpaceId @beanGetter
    type SpaceIndex                = pojo.SpaceIndex @beanGetter
    type SpaceIndexes              = pojo.SpaceIndexes @beanGetter
    type SpaceLeaseExpiration      = pojo.SpaceLeaseExpiration @beanGetter
    type SpacePersist              = pojo.SpacePersist @beanGetter
    type SpaceProperty             = pojo.SpaceProperty @beanGetter
    type SpaceRouting              = pojo.SpaceRouting @beanGetter
    type SpaceStorageType          = pojo.SpaceStorageType @beanGetter
    type SpaceVersion              = pojo.SpaceVersion @beanGetter  
    
  }
  
}
