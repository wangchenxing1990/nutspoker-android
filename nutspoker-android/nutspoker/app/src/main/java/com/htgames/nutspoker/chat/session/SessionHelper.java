package com.htgames.nutspoker.chat.session;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.game.model.GameStatus;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.contact.activity.UserProfileAC;
import com.htgames.nutspoker.chat.session.action.GameCreateAction;
import com.htgames.nutspoker.chat.session.activity.MessageInfoActivity;
import com.htgames.nutspoker.chat.session.activity.P2PMessageActivity;
import com.htgames.nutspoker.chat.session.activity.TeamMessageAC;
import com.htgames.nutspoker.chat.session.activity.TeamMessageActivity;
import com.htgames.nutspoker.chat.session.extension.BillAttachment;
import com.htgames.nutspoker.chat.session.extension.CustomAttachParser;
import com.htgames.nutspoker.chat.session.extension.GameAttachment;
import com.htgames.nutspoker.chat.session.extension.GuessAttachment;
import com.htgames.nutspoker.chat.session.extension.PaipuAttachment;
import com.htgames.nutspoker.chat.session.viewholder.MsgVHGame;
import com.htgames.nutspoker.chat.session.viewholder.MsgViewHolderBill;
import com.htgames.nutspoker.chat.session.viewholder.MsgViewHolderGuess;
import com.htgames.nutspoker.chat.session.viewholder.MsgViewHolderPaipu;
import com.htgames.nutspoker.chat.session.viewholder.MsgViewHolderTip;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.game.match.activity.FreeRoomAC;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalyticsEventConstants;
import com.htgames.nutspoker.ui.activity.Club.ClubInfoActivity;
import com.htgames.nutspoker.ui.activity.Club.GroupInfoActivity;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.session.SessionCustomization;
import com.netease.nim.uikit.session.SessionEventListener;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nim.uikit.session.actions.ImageAction;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * UIKit自定义消息界面用法展示类
 */
public class SessionHelper {
    private final static String TAG = "SessionHelper";
    private static final int ACTION_HISTORY_QUERY = 0;
    private static final int ACTION_SEARCH_MESSAGE = 1;
    private static final int ACTION_CLEAR_MESSAGE = 2;
    //
    private static SessionCustomization p2pCustomization;

    private static SessionCustomization teamCustomization;

    private static SessionCustomization gameCustomization;

    //    private static SessionCustomization myP2pCustomization;

    public static void init() {
        // 注册自定义消息附件解析器
        NIMClient.getService(MsgService.class).registerCustomAttachmentParser(new CustomAttachParser());
        // 注册各种扩展消息类型的显示ViewHolder
        registerViewHolders();
        // 设置会话中点击事件响应处理
        setSessionListener();
    }

    //
    public static void startP2PSession(Context context, String account) {
        if (!DemoCache.getAccount().equals(account)) {
            P2PMessageActivity.start(context, account, getP2pCustomization());
        } else {
            P2PMessageActivity.start(context, account, getP2pCustomization());
        }
    }

    public static void startP2PSession(Context context, String account , int newMessageNum) {
        if (!DemoCache.getAccount().equals(account)) {
            P2PMessageActivity.start(context, account, newMessageNum ,getP2pCustomization());
        } else {
            P2PMessageActivity.start(context, account, newMessageNum ,getP2pCustomization());
        }
    }

    public static void startTeamSession(Context context, String tid){
        startTeamSession(context , tid , null);
    }

    //带未读消息数量
    public static void startTeamSession(Context context, String tid , int newMessageNum){
        TeamMessageActivity.start(context, tid, newMessageNum , getTeamCustomization(tid));
    }

    //
    public static void startTeamSession(Context context, String tid , String serverIp) {
        LogUtil.i(TAG , "startTeamSession:" + tid);
        if(TeamDataCache.getInstance().getTeamById(tid) != null){
            String extServer = TeamDataCache.getInstance().getTeamById(tid).getExtServer();
            LogUtil.i("extension" , "extServer : " + extServer);
            int gameType = GameConstants.GAME_TYPE_CLUB;
            int gameStatus = GameStatus.GAME_STATUS_WAIT;
            String gid = null;
            String code = null;
            String gName = "";
            if(!TextUtils.isEmpty(extServer)){
                try {
                    JSONObject extensionJson = new JSONObject(extServer);
                    gameType = extensionJson.optInt(GameConstants.KEY_GAME_TYPE);
                    gameStatus = extensionJson.optInt(GameConstants.KEY_GAME_STATUS);
                    gid = extensionJson.optString(GameConstants.KEY_GAME_GID);
                    gName = extensionJson.optString(GameConstants.KEY_GAME_NAME);
                    code = extensionJson.optString(GameConstants.KEY_GAME_CODE);
                    //
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(gameType == GameConstants.GAME_TYPE_GAME){
                    if(gameStatus == GameStatus.GAME_STATUS_WAIT){
                        startGameTeamSession(context, tid ,serverIp);
                        return;
                    }else if(gameStatus == GameStatus.GAME_STATUS_START){
                        if(context instanceof MainActivity && !TextUtils.isEmpty(gid)){
                            String joinWay = UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_GROUP;
                            if(!TextUtils.isEmpty(tid) && TeamDataCache.getInstance().getTeamById(tid) != null){
                                if(TeamDataCache.getInstance().getTeamById(tid).getType() == TeamTypeEnum.Advanced){
                                    joinWay = UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_CLUB;
                                }
                            }
                            GameEntity gameEntity = new GameEntity();
                            gameEntity.tid = (tid);
                            gameEntity.gid = (gid);
                            gameEntity.code = (code);
                            gameEntity.name = (gName);
                            gameEntity.type = (gameType);
                            ((MainActivity) context).doGameJoin(joinWay,gameEntity, true, null);//可能有问题
                            return;
                        }else{
                            Toast.makeText(context, R.string.game_join_failure, Toast.LENGTH_SHORT).show();
                        }
                    }else if(gameStatus == GameStatus.GAME_STATUS_FINISH){
                        Toast.makeText(context , R.string.game_finished , Toast.LENGTH_SHORT).show();
                    }
                    return;
                } else {
//                    NimUIKit.startChatting(context, tid, SessionTypeEnum.Team, getTeamCustomization());
//                    TeamMessageActivity.start(context, tid, getTeamCustomization(tid));
                    TeamMessageAC.start(context, tid, getTeamCustomization(tid), TeamMessageAC.PAGE_TYPE_PAIJU);// TODO: 16/11/30 页面类型有待调整
                    return;
                }
            }
        }
        TeamMessageActivity.start(context, tid, getTeamCustomization(tid));
//        NimUIKit.startChatting(context, tid, SessionTypeEnum.Team, getTeamCustomization());
    }

    //打开游戏聊天接口
//    public static void startGameTeamSession(Context context, String tid) {
//        GameMessageActivity.start(context, tid, getGameTeamCustomization());
//    }

    public static void startGameTeamSession(Context context, String tid , String serverIp) {
//        GameMessageActivity.start(context, tid, serverIp , getGameTeamCustomization());
        FreeRoomAC.startByJoin((Activity) context, null, UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_GROUP);
    }
//
//    /**
//     * 打开游戏大厅
//     * @param activity
//     * @param roomId
//     * @param hostIp
//     * @param isFinish
//     */
//    public static void startGameMatchRoom(final Activity activity, String roomId, final String hostIp, final boolean isFinish) {
//        EnterChatRoomData data = new EnterChatRoomData(roomId);
//        AbortableFuture<EnterChatRoomResultData> enterRequest = NIMClient.getService(ChatRoomService.class).enterChatRoom(data);
//        if (NIMClient.getStatus() != StatusCode.LOGINED) {
//            DialogMaker.dismissProgressDialog();
//            return;
//        }
//        enterRequest.setCallback(new RequestCallback<EnterChatRoomResultData>() {
//            @Override
//            public void onSuccess(EnterChatRoomResultData result) {
//                ChatRoomInfo roomInfo = result.getRoomInfo();
//                MatchRoomActivity.start(activity, roomInfo, hostIp);
//                if (isFinish) {
//                    DialogMaker.dismissProgressDialog();
//                    activity.finish();
//                }
//            }
//
//            @Override
//            public void onFailed(int code) {
//                // test
////                LogUtil.ui("enter chat room failed, callback code=" + code + ", getErrorCode=" + NIMClient.getService
////                        (ChatRoomService.class).getEnterErrorCode(roomId));
//                DialogMaker.dismissProgressDialog();
////                enterRequest = null;
//                if (code == ResponseCode.RES_CHATROOM_BLACKLIST) {
//                    android.widget.Toast.makeText(DemoCache.getContext(), "你已被拉入黑名单，不能再进入", android.widget.Toast.LENGTH_SHORT).show();
//                } else if (code == ResponseCode.RES_ENONEXIST) {
//                    android.widget.Toast.makeText(DemoCache.getContext(), "聊天室不存在", android.widget.Toast.LENGTH_SHORT).show();
//                } else {
//                    android.widget.Toast.makeText(DemoCache.getContext(), "enter chat room failed, code=" + code, android.widget.Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onException(Throwable exception) {
//                DialogMaker.dismissProgressDialog();
//                android.widget.Toast.makeText(DemoCache.getContext(), "enter chat room exception, e=" + exception.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    //
//    // 定制化单聊界面。如果使用默认界面，返回null即可
    private static SessionCustomization getP2pCustomization() {
        if (p2pCustomization == null) {
            p2pCustomization = new SessionCustomization() {
                // 由于需要Activity Result， 所以重载该函数。
                @Override
                public void onActivityResult(final Activity activity, int requestCode, int resultCode, Intent data) {
                    super.onActivityResult(activity, requestCode, resultCode, data);
                }
            };
//
            // 背景
//            p2pCustomization.backgroundColor = Color.BLUE;
//            p2pCustomization.backgroundUri = "file:///android_asset/xx/bk.jpg";
//            p2pCustomization.backgroundUri = "file:///sdcard/Pictures/bk.png";
//            p2pCustomization.backgroundUri = "android.resource://com.netease.nim.demo/drawable/bk"
//
//            // 定制加号点开后可以包含的操作， 默认已经有图片，视频等消息了
            ArrayList<BaseAction> actions = new ArrayList<>();
            actions.add(new ImageAction());
//            actions.add(new BillAction());// TODO: 16/12/21 这两行功能暂时去掉
//            actions.add(new PaipuAction());
            p2pCustomization.actions = actions;
            p2pCustomization.withSticker = true;
//
            // 定制ActionBar右边的按钮，可以加多个
            ArrayList<SessionCustomization.OptionsButton> buttons = new ArrayList<>();
//            SessionCustomization.OptionsButton cloudMsgButton = new SessionCustomization.OptionsButton() {
//                @Override
//                public void onClick(Context context, String sessionId) {
//                    MessageHistoryActivity.start(context, sessionId, SessionTypeEnum.P2P); // 漫游消息查询
//                }
//            };
//            cloudMsgButton.iconId = R.drawable.nim_ic_messge_history;
//
            SessionCustomization.OptionsButton infoButton = new SessionCustomization.OptionsButton() {
                @Override
                public void onClick(Context context, View view, String sessionId) {
                    //                    NormalTeamInfoActivity.startForCreateNormalTeam(context, sessionId); // 创建群
                    MessageInfoActivity.start(context, sessionId);
                }
            };
//            infoButton.iconId = R.drawable.btn_chat_info;
//            buttons.add(cloudMsgButton);
//            buttons.add(infoButton);
            p2pCustomization.buttons = buttons;
        }
//
        return p2pCustomization;
    }
    public static SessionCustomization getTeamCustomization(String tid) {
        if (teamCustomization == null) {
            teamCustomization = new SessionCustomization() {
                @Override
                public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
//                    if (requestCode == NormalTeamInfoActivity.REQUEST_CODE) {
//                        if (resultCode == Activity.RESULT_OK) {
//                            String reason = data.getStringExtra(NormalTeamInfoActivity.RESULT_EXTRA_REASON);
//                            boolean finish = reason != null && (reason.equals(NormalTeamInfoActivity
//                                    .RESULT_EXTRA_REASON_DISMISS) || reason.equals(NormalTeamInfoActivity.RESULT_EXTRA_REASON_QUIT));
//                            if (finish) {
//                                activity.finish(); // 退出or解散群直接退出多人会话
//                            }
//                        }
//                    }
                }

                @Override
                public MsgAttachment createStickerAttachment(String category, String item) {
//                    return new StickerAttachment(category, item);
                    return null;
                }
            };

            // 定制加号点开后可以包含的操作， 默认已经有图片，视频等消息了
            ArrayList<BaseAction> actions = new ArrayList<>();
            actions.add(new ImageAction());
//            actions.add(new BillAction());// TODO: 16/12/21 以后加上   看388行   以后加上
//            actions.add(new PaipuAction());
//            actions.add(new GameCreateAction());
            teamCustomization.actions = actions;

            // 定制ActionBar右边的按钮，可以加多个
            ArrayList<SessionCustomization.OptionsButton> buttons = new ArrayList<>();
//            SessionCustomization.OptionsButton cloudMsgButton = new SessionCustomization.OptionsButton() {
//                @Override
//                public void onClick(Context context, String sessionId) {
//                    MessageHistoryActivity.start(context, sessionId, SessionTypeEnum.Team); // 群漫游消息查询; // 漫游消息查询
//                }
//            };
//            cloudMsgButton.iconId = R.drawable.nim_ic_messge_history;

            SessionCustomization.OptionsButton infoButton = new SessionCustomization.OptionsButton() {
                @Override
                public void onClick(Context context, View view, String sessionId) {
                    //获取缓存中该TEAM
                    Team team = TeamDataCache.getInstance().getTeamById(sessionId);
                    if (team == null) {
                        return;
                    }
                    if (team.getType() == TeamTypeEnum.Advanced) {
                        ClubInfoActivity.start(context, sessionId, ClubInfoActivity.FROM_TYPE_CLUB); // 启动固定群组资料页
                    } else {
                        GroupInfoActivity.start(context, sessionId); // 启动普通群组资料页
                    }
                }
            };
//            infoButton.iconId = R.drawable.btn_chat_info;
//            buttons.add(cloudMsgButton);
//            buttons.add(infoButton);
//            teamCustomization.buttons = buttons;

            teamCustomization.withSticker = true;
        }
        teamCustomization.actions = getClubActions(tid, teamCustomization.actions,null);
        //是否只能房主开局
        return teamCustomization;
    }

    /**
     * 获取俱乐部的ACTION(根据配置动态变更)
     * @param tid
     * @param actions
     * @return
     */
    public static ArrayList<BaseAction> getClubActions(String tid, ArrayList<BaseAction> actions,TeamMember tm) {
        //是否只能房主开局
        Team team = TeamDataCache.getInstance().getTeamById(tid);
        if (team != null && actions != null) {

            boolean isNeedDelete = false;
            //如果是限制開局
            if(ClubConstant.isClubCreateGameByCreatorLimit(team)) {//限制开局
                if (tm == null) {
                    tm = NIMClient.getService(TeamService.class).queryTeamMemberBlock(tid, DemoCache.getAccount());
                }
                if (tm != null && tm.getType() != TeamMemberType.Owner && tm.getType() != TeamMemberType.Manager) {
                    isNeedDelete = true;
                }
            }

            if(isNeedDelete){
                for (BaseAction baseAction : actions) {
                    if (baseAction instanceof GameCreateAction) {
                        actions.remove(baseAction);
                        break;
                    }
                }
            } else {
                boolean isExistGameCreate = false;
                for (BaseAction baseAction : actions) {
                    if (baseAction instanceof GameCreateAction) {
                        isExistGameCreate = true;
                        break;
                    }
                }
                if (!isExistGameCreate) {
//                    actions.add(new GameCreateAction());// TODO: 16/12/21 看309行   以后加上
                }
            }
        }
        return actions;
    }

    private static SessionCustomization getGameTeamCustomization() {
        if (gameCustomization == null) {
            gameCustomization = new SessionCustomization() {
                @Override
                public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
//                    if (requestCode == TeamRequestCode.REQUEST_CODE) {
//                        if (resultCode == Activity.RESULT_OK) {
//                            String reason = data.getStringExtra(TeamExtras.RESULT_EXTRA_REASON);
//                            boolean finish = reason != null && (reason.equals(TeamExtras
//                                    .RESULT_EXTRA_REASON_DISMISS) || reason.equals(TeamExtras.RESULT_EXTRA_REASON_QUIT));
//                            if (finish) {
//                                activity.finish(); // 退出or解散群直接退出多人会话
//                            }
//                        }
//                    }
                }

                @Override
                public MsgAttachment createStickerAttachment(String category, String item) {
//                    return new StickerAttachment(category, item);
                    return null;
                }
            };

            // 定制加号点开后可以包含的操作， 默认已经有图片，视频等消息了
            ArrayList<BaseAction> actions = new ArrayList<>();
            actions.add(new ImageAction());
//            actions.add(new BillAction());
//            actions.add(new PaipuAction());
//            actions.add(new GameCreateAction());
            gameCustomization.actions = actions;

            // 定制ActionBar右边的按钮，可以加多个
            ArrayList<SessionCustomization.OptionsButton> buttons = new ArrayList<>();
//            SessionCustomization.OptionsButton cloudMsgButton = new SessionCustomization.OptionsButton() {
//                @Override
//                public void onClick(Context context, String sessionId) {
//                    MessageHistoryActivity.start(context, sessionId, SessionTypeEnum.Team); // 群漫游消息查询; // 漫游消息查询
//                }
//            };
//            cloudMsgButton.iconId = R.drawable.nim_ic_messge_history;
            SessionCustomization.OptionsButton infoButton = new SessionCustomization.OptionsButton() {
                @Override
                public void onClick(Context context, View view, String sessionId) {
                    //获取缓存中该TEAM
                    Team team = TeamDataCache.getInstance().getTeamById(sessionId);
                    if (team == null) {
                        return;
                    }
                    if (team.getType() == TeamTypeEnum.Advanced) {
                        ClubInfoActivity.start(context, sessionId, ClubInfoActivity.FROM_TYPE_CLUB); // 启动固定群组资料页
                    } else {
                        GroupInfoActivity.start(context, sessionId); // 启动普通群组资料页
                    }
                }
            };
//            infoButton.iconId = R.drawable.btn_chat_info;
//            buttons.add(cloudMsgButton);
//            buttons.add(infoButton);
            gameCustomization.buttons = null;
            gameCustomization.withSticker = true;
        }
        return gameCustomization;
    }

    //
    private static void registerViewHolders() {
//        NimUIKit.registerMsgItemViewHolder(FileAttachment.class, MsgViewHolderFile.class);
//        NimUIKit.registerMsgItemViewHolder(AVChatAttachment.class, MsgViewHolderAVChat.class);
        NimUIKit.registerMsgItemViewHolder(GuessAttachment.class, MsgViewHolderGuess.class);
        NimUIKit.registerMsgItemViewHolder(BillAttachment.class, MsgViewHolderBill.class);//注册:战绩 消息视图
        NimUIKit.registerMsgItemViewHolder(PaipuAttachment.class, MsgViewHolderPaipu.class);//注册：牌谱 消息视图
        NimUIKit.registerMsgItemViewHolder(GameAttachment.class, MsgVHGame.class);//注册：牌局创建 消息视图
//        NimUIKit.registerMsgItemViewHolder(TipAttachment.class, MsgViewHolderTip.class);//注册游戏消息类型消息视图
        NimUIKit.registerTipMsgViewHolder(MsgViewHolderTip.class);//注册系统消息
//        NimUIKit.registerMsgItemViewHolder(CustomAttachment.class, MsgViewHolderDefCustom.class);
//        NimUIKit.registerMsgItemViewHolder(StickerAttachment.class, MsgViewHolderSticker.class);
//        NimUIKit.registerMsgItemViewHolder(SnapChatAttachment.class, MsgViewHolderSnapChat.class);
//        NimUIKit.registerMsgItemViewHolder(RTSAttachment.class, MsgViewHolderRTSNotification.class);
    }

    private static void setSessionListener() {
        SessionEventListener listener = new SessionEventListener() {
            @Override
            public void onAvatarClicked(Context context, IMMessage message) {
                // 一般用于打开用户资料页面
                if(context instanceof Activity) {
                    //UserProfileActivity.start((Activity)context, message.getFromAccount(), UserProfileActivity.FROM_FRIEND);
                    UserProfileAC.start((Activity)context, message.getFromAccount(), UserProfileAC.FROM_CLUB_CHAT, message.getSessionId(), false);//最后的参数不确定是不是"管理员"，在用户详情页再判断是不是管理员
                }
            }

            @Override
            public void onAvatarLongClicked(Context context, IMMessage message) {
                // 一般用于群组@功能，或者弹出菜单，做拉黑，加好友等功能
            }
        };
        NimUIKit.setSessionListener(listener);
    }
}
