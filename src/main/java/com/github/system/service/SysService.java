package com.github.system.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.task.bili.dao.AutoBilibiliDao;
import com.github.task.bili.model.AutoBilibili;
import com.github.system.base.constant.SystemConstant;
import com.github.system.base.dao.SysConfigDao;
import com.github.system.dao.UserDao;
import com.github.system.base.dto.AjaxResult;
import com.github.system.base.entity.SysConfig;
import com.github.system.security.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    private AutoBilibiliDao autoBilibiliDao;

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
    @Transactional
    public AjaxResult turnBiliPlan2Json(){
        Integer userid = SessionUtils.getPrincipal().getId();
        if (!userDao.getRole(userid).equals("ROLE_ADMIN")) {
            return AjaxResult.doError("你没有权限操作");
        }
        //只需要操作auto_bilibili表中的数据
        List<AutoBilibili> autoBilibilis = autoBilibiliDao.selectList(new QueryWrapper<>());
        for (AutoBilibili autoBilibili : autoBilibilis) {
            Integer numberofcoins = autoBilibili.getNumberofcoins();
            Integer reservecoins = autoBilibili.getReservecoins();
            Integer selectlike = autoBilibili.getSelectlike();
            String monthendautocharge = autoBilibili.getMonthendautocharge();
            String givegift = autoBilibili.getGivegift();
            String uplive = autoBilibili.getUplive();
            String chargeforlove = autoBilibili.getChargeforlove();
            String deviceplatform = autoBilibili.getDeviceplatform();
            Integer coinaddpriority = autoBilibili.getCoinaddpriority();
            String skipdailytask = autoBilibili.getSkipdailytask();
            String matchEnable = autoBilibili.getMatchEnable();
            Integer matchMinimumnumberofcoins = autoBilibili.getMatchMinimumnumberofcoins();
            Integer matchPredictnumberofcoins = autoBilibili.getMatchPredictnumberofcoins();
            String matchShowhandmodel = autoBilibili.getMatchShowhandmodel();
            BiliCoinConfig biliCoinConfig = new BiliCoinConfig(numberofcoins, reservecoins, selectlike != 0, coinaddpriority);
            BiliChargeConfig biliChargeConfig = new BiliChargeConfig(chargeforlove, Boolean.parseBoolean(monthendautocharge));
            BiliGiveGiftConfig biliGiveGiftConfig = new BiliGiveGiftConfig(Boolean.parseBoolean(givegift),uplive);
            BiliPreConfig biliPreConfig = new BiliPreConfig(Boolean.parseBoolean(matchEnable),Boolean.parseBoolean(matchShowhandmodel),matchPredictnumberofcoins,matchMinimumnumberofcoins);
            BiliTaskConfig biliTaskConfig = new BiliTaskConfig(biliPreConfig,biliCoinConfig,biliChargeConfig,biliGiveGiftConfig,deviceplatform);
            autoBilibili.setEnable(skipdailytask.equals("false") ? "true" : "false");
            autoBilibili.setTaskConfig(JSON.toJSONString(biliTaskConfig));
            autoBilibiliDao.updateById(autoBilibili);
        }
        return AjaxResult.doSuccess();
    }

}
