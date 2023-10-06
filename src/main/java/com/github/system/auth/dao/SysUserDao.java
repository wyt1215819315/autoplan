package com.github.system.auth.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.system.auth.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserDao extends BaseMapper<SysUser> {
}
