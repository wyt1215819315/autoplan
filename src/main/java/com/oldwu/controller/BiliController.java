package com.oldwu.controller;

import com.oldwu.entity.AjaxResult;
import com.oldwu.entity.AutoBilibili;
import com.oldwu.security.utils.SessionUtils;
import com.oldwu.service.BiliService;
import com.oldwu.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
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

}
