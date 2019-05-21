package com.htgames.nutspoker.ui.fragment.main;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.app_msg.activity.AppMsgControlAC;
import com.htgames.nutspoker.chat.app_msg.activity.AppMsgInfoAC;
import com.htgames.nutspoker.chat.app_msg.helper.HordeMessageHelper;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageStatus;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageType;
import com.htgames.nutspoker.chat.app_msg.receiver.AppMessageReceiver;
import com.htgames.nutspoker.chat.app_msg.view.AppMessageRecentView;
import com.htgames.nutspoker.chat.msg.helper.SystemMessageHelper;
import com.htgames.nutspoker.chat.msg.model.SystemMessage;
import com.htgames.nutspoker.chat.reminder.ReminderId;
import com.htgames.nutspoker.chat.reminder.ReminderItem;
import com.htgames.nutspoker.chat.reminder.ReminderManager;
import com.htgames.nutspoker.db.AppMsgDBHelper;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.net.RequestTimeLimit;
import com.htgames.nutspoker.ui.action.SearchAction;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.htgames.nutspoker.ui.adapter.FrgDiscoP2PAdapter;
import com.htgames.nutspoker.ui.base.BaseFragment;
import com.htgames.nutspoker.ui.recycler.MeRecyclerView;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.nav.Nav;
import com.netease.nim.uikit.nav.UrlConstants;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.msg.constant.SystemMessageStatus;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 发现
 */
public class DiscoveryFragment extends BaseFragment implements View.OnClickListener{
    public final static String TAG = DiscoveryFragment.class.getSimpleName();
    AppMessageRecentView mAppMessageView;
    AppMessageRecentView mAppMessageControl;
    AppMessageRecentView mAppMessageClub;
    MeRecyclerView frg_discovery_p2p_msg_recycler;
    BannerViewPager view_pager_auto_notice;
    DiscoveryBannerIndicator discovery_banner_indicator;
    DiscoveryBannerAdapter noticeAdapter;
    FrgDiscoP2PAdapter p2PAdapter;
    View view;
    SearchAction mSearchAction;
    public static boolean updateClubNum = false;
    public static boolean updateAppInfoNum = false;
    public static boolean updateAppControlNum = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtil.i("zzh", TAG + "onAttach " + getActivity() + " " + isAdded());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            onPause();
        } else {
            onResume();
        }
        super.onHiddenChanged(hidden);
    }

    public static DiscoveryFragment newInstance() {
        DiscoveryFragment mInstance = new DiscoveryFragment();
        return mInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i("zzh", TAG + "onCreate " + getActivity() + " " + isAdded());
        mSearchAction = new SearchAction(getActivity(), null);
        setFragmentName(TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LogUtil.i("zzh", TAG + "onCreateView " + getActivity() + " " + isAdded());
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_discovery, container, false);
        setHeadTitle(view, R.string.main_tab_discovery);
        initView();
        return view;
    }

    private void fetchNotice() {
        mSearchAction.fetchBanner(new GameRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (response == null || getActivity() == null) {
                    return;
                }
                mNoticeData.clear();
                try {
                    JSONArray datas = response.optJSONArray("data");
                    for (int i = 0; i < datas.length(); i++) {
                        JSONObject itemObject = datas.getJSONObject(i);
                        BannerItem item = new BannerItem();
                        item.picUrl = itemObject.optString("img");
                        item.href = itemObject.optString("url");
                        mNoticeData.add(item);
                    }
                    discovery_banner_indicator.setNumOfCircles(mNoticeData.size());
                    noticeAdapter = new DiscoveryBannerAdapter(getActivity(), getActivity());
                    noticeAdapter.addAll(mNoticeData);
                    view_pager_auto_notice.setAdapter(noticeAdapter);
                    view_pager_auto_notice.setCurrentItem(mNoticeData.size() == 1 ? 0 : 500);
                    initAutoNextHandler(mNoticeData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailed(int code, JSONObject response) {
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.i("zzh", TAG + "onActivityCreated " + getActivity() + " " + isAdded());
        registerObservers(true);
//        if (getActivity() instanceof MainActivity && ((MainActivity) getActivity()).hasShowFirstFragment == false) {
////            ((MainActivity) getActivity()).showFirstFragment();
//        }
        updateP2PMsg();//onActivityCreated
        updateClubInfo(null);//onActivityCreated
        updateAppMessageUI(null);//onActivityCreated
    }

    private void initView() {
        View view_pager_container = view.findViewById(R.id.view_pager_container);//750 * 210
        ((LinearLayout.LayoutParams) view_pager_container.getLayoutParams()).height = (int) (ScreenUtil.screenWidth * 210f / 750);
        discovery_banner_indicator = (DiscoveryBannerIndicator) view.findViewById(R.id.discovery_banner_indicator);
        view_pager_auto_notice = (BannerViewPager) view.findViewById(R.id.view_pager_auto_notice);
        view_pager_auto_notice.customIndicator(discovery_banner_indicator);
        mAppMessageView = (AppMessageRecentView) view.findViewById(R.id.mAppMessageView);
        mAppMessageView.setOnClickListener(this);
        mAppMessageView.setBigIcon(R.mipmap.message_system);
        mAppMessageView.setTitle(R.string.app_message);
        mAppMessageView.type = AppMsgDBHelper.TYPE_NOTICE;
        mAppMessageControl = (AppMessageRecentView) view.findViewById(R.id.mAppMessageControl);
        mAppMessageControl.setOnClickListener(this);
        mAppMessageControl.setBigIcon(R.mipmap.message_control);
        mAppMessageControl.setTitle(R.string.app_message_control_center);
        mAppMessageControl.type = AppMsgDBHelper.TYPE_CONTROL_CENTER;
        mAppMessageClub = (AppMessageRecentView) view.findViewById(R.id.mAppMessageClub);
        mAppMessageClub.setOnClickListener(this);
        mAppMessageClub.setBigIcon(R.mipmap.message_club);
        mAppMessageClub.setTitle(R.string.apply_notice);
        p2PAdapter = new FrgDiscoP2PAdapter(getActivity());
        final int DECORATION_SPACE_THIN = ScreenUtil.dp2px(getContext(), 10);
        frg_discovery_p2p_msg_recycler = (MeRecyclerView) view.findViewById(R.id.frg_discovery_p2p_msg_recycler);
        frg_discovery_p2p_msg_recycler.setAdapter(p2PAdapter);
    }

    public void registerObservers(boolean register) {
        if (register) {
            //注册未读消息数量观察者
            ReminderManager.getInstance().registerUnreadNumChangedCallback(unreadNumChangedCallback);
        } else {
            ReminderManager.getInstance().unregisterUnreadNumChangedCallback(unreadNumChangedCallback);
        }
    }

    ReminderManager.UnreadNumChangedCallback unreadNumChangedCallback = new ReminderManager.UnreadNumChangedCallback() {
        @Override
        public void onUnreadNumChanged(ReminderItem item) {
            if (item == null) {
                return;
            }
            if (item.getId() == ReminderId.SESSION) {
            } else if (item.getId() == ReminderId.CONTACT) {
            } else if (item.getId() == ReminderId.SYSTEM_MESSAGE) {
            } else if (item.getId() == ReminderId.APP_MESSAGE) {
            } else if (item.getId() == ReminderId.HORDE_MESSAGE) {
            }
        }
    };

    private ArrayList<AppMessage> allNoticeList = new ArrayList<AppMessage>();
    private ArrayList<AppMessage> allControlList = new ArrayList<AppMessage>();
    private int unreadNoticeCount;
    private int unHandledControlCount;
    public void updateAppMessageUI(AppMessage message) {
        //todo这四个   // TODO: 16/12/5 这四个sql查询条件有待优化，用两个或3个完成
        allNoticeList = AppMsgDBHelper.queryAppMessage(getContext(), AppMsgDBHelper.TYPE_NOTICE);//所有 系统消息, 包括 未读 和 已读
        allControlList = AppMsgDBHelper.queryAppMessage(getContext(), AppMsgDBHelper.TYPE_CONTROL_CENTER);//所有 系统消息, 包括 未读 和 已读
//        unreadNoticeCount = AppMsgDBHelper.queryAppMessageUnreadCountByType(getContext(), AppMsgDBHelper.TYPE_NOTICE);//系统消息, 未读
//        unHandledControlCount = AppMsgDBHelper.queryAppMessageInitCountByType(getContext(), AppMsgDBHelper.TYPE_CONTROL_CENTER);// 原始的消息，未做任何处理，但是可能已读，和"系统消息"不一样
        unreadP2PMsg = 0;
        for (Map.Entry<String, RecentContact> entry : ChessApp.unreadP2PMsg.entrySet()) {
            unreadP2PMsg += entry.getValue().getUnreadCount();
        }
        if (mAppMessageView == null || mAppMessageControl == null) {
            return;//只要有一个为null就返回，因为这两个view基本都是同时为null，同时不为null
        }
        mAppMessageView.nullData(allNoticeList.size());
        mAppMessageControl.nullData(allControlList.size());
        //处理"系统消息"
        unreadNoticeCount = 0;
        for (int i = 0; i < allNoticeList.size(); i++) {
            if (i == 0) {
                mAppMessageView.refresh(allNoticeList.get(0));//第0个是最新的
            }
            if (allNoticeList.get(i).unread == true) {
                unreadNoticeCount = unreadNoticeCount + 1;
            }
        }
        mAppMessageView.updateNewIndicator(unreadNoticeCount);
        mAppMessageView.setVisibility(View.VISIBLE);//((allNoticeList != null && allNoticeList.size() > 0) ? View.VISIBLE : View.GONE);
        unHandledControlCount = 0;
        for (int i = 0; i < allControlList.size(); i++) {
            if (i == 0) {
                mAppMessageControl.refresh(allControlList.get(0));//第0个是最新的
            }
            if (allControlList.get(i).status == AppMessageStatus.init) {
                unHandledControlCount = unHandledControlCount + 1;
            }
        }
        mAppMessageControl.updateNewIndicator(unHandledControlCount);
        mAppMessageControl.setVisibility(View.VISIBLE);//mAppMessageControl.setVisibility(unHandledControlCount > 0 ? View.VISIBLE : View.GONE);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateDiscoveryTabUI(unreadNoticeCount + unHandledControlCount + unreadP2PMsg + unreadClubInfoNum);
        }
        //同时处理"系统消息"和"控制中心"
        if (message != null) {
            if ((message.type == AppMessageType.MatchBuyChips || message.type == AppMessageType.GameBuyChips)) {
                //处理"控制中心"
                mAppMessageControl.refresh(message);
            } else {
                //处理"系统消息"
                mAppMessageView.refresh(message);
            }
        }
    }

    int unreadP2PMsg = 0;
    public void updateP2PMsg() {//处理私聊消息
        unreadP2PMsg = 0;
        for (Map.Entry<String, RecentContact> entry : ChessApp.unreadP2PMsg.entrySet()) {
            unreadP2PMsg += entry.getValue().getUnreadCount();
        }
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateDiscoveryTabUI(unreadNoticeCount + unHandledControlCount + unreadP2PMsg + unreadClubInfoNum);
        }
        if (p2PAdapter != null) {
            p2PAdapter.notifyDataSetChanged();
        }
    }

    private int unreadClubInfoNum = 0;
    ArrayList<SystemMessage> messagesList = new ArrayList<SystemMessage>();
    public void updateClubInfo(SystemMessage newMsg) {//处理 俱乐部消息
        unreadClubInfoNum = 0;
        messagesList.clear();
        messagesList.addAll(HordeMessageHelper.queryHordeMessageByType(getActivity(), HordeMessageHelper.SEARCH_TYPE_ALL));
        messagesList.addAll(SystemMessageHelper.querySystemMessageByType(getActivity() , SystemMessageHelper.TYPE_MESSAGE_TEAM_ALL, ""));
        Collections.sort(messagesList, new Comparator<SystemMessage>() {
            @Override
            public int compare(SystemMessage o1, SystemMessage o2) {
                return o1.time < o2.time ? 1 : (o1.time == o2.time ? 0 : -1);
            }
        });
        for (int i = 0; i < messagesList.size(); i++) {
            SystemMessage systemMessage = messagesList.get(i);
            if (i == 0) {
                mAppMessageClub.refreshClubInfo(systemMessage);//第0个是最新的
            }
            if (systemMessage.status == SystemMessageStatus.init) {
                unreadClubInfoNum++;
            }
        }
        unreadP2PMsg = 0;
        for (Map.Entry<String, RecentContact> entry : ChessApp.unreadP2PMsg.entrySet()) {
            unreadP2PMsg += entry.getValue().getUnreadCount();
        }
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateDiscoveryTabUI(unreadNoticeCount + unHandledControlCount + unreadP2PMsg + unreadClubInfoNum);
        }
        mAppMessageClub.updateNewIndicator(unreadClubInfoNum);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (updateClubNum) {
            updateClubInfo(null);//onResume
            updateClubNum = false;
        }
        if (updateAppInfoNum || updateAppControlNum) {
            updateAppMessageUI(null);//onResume
            updateAppInfoNum = false;
            updateAppControlNum = false;
        }
        if (mNoticeData == null || mNoticeData.size() <= 0) {
            fetchNotice();//请求网络获取自动滑动的viewpager的内容
        }
        LogUtil.i("zzh", TAG + "onResume " + getActivity() + " " + isAdded());
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.i("zzh", TAG + "onPause " + getActivity() + " " + isAdded());
    }

    @Override
    public void onDestroy() {
        registerObservers(false);
        if (autoNextHandler != null) {
            autoNextHandler.removeMessages(0);
        }
        autoNextHandler = null;
        if (mSearchAction != null) {
            mSearchAction.onDestroy();
            mSearchAction = null;
        }
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mAppMessageView:
                AppMsgInfoAC.start(getActivity());
                break;
            case R.id.mAppMessageControl:
                AppMsgControlAC.start(getActivity());
                break;
            case R.id.mAppMessageClub:
//                Intent intent = new Intent(getActivity() , MessageVerifyAC.class);
//                intent.putExtra(Extras.EXTRA_MESSAGE_TYPE , SystemMessageHelper.TYPE_MESSAGE_TEAM_APPLY);
//                intent.putExtra(Extras.EXTRA_TEAM_ID , team.getId());
//                activity.startActivity(intent);
                Nav.from(getActivity()).toUri(UrlConstants.CLUB_MESSAGE_VERIFY);//MessageVerifyAC
                break;
        }
    }

    public static final int INTERVAL = 4000;//每4秒轮播一次
    private Handler autoNextHandler;
    private boolean mDelayAutoNext = false;
    private List<BannerItem> mNoticeData = new ArrayList<BannerItem>();
    private void initAutoNextHandler(Collection<? extends Object> data) {
        if (data == null || data.size() <= 1) {
            if (autoNextHandler != null) {
                autoNextHandler.removeMessages(0);
            }
            autoNextHandler = null;
            return;
        }
        if (autoNextHandler == null) {
            autoNextHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (!mDelayAutoNext) {
                        if (view_pager_auto_notice.isBeingTouched()) {
                            // 按中时banner不轮播，什么都不做do nothing
                        } else {
                            int nextIndex = view_pager_auto_notice.currentBannerIndex + 1;
                            view_pager_auto_notice.setCurrentItem(nextIndex, true);
                        }
                    }
                    if (autoNextHandler != null) {
                        autoNextHandler.sendEmptyMessageDelayed(0, INTERVAL);
                    }
                }
            };
            autoNextHandler.sendEmptyMessageDelayed(0, INTERVAL);
            view_pager_auto_notice.setAutoNextHandler(autoNextHandler);
        }
    }
    public static class BannerItem implements Serializable {
        public String picUrl;
        public String href;

        @Override
        public String toString() {
            return "BannerItem{" +
                    "picUrl='" + picUrl + '\'' +
                    ", href='" + href + '\'' +
                    '}';
        }
    }
}
