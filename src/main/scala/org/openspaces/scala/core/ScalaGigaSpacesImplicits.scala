package org.openspaces.scala.core

import java.io.Serializable

import org.openspaces.core.GigaSpace
import org.openspaces.core.executor.DistributedTask
import org.openspaces.core.executor.Task
import org.openspaces.core.executor.TaskGigaSpaceAware

import com.gigaspaces.async.AsyncFuture
import com.gigaspaces.async.AsyncFutureListener
import com.gigaspaces.async.AsyncResult

object ScalaGigaSpacesImplicits {

  implicit class ScalaAsyncFutureListener[T](asyncFutureListener: AsyncResult[T] => Unit) 
    extends AsyncFutureListener[T] {
    override def onResult(result: AsyncResult[T]) {
      asyncFutureListener(result)
    }
  }
  
  implicit class ScalaEnhancedGigaSpaceWrapper(val gigaSpace: GigaSpace) {
    
    def execute[T <: Serializable, R](
      mapper: GigaSpace => T,
      reducer: Seq[AsyncResult[T]] => R): AsyncFuture[R] = {
      gigaSpace.execute(new ScalaDistributedTask(mapper, reducer))
    }
    
    def execute[T <: Serializable, R](
      mapper: GigaSpace => T,
      reducer: Seq[AsyncResult[T]] => R,
      routing: AnyRef*): AsyncFuture[R] = {
      gigaSpace.execute(new ScalaDistributedTask(mapper, reducer), routing:_*)
    }
    
    def execute[T <: Serializable](mapper: GigaSpace => T): AsyncFuture[T] = {
      gigaSpace.execute(new ScalaTask(mapper))
    }
    
    def execute[T <: Serializable](
        mapper: GigaSpace => T,
        routing: AnyRef): AsyncFuture[T] = {
      gigaSpace.execute(new ScalaTask(mapper), routing)
    }
    
    def execute[T <: Serializable](
        mapper: GigaSpace => T,
        routing: AnyRef,
        asyncFutureListener: AsyncResult[T] => Unit): AsyncFuture[T] = {
      gigaSpace.execute(new ScalaTask(mapper), routing, new ScalaAsyncFutureListener(asyncFutureListener))
    }
    
  }
  
}

class ScalaTask[T <: Serializable](
    mapper: GigaSpace => T)
  extends Task[T]
  with TaskGigaSpaceAware {
  
  var colocatedGigaSpace: GigaSpace = _
  
  override def setGigaSpace(colocatedGigaSpace: GigaSpace) {
    this.colocatedGigaSpace = colocatedGigaSpace
  }
  
  override def execute(): T = mapper(colocatedGigaSpace)
}

class ScalaDistributedTask[T <: Serializable, R](
    mapper: GigaSpace => T,
    reducer: Seq[AsyncResult[T]] => R)
  extends ScalaTask[T](mapper)
  with DistributedTask[T, R] {
  
  override def reduce(results: java.util.List[AsyncResult[T]]): R = 
    reducer(scala.collection.JavaConversions.asScalaBuffer(results))
  
}

