package com.github.task.bili.util;

import cn.hutool.json.JSONObject;
import com.github.task.bili.constant.BiliUrlConstant;
import com.github.task.bili.model.task.BiliData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * av bv utils.
 *
 * @author Junzhou Liu
 * @since 2020/10/11 20:49
 */
public class BiliHelpUtil {
    private static final Log logger = LogFactory.getLog(BiliHelpUtil.class);
    private static final String TABLE = "fZodR9XQDSUm21yCkr6zBqiveYah8bt4xsWpHnJE7jL5VG3guMTKNPAwcF";
    private static final HashMap<String, Integer> MP = new HashMap<>();
    private static final HashMap<Integer, String> MP2 = new HashMap<>();
    private static final int[] ss = {11, 10, 3, 8, 4, 6, 2, 9, 5, 7};
    private static final long xor = 177451812;
    private static final long add = 8728348608L;

    public static long power(int a, int b) {
        long power = 1;
        for (int c = 0; c < b; c++) {
            power *= a;
        }

        return power;
    }

    public static String bv2av(String s) {
        long r = 0;
        for (int i = 0; i < 58; i++) {
            String s1 = TABLE.substring(i, i + 1);
            MP.put(s1, i);
        }
        for (int i = 0; i < 6; i++) {
            r = r + MP.get(s.substring(ss[i], ss[i] + 1)) * power(58, i);
        }
        return ((r - add) ^ xor) + "";
    }

    public static String av2bv(String st) {
        long s = Long.parseLong(st.split("av")[1]);
        StringBuilder sb = new StringBuilder("BV1  4 1 7  ");
        s = (s ^ xor) + add;
        for (int i = 0; i < 58; i++) {
            String s1 = TABLE.substring(i, i + 1);
            MP2.put(i, s1);
        }
        for (int i = 0; i < 6; i++) {
            String r = MP2.get((int) (s / power(58, i) % 58));
            sb.replace(ss[i], ss[i] + 1, r);
        }
        return sb.toString();
    }

    /**
     * 返回主站查询到的硬币余额，查询失败返回0.0.
     *
     * @return data=数据，msg=需要记录的日志
     */
    public static Map<String, Object> getCoinBalance(BiliWebUtil biliWebUtil) throws Exception {
        Map<String, Object> map = new HashMap<>();
        JSONObject responseJson = biliWebUtil.doGet(BiliUrlConstant.BILI_GET_COIN_BALANCE);
        int responseCode = responseJson.getInt("code");
        JSONObject dataObject = responseJson.getJSONObject("data");
        if (responseCode == 0) {
            if (dataObject.get("money") == null) {
                map.put("data", 0.0);
                map.put("msg", "json is null");
                return map;
            } else {
                map.put("data", dataObject.getDouble("money"));
                return map;
            }
        } else {
            logger.warn("b站任务请求硬币余额接口错误。错误请求信息：" + responseJson);
            map.put("msg", "b站任务请求硬币余额接口错误。错误请求信息：" + responseJson);
            map.put("data", 0.0);
            return map;
        }
    }

    /**
     * 投币操作工具类.
     *
     * @param bvid       av号
     * @param multiply   投币数量
     * @param selectLike 是否同时点赞 1是
     * @return 是否投币成功
     */
    public static Map<String, Object> coinAdd(String bvid, int multiply, Boolean selectLike, BiliWebUtil biliWebUtil, String biliJct) throws Exception {
        Map<String, Object> map = new HashMap<>();
        String videoTitle = getVideoTitle(bvid, biliWebUtil);
        String msg = "";
        //判断曾经是否对此av投币过
        Map<String, Object> coinAddedMap = isCoinAdded(bvid, biliWebUtil);
        if (coinAddedMap.containsKey("msg")) {
            msg += coinAddedMap.get("msg") + "\n";
        }
        if (!(boolean) coinAddedMap.get("data")) {
            Map<String, String> headers = new HashMap<>();
            Map<String, Object> params = new HashMap<>();
            headers.put("Referer", "https://www.bilibili.com/video/" + bvid);
            headers.put("Origin", "https://www.bilibili.com");
            //请求参数
            params.put("bvid", bvid);
            params.put("multiply", String.valueOf(multiply));
            params.put("select_like", selectLike ? "1" : "0");
            params.put("cross_domain", "true");
            params.put("csrf", biliJct);
            JSONObject jsonObject = biliWebUtil.doPost(BiliUrlConstant.BILI_COIN_ADD, params, headers);
            if (jsonObject.getInt("code") == 0) {
                msg += String.format("为 %s投币成功", videoTitle);
                map.put("data", true);
            } else {
                msg += "投币失败:" + jsonObject.getStr("message");
                map.put("data", false);
            }
        } else {
            msg += String.format("已经为%s投过币了", videoTitle);
            map.put("data", false);
        }
        map.put("msg", msg);
        return map;
    }

    /**
     * 检查是否投币.
     *
     * @param bvid av号
     * @return 返回是否投过硬币了.
     */
    public static Map<String, Object> isCoinAdded(String bvid, BiliWebUtil biliWebUtil) throws Exception {
        Map<String, Object> map = new HashMap<>();
        String urlParam = "?bvid=" + bvid;
        JSONObject result = biliWebUtil.doGet(BiliUrlConstant.BILI_IS_COIN + urlParam);

        int multiply = result.getJSONObject("data").getInt("multiply");
        if (multiply > 0) {
            map.put("msg", "之前已经为av" + bvid + "投过" + multiply + "枚硬币啦");
            map.put("data", true);
            return map;
        } else {
            map.put("data", false);
            return map;
        }
    }

    /**
     * type 1大会员B币券  2 大会员福利.
     *
     * @return 执行结果字符串
     */
    public static String getVipPrivilege(int type, String biliJct, BiliWebUtil biliWebUtil) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("type", String.valueOf(type));
        params.put("csrf", biliJct);
        JSONObject jsonObject = biliWebUtil.doPost(BiliUrlConstant.BILI_VIP_PRIVILEGE_RECEIVE, params);
        int responseCode = jsonObject.getInt("code");
        if (responseCode == 0) {
            if (type == 1) {
                return "领取年度大会员每月赠送的B币券成功";
            } else if (type == 2) {
                return "领取大会员福利/权益成功";
            }
        } else {
            throw new Exception("领取年度大会员每月赠送的B币券/大会员福利失败，原因: " + jsonObject.getStr("message"));
        }
        return "";
    }

    /**
     * 请求视频title，未获取到时返回bvid.
     *
     * @return title
     */
    public static String getVideoTitle(String bvid, BiliWebUtil biliWebUtil) throws Exception {
        String title;
        String urlParameter = "?bvid=" + bvid;
        JSONObject jsonObject = biliWebUtil.doGet(BiliUrlConstant.BILI_VIDEO_VIEW + urlParameter);

        if (jsonObject.getInt("code") == 0) {
            title = jsonObject.getJSONObject("data").getJSONObject("owner").getStr("name") + ": ";
            title += jsonObject.getJSONObject("data").getStr("title");
        } else {
            title = "未能获取标题";
        }

        return title.replace("&", "-");
    }

    /**
     * query username.
     *
     * @param uid 用户uid
     * @return userName 查询到的用户名，为1则未查询到用户
     */
    public static Map<String, String> queryUserNameByUid(String uid,BiliWebUtil biliWebUtil) throws Exception {
        Map<String, String> map = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        params.put("mid", uid);
        params.put("jsonp", "jsonp");
        String userName = "1";
        JSONObject jsonObject = biliWebUtil.doGet(BiliUrlConstant.BILI_QUERY_USER_NAME, params);
        if (jsonObject.getInt("code") == 0) {
            userName = jsonObject.getJSONObject("data").getStr("name");
        } else {
            map.put("msg", "查询充电对象的用户名失败，原因：" + jsonObject);
        }
        map.put("data", userName);
        return map;
    }

    /**
     * 充电留言
     *
     */
    public static String chargeComments(String token, BiliWebUtil biliWebUtil, String biliJct) throws Exception {
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("order_id", token);
        postMap.put("message", "期待up主的新作品！");
        postMap.put("csrf", biliJct);
        JSONObject jsonObject = biliWebUtil.doPost(BiliUrlConstant.BILI_CHARGE_COMMENT, postMap);

        if (jsonObject.getInt("code") == 0) {
            return "充电留言成功";
        } else {
            return jsonObject.getStr("message");
        }
    }

    /**
     * 此功能依赖BiliData
     *
     * @return 返回会员类型.
     * 0:无会员（会员过期，当前不是会员）. 1:月会员. 2:年会员.
     */
    public static Map<String, Object> queryVipStatusType(BiliData biliData) {
        Map<String, Object> map = new HashMap<>();
        if (biliData == null) {
            map.put("msg", "暂时无法查询会员状态，默认非大会员");
        }
        if (biliData != null && biliData.getVipStatus() == 1) {
            //只有VipStatus为1的时候获取到VipType才是有效的。
            map.put("data", biliData.getVipType());
            return map;
        } else {
            map.put("data", 0);
            return map;
        }
    }

    /**
     * 获取当前投币获得的经验值.
     *
     * @return 本日已经投了几个币
     */
    public static int expConfirm(BiliWebUtil biliWebUtil) throws Exception {
        JSONObject resultJson = biliWebUtil.doGet(BiliUrlConstant.BILI_NEED_COIN_NEW);
        int getCoinExp = resultJson.getInt("data");
        return getCoinExp / 10;
    }

    /**
     * @return jsonObject 返回status对象，包含{"login":true,"watch":true,"coins":50,
     * "share":true,"email":true,"tel":true,"safe_question":true,"identify_card":false}
     * @author @srcrs
     */
    public static JSONObject getDailyTaskStatus(BiliWebUtil biliWebUtil) throws Exception {
        JSONObject jsonObject = biliWebUtil.doGet(BiliUrlConstant.BILI_REWARD);
        int responseCode = jsonObject.getInt("code");
        if (responseCode == 0) {
            return jsonObject.getJSONObject("data");
        } else {
            logger.error(jsonObject.getStr("message"));
            return biliWebUtil.doGet(BiliUrlConstant.BILI_REWARD);
        }
    }


}
