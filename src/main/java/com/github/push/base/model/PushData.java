package com.github.push.base.model;

import com.github.system.task.dto.TaskLog;
import com.github.system.task.service.TaskLogDisplayHandler;
import lombok.Data;

import static com.github.system.task.init.TaskInit.taskLogHandlerClassesMap;

@Data
public class PushData<T extends PushBaseConfig> {

    private Long webhookId;
    private Long userId;
    private Long logId;
    private TaskLog taskLog;
    private String title;
    private T config;

    public Object getContent(String handlerName) {
        TaskLogDisplayHandler taskLogDisplayHandler = taskLogHandlerClassesMap.get(handlerName);
        if (taskLogDisplayHandler == null) {
            taskLogDisplayHandler = taskLogHandlerClassesMap.get("TXT");
        }
        return taskLogDisplayHandler.handle(this.taskLog, this);
    }

    public String getContent() {
        return (String) getContent("TXT");
    }
}
