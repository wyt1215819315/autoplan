package com.github.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.system.domain.SysQuartzJobLog;
import com.github.system.domain.SysQuartzJobLogExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 定时任务调度日志表 SysQuartzJobLogMapper
 * @author fuce_自动生成
 * @email 115889198@qq.com
 * @date 2019-09-13 00:03:42
 */
@Mapper
public interface SysQuartzJobLogMapper extends BaseMapper<SysQuartzJobLog> {
      	      	   	      	   	      	   	      	   	      	   	      	   	      	   	      	   	      
    long countByExample(SysQuartzJobLogExample example);

    int deleteByExample(SysQuartzJobLogExample example);

    int deleteAll();

    List<SysQuartzJobLog> selectByExample(SysQuartzJobLogExample example);
		
    int updateByExampleSelective(@Param("record") SysQuartzJobLog record, @Param("example") SysQuartzJobLogExample example);

    int updateByExample(@Param("record") SysQuartzJobLog record, @Param("example") SysQuartzJobLogExample example);
  	  	
}