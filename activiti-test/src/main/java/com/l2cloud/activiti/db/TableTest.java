package com.l2cloud.activiti.db;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;

/**
 * @Description:activiti创建表的测试
 * @author Xu,Jin wind.j.xu@leaptocloud.com
 */
public class TableTest {

    // 通过配置文件生成表
    @Test
    public void testCreateTable() {
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource(
                "activiti.cfg.xml").buildProcessEngine();
    }

    // 直接生成表
    @Test
    public void firstCreateTable() {
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration
                .createStandaloneProcessEngineConfiguration();
        configuration.setJdbcDriver("com.mysql.jdbc.Driver");
        configuration.setJdbcUrl("jdbc:mysql://localhost:3306/activititest?useUnicode=true&amp;characterEncoding=utf8");
        configuration.setJdbcUsername("root");
        configuration.setJdbcPassword("root");
        /**
         * ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE
         * **/
        configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        ProcessEngine processEngine = configuration.buildProcessEngine();
        System.out.println(processEngine);
    }

}
