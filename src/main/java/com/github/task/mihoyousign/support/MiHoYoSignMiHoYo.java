package com.github.task.mihoyousign.support;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.system.base.util.HttpUtil;
import com.github.system.task.dto.TaskLog;
import com.github.system.task.dto.TaskResult;
import com.github.task.mihoyousign.constant.MihoyouSignConstant;
import com.github.task.mihoyousign.support.pojo.PostResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.*;

public class MiHoYoSignMiHoYo extends MiHoYoAbstractSign {

    /**
     * 浏览帖子数
     */
    private final static int VIEW_NUM = 5;
    /**
     * 点赞帖子数
     */
    private final static int UP_VOTE_NUM = 5;
    /**
     * 分享帖子数
     */
    private final static int SHARE_NUM = 3;

    private static final Logger log = LogManager.getLogger(MiHoYoSignMiHoYo.class.getName());
    private final MihoyouSignConstant.Hub hub;
    private final String stuid;
    private final String stoken;
    private final Random random = new Random();

    public MiHoYoSignMiHoYo(MihoyouSignConstant.Hub hub, String stuid, String stoken) {
        this(null, hub, stuid, stoken);
    }

    public MiHoYoSignMiHoYo(String cookie, MihoyouSignConstant.Hub hub, String stuid, String stoken) {
        super(cookie);
        this.hub = hub;
        this.stuid = stuid;
        this.stoken = stoken;
        setClientType(MihoyouSignConstant.COMMUNITY_CLIENT_TYPE);
        setAppVersion(MihoyouSignConstant.APP_VERSION);
        setSalt(MihoyouSignConstant.COMMUNITY_SALT);
    }

    @Override
    public TaskResult doSign(TaskLog log) throws Exception {
        List<PostResult> genShinHomePosts = getGenShinHomePosts();
        List<PostResult> homePosts = getPosts();
        genShinHomePosts.addAll(homePosts);
        log.info("获取社区帖子数: {}", genShinHomePosts.size());
        //执行任务
        int viewPost = doTask(this, this.getClass().getDeclaredMethod("viewPost", PostResult.class), VIEW_NUM, genShinHomePosts);
        int sharePost = doTask(this, this.getClass().getDeclaredMethod("sharePost", PostResult.class), SHARE_NUM, genShinHomePosts);
        int upVotePost = doTask(this, this.getClass().getDeclaredMethod("upVotePost", PostResult.class), UP_VOTE_NUM, genShinHomePosts);
        //打印日志
        appendPostLog(VIEW_NUM, viewPost, "浏览", log);
        appendPostLog(UP_VOTE_NUM, upVotePost, "点赞", log);
        appendPostLog(SHARE_NUM, sharePost, "分享", log);
        return TaskResult.doSuccess();
    }

    public void appendPostLog(int sum, int result, String action, TaskLog log) {
        int failed = sum - result;
        log.append(new TaskLog.LogInfo(
                (failed > 0) ? TaskLog.LogType.WARN : TaskLog.LogType.INFO,
                action + "帖子,成功: " + result + (failed > 0 ? ",失败：" + failed : "")));
    }

    public int doTask(Object obj, Method method, int num, List<PostResult> posts) {
        int sc = 0;
        // 保证每个浏览(点赞，分享)的帖子不重复
        HashSet<Object> set = new HashSet<>(num);
        for (int i = 0; i < num; i++) {
            int index = 0;
            while (set.contains(index)) {
                index = random.nextInt(posts.size());
            }
            set.add(index);
            try {
                method.invoke(obj, posts.get(index));
                sc++;
            } catch (Exception e) {
                log.error("米游社任务调用出错：", e);
            }
            ThreadUtil.safeSleep(random.nextInt(2));
        }
        return sc;
    }

    /**
     * 原神社区签到
     */
    public String sign() {
        Map<String, Object> data = new HashMap<>();
        data.put("gids", hub.getForumId());

        JSONObject signResult = HttpUtil.requestJson(MihoyouSignConstant.HUB_SIGN_URL, data, getHeaders(MihoyouSignConstant.DS_TYPE_ONE), HttpUtil.RequestType.JSON);
        if ("OK".equals(signResult.get("message")) || "重复".equals(signResult.get("message"))) {
            log.info("{}", signResult.get("message"));
            return "社区签到: " + signResult.get("message");
        } else {
            log.info("社区签到失败: {}", signResult.get("message"));
            return "社区签到失败: " + signResult.get("message");
        }
    }

    /**
     * 原神频道
     */
    public List<PostResult> getGenShinHomePosts() throws Exception {
        return getPosts(String.format(MihoyouSignConstant.HUB_LIST1_URL, hub.getForumId()));
    }

    /**
     * 旅行者社区讨论区
     */
    public List<PostResult> getPosts() throws Exception {
        return getPosts(String.format(MihoyouSignConstant.HUB_LIST2_URL, hub.getId()));
    }

    /**
     * 获取帖子
     */
    public List<PostResult> getPosts(String url) throws Exception {
        JSONObject result = HttpUtil.requestJson(url, null, getHeaders(MihoyouSignConstant.DS_TYPE_TWO), HttpUtil.RequestType.GET);
        if ("OK".equals(result.get("message"))) {
            JSONArray jsonArray = result.getJSONObject("data").getJSONArray("list");
            return JSONUtil.toList(jsonArray, PostResult.class);
        } else {
            throw new Exception("帖子数为空，请查配置并更新！！！");
        }
    }

    /**
     * 看帖
     */
    public boolean viewPost(PostResult post) {
        Map<String, Object> data = new HashMap<>();
        data.put("post_id", post.getPost().getPost_id());
        data.put("is_cancel", false);
        JSONObject result = HttpUtil.requestJson(String.format(MihoyouSignConstant.HUB_VIEW_URL, hub.getForumId()), data, getHeaders(MihoyouSignConstant.DS_TYPE_TWO), HttpUtil.RequestType.GET);
        return "OK".equals(result.get("message"));
    }

    /**
     * 点赞
     */
    public boolean upVotePost(PostResult post) {
        Map<String, Object> data = new HashMap<>();
        data.put("post_id", post.getPost().getPost_id());
        data.put("is_cancel", false);
        JSONObject result = HttpUtil.requestJson(MihoyouSignConstant.HUB_VOTE_URL, data, getHeaders(MihoyouSignConstant.DS_TYPE_TWO), HttpUtil.RequestType.JSON);
        return "OK".equals(result.get("message"));
    }

    /**
     * 分享
     */
    public boolean sharePost(PostResult post) {
        JSONObject result = HttpUtil.requestJson(String.format(MihoyouSignConstant.HUB_SHARE_URL, post.getPost().getPost_id()), null, getHeaders(MihoyouSignConstant.DS_TYPE_TWO), HttpUtil.RequestType.GET);
        return "OK".equals(result.get("message"));
    }

    @Override
    public Map<String, String> getHeaders(String dsType) {
        Map<String, String> headers = new HashMap<>();
        headers.put("x-rpc-client_type", getClientType());
        headers.put("x-rpc-app_version", getAppVersion());
        headers.put("x-rpc-sys_version", "12");
        headers.put("x-rpc-channel", "miyousheluodi");
        headers.put("x-rpc-device_id", UUID.randomUUID().toString().replace("-", "").toLowerCase());
        headers.put("x-rpc-device_name", "Xiaomi Redmi Note 4");
        headers.put("Referer", "https://app.mihoyo.com");
        headers.put("Content-Type", "application/json");
        headers.put("Host", "bbs-api.mihoyo.com");
        headers.put("Connection", "Keep-Alive");
        headers.put("Accept-Encoding", "gzip");
        headers.put("User-Agent", "okhttp/4.8.0");
        headers.put("x-rpc-device_model", "Redmi Note 4");
        headers.put("isLogin", "true");
        headers.put("cookie", "stuid=" + stuid + ";stoken=" + stoken + ";");
        if (MihoyouSignConstant.DS_TYPE_ONE.equals(dsType)) {
            JSONObject json = new JSONObject();
            json.set("gids", hub.getForumId());
            headers.put("DS", getDS(json.toString()));
        } else if (MihoyouSignConstant.DS_TYPE_TWO.equals(dsType)) {
            headers.put("DS", getDS());
        }
        return headers;
    }

}