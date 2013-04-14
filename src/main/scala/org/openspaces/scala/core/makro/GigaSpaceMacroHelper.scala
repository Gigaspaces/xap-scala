package org.openspaces.scala.core.makro

import scala.annotation.tailrec
import com.gigaspaces.client.ChangeSet
import com.gigaspaces.client.SpaceProxyOperationModifiers
import com.j_spaces.core.client.SQLQuery
import com.gigaspaces.async.AsyncFutureListener
import com.gigaspaces.client.ChangeResult

abstract class ReadTakeMultipleMacroHelper extends GigaSpaceMacroHelper {
  
  val take: Boolean
  
  override protected def getInvocationParams(sqlQueryTree: c.Tree): List[c.Tree] = {
    import c.universe._
    (maxEntriesOption, modifiersOption) match {
      case (None, None) => List(sqlQueryTree)
      case (Some(maxEntriesExpr), None) => List(sqlQueryTree, maxEntriesExpr.tree)
      case (Some(maxEntriesExpr), Some(modifiersExpr)) => List(sqlQueryTree, maxEntriesExpr.tree, modifiersExpr.tree)
      case _ => throw new IllegalStateException("Illegal method call")
    }
  }
  
  override protected def getInvocationMethodName: String = (if (take) "take" else "read") + "Multiple"
  
}

abstract class ReadTakeMacroHelper extends GigaSpaceMacroHelper {
  
  val ifExists: Boolean
  val take: Boolean
  
  override protected def getInvocationParams(sqlQueryTree: c.Tree): List[c.Tree] = {
    import c.universe._
    (timeoutOption, modifiersOption) match {
      case (None, None) => List(sqlQueryTree)
      case (Some(timeoutExpr), None) => List(sqlQueryTree, timeoutExpr.tree)
      case (Some(timeoutExpr), Some(modifiersExpr)) => List(sqlQueryTree, timeoutExpr.tree, modifiersExpr.tree)
      case _ => throw new IllegalStateException("Illegal method call")
    }
  }
  
  override protected def getInvocationMethodName: String = {
    val prefix = if (take) "take" else "read"
    if (ifExists) prefix + "IfExists" else prefix
  }
}

abstract class CountClearMacroHelper extends GigaSpaceMacroHelper {
  
  val clear: Boolean
  
  override protected def getInvocationParams(sqlQueryTree: c.Tree): List[c.Tree] = {
    import c.universe._
    modifiersOption match {
      case None => List(sqlQueryTree)
      case Some(modifiersExpr) => List(sqlQueryTree, modifiersExpr.tree)
    }
  }
  
  override protected def getInvocationMethodName: String = if (clear) "clear" else "count"
  
}

abstract class ChangeMacroHelper extends GigaSpaceMacroHelper {
  
  val changeSetExpr: c.Expr[ChangeSet]
  
  override protected def getInvocationParams(sqlQueryTree: c.Tree): List[c.Tree] = {
    import c.universe._
    val changeSetTree = changeSetExpr.tree
    (timeoutOption, modifiersOption) match {
      case (None, None) => List(sqlQueryTree, changeSetTree)
      case (Some(timeoutExpr), None) => List(sqlQueryTree, changeSetTree, timeoutExpr.tree)
      case (None, Some(modifiersExpr)) => List(sqlQueryTree, changeSetTree, modifiersExpr.tree)
      case (Some(timeoutExpr), Some(modifiersExpr)) => List(sqlQueryTree, changeSetTree, modifiersExpr.tree, timeoutExpr.tree)
    }
  }
  
  override protected def getInvocationMethodName: String = "change"
  
}

abstract class AsyncReadTakeMacroHelper[T] extends GigaSpaceMacroHelper {
  
  val take: Boolean
  val futureListenerOption: Option[c.Expr[AsyncFutureListener[T]]] = None
  
  override protected def getInvocationParams(sqlQueryTree: c.Tree): List[c.Tree] = {
    import c.universe._
    (timeoutOption, modifiersOption, futureListenerOption) match {
      case (None, None, None) => List(sqlQueryTree)
      case (None, None, Some(listener)) => List(sqlQueryTree, listener.tree)
      case (Some(timeout), None, None) => List(sqlQueryTree, timeout.tree)
      case (Some(timeout), None, Some(listener)) => List(sqlQueryTree, timeout.tree, listener.tree)
      case (Some(timeout), Some(modifiers), None) => List(sqlQueryTree, timeout.tree, modifiers.tree)
      case (Some(timeout), Some(modifiers), Some(listener)) => List(sqlQueryTree, timeout.tree, modifiers.tree, listener.tree)
      case _ => throw new IllegalStateException("Illegal method call")
    }
  }
  
  override protected def getInvocationMethodName: String = "async" + (if (take) "Take" else "Read")
}

abstract class AsyncChangeMacroHelper[T] extends GigaSpaceMacroHelper {
  
  val changeSetExpr: c.Expr[ChangeSet]
  val futureListenerOption: Option[c.Expr[AsyncFutureListener[ChangeResult[T]]]] = None
  
  override protected def getInvocationParams(sqlQueryTree: c.Tree): List[c.Tree] = {
    import c.universe._
    val changeSetTree = changeSetExpr.tree
    (timeoutOption, modifiersOption, futureListenerOption) match {
      case (None, None, None) => List(sqlQueryTree, changeSetTree)
      case (None, None, Some(listener)) => List(sqlQueryTree, changeSetTree, listener.tree)
      case (Some(timeout), None, None) => List(sqlQueryTree, changeSetTree, timeout.tree)
      case (Some(timeout), None, Some(listener)) => List(sqlQueryTree, changeSetTree, timeout.tree, listener.tree)
      case (None, Some(modifiers), None) => List(sqlQueryTree, changeSetTree, modifiers.tree)
      case (None, Some(modifiers), Some(listener)) => List(sqlQueryTree, changeSetTree, modifiers.tree, listener.tree)
      case (Some(timeout), Some(modifiers), None) => List(sqlQueryTree, changeSetTree, modifiers.tree, timeout.tree)
      case (Some(timeout), Some(modifiers), Some(listener)) => List(sqlQueryTree, changeSetTree, modifiers.tree, timeout.tree, listener.tree)
    }
  }
  
  override protected def getInvocationMethodName: String = "asyncChange"
}

abstract class GigaSpaceMacroHelper {
  
  val c: GigaSpaceMacros.TypedContext
  import c.universe._
  
  private val eqOp = "is"
  private val neOp = "is NOT"
  private val likeOp = "like"
  private val notLikeOp = "NOT like"
  private val rlikeOp = "rlike"
 
  protected val timeoutOption: Option[c.Expr[Long]] = None
  protected val modifiersOption: Option[c.Expr[SpaceProxyOperationModifiers]] = None
  protected val maxEntriesOption: Option[c.Expr[Int]] = None
    
  protected def getInvocationParams(sqlQueryTree: c.Tree): List[c.Tree]
  
  protected def getInvocationMethodName: String
  
  def generate[T](predicate: c.Expr[T => Boolean]): c.Tree = {

    val (paramName, typeName, body) = extractParameterNameTypeNameAndApplyTree(predicate)
    
//    c.echo(null, "0: " + showRaw(apply))
//    c.echo(null, "1: " + showRaw(sqlQueryTree))
//    c.echo(null, "2: " + sqlQueryTree.toString)

    body match {
      case Block(statements, rawExpression) => {
        processExpression(paramName, typeName, rawExpression)
      }
      case _ => processExpression(paramName, typeName, body) 
    }
    
 }
 
  private def processExpression[T](paramName: TermName, typeName: String, rawExpression: Tree) = {

    val expression = removeImplicitCalls(rawExpression)

    // build query and parameter list to build new SQLQuery
    val paramsBuffer = collection.mutable.ArrayBuffer[Tree]()
    val query: String = visitExpressionTree(true, paramName.decoded, expression, paramsBuffer).query
    val params = paramsBuffer.map(box) toList
    
    // build String trees for type name and query
    val typeNameStringTree = toStringTree(typeName)
    val queryStringTree = toStringTree(query)

    // build 'new SQLQuery(...)' tree
    val sqlQueryExpr = createPartialSqlQueryTree(typeNameStringTree, queryStringTree)
    val sqlQueryTree = addParametersToSqlQueryTreeAndFixGenericType(sqlQueryExpr.tree, params, typeName)
    
    // build the operation invocation tree and pass the new sqlQueryExpr
    // gigaSpace.read(new SQLQuery(typeName, query, QueryResultType.OBJECT, param1, param2, ..., paramN))
    // final result is something like this:
    createFinalInvocationTree(sqlQueryTree)
  }
  
  private def extractParameterNameTypeNameAndApplyTree(predicate: c.Expr[_]) = {
    val c.Expr(Function(ValDef(_, paramName, tpe, _)::Nil, body)) = predicate
    (paramName, tpe.toString, body)
  }
  
  private def removeImplicitCalls(apply: c.Tree): c.Tree = {
    val implicitRemoverTransformer = new Transformer {
      override val treeCopy = newStrictTreeCopier
      override def transform(t: Tree) = {
        super.transform(t) match {
          // for implicits in final final expression
          case Apply(Select(Apply(
            Select(
              Select(
                Select(
                  Select(
                    Select(
                      Ident(org), 
                      org_openspaces), 
                    org_openspaces_scala), 
                  org_openspaces_scala_core), 
                org_openspaces_scala_core_ScalaGigaSpacesImplicits), 
              _),
            lhs::Nil), operator), rhs::Nil) if 
              org_openspaces_scala_core_ScalaGigaSpacesImplicits.decoded == "ScalaGigaSpacesImplicits" => {
            Apply(Select(lhs, operator), rhs::Nil)
          }
          case other => other
        }
      }
    }
    implicitRemoverTransformer.transform(apply)
  }
  
  private def toStringTree(string: String): c.Tree = c.literal(string).tree
  
  private def createPartialSqlQueryTree(typeNameStringTree: c.Tree, queryStringTree: c.Tree) = {
    reify { 
      new SQLQuery[AnyRef](
        c.Expr[String](typeNameStringTree).splice, 
        c.Expr[String](queryStringTree).splice, 
        com.gigaspaces.query.QueryResultType.OBJECT, 
        "###PLACEHOLDER###"
      )
    }
  }
  
  private def addParametersToSqlQueryTreeAndFixGenericType(
      partialSqlQueryTree: c.Tree, 
      params: List[c.Tree], 
      typeName: String): c.Tree = {
    val sqlQueryTransformer = new Transformer {
      override val treeCopy = newStrictTreeCopier
      override def transform(t: Tree) = {
        super.transform(t) match {
          case Apply(select, typeName::query::resultType::placeHolder::Nil) => {
            Apply(select, typeName::query::resultType::params)
          }
          case Ident(name) if name.decoded == "AnyRef" => {
            createTreeFromTypeName(typeName)
          }

          case other => other
        }
      }
    }
    sqlQueryTransformer.transform(partialSqlQueryTree)
  }
  
  private def createTreeFromTypeName(typeName: String) = {
    @tailrec
    def helper(currentTree: Tree, names: List[String]): Tree = {
      names match {
        case last::Nil => Select(currentTree, newTypeName(last))
        case first::rest => helper(Select(currentTree, newTermName(first)), rest)
      }
    }
    val splitName = typeName.split('.')
    if (splitName.length == 1) {
      Ident(newTypeName(typeName))
    } else {
        val names = splitName.toList
        helper(Ident(names.head), names.tail)
    }
  }
  
  protected def createFinalInvocationTree(sqlQueryTree: c.Tree): c.Tree = {
    val c.Expr(gigaSpaceTree) = reify { c.prefix.splice.gigaSpace }
    Apply(Select(gigaSpaceTree, getInvocationMethodName), getInvocationParams(sqlQueryTree))
  }
  
  case class VisitResult(query: String, isLeaf: Boolean)
  
  private def visitExpressionTree
    (isLhs: Boolean, 
     paramName: String, 
     tree: c.Tree,
     params: collection.mutable.ArrayBuffer[c.Tree],
     op: String = null): VisitResult = {
     
    def processComplexQuery(lhs: Tree, sqlOperator: String, rhs: Tree): VisitResult = {
      
      val lhsVisitResult = visitExpressionTree(true, paramName, lhs, params)
      val rhsVisitResult = visitExpressionTree(false, paramName, rhs, params, op = sqlOperator)
      
      val stringLhs = buildStringFromVisitResult(lhsVisitResult)
      val stringRhs = buildStringFromVisitResult(rhsVisitResult)
        
      VisitResult(stringLhs + " " + sqlOperator + " " + stringRhs, false)
    }
    
    def processLeafQuery: VisitResult = {
      val stringTree = tree.toString
      if (isLhs) {
        if (!stringTree.startsWith(paramName + ".")) {
          throw new Exception(s"Left hand side parameter name must begin with '$paramName' reason: ${stringTree}")
        } else {
          VisitResult(stringTree.substring(paramName.length + 1), true)
        }
      } else {
        if ((op eq eqOp) || (op eq neOp)) {
          if (!("null" == stringTree)) {
            throw new Exception("can only use `eq` or `ne` with `null`")
          }
          VisitResult(stringTree, true)
        } else if ((op eq likeOp) || (op eq notLikeOp) || (op eq rlikeOp)) {
          tree match {
            case Literal(Constant(theString: String)) => VisitResult(s"'${theString}'", true) 
            case _ => throw new Exception(s"'${op}' can only be applied on strings")
          }
        } else {
          params += tree
          VisitResult("?", true)
        }
      }
    }
    
    tree match {
      case Apply(Select(lhs, operator), rhs::Nil) if !convertToSqlOperator(operator).isEmpty => {
        processComplexQuery(lhs, convertToSqlOperator(operator).get, rhs)
      }
      case _ => {
        processLeafQuery
      }
    }
  }
  

  
  private def buildStringFromVisitResult(visitResult: VisitResult): String = {
    var result = visitResult.query
    if (!visitResult.isLeaf)
      result = "( " + result + " )"
    result
  }
  
  private def box(t: c.Tree): c.Tree = {
    Apply(Select(Select(Select(Select(Select(Ident("org"), 
         newTermName("openspaces")), newTermName("core")), 
         newTermName("util")), newTermName("Boxer")), newTermName("box")), List(t))
  }
  
  private def convertToSqlOperator(treeOperator: Name): Option[String] = {
    treeOperator.toString match {
      case "$eq$eq"      => Some("=")
      case "$bang$eq"    => Some("<>")
      case "$greater"    => Some(">")
      case "$greater$eq" => Some(">=")
      case "$less"       => Some("<")
      case "$less$eq"    => Some("<=")
      case "$amp$amp"    => Some("AND")
      case "$bar$bar"    => Some("OR")
      case "eq"          => Some(eqOp)
      case "ne"          => Some(neOp)
      case "like"        => Some(likeOp)
      case "notLike"     => Some(notLikeOp)
      case "rlike"       => Some(rlikeOp)
      case _             => None
    }
  }
 
}