package com.github.loghandler.util;

import cn.hutool.core.util.StrUtil;
import com.github.loghandler.dto.TaskLogTree;
import com.github.system.task.dto.TaskLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TaskLogUtil {


    /**
     * 构建树结构的日志
     * chatppt，我的超人！
     *
     * @param logList 日志数组
     * @return 树结构数组
     */
    public static List<TaskLogTree> buildTaskLogTree(List<TaskLog.LogInfo> logList) {
        List<TaskLogTree> logTreeList = new ArrayList<>();
        buildTreeRecursive(logList, logTreeList, 0);
        handleLogTreeList(logTreeList);
        return logTreeList;
    }

    private static void handleLogTreeList(List<TaskLogTree> logTreeList) {
        for (TaskLogTree taskLogTree : logTreeList) {
            handleLogTree(taskLogTree);
        }
    }

    private static void handleLogTree(TaskLogTree taskLogTree) {
        if (taskLogTree.getChildren() != null) {
            // 有子树的时候要判断子树是不是都是成功的状态，先循环一次找出明显失败的标识
            for (TaskLogTree child : taskLogTree.getChildren()) {
                if (TaskLog.LogType.TASK_ERROR == child.getLogType() || TaskLog.LogType.USER_CHECK_ERROR == child.getLogType()) {
                    taskLogTree.setLogType(child.getLogType());
                    taskLogTree.setStyle("error");
                    break;
                }
            }
            // 将子树都处理好
            for (TaskLogTree child : taskLogTree.getChildren()) {
                handleLogTree(child);
            }
            // 要是还没得出结论的话再递归一次看下子树的状态
            if (taskLogTree.getStyle() == null) {
                taskLogTree.setStyle(getTaskStatus(taskLogTree.getChildren()));
            }
            // 最后一遍是为了清理没用的对象
            clearLogTree(taskLogTree.getChildren());
        } else {
            // 这种一般都要看他本身的状态
            taskLogTree.setStyle(getInfoStyle(taskLogTree));
        }
    }

    private static String getTaskStatus(List<TaskLogTree> taskLogTreeList) {
        String resultStyle = "info";
        // 扫错误
        for (TaskLogTree taskLogTree : taskLogTreeList) {
            TaskLog.LogType logType = taskLogTree.getLogType();
            if (logType == TaskLog.LogType.TASK_ERROR) {
                return "error";
            }
            if ("error".equals(getInfoStyle(taskLogTree))) {
                resultStyle = "warn";
            }
        }
        // 扫警告
        for (TaskLogTree taskLogTree : taskLogTreeList) {
            TaskLog.LogType logType = taskLogTree.getLogType();
            if (logType.name().toLowerCase().contains("warn")) {
                return "warn";
            }
        }
        if (!"warn".equals(resultStyle)) {
            resultStyle = "success";
        }
        return resultStyle;
    }

    private static String getInfoStyle(TaskLogTree taskLogTree) {
        TaskLog.LogType logType = taskLogTree.getLogType();
        String data = taskLogTree.getData().toLowerCase();
        if (logType == TaskLog.LogType.TASK_ERROR || logType == TaskLog.LogType.USER_CHECK_ERROR) {
            return "error";
        } else if (logType == TaskLog.LogType.TASK_RESULT) {
            if (data.contains("cannot") || data.contains("error")) {
                return "error";
            } else {
                return "info";
            }
        } else if (StrUtil.containsAny(data, "失败", "错误", "出错")) {
            return "error";
        } else if (logType.name().toLowerCase().contains("warn")) {
            return "warn";
        }
        if (data.contains("成功")) {
            return "success";
        }
        return "info";
    }

    private static void clearLogTree(List<TaskLogTree> logTreeList) {
        Set<TaskLog.LogType> notDisplayStyle = Set.of(TaskLog.LogType.TASK_ERROR, TaskLog.LogType.TASK_SUCCESS);
        List<TaskLogTree> removeList = new ArrayList<>();
        for (TaskLogTree taskLogTree : logTreeList) {
            if (taskLogTree.getChildren() != null) {
                clearLogTree(taskLogTree.getChildren());
            }
            if (notDisplayStyle.contains(taskLogTree.getLogType())) {
                removeList.add(taskLogTree);
            }
        }
        logTreeList.removeAll(removeList);
    }

    private static int buildTreeRecursive(List<TaskLog.LogInfo> logList, List<TaskLogTree> parentTreeList, int currentIndex) {
        while (currentIndex < logList.size()) {
            TaskLog.LogInfo logInfo = logList.get(currentIndex);

            if (logInfo.logType() == TaskLog.LogType.TASK_START) {
                // 开始新的子任务
                TaskLogTree newTree = new TaskLogTree();
                newTree.setLogType(logInfo.logType());
                newTree.setData(logInfo.text());
                newTree.setChildren(new ArrayList<>());
                parentTreeList.add(newTree);

                // 递归处理子任务
                currentIndex = buildTreeRecursive(logList, newTree.getChildren(), currentIndex + 1);
            } else if (logInfo.logType() == TaskLog.LogType.TASK_END) {
                // 结束当前子任务
                return currentIndex + 1;
            } else {
                // 添加普通日志
                TaskLogTree logTree = new TaskLogTree();
                logTree.setLogType(logInfo.logType());
                logTree.setData(logInfo.text());
                parentTreeList.add(logTree);
                currentIndex++;
            }
        }

        return currentIndex;
    }

    public static void main(String[] args) {
        // 示例用法
        List<TaskLog.LogInfo> logList = new ArrayList<>();
        logList.add(new TaskLog.LogInfo(TaskLog.LogType.TITLE, "Title"));
        logList.add(new TaskLog.LogInfo(TaskLog.LogType.TASK_START, "Task 1"));
        logList.add(new TaskLog.LogInfo(TaskLog.LogType.DEBUG, "Debug log 1"));
        logList.add(new TaskLog.LogInfo(TaskLog.LogType.TASK_START, "Subtask 1.1"));
        logList.add(new TaskLog.LogInfo(TaskLog.LogType.INFO, "Info log 1.1"));
        logList.add(new TaskLog.LogInfo(TaskLog.LogType.TASK_END, "Subtask 1.1"));
        logList.add(new TaskLog.LogInfo(TaskLog.LogType.WARN, "Warning log 1"));
        logList.add(new TaskLog.LogInfo(TaskLog.LogType.TASK_END, "Task 1"));
        logList.add(new TaskLog.LogInfo(TaskLog.LogType.ERROR, "Error log"));

        List<TaskLogTree> taskLogTreeList = buildTaskLogTree(logList);
        System.out.println(taskLogTreeList);
    }
}
