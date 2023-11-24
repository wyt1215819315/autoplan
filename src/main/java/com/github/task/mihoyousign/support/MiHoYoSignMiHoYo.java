package com.github.task.mihoyousign.support;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.system.task.dto.TaskLog;
import com.github.system.task.dto.TaskResult;
import com.github.task.mihoyousign.constant.MihoyouSignConstant;
import com.github.task.mihoyousign.support.pojo.PostResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

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
    private final MiHoYoConfig.Hub hub;
    private final String stuid;
    private final String stoken;
    private final Random random = new Random();
    private final CountDownLatch countDownLatch = new CountDownLatch(3);

    public MiHoYoSignMiHoYo(MiHoYoConfig.Hub hub, String stuid, String stoken) {
        this(null, hub, stuid, stoken);
    }

    public MiHoYoSignMiHoYo(String cookie, MiHoYoConfig.Hub hub, String stuid, String stoken) {
        super(cookie);
        this.hub = hub;
        this.stuid = stuid;
        this.stoken = stoken;
        setClientType(MihoyouSignConstant.COMMUNITY_CLIENT_TYPE);
        setAppVersion(MihoyouSignConstant.APP_VERSION);
        setSalt(MihoyouSignConstant.COMMUNITY_SALT);
    }

    public static String getCookieByName(String cookie, String name) {
        String[] split = cookie.split(";");
        for (String s : split) {
            String h = s.trim();
            if (h.startsWith(name)) {
                return h.substring(h.indexOf('=') + 1);
            }
        }
        return null;
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
        log.append(new TaskLog.LogInfo(
                ((VIEW_NUM - viewPost) > 0) ? TaskLog.LogType.WARN : TaskLog.LogType.INFO,
                "浏览帖子,成功: " + viewPost + ",失败：" + (VIEW_NUM - viewPost)));
        log.append(new TaskLog.LogInfo(
                ((UP_VOTE_NUM - upVotePost) > 0) ? TaskLog.LogType.WARN : TaskLog.LogType.INFO,
                "点赞帖子,成功: " + upVotePost + ",失败：" + (UP_VOTE_NUM - upVotePost)));
        log.append(new TaskLog.LogInfo(
                ((SHARE_NUM - sharePost) > 0) ? TaskLog.LogType.WARN : TaskLog.LogType.INFO,
                "分享帖子,成功: " + sharePost + ",失败：" + (SHARE_NUM - sharePost)));
        return TaskResult.doSuccess();
    }

    public Map<String, Object> doSingleThreadSign() throws Exception {
        Map<String, Object> map = new HashMap<>();
        String msg = "";
        String sign = sign();
        msg = msg + sign + "\n";
        List<PostResult> genShinHomePosts = getGenShinHomePosts();
        List<PostResult> homePosts = getPosts();
        genShinHomePosts.addAll(homePosts);
        log.info("获取社区帖子数: {}", genShinHomePosts.size());
        msg = msg + "获取社区帖子数: " + genShinHomePosts.size() + "\n";
        //执行任务
        Callable<Integer> viewPost = createTask(this, "viewPost", VIEW_NUM, genShinHomePosts);
        Callable<Integer> sharePost = createTask(this, "sharePost", SHARE_NUM, genShinHomePosts);
        Callable<Integer> upVotePost = createTask(this, "upVotePost", UP_VOTE_NUM, genShinHomePosts);

        FutureTask<Integer> vpf = new FutureTask<>(viewPost);
        FutureTask<Integer> upf = new FutureTask<>(upVotePost);
        FutureTask<Integer> spf = new FutureTask<>(sharePost);

        List<FutureTask<Integer>> fts = Arrays.asList(vpf, upf, spf);
        for (FutureTask<Integer> ft : fts) {
            new Thread(ft).start();
        }
        countDownLatch.await();
        //打印日志
        log.info("浏览帖子: {},点赞帖子: {},分享帖子: {}", vpf.get(), upf.get(), spf.get());
        msg = msg + "浏览帖子: " + vpf.get() + ",点赞帖子: " + upf.get() + ",分享帖子: " + spf.get();
        map.put("flag", true);
        map.put("msg", msg);
        return map;
    }

    public Callable<Integer> createTask(Object obj, String methodName, int num, List<PostResult> posts) {
        return () -> {
            try {
                return doTask(obj, obj.getClass().getDeclaredMethod(methodName, PostResult.class), num, posts);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return 0;
        };
    }

    public int doTask(Object obj, Method method, int num, List<PostResult> posts) {
        countDownLatch.countDown();
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
                e.printStackTrace();
            }
            try {
                TimeUnit.SECONDS.sleep(random.nextInt(2));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return sc;
    }

    /**
     * 原神社区签到
     */
    public String sign() {
        Map<String, Object> data = new HashMap<>();
        data.put("gids", hub.getForumId());

        JSONObject signResult = HttpUtils.doPost(MiHoYoConfig.HUB_SIGN_URL, getHeaders(MihoyouSignConstant.DS_TYPE_ONE), data);
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
     *
     */
    public List<PostResult> getGenShinHomePosts() throws Exception {
        return getPosts(String.format(MiHoYoConfig.HUB_LIST1_URL, hub.getForumId()));
    }

    /**
     * 旅行者社区讨论区
     *
     */
    public List<PostResult> getPosts() throws Exception {
        return getPosts(String.format(MiHoYoConfig.HUB_LIST2_URL, hub.getId()));
    }

    /**
     * 获取帖子
     *
     */
    public List<PostResult> getPosts(String url) throws Exception {
        JSONObject result = HttpUtils.doGet(url, getHeaders(MihoyouSignConstant.DS_TYPE_TWO));
        if ("OK".equals(result.get("message"))) {
            JSONArray jsonArray = result.getJSONObject("data").getJSONArray("list");
            List<PostResult> posts = JSONUtil.toList(jsonArray, PostResult.class);
            return posts;
        } else {
            throw new Exception("帖子数为空，请查配置并更新！！！");
        }
    }

    /**
     * 看帖
     *
     */
    public boolean viewPost(PostResult post) {
        Map<String, Object> data = new HashMap<>();
        data.put("post_id", post.getPost().getPost_id());
        data.put("is_cancel", false);
        JSONObject result = HttpUtils.doGet(String.format(MiHoYoConfig.HUB_VIEW_URL, hub.getForumId()), getHeaders(MihoyouSignConstant.DS_TYPE_TWO), data);
        return "OK".equals(result.get("message"));
    }

    /**
     * 点赞
     *
     */
    public boolean upVotePost(PostResult post) {
        Map<String, Object> data = new HashMap<>();
        data.put("post_id", post.getPost().getPost_id());
        data.put("is_cancel", false);
        JSONObject result = HttpUtils.doPost(MiHoYoConfig.HUB_VOTE_URL, getHeaders(MihoyouSignConstant.DS_TYPE_TWO), data);
        return "OK".equals(result.get("message"));
    }

    /**
     * 分享
     *
     */
    public boolean sharePost(PostResult post) {
        JSONObject result = HttpUtils.doGet(String.format(MiHoYoConfig.HUB_SHARE_URL, post.getPost().getPost_id()), getHeaders(MihoyouSignConstant.DS_TYPE_TWO));
        return "OK".equals(result.get("message"));
    }

    /**
     * 获取 stoken
     *
     */
    public String getCookieToken() throws Exception {
        JSONObject result = HttpUtils.
                doGet(String.format(MiHoYoConfig.HUB_COOKIE2_URL, getCookieByName("login_ticket"), getCookieByName("account_id")), getHeaders(MihayouConstants.DS_TYPE_TWO));
        if (!"OK".equals(result.get("message"))) {
            log.info("login_ticket已失效,请重新登录获取");
            throw new Exception("login_ticket已失效,请重新登录获取");
        }
        return (String) result.getJSONObject("data").getJSONArray("list").getJSONObject(0).get("token");
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