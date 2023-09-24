package com.system.controller;

import com.system.dao.UserDao;
import com.system.entity.AjaxResult;
import com.system.entity.AutoLog;
import com.system.security.utils.SessionUtils;
import com.system.service.LogService;
import com.system.service.UserService;
import com.system.vo.PageDataVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.Map;

/**
 * @author chenjj
 * @date 2022/09/06
 */
@RestController
@RequestMapping("admin/taskLog")
public class TaskLogController {

    @Autowired
    private LogService logService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    /**
     * 任务日志查询页
     *
     * @return ModelAndView
     */
    @RequestMapping("/page")
    public ModelAndView page() {
        return new ModelAndView("taskLog/list");
    }

    @RequestMapping("/list")
    public PageDataVO<AutoLog> list(Integer page, Integer limit, String taskType, String taskStatus) {
        Integer userid = SessionUtils.getPrincipal().getId();

        if (!userService.getRole(userid).equals("ROLE_ADMIN")) {
            return new PageDataVO<>();
        }

        return logService.queryPageList(page, limit, taskType, taskStatus);
    }

    @PostMapping("/getlog")
    public AjaxResult getLog(@RequestParam Map<String, String> params) {
        if (!params.containsKey("id") || StringUtils.isBlank(params.get("id"))){
            return AjaxResult.doError("ID不能为空！");
        }

        if (!params.containsKey("autoId") || StringUtils.isBlank(params.get("autoId"))){
            return AjaxResult.doError("任务ID不能为空！");
        }

        if (!params.containsKey("type") || StringUtils.isBlank(params.get("type"))){
            return AjaxResult.doError("TYPE不能为空！");
        }

        if (!params.containsKey("userId") || StringUtils.isBlank(params.get("userId"))){
            return AjaxResult.doError("userId不能为空！");
        }

        Long id = Long.valueOf(params.get("id"));
        Integer autoId = Integer.valueOf(params.get("autoId"));
        String type = params.get("type");
        Integer userId = Integer.valueOf(params.get("userId"));

        AutoLog log = logService.getLog(id, autoId, type, userId);

        if (log == null || log.getId() == null) {
            AutoLog newLog = new AutoLog();
            newLog.setText("当前无日志");
            newLog.setDate(new Date());
            return AjaxResult.doSuccess(newLog);
        }

        log.setText(log.getText().replaceAll("\n", "<br/>"));
        return AjaxResult.doSuccess(log);
    }
}
