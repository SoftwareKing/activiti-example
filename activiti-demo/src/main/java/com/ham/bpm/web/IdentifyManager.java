package com.ham.bpm.web;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IdentifyManager {
    @Autowired
    private IdentityService identityService;

    public void syncGroupAndUser() {
        // 子公司业务部门1
        GroupQuery groupQuery = identityService.createGroupQuery();
        Group group = groupQuery.groupId("busDept").singleResult();

        if (group == null) {
            group = identityService.newGroup("busDept");
            group.setName("油田业务部门1");
            group.setType("assignment");
            identityService.saveGroup(group);
        }

        User user = identityService.newUser("kaiming");
        user.setFirstName("gu");
        identityService.saveUser(user);

        User user2 = identityService.newUser("kaiming2");
        user2.setFirstName("gu");
        identityService.saveUser(user2);

        identityService.createMembership("kaiming", "busDept");
        identityService.createMembership("kaiming2", "busDept");
        
        // 数据公司IT部门1
        groupQuery = identityService.createGroupQuery();
        group = groupQuery.groupId("itDept").singleResult();

        if (group == null) {
            group = identityService.newGroup("itDept");
            group.setName("数据公司IT部门1");
            group.setType("assignment");
            identityService.saveGroup(group);
        }

        user = identityService.newUser("xujin");
        user.setFirstName("xu");
        identityService.saveUser(user);

        user2 = identityService.newUser("xujin2");
        user2.setFirstName("xu");
        identityService.saveUser(user2);

        identityService.createMembership("xujin", "itDept");
        identityService.createMembership("xujin2", "itDept");
        
    }

}
