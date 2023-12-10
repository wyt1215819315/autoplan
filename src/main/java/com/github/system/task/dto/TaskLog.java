package com.github.system.task.dto;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

import static com.github.system.task.init.TaskInit.taskLogHandlerClassesMap;

@Getter
public class TaskLog {
    private final Log logger = LogFactory.getLog(TaskLog.class);

    public record LogInfo(LogType logType, String text) {
    }

    public enum LogType {
        TITLE,
        DEBUG, INFO, WARN, ERROR,
        TASK_SUCCESS, TASK_ERROR, TASK_RESULT,
        TASK_START, TASK_END,
        USER_CHECK_ERROR, USER_INFO_UPDATE_ERROR
    }

    private final List<LogInfo> logList = new ArrayList<>();

    public void append(LogInfo logInfo) {
        this.logList.add(logInfo);
    }

    public void debug(String text) {
        logger.debug(text);
    }

    public void debug(String text, Throwable e) {
        logger.debug(text, e);
    }

    public void info(String text) {
        logList.add(new LogInfo(LogType.INFO, text));
    }

    public void info(CharSequence template, Object... params) {
        String text = StrUtil.format(template, params);
        info(text);
    }

    public void warn(String text) {
        logList.add(new LogInfo(LogType.WARN, text));
    }

    public void warn(CharSequence template, Object... params) {
        String text = StrUtil.format(template, params);
        warn(text);
    }

    public void error(String text) {
        logger.debug("TaskLog文本错误类型：" + text);
        logList.add(new LogInfo(LogType.ERROR, text));
    }

    public void error(CharSequence template, Object... params) {
        String text = StrUtil.format(template, params);
        error(text);
    }

    public void errorConsole(CharSequence template, Object... params) {
        String text = StrUtil.format(template, params);
        logger.error(text);
        error(text);
    }

    public void error(Throwable e) {
        logger.debug("TaskLog堆栈错误：" + e.getMessage(), e);
        String message = ExceptionUtil.getMessage(e);
        error(message);
    }

    public void error(String text, Throwable e) {
        logger.debug(text, e);
        String message = ExceptionUtil.getMessage(e);
        error(text + "：" + message);
    }

    @Override
    public String toString() {
        return (String) taskLogHandlerClassesMap.get("TXT").handle(this);
    }
}
