package com.htgames.nutspoker.ui.fragment.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.contact.activity.EditUserInfoActivity;
import com.htgames.nutspoker.chat.helper.UserUpdateHelper;
import com.htgames.nutspoker.ui.action.EditUserInfoAction;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.htgames.nutspoker.ui.activity.System.ShopActivity;
import com.htgames.nutspoker.ui.base.BaseFragment;
import com.htgames.nutspoker.ui.recycler.MeRecycAdapter;
import com.htgames.nutspoker.ui.recycler.MeRecyclerView;
import com.htgames.nutspoker.ui.recycler.MeVH;
import com.htgames.nutspoker.view.UserInfoView;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.api.ApiConstants;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.session.actions.PickImageAction;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.nos.NosService;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by 20150726 on 2015/9/22.
 */
public class MeFragment extends BaseFragment implements View.OnClickListener {
    private final static String TAG = "MeFragment";
    View view;
    private MeRecyclerView mRecyclerView;
    private MeRecycAdapter mAdapter;
    private ArrayList<MeRecycAdapter.MeItemData> mData = new ArrayList<MeRecycAdapter.MeItemData>();
    ImageView iv_dealer_new;
    private View editUserinfoIV;
    String account;
    private NimUserInfo userInfo;
    UserInfoView mUserInfoView;
    public int unReadDealarMessage = 0;
    EditUserInfoAction mEditUserInfoAction;

    public static MeFragment newInstance() {
        MeFragment mInstance = new MeFragment();
        return mInstance;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            updataInfoUI();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        account = DemoCache.getAccount();
        mEditUserInfoAction = new EditUserInfoAction(getActivity(), null);
        setFragmentName("MeFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_me, container, false);
        initView();
        setUnReadDealarMessage(unReadDealarMessage);//初始化
//        initSystemMessageObserver();//初始化系统消息监听
//        getIsUnReadCount();
        // 注册观察者
//        registerSystemMessageObservers(true);
        return view;
    }

    public void setUnReadDealarMessage(int unReadDealarMessage) {
        this.unReadDealarMessage = unReadDealarMessage;
        if(iv_dealer_new != null) {
            iv_dealer_new.setVisibility(unReadDealarMessage > 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.d(TAG, "onResume");
        if (!isUploadingAvatar) {//如果不在上传头像，更新"我的"界面UI
            updataInfoUI();
        }
    }

    public void updataInfoUI(){
        getUserInfo(false);
        getAmount();
        updateAmountUI();
    }

    public void getAmount(){
        if(getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity)getActivity()).getAmount();
        }
    }

    public void updateAmountUI() {
        if (mUserInfoView != null) {
            mUserInfoView.updataWealthView();
        }
    }

    /**
     * 获取未读消息(我的好友，俱乐部)
     */
//    protected void getIsUnReadCount() {
//        unReadClubMessage = MessageDataCache.getInstance().getUnreadCount(MessageDataCache.TYPE_MESSAGE_TEAM_INVITE) + MessageDataCache.getInstance().getUnreadCount(MessageDataCache.TYPE_MESSAGE_TEAM_APPLY);
//        unReadFriendMessage = MessageDataCache.getInstance().getUnreadCount(MessageDataCache.TYPE_MESSAGE_FRIEND);
//        updateNewNotifyUI();
//    }

//    /**
//     * 接受系统消息
//     */
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
//                                    Toast.makeText(getContext(), "已添加你为好友", Toast.LENGTH_SHORT).show();
//                                } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_AGREE_ADD_FRIEND) {
//                                    // 对方通过了你的好友请求
//                                    Toast.makeText(getContext(), "对方通过了你的好友请求", Toast.LENGTH_SHORT).show();
//                                } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_REJECT_ADD_FRIEND) {
//                                    // 对方拒绝了你的好友请求
//                                    Toast.makeText(getContext(), "对方拒绝了你的好友请求", Toast.LENGTH_SHORT).show();
//                                } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST) {
//                                    Toast.makeText(getContext(), "对方请求添加好友", Toast.LENGTH_SHORT).show();
//                                    iv_friend_new.setVisibility(View.VISIBLE);
////                                    showTabContactNew(true);
//                                    // 对方请求添加好友，一般场景会让用户选择同意或拒绝对方的好友请求。
//                                    // 通过message.getContent()获取好友验证请求的附言
//                                }
//                            }
//                        } else if (message.getType() == SystemMessageType.ApplyJoinTeam) {
//                            iv_club_new.setVisibility(View.VISIBLE);
////                            showTabContactNew(true);
//                        }
//                    }
//                }, true);
//    }

//    /**
//     * 注册/注销系统消息未读数变化
//     * @param register
//     */
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

    /**
     * 获取用户信息
     */
    public void getUserInfo(boolean isNet) {
        userInfo = NimUserInfoCache.getInstance().getUserInfo(account);
        if (userInfo == null || isNet) {
            NimUserInfoCache.getInstance().getUserInfoFromRemote(account, new RequestCallback<NimUserInfo>() {
                @Override
                public void onSuccess(NimUserInfo param) {
                    userInfo = param;
                    updateUI();
                }
                @Override
                public void onFailed(int code) {
                }
                @Override
                public void onException(Throwable exception) {
                }
            });
        } else {
            updateUI();
        }
    }

    private void updateUI() {
        mUserInfoView.updateUserInfo(userInfo);
    }

    private void initView() {
        final  MeVH.IShareInMe shareInterface = new MeVH.IShareInMe() {
            @Override
            public void share() {
                if(getActivity() != null && getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showShareView();
                }
            }
        };
        MeRecycAdapter.MeItemData shopItem = new MeRecycAdapter.MeItemData(R.string.me_column_shop, R.mipmap.me_icon_shop, "http://www.baidu.com", MeRecycAdapter.ITEM_TYPE_NORMAL);
        MeRecycAdapter.MeItemData settingItem = new MeRecycAdapter.MeItemData(R.string.me_column_settings, R.mipmap.me_icon_setting, "http://www.baidu.com", MeRecycAdapter.ITEM_TYPE_NORMAL);
        MeRecycAdapter.MeItemData dividerItem = new MeRecycAdapter.MeItemData(0, 0, "", MeRecycAdapter.ITEM_TYPE_EMPTY_DIVIDER);
        MeRecycAdapter.MeItemData aboutUsItem = new MeRecycAdapter.MeItemData(R.string.settings_column_aboutus, R.mipmap.me_icon_about, "http://www.baidu.com", MeRecycAdapter.ITEM_TYPE_NORMAL);
        MeRecycAdapter.MeItemData shareItem = new MeRecycAdapter.MeItemData(R.string.me_column_share, R.mipmap.me_icon_share, "http://www.baidu.com", MeRecycAdapter.ITEM_TYPE_NORMAL);
        MeRecycAdapter.MeItemData dealerItem = new MeRecycAdapter.MeItemData(R.string.dealer, R.mipmap.me_icon_dealer, "http://www.baidu.com", MeRecycAdapter.ITEM_TYPE_NORMAL);
        MeRecycAdapter.MeItemData protocolItem = new MeRecycAdapter.MeItemData(R.string.protocol, R.mipmap.icon_protocol, ApiConstants.URL_PROTOCOL_REGISTER, MeRecycAdapter.ITEM_TYPE_NORMAL);
        mData.add(shopItem);
        mData.add(settingItem);
        mData.add(dividerItem);
        mData.add(aboutUsItem);
        mData.add(protocolItem);
        mData.add(shareItem);//add顺序不能变
        mData.add(dealerItem);

        mAdapter = new MeRecycAdapter(getActivity());
        mAdapter.setShareInterface(shareInterface);//上下不能调换位置，先设接口再设数据
        mAdapter.setData(mData);
        mRecyclerView = (MeRecyclerView) view.findViewById(R.id.me_below_info_items_container);
        mRecyclerView.setAdapter(mAdapter);
        mUserInfoView = (UserInfoView) view.findViewById(R.id.mUserInfoView);
        mUserInfoView.setOnUserFuntionClick(new UserInfoView.OnUserFuntionClick() {
            @Override
            public void onEditInfo() {
                getActivity().startActivityForResult(new Intent(getActivity(), EditUserInfoActivity.class), MainActivity.REQUESTCODE_EDIT);
            }
            @Override
            public void onGoShop(int shopType) {
                ShopActivity.start(getActivity(), shopType);
            }
        });
        editUserinfoIV = view.findViewById(R.id.me_edit_userinfo_iv);
        editUserinfoIV.setOnClickListener(this);
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
        if (mEditUserInfoAction != null) {
            mEditUserInfoAction.onDestroy();
            mEditUserInfoAction = null;
        }
        super.onDestroy();
//        registerSystemMessageObservers(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.me_edit_userinfo_iv:
                getActivity().startActivityForResult(new Intent(getActivity(), EditUserInfoActivity.class), MainActivity.REQUESTCODE_EDIT);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isUploadingAvatar = false;//是否正在上传头像
    AbortableFuture<String> uploadAvatarFuture;
    public void updateAvatar(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        File file = new File(path);
        if (file == null) {
            return;
        }
        DialogMaker.showProgressDialog(getContext(), null, null, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancelUpload(R.string.user_info_update_cancel);
            }
        }).setCanceledOnTouchOutside(true);
        isUploadingAvatar = true;
        LogUtil.i(TAG, "start upload avatar, local file path=" + file.getAbsolutePath());
        new Handler(Looper.getMainLooper()).postDelayed(outimeTask, EditUserInfoActivity.AVATAR_TIME_OUT);
        uploadAvatarFuture = NIMClient.getService(NosService.class).upload(file, PickImageAction.MIME_JPEG);
        uploadAvatarFuture.setCallback(new RequestCallbackWrapper<String>() {
            @Override
            public void onResult(int code, final String url, Throwable exception) {
                if (code == ResponseCode.RES_SUCCESS && !TextUtils.isEmpty(url)) {
                    LogUtil.i(TAG, "upload avatar success, url =" + url);
                    LogUtil.i(TAG, "url : " + url);
                    UserUpdateHelper.update(UserInfoFieldEnum.AVATAR, url, new RequestCallbackWrapper<Void>() {
                        @Override
                        public void onResult(int code, Void result, Throwable exception) {
                            if (code == ResponseCode.RES_SUCCESS) {
                                Toast.makeText(getContext(), "提交成功，系统将在24小时内审核", Toast.LENGTH_LONG).show();//R.string.head_update_success
                                updateUserHead(url);
                                onUpdateDone();
                            } else {
                                Toast.makeText(getContext(), ChessApp.sAppContext.getResources().getString(R.string.head_update_failed), Toast.LENGTH_SHORT).show();
                                isUploadingAvatar = false;
                            }
                        }
                    }); // 更新资料
                } else {
                    Toast.makeText(getContext(), ChessApp.sAppContext.getResources().getString(R.string.user_info_update_failed), Toast.LENGTH_SHORT).show();
                    onUpdateDone();
                }
            }
        });
    }

    private void cancelUpload(int resId) {
        if (uploadAvatarFuture != null) {
            uploadAvatarFuture.abort();
            Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
            onUpdateDone();
        }
    }

    private void onUpdateDone() {
        uploadAvatarFuture = null;
        DialogMaker.dismissProgressDialog();
        getUserInfo(false);
        isUploadingAvatar = false;
    }

    /**
     * 上次头像到APP的服务器
     *
     * @param url
     */
    public void updateUserHead(String url) {
        mEditUserInfoAction.updateUserInfo(null, url, null, null, null, null);
    }

    private Runnable outimeTask = new Runnable() {
        @Override
        public void run() {
            cancelUpload(R.string.user_info_update_failed);
        }
    };
}