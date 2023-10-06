package com.github.task.xiaomi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.system.dao.AutoLogDao;
import com.github.system.dao.UserDao;
import com.github.system.base.dto.AjaxResult;
import com.github.system.entity.AutoLog;
import com.github.system.security.utils.SessionUtils;
import com.github.system.vo.PageDataVO;
import com.github.task.xiaomi.dao.AutoXiaomiDao;
import com.github.task.xiaomi.model.entity.AutoXiaomiEntity;
import com.github.task.xiaomi.service.AutoXiaomiService;
import com.github.task.xiaomi.task.XiaoMiTask;
import com.github.task.xiaomi.util.XiaomiLogin;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AutoXiaomiServiceImpl extends ServiceImpl<AutoXiaomiDao, AutoXiaomiEntity> implements AutoXiaomiService {

    @Autowired
    private AutoXiaomiDao autoXiaomiDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AutoLogDao autoLogDao;

    /**
     * 查询页面
     *
     * @param page
     * @param limit
     * @return
     */
    @Override
    public PageDataVO<AutoXiaomiEntity> queryPageList(Integer page, Integer limit) {
        LambdaQueryWrapper<AutoXiaomiEntity> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.orderByDesc(AutoXiaomiEntity::getCreatedTime);
        Page<AutoXiaomiEntity> pageObj = new Page<>(page, limit);
        IPage<AutoXiaomiEntity> data = autoXiaomiDao.selectPage(pageObj, queryWrapper);

        List<AutoXiaomiEntity> autoXiaomiEntityList = data.getRecords();

        autoXiaomiEntityList.forEach(x -> {
            if (StringUtils.isNotBlank(x.getRandomOrNot())) {
                if (x.getRandomOrNot().equals("1")) {
                    x.setSteps("随机");
                }
            }
            if (StringUtils.isNotBlank(x.getSteps())) {
                if (x.getSteps().equals("0")) {
                    x.setSteps("随机");
                }
            }
            x.setPassword(null);
            x.setWebhook(null);
            x.setName(null);
            x.setPhone(x.getPhone().replaceAll("(\\d{2})\\d{7}(\\d{2})", "$1*******$2"));
        });

        data.setRecords(autoXiaomiEntityList);
        return new PageDataVO<>(data.getTotal(), data.getRecords());
    }
    @Override
    public AjaxResult view(Integer id) {
        Integer userId = SessionUtils.getPrincipal().getId();
        AutoXiaomiEntity autoXiaomiEntity = this.getById(id);
        if (autoXiaomiEntity == null) {
            return AjaxResult.doError();
        } else {
            //放行管理员
            String role = userDao.getRole(userId);
            if (!autoXiaomiEntity.getUserId().equals(userId) && !role.equals("ROLE_ADMIN")) {
                return AjaxResult.doError("你无权访问！");
            }
        }
        return AjaxResult.doSuccess(autoXiaomiEntity);
    }

    @Override
    public AjaxResult listMine(Integer id) {
        LambdaQueryWrapper<AutoXiaomiEntity> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(AutoXiaomiEntity::getUserId, id);
        List<AutoXiaomiEntity> list = this.list(queryWrapper);
        for (AutoXiaomiEntity x : list) {
            if (StringUtils.isNotBlank(x.getRandomOrNot())) {
                if (x.getRandomOrNot().equals("1")) {
                    x.setSteps("随机");
                }
            }
            if (StringUtils.isNotBlank(x.getSteps())) {
                if (x.getSteps().equals("0")) {
                    x.setSteps("随机");
                }
            }

            if (StringUtils.isEmpty(x.getPreviousOccasion())) {
                x.setPreviousOccasion("暂无记录");
            }
        }
        return AjaxResult.doSuccess(list);
    }

    /**
     * @param autoXiaomiEntity
     * @return
     */
    @Override
    public Map<String, Object> editAutoXiaomi(AutoXiaomiEntity autoXiaomiEntity) {
        Map<String, Object> map = new HashMap<>();
        AutoXiaomiEntity xiaomiEntity = autoXiaomiDao.selectById(autoXiaomiEntity.getId());
        if (xiaomiEntity == null || xiaomiEntity.getId() == null) {
            map.put("code", -1);
            map.put("msg", "参数错误！");
            return map;
        }
        //放行管理员
        String role = userDao.getRole(autoXiaomiEntity.getUserId());
        if (!xiaomiEntity.getUserId().equals(autoXiaomiEntity.getUserId()) && !role.equals("ROLE_ADMIN")) {
            map.put("code", 403);
            map.put("msg", "你没有权限修改！");
            return map;
        }
        Map<String, Object> checkForm = checkForm(autoXiaomiEntity, true);
        if (!(boolean) checkForm.get("flag")) {
            return checkForm;
        }

        LambdaQueryWrapper<AutoXiaomiEntity> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(AutoXiaomiEntity::getPhone, autoXiaomiEntity.getPhone());
        queryWrapper.ne(AutoXiaomiEntity::getId, autoXiaomiEntity.getId());
        AutoXiaomiEntity one = this.getOne(queryWrapper);
        if (one != null) {
            map.put("code", 0);
            map.put("msg", "该账号已经存在！");
            return map;
        }

        //信息检查完毕后，尝试登录账号，进行验证
        Map<String, String> loginMap = XiaomiLogin.login(autoXiaomiEntity.getPhone(), autoXiaomiEntity.getPassword());
        if ("false".equals(loginMap.get("flag"))) {
            map.put("code", "-1");
            map.put("msg", loginMap.get("msg"));
            return map;
        }

        //如果用户的任务状态是已过期的，就把状态修改成等待运行
        if ("500".equals(xiaomiEntity.getStatus())) {
            autoXiaomiEntity.setStatus("100");
        }
        boolean b = this.updateById(autoXiaomiEntity);

        if (b) {
            map.put("code", 200);
            map.put("msg", "操作成功！");
            return map;
        }
        map.put("code", 0);
        map.put("msg", "操作失败！");
        return map;
    }

    @Override
    public AjaxResult delete(Integer id) throws Exception {
        //校验用户id
        Integer userid = SessionUtils.getPrincipal().getId();
        if (id == null || id == 0) {
            return AjaxResult.doError("传参不能为空！");
        }
        List<AutoXiaomiEntity> xiaomiEntityList = queryUserId(id);
        boolean flag = false;
        for (AutoXiaomiEntity xiaomiEntity : xiaomiEntityList) {
            int autoId = xiaomiEntity.getId();
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
        autoLog.setType("xiaomi");
        autoLogDao.deleteByAutoId(autoLog);
        //最后删除主要数据
        boolean b = this.removeById(id);
        if (b) {
            return AjaxResult.doSuccess("删除成功");
        }
        //删除失败后回滚
        throw new Exception("删除失败！");
    }

    @Override
    public Map<String, String> addXiaoMi(AutoXiaomiEntity autoXiaomiEntity) {
        Map<String, String> map = new HashMap<>();
        Map<String, Object> stringObjectMap = checkForm(autoXiaomiEntity, false);
        if (!(boolean) stringObjectMap.get("flag")) {
            map.put("code", "-1");
            map.put("msg", (String) stringObjectMap.get("msg"));
            return map;
        }
        //信息检查完毕后，尝试登录账号，进行验证
        Map<String, String> loginMap = XiaomiLogin.login(autoXiaomiEntity.getPhone(), autoXiaomiEntity.getPassword());
        if ("false".equals(loginMap.get("flag"))) {
            map.put("code", "-1");
            map.put("msg", loginMap.get("msg"));
            return map;
        }

        LambdaQueryWrapper<AutoXiaomiEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AutoXiaomiEntity::getPhone, autoXiaomiEntity.getPhone());
        AutoXiaomiEntity entity = this.getOne(queryWrapper);

        //判断数据是否存在，如果存在则不能添加！
        if (entity == null) {
            //设置状态默认为：等待运行
            autoXiaomiEntity.setStatus("100");
            autoXiaomiEntity.setCreatedTime(new Date());
            this.save(autoXiaomiEntity);
        } else {
            map.put("code", "-1");
            map.put("msg", "该账号已存在！");
            return map;
        }
        map.put("code", "200");
        map.put("msg", loginMap.get("msg"));
        return map;
    }

    @Override
    public Map<String, Object> doDailyTaskPersonal(Integer autoId, Integer userId) {
        Map<String, Object> map = new HashMap<>();
        AutoXiaomiEntity autoXiaomiEntity = autoXiaomiDao.selectById(autoId);
        if (autoXiaomiEntity == null || autoXiaomiEntity.getId() == null) {
            map.put("code", 500);
            map.put("msg", "参数错误！");
            return map;
        }

        String role = userDao.getRole(autoXiaomiEntity.getUserId());
        if (!autoXiaomiEntity.getUserId().equals(userId) && !role.equals("ROLE_ADMIN")) {
            map.put("code", 403);
            map.put("msg", "你没有权限执行！");
            return map;
        }
        if (autoXiaomiEntity.getStatus().equals("1")) {
            map.put("code", 1);
            map.put("msg", "任务已经在运行啦~请不要重复执行");
            return map;
        }

        Thread t = new Thread(() -> {
            XiaoMiTask miTask = new XiaoMiTask();
            miTask.runTask(autoId, userId, autoXiaomiEntity);
        });

        t.start();
        map.put("code", 200);
        map.put("msg", "运行指令已发送，请稍后查看运行状态");
        return map;
    }

    private Map<String, Object> checkForm(AutoXiaomiEntity autoXiaomiEntity, boolean flag) {
        Map<String, Object> map = new HashMap<>();

        if (!flag) {
            String name = autoXiaomiEntity.getName();
            String phone = autoXiaomiEntity.getPhone();
            String password = autoXiaomiEntity.getPassword();
            if (StringUtils.isBlank(name) || StringUtils.isBlank(phone) || StringUtils.isBlank(password)) {
                map.put("flag", false);
                map.put("msg", "前三项不能为空！");
                return map;
            }

        }
        if (StringUtils.isBlank(autoXiaomiEntity.getSteps()) && autoXiaomiEntity.getRandomOrNot().equals("0")) {
            map.put("flag", false);
            map.put("msg", "步数为空的情况下，随机步数不能关闭！");
            return map;
        }

        if (StringUtils.isBlank(autoXiaomiEntity.getWebhook())) {
            autoXiaomiEntity.setWebhook("");
        }
        if (StringUtils.isNotBlank(autoXiaomiEntity.getSteps())) {
            int num = Integer.parseInt(autoXiaomiEntity.getSteps());
            if (num > 98800) {
                map.put("flag", false);
                map.put("msg", "设置的步数不能超过98800");
                return map;
            }
        }

        map.put("flag", true);
        return map;
    }

    private List<AutoXiaomiEntity> queryUserId(Integer userid) {
        LambdaQueryWrapper<AutoXiaomiEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AutoXiaomiEntity::getUserId, userid);
        return this.list(queryWrapper);
    }
}
