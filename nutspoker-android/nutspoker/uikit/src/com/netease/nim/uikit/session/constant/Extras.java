package com.netease.nim.uikit.session.constant;

/**
 * Created by zhoujianghua on 2015/8/11.
 */
public interface Extras {
    // 参数
    String EXTRA_ACCOUNT = "account";
    String EXTRA_TYPE = "type";
    String EXTRA_CUSTOMIZATION = "customization";
    String EXTRA_DATA = "data";
    String EXTRA_FROM = "from";
    String EXTRA_FILE_PATH = "file_path";
    String EXTRA_BACK_TO_CLASS = "backToClass";
    //通知栏跳转过来意图
    String EXTRA_PENDINGINTENT_ACIONT = "pending_action";
    //
    String EXTRA_NEW_MESSAGE_NUM = "new_message_num";//新消息数量
    String EXTRA_URL = "url";//网站
    //系统通知
    String EXTRA_APP_MESSAGE = "app_message";
    String EXTRA_SHOW_DIVIDER = "show_divider";
    //自定义消息（IMMESSAGE）
    String EXTRA_MESSAGE = "message";
    //
    String EXTRA_REASON = "reason";//原因
    //
    String EXTRA_GAME_ID = "gid";
    String EXTRA_GAME_CANCEL = "extra_game_cancel";
    String EXTRA_GAME_MATCH_CHIPS = "match_chips";
    String EXTRA_GAME_MATCH_PLAYER = "match_player";
    //
    String EXTRA_IS_IOSER = "isIOSRegister";//是否是IOS注册用户
    String EXTRA_PHONE = "phone";//手机号
    String EXTRA_COUNTRY_CODE = "country_code";//手机号国际区号
    String EXTRA_REGION = "region";//区域
    String EXTRA_AUTHCODE = "authcode";//验证码
    String EXTRA_SESSION_ID = "session_id";
    String EXTRA_SESSION_TYPE = "session_type";
    String EXTRA_GAME_TYPE = "game_type";//游戏类型：1.私人 2.圈子俱乐部牌局
    String EXTRA_GAME_CREATE_IS_CLUB_MANAGER = "extra_game_create_is_club_manager";//在老的会员等级规则下：   俱乐部里面创建游戏规则---->1普通成员上限3  管理员5          俱乐部外面创建游戏规则：都是3
    String EXTRA_GAME_CREATE_NEW_GAME_INTERFACE = "extra_game_create_new_game_interface";
    String EXTRA_HANDCOUNT = "handCount";//手牌数量
    String EXTRA_BILL = "bill";
    String EXTRA_BILL_LIST = "bill_list";
    //编辑
    String EXTRA_EDIT_KEY = "edit_key";//编辑信息关键字
    //搜索
    String EXTRA_SEARCH_KEY = "search_key";//搜索信息关键字
    String EXTRA_USER_LIST = "list";//搜索到的列表
    //分享
    String EXTRA_SHARE_TYPE = "share_type";//分享类型
    String EXTRA_SHARE_DATA = "share_data";//分享数据
    //验证
    String EXTRA_VERIFY_TYPE = "verify_type";//发送验证类型
    //消息
    String EXTRA_MESSAGE_TYPE = "message_type";//消息类型
    String EXTRA_MESSAGE_DATA = "message_data";//消息数据
    //商店
    String EXTRA_SHOP_TYPE = "shop_type";//商店类型
    //俱乐部
    String EXTRA_TEAM_DATA = "team";
    String KEY_TEAM_MEMBER = "key_team_member";
    String EXTRA_TEAM_ID = "team_id";//俱乐部ID
    String EXTRA_TEAM_NAME = "team_name";//俱乐部名称
    String EXTRA_TEAM_LIST = "team_list";//俱乐部列表
    String EXTRA_SEARCH_CLUB_TYPE = "search_club_type";
    String EXTRA_SEARCH_CLUB_KEY = "search_club_key";
    String EXTRA_SEARCH_CLUB_AREA = "search_club_area";
    String EXTRA_ROOM_ID = "room_id";//
    String EXTRA_ROOM_INFO = "room_info";//
    //区域
    String EXTRA_REGION_TYPE = "region_type";//区域类型
    String EXTRA_REGION_SHOW = "region_show";//区域展示文字
    String EXTRA_REGION_DATA = "region_data";//区域数据
    String EXTRA_MY_AREA_ID = "my_area_id";//我的所在地ID
    String EXTRA_REGION_CHOICE = "region_choice";//父类区域
    //游戏
    String EXTRA_GAME_ACTION_TYPE = "gameActionType";//游戏动作：1.玩  2.播放
    //游戏（玩）
    String EXTRA_GAME_CODE = "code";
    String EXTRA_GAME_CHANNEL = "channel";
    String EXTRA_GAME_JOIN_CODE = "join_code";
    String EXTRA_GAME_MODE = "gameMode";
    String EXTRA_GAME_IS_CREATOR = "gameIsCreator";
    String EXTRA_GAME_NAME = "gameName";
    String EXTRA_GAME_SESSIONID = "sessionId";
    String EXTRA_GAME_GAMETYPE = "gameType";
    String EXTRA_GAME_GAMEID = "gid";
    String EXTRA_GAME_SERVERIP = "server_ip";//游戏服务器IP
    String EXTRA_GAME_KO_MODE = "ko_mode";//游戏服务器IP
    //MT模式观战
    String EXTRA_GAME_LOOK_TABLE_INDEX = "lookTableIndex";
    String EXTRA_GAME_LOOK_UID = "lookUid";
    //游戏（看）
    String EXTRA_GAME_SHEET_PATH = "sheetPath";
    String EXTRA_GAME_SHEET_UID = "sheetUid";
    // 选择图片
    String EXTRA_NEED_CROP = "need-crop";
    String EXTRA_OUTPUTX = "outputX";
    String EXTRA_OUTPUTY = "outputY";
    String EXTRA_FROM_LOCAL = "from_local";
    String EXTRA_SRC_FILE = "src-file";
    String EXTRA_RETURN_DATA = "return-data";
    //图片选自器
    String EXTRA_PHOTO_LISTS = "photo_list";
    String EXTRA_SELECTED_IMAGE_LIST = "selected_image_list";
    String EXTRA_MUTI_SELECT_MODE = "muti_select_mode";
    String EXTRA_MUTI_SELECT_SIZE_LIMIT = "muti_select_size_limit";
    String EXTRA_SUPPORT_ORIGINAL = "support_original";
    String EXTRA_IS_ORIGINAL = "is_original";
    String EXTRA_PREVIEW_CURRENT_POS = "current_pos";
    String EXTRA_PREVIEW_IMAGE_BTN_TEXT = "preview_image_btn_text";
    String EXTRA_SCALED_IMAGE_LIST = "scaled_image_list";
    String EXTRA_ORIG_IMAGE_LIST = "orig_image_list";
    String EXTRA_NEED_SHOW_SEND_ORIGINAL = "need_show_send_original_image";

    String EXTRA_HORDE_ID = "horde_id";
    String EXTRA_HORDE_COST_LSIT = "extra_horde_cost_lsit";
}
