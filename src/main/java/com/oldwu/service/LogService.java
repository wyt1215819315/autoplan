package com.oldwu.service;

import com.oldwu.dao.AutoLogDao;
import com.oldwu.entity.AutoLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LogService {
    @Autowired
    private AutoLogDao logDao;

    public AutoLog getLog(Integer id,String type,Integer uid){
        AutoLog autoLog = new AutoLog();
        if (StringUtils.isBlank(type)){
            return null;
        }
        autoLog.setAutoId(id);
        autoLog.setType(type);
        autoLog.setUserid(uid);
        return logDao.selectByCondition(autoLog);
    }

}
