package com.ham.bpm.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/xenapp")
public class XenAppController {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private IdentifyManager inIdentifyManager;


    @RequestMapping(value = "/syncGroupAndUser", method = RequestMethod.GET)
    public void syncGroupAndUser() {
        inIdentifyManager.syncGroupAndUser();
    }
    
    @RequestMapping(value = "/applyXenApp", method = RequestMethod.GET)
    public void applyXenApp() throws Exception {
        // 申请人
        String useId = "kaiming";
        String procId = runtimeService.startProcessInstanceByKey("createXenAppProcess").getId();
        
        // （一）子公司业务部门审批
        List<Task> taskList = taskService.createTaskQuery().taskCandidateGroup("busDept").list();
        for(Task task : taskList) {
            // 认领任务
            taskService.claim(task.getId(), useId);
            taskService.complete(task.getId());
        }
        
        // 产生流程进行状态图
        this.genPic(procId);
        
        // （二）科技公司IT部门审批
        taskList = taskService.createTaskQuery().taskCandidateGroup("itDept").list();
        for(Task task : taskList) {
            // 认领任务
            taskService.claim(task.getId(), useId);
            taskService.complete(task.getId());
        }
        
    }
    
    /**
     * 产生流程进行状态图
     * @param procId
     * @throws Exception
     */
    public void genPic(String procId) throws Exception {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(procId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
        List<String> activeIds = runtimeService.getActiveActivityIds(pi.getId());
        ProcessDiagramGenerator p = new DefaultProcessDiagramGenerator(); 
        InputStream is = p.generateDiagram(bpmnModel, "png", activeIds);
        
        File file = new File("C:\\Download\\process.png"); 
        OutputStream os = new FileOutputStream(file);
        
        byte[] buffer = new byte[1024]; 
        int len = 0;
        while((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len); 
        }
        
        os.close();
        is.close();
    }
    
}
