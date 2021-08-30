package com.miyoushe.sign;

import org.apache.http.Header;

import java.util.Map;

/**
 * @Author ponking
 * @Date 2021/7/17 21:24
 */
public interface Sign {

    Map<String, Object> doSign() throws Exception;

    Header[] getHeaders();
}
