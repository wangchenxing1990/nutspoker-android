package com.netease.nim.uikit.api;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.common.util.string.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * 德州圈网络CODE
 */
public class ApiCode {
    //GameStatusCode
    public final static int CODE_SUCCESS = 0;//
    public final static int CODE_MONOPOLY_UPDATE = 33333;//需要强制更新的code
    //
    public final static int CODE_PARAMS_ERROR = 1000;//参数错误
    public final static int CODE_ILLEGAL_REQUEST = 1001;//非法请求
    //
    public final static int CODE_USER_NICKNAME_LONG = 2000;//用户昵称太长
    public final static int CODE_ACCOUNT_PHONE_DIGITAL = 2001;//手机长度必须是11位
    public final static int CODE_REGISTER_FAILED = 2002;//注册失败
    public final static int CODE_ACCOUNT_NONE_EXISTENT = 2004;//该手机还未注册
    public final static int CODE_ACCOUNT_PASSWORD_IS_SHORT = 2005;//密码太短（本地判断，因为MD5）
    public final static int CODE_ACCOUNT_PASSWORD_IS_LONG = 2006;//密码太长（本地判断，因为MD5）
    public final static int CODE_ACCOUNT_PASSWORD_FORMAT_WRONG = 2007;//密码格式不正确
    public final static int CODE_ACCOUNT_EXIST = 2008;//该手机已经注册
    public final static int CODE_ACCOUNT_PASSWORD_INCORRECT = 2009;//密码无效
    public final static int CODE_USER_NO_FRIENDS = 2011;//该用户没有好友
    public final static int CODE_AUTHCODE_ERROR = 2013;//验证码无效
    public final static int CODE_NICKNAME_FORMAT_WRONG = 2014;//昵称设置格式不对
    public final static int CODE_NICKNAME_EXISTED = 2016;//用户昵称已经存在
    public final static int CODE_ACCOUNT_CLOSED = 2017;//账号被封
    //
    public final static int CODE_GAME_IS_CREATED = 3000;//已经创建过牌局
    public final static int CODE_BALANCE_INSUFFICIENT = 3001;//用户余额不足
    public final static int CODE_GAME_NAME_LENGTH_LONG = 3002;//游戏创建名称太长
    public final static int CODE_GAME_NAME_FORMAT_WRONG = 3003;//游戏名称格式不对
    public final static int CODE_GAME_CREATE_FAILURE = 3004;//游戏创建失败
    public final static int CODE_GAME_NOT_ING = 3005;//没有进行中的牌局   或者创建俱乐部游戏无teamid
    public final static int CODE_GAME_NONE_EXISTENT = 3007;//牌局不存在
    public final static int CODE_GAME_JOIN_FAILURE = 3008;//加入牌局失败
    public final static int CODE_GAME_IS_IN = 3010;//已经在该牌局中
    public final static int CODE_GAME_IS_BEGIN = 3011;//牌局已经开始
    public final static int CODE_GAME_IS_FINISH = 3012;//牌局已经结束
    public final static int CODE_GAME_JOIN_SUCCESS = 3013;//用户加入牌局成功
    public final static int CODE_USER_STATUS_UPDATE_FAILURE = 3014;//用户状态更新失败
    public final static int CODE_USER_IS_NOT_OWNER = 3015;//用户不是拥有者
    public final static int CODE_GAME_DISMISS_FAILURE = 3016;//牌局解散失败
    public final static int CODE_USER_NOT_IN_GAME = 3017;//用户不在游戏里
    public final static int CODE_GAME_NOT_START = 3021;//游戏没有开始
    public final static int CODE_GAME_TEAM_COUNT_IS_LIMIT = 3024;//用户的俱乐部牌局创建已经达到上限
    public final static int CODE_GAME_PRIVATE_COUNT_IS_LIMIT = 3025;//用户的私人牌局创建已经达到上限
    public final static int CODE_CLUB_CREATE_COUNT_IS_LIMIT = 3026;//用户创建的俱乐部已经达到上限
    public final static int CODE_CLUB_CREATE_BY_OWNER = 3028;//只能会长开局
    public final static int CODE_MATCH_CHECKIN_FAILURE_NOTEXIST = 3030;//游戏不存在（已经结束）
    public final static int CODE_MATCH_CHECKIN_ALREADY = 3031;//已经报名
    public final static int CODE_MATCH_CHECKIN_FAILURE_BLINDLEVEL_REACH = 3032;//盲注等级到达等级
    public final static int CODE_MATCH_CHECKIN_FAILURE_FINAL = 3033;//到达决赛桌
    public final static int CODE_MATCH_CHECKIN_FAILURE_DEALING = 3034;//报名处理中
    public final static int CODE_MATCH_CHECKIN_FAILURE_CUTOFF = 3036;//报名时间截至
    public final static int CODE_CHANNEL_BLOCK_CHECKIN = 3100;//渠道禁止报名
    public final static int CODE_MATCH_CHECKIN_START_MOMENT = 3093;//报名的时候新增错误码，游戏正在开始中，请稍后重试,位森加了个锁
    public final static int CODE_MATCH_CHECKIN_FAILURE_CHANNEL_NOT_FOUND = 5010;//没有这个渠道(多渠道&多管理员添加的code)
    public final static int CODE_MATCH_CHECKIN_FAILURE_CANT = 3037;//游戏已经开始不能报名,MT-SNG
    public final static int CODE_MATCH_CHECKIN_FAILURE_PLAYER_FULL = 3038;//用户已经报满
    public final static int CODE_MATCH_CHECKIN_FAILURE_ALREADY_DEALED = 3092;//该请求已被处理  比如别人已经处理了复活消息，再次处理这个复活
    public final static int CODE_MATCH_CHECKIN_FAILURE_ADMIN_FORBID = 3054;//管理员禁止报名
    public final static int CODE_MATCH_CHECKIN_CONTROL_NOTCHANGE = 3039;//状态修改失败，因为已经打开
    public final static int CODE_MATCH_CHECKIN_CONTROL_FAILURE = 3040;//状态修改失败
    public final static int CODE_MATCH_CHECKIN_CONTROL_HANDLED_BY_OTHER_MGR = 3051;//报名已经被其他管理员处理
    public final static int CODE_MATCH_CHECKIN_CONTROL_COIN_NOT_SUFFICIENT = 3061;//报名玩家余额不足(这种情况是玩家点击"报名"时余额还够，但是当房主同意时玩家余额不足了)
    public final static int CODE_MATCH_CHECKIN_ONLY_IN_ORIGINAL_CHANNEL = 3063;//要在原channel复活
    public final static int CODE_QUITE_HORDE_HAVING_GAME = 3047;//退出部落失败，因为存在牌局
    public final static int CODE_MODIFY_CLUB_NAME_TIME_FORBIDDEN = 3089;//修改俱乐部名称允许修改时间还没到 90天
    public final static int CODE_MODIFY_CLUB_NAME_ALREADY_EXISTED = 3090;//修改俱乐部名称已经被暂用    参考 2016 和 3071
    public final static int CODE_MODIFY_HORDE_NAME_ALREADY = 3091;//部落修改过一次， 不能再修改
    public final static int CODE_MATCH_CHECKIN_FAILURE_SCORE_RETURN = 3097;//取消报名返回部落上分失败

    public final static int CODE_SHOP_GOODS_BUY_FAILURE = 4003;//购买商品失败
    public final static int CODE_SHOP_GOODS_BUY_CHECK_ALREADY_USED = 4006;//购买商品已近被消耗
    public final static int CODE_UPDATE_NICKNAME_UBSUFFICIENT_DIAMOND = 3009;//钻石不足
    //房主主动暂停错误码
    public final static int CODE_MATCH_PAUSE_PLAYER_NOTEXIST = 5005;//玩家不存在
    public final static int CODE_MATCH_PAUSE_NOT_OWNER = 5006;//不是房主
    public final static int CODE_MATCH_PAUSE_AREADY = 5007;//牌局已暂停
    public final static int CODE_MATCH_PAUSE_GAME_NOTEXIST = 5008;//牌局不存在
    public final static int CODE_MATCH_PAUSE_NOT = 5009;//牌局没有暂停 不能开始
    //
    public final static int CODE_CIRCLE_COMMENT_FAILURE = 6000;//评论失败
    public final static int CODE_CIRCLE_LIKE_FAILURE = 6001;//点赞失败
    public final static int CODE_CIRCLE_COMMENT_NOT_EXIST = 6002;//评论不存在
    public final static int CODE_CIRCLE_ALLOW_PUBLISHER_DELETE = 6003;//只允许发布者删除
    public final static int CODE_HANDS_COLLECT_AREADY = 9102;//已经收藏过
    public final static int CODE_HANDS_COLLECT_LIMIT = 9103;//收藏达到上限
    public final static int CODE_NETEASE_ERROR = 9999;//（第三方服务）服务器错误

    public final static int CODE_JSON_ERROR = 20000;//系统解析异常
    public final static int CODE_NETWORD_ERROR = 20001;//网络异常

    public final static int CODE_MESSAGE_SHOW = 998;//接口信息按照接口返回显示

    public final static int CODE_ADMIN_DEL_HAS_CHECKIN = 9021;//mtt移除管理员返回code, 这个值表示管理员已有报名用户，无法移除
    public final static int CODE_ADMIN_DEL_HAS_CHECKIN_ORIGINAL = 3053;//mtt移除管理员返回code, 这个值表示管理员已有报名请求(未处理)，无法移除

    public final static int CODE_ADD_GAME_MGR_FAILER_ALREADY_CHECK = 3055;//mtt添加管理员失败code，表示玩家已经报名，不能设置为管理员
    public final static int CODE_ADD_GAME_MGR_FAILER_ALREADY_IS = 3066;//mtt添加管理员失败code，表示玩家已经是管理员

    public final static int CODE_SEND_GAME_NOTICE = 3065;//发送文本公告10秒内只能发送一次

    public final static int CODE_CLUB_NOT_EXISTED = 3067;//俱乐部不存在
    public final static int CODE_NO_AUTHORITY = 3068;//无权限
    public final static int CODE_CREATE_HORDE_LIMIT = 3069;//只能创建一个部落
    public final static int CODE_UPDATE_HORDE_NAME_INVALID = 3003;//部落名称不合法
    public final static int CODE_UPDATE_HORDE_NAME_TOO_LONG = 3002;//部落名称长度超出范围
    public final static int CODE_UPDATE_HORDE_NOT_CREATOR = 3074;//用户不是部落创建者
    public final static int CODE_UPDATE_HORDE_NAME_EXISTED = 3071;//部落名称已经存在
    public final static int CODE_UPDATE_HORDE_NOT_EXISTED = 3072;//部落不存在
    public final static int CODE_UPDATE_HORDE_FAILED = 3073;//部落更新失败
    public final static int CODE_CLUB_ALREADY_IN_HORDE = 3076;//该俱乐部已经加入该部落
    public final static int CODE_HORDE_PASSJOIN_FAILED = 3078  ;//horde/passJoin失败
    public final static int CODE_HORDE_PASSJOIN_EXPIRED = 3080;//horde/passJoin 申请超时，一个月失效
    public final static int CODE_DISMISS_CLUB_WITH_HORDE = 3081;//接口增加参数，该俱乐部下有部落不允许删除;
    public final static int CODE_HORDE_PLAYING_NO_HORDE = 3086;//加入部落信息不存在
    public final static int CODE_HORDE_CANCEL_WITH_CLUB = 3083;//解散部落的时候存在俱乐部
    public final static int CODE_HORDE_JOIN_LIMIT = 3087;//俱乐部不能加入大于3个部落
    public final static int CODE_HORDE_SET_SCORE_FAILED = 3094;//修改部落中俱乐部的上分失败
    public final static int CODE_HORDE_SET_SCORE_FAILED_CONTROL_OFF = 3095;//修改部落中俱乐部的上分失败 没有打开上分控制
    public final static int CODE_MATCH_CHECKIN_FAILURE_SCORE_INSUFFICIENT = 3096;//报名 3096 上分控制，积分不足

    public final static int CODE_HORDE_LIST_NO_AUTHORITY = 3075;//用户没有权限

    //基金相关接口的code
    public final static int CODE_FUND_PURCHASE_FAILED = 4010;//新增错误码 4010  基金添加失败
    public final static int CODE_FUND_GRANT_INSUFFICIENT = 4011;//新增错误码 4010  基金添加失败
    //设置比赛说明
    public final static int CODE_MATCH_REMARK_FAILED_CONTENT_LENGTH = 3098;//备注内容长度不正确
    public final static int CODE_MATCH_REMARK_FAILED = 3099;//设置牌局备注失败

    public static String SwitchCode(int code, String result) {
        String temp = CacheConstant.GetString(R.string.control_buy_into_agree_failure) + "  code: " + code;
        if (!StringUtil.isSpace(result)) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                temp = jsonObject.optString("message");
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        String ret = temp;
        switch (code) {
            case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                ret = CacheConstant.GetString(R.string.request_nonet_nocache);
                break;
            case 998://接口信息按照接口返回显示
                ret = temp;
                break;
            case 1000://参数错误
                ret = CacheConstant.GetString(R.string.param_error);
                break;
            case 1001://非法请求
                ret = CacheConstant.GetString(R.string.request_illegal);
                break;
            case 2000://用户昵称太长
                ret = CacheConstant.GetString(R.string.name_is_toolong);
                break;
            case 2001://手机长度必须是11位
                ret = CacheConstant.GetString(R.string.phone_number_11);
                break;
            case 2002://注册失败
                ret = CacheConstant.GetString(R.string.register_failed);
                break;
            case 2004://该手机还未注册
                ret = CacheConstant.GetString(R.string.phone_not_register);
                break;
            case 2005://密码太短（本地判断，因为MD5）
                ret = CacheConstant.GetString(R.string.pwd_too_short);
                break;
            case 2006://密码太长（本地判断，因为MD5）
                ret = CacheConstant.GetString(R.string.pwd_too_long);
                break;
            case 2007://密码格式不正确
                ret = CacheConstant.GetString(R.string.pwd_format_error);
                break;
            case 2008://该手机已经注册
                ret = CacheConstant.GetString(R.string.phone_is_registered);
                break;
            case 2009://密码无效
                ret = CacheConstant.GetString(R.string.pwd_invalied);
                break;
            case 2011://该用户没有好友
                ret = CacheConstant.GetString(R.string.no_friend);
                break;
            case 2013://验证码无效
                ret = CacheConstant.GetString(R.string.verification_invalied);
                break;
            case 2014://昵称设置格式不对
                ret = CacheConstant.GetString(R.string.name_format_error);
                break;
            case 2018:
                ret = "备注名太长";
                break;
            case 2019:
                ret = "备注失败";
                break;
            case 3000://已经创建过牌局
                ret = CacheConstant.GetString(R.string.game_has_created);
                break;
            case 3001://用户余额不足
            case 3061:
                ret = CacheConstant.GetString(R.string.money_not_enough);
                break;
            case 3002://牌局创建名称太长
                ret = CacheConstant.GetString(R.string.gamename_too_long);
                break;
            case 3003://牌局名称格式不对
                ret = CacheConstant.GetString(R.string.gamename_format_error);
                break;
            case 3004://牌局创建失败
                ret = CacheConstant.GetString(R.string.gamename_format_error);
                break;
            case 3005://没有进行中的牌局   或者创建俱乐部游戏无teamid
                ret = CacheConstant.GetString(R.string.game_create_failed);
                break;
            case 3007://牌局不存在
                ret = CacheConstant.GetString(R.string.game_not_exist);
                break;
            case 3008://加入牌局失败
                ret = CacheConstant.GetString(R.string.game_join_failed);
                break;
            case 3010://已经在该牌局中
                ret = CacheConstant.GetString(R.string.game_playing);
                break;
            case 3011://牌局已经开始
                ret = CacheConstant.GetString(R.string.game_started);
                break;
            case 3012://牌局已经结束
                ret = CacheConstant.GetString(R.string.game_overd);
                break;
            case 3013://用户加入牌局成功
                ret = CacheConstant.GetString(R.string.user_join_success);
                break;
            case 3014://用户状态更新失败
                ret = CacheConstant.GetString(R.string.user_status_update_failed);
                break;
            case 3015://用户不是拥有者
                ret = CacheConstant.GetString(R.string.user_not_owner);
                break;
            case 3016://牌局解散失败
                ret = CacheConstant.GetString(R.string.game_dissolution_failed);
                break;
            case 3017://用户不在游戏里
                ret = CacheConstant.GetString(R.string.user_notin_game);
                break;
            case 3021://游戏没有开始
                ret = CacheConstant.GetString(R.string.game_not_started);
                break;
            case 3024://用户的俱乐部牌局创建已经达到上限
                ret = CacheConstant.GetString(R.string.clubgame_in_limit);
                break;
            case 3025://用户的私人牌局创建已经达到上限
                ret = CacheConstant.GetString(R.string.usergame_in_limit);
                break;
            case 3026://用户创建的俱乐部已经达到上限
                ret = CacheConstant.GetString(R.string.user_club_in_limit);
                break;
            case 3028://只能会长开局
                ret = CacheConstant.GetString(R.string.creater_only_startgame);
                break;
            case 3030://游戏不存在（已经结束）
                ret = CacheConstant.GetString(R.string.game_is_over);
                break;
            case 3031://已经报名
                ret = CacheConstant.GetString(R.string.game_match_agree_checkin_aready);
                break;
            case 3032://盲注等级到达等级
                ret = CacheConstant.GetString(R.string.blinds_in_level);
                break;
            case 3033://到达决赛桌
                ret = CacheConstant.GetString(R.string.game_match_checkin_failure_final);
                break;
            case 3034://报名处理中
                ret = CacheConstant.GetString(R.string.checkin_in_deal);
                break;
            case 3036://报名时间截至
                ret = CacheConstant.GetString(R.string.checkin_end);
                break;
            case 3037://游戏已经开始不能报名,MT-SNG
                ret = CacheConstant.GetString(R.string.game_notallow_checkin);
                break;
            case 3038://用户已经报满
                ret = CacheConstant.GetString(R.string.game_match_checkin_failure_player_full);
                break;
            case 3039://状态修改失败，因为已经打开
                ret = CacheConstant.GetString(R.string.status_modify_falied_foropen);
                break;
            case 3040://状态修改失败
                ret = CacheConstant.GetString(R.string.status_modify_failed);
                break;
            case 3041:
                ret = CacheConstant.GetString(R.string.user_not_club_manager);
                break;
            case 3042:
                ret = CacheConstant.GetString(R.string.clubadmin_delete_failed);
                break;
            case 3043:
                ret = CacheConstant.GetString(R.string.clubadmin_in_limit);
                break;
            case 3044:
                ret = CacheConstant.GetString(R.string.clubadmin_add_failed);
                break;
            case 3045:
                ret = CacheConstant.GetString(R.string.user_is_clubowner);
                break;
            case 3054:
                ret = CacheConstant.GetString(R.string.game_match_checkin_failure_admin_forbid);
                break;
            case 3055:
                ret = "该用户已报名参赛，无法添加为管理员";
                break;
            case 3066:
                ret = "添加失败，该用户已经是赛事管理员";
                break;
            case 3092:
                ret = "该请求已被处理";
                break;
            case 3093:
                ret = "游戏正在开始中，请稍后重试";
                break;
            case 3098:
                ret = "修改比赛说明失败：文字长度不正确";
                break;
            case 3099:
                ret = "修改比赛说明失败";
                break;
            case 3100:
                ret = "该通道被禁止报名";
                break;
            case 4003://购买商品失败
                ret = CacheConstant.GetString(R.string.buy_goods_failed);
                break;
            case 4006://购买商品已近被消耗
                ret = CacheConstant.GetString(R.string.goods_is_empty);
                break;
            case 4013:
                ret = "更改信用分失败";
                break;
            //房主主动暂停错误码
            case 5005://玩家不存在
                ret = CacheConstant.GetString(R.string.user_not_exist);
                break;
            case 5006://不是房主
                ret = CacheConstant.GetString(R.string.user_not_roomowner);
                break;
            case 5007://牌局已暂停
                ret = CacheConstant.GetString(R.string.game_is_paused);
                break;
            case 5008://牌局不存在
                ret = CacheConstant.GetString(R.string.game_not_exist);
                break;
            case 5009://牌局没有暂停 不能开始
                ret = CacheConstant.GetString(R.string.game_not_paused);
                break;
            //
            case 6000://评论失败
                ret = CacheConstant.GetString(R.string.comment_failed);
                break;
            case 6001://点赞失败
                ret = CacheConstant.GetString(R.string.clickgood_failed);
                break;
            case 6002://评论不存在
                ret = CacheConstant.GetString(R.string.comment_not_exist);
                break;
            case 6003://只允许发布者删除
                ret = CacheConstant.GetString(R.string.delete_only_owner);
                break;
            case 9102://已经收藏过
                ret = CacheConstant.GetString(R.string.in_collection);
                break;
            case 9103://收藏达到上限
                ret = CacheConstant.GetString(R.string.collection_limit);
                break;
            case 9999://（第三方服务）服务器错误
                ret = CacheConstant.GetString(R.string.third_error);
                break;
            case 20000://系统解析异常
                ret = CacheConstant.GetString(R.string.system_parse_error);
                break;
            case 20001://网络异常
                ret = CacheConstant.GetString(R.string.network_error);
                break;
            case 2016:
            case 3071:
            case 3090:
                ret = CacheConstant.GetString(R.string.nickname_existed);
                break;
        }
        return ret + "  code: " + code;
    }

    public static int getHttpResultPromptResId(int code) {
        switch (code) {
            case ApiCode.CODE_GAME_NONE_EXISTENT:
            case ApiCode.CODE_MATCH_CHECKIN_FAILURE_CHANNEL_NOT_FOUND:
                return R.string.game_join_notexist;
            case ApiCode.CODE_GAME_NAME_FORMAT_WRONG:
                return R.string.name_format_wrong;
            default:
                return R.string.get_failuer;
        }
    }

    public static int getAuthcodeResultResId(String resultData){
        try {
            JSONObject jsonObject = new JSONObject(resultData);
            int code = jsonObject.optInt("code");
            if(code == 3){
                //账户余额不足
//                return "";
            } else if(code == 8){
                //同一手机号30秒内重复提交相同的内容
            } else if(code == 9){
                //同一手机号5分钟内重复提交相同的内容超过3次
            } else if(code == 10){
                //手机号黑名单过滤
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

}