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
package cn.beecp.boot.datasource.sqltrace;

import java.util.Date;

import static cn.beecp.boot.datasource.SpringBootDataSourceUtil.formatDate;

/*
 *  SQL Execute Trace entry
 *
 *  @author Chris.Liao
 */
public class SqlTraceEntry {
    private String sql;
    private String dsId;
    private String statementType;
    private String execStartTime;
    private long execStartTimeMs;

    private String execEndTime;
    private long execTookTimeMs;
    private long traceStartTime;

    private boolean execInd;
    private boolean execSlowInd;
    private boolean execSuccessInd;
    private boolean alertInd;
    private Throwable failCause;
    private String methodName;

    SqlTraceEntry(String dsId, String sql, String statementType) {
        this.sql = sql;
        this.dsId = dsId;
        this.statementType = statementType;

        Date startTime = new Date();
        this.execStartTimeMs = startTime.getTime();
        this.execStartTime = formatDate(startTime);
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getDsId() {
        return dsId;
    }

    public void setDsId(String dsId) {
        this.dsId = dsId;
    }

    public String getStatementType() {
        return statementType;
    }

    public void setStatementType(String statementType) {
        this.statementType = statementType;
    }

    public String getExecStartTime() {
        return execStartTime;
    }

    public void setExecStartTime(String execStartTime) {
        this.execStartTime = execStartTime;
    }

    public long getExecStartTimeMs() {
        return execStartTimeMs;
    }

    public void setExecStartTimeMs(long execStartTimeMs) {
        this.execStartTimeMs = execStartTimeMs;
    }

    public String getExecEndTime() {
        return execEndTime;
    }

    public void setExecEndTime(String execEndTime) {
        this.execEndTime = execEndTime;
    }

    public long getExecTookTimeMs() {
        return execTookTimeMs;
    }

    public void setExecTookTimeMs(long execTookTimeMs) {
        this.execTookTimeMs = execTookTimeMs;
    }

    public long getTraceStartTime() {
        return traceStartTime;
    }

    public void setTraceStartTime(long traceStartTime) {
        this.traceStartTime = traceStartTime;
    }

    public boolean isExecInd() {
        return execInd;
    }

    public void setExecInd(boolean execInd) {
        this.execInd = execInd;
    }

    public boolean isExecSuccessInd() {
        return execSuccessInd;
    }

    public void setExecSuccessInd(boolean execSuccessInd) {
        this.execSuccessInd = execSuccessInd;
    }

    public boolean isExecSlowInd() {
        return execSlowInd;
    }

    public void setExecSlowInd(boolean execSlowInd) {
        this.execSlowInd = execSlowInd;
    }

    public boolean isAlertInd() {
        return alertInd;
    }

    public void setAlertInd(boolean alertInd) {
        this.alertInd = alertInd;
    }

    public Throwable getFailCause() {
        return failCause;
    }

    public void setFailCause(Throwable failCause) {
        this.failCause = failCause;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String toString() {
        return sql;
    }
}
