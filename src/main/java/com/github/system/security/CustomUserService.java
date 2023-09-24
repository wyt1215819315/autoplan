package com.github.system.security;

import com.github.system.dao.UserDao;
import com.github.system.domain.SysRole;
import com.github.system.domain.SysUser;
import com.github.system.security.entity.SystemUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangyibo on 17/1/18.
 */
@Service
public class CustomUserService implements UserDetailsService { //自定义UserDetailsService 接口

    @Autowired
    UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) { //重写loadUserByUsername 方法获得 userdetails 类型用户

        SysUser user = userDao.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        List<SysRole> authorities = new ArrayList<>();
        //用于添加用户的权限。只要把用户权限添加到authorities 就万事大吉。
        for (SysRole role : user.getRoles()) {
            authorities.add(new SysRole(role.getName()));
            System.out.println(role.getName());
        }
        return new SystemUser(user.getId(), user.getUsername(), user.getPassword(), authorities);

    }

}
