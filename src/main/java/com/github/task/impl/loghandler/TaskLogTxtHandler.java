package com.github.task.impl.loghandler;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.task.base.dto.TaskLog;
import com.github.task.base.service.TaskLogDisplayHandler;

import java.util.List;

public class TaskLogTxtHandler implements TaskLogDisplayHandler {
    @Override
    public String getName() {
        return "TXT";
    }

    @Override
    public Object handle(TaskLog taskLog) {
        List<TaskLog.LogInfo> logList = taskLog.getLogList();
        StringBuilder sb = new StringBuilder();
        for (TaskLog.LogInfo logInfo : logList) {
            TaskLog.LogType logType = logInfo.logType();
            if (logType != TaskLog.LogType.INFO) {
                sb.append("[").append(logType.name()).append("]");
            }
            sb.append(logInfo.text()).append("\n");
        }
        return sb.toString();
    }
}
