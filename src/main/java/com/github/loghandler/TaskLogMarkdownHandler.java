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
        return handle(taskLog, null);
    }

    @Override
    public Object handle(TaskLog taskLog, PushData<?> pushData) {
        List<TaskLog.LogInfo> logList = taskLog.getLogList();
        StringBuilder sb = new StringBuilder();
        // 构建标题
        if (pushData != null) {
            sb.append("# ").append(pushData.getTitle()).append("\n\n");
        }
        // 构建树结构
        List<TaskLogTree> taskLogTrees = TaskLogUtil.buildTaskLogTree(logList);
        buildMarkdown(taskLogTrees, sb, 3); // 从h3开始观感比较正常一点
        return sb.toString();
    }

    private void buildMarkdown(List<TaskLogTree> taskLogTrees, StringBuilder sb, int depth) {
        for (TaskLogTree taskLogTree : taskLogTrees) {
            String style = taskLogTree.getStyle();
            String data = taskLogTree.getData();
            List<TaskLogTree> children = taskLogTree.getChildren();
            if (children != null) {
                sb.append(StrUtil.repeat("#", depth)).append(" ").append(getStatusSpan(style, data)).append("\n\n");
//                sb.append(StrUtil.repeat("#", depth)).append(" ").append(data).append(getStatusSpan(style, null)).append("\n\n");
                buildMarkdown(children, sb, depth + 1);
            } else {
                sb.append(getStatusSpan(style, null));
                sb.append(data).append("\n\n");
            }
        }
    }

    private String getStatusSpan(String style, String customString) {
        if ("warn".equals(style)) {
            return "<span style='color: #e6a23c'>" + (StrUtil.isNotBlank(customString) ? customString : "[警告]") + "</span>";
        }
        if ("error".equals(style)) {
            return "<span style='color: #f56c6c'>" + (StrUtil.isNotBlank(customString) ? customString : "[错误]") + "</span>";
        }
        if ("success".equals(style)) {
            return "<span style='color: #67c23a'>" + (StrUtil.isNotBlank(customString) ? customString : "[成功]") + "</span>";
        }
        if ("info".equals(style)) {
            return "<span style='color: #909399'>" + (StrUtil.isNotBlank(customString) ? customString : "[信息]") + "</span>";
        }
        return "";
    }

}
