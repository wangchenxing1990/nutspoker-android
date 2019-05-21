package com.htgames.nutspoker.ui.action;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiCode;
import com.htgames.nutspoker.game.model.GameStatus;
import com.netease.nim.uikit.api.ApiConstants;
import com.htgames.nutspoker.api.ApiResultHelper;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMtSngConfig;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.bean.GameNormalConfig;
import com.netease.nim.uikit.bean.GameSngConfigEntity;
import com.netease.nim.uikit.bean.PineappleConfig;
import com.netease.nim.uikit.bean.PineappleConfigMtt;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.preference.PaijuListPref;
import com.netease.nim.uikit.login.LogoutHelper;
import com.htgames.nutspoker.chat.session.SessionHelper;
import com.htgames.nutspoker.chat.session.activity.TeamMessageActivity;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.db.DBUtil;
import com.htgames.nutspoker.db.HandsCollectDBHelper;
import com.htgames.nutspoker.game.match.activity.MatchRoomActivity;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.interfaces.VolleyCallback;
import com.netease.nim.uikit.api.NetWork;
import com.htgames.nutspoker.net.RequestTimeLimit;
import com.netease.nim.uikit.api.SignStringRequest;
import com.htgames.nutspoker.push.GeTuiHelper;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalytics;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalyticsEvent;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalyticsEventConstants;
import com.htgames.nutspoker.tool.JsonResolveUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.htgames.nutspoker.ui.activity.Login.LoginActivity;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.helper.RecentContactHelp;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 * 游戏相关操作类
 * 功能：1.创建牌局 2.加入牌局 3.开始牌局 4.取消牌局 5.获取牌局状态 6.获取所有进行中的牌局]
 * 7.邀请好友加入牌局  8.进入等待中的牌局  9.退出等待中的牌局  10.忽略邀请
 * 11.获取进行中的群牌局数量
 */
public class GameAction extends NewGameAction {
    public final static String TAG = "GameAction";
    String requestCreateUrl;
    String requestJoinUrl;
    String requestStartUrl;
    String requestCancelUrl;
    String requestPlayingListUrl;
    String requestGameStatusUrl;
    String requestHistoryUrl;
    String requestInviteFriendUrl;
    String requestJoinWaitGameUrl;
    String requestLeaveWaitGameUrl;
    String requestIgnoreGameInviteUrl;
    String requestGamePlayingListCountUrl;
    String requestGameMatchCreateUrl;

    public GameAction(Activity activity, View baseView) {
        super(activity, baseView);
    }

    /**
     * 创建牌局
     * @param gameAreType 牌局范围类型 ： 1）.GameConstants.GAME_TYPE_CLUB 俱乐部  2）.GameConstants.GAME_TYPE_GAME 私人
     * @param teamId 群ID
     * @param gameName 牌局名称
     * @param isControlBuy 控制买入
     * @param mGameRequestCallback 结果接口回调
     */
    public void doGameCreate(final int gameAreType , final String teamId , final String gameName, final Object gameConfig, final boolean isControlBuy , final GameRequestCallback mGameRequestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            if (mGameRequestCallback != null) {
                mGameRequestCallback.onFailed(-1, null);
            }
            return;
        }
        DialogMaker.showProgressDialog(mActivity, "创建中...", false);
        requestCreateUrl = getHost() + ApiConstants.URL_GAME_CREATE;
        LogUtil.i(TAG, requestCreateUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestCreateUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        if(mGameRequestCallback != null) {
                            mGameRequestCallback.onSuccess(json.getJSONObject("data"));
                        }
                    } else {
                        DialogMaker.dismissProgressDialog();
                        if (code == ApiCode.CODE_GAME_IS_CREATED) {
                            Toast.makeText(ChessApp.sAppContext, R.string.game_create_failed_already, Toast.LENGTH_SHORT).show();
                        } else if (code == ApiCode.CODE_BALANCE_INSUFFICIENT) {
                            //余额不足，创建失败，提示购买
                            if (mGameRequestCallback != null) {
                                mGameRequestCallback.onFailed(code, json);
                            }
                        } else if (code == ApiCode.CODE_GAME_CREATE_FAILURE) {
                            Toast.makeText(ChessApp.sAppContext, R.string.game_create_failed, Toast.LENGTH_SHORT).show();
                        } else if (code == ApiCode.CODE_GAME_NAME_LENGTH_LONG) {
                            Toast.makeText(ChessApp.sAppContext, R.string.game_create_name_long, Toast.LENGTH_SHORT).show();
                        } else if (code == ApiCode.CODE_GAME_NAME_FORMAT_WRONG) {
                            Toast.makeText(ChessApp.sAppContext, R.string.game_create_name_invalid, Toast.LENGTH_SHORT).show();
                        } else if (code == ApiCode.CODE_GAME_TEAM_COUNT_IS_LIMIT) {
//                        //用户的俱乐部牌局创建已经达到上限
                            if (mGameRequestCallback != null) {
                                mGameRequestCallback.onFailed(code, json);
                            }
//                        Toast.makeText(ChessApp.sAppContext, R.string.game_create_team_count_is_limit, Toast.LENGTH_SHORT).show();
                        } else if (code == ApiCode.CODE_GAME_PRIVATE_COUNT_IS_LIMIT) {
//                        //用户的私人牌局创建已经达到上限
                            if (mGameRequestCallback != null) {
                                mGameRequestCallback.onFailed(code, json);
                            }
//                        Toast.makeText(ChessApp.sAppContext, R.string.game_create_private_count_is_limit, Toast.LENGTH_SHORT).show();
                        } else if(code == ApiCode.CODE_CLUB_CREATE_BY_OWNER) {
                            Toast.makeText(ChessApp.sAppContext, R.string.game_create_by_club_creator, Toast.LENGTH_SHORT).show();
                        } else {
                            String message = ApiResultHelper.getShowMessage(json);
                            if (TextUtils.isEmpty(message)) {
                                message = ChessApp.sAppContext.getString(R.string.game_create_failed);
                            }
                            Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    DialogMaker.dismissProgressDialog();
                    Toast.makeText(ChessApp.sAppContext, R.string.game_create_failed, Toast.LENGTH_SHORT).show();
                    if (mGameRequestCallback != null) {
                        mGameRequestCallback.onFailed(ApiCode.CODE_JSON_ERROR, null);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(ChessApp.sAppContext, R.string.game_create_failed, Toast.LENGTH_SHORT).show();
                if (mGameRequestCallback != null) {
                    mGameRequestCallback.onFailed(-1, null);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                if (gameAreType == GameConstants.GAME_TYPE_CLUB && !TextUtils.isEmpty(teamId)) {
                    //tid私人牌局可以为空（默认）,圈子或俱乐部中组局时，为圈子或俱乐部的tid
                    paramsMap.put("tid", teamId);
                }
                paramsMap.put("name", gameName);
                int gameMode = GameConstants.GAME_MODE_NORMAL;
                paramsMap.put("type", "" + gameAreType);
                if(isControlBuy){
                    paramsMap.put("is_control", "" + GameConstants.CONTROL_BUY_MODE);
                }
                if(gameConfig instanceof GameNormalConfig) {
                    GameNormalConfig gameNormalConfig = (GameNormalConfig) gameConfig;
                    int max_buy_chips = gameNormalConfig.max_chips == Integer.MAX_VALUE ? -1 : gameNormalConfig.max_chips;
                    int total_buy_chips = gameNormalConfig.total_chips == Integer.MAX_VALUE ? -1 : gameNormalConfig.total_chips;
                    gameMode = GameConstants.GAME_MODE_NORMAL;
                    paramsMap.put("blinds", "" + gameNormalConfig.blindType);
                    paramsMap.put("duration", "" + gameNormalConfig.timeType);
                    paramsMap.put("tilt_mode", "" + gameNormalConfig.tiltMode);
                    paramsMap.put("ante", "" + gameNormalConfig.ante);
                    paramsMap.put("game_mode", "" + gameMode);
                    paramsMap.put("match_player", "" + gameNormalConfig.matchPlayer);//单桌人数
                    paramsMap.put("min_buy_chips", "" + gameNormalConfig.min_chips);
                    paramsMap.put("max_buy_chips", "" + max_buy_chips);
                    paramsMap.put("total_buy_chips", "" + total_buy_chips);
                    paramsMap.put("check_ip", "" + gameNormalConfig.check_ip);
                    paramsMap.put("check_gps", "" + gameNormalConfig.check_gps);
                    paramsMap.put("check_straddle", "" + gameNormalConfig.check_straddle);
                } else if(gameConfig instanceof GameSngConfigEntity) {
                    GameSngConfigEntity gameSngConfigEntity = (GameSngConfigEntity) gameConfig;
                    gameMode = GameConstants.GAME_MODE_SNG;
                    paramsMap.put("match_chips", "" + gameSngConfigEntity.getChips());
                    paramsMap.put("match_player", "" + gameSngConfigEntity.getPlayer());
                    paramsMap.put("match_duration", "" + gameSngConfigEntity.getDuration());
                    paramsMap.put("game_mode" , "" + gameMode);
                    paramsMap.put("match_checkin_fee" , "" + gameSngConfigEntity.checkInFee);
                    paramsMap.put("start_sblinds" , "" + gameSngConfigEntity.start_sblinds);
                    paramsMap.put("sblinds_mode" , "" + gameSngConfigEntity.sblinds_mode);
                }
                LogUtil.i(TAG, paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestCreateUrl);
        signRequest.setRetryPolicy(new DefaultRetryPolicy(SignStringRequest.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));//有时候点击"创建"生成多个现金局，暂时把这个接口的重试次数改为0，看下这个bug还能不能浮现
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 创建牌局
     * @param gameAreType 牌局范围类型 ： 1）.GameConstants.GAME_TYPE_CLUB 俱乐部  2）.GameConstants.GAME_TYPE_GAME 私人
     * @param teamId 群ID
     * @param gameName 牌局名称
     * @param isControlBuy 控制买入
     * @param mGameRequestCallback 结果接口回调
     */
    private String requestCreateFreeUrl;
    public void doGameFreeCreate(final int gameAreType , final String teamId , final String gameName,
                                 final Object gameConfig, final boolean isControlBuy, final String horde_id,
                                 int play_mode, final VolleyCallback mGameRequestCallback) {
        DialogMaker.showProgressDialog(mActivity, "创建中...", false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("play_mode", String.valueOf(play_mode));
        if (gameAreType == GameConstants.GAME_TYPE_CLUB && !TextUtils.isEmpty(teamId)) {
            //tid私人牌局可以为空（默认）,圈子或俱乐部中组局时，为圈子或俱乐部的tid
            paramsMap.put("tid", teamId);
        }
        if (!StringUtil.isSpace(horde_id)) {
            paramsMap.put("horde_id", horde_id);
        }
        paramsMap.put("name", gameName);
        int gameMode = GameConstants.GAME_MODE_NORMAL;
        paramsMap.put("type", "" + gameAreType);
        if(isControlBuy){
            paramsMap.put("is_control", "" + GameConstants.CONTROL_BUY_MODE);
        }
        if(gameConfig instanceof GameNormalConfig) {
            GameNormalConfig gameNormalConfig = (GameNormalConfig) gameConfig;
            int max_buy_chips = gameNormalConfig.max_chips == Integer.MAX_VALUE ? -1 : gameNormalConfig.max_chips;
            int total_buy_chips = gameNormalConfig.total_chips == Integer.MAX_VALUE ? -1 : gameNormalConfig.total_chips;
            gameMode = GameConstants.GAME_MODE_NORMAL;
            paramsMap.put("blinds", "" + gameNormalConfig.blindType);
            paramsMap.put("duration", "" + gameNormalConfig.timeType);
            paramsMap.put("tilt_mode", "" + gameNormalConfig.tiltMode);
            paramsMap.put("ante", "" + gameNormalConfig.ante);
            paramsMap.put("game_mode", "" + gameMode);
            paramsMap.put("match_player", "" + gameNormalConfig.matchPlayer);//单桌人数
            paramsMap.put("min_buy_chips", "" + gameNormalConfig.min_chips);
            paramsMap.put("max_buy_chips", "" + max_buy_chips);
            paramsMap.put("total_buy_chips", "" + total_buy_chips);
            paramsMap.put("check_ip", "" + gameNormalConfig.check_ip);
            paramsMap.put("check_gps", "" + gameNormalConfig.check_gps);
            paramsMap.put("check_straddle", "" + gameNormalConfig.check_straddle);
        } else if(gameConfig instanceof GameSngConfigEntity) {
            GameSngConfigEntity gameSngConfigEntity = (GameSngConfigEntity) gameConfig;
            gameMode = GameConstants.GAME_MODE_SNG;
            paramsMap.put("match_chips", "" + gameSngConfigEntity.getChips());
            paramsMap.put("match_player", "" + gameSngConfigEntity.getPlayer());
            paramsMap.put("match_duration", "" + gameSngConfigEntity.getDuration());
            paramsMap.put("game_mode" , "" + gameMode);
            paramsMap.put("match_checkin_fee" , "" + gameSngConfigEntity.checkInFee);
            paramsMap.put("start_sblinds" , "" + gameSngConfigEntity.start_sblinds);
            paramsMap.put("sblinds_mode" , "" + gameSngConfigEntity.sblinds_mode);
            paramsMap.put("check_ip", "" + gameSngConfigEntity.check_ip);
            paramsMap.put("check_gps", "" + gameSngConfigEntity.check_gps);
        }
        LogUtil.i(TAG, paramsMap.toString());
        requestCreateFreeUrl = getHost() + ApiConstants.URL_GAME_FREE_CREATE + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestCreateFreeUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestCreateFreeUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                if (mGameRequestCallback != null) {
                    mGameRequestCallback.onResponse(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mGameRequestCallback != null) {
                    mGameRequestCallback.onErrorResponse(null);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestCreateFreeUrl);
        signRequest.setRetryPolicy(new DefaultRetryPolicy(SignStringRequest.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));//有时候点击"创建"生成多个现金局，暂时把这个接口的重试次数改为0，看下这个bug还能不能浮现
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 创建比赛牌局
     * @param gameAreType
     * @param teamId
     * @param gameName
     * @param gameConfig
     * @param isControlBuy
     * @param mGameRequestCallback
     */
    public void doGameMatchCreate(final int gameAreType , final String teamId , final String gameName, final Object gameConfig, final boolean isControlBuy, final String horde_id,
                                  final int play_mode,
                                  final GameRequestCallback mGameRequestCallback) {
        DialogMaker.showProgressDialog(mActivity, "创建中...", false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("play_mode", String.valueOf(play_mode));
        if (gameAreType == GameConstants.GAME_TYPE_CLUB && !StringUtil.isSpaceOrZero(teamId)) {
            //tid私人牌局可以为空（默认）,圈子或俱乐部中组局时，为圈子或俱乐部的tid
            paramsMap.put("tid", teamId);
        }
        if (!StringUtil.isSpaceOrZero(horde_id)) {
            paramsMap.put("horde_id", horde_id);
        }
        paramsMap.put("name", gameName);
        paramsMap.put("type", "" + gameAreType);
        if (isControlBuy) {
            paramsMap.put("is_control", String.valueOf(GameConstants.CONTROL_BUY_MODE));
        }
        if (gameConfig instanceof GameMttConfig) {
            GameMttConfig gameMttConfig = (GameMttConfig) gameConfig;
            int gameMode = GameConstants.GAME_MODE_MTT;
            paramsMap.put("match_chips", "" + gameMttConfig.matchChips);
            paramsMap.put("match_type", "" + gameMttConfig.match_type);
            paramsMap.put("match_player", "" + gameMttConfig.matchPlayer);
            paramsMap.put("match_duration", "" + gameMttConfig.matchDuration);
            paramsMap.put("match_level", "" + gameMttConfig.matchLevel);
            paramsMap.put("match_rebuy", "" + gameMttConfig.rebuyMode);
            paramsMap.put("match_addon", "" + gameMttConfig.addonMode);
            paramsMap.put("rest_mode", "" + gameMttConfig.restMode);
            paramsMap.put("begin_time", "" + gameMttConfig.beginTime);
            paramsMap.put("game_mode", "" + gameMode);
            paramsMap.put("match_checkin_fee", "" + gameMttConfig.matchCheckinFee);
            paramsMap.put("sblinds_mode", "" + gameMttConfig.sblinds_mode);
            paramsMap.put("start_sblinds", "" + gameMttConfig.start_sblinds);
            paramsMap.put("match_max_buy_cnt", "" + gameMttConfig.match_max_buy_cnt);
            paramsMap.put("match_rebuy_cnt", "" + gameMttConfig.match_rebuy_cnt);
            paramsMap.put("is_auto", "" + gameMttConfig.is_auto);
            paramsMap.put("ko_mode", "" + gameMttConfig.ko_mode);
            paramsMap.put("ko_reward_rate", "" + gameMttConfig.ko_reward_rate);
            paramsMap.put("ko_head_rate", "" + gameMttConfig.ko_head_rate);
        } else if (gameConfig instanceof GameMtSngConfig) {
            GameMtSngConfig gameMtSngConfig = (GameMtSngConfig) gameConfig;
            int gameMode = GameConstants.GAME_MODE_MT_SNG;
            paramsMap.put("match_chips", String.valueOf(gameMtSngConfig.matchChips));
            paramsMap.put("match_player", String.valueOf(gameMtSngConfig.matchPlayer));
            paramsMap.put("match_duration", String.valueOf(gameMtSngConfig.matchDuration));
            paramsMap.put("total_player", String.valueOf(gameMtSngConfig.totalPlayer));
            paramsMap.put("game_mode", String.valueOf(gameMode));
        }
        LogUtil.i(TAG, paramsMap.toString());
        requestGameMatchCreateUrl = getHost() + ApiConstants.URL_GAME_MATCH_CREATE + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestGameMatchCreateUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestGameMatchCreateUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onSuccess(json.getJSONObject("data"));
                        }
                    } else {
                        DialogMaker.dismissProgressDialog();
                        if (code == ApiCode.CODE_GAME_IS_CREATED) {
                            Toast.makeText(ChessApp.sAppContext, R.string.game_create_failed_already, Toast.LENGTH_SHORT).show();
                        } else if (code == ApiCode.CODE_BALANCE_INSUFFICIENT) {
                            //余额不足，创建失败，提示购买
                            if (mGameRequestCallback != null) {
                                mGameRequestCallback.onFailed(code, json);
                            }
                        } else if (code == ApiCode.CODE_GAME_CREATE_FAILURE) {
                            Toast.makeText(ChessApp.sAppContext, R.string.game_create_failed, Toast.LENGTH_SHORT).show();
                        } else if (code == ApiCode.CODE_GAME_NAME_LENGTH_LONG) {
                            Toast.makeText(ChessApp.sAppContext, R.string.game_create_name_long, Toast.LENGTH_SHORT).show();
                        } else if (code == ApiCode.CODE_GAME_NAME_FORMAT_WRONG) {
                            Toast.makeText(ChessApp.sAppContext, R.string.game_create_name_invalid, Toast.LENGTH_SHORT).show();
                        } else if (code == ApiCode.CODE_GAME_TEAM_COUNT_IS_LIMIT) {
//                        //用户的俱乐部牌局创建已经达到上限
                            if (mGameRequestCallback != null) {
                                mGameRequestCallback.onFailed(code, json);
                            }
//                        Toast.makeText(ChessApp.sAppContext, R.string.game_create_team_count_is_limit, Toast.LENGTH_SHORT).show();
                        } else if (code == ApiCode.CODE_GAME_PRIVATE_COUNT_IS_LIMIT) {
//                        //用户的私人牌局创建已经达到上限
                            if (mGameRequestCallback != null) {
                                mGameRequestCallback.onFailed(code, json);
                            }
//                        Toast.makeText(ChessApp.sAppContext, R.string.game_create_private_count_is_limit, Toast.LENGTH_SHORT).show();
                        } else {
                            String message = ApiResultHelper.getShowMessage(json);
                            if (TextUtils.isEmpty(message)) {
                                message = ChessApp.sAppContext.getString(R.string.game_create_failed);
                            }
                            Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    DialogMaker.dismissProgressDialog();
                    Toast.makeText(ChessApp.sAppContext, R.string.game_create_failed, Toast.LENGTH_SHORT).show();
                    if (mGameRequestCallback != null) {
                        mGameRequestCallback.onFailed(ApiCode.CODE_JSON_ERROR, null);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(ChessApp.sAppContext, R.string.buychips_result_network_error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestGameMatchCreateUrl);
        signRequest.setRetryPolicy(new DefaultRetryPolicy(SignStringRequest.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));//有时候点击"创建"生成多个现金局，暂时把这个接口的重试次数改为0，看下这个bug还能不能浮现
        ChessApp.sRequestQueue.add(signRequest);
    }

    public void doPineappleCreate(final int gameAreType , final String teamId , final String gameName,
                                  final PineappleConfig pineappleObject, final String horde_id,
                                  int play_mode, final GameRequestCallback mGameRequestCallback) {
        DialogMaker.showProgressDialog(mActivity, "创建中...", false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("play_mode", String.valueOf(play_mode));
        if (gameAreType == GameConstants.GAME_TYPE_CLUB && !StringUtil.isSpaceOrZero(teamId)) {
            //tid私人牌局可以为空（默认）,圈子或俱乐部中组局时，为圈子或俱乐部的tid
            paramsMap.put("tid", teamId);
        }
        if (!StringUtil.isSpaceOrZero(horde_id)) {
            paramsMap.put("horde_id", horde_id);
        }
        paramsMap.put("name", gameName);
        int gameMode = GameConstants.GAME_MODE_NORMAL;
        paramsMap.put("game_mode", "" + gameMode);
        paramsMap.put("type", "" + gameAreType);
        paramsMap.put(GameConstants.KEY_DEAL_ORDER, "" + pineappleObject.getDeal_order());
        paramsMap.put("is_control", "" + pineappleObject.getBuy_in_control());
        paramsMap.put("duration", "" + pineappleObject.getDuration_index());
        paramsMap.put("ante", "" + pineappleObject.getAnte());
        paramsMap.put("match_player", "" + pineappleObject.getMatch_player());//单桌人数
        paramsMap.put("check_ip", "" + pineappleObject.getIp_limit());
        paramsMap.put("check_gps", "" + pineappleObject.getGps_limit());
        paramsMap.put("match_chips", "" + pineappleObject.getChips());
        paramsMap.put("limit_chips", "" + pineappleObject.getLimit_chips());
        paramsMap.put("play_type", "" + pineappleObject.getPlay_type());
        paramsMap.put("ready_time", "" + pineappleObject.getReady_time());
        LogUtil.i(TAG, paramsMap.toString());
        requestCreateFreeUrl = getHost() + ApiConstants.URL_GAME_FREE_CREATE + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestCreateFreeUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestCreateFreeUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                DialogMaker.dismissProgressDialog();
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onSuccess(json);
                        }
                    } else {
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onFailed(code, json);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (mGameRequestCallback != null) {
                        mGameRequestCallback.onFailed(ApiCode.CODE_JSON_ERROR, null);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                if (mGameRequestCallback != null) {
                    mGameRequestCallback.onFailed(-1, null);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestCreateFreeUrl);
        signRequest.setRetryPolicy(new DefaultRetryPolicy(SignStringRequest.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));//有时候点击"创建"生成多个现金局，暂时把这个接口的重试次数改为0，看下这个bug还能不能浮现
        ChessApp.sRequestQueue.add(signRequest);
    }

    public void doPineappleMttCreate(final int gameAreType , final String teamId , final String gameName,
                                     final PineappleConfigMtt pineappleObject, final String horde_id,
                                     int play_mode, final GameRequestCallback mGameRequestCallback) {
        DialogMaker.showProgressDialog(mActivity, "创建中...", false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("play_mode", String.valueOf(play_mode));
        if (gameAreType == GameConstants.GAME_TYPE_CLUB && !StringUtil.isSpaceOrZero(teamId)) {
            //tid私人牌局可以为空（默认）,圈子或俱乐部中组局时，为圈子或俱乐部的tid
            paramsMap.put("tid", teamId);
        }
        if (!StringUtil.isSpaceOrZero(horde_id)) {
            paramsMap.put("horde_id", horde_id);
        }
        int gameMode = GameConstants.GAME_MODE_MTT;
        paramsMap.put("game_mode", "" + gameMode);
        paramsMap.put("name", gameName);
        paramsMap.put("type", "" + gameAreType);
        paramsMap.put("play_type", "" + pineappleObject.getPlay_type());
        paramsMap.put("match_type", "" + pineappleObject.match_type);
        paramsMap.put("match_checkin_fee", "" + pineappleObject.matchCheckinFee);
        paramsMap.put("begin_time", "" + pineappleObject.beginTime);
        paramsMap.put("match_chips", "" + pineappleObject.matchChips);
        paramsMap.put("match_duration", "" + pineappleObject.matchDuration);
        paramsMap.put("is_auto", "" + pineappleObject.is_auto);
        paramsMap.put("sblinds_mode", "" + pineappleObject.getAnte_table_type());//ante_table_type和普通mtt的盲注结构表的类型同一个字段
//        paramsMap.put("start_sblinds", "" + pineappleObject.getStart_sblinds());
        paramsMap.put("match_max_buy_cnt", "" + pineappleObject.match_max_buy_cnt);
        paramsMap.put("match_level", "" + pineappleObject.matchLevel);
        paramsMap.put("match_rebuy_cnt", "" + pineappleObject.match_rebuy_cnt);
        paramsMap.put("is_control", "" + pineappleObject.is_control);
        paramsMap.put("rest_mode", "" + pineappleObject.restMode);
        paramsMap.put("ko_mode", "" + pineappleObject.ko_mode);
        paramsMap.put("ko_reward_rate", "" + pineappleObject.ko_reward_rate);
        paramsMap.put("ko_head_rate", "" + pineappleObject.ko_head_rate);
        LogUtil.i(TAG, paramsMap.toString());
        requestGameMatchCreateUrl = getHost() + ApiConstants.URL_GAME_MATCH_CREATE + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestGameMatchCreateUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestGameMatchCreateUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                DialogMaker.dismissProgressDialog();
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onSuccess(json);
                        }
                    } else {
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onFailed(code, json);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (mGameRequestCallback != null) {
                        mGameRequestCallback.onFailed(ApiCode.CODE_JSON_ERROR, null);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                if (mGameRequestCallback != null) {
                    mGameRequestCallback.onFailed(-1, null);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestGameMatchCreateUrl);
        signRequest.setRetryPolicy(new DefaultRetryPolicy(SignStringRequest.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));//有时候点击"创建"生成多个现金局，暂时把这个接口的重试次数改为0，看下这个bug还能不能浮现
        ChessApp.sRequestQueue.add(signRequest);
    }

    public void doGameJoin(final String joinWay, final GameEntity gameEntity, final boolean isFinish ,final boolean isDelete) {
        doGameJoin(joinWay, gameEntity, isFinish, isDelete, null);
    }

    /**
     * 加入牌局
     * @param gameEntity 牌局信息
     * @param isFinish 加入成功是否关闭当前Activity
     * @param isDelete 牌局已经解散是否删除最近联系记录
     */
    public void doGameJoin(final String joinWay, final GameEntity gameEntity, final boolean isFinish, final boolean isDelete, final GameRequestCallback mGameRequestCallback){
        String sessionId = gameEntity.tid;
        final String gName = gameEntity.name;
        final String gameCode = gameEntity.code;
        final int gameType = gameEntity.type;
        final int gameMode = gameEntity.gameMode;
        if (gameType == GameConstants.GAME_TYPE_CLUB && (gameMode == GameConstants.GAME_MODE_NORMAL || gameMode == GameConstants.GAME_MODE_SNG)) {
            String roomId = gameEntity.room_id;
            //俱乐部牌局，并且是普通牌局或者SNG牌局
            if (TextUtils.isEmpty(roomId) || "0".equals(roomId)) {
                //没有聊天室ROOMID，使用之前逻辑
                sessionId = gameEntity.tid;
            } else {
                //有聊天室ROOMID，使用聊天室
                sessionId = roomId;
            }
        }
        final String currentSessionId = sessionId;
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            DialogMaker.dismissProgressDialog();
            return;
        }
        if(!DialogMaker.isShowing()){
            DialogMaker.showProgressDialog(mActivity , getString(com.netease.nim.uikit.R.string.empty), false);
        }
        requestJoinUrl = getHost() + ApiConstants.URL_GAME_JOIN;
        LogUtil.i(TAG, requestJoinUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestJoinUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    final JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == ApiCode.CODE_SUCCESS || code == ApiCode.CODE_GAME_IS_IN) {
                        //3010：已经加入的代表加入成功
                        if (!TextUtils.isEmpty(joinWay)) {
                            UmengAnalyticsEvent.onEventGameJoin(ChessApp.sAppContext, joinWay, gameEntity.gameMode);//统计
                        }
//                        Toast.makeText(ChessApp.sAppContext , "加入牌局 " + gid + " 成功！", Toast.LENGTH_SHORT).show();
                        JSONObject dataJsonObject = json.optJSONObject("data");
                        final String hostIp = dataJsonObject.optString(GameConstants.KEY_GAME_SERVER);//游戏服务器
                        {
                            int ko_mode = 0;
                            if (gameEntity != null && gameEntity.gameConfig instanceof  GameMttConfig) {
                                ko_mode = ((GameMttConfig) gameEntity.gameConfig).ko_mode;
                            }
//                            PokerActivity.startGameByPlay(mActivity, gameEntity.gid, "" + gameMode, gameEntity.originalCode, currentSessionId, gameType, hostIp, ko_mode, gameEntity.code);
                            if (mGameRequestCallback != null) {
                                mGameRequestCallback.onSuccess(json);
                            }
                            if (isFinish) {
                                mActivity.finish();
                            } else {
                                if (code == ApiCode.CODE_SUCCESS
                                        && mActivity != null && mActivity instanceof TeamMessageActivity && !TextUtils.isEmpty(gName)) {
                                    //第一次加入才发消息
                                    String nickname = NimUserInfoCache.getInstance().getUserDisplayName(DemoCache.getAccount());
                                    if (!TextUtils.isEmpty(nickname)) {
                                        ((TeamMessageActivity) mActivity).sendGameTipNotification(ChessApp.sAppContext.getString(R.string.enter_game_tip, nickname, gName));//发送消息
                                    }
                                }
                            }
                        }
                    } else if (code == ApiCode.CODE_GAME_NONE_EXISTENT) {
                        Toast.makeText(ChessApp.sAppContext, getString(R.string.game_join_notexist), Toast.LENGTH_SHORT).show();
                        if (isDelete
                                && gameType == GameConstants.GAME_TYPE_GAME
                                && (gameMode == GameConstants.GAME_MODE_NORMAL || gameMode == GameConstants.GAME_MODE_SNG)) {
                            //牌局已经无效的，清除最近联系人列表中的数据
                            RecentContactHelp.deleteRecentContact(currentSessionId, SessionTypeEnum.Team, true);
                        }
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onFailed(code, json);
                        }
                    } else if (code == ApiCode.CODE_GAME_IS_CREATED) {
                        Toast.makeText(ChessApp.sAppContext, getString(R.string.game_join_failure), Toast.LENGTH_SHORT).show();
                    } else {
                        String message = ApiResultHelper.getShowMessage(json);
                        if (TextUtils.isEmpty(message)) {
                            message = ChessApp.sAppContext.getString(R.string.game_join_failure);
                        }
                        Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ChessApp.sAppContext ,getString(R.string.game_join_failure), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(ChessApp.sAppContext ,getString(R.string.game_join_failure), Toast.LENGTH_SHORT).show();
                if(error.getMessage() != null){
                    LogUtil.i(TAG ,  error.getMessage());
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> paramsMap = new HashMap<String, String>();
//                paramsMap.put("uid", UserPreferences.getInstance(ChessApp.sAppContext).getUserId());
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("code", gameCode);
                LogUtil.i(TAG, paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestJoinUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 开始牌局
     * @param gameCode 游戏CODE
     */
    public void doGameStart(final String gameCode , final GameRequestCallback mGameRequestCallback, final View startView) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(mActivity, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            startView.setEnabled(true);
            return;
        }
        DialogMaker.showProgressDialog(mActivity, ChessApp.sAppContext.getString(com.netease.nim.uikit.R.string.empty), false);
        requestStartUrl = getHost() + ApiConstants.URL_GAME_START;
        LogUtil.i(TAG, requestStartUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestStartUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        startView.setEnabled(true);
                        DialogMaker.dismissProgressDialog();
                        LogUtil.i(TAG, response);
                        try {
                            JSONObject json = new JSONObject(response);
                            int resultCode = json.getInt("code");
                            if (resultCode == ApiCode.CODE_SUCCESS || resultCode == ApiCode.CODE_GAME_IS_IN) {
//                        Toast.makeText(ChessApp.sAppContext, "开始牌局！", Toast.LENGTH_SHORT).show();
                                if (mGameRequestCallback != null) {
                                    mGameRequestCallback.onSuccess(json);
                                }
                            } else if (resultCode == ApiCode.CODE_GAME_IS_CREATED) {
                                Toast.makeText(ChessApp.sAppContext, R.string.game_start_failure, Toast.LENGTH_SHORT).show();
                            } else if (resultCode == ApiCode.CODE_GAME_NONE_EXISTENT) {
                                Toast.makeText(ChessApp.sAppContext, R.string.game_start_notexist, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ChessApp.sAppContext, R.string.game_start_failure, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ChessApp.sAppContext, R.string.game_start_failure, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        startView.setEnabled(true);
                        DialogMaker.dismissProgressDialog();
                        Toast.makeText(ChessApp.sAppContext, R.string.game_start_failure, Toast.LENGTH_SHORT).show();
                        if (error.getMessage() != null) {
                            LogUtil.i(TAG, error.getMessage());
                        }
                    }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> paramsMap = new HashMap<String, String>();
//                paramsMap.put("uid", UserPreferences.getInstance(ChessApp.sAppContext).getUserId());
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("code", gameCode);
                LogUtil.i(TAG, paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestStartUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 开始牌局
     * @param gameCode 游戏CODE   代码重构自doGameStart函数
     */
    public void doGameStartNew(final String gameCode , Response.Listener<String> listener, Response.ErrorListener errorListener) {
        requestStartUrl = getHost() + ApiConstants.URL_GAME_START;
        LogUtil.i(TAG, requestStartUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestStartUrl,listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> paramsMap = new HashMap<String, String>();
//                paramsMap.put("uid", UserPreferences.getInstance(ChessApp.sAppContext).getUserId());
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("code", gameCode);
                LogUtil.i(TAG, paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestStartUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 解散牌局
     * @param teamId 群ID
     * @param gameCode 游戏CODE
     */
    public void doGameCancel(final  String teamId , final String gameCode, final String gid) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        requestCancelUrl = getHost() + ApiConstants.URL_GAME_CANCEL;
        LogUtil.i(TAG, requestCancelUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestCancelUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == ApiCode.CODE_SUCCESS) {
                        //解散牌局成功！
                        Toast.makeText(ChessApp.sAppContext, getString(R.string.game_dismiss_success), Toast.LENGTH_SHORT).show();
                        RecentContactHelp.deleteRecentContact(teamId, SessionTypeEnum.Team, true);
                        RequestTimeLimit.lastGetRecentGameTime = (0);
//                        Intent intent = new Intent(NewGameReceiver.ACTION_NEWGAME);
//                        intent.putExtra(Extras.EXTRA_GAME_ID, gid);
//                        intent.putExtra(Extras.EXTRA_GAME_CANCEL, true);
//                        mActivity.sendBroadcast(intent);
                        mActivity.finish();
                    } else if (code == ApiCode.CODE_GAME_IS_BEGIN) {
                        Toast.makeText(ChessApp.sAppContext, getString(R.string.game_dismiss_failure_be_gameisstart), Toast.LENGTH_SHORT).show();
                        mActivity.finish();
                    } else {
                        String message = ApiResultHelper.getShowMessage(json);
                        if (TextUtils.isEmpty(message)) {
                            message = ChessApp.sAppContext.getString(R.string.game_dismiss_failure);
                        }
                        Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(ChessApp.sAppContext, getString(R.string.game_dismiss_failure), Toast.LENGTH_SHORT).show();
                if (error.getMessage() != null) {
                    LogUtil.i(TAG, error.getMessage());
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> paramsMap = new HashMap<String, String>();
//                paramsMap.put("uid", UserPreferences.getInstance(ChessApp.sAppContext).getUserId());
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("code", gameCode);
                LogUtil.i(TAG, paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestCancelUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    public String requestGameInfoUrl;
    public void getGameInfo(final String joinWay , final String code , final VolleyCallback volleyCallback) {
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("code", code);
        requestGameInfoUrl = getHost() + ApiConstants.URL_GAME_GET_GAME + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestGameInfoUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestGameInfoUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                if(volleyCallback != null){
                    volleyCallback.onResponse(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(ChessApp.sAppContext, getString(R.string.game_join_failure), Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(error.getMessage())) {
                    LogUtil.i(TAG, error.getMessage());
                }
                if (volleyCallback != null) {
                    volleyCallback.onErrorResponse(null);//不回调   转菊花 消失不了
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestGameInfoUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 获取牌局状态
     * @param code 牌局code
     * @param mGameRequestCallback 牌局状态接口回调
     */
    public void getGameStatus(final String joinWay , final String code , final GameRequestCallback mGameRequestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            if (mGameRequestCallback != null) {
                mGameRequestCallback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);//不回调   转菊花 消失不了
            }
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("code", code);
        requestGameStatusUrl = getHost() + ApiConstants.URL_GAME_GETTID + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestGameStatusUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestGameStatusUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int resultCode = json.getInt("code");
                    if (resultCode == 0) {
                        if(mGameRequestCallback != null){
                            mGameRequestCallback.onSuccess(json);
                        }
                    } else {
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onFailed(resultCode, json);
                        }
                        if (resultCode == ApiCode.CODE_GAME_NONE_EXISTENT || resultCode == ApiCode.CODE_MATCH_CHECKIN_FAILURE_CHANNEL_NOT_FOUND) {
                            if (joinWay == UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_CODE || joinWay == UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_APP_MESSAGE) {
                                Toast.makeText(ChessApp.sAppContext, getString(R.string.game_join_notexist), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String message = ApiResultHelper.getShowMessage(json);
                            if (TextUtils.isEmpty(message)) {
                                message = ChessApp.sAppContext.getString(R.string.game_join_failure);
                            }
                            Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(ChessApp.sAppContext, getString(R.string.game_join_failure), Toast.LENGTH_SHORT).show();
                    if (mGameRequestCallback != null) {
                        mGameRequestCallback.onFailed(ApiCode.CODE_JSON_ERROR, null);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(ChessApp.sAppContext, getString(R.string.game_join_failure), Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(error.getMessage())) {
                    LogUtil.i(TAG, error.getMessage());
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestGameStatusUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 获取进行中的牌局
     * @param teamId 群ID
     * @param requestCallback 数据回调
     */
    public void getGamePlayingList(final String teamId , final GameRequestCallback requestCallback){
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            return;
        }
//        final HashMap<String, String> paramsMap = new HashMap<String, String>();
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("tid", teamId);
        requestPlayingListUrl = getHost() + ApiConstants.URL_GAME_PLAYING + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestPlayingListUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestPlayingListUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                try {
                    org.json.JSONObject json = new org.json.JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        PaijuListPref.getInstance().setTeamPaijuList(teamId, response);
                        if(requestCallback != null){
                            requestCallback.onSuccess(json);
                        }
                    } else if(code == ApiCode.CODE_GAME_NOT_ING){
                        LogUtil.i(TAG, "没有进行中的牌局！");
                        if(requestCallback != null){
                            requestCallback.onFailed(code, json);
                        }
                    }
                } catch (JSONException e) {
                    if(requestCallback != null){
                        requestCallback.onFailed(1111, null);
                    }
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getMessage() != null){
                    LogUtil.i(TAG ,  error.getMessage());
                }
                if(requestCallback != null){
                    requestCallback.onFailed(11, null);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestPlayingListUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 获取最近的牌局记录
     * @param requestCallback
     */
    public void getRecentGameList(final GameRequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            return;
        }
        cancelAll(requestHistoryUrl);
        //
//        final HashMap<String, String> paramsMap = new HashMap<String, String>();
//        paramsMap.put("uid", UserPreferences.getInstance(ChessApp.sAppContext).getUserId());
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        requestHistoryUrl = getHost() + ApiConstants.URL_GAME_HISTORY + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestHistoryUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestHistoryUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response.substring(0, response.length() / 2));
                LogUtil.i(TAG, response.substring(response.length() / 2, response.length()));//data太长，log显示不全，分开显示
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        PaijuListPref.getInstance().setMainList(response);
                        if(requestCallback != null){
                            requestCallback.onSuccess(json);
                        }
                    } else {
                        if(requestCallback != null) {
                            requestCallback.onFailed(code, json);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if(requestCallback != null) {
                        requestCallback.onFailed(ApiCode.CODE_JSON_ERROR, null);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(context, R.string.record_get_failed, Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(error.getMessage())) {
                    LogUtil.i(TAG, error.getMessage());
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestHistoryUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 邀请好友加入牌局
     * @param gameCode 游戏CODE
     * @param friends
     */
    public void doInviteFriedns(final String gameCode , ArrayList<String> friends) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            return;
        }
        if(friends == null || friends.size() == 0){
            return;
        }
        int size = friends.size();
        final StringBuffer friendsBuffer = new StringBuffer();
        for(int i = 0 ; i < size ; i++){
            friendsBuffer.append(friends.get(i));
            if(i != (size - 1)){
                friendsBuffer.append(",");
            }
        }
        final String firendsStr = friendsBuffer.toString();
        //
        requestInviteFriendUrl = getHost() + ApiConstants.URL_GAME_INVITEFRIEND;
        LogUtil.i(TAG, requestInviteFriendUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestInviteFriendUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() != null) {
                    LogUtil.i(TAG, error.getMessage());
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> paramsMap = new HashMap<String, String>();
//                paramsMap.put("uid", UserPreferences.getInstance(ChessApp.sAppContext).getUserId());
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("code", gameCode);
                paramsMap.put("friends", firendsStr);
                LogUtil.i(TAG, paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestInviteFriendUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 进入等待中的牌局
     * @param gameCode 游戏CODE
     */
    public void doJoinWaitGame(final String gameCode) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            return;
        }
        requestJoinWaitGameUrl = getHost() + ApiConstants.URL_GAME_WAIT;
        LogUtil.i(TAG, requestJoinWaitGameUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestJoinWaitGameUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() != null) {
                    LogUtil.i(TAG, error.getMessage());
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> paramsMap = new HashMap<String, String>();
//                paramsMap.put("uid", UserPreferences.getInstance(ChessApp.sAppContext).getUserId());
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("code", gameCode);
                LogUtil.i(TAG, paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestJoinWaitGameUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 退出等待中的牌局
     * @param gameCode 游戏CODE
     */
    public void doLeaveWaitGame(final String gameCode) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            return;
        }
        requestLeaveWaitGameUrl = getHost() + ApiConstants.URL_GAME_LEAVE;
        LogUtil.i(TAG, requestLeaveWaitGameUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestLeaveWaitGameUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() != null) {
                    LogUtil.i(TAG, error.getMessage());
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> paramsMap = new HashMap<String, String>();
//                paramsMap.put("uid", UserPreferences.getInstance(ChessApp.sAppContext).getUserId());
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("code", gameCode);
                LogUtil.i(TAG, paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestLeaveWaitGameUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 忽略牌局邀请
     * @param gameCode 游戏CODE
     */
    public void doIgnoreGameInvite(final String gameCode) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            return;
        }
        requestIgnoreGameInviteUrl = getHost() + ApiConstants.URL_GAME_IGNORE;
        LogUtil.i(TAG, requestIgnoreGameInviteUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestIgnoreGameInviteUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() != null) {
                    LogUtil.i(TAG, error.getMessage());
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> paramsMap = new HashMap<String, String>();
//                paramsMap.put("uid", UserPreferences.getInstance(ChessApp.sAppContext).getUserId());
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("code", gameCode);
                LogUtil.i(TAG, paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestIgnoreGameInviteUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    StringBuffer paramsBuf = new StringBuffer();

    /**
     * 获取进行中的群牌局数量
     */
    public synchronized void getGamePlayingListCount(List<RecentContact> recentContactList ,final GameRequestCallback mGameRequestCallback) {
        if (recentContactList == null || recentContactList.isEmpty()|| !NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            return;
        }
        long currentTime = DemoCache.getCurrentServerSecondTime();
        long lastGetTime = RequestTimeLimit.lastGetGamePlayingTime;
        if ((currentTime - lastGetTime) < RequestTimeLimit.GET_GAME_PLAYING_TIME_LIMIT) {
            LogUtil.i(TAG, "获取数据时间未到");
            return;
        }
        paramsBuf = new StringBuffer();
        Team team;
        //获取出数据
        for (RecentContact recentContact : recentContactList) {
            if (recentContact.getSessionType() == SessionTypeEnum.Team) {
                team = TeamDataCache.getInstance().getTeamById(recentContact.getContactId());
                if (team != null && team.isMyTeam() && !GameConstants.isGmaeClub(team)) {
                    //是我的群，并且不是游戏群
                    paramsBuf.append(recentContact.getContactId()).append(",");
                }
            }
        }
        if (paramsBuf.length() == 0) {
            return;
        }
        String params = paramsBuf.substring(0, paramsBuf.length() - 1);//去除最后个，
//        final HashMap<String, String> paramsMap = new HashMap<String, String>();
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("tids", params);
        requestGamePlayingListCountUrl = getHost() + ApiConstants.URL_GAME_PLAYINGLIST + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestGamePlayingListCountUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestGamePlayingListCountUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new org.json.JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        RequestTimeLimit.lastGetGamePlayingTime = (DemoCache.getCurrentServerSecondTime());
                        if(mGameRequestCallback != null){
                            mGameRequestCallback.onSuccess(json);
                        }
                    }
                } catch (JSONException e) {
                    if(mGameRequestCallback != null){
                        mGameRequestCallback.onFailed(1, null);
                    }
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() != null) {
                    LogUtil.i(TAG, error.getMessage());
                }
                if(mGameRequestCallback != null){
                    mGameRequestCallback.onFailed(2, null);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestGamePlayingListCountUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 获取进行中的群牌局数量
     */
    public synchronized void getGamePlayingListCountByTeamList(List<Team> teamList ,final GameRequestCallback mGameRequestCallback) {
        if (teamList == null || teamList.isEmpty()|| !NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            if(mGameRequestCallback != null){
                mGameRequestCallback.onFailed(1, null);
            }
            return;
        }
        long currentTime = DemoCache.getCurrentServerSecondTime();
        long lastGetTime = RequestTimeLimit.lastGetGamePlayingTime;
        if ((currentTime - lastGetTime) < RequestTimeLimit.GET_GAME_PLAYING_TIME_LIMIT) {
            LogUtil.i(TAG, "获取数据时间未到");
            if(mGameRequestCallback != null){
                mGameRequestCallback.onFailed(1, null);
            }
            return;
        }
        paramsBuf = new StringBuffer();
//        Team team;
        //获取出数据
        for (Team team : teamList) {
//            if (team.get == SessionTypeEnum.Team) {
//            }
            if (team != null && team.isMyTeam() && !GameConstants.isGmaeClub(team)) {
                //是我的群，并且不是游戏群
                paramsBuf.append(team.getId()).append(",");
            }
        }
        if (paramsBuf.length() == 0) {
            if(mGameRequestCallback != null){
                mGameRequestCallback.onFailed(1, null);
            }
            return;
        }
        String params = paramsBuf.substring(0, paramsBuf.length() - 1);//去除最后个，
//        final HashMap<String, String> paramsMap = new HashMap<String, String>();
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("tids", params);
        requestGamePlayingListCountUrl = getHost() + ApiConstants.URL_GAME_PLAYINGLIST + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestGamePlayingListCountUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestGamePlayingListCountUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new org.json.JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        RequestTimeLimit.lastGetGamePlayingTime = (DemoCache.getCurrentServerSecondTime());
                        if(mGameRequestCallback != null){
                            mGameRequestCallback.onSuccess(json);
                        }
                    }
                } catch (JSONException e) {
                    if(mGameRequestCallback != null){
                        mGameRequestCallback.onFailed(1, null);
                    }
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() != null) {
                    LogUtil.i(TAG, error.getMessage());
                }
                if(mGameRequestCallback != null){
                    mGameRequestCallback.onFailed(2, null);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestGamePlayingListCountUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    public synchronized void getGamePlayingList(List<Team> teamList ,final Response.Listener<String> listener, Response.ErrorListener errorListener) {
        paramsBuf = new StringBuffer();
//        Team team;
        //获取出数据
        for (Team team : teamList) {
//            if (team.get == SessionTypeEnum.Team) {
//            }
            if (team != null && team.isMyTeam() && !GameConstants.isGmaeClub(team)) {
                //是我的群，并且不是游戏群
                paramsBuf.append(team.getId()).append(",");
            }
        }
        if (paramsBuf.length() == 0) {
            if (errorListener != null) {
                errorListener.onErrorResponse(null);
            }
            return;
        }
        String params = paramsBuf.substring(0, paramsBuf.length() - 1);//去除最后个，
//        final HashMap<String, String> paramsMap = new HashMap<String, String>();
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("tids", params);
        requestGamePlayingListCountUrl = getHost() + ApiConstants.URL_GAME_PLAYINGLIST + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestGamePlayingListCountUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestGamePlayingListCountUrl, listener, errorListener){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestGamePlayingListCountUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 加入俱乐部牌局
     * @param joinWay
     * @param gameRequestCallback
     */
//    public void executeJoinClubGame(final String joinWay, GameEntity gameEntity, final GameRequestCallback gameRequestCallback) {
//
//        //俱乐部牌局默认使用聊天室
//        {
//            executeJoinGame(joinWay, gameEntity.getCode(), new GameRequestCallback() {
//                @Override
//                public void onSuccess(JSONObject response) {
//                    if (gameRequestCallback != null) {
//                        gameRequestCallback.onSuccess(response);
//                    }
//                }
//
//                @Override
//                public void onFailed(int code) {
//                    if (gameRequestCallback != null) {
//                        gameRequestCallback.onFailed(code);
//                    }
//                }
//            });
//        }
//    }

    /**
     * 加入牌局
     * @param joinWay
     * @param code
     * @param gameRequestCallback
     */
    public void executeJoinGame(final String joinWay, final String code , final GameRequestCallback gameRequestCallback) {
        DialogMaker.showProgressDialog(mActivity , getString(com.netease.nim.uikit.R.string.empty), false);

        getGameStatus(joinWay, code, new GameRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                DialogMaker.dismissProgressDialog();
                //加入牌局
                try {
                    JSONObject jsonData = response.getJSONObject("data");
                    GameEntity gameEntity = new GameEntity();
                    String tid = jsonData.optString("tid");
                    String gid = jsonData.optString("gid");
                    String serverIp = jsonData.optString(GameConstants.KEY_GAME_SERVER);
                    int status = jsonData.optInt("status");
                    int gameMode = jsonData.optInt(GameConstants.KEY_GAME_MODE);
                    int gameType = jsonData.optInt("type");
                    String websocketUrl = jsonData.optString("ws_url");
                    gameEntity.room_id = jsonData.optString("room_id","");
                    gameEntity.tid = (tid);
                    gameEntity.gid = (gid);
                    gameEntity.code = (jsonData.optString("code"));
                    gameEntity.joinCode = code;
                    gameEntity.channel = jsonData.optString("channel");
                    gameEntity.gameMode = (gameMode);
                    gameEntity.serverIp = (serverIp);
                    gameEntity.status = (status);
                    gameEntity.type = (gameType);
                    //如果是私人牌局，并且是普通牌局或者普通SNG，那么进入群
                    if (gameMode == GameConstants.GAME_MODE_NORMAL || gameMode == GameConstants.GAME_MODE_SNG) {
                        intentGameByStatus(joinWay, gameEntity, gameRequestCallback, websocketUrl);
                    } else if (gameMode == GameConstants.GAME_MODE_MT_SNG || gameMode == GameConstants.GAME_MODE_MTT) {
                        //其他赛事，那么先进入聊天室
                        String roomId = jsonData.optString(GameConstants.KEY_ROOM_ID);
                        enterMatchRoom(roomId, serverIp, true, code, gameRequestCallback, gameEntity, websocketUrl);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (gameRequestCallback != null) {
                        gameRequestCallback.onFailed(ApiCode.CODE_JSON_ERROR, null);
                    }
                }
            }

            @Override
            public void onFailed(int code, JSONObject response) {
                DialogMaker.dismissProgressDialog();
                if (gameRequestCallback != null) {
                    gameRequestCallback.onFailed(code, response);
                }
            }
        });
    }

    //根据牌局状态进行相关操作
    public void intentGameByStatus(String joinWay ,GameEntity gameEntity,final GameRequestCallback gameRequestCallback, final String websocketUrl) {
        int status = gameEntity.status;
        if (status == GameStatus.GAME_STATUS_WAIT || status == GameStatus.GAME_STATUS_START) {
            //等待和开始的都需要先加入对应的俱乐部
            applyJoinTeam(joinWay, gameEntity, gameRequestCallback, websocketUrl);
        } else if (status == GameStatus.GAME_STATUS_FINISH) {
            DialogMaker.dismissProgressDialog();
            Toast.makeText(ChessApp.sAppContext, getString(R.string.game_finished), Toast.LENGTH_SHORT).show();
            if (gameRequestCallback != null) {
                gameRequestCallback.onFailed(ApiCode.CODE_GAME_NONE_EXISTENT, null);
            }
        }
    }

    public void applyJoinTeam(final String joinWay , final GameEntity gameEntity, final GameRequestCallback mGameRequestCallback, final String websocketUrl) {
        if(gameEntity.type == GameConstants.GAME_TYPE_CLUB) {//俱乐部使用聊天室了
            String roomId = gameEntity.room_id;
            if(!TextUtils.isEmpty(roomId) && !"0".equals(roomId)) {
                //如果是新版本的牌局，需要加入聊天室.
                this.enterMatchRoom(roomId, gameEntity.serverIp, false, new GameRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        joinPrivateClubSuccess(joinWay, gameEntity, mGameRequestCallback);
                    }

                    @Override
                    public void onFailed(int code, JSONObject response) {
                        Toast.makeText(ChessApp.sAppContext, R.string.game_join_failure, Toast.LENGTH_SHORT).show();
                    }
                }, gameEntity, websocketUrl);
            } else {
                //兼容旧版本
                joinPrivateClubSuccess(joinWay, gameEntity, mGameRequestCallback);
            }
        } else {
            final String teamId = gameEntity.tid;
            Team team = TeamDataCache.getInstance().getTeamById(teamId);
            if (team != null && team.isMyTeam()) {
                //已经在游戏群中
                joinPrivateClubSuccess(joinWay, gameEntity, mGameRequestCallback);
                return;
            }
            //
            if (NIMClient.getStatus() != StatusCode.LOGINED) {
                Toast.makeText(ChessApp.sAppContext, R.string.game_join_failure, Toast.LENGTH_SHORT).show();
                DialogMaker.dismissProgressDialog();
                return;
            }
            //
            if (!DialogMaker.isShowing()) {
                DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
            }
            NIMClient.getService(TeamService.class).applyJoinTeam(teamId, "").setCallback(new RequestCallback<Team>() {
                @Override
                public void onSuccess(Team team) {
                    TeamDataCache.getInstance().addOrUpdateTeam(team);
                    joinPrivateClubSuccess(joinWay, gameEntity, mGameRequestCallback);
                }

                @Override
                public void onFailed(int code) {
                    if (code == ResponseCode.RES_TEAM_APPLY_SUCCESS) {
                        DialogMaker.dismissProgressDialog();
                        Toast.makeText(ChessApp.sAppContext, R.string.team_apply_to_join_send_success, Toast.LENGTH_SHORT).show();
                    } else if (code == ResponseCode.RES_TEAM_ALREADY_IN) {
                        //已经在群里
                        joinPrivateClubSuccess(joinWay, gameEntity, mGameRequestCallback);
//                    SessionHelper.startTeamSession(mActivity, teamId, serverIp);
//                    if(mGameRequestCallback != null){
//                        mGameRequestCallback.onSuccess(null);
//                    }
//                    UmengAnalyticsEvent.onEventGameJoin(ChessApp.sAppContext, joinWay);
                    } else if (code == ResponseCode.RES_ETIMEOUT) {
                        DialogMaker.dismissProgressDialog();
                        Toast.makeText(ChessApp.sAppContext, R.string.game_join_failure, Toast.LENGTH_SHORT).show();
                    } else {
                        DialogMaker.dismissProgressDialog();
                        Toast.makeText(ChessApp.sAppContext, R.string.game_join_failure, Toast.LENGTH_SHORT).show();
                        LogUtil.i(TAG, "failed, error code =" + code);
                    }
                }

                @Override
                public void onException(Throwable throwable) {
                    Toast.makeText(ChessApp.sAppContext, R.string.game_join_failure, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //加入游戏群成功或者已经在该俱乐部中
    public void joinPrivateClubSuccess(String joinWay, GameEntity gameEntity, GameRequestCallback mGameRequestCallback) {
        if (gameEntity.status == GameStatus.GAME_STATUS_START) {
            //牌局已经开始，加入牌局
            doGameJoin(joinWay, gameEntity, false, false, mGameRequestCallback);
        } else if (gameEntity.status == GameStatus.GAME_STATUS_WAIT) {
            UmengAnalyticsEvent.onEventGameJoin(ChessApp.sAppContext, joinWay, gameEntity.gameMode);
            if (mGameRequestCallback != null) {
                mGameRequestCallback.onSuccess(null);
            }
            //牌局还没开始，打开聊天界面
            if (mActivity instanceof MainActivity) {
                ((MainActivity) mActivity).switchChatFragment(gameEntity.tid, gameEntity.serverIp, false);
            } else {
                SessionHelper.startTeamSession(mActivity, gameEntity.tid, gameEntity.serverIp);
            }
            DialogMaker.dismissProgressDialog();
        }
    }

    /**
     */
//    public void doJoinGameMatch(final String gameCode , final GameRequestCallback mGameRequestCallback) {
//        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
//            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
//            DialogMaker.dismissProgressDialog();
//            return;
//        }
//        if (!DialogMaker.isShowing()) {
//            DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
//        }
//        requestJoinUrl = ApiConstants.URL_GAME_JOIN;
//        LogUtil.i(TAG, requestJoinUrl);
//        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestJoinUrl, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                LogUtil.i(TAG, response);
//                try {
//                    JSONObject json = new JSONObject(response);
//                    int code = json.getInt("code");
//                    if (code == ApiCode.CODE_SUCCESS || code == ApiCode.CODE_GAME_IS_IN) {
//                        //3010：已经加入的代表加入成功
//                        JSONObject dataJsonObject = json.optJSONObject("data");
//                        String hostIp = dataJsonObject.optString(GameConstants.KEY_GAME_SERVER);//游戏服务器
//                        String roomId = dataJsonObject.optString(GameConstants.KEY_ROOM_ID);
//                        enterMatchRoom(roomId, hostIp, mGameRequestCallback);
//                    } else if (code == ApiCode.CODE_GAME_NONE_EXISTENT) {
//                        DialogMaker.dismissProgressDialog();
//                        Toast.makeText(ChessApp.sAppContext, getString(R.string.game_join_notexist), Toast.LENGTH_SHORT).show();
//                        if (mGameRequestCallback != null) {
//                            mGameRequestCallback.onFailed(code);
//                        }
//                    } else if (code == ApiCode.CODE_GAME_IS_CREATED) {
//                        DialogMaker.dismissProgressDialog();
//                        Toast.makeText(ChessApp.sAppContext, getString(R.string.game_join_failure), Toast.LENGTH_SHORT).show();
//                    } else {
//                        DialogMaker.dismissProgressDialog();
//                        String message = ApiResultHelper.getShowMessage(json);
//                        if (TextUtils.isEmpty(message)) {
//                            message = ChessApp.sAppContext.getString(R.string.game_join_failure);
//                        }
//                        Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    DialogMaker.dismissProgressDialog();
//                    Toast.makeText(ChessApp.sAppContext, getString(R.string.game_join_failure), Toast.LENGTH_SHORT).show();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                DialogMaker.dismissProgressDialog();
//                Toast.makeText(ChessApp.sAppContext, getString(R.string.game_join_failure), Toast.LENGTH_SHORT).show();
//                if (error.getMessage() != null) {
//                    LogUtil.i(TAG, error.getMessage());
//                }
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
//                paramsMap.put("code", gameCode);
//                LogUtil.i(TAG, paramsMap.toString());
//                return paramsMap;
//            }
//        };
//        signRequest.setTag(requestJoinUrl);
//        ChessApp.sRequestQueue.add(signRequest);
//    }

    private AbortableFuture<EnterChatRoomResultData> enterRequest;
    //进入聊天室
    public void enterMatchRoom(final String roomId ,final String hostIp ,final boolean needGo, final String joinCode, final GameRequestCallback mGameRequestCallback, final GameEntity gameEntity, final String websocketUrl) {
        if (NIMClient.getStatus() != StatusCode.LOGINED || TextUtils.isEmpty(roomId) || TextUtils.isEmpty(hostIp) || NIMClient.getStatus() != StatusCode.LOGINED) {
            Toast.makeText(ChessApp.sAppContext, R.string.game_join_failure, Toast.LENGTH_SHORT).show();
            DialogMaker.dismissProgressDialog();
            if (mGameRequestCallback != null) {
                mGameRequestCallback.onFailed(0, null);
            }
            return;
        }
        LogUtil.e(TAG, "云信进入聊天室, roomId=" + roomId);
        EnterChatRoomData data = new EnterChatRoomData(roomId);
        enterRequest = NIMClient.getService(ChatRoomService.class).enterChatRoom(data);
        enterRequest.setCallback(new RequestCallback<EnterChatRoomResultData>() {
            @Override
            public void onSuccess(EnterChatRoomResultData result) {
                if (result == null || result.getRoomInfo() == null) {
                    if (mGameRequestCallback != null) {
                        mGameRequestCallback.onFailed(-1, null);
                    }
                    return;
                }
                if(needGo) {
                    ChatRoomInfo roomInfo = result.getRoomInfo();
                    onLoginDone();
                    MatchRoomActivity.startWithWebsockUrl(mActivity, roomInfo, hostIp, joinCode, gameEntity, websocketUrl, roomId);
                }
                if (mGameRequestCallback != null) {
                    mGameRequestCallback.onSuccess(null);
                }
            }
            @Override
            public void onFailed(int code) {
                LogUtil.e(TAG, "云信进入聊天室失败, code=" + code);
                if(code == 302){ //云信异常...
                    Toast.makeText(ChessApp.sAppContext, R.string.game_join_failure, Toast.LENGTH_SHORT).show();
                    UserPreferences.getInstance(ChessApp.sAppContext).setUserToken("");
                    NIMClient.getService(AuthService.class).logout();
                    GeTuiHelper.unBindAliasUid(ChessApp.sAppContext);//解绑个推别名
                    HandsCollectDBHelper.clearCollectHands(ChessApp.sAppContext);//清空手牌收藏
                    UserPreferences.getInstance(ChessApp.sAppContext).setCollectHandNum(0);//清空手牌收藏数量
                    UmengAnalytics.onProfileSignOff();
                    //清除登录状态&缓存&注销监听
                    LogoutHelper.logout();
                    DBUtil.closeDBUtil();//关闭数据库，因为数据库以个人UI作为账户
                    // 启动登录
                    Intent intent = new Intent(ChessApp.sAppContext, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Extras.EXTRA_REASON, LoginActivity.REASON_NO);
                    ChessApp.sAppContext.startActivity(intent);
                    return ;
                }
                int errorCode = NIMClient.getService(ChatRoomService.class).getEnterErrorCode(roomId);
                Toast.makeText(ChessApp.sAppContext, R.string.game_join_failure, Toast.LENGTH_SHORT).show();
                onLoginDone();
                if (mGameRequestCallback != null) {
                    mGameRequestCallback.onFailed(code, null);
                }
//                if (code == ResponseCode.RES_CHATROOM_BLACKLIST) {
//                    Toast.makeText(ChessApp.sAppContext, "你已被拉入黑名单，不能再进入", android.widget.Toast.LENGTH_SHORT).show();
//                } else if (code == ResponseCode.RES_ENONEXIST) {
//                    Toast.makeText(ChessApp.sAppContext, "聊天室不存在", android.widget.Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(ChessApp.sAppContext, "enter chat room failed, code=" + code, android.widget.Toast.LENGTH_SHORT).show();
//                }
            }
            @Override
            public void onException(Throwable exception) {
                onLoginDone();
                android.widget.Toast.makeText(ChessApp.sAppContext, "enter chat room exception, e=" + exception.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }


    //重载
    public void enterMatchRoom(final String roomId ,final String hostIp ,final boolean needGo, final GameRequestCallback mGameRequestCallback, GameEntity gameEntity, final String websocketUrl) {
        enterMatchRoom(roomId, hostIp, needGo, "", mGameRequestCallback, gameEntity, websocketUrl);
    }

    /**
     * 获取在线(在线表示未结束)游戏的管理员列表
     */
    String requestGetMttMgrListUrl;
    public void getMgrList(final String gameId, final String creatorId, final String gameCode, final GameRequestCallback mGameRequestCallback) {
        if (TextUtils.isEmpty(creatorId) || TextUtils.isEmpty(gameCode)) {
            return;
        }
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            com.htgames.nutspoker.widget.Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, com.htgames.nutspoker.widget.Toast.LENGTH_LONG).show();
            if (mGameRequestCallback != null) {
                mGameRequestCallback.onFailed(-1, null);
            }
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
//        paramsMap.put("uid", creatorId);//改版后这个参数不需要了
        paramsMap.put("code", gameCode);
        requestGetMttMgrListUrl = getHost() + ApiConstants.URL_MTT_MGR_LSIT + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestGetMttMgrListUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestGetMttMgrListUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                DialogMaker.dismissProgressDialog();
                JsonResolveUtil.getChannelList(gameId, response);//位森做了限制，只有管理员能请求到结果，不是管理员返回空
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onSuccess(json);
                        }
                    } else {
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onFailed(code, json);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    DialogMaker.dismissProgressDialog();
                    if (mGameRequestCallback != null) {
                        mGameRequestCallback.onFailed(-1, null);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                if (mGameRequestCallback != null) {
                    mGameRequestCallback.onFailed(-1, null);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestGetMttMgrListUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 获取结束游戏的管理员列表  GET 参数　gid
     */
    String requestGetMttMgrListOffUrl;
    public void getMgrListOff(final String gid, final VolleyCallback callback) {
        if (TextUtils.isEmpty(gid)) {
            return;
        }
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            com.htgames.nutspoker.widget.Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, com.htgames.nutspoker.widget.Toast.LENGTH_LONG).show();
            callback.onErrorResponse(null);
            return;
        }
        DialogMaker.showProgressDialog(ChessApp.sAppContext, "", false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("gid", gid);
        requestGetMttMgrListOffUrl = getHost() + ApiConstants.URL_ADMIN_LIST_OFF + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestGetMttMgrListOffUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestGetMttMgrListOffUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                DialogMaker.dismissProgressDialog();
                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                callback.onErrorResponse(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestGetMttMgrListOffUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    private String requestDelCheckinPlayer;
    public void delCheckinPlayer(final String gameCode, final String player_id, final String handledId, final VolleyCallback callback) {
        if (TextUtils.isEmpty(player_id) || TextUtils.isEmpty(gameCode) || TextUtils.isEmpty(handledId)) {
            if (callback != null) {
                callback.onErrorResponse(null);
            }
            return;
        }
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            com.htgames.nutspoker.widget.Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, com.htgames.nutspoker.widget.Toast.LENGTH_LONG).show();
            if (callback != null) {
                callback.onErrorResponse(null);
            }
            return;
        }
        DialogMaker.showProgressDialog(ChessApp.sAppContext, "", false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("uid", handledId);
        paramsMap.put("player_id", player_id);
        paramsMap.put("code", gameCode);
        requestDelCheckinPlayer = getHost() + ApiConstants.URL_MTT_REMOVE_CHECKIN_PLAYER + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestDelCheckinPlayer);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestDelCheckinPlayer, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                DialogMaker.dismissProgressDialog();
                if (callback != null) {
                    callback.onResponse(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                if (callback != null) {
                    callback.onErrorResponse(error);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestDelCheckinPlayer);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 获取通道下的玩家
     * @param code 游戏code
     * @param p 分页请求的页数
     */
    private String requestChannelPlayerList;
    public void getChannelPlayerList(final String code, final int p, final VolleyCallback mGameRequestCallback) {
        if (StringUtil.isSpace(code)) {
            mGameRequestCallback.onErrorResponse(null);
            return;
        }
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            mGameRequestCallback.onErrorResponse(null);
            com.htgames.nutspoker.widget.Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, com.htgames.nutspoker.widget.Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(ChessApp.sAppContext, "", false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("channel", code);
        paramsMap.put("p", p + "");
        requestChannelPlayerList = getHost() + ApiConstants.URL_USER_BY_ADMIN + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestChannelPlayerList);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestChannelPlayerList, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                //{"code":0,"message":"ok","data":{"checkin_player":3,"rebuy_cnt":"0","pagesize":1,"users":[{"uid":"10034","rebuy_cnt":0},{"uid":"10037","rebuy_cnt":0},{"uid":"10033","rebuy_cnt":0}]}}
                DialogMaker.dismissProgressDialog();
                if (mGameRequestCallback != null) {
                    mGameRequestCallback.onResponse(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                mGameRequestCallback.onErrorResponse(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestChannelPlayerList);
        signRequest.setRetryPolicy(new DefaultRetryPolicy(SignStringRequest.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));//重试次数改为0，防止null point exception
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 获取通道下的玩家
     * @param code 游戏code
     * @param p 分页请求的页数
     */
    private String requestChannelPlayerListOff;
    public void getChannelPlayerListOff(final String gid, final int p, final String channel, String mgrId, final VolleyCallback mGameRequestCallback) {
        if (StringUtil.isSpace(gid)) {
            mGameRequestCallback.onErrorResponse(null);
            return;
        }
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            mGameRequestCallback.onErrorResponse(null);
            com.htgames.nutspoker.widget.Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, com.htgames.nutspoker.widget.Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(ChessApp.sAppContext, "", false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("gid", gid);//
        paramsMap.put("channel", channel);
//        paramsMap.put("channel", channel);
        paramsMap.put("p", p + "");
        requestChannelPlayerListOff = getHost() + ApiConstants.URL_USER_BY_ADMIN_OFF + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestChannelPlayerListOff);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestChannelPlayerListOff, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                //{"code":0,"message":"ok","data":{"checkin_player":3,"rebuy_cnt":"0","pagesize":1,"users":[{"uid":"10034","rebuy_cnt":0},{"uid":"10037","rebuy_cnt":0},{"uid":"10033","rebuy_cnt":0}]}}
                DialogMaker.dismissProgressDialog();
                if (mGameRequestCallback != null) {
                    mGameRequestCallback.onResponse(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                mGameRequestCallback.onErrorResponse(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestChannelPlayerListOff);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 搜索某个渠道下的玩家
     * @param gid 游戏gid
     */
    private String requestChannelSearchPlayer;
    public void searchChannelPlayer(String gameChannel, final String gid, final String word, final VolleyCallback mGameRequestCallback) {
        if (StringUtil.isSpace(gid) || StringUtil.isSpace(gameChannel)) {
            Toast.makeText(ChessApp.sAppContext, "信息不全", Toast.LENGTH_LONG).show();
            mGameRequestCallback.onErrorResponse(null);
            return;
        }
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            mGameRequestCallback.onErrorResponse(null);
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(ChessApp.sAppContext, "", false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("gid", gid);
        paramsMap.put("word", word);
        paramsMap.put("channel", gameChannel);
        requestChannelSearchPlayer = getHost() + ApiConstants.URL_ADMIN_LIST_SEARCH_PLAYER + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestChannelSearchPlayer);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestChannelSearchPlayer, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                //  {"code":0,"message":"ok","data":[{"id":"10041","nickname":"sugar","gid":"1946","ranking":0,"uid":"10041","reward":"200","index":false}]}
                DialogMaker.dismissProgressDialog();
                if (mGameRequestCallback != null) {
                    mGameRequestCallback.onResponse(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                mGameRequestCallback.onErrorResponse(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestChannelSearchPlayer);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * mtt大厅发送语音公告    新增错误码 3064 通知发送失败       ApiConstants.URL_SEND_TEXT_NOTICE   //POST方式 参数uid code msg
     * @param uid
     * @param code
     * @param msg
     */
    private String URL_SEND_TEXT_NOTICE;
    public void sendTextNotice(String uid, String code, String msg, final VolleyCallback mGameRequestCallback) {
        if (StringUtil.isSpace(code) || StringUtil.isSpace(msg)) {
            return;
        }
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            com.htgames.nutspoker.widget.Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, com.htgames.nutspoker.widget.Toast.LENGTH_LONG).show();
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("code", code);
        paramsMap.put("msg", msg);
        URL_SEND_TEXT_NOTICE = getHost() + ApiConstants.URL_SEND_TEXT_NOTICE + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, URL_SEND_TEXT_NOTICE);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, URL_SEND_TEXT_NOTICE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                if (mGameRequestCallback != null) {
                    mGameRequestCallback.onResponse(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mGameRequestCallback != null) {
                    mGameRequestCallback.onErrorResponse(error);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(URL_SEND_TEXT_NOTICE);
        ChessApp.sRequestQueue.add(signRequest);
    }

    String requestAddClubChannel;//添加赛事管理员的俱乐部渠道
    public void addClubChannel(String tid, String code, final GameRequestCallback callback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("tid", tid);
        paramsMap.put("code", code);
        requestAddClubChannel = getHost() + ApiConstants.URL_MTT_ADD_MGR_CLUB_CHANNEL + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestAddClubChannel);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestAddClubChannel, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int resultCode = json.getInt("code");
                    if (resultCode == 0) {
                        if(callback != null) {
                            callback.onSuccess(json);
                        }
                    } else {
                        if (callback != null) {
                            callback.onFailed(resultCode, json);
                        }
                    }
                } catch (JSONException e) {
                    if (callback != null) {
                        callback.onFailed(ApiCode.CODE_JSON_ERROR, null);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (callback != null) {
                    callback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestAddClubChannel);
        ChessApp.sRequestQueue.add(signRequest);
    }

    String requestDeleteClubChannel;
    public void deleteClubChannel(String tid, String code, String blockInfo, final GameRequestCallback callback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("tid", tid);
        paramsMap.put("code", code);
        paramsMap.put("block", blockInfo);
        requestDeleteClubChannel = getHost() + ApiConstants.URL_MTT_DELETE_MGR_CLUB_CHANNEL + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestDeleteClubChannel);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestDeleteClubChannel, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);//{"code":0,"message":"ok","data":{"horde_id":"4","horde_vid":3939732}}
                try {
                    JSONObject json = new JSONObject(response);
                    int resultCode = json.getInt("code");
                    if (resultCode == 0) {
                        if(callback != null) {
                            callback.onSuccess(json);
                        }
                    } else {
                        if (callback != null) {
                            callback.onFailed(resultCode, json);
                        }
                    }
                } catch (JSONException e) {
                    if (callback != null) {
                        callback.onFailed(ApiCode.CODE_JSON_ERROR, null);
                    }
                }
                DialogMaker.dismissProgressDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                String failMsg = ApiCode.SwitchCode(ApiCode.CODE_NETWORD_ERROR, null);
                Toast.makeText(ChessApp.sAppContext, failMsg + "  code: " + ApiCode.CODE_NETWORD_ERROR, Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestDeleteClubChannel);
        ChessApp.sRequestQueue.add(signRequest);
    }

    private void onLoginDone() {
        enterRequest = null;
        DialogMaker.dismissProgressDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAll(requestCreateUrl);
        cancelAll(requestJoinUrl);
        cancelAll(requestStartUrl);
        cancelAll(requestCancelUrl);
        cancelAll(requestPlayingListUrl);
        cancelAll(requestGameStatusUrl);
        cancelAll(requestHistoryUrl);
        //
        cancelAll(requestInviteFriendUrl);
        cancelAll(requestJoinWaitGameUrl);
        cancelAll(requestLeaveWaitGameUrl);
        cancelAll(requestIgnoreGameInviteUrl);
        cancelAll(requestGamePlayingListCountUrl);
        cancelAll(requestGetMttMgrListUrl);
        cancelAll(requestDelCheckinPlayer);

        //add by db
        cancelAll(requestChannelPlayerList);
        cancelAll(requestGetMttMgrListOffUrl);
        cancelAll(requestChannelSearchPlayer);
        cancelAll(URL_SEND_TEXT_NOTICE);//语音公告
        cancelAll(requestCreateFreeUrl);
        cancelAll(requestGameInfoUrl);
        cancelAll(requestAddClubChannel);
        cancelAll(requestDeleteClubChannel);
        cancelAll(requestChannelPlayerListOff);
    }
}
