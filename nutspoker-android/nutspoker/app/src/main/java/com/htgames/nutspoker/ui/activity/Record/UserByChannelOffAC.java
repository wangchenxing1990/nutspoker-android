package com.htgames.nutspoker.ui.activity.Record;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.interfaces.VolleyCallback;
import com.htgames.nutspoker.tool.JsonResolveUtil;
import com.htgames.nutspoker.ui.action.GameAction;
import com.htgames.nutspoker.ui.adapter.RecordMemberAdapter;
import com.htgames.nutspoker.ui.adapter.recordmember.RecordMemberAdap;
import com.htgames.nutspoker.ui.adapter.recordmember.RecordMemberItem;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.ui.helper.RecordMemberItemType;
import com.htgames.nutspoker.ui.helper.RecordMemberSection;
import com.htgames.nutspoker.ui.items.HeaderItem;
import com.htgames.nutspoker.ui.recycler.MeRecyclerView;
import com.htgames.nutspoker.view.ResultDataView;
import com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.bean.BaseMttConfig;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMemberEntity;
import com.netease.nim.uikit.bean.UserEntity;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.interfaces.IClick;
import com.netease.nim.uikit.nav.UIUrl;
import com.netease.nim.uikit.nav.UrlConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 周智慧 on 17/1/20.
 */
@UIUrl(urls = UrlConstants.RECORD_DETAIL_MY_MGR)
public class UserByChannelOffAC extends BaseActivity implements View.OnClickListener, IClick {
    private RecordMemberAdapter mRecordMemberAdapter;
    private RecordMemberAdapter searchAdapter;
    private RecordMemberAdap mAdapter;
    private ArrayList mDatas = new ArrayList();
    private RecordMemberAdap mSearchAdapter;
    private ArrayList mSearchDatas = new ArrayList();
    private GameAction mGameAction;//用来获取channel下的玩家列表
    private GameEntity gameInfo;
    private String channel;
    private String mgrId;//管理员id
    private ArrayList<GameMemberEntity> playerList = new ArrayList<GameMemberEntity>();//当前渠道(管理员)负责的玩家列表
    private ArrayList<GameMemberEntity> searchList = new ArrayList<GameMemberEntity>();//搜索到的玩家列表
    ArrayList<GameMemberEntity> insuranceMemberList = new ArrayList<>();
    private boolean isShowNormalAdapter = true;//是显示正常的adapter还是搜索adapter
    @BindView(R.id.search_mtt_mgr_recycler) MeRecyclerView mRecyclerView;
    @BindView(R.id.search_mtt_mgr_edit_search) ClearableEditTextWithIcon search_mtt_mgr_edit_search;
    @BindView(R.id.edit_text_parent) View edit_text_parent;
    @BindView(R.id.mgr_action_checkin_cnt) TextView mgr_action_checkin_cnt;//批准报名人数
    @BindView(R.id.mgr_action_buyin_cnt_right) TextView mgr_action_buyin_cnt_right;//批准买入次数
    @BindView(R.id.mgr_action_buyin_cnt_left_bottom) TextView mgr_action_buyin_cnt_left_bottom;
    @BindView(R.id.mResultDataView) ResultDataView mResultDataView;
    @BindView(R.id.refresh_layout) SwipeRefreshLayout refresh_layout;
    @BindView(R.id.free_info_container) View free_info_container;
    @BindView(R.id.match_info_container) View match_info_container;
    @BindView(R.id.tv_free_player_num) TextView tv_free_player_num;
    @BindView(R.id.tv_free_total_buy) TextView tv_free_total_buy;
    @BindView(R.id.tv_free_reward) TextView tv_free_reward;
    @BindView(R.id.sng_info_container) View sng_info_container;
    @BindView(R.id.tv_sng_checkin_player) TextView tv_sng_checkin_player;
    @BindView(R.id.ll_record_member_column) View ll_record_member_column;
    @BindView(R.id.tv_game_memeber) TextView tv_game_memeber;
    @BindView(R.id.tv_details_all_buy_title) TextView tv_details_all_buy_title;
    @BindView(R.id.tv_details_all_gain_title) TextView tv_details_all_gain_title;
    public static void startByMgr(Activity activity, GameEntity gameInfo, String mgrChannel) {//管理员点入
        if (gameInfo == null) {
            Toast.makeText(activity, "信息不全", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(activity, UserByChannelOffAC.class);
        intent.putExtra(UrlConstants.RECORD_DETAIL_MY_MGR_HEAD_TITLE_KEY, "参赛人员");
        intent.putExtra(GameConstants.KEY_GAME_INFO, gameInfo);//不要传bill，intent传输数据大小有限制,bill太大会crash
        intent.putExtra(UrlConstants.RECORD_DETAIL_MY_MGR_CHANNEL, mgrChannel);
        intent.putExtra(UrlConstants.RECORD_DETAIL_MY_MGR_FROM_TYPE, UrlConstants.RECORD_DETAIL_MY_MGR_FROM_HEAD);
        intent.putExtra(UrlConstants.RECORD_DETAIL_MY_MGR_ID, DemoCache.getAccount());
        activity.startActivity(intent);
    }

    public static void startByGameCreator(Activity activity, GameEntity gameInfo, String mgrChannel, String pageTitle, String mgrAccount) {//管理员点入
        if (gameInfo == null) {
            Toast.makeText(activity, "信息不全", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(activity, UserByChannelOffAC.class);
        intent.putExtra(GameConstants.KEY_GAME_INFO, gameInfo);//不要传bill，intent传输数据大小有限制,bill太大会crash
        intent.putExtra(UrlConstants.RECORD_DETAIL_MY_MGR_HEAD_TITLE_KEY, pageTitle);
        intent.putExtra(UrlConstants.RECORD_DETAIL_MY_MGR_ID, mgrAccount);
        intent.putExtra(UrlConstants.RECORD_DETAIL_MY_MGR_FROM_TYPE, UrlConstants.RECORD_DETAIL_MY_MGR_FROM_ITEM);
        intent.putExtra(UrlConstants.RECORD_DETAIL_MY_MGR_CHANNEL, mgrChannel);
        activity.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gameInfo = (GameEntity) getIntent().getSerializableExtra(GameConstants.KEY_GAME_INFO);
        channel = (String) getIntent().getSerializableExtra(UrlConstants.RECORD_DETAIL_MY_MGR_CHANNEL);
        mgrId = (String) getIntent().getSerializableExtra(UrlConstants.RECORD_DETAIL_MY_MGR_ID);
        mGameAction = new GameAction(this, null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mtt_my_mgr);
        mUnbinder = ButterKnife.bind(this);
        findViewById(R.id.mgr_gamecode).setVisibility(View.GONE);
        setHeadTitle(getIntent().getStringExtra(UrlConstants.RECORD_DETAIL_MY_MGR_HEAD_TITLE_KEY));
        plunderFocus();
        initAdapter();
        free_info_container.setVisibility(gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL ? View.VISIBLE : View.GONE);
        sng_info_container.setVisibility(gameInfo.gameMode == GameConstants.GAME_MODE_SNG ? View.VISIBLE : View.GONE);
        match_info_container.setVisibility(gameInfo.gameMode >= GameConstants.GAME_MODE_MT_SNG ? View.VISIBLE : View.GONE);
        search_mtt_mgr_edit_search.setIconResource(R.mipmap.icon_search);
        search_mtt_mgr_edit_search.setHint("玩家昵称");
        search_mtt_mgr_edit_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_mtt_mgr_edit_search.setFocusable(true);
                search_mtt_mgr_edit_search.setFocusableInTouchMode(true);
                search_mtt_mgr_edit_search.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
//                isShowNormalAdapter = false;
//                mResultDataView.successShow();//隐藏
            }
        });
        search_mtt_mgr_edit_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String key = search_mtt_mgr_edit_search.getText().toString();
                    key = key.trim();
                    if (TextUtils.isEmpty(key)) {
                        Toast.makeText(getApplicationContext(), R.string.not_allow_empty, Toast.LENGTH_SHORT).show();
                    } else {
                        searchKey(key);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    return true;
                }
                return false;
            }
        });
        search_mtt_mgr_edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || StringUtil.isEmpty(s.toString())) {//输入框的内容为空时显示"历史记录"，隐藏"搜索结果"
                    isShowNormalAdapter = true;
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.setLongPressDragEnabled(false)
                            .setHandleDragEnabled(true)
                            .setSwipeEnabled(false)
                            .setUnlinkAllItemsOnRemoveHeaders(true)
                            .setDisplayHeadersAtStartUp(true)
                            .setStickyHeaders(true);
                    refreshRecyclerViewResult();
                }
            }
        });
        refreshRecyclerViewResult();
        refresh_layout.setColorSchemeResources(R.color.login_solid_color, R.color.login_solid_color, R.color.login_solid_color, R.color.login_solid_color);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pagesize = -1;//分页查询的总页数
                curPage = 0;
                playerList.clear();
                checkin_player = -1;
                insurancePond = 0;
                getListData(true);
            }
        });
        mgr_action_buyin_cnt_left_bottom.setVisibility(gameInfo != null && gameInfo.gameConfig instanceof BaseMttConfig && ((BaseMttConfig) gameInfo.gameConfig).ko_mode != 0 ? View.VISIBLE : View.GONE);
        ll_record_member_column.setVisibility(View.GONE);
        getListData(true);
    }

    private boolean mIsLoading = false;
    private boolean mIsLastItem = false;
    private void initAdapter() {
        int insurancePond = 0;
        mRecordMemberAdapter = new RecordMemberAdapter(getApplicationContext(), gameInfo.gameMode, gameInfo);
        mRecordMemberAdapter.addSection(RecordMemberItemType.NORMAL, new RecordMemberSection(RecordMemberItemType.NORMAL, playerList));
        for (int i = 0; i < playerList.size(); i++) {
            GameMemberEntity gameMemberEntity = playerList.get(i);
            if (gameMemberEntity.insurance != 0) {
                insuranceMemberList.add(gameMemberEntity);
            }
            insurancePond = insurancePond + gameMemberEntity.insurance;
        }
        mRecordMemberAdapter.insurancePond = -insurancePond;
        Collections.sort(insuranceMemberList, RecordDetailsActivity.insuranceComp);
        mRecordMemberAdapter.addSection(RecordMemberItemType.INSURANCE, new RecordMemberSection(RecordMemberItemType.INSURANCE, insuranceMemberList));
//        mRecyclerView.setddddAdapter(mRecordMemberAdapter);
        refreshRecyclerViewResult();
        searchAdapter = new RecordMemberAdapter(getApplicationContext(), gameInfo.gameMode, gameInfo);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                int firstVisibleItemPos = linearLayoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPos = linearLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = linearLayoutManager.getItemCount();
                if (!mIsLoading && !mIsLastItem) {
                    if (lastVisibleItemPos + 5 >= totalItemCount && (lastVisibleItemPos - firstVisibleItemPos) != totalItemCount) {
                        getListData(false);
                    }
                }
            }
        });
        mAdapter = new RecordMemberAdap(mDatas, this, true);
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
        mAdapter.setLongPressDragEnabled(false)
                .setHandleDragEnabled(true)
                .setSwipeEnabled(false)
                .setUnlinkAllItemsOnRemoveHeaders(true)
                .setDisplayHeadersAtStartUp(true)
                .setStickyHeaders(true);

        mSearchAdapter = new RecordMemberAdap(mSearchDatas, this, true);
        mSearchAdapter.setMGameInfo(gameInfo);
        mSearchAdapter.setAnimateChangesWithDiffUtil(true)
                .setAnimateToLimit(10000)
                .setNotifyMoveOfFilteredItems(false)
                .setNotifyChangeOfUnfilteredItems(true)
                .setAnimationDelay(40)
                .setAnimationOnScrolling(true)
                .setAnimationOnReverseScrolling(true)
                .setOnlyEntryAnimation(true);
    }

    private void refreshRecyclerViewResult() {
        if (refresh_layout == null || mRecyclerView == null || mResultDataView == null) {
            return;
        }
        if (isShowNormalAdapter) {
            refresh_layout.setEnabled(true);
            if (playerList == null || playerList.size() <= 0) {
                mResultDataView.nullDataShow(R.string.data_null);
            } else {
                mResultDataView.successShow();
                mRecyclerView.setVisibility(View.VISIBLE);
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
        mRecyclerView.setAdapter(mSearchAdapter);
        //调用搜索接口
        mGameAction.searchChannelPlayer(channel, gameInfo.gid, word, new VolleyCallback() {
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
                            int ranking = item.optInt("ranking");
                            int reward = item.optInt("reward");
                            GameMemberEntity bean = new GameMemberEntity();
                            UserEntity userInfo = new UserEntity();
                            userInfo.uuid = item.optString("uuid");
                            if ("0".equals(userInfo.uuid)) {
                                userInfo.uuid = "";
                            }
                            userInfo.account = uid;
                            userInfo.name = nickname;
                            bean.userInfo = userInfo;
                            bean.rebuyCnt = rebuy_cnt;
                            bean.ranking = ranking;
                            bean.reward = reward;
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
        searchAdapter.addSection(RecordMemberItemType.NORMAL, new RecordMemberSection(RecordMemberItemType.NORMAL, searchList));
        searchAdapter.notifyDataSetChanged();
        mSearchAdapter.setLongPressDragEnabled(false)
                .setHandleDragEnabled(true)
                .setSwipeEnabled(false)
                .setUnlinkAllItemsOnRemoveHeaders(true)
                .setDisplayHeadersAtStartUp(true)
                .setStickyHeaders(true);
        mSearchDatas.clear();
        boolean isNormal = gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL;
        String headLeft = "玩家：" + searchList.size(), headMiddle = isNormal ? "买入" : "", headRight = isNormal ? "盈利" : (gameInfo.match_type == 0 ? "奖励" : "排名");
        Drawable leftDrawable = getResources().getDrawable(R.mipmap.icon_club_chat_member);
        leftDrawable.setBounds(0, 0, leftDrawable.getIntrinsicWidth(), leftDrawable.getIntrinsicHeight());
        for (int i = 0; i < searchList.size(); i++) {
            GameMemberEntity memberEntity = searchList.get(i);
            RecordMemberItem item = new RecordMemberItem(memberEntity, memberEntity.userInfo != null ? memberEntity.userInfo.account : (i + ""));
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
            mSearchDatas.add(item);
        }
        mSearchAdapter.updateDataSet(mSearchDatas);
        refreshRecyclerViewResult();
    }

    int checkin_player = -1;
    int insurancePond = 0;
    private int pagesize = -1;//分页查询的总页数
    private int curPage = 0;
    private void getListData(final boolean clearOld) {
        if (pagesize > -1 && curPage >= pagesize) {
            return;//表示分页查询已经查询结束，即数据请求完了
        }
        mIsLoading = true;
        mGameAction.getChannelPlayerListOff(gameInfo.gid, curPage, channel, mgrId, new VolleyCallback() {
            @Override
            public void onResponse(String response) {
                mIsLoading = false;
                if (refresh_layout != null) {
                    refresh_layout.setRefreshing(false);
                }
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        curPage++;
                        JSONObject data = json.getJSONObject("data");
                        if (clearOld) {
                            playerList.clear();
                            insuranceMemberList.clear();
                        }
                        final ArrayList<GameMemberEntity> newData = JsonResolveUtil.getUserByMgrOff(data, null);
                        for (int i = 0; i < newData.size(); i++) {
                            GameMemberEntity gameMemberEntity = newData.get(i);
                            if (gameMemberEntity.insurance != 0) {
                                insuranceMemberList.add(gameMemberEntity);
                            }
                            insurancePond = insurancePond + gameMemberEntity.insurance;
                        }
                        if (mRecordMemberAdapter != null) {
                            mRecordMemberAdapter.insurancePond = -insurancePond;
                        }
                        Collections.sort(insuranceMemberList, RecordDetailsActivity.insuranceComp);
//                        Collections.sort(playerList, sngRankComp);//让服务端排序
                        if (curPage == 1 && mgr_action_checkin_cnt != null) {//这些信息每一页都相同，只在第一页判断就行了
                            checkin_player = data.getInt("checkin_player");
                            int rebuy_cnt = data.getInt("rebuy_cnt");
                            pagesize = data.getInt("pagesize");
                            mIsLastItem = (curPage >= pagesize);//根据总页数判断数据是否已经请求完
                            mgr_action_checkin_cnt.setText("批准报名人数：" + checkin_player);
                            mgr_action_buyin_cnt_right.setText("批准买入次数：" + (checkin_player + rebuy_cnt));
                            if (gameInfo != null && gameInfo.gameConfig instanceof BaseMttConfig) {
                                if (((BaseMttConfig) gameInfo.gameConfig).ko_mode == 0) {
                                    mgr_action_buyin_cnt_right.setText("批准买入次数：" + (checkin_player + rebuy_cnt));
                                } else if (((BaseMttConfig) gameInfo.gameConfig).ko_mode == 1) {
                                    DecimalFormat df = new DecimalFormat("#.##");
                                    mgr_action_buyin_cnt_right.setText("猎头总数：" + df.format(data.optInt("ko_head_total") / 100f));
                                } else if (((BaseMttConfig) gameInfo.gameConfig).ko_mode == 2) {
                                    mgr_action_buyin_cnt_right.setText("总赏金：" + data.optInt("ko_reward_total"));
                                }
                            }
                            mgr_action_buyin_cnt_left_bottom.setText("批准买入次数：" + (checkin_player + rebuy_cnt));
                            //下面是普通局的
                            tv_free_player_num.setText("" + checkin_player);
                            tv_free_total_buy.setText("" + data.optInt("all_buy"));
                            int all_reward = data.optInt("all_reward") + insurancePond;
                            tv_free_reward.setText("" + all_reward);
                            //下面是sng的
                            tv_sng_checkin_player.setText("参赛人数：" + checkin_player);
                        }
                        List<String> accounts = new ArrayList<>();
                        for (int i = 0; i < newData.size(); i++) {
                            accounts.add(newData.get(i).userInfo.account);
                        }
                        NimUserInfoCache.getInstance().getUserListByNeteaseEx(accounts, 0, new NimUserInfoCache.IFetchCallback() {
                            @Override
                            public void onFetchFinish(List list) {
                                for (int i = 0; i < newData.size(); i++) {
                                    GameMemberEntity playerEntity = newData.get(i);
                                    playerEntity.userInfo.name = NimUserInfoCache.getInstance().getUserDisplayName(playerEntity.userInfo.account);
                                }
                                if (playerList != null && newData != null) {
                                    playerList.addAll(newData);
                                }
                                normalDatasetChanged();
                            }
                        });
                    } else {
                        Toast.makeText(UserByChannelOffAC.this, "获取数据失败code:" + code, Toast.LENGTH_SHORT).show();
                    }
                } catch(Exception e) {
                    Toast.makeText(UserByChannelOffAC.this, "获取数据异常:" + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onErrorResponse(VolleyError error) {
                if (refresh_layout != null) {
                    refresh_layout.setRefreshing(false);
                }
                mIsLoading = false;
                normalDatasetChanged();
            }
        });
    }

    private void normalDatasetChanged() {
        if (mRecordMemberAdapter != null) {
            mRecordMemberAdapter.notifyDataSetChanged();
        }
        mDatas.clear();
        boolean isNormal = gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL;
        String headLeft = "玩家：" + checkin_player, headMiddle = isNormal ? "买入" : "", headRight = isNormal ? "盈利" : (gameInfo.match_type == 0 ? "奖励" : "排名");
        Drawable leftDrawable = getResources().getDrawable(R.mipmap.icon_club_chat_member);
        leftDrawable.setBounds(0, 0, leftDrawable.getIntrinsicWidth(), leftDrawable.getIntrinsicHeight());
        for (int i = 0; i < playerList.size(); i++) {
            GameMemberEntity memberEntity = playerList.get(i);
            RecordMemberItem item = new RecordMemberItem(memberEntity, memberEntity.userInfo != null ? memberEntity.userInfo.account : (i + ""));
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
        for (int i = 0; i < insuranceMemberList.size(); i++) {
            GameMemberEntity memberEntity = insuranceMemberList.get(i);
            RecordMemberItem item = new RecordMemberItem(memberEntity, memberEntity.userInfo != null ? (memberEntity.userInfo.account + " insurance") : (i + " insurance"));
            item.setMType(item.Companion.getTYPE_INSURANCE());
            if (i == 0) {
                HeaderItem headerItem = new HeaderItem((i + 10) + "", headLeft, headMiddle, headRight, leftDrawable, HeaderItem.Companion.getTYPE_INSURANCE());
                headerItem.setInsurancePond(-insurancePond);
                item.setHeader(headerItem);
            }
            mDatas.add(item);
        }
        mAdapter.updateDataSet(mDatas);
        refreshRecyclerViewResult();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
    }

    /**
     * 抢夺焦点
     */
    private void plunderFocus() {
        search_mtt_mgr_edit_search.clearFocus();
        search_mtt_mgr_edit_search.setFocusable(false);
        search_mtt_mgr_edit_search.setFocusableInTouchMode(false);
        search_mtt_mgr_edit_search.setText("");
        edit_text_parent.setFocusableInTouchMode(true);
        edit_text_parent.setFocusable(true);
        edit_text_parent.requestFocus();
    }

    @Override
    public void onDelete(int position) {
    }

    @Override
    public void onClick(int position) {
    }

    @Override
    public void onLongClick(int position) {
    }

    @Override
    protected void onDestroy() {
        if (mGameAction != null) {
            mGameAction.onDestroy();
            mGameAction = null;
        }
        super.onDestroy();
    }
}
