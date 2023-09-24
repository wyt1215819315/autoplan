package com.task.bili;

import com.alibaba.fastjson.JSON;
import com.task.bili.model.AutoBilibili;
import com.task.bili.model.task.BiliData;
import com.task.bili.model.task.BiliTaskInfo;
import com.task.bili.model.task.config.BiliTaskConfig;
import com.task.bili.util.BiliTaskUtil;
import com.system.constant.SystemConstant;
import com.system.entity.TaskResult;
import com.system.util.DateUtils;
import com.system.util.NumberUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BiliTaskMain {
    private final Log logger = LogFactory.getLog(BiliTaskMain.class);
    //创建一个String来保存简易日志
    StringBuilder simpleLog = new StringBuilder();
    //只要有一个任务失败了，这个标识就应该为false
    boolean flag = true;


    public static void main(String[] args) {
        BiliTaskMain biliTaskMain = new BiliTaskMain();
        AutoBilibili autoBilibili = new AutoBilibili();
        autoBilibili.setBiliJct("");
        autoBilibili.setDedeuserid("");
        autoBilibili.setSessdata("");
        autoBilibili.setTaskConfig("");
        TaskResult run = biliTaskMain.run(autoBilibili);
        System.out.println(run.toString());
    }

    /**
     * bili任务入口
     *
     * @param autoBilibili 任务参数
     * @return 任务结果
     */
    public TaskResult run(AutoBilibili autoBilibili) {
        //加载taskConfig任务配置
        String taskConfigJsonStr = autoBilibili.getTaskConfig();
        BiliTaskConfig taskConfig = JSON.parseObject(taskConfigJsonStr, BiliTaskConfig.class);
        BiliTaskInfo taskInfo = new BiliTaskInfo(autoBilibili.getDedeuserid(), autoBilibili.getSessdata(), autoBilibili.getBiliJct());
        taskInfo.setTaskConfig(taskConfig);
        //实例化一个对象
        BiliTaskUtil biliTaskUtil = new BiliTaskUtil(taskInfo);
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
            simpleLog.append(e.getMessage()).append("\n");
        }

        //传统办法生成随机数来决定任务执行顺序
        int[] randoms = NumberUtil.getRandoms(0, 9, 10);
        for (int random : randoms) {
            try {
                switch (random) {
                    //需要检测某些特定任务是否开启，没有开启则跳过
                    case 0:
                        biliTaskUtil.cartoonSign();
                        break;
                    case 1:
                        biliTaskUtil.chargeMe();
                        break;
                    case 2:
                        biliTaskUtil.coinAdd();
                        break;
                    case 3:
                        biliTaskUtil.liveGift();
                        break;
                    case 4:
                        biliTaskUtil.liveSign();
                        break;
                    case 5:
                        biliTaskUtil.matchGame();
                        break;
                    case 6:
                        biliTaskUtil.readCartoon();
                        break;
                    case 7:
                        biliTaskUtil.silver2Coin();
                        break;
                    case 8:
                        biliTaskUtil.vipCartoonRec();
                        break;
                    case 9:
                        biliTaskUtil.watchVideo();
                        break;
                }
            } catch (Exception e) {
                logger.warn("b站任务运行时出错：" + e.getMessage());
                simpleLog.append("任务运行中出现错误：").append(e.getMessage()).append("\n");
                flag = false;
            }
            //随机延迟执行下一个任务
            DateUtils.threadSleep(SystemConstant.BILI_DEFAULT_DELAY);
        }
        //执行最后的统计任务
        BiliData biliData = null;
        try {
            biliData = biliTaskUtil.calculateUpgradeDays();
        } catch (Exception e) {
            simpleLog.append(e.getMessage()).append("\n");
        }
        //判断任务是否部分失败
        if (!flag) {
            return TaskResult.doTaskPartError(simpleLog.toString(), biliTaskUtil.getLog(), biliData);
        }
        //任务执行成功
        return TaskResult.doAllSuccess(simpleLog.toString(), biliTaskUtil.getLog(), biliData);
    }

}


//暂时不做部分失败判断，感觉没必要
//    int[] randoms = NumberUtil.getRandoms(0, 9, 10);
//    boolean flag0 = true, flag1 = true, flag2 = true, flag3 = true, flag4 = true, flag5 = true, flag6 = true, flag7 = true, flag8 = true, flag9 = true;
////随机延迟执行下一个任务
//        for (int random : randoms) {
//                switch (random) {
//                //需要检测某些特定任务是否开启，没有开启则跳过
//                case 0:
//                try {
//                biliTaskUtil.cartoonSign();
//                } catch (Exception e) {
//                simpleLog.append(e.getMessage()).append("\n");
//                flag0 = false;
//                }
//                break;
//                case 1:
//                try {
//                biliTaskUtil.chargeMe();
//                } catch (Exception e) {
//                simpleLog.append(e.getMessage()).append("\n");
//                flag1 = false;
//                }
//                break;
//                case 2:
//                try {
//                biliTaskUtil.coinAdd();
//                } catch (Exception e) {
//                simpleLog.append(e.getMessage()).append("\n");
//                flag2 = false;
//                }
//                break;
//                case 3:
//                try {
//                biliTaskUtil.liveGift();
//                } catch (Exception e) {
//                simpleLog.append(e.getMessage()).append("\n");
//                flag3 = false;
//                }
//                break;
//                case 4:
//                try {
//                biliTaskUtil.liveSign();
//                } catch (Exception e) {
//                simpleLog.append(e.getMessage()).append("\n");
//                flag4 = false;
//                }
//                break;
//                case 5:
//                try {
//                biliTaskUtil.matchGame();
//                } catch (Exception e) {
//                simpleLog.append(e.getMessage()).append("\n");
//                flag5 = false;
//                }
//                break;
//                case 6:
//                try {
//                biliTaskUtil.readCartoon();
//                } catch (Exception e) {
//                simpleLog.append(e.getMessage()).append("\n");
//                flag6 = false;
//                }
//                break;
//                case 7:
//                try {
//                biliTaskUtil.silver2Coin();
//                } catch (Exception e) {
//                simpleLog.append(e.getMessage()).append("\n");
//                flag7 = false;
//                }
//                break;
//                case 8:
//                try {
//                biliTaskUtil.vipCartoonRec();
//                } catch (Exception e) {
//                simpleLog.append(e.getMessage()).append("\n");
//                flag8 = false;
//                }
//                break;
//                case 9:
//                try {
//                biliTaskUtil.watchVideo();
//                } catch (Exception e) {
//                simpleLog.append(e.getMessage()).append("\n");
//                flag9 = false;
//                }
//                break;
//                }
//                }
//                //执行最后的统计任务
//                try {
//                biliTaskUtil.calculateUpgradeDays();
//                } catch (Exception e) {
//                simpleLog.append(e.getMessage()).append("\n");
//                }
//                //判断任务是否全部失败
//                if (flag0 && flag1){
//
//                }
