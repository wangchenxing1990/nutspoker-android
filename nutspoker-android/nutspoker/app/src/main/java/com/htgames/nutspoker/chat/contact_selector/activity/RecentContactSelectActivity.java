package com.htgames.nutspoker.chat.contact_selector.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.contact.adapter.UserAdapter;
import com.htgames.nutspoker.ui.adapter.team.GroupAdapter;
import com.htgames.nutspoker.data.common.CircleConstant;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.view.ShareToSessionDialog;
import com.htgames.nutspoker.view.widget.CustomListView;
import com.netease.nim.uikit.cache.FriendDataCache;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.common.adapter.TAdapterDelegate;
import com.netease.nim.uikit.common.adapter.TViewHolder;
import com.netease.nim.uikit.recent.RecentContactsCallback;
import com.netease.nim.uikit.recent.viewholder.RecentContactAdapter;
import com.netease.nim.uikit.recent.viewholder.SelectModeCommonRecentViewHolder;
import com.netease.nim.uikit.recent.viewholder.SelectModeTeamRecentViewHolder;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 最近联系人选择
 */
public class RecentContactSelectActivity extends BaseActivity implements TAdapterDelegate , View.OnClickListener{
    RelativeLayout rl_constact_new;
    // data
    private List<RecentContact> items;
    private RecentContactAdapter adapter;
    private RecentContactsCallback callback;
    private boolean msgLoaded = false;
    public static final long RECENT_TAG_STICKY = 1; // 联系人置顶tag
    ShareToSessionDialog mShareToSessionDialog;
    SearchView searchView;
    ArrayList<Team> myClubList = new ArrayList<Team>();
    ArrayList<NimUserInfo> friendList = new ArrayList<NimUserInfo>();
    ArrayList<Team> groupList = new ArrayList<Team>();
    ArrayList<Team> clubList = new ArrayList<Team>();
    LinearLayout ll_recentcontact;
    LinearLayout ll_search;
    //
    LinearLayout ll_recentlist;
    LinearLayout ll_friendlist;
    LinearLayout ll_grouplist;
    LinearLayout ll_clublist;
    CustomListView lv_recent_contact;
    CustomListView lv_friend;
    CustomListView lv_group;
    CustomListView lv_club;
    UserAdapter mUserAdapter;
    GroupAdapter mGroupAdapter;
    TextView tv_search_cancel;
    private int shareType = CircleConstant.TYPE_PAIJU;
    Object shareContent;
    int from = ContactSelectActivity.FROM_PAIJU_DETAILS;

    public static void start(Activity activity , int shareType , Object shareContent , int from){
        Intent intent = new Intent(activity , RecentContactSelectActivity.class);
        intent.putExtra(Extras.EXTRA_SHARE_TYPE , shareType);
        intent.putExtra(Extras.EXTRA_SHARE_DATA , (Serializable)shareContent);
        intent.putExtra(Extras.EXTRA_FROM , from);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recentcontact_select);
        setHeadTitle(R.string.select);
        shareType =  getIntent().getIntExtra(Extras.EXTRA_SHARE_TYPE, CircleConstant.TYPE_PAIJU);
        shareContent = getIntent().getSerializableExtra(Extras.EXTRA_SHARE_DATA);
        from =  getIntent().getIntExtra(Extras.EXTRA_FROM, ContactSelectActivity.FROM_PAIJU_DETAILS);
        initView();
        initSearchView();
        initList();
        requestMessages(true);
        initMyClubList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mShareToSessionDialog = null;
    }

    private void initMyClubList() {
        myClubList = new ArrayList<Team>();
        if(TeamDataCache.getInstance().getAllAdvancedTeams() != null){
            for(Team team : TeamDataCache.getInstance().getAllAdvancedTeams()){
                if(team.isMyTeam() && !GameConstants.isGmaeClub(team)){
                    //过滤掉不是我的俱乐部和游戏俱乐部
                    myClubList.add(team);
                }
            }
        }
    }

    private void initSearchView() {
        searchView = (SearchView) findViewById(R.id.mSearchView);
//        searchView.setIconified(false);//是否一开始就处于显示SearchView的状态
        searchView.setIconifiedByDefault(true);//是否可以隐藏
        searchView.setQueryHint(getString(R.string.contact_search));
//        searchView.set
//        final int closeImgId = getResources().getIdentifier("search_close_btn", "id", getPackageName());
        ImageView search_button = (ImageView) searchView.findViewById(R.id.search_button);
//        search_button.setImageResource(R.drawable.btn_head_search);
        //
        ImageView search_mag_icon = (ImageView) searchView.findViewById(R.id.search_mag_icon);
//        search_mag_icon.setImageResource(R.drawable.btn_head_search);
        search_mag_icon.setVisibility(View.GONE);
        //删除按钮
        ImageView closeImg = (ImageView) searchView.findViewById(R.id.search_close_btn);
        closeImg.setImageResource(R.mipmap.icon_edit_delete);
        //输入框
        SearchView.SearchAutoComplete mEditSearchView = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        mEditSearchView.setTextColor(Color.WHITE);
        mEditSearchView.setHintTextColor(Color.GRAY);
        //提示
        LinearLayout tipLayout = (LinearLayout) searchView.findViewById(R.id.search_edit_frame);
        //搜索按钮
        ImageView icTip = (ImageView) searchView.findViewById(R.id.search_mag_icon);
//        icTip.setImageResource(R.drawable.btn_head_search);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                showSearchView(false);
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchView(true);
            }
        });
        //文本内容监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchKey(newText);
                return true;
            }
        });
    }

    public void showSearchView(boolean show){
        if(show){
            findViewById(R.id.rl_head_normal).setVisibility(View.GONE);
            ll_recentcontact.setVisibility(View.GONE);
            ll_search.setVisibility(View.VISIBLE);
            tv_search_cancel.setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.rl_head_normal).setVisibility(View.VISIBLE);
            ll_recentcontact.setVisibility(View.VISIBLE);
            ll_search.setVisibility(View.GONE);
            tv_search_cancel.setVisibility(View.GONE);
        }
    }

    private void initList() {
        items = new ArrayList<>();
        adapter = new RecentContactAdapter(this, items, this);
        adapter.setCallback(callback);
        lv_recent_contact.setAdapter(adapter);
        lv_recent_contact.setItemsCanFocus(true);
        lv_recent_contact.setOnItemClickListener(mOnItemClickListener);
    }

    public void confimSend(String sessionId , SessionTypeEnum sessionTypeEnum) {
        if (mShareToSessionDialog == null) {
            mShareToSessionDialog = new ShareToSessionDialog(this, shareType , shareContent , from);
        }
        mShareToSessionDialog.show(sessionId , sessionTypeEnum);
    }

    private List<RecentContact> loadedRecents;

    private void requestMessages(boolean delay) {
        if (msgLoaded) {
            return;
        }
        getHandler().postDelayed(new Runnable() {

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
                        onRecentContactsLoaded();
                    }
                });
            }
        }, delay ? 250 : 0);
    }

    private void onRecentContactsLoaded() {
        items.clear();
        if (loadedRecents != null) {
            items.addAll(loadedRecents);
            loadedRecents = null;
        }
        refreshMessages(true);
        if (callback != null) {
            callback.onRecentContactsLoaded();
        }
    }

    private void refreshMessages(boolean unreadChanged) {
        sortRecentContacts(items);
//        deleteEmptyMessage(items);
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
        boolean empty = items.isEmpty() && msgLoaded;
        if(empty){
            ll_recentlist.setVisibility(View.GONE);
        }else{
            ll_recentlist.setVisibility(View.VISIBLE);
        }
//        mResultDataView.setVisibility(empty ? View.VISIBLE : View.GONE);
//        if(empty){
//            mResultDataView.nullDataShow(R.string.chat_null , R.mipmap.img_chat_null);
//        }else{
//            mResultDataView.successShow();
//        }
    }


    private void sortRecentContacts(List<RecentContact> list) {
        if (list.size() == 0) {
            return;
        }
        //处理最近联系人不显示：1.游戏的牌局 2.不是我的群 3.不是我的好友
        if (list != null && list.size() != 0) {
            int size = list.size();
            for (int i = (size - 1); i >= 0; i--) {
                RecentContact recentContact = list.get(i);
                if (recentContact.getSessionType() == SessionTypeEnum.Team) {
                    if (GameConstants.isGmaeClub(recentContact.getContactId())
                            || !TeamDataCache.getInstance().isMyTeam(recentContact.getContactId())) {
                        list.remove(i);
                    }
                } else if(recentContact.getSessionType() == SessionTypeEnum.P2P){
                    if(!NIMClient.getService(FriendService.class).isMyFriend(recentContact.getContactId())){
                        list.remove(i);
                    }
                }
            }
        }
        Collections.sort(list, comp);
    }

    private static Comparator<RecentContact> comp = new Comparator<RecentContact>() {

        @Override
        public int compare(RecentContact o1, RecentContact o2) {
            // 先比较置顶tag
            long sticky = (o1.getTag() & RECENT_TAG_STICKY) - (o2.getTag() & RECENT_TAG_STICKY);
            if (sticky != 0) {
                return sticky > 0 ? -1 : 1;
            } else {
                long time = o1.getTime() - o2.getTime();
                return time == 0 ? 0 : (time > 0 ? -1 : 1);
            }
        }
    };

    private void initView() {
        //
        lv_recent_contact = (CustomListView) findViewById(R.id.lv_recent_contact);
        rl_constact_new = (RelativeLayout) findViewById(R.id.rl_constact_new);
        ll_recentcontact = (LinearLayout) findViewById(R.id.ll_recentcontact);
        ll_search = (LinearLayout) findViewById(R.id.ll_search);
        tv_search_cancel = (TextView) findViewById(R.id.tv_search_cancel);
        tv_search_cancel.setOnClickListener(this);
        rl_constact_new.setOnClickListener(this);
        //
        ll_recentlist = (LinearLayout)findViewById(R.id.ll_recentlist);
        ll_friendlist = (LinearLayout)findViewById(R.id.ll_friendlist);
        ll_grouplist = (LinearLayout)findViewById(R.id.ll_grouplist);
        ll_clublist = (LinearLayout)findViewById(R.id.ll_clublist);
        lv_recent_contact = (CustomListView)findViewById(R.id.lv_recent_contact);
        lv_friend = (CustomListView)findViewById(R.id.lv_friend);
        lv_group = (CustomListView)findViewById(R.id.lv_group);
        lv_club = (CustomListView)findViewById(R.id.lv_club);
        lv_friend.setOnItemClickListener(mOnItemClickListener);
        lv_club.setOnItemClickListener(mOnItemClickListener);
        lv_group.setOnItemClickListener(mOnItemClickListener);
        mUserAdapter = new UserAdapter(this , friendList , false , false);
        mGroupAdapter = new GroupAdapter(getApplicationContext() , groupList);
//        mClubAdapter = new ClubAdapter(this , clubList , true);
        lv_friend.setAdapter(mUserAdapter);
        lv_group.setAdapter(mGroupAdapter);
//        lv_club.setAdapter(mClubAdapter);
    }

    AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position < lv_recent_contact.getHeaderViewsCount()) {
                return;
            }
            if(parent.getItemAtPosition(position) instanceof RecentContact){
                RecentContact recent = (RecentContact) parent.getItemAtPosition(position);
                confimSend(recent.getContactId() ,recent.getSessionType());
            } else if(parent.getItemAtPosition(position) instanceof Team){
                Team team = (Team) parent.getItemAtPosition(position);
                confimSend(team.getId() , SessionTypeEnum.Team);
            } else if(parent.getItemAtPosition(position) instanceof NimUserInfo){
                NimUserInfo user = (NimUserInfo) parent.getItemAtPosition(position);
                confimSend(user.getAccount() , SessionTypeEnum.P2P);
            }
        }
    };

    public void searchKey(String key) {
        groupList.clear();
        friendList.clear();
        clubList.clear();
        if(TextUtils.isEmpty(key)){
            ll_clublist.setVisibility(View.GONE);
            ll_friendlist.setVisibility(View.GONE);
            ll_grouplist.setVisibility(View.GONE);
            return;
        }
//        mClubAdapter.setKeyWord(key);
        for(Team team : TeamDataCache.getInstance().getAllNormalTeams()){
            if(team.isMyTeam() && team.getName().contains(key)){
                groupList.add(team);
                LogUtil.i("search" , team.getName());
            }
        }
        for(String friendAccount : FriendDataCache.getInstance().getMyFriendAccounts()){
            if(NimUserInfoCache.getInstance().hasUser(friendAccount) && NimUserInfoCache.getInstance().getUserDisplayName(friendAccount).contains(key)){
                friendList.add(NimUserInfoCache.getInstance().getUserInfo(friendAccount));
                LogUtil.i("search", NimUserInfoCache.getInstance().getUserDisplayName(friendAccount));
            }
        }
        for(Team team : myClubList){
            if(team.isMyTeam() && team.getName().contains(key)){
                clubList.add(team);
            }
        }
        if(groupList == null || groupList.size() == 0){
            ll_grouplist.setVisibility(View.GONE);
        }else{
            ll_grouplist.setVisibility(View.VISIBLE);
            mGroupAdapter.notifyDataSetChanged();
        }
        if(friendList == null || friendList.size() == 0){
            ll_friendlist.setVisibility(View.GONE);
        }else{
            ll_friendlist.setVisibility(View.VISIBLE);
            mUserAdapter.notifyDataSetChanged();
        }
        if(clubList == null || clubList.size() == 0){
            ll_clublist.setVisibility(View.GONE);
        }else{
            ll_clublist.setVisibility(View.VISIBLE);
//            mClubAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        if(!searchView.isIconified()){
            searchView.setQuery("", true);
            searchView.setIconified(true);
            showKeyboard(false);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public Class<? extends TViewHolder> viewHolderAtPosition(int position) {
        SessionTypeEnum type = items.get(position).getSessionType();
        if (type == SessionTypeEnum.Team) {
//            return TeamRecentViewHolder.class;
            return SelectModeTeamRecentViewHolder.class;
        } else {
//            return CommonRecentViewHolder.class;
            return SelectModeCommonRecentViewHolder.class;
        }
    }

    @Override
    public boolean enabled(int position) {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_constact_new:
                final ContactSelectActivity.Option option = new ContactSelectActivity.Option();
                option.title = getString(R.string.contact_select);
//                Intent intent = new Intent(RecentContactSelectActivity.this, ContactSelectActivity.class);
//                intent.putExtra(ContactSelectActivity.EXTRA_DATA, option);
//                intent.putExtra(ContactSelectActivity.EXTRA_FROM , ContactSelectActivity.FROM_NEWCONTACT);
//                startActivity(intent);
                ContactSelectActivity.start(RecentContactSelectActivity.this , option , shareType ,shareContent, from);
//                Intent intent = new Intent(RecentContactSelectActivity.this , FriendsListActivity.class);
//                intent.putExtra("from" ,FriendsListActivity.FROM_FRIEND_SELECT);
//                startActivity(intent);
                break;
            case R.id.tv_search_cancel:
                searchView.setQuery("", true);
                searchView.setIconified(true);
                showKeyboard(false);
                break;
        }
    }
}
