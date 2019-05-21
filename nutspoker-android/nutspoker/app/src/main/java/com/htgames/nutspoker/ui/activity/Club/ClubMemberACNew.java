package com.htgames.nutspoker.ui.activity.Club;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.contact.activity.UserProfileAC;
import com.htgames.nutspoker.chat.main.activity.AddClubMember;
import com.htgames.nutspoker.ui.action.ClubAction;
import com.htgames.nutspoker.ui.adapter.clubmember.ClubMemberAdapNew;
import com.htgames.nutspoker.ui.adapter.clubmember.ClubMemberHeader;
import com.htgames.nutspoker.ui.adapter.clubmember.ClubMemberItem;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.view.ResultDataView;
import com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.AnimUtil;
import com.netease.nim.uikit.api.ApiCode;
import com.netease.nim.uikit.api.ApiConstants;
import com.netease.nim.uikit.api.HttpApi;
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.ui.liv.LetterIndexView;
import com.netease.nim.uikit.common.ui.liv.LivIndex;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.interfaces.IClick;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nim.uikit.uinfo.UserInfoHelper;
import com.netease.nim.uikit.uinfo.UserInfoObservable;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.fastscroller.FastScroller;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by 周智慧 on 2017/5/22.
 */

public class ClubMemberACNew extends BaseActivity implements View.OnClickListener, IClick, FastScroller.OnScrollStateChangeListener, FlexibleAdapter.OnUpdateListener {
    String teamId;
    Team team;
    private String creator;
    private boolean isSelfAdmin = false;
    ClubAction mClubAction;
    private List<TeamMember> memberList = new ArrayList<>();
    private List<String> memberAccounts = new ArrayList<>();
    ClubMemberAdapNew mAdapter;
    @BindView(R.id.lv_members) RecyclerView lv_members;
    //搜索成员相关view
    @BindView(R.id.search_mtt_mgr_tv_cancel) TextView search_mtt_mgr_tv_cancel;
    @BindView(R.id.edit_search_record) ClearableEditTextWithIcon edit_search_record;
    @BindView(R.id.mResultDataView) ResultDataView mResultDataView;
    private UserInfoObservable.UserInfoObserver userInfoObserver;
    public static void start(Activity activity, String tid) {
        Intent intent = new Intent(activity, ClubMemberACNew.class);
        intent.putExtra(Extras.EXTRA_TEAM_ID, tid);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_members);
        teamId = getIntent().getStringExtra(Extras.EXTRA_TEAM_ID);
        mUnbinder = ButterKnife.bind(this);
        initAdapter();
        setHeadTitle(R.string.club_member_manager);
        loadTeamInfo();
        registerUserInfoChangedObserver(true);
        registerObservers(true);
        mClubAction = new ClubAction(this, null);
        requestData();//第一次进来延迟200毫秒更新ui，否则在部分差手机上比如魅蓝note2要等很久很久才能进入这个页面
        setSearchRelated();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (aliasHasChanged && mAdapter != null) {
            aliasHasChanged = false;
            notifyData();
        }
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                return false;
            }
        });
    }

    private void initAdapter() {
        mAdapter = new ClubMemberAdapNew(datas, this, true);
        mAdapter.DEBUG = CacheConstant.debugBuildType;
//        mAdapter.setAnimationEntryStep(true)
//                .setOnlyEntryAnimation(false)
//                .setAnimationInitialDelay(500L)
//                .setAnimationDelay(70L)
//                .setAnimationOnScrolling(true)
//                .setAnimationDuration(300L);
        mAdapter.setAnimateChangesWithDiffUtil(true)
                .setAnimateToLimit(10000)
                .setNotifyMoveOfFilteredItems(false)
                .setNotifyChangeOfUnfilteredItems(true)
//                .setAnimationInitialDelay(300)
                .setAnimationDelay(70)
                .setAnimationOnScrolling(true)
                .setAnimationDuration(250L)
                .setAnimationOnReverseScrolling(true)
                .setOnlyEntryAnimation(false);
        lv_members.setAdapter(mAdapter);
        lv_members.setHasFixedSize(true); //Size of RV will not change
        lv_members.setItemViewCacheSize(0); //Setting ViewCache to 0 (default=2) will animate items better while scrolling down+up with LinearLayout
        FastScroller fastScroller = (FastScroller) findViewById(R.id.fast_scroller);
        fastScroller.addOnScrollStateChangeListener(this);
        fastScroller.setBubbleAndHandleColor(Color.RED);
        mAdapter.setFastScroller(fastScroller);
        mAdapter.setLongPressDragEnabled(false)
                .setHandleDragEnabled(true)
                .setSwipeEnabled(false)
                .setUnlinkAllItemsOnRemoveHeaders(true)
                // Show Headers at startUp, 1st call, correctly executed, no warning log message!
                .setDisplayHeadersAtStartUp(true)
                .setStickyHeaders(true);
        // Simulate developer 2nd call mistake, now it's safe, not executed, no warning log message!
        // Simulate developer 3rd call mistake, still safe, not executed, warning log message displayed!
//                    .showAllHeaders();
        // Show Headers at startUp, 1st call, correctly executed, no warning log message!
    }

    private void loadTeamInfo() {
        team = TeamDataCache.getInstance().getTeamById(teamId);
        if (team != null) {
            creator = team.getCreator();
            if(creator.equals(DemoCache.getAccount())) {
                isSelfAdmin = true;
                mAdapter.isMeCreator = true;
                mAdapter.isSelfAdmin = true;
                setHeadRightButton(R.string.more, this);
//                setHeadRightSecondButton(R.string.upgrade, this);// TODO: 16/12/8
            }
        }
    }

    List<AbstractFlexibleItem> datas = new ArrayList<>();
    private void notifyData() {
        datas.clear();
        isSelfAdmin = false;
        mAdapter.isSelfAdmin = false;
        mAdapter.isMeCreator = false;
        ClubMemberHeader headerManager = new ClubMemberHeader("headerManager");
        ClubMemberHeader headerNormalMember = new ClubMemberHeader("headerNormalMember");
        List<ClubMemberItem> ownerList = new ArrayList<>();
        List<ClubMemberItem> managerList = new ArrayList<>();
        List<ClubMemberItem> normal = new ArrayList<>();
        if(memberList != null && !memberList.isEmpty()) {
            for(TeamMember tm : memberList) {
                ClubMemberItem clubMemberItem = new ClubMemberItem(tm);
                if(tm.getType() == TeamMemberType.Owner) {
                    ownerList.add(clubMemberItem);
                    if (tm.getAccount().equals(DemoCache.getAccount())) {
                        isSelfAdmin = true;
                        mAdapter.isSelfAdmin = true;
                        mAdapter.isMeCreator = true;
                    }
                } else if (tm.getType() == TeamMemberType.Manager) {
                    managerList.add(clubMemberItem);
                    if (tm.getAccount().equals(DemoCache.getAccount())) {
                        isSelfAdmin = true;
                        mAdapter.isSelfAdmin = true;
                    }
                } else if(tm.getType() == TeamMemberType.Normal) {
                    normal.add(clubMemberItem);
                }
            }
            String normalHeadStr = getResources().getString(R.string.item_head_member_format, normal.size());
            headerManager.setTitle(getResources().getString(R.string.item_head_mgr_format, (ownerList.size() + managerList.size())));
            headerNormalMember.setTitle(normalHeadStr);
            if (ownerList.size() > 0) {
                ((ClubMemberItem) ownerList.get(0)).setHeader(headerManager);
            }
            if (normal.size() > 0) {
                Collections.sort(normal, ClubMemberItem.comparator);
                for (int i = 0; i < normal.size(); i++) {
                    ClubMemberItem preItem = i >= 1 && normal.get(i - 1) instanceof ClubMemberItem ?  (ClubMemberItem) normal.get(i - 1) : null;
                    ClubMemberItem currentItem = normal.get(i) instanceof ClubMemberItem ? (ClubMemberItem) normal.get(i) : null;
                    if (currentItem != null) {
                        if (preItem == null) {
                            String currentAlpha = ClubMemberItem.getAlpha(currentItem.pinyin);
                            ClubMemberHeader header = new ClubMemberHeader(currentAlpha);
                            header.setTitle(currentAlpha);
                            currentItem.setHeader(header);
                        } else {
                            String preAlpha = ClubMemberItem.getAlpha(preItem.pinyin);
                            String currentAlpha = ClubMemberItem.getAlpha(currentItem.pinyin);
                            if (!preAlpha.equals(currentAlpha)) {
                                ClubMemberHeader header = new ClubMemberHeader(currentAlpha);
                                header.setTitle(currentAlpha);
                                currentItem.setHeader(header);
                            }
                        }
                    }
                }
                ClubMemberItem noneItem = new ClubMemberItem(null);
                noneItem.setHidden(true);
                noneItem.setHeader(headerNormalMember);
                normal.add(0, noneItem);
            }
            datas.addAll(ownerList);
            datas.addAll(managerList);
            datas.addAll(normal);
            mAdapter.mMaxNum = memberList.size();
            mAdapter.updateDataSet(datas);
            if (mAdapter.mAnimatorNotifierObserver != null) {
                mAdapter.mAnimatorNotifierObserver.notified = false;
            }
            buildLitterIdx(normal);
        }
    }

    private LivIndex litterIdx;
    ArrayList<String> letters = new ArrayList<>();
    protected final HashMap<String, Integer> indexes = new HashMap<>();
    private void buildLitterIdx(List<ClubMemberItem> normal) {
        getIndexes(normal);
        LetterIndexView livIndex = (LetterIndexView) findViewById(R.id.liv_index);
        livIndex.setNormalColor(getResources().getColor(R.color.contacts_letters_color));
        TextView litterHit = (TextView) findViewById(R.id.tv_hit_letter);
        litterIdx = new LivIndex(lv_members, null, livIndex, litterHit, indexes);
//        litterIdx.scrollOffset = datas.size() - normal.size() + 1;
        litterIdx.updateLetters(letters.toArray(new String[letters.size()]));
        litterIdx.show();
    }

    private int preHeaders = 0;
    public HashMap<String, Integer> getIndexes(List<ClubMemberItem> normal) {
        preHeaders = datas.size() - normal.size() + 1 + 1;
        this.indexes.clear();
        letters.clear();
//        this.indexes.putAll(indexes);
        for (int i = 0; i < normal.size(); i++) {
            // 得到字母
            if (!normal.get(i).isHidden() && normal.get(i).getHeader() != null) {
                String name = ClubMemberItem.getAlpha(normal.get(i).sortKey);
                if (!indexes.containsKey(name)) {
                    indexes.put(name, preHeaders + i);
                    letters.add(name);
                }
                preHeaders++;
            }
        }
        return indexes;
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
                        ClubMemberLimitActivity.start(ClubMemberACNew.this, teamId);
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            showLimitDialog.show();
        }
    }

    public void showRemoveMode(boolean show) {
        mAdapter.showRemoveMode(show);
        if(show){
            setHeadRightButtonText(R.string.finish);
            setHeadRightSecondButtonGone();
        } else {
            setHeadRightButtonText(R.string.more);
            if(isSelfAdmin){
//                setHeadRightSecondButton(R.string.upgrade , this);// TODO: 16/12/8
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mAdapter != null && mAdapter.isShowRemove || !StringUtil.isSpace(queryText)) {
            if (mAdapter.isShowRemove) {
                showRemoveMode(false);
            }
            if (!StringUtil.isSpace(queryText)) {
                queryText = "";
                edit_search_record.setText("");
                filterSearch();
            }
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_head_right:
                if(mAdapter.isShowRemove) {
                    showRemoveMode(false);
                } else {
                    if(morePopupWindow != null && morePopupWindow.isShowing()){
                        dissmissMorePopWindow();
                    } else {
                        showMorePopWindow();
                    }
                }
                break;
            case R.id.ll_pop_club_create:
                dissmissMorePopWindow();
                if(memberList.size() >= ClubConstant.getClubMemberLimit(team)){
//                    Toast.makeText(getApplicationContext() , R.string.club_invite_member_count_limit , Toast.LENGTH_SHORT).show();
                    showMemberLimitDialog();
                } else{
//                    ContactSelectActivity.Option option = ContactSelectHelper.getContactSelectOption(memberAccounts, ClubConstant.getClubMemberLimit(team));
//                    option.maxSelectedTip = getString(R.string.club_invite_member_count_limit);
//                    ContactSelectHelper.startContactSelect(this, option, REQUEST_CODE_CONTACT_SELECT);
//                    List<String> tmpAccounts = new ArrayList<>();
//                    tmpAccounts.addAll(memberAccounts);
//                    ContactSelectActivity.Option option = ContactSelectHelper.getContactSelectOption(tmpAccounts, ClubConstant.getClubMemberLimit(team));
//                    option.maxSelectedTip = getString(R.string.club_invite_member_count_limit);
////                    ContactSelectHelper.startContactSelect(ClubInfCoActivity.this, option, REQUEST_CODE_CONTACT_SELECT);  by 20161127，把"邀请俱乐部成员"页面改为搜索页面
//                    Intent toSearchIntent = new Intent(ClubMemberACNew.this, SearchActivity.class);
//                    toSearchIntent.putExtra(Extras.EXTRA_TEAM_DATA, team);
//                    startActivity(new Intent(toSearchIntent));
                    AddClubMember.Companion.start(ClubMemberACNew.this, team);
                }
                break;
            case R.id.ll_pop_club_join:
                dissmissMorePopWindow();
                showRemoveMode(true);
                break;
        }
    }

    private void requestData() {
        List<TeamMember> members = TeamDataCache.getInstance().getTeamMemberList(teamId);
        if (!members.isEmpty()) {
            memberList.clear();
            memberAccounts.clear();
            for (TeamMember member : members) {
                memberAccounts.add(member.getAccount());
                memberList.add(member);
            }
            notifyData();//requestData
        }
//        TeamDataCache.getInstance().fetchTeamMemberList(teamId, new SimpleCallback<List<TeamMember>>() {
//            @Override
//            public void onResult(boolean success, List<TeamMember> members) {
//                if(success) {
//                    if (!members.isEmpty()) {
//                        memberList.clear();
//                        memberAccounts.clear();
//                        for (TeamMember member : members) {
//                            memberAccounts.add(member.getAccount());
//                            memberList.add(member);
//                        }
//                        notifyData();
//                    }
//                }
//            }
//        });
    }

    private void registerUserInfoChangedObserver(boolean register) {
        if (register) {
            if (userInfoObserver == null) {
                userInfoObserver = new UserInfoObservable.UserInfoObserver() {
                    @Override
                    public void onUserInfoChanged(List<String> accounts) {
                        notifyData();//registerUserInfoChangedObserver
                    }
                };
            }
            UserInfoHelper.registerObserver(userInfoObserver);
        } else {
            UserInfoHelper.unregisterObserver(userInfoObserver);
        }
    }

    private void registerObservers(boolean register) {
        if (register) {
            TeamDataCache.getInstance().registerTeamMemberDataChangedObserver(teamMemberObserver);
        } else {
            TeamDataCache.getInstance().unregisterTeamMemberDataChangedObserver(teamMemberObserver);
        }
    }

    TeamDataCache.TeamMemberDataChangedObserver teamMemberObserver = new TeamDataCache.TeamMemberDataChangedObserver() {
        @Override
        public void onUpdateTeamMember(List<TeamMember> members) {
            for(TeamMember teamMember : members){
                if(teamId.equals(teamMember.getTid())){
                    //新增会员
                    if(!memberAccounts.contains(teamMember.getAccount())) {
                        memberAccounts.add(teamMember.getAccount());
                        memberList.add(teamMember);
                    } else {
                        //信息更新
                        int i = 0;
                        for(TeamMember currentMember: memberList){
                            if(currentMember.getAccount().equals(teamMember.getAccount())){
                                memberList.set(i,teamMember);
                                break;
                            }
                            i ++;
                        }
                    }
                }
            }
            notifyData();//teamMemberObserver
        }
        @Override
        public void onRemoveTeamMember(TeamMember member) {
            removeMember(member.getAccount());
        }
    };

    /**
     * 移除群成员成功后，删除列表中的群成员
     */
    private void removeMember(String uid) {
        if (TextUtils.isEmpty(uid)) {
            return;
        }
        for (TeamMember item : memberList) {
            if (item.getAccount() != null && item.getAccount().equals(uid)) {
                memberList.remove(item);
                memberAccounts.remove(item.getAccount());
                break;
            }
        }
        notifyData();//removeMember
    }

    @Override
    protected void onDestroy() {
        registerUserInfoChangedObserver(false);
        registerObservers(false);
        morePopupWindow = null;
        if(mClubAction != null) {
            mClubAction.onDestroy();
            mClubAction = null;
        }
        super.onDestroy();
    }

    PopupWindow morePopupWindow = null;
    View ll_pop_club_row1;
    View ll_pop_club_row2;
    private void showMorePopWindow() {
        if (morePopupWindow == null) {
            View popView = LayoutInflater.from(this).inflate(R.layout.pop_club_add, null);
            morePopupWindow = new PopupWindow(popView);
            //获取popwindow焦点
            morePopupWindow.setFocusable(true);
            //设置popwindow如果点击外面区域，便关闭。
            morePopupWindow.setOutsideTouchable(true);
            morePopupWindow.setBackgroundDrawable(new ColorDrawable(0));
            morePopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            morePopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            //设置popwindow出现和消失动画
//        clubPopupWindow.setAnimationStyle(R.style.PopMenuAnimation);
            ll_pop_club_row1 = popView.findViewById(R.id.ll_pop_club_create);//邀请新会员
            ll_pop_club_row1.setOnClickListener(this);
            ll_pop_club_row1.setVisibility(View.GONE);//mTeamMemberAdapter
            ll_pop_club_row2 = popView.findViewById(R.id.ll_pop_club_join);//移除会员
            ll_pop_club_row2.setOnClickListener(this);
            ll_pop_club_row2.setVisibility(View.VISIBLE);
            popView.findViewById(R.id.ll_pop_club_divider2).setVisibility(View.GONE);
            int paddingleft = ScreenUtil.dp2px(this, 20);
            Drawable addLeftDrawable = getResources().getDrawable(R.mipmap.room_manager_add);
            addLeftDrawable.setBounds(0, 0, addLeftDrawable.getIntrinsicWidth(), addLeftDrawable.getIntrinsicHeight());
            TextView addText = (TextView) popView.findViewById(R.id.tv_pop_club_create);
            ((FrameLayout.LayoutParams) addText.getLayoutParams()).gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            addText.setText(getResources().getString(R.string.club_member_more_invite));
            addText.setCompoundDrawables(null, null, null, null);
            addText.setPadding(paddingleft, addText.getPaddingTop(), addText.getPaddingRight(), addText.getPaddingBottom());

            Drawable pauseLeftDrawable = getResources().getDrawable(R.mipmap.room_manager_end);
            pauseLeftDrawable.setBounds(0, 0, pauseLeftDrawable.getIntrinsicWidth(), pauseLeftDrawable.getIntrinsicHeight());
            TextView pauseText = (TextView) popView.findViewById(R.id.tv_pop_club_join);
            ((FrameLayout.LayoutParams) pauseText.getLayoutParams()).gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            pauseText.setText(getResources().getString(R.string.club_member_more_remove));
            pauseText.setCompoundDrawables(null, null, null, null);
            pauseText.setPadding(paddingleft, pauseText.getPaddingTop(), pauseText.getPaddingRight(), pauseText.getPaddingBottom());

        }
        morePopupWindow.showAsDropDown(findViewById(R.id.tv_head_right), -ScreenUtil.dp2px(this, 9), -ScreenUtil.dp2px(this, 16));
    }

    public void dissmissMorePopWindow(){
        if(morePopupWindow != null && morePopupWindow.isShowing()){
            morePopupWindow.dismiss();
        }
    }

    public void onDelete(int position) {
        if (!(mAdapter.getItem(position) instanceof ClubMemberItem)) {
            return;
        }
        TeamMember member = ((ClubMemberItem) mAdapter.getItem(position)).mTeamMember;
        if (member == null) {
            return;
        }
        if (isSelfAdmin && !DemoCache.getAccount().equals(member.getAccount())) {
            onRemoveClubMember(member.getAccount(),member.getType() == TeamMemberType.Manager, position);
        }
    }

    public static boolean aliasHasChanged = false;
    public void onClick(int position) {
        if (!(mAdapter.getItem(position) instanceof ClubMemberItem)) {
            return;
        }
        TeamMember member = ((ClubMemberItem) mAdapter.getItem(position)).mTeamMember;
        if (member == null) {
            return;
        }
        if(mAdapter.isShowRemove && !DemoCache.getAccount().equals(member.getAccount())) {
            onRemoveClubMember(member.getAccount(),member.getType() == TeamMemberType.Manager, position);
        } else {
            if (!member.getAccount().equals(DemoCache.getAccount())) {
                if (DemoCache.getAccount().equals(creator)) {
                    //是创建者
//                            UserProfileActivity.start(ClubMemberActivity.this, member.getAccount(), UserProfileActivity.FROM_CLUB_OWNER, teamId,member.getType() == TeamMemberType.Manager);
                    UserProfileAC.start(ClubMemberACNew.this, member.getAccount(), UserProfileAC.FROM_CLUB_OWNER, teamId, member.getType() == TeamMemberType.Manager);
                } else {
                    //不是管理员
//                            UserProfileActivity.start(ClubMemberActivity.this, member.getAccount());
                    UserProfileAC.start(ClubMemberACNew.this, member.getAccount(), UserProfileAC.FROM_CLUB_OWNER, teamId, member.getType() == TeamMemberType.Manager);
                }
            }
        }
    }

    public void onLongClick(int position) {
        if (!(mAdapter.getItem(position) instanceof ClubMemberItem)) {
            return;
        }
        TeamMember member = ((ClubMemberItem) mAdapter.getItem(position)).mTeamMember;
        if (member == null) {
            return;
        }
        if (isSelfAdmin && !member.getAccount().equals(DemoCache.getAccount())) {
            showLongClickMenu(position);
        }
    }

    private void showLongClickMenu(final int position) {
        CustomAlertDialog alertDialog = new CustomAlertDialog(this);
        if (!(mAdapter.getItem(position) instanceof ClubMemberItem)) {
            return;
        }
        final TeamMember member = ((ClubMemberItem) mAdapter.getItem(position)).mTeamMember;
        if (member == null) {
            return;
        }
        alertDialog.setTitle(UserInfoHelper.getUserTitleName(member.getAccount(), SessionTypeEnum.P2P));
        String title = getString(R.string.club_members_manage_remove);
        alertDialog.addItem(title, new CustomAlertDialog.onSeparateItemClickListener() {
            @Override
            public void onClick() {
                onRemoveClubMember(member.getAccount(),member.getType() == TeamMemberType.Manager, position);
            }
        });
        alertDialog.show();
    }

    /**
     * 移除群成员
     */
    private void onRemoveClubMember(final String memberAccount,boolean isMgr, final int position) {
        if (!NetworkUtil.isNetAvailable(this)) {
            Toast.makeText(getApplicationContext(), R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(this, null,
                getString(isMgr?R.string.club_remove_membersmgr_tip:R.string.club_remove_members_tip , NimUserInfoCache.getInstance().getUserDisplayName(memberAccount)), true,
                new EasyAlertDialogHelper.OnDialogActionListener() {
                    @Override
                    public void doCancelAction() {
                    }
                    @Override
                    public void doOkAction() {
                        //移出俱乐部C
                        doRemoveMember(memberAccount,teamId,ClubMemberACNew.this, position);
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            dialog.show();
        }
    }

    //移出会员请求
    public void doRemoveMember(final String account,String teamId,final Activity activity, final int position) {
        HttpApi.Builder builder = new HttpApi.Builder()
                .methodPost(ApiConstants.METHOD_KICK_USER)
                .mapParams(NetWork.getRequestCommonParams())
                .param("tid", teamId)
                .param("member_id", account);
        HttpApi.GetNetCodeObservable(builder
                , new Action0() {
                    @Override
                    public void call() {
                        DialogMaker.showProgressDialog(activity, "", true);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        DialogMaker.dismissProgressDialog();

                        if (integer == 0) {
                            Toast.makeText(ChessApp.sAppContext, R.string.remove_member_success, Toast.LENGTH_SHORT).show();
                            mAdapter.closeOpenedSwipeItemLayoutWithAnim();
                        } else {
                            String message = ApiCode.SwitchCode(integer, ChessApp.sAppContext.getString(R.string.remove_member_failed));
                            Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //移出会员请求
    public static void doRemoveMember(final String account,String teamId,final Activity activity) {
        HttpApi.Builder builder = new HttpApi.Builder()
                .methodPost(ApiConstants.METHOD_KICK_USER)
                .mapParams(NetWork.getRequestCommonParams())
                .param("tid", teamId)
                .param("member_id", account);
        HttpApi.GetNetCodeObservable(builder
                , new Action0() {
                    @Override
                    public void call() {
                        DialogMaker.showProgressDialog(activity, "", true);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        DialogMaker.dismissProgressDialog();

                        if (integer == 0) {
                            Toast.makeText(ChessApp.sAppContext, R.string.remove_member_success, Toast.LENGTH_SHORT).show();
                        } else {
                            String message = ApiCode.SwitchCode(integer, ChessApp.sAppContext.getString(R.string.remove_member_failed));
                            Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onFastScrollerStateChange(boolean scrolling) {

    }

    private String queryText;
    private void setSearchRelated() {
        search_mtt_mgr_tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryText = "";
                edit_search_record.setText("");
                filterSearch();
                showKeyboard(false);
                showCancelSearchTV(false);
            }
        });
        edit_search_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (search_mtt_mgr_tv_cancel.getVisibility() != View.VISIBLE) {
                    showCancelSearchTV(true);
                }
            }
        });
        edit_search_record.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                queryText = s.toString();
                filterSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void showCancelSearchTV(boolean show) {
        if (show) {
            AnimUtil.scaleLargeIn(search_mtt_mgr_tv_cancel, 300);
        } else {
            AnimUtil.scaleSmall(search_mtt_mgr_tv_cancel, 300);
        }
    }

    private void filterSearch() {
        if (mAdapter.hasNewSearchText(queryText)) {
            mAdapter.setSearchText(queryText);
            mAdapter.filterItems(300);
        }
    }

    @Override
    public void onUpdateEmptyView(int size) {
        if (size > 0) {
            mResultDataView.successShow();
        } else {
            mResultDataView.nullDataShow(R.string.no_data);
        }
    }
}
