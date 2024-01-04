package com.github.system.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.push.base.entity.PushResultLog;
import com.github.push.base.vo.TaskPushResultVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PushResultLogDao extends BaseMapper<PushResultLog> {

    @Select("select pl.success,pl.data,pl.date,w.name as webhookName,w.type as webhookType " +
            "from log_push_result pl join sys_webhook w on w.id = pl.webhook_id " +
            "where pl.log_id = #{logId}")
    List<TaskPushResultVo> getTaskPushResult(Long logId);

}
