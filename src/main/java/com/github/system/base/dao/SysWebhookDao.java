package com.github.system.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.push.base.entity.SysWebhook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysWebhookDao extends BaseMapper<SysWebhook> {
}
