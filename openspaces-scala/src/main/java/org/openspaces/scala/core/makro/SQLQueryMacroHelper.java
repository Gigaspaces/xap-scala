package org.openspaces.scala.core.makro;

import com.gigaspaces.query.QueryResultType;
import com.j_spaces.core.client.SQLQuery;

/**
 * Helper class written in java that deals with the differences between scala and java varargs.
 * 
 * @since 9.6
 * @author Dan Kilman
 *
 */
public class SQLQueryMacroHelper
{

    /**
     * Delegates the instance creation to the matching SQLQuery constructor.
     * The difference here is that we pass an array instead of calling the varargs method.
     */
    public static <T> SQLQuery<T> newQuery(String typeName, String sqlExpression, QueryResultType queryResultType, Object[] parameters)
    {
        return new SQLQuery<T>(typeName, sqlExpression, queryResultType, parameters);
    }
    
}
