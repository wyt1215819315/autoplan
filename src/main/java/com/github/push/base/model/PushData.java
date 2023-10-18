package com.github.push.base.model;

import com.github.task.base.dto.TaskLog;
import com.github.task.base.service.TaskLogDisplayHandler;
import lombok.Data;

import static com.github.task.base.init.TaskInit.taskLogHandlerClassesMap;

@Data
public class PushData<T extends PushBaseConfig> {

    private Integer userId;
    private Long logId;
    private TaskLog taskLog;
    private String title;
    private T config;

    public Object getContent(String handlerName) {
        TaskLogDisplayHandler taskLogDisplayHandler = taskLogHandlerClassesMap.get(handlerName);
        if (taskLogDisplayHandler == null) {
            taskLogDisplayHandler = taskLogHandlerClassesMap.get("TXT");
        }
        return taskLogDisplayHandler.handle(this.taskLog);
    }

    public String getContent() {
        return (String) getContent("TXT");
    }
}
