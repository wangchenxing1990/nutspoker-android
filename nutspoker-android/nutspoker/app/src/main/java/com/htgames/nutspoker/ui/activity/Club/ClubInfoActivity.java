package com.htgames.nutspoker.ui.activity.Club;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.ui.activity.Club.credit.CreditListAC;
import com.htgames.nutspoker.ui.activity.Club.fund.ClubFundAC;
import com.netease.nim.uikit.api.ApiCode;
import com.htgames.nutspoker.chat.contact.activity.UserProfileActivity;
import com.htgames.nutspoker.chat.contact_selector.activity.ContactSelectActivity;
import com.htgames.nutspoker.chat.main.activity.AddClubMember;
import com.htgames.nutspoker.chat.main.activity.AddVerifyActivity;
import com.htgames.nutspoker.chat.msg.activity.MessageVerifyActivity;
import com.htgames.nutspoker.chat.msg.model.SystemMessage;
import com.htgames.nutspoker.chat.region.db.RegionDBTools;
import com.htgames.nutspoker.data.DataManager;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.ui.action.ClubAction;
import com.htgames.nutspoker.ui.action.EditClubInfoAction;
import com.htgames.nutspoker.ui.action.HordeAction;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.htgames.nutspoker.ui.activity.horde.HordeAC;
import com.htgames.nutspoker.ui.activity.horde.util.HordeUpdateManager;
import com.htgames.nutspoker.ui.activity.horde.util.UpdateItem;
import com.htgames.nutspoker.ui.adapter.team.TeamMemberAdapter;
import com.htgames.nutspoker.ui.base.BaseTeamActivity;
import com.htgames.nutspoker.view.ShareView;
import com.htgames.nutspoker.view.TeamInfoGridView;
import com.htgames.nutspoker.view.switchbutton.SwitchButton;
import com.htgames.nutspoker.view.widget.ObservableScrollView;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.api.ApiConstants;
import com.netease.nim.uikit.api.HttpApi;
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.bean.TeamMemberItem;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.chesscircle.entity.HordeEntity;
import com.netease.nim.uikit.chesscircle.helper.RecentContactHelp;
import com.netease.nim.uikit.common.DateTools;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nim.uikit.team.helper.TeamHelper;
import com.netease.nim.uikit.uinfo.UserInfoHelper;
import com.netease.nim.uikit.uinfo.UserInfoObservable;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * 俱乐部详情页面
 */
public class ClubInfoActivity extends BaseTeamActivity implements View.OnClickListener {
    private final static String TAG = "ClubInfoActivity";
    public static final String RESULT_EXTRA_REASON = "RESULT_EXTRA_REASON";
    public static final String RESULT_EXTRA_REASON_QUIT = "RESULT_EXTRA_REASON_QUIT";
    public static final String RESULT_EXTRA_REASON_DISMISS = "RESULT_EXTRA_REASON_DISMISS";
    private static final int REQUEST_CODE_CONTACT_SELECT = 103;
    public static final int FROM_TYPE_LIST = 1;//可能是"俱乐部"会员        1俱乐部搜索结果 2邀请自己加入俱乐部  3别人加入的俱乐部  4
    public static final int FROM_TYPE_CLUB = 2;//已经是"俱乐部"会员        1俱乐部聊天页
    public static final int FROM_TYPE_MESSAGE_INVITE = 3;//消息界面过来，通过邀请，还不是"俱乐部"会员
    //public static final int FROM_TYPE_TEAMMSG_REJECT = 4;//俱乐部拒绝邀请
    private int mFromType = FROM_TYPE_LIST;
    private String teamId;
    RelativeLayout rl_club_info;
    LinearLayout ll_club_info_members;
    LinearLayout ll_club_info_introduce;
    RelativeLayout ll_club_info_newmessage_notify;
    LinearLayout ll_club_info_clear_record;
    Button tv_club_info_remove;
    TextView tv_club_info_name;
    TextView tv_club_info_member_city;
    TextView tv_club_info_member_count;
    TextView tv_club_info_create_time;
    TextView tv_club_info_creator;
    Button btn_club_operate;
    HeadImageView iv_club_info_head;
    HeadImageView iv_club_creator_userhead;
    ObservableScrollView mObservableScrollView;
    private Team team;
    //是不是群管理员
    private boolean isSelfManager = false;
    //是不是群创建者
    private boolean isSelfCreator = false;
    //是不是群成员
    private boolean isSelfMember = false;
    private TeamInfoGridView gridView;
    private Set<String> memberAccounts = new HashSet<>();
    //群成员
    public static List<TeamMember> teamMembers;
    //private List<String> managerList;
    private List<TeamMemberItem> dataSource;
    TeamMemberAdapter mTeamMemberAdapter;
    public static String creator;
    private static final int TEAM_MEMBERS_SHOW_LIMIT = 5;
    SwitchButton switch_newmessage_notify;
    private TextView tv_club_info_card;
    private TextView tv_club_info_introduce;
    private View me_edit_userinfo_iv;
    private UserInfoObservable.UserInfoObserver userInfoObserver;
    private Drawable mArrowDrawable;
    /**
     * 是否不接受新消息提醒
     */
    boolean isMute = false;
    RelativeLayout rl_head;
    SystemMessage mSystemMessage;
    /**
     * 消息是否处理
     */
    boolean isMessageDeal = false;
    ClubAction mClubAction;
    HordeAction mHordeAction;
    ArrayList<HordeEntity> mHordeList = new ArrayList<>();
    private String mTotalFund = "-1";
    @BindView(R.id.rl_set_club_manager) View rl_set_club_manager;
    @BindView(R.id.rl_create_game_limit) View rl_create_game_limit;
    @BindView(R.id.rl_search_club_limit) View rl_search_club_limit;
    @BindView(R.id.switch_just_creator_create) SwitchButton switch_just_creator_create;
    @BindView(R.id.switch_is_private) SwitchButton switch_is_private;
    @BindView(R.id.tv_horde_num) TextView tv_horde_num;
    @BindView(R.id.tv_fund_num) TextView tv_fund_num;//基金
    @BindView(R.id.rl_fund) View rl_fund;//基金
    //信用分
    @BindView(R.id.rl_credit) View rl_credit;
    @BindView(R.id.tv_credit_up) TextView tv_credit_up;
    @BindView(R.id.tv_credit_right) TextView tv_credit_right;
    public static void start(Context context, String tid, int fromType) {
        Intent intent = new Intent();
        intent.putExtra(Extras.EXTRA_TEAM_ID, tid);
        intent.putExtra(Extras.EXTRA_FROM, fromType);
        intent.setClass(context, ClubInfoActivity.class);
        context.startActivity(intent);
    }

    public static void start(Context context, Team team, int fromType, ArrayList<HordeEntity> costList) {
        Intent intent = new Intent();
        intent.putExtra(Extras.EXTRA_TEAM_DATA, team);
        intent.putExtra(Extras.EXTRA_FROM, fromType);
        intent.putExtra(Extras.EXTRA_HORDE_COST_LSIT, costList);
        intent.setClass(context, ClubInfoActivity.class);
        context.startActivity(intent);
    }

    public static void start(Context context, String tid, int fromType, SystemMessage mSystemMessage) {
        Intent intent = new Intent();
        intent.putExtra(Extras.EXTRA_TEAM_ID, tid);
        intent.putExtra(Extras.EXTRA_FROM, fromType);
        intent.putExtra(Extras.EXTRA_MESSAGE_DATA, mSystemMessage);
        intent.setClass(context, ClubInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_info);
        mArrowDrawable = getResources().getDrawable(R.mipmap.icon_common_arrow_normal);
        mArrowDrawable.setBounds(0, 0, mArrowDrawable.getIntrinsicWidth(), mArrowDrawable.getIntrinsicHeight());
        mUnbinder = ButterKnife.bind(this);
        mHordeAction = new HordeAction(this, null);
        mClubAction = new ClubAction(this, null);
        mEditClubInfoAction = new EditClubInfoAction(this, null);
        team = (Team) getIntent().getSerializableExtra(Extras.EXTRA_TEAM_DATA);
        if (team == null) {
            teamId = getIntent().getStringExtra(Extras.EXTRA_TEAM_ID);
        } else {
            teamId = team.getId();
            creator = team.getCreator();
        }
        mFromType = getIntent().getIntExtra(Extras.EXTRA_FROM, FROM_TYPE_LIST);
        initHead();
        if (mFromType == FROM_TYPE_MESSAGE_INVITE) {
            mSystemMessage = (SystemMessage) getIntent().getSerializableExtra(Extras.EXTRA_MESSAGE_DATA);
        }
        initView();
        initSwitchButton();
        initMemberList();
        //判断是否是群管理员或者群主
        if (team == null) {
            loadTeamInfo(teamId);
        } else {
            teamId = team.getId();
            updateClubInfo(team);
        }
        requestMembers();//获取群成员
        registerObservers(true);//注册监听
    }

    private void getHordeList() {
        if (mHordeAction == null) {//可能ondestroy导致mHordeAction为null
            return;
        }
        mHordeAction.hordeList(teamId, new GameRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {//{"code":0,"message":"ok","data":[{"id":"4","horde_vid":"3939732","name":"dgtf","ctime":"1490010504","tid":"25622697","owner":"10033","is_my":1,"horde_id":"4"}]}
                mHordeList.clear();
                HordeEntity mHordeEntity = null;
                try {
                    JSONArray data = response.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject gameJson = data.getJSONObject(i);
                        HordeEntity hordeEntity = new HordeEntity();
                        hordeEntity.horde_id = gameJson.optString("horde_id");
                        hordeEntity.horde_vid = gameJson.optString("horde_vid");
                        hordeEntity.name = gameJson.optString("name");
                        hordeEntity.ctime = gameJson.optLong("ctime");
                        hordeEntity.tid = gameJson.optString("tid");//创建者的俱乐部id
                        hordeEntity.owner = gameJson.optString("owner");//创建者的uid
                        hordeEntity.is_my = gameJson.optInt("is_my");
                        hordeEntity.is_score = gameJson.optInt("is_score");
                        hordeEntity.playing_count = gameJson.optInt("playing_count");
                        hordeEntity.is_control = gameJson.optInt("is_control");
                        hordeEntity.is_modify = gameJson.optInt("is_modify");
//                        if (!hordeEntity.tid.equals(teamId)) {
                        if (hordeEntity.is_my == 0) {
                            mHordeList.add(hordeEntity);
                        } else {
                            mHordeEntity = hordeEntity;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (mHordeEntity != null) {
                    mHordeList.add(0, mHordeEntity);
                }
                if (tv_horde_num != null) {
                    if (mHordeList == null || mHordeList.size() <= 0) {
                        if (tv_horde_num != null) {
                            tv_horde_num.setText(R.string.horde_join_empty);
                        }
                    } else if (mHordeList.size() == 1) {
                        if (tv_horde_num != null) {
                            tv_horde_num.setText(mHordeList.get(0).name);
                        }
                    } else {
                        if (tv_horde_num != null) {
                            tv_horde_num.setText(getResources().getString(R.string.horde_join_num, mHordeList.size()));
                        }
                    }
                }
            }
            @Override
            public void onFailed(int code, JSONObject response) {
            }
        });
    }

    private void initHead() {
        setHeadTitle(R.string.club_info_head);
        me_edit_userinfo_iv = findViewById(R.id.me_edit_userinfo_iv);
        me_edit_userinfo_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelfCreator && mFromType == FROM_TYPE_CLUB) {
                    Intent intent = new Intent(ClubInfoActivity.this, ClubEditActivity.class);
                    intent.putExtra("team", team);
                    startActivityForResult(intent, REQUEST_EDIT_CLUBINFO);//新版本需求：功能移到headright右上角
                }
            }
        });
    }

    @Override
    protected void onResume() {
//        if(sIsChangeMgr || sIsChangeMember) { //如果在管理、会员列表界面有更新管理员,那么需要更新列表
//            requestMembers();
//            sIsChangeMgr = false;
//            sIsChangeMember = false;
//        }
        super.onResume();
        getHordeList();//onresume
    }

    /**
     * 群消息提醒设置
     */
    private void initSwitchButton() {
        switch_newmessage_notify = (SwitchButton) findViewById(R.id.switch_newmessage_notify);
        if (team != null) {
            switch_newmessage_notify.setCheckedImmediately(team.mute());
        }
        switch_newmessage_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean isChecked = switch_newmessage_notify.isChecked();
                boolean mute = isChecked;
                if (!NetworkUtil.isNetAvailable(getApplicationContext())) {
                    Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
                    switch_newmessage_notify.setChecked(!isChecked);
                    return;
                }
                //是否消息提醒，mute沉默，不开启提醒
                if (mute != isMute) {
                    //未选中为静音
                    muteTeamConfig(teamId, mute, new RequestCallback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            LogUtil.i(TAG, "muteTeam onSuccess");
                            isMute = !isMute;
                        }
                        @Override
                        public void onFailed(int code) {
                            LogUtil.i(TAG, "muteTeam failed code:" + code);
                            switch_newmessage_notify.setChecked(!isChecked);
                        }
                        @Override
                        public void onException(Throwable throwable) {
                            switch_newmessage_notify.setChecked(!isChecked);
                        }
                    });
                }
            }
        });
    }

    /**
     * 初始化成员
     */
    public void initMemberList() {
        teamMembers = new ArrayList<>();
        dataSource = new ArrayList<>();
        mTeamMemberAdapter = new TeamMemberAdapter(this, (ArrayList) dataSource, TeamTypeEnum.Advanced, null, new TeamMemberAdapter.AddMemberCallback() {
            @Override
            public void onAddMember() {
                if (team.getMemberCount() >= ClubConstant.getClubMemberLimit(team)) {
//                   Toast.makeText(getApplicationContext() , R.string.club_invite_member_count_limit , Toast.LENGTH_SHORT).show();
                    showMemberLimitDialog();
                } else {
//                    List<String> tmpAccounts = new ArrayList<>();
//                    tmpAccounts.addAll(memberAccounts);
//                    ContactSelectActivity.Option option = ContactSelectHelper.getContactSelectOption(tmpAccounts, ClubConstant.getClubMemberLimit(team));
//                    option.maxSelectedTip = getString(R.string.club_invite_member_count_limit);
//                    ContactSelectHelper.startContactSelect(ClubInfoActivity.this, option, REQUEST_CODE_CONTACT_SELECT);  by 20161127，把"邀请俱乐部成员"页面改为搜索页面
//                    Intent toSearchIntent = new Intent(ClubInfoActivity.this, SearchActivity.class);
//                    toSearchIntent.putExtra(Extras.EXTRA_TEAM_DATA, team);
//                    startActivity(new Intent(toSearchIntent));
                    AddClubMember.Companion.start(ClubInfoActivity.this, team);
                }
            }
        }, new TeamMemberAdapter.TeamMemberHolderEventListener() {
            @Override
            public void onHeadImageViewClick(String account) {
                //进成员管理界面
                ll_club_info_members.performClick();
            }
        });
        gridView.setSelector(R.color.transparent);
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 0) {
                    mTeamMemberAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        gridView.setAdapter(mTeamMemberAdapter);
    }

    private void updateTeamMember(final List<TeamMember> m) {
        if (m != null && m.isEmpty()) {
            return;
        }
        //updateTeamBusinessCard(m);
        addTeamMembers(m, true);
    }

    /**
     * 更新我的群名片
     */
    private void updateTeamBusinessCard(List<TeamMember> m) {
//        for (TeamMember teamMember : m) {
//            if (teamMember.getAccount().equals(DemoCache.getAccount())) {
//                teamBusinessCard.setText(teamMember.getTeamNick() != null ? teamMember.getTeamNick() : "");
//            }
//        }
    }

    /**
     * 添加群成员到列表
     *
     * @param m     群成员列表
     * @param clear 是否清除
     */
    private synchronized void addTeamMembers(final List<TeamMember> m, boolean clear) {
        if (m == null || m.isEmpty()) {
            return;
        }
        isSelfManager = false;
        isSelfCreator = false;
        if (clear) {
            this.teamMembers.clear();
            this.memberAccounts.clear();
        }
        // add
        if (this.teamMembers.isEmpty()) {
            this.teamMembers.addAll(m);
        } else {
            for (TeamMember tm : m) {
                if (!this.memberAccounts.contains(tm.getAccount())) {
                    this.teamMembers.add(tm);
                }
            }
        }
        // sort
        Collections.sort(this.teamMembers, TeamHelper.teamMemberComparator);
        // accounts, manager, creator
        this.memberAccounts.clear();
        DataManager.getInstance().sManagerList.clear();
        DataManager.getInstance().sMgrList.clear();
        for (TeamMember tm : teamMembers) {
            if (tm.getType() == TeamMemberType.Manager) {
                DataManager.getInstance().sManagerList.add(tm.getAccount());
                DataManager.getInstance().sMgrList.add(tm);
            }
            if (tm.getAccount().equals(DemoCache.getAccount())) {
                if (tm.getType() == TeamMemberType.Manager) {
                    isSelfManager = true;
                } else if (tm.getType() == TeamMemberType.Owner) {
                    isSelfCreator = true;
                    if (isSelfCreator && mFromType == FROM_TYPE_CLUB) {
                        me_edit_userinfo_iv.setVisibility(View.VISIBLE);
                    }
                    creator = DemoCache.getAccount();
                }
            }
            this.memberAccounts.add(tm.getAccount());
        }

        if (isSelfCreator /*|| isSelfManager*/) {
            if (rl_set_club_manager != null) {
                rl_set_club_manager.setVisibility(View.VISIBLE);
            }
        }
        updateTeamMemberDataSource();//addTeamMembers
        if (tv_horde_num != null) {
            tv_horde_num.setCompoundDrawablesWithIntrinsicBounds(null, null, (isSelfCreator || isSelfManager) ? mArrowDrawable : null, null);
        }
    }

    /**
     * 更新成员信息
     */
    private synchronized void updateTeamMemberDataSource() {
        if (teamMembers.size() > 0 && (mFromType == FROM_TYPE_CLUB || (mFromType == FROM_TYPE_LIST && isSelfMember))) {
            gridView.setVisibility(View.VISIBLE);
            ll_club_info_members.setVisibility(View.VISIBLE);
        } else {
            gridView.setVisibility(View.GONE);
            ll_club_info_members.setVisibility(View.GONE);
            return;
        }
        dataSource.clear();
        // member item
        int count = 0;
        String identity = null;
        for (TeamMember account : teamMembers) {
            if (account.getAccount().equals(DemoCache.getAccount())) {
                isSelfManager = account.getType() == TeamMemberType.Manager;
                isSelfCreator = account.getType() == TeamMemberType.Owner;
            }
            int limit = TEAM_MEMBERS_SHOW_LIMIT;
            if (isSelfCreator || isSelfManager) {
//                limit = TEAM_MEMBERS_SHOW_LIMIT - 1;//邀请成员的功能去掉20170802
            }
            if (count < limit) {
                if (account.getType() == TeamMemberType.Owner) {
                    identity = TeamMemberItem.OWNER;
                } else if (account.getType() == TeamMemberType.Manager) {
                    identity = TeamMemberItem.ADMIN;
                } else {
                    identity = null;
                }
                dataSource.add(new TeamMemberItem(TeamMemberItem.TeamMemberItemTag.NORMAL, teamId, account.getAccount(), identity));
            }
            count++;
        }
        // add item
        if (isSelfCreator || isSelfManager) {
//            dataSource.add(new TeamMemberItem(TeamMemberItem.TeamMemberItemTag.ADD, null, null, null));//邀请成员的功能去掉20170802
        }
        // refresh
        mTeamMemberAdapter.notifyDataSetChanged();
//        memberCountText.setText(String.format("共%d人", count));
        if (rl_fund == null) {
            rl_fund = findViewById(R.id.rl_fund);
        }
        rl_fund.setOnClickListener(this);
        rl_fund.setVisibility(isSelfCreator || isSelfManager ? View.VISIBLE : View.GONE);
        setCreditInfo();
    }

    private void setCreditInfo() {
        if (rl_credit == null) {
            rl_credit = findViewById(R.id.rl_credit);
            tv_credit_up = (TextView) findViewById(R.id.tv_credit_up);
            tv_credit_right = (TextView) findViewById(R.id.tv_credit_right);
        }
        rl_credit.setVisibility(View.GONE);//rl_credit.setVisibility(isSelfMember ? View.VISIBLE : View.GONE);
        rl_credit.setOnClickListener(isSelfCreator || isSelfManager ? this : null);
        tv_credit_up.setText(isSelfCreator || isSelfManager ? R.string.credit : R.string.my_credit);
//        tv_credit_right.setText(isSelfCreator || isSelfManager ? "" : "0");
        tv_credit_right.setCompoundDrawables(null, null, isSelfCreator || isSelfManager ? mArrowDrawable : null, null);
        if (mClubAction != null) {
            mClubAction.creditMyScore(teamId, DemoCache.getAccount(), new GameRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    if (response != null && response.has("data")) {
                        if (tv_credit_right != null) {
                            tv_credit_right.setText(isSelfCreator || isSelfManager ? "" : ("" + response.optInt("data")));
                        }
                    }
                }
                @Override
                public void onFailed(int code, JSONObject response) {
                }
            });
        }
    }

    private synchronized void requestMembers() {
        TeamDataCache.getInstance().fetchTeamMemberList(teamId, new SimpleCallback<List<TeamMember>>() {
            @Override
            public void onResult(boolean success, List<TeamMember> members) {
                if (success) {
                    updateTeamMember(members);
                    NimUserInfoCache.getInstance().getUserListByNeteaseEx(new ArrayList(memberAccounts), 0, null);
                }
            }
        });
    }

    private void initView() {
        rl_set_club_manager.setOnClickListener(this);
        rl_head = (RelativeLayout) findViewById(R.id.rl_head);
        mObservableScrollView = (ObservableScrollView) findViewById(R.id.mObservableScrollView);
        setObservableScrollViewListener(mObservableScrollView, rl_head);
        //
        rl_club_info = (RelativeLayout) findViewById(R.id.rl_club_info);
        ll_club_info_members = (LinearLayout) findViewById(R.id.ll_club_info_members);
        ll_club_info_introduce = (LinearLayout) findViewById(R.id.ll_club_info_introduce);
        ll_club_info_newmessage_notify = (RelativeLayout) findViewById(R.id.ll_club_info_newmessage_notify);
        ll_club_info_clear_record = (LinearLayout) findViewById(R.id.ll_club_info_clear_record);
        ll_club_info_clear_record.setOnClickListener(this);
        ll_club_info_members.setOnClickListener(this);
        iv_club_info_head = (HeadImageView) findViewById(R.id.iv_club_info_head);
        //
        tv_club_info_remove = (Button) findViewById(R.id.tv_club_info_remove);
        tv_club_info_remove.setOnClickListener(this);
        iv_club_creator_userhead = (HeadImageView) findViewById(R.id.iv_club_creator_userhead);
        tv_club_info_name = (TextView) findViewById(R.id.tv_club_info_name);
        tv_club_info_introduce = (TextView) findViewById(R.id.tv_club_info_introduce);
        tv_club_info_member_count = (TextView) findViewById(R.id.tv_club_info_member_count);
        tv_club_info_member_city = (TextView) findViewById(R.id.tv_club_info_member_city);
        tv_club_info_creator = (TextView) findViewById(R.id.tv_club_info_creator);
        tv_club_info_create_time = (TextView) findViewById(R.id.tv_club_info_create_time);
        tv_club_info_card = (TextView) findViewById(R.id.tv_club_info_card);
        btn_club_operate = (Button) findViewById(R.id.btn_club_operate);
        btn_club_operate.setVisibility(View.GONE);
        iv_club_creator_userhead.setOnClickListener(this);
        btn_club_operate.setOnClickListener(this);
        findViewById(R.id.rl_horde).setOnClickListener(this);
        gridView = (TeamInfoGridView) findViewById(R.id.team_member_grid_view);
//        gridView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    ClubMemberAC.start(ClubInfoActivity.this, teamId);
//                    return true;
//                }
//                return false;
//            }
//        });
    }

    /**
     * 初始化群组基本信息
     *
     * @param teamId
     */
    public void loadTeamInfo(String teamId) {
        Team t = TeamDataCache.getInstance().getTeamById(teamId);
        if (t != null) {
            updateClubInfo(t);
        } else {
            if (!NetworkUtil.isNetAvailable(getApplicationContext())) {
                showGetInfoFailureDialog();
                return;
            }
            DialogMaker.showProgressDialog(this, getString(R.string.get_ing), false).setCanceledOnTouchOutside(false);
            searchTeam(teamId, new RequestCallback<Team>() {
                @Override
                public void onSuccess(Team team) {
                    DialogMaker.dismissProgressDialog();
                    TeamDataCache.getInstance().addOrUpdateTeam(team);
                    updateClubInfo(team);
                }

                @Override
                public void onFailed(int code) {
                    DialogMaker.dismissProgressDialog();
                    if (code == ResponseCode.RES_ETIMEOUT) {

                    }
                    showGetInfoFailureDialog();
                    LogUtil.i(TAG , getString(R.string.team_not_exist) + ", errorMsg=" + code);
                }

                @Override
                public void onException(Throwable throwable) {
                    DialogMaker.dismissProgressDialog();
                    LogUtil.i(TAG, getString(R.string.team_not_exist) + ", errorMsg=" + throwable.getMessage());
                    showGetInfoFailureDialog();
                }
            });
        }
    }

    EasyAlertDialog infoFailureDialog = null;

    public void showGetInfoFailureDialog() {
        if (infoFailureDialog == null) {
            infoFailureDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, getString(R.string.prompt), getString(R.string.get_teaminfo_failure),
                    getString(R.string.retry), getString(R.string.cancel), false, new EasyAlertDialogHelper.OnDialogActionListener() {
                        @Override
                        public void doCancelAction() {
                            finish();
                        }

                        @Override
                        public void doOkAction() {
                            loadTeamInfo(teamId);
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            infoFailureDialog.show();
        }
    }

    public void updateClubInfo(Team team) {
        if (isFinishing()) {
            return;
        }
        this.team = team;
        creator = team.getCreator();
        if (creator.equals(DemoCache.getAccount())) {
            isSelfCreator = true;
            if (isSelfCreator && mFromType == FROM_TYPE_CLUB) {
                me_edit_userinfo_iv.setVisibility(View.VISIBLE);
            }
            isSelfMember = true;
        } else {
            //判断是不是群成员
            if (team.isMyTeam()) {
                isSelfMember = true;
            } else {
                isSelfMember = false;
            }
        }
        teamId = team.getId();
        btn_club_operate.setVisibility(View.VISIBLE);
        iv_club_creator_userhead.loadBuddyAvatar(creator);//头像
        tv_club_info_creator.setText(NimUserInfoCache.getInstance().getUserDisplayName(creator));//this.tvNickname.setText(NimUserInfoCache.getInstance().getUserDisplayName(recent.getFromAccount())
        String createTime = String.valueOf(team.getCreateTime()).substring(0, 10);
        tv_club_info_create_time.setText(String.format(getString(R.string.club_info_create_time), DateTools.getStrTime_ymd(createTime)));
        tv_club_info_name.setText(team.getName());
        if (TextUtils.isEmpty(team.getIntroduce()) || ClubConstant.GROUP_IOS_DEFAULT_NAME.equals(team.getIntroduce())) {
            tv_club_info_introduce.setText(R.string.club_introduce_null);
            tv_club_info_introduce.setVisibility(View.GONE);
        } else {
            tv_club_info_introduce.setVisibility(View.VISIBLE);
            tv_club_info_introduce.setText(team.getIntroduce());
        }
        //基金
        mTotalFund = ClubConstant.getClubFund(team);
        if (tv_fund_num == null) {
            tv_fund_num = (TextView) findViewById(R.id.tv_fund_num);
        }
        tv_fund_num.setText(mTotalFund);
        //区域
        String extServer = team.getExtServer();
        int areaId = ClubConstant.getClubExtAreaId(extServer);
        if (areaId != 0) {
            String area = RegionDBTools.getShowRegionContent(Integer.valueOf(areaId), " | ");
            tv_club_info_member_city.setText(area);
        }
        tv_club_info_card.setText("俱乐部ID: " + ClubConstant.getClubVirtualId(team.getId(), extServer));//改成俱乐部虚拟ID
        //俱乐部头像
        String avatar = ClubConstant.getClubExtAvatar(extServer);
        iv_club_info_head.loadClubAvatarByUrl(team.getId(), avatar, HeadImageView.DEFAULT_AVATAR_THUMB_SIZE);
        //所在成员
        tv_club_info_member_count.setText(team.getMemberCount() + "/" + ClubConstant.getClubMemberLimit(team));
        LogUtil.i(TAG , "mute : " + team.mute());
        //设置对应的UI
        if (mFromType == FROM_TYPE_LIST || mFromType == FROM_TYPE_MESSAGE_INVITE) {
            //列表进来：进入俱乐部/申请加入
            tv_club_info_remove.setVisibility(View.GONE);
            if (!isSelfMember) {
                //不是群成员，申请加入
                ll_club_info_members.setVisibility(View.GONE);
                ll_club_info_newmessage_notify.setVisibility(View.GONE);
                ll_club_info_clear_record.setVisibility(View.GONE);
                //
                if (mFromType == FROM_TYPE_MESSAGE_INVITE) {
                    btn_club_operate.setText(R.string.club_info_operate_agree_inviter);
                } else {
                    btn_club_operate.setText(R.string.club_info_operate_apply);
                }
                btn_club_operate.setVisibility(View.VISIBLE);
                btn_club_operate.setBackgroundResource(R.drawable.bg_login_btn);
            } else {
                //是群成员，进入俱乐部
                ll_club_info_members.setVisibility(View.VISIBLE);
                ll_club_info_newmessage_notify.setVisibility(View.VISIBLE);
                ll_club_info_clear_record.setVisibility(View.VISIBLE);
                //
                btn_club_operate.setVisibility(View.VISIBLE);
                btn_club_operate.setText(R.string.club_info_operate_enter);
                btn_club_operate.setBackgroundResource(R.drawable.bg_login_btn);
            }
        } else if (mFromType == FROM_TYPE_CLUB) {
            if (isSelfCreator) {
                tv_club_info_remove.setVisibility(View.VISIBLE);
                rl_club_info.setOnClickListener(this);
                ll_club_info_introduce.setOnClickListener(this);
                tv_club_info_remove.setText(R.string.club_info_head_operate_dismiss);
            } else {
                tv_club_info_remove.setVisibility(View.VISIBLE);
                tv_club_info_remove.setText(R.string.club_info_head_operate_quit);
            }
            //聊天进来：退出俱乐部
            ll_club_info_members.setVisibility(View.VISIBLE);
            ll_club_info_newmessage_notify.setVisibility(View.VISIBLE);
            ll_club_info_clear_record.setVisibility(View.VISIBLE);
            btn_club_operate.setText(R.string.club_info_operate_quit);
            btn_club_operate.setBackgroundResource(R.drawable.bg_login_btn_warning);
            btn_club_operate.setVisibility(View.GONE);
        }
        //
        isMute = team.mute();
        switch_newmessage_notify.setCheckedImmediately(isMute);
        if (isSelfCreator && this.team != null && !hasSetClubConfig) {
            rl_create_game_limit.setVisibility(View.VISIBLE);
            rl_search_club_limit.setVisibility(View.VISIBLE);
            switch_just_creator_create.setCheckedImmediately(ClubConstant.isClubCreateGameByCreatorLimit(this.team));
//            switch_just_creator_create.setOnCheckedChangeListener(clubCreateConfigChangeListener);
            switch_just_creator_create.setOnClickListener(this);
            switch_is_private.setCheckedImmediately(ClubConstant.isClubPrivate(this.team));
//            switch_is_private.setOnCheckedChangeListener(clubSearchConfigChangeListener);
            switch_is_private.setOnClickListener(this);
            hasSetClubConfig = true;
        }
        if (tv_horde_num != null) {
            tv_horde_num.setCompoundDrawablesWithIntrinsicBounds(null, null, (isSelfCreator || isSelfManager) ? mArrowDrawable : null, null);
        }
        getHordeList();//updateclubinfo
    }

    private boolean hasSetClubConfig = false;
    EditClubInfoAction mEditClubInfoAction;
    public void updateClubConfig(final int actionType) {
        mEditClubInfoAction.updateClubConfig(team.getId(), switch_is_private.isChecked(),
                switch_just_creator_create.isChecked(), new com.htgames.nutspoker.interfaces.RequestCallback() {
                    @Override
                    public void onResult(int code, String result, Throwable var3) {
                        if (code == 0) {
                        } else {
                            if (actionType == 0) {
                                switch_just_creator_create.setChecked(!switch_just_creator_create.isChecked());
                            } else if (actionType == 1) {
                                switch_is_private.setChecked(!switch_is_private.isChecked());
                            }
                        }
                    }
                    @Override
                    public void onFailed() {
                        if (actionType == 0) {
                            switch_just_creator_create.setChecked(!switch_just_creator_create.isChecked());
                        } else if (actionType == 1) {
                            switch_is_private.setChecked(!switch_is_private.isChecked());
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_just_creator_create:
                updateClubConfig(0);//0表示开局限制
                break;
            case R.id.switch_is_private:
                updateClubConfig(1);//1表示搜索限制
                break;
            case R.id.btn_club_operate:
                if (mFromType == FROM_TYPE_LIST) {
                    if (!isSelfMember) {
                        //不是群成员，并且俱乐部成员没达到上限,申请加入
                        AddVerifyActivity.start(ClubInfoActivity.this, teamId, AddVerifyActivity.TYPE_VERIFY_CLUB);
                    } else {
                        //聊天
                        MainActivity.startSession(ClubInfoActivity.this, teamId, MainActivity.TYPE_SESSION_TEAM);
                    }
                } else if (mFromType == FROM_TYPE_CLUB) {
                    showConfirmDialog(isSelfCreator);
                } else if (mFromType == FROM_TYPE_MESSAGE_INVITE) {
                    if (!isSelfMember) {
                        doAgreeTeamInvite();
                    } else {
                        //聊天
                        MainActivity.startSession(ClubInfoActivity.this, teamId, MainActivity.TYPE_SESSION_TEAM);
                    }
                }
                break;
            case R.id.ll_club_info_members:
                ClubMemberACNew.start(ClubInfoActivity.this, teamId);
                break;
            case R.id.ll_club_info_clear_record:
                doClearCache(teamId);
                break;
            case R.id.rl_club_info:
                if (isSelfCreator && mFromType == FROM_TYPE_CLUB) {
                    Intent intent = new Intent(this, ClubEditActivity.class);
                    intent.putExtra("team", team);
//                    startActivityForResult(intent, REQUEST_EDIT_CLUBINFO);新版本需求：功能移到headright右上角
                }
                break;
            case R.id.tv_club_info_remove:
                if (mFromType == FROM_TYPE_CLUB) {
                    showConfirmDialog(isSelfCreator);
                }
                break;
            case R.id.ll_club_info_introduce:
                if (isSelfCreator && mFromType == FROM_TYPE_CLUB) {
                    Intent intent = new Intent(this, ClubEditActivity.class);
                    intent.putExtra("team", team);
//                    startActivityForResult(intent, REQUEST_EDIT_CLUBINFO);新版本需求：功能移到headright右上角
                }
                break;
            case R.id.iv_club_creator_userhead:
                UserProfileActivity.start(this, creator);
                break;
            case R.id.rl_horde:
                if (isSelfCreator || isSelfManager) {
                    ArrayList<HordeEntity> costList = (ArrayList<HordeEntity>) getIntent().getSerializableExtra(Extras.EXTRA_HORDE_COST_LSIT);
                    HordeAC.start(ClubInfoActivity.this, teamId, isSelfCreator, isSelfManager, mHordeList, costList);
                } else {
                    Toast.makeText(this, "只有部长和管理员才能进入部落大厅", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rl_set_club_manager:
                ManagerSetActivity.Start(this, team);
                break;
            case R.id.rl_fund:
                if (mTotalFund.equals("-1")) {
                    return;
                }
                if (isSelfCreator || isSelfManager) {
                    ClubFundAC.Companion.start(this, team, mTotalFund);
                } else {
                    android.widget.Toast.makeText(this, "只有部长和管理员才能基金页面", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rl_credit:
                if (isSelfCreator || isSelfManager) {
                    CreditListAC.Companion.start(this, team);
                }
                break;
        }
    }

    public void doAgreeTeamInvite() {
        //不是群成员，并且俱乐部成员没有达到上限，通过邀请
        if (!NetworkUtil.isNetAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(this, null, false).setCanceledOnTouchOutside(false);
        TeamDataCache.getInstance().searchTeamById(mSystemMessage.getTargetId(), new SimpleCallback<Team>() {
            @Override
            public void onResult(boolean success, Team team) {
                DialogMaker.dismissProgressDialog();
                if (success) {
                    if (ClubConstant.isClubMemberIsFull(team)) {
                        Toast.makeText(getApplicationContext(), R.string.club_member_count_limit, Toast.LENGTH_SHORT).show();
                    } else {
                        acceptClubInvite(true);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.taem_invite_agree_failure, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    EasyAlertDialog actionDialog;

    public void showConfirmDialog(final boolean isCreator) {
//        String title = getString(R.string.quit_club_dialog_title);
        String message = getString(R.string.quit_club_dialog_message);
        if (isCreator) {
            if (ClubConstant.getClubMemberLimit(team) > ClubConstant.CLUB_MEMBER_LIMIT) {
                //购买过俱乐部上限
                message = getString(R.string.dismiss_club_club_upgrade_prompt);
            } else {
                message = getString(R.string.dismiss_club_dialog_message);
            }
        }
        actionDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, null, message, true,
                new EasyAlertDialogHelper.OnDialogActionListener() {

                    @Override
                    public void doCancelAction() {
                    }

                    @Override
                    public void doOkAction() {
                        if (isCreator) {
                            doDismissTeam();
                        } else {
                            doQuitTeam();
                        }
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            actionDialog.show();
        }
    }

    EasyAlertDialog showLimitDialog;

    public void showMemberLimitDialog() {
        showLimitDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, null, getString(R.string.club_invite_limit_dialog_message),
                true, new EasyAlertDialogHelper.OnDialogActionListener() {

                    @Override
                    public void doCancelAction() {

                    }

                    @Override
                    public void doOkAction() {
                        ClubMemberLimitActivity.start(ClubInfoActivity.this, teamId);
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            showLimitDialog.show();
        }
    }

    /**
     * 解散俱乐部：通过APP服务器
     */
    public void doDismissTeam() {
        mClubAction.dismissTeam(teamId, new com.htgames.nutspoker.interfaces.RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if (code == 0) {
                    getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            RecentContactHelp.deleteRecentContact(teamId, SessionTypeEnum.Team, true);
                            MainActivity.start(ClubInfoActivity.this);//打开主页
                            finish();
                        }
                    }, 200L);
                } else if (code == ApiCode.CODE_DISMISS_CLUB_WITH_HORDE) {
                    showHasHordeDialog();
                }
            }

            @Override
            public void onFailed() {

            }
        });
    }

    EasyAlertDialog hasHordeDialog;
    private void showHasHordeDialog() {
        if (hasHordeDialog == null) {
            hasHordeDialog = EasyAlertDialogHelper.createOneButtonDiolag(this, "",
                    "俱乐部中存在部落，请先解散部落", getString(R.string.ok), true,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            hasHordeDialog.show();
        }
    }

    /**
     * 非群主退出群
     */
    public void doQuitTeam() {
        HttpApi.Builder builder = new HttpApi.Builder()
                .methodPost(ApiConstants.METHOD_LEAVE_TEAM)
                .mapParams(NetWork.getRequestCommonParams())
                .param("tid", teamId);
        HttpApi.GetNetCodeObservable(builder
                , new Action0() {
                    @Override
                    public void call() {
                        DialogMaker.showProgressDialog(ClubInfoActivity.this, getString(R.string.quit_club_ing), true);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        DialogMaker.dismissProgressDialog();
                        if (integer != 0)
                            Toast.makeText(getApplicationContext(), R.string.quit_club_failed, Toast.LENGTH_SHORT).show();
                        else {
                            Toast.makeText(getApplicationContext(), R.string.quit_club_success, Toast.LENGTH_SHORT).show();

                            getHandler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    RecentContactHelp.deleteRecentContact(teamId, SessionTypeEnum.Team, true);
                                    MainActivity.start(ClubInfoActivity.this);//打开主页
                                    finish();
                                }
                            }, 200L);
                        }
                    }
                });
    }

    /**
     * 邀请群成员
     *
     * @param accounts 邀请帐号
     */
    private void inviteMembers(ArrayList<String> accounts) {
        NIMClient.getService(TeamService.class).addMembers(teamId, accounts).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                Toast.makeText(getApplicationContext(), R.string.club_invite_members_success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int code) {
                if (code == 801) {
                    Toast.makeText(getApplicationContext(), R.string.club_invite_member_count_limit, Toast.LENGTH_SHORT).show();
                } else if (code == 810) {
                    //邀请成功
                    Toast.makeText(getApplicationContext(), R.string.club_invite_members_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.club_invite_members_failed, Toast.LENGTH_SHORT).show();
                }
                LogUtil.e(TAG, "invite teamembers failed, code=" + code);
            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }

    /**
     * 判断是否是群成员
     *
     * @return
     */
    private boolean hasPermission() {
        if (isSelfCreator || isSelfManager) {
            return true;
        } else {
            Toast.makeText(this, R.string.no_permission, Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void registerObservers(boolean register) {
        if (register) {
            TeamDataCache.getInstance().registerTeamMemberDataChangedObserver(teamMemberObserver);
            TeamDataCache.getInstance().registerTeamDataChangedObserver(teamDataObserver);
            HordeUpdateManager.getInstance().registerCallback(hordeUpdateCallback);
        } else {
            TeamDataCache.getInstance().unregisterTeamMemberDataChangedObserver(teamMemberObserver);
            TeamDataCache.getInstance().unregisterTeamDataChangedObserver(teamDataObserver);
            HordeUpdateManager.getInstance().unregisterCallback(hordeUpdateCallback);
        }
        registerUserInfoChangedObserver(register);
    }

    TeamDataCache.TeamMemberDataChangedObserver teamMemberObserver = new TeamDataCache.TeamMemberDataChangedObserver() {
        @Override
        public void onUpdateTeamMember(List<TeamMember> members) {
            boolean needUpdate = false;
            for(TeamMember teamMember : members){
                if(teamId.equals(teamMember.getTid())){
                    //新增会员
                    if(!memberAccounts.contains(teamMember.getAccount())) {
                        memberAccounts.add(teamMember.getAccount());
                        teamMembers.add(teamMember);
                    } else {
                        //信息更新
                        int i = 0;
                        for(TeamMember currentMember: teamMembers){
                            if(currentMember.getAccount().equals(teamMember.getAccount())){
                                teamMembers.set(i,teamMember);
                                break;
                            }
                            i++;
                        }
                    }
                    needUpdate = true;
                }
            }
            updateMgrList();
            if(needUpdate) {
                Collections.sort(teamMembers, TeamHelper.teamMemberComparator);
                updateTeamMemberDataSource();//teamMemberObserver
            }
            if (tv_horde_num != null) {
                tv_horde_num.setCompoundDrawablesWithIntrinsicBounds(null, null, (isSelfCreator || isSelfManager) ? mArrowDrawable : null, null);
            }
        }

        @Override
        public void onRemoveTeamMember(TeamMember member) {
            String account = member.getAccount();
            if (memberAccounts.contains(account)) {
                memberAccounts.remove(account);
                for (TeamMember currentMember : teamMembers) {
                    if (currentMember.getAccount().equals(account)) {
                        teamMembers.remove(currentMember);
                        break;
                    }
                }
            }
            updateMgrList();
            updateTeamMemberDataSource();//onRemoveTeamMember
        }
    };

    void updateMgrList(){
        DataManager.getInstance().sManagerList.clear();
        DataManager.getInstance().sMgrList.clear();
        for(TeamMember tm : teamMembers){
            if(tm.getType() == TeamMemberType.Manager){
                DataManager.getInstance().sManagerList.add(tm.getAccount());
                DataManager.getInstance().sMgrList.add(tm);
            }
        }
    }

    TeamDataCache.TeamDataChangedObserver teamDataObserver = new TeamDataCache.TeamDataChangedObserver() {
        @Override
        public void onUpdateTeams(List<Team> teams) {
            for (Team team : teams) {
                if (team.getId().equals(teamId)) {
                    updateClubInfo(team);
                    break;
                }
            }
        }

        @Override
        public void onRemoveTeam(Team team) {
            if (team.getId().equals(teamId)) {
                ClubInfoActivity.this.team = team;
            }
            finish();
        }
    };

    private void registerUserInfoChangedObserver(boolean register) {
        if (register) {
            if (userInfoObserver == null) {
                userInfoObserver = new UserInfoObservable.UserInfoObserver() {
                    @Override
                    public void onUserInfoChanged(List<String> accounts) {
                        for (String account : accounts) {
                            //判断是不是群主，是的话刷新
                            if (account.equals(creator)) {
                                iv_club_creator_userhead.loadBuddyAvatar(creator);//头像
                                tv_club_info_creator.setText(NimUserInfoCache.getInstance().getUserDisplayName(creator));
                                break;
                            }
                        }
                        mTeamMemberAdapter.notifyDataSetChanged();
                    }
                };
            }
            UserInfoHelper.registerObserver(userInfoObserver);
        } else {
            UserInfoHelper.unregisterObserver(userInfoObserver);
        }
    }

    ShareView mShareView;

    /**
     * 调用postShare分享。跳转至分享编辑页，然后再分享。</br> [注意]<li>
     * 对于新浪，豆瓣，人人，腾讯微博跳转到分享编辑页，其他平台直接跳转到对应的客户端
     */
    public void postShare() {
        if (mShareView == null) {
            mShareView = new ShareView(ClubInfoActivity.this);
        }
        mShareView.show();
    }

    @Override
    protected void onDestroy() {
        registerObservers(false);
        if (mShareView != null) {
            mShareView.onDestroy();
            mShareView = null;
        }
        if (mClubAction != null) {
            mClubAction.onDestroy();
            mClubAction = null;
        }
        if (mEditClubInfoAction != null) {
            mEditClubInfoAction.onDestroy();
            mEditClubInfoAction = null;
        }
        if (mHordeAction != null) {
            mHordeAction.onDestroy();
            mHordeAction = null;
        }
        super.onDestroy();
    }

    public final static int REQUEST_EDIT_CLUBINFO = 1;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            loadTeamInfo(teamId);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mShareView != null) {
            mShareView.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == REQUEST_EDIT_CLUBINFO && resultCode == Activity.RESULT_OK) {
            loadTeamInfo(teamId);
        }
        if (requestCode == REQUEST_CODE_CONTACT_SELECT && resultCode == Activity.RESULT_OK) {
            final ArrayList<String> selected = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
            if (selected != null && !selected.isEmpty()) {
                inviteMembers(selected);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 通过俱乐部邀请
     *
     * @param pass
     */
    public void acceptClubInvite(boolean pass){
        LogUtil.i(TAG , "acceptClubInvite");
        if (mSystemMessage != null && mSystemMessage.getType() == SystemMessageType.TeamInvite) {
            LogUtil.i(TAG , "验证入群邀请");
            //验证入群邀请
            if (pass) {
                DialogMaker.showProgressDialog(this, getString(com.netease.nim.uikit.R.string.empty), false);
                // 接受邀请
                NIMClient.getService(TeamService.class).acceptInvite(mSystemMessage.getTargetId(), mSystemMessage.getFromAccount()).setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DialogMaker.dismissProgressDialog();
                        isMessageDeal = true;
                        Intent intentSuccess = new Intent(MessageVerifyActivity.ACTION_MESSAGE_DEAL);
                        intentSuccess.putExtra(Extras.EXTRA_MESSAGE_DATA, mSystemMessage);
                        sendBroadcast(intentSuccess);
//                        //获取群成员
//                        requestMembers();
                        finish();
                    }

                    @Override
                    public void onFailed(int i) {
                        DialogMaker.dismissProgressDialog();
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        DialogMaker.dismissProgressDialog();
                    }
                });
            }
        }
    }

    HordeUpdateManager.HordeUpdateCallback hordeUpdateCallback = new HordeUpdateManager.HordeUpdateCallback() {
        @Override
        public void onUpdated(UpdateItem item) {
            if (tv_horde_num == null || item == null) {
                return;
            }
            if (item.updateType == UpdateItem.UPDATE_TYPE_CREATE_HORDE) {
                getHordeList();//hordeUpdateCallback
                return;
            }
            if (mHordeList != null && mHordeList.size() > 0) {
                for (int i = 0; i < mHordeList.size(); i++) {
                    HordeEntity hordeEntity = mHordeList.get(i);
                    if (hordeEntity.horde_id.equals(item.horde_id)) {
                        if (item.updateType == UpdateItem.UPDATE_TYPE_NAME && !StringUtil.isSpace(item.name)) {
                            hordeEntity.name = item.name;
                        }
                        if (item.updateType == UpdateItem.UPDATE_TYPE_IS_CONTROL) {
                            hordeEntity.is_control = item.is_control;//这个不需要更新adapter
                        }
                        if (item.updateType == UpdateItem.UPDATE_TYPE_CANCEL_HORDE) {
                            mHordeList.remove(hordeEntity);
                        }
                        if (item.updateType == UpdateItem.UPDATE_TYPE_IS_SCORE) {
                            hordeEntity.is_score = item.is_score;
                        }
                        break;
                    }
                }
                if (mHordeList == null || mHordeList.size() <= 0) {
                    if (tv_horde_num != null) {
                        tv_horde_num.setText(R.string.horde_join_empty);
                    }
                } else if (mHordeList.size() == 1) {
                    if (tv_horde_num != null) {
                        tv_horde_num.setText(mHordeList.get(0).name);
                    }
                } else {
                    if (tv_horde_num != null) {
                        tv_horde_num.setText(getResources().getString(R.string.horde_join_num, mHordeList.size()));
                    }
                }
            }
        }
    };
}
