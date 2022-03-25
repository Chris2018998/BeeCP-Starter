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

import cn.beecp.boot.datasource.factory.SpringBootDataSourceException;
import cn.beecp.pool.PoolStaticCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.*;

import static cn.beecp.boot.datasource.SpringBootDataSourceUtil.*;

/*
 *  SpringBoot dataSource config demo
 *
 *  spring.datasource.sqlExecutionTrace=true
 *  spring.datasource.sqlExecutionTraceTimeout=18000
 *
 *  spring.datasource.ds-ids=ds1,ds2
 *  spring.datasource.ds1.datasourceType=cn.beecp.BeeDataSoruce
 *  spring.datasource.ds1.propertySetFactory=cn.beecp.boot.BeeDsAttributeSetFactory
 *  spring.datasource.ds1.primary=true
 *  spring.datasource.ds1.attributeX=xxxx
 *
 *  spring.datasource.ds2.primary=false
 *  spring.datasource.ds2.jndiName=PlatformJndi
 *
 *   @author Chris.Liao
 */
public class MultiDataSourceRegister extends SingleDataSourceRegister implements EnvironmentAware, ImportBeanDefinitionRegistrar {
    //logger
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    //springboot environment
    private Environment environment;

    /**
     * Read dataSource configuration from environment and create DataSource
     *
     * @param environment SpringBoot Environment
     */
    public final void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * Read dataSource configuration from environment and create DataSource
     *
     * @param classMetadata Annotation use class meta
     * @param registry      springboot bean definition registry factory
     */
    public final void registerBeanDefinitions(AnnotationMetadata classMetadata,
                                              BeanDefinitionRegistry registry) {

        //1:read multi-dataSource id list
        List<String> dsIdList = this.getIdList(environment, registry);

        //2:read combine-ds config
        Properties combineProperties = getCombineInfo(dsIdList, environment, registry);

        //3:config sql-trace pool
        boolean isSqlTrace = this.configSqlTracePool(environment);

        //4:create dataSources by id list
        Map<String, DataSourceHolder> dsMap = this.createDataSources(dsIdList, environment, isSqlTrace);

        //5:register datasource to spring container
        this.registerDataSources(dsMap, combineProperties, isSqlTrace, registry);

    }


    /**
     * 1: get datasource config id list
     *
     * @param environment springboot environment
     * @param registry    springboot registry
     * @return datasource name list
     */
    private List<String> getIdList(Environment environment, BeanDefinitionRegistry registry) {
        String dsIdsText = getConfigValue(environment, SP_DS_Prefix, SP_DS_Id);
        if (PoolStaticCenter.isBlank(dsIdsText))
            throw new SpringBootDataSourceException("Missed or not found config item:" + SP_DS_Prefix + "." + SP_DS_Id);

        String[] dsIds = dsIdsText.trim().split(",");
        ArrayList<String> dsIdList = new ArrayList(dsIds.length);
        for (String id : dsIds) {
            if (PoolStaticCenter.isBlank(id)) continue;

            id = id.trim();
            if (dsIdList.contains(id))
                throw new SpringBootDataSourceException("Duplicated id(" + id + ")in multi-datasource id list");
            if (this.existsBeanDefinition(id, registry))
                throw new SpringBootDataSourceException("DataSource id(" + id + ")has been registered by another bean");

            dsIdList.add(id);
        }
        if (dsIdList.isEmpty())
            throw new SpringBootDataSourceException("Missed or not found config item:" + SP_DS_Prefix + "." + SP_DS_Id);

        return dsIdList;
    }

    /**
     * 2: get combine config info
     *
     * @param dsIdList    datasource name list
     * @param environment springboot environment
     * @return datasource name list
     */
    private Properties getCombineInfo(List<String> dsIdList, Environment environment, BeanDefinitionRegistry registry) {
        String combineId = getConfigValue(environment, SP_DS_Prefix, SP_DS_CombineId);
        String primaryDs = getConfigValue(environment, SP_DS_Prefix, SP_DS_Combine_PrimaryDs);

        combineId = (combineId == null) ? "" : combineId;
        primaryDs = (primaryDs == null) ? "" : primaryDs;

        if (!PoolStaticCenter.isBlank(combineId)) {
            if (dsIdList.contains(combineId))
                throw new SpringBootDataSourceException("Combine-dataSource id (" + combineId + ")can't be in ds-id list");
            if (this.existsBeanDefinition(combineId, registry))
                throw new SpringBootDataSourceException("Combine-dataSource id(" + combineId + ")has been registered by another bean");

            if (PoolStaticCenter.isBlank(primaryDs))
                throw new SpringBootDataSourceException("Missed or not found config item:" + SP_DS_Prefix + "." + SP_DS_Combine_PrimaryDs);
            if (!dsIdList.contains(primaryDs.trim()))
                throw new SpringBootDataSourceException("Combine-primaryDs(" + primaryDs + "not found in ds-id list");
        }

        Properties combineProperties = new Properties();
        combineProperties.put(SP_DS_CombineId, combineId);
        combineProperties.put(SP_DS_Combine_PrimaryDs, primaryDs);
        return combineProperties;
    }

    /**
     * 3: create dataSource by config
     *
     * @param dsIdList    datasource name list
     * @param environment springboot environment
     * @return
     */
    private Map<String, DataSourceHolder> createDataSources(List<String> dsIdList, Environment environment, boolean isSqlTrace) {
        DataSourceBuilder dsBuilder = new DataSourceBuilder();
        Map<String, DataSourceHolder> dsMap = new LinkedHashMap<String, DataSourceHolder>(dsIdList.size());
        try {
            for (String dsId : dsIdList) {
                String dsPrefix = SP_DS_Prefix + "." + dsId;
                String primaryText = getConfigValue(environment, dsPrefix, SP_DS_Primary);
                boolean primary = PoolStaticCenter.isBlank(primaryText) ? false : Boolean.valueOf(primaryText);
                DataSourceHolder ds = dsBuilder.createDataSource(dsId, dsPrefix, environment, isSqlTrace);//create datasource instanc
                ds.setPrimary(primary);
                dsMap.put(dsId, ds);
            }
            return dsMap;
        } catch (Throwable e) {//failed then close all created dataSource
            for (DataSourceHolder ds : dsMap.values())
                ds.close();
            throw new RuntimeException("multi-DataSource created failed", e);
        }
    }

    /**
     * 4: register datasource to springBoot
     *
     * @param dsMap datasource list
     */
    private void registerDataSources(Map<String, DataSourceHolder> dsMap, Properties combineProperties, boolean isSqlTrace, BeanDefinitionRegistry registry) {
        String combineId = combineProperties.getProperty(SP_DS_CombineId);
        String primaryDsId = combineProperties.getProperty(SP_DS_Combine_PrimaryDs);

        for (DataSourceHolder regInfo : dsMap.values())
            registerDataSourceBean(regInfo, isSqlTrace, registry);

        //register combine DataSource
        if (!PoolStaticCenter.isBlank(combineId) && !PoolStaticCenter.isBlank(primaryDsId)) {
            CombineDataSource combineDataSource = new CombineDataSource(primaryDsId);
            GenericBeanDefinition define = new GenericBeanDefinition();
            define.setBeanClass(combineDataSource.getClass());
            define.setInstanceSupplier(createSupplier(combineDataSource));
            registry.registerBeanDefinition(combineId, define);
            log.info("Registered Combine-DataSource({})with id:{}", define.getBeanClassName(), combineId);

            String dsIdSetterId = DataSourceIdSetter.class.getName();
            GenericBeanDefinition dsIdSetDefine = new GenericBeanDefinition();
            dsIdSetDefine.setBeanClass(DataSourceIdSetter.class);
            dsIdSetDefine.setInstanceSupplier(createSupplier(new DataSourceIdSetter()));
            registry.registerBeanDefinition(dsIdSetterId, dsIdSetDefine);
            log.info("Registered DsId-setter({})with id:{}", dsIdSetDefine.getBeanClassName(), dsIdSetterId);
        }
    }

    //4.1:register dataSource to Spring bean container
    private void registerDataSourceBean(DataSourceHolder regInfo, boolean traceSQL, BeanDefinitionRegistry registry) {
        SpringRegDataSource ds = regInfo.getDs();
        GenericBeanDefinition define = new GenericBeanDefinition();
        define.setPrimary(regInfo.isPrimary());
        define.setBeanClass(ds.getClass());
        define.setInstanceSupplier(createSupplier(ds));
        registry.registerBeanDefinition(regInfo.getDsId(), define);
        log.info("Registered DataSource({})with id:{}", define.getBeanClassName(), regInfo.getDsId());
        SpringDataSourceRegMap.getInstance().addDataSource(ds);
    }

    private boolean existsBeanDefinition(String beanName, BeanDefinitionRegistry registry) {
        try {
            return registry.getBeanDefinition(beanName) != null;
        } catch (NoSuchBeanDefinitionException e) {
            return false;
        }
    }
}

