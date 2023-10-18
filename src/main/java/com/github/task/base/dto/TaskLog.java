package com.github.task.base.dto;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TaskLog {
    private final Log logger = LogFactory.getLog(TaskLog.class);

    public record LogInfo(LogType logType, String text) {
    }

    public enum LogType {
        DEBUG, INFO, WARN, ERROR
    }

    private final List<LogInfo> logList = new ArrayList<>();

    public void debug(String text) {
        logger.debug(text);
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
        logList.add(new LogInfo(LogType.ERROR, text));
    }

    public void error(CharSequence template, Object... params) {
        String text = StrUtil.format(template, params);
        error(text);
    }

    public void error(CharSequence template, boolean console, Object... params) {
        String text = StrUtil.format(template, params);
        if (console) {
            logger.error(text);
        }
        error(text);
    }

    public void error(Throwable e) {
        String message = ExceptionUtil.getMessage(e);
        error(message);
    }

    public void error(String text, Throwable e) {
        String message = ExceptionUtil.getMessage(e);
        error(text + "：" + message);
    }

}
