package com.netmusic.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.netmusic.dao.AutoNetmusicDao;
import com.netmusic.model.AutoNetmusic;
import com.netmusic.util.NeteaseMusicUtil;
import com.oldwu.dao.AutoLogDao;
import com.oldwu.dao.UserDao;
import com.oldwu.entity.AjaxResult;
import com.oldwu.entity.AutoLog;
import com.oldwu.security.utils.SessionUtils;
import com.oldwu.task.NetMusicTask;
import com.oldwu.vo.PageDataVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NetmusicService {

    @Autowired
    private AutoNetmusicDao netmusicDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AutoLogDao autoLogDao;


    /**
     * 根据id查询
     *
     * @return
     */
    public AjaxResult view(Integer id) {
        Integer userId = SessionUtils.getPrincipal().getId();
        AutoNetmusic autoNetmusic = netmusicDao.selectById(id);
        if (autoNetmusic == null) {
            return AjaxResult.doError();
        } else {
            //放行管理员
            String role = userDao.getRole(userId);
            if (!autoNetmusic.getUserid().equals(userId) && !role.equals("ROLE_ADMIN")) {
                return AjaxResult.doError("你无权访问！");
            }
        }
        //移除cookie
        autoNetmusic.setCookie(null);
        autoNetmusic.setPhone(null);
        autoNetmusic.setPassword(null);
        return AjaxResult.doSuccess(autoNetmusic);
    }

    public PageDataVO<AutoNetmusic> queryPageList(Integer page, Integer limit) {

        QueryWrapper<AutoNetmusic> queryWrapper = new QueryWrapper<>();
        Page<AutoNetmusic> pageObj = new Page<>(page, limit);
        IPage<AutoNetmusic> data = netmusicDao.selectPage(pageObj, queryWrapper);

        List<AutoNetmusic> autoNetmusicList = data.getRecords();

        for (AutoNetmusic autoNetmusic : autoNetmusicList) {
            autoNetmusic.setPassword(null);
            autoNetmusic.setNetmusicId(null);
            autoNetmusic.setPhone(null);
            autoNetmusic.setCookie(null);
            autoNetmusic.setWebhook(null);
            autoNetmusic.setNetmusicName(com.oldwu.util.StringUtils.userNameEncode(autoNetmusic.getNetmusicName()));
        }

        data.setRecords(autoNetmusicList);

        return new PageDataVO<>(data.getTotal(), data.getRecords());
    }


    public Map<String, String> addNetMusicPlan(AutoNetmusic autoNetmusic) {
        Map<String, String> map = new HashMap<>();
        Map<String, Object> stringObjectMap = checkForm(autoNetmusic, false);
        if (!(boolean) stringObjectMap.get("flag")) {
            map.put("code", "-1");
            map.put("msg", (String) stringObjectMap.get("msg"));
            return map;
        }
        //信息检查完毕后，尝试登录账号，进行验证
        Map<String, String> infos = new HashMap<>();
        infos.put("phone", autoNetmusic.getPhone());
        infos.put("password", autoNetmusic.getPassword());
        infos.put("countrycode", autoNetmusic.getCountrycode());
        Map<String, String> usercheck = NeteaseMusicUtil.login(infos);
        if (usercheck.get("flag").equals("false")) {
            map.put("code", "-1");
            map.put("msg", usercheck.get("msg"));
            return map;
        }
        //账号验证成功,写入用户数据
        autoNetmusic.setNetmusicName(usercheck.get("nickname"));
        autoNetmusic.setAvatar(usercheck.get("avatarUrl"));
        autoNetmusic.setNetmusicId(usercheck.get("uid"));
        autoNetmusic.setNetmusicNeedDay(usercheck.get("days"));
        autoNetmusic.setNetmusicNeedListen(usercheck.get("count"));
        autoNetmusic.setNetmusicLevel(usercheck.get("level"));
        autoNetmusic.setStatus("100");
        //判断数据是否存在，使用网易云uid进行检索
        AutoNetmusic autoNetmusic1 = netmusicDao.selectByUid(autoNetmusic.getNetmusicId());
        if (autoNetmusic1 == null || autoNetmusic1.getId() == null) {
            //insert
            netmusicDao.insert(autoNetmusic);
        } else {
            //update
            autoNetmusic.setId(autoNetmusic1.getId());
            netmusicDao.updateById(autoNetmusic);
        }
        map.put("code", "200");
        map.put("msg", usercheck.get("msg"));
        return map;
    }

    public Map<String, Object> checkForm(AutoNetmusic autoNetmusic, boolean skipCheckCookie) {
        Map<String, Object> map = new HashMap<>();
        String enable = autoNetmusic.getEnable();
        String webhook = autoNetmusic.getWebhook();
        if (!skipCheckCookie) {
            String name = autoNetmusic.getName();
            String phone = autoNetmusic.getPhone();
            String password = autoNetmusic.getPassword();
            String countrycode = autoNetmusic.getCountrycode();
            if (StringUtils.isBlank(name) || StringUtils.isBlank(phone) || StringUtils.isBlank(password)) {
                map.put("flag", false);
                map.put("msg", "前三项不能为空！");
                return map;
            }
            if (StringUtils.isBlank(countrycode) || !StringUtils.isNumeric(countrycode)) {
                autoNetmusic.setCountrycode("86");
            }
        }
        if (StringUtils.isBlank(enable) || !enable.equals("true") && !enable.equals("false")) {
            autoNetmusic.setEnable("true");
        }
        if (StringUtils.isBlank(webhook)) {
            autoNetmusic.setWebhook("");
        }
        map.put("flag", true);
        map.put("msg", "check complete");
        return map;
    }

    /**
     * 删除网易云任务，开启事务
     *
     * @param id 传入要删除的autoId
     * @return AjaxResult 删除结果
     */
    @Transactional
    public AjaxResult deleteNetMusicPlan(Integer id) throws Exception {
        //校验用户id
        Integer userid = SessionUtils.getPrincipal().getId();
        if (id == null || id == 0) {
            return AjaxResult.doError("传参不能为空！");
        }
        List<AutoNetmusic> autoNetmusics = netmusicDao.selectMine(userid);
        boolean flag = false;
        for (AutoNetmusic netmusic : autoNetmusics) {
            int autoId = netmusic.getId();
            if (autoId == id) {
                flag = true;
                break;
            }
        }
        if (userDao.getRole(userid).equals("ROLE_ADMIN")) {  //忽略管理
            flag = true;
        }
        if (!flag) {
            return AjaxResult.doError("你没有权限删除这条或数据不存在！");
        }
        //首先删除日志
        AutoLog autoLog = new AutoLog();
        autoLog.setUserid(userid);
        autoLog.setAutoId(id);
        autoLog.setType("netmusic");
        autoLogDao.deleteByAutoId(autoLog);
        //最后删除主要数据
        int i = netmusicDao.deleteById(id);
        if (i > 0) {
            return AjaxResult.doSuccess("删除成功");
        }
        //删除失败后回滚
        throw new Exception("删除失败！");
    }

    public AutoNetmusic getMyEditPlan(AutoNetmusic autoNetmusic1) {
        AutoNetmusic autoNetmusic = netmusicDao.selectById(autoNetmusic1.getId());
        if (autoNetmusic == null || autoNetmusic.getId() == null) {
            return null;
        }
        //放行管理员
        String role = userDao.getRole(autoNetmusic1.getUserid());
        if (!autoNetmusic.getUserid().equals(autoNetmusic1.getUserid()) && !role.equals("ROLE_ADMIN")) {
            return null;
        }
        return autoNetmusic;
    }

    public Map<String, Object> editNetMusicPlan(AutoNetmusic autoNetmusic1) {
        Map<String, Object> map = new HashMap<>();
        AutoNetmusic autoNetmusic = netmusicDao.selectById(autoNetmusic1.getId());
        if (autoNetmusic == null || autoNetmusic.getId() == null) {
            map.put("code", -1);
            map.put("msg", "参数错误！");
            return map;
        }
        //放行管理员
        String role = userDao.getRole(autoNetmusic1.getUserid());
        if (!autoNetmusic.getUserid().equals(autoNetmusic1.getUserid()) && !role.equals("ROLE_ADMIN")) {
            map.put("code", 403);
            map.put("msg", "你没有权限修改！");
            return map;
        }
        checkForm(autoNetmusic1, true);
        int i = netmusicDao.updateById(autoNetmusic1);
        if (i > 0) {
            map.put("code", 200);
            map.put("msg", "操作成功！");
            return map;
        }
        map.put("code", 0);
        map.put("msg", "操作失败！");
        return map;
    }

    public Map<String, Object> doDailyTaskPersonal(Integer autoId, Integer userid) {
        Map<String, Object> map = new HashMap<>();
        AutoNetmusic autoNetmusic = netmusicDao.selectById(autoId);
        if (autoNetmusic == null || autoNetmusic.getId() == null) {
            map.put("code", 500);
            map.put("msg", "参数错误！");
            return map;
        }
        String role = userDao.getRole(autoNetmusic.getUserid());
        if (!autoNetmusic.getUserid().equals(userid) && !role.equals("ROLE_ADMIN")) {
            map.put("code", 403);
            map.put("msg", "你没有权限执行！");
            return map;
        }
        if (autoNetmusic.getStatus().equals("1")) {
            map.put("code", 1);
            map.put("msg", "任务已经在运行啦~请不要重复执行");
            return map;
        }
        Thread t = new Thread(() -> {
            NetMusicTask netMusicTask = new NetMusicTask();
            netMusicTask.runTask(autoId, userid, autoNetmusic);
        });
        t.start();
        map.put("code", 200);
        map.put("msg", "运行指令已发送，请稍后查看运行状态");
        return map;
    }

    public AjaxResult listMine(Integer id) {
        List<AutoNetmusic> autoNetmusics = netmusicDao.selectMine(id);
        return AjaxResult.doSuccess(autoNetmusics);
    }
}
