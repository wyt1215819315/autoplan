package com.github.loghandler;

import cn.hutool.json.JSONUtil;
import com.github.loghandler.dto.TaskLogTree;
import com.github.loghandler.util.TaskLogUtil;
import com.github.system.task.dto.TaskLog;
import com.github.system.task.service.TaskLogDisplayHandler;

import java.util.List;

public class TaskLogJsonTreeHandler implements TaskLogDisplayHandler {
    @Override
    public String getName() {
        return "JsonTree";
    }

    @Override
    public Object handle(TaskLog taskLog) {
        List<TaskLog.LogInfo> logList = taskLog.getLogList();
        List<TaskLogTree> taskLogTrees = TaskLogUtil.buildTaskLogTree(logList);
        return JSONUtil.toJsonStr(taskLogTrees);
    }
}
