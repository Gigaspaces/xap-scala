/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

/**
 * Holders a GigaSpace proxy, the application context and the cluster info for execute operations
 * performed in the REPL.
 * 
 * @see [[org.openspaces.scala.repl.GigaSpacesScalaReplUtils#execute]]
 * @author Dan Kilman
 * @since 9.6
 */
case class ExecutionHolder(
    gigaSpace: GigaSpace, 
    context: ApplicationContext = null, 
    clusterInfo: ClusterInfo = null)

/**
 * Utility methods to simplify work during an REPL session.
 * @author Dan Kilman
 * @since 9.6
 */
object GigaSpacesScalaReplUtils {
  
  /**
   * Helper method to obtain a GigaSpace proxy by name.
   */
  def getGigaSpace(name: String)(implicit admin: Admin): Option[GigaSpace] = {
    val space = admin.getSpaces().waitFor(name, 1, TimeUnit.MILLISECONDS)
    space match {
      case space if space ne null => Option(space.getGigaSpace())
      case _ => None
    }
  }
  
  /**
   * Convenience method to execute tasks on a GigaSpace proxy.
   */
  def execute[T <: Serializable](
      gigaSpace: GigaSpace, 
      routing: Any = null)(
      task: ExecutionHolder => T): AsyncFuture[T] = {
    gigaSpace.execute(new ExecutionHolderTask(task), routing)
  }
  
  private class ExecutionHolderTask[T <: Serializable](task: ExecutionHolder => T)
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