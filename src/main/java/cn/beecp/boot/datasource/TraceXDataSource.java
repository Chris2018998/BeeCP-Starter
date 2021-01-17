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
package cn.beecp.boot.datasource;

import javax.sql.DataSource;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import java.sql.SQLException;

/**
 * trace XADatasource
 *
 * @author Chris.Liao
 */
public class TraceXDataSource extends TraceDataSource implements XADataSource {
    private javax.sql.XADataSource delegate;

    public TraceXDataSource(String dsId, XADataSource delegate, boolean traceSQL, boolean jndiDs) {
        super(dsId, (DataSource) delegate, traceSQL, jndiDs);
        this.delegate = delegate;
    }

    public XAConnection getXAConnection() throws SQLException {
        return delegate.getXAConnection();
    }

    public XAConnection getXAConnection(String user, String password) throws SQLException {
        return delegate.getXAConnection(user, password);
    }
}


