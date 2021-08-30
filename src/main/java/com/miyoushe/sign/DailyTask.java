package com.miyoushe.sign;

import com.miyoushe.sign.gs.GenShinSignMiHoYo;
import com.miyoushe.sign.gs.GenshinHelperProperties;
import com.miyoushe.sign.gs.MiHoYoConfig;
import com.miyoushe.sign.gs.MiHoYoSignMiHoYo;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


/**
 * @Author ponking
 * @Date 2021/5/31 15:54
 */
public class DailyTask implements Runnable {

    private static final Logger log = LogManager.getLogger(DailyTask.class);

    public GenShinSignMiHoYo genShinSign;

    public MiHoYoSignMiHoYo miHoYoSign;


    /**
     * @param account 账号配置信息
     */
    public DailyTask(GenshinHelperProperties.Account account) {
        // 默认目录,因为云腾讯函数，只能在/tmp有读取日志权限，故手动设置腾讯云函数使用/tmp
//        if (System.getProperty(Constant.GENSHIN_ENV_LOG_PATH).equals(Constant.ENV_TENCENT_LOG_PATH)) {
//            this.logFilePath = Constant.ENV_TENCENT_LOG_PATH;
//        } else {
//            String baseDir = System.getProperty("user.dir");
//            this.logFilePath = baseDir + File.separator + "logs";
//        }

        genShinSign = new GenShinSignMiHoYo(account.getCookie());
        if (account.getStuid() != null && account.getStoken() != null) {
            miHoYoSign = new MiHoYoSignMiHoYo(MiHoYoConfig.HubsEnum.YS.getGame(), account.getStuid(), account.getStoken());
        }
    }

    @SneakyThrows
    @Override
    public void run() {
        doDailyTask();
    }

    public Map<String,Object> doDailyTask() {
        Map<String,Object> result = new HashMap<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StringBuilder stringBuilder = new StringBuilder();
        log.info("开始执行时间[ {} ],执行环境[ {} ]", dtf.format(LocalDateTime.now()), System.getProperty(Constant.GENSHIN_EXEC));
        stringBuilder.append("开始执行时间[ ").append(dtf.format(LocalDateTime.now())).append(" ],执行环境[ ").append(System.getProperty(Constant.GENSHIN_EXEC)).append(" ]");
        if (genShinSign != null) {
            Map<String, Object> map = genShinSign.doSign();
            if (!(boolean) map.get("flag")){
                //登录失败，直接返回
                map.put("msg",stringBuilder.toString() + "\n" + map.get("msg"));
                return map;
            }
            stringBuilder.append("\n").append(map.get("msg"));
        }
        if (miHoYoSign != null) {
            try {
                Map<String, Object> map = miHoYoSign.doSingleThreadSign();
                stringBuilder.append("\n").append(map.get("msg"));
            } catch (Exception e) {
                stringBuilder.append("\n").append("[ERROR]miHoYoThreadSign执行异常！").append(e.getMessage());
                e.printStackTrace();
            }
        }
        if (miHoYoSign != null) {
            try {
                Map<String, Object> map = miHoYoSign.doSign();
                stringBuilder.append("\n").append(map.get("msg"));
            } catch (Exception e) {
                stringBuilder.append("\n").append("[ERROR]miHoYoSign执行异常！").append(e.getMessage());
                e.printStackTrace();
            }
        }
        result.put("msg",stringBuilder.toString());
        result.put("flag",true);
        return result;
//        if (pushed && messagePush != null) {
//
//            String fileName = Thread.currentThread().getName() + ".log";
//            messagePush.sendMessage("原神签到", FileUtils.loadDaily(logFilePath + File.separator + fileName));
//        }
    }

}
