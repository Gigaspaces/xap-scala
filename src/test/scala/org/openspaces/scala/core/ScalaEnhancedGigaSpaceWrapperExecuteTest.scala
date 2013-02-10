package org.openspaces.scala.core

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import scala.beans.BeanProperty
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.openspaces.core.GigaSpace
import org.openspaces.core.GigaSpaceConfigurer
import org.openspaces.core.space.UrlSpaceConfigurer
import com.gigaspaces.async.AsyncResult
import ScalaGigaSpacesImplicits.ScalaEnhancedGigaSpaceWrapper
import com.gigaspaces.annotation.pojo.SpaceId
import org.openspaces.scala.common.StartNewGigaSpace
import org.openspaces.scala.common.ScalaDataClass

class ScalaEnhancedGigaSpaceWrapperExecuteTest extends StartNewGigaSpace {

  @Test
  def testExecute {
    
    val mapper = { gigaSpace: GigaSpace => gigaSpace.read(new ScalaDataClass()) }
    val reducer = { results: Seq[AsyncResult[ScalaDataClass]] => results(0).getResult().id }

    val routing = new Object
    val dataClass = ScalaDataClass("id1")
    gigaSpace.write(dataClass)
    
    val result1 = gigaSpace.execute(mapper, reducer)
    Assert.assertEquals(dataClass.id, result1.get())
    
    val result2 = gigaSpace.execute(mapper)
    Assert.assertEquals(dataClass, result2.get())
    
    val result3 = gigaSpace.execute(mapper, reducer, routing, routing)
    Assert.assertEquals(dataClass.id, result3.get())
    
    val result4 = gigaSpace.execute(mapper, routing)
    Assert.assertEquals(dataClass, result4.get())
    
    val latch = new CountDownLatch(1)
    val validResult = new AtomicBoolean(false)
    val asyncFutureListener = { result: AsyncResult[ScalaDataClass] => 
      validResult.set(dataClass == result.getResult())
      latch.countDown()
    }
    val result5 = gigaSpace.execute(mapper, routing, asyncFutureListener)
    Assert.assertEquals(dataClass, result5.get())
    latch.await(5, TimeUnit.SECONDS)
    Assert.assertTrue(validResult.get())
    
  }
  
}