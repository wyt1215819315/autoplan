package com.github.task.impl.loghandler;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.task.base.dto.TaskLog;
import com.github.task.base.service.TaskLogDisplayHandler;

import java.util.List;

public class TaskLogJsonHandler implements TaskLogDisplayHandler {
    @Override
    public String getName() {
        return "JSON";
    }

    @Override
    public Object handle(TaskLog taskLog) {
        List<TaskLog.LogInfo> logList = taskLog.getLogList();
        JSONArray jsonArray = new JSONArray();
        for (TaskLog.LogInfo logInfo : logList) {
            JSONObject obj = new JSONObject();
            obj.set("style", logInfo.logType().name());
            obj.set("data", logInfo.text());
            jsonArray.add(obj);
        }
        return JSONUtil.toJsonStr(jsonArray);
    }
}
