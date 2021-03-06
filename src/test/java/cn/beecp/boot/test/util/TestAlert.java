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
package cn.beecp.boot.test.util;

import cn.beecp.boot.datasource.sqltrace.SqlTraceAlert;
import cn.beecp.boot.datasource.sqltrace.SqlTraceEntry;

import java.util.List;

public class TestAlert extends SqlTraceAlert {
    public void alert(List<SqlTraceEntry> alertList) {
        System.out.println("....test alert siz:" + alertList.size());
    }
}
