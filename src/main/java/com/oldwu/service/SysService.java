package com.oldwu.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.oldwu.constant.SystemConstant;
import com.oldwu.dao.SysConfigDao;
import com.oldwu.dao.UserDao;
import com.oldwu.entity.AjaxResult;
import com.oldwu.entity.SysConfig;
import com.oldwu.security.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SysService {

    QueryWrapper<SysConfig> queryWrapper ;

    public QueryWrapper<SysConfig> getSystemConfigKeyValueQueryWrapper() {
        queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","value").eq("bond", SystemConstant.SYSTEM_NOTICE_CONTENT);
        return queryWrapper;
    }

    @Autowired
    private SysConfigDao sysConfigDao;

    @Autowired
    private UserDao userDao;

    /**
     * 获取系统公告
     * @return AjaxResult 公告
     */
    public AjaxResult getSystemNoticeContent(){
        SysConfig sysConfig = sysConfigDao.selectOne(getSystemConfigKeyValueQueryWrapper());
        if (sysConfig == null){
            return AjaxResult.doSuccess("", "当前无公告！");
        }
        return AjaxResult.doSuccess("", sysConfig.getValue());
    }

    /**
     * 编辑系统公告
     * @return AjaxResult 结果
     */
    public AjaxResult setSystemNoticeContent(String text){
        Integer userid = SessionUtils.getPrincipal().getId();
        if (!userDao.getRole(userid).equals("ROLE_ADMIN")) {
            return AjaxResult.doError("你没有权限操作");
        }
        //查询原有数据
        SysConfig sysConfig = sysConfigDao.selectOne(getSystemConfigKeyValueQueryWrapper());
        int i;
        if (sysConfig == null){
            //无数据，insert
            sysConfig = new SysConfig();
            sysConfig.setBond(SystemConstant.SYSTEM_NOTICE_CONTENT);
            sysConfig.setValue(text);
            i = sysConfigDao.insert(sysConfig);
        }else {
            //update
            sysConfig.setValue(text);
            i = sysConfigDao.updateById(sysConfig);
        }
        if (i > 0){
            return AjaxResult.doSuccess("修改成功！");
        }
        return AjaxResult.doError("修改失败！");
    }

    /**
     * 用于转换老版本的字段到json
     * @return AjaxResult
     */
    public AjaxResult turnBiliPlan2Json(){
        return AjaxResult.doSuccess();
    }

}
