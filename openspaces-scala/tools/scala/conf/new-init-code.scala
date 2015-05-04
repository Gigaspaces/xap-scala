import com.gigaspaces.document.SpaceDocument
import org.openspaces.scala.core.ScalaGigaSpacesImplicits._
import org.openspaces.scala.core.aliases.annotation._
import scala.beans.BeanProperty
import com.gigaspaces.annotation.pojo.SpaceClass.IncludeProperties
import org.openspaces.core.GigaSpace
import org.openspaces.scala.repl._
import org.openspaces.scala.repl.GigaSpacesScalaReplUtils._

implicit val admin = new org.openspaces.admin.AdminFactory().create()