package org.openspaces.scala.repl

import org.openspaces.admin.Admin
import org.openspaces.core.GigaSpace
import java.io.Serializable
import java.util.concurrent.TimeUnit
import org.springframework.context.ApplicationContext
import org.openspaces.core.cluster.ClusterInfo
import com.gigaspaces.async.AsyncFuture
import org.openspaces.core.executor.TaskGigaSpaceAware
import org.springframework.context.ApplicationContextAware
import org.openspaces.core.cluster.ClusterInfoAware
import org.openspaces.core.executor.Task

case class ExecutionHolder(
    gigaSpace: GigaSpace, 
    context: ApplicationContext = null, 
    clusterInfo: ClusterInfo = null)

// Some sample REPL helper methods
object GigaSpacesScalaReplUtils {
  
  def getGigaSpace(name: String)(implicit admin: Admin): Option[GigaSpace] = {
    val space = admin.getSpaces().waitFor(name, 1, TimeUnit.MILLISECONDS)
    space match {
      case space if space ne null => Option(space.getGigaSpace())
      case _ => None
    }
  }
  
  def execute[T <: Serializable](
      gigaSpace: GigaSpace, 
      routing: Any = null)(
      task: ExecutionHolder => T): AsyncFuture[T] = {
    gigaSpace.execute(new ExecutionHolderTask(task), routing)
  }
  
  class ExecutionHolderTask[T <: Serializable](task: ExecutionHolder => T)
      extends Task[T]
      with TaskGigaSpaceAware
      with ApplicationContextAware
      with ClusterInfoAware {
    
    var gigaSpace: GigaSpace = _
    var context: ApplicationContext = _
    var clusterInfo: ClusterInfo = _
    
    override def setGigaSpace(gigaSpace: GigaSpace) {
      this.gigaSpace = gigaSpace
    }
    
    override def setApplicationContext(applicationContext: ApplicationContext) {
      this.context = applicationContext
    }
    
    override def setClusterInfo(clusterInfo: ClusterInfo) {
      this.clusterInfo = clusterInfo
    }
    
    override def execute(): T = task(ExecutionHolder(gigaSpace, context, clusterInfo))
  }
  
}