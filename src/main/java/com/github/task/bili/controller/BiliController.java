package com.task.bili.controller;

import com.system.entity.AjaxResult;
import com.system.security.utils.SessionUtils;
import com.task.bili.service.BiliService;
import com.system.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user/bili")
public class BiliController {

    @Autowired
    private BiliService service;

    @Autowired
    private UserService userService;

    /**
     * 根据id查询详细信息
     * @param id
     * @return
     */
    @PostMapping("/view")
    public AjaxResult view(@Param("id") Integer id) {
        return service.view(id);
    }

    /**
     * 获取这个用户的b站挂机列表
     * @return
     */
    @PostMapping("/list")
    public AjaxResult list() {
        Integer id = SessionUtils.getPrincipal().getId();
        return service.listMine(id);
    }

    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody String json) {
        return service.editBiliPlan(json);
    }

    @PostMapping("/add")
    public AjaxResult add(@RequestBody String json) {
        return service.addBiliPlan(json);
    }

    @PostMapping("/delete")
    public AjaxResult delete(@Param("bid") Integer id) {
        try {
            return service.deleteBiliPlan(id);
        } catch (Exception e) {
            return AjaxResult.doError(e.getMessage());
        }
    }

    @GetMapping("/qrcode")
    public AjaxResult getQrcode(){
        return service.getQrcodeAuth();
    }

    @PostMapping("/qrcode")
    public Map<String,Object> getQrcodeStatus(@RequestBody String oauthKey){
        return service.getQrcodeStatus(oauthKey);
    }

    @PostMapping("/run")
    public AjaxResult run(@Param("id") Integer id) {
        return service.doDailyTaskPersonal(id);
    }
}
