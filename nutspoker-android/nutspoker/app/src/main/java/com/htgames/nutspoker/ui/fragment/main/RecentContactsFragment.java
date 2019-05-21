package com.htgames.nutspoker.ui.fragment.main;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.data.common.UserConstant;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalytics;
import com.htgames.nutspoker.ui.activity.Club.ClubCreateActivity;
import com.htgames.nutspoker.ui.activity.Club.ClubJoinActivity;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.htgames.nutspoker.ui.adapter.team.ClubAdapterRecyc;
import com.htgames.nutspoker.ui.recycler.MeRecyclerView;
import com.netease.nim.uikit.cache.FriendDataCache;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.recent.RecentContactsCallback;
import com.netease.nim.uikit.uinfo.UserInfoHelper;
import com.netease.nim.uikit.uinfo.UserInfoObservable;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 最近联系人列表(会话列表)
 */
public class RecentContactsFragment extends TFragment implements View.OnClickListener {
    //////////////////////////////////////////////////////////
    private boolean msgLoaded = false;
    private RecentContactsCallback callback;
    private UserInfoObservable.UserInfoObserver uinfoObserver;
    //those variavles above are from "通讯"tab，notused now
    //////////////////////////////////////////////////////////

    public boolean msgMute = false;//消息禁止默认为false
    private final String TAG = "RecentContactsFragment";

    ImageView iv_head_message_new;
    FrameLayout ll_club_list_null;
    MeRecyclerView mRecyclerView;
    ClubAdapterRecyc mClubAdapterRecy;
    //没有俱乐部
    Button btn_club_create;
    Button btn_club_join;
    public static int myOwnClubCount = 0;//我创建的俱乐部数量

    PopupWindow clubPopupWindow = null;
    View ll_pop_club_create;
    View ll_pop_club_join;

    public static RecentContactsFragment newInstance() {
        RecentContactsFragment mInstance = new RecentContactsFragment();
        return mInstance;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
//            mClubAdapterRecy.getPaijuDataByTeamId();
        }
    }

    public void getPaijuDataByTeamId() {
        if (mClubAdapterRecy != null) {
            mClubAdapterRecy.getPaijuDataByTeamId();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        requestMessages(true);
//        registerObservers(true);
        //上面的是"通讯"的代码，暂时没有删除
        initHead(mView);
        initView(mView);
        initListView(mView);
        updateClubUI();
        myOwnClubCount = 0;
        for(Team team : ChessApp.teamList) {
            if(!GameConstants.isGmaeClub(team) && team.getCreator().equals(DemoCache.getAccount())) {
                myOwnClubCount++;
            }
        }
        registerObservers(true);
//        registerMessageObserver(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClubAdapterRecy = new ClubAdapterRecyc(getActivity());//提前初始化adapter
        mClubAdapterRecy.setData(ChessApp.teamList);
    }

    private View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_club, container, false);
        mView = view;
        return view;
    }

    private void initHead(View v) {
        Drawable tv_head_right_drawable = getActivity().getResources().getDrawable(R.mipmap.btn_club_more);
        tv_head_right_drawable.setBounds(0, 0, tv_head_right_drawable.getIntrinsicWidth(), tv_head_right_drawable.getIntrinsicHeight());
        v.findViewById(R.id.btn_head_back).setVisibility(View.GONE);
        ((TextView) v.findViewById(R.id.tv_head_title)).setText(R.string.club);//中间题目"俱乐部"
        TextView tv_head_right = ((TextView) v.findViewById(R.id.tv_head_right));//右边"更多"
        tv_head_right.setVisibility(View.VISIBLE);
        tv_head_right.setText("");
        tv_head_right.setCompoundDrawables(tv_head_right_drawable, null, null, null);
        tv_head_right.setOnClickListener(this);
    }

    private void initView(View view) {
        ll_club_list_null = (FrameLayout) view.findViewById(R.id.ll_club_list_null);
        btn_club_create = (Button) view.findViewById(R.id.btn_club_create);
        btn_club_join = (Button) view.findViewById(R.id.btn_club_join);
        iv_head_message_new = ((ImageView) view.findViewById(R.id.iv_head_message_new));
        ll_club_list_null.setVisibility(View.GONE);
        btn_club_create.setOnClickListener(this);
        btn_club_join.setOnClickListener(this);
    }

    private void initListView(View view) {
        mRecyclerView = (MeRecyclerView) mView.findViewById(R.id.mRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mClubAdapterRecy);
        mRecyclerView.setHasFixedSize(true);
    }

    public void chatMsgChanged() {//俱乐部的聊天有新信息
        if (mClubAdapterRecy != null) {
            mClubAdapterRecy.notifyDataSetChanged();
        }
    }

    public void updateClubUI() {
        if(ChessApp.teamList.size() > 0){
            Collections.sort(ChessApp.teamList, recentComp);
        }
        if (mView == null) {
            return;
        }
        mClubAdapterRecy.setData(ChessApp.teamList);
        if (ChessApp.teamList.size() <= 0) {
            mRecyclerView.setVisibility(View.GONE);
//            if(from == FROM_LIST){
                ll_club_list_null.setVisibility(View.VISIBLE);
//            }
        } else {
            ll_club_list_null.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void registerObservers(boolean register) {
        if (register) {
            TeamDataCache.getInstance().registerTeamDataChangedObserver(teamDataChangedObserver); //群列表变化
        } else {
            TeamDataCache.getInstance().unregisterTeamDataChangedObserver(teamDataChangedObserver);
        }
    }

    /**
     * 监听群组资料变化
     * 由于获取群组资料需要跨进程异步调用，开发者最好能在第三方 APP 中做好群组资料缓存，查询群组资料时都从本地缓存中访问。
     * 在群组资料有变化时，SDK 会告诉注册的观察者，此时，第三方 APP 可更新缓存，并刷新界面。
     */
    TeamDataCache.TeamDataChangedObserver teamDataChangedObserver = new TeamDataCache.TeamDataChangedObserver() {
        @Override
        public void onUpdateTeams(List<Team> teams) {
//            adapter.load(true);
//            updateTeamList(teams);这个方法在mainactivity里面已经调用过，不要再调用
            updateClubUI();//先update完数据源再刷新ui
        }
        @Override
        public void onRemoveTeam(Team team) {
            // 创建群组被移除的观察者。在退群，被踢，群被解散时会收到该通知。
            //群被解散后，群内所有成员都会收到一条消息类型为 notification 的 IMMessage，带有一个消息附件，类型为 DismissAttachment，
            // 原群主为该消息的 fromAccount。如果注册了群组被移除的观察者，这个观察者会受到通知。
            for (Team teamItem : ChessApp.teamList) {
                if (teamItem.getId().equals(team.getId())) {
                    ChessApp.teamList.remove(teamItem);
                    if (team.getCreator().equals(DemoCache.getAccount())) {
                        myOwnClubCount--;
                    }
                    break;
                }
            }
            for (Team teamItem : ChessApp.allList) {
                if (teamItem.getId().equals(team.getId())) {
                    ChessApp.allList.remove(teamItem);
                    break;
                }
            }
            updateClubUI();
        }
    };

    /**
     * 显示popWindow
     * */
    private void showClubPopWindow() {
        if(clubPopupWindow == null){
            View popView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_club_add, null);
            clubPopupWindow = new PopupWindow(popView);
            //获取popwindow焦点
            clubPopupWindow.setFocusable(true);
            //设置popwindow如果点击外面区域，便关闭。
            clubPopupWindow.setOutsideTouchable(true);
            clubPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
            clubPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            clubPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            //设置popwindow出现和消失动画
//        clubPopupWindow.setAnimationStyle(R.style.PopMenuAnimation);
            ll_pop_club_create = popView.findViewById(R.id.ll_pop_club_create);
            ll_pop_club_create.setOnClickListener(this);
            ll_pop_club_join = popView.findViewById(R.id.ll_pop_club_join);
            ll_pop_club_join.setOnClickListener(this);
            ((TextView) popView.findViewById(R.id.tv_pop_club_create)).setText(getContext().getResources().getString(R.string.club_create));
            ((TextView) popView.findViewById(R.id.tv_pop_club_join)).setText(getContext().getResources().getString(R.string.club_join));
        }
        clubPopupWindow.showAsDropDown(mView.findViewById(R.id.tv_head_right), -ScreenUtil.dp2px(getActivity(), 9), -ScreenUtil.dp2px(getActivity(), 16));
    }


    public void dissmissClubPopWindow(){
        if(clubPopupWindow != null && clubPopupWindow.isShowing()){
            clubPopupWindow.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        UmengAnalytics.onPageStart("RecentContactsFragment");
        if (mClubAdapterRecy != null) {
            mClubAdapterRecy.getPaijuDataByTeamId();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        UmengAnalytics.onPageEnd("RecentContactsFragment");
    }

    @Override
    public void onDestroy() {
        registerObservers(false);
        if (mClubAdapterRecy != null) {
            mClubAdapterRecy.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mSystemTipView:
                if (getActivity() != null && getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).intentSystemSettings();
                }
                break;
            case R.id.tv_head_right:
                if (clubPopupWindow != null && clubPopupWindow.isShowing()) {
                    dissmissClubPopWindow();
                } else {
                    showClubPopWindow();
                }
                break;
            case R.id.ll_pop_club_create:
//                dissmissClubPopWindow();
                btn_club_create.performClick();
                break;
            case R.id.ll_pop_club_join:
                btn_club_join.performClick();
                break;
            case R.id.btn_club_create:
                dissmissClubPopWindow();
                int ownerClubLimit = UserConstant.getMyCreateClubLimit();
                if (myOwnClubCount >= ownerClubLimit) {
                    showClubNumLimitDialog(ownerClubLimit);
                } else {
                    Intent intent = new Intent(getActivity(), ClubCreateActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.btn_club_join:
                dissmissClubPopWindow();
                startActivity(new Intent(getActivity(), ClubJoinActivity.class));
                break;
        }
    }

    private void showClubNumLimitDialog(int ownerClubLimit) {
        String title = "俱乐部数量已满";
        String message = "只能创建" + ownerClubLimit + "个俱乐部";
        EasyAlertDialogHelper.showOneButtonDiolag(getActivity(), title, message, getString(R.string.ok),
                false, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
    }

    public static Comparator<Team> recentComp = new Comparator<Team>() {
        @Override
        public int compare(Team team1, Team team2) {
            boolean isMyCreat = team1.getCreator().equals(DemoCache.getAccount()) != team2.getCreator().equals(DemoCache.getAccount());
            if(isMyCreat){
                if(team1.getCreator().equals(DemoCache.getAccount())){
                    return -1;
                }else{
                    return 1;
                }
            }
            return 0;
        }
    };


















































    /* -----------------------------------------------------------------------------------------***********************************----------------------------------------------------------------------------------------- */
    /* -----------------------------------------------------------------------------------------"通讯"tab遗留下来的代码，没有删除----------------------------------------------------------------------------------------- */
    /* -----------------------------------------------------------------------------------------***********************************----------------------------------------------------------------------------------------- */
    private List<RecentContact> loadedRecents;

    private void requestMessages(boolean delay) {
        if (msgLoaded) {
            return;
        }
        try{
        postDelayed(new Runnable() {

            @Override
            public void run() {
                if (msgLoaded) {
                    return;
                }
                // 查询最近联系人列表数据
                NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(new RequestCallbackWrapper<List<RecentContact>>() {

                    @Override
                    public void onResult(int code, List<RecentContact> recents, Throwable exception) {
                        if (code != ResponseCode.RES_SUCCESS || recents == null) {
                            return;
                        }
                        loadedRecents = recents;

                        // 此处如果是界面刚初始化，为了防止界面卡顿，可先在后台把需要显示的用户资料和群组资料在后台加载好，然后再刷新界面
                        //
                        msgLoaded = true;
                        if (isAdded()) {
                            onRecentContactsLoaded();
                        }
                    }
                });
            }
        }, delay ? 250 : 0);
        } catch (Exception e) {

        }
    }

    private void onRecentContactsLoaded() {
        refreshMessages(true);
        if (callback != null) {
            callback.onRecentContactsLoaded();
        }
    }

    private void refreshMessages(boolean unreadChanged) {
        if (unreadChanged) {
            int unreadNum = 0;
            if (callback != null) {
                callback.onUnreadCountChange(unreadNum);
            }
        }
    }

    FriendDataCache.FriendDataChangedObserver friendDataChangedObserver = new FriendDataCache.FriendDataChangedObserver() {
        @Override
        public void onAddedOrUpdatedFriends(List<String> accounts) {
            //好友状态发生变化
//            adapter.notifyDataSetChanged();
        }
        @Override
        public void onDeletedFriends(List<String> accounts) {
        }
        @Override
        public void onAddUserToBlackList(List<String> accounts) {
        }
        @Override
        public void onRemoveUserFromBlackList(List<String> accounts) {
        }
    };

    /**
     * 注册群信息&群成员更新监听
     */
//    private void registerTeamUpdateObserver(boolean register) {
//        if (register) {
//            TeamDataCache.getInstance().registerTeamDataChangedObserver(teamDataChangedObserver);
//        } else {
//            TeamDataCache.getInstance().unregisterTeamDataChangedObserver(teamDataChangedObserver);
//        }
//    }

    private void registerTeamMemberUpdateObserver(boolean register) {
        if (register) {
            TeamDataCache.getInstance().registerTeamMemberDataChangedObserver(teamMemberDataChangedObserver);
        } else {
            TeamDataCache.getInstance().unregisterTeamMemberDataChangedObserver(teamMemberDataChangedObserver);
        }
    }

    Observer<List<RecentContact>> messageObserver = new Observer<List<RecentContact>>() {
        @Override
        public void onEvent(List<RecentContact> messages) {
            int index;
            for (RecentContact msg : messages) {
            }
            refreshMessages(true);
        }
    };

    Observer<IMMessage> statusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage message) {
        }
    };

    Observer<RecentContact> deleteObserver = new Observer<RecentContact>() {
        @Override
        public void onEvent(RecentContact recentContact) {
            if (recentContact != null) {
            }
        }
    };

//    TeamDataCache.TeamDataChangedObserver teamDataChangedObserver = new TeamDataCache.TeamDataChangedObserver() {
//        @Override
//        public void onUpdateTeams(List<Team> teams) {
////            adapter.notifyDataSetChanged();
//        }
//        @Override
//        public void onRemoveTeam(Team team) {
//        }
//    };

    TeamDataCache.TeamMemberDataChangedObserver teamMemberDataChangedObserver = new TeamDataCache.TeamMemberDataChangedObserver() {
        @Override
        public void onUpdateTeamMember(List<TeamMember> members) {
//            adapter.notifyDataSetChanged();
        }

        @Override
        public void onRemoveTeamMember(TeamMember member) {

        }
    };

    protected void refreshViewHolderByIndex(final int index) {
    }

    public void setCallback(RecentContactsCallback callback) {
        this.callback = callback;
    }

    private void registerUserInfoObserver(boolean register) {
        if (uinfoObserver == null) {
            uinfoObserver = new UserInfoObservable.UserInfoObserver() {
                @Override
                public void onUserInfoChanged(List<String> accounts) {
                    refreshMessages(false);
                }
            };
        }
        if (register) {
            UserInfoHelper.registerObserver(uinfoObserver);
        } else {
            UserInfoHelper.unregisterObserver(uinfoObserver);
        }
    }

    Map<String, Integer> teamCountMap = new HashMap<String, Integer>();

}
