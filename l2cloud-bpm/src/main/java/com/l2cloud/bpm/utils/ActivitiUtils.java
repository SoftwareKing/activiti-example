package com.l2cloud.bpm.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description:ActivitiUtils
 * @author Xu,Jin wind.j.xu@leaptocloud.com
 */
@Component("activitiUtils")
public class ActivitiUtils {

    @Autowired
    private ProcessEngine processEngine;

    /**
     * @Description:部署流程
     * @param: file 文件上传的内容
     * @param: processName 流程名称
     * */
    public void deploy(File file, String processName) throws Exception {
        InputStream in = new FileInputStream(file);
        ZipInputStream zipInputStream = new ZipInputStream(in);
        this.processEngine.getRepositoryService().createDeployment().name(processName)
                .addZipInputStream(zipInputStream).deploy();
    }

    /**
     * @Description:查询所有的部署信息
     */
    public List<Deployment> getAllDeployment() {
        return this.processEngine.getRepositoryService().createDeploymentQuery().orderByDeploymenTime().desc().list();
    }

    /**
     * @Description:查询所有的流程定义的信息
     */
    public List<ProcessDefinition> getAllProcessDefinition() {
        return this.processEngine.getRepositoryService().createProcessDefinitionQuery()
                .orderByProcessDefinitionVersion().desc().list();
    }

    /**
     * @Description:删除某一个部署
     */
    public void deleteDeployment(String deploymentId) {
        this.processEngine.getRepositoryService().deleteDeployment(deploymentId, true);
    }

    /**
     * @Description:查看流程图
     */
    public InputStream showImages(String pdid) {
        return this.processEngine.getRepositoryService().getProcessDiagram(pdid);
    }

    /**
     * 启动流程实例 1、启动流程实例的api 2、传入一个参数：新增加的请假单的id
     * 3、因为在提交申请的任务中有#{userID},所以在进入提交申请的任务之前，必须通过流程变量给userID赋值
     */
    public void startPI(Long id, String userId) {
        Map<String, Object> variables = new HashMap<String, Object>();
        // 流程变量传入用户的ID
        variables.put("userID", userId);
        this.processEngine.getRuntimeService()
        /**
         * 第二个参数是businesskey:请假单的主键，
         */
        .startProcessInstanceByKey("LeaveBill", "" + id, variables);
    }

    /**
     * @Description:当前登录人登录系统以后要执行的任务
     */
    public List<Task> getTasksByAssignee(String userId) {
        return this.processEngine.getTaskService().createTaskQuery().taskAssignee(userId).orderByTaskCreateTime()
                .desc().list();
    }

    /**
     * @Description:根据taskId得到当前任务所在的流程实例正在执行的节点的所有的sequenceFlow的名称
     * @param taskId
     * @return
     */
    public List<PvmTransition> getPvmTransitions(String taskId) {
        ActivityImpl activityImpl = this.getActivityImplByTaskId(taskId);
        return activityImpl.getOutgoingTransitions();
    }

    /**
     * @Description:根据taskId得到当前流程实例正在执行的节点ActivityImpl
     */
    public ActivityImpl getActivityImplByTaskId(String taskId) {
        /**
         * 根据taskId获取到task
         */
        Task task = this.processEngine.getTaskService().createTaskQuery().taskId(taskId).singleResult();
        /**
         * 根据task获取到pi
         */
        ProcessInstance pi = this.processEngine.getRuntimeService().createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId()).singleResult();

        ProcessDefinitionEntity processDefinitionEntity = this.getProcessDefinitionEntityByTaskId(taskId);
        return processDefinitionEntity.findActivity(pi.getActivityId());
    }

    /**
     * @Description:根据taskId获取到ProcessDefinitionEntity
     */
    private ProcessDefinitionEntity getProcessDefinitionEntityByTaskId(String taskId) {
        Task task = this.processEngine.getTaskService().createTaskQuery().taskId(taskId).singleResult();
        return (ProcessDefinitionEntity) this.processEngine.getRepositoryService().getProcessDefinition(
                task.getProcessDefinitionId());
    }

    /**
     * @Description:根据taskId查找businessKey
     */
    public String getBusinessKeyByTaskId(String taskId) {
        Task task = this.processEngine.getTaskService().createTaskQuery().taskId(taskId).singleResult();
        ProcessInstance pi = this.processEngine.getRuntimeService().createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId()).singleResult();
        return pi.getBusinessKey();
    }

    /**
     * @Description:根据taskId完成任务，并且在完成任务以后判断流程实例是否结束
     */
    public ProcessInstance finishTask(String taskId) {
        /**
         * 根据taskId提取任务
         */
        Task task = this.processEngine.getTaskService().createTaskQuery().taskId(taskId).singleResult();
        // 根据任务得到piid
        String piid = task.getProcessInstanceId();
        this.processEngine.getTaskService().complete(taskId);
        // 根据piid过滤流程实例
        ProcessInstance pi = this.processEngine.getRuntimeService().createProcessInstanceQuery()
                .processInstanceId(piid).singleResult();
        // 如果整个流程实例结束了，则pi为null,如果没有结束就是一个对象
        return pi;
    }
}
