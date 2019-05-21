package com.htgames.nutspoker.ui.base;

import android.os.Bundle;
import android.text.TextUtils;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.data.common.CircleConstant;
import com.htgames.nutspoker.view.ShareToSessionDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nim.uikit.session.helper.MessageListPanelHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.constant.VerifyTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 20150726 on 2015/10/10.
 */
public class BaseTeamActivity extends BaseActivity {
    public static final int FROM_LIST = 0;
//    public static final int FROM_SELECT = 1;
    public int from = FROM_LIST;
    public ShareToSessionDialog mShareToSessionDialog;
    public Object shareObject;
    public int shareType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        from = getIntent().getIntExtra(Extras.EXTRA_FROM , FROM_LIST);
        shareObject = getIntent().getSerializableExtra(Extras.EXTRA_SHARE_DATA);
        shareType = getIntent().getIntExtra(Extras.EXTRA_SHARE_TYPE , CircleConstant.TYPE_PAIJU);
    }

    /**
     * 清除聊天记录
     * @param teamId
     */
    public void doClearCache(final String teamId) {
        EasyAlertDialogHelper.createOkCancelDiolag(this, null, getString(R.string.clear_chat_history_confirm), true, new EasyAlertDialogHelper.OnDialogActionListener() {
            @Override
            public void doCancelAction() {

            }

            @Override
            public void doOkAction() {
                clearChattingHistory(teamId, SessionTypeEnum.Team);
                MessageListPanelHelper.getInstance().notifyClearMessages(teamId);
            }
        }).show();
    }

    /**
     * 删除单条消息
     */
    public void deleteChattingHistory(IMMessage message){
        // 删除单条消息
        NIMClient.getService(MsgService.class).deleteChattingHistory(message);
    }

    /**
     * 删除与某个聊天对象的全部消息记录
     * @param sessionId
     * @param sessionType
     */
    public void clearChattingHistory(String sessionId , SessionTypeEnum sessionType){
        // 删除与某个聊天对象的全部消息记录
        NIMClient.getService(MsgService.class).clearChattingHistory(sessionId, sessionType);
    }

    /**
     * 修改群组资料:根据指定的属性类型修改
     * @param teamId
     * @param teamName        群组名称
     * @param teamIntroduce   群组介绍
     * @param verifyType      群组验证方式
     * @param teamFieldEnum   该值类型必须和field中定义的fieldType一致
     * @param requestCallback 回调接口
     */
    public void updateTeamInfo(String teamId, String teamName, String teamIntroduce, VerifyTypeEnum verifyType, TeamFieldEnum teamFieldEnum, RequestCallback<Void> requestCallback) {
        // 每次仅修改群的一个属性
        // 可修改的属性包括：群名，介绍，公告，验证类型等。
        HashMap<TeamFieldEnum, Serializable> fields = new HashMap<TeamFieldEnum, Serializable>();
        if(!TextUtils.isEmpty(teamName)){
            fields.put(TeamFieldEnum.Name, teamName);
        }
        if(!TextUtils.isEmpty(teamIntroduce)){
            fields.put(TeamFieldEnum.Introduce, teamIntroduce);
        }
        if(verifyType != null){
            fields.put(TeamFieldEnum.VerifyType, verifyType);
        }
        NIMClient.getService(TeamService.class).
                updateTeam(teamId, teamFieldEnum, fields)
                .setCallback(requestCallback);
    }

    public void updateTeamInfo(String teamId, String teamExtension, RequestCallback<Void> requestCallback) {
        // 每次仅修改群的一个属性
        // 可修改的属性包括：群名，介绍，公告，验证类型等。
        NIMClient.getService(TeamService.class).updateTeam(teamId, TeamFieldEnum.Extension, teamExtension).setCallback(requestCallback);
    }

    /**
     * 修改群组资料：可以一次性更新多个字段的值
     * @param teamId
     * @param teamName        群组名称
     * @param teamIntroduce   群组介绍
     * @param verifyType      群组验证方式
     * @param requestCallback 回调接口
     */
    public void updateTeamInfoFields(String teamId, String teamName, String teamIntroduce, VerifyTypeEnum verifyType, RequestCallback<Void> requestCallback) {
        // 可修改的属性包括：群名，介绍，公告，验证类型等。
        HashMap<TeamFieldEnum, Serializable> fields = new HashMap<TeamFieldEnum, Serializable>();
        //该值类型必须和field中定义的fieldType一致
        if(teamName != null){
            fields.put(TeamFieldEnum.Name, teamName);
        }
        if(teamIntroduce != null){
            fields.put(TeamFieldEnum.Introduce, teamIntroduce);
        }
        if(verifyType != null){
            fields.put(TeamFieldEnum.VerifyType, verifyType);
        }
        NIMClient.getService(TeamService.class).
                updateTeamFields(teamId, fields)
                .setCallback(requestCallback);
    }

    /**
     * 主动退群(除拥有者外，其他用户均可以主动退群)
     * 普通群群主可以退群，若退群，该群没有群主。高级群除群主外，其他用户均可以主动退群
     * @param teamId
     * @param requestCallback 回调接口
     */
    public void quitTeam(String teamId, RequestCallback<Void> requestCallback) {
        //退群后，群内所有成员(包括退出者)会收到一条消息类型为 notification 的 IMMessage，附件类型为 MemberChangeAttachment。
        NIMClient.getService(TeamService.class).quitTeam(teamId).setCallback(requestCallback);
    }

    /**
     * 解散群组（普通群和高级群的群主均可以解散群：）
     * @param teamId
     * @param requestCallback
     */
    public void dismissTeam(String teamId , RequestCallback<Void> requestCallback) {
        NIMClient.getService(TeamService.class).dismissTeam(teamId)
                .setCallback(requestCallback);
    }

    /**
     * 拉人入群 (普通群所有人都可以拉人入群，高级群仅管理员和拥有者可以邀请人入群)
     * @param teamId
     * @param accounts
     * @param requestCallback
     */
    public void addMembers(String teamId, List<String> accounts ,RequestCallback<Void> requestCallback) {
        //普通群可直接将用户拉人群聊。 高级群不能直接拉入，被邀请的人会收到一条系统消息 (SystemMessage)，类型为 SystemMessageType#TeamInvite。用户接受该邀请后，才真正入群。
        // 用户入群（普通群被拉人，高级群接受邀请）后，在收到之后的第一条消息时，群内所有成员（包括入群者）会收到一条入群消息，附件类型为 MemberChangeAttachment。
        NIMClient.getService(TeamService.class).addMembers(teamId, accounts)
                .setCallback(requestCallback);
    }

    /**
     * 踢人出群(普通群仅拥有者可以踢人，高级群拥有者和管理员可以踢人，且管理员不能踢拥有者和其他管理员。)
     * @param teamId
     * @param account
     * @param requestCallback
     */
    public void removeMember(String teamId, String account ,RequestCallback<Void> requestCallback) {
        //踢人后，群内所有成员(包括被踢者)会收到一条消息类型为 notification 的 IMMessage，附件类型为 MemberChangeAttachment。
        NIMClient.getService(TeamService.class).removeMember(teamId, account)
                .setCallback(requestCallback);
    }

    /**
     * 查询群成员
     * @param teamId
     */
    public void queryMemberList(String teamId ,RequestCallback<List<TeamMember>> requestCallback) {
        // 该操作有可能只是从本地数据库读取缓存数据，也有可能会从服务器同步新的数据，
        // 因此耗时可能会比较长。
        NIMClient.getService(TeamService.class).queryMemberList(teamId)
                .setCallback(requestCallback);
    }

    /**
     * 查询TEAM的相关信息
     * 群聊消息提醒可以单独打开或关闭，关闭提醒之后，用户仍然会收到这个群的消息。
     * 如果开发者使用的是云信内建的消息提醒，用户收到新消息后不会再用通知栏提醒，
     * 如果用户使用的 iOS 客户端，则他将收不到该群聊消息的 APNS 推送。
     * 如果开发者自行实现状态栏提醒，可通过 Team 的 mute 接口获取提醒配置，并决定是不是要显示通知。 开发者可通过调用一下接口打开或关闭群聊消息提醒：
     * @param teamId
     * @param requestCallback
     */
    public void searchTeam(String teamId , RequestCallback<Team> requestCallback){
        NIMClient.getService(TeamService.class).searchTeam(teamId).setCallback(requestCallback);
    }

    /**
     * 关闭群聊消息提醒
     * @param teamId
     * @param isMute
     */
    public void muteTeamConfig(String teamId , boolean isMute, RequestCallback<Void> requestCallback) {
        NIMClient.getService(TeamService.class).muteTeam(teamId, isMute).setCallback(requestCallback);
    }

    /**
     * 获取群组:所有群的列表的接口
     * @param requestCallbackWrapper
     */
    public void queryTeamList(RequestCallbackWrapper<List<Team>> requestCallbackWrapper){
        NIMClient.getService(TeamService.class).queryTeamList().setCallback(requestCallbackWrapper);
    }

    /**
     * 获取群组:根据群组类型
     * @param type
     * @param requestCallback
     */
    public void queryTeamListByType(TeamTypeEnum type ,RequestCallback<List<Team>> requestCallback){
        NIMClient.getService(TeamService.class).queryTeamListByType(type).setCallback(requestCallback);
        //或者用以下方法
        //List<Team> teams = NIMClient.getService(TeamService.class).queryTeamListBlock();
    }

    /**
     * 拥有者添加管理员
     * @param teamId 群 ID
     * @param accounts 待提升为管理员的用户帐号列表
     * @return InvocationFuture 可以设置回调函数,如果成功，参数为新增的群管理员列表
     */
    public static void addClubMgr(String teamId,List<String> accounts,RequestCallback<List<TeamMember>> callback){
        //操作完成后，群内所有成员都会收到一条消息类型为 notification 的 IMMessage，附件类型为 MemberChangeAttachment
        NIMClient.getService(TeamService.class)
                .addManagers(teamId, accounts)
                .setCallback(callback);
    }


    /**
     * 拥有者撤销管理员权限 <br>
     * @param teamId 群ID
     * @param managers 待撤销的管理员的帐号列表
     * @return InvocationFuture 可以设置回调函数，如果成功，参数为被撤销的群成员列表(权限已被降为Normal)。
     */
    public static void removeClubMgr(String teamId,List<String> managers,RequestCallback<List<TeamMember>> callback){
        //操作完成后，群内所有成员都会收到一条消息类型为 notification 的 IMMessage，附件类型为 MemberChangeAttachment
        NIMClient.getService(TeamService.class)
                .removeManagers(teamId, managers)
                .setCallback(callback);
    }
}
