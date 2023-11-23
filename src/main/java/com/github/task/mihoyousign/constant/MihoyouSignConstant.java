package com.github.task.mihoyousign.constant;

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
}
