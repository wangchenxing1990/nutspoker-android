package com.htgames.nutspoker.game.match.activity;

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
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.game.match.adapter.MatchPlayerAdapter;
import com.htgames.nutspoker.game.match.bean.MatchPlayerEntity;
import com.htgames.nutspoker.game.match.helper.MatchManager;
import com.htgames.nutspoker.game.match.helper.MatchMgrItem;
import com.htgames.nutspoker.game.match.item.MatchPlayerItemType;
import com.htgames.nutspoker.game.match.item.PlayerSection;
import com.htgames.nutspoker.game.model.GameStatus;
import com.htgames.nutspoker.interfaces.VolleyCallback;
import com.htgames.nutspoker.tool.JsonResolveUtil;
import com.htgames.nutspoker.ui.action.GameAction;
import com.htgames.nutspoker.ui.adapter.FooterLoadingVH;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.ui.helper.WealthHelper;
import com.htgames.nutspoker.ui.recycler.MeRecyclerView;
import com.htgames.nutspoker.view.ResultDataView;
import com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.bean.BaseMttConfig;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameNormalConfig;
import com.netease.nim.uikit.bean.GameSngConfigEntity;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.nav.UIUrl;
import com.netease.nim.uikit.nav.UrlConstants;
import com.netease.nim.uikit.pulltorefresh.BottomLoadingView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 周智慧 on 17/1/19.
 * mtt大厅右上角"我的管理"点击落地页
 */
@UIUrl(urls = UrlConstants.MTT_MY_MGR)
public class UserByChannelAC extends BaseActivity implements View.OnClickListener {
    private GameAction mGameAction;//用来处理移除报名玩家
    private String gameId = "";
    private String creatorId = "";
    private String gameChannel;
    MatchPlayerAdapter adapter;
    MatchPlayerAdapter searchAdapter;
    private ArrayList<MatchPlayerEntity> playerList = new ArrayList<MatchPlayerEntity>();
    private ArrayList<MatchPlayerEntity> searchList = new ArrayList<MatchPlayerEntity>();
    private boolean isShowNormalAdapter = true;//是显示正常的adapter还是搜索adapter
    private boolean isLoading = false;//是否在请求数据
    private boolean isFinished = false;//数据是否已经请求完
    GameEntity gameInfo;
    @BindView(R.id.search_mtt_mgr_recycler) MeRecyclerView mRecyclerView;
    @BindView(R.id.search_mtt_mgr_edit_search) ClearableEditTextWithIcon search_mtt_mgr_edit_search;
    @BindView(R.id.edit_text_parent) View edit_text_parent;
    @BindView(R.id.mgr_action_checkin_cnt) TextView mgr_action_checkin_cnt;//批准报名人数
    @BindView(R.id.mgr_action_buyin_cnt_right) TextView mgr_action_buyin_cnt_right;//批准买入次数
    @BindView(R.id.mgr_action_buyin_cnt_left_bottom) TextView mgr_action_buyin_cnt_left_bottom;
    @BindView(R.id.mgr_gamecode) TextView mgr_gamecode;//我的邀请码
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
    public static void startByGameCreator(Activity activity, GameEntity gameEntity) {
        Intent data = new Intent(activity, UserByChannelAC.class);
        data.putExtra(UrlConstants.MTT_MY_MGR_GAME_ID, gameEntity.gid);
        data.putExtra(UrlConstants.MTT_MY_MGR_CREATOR_ID, gameEntity.creatorInfo.account);
        data.putExtra(UrlConstants.MTT_MY_MGR_GAME_CODE, gameEntity.channel);
        data.putExtra(GameConstants.KEY_GAME_INFO, gameEntity);
        activity.startActivity(data);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameInfo = (GameEntity) getIntent().getSerializableExtra(GameConstants.KEY_GAME_INFO);
        mGameAction = new GameAction(this, null);
        gameId = getIntent().getStringExtra(UrlConstants.MTT_MY_MGR_GAME_ID);
        creatorId = getIntent().getStringExtra(UrlConstants.MTT_MY_MGR_CREATOR_ID);
        gameChannel = getIntent().getStringExtra(UrlConstants.MTT_MY_MGR_GAME_CODE);
        setContentView(R.layout.activity_mtt_my_mgr);
        mUnbinder = ButterKnife.bind(this);
        free_info_container.setVisibility(gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL ? View.VISIBLE : View.GONE);
        sng_info_container.setVisibility(gameInfo.gameMode == GameConstants.GAME_MODE_SNG ? View.VISIBLE : View.GONE);
        match_info_container.setVisibility(gameInfo.gameMode >= GameConstants.GAME_MODE_MT_SNG ? View.VISIBLE : View.GONE);
        getListData(true);
        mgr_gamecode.setVisibility(View.GONE);
        mgr_gamecode.setText("我的邀请码：" + gameChannel);
        adapter = new MatchPlayerAdapter(this, null, null);
        adapter.isAllPlayer = false;
        adapter.gameInfo = gameInfo;
        adapter.gameStatus = gameInfo.status;
        searchAdapter = new MatchPlayerAdapter(this, null, null);
        searchAdapter.isAllPlayer = false;
        searchAdapter.gameInfo = gameInfo;
        searchAdapter.gameStatus = gameInfo.status;
        if (gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL) {
            adapter.addSection(MatchPlayerItemType.NormalMgrPlayer, new PlayerSection(MatchPlayerItemType.NormalMgrPlayer, playerList));
            searchAdapter.addSection(MatchPlayerItemType.NormalMgrPlayer, new PlayerSection(MatchPlayerItemType.NormalMgrPlayer, searchList));
        } else if (gameInfo.gameMode == GameConstants.GAME_MODE_SNG) {
            adapter.addSection(MatchPlayerItemType.SngMgrPlayer, new PlayerSection(MatchPlayerItemType.SngMgrPlayer, playerList));
            searchAdapter.addSection(MatchPlayerItemType.SngMgrPlayer, new PlayerSection(MatchPlayerItemType.SngMgrPlayer, searchList));
        } else if (gameInfo.gameMode == GameConstants.GAME_MODE_MTT) {
            adapter.addSection(MatchPlayerItemType.MttPlayer, new PlayerSection(MatchPlayerItemType.MttPlayer, playerList));
            searchAdapter.addSection(MatchPlayerItemType.MttPlayer, new PlayerSection(MatchPlayerItemType.MttPlayer, searchList));
        }
        plunderFocus();
        setHeadTitle("参赛人员");
        mRecyclerView.setAdapter(adapter);
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
//                mRecyclerView.setAdapter(searchAdapter);
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
                    mRecyclerView.setAdapter(adapter);
                    refreshRecyclerViewResult();
                }
            }
        });
        refreshRecyclerViewResult();
        refresh_layout.setColorSchemeResources(R.color.login_solid_color, R.color.login_solid_color, R.color.login_solid_color, R.color.login_solid_color);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstVisibleItemPos = linearLayoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPos = linearLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = linearLayoutManager.getItemCount();
                if (!isLoading && !isFinished) {
                    if (lastVisibleItemPos + 5 >= totalItemCount && (lastVisibleItemPos - firstVisibleItemPos) != totalItemCount) {
                        // 将会触发上拉加载更多事件
                        getListData(false);
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
        registerObservers(true);
        //需求更改：猎头总数和赏金总数只在战绩里面显示，比赛的时候获取不到数据，不显示
        mgr_action_buyin_cnt_left_bottom.setVisibility(gameInfo != null && gameInfo.gameConfig instanceof BaseMttConfig && ((BaseMttConfig) gameInfo.gameConfig).ko_mode != 0 ? View.VISIBLE : View.GONE);
        ll_record_member_column.setVisibility(View.GONE);
    }

    private void notifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    //重新刷新界面，从第一页开始请求
    private void refreshList() {
        pagesize = -1;//分页查询的总页数
        curPage = 0;
        playerList.clear();
        getListData(true);
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

    private void searchKey(String word) {
        searchList.clear();
        isShowNormalAdapter = false;
        mRecyclerView.setAdapter(searchAdapter);
        //调用搜索接口
        mGameAction.searchChannelPlayer(gameChannel, gameId, word, new VolleyCallback() {
            @Override
            public void onResponse(String response) {
                //{"code":0,"message":"ok","data":[{"id":"10041","nickname":"sugar","gid":"1946","ranking":0,"uid":"10041","reward":"200","rebuy_cnt":0,"index":false}]}
                try {
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
                            bean.chips = item.optInt("reward");
                            DecimalFormat df = new DecimalFormat("#.##");
                            bean.ko_head_cnt = df.format(item.optInt("ko_head_cnt") / 100f);
                            bean.ko_head_reward = item.optInt("ko_head_reward");
                            bean.ko_worth = item.optInt("ko_worth");
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
            searchAdapter.notifyDataSetChanged();
        }
        refreshRecyclerViewResult();
    }

    private void showDeleteDialog(final MatchPlayerEntity bean) {
        EasyAlertDialog backDialog = EasyAlertDialogHelper.createOkCancelDiolag(UserByChannelAC.this, "", "确定移除“" + bean.nickname + "”？", true,
                new EasyAlertDialogHelper.OnDialogActionListener() {
                    @Override
                    public void doCancelAction() {
                    }
                    @Override
                    public void doOkAction() {
                        deleteCheckinPlayer(bean);
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            backDialog.show();
        }
    }

    private void deleteCheckinPlayer(final MatchPlayerEntity bean) {
        mGameAction.delCheckinPlayer(gameChannel, bean.uid, DemoCache.getAccount(), new VolleyCallback() {
            @Override
            public void onResponse(String response) {
                //有接口mCheckinPlayerChange动态更新数据，不需要再次更新数据了-------------------{"code":0,"message":"ok","data":""}
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        Toast.makeText(UserByChannelAC.this, "移除成功", Toast.LENGTH_SHORT).show();
                        removeListData(bean);
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                        if (searchAdapter != null) {
                            searchAdapter.notifyDataSetChanged();
                        }
                        refreshRecyclerViewResult();
                        refreshList();//暂时从头开始请求，体验不是很好
                    } else if (code == 3015) {
                        Toast.makeText(UserByChannelAC.this, "您已经不是管理员", Toast.LENGTH_SHORT).show();//现在需求是只要管理员成功审批过一个玩家的报名请求，那么这个管理员就不能被移除
                    } else {// TODO: 17/2/21 code是3031应该是表示该玩家已经自己取消报名了，等待黄老师确认
                        Toast.makeText(UserByChannelAC.this, "比赛开始后无法移除报名玩家", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(UserByChannelAC.this, "移除失败", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UserByChannelAC.this, "移除失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeListData(final MatchPlayerEntity bean) {
        Iterator<MatchPlayerEntity> iterator = playerList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().uid.equals(bean.uid)) {
                iterator.remove();
                break;
            }
        }
        Iterator<MatchPlayerEntity> searchIterator = searchList.iterator();
        while (searchIterator.hasNext()) {
            if (searchIterator.next().uid.equals(bean.uid)) {
                searchIterator.remove();
                break;
            }
        }
    }

    private void refreshRecyclerViewResult() {
        if (ll_record_member_column == null || refresh_layout == null || mResultDataView == null) {
            return;
        }
        ll_record_member_column.setVisibility(isShowNormalAdapter && playerList.size() > 0 ? View.VISIBLE : View.GONE);
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
    @Override
    public void onClick(View v) {
        int viewId = v.getId();
    }

    public class ThisAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private int TYPE_ITEM = 0;
        private int TYPE_FOOTER = 1;
        ArrayList<MatchPlayerEntity> mDatas;
        public void setData(ArrayList<MatchPlayerEntity> datas) {
            mDatas = datas;
            notifyDataSetChanged();
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType==TYPE_ITEM) {
                View view = LayoutInflater.from(UserByChannelAC.this).inflate(R.layout.view_swipe_content, parent, false);
                return new ThisViewHolder(view);
            } else if (viewType==TYPE_FOOTER) {
                BottomLoadingView view = new BottomLoadingView(UserByChannelAC.this);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(lp);
                return new FooterLoadingVH(view);
            }
            return null;
        }
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (mDatas != null && mDatas.size() > position && holder instanceof ThisViewHolder) {
                ((ThisViewHolder) holder).bind(mDatas.get(position));
            }
            if (holder instanceof FooterLoadingVH) {
                if (isLoading) {
                    ((FooterLoadingVH) holder).stateLoading();
                } else {
//                    if (playerList != null && playerList.size() > 0) {
//                        ((FooterLoadingVH) holder).stateAllDataLoadDone(isFinished);
//                    } else {
//                    }
                    ((FooterLoadingVH) holder).gone();
                }
            }
        }
        @Override
        public int getItemCount() {
            return mDatas == null ? 1 : mDatas.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position + 1 == getItemCount()) {
                return TYPE_FOOTER;
            } else {
                return TYPE_ITEM;
            }
        }
    }

    public class ThisViewHolder extends RecyclerView.ViewHolder {
        TextView tv_club_myself;
        private HeadImageView contacts_item_head;
        public TextView btn_contact_action_agree;
        public TextView contacts_item_name;
        public TextView user_type;
        public TextView contacts_item_desc;
        public ThisViewHolder(View itemView) {
            super(itemView);
            tv_club_myself = (TextView) itemView.findViewById(R.id.tv_club_myself);
            contacts_item_head = (HeadImageView) itemView.findViewById(R.id.contacts_item_head);
            btn_contact_action_agree = (TextView) itemView.findViewById(R.id.btn_contact_action_agree);
            contacts_item_name = (TextView) itemView.findViewById(R.id.contacts_item_name);
            user_type = (TextView) itemView.findViewById(R.id.user_type);
            contacts_item_desc = (TextView) itemView.findViewById(R.id.contacts_item_desc);
        }
        public void bind(final MatchPlayerEntity bean) {
//            tv_club_myself.setVisibility(bean.uid.equals(DemoCache.getAccount()) ? View.VISIBLE : View.GONE);
            contacts_item_head.loadBuddyAvatar(bean.uid);
            contacts_item_name.setText(bean.nickname);
            user_type.setText("R" + bean.rebuyCnt);
            user_type.setVisibility(bean.rebuyCnt <= 0 ? View.GONE : View.VISIBLE);
            contacts_item_desc.setText("ID: " + bean.uuid);
            contacts_item_desc.setVisibility(StringUtil.isSpace(bean.uuid) ? View.GONE : View.VISIBLE);
            btn_contact_action_agree.setText("移除");
            btn_contact_action_agree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteDialog(bean);
                }
            });
            btn_contact_action_agree.setVisibility(View.GONE);
            if (gameInfo.gameMode <= GameConstants.GAME_MODE_SNG) {////////////////////////////////////普通
                tv_club_myself.setVisibility(gameInfo.status == GameStatus.GAME_STATUS_WAIT ? View.GONE : View.VISIBLE);
                tv_club_myself.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                WealthHelper.SetMoneyText(tv_club_myself, bean.reward, itemView.getContext());
                Drawable leftDrawable = itemView.getContext().getResources().getDrawable(R.mipmap.icon_club_chat_chip);
                leftDrawable.setBounds(0, 0, leftDrawable.getIntrinsicWidth(), leftDrawable.getIntrinsicHeight());
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mGameAction != null) {
            mGameAction.onDestroy();
            mGameAction = null;
        }
        registerObservers(true);
        super.onDestroy();
    }

    private int pagesize = -1;//分页查询的总页数
    private int curPage = 0;
    private void getListData(final boolean clearOld) {
        if (pagesize > -1 && curPage >= pagesize) {
            return;//表示分页查询已经查询结束，即数据请求完了
        }
        isLoading = true;
        mGameAction.getChannelPlayerList(gameChannel, curPage, new VolleyCallback() {
            @Override
            public void onResponse(String response) {
                refresh_layout.setRefreshing(false);
                isLoading = false;
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        curPage++;
                        JSONObject data = json.getJSONObject("data");
                        int checkin_player = data.getInt("checkin_player");
                        int rebuy_cnt = data.getInt("rebuy_cnt");
                        pagesize = data.getInt("pagesize");
                        isFinished = (curPage >= pagesize);//根据总页数判断数据是否已经请求完
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
                        tv_free_reward.setText("" + data.optInt("all_reward"));
                        //下面是sng的
                        tv_sng_checkin_player.setText("批准报名人数：" + checkin_player);
                        final ArrayList<MatchPlayerEntity> newData = JsonResolveUtil.getUserByMgr(data, new NimUserInfoCache.IFetchCallback() {
                            @Override
                            public void onFetchFinish(List list) {
                                if (playerList != null) {
                                    if (clearOld) {
                                        playerList.clear();
                                    }
                                    if (list != null) {
                                        playerList.addAll(list);
                                    }
                                }
                                if (ll_record_member_column != null) {
                                    ll_record_member_column.setVisibility(isShowNormalAdapter && playerList.size() > 0 ? View.VISIBLE : View.GONE);
                                }
                                normalDatasetChanged();
                            }
                        });
                        tv_game_memeber.setText(getResources().getString(R.string.record_details_colunm_members, checkin_player));
                        if (gameInfo != null) {
                            if (gameInfo.gameConfig instanceof GameNormalConfig) {
                                tv_details_all_buy_title.setText(R.string.record_details_colunm_all_buy);
                                tv_details_all_gain_title.setText(R.string.record_details_colunm_all_winchips);
                            } else if (gameInfo.gameConfig instanceof GameSngConfigEntity) {
                                tv_details_all_buy_title.setText("");
                                tv_details_all_gain_title.setText(R.string.game_match_table_chips);
                            } else if (gameInfo.gameConfig instanceof BaseMttConfig) {
                                BaseMttConfig config = ((BaseMttConfig) gameInfo.gameConfig);
                                if (gameInfo.match_type == GameConstants.MATCH_TYPE_NORMAL) {
                                    tv_details_all_buy_title.setText(config.ko_mode == 0 ? "" : config.ko_mode == 1 ? "猎头/赏金" : "身价/赏金");
                                } else {
                                    tv_details_all_buy_title.setText(config.ko_mode == 0 ? "" : config.ko_mode == 1 ? "猎头" : "赏金");
                                }
                                tv_details_all_gain_title.setText(R.string.game_match_table_chips);
                            }
                        }
                    } else {
                        Toast.makeText(UserByChannelAC.this, "获取数据失败code:" + code, Toast.LENGTH_SHORT).show();
                    }
                } catch(Exception e) {
                    Toast.makeText(UserByChannelAC.this, "获取数据异常:" + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                refresh_layout.setRefreshing(false);
                isLoading = false;
                Toast.makeText(UserByChannelAC.this, "获取数据失败:" + (error == null ? "error=null" : error.toString()), Toast.LENGTH_SHORT).show();
                normalDatasetChanged();
            }
        });
    }

    private void normalDatasetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        refreshRecyclerViewResult();
    }

    private void registerObservers(boolean register) {
        if (register) {
            MatchManager.getInstance().registerCallback(matchStatusCallback);
        } else {
            MatchManager.getInstance().unregisterCallback(matchStatusCallback);
        }
    }

    private MatchManager.MatchStatusCallback matchStatusCallback = new MatchManager.MatchStatusCallback() {
        @Override
        public void onUpdated(MatchMgrItem item) {
            if (item == null || item.matchStatusEntity == null) {
                return;
            }
            if (adapter != null) {
                adapter.updateStatus(item.matchStatusEntity.gameStatus, item.matchStatusEntity.myCheckInStatus);
            }
            if (searchAdapter != null) {
                searchAdapter.updateStatus(item.matchStatusEntity.gameStatus, item.matchStatusEntity.myCheckInStatus);
            }
        }
    };
}
