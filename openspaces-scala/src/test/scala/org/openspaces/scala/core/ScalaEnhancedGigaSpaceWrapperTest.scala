package org.openspaces.scala.core

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

import org.junit.Assert
import org.junit.Test
import org.openspaces.core.GigaSpace
import org.openspaces.scala.common.ScalaImmutableDataClass1
import org.openspaces.scala.common.ScalaImmutableDataClassNullValues
import org.openspaces.scala.common.StartNewGigaSpace

import com.gigaspaces.annotation.pojo.SpaceClass
import com.gigaspaces.async.AsyncResult
import com.gigaspaces.client.ChangeModifiers
import com.gigaspaces.client.ChangeResult
import com.gigaspaces.client.ChangeSet
import com.gigaspaces.client.ReadModifiers
import com.gigaspaces.client.TakeModifiers
import com.gigaspaces.query.QueryResultType
import com.j_spaces.core.client.SQLQuery

import ScalaGigaSpacesImplicits.ScalaAsyncFutureListener
import ScalaGigaSpacesImplicits.ScalaEnhancedGigaSpaceWrapper

class ScalaEnhancedGigaSpaceWrapperTest extends StartNewGigaSpace {

  @Test
  def testExecute() {
    
    val mapper = { gigaSpace: GigaSpace => gigaSpace.read(new ScalaImmutableDataClass1(null, null)) }
    val reducer = { results: Seq[AsyncResult[ScalaImmutableDataClass1]] => results(0).getResult().id }

    val routing = new Object
    val dataClass = ScalaImmutableDataClass1("id1", "name1")
    gigaSpace.write(dataClass)
    
    val result1 = gigaSpace.execute(mapper, reducer)
    Assert.assertEquals(dataClass.id, result1.get())
    
    val result2 = gigaSpace.execute(mapper)
    Assert.assertEquals(dataClass, result2.get())
    
    val result3 = gigaSpace.execute(mapper, reducer, routing, routing)
    Assert.assertEquals(dataClass.id, result3.get())
    
    val result4 = gigaSpace.execute(mapper, routing)
    Assert.assertEquals(dataClass, result4.get())
    
    assertAsyncOperation(dataClass) { testData =>
      gigaSpace.execute(mapper, routing, testData.asyncFutureListener)
    }

  }
  
  @Test
  def testAsyncRead() {

    val written = ScalaImmutableDataClass1("id1", "name1")
    val template = ScalaImmutableDataClass1(null, null)
    val query = new SQLQuery(classOf[ScalaImmutableDataClass1], "", QueryResultType.DEFAULT)
    val timeout = 0
    val readModifiers = ReadModifiers.NONE
    
    assertAsyncOperation(written) { testData =>
      gigaSpace.asyncRead(template, timeout, readModifiers, testData.asyncFutureListener)  
    }

    assertAsyncOperation(written) { testData =>
      gigaSpace.asyncRead(template, timeout, testData.asyncFutureListener)  
    }
        
    assertAsyncOperation(written) { testData =>
      gigaSpace.asyncRead(template, testData.asyncFutureListener)  
    }
            
    assertAsyncOperation(written) { testData  =>
      gigaSpace.asyncRead(query, timeout, readModifiers, testData.asyncFutureListener)  
    }
                
    assertAsyncOperation(written) { testData =>
      gigaSpace.asyncRead(query, timeout, testData.asyncFutureListener)  
    }
                    
    assertAsyncOperation(written) { testData  =>
      gigaSpace.asyncRead(query, testData.asyncFutureListener)  
    }
  }
  
  @Test
  def testAsyncTake() {

    val written = ScalaImmutableDataClass1("id1", "name1")
    val template = ScalaImmutableDataClass1(null, null)
    val query = new SQLQuery(classOf[ScalaImmutableDataClass1], "", QueryResultType.DEFAULT)
    val timeout = 0
    val takeModifiers = TakeModifiers.NONE
    
    assertAsyncOperation(written) { testData =>
      gigaSpace.asyncTake(template, timeout, takeModifiers, testData.asyncFutureListener)  
    }

    assertAsyncOperation(written) { testData =>
      gigaSpace.asyncTake(template, timeout, testData.asyncFutureListener)  
    }
        
    assertAsyncOperation(written) { testData =>
      gigaSpace.asyncTake(template, testData.asyncFutureListener)  
    }
            
    assertAsyncOperation(written) { testData =>
      gigaSpace.asyncTake(query, timeout, takeModifiers, testData.asyncFutureListener)  
    }
                
    assertAsyncOperation(written) { testData =>
      gigaSpace.asyncTake(query, timeout, testData.asyncFutureListener)  
    }
                    
    assertAsyncOperation(written) { testData =>
      gigaSpace.asyncTake(query, testData.asyncFutureListener)  
    }
    
  }
  
  @Test
  def testAsyncChange() {
    
    val written = ScalaImmutableDataClassNullValues("id1", 1)
    val template = ScalaImmutableDataClassNullValues()
    val query = new SQLQuery(classOf[ScalaImmutableDataClassNullValues], "", QueryResultType.DEFAULT)
    val timeout = 0
    val changeModifiers = ChangeModifiers.NONE
    val changeSet = new ChangeSet().increment("number", 1)
    gigaSpace.write(written)
    
    assertAsyncChangeOperation(written) { testData =>
      gigaSpace.asyncChange(template, changeSet, changeModifiers, timeout, testData.asyncFutureListener)      
    }
    
    assertAsyncChangeOperation(written) { testData =>
      gigaSpace.asyncChange(template, changeSet, timeout, testData.asyncFutureListener)      
    }
    
    assertAsyncChangeOperation(written) { testData =>
      gigaSpace.asyncChange(template, changeSet, changeModifiers, testData.asyncFutureListener)      
    }
    
    assertAsyncChangeOperation(written) { testData =>
      gigaSpace.asyncChange(template, changeSet, testData.asyncFutureListener)      
    }
    
    assertAsyncChangeOperation(written) { testData =>
      gigaSpace.asyncChange(query, changeSet, changeModifiers, timeout, testData.asyncFutureListener)      
    }
    
    assertAsyncChangeOperation(written) { testData =>
      gigaSpace.asyncChange(query, changeSet, timeout, testData.asyncFutureListener)      
    }
    
    assertAsyncChangeOperation(written) { testData =>
      gigaSpace.asyncChange(query, changeSet, changeModifiers, testData.asyncFutureListener)      
    }
    
    assertAsyncChangeOperation(written) { testData =>
      gigaSpace.asyncChange(query, changeSet, testData.asyncFutureListener)      
    }
    
  }
  
  def assertAsyncChangeOperation(data: ScalaImmutableDataClassNullValues)(op: ChangeTestData => Unit) {
    val testData = new ChangeTestData()
    op(testData)
    testData.assertValid()
  }
  
  def assertAsyncOperation(data: ScalaImmutableDataClass1)(op: TestData => Unit) {
    gigaSpace.write(data) // write or update, doesn't really matter for this test
    val testData = new TestData(data)
    op(testData)
    testData.assertValid()
  }
  
  trait TestApi[T] {
    val latch = new CountDownLatch(1)
    val validResult = new AtomicBoolean(false)
    val predicate: AsyncResult[T] => Boolean
    val asyncFutureListener: AsyncResult[T] => Unit = { result: AsyncResult[T] => 
      validResult.set(predicate(result))
      latch.countDown()
    }
    def assertValid() {
      latch.await(5, TimeUnit.SECONDS)
      Assert.assertTrue(validResult.get())
    }
  }
  
  class TestData(written: ScalaImmutableDataClass1) extends TestApi[ScalaImmutableDataClass1] {
    val predicate = { result: AsyncResult[ScalaImmutableDataClass1] => 
      written == result.getResult() 
    }
  }
  
  class ChangeTestData() extends TestApi[ChangeResult[ScalaImmutableDataClassNullValues]] {
    val predicate = { result: AsyncResult[ChangeResult[ScalaImmutableDataClassNullValues]] => 
      result.getResult().getNumberOfChangedEntries() == 1 
    }
  }
  
}