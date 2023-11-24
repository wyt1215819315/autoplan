package com.github.task.mihoyousign.support;

import com.github.system.task.dto.TaskLog;
import com.github.system.task.dto.TaskResult;

import java.util.Map;

public interface Sign {

    TaskResult doSign(TaskLog log) throws Exception;

    Map<String,String> getHeaders(String dsType);
}
