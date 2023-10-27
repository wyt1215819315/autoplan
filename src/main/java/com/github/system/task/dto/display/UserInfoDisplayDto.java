package com.github.system.task.dto.display;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoDisplayDto {

    private String field;
    private String fieldType;
    private String name;
    private List<UserInfoDisplayOptions> options;


}
