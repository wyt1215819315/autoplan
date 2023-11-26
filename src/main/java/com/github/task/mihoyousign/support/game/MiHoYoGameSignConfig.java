package com.github.task.mihoyousign.support.game;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MiHoYoGameSignConfig {

    private String gameName;
    private String roleUrl;
    private String signActId;
    private String signUrl;
    private String awardUrl;
    private String hubInfoUrl;

}
