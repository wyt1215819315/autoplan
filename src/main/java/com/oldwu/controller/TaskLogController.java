package com.oldwu.controller;

import com.oldwu.entity.AutoLog;
import com.oldwu.security.utils.SessionUtils;
import com.oldwu.service.LogService;
import com.oldwu.service.UserService;
import com.oldwu.vo.PageDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

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
}
