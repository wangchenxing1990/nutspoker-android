package com.netease.nim.uikit.api;

/**
 * Created by 20150726 on 2015/10/21.
 */
public class ApiConstants {
    //http://192.168.0.156:10000/#!/home/project/api     接口示例
    public static final String URL_VALIDATE = /*getHost() + */"user/validate";//验证码
    public static final String URL_SIGN = /*getHost() + */"user/sign";//注册
    public static final String URL_LOGIN = /*getHost() + */"user/login";//登录(测试好)
    public static final String URL_LOGIN_FACEBOOK = /*getHost() + */"user/fblogin";//登录FB(测试好)
    public static final String URL_RESETPASSWORD = /*getHost() + */"user/resetpassword";//重置密码
//    public static final String URL_GETUIDS = ApiConfig./*getHost() + */"user/getuids";//获取手机号对应的UID
    public static final String URL_GETUIDS = /*getHost() + */"user/getuidslist";//获取手机号对应的UID
    public static final String URL_GAME_CREATE = /*getHost() + */"game/create";//创建牌局(测试好)
    public static final String URL_GAME_GETTID = /*getHost() + */"game/gettid";//验证码加入私人牌局(测试好)
    public static final String URL_GAME_START = /*getHost() + */"game/boot";//"game/start";//开始牌局(测试好)
    public static final String URL_GAME_JOIN = /*getHost() + */"game/join";//加入牌局(测试好)
    public static final String URL_GAME_CANCEL = /*getHost() + */"game/cancel";//解散牌局(测试好)
    public static final String URL_GAME_PLAYING = /*getHost() + */"game/playing";//游戏中的牌局(测试好)
    public static final String URL_GAME_PLAYINGLIST = /*getHost() + */"game/playinglist";//群组中牌局的数量
//    public static final String URL_GAME_RECORD = ApiConfig./*getHost() + */"game/record";//七日牌局(测试好)
//    public static final String URL_GAME_RECORDLIST = ApiConfig./*getHost() + */"game/recordlist";//牌局战绩(测试好)
    public static final String URL_GAME_MY_RECORDLIST = /*getHost() + */"game/myrecordlist";//只返回gid列表
    public static final String URL_GAME_MY_RECORDLIST_GID = /*getHost() + */"game/gameList";//根据gid查询战绩
    public static final String URL_GAME_MY_RECORDLIST_GID_TEAM = /*getHost() + */"game/teamGameList";//只返回gid列表--team
    public static final String URL_GAME_MY_RECORDLIST_GID_HORDE = /*getHost() + */"game/hordeGameList";//只返回gid列表--horde
    public static final String URL_GAME_MY_RECORDLIST_BY_GID = /*getHost() + */"/game/gameView";//?gids=6782,6781,6773
    public static final String URL_GAME_RECORD_MEMBERS = /*getHost() + */"game/gameMemebers";//牌局战绩成员列表接口
//    public static final String URL_GAME_WINCHIPS = ApiConfig./*getHost() + */"game/winchips";//战绩-用户每日输赢值波形图(测试好)
    public static final String URL_GAME_USERSTYLE = /*getHost() + */"game/userstyle";//数据统计(测试好)
    public static final String URL_GAME_HISTORY = /*getHost() + */"game/history";//历史数据-加入过的牌局
    public static final String URL_GAME_GAMERECORD = /*getHost() + */"game/gamerecord";//数据统计
    public static final String URL_GAME_GAMEDATA = /*getHost() + */"game/gameData";//数据分析
    public static final String URL_GAME = /*getHost() + */"game";//
    public static final String URL_GAME_CONTROLIN = /*getHost() + */"game/controlin";//控制买入
    public static final String URL_GAME_CONFIG = /*getHost() + */"game/config";//
    public static final String URL_GAME_MTT_START_IN_ADVANCE = /*getHost() + */"/game/StartMtt";// mtt比赛提前开赛  条件是报名人数>单桌人数 POST  params: uid  code

    public static final String URL_GAME_MATCH_CREATE = /*getHost() + */"game/createmtt";//比赛模式创建
    public static final String URL_GAME_MATCH_MTT_CHECKIN = /*getHost() + */"game/mttcheckin";//报名
    public static final String URL_GAME_MATCH_MTT_CANCEL_CHECKIN = /*getHost() + */"game/mttcancelcheckin";//取消报名
    public static final String URL_GAME_MATCH_MTT_PASS_CHECKIN = /*getHost() + */"game/mttpasscheckin";//比赛等级许可
    public static final String URL_GAME_MATCH_MTT_DISMISS = /*getHost() + */"game/mttCancel";//解散牌局
    public static final String URL_GAME_MATCH_MTT_WAIT = /*getHost() + */"game/mttwait";//进入等待中的MTT牌局
    public static final String URL_GAME_MATCH_UPDATE_CONTROL = /*getHost() + */"game/updateControl";//更新牌局控制带入
    public static final String URL_GAME_MATCH_PAUSE = /*getHost() + */"game/mttPause";//暂停
    public static final String URL_GAME_MATCH_MTT_REVIVAL = /*getHost() + */"game/matchRevival";//复活重购 方式POST 参数　code(channel的code) uid       // 1000 参数错误　
                                                                                                                                                  // 5010通道不存在　
                                                                                                                                                  // 3054不是管理员　
                                                                                                                                                  // 3012游戏没开始　
                                                                                                                                                  // 3035用户没有报名　
                                                                                                                                                  // 3001费用不够　
                                                                                                                                                  // 3058游戏在决赛了　
                                                                                                                                                  // 3032当前比赛等级不能重构了　
                                                                                                                                                  // 3056已经申请重构中
                                                                                                                                                  // 3057用户没被淘汰　
                                                                                                                                                  // 3059重构次数已满
    public static final String URL_GAME_MATCH_MTT_CONTROL_REVIVAL = /*getHost() + */"game/matchRevivalPass";//是否同意或拒绝"复活"code uid player_id action 0->拒绝 1->同意
    //修改比赛的比赛说明
    public static final String URL_GAME_MATCH_MTT_REMARK = "game/mttRemark";//  method:GET  params:code 获取牌局备注  method:POST params:code content  设置牌局备注  新增错误码  3098 备注内容长度不正确 3099 设置牌局备注失败
    //
    public static final String URL_GAME_INVITEFRIEND = /*getHost() + */"game/invitefriend";//邀请好友加入牌局
    public static final String URL_GAME_WAIT = /*getHost() + */"game/wait";//进入等待中的牌局
    public static final String URL_GAME_LEAVE = /*getHost() + */"game/leave";//退出等待中的牌局
    public static final String URL_GAME_IGNORE = /*getHost() + */"game/ignore";//忽略邀请
    //
    public static final String URL_USER_FRIENDSTATUS = /*getHost() + */"user/friendstatus";//好友状态(测试好)
    public static final String URL_USER_RESETUINFO = /*getHost() + */"user/resetuinfo";//设置个人信息(不好)
    public static final String URL_USER_FINDFRIEND = /*getHost() + */"user/findfriend";//昵称搜索(测试好)
    public static final String URL_USER_AMOUNT = /*getHost() + */"user/amount";//用户余额接口(测试好)

    public static final String URL_TEAM = /*getHost() + */"team";//俱乐部相关(测试好)
    public static final String URL_TEAM_UPDATE = /*getHost() + */"team/update";//俱乐部资料更新(测试好)
    public static final String URL_TEAM_SEARCH = /*getHost() + */"team/tname";//俱乐部搜索(测试好)
    public static final String URL_TEAM_FRIEND = /*getHost() + */"team/friend";//用户加入的所有俱乐部   用户详情页显示这个用户加入的俱乐部，新版本不需要再调用这个接口
    public static final String URL_TEAM_UPGRADE = /*getHost() + */"team/upgrade";//俱乐部成员上限提升
    public static final String URL_TEAM_RECOMMEND = /*getHost() + */"team/recommend";//推荐俱乐部
    public static final String URL_TEAM_CHECK_ADMIN = /*getHost() + */"team/checkAdmin";//  tid uid "  get
    public static final String URL_TEAM_FUND_GOODS = /*getHost() + */"team/fundGoods";//基金可买物品列表 method:GET   params: 无  return  array
    public static final String URL_TEAM_FUND_PURCHASE = /*getHost() + */"team/purchaseFund";//基金购买  method:POST params:goods_id num tid reurn array  新增错误码 4010  基金添加失败
    public static final String URL_TEAM_FUND_LOG = "team/fundLog";//基金交易记录，每页20条  method:GET  params: id tid  当第一页时候id传0，后面分页传最后一个id
    public static final String URL_TEAM_FUND_GRANT = "team/grantFund";//method:POST  params: fund  member_uid tid
    public static final String URL_TEAM_USER_ALIAS = "team/userAlias";//method:POST  params:member_uid  name  新增错误码 2018 备注名太长  2019 备注失败
    public static final String URL_TEAM_CREDIT_LOG = "team/scoreLog";//team/scoreLog  method:GET 每页20条  params: id tid  当第一页时候id传0，后面分页传最后一个id
    public static final String URL_TEAM_CREDIT_SCORE_SET = "team/creditScoreSet";// method：POST params: tid member_uid score   4013加失败
    public static final String URL_TEAM_CREDIT_SCORE_LIST = "team/creditScoreList";///team/creditScoreList  method:GET params:tid
    public static final String URL_TEAM_CREDIT_MY_SCORE = "team/myScore";//team/myScore  method:GET params:uid   tid

    public static final String URL_SEARCH = /*getHost() + */"index/search";//搜索用户

    public static final String URL_SHOP = /*getHost() + */"shop";//商店
    public static final String URL_SHOP_BUY = /*getHost() + */"shop/buy";//商店购买接口
    public static final String URL_SHOP_PAYMENT = /*getHost() + */"shop/payment";//商店购买校验接口

    public static final String URL_FRIEND_ADD = /*getHost() + */"friend/add";//添加好友
    public static final String URL_FRIEND_DEL = /*getHost() + */"friend/del";//删除好友

    public static final String URL_SHARE = /*getHost() + */"share/share";//分享
    public static final String URL_SHARE_FIND = /*getHost() + */"share/finding";//发现
    public static final String URL_SHARE_COMMENT = /*getHost() + */"share/share";//评论
    public static final String URL_SHARE_LIKE = /*getHost() + */"share/like";//点赞
    public static final String URL_SHARE_REVOKE = /*getHost() + */"share/revoke";//撤回评论

    public static final String URL_HAND_HISTORY_UPLOAD = /*getHost() + */"handhistory/uploads";//上传牌谱
    public static final String URL_HAND_HISTORY_COLOECT = /*getHost() + */"handhistory/collect";//收藏牌谱
    public static final String URL_HAND_HISTORY_UNCOLOECT = /*getHost() + */"handhistory/uncollect";//取消收藏牌谱
    public static final String URL_HAND_HISTORY = /*getHost() + */"handhistory";//牌谱列表

    public static final String URL_APP_UPGRADE = /*getHost() + */"index/ver";//APP检测更新接口
    public static final String URL_HOT_UPDATE = /*getHost() + */"game/checkupdate";//热更新接口(和下载地址区分)

    public static final String URL_LOCATION = /*getHost() + */"user/gps";//根据经纬度获取当前城市

    public static final String URL_LOG = /*getHost() + */".gif";//APP日志统计

    public static final String URL_OSS = /*getHost() + */"oss";//OSS
    public static final String URL_SERVICE = /*getHost() + */"public/service.html";//服务条款
    public static final String URL_PRIVATE = /*getHost() + */"public/private.html";//隐私政策
    public static final String URL_HELP = /*getHost() + */"public/help/index.html";//帮助
    public static final String URL_MTT_RULE = /*getHost() + */"public/help/mtt.html";//MTT规则
    public static final String URL_SNG_RULE = /*getHost() + */"public/help/play.html";//SNG
    public static final String URL_SERVICE_TW = /*getHost() + */"public/service_tw.html";//服务条款

    public static final String URL_PRIVATE_TW = /*getHost() + */"public/private_tw.html";//隐私政策
    public static final String URL_HELP_TW = /*getHost() + */"public/help/index_tw.html";//帮助
    public static final String URL_MTT_RULE_TW = /*getHost() + */"public/help/mtt_tw.html";//MTT规则
    public static final String URL_SNG_RULE_TW = /*getHost() + */"public/help/play_tw.html";//SNG

    public static final String URL_DEVELOP_WEB_SITE = "https://everpoker.win/";//"扑克部落"开发者网站
    public static final String URL_SHARE_URL = /*getHost() + */"public/share/index_share.html";//分享到第三方的URL   替换老的站鱼开发者网站
    public static final String URL_SHARE_URL_TEST = "http://192.168.0.186/assets/nutspoker-other/share/index_share.html";
    public static final String URL_SHARE_URL_IMAGE = /*getHost() + */"public/share/icon.png";//分享到第三方的URL对应图片
    public static final  String URL_ABOUT_US = "file:///android_asset/web/aboutus/about.html";//"关于我们"
    public static final  String URL_ABOUT_US_TEST = "http://192.168.0.186/assets/nutspoker-other/aboutus/about.html";//"关于我们"test

    public static final int TYPE_TEAM_ADVANCE_CLUB = 0;//
    public static final int TYPE_TEAM_ADVANCE_GAME = 1;//

    public static final int AUTHCODE_TYPE_REGISTER = 1;//验证码类型：注册
    public static final int AUTHCODE_TYPE_FORGETPASSWORD = 2;//验证码类型：找回密码

    public static final int SHOP_GOODS_TYPE_COIN = 0;//商店商品类型：金币
    public static final int SHOP_GOODS_TYPE_DIAMOND = 1;//商店商品类型：钻石

    public static final String HTTP_HEADER_TOKEN1 = "Dddi23*DOO#LKD1";
    public static final String HTTP_HEADER_TOKEN2 = "Dddi23*DOO#LKD2";
    public static final String HTTP_HEADER_TOKEN3 = "Dddi23*DOO#LKD4";
    public static final String HTTP_HEADER_TOKEN = "Dddi23*DOO#LKD3";
    public static final String HTTP_HEADER_TOKEN4 = "Dddi23*DOO#LKD5";
    public static final String HTTP_HEADER_TOKEN5 = "Dddi23*DOO#LKD6";

//    public static final int AUTHCODE_TYPE_REGISTER = 1;//验证码类型：注册
//    public static final int AUTHCODE_TYPE_FORGETPASSWORD = 2;//验证码类型：找回密码

    public static final int UPGRADE_IS_MANDTORY = 1;//需要强制更新
    public static final int UPGRADE_IS_SHOW = 1;//需要显示更新提示

    public static final String OS_ANDROID = "1";//设备：android
    public static final int IS_IOS_REGISTER = 1;//已经是IOS注册用户

    //ver = 1.2.1
    //Post param : tid admin_id uid
    public static final String URL_CLUB_ADD_ADMIN = /*getHost() + */"team/addAdmin";
    //Post param : tid admin_id uid
    public static final String URL_CLUB_DELETE_ADMIN = /*getHost() + */"team/delAdmin";


    //ver = 1.2.2
    //战绩首页请求数据 get uid
    public static final String URL_RECORD_INDEX = /*getHost() + */"data/gameRecord";
    // 普通战绩详情 get uid 0日 1月
    public static final String URL_RECORD_NORMAL = /*getHost() + */"data/dataRecord";
    // 比赛战绩数据（sng，mt_sng，mtt）get uid
    public static final String URL_RECORD_MATCH = /*getHost() + */"data/matchRecord";
    // 数据统计 get uid 0全部，1近七天，2近一月
    public static final String URL_NORMAL_DATA_FIND = /*getHost() + */"data/statistics";
    // 普通战绩详情-> 保险数据 get uid
    public static final String URL_INSURANCE_DATA_FIND = /*getHost() + */"data/insuranceRecord";

    //牌谱数据请求 翻页get uid (option)last_hid 如果有此参数，获取下一页数据，没有则获取第一页数据  （默认一页20手）
    //牌谱数据总数 get uid count=1(参数固定)
    public static final String URL_CARDRECORD_LIST = /*getHost() + */"Handhistory/list";
    //牌谱下载 get uid hid
    public static final String URL_CARDRECORD_DOWN = /*getHost() + */"Handhistory/download";

    //牌谱收藏 post uid hid
    public static final String URL_CARDRECORD_COLLECT = /*getHost() + */"Handhistory/collectHands";

    //获取牌局信息
    public static final String URL_GAMEINFO_FROM_GID = /*getHost() + */"game/view";
    //牌谱收藏列表获取接口，使用参考URL_CARDRECORD_LIST
    public static final String URL_CARDCOLLECT_LIST = /*getHost() + */"Handhistory/collectList";
    public static final String METHOD_CARDCOLLECT_LIST = "Handhistory/collectList";

    public static final String URL_CARDCOLLECT_CANCEL = /*getHost() + */"Handhistory/unCollect";
    //mtt添加赛事管理员
    public static final String URL_MTT_ADD_MGR_CLUB_CHANNEL = /*getHost() + */"game/teamAdd";//添加赛事管理员的俱乐部渠道   POST tid code uid
    public static final String URL_MTT_DELETE_MGR_CLUB_CHANNEL = /*getHost() + */"game/teamDel";//  POST  tid code uid
    public static final String URL_MTT_ADD_MGR = /*getHost() + */"game/adminAdd";//方式POST 参数 uid admin_id code 错误码3050 操作失败， 0 成功
    public static final String URL_MTT_DEL_MGR = /*getHost() + */"game/adminDel";//方式POST 参数 uid admin_id code 错误码3050 操作失败， 0 成功
    public static final String URL_MTT_CHECK_MGR = /*getHost() + */"game/adminCheck";//方式GET 参数 uid--改版后这个参数不需要了 admin_id code 错误码3050 操作失败， 1 管理员   0不是管理员
    public static final String URL_USER_BY_ADMIN = /*getHost() + */"game/userByChannel"; //GET 参数　code p (p是页数)
    public static final String URL_MTT_MGR_LSIT = /*getHost() + */"game/channelList";//方式GET 参数 uid--改版后这个参数不需要了 code 错误码3050 操作失败， 0 成功
    public static final String URL_MTT_REMOVE_CHECKIN_PLAYER = /*getHost() + */"game/mttKillUser";//报名踢人  POST  参数 code player_id uid      {"code":3015,"message":"The user is not the owner","data":""}
    //下面三个是对应上面三个的off版本，应该是在战绩详情里面用到的
    public static final String URL_CHECK_ADMIN_OFF = /*getHost() + */"/game/adminCheckOff";  //GET  参数admin_id gid
    public static final String URL_USER_BY_ADMIN_OFF = /*getHost() + */"game/userByChannelOff"; //GET 参数　gid p channel(房主的channel传空)
    public static final String URL_ADMIN_LIST_OFF = /*getHost() + */"game/channelListOff";  //GET 参数　gid
    //
    public static final String URL_ADMIN_LIST_SEARCH_PLAYER = /*getHost() + */"game/searchByAdmin";  //搜索某个渠道下的玩家，GET 参数　gid， word
    public static final String URL_SEND_TEXT_NOTICE = /*getHost() + */"game/notice";  //mtt大厅发送文本公告     POST方式 参数uid code msg     新增错误码 3064 通知发送失败

    //用户离开俱乐部 method:POST  params:  tid, uid
    public static final String URL_LEAVE_TEAM = "team/leave";
    //会长踢人  method:POST  params:uid member_id  tid
    public static final String URL_KICK_USER = "team/kick";
    public static final String URL_TEAM_SEARCH_BY_VID = /*getHost() + */"team/search";//根据vid获取team信息
    public static final String METHOD_LEAVE_TEAM = "team/leave";
    public static final String METHOD_KICK_USER = "team/kick";
    public static final String METHOD_ALI_PAY = "ali/pay";
    public static final String METHOD_PAY_WEIXIN = "shop/wechatpayOrder";//提交方式POST  参数 product_id uid ccode
    public static final String METHOD_PAY_ALI = "shop/alipayOrder";//提交方式POST  参数 product_id uid ccode

    public static final String URL_GAME_FREE_CREATE = /*getHost() + */"game/createGame";//创建自由牌局(测试好)包括普通和sng
    public static final String URL_GAME_GET_GAME = /*getHost() + */"game/getGame";//获取游戏配置信息
    public static final String URL_GAME_JOIN_GAME = "game/joinGame";// : 17/3/8  新接口
    public static final String URL_GAME_START_GAME = /*getHost() + */"game/startGame";//: 17/3/8  新接口

    public static final String URL_INDEX_BANNER = /*getHost() + */"index/banner";//"动态"tab页面顶部的广告业

    //部落相关接口
    public static final String URL_HORDE_CREATE = /*getHost() + */"horde/create";
    public static final String URL_HORDE_UPDATE = /*getHost() + */"horde/update";
    public static final String URL_HORDE_LIST = /*getHost() + */"horde/list";//某个俱乐部下面的部落列表    get   uid  tid
    public static final String URL_HORDE_VIEW = /*getHost() + */"horde/view";//某个部落下的俱乐部列表    get   uid  horde_id
    public static final String URL_HORDE_SEARCH = /*getHost() + */"horde/search";//搜索部落
    public static final String URL_HORDE_JOIN = /*getHost() + */"horde/join";//申请加入部落
    public static final String URL_HORDE_PASS_JOIN = /*getHost() + */"horde/passJoin";//通过或者拒绝加入部落
    public static final String URL_HORDE_PLAYING = /*getHost() + */"horde/playing";//部落进行中的牌局
    public static final String URL_HORDE_CANCEL = /*getHost() + */"horde/cancel";//解散部落
    public static final String URL_HORDE_QUIT = /*getHost() + */"horde/quit";//主动退出部落
    public static final String URL_HORDE_KILL = /*getHost() + */"horde/kill";//将俱乐部移出部落
    public static final String URL_COST_LIST = /*getHost() + */"horde/costList";////创建牌局的时候共享部落的时候配置信息
    public static final String URL_RECORD_LIST_TEAM = /*getHost() + */"game/teamRecordList";//teamRecordList?uid=10033&tid=23884571
    public static final String URL_RECORD_LIST_HORDE = /*getHost() + */"game/hordeRecordList";//teamRecordList?uid=10033&tid=23884571
    public static final String URL_HORDE_SET_SCORE = /*getHost() + */"horde/setScore";//新增错误码:3095 没有打开上分控制 3094 修改积分失败

    public static final String URL_PROTOCOL_REGISTER = "https://www.everpoker.win/protocol/protocol_register.html";//注册时候的扑克部落游戏许可与服务协议
    public static final String URL_PROTOCOL_GAME = "https://www.everpoker.win/protocol/protocol_game.html";//创局和加局时候的扑克部落游戏许可与服务协议

}
