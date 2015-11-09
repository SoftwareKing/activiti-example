package com.l2cloud.bpm.web;

import java.util.List;

import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.l2cloud.bpm.utils.ActivitiUtils;

@RestController
@RequestMapping(value = "/bpm")
public class TestBpmController {

    @Autowired
    private ActivitiUtils activitiUtils;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public void getAllProcessDefinition() {
        List<ProcessDefinition> list = activitiUtils.getAllProcessDefinition();
        for (ProcessDefinition processDefinition : list) {
            System.out.println(processDefinition.toString());
        }
    }

}
