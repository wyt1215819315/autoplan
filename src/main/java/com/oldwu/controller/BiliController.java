package com.oldwu.controller;

import com.oldwu.entity.AutoBilibili;
import com.oldwu.service.BiliService;
import com.oldwu.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/api/user/bili")
public class BiliController {

    @Autowired
    private BiliService service;

    @Autowired
    private UserService userService;


    @PostMapping("/add")
    @ResponseBody
    public Map<String, String> add(@RequestBody AutoBilibili autoBilibili, Principal principal) {
        autoBilibili.setUserid(userService.getUserId(principal.getName()));
        return service.addBiliPlan(autoBilibili);
    }

    @PostMapping("/delete")
    @ResponseBody
    public Map<String, Object> delete(@Param("bid") Integer bid, Principal principal) {
        AutoBilibili autoBilibili = new AutoBilibili();
        autoBilibili.setUserid(userService.getUserId(principal.getName()));
        autoBilibili.setId(bid);
        return service.deleteBiliPlan(autoBilibili);
    }

    @GetMapping("/qrcode")
    @ResponseBody
    public String getQrcode(){
        return service.getQrcodeAuth();
    }

    @PostMapping("/qrcode")
    @ResponseBody
    public Map<String,Object> getQrcodeStatus(@RequestBody String oauthKey){
        return service.getQrcodeStatus(oauthKey);
    }

//    @RequestMapping("/add")
//    public Map<String,String> addBiliInfo(){
//
//    }

}
