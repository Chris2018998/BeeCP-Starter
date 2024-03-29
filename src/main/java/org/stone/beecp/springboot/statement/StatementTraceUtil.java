/*
 * Copyright Chris2018998
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.stone.beecp.springboot.statement;

import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Statement;

/**
 * @author Chris Liao
 */
public class StatementTraceUtil {
    private static final Class[] INTF_Connection = new Class[]{Connection.class};
    private static final Class[] INTF_CallableStatement = new Class[]{CallableStatement.class};
    private static final ClassLoader classLoader = StatementTraceUtil.class.getClassLoader();

    public static Connection createConnection(Connection delegate, String dsId, String dsUUID) {
        return (Connection) Proxy.newProxyInstance(
                classLoader,
                INTF_Connection,
                new ConnectionHandler(delegate, dsId, dsUUID));
    }

    static Statement createStatementProxy(Statement delegate, String statementType, String dsId, String dsUUID, StatementTrace trace) {
        return (Statement) Proxy.newProxyInstance(
                classLoader,
                INTF_CallableStatement,
                new StatementHandler(delegate, statementType, dsId, dsUUID, trace));
    }
}
