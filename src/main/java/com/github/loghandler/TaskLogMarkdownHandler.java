package com.github.loghandler;

import com.github.system.task.dto.TaskLog;
import com.github.system.task.service.TaskLogDisplayHandler;

import java.util.List;

public class TaskLogMarkdownHandler implements TaskLogDisplayHandler {
    @Override
    public String getName() {
        return "Markdown";
    }

    @Override
    public Object handle(TaskLog taskLog) {
        List<TaskLog.LogInfo> logList = taskLog.getLogList();
        StringBuilder sb = new StringBuilder();
        for (TaskLog.LogInfo logInfo : logList) {

        }
        return sb.toString();
    }
}
