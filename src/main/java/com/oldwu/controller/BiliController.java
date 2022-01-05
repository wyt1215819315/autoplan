package com.oldwu.controller;

import com.oldwu.entity.AjaxResult;
import com.oldwu.entity.AutoBilibili;
import com.oldwu.service.BiliService;
import com.oldwu.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/user/bili")
public class BiliController {

    @Autowired
    private BiliService service;

    @Autowired
    private UserService userService;

    /**
     * 获取这个用户的b站挂机列表
     * @param principal user
     * @return
     */
    @PostMapping("/list")
    public AjaxResult list(Principal principal) {
        //TODO 获取这个用户的b站挂机列表
        
        return null;
    }

    @PostMapping("/edit")
    public Map<String, Object> edit(@RequestBody AutoBilibili autoBilibili, Principal principal) {
        autoBilibili.setUserid(userService.getUserId(principal.getName()));
        return service.editBiliPlan(autoBilibili);
    }

    @PostMapping("/add")
    public Map<String, String> add(@RequestBody AutoBilibili autoBilibili, Principal principal) {
        autoBilibili.setUserid(userService.getUserId(principal.getName()));
        return service.addBiliPlan(autoBilibili);
    }

    @PostMapping("/delete")
    public Map<String, Object> delete(@Param("bid") Integer bid, Principal principal) {
        AutoBilibili autoBilibili = new AutoBilibili();
        autoBilibili.setUserid(userService.getUserId(principal.getName()));
        autoBilibili.setId(bid);
        return service.deleteBiliPlan(autoBilibili);
    }

    @GetMapping("/qrcode")
    public AjaxResult getQrcode(){
        return service.getQrcodeAuth();
    }

    @PostMapping("/qrcode")
    public Map<String,Object> getQrcodeStatus(@RequestBody String oauthKey){
        return service.getQrcodeStatus(oauthKey);
    }


//    @RequestMapping("/add")
//    public Map<String,String> addBiliInfo(){
//
//    }

}
