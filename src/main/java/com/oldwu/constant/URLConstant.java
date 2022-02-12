package com.oldwu.constant;

public class URLConstant {
    /**
     * 通用http请求User-Agent
     */
    public static final String HTTP_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36 Edg/86.0.622.69";
    //bili url start--------------
    //bili url start--------------
    //bili url start--------------

    public static final String BILI_QRCODE_URL = "http://passport.bilibili.com/qrcode/getLoginUrl";
    public static final String BILI_QRCODE_STATUS_URL = "http://passport.bilibili.com/qrcode/getLoginInfo";
    /**
     * 直播签到
     */
    public static final String BILI_LIVE_CHECKING = "https://api.live.bilibili.com/xlive/web-ucenter/v1/sign/DoSign";
    public static final String BILI_LOGIN = "https://api.bilibili.com/x/web-interface/nav";
    public static final String BILI_MANGA = "https://manga.bilibili.com/twirp/activity.v1.Activity/ClockIn";
    public static final String BILI_AV_SHARE = "https://api.bilibili.com/x/web-interface/share/add";
    public static final String BILI_COIN_ADD = "https://api.bilibili.com/x/web-interface/coin/add";
    public static final String BILI_IS_COIN = "https://api.bilibili.com/x/web-interface/archive/coins";
    public static final String BILI_GET_REGION_RANKING = "https://api.bilibili.com/x/web-interface/ranking/region";
    public static final String BILI_REWARD = "https://api.bilibili.com/x/member/web/exp/reward";
    /**
     * 查询获取已获取的投币经验.
     */
    public static final String BILI_NEED_COIN = "https://www.bilibili.com/plus/account/exp.php";
    public static final String BILI_NEED_COIN_NEW = "https://api.bilibili.com/x/web-interface/coin/today/exp";
    /**
     * 硬币换银瓜子.
     */
    public static final String BILI_SILVER_2_COIN = "https://api.live.bilibili.com/xlive/revenue/v1/wallet/silver2coin";
    /**
     * 查询银瓜子兑换状态.
     */
    public static final String BILI_GET_SILVER_2_COIN_STATUS = "https://api.live.bilibili.com/xlive/revenue/v1/wallet/myWallet?need_bp=1&need_metal=1&platform=pc";
    /**
     * 上报观看进度.
     */
    public static final String BILI_VIDEO_HEARTBEAT = "https://api.bilibili.com/x/click-interface/web/heartbeat";
    /**
     * 查询主站硬币余额.
     */
    public static final String BILI_GET_COIN_BALANCE = "https://account.bilibili.com/site/getCoin";
    /**
     * 充电请求.
     */
    public static final String BILI_AUTO_CHARGE = "https://api.bilibili.com/x/ugcpay/web/v2/trade/elec/pay/quick";
    /**
     * 充电留言.
     */
    public static final String BILI_CHARGE_COMMENT = "https://api.bilibili.com/x/ugcpay/trade/elec/message";
    public static final String BILI_CHARGE_QUERY = "https://api.bilibili.com/x/ugcpay/web/v2/trade/elec/panel";
    public static final String BILI_QUERY_USER_NAME = "https://api.bilibili.com/x/space/acc/info";
    /**
     * 领取大会员福利.
     */
    public static final String BILI_VIP_PRIVILEGE_RECEIVE = "https://api.bilibili.com/x/vip/privilege/receive";
    /**
     * 领取大会员漫画福利.
     */
    public static final String BILI_MANGA_GET_VIP_REWARD = "https://manga.bilibili.com/twirp/user.v1.User/GetVipReward";
    public static final String BILI_QUERY_DYNAMIC_NEW = "https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/dynamic_new";
    public static final String BILI_VIDEO_VIEW = "https://api.bilibili.com/x/web-interface/view";
    /**
     * 漫画阅读.
     */
    public static final String BILI_MANGA_READ = "https://manga.bilibili.com/twirp/bookshelf.v1.Bookshelf/AddHistory?device=pc&platform=web";
    public static final String BILI_GET_BVID_BY_CREATE = "https://api.bilibili.com/x/space/arc/search";
    public static final String BILI_GET_COIN_LOG = "https://api.bilibili.com/x/member/web/coin/log?jsonp=jsonp";
    public static final String BILI_QUERY_QUESTIONS = "https://api.bilibili.com/x/esports/guess/collection/question";
    public static final String BILI_QUERY_MATCH_INFO = "https://api.bilibili.com/x/esports/guess/collection/statis";
    public static final String BILI_DO_MATCH_ADD = "https://api.bilibili.com/x/esports/guess/add";
    //-------------bili url end
    //-------------bili url end
    //-------------bili url end

    //-------------push url start
    public static final String PUSH_WECHAT_PUSH = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=";
    public static final String PUSH_WECOM_APP_PUSH_GET_TOKEN = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
    public static final String PUSH_WECOM_APP_PUSH = "https://qyapi.weixin.qq.com/cgi-bin/message/send";
    public static final String PUSH_PUSH_PLUS = "https://www.pushplus.plus/send";
    public static final String PUSH_SERVER_PUSH = "https://sc.ftqq.com/";
    public static final String PUSH_SERVER_PUSH_V2 = "https://sctapi.ftqq.com/";
    public static final String PUSH_SERVER_PUSH_TELEGRAM = "https://api.telegram.org/bot";
    //-------------push url end

    //-------------mys url start
    //-------------mys url start
    //-------------mys url start
    public static final String MYS_PERSONAL_INFO_URL = "https://bbs-api.mihoyo.com/user/wapi/getUserFullInfo?gids=2";
    public static final String MYS_TOKEN_URL = "https://api-takumi.mihoyo.com/auth/api/getMultiTokenByLoginTicket?login_ticket=%s&token_types=3&uid=%s";

}
