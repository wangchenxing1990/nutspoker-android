package com.htgames.nutspoker.ui.action;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.netease.nim.uikit.api.ApiCode;
import com.htgames.nutspoker.api.ApiResultHelper;
import com.htgames.nutspoker.cocos2d.PokerActivity;
import com.htgames.nutspoker.db.HandsCollectDBHelper;
import com.htgames.nutspoker.game.match.activity.FreeRoomAC;
import com.htgames.nutspoker.game.match.activity.MatchRoomActivity;
import com.htgames.nutspoker.game.model.GameStatus;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.interfaces.VolleyCallback;
import com.htgames.nutspoker.net.RequestTimeLimit;
import com.htgames.nutspoker.push.GeTuiHelper;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalytics;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalyticsEventConstants;
import com.htgames.nutspoker.ui.activity.Login.LoginActivity;
import com.htgames.nutspoker.ui.base.BaseAction;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.api.ApiConstants;
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.api.SignStringRequest;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.PineappleConfig;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.preference.SettingsPreferences;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.db.DBUtil;
import com.netease.nim.uikit.login.LogoutHelper;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 * Created by 周智慧 on 17/3/8.  自由局
 */

public class NewGameAction extends BaseAction {
    public final static String TAG = GameAction.TAG;
    String requestJoinGameUrl;
    String requestStartGameUrl;
    public NewGameAction(Activity activity, View baseView) {
        super(activity, baseView);
    }

    public void joinGame(final String joinWay, final String code , final GameRequestCallback mGameRequestCallback) {
        boolean hasAgree = SettingsPreferences.getInstance(mActivity).hasAgreePokerClansProtocol();
        if (!hasAgree) {
            MainActivity.showPokerClansProtocol(mActivity, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    joinGameAfterAgreeProtocol(joinWay, code, mGameRequestCallback);
                }
            }, null);
        } else {
            joinGameAfterAgreeProtocol(joinWay, code, mGameRequestCallback);
        }
    }

    public void joinGameAfterAgreeProtocol(final String joinWay, final String code , final GameRequestCallback mGameRequestCallback) {
        DialogMaker.showProgressDialog(mActivity , getString(com.netease.nim.uikit.R.string.empty), false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("code", code);
        requestJoinGameUrl = getHost() + ApiConstants.URL_GAME_JOIN_GAME + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestJoinGameUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestJoinGameUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int resultCode = json.getInt("code");
                    if (resultCode == 0) {
                        RequestTimeLimit.lastGetRecentGameTime = (0);
                        if(mGameRequestCallback != null) {
                            mGameRequestCallback.onSuccess(json);
                        }
                        JSONObject jsonData = json.getJSONObject("data");
                        GameEntity gameEntity = new GameEntity();
                        String tid = jsonData.optString("tid");
                        String gid = jsonData.optString("gid");
                        String serverIp = jsonData.optString(GameConstants.KEY_GAME_SERVER);
                        int status = jsonData.optInt("status");
                        int gameMode = jsonData.optInt(GameConstants.KEY_GAME_MODE);
                        int gameType = jsonData.optInt("type");
                        String websocketUrl = jsonData.optString("ws_url");
                        gameEntity.room_id = jsonData.optString("room_id", "");
                        gameEntity.tid = (tid);
                        gameEntity.gid = (gid);
                        gameEntity.code = jsonData.optString("code");//这个code是游戏最原始的code，和方法里面的参数不一样
                        gameEntity.joinCode = code;
                        gameEntity.channel = jsonData.optString("channel");
                        gameEntity.play_mode = jsonData.optInt("play_mode");
                        gameEntity.gameMode = (gameMode);
                        gameEntity.serverIp = (serverIp);
                        gameEntity.status = (status);
                        gameEntity.type = (gameType);
                        gameEntity.name = (jsonData.optString(GameConstants.KEY_GAME_NAME));
                        gameEntity.is_admin = jsonData.optInt(GameConstants.KEY_GAME_IS_ADMIN);
                        if (gameEntity.play_mode == GameConstants.PLAY_MODE_PINEAPPLE) {
                            PineappleConfig config = new PineappleConfig(jsonData.optInt("play_type"));
                            gameEntity.gameConfig = config;
                        }
                        if (gameEntity.status == GameStatus.GAME_STATUS_FINISH) {
                            Toast.makeText(ChessApp.sAppContext, getString(R.string.game_finished), Toast.LENGTH_SHORT).show();
                            if (mGameRequestCallback != null) {
                                mGameRequestCallback.onFailed(ApiCode.CODE_GAME_NONE_EXISTENT, json);
                            }
                        } else if (gameMode == GameConstants.GAME_MODE_NORMAL || gameMode == GameConstants.GAME_MODE_SNG) {
                            DialogMaker.dismissProgressDialog();
                            enterFreeRoom(gameEntity, joinWay);
                        } else if (gameMode == GameConstants.GAME_MODE_MT_SNG || gameMode == GameConstants.GAME_MODE_MTT) {
                            //其他赛事，那么先进入聊天室
                            String roomId = jsonData.optString(GameConstants.KEY_ROOM_ID);
                            MatchRoomActivity.startWithWebsockUrl(mActivity, null, serverIp, code, gameEntity, websocketUrl, roomId);
                        }
                    } else {
                        DialogMaker.dismissProgressDialog();
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onFailed(resultCode, json);
                        }
                        if (resultCode == ApiCode.CODE_GAME_NONE_EXISTENT || resultCode == ApiCode.CODE_MATCH_CHECKIN_FAILURE_CHANNEL_NOT_FOUND) {
                            if (joinWay != UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_CODE) {
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
                    DialogMaker.dismissProgressDialog();
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
                LogUtil.i(TAG, error == null ? "VolleyError==null" : error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestJoinGameUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    private void enterFreeRoom(final GameEntity gameEntity, final String joinWay) {
        if (gameEntity.is_admin != 1 && gameEntity.status == GameStatus.GAME_STATUS_START) {
            // : 17/3/9普通玩家（不是管理员）并且游戏已经开始那么直接进入游戏
            EnterChatRoomData data = new EnterChatRoomData(gameEntity.room_id);
            data.setNick(NimUserInfoCache.getInstance().getUserDisplayName(UserPreferences.getInstance(mActivity).getUserId()));
            enterRequest = NIMClient.getService(ChatRoomService.class).enterChatRoom(data);
            enterRequest.setCallback(new RequestCallback<EnterChatRoomResultData>() {
                @Override
                public void onSuccess(EnterChatRoomResultData result) {
                    ChatRoomInfo roomInfo = result.getRoomInfo();
                    String jsonStr = new JSONObject(roomInfo.getExtension()).toString();
                    LogUtil.i(TAG, "获取云信扩展字段" + jsonStr);
                    onLoginDone();
                    LogUtil.i(TAG, result.toString());
                }
                @Override
                public void onFailed(int code) {
                    LogUtil.e(TAG, "云信进入聊天室失败, code=" + code);
                    if(code == 302){ //云信异常...
                        android.widget.Toast.makeText(ChessApp.sAppContext, R.string.game_join_failure, android.widget.Toast.LENGTH_SHORT).show();
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
                    int errorCode = NIMClient.getService(ChatRoomService.class).getEnterErrorCode(gameEntity.room_id);
                    android.widget.Toast.makeText(ChessApp.sAppContext, "云信进入聊天室失败errorCode=" + errorCode, android.widget.Toast.LENGTH_SHORT).show();
                    onLoginDone();
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
                    Toast.makeText(ChessApp.sAppContext, "enter chat room exception, e=" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            PokerActivity.startGameByPlay(mActivity, gameEntity, gameEntity.room_id, 0);//不管有没有成功进入聊天室，都直接进游戏
        } else {//否则进入大厅
            FreeRoomAC.startByJoin(mActivity, gameEntity, joinWay);
        }
    }

    private AbortableFuture<EnterChatRoomResultData> enterRequest;

    String requestStartFreeGameUrl;
    public void startFreeGame(String gameCode, final VolleyCallback volleyCallback) {//开始自由局游戏
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("code", gameCode);
        requestStartFreeGameUrl = getHost() + ApiConstants.URL_GAME_START_GAME + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestStartFreeGameUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestStartFreeGameUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response.toString());
                if (volleyCallback != null) {
                    volleyCallback.onResponse(response);
                }
                DialogMaker.dismissProgressDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                if (volleyCallback != null) {
                    volleyCallback.onErrorResponse(error);
                }
                Toast.makeText(ChessApp.sAppContext, getString(R.string.game_dismiss_failure), Toast.LENGTH_SHORT).show();
                if (error != null && error.getMessage() != null) {
                    LogUtil.i(TAG, error.getMessage());
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestStartFreeGameUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    private void onLoginDone() {
        enterRequest = null;
        DialogMaker.dismissProgressDialog();
    }

    private String requestCancelUrl;
    public void dismissFreeGame(final  String teamId , final String gameCode, final VolleyCallback vollCallback) {
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("code", gameCode);
        LogUtil.i(TAG, paramsMap.toString());
        requestCancelUrl = getHost() + ApiConstants.URL_GAME_CANCEL + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestCancelUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestCancelUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                if (vollCallback != null) {
                    vollCallback.onResponse(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(ChessApp.sAppContext, getString(R.string.game_dismiss_failure), Toast.LENGTH_SHORT).show();
                if (error != null && error.getMessage() != null) {
                    LogUtil.i(TAG, error.getMessage());
                }
                if (vollCallback != null) {
                    vollCallback.onErrorResponse(error);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestCancelUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    String requestMttRemark;
    public void mttRemark(final boolean post, final String code, String content, String pic_list, final GameRequestCallback mGameRequestCallback) {//比赛说明，只有"金币赛"和"钻石赛"才有这个东西
        if (!checkNetWork()) {
            if (mGameRequestCallback != null) {
                mGameRequestCallback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
            }
            return;
        }
        if (post) {
            DialogMaker.showProgressDialog(mActivity, "", false);
        }
        if (StringUtil.isSpace(content)) {
            content = " ";
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("code", code);
        if (post) {
            paramsMap.put("content", content);
            paramsMap.put("pic_list", pic_list);
        }
        requestMttRemark = getHost() + ApiConstants.URL_GAME_MATCH_MTT_REMARK + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestMttRemark);
        SignStringRequest signRequest = new SignStringRequest(post ? Request.Method.POST : Request.Method.GET, requestMttRemark, new Response.Listener<String>() {
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
                } catch (Exception e) {
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
                    mGameRequestCallback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestMttRemark);
        ChessApp.sRequestQueue.add(signRequest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAll(requestJoinGameUrl);
        cancelAll(requestStartGameUrl);
        cancelAll(requestStartFreeGameUrl);
        cancelAll(requestCancelUrl);
        cancelAll(requestMttRemark);
    }
}
