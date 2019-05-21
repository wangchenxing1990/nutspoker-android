package com.htgames.nutspoker.game.match.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiCode;
import com.htgames.nutspoker.api.ApiResultHelper;
import com.htgames.nutspoker.chat.app_msg.attach.MatchBuyChipsResultNotify;
import com.htgames.nutspoker.chat.app_msg.interfaces.AppMessageListener;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageStatus;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageType;
import com.htgames.nutspoker.chat.app_msg.viewholder.BuyChipsVHNew;
import com.htgames.nutspoker.chat.reminder.ReminderManager;
import com.htgames.nutspoker.db.AppMsgDBHelper;
import com.htgames.nutspoker.game.helper.BuyChipsResultHelper;
import com.htgames.nutspoker.game.match.activity.MatchRoomActivity;
import com.htgames.nutspoker.game.match.adapter.MatchPlayerAdapter;
import com.htgames.nutspoker.game.match.bean.MatchPlayerEntity;
import com.htgames.nutspoker.game.match.bean.MatchStatusEntity;
import com.htgames.nutspoker.game.match.interfaces.IMsgExpired;
import com.htgames.nutspoker.game.match.item.MatchPlayerItemType;
import com.htgames.nutspoker.game.match.item.PlayerSection;
import com.htgames.nutspoker.game.model.CheckInStatus;
import com.htgames.nutspoker.game.model.GameMatchBuyType;
import com.htgames.nutspoker.game.model.GameStatus;
import com.htgames.nutspoker.game.receiver.CheckInDealReceiver;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.interfaces.VolleyCallback;
import com.htgames.nutspoker.ui.action.GameAction;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.ui.base.BaseFragment;
import com.htgames.nutspoker.ui.recycler.MeRecyclerView;
import com.htgames.nutspoker.view.ResultDataView;
import com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.AnimUtil;
import com.netease.nim.uikit.bean.BaseMttConfig;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.bean.PineappleConfigMtt;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.framework.ThreadUtil;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.interfaces.IClick;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.team.model.Team;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * MTT奖励页面
 */
public class MatchMgrFragment extends BaseFragment implements View.OnClickListener {
    public final static String TAG = "MatchMgrFragment";
    View view;
    View mgr_info_container;
    View mgr_sort_container;
    private GameAction mGameAction;//用来处理移除报名玩家
    int gameStatus = GameStatus.GAME_STATUS_WAIT;
    MatchStatusEntity matchStatusEntity;
    GameEntity gameInfo;
    private boolean isShowNormalAdapter = true;//是显示正常的adapter还是搜索adapter
    private ArrayList<MatchPlayerEntity> playerList = new ArrayList<MatchPlayerEntity>();
    private ArrayList<MatchPlayerEntity> searchList = new ArrayList<MatchPlayerEntity>();
    ArrayList<AppMessage> appMessageList = new ArrayList<AppMessage>();
    MatchPlayerAdapter adapter;
    MatchPlayerAdapter searchAdapter;
    //////////////////////////////////
    TextView mgr_gamecode;
    TextView mgr_action_checkin_cnt;
    TextView mgr_action_buyin_cnt;
    ClearableEditTextWithIcon edt_player_search;
    View tv_search_cancel;
    //搜索和排序相关
    public int rank_type = 1;//1名次  2猎头  3赏金 4身价
    View rl_player_search;
    View rl_player_search_btn;
    TextView tv_player_search_hint;
    TextView tv_match_sort_type;
    ImageView iv_match_sort_type;
    TextView match_sort_hunter_mark;
    View match_sort_type_container;
    ///////////////
    public SwipeRefreshLayout refresh_layout;
    ResultDataView mResultDataView;
    MeRecyclerView mRecyclerView;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        LogUtil.i(TAG + " onVisible");
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();
    }

    public static MatchMgrFragment newInstance(GameEntity gameEntity) {
        MatchMgrFragment mInstance = new MatchMgrFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Extras.EXTRA_DATA, gameEntity);
        mInstance.setArguments(bundle);
        return mInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG + " onCreate");
        mGameAction = new GameAction(getActivity(), null);
        setFragmentName("MatchMgrFragment");
        gameInfo = (GameEntity) getArguments().getSerializable(Extras.EXTRA_DATA);
        gameStatus = gameInfo.status;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtil.i(TAG + " onCreateView");
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_match_mgr, container, false);
        initAdapter();
        initView();
        isFragmentCreated = true;
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof MatchRoomActivity) {
            ((MatchRoomActivity) getActivity()).lastGetMgrPlayerTime = 0;
            ((MatchRoomActivity) getActivity()).getMgrPlayerList();//开赛前只请求一次，开赛后10秒一次请求 onActivityCreated
        }
        ThreadUtil.Execute(new Runnable() {
            @Override
            public void run() {
                getAppMessageList();
            }
        });
    }

    private void getAppMessageList() {
        AppMsgDBHelper.resetMatchBuyChipsUnreadCountByGid(getContext(), gameInfo.gid);
        ArrayList<AppMessage> loaclAppMessageList = AppMsgDBHelper.queryInitMatchBuyChipsByGid(getContext(), gameInfo.gid);
        if (loaclAppMessageList != null && loaclAppMessageList.size() != 0) {
            appMessageList.addAll(loaclAppMessageList);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                updateBuyChipsNotify();
                registerObservers(true);
            }
        });
    }

    public void registerObservers(boolean register) {
        try {
            if (register) {
                IntentFilter checkInIntentFilter = new IntentFilter(CheckInDealReceiver.ACTION_CHECKIN);
                getActivity().registerReceiver(checkInDealReceiver, checkInIntentFilter);
            } else {
                getActivity().unregisterReceiver(checkInDealReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 游戏内部处理报名请求结果通知
     */
    CheckInDealReceiver checkInDealReceiver = new CheckInDealReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            try {
                if (intent != null && intent.getSerializableExtra(Extras.EXTRA_APP_MESSAGE) != null) {
                    AppMessage appMessage = (AppMessage) intent.getSerializableExtra(Extras.EXTRA_APP_MESSAGE);
                    if (appMessage != null && appMessage.type == AppMessageType.MatchBuyChips && appMessage.sortKey.equals(gameInfo.gid)) {
                        //消息是1.买入 2.当前牌局
                        for (AppMessage currentAppMessage : appMessageList) {
                            if (currentAppMessage.type == AppMessageType.MatchBuyChips
                                    && !TextUtils.isEmpty(currentAppMessage.key)
                                    && currentAppMessage.key.equals(appMessage.key)
                                    && currentAppMessage.type == appMessage.type
                                    && currentAppMessage.checkinPlayerId.equals(appMessage.checkinPlayerId)) {
                                appMessageList.remove(currentAppMessage);
                                break;
                            }
                        }
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                        updateBuyChipsNotify();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    public synchronized void onIncommingMessage(AppMessage appMessage) {
        if (!appMessage.sortKey.equals(gameInfo.gid)) {
            return;
        }
        if (appMessage.type == AppMessageType.MatchBuyChips) {
            //消息是1.买入 2.当前牌局
            for (int i = 0; i < appMessageList.size(); i++) {
                AppMessage currentAppMessage = appMessageList.get(i);
                if (currentAppMessage.type == AppMessageType.MatchBuyChips
                        && !TextUtils.isEmpty(currentAppMessage.key)
                        && currentAppMessage.key.equals(appMessage.key)
                        && currentAppMessage.type == appMessage.type
                        && (appMessage.checkinPlayerId.equals(currentAppMessage.checkinPlayerId))
                        ) {
                    AppMsgDBHelper.setSystemMessageStatus(getContext(), currentAppMessage.type, appMessage.checkinPlayerId, currentAppMessage.key, appMessage.status);
                    RecyclerView.ViewHolder viewHolder = mRecyclerView != null ? mRecyclerView.findViewHolderForAdapterPosition(i) : null;
                    if (viewHolder instanceof BuyChipsVHNew) {
                        if (((BuyChipsVHNew) viewHolder).mCountDownTimers != null) {
                            ((BuyChipsVHNew) viewHolder).mCountDownTimers.cancel();
                            ((BuyChipsVHNew) viewHolder).mCountDownTimers = null;
                        }
                    }
                    appMessageList.remove(currentAppMessage);
                    break;
                }
            }
            LogUtil.i(TAG, "onImcomingMessage :" + appMessage.status);
            if (appMessage.status == AppMessageStatus.init) {
                //未处理的消息 加入队列
                appMessageList.add(appMessage);
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            refreshRecyclerViewResult();
//            if (appMessage.status == AppMessageStatus.passed || appMessage.status == AppMessageStatus.declined) {
//                refreshList();//通过报名后重新请求刷新列表
//            }
        } else if (appMessage.type == AppMessageType.MatchBuyChipsResult) {//买入结果
            //同意或者拒绝
            MatchBuyChipsResultNotify matchBuyChipsNotify = (MatchBuyChipsResultNotify) appMessage.attachObject;
            int buyType = matchBuyChipsNotify.buyType;
            if (matchBuyChipsNotify.gameId.equals(gameInfo.gid)) {
                boolean openGame = true;
                int myCheckInStatus = 0;
                if (buyType == GameMatchBuyType.TYPE_BUY_CHECKIN) {//报名结果
                    myCheckInStatus = (appMessage.status == AppMessageStatus.passed ? CheckInStatus.CHECKIN_STATUES_ED : CheckInStatus.CHECKIN_STATUES_NOT);
                } else if (buyType == GameMatchBuyType.TYPE_BUY_REBUY_REVIVAL) {//复活重购结果
                    myCheckInStatus = CheckInStatus.CHECKIN_STATUES_ED;//复活重购  就是已经报名了
                    openGame = appMessage.status == AppMessageStatus.passed;//是否直接进入游戏，true进入false不进入
                    if (getActivity() instanceof MatchRoomActivity) {
                        ((MatchRoomActivity) getActivity()).buyTypeStatus = 0;
                    }
                }
                if (getActivity() instanceof MatchRoomActivity) {
                    ((MatchRoomActivity) getActivity()).updateMatchStatusBottomUI(myCheckInStatus, openGame);
                }
            }
        }
        AppMsgDBHelper.resetOneAppMessageUnreadCount(getContext(), appMessage);
        int unReadCount = AppMsgDBHelper.queryAllAppMessageUnreadCount(getContext());
        ReminderManager.getInstance().updateAppMsgUnreadNum(unReadCount);//重置为已读
        BuyChipsResultHelper.showBuyChipsResultDialog(getActivity(), appMessage);
        updateBuyChipsNotify();
    }

    public void updateGameInfo(GameEntity gameInfoP) {
        if (gameInfoP == null) {
            return;
        }
        this.gameInfo = gameInfoP;
        setMyChannel();
    }

    MatchPlayerAdapter.OnPlayerClick playerClick = new MatchPlayerAdapter.OnPlayerClick() {
        @Override
        public void onClick(MatchPlayerEntity playerEntity) {
            LogUtil.i(TAG, "playerEntity : " + playerEntity.uid);
            if (gameStatus == GameStatus.GAME_STATUS_START/* && playerEntity.tableNo != 0*/) {
                //比赛中，并且还未被淘汰
                if (playerEntity.uid.equals(DemoCache.getAccount())) {
                    ((MatchRoomActivity) getActivity()).openGame("", "", false);
                } else {
                    ((MatchRoomActivity) getActivity()).openGame(playerEntity.uid, "", true);
                }
            }
        }
    };

    private void initAdapter() {
        AppMessageListener appMessageListener = new AppMessageListener() {
            @Override
            public void onAgree(AppMessage message, int position) {
                doControlCheckIn(message, AppMessageStatus.passed, position);
            }
            @Override
            public void onReject(AppMessage message, int position) {
                doControlCheckIn(message, AppMessageStatus.declined, position);
            }
            @Override
            public void onClick(AppMessage message, int position) {
            }
            @Override
            public void onLongPressed(AppMessage message, int position) {

            }
        };
        adapter = new MatchPlayerAdapter(getContext(), appMessageListener, playerClick);
        adapter.canHandle = true;
        adapter.iMsgExpired = new IMsgExpired() {
            @Override
            public void onExpired(AppMessage appMessage) {
                if (appMessageList.contains(appMessage)) {
                    LogUtil.i("GameMatchAction", "过期的消息存在 ， 删除");
                    appMessageList.remove(appMessage);
                } else {
                    LogUtil.i("GameMatchAction", "过期的消息不存在");
                }
                adapter.notifyDataSetChanged();
                updateBuyChipsNotify();
            }
        };
        IClick.IOnlyClick removeClickListener = new IClick.IOnlyClick() {
            @Override
            public void onClick(int position) {
                if (isShowNormalAdapter) {
                    if (playerList != null && playerList.size() - 1 >= position) {
                        showDeleteDialog(playerList.get(position));
                    }
                } else {
                    if (searchList != null && searchList.size() - 1 >= position) {
                        showDeleteDialog(searchList.get(position));
                    }
                }
            }
        };
        adapter.clickListener = removeClickListener;
        adapter.gameInfo = gameInfo;
        ArrayList moreList = new ArrayList();moreList.add("more");
        adapter.addSection(MatchPlayerItemType.ChipBuy, new PlayerSection(MatchPlayerItemType.ChipBuy, appMessageList));
        adapter.addSection(MatchPlayerItemType.MttPlayer, new PlayerSection(MatchPlayerItemType.MttPlayer, playerList));
        adapter.gameStatus = gameInfo.status;
        //下面初始化searchadapter
        searchAdapter = new MatchPlayerAdapter(getContext(), appMessageListener, null);
        searchAdapter.addSection(MatchPlayerItemType.MttPlayer, new PlayerSection(MatchPlayerItemType.MttPlayer, searchList));
        searchAdapter.gameInfo = gameInfo;
        searchAdapter.canHandle = true;
        searchAdapter.clickListener = removeClickListener;
        searchAdapter.gameStatus = gameInfo.status;
    }

    public void doControlCheckIn(final AppMessage appMessage, final AppMessageStatus dealStatus, final int position) {
        if (getActivity() != null && getActivity() instanceof MatchRoomActivity) {
            ((MatchRoomActivity) getActivity()).doControlCheckIn(appMessage, dealStatus, new GameRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    RecyclerView.ViewHolder viewHolder = mRecyclerView != null ? mRecyclerView.findViewHolderForAdapterPosition(position) : null;
                    if (viewHolder instanceof BuyChipsVHNew) {
                        if (((BuyChipsVHNew) viewHolder).mCountDownTimers != null) {
                            ((BuyChipsVHNew) viewHolder).mCountDownTimers.cancel();
                            ((BuyChipsVHNew) viewHolder).mCountDownTimers = null;
                        }
                    }
                    AppMsgDBHelper.setSystemMessageStatus(getContext(), appMessage.type, appMessage.checkinPlayerId, appMessage.key, dealStatus);
//                    appMessage.setStatus(dealStatus);
                    if (appMessageList.contains(appMessage)) {
                        LogUtil.i("GameMatchAction", "消息存在 ， 删除");
                        appMessageList.remove(appMessage);
                    } else {
                        LogUtil.i("GameMatchAction", "消息不存在");
                    }
                    //
                    if (dealStatus == AppMessageStatus.passed) {
//                        refreshList();//通过报名后重新请求刷新列表
                    }
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    updateBuyChipsNotify();
                }

                @Override
                public void onFailed(int code, JSONObject response) {
                    LogUtil.i("GameMatchAction", code + "");
                    if (ApiResultHelper.isMatchBuychipsInvalid(code)) {
                        if (code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_SCORE_INSUFFICIENT && getActivity() instanceof MatchRoomActivity) {
                            ((MatchRoomActivity) getActivity()).showInsufficientDialog();
                        }
                        RecyclerView.ViewHolder viewHolder = mRecyclerView != null ? mRecyclerView.findViewHolderForAdapterPosition(position) : null;
                        if (viewHolder instanceof BuyChipsVHNew) {
                            if (((BuyChipsVHNew) viewHolder).mCountDownTimers != null) {
                                ((BuyChipsVHNew) viewHolder).mCountDownTimers.cancel();
                                ((BuyChipsVHNew) viewHolder).mCountDownTimers = null;
                            }
                        }
                        //消息已经失效
                        AppMessageStatus status = AppMessageStatus.expired;
                        AppMsgDBHelper.setSystemMessageStatus(getContext(), appMessage.type, appMessage.checkinPlayerId, appMessage.key, status);
                        appMessage.status = (status);
                        if (appMessageList.contains(appMessage)) {
                            appMessageList.remove(appMessage);
                        }
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                        updateBuyChipsNotify();
                    }
                    if (code == ApiCode.CODE_MATCH_CHECKIN_START_MOMENT) {//新需求，点击开始前30秒不能报名、移除、同意、拒绝 20170808
                        if (getActivity() instanceof MatchRoomActivity) {
                            ((MatchRoomActivity) getActivity()).showThirtyLimitDialog("比赛即将开始\n此段时间内暂停报名");
                        }
                    }
                }
            });
        }
    }

    private void showDeleteDialog(final MatchPlayerEntity bean) {
        EasyAlertDialog backDialog = EasyAlertDialogHelper.createOkCancelDiolag(getActivity(), "", "确定移除“" + bean.nickname + "”？", true,
                new EasyAlertDialogHelper.OnDialogActionListener() {
                    @Override
                    public void doCancelAction() {
                    }
                    @Override
                    public void doOkAction() {
                        deleteCheckinPlayer(bean);
                    }
                });
        if (getActivity() instanceof BaseActivity && !getActivity().isFinishing() && !((BaseActivity) getActivity()).isDestroyedCompatible()) {
            backDialog.show();
        }
    }

    private void deleteCheckinPlayer(final MatchPlayerEntity bean) {
        mGameAction.delCheckinPlayer(gameInfo.channel, bean.uid, DemoCache.getAccount(), new VolleyCallback() {
            @Override
            public void onResponse(String response) {
                //有接口mCheckinPlayerChange动态更新数据，不需要再次更新数据了-------------------{"code":0,"message":"ok","data":""}
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        Toast.makeText(getActivity(), "移除成功", Toast.LENGTH_SHORT).show();
                        removeListData(bean);//手动移除报名玩家
                        refreshRecyclerViewResult();
//                        refreshList();//暂时从头开始请求，体验不是很好
                    } else if (code == 3015) {
                        Toast.makeText(getActivity(), "您已经不是管理员", Toast.LENGTH_SHORT).show();//现在需求是只要管理员成功审批过一个玩家的报名请求，那么这个管理员就不能被移除
                    } else if (code == ApiCode.CODE_MATCH_CHECKIN_START_MOMENT) {//新需求，点击开始前30秒不能报名、移除、同意、拒绝 20170808
                        if (getActivity() instanceof MatchRoomActivity) {
                            ((MatchRoomActivity) getActivity()).showThirtyLimitDialog("比赛开始前30秒无法移除玩家");
                        }
                    } else {// TODO: 17/2/21 code是3031应该是表示该玩家已经自己取消报名了，等待黄老师确认
                        Toast.makeText(getActivity(), "比赛开始后无法移除报名玩家", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "移除失败", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "移除失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeListData(final MatchPlayerEntity bean) {
        Iterator<MatchPlayerEntity> iterator = playerList.iterator();
        int pos = 0;
        while (iterator.hasNext()) {
            if (iterator.next().uid.equals(bean.uid)) {
                iterator.remove();
                m_checkin_cnt--;
                adapter.notifyItemRemoved(appMessageList.size() + pos);
                break;
            }
            pos++;
        }
        Iterator<MatchPlayerEntity> searchIterator = searchList.iterator();
        while (searchIterator.hasNext()) {
            if (searchIterator.next().uid.equals(bean.uid)) {
                searchIterator.remove();
                searchAdapter.notifyItemRemoved(pos);
                break;
            }
            pos++;
        }
    }

    public void updateMgrPlayer(MatchPlayerEntity matchPlayerEntity, int type, String channel) {//增量更新玩家列表   type 1加  2减    只在比赛前增量改变
        if (matchPlayerEntity == null || gameInfo == null || StringUtil.isSpace(gameInfo.channel) || !gameInfo.channel.equals(channel)) {
            return;
        }
        if (type == 1) {
            for (int i = 0; i < playerList.size(); i++) {
                MatchPlayerEntity oldItem = playerList.get(i);
                if (matchPlayerEntity.uid.equals(oldItem.uid)) {
                    return;//有这个玩家不增量增加，直接return
                }
            }
            if (gameStatus == GameStatus.GAME_STATUS_START) {
                playerList.add(matchPlayerEntity);
                adapter.notifyItemInserted(appMessageList.size() + playerList.size());
            } else if (gameStatus == GameStatus.GAME_STATUS_WAIT) {
                playerList.add(0, matchPlayerEntity);
                adapter.notifyItemInserted(appMessageList.size() + 0);
            }
            m_checkin_cnt++;
        } else if (type == 2) {
            removeListData(matchPlayerEntity);//长连接移除报名玩家
        }
        refreshRecyclerViewResult();//动态更新玩家(加或者减)
        showTotalPlayAndKillHeadNum();//动态更新玩家(加或者减)
    }

    public void updateBuyChipsNotify() {
        if (getActivity() instanceof MatchRoomActivity) {
            ((MatchRoomActivity) getActivity()).updateBuyChipsNotify(appMessageList == null ? 0 : appMessageList.size());
        }
    }

    private void setMyChannel() {
        if (gameInfo == null || view == null) {
            return;
        }
        if (this.gameInfo.gameConfig instanceof PineappleConfigMtt) {
            changePineappleBg();
        }
        if (gameInfo.channel == null) {
            gameInfo.channel = "";
        }
        if (/*gameInfo.type == GameConstants.GAME_TYPE_GAME || */gameInfo.channel.length() == 6) {//如果是俱乐部比赛但是code为6位那么还是显示code而不是显示俱乐部信息
            mgr_gamecode.setText("我的邀请码：" + gameInfo.channel);
        } else if (gameInfo.channel.length() > 6) {
            String teamId = gameInfo.channel.substring(6, gameInfo.channel.length());//gameInfo.getTid();
            if (StringUtil.isSpace(teamId)) {
                return;
            }
            final Team team = TeamDataCache.getInstance().getTeamById(teamId);
            if (team != null) {
                String clubName = team.getName();
                mgr_gamecode.setText("来自俱乐部“" + clubName + "”");
            } else {
                TeamDataCache.getInstance().fetchTeamById(teamId, new SimpleCallback<Team>() {
                    @Override
                    public void onResult(boolean success, Team result) {
                        if (success && result != null) {
                            mgr_gamecode.setText("来自俱乐部“" + result.getName() + "”");
                        } else {
                        }
                    }
                });
            }
        }
    }
    private void initView() {
        int ko_mode = gameInfo != null && gameInfo.gameConfig instanceof GameMttConfig ? ((GameMttConfig) gameInfo.gameConfig).ko_mode : 0;
        mgr_gamecode = (TextView) view.findViewById(R.id.mgr_gamecode);
        mgr_info_container = view.findViewById(R.id.mgr_info_container);
        mgr_sort_container = view.findViewById(R.id.mgr_sort_container);
        setMyChannel();
        mgr_action_checkin_cnt = (TextView) view.findViewById(R.id.mgr_action_checkin_cnt);
        mgr_action_buyin_cnt = (TextView) view.findViewById(R.id.mgr_action_buyin_cnt);
        tv_match_sort_type = (TextView) view.findViewById(R.id.tv_match_sort_type);
        tv_match_sort_type.setText((gameInfo != null && gameInfo.status == GameStatus.GAME_STATUS_START || gameStatus == GameStatus.GAME_STATUS_START) ? "玩家排名" : "玩家");
        iv_match_sort_type = (ImageView) view.findViewById(R.id.iv_match_sort_type);
        iv_match_sort_type.setVisibility(View.GONE);//iv_match_sort_type.setVisibility((gameInfo != null && gameInfo.status == GameStatus.GAME_STATUS_START || gameStatus == GameStatus.GAME_STATUS_START) ? View.VISIBLE : View.GONE);
        match_sort_hunter_mark = (TextView) view.findViewById(R.id.match_sort_hunter_mark);
        if (gameInfo.match_type == GameConstants.MATCH_TYPE_NORMAL) {
            match_sort_hunter_mark.setText(gameStatus == GameStatus.GAME_STATUS_START ? (ko_mode == 1 ? "猎头/赏金" : (ko_mode == 2 ? "身价/赏金" : "")) : "");
        } else {
            match_sort_hunter_mark.setText(gameStatus == GameStatus.GAME_STATUS_START ? (ko_mode == 1 ? "猎头" : (ko_mode == 2 ? "赏金" : "")) : "");
        }
        match_sort_type_container = view.findViewById(R.id.match_sort_type_container);
        match_sort_type_container.setOnClickListener(this);
        refresh_layout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refresh_layout.setColorSchemeResources(R.color.login_solid_color, R.color.login_solid_color, R.color.login_solid_color, R.color.login_solid_color);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getActivity() instanceof MatchRoomActivity && gameStatus == GameStatus.GAME_STATUS_START) {
                    ((MatchRoomActivity) getActivity()).lastGetMgrPlayerTime = 0;
                    ((MatchRoomActivity) getActivity()).getMgrPlayerList();//开赛前只请求一次，开赛后10秒一次请求  onRefresh
                } else {
                    refresh_layout.setRefreshing(false);//开赛前不刷新，开赛前只请求一次
                }
            }
        });
        mResultDataView = (ResultDataView) view.findViewById(R.id.mResultDataView);
        mRecyclerView = (MeRecyclerView) view.findViewById(R.id.search_mtt_mgr_recycler);
        mRecyclerView.setAdapter(adapter);
        tv_search_cancel = view.findViewById(R.id.tv_search_cancel);
        tv_search_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchView(false);
                mRecyclerView.setAdapter(adapter);
                searchList.clear();
                if (searchAdapter != null) {
                    searchAdapter.notifyDataSetChanged();//searchAdapter.setData(searchList);
                }
                isShowNormalAdapter = true;
                refreshRecyclerViewResult();
            }
        });
        rl_player_search = view.findViewById(R.id.rl_player_search);
        rl_player_search_btn = view.findViewById(R.id.rl_player_search_btn);
        tv_player_search_hint = (TextView) view.findViewById(R.id.tv_player_search_hint);
        tv_player_search_hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchView(true);
                mRecyclerView.setAdapter(searchAdapter);
                mResultDataView.successShow();//隐藏
                refresh_layout.setEnabled(false);//禁止刷新
            }
        });
        edt_player_search = (ClearableEditTextWithIcon) view.findViewById(R.id.edt_player_search);
        edt_player_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String key = edt_player_search.getText().toString();
                    key = key.trim();
                    if (TextUtils.isEmpty(key)) {
                        Toast.makeText(getActivity(), R.string.not_allow_empty, Toast.LENGTH_SHORT).show();
                    } else {
                        searchKey(key);
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    return true;
                }
                return false;
            }
        });
        refresh_layout.setBackgroundColor(getActivity().getResources().getColor(R.color.transparent));
        mResultDataView.nullDataShow(R.string.data_null);
    }

    public void showSearchView(boolean show) {
        isShowNormalAdapter = !show;
        if (show) {
            rl_player_search.setVisibility(View.VISIBLE);
            rl_player_search_btn.setVisibility(View.GONE);
            edt_player_search.setText("");
            edt_player_search.setFocusable(true);
            edt_player_search.requestFocus();
            searchList.clear();
            mRecyclerView.setAdapter(searchAdapter);
            showKeyboard(true);
        } else {
            rl_player_search.setVisibility(View.GONE);
            rl_player_search_btn.setVisibility(View.VISIBLE);
            mRecyclerView.setAdapter(adapter);
        }
    }

    public int m_checkin_cnt;
    public int m_rebuy_cnt;
    public String m_ko_head_total;
    public void updateList(ArrayList<MatchPlayerEntity> list, boolean isClear , int isEnd, int rebuy_cnt, int checkin_cnt, String ko_head_total) {
        m_checkin_cnt = checkin_cnt;
        m_rebuy_cnt = rebuy_cnt;
        m_ko_head_total = ko_head_total;
        showTotalPlayAndKillHeadNum();//全刷玩家列表
        if (isClear) {
            playerList.clear();
        }
        playerList.addAll(list);
        if (isEnd == 1) {
            //到达结尾，刷新
            LogUtil.i(TAG, "到达结尾，刷新");
            normalDatasetChanged();//全刷新
        }
    }

    public void gameStatusChanged(MatchStatusEntity matchStatusEntityPa) {
        int ko_mode = gameInfo != null && gameInfo.gameConfig instanceof GameMttConfig ? ((GameMttConfig) gameInfo.gameConfig).ko_mode : 0;
        gameStatus = matchStatusEntityPa == null ? GameStatus.GAME_STATUS_WAIT : matchStatusEntityPa.gameStatus;
        if (gameInfo != null) {
            gameInfo.status = gameStatus;
        }
        int myCheckInStatus = matchStatusEntityPa == null ? CheckInStatus.CHECKIN_STATUES_ED : matchStatusEntityPa.myCheckInStatus;
        showTotalPlayAndKillHeadNum();//游戏状态status更改
        if (!isAdded()) {
            return;
        }
        if (adapter != null) {
            adapter.updateStatus(gameStatus, myCheckInStatus);
        }
        if (searchAdapter != null) {
            searchAdapter.updateStatus(gameStatus, myCheckInStatus);
        }
        if (this.gameStatus == GameStatus.GAME_STATUS_START && tv_match_sort_type != null) {
            if (tv_match_sort_type != null && match_sort_hunter_mark != null) {
                tv_match_sort_type.setText("玩家排名");
                iv_match_sort_type.setVisibility(View.GONE);//iv_match_sort_type.setVisibility(View.VISIBLE);// TODO: 17/4/19
                if (gameInfo.match_type == GameConstants.MATCH_TYPE_NORMAL) {
                    match_sort_hunter_mark.setText(gameStatus == GameStatus.GAME_STATUS_START ? (ko_mode == 1 ? "猎头/赏金" : (ko_mode == 2 ? "身价/赏金" : "")) : "");
                } else {
                    match_sort_hunter_mark.setText(gameStatus == GameStatus.GAME_STATUS_START ? (ko_mode == 1 ? "猎头" : (ko_mode == 2 ? "赏金" : "")) : "");
                }
            }
        }
    }

    private void showTotalPlayAndKillHeadNum() {
        if (view != null) {
            if (gameStatus == GameStatus.GAME_STATUS_WAIT) {
                mgr_action_checkin_cnt.setText("报名人数：" + m_checkin_cnt);
                mgr_action_buyin_cnt.setVisibility(View.GONE);
            } else {
                mgr_action_buyin_cnt.setVisibility(View.VISIBLE);
                int ko_mode = gameInfo != null && gameInfo.gameConfig instanceof GameMttConfig ? ((GameMttConfig) gameInfo.gameConfig).ko_mode : 0;
                if (ko_mode == 1) {//普通猎人赛显示总猎头数，其余情况不显示总猎头数
                    mgr_action_checkin_cnt.setText("报名/买入：" + m_checkin_cnt + "/" + (m_checkin_cnt + m_rebuy_cnt));
                    mgr_action_buyin_cnt.setText("猎头总数：" + (m_ko_head_total));
                } else {
                    mgr_action_checkin_cnt.setText("报名人数：" + m_checkin_cnt);
                    mgr_action_buyin_cnt.setText("买入次数：" + (m_checkin_cnt + m_rebuy_cnt));
                }
            }
        }
    }

    private void normalDatasetChanged() {
        if (adapter != null) {
//            adapter.setData(playerList);
            adapter.notifyDataSetChanged();
        }
        refreshRecyclerViewResult();
    }

    private void refreshRecyclerViewResult() {
        if (getActivity() == null || view == null) {
            return;
        }
        if (isShowNormalAdapter) {
            refresh_layout.setEnabled(true);
            if (playerList.size() > 0 || appMessageList.size() > 0) {
                mResultDataView.successShow();
                mRecyclerView.setVisibility(View.VISIBLE);
            } else {
                mResultDataView.nullDataShow(R.string.data_null);
            }
        } else {
            refresh_layout.setEnabled(false);
            if (searchList == null || searchList.size() <= 0) {
                mResultDataView.nullDataShow(R.string.search_null);
            } else {
                mResultDataView.successShow();
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void searchKey(String word) {
        isShowNormalAdapter = false;
        //调用搜索接口
        mGameAction.searchChannelPlayer(gameInfo.channel, gameInfo.gid, word, new VolleyCallback() {
            @Override
            public void onResponse(String response) {
                //{"code":0,"message":"ok","data":[{"id":"10041","nickname":"sugar","gid":"1946","ranking":0,"uid":"10041","reward":"200","rebuy_cnt":0,"index":false}]}
                try {
                    searchList.clear();
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        JSONArray data = json.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject item = data.getJSONObject(i);
                            String uid = item.optString("uid");
                            String nickname = item.optString("nickname");
                            int rebuy_cnt = item.optInt("rebuy_cnt");
                            MatchPlayerEntity bean = new MatchPlayerEntity();
                            bean.uuid = item.optString("uuid");
                            if ("0".equals(bean.uuid)) {
                                bean.uuid = "";
                            }
                            bean.uid = uid;
                            bean.nickname = nickname;
                            bean.rebuyCnt = rebuy_cnt;
                            bean.rank = item.optInt("ranking");
                            bean.chips = item.optInt("reward");
                            DecimalFormat df = new DecimalFormat("#.##");
                            bean.ko_head_cnt = df.format(item.optInt("ko_head_cnt") / 100f);
                            bean.ko_head_reward = item.optInt("ko_head_reward");
                            searchList.add(bean);
                        }
                    }
                    afterSearch();
                } catch (JSONException e) {
                    e.printStackTrace();
                    afterSearch();
                }
            }
            @Override
            public void onErrorResponse(VolleyError error) {
                afterSearch();
            }
        });
    }

    //查询结束后的操作
    private void afterSearch() {
        if (searchAdapter != null) {
            searchAdapter.notifyDataSetChanged();//searchAdapter.setData(searchList);
        }
        refreshRecyclerViewResult();
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.i(TAG + " onStop");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.i(TAG + " onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(TAG + " onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.match_sort_type_container://弹出排序的dialog对话框
                if (/*gameStatus == GameStatus.GAME_STATUS_WAIT || view == null*/true) {//这个排序的需求不做
                    return;
                }
                if (sortPlayerPopupWindow != null && sortPlayerPopupWindow.isShowing()) {
                    dismissSortPlayerPopupWindow(match_sort_type_container);
                } else {
                    showSortPlayerPopupWindow();
                }
                break;
            case R.id.match_sort_one_container://按名次排序
                dismissSortPlayerPopupWindow(match_sort_one_container);
                if (previousSortView == match_sort_one_container) {
                    return;
                }
                previousSortView = match_sort_one_container;
                rank_type = 1;
                tv_match_sort_type.setText("按名次排序");
//                tryGetPlayListByRank(true);
                break;
            case R.id.match_sort_two_container://按猎头数排序    普通猎人赛  ko_mode = 0
                dismissSortPlayerPopupWindow(match_sort_two_container);
                if (previousSortView == match_sort_two_container) {
                    return;
                }
                previousSortView = match_sort_two_container;
                rank_type = 2;
                tv_match_sort_type.setText("按猎头数排序");
//                tryGetPlayListByRank(true);
                break;
            case R.id.match_sort_three_container://按身价排序    超级猎人赛  ko_mode = 1
                dismissSortPlayerPopupWindow(match_sort_three_container);
                if (previousSortView == match_sort_three_container) {
                    return;
                }
                previousSortView = match_sort_three_container;
                rank_type = 4;
                tv_match_sort_type.setText("按身价排序");
//                tryGetPlayListByRank(true);
                break;
            case R.id.match_sort_four_container://按赏金排序     超级猎人赛  ko_mode = 1
                dismissSortPlayerPopupWindow(match_sort_four_container);
                if (previousSortView == match_sort_four_container) {
                    return;
                }
                previousSortView = match_sort_four_container;
                rank_type = 3;
                tv_match_sort_type.setText("按赏金排序");
//                tryGetPlayListByRank(true);
                break;
        }
    }

    View match_sort_one_container;//按名次排序
    View match_sort_two_container;//按猎头数排序    普通猎人赛  ko_mode = 1
    View match_sort_three_container;//按身价排序    超级猎人赛  ko_mode = 2
    View match_sort_four_container;//按赏金排序     超级猎人赛  ko_mode = 2
    View iv_sort_one_selected;
    View iv_sort_two_selected;
    View iv_sort_three_selected;
    View iv_sort_four_selected;
    View previousSortView = match_sort_one_container;
    PopupWindow sortPlayerPopupWindow;
    private Animation mRotateAnimation, mResetRotateAnimation;
    private void dismissSortPlayerPopupWindow(View srcView) {//参数的意思是点击哪个view来关闭popupwindow的，
        if (sortPlayerPopupWindow != null && sortPlayerPopupWindow.isShowing()) {
            iv_sort_one_selected.setVisibility(srcView == match_sort_one_container ? View.VISIBLE : View.GONE);
            iv_sort_two_selected.setVisibility(srcView == match_sort_two_container ? View.VISIBLE : View.GONE);
            iv_sort_three_selected.setVisibility(srcView == match_sort_three_container ? View.VISIBLE : View.GONE);
            iv_sort_four_selected.setVisibility(srcView == match_sort_four_container ? View.VISIBLE : View.GONE);
            sortPlayerPopupWindow.dismiss();
        }
    }
    private void showSortPlayerPopupWindow() {
        if (sortPlayerPopupWindow == null) {
            final int rotateAngle = 180;
            mRotateAnimation = new RotateAnimation(0, rotateAngle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            mRotateAnimation.setInterpolator(new LinearInterpolator());
            mRotateAnimation.setDuration(300);
            mRotateAnimation.setFillAfter(true);

            mResetRotateAnimation = new RotateAnimation(rotateAngle, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            mResetRotateAnimation.setInterpolator(new LinearInterpolator());
            mResetRotateAnimation.setDuration(300);
            mResetRotateAnimation.setFillAfter(true);

            View popView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_sort_match_player, null);
            sortPlayerPopupWindow = new PopupWindow(popView);
            //获取popwindow焦点
            sortPlayerPopupWindow.setFocusable(true);
            //设置popwindow如果点击外面区域，便关闭。
            sortPlayerPopupWindow.setOutsideTouchable(false);
            sortPlayerPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
            sortPlayerPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            sortPlayerPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            //设置popwindow出现和消失动画
//        clubPopupWindow.setAnimationStyle(R.style.PopMenuAnimation);
            match_sort_one_container = popView.findViewById(R.id.match_sort_one_container);
            match_sort_one_container.setOnClickListener(this);
            match_sort_two_container = popView.findViewById(R.id.match_sort_two_container);
            match_sort_two_container.setVisibility(gameInfo != null && gameInfo.gameConfig instanceof BaseMttConfig && ((BaseMttConfig) gameInfo.gameConfig).ko_mode == 1 ? View.VISIBLE : View.GONE);
            match_sort_two_container.setOnClickListener(this);
            match_sort_three_container = popView.findViewById(R.id.match_sort_three_container);
            match_sort_three_container.setVisibility(gameInfo != null && gameInfo.gameConfig instanceof BaseMttConfig && ((BaseMttConfig) gameInfo.gameConfig).ko_mode == 2 ? View.VISIBLE : View.GONE);
            match_sort_three_container.setOnClickListener(this);
            match_sort_four_container = popView.findViewById(R.id.match_sort_four_container);
            match_sort_four_container.setVisibility(gameInfo != null && gameInfo.gameConfig instanceof BaseMttConfig && ((BaseMttConfig) gameInfo.gameConfig).ko_mode == 2 ? View.VISIBLE : View.GONE);
            match_sort_four_container.setOnClickListener(this);
            iv_sort_one_selected = popView.findViewById(R.id.iv_sort_one_selected);
            iv_sort_two_selected = popView.findViewById(R.id.iv_sort_two_selected);
            iv_sort_three_selected = popView.findViewById(R.id.iv_sort_three_selected);
            iv_sort_four_selected = popView.findViewById(R.id.iv_sort_four_selected);
            previousSortView = match_sort_one_container;
            sortPlayerPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    AnimUtil.startRotate(iv_match_sort_type, 180, 0, 300, Animation.RESTART);
                }
            });
            changePineappleBg();
        }
        AnimUtil.startRotate(iv_match_sort_type, 0, 180, 300, Animation.RESTART);
        sortPlayerPopupWindow.showAsDropDown(rl_player_search_btn);
    }

    public void changePineappleBg() {
        if (!(gameInfo.gameConfig instanceof PineappleConfigMtt)) {
            return;
        }
        if (mgr_info_container != null && mgr_sort_container != null) {
            mgr_info_container.setBackgroundColor(ChessApp.sAppContext.getResources().getColor(R.color.bg_pineapple_mtt));
            mgr_sort_container.setBackgroundColor(ChessApp.sAppContext.getResources().getColor(R.color.bg_pineapple_mtt_deep));
        }
        if (match_sort_one_container != null) {
            match_sort_one_container.setBackgroundColor(ChessApp.sAppContext.getResources().getColor(R.color.bg_pineapple_mtt_deep));
        }
        if (match_sort_two_container != null) {
            match_sort_two_container.setBackgroundColor(ChessApp.sAppContext.getResources().getColor(R.color.bg_pineapple_mtt_deep));
        }
        if (match_sort_three_container != null) {
            match_sort_three_container.setBackgroundColor(ChessApp.sAppContext.getResources().getColor(R.color.bg_pineapple_mtt_deep));
        }
        if (match_sort_four_container != null) {
            match_sort_four_container.setBackgroundColor(ChessApp.sAppContext.getResources().getColor(R.color.bg_pineapple_mtt_deep));
        }
    }
}
