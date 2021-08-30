package com.netmusic.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.oldwu.util.HttpUtils;
import com.oldwu.util.NumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * 网易云音乐解析
 */
public class NeteaseMusicUtil {
    private static final String key = "0CoJUm6Qyw8W8jud";
    private static final String f = "00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7";
    private static final String e = "010001";
    private static final String url = "https://music.163.com/weapi/song/enhance/player/url/v1?csrf_token=";
    private static final String login_url = "https://music.163.com/weapi/w/login/cellphone?csrf_token=";
    private static final String level_url = "https://music.163.com/weapi/user/level";
    private static final String sign_url = "https://music.163.com/weapi/point/dailyTask?";
    private static final String recommend_url = "https://music.163.com/weapi/v1/discovery/recommend/resource";
    private static final String private_url = "https://music.163.com/weapi/user/playlist?csrf_token=";
    private static final String detail_url = "https://music.163.com/weapi/v6/playlist/detail?csrf_token=";
    private static final String feedback_url = "http://music.163.com/weapi/feedback/weblog";
//    private static String cookie;
//    private static String csrf;
//    private static Integer uid;

    public static void main(String[] args) {
        Map<String, String> infos = new HashMap<>();
        infos.put("phone", "");
        infos.put("countrycode", "86");
        infos.put("password", "");
        Map<String, String> login = login(infos);
//        shuaMusicTask();
//        sign(0);
//        Map<String, String> mlist = getUserSubscribePlayLists();
//        getListMusics(JSON.parseArray(mlist.get("playlists")));
    }

    /**
     * 执行任务主方法
     *
     * @param userInfo
     * @return
     */
    public static Map<String, Object> run(Map<String, String> userInfo) {
        int reconn = 5;
        Map<String, Object> map = new HashMap<>();
        String countrycode = userInfo.get("countrycode");
        String password = userInfo.get("password");
        if (countrycode == null || countrycode.equals("") || !StringUtils.isNumeric(countrycode)) {
            userInfo.put("countrycode", "86");
        }
        if (password.length() != 32) {
            userInfo.put("password", md532(password));
        }
        //登录校验
        boolean flag = false;
        StringBuilder msg = new StringBuilder();
        Map<String, String> login = new HashMap<>();
        for (int i = 0; i < reconn; i++) {
            login = login(userInfo);
            flag = Boolean.parseBoolean(login.get("flag"));
            if (flag) {
                msg.append("\n").append(login.get("msg"));
                break;
            }
            msg.append("\n").append(login.get("msg"));
            msg.append("\n[WARNING]登录失败，开始第").append(i + 1).append("次尝试");
        }
        if (!flag) {
            map.put("flag", false);
            map.put("msg", msg.toString());
            return map;
        }
        //获取cookie，uid，csrf
        Integer uid = Integer.valueOf(login.get("uid"));
        String cookie = login.get("cookie");
        String csrf = login.get("csrf");
        //签到
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        for (int i = 0; i < reconn; i++) {
            Map<String, String> sign = sign(0, csrf, cookie);
            flag1 = Boolean.parseBoolean(sign.get("flag"));
            Map<String, String> sign1 = sign(1, csrf, cookie);
            flag2 = Boolean.parseBoolean(sign1.get("flag"));
            if (flag1 && flag2) {
                msg.append("\n").append(sign.get("msg")).append("\n").append(sign1.get("msg"));
                break;
            }
            msg.append("\n").append(sign.get("msg")).append(sign1.get("msg"));
            msg.append("\n[WARNING]签到出现异常，开始第").append(i + 1).append("次尝试");
        }
        //刷歌
        for (int i = 0; i < reconn; i++) {
            Map<String, String> shuaMap = shuaMusicTask(csrf, cookie);
            flag3 = Boolean.parseBoolean(shuaMap.get("flag"));
            if (flag3) {
                msg.append("\n").append(shuaMap.get("msg"));
                break;
            }
            msg.append("\n").append(shuaMap.get("msg"));
            msg.append("\n[WARNING]刷歌出现异常，开始第").append(i + 1).append("次尝试");
        }
        if (flag1 && flag2 && flag3) {
            map.put("complete", true);
        }
        map.put("flag", true);
        map.put("msg", login.get("msg") + "\n" + msg);
        return map;
    }

    public static Map<String, String> shuaMusicTask(String csrf, String cookie) {
        int reconn = 3;
        Map<String, String> result = new HashMap<>();
        Map<String, String> taskMusicsMap = new HashMap<>();
        for (int i = 0; i < reconn; i++) {
            taskMusicsMap = getTaskMusics(csrf, cookie);
            if (taskMusicsMap.get("flag").equals("true")) {
                break;
            }
        }
        if (taskMusicsMap.get("flag").equals("false")) {
            result.put("flag", "false");
            result.put("msg", "获取任务歌单" + reconn + "次均失败！" + taskMusicsMap.get("msg"));
            return result;
        }
        result.put("msg", taskMusicsMap.get("msg") + "\n");
        JSONArray taskMusics = JSON.parseArray(taskMusicsMap.get("taskMusics"));
        JSONArray musicsTask = new JSONArray();
        JSONObject postData = new JSONObject();
        for (int i = 0; i < taskMusics.size(); i++) {
            JSONObject music = new JSONObject();
            JSONObject json = new JSONObject();
            json.put("download", 0);
            json.put("end", "playend");
            json.put("id", taskMusics.getLong(i));
            json.put("sourceId", "");
            json.put("time", 300);
            json.put("type", "song");
            json.put("wifi", 0);
            music.put("action", "play");
            music.put("json", json);
            musicsTask.add(music);
        }
        postData.put("logs", musicsTask);
        try {
            HttpResponse httpResponse = HttpUtils.doPost(feedback_url, null, HttpUtils.getHeaders(), null, getRequestParam(postData.toJSONString()));
            JSONObject json = HttpUtils.getJson(httpResponse);
            if (json.getInteger("code") == 200) {
                result.put("flag", "true");
                result.put("msg", result.get("msg") + "刷听歌量成功，共" + musicsTask.size() + "首");
            } else {
                result.put("flag", "false");
                result.put("msg", result.get("msg") + "刷听歌量失败 " + json.getInteger("code") + "：" + json.getString("message"));
            }
            return result;
        } catch (Exception exception) {
            result.put("flag", "false");
            result.put("msg", result.get("msg") + "刷听歌量失败 " + exception.getMessage());
            exception.printStackTrace();
            return result;
        }
    }

    /**
     * 获取任务歌单池内的所有音乐ID
     *
     * @return
     */
    public static Map<String, String> getTaskMusics(String csrf, String cookie) {
        int num = 320;
        Map<String, String> map = new HashMap<>();
        JSONArray musics = new JSONArray();
        Map<String, String> userRecommendPlayLists = getUserRecommendPlayLists(csrf, cookie);
        if (userRecommendPlayLists.get("flag").equals("false")) {
            map.put("flag", "false");
            map.put("msg", userRecommendPlayLists.get("msg"));
            return map;
        }
        JSONArray array = JSON.parseArray(userRecommendPlayLists.get("playlists"));
        Map<String, String> playlists = getListMusics(array, csrf, cookie);
        if (playlists.get("flag").equals("false")) {
            map.put("flag", "false");
            map.put("msg", playlists.get("msg"));
            return map;
        }
        String msg = userRecommendPlayLists.get("msg") + "\n" + playlists.get("msg");
        JSONArray recommend_musics = JSON.parseArray(playlists.get("musiclists"));
        if (recommend_musics.size() > num) {
            int[] ints = NumberUtil.randomCommon(1, recommend_musics.size(), num);
            for (int anInt : ints) {
                musics.add(recommend_musics.getLong(anInt));
            }
        } else {
            musics.addAll(recommend_musics);
        }
        //System.out.println(musics);
        //JSONArray subscribe_musics = JSON.parseArray(getListMusics(JSON.parseArray(getUserSubscribePlayLists().get("playlists"))).get("musiclists"));
//        if (recommend_musics.size() > num) {
//            for (int i = 0; i < num; i++) {
//                musics.add(recommend_musics.getLong(i));
//            }
//        } else {
//            musics.addAll(recommend_musics);
//        }
        msg = msg + "\n已添加" + musics.size() + "首歌曲到播放列表";
        map.put("flag", "true");
        map.put("msg", msg);
        map.put("taskMusics", musics.toJSONString());
        return map;
    }

    /**
     * 获取歌单内所有music的id
     *
     * @param mList
     * @return
     */
    public static Map<String, String> getListMusics(JSONArray mList, String csrf, String cookie) {
        Map<String, String> result = new HashMap<>();
        JSONArray allMusics = new JSONArray();
        String msg = "";
        for (int i = 0; i < mList.size(); i++) {
            Long listId = Long.valueOf(mList.getString(i));
            JSONObject up = new JSONObject();
            up.put("id", listId);
            up.put("n", 1000);
            up.put("csrf_token", csrf);
            Map<String, String> headers = getHeaders();
            headers.put("cookie", cookie);
            try {
                HttpResponse httpResponse = HttpUtils.doPost(detail_url + csrf, null, headers, null, getRequestParam(up.toJSONString()));
                String s = EntityUtils.toString(httpResponse.getEntity());
                //System.out.println(s);
                JSONObject json = JSON.parseObject(s);
                if (json == null) {
                    continue;
                }
                if (json.getInteger("code") == 200) {
                    JSONArray trackIds = json.getJSONObject("playlist").getJSONArray("trackIds");
                    for (int i1 = 0; i1 < trackIds.size(); i1++) {
                        JSONObject track = trackIds.getJSONObject(i1);
                        Long id = track.getLong("id");
                        allMusics.add(id);
                    }
                }
                msg = msg + "\n获取歌单" + listId + "成功！";
            } catch (Exception exception) {
                exception.printStackTrace();
                result.put("flag", "false");
                msg = msg + "\n获取歌单" + listId + "失败！";
                msg = msg + "\n获取歌单" + listId + "失败！";
                result.put("msg", msg);
                return result;
            }
        }
        result.put("flag", "true");
        result.put("msg", "获取歌单音乐完毕，日志：" + msg);
        result.put("musiclists", allMusics.toJSONString());
        return result;
    }

    /**
     * 获取用户收藏歌单
     *
     * @return
     */
    public static Map<String, String> getUserSubscribePlayLists(Integer uid, String csrf, String cookie) {
        Map<String, String> result = new HashMap<>();
        JSONObject up = new JSONObject();
        up.put("uid", uid);
        up.put("limit", 1001);
        up.put("offset", 0);
        up.put("csrf_token", csrf);
        Map<String, String> headers = getHeaders();
        headers.put("cookie", cookie);
        try {
            HttpResponse httpResponse = HttpUtils.doPost(private_url + csrf, null, headers, null, getRequestParam(up.toJSONString()));
            JSONObject json = HttpUtils.getJson(httpResponse);
            result.put("data", json.toJSONString());
            if (json.getInteger("code") == 200) {
                JSONArray playlist = json.getJSONArray("playlist");
                JSONArray playLists = new JSONArray();
                for (int i = 0; i < playlist.size(); i++) {
                    JSONObject play = playlist.getJSONObject(i);
                    if (play.getBoolean("subscribed")) {
                        String id = play.getString("id");
                        playLists.add(id);
                    }
                }
                result.put("flag", "true");
                result.put("msg", "获取个人订阅歌单成功");
                result.put("playlists", playLists.toJSONString());
                return result;
            } else {
                result.put("flag", "false");
                result.put("msg", "获取个人订阅歌单失败，错误信息：" + json.toJSONString());
                return result;
            }
        } catch (Exception exception) {
            result.put("flag", "false");
            result.put("msg", "获取个人订阅歌单失败，错误信息：" + exception.getMessage());
            exception.printStackTrace();
            return result;
        }
    }

    /**
     * 获取用户推荐歌单
     *
     * @return
     */
    public static Map<String, String> getUserRecommendPlayLists(String csrf, String cookie) {
        Map<String, String> result = new HashMap<>();
        String str = "{\"csrf_token\":\"" + csrf + "\"}";
        Map<String, String> headers = getHeaders();
        headers.put("cookie", cookie);
        try {
            HttpResponse httpResponse = HttpUtils.doPost(recommend_url, null, headers, null, getRequestParam(str));
            JSONObject json = HttpUtils.getJson(httpResponse);
            result.put("data", json.toJSONString());
            if (json.getInteger("code") == 200) {
                JSONArray recommend = json.getJSONArray("recommend");
                JSONArray playLists = new JSONArray();
                for (int i = 0; i < recommend.size(); i++) {
                    JSONObject re = recommend.getJSONObject(i);
                    String id = re.getString("id");
                    playLists.add(id);
                }
                result.put("flag", "true");
                result.put("msg", "获取推荐歌单成功");
                result.put("playlists", playLists.toJSONString());
                return result;
            } else {
                result.put("flag", "false");
                result.put("msg", "获取推荐歌单失败，错误信息：" + json.toJSONString());
                return result;
            }
        } catch (Exception exception) {
            result.put("flag", "false");
            result.put("msg", "获取推荐歌单失败，错误信息：" + exception.getMessage());
            exception.printStackTrace();
            return result;
        }
    }

    /**
     * @param flag 签到的客户端0=PC/web；1=android/ios
     * @return
     */
    public static Map<String, String> sign(int flag, String csrf, String cookie) {
        if (flag < 0 || flag > 1) {
            flag = 0;
        }
        Map<String, String> map = new HashMap<>();
        Map<String, String> headers = getHeaders();
        headers.put("cookie", cookie);
        String str = "{\"type\":" + flag + "}";
        try {
            HttpResponse httpResponse = HttpUtils.doPost(sign_url + csrf, null, headers, null, getRequestParam(str));
            JSONObject json = HttpUtils.getJson(httpResponse);
            map.put("data", json.toJSONString());
            String text;
            Integer code = json.getInteger("code");
            map.put("code", code.toString());
            if (code == 200) {
                text = (flag == 0 ? "PC/WEB" : "移动端") + "签到成功，经验+" + json.getString("point");
                map.put("flag", "true");
            } else if (code == -2) {
                text = (flag == 0 ? "PC/WEB" : "移动端") + "今天已经签到过了";
                map.put("flag", "true");
            } else {
                text = (flag == 0 ? "PC/WEB" : "移动端") + "签到失败，错误代码" + code + "，信息：" + json.getString("message");
                map.put("flag", "false");
            }
            map.put("msg", text);
            return map;
        } catch (Exception exception) {
            map.put("flag", "false");
            map.put("msg", "签到发生异常！" + exception.getMessage());
            exception.printStackTrace();
            return map;
        }
    }

    public static Map<String, String> getRequestParam(String text) {
        Map<String, String> encrypt = encrypt(text);
        Map<String, String> data = new HashMap<>();
        String encText = encrypt.get("encText");
        String encSecKey = encrypt.get("encSecKey");
        data.put("params", encText);
        data.put("encSecKey", encSecKey);
        return data;
    }


    /**
     * 获取登录加密数据
     *
     * @param phone
     * @param countrycode
     * @param password
     * @return
     */
    public static Map<String, String> getLoginData(String phone, String countrycode, String password) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("phone", phone);
        jsonObject.put("countrycode", countrycode);
        jsonObject.put("password", password);
        jsonObject.put("rememberLogin", "true");
        Map<String, String> encrypt = encrypt(jsonObject.toJSONString());
        String encText = encrypt.get("encText");
        String encSecKey = encrypt.get("encSecKey");
        Map<String, String> login_data = new HashMap<>();
        login_data.put("params", encText);
        login_data.put("encSecKey", encSecKey);
        return login_data;
    }

    /**
     * 登录流程
     *
     * @param info
     * @return
     */
    public static Map<String, String> login(Map<String, String> info) {
        Map<String, String> result = new HashMap<>();
        Map<String, String> headers = getNetMusicLoginHeaders();
        if (StringUtils.isBlank(info.get("phone")) || StringUtils.isBlank("password")) {
            result.put("msg", "用户名或密码不能为空！");
            result.put("flag", "false");
        }
        try {
            Map<String, String> loginData = getLoginData(info.get("phone"), info.get("countrycode"), info.get("password"));
            HttpResponse httpResponse = HttpUtils.doPost(login_url, null, headers, null, loginData);
            JSONObject json = HttpUtils.getJson(httpResponse);
            if (json.getInteger("code") == 200) {
                Map<String, String> cookies = HttpUtils.getCookies(httpResponse);
                String cookieString = HttpUtils.getCookieString(httpResponse);
                result.put("cookie", cookieString);
                String csrf = cookies.get("__csrf");
                result.put("csrf", csrf);
                result.put("data", json.toJSONString());
                String nickname = json.getJSONObject("profile").getString("nickname");
                result.put("nickname", nickname);
                String uid = json.getJSONObject("account").getString("id");
                result.put("uid", uid);
                Map<String, String> level = getLevel(loginData, cookieString);
                String level1 = level.get("level");
                int count = Integer.parseInt(level.get("nextPlayCount")) - Integer.parseInt(level.get("nowPlayCount"));
                int days = Integer.parseInt(level.get("nextLoginCount")) - Integer.parseInt(level.get("nowLoginCount"));
                String msg = nickname + " 登录成功，当前等级：" + level1 + "\n\n距离升级还需听" + count + "首歌\n\n距离升级还需登录" + days + "天";
                result.put("level", level1);
                result.put("days", String.valueOf(days));
                result.put("count", String.valueOf(count));
                result.put("msg", msg);
                result.put("flag", "true");
                return result;
            } else {
                result.put("flag", "false");
                result.put("msg", "登录失败 : " + json.getString("msg"));
                result.put("code", json.getString("code"));
                return result;
            }
        } catch (Exception exception) {
            result.put("flag", "false");
            result.put("msg", "登录失败 : " + exception.getMessage());
            exception.printStackTrace();
            return result;
        }
    }

    /**
     * 获取等级等信息
     *
     * @param login_data
     * @return
     */
    public static Map<String, String> getLevel(Map<String, String> login_data, String cookie) {
        Map<String, String> result = new HashMap<>();
        Map<String, String> headers = getHeaders();
        headers.put("cookie", cookie);
        try {
            HttpResponse httpResponse = HttpUtils.doPost(level_url, null, headers, null, login_data);
            JSONObject json = HttpUtils.getJson(httpResponse);
            result.put("code", json.getString("code"));
            if (json.getInteger("code") != 200) {
                result.put("flag", "false");
                result.put("msg", json.getString("msg"));
                return result;
            }
            result.put("data", json.toJSONString());
            JSONObject data = json.getJSONObject("data");
            result.put("userId", data.getString("userId"));
            result.put("info", data.getString("info"));
            result.put("progress", data.getString("progress"));
            result.put("nextPlayCount", data.getString("nextPlayCount"));
            result.put("nextLoginCount", data.getString("nextLoginCount"));
            result.put("nowPlayCount", data.getString("nowPlayCount"));
            result.put("nowLoginCount", data.getString("nowLoginCount"));
            result.put("level", data.getString("level"));
            result.put("flag", "true");
            return result;
        } catch (Exception exception) {
            result.put("flag", "false");
            result.put("msg", exception.getMessage());
            exception.printStackTrace();
            return result;
        }
    }

    public static String md532(String plainText) {
        byte[] mdBytes = null;
        try {
            mdBytes = MessageDigest.getInstance("MD5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不存在！");
        }
        String mdCode = new BigInteger(1, mdBytes).toString(16);

        if (mdCode.length() < 32) {
            int a = 32 - mdCode.length();
            for (int i = 0; i < a; i++) {
                mdCode = "0" + mdCode;
            }
        }
//        return mdCode.toUpperCase(); //返回32位大写
        return mdCode;            // 默认返回32位小写
    }

    public static String aESencrypt(String msg, String key) {
        int padding = 16 - msg.length() % 16;
        char ch = (char) (padding);
        for (int i = 0; i < padding; i++) {
            msg = msg + ch;
        }
        String ivParameter = "0102030405060708";
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            byte[] raw = key.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(msg.getBytes(StandardCharsets.UTF_8));
            String base64 = new BASE64Encoder().encode(encrypted);//此处使用BASE64做转码。
            base64 = base64.replaceAll("\r", "");
            base64 = base64.replaceAll("\n", "");
            return base64;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String rSAencrypt(String randomStrs, String key, String f) {
        StringBuilder stringBuilder = new StringBuilder(randomStrs);
        randomStrs = stringBuilder.reverse().toString();
        byte[] text = randomStrs.getBytes(StandardCharsets.UTF_8);
        BigInteger i1 = new BigInteger(byteToHex(text), 16);
        int i2 = Integer.parseInt(key, 16);
        BigInteger i3 = new BigInteger(f, 16);
        BigInteger j = i1.pow(i2);
        BigInteger seckey = j.mod(i3);
        String s = seckey.toString(16);
        return String.format(s, 256);
    }

    public static Map<String, String> encrypt(String text) {
        String enctext = aESencrypt(text, key);
        String i = NumberUtil.generateRandomStrs(16);
        String encText = aESencrypt(enctext, i);
        String encSecKey = rSAencrypt(i, e, f);
        Map<String, String> map = new HashMap<>();
        map.put("encText", encText);
        map.put("encSecKey", encSecKey);
        return map;
    }

    public static Map<String, String> getMusicParamByMusicId(String ids) {
        String msg = "{ids: \"[" + ids + "]\", level: \"standard\", encodeType: \"mp3\", csrf_token: \"\"}";
        return encrypt(msg);
    }

    public static Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        headers.put("Referer", "https://music.163.com/");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        return headers;
    }

    public static Map<String, String> getNetMusicLoginHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        headers.put("Origin", "https://music.163.com");
        headers.put("Referer", "https://music.163.com/");
        headers.put("Cookie", "os=pc; osver=Microsoft-Windows-10-Professional-build-10586-64bit; appver=2.0.3.131777; channel=netease; __remember_me=true;");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        return headers;
    }

    public static Map<String, String> getNetMusicSearchHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Host", "music.163.com");
        headers.put("Connection", "keep-alive");
        headers.put("Pragma", "no-cache");
        headers.put("Cache-Control", "no-cache");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Accept", "*/*");
        headers.put("Origin", "https://music.163.com");
        headers.put("Sec-Fetch-Site", "same-origin");
        headers.put("Sec-Fetch-Mode", "cors");
        headers.put("Sec-Fetch-Dest", "empty");
        headers.put("Referer", "https://music.163.com/user/fans?id=1411492497");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9");
        return headers;
    }

    public static JSONObject getMusicJsonByMusicId(String musicId, String cookie) {
        Map<String, String> param1 = getMusicParamByMusicId(musicId);
        Map<String, String> headers = getNetMusicSearchHeaders();
        Map<String, String> map = new HashMap<>();
        headers.put("cookie", cookie);
        map.put("params", param1.get("encText"));
        map.put("encSecKey", param1.get("encSecKey"));
        try {
            HttpResponse httpResponse = HttpUtils.doPost(url, null, headers, null, map);
            return HttpUtils.getJson(httpResponse);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }


    /**
     * byte数组转hex
     *
     * @param bytes
     * @return
     */
    public static String byteToHex(byte[] bytes) {
        String strHex = "";
        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < bytes.length; n++) {
            strHex = Integer.toHexString(bytes[n] & 0xFF);
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex); // 每个字节由两个字符表示，位数不够，高位补0
        }
        return sb.toString().trim();
    }


}
