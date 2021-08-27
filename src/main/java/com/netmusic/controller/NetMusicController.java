package com.netmusic.controller;

import com.netmusic.model.AutoNetmusic;
import com.netmusic.service.NetmusicService;
import com.oldwu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/user/netmusic")
public class NetMusicController {

    @Autowired
    private NetmusicService netmusicService;

    @Autowired
    private UserService userService;


    @PostMapping("/add")
    public Map<String, String> add(@RequestBody AutoNetmusic autoNetmusic, Principal principal) {
        autoNetmusic.setUserid(userService.getUserId(principal.getName()));
        return netmusicService.addBiliPlan(autoNetmusic);
    }

}
