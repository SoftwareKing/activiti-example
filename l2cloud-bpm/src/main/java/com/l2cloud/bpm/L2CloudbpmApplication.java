package com.l2cloud.bpm;

import javax.sql.DataSource;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:L2CloudbpmApplication
 * @author Xu,Jin wind.j.xu@leaptocloud.com
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class L2CloudbpmApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(L2CloudbpmApplication.class, args);
    }

    @Bean
    public DataSource database() {
        return DataSourceBuilder.create().url("jdbc:mysql://127.0.0.1:3306/activititest?characterEncoding=UTF-8")
                .username("root").password("root").driverClassName("com.mysql.jdbc.Driver").build();
    }

    @Bean
    public CommandLineRunner init(final RepositoryService repositoryService, final RuntimeService runtimeService,
            final TaskService taskService) {

        return new CommandLineRunner() {
            public void run(String... strings) throws Exception {
                System.out.println("Number of process definitions : "
                        + repositoryService.createProcessDefinitionQuery().count());
                System.out.println("Number of tasks : " + taskService.createTaskQuery().count());
                runtimeService.startProcessInstanceByKey("oneTaskProcess");
                System.out.println("Number of tasks after process start: " + taskService.createTaskQuery().count());
            }
        };

    }

}
