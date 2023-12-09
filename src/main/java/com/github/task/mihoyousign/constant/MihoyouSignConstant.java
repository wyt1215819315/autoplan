package com.github.task.mihoyousign.constant;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

public class MihoyouSignConstant {

    public static final String MYS_PERSONAL_INFO_URL = "https://bbs-api.miyoushe.com/user/wapi/getUserFullInfo?gids=2";
    public static final String MYS_TOKEN_URL = "https://api-takumi.miyoushe.com/auth/api/getMultiTokenByLoginTicket?login_ticket=%s&token_types=3&uid=%s";


    /**
     * DS 类型 1-社区签到
     */
    public static final String DS_TYPE_ONE = "communityCheckIn";

    /**
     * DS 类型 2-帖子操作相关
     */
    public static final String DS_TYPE_TWO = "postAction";

    /**
     * AppVersion
     */
    public static final String APP_VERSION = "2.60.1";

    /**
     * 签到Salt
     */
    public static final String SIGN_SALT = "1OJyMNCqFlstEQqqMOv0rKCIdTOoJhNt";

    /**
     * 社区签到Salt
     */
    public static final String COMMUNITY_SIGN_SALT = "t0qEgfub6cvueAPgR5m9aQWWVciEer7v";

    /**
     * 社区Salt
     */
    public static final String COMMUNITY_SALT ="AcpNVhfh0oedCobdCyFV8EE1jMOVDy9q";

    /**
     * 签到ClientType
     */
    public static final String SIGN_CLIENT_TYPE = "5";

    /**
     * 社区ClientType
     */
    public static final String COMMUNITY_CLIENT_TYPE = "2";



    /**
     * genshin
     **/
    public static final String YS_SIGN_ACT_ID = "e202311201442471"; // 切勿乱修改

    public static final String XQTD_SIGN_ACT_ID = "e202304121516551"; // 切勿乱修改

    public static final String NEW_SIGN_ORIGIN = "https://act.mihoyo.com/";


    /**
     * Android 2
     */
    public static final String CLIENT_TYPE = "5"; // 切勿乱修改

    public static final String DEVICE_NAME = "Xiaomi Redmi Note 4";

    public static final String DEVICE_MODE = "Redmi Note 4";

    public static final String REGION = "cn_gf01"; // 切勿乱修改

    public static final String REFERER_URL = String.format("https://webstatic.mihoyo.com/bbs/event/signin-ys/index.html?bbs_auth_required=%s&act_id=%s&utm_source=%s&utm_medium=%s&utm_campaign=%s", true, YS_SIGN_ACT_ID, "bbs", "mys", "icon");

    public static final String AWARD_URL = "https://api-takumi.mihoyo.com/event/luna/home";

    public static final String YS_ROLE_URL = String.format("https://api-takumi.mihoyo.com/binding/api/getUserGameRolesByCookie?game_biz=%s", "hk4e_cn");

    public static final String XQTD_ROLE_URL = String.format("https://api-takumi.mihoyo.com/binding/api/getUserGameRolesByCookie?game_biz=%s", "hkrpg_cn");


    public static final String INFO_URL = "https://api-takumi.mihoyo.com/event/luna/info";

    public static final String SIGN_URL = "https://api-takumi.mihoyo.com/event/luna/sign";


    public static final String USER_AGENT_TEMPLATE = "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) miHoYoBBS/%s";
    public static final String USER_AGENT = String.format(USER_AGENT_TEMPLATE, APP_VERSION);



    /**
     * mihoyo hub
     */
    public static final String SIGN_IN_STATUS = "https://bbs-api.mihoyo.com/apihub/sapi/querySignInStatus";

    public static final String HUB_SIGN_URL = "https://bbs-api.mihoyo.com/apihub/app/api/signIn";

    public static final String HUB_LIST1_URL = "https://bbs-api.mihoyo.com/post/api/getForumPostList?forum_id=%s&is_good=false&is_hot=false&page_size=20&sort_type=1";

    public static final String HUB_LIST2_URL = "https://bbs-api.mihoyo.com/post/api/feeds/posts?fresh_action=1&gids=%s&last_id=";

    public static final String HUB_VIEW_URL = "https://bbs-api.mihoyo.com/post/api/getPostFull?post_id=%s";

    public static final String HUB_SHARE_URL = "https://bbs-api.mihoyo.com/apihub/api/getShareConf?entity_id=%s&entity_type=1";

    public static final String HUB_EXTERNAL_LINK_URL = "https://bbs-api.mihoyo.com/post/api/externalLink?post_id=%s";

    public static final String HUB_VOTE_URL = "https://bbs-api.mihoyo.com/apihub/sapi/upvotePost";


    @Getter
    public enum HubsEnum {
        BH3(new Hub.HubBuilder().id("1").forumId("1").name("崩坏3").url("https://bbs.mihoyo.com/bh3/").build()),
        YS(new Hub.HubBuilder().id("2").forumId("26").name("原神").url("https://bbs.mihoyo.com/ys/").build()),
        BH2(new Hub.HubBuilder().id("3").forumId("30").name("崩坏2").url("https://bbs.mihoyo.com/bh2/").build()),
        WD(new Hub.HubBuilder().id("4").forumId("37").name("未定事件簿").url("https://bbs.mihoyo.com/wd/").build()),
        DBY(new Hub.HubBuilder().id("5").forumId("34").name("大别野").url("https://bbs.mihoyo.com/dby/").build());

        private final Hub game;

        HubsEnum(Hub game) {
            this.game = game;
        }

    }


    @Data
    @Builder
    public static class Hub {

        private String id;
        private String forumId;
        private String name;
        private String url;

    }
}
