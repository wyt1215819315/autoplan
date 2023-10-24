package com.github.system.base.util;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public class EntityUtil {

    public static <T> Page<T> copyPageBean(Page page, Class<T> clazz) {
        List<T> list = BeanUtil.copyToList(page.getRecords(), clazz);
        page.setRecords(list);
        return (Page<T>) page;
    }

}
