package com.oldwu.controller;

import com.github.pagehelper.PageInfo;
import com.oldwu.domain.ResultTable;
import com.oldwu.domain.SysQuartzJob;
import com.oldwu.domain.Tablepar;
import com.oldwu.entity.AjaxResult;
import com.oldwu.service.SysQuartzJobService;
import com.oldwu.util.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

/**
 * 
* @ClassName: QuartzJobController
* @author Jan 橙寂
* @date 2019-11-20 22:49
 */
@Controller
@RequestMapping("/admin/job")
public class QuartzJobController extends BaseController{

	private final String prefix = "sysQuartzJob";

	@Autowired
	private SysQuartzJobService sysQuartzJobService;
	
	/**
	 * 展示页面
	 * @param model
	 * @return
	 * @author fuce
	 * @Date 2019年11月11日 下午3:55:01
	 */
	@GetMapping("/view")
    public String view(ModelMap model)
    {
        return "sysQuartzJob/sysQuartzJoblist";
    }
	/**
	 * 定时任务调度list
	 * @param tablepar
	 * @param searchText
	 * @return
	 */
	@GetMapping("/list")
	@ResponseBody
	public ResultTable list(Tablepar tablepar, String searchText){
		PageInfo<SysQuartzJob> page=sysQuartzJobService.list(tablepar,searchText) ;
		return pageTable(page.getList(),page.getTotal());
	}
	
	/**
	 * 新增跳转页面
	 * @param modelMap
	 * @return
	 */
    @GetMapping("/add")
    public String add(ModelMap modelMap)
    {
        return prefix + "/add";
    }
	
    /**
     * 新增保存
     * @param sysQuartzJob
     * @return
     * @author fuce
     * @Date 2019年11月11日 下午4:00:08
     */
	@PostMapping("/add")
	@ResponseBody
	public AjaxResult add(SysQuartzJob sysQuartzJob){
		if (StringUtils.isEmpty(sysQuartzJob.getInvokeTarget())) {
			return error("调用目标字符串不能为空");
		}
		int b = sysQuartzJobService.insertSelective(sysQuartzJob);
		if (b > 0) {
			return success();
		} else {
			return error();
		}
	}
	
	/**
	 * 删除
	 * @param ids
	 * @return
	 */
	//@Log(title = "定时任务调度表删除", action = "111")
	@DeleteMapping("/remove")
	@ResponseBody
	public AjaxResult remove(String ids){
		int b=sysQuartzJobService.deleteByPrimaryKey(ids);
		if(b>0){
			return success();
		}else{
			return error();
		}
	}
	
	/**
	 * 检查
	 * @param
	 * @return
	 */
	@PostMapping("/checkNameUnique")
	@ResponseBody
	public int checkNameUnique(SysQuartzJob sysQuartzJob){
		int b=sysQuartzJobService.checkNameUnique(sysQuartzJob);
		if(b>0){
			return 1;
		}else{
			return 0;
		}
	}

	/**
	 * 修改跳转
	 * @param id
	 * @param mmap
	 * @return
	 */
	@GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, ModelMap mmap)
    {
        mmap.put("SysQuartzJob", sysQuartzJobService.selectByPrimaryKey(id));

        return prefix + "/edit";
    }
	
	/**
     * 修改保存
     */
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(SysQuartzJob record)
    {
        return toAjax(sysQuartzJobService.updateByPrimaryKeySelective(record));
    }

    /**
     * 任务调度状态修改
     */
    @PutMapping("/changeStatus")
    @ResponseBody
    public AjaxResult changeStatus(@RequestBody SysQuartzJob job) throws SchedulerException
    {
    	SysQuartzJob newJob = sysQuartzJobService.selectByPrimaryKey(job.getId());
        newJob.setStatus(job.getStatus());
        sysQuartzJobService.updateByPrimaryKeySelective(newJob);
        return toAjax(sysQuartzJobService.changeStatus(newJob));
    }
    
    /**
     * 任务调度立即执行一次
     */
    @GetMapping("/run/{id}")
    @ResponseBody
    public AjaxResult run(@PathVariable("id") String id) throws SchedulerException
    {
    	SysQuartzJob newJob = sysQuartzJobService.selectByPrimaryKey(id);
    	sysQuartzJobService.run(newJob);
        return success();
    }

	
}
