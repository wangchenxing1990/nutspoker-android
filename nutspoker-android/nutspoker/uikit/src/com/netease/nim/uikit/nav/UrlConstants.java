package com.netease.nim.uikit.nav;

/**
 * Created by 周智慧 on 17/1/2.  nav导航
 * //activity页面通过uri跳转，实现解耦
 */

public class UrlConstants {
    //<data android:host="com.htgames.nutspoker" android:pathPrefix="/goto/recordDetailMgrLsit" android:scheme="everpoker"/>
    public static  final String URL_PREFIX = "everpoker://com.htgames.nutspoker/goto/";

    public static final String URL_WELCOME = "everpoker://com.htgames.nutspoker/launcher";//WelcomeActivity

    public static final String URL_LOGIN = URL_PREFIX + "login";//LoginActivity

    public static final String URL_WEBVIEW = URL_PREFIX + "webview";//WebViewActivity

    public static final String URL_MAIN = URL_PREFIX + "mainactivity";//MainActivity

    public static final String URL_APP_CONTROL = URL_PREFIX + "app_msg_control";//AppMsgControlAC

    public static final String DEVELOP = URL_PREFIX + "develop"; //开发者彩蛋
    public static final String NETWORK_MONITOR = URL_PREFIX + "network_monitor"; //开发者彩蛋network_monitor

    public static final String WATCH_PIC_AC = URL_PREFIX + "watchPicAc"; //点击用户头像显示大图
    public static final String WATCH_PIC_AC_USER_INFO = WATCH_PIC_AC + "_user_info"; //传递的NimUserInfo信息

    public static final String SNG_BLIND_STRUCTURE = URL_PREFIX + "sngBlindStructure"; //sng盲注结构表页面
    public static final String MTT_BLIND_STRUCTURE = URL_PREFIX + "mttBlindStructure"; //mtt盲注结构表页面
    public static final String CLUB_MESSAGE_VERIFY = URL_PREFIX + "clubMessageVerify"; //俱乐部验证消息页面

    public static final String ADD_GAME_MANAGER = URL_PREFIX + "addGameManager"; //mtt大厅添加游戏管理员
    public static final String ADD_GAME_MANAGER_CREATOR_ID = ADD_GAME_MANAGER + "_creatorId";//nav跳转的时候传给intent的key
    public static final String ADD_GAME_MANAGER_GAME_ID = ADD_GAME_MANAGER + "_gameId";
    public static final String ADD_GAME_MANAGER_GAME_INFO = ADD_GAME_MANAGER + "_gameInfo";
    public static final String ADD_GAME_MANAGER_GAME_CODE = ADD_GAME_MANAGER + "_gameCode";
    public static final String ADD_GAME_MANAGER_CLUBIDENTITY = ADD_GAME_MANAGER + "_clubIdentity";

    public static final String MTT_MY_MGR = URL_PREFIX + "mttMyMgr"; //mtt大厅右上角"我的管理"点击落地页
    public static final String MTT_MY_MGR_STATUS_IS_WAIT = MTT_MY_MGR + "_statusIsWait";//nav跳转的时候传给intent的key  -- 比赛是否是准备状态，只有这个状态才能移除报名玩家
    public static final String MTT_MY_MGR_GAME_ID = MTT_MY_MGR + "_gameId";//nav跳转的时候传给intent的key
    public static final String MTT_MY_MGR_CREATOR_ID = MTT_MY_MGR + "_creatorId";
    public static final String MTT_MY_MGR_GAME_CODE = MTT_MY_MGR + "_gameCode";

    public static final String RECORD_DETAIL_MGR_LSIT = URL_PREFIX + "recordDetailMgrLsit"; //战绩->牌局详情 房主的管理员列表页面，和mtt大厅房主的管理员列表页面不复用activity，但是布局xml是复用的

    public static final String RECORD_DETAIL_MY_MGR = URL_PREFIX + "recordDetailMyMgr"; //战绩->牌局详情 1管理员 我的管理 页面， 2房主有个管理员列表，这个列表item点击的落地页也是在这个页面
    public static final String RECORD_DETAIL_MY_MGR_HEAD_TITLE_KEY = RECORD_DETAIL_MY_MGR + "key_head_title";//房主点击管理员listitem显示这个管理员nickname；房主点击自己item的显示"我的管理"；管理员点击"我的管理"显示"参赛人员"
    public static final String RECORD_DETAIL_MY_MGR_FROM_TYPE = RECORD_DETAIL_MY_MGR + "_fromType";//这个页面的来源，1管理员 我的管理 页面， 2房主有个管理员列表，这个列表item点击的落地页也是在这个页面
    public static final String RECORD_DETAIL_MY_MGR_CHANNEL = RECORD_DETAIL_MY_MGR + "_channel";//渠道号
    public static final String RECORD_DETAIL_MY_MGR_ID = RECORD_DETAIL_MY_MGR + "_mgrID";//管理员id
    public static final int RECORD_DETAIL_MY_MGR_FROM_HEAD = 0;//来自牌局详情右上角"赛事管理员"
    public static final int RECORD_DETAIL_MY_MGR_FROM_ITEM = 1;//来自    房主有个管理员列表，这个列表item点击是第二个点击来源

}
