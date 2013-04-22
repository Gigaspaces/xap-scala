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
