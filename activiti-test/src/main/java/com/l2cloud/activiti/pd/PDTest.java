package com.l2cloud.activiti.pd;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.Test;

/**
 * @Description: 流程定义 1、部署 2、查询部署 3、查看图片 4、删除部署
 * @author Xu,Jin wind.j.xu@leaptocloud.com
 *
 */
public class PDTest {
    /**
     * 通过classpath路径进行部署
     *   涉及到的表
     *      act_ge_bytearray:
     *        1、英文解释
     *           act:activiti
     *           ge:general
     *           bytearray:二进制
     *        2、字段
     *           name_:文件的路径加上名称
     *           bytes_:存放内容
     *           deployment_id_:部署ID
     *        3、说明：
     *             如果要查询文件(bpmn和png)，需要知道deploymentId
     *      act_re_deployment
     *        1、解析
     *           re:repository
     *           deployment:部署  用户描述一次部署
     *        2、字段
     *            ID_：部署ID  主键
     *      act_re_procdef
     *        1、解释
     *            procdef: process definition  流程定义
     *        2、字段
     *            id_:pdid:pdkey:pdversion:随机数
     *            name:名称
     *            key:名称
     *            version:版本号
     *                如果名称不变，每次部署，版本号加1
     *                如果名称改变，则版本号从1开始计算
     *            deployment_id_:部署ID
     *            
     */
    @Test
    public void testDeployFromClasspath() {
        // 得到流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getRepositoryService().createDeployment().name("请假流程").addClasspathResource("qingjia.bpmn")
                .addClasspathResource("qingjia.png").deploy();
    }

    /**
     * 通过 inputstream完成部署
     */
    @Test
    public void testDeployFromInputStream() {
        InputStream bpmnStream = this.getClass().getClassLoader().getResourceAsStream("qingjia.bpmn");
        // 得到流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getRepositoryService().createDeployment().addInputStream("qingjia.bpmn", bpmnStream).deploy();
    }

    /**
     * 通过zipinputstream完成部署
     */
    @Test
    public void testDeployFromZipinputStream() {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("qingjia.zip");
        ZipInputStream zipInputStream = new ZipInputStream(in);
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getRepositoryService().createDeployment().addZipInputStream(zipInputStream).deploy();
    }

    /**
     * 删除
     */
    @Test
    public void testDelete() {
        // 得到流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getRepositoryService().deleteDeployment("1", true);
    }

    /**
     * 查询流程部署
     */
    @Test
    public void testQueryAllDeployment() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<Deployment> deployments = processEngine.getRepositoryService().createDeploymentQuery()
                .orderByDeploymenTime()// 按照部署时间排序
                .desc()// 按照降序排序
                .list();
        for (Deployment deployment : deployments) {
            System.out.println(deployment.getId());
        }
    }

    /**
     * 根据名称查询流程部署
     */
    @Test
    public void testQueryDeploymentByName() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<Deployment> deployments = processEngine.getRepositoryService().createDeploymentQuery()
                .orderByDeploymenTime()// 按照部署时间排序
                .desc()// 按照降序排序
                .deploymentName("请假流程").list();
        for (Deployment deployment : deployments) {
            System.out.println(deployment.getId());
        }
    }

    /**
     * 查询所有的流程定义
     */
    @Test
    public void testQueryAllPD() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<ProcessDefinition> pdList = processEngine.getRepositoryService().createProcessDefinitionQuery()
                .orderByProcessDefinitionVersion().desc().list();
        for (ProcessDefinition pd : pdList) {
            System.out.println(pd.getVersion());
        }
    }

    /**
     * 查看流程图 根据deploymentId和name
     */
    @Test
    public void testShowImage() throws Exception {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        InputStream inputStream = processEngine.getRepositoryService()
        /**
         * deploymentID 文件的名称和路径
         */
        .getResourceAsStream("501", "qingjia.png");
        OutputStream outputStream3 = new FileOutputStream("e:/processimg.png");
        for (int b = -1; (b = inputStream.read()) != -1;) {
            outputStream3.write(b);
        }
        inputStream.close();
        outputStream3.close();
    }

    /**
     * 根据pdid查看图片
     * 
     * @throws Exception
     */
    @Test
    public void testShowImage2() throws Exception {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        InputStream inputStream = processEngine.getRepositoryService().getProcessDiagram("qingjia1:1:804");
        OutputStream outputStream3 = new FileOutputStream("e:/processimg.png");
        for (int b = -1; (b = inputStream.read()) != -1;) {
            outputStream3.write(b);
        }
        inputStream.close();
        outputStream3.close();
    }

    /**
     * 查看bpmn文件
     */
    @Test
    public void testShowBpmn() throws Exception {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        InputStream inputStream = processEngine.getRepositoryService().getProcessModel("qingjia1:1:804");
        OutputStream outputStream3 = new FileOutputStream("e:/processimg.bpmn");
        for (int b = -1; (b = inputStream.read()) != -1;) {
            outputStream3.write(b);
        }
        inputStream.close();
        outputStream3.close();
    }
}
