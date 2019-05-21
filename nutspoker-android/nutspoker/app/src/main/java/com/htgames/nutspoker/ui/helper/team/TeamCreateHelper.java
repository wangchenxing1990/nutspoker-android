package com.htgames.nutspoker.ui.helper.team;

import android.app.Activity;
import android.content.Context;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.session.SessionHelper;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.constant.VerifyTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 20150726 on 2015/10/19.
 */
public class TeamCreateHelper {
    private static final String TAG = TeamCreateHelper.class.getSimpleName();

    // 是否演示创建高级群成功后，立即往群中插入一条自定义消息，使得该群聊能立即进入最近联系人列表（会话列表）中
    private static boolean SEND_CUSTOM_MESSAGE_AFTER_CREATE_ADVANCED_TEAM = false;

    /**
     * 创建普通群
     */
    public static void createNormalTeam(final Context context, List<String> memberAccounts, final boolean isNeedBack, final RequestCallback<Void> callback) {
//        String teamName = "普通群";
        String teamName = "";
        DialogMaker.showProgressDialog(context, context.getString(com.netease.nim.uikit.R.string.empty), true);
        // 创建群
        HashMap<TeamFieldEnum, Serializable> fields = new HashMap<TeamFieldEnum, Serializable>();
        fields.put(TeamFieldEnum.Name, teamName);
        NIMClient.getService(TeamService.class).createTeam(fields, TeamTypeEnum.Normal, "",
                memberAccounts).setCallback(
                new RequestCallback<Team>() {
                    @Override
                    public void onSuccess(Team team) {
                        DialogMaker.dismissProgressDialog();
                        TeamDataCache.getInstance().addOrUpdateTeam(team);
                        Toast.makeText(DemoCache.getContext(), com.netease.nim.uikit.R.string.create_team_success, Toast.LENGTH_SHORT).show();
                        if (isNeedBack) {
//                            SessionHelper.startTeamSession(context, team.getId(), MainActivity.class); // 进入创建的群
//                            SessionHelper.startTeamSession(context, team.getId());
                            MainActivity.startSession((Activity)context , team.getId() , MainActivity.TYPE_SESSION_TEAM);
                        } else {
                            SessionHelper.startTeamSession(context, team.getId());
                        }
                        if (callback != null) {
                            callback.onSuccess(null);
                        }
                    }

                    @Override
                    public void onFailed(int code) {
                        DialogMaker.dismissProgressDialog();
                        if (code == 801) {
                            String tip = context.getString(com.netease.nim.uikit.R.string.over_team_member_capacity, ClubConstant.getGroupMemberLimit());
                            Toast.makeText(DemoCache.getContext(), tip,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DemoCache.getContext(), com.netease.nim.uikit.R.string.create_team_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                        LogUtil.e(TAG, "create team error: " + code);
                    }

                    @Override
                    public void onException(Throwable exception) {
                        DialogMaker.dismissProgressDialog();
                    }
                }
        );
    }

    /**
     * 创建普通群组
     * @param teamName
     * @param accounts
     * @param requestCallback
     */
    public static void creteNormalTeam(String teamName , List<String> accounts , RequestCallback<Team> requestCallback) {
        // 群组类型
        TeamTypeEnum type = TeamTypeEnum.Normal;
        // 创建时可以预设群组的一些相关属性，如果是普通群，仅群名有效。
        // fields中，key为数据字段，value对对应的值，该值类型必须和field中定义的fieldType一致
        HashMap<TeamFieldEnum, Serializable> fields = new HashMap<TeamFieldEnum, Serializable>();
        fields.put(TeamFieldEnum.Name, teamName);
        NIMClient.getService(TeamService.class).createTeam(fields, type, "", accounts).setCallback(requestCallback);
    }

    /**
     * 创建高级群组,设置群上限
     * @param teamName
     * @param teamIntroduce
     * @param verifyType
     * @param accounts
     * @param requestCallback
     */
    public static void creteAdvancedTeam(String teamName, String teamIntroduce, VerifyTypeEnum verifyType, List<String> accounts , RequestCallback<Team> requestCallback) {
        // 群组类型
        TeamTypeEnum type = TeamTypeEnum.Advanced;
        // 创建时可以预设群组的一些相关属性，如果是普通群，仅群名有效。
        // fields中，key为数据字段，value对对应的值，该值类型必须和field中定义的fieldType一致
        HashMap<TeamFieldEnum, Serializable> fields = new HashMap<TeamFieldEnum, Serializable>();
        fields.put(TeamFieldEnum.Name, teamName);
        fields.put(TeamFieldEnum.Introduce, teamIntroduce);
        fields.put(TeamFieldEnum.VerifyType, verifyType);
        NIMClient.getService(TeamService.class).createTeam(fields, type, "", accounts).setCallback(requestCallback);
    }

    public static void creteAdvancedTeam(String teamName, String teamIntroduce, String teamExtension ,VerifyTypeEnum verifyType, List<String> accounts , RequestCallback<Team> requestCallback) {
        // 群组类型
        TeamTypeEnum type = TeamTypeEnum.Advanced;
        // 创建时可以预设群组的一些相关属性，如果是普通群，仅群名有效。
        // fields中，key为数据字段，value对对应的值，该值类型必须和field中定义的fieldType一致
        HashMap<TeamFieldEnum, Serializable> fields = new HashMap<TeamFieldEnum, Serializable>();
        fields.put(TeamFieldEnum.Name, teamName);
        fields.put(TeamFieldEnum.Introduce, teamIntroduce);
        fields.put(TeamFieldEnum.Extension, teamExtension);
        fields.put(TeamFieldEnum.VerifyType, verifyType);
        NIMClient.getService(TeamService.class).createTeam(fields, type, "", accounts).setCallback(requestCallback);
    }
}
