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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/mihuyou")
public class MihayouController {

    @Autowired
    private UserService userService;

    @Autowired
    private MihayouService mihayouService;

    @PostMapping("/edit")
    public Map<String, Object> edit(@RequestBody AutoMihayou autoMihayou, Principal principal) {
        autoMihayou.setUserId(userService.getUserId(principal.getName()));
        return mihayouService.editMiHuYouPlan(autoMihayou);
    }

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
        Map<String, String> data = new HashMap<>();
        List<Map<String, String>> maps = mihayouService.addMiHuYouPlan(autoMihayou);
        //如果返回多个结果，合并到一个map返回给前端
        //按理来说不会出现code不同的情况，所以直接取第一个map的返回结果就行了
        //但是msg需要全部遍历出来
        int flag = -1;
        for (int i = 0; i < maps.size(); i++) {
            Map<String, String> map = maps.get(i);
            if (!data.containsKey("msg")) {
                data.put("msg", "");
            }
            if (map.get("code").equals("201")) {
                flag = i;
            }
            data.put("msg", data.get("msg") + "<br>" + map.get("msg"));
        }
        //感叹号201特殊处理
        if (flag != -1) {
            data.put("msg", data.get("msg") + maps.get(flag).get("msg1"));
        }
        data.put("code", maps.get(0).get("code"));
        return data;
    }

    @PostMapping("/run")
    public Map<String, Object> run(@Param("id") Integer id, Principal principal) {
        Integer userId = userService.getUserId(principal.getName());
        return mihayouService.doDailyTaskPersonal(id, userId);
    }

}
