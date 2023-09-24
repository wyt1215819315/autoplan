package com.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.dao.AutoLogDao;
import com.system.entity.AutoLog;
import com.system.vo.PageDataVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogService {
    @Autowired
    private AutoLogDao logDao;

    public AutoLog getLog(Long id, Integer autoId, String type, Integer uid) {
        if (id == null) {
            LambdaQueryWrapper<AutoLog> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(AutoLog::getAutoId, autoId);
            queryWrapper.eq(AutoLog::getType, type);
            queryWrapper.eq(AutoLog::getUserid, uid);
            queryWrapper.orderByDesc(AutoLog::getDate);
            queryWrapper.last("limit 1");
            return logDao.selectOne(queryWrapper);
        } else {
            return logDao.selectById(id);
        }
    }

    public PageDataVO<AutoLog> queryPageList(Integer page, Integer limit, String taskType, String taskStatus) {
        QueryWrapper<AutoLog> queryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotEmpty(taskType)) {
            queryWrapper.eq("type", taskType);
        }

        if (StringUtils.isNotEmpty(taskStatus)) {
            queryWrapper.eq("status", taskStatus);
        }

        queryWrapper.orderByDesc("date");

        Page<AutoLog> pageObj = new Page<>(page, limit);
        IPage<AutoLog> data = logDao.selectPage(pageObj, queryWrapper);

        return new PageDataVO<>(data.getTotal(), data.getRecords());
    }
}
