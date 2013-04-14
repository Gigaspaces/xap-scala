package org.openspaces.scala.core.makro

import org.mockito.Matchers._
import org.mockito.Mockito._
import org.openspaces.core.GigaSpace
import org.openspaces.scala.core.ScalaGigaSpacesImplicits._
import com.j_spaces.core.client.SQLQuery
import org.junit.Test
import com.gigaspaces.query.QueryResultType
import com.gigaspaces.query.ISpaceQuery
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock
import com.j_spaces.jdbc.parser.grammar.SqlParser
import java.io.ByteArrayInputStream
import java.util.Date

class GigaSpaceMacrosQueriesTest {

  val gigaSpace: GigaSpace = mock(classOf[GigaSpace])
  val date = new Date(1)
  
  @Test
  def testQueries() {

    // compile each query to ensure its valid. otherwise an exception will be thrown
    when(gigaSpace.read(any(classOf[ISpaceQuery[_]]))).thenAnswer(new Answer[Person]() {
      def answer(invocation: InvocationOnMock): Person = {
        val sqlQuery = invocation.getArguments()(0).asInstanceOf[SQLQuery[_]]
        val sqlParser = new SqlParser(new ByteArrayInputStream(sqlQuery.toString().getBytes()))
        sqlParser.parseStatement()
        null
      }
    })
    
    val pGigaSpace = gigaSpace.predicate
    
    // test =
    pGigaSpace.read { p: Person => p.name == "john" }
    verify(gigaSpace).read(new SQLQuery(classOf[Person], "name = ?", "john"))

    // test <>
    pGigaSpace.read { p: Person => p.name != "john" }
    verify(gigaSpace).read(new SQLQuery(classOf[Person], "name <> ?", "john"))

    // test >
    pGigaSpace.read { p: Person => p.age > 10 }
    verify(gigaSpace).read(new SQLQuery(classOf[Person], "age > ?", 10: java.lang.Integer))

    // test >=
    pGigaSpace.read { p: Person => p.age >= 10 }
    verify(gigaSpace).read(new SQLQuery(classOf[Person], "age >= ?", 10: java.lang.Integer))
 
    // test <
    pGigaSpace.read { p: Person => p.age < 10 }
    verify(gigaSpace).read(new SQLQuery(classOf[Person], "age < ?", 10: java.lang.Integer))

    // test <=
    pGigaSpace.read { p: Person => p.age <= 10 }
    verify(gigaSpace).read(new SQLQuery(classOf[Person], "age <= ?", 10: java.lang.Integer))
    
    // test AND
    pGigaSpace.read { p: Person => p.age < 100 && p.age > 10 }
    verify(gigaSpace).read(new SQLQuery(classOf[Person], "( age < ? ) AND ( age > ? )", 100: java.lang.Integer, 10: java.lang.Integer))
    
    // test OR
    pGigaSpace.read { p: Person => p.age > 100 || p.age < 10 }
    verify(gigaSpace).read(new SQLQuery(classOf[Person], "( age > ? ) OR ( age < ? )", 100: java.lang.Integer, 10: java.lang.Integer))
  
    // test is null
    pGigaSpace.read { p: Person => p.name eq null }
    verify(gigaSpace).read(new SQLQuery(classOf[Person], "name is null", QueryResultType.OBJECT))

    // test is NOT null
    pGigaSpace.read { p: Person => p.name ne null }
    verify(gigaSpace).read(new SQLQuery(classOf[Person], "name is NOT null", QueryResultType.OBJECT))
    
    // test like
    pGigaSpace.read { p: Person => p.name like "%A" }
    verify(gigaSpace).read(new SQLQuery(classOf[Person], "name like '%A'", QueryResultType.OBJECT))
    
    // test NOT like
    pGigaSpace.read { p: Person => p.name notLike "%A" }
    verify(gigaSpace).read(new SQLQuery(classOf[Person], "name NOT like '%A'", QueryResultType.OBJECT))
    
    // test rlike
    pGigaSpace.read { p: Person => p.name rlike "a.*" }
    verify(gigaSpace).read(new SQLQuery(classOf[Person], "name rlike 'a.*'", QueryResultType.OBJECT))
    
    // test nested query
    pGigaSpace.read { p: Person => p.son.name == "john" }
    verify(gigaSpace).read(new SQLQuery(classOf[Person], "son.name = ?", "john"))
    
    // test date <
    pGigaSpace.read { p: Person => p.birthday < date }
    verify(gigaSpace).read(new SQLQuery(classOf[Person], "birthday < ?", date))

    // test date >
    pGigaSpace.read { p: Person => p.birthday > date }
    verify(gigaSpace).read(new SQLQuery(classOf[Person], "birthday > ?", date))

    // test date <
    pGigaSpace.read { p: Person => p.birthday <= date }
    verify(gigaSpace).read(new SQLQuery(classOf[Person], "birthday <= ?", date))

    // test date >
    pGigaSpace.read { p: Person => p.birthday >= date }
    verify(gigaSpace).read(new SQLQuery(classOf[Person], "birthday >= ?", date))
    
    // test object instantiation in RHS
    pGigaSpace.read { p: Person => p.birthday == new Date(1) }
    verify(gigaSpace).read(new SQLQuery(classOf[Person], "birthday = ?", date))
    
  }
  
}
