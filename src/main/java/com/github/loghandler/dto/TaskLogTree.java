package com.github.loghandler.dto;

import com.github.system.task.dto.TaskLog;
import lombok.Data;

import java.util.List;

@Data
public class TaskLogTree {

    private String style;
    private String data;
    private List<TaskLogTree> children;

}
