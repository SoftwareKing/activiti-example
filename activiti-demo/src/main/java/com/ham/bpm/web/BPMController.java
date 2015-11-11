package com.ham.bpm.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/bpm")
public class BPMController {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private IdentifyManager idManager;
    
    @RequestMapping(value = "/createVMProcess", method = RequestMethod.GET)
    public void createVMProcess() throws Exception {
//        idManager.addUserToGroup();
        idManager.syncGroupAndUser();
        // runProcess();
    }

    public void runProcess() throws Exception {
        Map<String, Object> variableMap = new HashMap<String, Object>();
        variableMap.put("name", "kaiming");
        String procId = runtimeService.startProcessInstanceByKey("createVMProcess", variableMap).getId();
        // 进入任务的第一环节
        List<Task> taskList = taskService.createTaskQuery().taskCandidateGroup("sales").list();
        for(Task task : taskList) {
            System.out.println(task.getName());
            // 认领任务
            taskService.claim(task.getId(), "kaiming");
            taskService.complete(task.getId());
        }
        // 进入任务的第二环节
        taskList = taskService.createTaskQuery().taskCandidateGroup("it").list();
        for(Task task : taskList) {
            System.out.println(task.getName());
            // 认领任务
            taskService.claim(task.getId(), "chenli");
            taskService.complete(task.getId());
        }
        
        // 判断任务是否结束
        HistoricProcessInstance historicProcessInstance  = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(procId).singleResult();
        System.out.println("Process instance end time: " + historicProcessInstance.getEndTime());
    }
}
