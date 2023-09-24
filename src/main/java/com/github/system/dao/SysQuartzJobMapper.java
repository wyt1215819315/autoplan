package com.github.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.system.domain.SysQuartzJob;
import com.github.system.domain.SysQuartzJobExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 定时任务调度表 SysQuartzJobMapper
 * @author fuce_自动生成
 * @email 115889198@qq.com
 * @date 2019-09-13 00:03:35
 */
@Mapper
public interface SysQuartzJobMapper extends BaseMapper<SysQuartzJob> {
      	      	   	      	   	      	   	      	   	      	   	      	   	      	   	      
    long countByExample(SysQuartzJobExample example);

    int deleteByExample(SysQuartzJobExample example);

    List<SysQuartzJob> selectByExample(SysQuartzJobExample example);

    int updateByExampleSelective(@Param("record") SysQuartzJob record, @Param("example") SysQuartzJobExample example);

    int updateByExample(@Param("record") SysQuartzJob record, @Param("example") SysQuartzJobExample example);
  	  	
}