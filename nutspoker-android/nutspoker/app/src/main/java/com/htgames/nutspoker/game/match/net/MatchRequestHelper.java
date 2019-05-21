package com.htgames.nutspoker.game.match.net;

import com.google.protobuf.ByteString;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.game.match.Match;
import com.htgames.nutspoker.game.match.activity.MatchRoomActivity;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;

import org.java_websocket.client.WebSocketClient;

/**
 */
public class MatchRequestHelper {
    private final String TAG = "MatchRequestHelper";
    public final static String KEY_CMD = "cmd";
    public final static String KEY_DATA = "data";
    public final static String KEY_TYPE = "type";
    public final static String KEY_CODE = "code";
    public final static String KEY_END = "end";//标记位，代表是否已经发送完毕
    public final static String KEY_WORD = "word";//搜索的关键字
    public final static String KEY_SEQUENCE = "sequence";//请求号
    public final static String KEY_PLAYER_MORE = "more";//是否点击"更多"按钮标记
    //
    public final static String TYPE_PLAYER_PLAYER = "0";
    public final static String TYPE_PLAYER_RANK = "1";
    //
    public final static String REQUEST_MY_STATUS = "MyStatus";//牌局状态-------------------MatchStatusEntity
    public final static String REQUEST_USER_LIST = "UserList";//用户列表-------------------MatchPlayerEntity
    public final static String REQUEST_TABLE_LIST = "TableList";//桌子列表-----------------MatchTableEntity
    public final static String REQUEST_SEARCH_PLAYER = "userSearch";//用户搜索-------------MatchPlayerEntity
    public final static String REQUEST_GET_TIME = "GetTime";//校时------------------------long
    public final static String RESULT_PUSHMSG = "PushMSG";//大厅广播消息--------------------MatchPushMsgEntity
    //
    public final static short CMD_ID_USER = 100;//这个cmd的请求和回调是同一个，不加1，其余的回调cmd加1
    public final static short HEART_BEAT_NTF      = CMD_ID_USER;
    public final static short MATCH_SELF_STATUS_REQ = CMD_ID_USER + 1;//牌局状态-------------------MatchStatusEntity
    public final static short MATCH_SELF_STATUS_REP = CMD_ID_USER + 2;

    public final static short MATCH_USERLIST_REQ   = CMD_ID_USER + 3;//用户列表-------------------MatchPlayerEntity     0名次排序   1猎头排序   2身价排序  3赏金排序
    public final static short MATCH_USERLIST_REP   = CMD_ID_USER + 4;

    public final static short MATCH_SEARCH_PLAYER_REQ = CMD_ID_USER + 5;//搜索玩家-------------
    public final static short MATCH_SEARCH_PLAYER_REP = CMD_ID_USER + 6;//

    public final static short MATCH_USER_INFO_REQ = CMD_ID_USER + 7;//增量更新玩家列表
    public final static short MATCH_USERINFO_REP = CMD_ID_USER + 8;

    public final static short MATCH_TABLELIST_REQ = CMD_ID_USER + 9;//桌子列表-----------------MatchTableEntity
    public final static short MATCH_TABLELIST_REP = CMD_ID_USER + 10;

    public final static short MATCH_CHECKIN_NTF = CMD_ID_USER + 11;//玩家报名信息更改

    public final static short MATCH_START_NTF = CMD_ID_USER + 12;//房主手动提前开赛

    public final static short MATCH_INR_ESTT_NTF = CMD_ID_USER + 13;//mtt比赛暂停

    public boolean isRequestingPlayerList = false;//是否在请求玩家列表，以前没有这个变量，是根据sequence区分的，现在只要不回包(isRequestingPlayerList=true)的话，就一直不请求玩家列表

    WebSocketClient mWebSocketClient;
    public boolean hasOnStop = false;
    public MatchRequestHelper(WebSocketClient mWebSocketClient, String code) {
        this.mWebSocketClient = mWebSocketClient;
    }

    public boolean isConnectionOpen() {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            return false;
        }
        if (mWebSocketClient != null && mWebSocketClient.getConnection() != null && mWebSocketClient.getConnection().isOpen()) {
            return true;
        }
        return false;
    }

    public void sendBytesMsg(short cmd, byte[] data) {
        if (hasOnStop) {
            return;
        }
        byte[] sendData = new byte[data.length + 2];

        sendData[0] = (byte) (cmd >> 8);
        sendData[1] = (byte) cmd;

        System.arraycopy(data, 0, sendData, 2, data.length);

        StringBuilder sb = new StringBuilder();
        for (byte b : sendData) {
            sb.append(String.format("0x%02X ", b));
        }
        LogUtil.i(MatchRoomActivity.TAG, "cmd: " + cmd + "    " + sb.toString());
        if (isConnectionOpen()) {
            try {
                mWebSocketClient.send(sendData);
            } catch (Exception e) {
                LogUtil.i(MatchRoomActivity.TAG, "mWebSocketClient.send exception: " + (e == null ? "e==null" : e.toString()));
            }
        }
    }

    public void getMyStatus() {
        if (isConnectionOpen()) {
//            try {
//                JSONObject data = new JSONObject();
//                data.put(KEY_CMD, REQUEST_MY_STATUS);
//                JSONObject dataJson = new JSONObject();
//                dataJson.put(KEY_CODE, gameCode);
//                data.put(KEY_DATA, dataJson);
//                LogUtil.i(TAG, data.toString());
//                mWebSocketClient.send(data.toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            Match.MatchSelfStatusReq rep = Match.MatchSelfStatusReq
                    .newBuilder()
                    .build();
            sendBytesMsg(MATCH_SELF_STATUS_REQ, rep.toByteArray());
        }

    }

    public void getPlayer(int uid, String channel) {
        if (isConnectionOpen()) {
            Match.MatchUserInfoReq req = Match.MatchUserInfoReq
                    .newBuilder()
                    .setUid(uid)
                    .setChannel(channel)
//                    .setSequence(Integer.parseInt(sequence))
                    .build();
            sendBytesMsg(MATCH_USER_INFO_REQ, req.toByteArray());
        }
    }

    /**
     * 获取成员列表
     *
     * @param playerType
     */
    public void getPlayerList(String playerType, int sequence, int rank_type) {
        if (isRequestingPlayerList == true) {
            return;
        }
        if (isConnectionOpen()) {
//            try {
//                JSONObject data = new JSONObject();
//                data.put(KEY_CMD, REQUEST_USER_LIST);
//                JSONObject dataJson = new JSONObject();
//                dataJson.put(KEY_TYPE, playerType);
//                dataJson.put(KEY_CODE, gameCode);
//                dataJson.put(KEY_SEQUENCE, sequence);
//                if (clickMore) {
//                    dataJson.put(KEY_PLAYER_MORE, 1 + "");
//                }
//                data.put(KEY_DATA, dataJson);
//                LogUtil.i(TAG, data.toString());
//                mWebSocketClient.send(data.toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            Match.MatchUserListReq rep = Match.MatchUserListReq
                    .newBuilder()
                    .setRankType(rank_type)
                    .setSequence(sequence)
                    .build();
            sendBytesMsg(MATCH_USERLIST_REQ, rep.toByteArray());
            isRequestingPlayerList = true;
        }
    }

    public void getMgrPlayerList(String channel, int sequence, int rank_type) {
        if (isConnectionOpen()) {
            Match.MatchUserListReq rep = Match.MatchUserListReq
                    .newBuilder()
                    .setChannel(channel)
                    .setRankType(rank_type)
                    .setSequence(sequence)
                    .build();
            sendBytesMsg(MATCH_USERLIST_REQ, rep.toByteArray());
        }
    }

    /**
     * 获取桌子列表
     */
    public void getTableList() {
        if (isConnectionOpen()) {
//            try {
//                JSONObject data = new JSONObject();
//                data.put(KEY_CMD, REQUEST_TABLE_LIST);
//                JSONObject dataJson = new JSONObject();
//                dataJson.put(KEY_CODE, gameCode);
//                data.put(KEY_DATA, dataJson);
//                LogUtil.i(TAG, data.toString());
//                mWebSocketClient.send(data.toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            Match.MatchTableListReq rep = Match.MatchTableListReq
                    .newBuilder()
                    .build();
            sendBytesMsg(MATCH_TABLELIST_REQ, rep.toByteArray());
        }
    }

    /**
     * 搜索用户
     * @param searchKey
     */
    public void searchPlayer(String searchKey) {
        if (isConnectionOpen()) {
//            try {
//                JSONObject data = new JSONObject();
//                data.put(KEY_CMD, REQUEST_SEARCH_PLAYER);
//                JSONObject dataJson = new JSONObject();
//                dataJson.put(KEY_CODE, gameCode);
//                dataJson.put(KEY_WORD, searchKey);
//                data.put(KEY_DATA, dataJson);
//                LogUtil.i(TAG, data.toString());
//                mWebSocketClient.send(data.toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            Match.SearchMatchUserReq rep = Match.SearchMatchUserReq
                    .newBuilder()
                    .setNickname(ByteString.copyFromUtf8(searchKey))
                    .build();
            sendBytesMsg(MATCH_SEARCH_PLAYER_REQ, rep.toByteArray());
        }
    }

    /**
     * 校时
     */
    public void getTime() {
        if (isConnectionOpen()) {
//            try {
//                JSONObject data = new JSONObject();
//                data.put(KEY_CMD, REQUEST_GET_TIME);
//                JSONObject dataJson = new JSONObject();
//                dataJson.put(KEY_CODE, gameCode);
//                data.put(KEY_DATA, dataJson);
//                LogUtil.i(TAG, data.toString());
//                mWebSocketClient.send(data.toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            Match.HeartbeatNtf rep = Match.HeartbeatNtf
                    .newBuilder()
                    .build();
            sendBytesMsg(HEART_BEAT_NTF, rep.toByteArray());
        }
    }
}
