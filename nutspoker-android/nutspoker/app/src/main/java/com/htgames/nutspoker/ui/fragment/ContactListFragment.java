package com.htgames.nutspoker.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import com.netease.nim.uikit.common.util.log.LogUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.FriendStatusEntity;
import com.htgames.nutspoker.chat.MessageDataCache;
import com.htgames.nutspoker.chat.msg.activity.MessageVerifyActivity;
import com.htgames.nutspoker.chat.msg.helper.SystemMessageHelper;
import com.htgames.nutspoker.ui.activity.Club.GroupListActivity;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.htgames.nutspoker.ui.base.BaseFragment;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.LoginSyncDataStatusObserver;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.UIKitLogTag;
import com.netease.nim.uikit.cache.FriendDataCache;
import com.netease.nim.uikit.common.ui.liv.LetterIndexView;
import com.netease.nim.uikit.common.ui.liv.LivIndex;
import com.netease.nim.uikit.contact.ContactsCustomization;
import com.netease.nim.uikit.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.contact.core.item.ContactItem;
import com.netease.nim.uikit.contact.core.item.ItemTypes;
import com.netease.nim.uikit.contact.core.model.ContactDataAdapter;
import com.netease.nim.uikit.contact.core.model.ContactGroupStrategy;
import com.netease.nim.uikit.contact.core.provider.ContactDataProvider;
import com.netease.nim.uikit.contact.core.query.IContactDataProvider;
import com.netease.nim.uikit.contact.core.viewholder.ContactHolder;
import com.netease.nim.uikit.contact.core.viewholder.LabelHolder;
import com.netease.nim.uikit.uinfo.UserInfoHelper;
import com.netease.nim.uikit.uinfo.UserInfoObservable;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 20150726 on 2015/9/22.
 */
public class ContactListFragment extends BaseFragment implements View.OnClickListener {
    private final static String TAG = "ContactListFragment";
    View view;
    public static ContactListFragment mInstance;
    private TextView tv_contacts_num;
    private RelativeLayout rl_new_friends;
    private RelativeLayout rl_my_group;
    private RelativeLayout rl_my_club;
//    private ListView lv_contacts;

    private ListView listView;
    private ContactDataAdapter adapter;
    private LivIndex litterIdx;
    private ContactsCustomization customization;
    private ReloadFrequencyControl reloadControl = new ReloadFrequencyControl();

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
//            getFriendstatusList();
        }
    }

    private static final class ContactsGroupStrategy extends ContactGroupStrategy {
        public ContactsGroupStrategy() {
            add(ContactGroupStrategy.GROUP_NULL, -1, "");
            addABC(0);
        }
    }

    public static ContactListFragment newInstance() {
        if (mInstance == null) {
            mInstance = new ContactListFragment();
        }
        return mInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFragmentName("ContactListFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_contacts, container, false);
        initView();
//        initFriendChangedNotifyObserver();
        initSystemMessageObserver();//初始化系统消息监听
        buildLitterIdx(view);
        getIsUnReadCount();
        // 注册观察者
        registerObserver(true);
        registerSystemMessageObservers(true);
        // 加载本地数据
        reload(false);
        return view;
    }

    private void initAdapter() {
        IContactDataProvider dataProvider = new ContactDataProvider(ItemTypes.FRIEND);

        adapter = new ContactDataAdapter(getActivity(), new ContactsGroupStrategy(), dataProvider) {
            @Override
            protected List<AbsContactItem> onNonDataItems() {
                if (customization != null) {
                    return customization.onGetFuncItems();
                }
                return new ArrayList<>();
            }

            @Override
            protected void onPreReady() {
//                loadingFrame.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostLoad(boolean empty, String queryText, boolean all) {
//                loadingFrame.setVisibility(View.GONE);
                int userCount = NimUIKit.getContactProvider().getMyFriendsCount();
                tv_contacts_num.setText(String.format(getString(R.string.contancts_num), userCount));

                onReloadCompleted();
            }
        };

        adapter.addViewHolder(ItemTypes.LABEL, LabelHolder.class);
        if (customization != null) {
            adapter.addViewHolder(ItemTypes.FUNC, customization.onGetFuncViewHolderClass());
        }
        adapter.addViewHolder(ItemTypes.FRIEND, ContactHolder.class);
    }

    private void buildLitterIdx(View view) {
        LetterIndexView livIndex = (LetterIndexView) view.findViewById(R.id.liv_index);
        livIndex.setNormalColor(getResources().getColor(R.color.contacts_letters_color));
        TextView litterHit = (TextView) view.findViewById(R.id.tv_hit_letter);
        litterIdx = adapter.createLivIndex(listView, livIndex, litterHit);
        litterIdx.show();
    }

    public void scrollToTop() {
        if (listView != null) {
            int top = listView.getFirstVisiblePosition();
            int bottom = listView.getLastVisiblePosition();
            if (top >= (bottom - top)) {
                listView.setSelection(bottom - top);
                listView.smoothScrollToPosition(0);
            } else {
                listView.smoothScrollToPosition(0);
            }
        }
    }

    private final class ContactItemClickListener implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            AbsContactItem item = (AbsContactItem) adapter.getItem(position - listView.getHeaderViewsCount());
//            AbsContactItem item = (AbsContactItem) adapter.getItem(position);
            if (item == null) {
                return;
            }

            int type = item.getItemType();

            if (type == ItemTypes.FUNC && customization != null) {
                customization.onFuncItemClick(item);
                return;
            }

            if (type == ItemTypes.FRIEND && item instanceof ContactItem && NimUIKit.getContactEventListener() != null) {
                NimUIKit.getContactEventListener().onItemClick(getActivity(), (((ContactItem) item).getContact()).getContactId());
            }
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int position, long id) {
//            AbsContactItem item = (AbsContactItem) adapter.getItem(position);
            AbsContactItem item = (AbsContactItem) adapter.getItem(position - listView.getHeaderViewsCount());
            if (item == null) {
                return false;
            }

            if (item instanceof ContactItem && NimUIKit.getContactEventListener() != null) {
                NimUIKit.getContactEventListener().onItemLongClick(getActivity(), (((ContactItem) item).getContact()).getContactId());
            }

            return true;
        }
    }

    public void setContactsCustomization(ContactsCustomization customization) {
        this.customization = customization;
    }

//    private void registerObserver(boolean register) {
//        NimUserInfoCache.getInstance().registerUserDataChangedObserver(userDataChangedObserver, register);
//        FriendDataCache.getInstance().registerFriendDataChangedObserver(friendDataChangedObserver, register);
//    }

//    private Observer<FriendChangedNotify> friendChangedNotifyObserver;

    /**
     * 监听好友关系变化
     */
//    private void initFriendChangedNotifyObserver() {
//        friendChangedNotifyObserver = new Observer<FriendChangedNotify>() {
//            @Override
//            public void onEvent(FriendChangedNotify friendChangedNotify) {
//                final String account = friendChangedNotify.getAccount();
//                if (friendChangedNotify.getChangeType() == FriendChangedNotify.ChangeType.ADD) {
//                    refresh();
//                    // 新增好友
//                    Toast.makeText(getContext(), "新增好友", Toast.LENGTH_SHORT).show();
//                } else if (friendChangedNotify.getChangeType() == FriendChangedNotify.ChangeType.DELETE) {
//                    // 删除好友或者被解除好友关系
//                    Toast.makeText(getContext(), "删除好友或者被解除好友关系", Toast.LENGTH_SHORT).show();
//                }
//            }
//        };
//        NIMClient.getService(FriendServiceObserve.class).observeFriendChangedNotify(friendChangedNotifyObserver, true);
//    }

    /**
     * 接受系统消息
     */
    public void initSystemMessageObserver() {
        NIMClient.getService(SystemMessageObserver.class)
                .observeReceiveSystemMsg(new Observer<SystemMessage>() {
                    @Override
                    public void onEvent(SystemMessage message) {
                        //判断类型
                        if (message.getType() == SystemMessageType.AddFriend) {
                            AddFriendNotify attachData = (AddFriendNotify) message.getAttachObject();
                            if (attachData != null) {
                                // 针对不同的事件做处理
                                if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_DIRECT) {
                                    // 已添加你为好友
                                    Toast.makeText(getContext(), "已添加你为好友", Toast.LENGTH_SHORT).show();
                                } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_AGREE_ADD_FRIEND) {
                                    // 对方通过了你的好友请求
                                    Toast.makeText(getContext(), "对方通过了你的好友请求", Toast.LENGTH_SHORT).show();
                                } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_REJECT_ADD_FRIEND) {
                                    // 对方拒绝了你的好友请求
                                    Toast.makeText(getContext(), "对方拒绝了你的好友请求", Toast.LENGTH_SHORT).show();
                                } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST) {
                                    Toast.makeText(getContext(), "对方请求添加好友", Toast.LENGTH_SHORT).show();
                                    tv_newfiends_newnotify.setVisibility(View.VISIBLE);
//                                    showTabContactNew(true);
                                    // 对方请求添加好友，一般场景会让用户选择同意或拒绝对方的好友请求。
                                    // 通过message.getContent()获取好友验证请求的附言
                                }
                            }
                        } else if (message.getType() == SystemMessageType.ApplyJoinTeam) {
                            tv_club_newnotify.setVisibility(View.VISIBLE);
//                            showTabContactNew(true);
                        }
                    }
                }, true);
    }

    /**
     * 注册/注销系统消息未读数变化
     *
     * @param register
     */
    private void registerSystemMessageObservers(boolean register) {
        if (register) {
            MessageDataCache.getInstance().registerMessageDataChangedObserverObserver(messageDataChangedObserver);
        } else {
            MessageDataCache.getInstance().unregisterTeamMemberDataChangedObserver(messageDataChangedObserver);
        }
//        NIMClient.getService(SystemMessageObserver.class).observeUnreadCountChange(sysMsgUnreadCountChangedObserver, register);
    }

    private MessageDataCache.MessageDataChangedObserver messageDataChangedObserver = new MessageDataCache.MessageDataChangedObserver() {
        @Override
        public void onMessageUpdate(SystemMessage message) {
            getIsUnReadCount();
        }
    };

    TextView tv_newfiends_newnotify;
    TextView tv_club_newnotify;

    private void initView() {
//        setHeadTitle(view, R.string.main_tab_contacts);
//        TextView tv_head_right = (TextView) view.findViewById(R.id.tv_head_right);
//        tv_head_right.setText(R.string.contacts_new_friend);
//        tv_head_right.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), AddFriendActivity.class));
//            }
//        });
        //
//        lv_contacts = (ListView) view.findViewById(R.id.lv_contacts);
        listView = (ListView) view.findViewById(R.id.contact_list_view);
        //
        View headView = LayoutInflater.from(getContext()).inflate(R.layout.layout_contact_head, null);
        rl_new_friends = (RelativeLayout) headView.findViewById(R.id.rl_new_friends);
        rl_my_group = (RelativeLayout) headView.findViewById(R.id.rl_my_group);
        rl_my_club = (RelativeLayout) headView.findViewById(R.id.rl_my_club);
        tv_newfiends_newnotify = (TextView) headView.findViewById(R.id.tv_newfiends_newnotify);
        tv_club_newnotify = (TextView) headView.findViewById(R.id.tv_club_newnotify);
        rl_new_friends.setOnClickListener(this);
        rl_my_group.setOnClickListener(this);
        rl_my_club.setOnClickListener(this);
        //
        View countView = LayoutInflater.from(getContext()).inflate(R.layout.layout_contact_count, null);
        tv_contacts_num = (TextView) countView.findViewById(R.id.tv_contacts_num);
        tv_contacts_num.setText(String.format(getString(R.string.contancts_num), 0));
        //
        listView.addHeaderView(headView);
        listView.addFooterView(countView);
        initAdapter();
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));

        ContactItemClickListener listener = new ContactItemClickListener();
        listView.setOnItemClickListener(listener);
        listView.setOnItemLongClickListener(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean isShowTabNotify = (tv_newfiends_newnotify.getVisibility() != View.GONE || tv_club_newnotify.getVisibility() != View.GONE);
        getIsUnReadCount();
//        getFriendstatusList();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registerSystemMessageObservers(false);
        registerObserver(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public int unReadClubMessage = 0;
    public int unReadFriendMessage = 0;

    /**
     * 获取未读消息(我的好友，俱乐部)
     */
    protected void getIsUnReadCount() {
//        unReadClubMessage = MessageDataCache.getInstance().getUnreadCount(MessageDataCache.TYPE_MESSAGE_TEAM_INVITE) + MessageDataCache.getInstance().getUnreadCount(MessageDataCache.TYPE_MESSAGE_TEAM_APPLY);
//        unReadFriendMessage = MessageDataCache.getInstance().getUnreadCount(MessageDataCache.TYPE_MESSAGE_FRIEND);
        unReadClubMessage = SystemMessageHelper.querySystemMessageUnreadCountByType(getContext(), SystemMessageHelper.TYPE_MESSAGE_TEAM_INVITE)
                + SystemMessageHelper.querySystemMessageUnreadCountByType(getContext(), SystemMessageHelper.TYPE_MESSAGE_TEAM_APPLY);
        unReadFriendMessage = SystemMessageHelper.querySystemMessageUnreadCountByType(getContext(), SystemMessageHelper.TYPE_MESSAGE_FRIEND);
        updateNewNotifyUI();
//        NIMClient.getService(SystemMessageService.class).querySystemMessages(0, 88888).setCallback(new RequestCallback<List<SystemMessage>>() {
//            @Override
//            public void onSuccess(List<SystemMessage> msgList) {
//                if (msgList != null) {
//                    msgList = MessageDataCache.dealMessageNoRepeat((ArrayList) msgList);
//                    for (SystemMessage message : msgList) {
//                        Log.d("unread", "message : " + message.getContent());
//                        if (message.getStatus() == SystemMessageStatus.init) {
//                            if (message.getType() == SystemMessageType.ApplyJoinTeam) {
//                                unReadClubMessage = unReadClubMessage + 1;
//                            } else if (message.getType() == SystemMessageType.AddFriend) {
//                                unReadFriendMessage = unReadFriendMessage + 1;
//                            }
//                        }
//                    }
//                    Log.d("unread", "unReadFriendMessage : " + unReadFriendMessage + ";unReadClubMessage:" + unReadClubMessage);
//                    updateNewNotifyUI();
//                }
//            }
//
//            @Override
//            public void onFailed(int code) {
//
//            }
//
//            @Override
//            public void onException(Throwable exception) {
//
//            }
//        });
    }

    public void updateNewNotifyUI() {
        if (unReadFriendMessage > 0) {
            tv_newfiends_newnotify.setVisibility(View.VISIBLE);
            tv_newfiends_newnotify.setText(String.valueOf(unReadFriendMessage));
        } else {
            tv_newfiends_newnotify.setVisibility(View.GONE);
        }
        if (unReadClubMessage > 0) {
            tv_club_newnotify.setVisibility(View.VISIBLE);
            tv_club_newnotify.setText(String.valueOf(unReadClubMessage));
        } else {
            tv_club_newnotify.setVisibility(View.GONE);
        }
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateNewSysMessageUI(unReadFriendMessage + unReadClubMessage);
        }
    }

    ArrayList<FriendStatusEntity> friendStatusList;

    /**
     * 获取好友状态列表
     */
//    public void getFriendstatusList() {
//        if (!NetworkUtil.isNetAvailable(getContext())) {
//            Toast.makeText(getContext(), R.string.network_is_not_available, Toast.LENGTH_LONG).show();
//            return;
//        }
//        HashMap<String, String> paramsMap = new HashMap<String, String>();
//        paramsMap.put("uid", UserPreferences.getInstance(getContext()).getUserId());
//        requestUrl = ApiConstants.URL_USER_FRIENDSTATUS + NetWork.getRequestParams(paramsMap);
//        Log.d(TAG, requestUrl);
//        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestUrl, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG, response);
//                try {
//                    JSONObject json = new JSONObject(response);
//                    int code = json.getInt("code");
//                    if (code == 0) {
//                        friendStatusList = JsonResolveUtil.getFriendStatusList(json);
//                        ArrayList<String> accountList = new ArrayList<String>();
//                        for (FriendStatusEntity friendStatusEntity : friendStatusList) {
//                            accountList.add(friendStatusEntity.getUsername());
////                            Log.d(TAG , "acount : " + friendStatusEntity.getUsername() + "; gid : " + friendStatusEntity.getGid());
//                        }
//                        FriendStatusCache.setGamingAccounts(accountList);
//                        adapter.notifyDataSetChanged();
//                    } else {
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (error.getMessage() != null) {
//                    Log.d(TAG, error.getMessage());
//                }
//                Log.d(TAG, "onErrorResponse");
//            }
//        });
//        signRequest.setTag(requestUrl);
//        mRequestQueue.add(signRequest);
//    }

    /**
     * *********************************** 通讯录加载控制 *******************************
     */

    /**
     * 加载通讯录数据并刷新
     *
     * @param reload true则重新加载数据；false则判断当前数据源是否空，若空则重新加载，不空则不加载
     */
    private void reload(boolean reload) {
        if (!reloadControl.canDoReload(reload)) {
            return;
        }
        if (adapter == null) {
            if (getActivity() == null) {
                return;
            }
            initAdapter();
        }
        // 开始加载
        if (!adapter.load(reload)) {
            // 如果不需要加载，则直接当完成处理
            onReloadCompleted();
        }
    }

    private void onReloadCompleted() {
        if (reloadControl.continueDoReloadWhenCompleted()) {
            // 计划下次加载，稍有延迟
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    boolean reloadParam = reloadControl.getReloadParam();
                    LogUtil.i(UIKitLogTag.CONTACT, "continue reload " + reloadParam);
                    reloadControl.resetStatus();
                    reload(reloadParam);
                }
            }, 50);
        } else {
            // 本次加载完成
            reloadControl.resetStatus();
        }

        com.netease.nim.uikit.common.util.log.LogUtil.i(UIKitLogTag.CONTACT, "contact load completed");
    }

    /**
     * 通讯录加载频率控制
     */
    class ReloadFrequencyControl {
        boolean isReloading = false;
        boolean needReload = false;
        boolean reloadParam = false;

        boolean canDoReload(boolean param) {
            if (isReloading) {
                // 正在加载，那么计划加载完后重载
                needReload = true;
                if (param) {
                    // 如果加载过程中又有多次reload请求，多次参数只要有true，那么下次加载就是reload(true);
                    reloadParam = true;
                }
                com.netease.nim.uikit.common.util.log.LogUtil.i(UIKitLogTag.CONTACT, "pending reload task");

                return false;
            } else {
                // 如果当前空闲，那么立即开始加载
                isReloading = true;
                return true;
            }
        }

        boolean continueDoReloadWhenCompleted() {
            return needReload;
        }

        void resetStatus() {
            isReloading = false;
            needReload = false;
            reloadParam = false;
        }

        boolean getReloadParam() {
            return reloadParam;
        }
    }

    /**
     * *********************************** 用户资料、好友关系变更、登录数据同步完成观察者 *******************************
     */

    private void registerObserver(boolean register) {
        if (register) {
            UserInfoHelper.registerObserver(userInfoObserver);
        } else {
            UserInfoHelper.unregisterObserver(userInfoObserver);
        }

        FriendDataCache.getInstance().registerFriendDataChangedObserver(friendDataChangedObserver, register);
        LoginSyncDataStatusObserver.getInstance().observeSyncDataCompletedEvent(loginSyncCompletedObserver);
    }

    FriendDataCache.FriendDataChangedObserver friendDataChangedObserver = new FriendDataCache.FriendDataChangedObserver() {
        @Override
        public void onAddedOrUpdatedFriends(List<String> accounts) {
            reloadWhenDataChanged(accounts, "onAddedOrUpdatedFriends", true);
        }

        @Override
        public void onDeletedFriends(List<String> accounts) {
            reloadWhenDataChanged(accounts, "onDeletedFriends", true);
        }

        @Override
        public void onAddUserToBlackList(List<String> accounts) {
            reloadWhenDataChanged(accounts, "onAddUserToBlackList", true);
        }

        @Override
        public void onRemoveUserFromBlackList(List<String> accounts) {
            reloadWhenDataChanged(accounts, "onRemoveUserFromBlackList", true);
        }
    };

    private UserInfoObservable.UserInfoObserver userInfoObserver = new UserInfoObservable.UserInfoObserver() {
        @Override
        public void onUserInfoChanged(List<String> accounts) {
//            Toast.makeText(getContext() , "onUserInfoChanged" , Toast.LENGTH_SHORT).show();
            reloadWhenDataChanged(accounts, "onUserInfoChanged", true);
        }
    };

    private Observer<Void> loginSyncCompletedObserver = new Observer<Void>() {
        @Override
        public void onEvent(Void aVoid) {
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    reloadWhenDataChanged(null, "onLoginSyncCompleted", false);
                }
            }, 50);
        }
    };

    private void reloadWhenDataChanged(List<String> accounts, String reason, boolean reload) {
        // log
        StringBuilder sb = new StringBuilder();
        sb.append("ContactFragment received data changed as [" + reason + "] : ");
        if (accounts != null && !accounts.isEmpty()) {
            for (String account : accounts) {
                sb.append(account);
                sb.append(" ");
            }
            sb.append(", changed size=" + accounts.size());
        }
        LogUtil.i(UIKitLogTag.CONTACT, sb.toString());
        // reload
        reload(reload);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_new_friends:
                tv_newfiends_newnotify.setVisibility(View.GONE);
                startActivity(new Intent(getActivity(), MessageVerifyActivity.class));
                break;
            case R.id.rl_my_group:
                startActivity(new Intent(getActivity(), GroupListActivity.class));
                break;
            case R.id.rl_my_club:
//                startActivity(new Intent(getActivity(), ClubListActivity.class));
                break;
        }
    }
}
