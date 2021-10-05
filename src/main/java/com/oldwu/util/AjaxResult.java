package com.oldwu.util;

import java.util.HashMap;
import java.util.Map;

public class AjaxResult {

    public static Map<String, Object> success() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        return map;
    }

    public static Map<String, Object> success(String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("msg", msg);
        return map;
    }

    public static Map<String, Object> error(String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", -1);
        map.put("msg", msg);
        return map;
    }

}
