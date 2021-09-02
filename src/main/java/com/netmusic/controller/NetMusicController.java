package com.netmusic.controller;

import com.netmusic.model.AutoNetmusic;
import com.netmusic.service.NetmusicService;
import com.oldwu.entity.AutoBilibili;
import com.oldwu.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/user/netmusic")
public class NetMusicController {

    @Autowired
    private NetmusicService netmusicService;

    @Autowired
    private UserService userService;

    @PostMapping("/edit")
    public Map<String, Object> edit(@RequestBody AutoNetmusic autoNetmusic, Principal principal) {
        autoNetmusic.setUserid(userService.getUserId(principal.getName()));
        return netmusicService.editNetMusicPlan(autoNetmusic);
    }

    @PostMapping("/delete")
    public Map<String, Object> delete(@Param("nid") Integer nid, Principal principal) {
        AutoNetmusic autoNetmusic = new AutoNetmusic();
        autoNetmusic.setUserid(userService.getUserId(principal.getName()));
        autoNetmusic.setId(nid);
        return netmusicService.deleteNetMusicPlan(autoNetmusic);
    }

    @PostMapping("/add")
    public Map<String, String> add(@RequestBody AutoNetmusic autoNetmusic, Principal principal) {
        autoNetmusic.setUserid(userService.getUserId(principal.getName()));
        return netmusicService.addNetMusicPlan(autoNetmusic);
    }

}
