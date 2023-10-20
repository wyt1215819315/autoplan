package com.github.task.xiaomi.controller;

import com.github.system.base.dto.AjaxResult;
import com.github.system.security.utils.SessionUtils;
import com.github.system.service.UserService;
import com.github.task.xiaomi.service.AutoXiaomiService;
import com.github.task.xiaomi.model.entity.AutoXiaomiEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/user/xiaomi")
public class AutoXiaomiController {

    @Autowired
    private AutoXiaomiService autoXiaomiService;

    @Autowired
    private UserService userService;

    /**
     * 根据id查询详细信息
     *
     * @param id
     * @return
     */
    @PostMapping("/view")
    public AjaxResult view(@Param("id") Integer id) {
        return autoXiaomiService.view(id);
    }

    /**
     * 获取这个用户的托管列表
     *
     * @return
     */
    @PostMapping("/list")
    public AjaxResult list() {
        Integer id = SessionUtils.getPrincipal().getId();
        return autoXiaomiService.listMine(id);
    }

    /**
     * 编辑
     *
     * @param autoXiaomiEntity
     * @param principal
     * @return
     */
    @PostMapping("/edit")
    public Map<String, Object> edit(@RequestBody AutoXiaomiEntity autoXiaomiEntity, Principal principal) {
        autoXiaomiEntity.setUserId(userService.getUserId(principal.getName()));
        return autoXiaomiService.editAutoXiaomi(autoXiaomiEntity);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @PostMapping("/delete")
    public AjaxResult delete(@Param("id") Integer id) {
        try {
            return autoXiaomiService.delete(id);
        } catch (Exception e) {
            return AjaxResult.doError(e.getMessage());
        }
    }

    /**
     * 添加
     *
     * @param autoXiaomiEntity
     * @param principal
     * @return
     */
    @PostMapping("/add")
    public Map<String, String> add(@RequestBody AutoXiaomiEntity autoXiaomiEntity, Principal principal) {
        autoXiaomiEntity.setUserId(userService.getUserId(principal.getName()));
        return autoXiaomiService.addXiaoMi(autoXiaomiEntity);
    }

    /**
     * 执行任务
     *
     * @param id
     * @param principal
     * @return
     */
    @PostMapping("/run")
    public Map<String, Object> run(@Param("id") Integer id, Principal principal) {
        Integer userId = userService.getUserId(principal.getName());
        return autoXiaomiService.doDailyTaskPersonal(id, userId);
    }
}
