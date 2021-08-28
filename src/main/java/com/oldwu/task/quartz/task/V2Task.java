package com.oldwu.task.quartz.task;

import cn.hutool.core.date.DateUtil;
import com.oldwu.dao.BiliUserDao;
import com.oldwu.entity.BiliPlan;
import com.oldwu.entity.BiliUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 测试类
 *
 * @CLASSNAME V2Task
 * @Description 定时调度具体工作类
 * @Auther Jan  橙寂
 * @DATE 2019/9/2 0002 15:33
 */
@Component("autoTask")
public class V2Task {
    private static BiliUserDao biliUserDao;

    @Autowired
    public void getBiliDao(BiliUserDao biliUserDao){
        V2Task.biliUserDao = biliUserDao;
    }

    /**
     * 无参的任务
     */
    public void runTask1() {
        System.out.println("正在执行定时任务，无参方法");
    }

    /**
     * 有参任务
     * 目前仅执行常见的数据类型  Integer Long  带L  string  带 ''  bool Double 带 d
     *
     * @param a
     * @param b
     */
    public void runTask2(Integer a, Long b, String c, Boolean d, Double e) {
        System.out.println("正在执行定时任务，带多个参数的方法" + a + "   " + b + " " + c + "  " + d + " " + e + "执行时间:" + DateUtil.now());
    }


}
