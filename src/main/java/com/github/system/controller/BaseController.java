package com.github.system.controller;

import cn.hutool.core.util.StrUtil;
import com.github.system.domain.ResuTree;
import com.github.system.domain.ResultTable;
import com.github.system.entity.AjaxResult;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * web层通用数据处理
 *
 * @author fuce
 * @ClassName: BaseController
 * @date 2018年8月18日
 */
@Controller
public class BaseController {

    /**
     * Describe: 返回 Tree 数据
     * Param data
     * Return Tree数据
     */
    protected static ResuTree dataTree(Object data) {
        ResuTree resuTree = new ResuTree();
        resuTree.setData(data);
        return resuTree;
    }

    /**
     * Describe: 返回数据表格数据 分页
     * Param data
     * Return 表格分页数据
     */
    protected static ResultTable pageTable(Object data, long count) {
        return ResultTable.pageTable(count, data);
    }

    /**
     * Describe: 返回数据表格数据
     * Param data
     * Return 表格分页数据
     */
    protected static ResultTable dataTable(Object data) {
        return ResultTable.dataTable(data);
    }

    /**
     * Describe: 返回树状表格数据 分页
     * Param data
     * Return 表格分页数据
     */
    protected static ResultTable treeTable(Object data) {
        return ResultTable.dataTable(data);
    }

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected AjaxResult toAjax(int rows) {
        return rows > 0 ? success() : error();
    }

    /**
     * 返回成功
     */
    public AjaxResult success() {
        return AjaxResult.doSuccess();
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error() {
        return AjaxResult.doError();
    }

    /**
     * 返回成功消息
     */
    public AjaxResult success(String message) {
        return AjaxResult.doSuccess(message);
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error(String message) {
        return AjaxResult.doError(message);
    }

    /**
     * 返回错误码消息
     */
    public AjaxResult error(int code, String message) {
        return AjaxResult.doError(message, "", code);
    }

    /**
     * 返回object数据
     */
    public AjaxResult retobject(int code, Object data) {
        return AjaxResult.doSuccess("", data, code);
    }

    /**
     * 页面跳转
     */
    public String redirect(String url) {
        return StrUtil.format("redirect:{}", url);
    }


}
