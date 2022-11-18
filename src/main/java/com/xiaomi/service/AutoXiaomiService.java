package com.xiaomi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oldwu.entity.AjaxResult;
import com.oldwu.vo.PageDataVO;
import com.xiaomi.model.entity.AutoXiaomiEntity;

import java.util.Map;

public interface AutoXiaomiService extends IService<AutoXiaomiEntity> {

    AjaxResult view(Integer id);

    PageDataVO<AutoXiaomiEntity> queryPageList(Integer page, Integer limit);

    AjaxResult listMine(Integer id);

    Map<String, Object> editAutoXiaomi(AutoXiaomiEntity autoXiaomiEntity);

    AjaxResult delete(Integer id) throws Exception;

    Map<String, String> addXiaoMi(AutoXiaomiEntity autoXiaomiEntity);

    Map<String, Object> doDailyTaskPersonal(Integer autoId, Integer userId);
}
