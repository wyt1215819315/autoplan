package com.github.task.bili.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.system.base.dto.AjaxResult;
import com.github.system.base.util.HttpUtil;
import com.github.task.bili.constant.BiliUrlConstant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/task/bili")
public class BiliLoginController {

    @GetMapping("/getQrCode")
    public JSONObject getQrCode() {
        return HttpUtil.requestJson(BiliUrlConstant.NEW_BILI_QRCODE_URL, null, HttpUtil.RequestType.GET);
    }

    @GetMapping("/getQrCodeResult/{key}")
    public AjaxResult getQrCodeResult(@PathVariable String key) {
        HttpResponse execute = null;
        try {
            execute = HttpUtil.createGet(BiliUrlConstant.NEW_BILI_QRCODE_STATUS_URL + key).execute();
            if (execute.isOk()) {
                JSONObject body = JSONUtil.parseObj(execute.body());
                if (body.getInt("code") != null && body.getInt("code") == 0) {
                    JSONObject data = body.getJSONObject("data");
                    if (data.getInt("code") == 0) {
                        // 获取cookie
                        String[] cookieKey = {"DedeUserID", "DedeUserID__ckMd5", "SESSDATA", "bili_jct"};
                        for (String ck : cookieKey) {
                            data.set(ck, execute.getCookieValue(ck));
                        }
                    }
                    return AjaxResult.doSuccess(data);
                } else {
                    return AjaxResult.doError(body.getStr("message"));
                }
            }
            return AjaxResult.doError("检查扫码状态错误，服务器返回code:" + execute.getStatus());
        } finally {
            IoUtil.closeIfPosible(execute);
        }
    }


}
