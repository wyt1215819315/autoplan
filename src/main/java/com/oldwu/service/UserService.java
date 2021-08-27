package com.oldwu.service;

import com.oldwu.dao.UserDao;
import com.oldwu.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    public int getUserId(String username){
        SysUser byUserName = userDao.findByUserName(username);
        return byUserName.getId();
    }

}
