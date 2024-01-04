package com.github.task.alipan.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.system.base.dto.AjaxResult;
import com.github.system.base.util.HttpUtil;
import com.github.task.alipan.constant.AliPanConstant;
import com.github.task.alipan.vo.AliPanQrCodeVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/task/alipan")
public class AliPanLoginController {

    @GetMapping("/getQrCode")
    public AjaxResult getQrCode() {
        Map<String, Object> params = new HashMap<>();
        params.put("appName", "aliyun_drive");
        params.put("fromSite", "52");
        params.put("appEntrance", "web_default");
        params.put("isMobile", "false");
        params.put("lang", "zh_CN");
        params.put("returnUrl", "");
        params.put("bizParams", "");
        JSONObject jsonObject = HttpUtil.requestJson(AliPanConstant.GenerateQrCodeUrl, params, HttpUtil.RequestType.GET);
        if (!jsonObject.getBool("hasError") && jsonObject.getJSONObject("content").getBool("success")) {
            return AjaxResult.doSuccess(jsonObject.getJSONObject("content").getJSONObject("data"));
        }
        return AjaxResult.doError("获取二维码失败，返回信息：" + jsonObject);
    }

    @PostMapping("/getQrCodeResult")
    public AjaxResult getQrCodeResult(@RequestBody @Validated AliPanQrCodeVo aliPanQrCodeVo) {
        Map<String, Object> params = new HashMap<>();
        params.put("t", aliPanQrCodeVo.getT());
        params.put("ck", aliPanQrCodeVo.getCk());
        params.put("appName", "aliyun_drive");
        params.put("appEntrance", "web_default");
        params.put("isMobile", "false");
        params.put("lang", "zh_CN");
        params.put("returnUrl", "");
        params.put("fromSite", "52");
        params.put("bizParams", "");
        params.put("navlanguage", "zh-CN");
        params.put("navPlatform", "Win32");
        JSONObject jsonObject = HttpUtil.requestJson(AliPanConstant.QueryQrCodeUrl, params, HttpUtil.RequestType.X_WWW_FORM);
        if (!jsonObject.getBool("hasError") && jsonObject.getJSONObject("content").getBool("success")) {
            JSONObject data = jsonObject.getJSONObject("content").getJSONObject("data");
            String bizExtBase64 = data.getStr("bizExt");
            String bizExtDecode = Base64.decodeStr(bizExtBase64);
            data.set("bizExt", JSONUtil.parseObj(bizExtDecode));
            return AjaxResult.doSuccess(data);
        }
        return AjaxResult.doError("查询二维码扫描信息失败，返回信息：" + jsonObject);
    }


}
