package com.github.loghandler.util;

import com.github.loghandler.dto.TaskLogTree;
import com.github.system.task.dto.TaskLog;

import java.util.ArrayList;
import java.util.List;

public class TaskLogUtil {


    /**
     * 构建树结构的日志
     * chatppt，我的超人！
     * @param logList 日志数组
     * @return 树结构数组
     */
    public static List<TaskLogTree> buildTaskLogTree(List<TaskLog.LogInfo> logList) {
        List<TaskLogTree> logTreeList = new ArrayList<>();
        buildTreeRecursive(logList, logTreeList, 0);
        return logTreeList;
    }

    private static int buildTreeRecursive(List<TaskLog.LogInfo> logList, List<TaskLogTree> parentTreeList, int currentIndex) {
        while (currentIndex < logList.size()) {
            TaskLog.LogInfo logInfo = logList.get(currentIndex);

            if (logInfo.logType() == TaskLog.LogType.TASK_START) {
                // 开始新的子任务
                TaskLogTree newTree = new TaskLogTree();
                newTree.setStyle(logInfo.logType().name());
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
                logTree.setStyle(logInfo.logType().name());
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
