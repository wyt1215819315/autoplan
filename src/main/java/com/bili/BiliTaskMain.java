package com.bili;

import com.alibaba.fastjson.JSON;
import com.bili.model.task.BiliTaskInfo;
import com.bili.model.task.config.BiliTaskConfig;
import com.bili.util.BiliTaskUtil;
import com.misec.config.TaskConfig;
import com.oldwu.entity.AutoBilibili;
import com.oldwu.entity.TaskResult;
import com.oldwu.util.NumberUtil;

public class BiliTaskMain {
    /**
     * 定义默认单个任务执行间隔时间，单位ms
     * 此延迟由系统从数据库中读取，不由用户指定，防止任务队列执行缓慢
     */
    private final Long defaultDelay = 2000L;

    public static void main(String[] args) {
        BiliTaskMain biliTaskMain = new BiliTaskMain();
        AutoBilibili autoBilibili = new AutoBilibili();
        TaskResult run = biliTaskMain.run(autoBilibili);
        System.out.println(run);
    }

    /**
     * bili任务入口
     * @param autoBilibili 任务参数
     * @return 任务结果
     */
    public TaskResult run(AutoBilibili autoBilibili){
        //加载taskConfig任务配置
        String taskConfigJsonStr = autoBilibili.getTaskConfig();
        BiliTaskConfig taskConfig = JSON.parseObject(taskConfigJsonStr, BiliTaskConfig.class);
        BiliTaskInfo taskInfo = new BiliTaskInfo(autoBilibili.getDedeuserid(), autoBilibili.getSessdata(), autoBilibili.getBiliJct());
        taskInfo.setTaskConfig(taskConfig);
        //只要有一个任务失败了，这个标识就应该为false
        boolean flag = true;
        //实例化一个对象
        BiliTaskUtil biliTaskUtil = new BiliTaskUtil(taskInfo);
        //创建一个String来保存简易日志
        String simpleLog = "";
        //首先需要执行登录任务以及硬币检查任务
        try {
            biliTaskUtil.userCheck();
        } catch (Exception e) {
            //登录检查失败，直接返回失败
            return TaskResult.doLoginError(e.getMessage(), biliTaskUtil.getLog());
        }
        try {
            biliTaskUtil.coinLogs();
        } catch (Exception e) {
            simpleLog += e.getMessage() + "\n";
        }

        //传统办法生成随机数来决定任务执行顺序
        int[] randoms = NumberUtil.getRandoms(0, 6, 7);
        for (int random : randoms) {
            switch (random){
                //需要检测某些特定任务是否开启，没有开启则跳过
                case 0:
                    break;
            }
            //随机延迟执行下一个任务
        }

        //判断任务是否全部失败

        //判断任务是否部分失败
        if (!flag){
            return TaskResult.doTaskPartError(biliTaskUtil.getLog());
        }
        //任务执行成功
        return TaskResult.doAllSuccess(biliTaskUtil.getLog());
    }

}
