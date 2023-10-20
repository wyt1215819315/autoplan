package com.github.system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.system.base.constant.SystemConstant;
import com.github.system.base.dao.SysConfigDao;
import com.github.system.base.dto.AjaxResult;
import com.github.system.base.entity.SysConfig;
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
        //查询原有数据
        SysConfig sysConfig = sysConfigDao.selectOne(getSystemConfigKeyValueQueryWrapper());
        int i;
        if (sysConfig == null){
            //无数据，insert
            sysConfig = new SysConfig();
            sysConfig.setKey(SystemConstant.SYSTEM_NOTICE_CONTENT);
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

}
