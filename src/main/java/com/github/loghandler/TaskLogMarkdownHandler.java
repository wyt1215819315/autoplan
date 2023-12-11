package com.github.loghandler;

import cn.hutool.core.util.StrUtil;
import com.github.loghandler.dto.TaskLogTree;
import com.github.loghandler.util.TaskLogUtil;
import com.github.push.base.model.PushData;
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
        return handle(taskLog,null);
    }

    @Override
    public Object handle(TaskLog taskLog, PushData<?> pushData) {
        List<TaskLog.LogInfo> logList = taskLog.getLogList();
        StringBuilder sb = new StringBuilder();
        // 构建标题
        if (pushData != null) {
            sb.append("# ").append(pushData.getTitle()).append("\n");
        }
        // 构建树结构
        List<TaskLogTree> taskLogTrees = TaskLogUtil.buildTaskLogTree(logList);
        buildMarkdown(taskLogTrees, sb, 2);
        return sb.toString();
    }

    private void buildMarkdown(List<TaskLogTree> taskLogTrees, StringBuilder sb, int depth) {
        for (TaskLogTree taskLogTree : taskLogTrees) {
            String style = taskLogTree.getStyle();
            String data = taskLogTree.getData();
            List<TaskLogTree> children = taskLogTree.getChildren();
            if (children != null && !children.isEmpty()) {
                sb.append(StrUtil.repeat("#", depth)).append(" ").append(data).append("\n");
                buildMarkdown(children, sb, depth + 1);
            } else {
                sb.append(data).append("\n");
            }
        }
    }

}
