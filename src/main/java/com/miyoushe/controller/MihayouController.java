package com.miyoushe.controller;

import com.miyoushe.model.AutoMihayou;
import com.miyoushe.service.MihayouService;
import com.oldwu.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/user/mihayou")
public class MihayouController {

    @Autowired
    private UserService userService;

    @Autowired
    private MihayouService mihayouService;


    @PostMapping("/delete")
    public Map<String, Object> delete(@Param("id") Integer id, Principal principal) {
        AutoMihayou autoMihayou = new AutoMihayou();
        autoMihayou.setUserId(userService.getUserId(principal.getName()));
        autoMihayou.setId(id);
        return mihayouService.deleteMiHuYouPlan(autoMihayou);
    }

    @PostMapping("/add")
    public Map<String, String> add(@RequestBody AutoMihayou autoMihayou, Principal principal) {
        autoMihayou.setUserId(userService.getUserId(principal.getName()));
        return mihayouService.addMiHuYouPlan(autoMihayou);
    }

}
