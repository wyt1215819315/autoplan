package com.github.system.base.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.github.system.auth.entity.SysRole;
import com.github.system.auth.service.SysRoleService;
import com.github.system.auth.service.UserService;
import com.github.system.base.dto.AjaxResult;
import com.github.system.base.vo.SysUserRoleVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/system/role")
@SaCheckRole("ADMIN")
public class SysRoleController {

    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private UserService userService;


    @ApiOperation("获取所有角色")
    @GetMapping("/list")
    public List<SysRole> list() {
        return sysRoleService.getAllRole();
    }

    @ApiOperation("获取用户拥有的角色")
    @GetMapping("/{userId}/getRole")
    public List<SysRole> getUserRole(@PathVariable Long userId) {
        return sysRoleService.getUserRoleInfo(userId);
    }

    @ApiOperation("编辑用户角色")
    @PostMapping("/editUserRole")
    public AjaxResult editUserRole(@RequestBody @Validated SysUserRoleVo sysUserRoleVo) {
        if (userService.checkIfAdmin(sysUserRoleVo.getUserId())) {
            return AjaxResult.doError("不能修改管理员用户！");
        }
        return AjaxResult.status(sysRoleService.editUserRole(sysUserRoleVo));
    }


}
