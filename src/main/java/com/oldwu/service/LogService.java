package com.oldwu.service;

import com.oldwu.dao.AutoLogDao;
import com.oldwu.entity.AutoLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LogService {
    @Autowired
    private AutoLogDao logDao;

    public AutoLog getLog(Integer bid,Integer nid,Integer uid){
        AutoLog autoLog = new AutoLog();
        autoLog.setBiAutoId(bid);
        autoLog.setNetAutoId(nid);
        autoLog.setUserid(uid);
        return logDao.selectByCondition(autoLog);
    }

}
