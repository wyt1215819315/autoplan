package com.github.task.mihoyousign.support;

import com.github.system.task.dto.TaskLog;
import com.github.system.task.dto.TaskResult;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DailyTask {

    private static final Logger log = LogManager.getLogger(DailyTask.class);

    public GenShinSignMiHoYo genShinSign;

    public StarRailSignMihoYo starRailSign;

    public MiHoYoSignMiHoYo miHoYoSign;


    /**
     * @param account 账号配置信息
     */
    public DailyTask(GenshinHelperProperties.Account account) {
        genShinSign = new GenShinSignMiHoYo(account.getCookie());
        starRailSign = new StarRailSignMihoYo(account.getCookie());
        if (account.getStuid() != null && account.getStoken() != null) {
            miHoYoSign = new MiHoYoSignMiHoYo(MiHoYoConfig.HubsEnum.YS.getGame(), account.getStuid(), account.getStoken());
        }
    }

    /**
     * 原神社区签到任务
     */
    public TaskResult genshinSign(TaskLog log) {
        if (genShinSign != null) {
            List<Map<String, Object>> list = genShinSign.doSign(log);

            for (Map<String, Object> map : list) {
                if (!(boolean) map.get("flag")){
                    //登录失败，直接返回
                    return TaskResult.doError((String) map.get("msg"));
                }
                log.info(map.get("msg"));
                stringBuilder.append("\n").append("-----------------\n").append(map.get("msg"));
            }
        }
    }

    public Map<String,Object> doDailyTask(TaskLog log) {
        Map<String,Object> result = new HashMap<>();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        StringBuilder stringBuilder = new StringBuilder();

        log.info("开始执行时间[ {} ] ]", dtf.format(LocalDateTime.now()));

        if (genShinSign != null) {
            List<Map<String, Object>> list = genShinSign.doSign();

            for (Map<String, Object> map : list) {
                if (!(boolean) map.get("flag")){
                    //登录失败，直接返回
                    map.put("msg",stringBuilder.toString() + "\n" + map.get("msg"));
                    return map;
                }

                stringBuilder.append("\n").append("-----------------\n").append(map.get("msg"));
            }
        }

        if (starRailSign != null) {
            List<Map<String, Object>> list = starRailSign.doSign();

            for (Map<String, Object> map : list) {
                if (!(boolean) map.get("flag")){
                    //登录失败，直接返回
                    map.put("msg", stringBuilder + "\n" + map.get("msg"));
                    return map;
                }

                stringBuilder.append("\n").append("-----------------\n").append(map.get("msg"));
            }
        }

        if (miHoYoSign != null) {
            try {
                Map<String, Object> map = miHoYoSign.doSingleThreadSign();
                stringBuilder.append("\n").append("-----------------\n").append(map.get("msg"));
            } catch (Exception e) {
                stringBuilder.append("\n").append("[ERROR]miHoYoThreadSign执行异常！").append(e.getMessage());
                e.printStackTrace();
            }
        }
        if (miHoYoSign != null) {
            try {
                List<Map<String, Object>> list = miHoYoSign.doSign();
                for (Map<String, Object> map : list) {
                    stringBuilder.append("\n").append("-----------------\n").append(map.get("msg"));
                }
            } catch (Exception e) {
                stringBuilder.append("\n").append("[ERROR]miHoYoSign执行异常！").append(e.getMessage());
                e.printStackTrace();
            }
        }
        result.put("msg",stringBuilder.toString());
        result.put("flag",true);
        return result;
    }

}
