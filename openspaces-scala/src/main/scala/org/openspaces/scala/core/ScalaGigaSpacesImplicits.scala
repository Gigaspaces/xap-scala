package org.openspaces.scala.core

import language.experimental.macros
import java.io.Serializable
import org.openspaces.core.GigaSpace
import org.openspaces.core.executor.DistributedTask
import org.openspaces.core.executor.Task
import org.openspaces.core.executor.TaskGigaSpaceAware
import com.gigaspaces.async.AsyncFuture
import com.gigaspaces.async.AsyncFutureListener
import com.gigaspaces.async.AsyncResult
import org.openspaces.scala.core.makro.GigaSpaceMacros
import com.gigaspaces.client.ReadModifiers
import com.gigaspaces.client.TakeModifiers
import com.gigaspaces.client.CountModifiers
import com.gigaspaces.client.ClearModifiers
import com.gigaspaces.client.ChangeSet
import com.gigaspaces.client.ChangeModifiers
import com.gigaspaces.client.ChangeResult
import java.util.concurrent.Future
import com.gigaspaces.query.ISpaceQuery

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
    
    def predicate = new GigaSpaceMacroPredicateWrapper(gigaSpace)
    
  }
  
  implicit class QueryMacroStringImplicits(value: String) {
    def like    (regex: String): Boolean = ???
    def notLike (regex: String): Boolean = ???
    def rlike   (regex: String): Boolean = ???
  }
  
  implicit class QueryMacroDateImplicits(date: java.util.Date) extends Ordered[java.util.Date] {
    def compare(anotherDate: java.util.Date): Int = ???
  }
  
}

class GigaSpaceMacroPredicateWrapper(val gigaSpace: GigaSpace) {
  
  def read[T](predicate: T => Boolean): T = 
    macro GigaSpaceMacros.read_impl[T]
  
  def read[T](predicate: T => Boolean, timeout: Long): T = 
    macro GigaSpaceMacros.readWithTimeout_impl[T]
  
  def read[T](predicate: T => Boolean, timeout: Long, modifiers: ReadModifiers): T = 
    macro GigaSpaceMacros.readWithTimeoutAndModifiers_impl[T]

  def readIfExists[T](predicate: T => Boolean): T = 
    macro GigaSpaceMacros.readIfExists_impl[T]
  
  def readIfExists[T](predicate: T => Boolean, timeout: Long): T = 
    macro GigaSpaceMacros.readIfExistsWithTimeout_impl[T]
  
  def readIfExists[T](predicate: T => Boolean, timeout: Long, modifiers: ReadModifiers): T = 
    macro GigaSpaceMacros.readIfExistsWithTimeoutAndModifiers_impl[T]

  def take[T](predicate: T => Boolean): T = 
    macro GigaSpaceMacros.take_impl[T]
  
  def take[T](predicate: T => Boolean, timeout: Long): T = 
    macro GigaSpaceMacros.takeWithTimeout_impl[T]
  
  def take[T](predicate: T => Boolean, timeout: Long, modifiers: TakeModifiers): T = 
    macro GigaSpaceMacros.takeWithTimeoutAndModifiers_impl[T]

  def takeIfExists[T](predicate: T => Boolean): T = 
    macro GigaSpaceMacros.takeIfExists_impl[T]
  
  def takeIfExists[T](predicate: T => Boolean, timeout: Long): T = 
    macro GigaSpaceMacros.takeIfExistsWithTimeout_impl[T]
  
  def takeIfExists[T](predicate: T => Boolean, timeout: Long, modifiers: TakeModifiers): T = 
    macro GigaSpaceMacros.takeIfExistsWithTimeoutAndModifiers_impl[T]
  
  def count[T](predicate: T => Boolean): Int =
    macro GigaSpaceMacros.count_impl[T]
  
  def count[T](predicate: T => Boolean, modifiers: CountModifiers): Int =
    macro GigaSpaceMacros.countWithModifiers_impl[T]

  def clear[T](predicate: T => Boolean): Unit =
    macro GigaSpaceMacros.clear_impl[T]
  
  def clear[T](predicate: T => Boolean, modifiers: ClearModifiers): Int =
    macro GigaSpaceMacros.clearWithModifiers_impl[T]
  
  def readMultiple[T](predicate: T => Boolean): Array[T] =
    macro GigaSpaceMacros.readMultiple_impl[T]
  
  def readMultiple[T](predicate: T => Boolean, maxEntries: Int): Array[T] =
    macro GigaSpaceMacros.readMultipleWithMaxEntries_impl[T]
  
  def readMultiple[T](predicate: T => Boolean, maxEntries: Int, modifiers: ReadModifiers): Array[T] =
    macro GigaSpaceMacros.readMultipleWithMaxEntriesAndModifiers_impl[T]

  def takeMultiple[T](predicate: T => Boolean): Array[T] =
    macro GigaSpaceMacros.takeMultiple_impl[T]
  
  def takeMultiple[T](predicate: T => Boolean, maxEntries: Int): Array[T] =
    macro GigaSpaceMacros.takeMultipleWithMaxEntries_impl[T]
  
  def takeMultiple[T](predicate: T => Boolean, maxEntries: Int, modifiers: TakeModifiers): Array[T] =
    macro GigaSpaceMacros.takeMultipleWithMaxEntriesAndModifiers_impl[T]
  
  def change[T](predicate: T => Boolean, changeSet: ChangeSet): ChangeResult[T] = 
    macro GigaSpaceMacros.change_impl[T]
  
  def change[T](predicate: T => Boolean, changeSet: ChangeSet, timeout: Long): ChangeResult[T] = 
    macro GigaSpaceMacros.changeWithTimeout_impl[T]

  def change[T](predicate: T => Boolean, changeSet: ChangeSet, modifiers: ChangeModifiers): ChangeResult[T] = 
    macro GigaSpaceMacros.changeWithModifiers_impl[T]
  
  def change[T](predicate: T => Boolean, changeSet: ChangeSet, modifiers: ChangeModifiers, timeout: Long): ChangeResult[T] = 
    macro GigaSpaceMacros.changeWithModifiersAndTimeout_impl[T]
  
  def asyncRead[T](predicate: T => Boolean): AsyncFuture[T] =
    macro GigaSpaceMacros.asyncRead_impl[T]
  
  def asyncRead[T](predicate: T => Boolean, timeout: Long): AsyncFuture[T] = 
    macro GigaSpaceMacros.asyncReadWithTimeout_impl[T]
  
  def asyncRead[T](predicate: T => Boolean, timeout: Long, modifiers: ReadModifiers): AsyncFuture[T] = 
    macro GigaSpaceMacros.asyncReadWithTimeoutAndModifiers_impl[T]

  def asyncRead[T](predicate: T => Boolean, listener: AsyncFutureListener[T]): AsyncFuture[T] =
    macro GigaSpaceMacros.asyncReadWithListener_impl[T]
  
  def asyncRead[T](predicate: T => Boolean, timeout: Long, listener: AsyncFutureListener[T]): AsyncFuture[T] = 
    macro GigaSpaceMacros.asyncReadWithTimeoutAndListener_impl[T]
  
  def asyncRead[T](predicate: T => Boolean, timeout: Long, modifiers: ReadModifiers, listener: AsyncFutureListener[T]): AsyncFuture[T] = 
    macro GigaSpaceMacros.asyncReadWithTimeoutAndModifiersAndListener_impl[T]

  def asyncTake[T](predicate: T => Boolean): AsyncFuture[T] =
    macro GigaSpaceMacros.asyncTake_impl[T]
  
  def asyncTake[T](predicate: T => Boolean, timeout: Long): AsyncFuture[T] = 
    macro GigaSpaceMacros.asyncTakeWithTimeout_impl[T]
  
  def asyncTake[T](predicate: T => Boolean, timeout: Long, modifiers: TakeModifiers): AsyncFuture[T] = 
    macro GigaSpaceMacros.asyncTakeWithTimeoutAndModifiers_impl[T]

  def asyncTake[T](predicate: T => Boolean, listener: AsyncFutureListener[T]): AsyncFuture[T] =
    macro GigaSpaceMacros.asyncTakeWithListener_impl[T]
  
  def asyncTake[T](predicate: T => Boolean, timeout: Long, listener: AsyncFutureListener[T]): AsyncFuture[T] = 
    macro GigaSpaceMacros.asyncTakeWithTimeoutAndListener_impl[T]
  
  def asyncTake[T](predicate: T => Boolean, timeout: Long, modifiers: TakeModifiers, listener: AsyncFutureListener[T]): AsyncFuture[T] = 
    macro GigaSpaceMacros.asyncTakeWithTimeoutAndModifiersAndListener_impl[T]

  def asyncChange[T](predicate: T => Boolean, changeSet: ChangeSet): Future[ChangeResult[T]] = 
    macro GigaSpaceMacros.asyncChange_impl[T]
  
  def asyncChange[T](predicate: T => Boolean, changeSet: ChangeSet, timeout: Long): Future[ChangeResult[T]] = 
    macro GigaSpaceMacros.asyncChangeWithTimeout_impl[T]

  def asyncChange[T](predicate: T => Boolean, changeSet: ChangeSet, modifiers: ChangeModifiers): Future[ChangeResult[T]] = 
    macro GigaSpaceMacros.asyncChangeWithModifiers_impl[T]
  
  def asyncChange[T](predicate: T => Boolean, changeSet: ChangeSet, modifiers: ChangeModifiers, timeout: Long): Future[ChangeResult[T]] = 
    macro GigaSpaceMacros.asyncChangeWithModifiersAndTimeout_impl[T]

  def asyncChange[T](predicate: T => Boolean, changeSet: ChangeSet, listener: AsyncFutureListener[ChangeResult[T]]): Future[ChangeResult[T]] = 
    macro GigaSpaceMacros.asyncChangeWithListener_impl[T]
  
  def asyncChange[T](predicate: T => Boolean, changeSet: ChangeSet, timeout: Long, listener: AsyncFutureListener[ChangeResult[T]]): Future[ChangeResult[T]] = 
    macro GigaSpaceMacros.asyncChangeWithTimeoutAndListener_impl[T]

  def asyncChange[T](predicate: T => Boolean, changeSet: ChangeSet, modifiers: ChangeModifiers, listener: AsyncFutureListener[ChangeResult[T]]): Future[ChangeResult[T]] = 
    macro GigaSpaceMacros.asyncChangeWithModifiersAndListener_impl[T]
  
  def asyncChange[T](predicate: T => Boolean, changeSet: ChangeSet, modifiers: ChangeModifiers, timeout: Long, listener: AsyncFutureListener[ChangeResult[T]]): Future[ChangeResult[T]] = 
    macro GigaSpaceMacros.asyncChangeWithModifiersAndTimeoutAndListener_impl[T]
  
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

