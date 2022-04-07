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
package cn.beecp.boot.test.controller;

import cn.beecp.boot.DsId;
import cn.beecp.boot.EnableDsMonitor;
import cn.beecp.boot.EnableMultiDs;
import cn.beecp.boot.datasource.factory.SpringBootDataSourceException;
import cn.beecp.boot.test.util.TestUtil;
import cn.beecp.pool.PoolStaticCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

/*
 *  DataSource Tester Controller
 *
 *  @author Chris.Liao
 */
@EnableMultiDs
@EnableDsMonitor
@SpringBootApplication
@RestController
public class MultiDsController {
    @Autowired
    @Qualifier("ds1")
    private DataSource ds1;
    @Autowired
    @Qualifier("ds2")
    private DataSource ds2;
    @Autowired
    @Qualifier("combineDs")
    private DataSource combineDs;

    @GetMapping("/testGetConnection")
    public String testGetConnection(String dsId) throws Exception {
        if (PoolStaticCenter.isBlank(dsId))
            throw new SpringBootDataSourceException("DataSource Id cant't be null or empty");
        if (!"ds1".equals(dsId) && !"ds2".equals(dsId))
            throw new SpringBootDataSourceException("DataSource Id must be one of list(ds1,ds2)");

        DataSource ds = "ds1".equals(dsId) ? ds1 : ds2;
        return TestUtil.testGetConnection(ds);
    }


    @GetMapping("/testSQL")
    public String testSQL(String dsId, String sql, String type, String slowInd) throws Exception {
        if (PoolStaticCenter.isBlank(dsId))
            throw new SpringBootDataSourceException("DataSource Id cant't be null or empty");
        if (!"ds1".equals(dsId) && !"ds2".equals(dsId))
            throw new SpringBootDataSourceException("DataSource Id must be one of list(ds1,ds2)");
        if (PoolStaticCenter.isBlank(sql))
            throw new SpringBootDataSourceException("Execute SQL can't be null or empty");
        if (PoolStaticCenter.isBlank(type)) throw new SpringBootDataSourceException("Execute type't be null or empty");
        if (!"Statement".equalsIgnoreCase(type) && !"PreparedStatement".equalsIgnoreCase(type) && !"CallableStatement".equalsIgnoreCase(type))
            throw new SpringBootDataSourceException("Execute type must be one of list(Statement,PreparedStatement,CallableStatement)");

        DataSource ds = "ds1".equals(dsId) ? ds1 : ds2;
        return TestUtil.testSQL(ds, sql, type, slowInd);
    }

    @GetMapping("/testGetConnection1")
    @DsId("ds1")
    public String testCombineDs1() throws Exception {
        return TestUtil.testGetConnection(combineDs);
    }

    @GetMapping("/testGetConnection2")
    @DsId("ds2")
    public String testCombineDs2() throws Exception {
        return TestUtil.testGetConnection(combineDs);
    }

    @GetMapping("/testExecSQL1")
    @DsId("ds1")
    public String testExecSQL1(String sql, String type, String slowInd) throws Exception {
        return TestUtil.testSQL(combineDs, sql, type, slowInd);
    }

    @GetMapping("/testExecSQL2")
    @DsId("ds2")
    public String testExecSQL2(String sql, String type, String slowInd) throws Exception {
        return TestUtil.testSQL(combineDs, sql, type, slowInd);
    }
}