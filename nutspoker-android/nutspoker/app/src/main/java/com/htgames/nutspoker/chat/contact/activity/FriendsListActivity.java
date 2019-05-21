package com.htgames.nutspoker.chat.contact.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.FriendStatusEntity;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.tool.JsonResolveUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.htgames.nutspoker.ui.action.FriendRelationshipAction;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.view.ContactCountView;
import com.htgames.nutspoker.view.ResultDataView;
import com.netease.nim.uikit.LoginSyncDataStatusObserver;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.UIKitLogTag;
import com.netease.nim.uikit.cache.FriendDataCache;
import com.netease.nim.uikit.chesscircle.FriendStatusCache;
import com.netease.nim.uikit.common.ui.liv.LetterIndexView;
import com.netease.nim.uikit.common.ui.liv.LivIndex;
import com.netease.nim.uikit.contact.ContactsCustomization;
import com.netease.nim.uikit.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.contact.core.item.ContactItem;
import com.netease.nim.uikit.contact.core.item.ItemTypes;
import com.netease.nim.uikit.contact.core.model.ContactDataAdapter;
import com.netease.nim.uikit.contact.core.model.ContactGroupStrategy;
import com.netease.nim.uikit.contact.core.provider.ContactDataProvider;
import com.netease.nim.uikit.contact.core.viewholder.ContactHolder;
import com.netease.nim.uikit.contact.core.viewholder.LabelHolder;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nim.uikit.uinfo.UserInfoHelper;
import com.netease.nim.uikit.uinfo.UserInfoObservable;
import com.netease.nimlib.sdk.Observer;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class FriendsListActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = "FriendsListActivity";
    RelativeLayout rl_friends;
    ContactCountView mContactCountView;
    //
    private ListView listView;
    private ContactDataAdapter adapter;
    private LivIndex litterIdx;
    private ContactsCustomization customization;
    private ReloadFrequencyControl reloadControl = new ReloadFrequencyControl();
    ResultDataView mResultDataView;
    LetterIndexView livIndex;
    public final static int FROM_FRIEND_LIST = 0;
    public final static int FROM_FRIEND_SELECT = 1;
    private int from = FROM_FRIEND_LIST;
    //
    RelativeLayout rl_search;
    RelativeLayout rl_select_club;
    RelativeLayout rl_select_group;
    FriendRelationshipAction mFriendRelationshipAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        from = getIntent().getIntExtra(Extras.EXTRA_FROM, FROM_FRIEND_LIST);
        initView();
        initHead();
//        initFriendChangedNotifyObserver();
//        initSystemMessageObserver();//初始化系统消息监听
        mFriendRelationshipAction = new FriendRelationshipAction(this, null);
        buildLitterIdx(rl_friends);
//        getIsUnReadCount();
        // 注册观察者
        registerObserver(true);
//        registerSystemMessageObservers(true);
        // 加载本地数据
        reload(false);
        updateNullView();
    }

    public void initHeaderView() {
        View headerView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_newcontact_select, null);
        rl_select_club = (RelativeLayout) headerView.findViewById(R.id.rl_select_club);
        rl_select_group = (RelativeLayout) headerView.findViewById(R.id.rl_select_group);
        rl_select_club.setOnClickListener(this);
        rl_select_group.setOnClickListener(this);
        listView.addHeaderView(headerView);
    }

    private void initHead() {
        if (from == FROM_FRIEND_LIST) {
            setHeadTitle(R.string.friend);
            setHeadRightButton(R.string.add, this);
        } else {
            setHeadTitle(R.string.select);
        }
    }

    private static final class ContactsGroupStrategy extends ContactGroupStrategy {
        public ContactsGroupStrategy() {
            add(ContactGroupStrategy.GROUP_NULL, -1, "");
            addABC(0);
        }
    }

    private void initAdapter() {
        adapter = new ContactDataAdapter(this, new ContactsGroupStrategy(), new ContactDataProvider(ItemTypes.FRIEND)) {
            @Override
            protected List<AbsContactItem> onNonDataItems() {
                if (customization != null) {
                    return customization.onGetFuncItems();
                }
                return new ArrayList<>();
            }

            @Override
            protected void onPreReady() {
            }

            @Override
            protected void onPostLoad(boolean empty, String queryText, boolean all) {
//                loadingFrame.setVisibility(View.GONE);
                int userCount = NimUIKit.getContactProvider().getMyFriendsCount();
                mContactCountView.setCount(String.format(getString(R.string.contancts_num), userCount));

                onReloadCompleted();
            }
        };

        adapter.addViewHolder(ItemTypes.LABEL, LabelHolder.class);
        if (customization != null) {
            adapter.addViewHolder(ItemTypes.FUNC, customization.onGetFuncViewHolderClass());
        }
        adapter.addViewHolder(ItemTypes.FRIEND, ContactHolder.class);
    }

    public void updateNullView() {
        int userCount = NimUIKit.getContactProvider().getMyFriendsCount();
        if (userCount == 0) {
            mResultDataView.nullDataShow(R.string.friend_null, R.mipmap.img_friend_null);
            listView.setVisibility(View.GONE);
            livIndex.setVisibility(View.GONE);
        } else {
            mResultDataView.successShow();
            listView.setVisibility(View.VISIBLE);
            livIndex.setVisibility(View.VISIBLE);
        }
    }

    private void buildLitterIdx(View view) {
        livIndex = (LetterIndexView) view.findViewById(R.id.liv_index);
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

            if (from == FROM_FRIEND_LIST) {
                if (type == ItemTypes.FUNC && customization != null) {
                    customization.onFuncItemClick(item);
                    return;
                }

                if (type == ItemTypes.FRIEND && item instanceof ContactItem && NimUIKit.getContactEventListener() != null) {
                    NimUIKit.getContactEventListener().onItemClick(FriendsListActivity.this, (((ContactItem) item).getContact()).getContactId());
                }
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
                NimUIKit.getContactEventListener().onItemLongClick(FriendsListActivity.this, (((ContactItem) item).getContact()).getContactId());
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
//    private void initFriendChangedNotifyObserver() {
//        friendChangedNotifyObserver = new Observer<FriendChangedNotify>() {
//            @Override
//            public void onEvent(FriendChangedNotify friendChangedNotify) {
//                final String account = friendChangedNotify.getAccount();
//                if (friendChangedNotify.getChangeType() == FriendChangedNotify.ChangeType.ADD) {
//                    refresh();
//                    // 新增好友
//                    Toast.makeText(mContext, "新增好友", Toast.LENGTH_SHORT).show();
//                } else if (friendChangedNotify.getChangeType() == FriendChangedNotify.ChangeType.DELETE) {
//                    // 删除好友或者被解除好友关系
//                    Toast.makeText(mContext, "删除好友或者被解除好友关系", Toast.LENGTH_SHORT).show();
//                }
//            }
//        };
//        NIMClient.getService(FriendServiceObserve.class).observeFriendChangedNotify(friendChangedNotifyObserver, true);
//    }
//    public void initSystemMessageObserver() {
//        NIMClient.getService(SystemMessageObserver.class)
//                .observeReceiveSystemMsg(new Observer<SystemMessage>() {
//                    @Override
//                    public void onEvent(SystemMessage message) {
//                        //判断类型
//                        if (message.getType() == SystemMessageType.AddFriend) {
//                            AddFriendNotify attachData = (AddFriendNotify) message.getAttachObject();
//                            if (attachData != null) {
//                                // 针对不同的事件做处理
//                                if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_DIRECT) {
//                                    // 已添加你为好友
//                                    Toast.makeText(getApplicationContext(), "已添加你为好友", Toast.LENGTH_SHORT).show();
//                                } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_AGREE_ADD_FRIEND) {
//                                    // 对方通过了你的好友请求
//                                    Toast.makeText(getApplicationContext(), "对方通过了你的好友请求", Toast.LENGTH_SHORT).show();
//                                } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_REJECT_ADD_FRIEND) {
//                                    // 对方拒绝了你的好友请求
//                                    Toast.makeText(getApplicationContext(), "对方拒绝了你的好友请求", Toast.LENGTH_SHORT).show();
//                                } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST) {
//                                    Toast.makeText(getApplicationContext(), "对方请求添加好友", Toast.LENGTH_SHORT).show();
//                                    tv_newfiends_newnotify.setVisibility(View.VISIBLE);
////                                    showTabContactNew(true);
//                                    // 对方请求添加好友，一般场景会让用户选择同意或拒绝对方的好友请求。
//                                    // 通过message.getContent()获取好友验证请求的附言
//                                }
//                            }
//                        } else if (message.getType() == SystemMessageType.ApplyJoinTeam) {
////                            showTabContactNew(true);
//                        }
//                    }
//                }, true);
//    }
//    private void registerSystemMessageObservers(boolean register) {
//        if (register) {
//            MessageDataCache.getInstance().registerMessageDataChangedObserverObserver(messageDataChangedObserver);
//        } else {
//            MessageDataCache.getInstance().unregisterTeamMemberDataChangedObserver(messageDataChangedObserver);
//        }
////        NIMClient.getService(SystemMessageObserver.class).observeUnreadCountChange(sysMsgUnreadCountChangedObserver, register);
//    }
//
//    private MessageDataCache.MessageDataChangedObserver messageDataChangedObserver = new MessageDataCache.MessageDataChangedObserver() {
//        @Override
//        public void onMessageUpdate(SystemMessage message) {
//            getIsUnReadCount();
//        }
//    };

    private void initView() {
        mResultDataView = (ResultDataView) findViewById(R.id.friends_loading_view);
        rl_friends = (RelativeLayout) findViewById(R.id.rl_friends);

        listView = (ListView) findViewById(R.id.contact_list_view);
        //
//        View headView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_contact_head, null);
//        rl_new_friends = (RelativeLayout) headView.findViewById(R.id.rl_new_friends);
//        tv_newfiends_newnotify = (TextView) headView.findViewById(R.id.tv_newfiends_newnotify);
//        rl_new_friends.setOnClickListener(this);
        //
        mContactCountView = new ContactCountView(getApplicationContext());
        mContactCountView.setCount(String.format(getString(R.string.contancts_num), 0));
        //
        if (from == FROM_FRIEND_SELECT) {
            initHeaderView();
        }
//        listView.addHeaderView(headView);
        listView.addFooterView(mContactCountView);
        initAdapter();
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));

        ContactItemClickListener listener = new ContactItemClickListener();
        listView.setOnItemClickListener(listener);
        listView.setOnItemLongClickListener(listener);

        //
        rl_search = (RelativeLayout) findViewById(R.id.rl_search);

        if (from == FROM_FRIEND_LIST) {
            rl_search.setVisibility(View.GONE);
        } else {
            rl_search.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        getIsUnReadCount();
        getFriendstatusList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        registerSystemMessageObservers(false);
        registerObserver(false);
        if (mFriendRelationshipAction != null) {
            mFriendRelationshipAction.onDestroy();
            mFriendRelationshipAction = null;
        }
    }

    public int unReadFriendMessage = 0;

//    /**
//     * 获取未读消息(我的好友，俱乐部)
//     */
//    protected void getIsUnReadCount() {
//        unReadFriendMessage = MessageDataCache.getInstance().getUnreadCount(MessageDataCache.TYPE_MESSAGE_FRIEND);
//        updateNewNotifyUI();
////        NIMClient.getService(SystemMessageService.class).querySystemMessages(0, 88888).setCallback(new RequestCallback<List<SystemMessage>>() {
////            @Override
////            public void onSuccess(List<SystemMessage> msgList) {
////                if (msgList != null) {
////                    msgList = MessageDataCache.dealMessageNoRepeat((ArrayList) msgList);
////                    for (SystemMessage message : msgList) {
////                        Log.d("unread", "message : " + message.getContent());
////                        if (message.getStatus() == SystemMessageStatus.init) {
////                            if (message.getType() == SystemMessageType.ApplyJoinTeam) {
////                                unReadClubMessage = unReadClubMessage + 1;
////                            } else if (message.getType() == SystemMessageType.AddFriend) {
////                                unReadFriendMessage = unReadFriendMessage + 1;
////                            }
////                        }
////                    }
////                    Log.d("unread", "unReadFriendMessage : " + unReadFriendMessage + ";unReadClubMessage:" + unReadClubMessage);
////                    updateNewNotifyUI();
////                }
////            }
////
////            @Override
////            public void onFailed(int code) {
////
////            }
////
////            @Override
////            public void onException(Throwable exception) {
////
////            }
////        });
//    }

//    public void updateNewNotifyUI() {
//        if (unReadFriendMessage > 0) {
//            tv_newfiends_newnotify.setVisibility(View.VISIBLE);
//            tv_newfiends_newnotify.setText(String.valueOf(unReadFriendMessage));
//        } else {
//            tv_newfiends_newnotify.setVisibility(View.GONE);
//        }
//    }

    ArrayList<FriendStatusEntity> friendStatusList;

    /**
     * 获取好友状态列表
     */
    public void getFriendstatusList() {
        mFriendRelationshipAction.getFriendStatusList(new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if (code == 0) {
                    friendStatusList = JsonResolveUtil.getFriendStatusList(result);
                    ArrayList<String> accountList = new ArrayList<String>();
                    for (FriendStatusEntity friendStatusEntity : friendStatusList) {
                        accountList.add(friendStatusEntity.username);
//                            Log.d(TAG , "acount : " + friendStatusEntity.getUsername() + "; gid : " + friendStatusEntity.getGid());
                    }
                    FriendStatusCache.setGamingAccounts(accountList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailed() {

            }
        });
    }

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

        LogUtil.i(UIKitLogTag.CONTACT, "contact load completed");
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
                LogUtil.i(UIKitLogTag.CONTACT, "pending reload task");

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
//        // log
//        StringBuilder sb = new StringBuilder();
//        sb.append("ContactFragment received data changed as [" + reason + "] : ");
//        if (accounts != null && !accounts.isEmpty()) {
//            for (String account : accounts) {
//                sb.append(account);
//                sb.append(" ");
//            }
//            sb.append(", changed size=" + accounts.size());
//        }
//        LogUtil.i(UIKitLogTag.CONTACT, sb.toString());
        // reload
        reload(reload);
        updateNullView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_head_right:
                startActivity(new Intent(this, AddFriendActivity.class));
                break;
//            case R.id.rl_new_friends:
////                tv_newfiends_newnotify.setVisibility(View.GONE);
//                startActivity(new Intent(this, MessageVerifyActivity.class));
//                break;
            case R.id.rl_select_club:
//                ClubListActivity.start(FriendsListActivity.this, ClubListActivity.FROM_SELECT);
                break;
            case R.id.rl_select_group:
//                GroupListActivity.start(FriendsListActivity.this, GroupListActivity.FROM_SELECT);
                break;
        }
    }
}
