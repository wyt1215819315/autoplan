package com.oldwu.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oldwu.dao.AutoLogDao;
import com.oldwu.entity.AutoLog;
import com.oldwu.vo.PageDataVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogService {
    @Autowired
    private AutoLogDao logDao;

    public AutoLog getLog(Long id, Integer autoId, String type, Integer uid){
        QueryWrapper<AutoLog> queryWrapper = new QueryWrapper<>();

        if (id == null) {
            queryWrapper.eq("auto_id", autoId);
            queryWrapper.eq("type", type);
            queryWrapper.eq("userid", uid);
            queryWrapper.orderByDesc("date");
            Page<AutoLog> page = new Page<>(1, 1);
            Page<AutoLog> list = logDao.selectPage(page, queryWrapper);
            return list.getRecords().get(0);
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
