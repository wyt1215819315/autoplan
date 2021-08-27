package com.oldwu.controller;

import com.oldwu.entity.AutoBilibili;
import com.oldwu.service.BiliService;
import com.oldwu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/user/bili")
public class BiliController {

    @Autowired
    private BiliService service;

    @Autowired
    private UserService userService;


    @RequestMapping("/bili-getall")
    public Map<String, String> getBiliInfo() {
        return null;
    }

    @PostMapping("/add")
    public Map<String, String> add(@RequestBody AutoBilibili autoBilibili, Principal principal) {
        autoBilibili.setUserid(userService.getUserId(principal.getName()));
        return service.addBiliPlan(autoBilibili);
    }

//    @RequestMapping("/add")
//    public Map<String,String> addBiliInfo(){
//
//    }

}
