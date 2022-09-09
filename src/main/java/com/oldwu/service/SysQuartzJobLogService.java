package com.oldwu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oldwu.dao.SysQuartzJobLogMapper;
import com.oldwu.domain.SysQuartzJobLog;
import com.oldwu.domain.SysQuartzJobLogExample;
import com.oldwu.domain.Tablepar;
import com.oldwu.util.ConvertUtil;
import com.oldwu.util.SnowflakeIdWorker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 定时任务调度日志表 SysQuartzJobLogService
 *
 * @author fuce_自动生成
 * @Title: SysQuartzJobLogService.java
 * @Package com.fc.v2.service
 * @email 115889198@qq.com
 * @date 2019-09-13 00:03:42
 **/
@Service
public class SysQuartzJobLogService implements BaseService<SysQuartzJobLog, SysQuartzJobLogExample> {
    @Autowired
    private SysQuartzJobLogMapper sysQuartzJobLogMapper;

    public Page<SysQuartzJobLog> list(Tablepar tablepar, String name) {

        Page<SysQuartzJobLog> page = new Page<>(tablepar.getPage(), tablepar.getLimit());

        LambdaQueryWrapper<SysQuartzJobLog> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(StringUtils.isNotEmpty(name), SysQuartzJobLog::getJobName, name);
        queryWrapper.orderByDesc(SysQuartzJobLog::getStartTime);

        Page<SysQuartzJobLog> data = sysQuartzJobLogMapper.selectPage(page, queryWrapper);
        return data;
    }

    @Override
    public int deleteByPrimaryKey(String ids) {
        List<String> lista = ConvertUtil.toListStrArray(ids);
        SysQuartzJobLogExample example = new SysQuartzJobLogExample();
        example.createCriteria().andIdIn(lista);
        return sysQuartzJobLogMapper.deleteByExample(example);
    }

    @Override
    public SysQuartzJobLog selectByPrimaryKey(String id) {

        return sysQuartzJobLogMapper.selectById(id);
    }

    @Override
    public int updateByPrimaryKeySelective(SysQuartzJobLog record) {
        return sysQuartzJobLogMapper.updateById(record);
    }

    /**
     * 添加
     */
    @Override
    public int insertSelective(SysQuartzJobLog record) {
        //添加雪花主键id
        record.setId(SnowflakeIdWorker.getUUID());
        return sysQuartzJobLogMapper.insert(record);
    }

    @Override
    public int updateByExampleSelective(SysQuartzJobLog record, SysQuartzJobLogExample example) {

        return sysQuartzJobLogMapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateByExample(SysQuartzJobLog record, SysQuartzJobLogExample example) {

        return sysQuartzJobLogMapper.updateByExample(record, example);
    }

    @Override
    public List<SysQuartzJobLog> selectByExample(SysQuartzJobLogExample example) {

        return sysQuartzJobLogMapper.selectByExample(example);
    }

    @Override
    public long countByExample(SysQuartzJobLogExample example) {

        return sysQuartzJobLogMapper.countByExample(example);
    }

    @Override
    public int deleteByExample(SysQuartzJobLogExample example) {

        return sysQuartzJobLogMapper.deleteByExample(example);
    }

    /**
     * 检查name
     *
     * @param sysQuartzJobLog
     * @return
     */
    public int checkNameUnique(SysQuartzJobLog sysQuartzJobLog) {
        SysQuartzJobLogExample example = new SysQuartzJobLogExample();
        example.createCriteria().andJobNameEqualTo(sysQuartzJobLog.getJobName());
        List<SysQuartzJobLog> list = sysQuartzJobLogMapper.selectByExample(example);
        return list.size();
    }

}
