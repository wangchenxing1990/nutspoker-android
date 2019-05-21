package com.htgames.nutspoker.ui.activity.Record;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.contact.activity.UserProfileActivity;
import com.htgames.nutspoker.db.GameRecordDBHelper;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.interfaces.VolleyCallback;
import com.htgames.nutspoker.tool.json.RecordJsonTools;
import com.htgames.nutspoker.ui.action.ClubAction;
import com.htgames.nutspoker.ui.action.GameAction;
import com.htgames.nutspoker.ui.action.RecordAction;
import com.htgames.nutspoker.ui.adapter.RecordDetailsPagerAdapter;
import com.htgames.nutspoker.ui.adapter.channel.ChannelType;
import com.htgames.nutspoker.ui.adapter.recordmember.RecordMemberAdap;
import com.htgames.nutspoker.ui.adapter.recordmember.RecordMemberItem;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.ui.items.HeaderItem;
import com.htgames.nutspoker.view.PaipuMoreView;
import com.htgames.nutspoker.view.RecordGameBg;
import com.netease.nim.uikit.bean.BaseMttConfig;
import com.netease.nim.uikit.bean.GameBillEntity;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMemberEntity;
import com.netease.nim.uikit.bean.GameMgrBean;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.bean.UserEntity;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.interfaces.IClick;
import com.netease.nim.uikit.session.constant.Extras;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import eu.davidea.fastscroller.FastScroller;

/**
 * Created by 20150726 on 2015/11/16.
 */
public class RecordDetailsActivity extends BaseActivity implements View.OnClickListener, IClick {
    private final static String TAG = "RecordDetailsActivity";
    public static int FROM_NORMAL = 0;
    public static int FROM_CHAT = 1;
    public static int FROM_CIRCLE = 2;
    TextView tv_game_memeber;
    public GameBillEntity bill;
    GameEntity gameInfo;
    private ArrayList mDatas = new ArrayList();
    private RecordMemberAdap mAdapter;
    FastScroller fastScroller;
    PaipuMoreView mPaipuMoreView;
    int from = FROM_NORMAL;
    RelativeLayout rl_record_content;
    ViewPager mViewPager;
    RecordDetailsPagerAdapter mRecordDetailsPagerAdapter;
    View view_indicator_1;
    View view_indicator_2;
    int checkPosition = 0;
    TextView tv_record_details_info;
    GameAction mGameAction;
    ClubAction mClubAction;
    int insurancePond = 0;
    public int club_channel = 1;//0俱乐部普通成员1俱乐部管理员包括会长   默认是管理员
//    TextView tv_details_all_buy_title;
//    TextView tv_details_all_gain_title;
    RecordAction mRecordAction;
    //
    boolean isLoadMore = false;
    ArrayList<GameMemberEntity> gameMemberList = new ArrayList<>();
    ArrayList<GameMemberEntity> insuranceMemberList = new ArrayList<>();
    RecyclerView mRecyclerView;
    LinearLayoutManager layoutManager;
    int gameMode = GameConstants.GAME_MODE_NORMAL;

    public static void start(Activity activity, String gid) {//额   有时候GameBillEntity过大，gamemembers数组多达1500个，导致这个页面起不来  Activity之间通过intent传递大量数据，导致新Activity无法启动。
        //通过传gid再查数据库得到GameBillEntity
        Intent intent = new Intent(activity, RecordDetailsActivity.class);
        intent.putExtra(Extras.EXTRA_GAME_ID, gid);
        activity.startActivity(intent);
    }

    public static void start(Activity activity, GameBillEntity bill, int from) {
        Intent intent = new Intent(activity, RecordDetailsActivity.class);
        intent.putExtra(Extras.EXTRA_BILL, bill);
        intent.putExtra(Extras.EXTRA_FROM, from);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_details);
        String gid = (String) getIntent().getSerializableExtra(Extras.EXTRA_GAME_ID);
        if (!StringUtil.isSpace(gid)) {
            bill = GameRecordDBHelper.getGameRecordByGid(this, gid);
        }
        if (bill == null) {
            bill = (GameBillEntity) getIntent().getSerializableExtra(Extras.EXTRA_BILL);
        }
        from = getIntent().getIntExtra(Extras.EXTRA_FROM, FROM_NORMAL);
//        sessionMember = new LinkedHashMap<>();
//        sessionMember.put(RecordMemberItemType.NORMAL, gameMemberList);
//        sessionMember.put(RecordMemberItemType.INSURANCE, insuranceMemberList);
        //
        initView();
        mGameAction = new GameAction(this, null);
        mClubAction = new ClubAction(this, null);
        if (bill != null && bill.gameInfo != null) {
            gameInfo = bill.gameInfo;
            gameInfo.checkinPlayerCount = bill.totalPlayer;//
//            //下面几行代码处理bug: 位森websocket长连返回的玩家列表的昵称可能是老的昵称，需要请求云信刷新用户，得到用户信息显示正确的昵称
            List<String> nimRefreshList = new ArrayList<>();
            if (bill.gameMemberList != null) {
                for (int i = 0; i < bill.gameMemberList.size(); i++) {
                    nimRefreshList.add(bill.gameMemberList.get(i).userInfo.account);
                }
            }
            NimUserInfoCache.getUserListByNeteaseEx(nimRefreshList, 0, null);
            if (bill.gameMemberList != null && (gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL || gameInfo.gameMode == GameConstants.GAME_MODE_SNG)) {
                gameMemberList.addAll(bill.gameMemberList);
                //如果是普通模式或者SNG模式，过滤掉排名是0并且买入是0的用户
                int memberSize = (gameMemberList == null ? 0 : gameMemberList.size());
                for (int i = memberSize - 1; i >= 0; i--) {
                    GameMemberEntity gameMemberEntity = gameMemberList.get(i);
                    insurancePond = insurancePond + gameMemberEntity.insurance;
                    if (gameMemberEntity.ranking == 0 && gameMemberEntity.totalBuy == 0) {
                        gameMemberList.remove(i);
                    }
                }
                Collections.sort(gameMemberList, sngRankComp);
            }
            club_channel = gameInfo.club_channel;
            initViewPager();
            setData(gameInfo);
            getMgrListOff();//如果是mtt比赛，那么请求赛事管理员
            initAdapterNew();
        }
        if (gameInfo != null && (gameInfo.gameMode == GameConstants.GAME_MODE_MTT || gameInfo.gameMode == GameConstants.GAME_MODE_MT_SNG)) {
            getRecordMemberList("");
        }
    }

    private void initAdapterNew() {
        boolean isNormal = gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL;
        String headLeft = "玩家：" + gameMemberList.size(), headMiddle = isNormal ? "买入" : "", headRight = isNormal ? "盈利" : "奖励";
        Drawable leftDrawable = getResources().getDrawable(R.mipmap.icon_club_chat_member);
        leftDrawable.setBounds(0, 0, leftDrawable.getIntrinsicWidth(), leftDrawable.getIntrinsicHeight());
        for (int i = 0; i< gameMemberList.size(); i++) {
            GameMemberEntity memberEntity = gameMemberList.get(i);
            RecordMemberItem item = new RecordMemberItem(memberEntity, memberEntity.userInfo != null ? memberEntity.userInfo.account : (i + ""));
            item.setBill(bill);
            item.setMType(item.Companion.getTYPE_NORMAL());
            if (i == 0) {
                HeaderItem headerItem = new HeaderItem(i + "", headLeft, headMiddle, headRight, leftDrawable, HeaderItem.Companion.getTYPE_NORMAL());
                item.setHeader(headerItem);
            }
            mDatas.add(item);
        }
        for (int i = 0; i < insuranceMemberList.size(); i++) {
            GameMemberEntity memberEntity = insuranceMemberList.get(i);
            RecordMemberItem item = new RecordMemberItem(memberEntity, memberEntity.userInfo != null ? (memberEntity.userInfo.account + " insurance") : (i + " insurance"));
            item.setBill(bill);
            item.setMType(item.Companion.getTYPE_INSURANCE());
            if (i == 0) {
                HeaderItem headerItem = new HeaderItem((i + 10) + "", headLeft, headMiddle, headRight, leftDrawable, HeaderItem.Companion.getTYPE_INSURANCE());
                headerItem.setInsurancePond(-insurancePond);
                item.setHeader(headerItem);
            }
            mDatas.add(item);
        }
        fastScroller = (FastScroller) findViewById(R.id.fast_scroller);
        fastScroller.setBubbleAndHandleColor(Color.RED);
        mAdapter = new RecordMemberAdap(mDatas, this, true);
        mAdapter.setMMaxNum(gameMemberList.size() + insuranceMemberList.size());
        mAdapter.setMGameInfo(gameInfo);
        mAdapter.setAnimateChangesWithDiffUtil(true)
                .setAnimateToLimit(10000)
                .setNotifyMoveOfFilteredItems(false)
                .setNotifyChangeOfUnfilteredItems(true)
                .setAnimationDelay(40)
                .setAnimationOnScrolling(true)
                .setAnimationOnReverseScrolling(true)
                .setOnlyEntryAnimation(true);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemViewCacheSize(0);
        mAdapter.setFastScroller(fastScroller);
        mAdapter.setLongPressDragEnabled(false)
                .setHandleDragEnabled(true)
                .setSwipeEnabled(false)
                .setUnlinkAllItemsOnRemoveHeaders(true)
                .setDisplayHeadersAtStartUp(true)
                .setStickyHeaders(true);
        if (mAdapter.mAnimatorNotifierObserver != null) {
            mAdapter.mAnimatorNotifierObserver.notified = false;
        }
    }

    private void getMgrListOff() {
        if (gameInfo == null) {
            return;
        }
        mGameAction.getMgrListOff(gameInfo.gid, new VolleyCallback() {
            @Override
            public void onResponse(String result) {
                boolean isMeGameMgr = false;
                String thisChannel = "";
                ArrayList<UserEntity> allManagers = new ArrayList<UserEntity>();
                try {
                    JSONObject response = new JSONObject(result);
                    if(!response.isNull("data")) {
                        JSONArray array = response.getJSONArray("data");
                        int size = array.length();
                        for (int i = 0; i < size; i++) {
                            JSONObject channel = (JSONObject) array.get(i);
                            GameMgrBean bean = new GameMgrBean();
                            bean.mgrList = new ArrayList();
                            bean.channel = channel.optString("channel");
                            bean.channelType = bean.channel.length() == 6 ? ChannelType.personal : (club_channel != 1 ? ChannelType.creator : ChannelType.club);
                            JSONArray usersArray = channel.getJSONArray("users");
                            for (int j = 0; j < usersArray.length(); j++) {
                                UserEntity userEntity = new UserEntity();
                                JSONObject inner = usersArray.getJSONObject(j);
                                userEntity.account = inner.optString("uid");
                                userEntity.uuid = inner.optString("uuid");
                                userEntity.nickname = inner.optString("nickname");
                                userEntity.channel = bean.channel;
                                userEntity.channelType = bean.channelType;
                                if (userEntity.account.equals(DemoCache.getAccount())) {
                                    isMeGameMgr = true;
                                    thisChannel = userEntity.channel;
                                }
                                bean.mgrList.add(userEntity);
                            }
                            allManagers.addAll(bean.mgrList);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //下面几行代码处理bug: 位森websocket长连返回的玩家列表的昵称可能是老的昵称，需要请求云信刷新用户，得到用户信息显示正确的昵称
                List<String> nimRefreshList = new ArrayList<>();
                for (int i = 0; i < allManagers.size(); i++) {
                    nimRefreshList.add(allManagers.get(i).account);
                }
                NimUserInfoCache.getUserListByNeteaseEx(nimRefreshList, 0, null);
                initHead(isMeGameMgr, thisChannel);
                if (mAdapter != null && insuranceMemberList.size() > 0) {
                    mAdapter.setMeGameMgr(isMeGameMgr);
                    mAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });//获取赛事管理员列表
    }

    private void updateMttDataset() {
        mDatas.clear();
        String headLeft = "玩家：" + bill.totalPlayer, headMiddle = "", headRight = gameInfo.match_type == 0 ? "奖励" : "排名";
        Drawable leftDrawable = getResources().getDrawable(R.mipmap.icon_club_chat_member);
        leftDrawable.setBounds(0, 0, leftDrawable.getIntrinsicWidth(), leftDrawable.getIntrinsicHeight());
        for (int i = 0; i < gameMemberList.size(); i++) {
            GameMemberEntity memberEntity = gameMemberList.get(i);
            RecordMemberItem item = new RecordMemberItem(memberEntity, memberEntity.userInfo != null ? memberEntity.userInfo.account : (i + ""));
            item.setBill(bill);
            item.setMType(item.Companion.getTYPE_NORMAL());
            if (i == 0) {
                if (gameInfo.gameConfig instanceof BaseMttConfig) {
                    BaseMttConfig config = (BaseMttConfig) gameInfo.gameConfig;
                    headMiddle = (config.ko_mode == 1 ? "猎头/赏金" : (config.ko_mode == 2 ? "赏金" : ""));
                    if (config.ko_mode == 1 && gameInfo.match_type != GameConstants.MATCH_TYPE_NORMAL) {//钻石赛
                        headMiddle = "猎头";
                    }
                }
                HeaderItem headerItem = new HeaderItem(i + "", headLeft, headMiddle, headRight, leftDrawable, HeaderItem.Companion.getTYPE_NORMAL());
                item.setHeader(headerItem);
            }
            mDatas.add(item);
        }
        mAdapter.updateDataSet(mDatas);
        mAdapter.setMMaxNum(bill.totalPlayer);
        if (mAdapter.mAnimatorNotifierObserver != null) {
            mAdapter.mAnimatorNotifierObserver.notified = false;
        }
    }

    private void getRecordMemberList(final String lastRank) {
        if (mRecordAction == null) {
            mRecordAction = new RecordAction(this, null);
        }
        isLoadMore = true;
        mRecordAction.getRecordMemberList(gameInfo.gid, lastRank, new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                ArrayList<GameMemberEntity> newDatas = RecordJsonTools.getGameMemberList(result);
                if (code == 0) {
                    gameMemberList.addAll(newDatas);
                    if (gameMemberList != null && gameMemberList.size() != 0 && StringUtil.isSpace(lastRank)) {
                        mRecordDetailsPagerAdapter.setFirstGameMember(gameMemberList.get(0));
                    }
                    updateMttDataset();
                }
                isLoadMore = false;
                //下面几行代码处理bug: 位森websocket长连返回的玩家列表的昵称可能是老的昵称，需要请求云信刷新用户，得到用户信息显示正确的昵称
                List<String> nimRefreshList = new ArrayList<>();
                for (int i = 0; i < newDatas.size(); i++) {
                    nimRefreshList.add(newDatas.get(i).userInfo.account);
                }
                NimUserInfoCache.getUserListByNeteaseEx(nimRefreshList, 0, null);
            }

            @Override
            public void onFailed() {
                isLoadMore = false;
            }
        });
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        view_indicator_1 = (View) findViewById(R.id.view_indicator_1);
        view_indicator_2 = (View) findViewById(R.id.view_indicator_2);
        mRecordDetailsPagerAdapter = new RecordDetailsPagerAdapter(getApplicationContext(), bill, gameMemberList);
        mViewPager.setAdapter(mRecordDetailsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    view_indicator_1.setBackgroundResource(R.mipmap.gamerecord_indicator_focused);
                    view_indicator_2.setBackgroundResource(R.mipmap.gamerecord_indicator_unfocused);
                    if (mAdapter != null) {
                        mAdapter.setCheckPosition(-1);
                        mAdapter.notifyDataSetChanged();
                    }
//                    setSwipeBackEnable(true);
                } else {
                    view_indicator_1.setBackgroundResource(R.mipmap.gamerecord_indicator_unfocused);
                    view_indicator_2.setBackgroundResource(R.mipmap.gamerecord_indicator_focused);
                    if (mAdapter != null) {
                        mAdapter.setCheckPosition(checkPosition < 1 ? 1 : checkPosition);
                        mAdapter.notifyDataSetChanged();
                    }
//                    setSwipeBackEnable(false);
                }
                super.onPageSelected(position);
            }
        });
        mViewPager.setCurrentItem(0);
        RecordGameBg record_game_bg = (RecordGameBg) findViewById(R.id.record_game_bg);
        record_game_bg.setInfo(gameInfo);
    }

    private void initView() {
        rl_record_content = (RelativeLayout) findViewById(R.id.rl_record_content);
        tv_record_details_info = (TextView) findViewById(R.id.tv_record_details_info);
        tv_game_memeber = (TextView) findViewById(R.id.tv_game_memeber);
        tv_record_details_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordDetailsInfoActivity.start(RecordDetailsActivity.this, bill);
            }
        });
        tv_record_details_info.setVisibility(View.GONE);
    }

    private void initAdapter() {
        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        if (gameMode == GameConstants.GAME_MODE_MTT) {
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                private int lastVisibleItem = 0;
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (mRecyclerView == null || isLoadMore) {
                        return;
                    }
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                    int firstVisibleItemPos = linearLayoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItemPos = linearLayoutManager.findLastVisibleItemPosition();
                    int totalItemCount = linearLayoutManager.getItemCount();
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && totalItemCount >= 1 && lastVisibleItemPos + 5 >= totalItemCount && (lastVisibleItemPos - firstVisibleItemPos) != totalItemCount) {
                        if (gameMemberList != null && gameMemberList.size() != 0) {
                            //Log.d("Circle", "加载更多");
                            isLoadMore = true;
                            getRecordMemberList(String.valueOf(gameMemberList.get(gameMemberList.size() - 1).ranking));
                        }
                    }
                    super.onScrollStateChanged(recyclerView, newState);
                }
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
        }
    }

    public void setData(GameEntity gameInfo) {
        gameMode = gameInfo.gameMode;
        if (gameMode == GameConstants.GAME_MODE_NORMAL) {
            tv_record_details_info.setVisibility(View.GONE);//tv_record_details_info.setVisibility(View.VISIBLE);这个功能暂时不需要
        } else if (gameMode == GameConstants.GAME_MODE_SNG || gameMode == GameConstants.GAME_MODE_MT_SNG) {
            tv_record_details_info.setVisibility(View.GONE);
        } else if (gameMode == GameConstants.GAME_MODE_MTT) {
            tv_record_details_info.setVisibility(View.GONE);
        }
        //
        setHeadTitle(gameInfo.name);
        //设置列表
//        setMembersList(gameMode);
        initAdapter();
        setInsuranceMembersList();
        //
//        String mvp = bill.getMvp();
//        String fish = bill.getFish();
//        String rechest = bill.getRichest();
//        GameMemberEntity mvpInfo = getInfoByAccount(mvp);
//        GameMemberEntity fishInfo = getInfoByAccount(fish);
//        GameMemberEntity rechestInfo = getInfoByAccount(rechest);
    }

    public void startUserProfile(String account) {
        if (!TextUtils.isEmpty(account) && !DemoCache.getAccount().equals(account)) {
            UserProfileActivity.start(this, account);
        }
    }

    public void setInsuranceMembersList() {
        if (gameMemberList == null || gameMemberList.size() == 0) {
            return;
        }
        for (GameMemberEntity gameMemberEntity : gameMemberList) {
            if (gameMemberEntity.insurance != 0) {
                insuranceMemberList.add(gameMemberEntity);
            }
        }
        Collections.sort(insuranceMemberList, insuranceComp);
    }

    public static Comparator<GameMemberEntity> insuranceComp = new Comparator<GameMemberEntity>() {
        @Override
        public int compare(GameMemberEntity o1, GameMemberEntity o2) {
            if (o1.premium != o2.premium) {
                long insurance = o1.insurance - o2.insurance;
                return insurance == 0 ? 0 : (insurance > 0 ? -1 : 1);
            } else {
                long premium = o1.premium - o2.premium;
                return premium == 0 ? 0 : (premium > 0 ? -1 : 1);
            }
        }
    };

    public static Comparator<GameMemberEntity> sngRankComp = new Comparator<GameMemberEntity>() {
        @Override
        public int compare(GameMemberEntity o1, GameMemberEntity o2) {
//            if (o1.ranking == 0 || o2.ranking == 0) {
//                return -1;
//            }
            int rank = o1.ranking - o2.ranking;
            return rank == 0 ? 0 : (rank > 0 ? 1 : -1);
        }
    };

    private void initHead(boolean isMeGameMgr, final String channel) {
        if (from == FROM_NORMAL) {
            //分享功能暂时隐藏
//            setHeadRightButton(R.string.club_info_share, new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mPaipuMoreView == null) {
//                        mPaipuMoreView = new PaipuMoreView(RecordDetailsActivity.this, CircleConstant.TYPE_PAIJU, false);
//                        mPaipuMoreView.setOnMoreListener(new PaipuMoreView.OnMoreListener() {
//                            @Override
//                            public void onSend(int position) {
//                                RecentContactSelectActivity.start(RecordDetailsActivity.this, CircleConstant.TYPE_PAIJU, bill, ContactSelectActivity.FROM_PAIJU_DETAILS);
//                            }
//                            @Override
//                            public void onShare(int position) {
//                                PublishActivity.start(RecordDetailsActivity.this, CircleConstant.TYPE_PAIJU, bill, from);
//                            }
//                            @Override
//                            public void onCollect(int position) {
//                            }
//                            @Override
//                            public void onShareWechat(int position , View view) {
//                            }
//                            @Override
//                            public void onShareMoments(int position , View view) {
//                            }
//                            @Override
//                            public void onShareSinaweibo(int position , View view) {
//                            }
//                        });
//                        mPaipuMoreView.setOnCallOverback(new PaipuMoreView.OnCallOverback() {
//                            @Override
//                            public void showShadow(boolean show) {
//                                if (show) {
//                                    findViewById(R.id.view_shadow).setVisibility(View.VISIBLE);
//                                } else {
//                                    findViewById(R.id.view_shadow).setVisibility(View.GONE);
//                                }
//                            }
//                        });
//                    }
//                    mPaipuMoreView.showAtLocation(findViewById(R.id.rl_record_details), Gravity.CENTER | Gravity.BOTTOM, 0, 0);
//                }
//            });
        }
        View.OnClickListener creatorClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChannelListOffAC.start(RecordDetailsActivity.this, gameInfo, club_channel);//赛事管理员
            }
        };
        View.OnClickListener mgrClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserByChannelOffAC.startByMgr(RecordDetailsActivity.this, gameInfo, channel);//渠道玩家
            }
        };
        TextView tv_head_right = ((TextView) findViewById(R.id.tv_head_right));
        tv_head_right.setVisibility(View.VISIBLE);
        boolean isMeCreator = DemoCache.getAccount().equals(gameInfo.creatorInfo.account);
        tv_head_right.setText(isMeCreator ? "管理员" : (isMeGameMgr ? "我的管理" : ""));
        tv_head_right.setOnClickListener(isMeCreator ? creatorClick : (isMeGameMgr ? mgrClick : null));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    protected void onDestroy() {
        if (mRecordAction != null) {
            mRecordAction.onDestroy();
            mRecordAction = null;
        }
        if (mGameAction != null) {
            mGameAction.onDestroy();
            mGameAction = null;
        }
        if (mClubAction != null) {
            mClubAction.onDestroy();
            mClubAction = null;
        }
        super.onDestroy();
    }

    @Override
    public void onDelete(int position) {
    }
    @Override
    public void onClick(int position) {
        if (position >= mAdapter.getItemCount() || !(mAdapter.getItem(position) instanceof RecordMemberItem)) {
            return;
        }
        RecordMemberItem item = (RecordMemberItem) mAdapter.getItem(position);
        GameMemberEntity gameMemberEntity = item.getMGameMemberEntity();
        if (mViewPager.getCurrentItem() == 0) {
            mViewPager.setCurrentItem(1);
        }
        checkPosition = position;
        mAdapter.setCheckPosition(checkPosition);
        mAdapter.notifyDataSetChanged();
        mRecordDetailsPagerAdapter.setRecordDetailsUserInfo(gameMemberEntity, bill.gameInfo);
    }
    @Override
    public void onLongClick(int position) {
    }
}
