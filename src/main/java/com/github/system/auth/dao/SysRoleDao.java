package com.github.system.auth.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.system.auth.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysRoleDao extends BaseMapper<SysRole> {

    @Select("select code from sys_role r join sys_role_user ur on ur.user_id = #{userId}")
    List<String> queryUserRole(Integer userId);

}
