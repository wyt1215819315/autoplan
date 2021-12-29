package com.miyoushe.sign;

import org.apache.http.Header;

import java.util.List;
import java.util.Map;

/**
 * @Author ponking
 * @Date 2021/7/17 21:24
 */
public interface Sign {

    List<Map<String, Object>> doSign() throws Exception;

    Header[] getHeaders();
}
