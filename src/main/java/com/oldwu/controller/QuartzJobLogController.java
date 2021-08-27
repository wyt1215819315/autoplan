package com.oldwu.controller;

import com.github.pagehelper.PageInfo;
import com.oldwu.domain.AjaxResult;
import com.oldwu.domain.ResultTable;
import com.oldwu.domain.SysQuartzJobLog;
import com.oldwu.domain.Tablepar;
import com.oldwu.service.SysQuartzJobLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

/**
 * 定时任务日志Controller
 *
 * @author fuce
 * @ClassName: QuartzJobLogController
 * @date 2019-11-20 22:51
 */
@Controller
@RequestMapping("/admin/joblog")
public class QuartzJobLogController extends BaseController{

    private final String prefix = "sysQuartzJobLog";
    @Autowired
    private SysQuartzJobLogService sysQuartzJobLogService;

    /**
     * 展示跳转页面
     *
     * @param model
     * @return
     * @author fuce
     * @Date 2019年11月11日 下午4:01:13
     */
    @GetMapping("/view")
    public String view(ModelMap model) {
        return  prefix + "/list";
    }

    /**
     * 定时任务调度日志list
     *
     * @param tablepar
     * @param searchText
     * @return
     * @author fuce
     * @Date 2019年11月11日 下午4:01:26
     */
    //@Log(title = "定时任务调度日志表集合查询", action = "111")
    @GetMapping("/list")
    @ResponseBody
    public ResultTable list(Tablepar tablepar, String searchText) {
        PageInfo<SysQuartzJobLog> page = sysQuartzJobLogService.list(tablepar, searchText);
        return pageTable(page.getList(), page.getTotal());
    }

    /**
     * 查看详情
     *
     * @param modelMap
     * @return
     * @author fuce
     * @Date 2019年9月14日 下午11:50:42
     */
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") String id, ModelMap modelMap) {
        SysQuartzJobLog log = sysQuartzJobLogService.selectByPrimaryKey(id);
        modelMap.put("SysQuartzJobLog", log);
        return prefix + "/detail";
    }


    /**
     * 定时任务日志删除
     *
     * @param ids id集合
     * @return
     * @author fuce
     * @Date 2019年11月20日 下午10:51:52
     */
    //@Log(title = "定时任务调度日志表删除", action = "111")
    @DeleteMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        int b = sysQuartzJobLogService.deleteByPrimaryKey(ids);
        if (b > 0) {
            return success();
        } else {
            return error();
        }
    }


}
