package com.htgames.nutspoker.game.match.fragment;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.BaseMttConfig;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.bean.PineappleConfigMtt;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.game.match.activity.MatchRoomActivity;
import com.htgames.nutspoker.game.match.adapter.MatchPlayerAdapter;
import com.htgames.nutspoker.game.match.bean.MatchPlayerEntity;
import com.htgames.nutspoker.game.match.bean.MatchStatusEntity;
import com.htgames.nutspoker.game.match.item.MatchPlayerItemType;
import com.htgames.nutspoker.game.match.item.PlayerSection;
import com.htgames.nutspoker.game.model.GameStatus;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.ui.base.BaseFragment;
import com.htgames.nutspoker.view.ResultDataView;
import com.htgames.nutspoker.view.switchbutton.SwitchButton;
import com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.AnimUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.interfaces.IClick;
import com.netease.nim.uikit.session.constant.Extras;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 比赛玩家列表
 */
public class MatchPlayerFragment extends BaseFragment implements View.OnClickListener {
    private final static String TAG = MatchRoomActivity.TAG;
    View view;
    View check_in_count_container;
    View search_and_sort_container;
    RecyclerView mRecyclerView;
    LinearLayoutManager layoutManager;
    ResultDataView mResultDataView;
    TextView tv_mtt_count;
    TextView tv_mtt_total_buy_count_bottom;
    TextView tv_mtt_total_buy_count_right;
    int checkInCount = 0;
    public int rank_type = 1;//1名次  2猎头  3赏金 4身价
    //
    public ArrayList<MatchPlayerEntity> playerList = new ArrayList<MatchPlayerEntity>();
    ArrayList<MatchPlayerEntity> searchList;
    MatchPlayerAdapter adapter;
    MatchPlayerAdapter searchAdapter;
    LinearLayout ll_player_other_info;
    SwitchButton ck_game_control_into;
    TextView ck_game_control_into_des;
    //搜索和排序相关
    RelativeLayout rl_player_search;
    RelativeLayout rl_player_search_btn;
    TextView tv_player_search_hint;
    TextView tv_search_cancel;
    TextView tv_match_sort_type;
    ImageView iv_match_sort_type;
    TextView match_sort_hunter_mark;
    View match_sort_type_container;
    ClearableEditTextWithIcon edt_player_search;
    //
    String creatorId = "";
    private String roomId;
    private String gid;
    private int matchChips = 0;
    int gameStatus = GameStatus.GAME_STATUS_WAIT;
    MatchStatusEntity matchStatusEntity;
    Map<Integer, Long> remainTimePositionMap;
    GameEntity gameInfo;
    Vibrator vibrator;//震动
    boolean isMessageShake = true;

    @Override
    public void onHiddenChanged(boolean hidden) {
//        if (hidden) {
//            onPause();
//        } else {
//            onResume();
//            ReminderManager.getInstance().updateAppMsgUnreadNum(0);
//            AppMsgDBHelper.resetAppMessageUnreadCountByGid(getContext(), gid);
//        }
        LogUtil.i("MatchPlayerFragment onHiddenChanged hidden： " + hidden);
        super.onHiddenChanged(hidden);
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        LogUtil.i("MatchPlayerFragment onVisible");
        if (getActivity() instanceof MatchRoomActivity && gameStatus == GameStatus.GAME_STATUS_START) {
            ((MatchRoomActivity) getActivity()).getPlayerList();//开赛前只请求一次，开赛后10秒一次请求  onvisible
        }
    }

    public static MatchPlayerFragment newInstance(GameEntity gameEntity, String roomId, String gid, int matchChips) {
        MatchPlayerFragment mInstance = new MatchPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Extras.EXTRA_ROOM_ID, roomId);
        bundle.putString(Extras.EXTRA_GAME_ID, gid);
        bundle.putInt(Extras.EXTRA_GAME_MATCH_CHIPS, matchChips);
        bundle.putSerializable(Extras.EXTRA_DATA, gameEntity);
        mInstance.setArguments(bundle);
        return mInstance;
    }

    public void setGameInfo(GameEntity gameEntity) {
        if (gameEntity != null) {
            gameInfo = gameEntity;
            gameStatus = gameInfo.status;
        }
        setViewInfo();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFragmentName("MatchPlayerFragment");
        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        remainTimePositionMap = new HashMap<>();
        if (getArguments().getSerializable(Extras.EXTRA_DATA) instanceof GameEntity) {
            gameInfo = (GameEntity) getArguments().getSerializable(Extras.EXTRA_DATA);
            gameStatus = gameInfo.status;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_match_member, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isFragmentCreated = true;
        setViewInfo();
    }

    private void setViewInfo() {
        if (gameInfo == null || view == null || !isFragmentCreated) {
            return;
        }
        roomId = getArguments().getString(Extras.EXTRA_ROOM_ID);
        gid = gameInfo.gid;
        matchChips = ((BaseMttConfig) gameInfo.gameConfig).matchChips;
        if (getActivity() instanceof MatchRoomActivity) {
            creatorId = ((MatchRoomActivity) getActivity()).creatorId;
        }
        initView();
        initAdapter();
        onCurrent();
        if (getActivity() instanceof MatchRoomActivity) {
            ((MatchRoomActivity) getActivity()).lastGetPlayerTime = 0;
            ((MatchRoomActivity) getActivity()).getPlayerList();//开赛前只请求一次，开赛后10秒一次请求 onActivityCreated
        }
        if (gameInfo.gameConfig instanceof PineappleConfigMtt) {
            changePineappleBg();
        }
    }

    public boolean isSearchMode() {
        return rl_player_search != null && rl_player_search.getVisibility() == View.VISIBLE;
    }

    public void updateList(ArrayList<MatchPlayerEntity> list, boolean isClear , int isEnd) {
        if (isClear) {
            playerList.clear();
        }
        playerList.addAll(list);
        if (isEnd == 1) {
            //到达结尾，刷新
            LogUtil.i(TAG, "到达结尾，刷新");
            notifyDataSetChanged();
        }
        if (mResultDataView != null) {
            if (playerList == null || playerList.size() <= 0) {
                mResultDataView.nullDataShowNotImage(rank_type == 1 ? "暂无数据" : (rank_type == 2 ? "暂无猎头数据" : (rank_type == 3 ? "暂无赏金数据" : (rank_type == 4 ? "暂无身价数据" : "暂无数据"))), R.color.shop_text_no_select_color);
            } else {
                mResultDataView.successShow();
            }
        }
    }

    public void updatePlayer(MatchPlayerEntity matchPlayerEntity, int type) {//增量更新玩家列表   type 1加  2减    只在比赛前增量改变
        if (mResultDataView != null) {
            mResultDataView.successShow();
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
            } else if (gameStatus == GameStatus.GAME_STATUS_WAIT) {
                playerList.add(0, matchPlayerEntity);
            }
        } else if (type == 2) {
            for (int i = 0; i < playerList.size(); i++) {
                MatchPlayerEntity oldItem = playerList.get(i);
                if (matchPlayerEntity.uid.equals(oldItem.uid)) {
                    playerList.remove(i);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateSearchList(ArrayList<MatchPlayerEntity> list) {
        LogUtil.i(TAG, "updateSearchList");
        searchList.addAll(list);
        notifyDataSetChanged();
    }

    public void updateCheckinPlayer(int checkInCount) {
        if (view == null) {
            return;
        }
        if (gameStatus == GameStatus.GAME_STATUS_WAIT) {
            tv_mtt_count.setText(getString(R.string.game_mtt_checkin_count, checkInCount));
        } else if (gameStatus == GameStatus.GAME_STATUS_START && matchStatusEntity != null) {
            tv_mtt_count.setText(getString(R.string.game_mtt_player_join_count, matchStatusEntity.leftPlayer, checkInCount));
        }
        if (matchStatusEntity == null || gameInfo == null) {
            return;
        }
        int checkinFee = ((GameMttConfig) gameInfo.gameConfig).matchCheckinFee;
        int allReward = matchStatusEntity.allReward;//总奖池
        int total_buy = (int) (allReward * 1.0f / checkinFee);
        tv_mtt_total_buy_count_bottom.setText("参赛人次：" + total_buy);
        tv_mtt_total_buy_count_right.setText("参赛人次：" + total_buy);
    }

    public void notifyDataSetChanged() {
        if (isSearchMode()) {
            searchAdapter.notifyDataSetChanged();
        } else {
            adapter.checkInCount = checkInCount;
            adapter.notifyDataSetChanged();
        }
    }

    private void initAdapter() {
        searchList = new ArrayList<MatchPlayerEntity>();
        adapter = new MatchPlayerAdapter(getContext(), null, playerClick);
        adapter.gameInfo = gameInfo;
        adapter.clickListener = new IClick.IOnlyClick() {
            @Override
            public void onClick(int position) {
                adapter.hasClick = true;
//                ((MatchRoomActivity) getActivity()).getPlayListMore();
            }
        };
        ArrayList moreList = new ArrayList();moreList.add("more");
        adapter.addSection(MatchPlayerItemType.Player, new PlayerSection(MatchPlayerItemType.Player, playerList));
        adapter.addSection(MatchPlayerItemType.More, new PlayerSection(MatchPlayerItemType.More, moreList));//点击"更多"按钮
        searchAdapter = new MatchPlayerAdapter(getContext(), null, playerClick);
        searchAdapter.addSection(MatchPlayerItemType.Player, new PlayerSection(MatchPlayerItemType.Player, searchList));
        searchAdapter.gameInfo = gameInfo;
        layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(adapter);
    }

    MatchPlayerAdapter.OnPlayerClick playerClick = new MatchPlayerAdapter.OnPlayerClick() {
        @Override
        public void onClick(MatchPlayerEntity playerEntity) {
            LogUtil.i(TAG, "playerEntity : " + playerEntity.uid);
            if (gameStatus == GameStatus.GAME_STATUS_START && playerEntity.chips > 0/* && playerEntity.tableNo != 0*/) {
                //比赛中，并且还未被淘汰
                if (playerEntity.uid.equals(DemoCache.getAccount())) {
                    ((MatchRoomActivity) getActivity()).openGame("", "", false);
                } else {
                    ((MatchRoomActivity) getActivity()).openGame(playerEntity.uid, "", true);
                }
            }
        }
    };

    private void initView() {
        int ko_mode = gameInfo != null && gameInfo.gameConfig instanceof GameMttConfig ? ((GameMttConfig) gameInfo.gameConfig).ko_mode : 0;
        check_in_count_container = view.findViewById(R.id.check_in_count_container);
        search_and_sort_container = view.findViewById(R.id.search_and_sort_container);
        tv_mtt_total_buy_count_right = (TextView) view.findViewById(R.id.tv_mtt_total_buy_count_right);
        tv_mtt_total_buy_count_bottom = (TextView) view.findViewById(R.id.tv_mtt_total_buy_count);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);
        mResultDataView = (ResultDataView) view.findViewById(R.id.mResultDataView);
        tv_mtt_count = (TextView) view.findViewById(R.id.tv_mtt_count);
        ll_player_other_info = (LinearLayout) view.findViewById(R.id.ll_player_other_info);
        ck_game_control_into = (SwitchButton) view.findViewById(R.id.ck_game_control_into);
        ck_game_control_into_des = (TextView) view.findViewById(R.id.ck_game_control_into_des);
        rl_player_search_btn = (RelativeLayout) view.findViewById(R.id.rl_player_search_btn);
        rl_player_search = (RelativeLayout) view.findViewById(R.id.rl_player_search);
        tv_search_cancel = (TextView) view.findViewById(R.id.tv_search_cancel);
        edt_player_search = (ClearableEditTextWithIcon) view.findViewById(R.id.edt_player_search);
        tv_player_search_hint = (TextView) view.findViewById(R.id.tv_player_search_hint);
        tv_player_search_hint.setOnClickListener(this);
        tv_match_sort_type = (TextView) view.findViewById(R.id.tv_match_sort_type);
        tv_match_sort_type.setText((gameInfo != null && gameInfo.status == GameStatus.GAME_STATUS_START || gameStatus == GameStatus.GAME_STATUS_START) ? "玩家排名" : "玩家");
        iv_match_sort_type = (ImageView) view.findViewById(R.id.iv_match_sort_type);
        iv_match_sort_type.setVisibility((gameInfo != null && gameInfo.status == GameStatus.GAME_STATUS_START || gameStatus == GameStatus.GAME_STATUS_START) ? View.VISIBLE : View.GONE);
        match_sort_hunter_mark = (TextView) view.findViewById(R.id.match_sort_hunter_mark);
        if (gameInfo.match_type == GameConstants.MATCH_TYPE_NORMAL) {
            match_sort_hunter_mark.setText(gameStatus == GameStatus.GAME_STATUS_START ? (ko_mode == 1 ? "猎头/赏金" : (ko_mode == 2 ? "身价/赏金" : "")) : "");
        } else {
            match_sort_hunter_mark.setText(gameStatus == GameStatus.GAME_STATUS_START ? (ko_mode == 1 ? "猎头" : (ko_mode == 2 ? "赏金" : "")) : "");
        }
        match_sort_type_container = view.findViewById(R.id.match_sort_type_container);
        match_sort_type_container.setOnClickListener(this);
        edt_player_search.setIconResource(R.mipmap.icon_edt_match_search_player);
        edt_player_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                }
            }
        });
        edt_player_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String key = edt_player_search.getText().toString();
                    key = key.trim();
                    searchList.clear();
                    notifyDataSetChanged();
                    if (TextUtils.isEmpty(key)) {
                        Toast.makeText(getContext(), R.string.not_allow_empty, Toast.LENGTH_SHORT).show();
                    } else {
                        searchKey(key);
                    }
                    return true;
                }
                return false;
            }
        });
        ck_game_control_into.setOnClickListener(this);
        tv_search_cancel.setOnClickListener(this);
        mResultDataView.successShow();
        if (creatorId.equals(DemoCache.getAccount())) {
            ck_game_control_into.setVisibility(View.VISIBLE);
            ck_game_control_into_des.setVisibility(View.VISIBLE);
            tv_mtt_total_buy_count_bottom.setVisibility(View.VISIBLE);
            tv_mtt_total_buy_count_right.setVisibility(View.GONE);
        } else {
            ck_game_control_into.setVisibility(View.GONE);
            ck_game_control_into_des.setVisibility(View.GONE);
            tv_mtt_total_buy_count_bottom.setVisibility(View.GONE);
            tv_mtt_total_buy_count_right.setVisibility(View.VISIBLE);
        }
        tv_mtt_count.setText(getString(R.string.game_mtt_checkin_count, checkInCount));
        if (matchStatusEntity == null || gameInfo == null) {
            return;
        }
        setInfoFromMatchStatus();
    }

    public void searchKey(String key) {
        ((MatchRoomActivity) getActivity()).searchPlayer(key);
        showKeyboard(false);
    }

    public void updatePlyaerCount(int gameStatusPa, MatchStatusEntity matchStatusEntityPa) {
        int ko_mode = gameInfo != null && gameInfo.gameConfig instanceof GameMttConfig ? ((GameMttConfig) gameInfo.gameConfig).ko_mode : 0;
        matchStatusEntity = matchStatusEntityPa;
        checkInCount = matchStatusEntity == null ? 0 : matchStatusEntity.checkInPlayer;
        if (this.gameStatus != gameStatusPa && gameStatusPa == GameStatus.GAME_STATUS_START && tv_match_sort_type != null) {
            if (tv_match_sort_type != null && match_sort_hunter_mark != null) {
                tv_match_sort_type.setText("玩家排名");
                iv_match_sort_type.setVisibility(View.VISIBLE);
                if (gameInfo.match_type == GameConstants.MATCH_TYPE_NORMAL) {
                    match_sort_hunter_mark.setText(gameStatusPa == GameStatus.GAME_STATUS_START ? (ko_mode == 1 ? "猎头/赏金" : (ko_mode == 2 ? "身价/赏金" : "")) : "");
                } else {
                    match_sort_hunter_mark.setText(gameStatusPa == GameStatus.GAME_STATUS_START ? (ko_mode == 1 ? "猎头" : (ko_mode == 2 ? "赏金" : "")) : "");
                }
            }
        }
        this.gameStatus = gameStatusPa;
        if  (adapter != null && matchStatusEntity != null) {
            adapter.updateStatus(gameStatus, matchStatusEntity.myCheckInStatus);
        }
        if (searchAdapter != null && matchStatusEntity != null) {
            searchAdapter.updateStatus(gameStatus, matchStatusEntity.myCheckInStatus);
        }
        if (view == null) {
            return;
        }
        if (gameStatus == GameStatus.GAME_STATUS_WAIT) {
            if (tv_mtt_count != null) {
                tv_mtt_count.setText(ChessApp.sAppContext.getResources().getString(R.string.game_mtt_checkin_count, checkInCount));
            }
        } else if (gameStatus == GameStatus.GAME_STATUS_START) {
            if (tv_mtt_count != null) {
                tv_mtt_count.setText(ChessApp.sAppContext.getResources().getString(R.string.game_mtt_player_join_count, matchStatusEntity == null ? 0 : matchStatusEntity.leftPlayer, checkInCount));
            }
        }

        if (matchStatusEntity == null || gameInfo == null) {
            return;
        }
        setInfoFromMatchStatus();
    }

    private void setInfoFromMatchStatus() {
        ck_game_control_into.setChecked(matchStatusEntity.isControl == 1);
        int checkinFee = 1;
        if (gameInfo.gameConfig instanceof BaseMttConfig) {
            checkinFee = ((BaseMttConfig) gameInfo.gameConfig).matchCheckinFee;
        }
        int allReward = matchStatusEntity.allReward;//总奖池
        int total_buy = (int) (allReward * 1.0f / checkinFee);
        tv_mtt_total_buy_count_bottom.setText("参赛人次：" + total_buy);
        tv_mtt_total_buy_count_right.setText("参赛人次：" + total_buy);
    }

    public void showSearchView(boolean show) {
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

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.i(TAG, "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(TAG, "onResume");
        onCurrent();
    }

    @Override
    public void onDestroy() {
        LogUtil.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        LogUtil.i(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search_cancel:
                showSearchView(false);
                break;
            case R.id.tv_player_search_hint:
                showSearchView(true);
                break;
            case R.id.ck_game_control_into:
                ((MatchRoomActivity) getActivity()).updateControl(ck_game_control_into.isChecked(), new RequestCallback() {
                    @Override
                    public void onResult(int code, String result, Throwable var3) {
                        if (getActivity() instanceof MatchRoomActivity) {
                            ((MatchRoomActivity) getActivity()).updateIsControlResult(ck_game_control_into != null && ck_game_control_into.isChecked() ? 1 : 0);
                        }
                    }
                    @Override
                    public void onFailed() {
                        ck_game_control_into.setChecked(!ck_game_control_into.isChecked());
                        if (getActivity() instanceof MatchRoomActivity) {
                            ((MatchRoomActivity) getActivity()).updateIsControlResult(ck_game_control_into != null && ck_game_control_into.isChecked() ? 1 : 0);
                        }
                    }
                });
                break;
            case R.id.match_sort_type_container://弹出排序的dialog对话框
                if (gameStatus == GameStatus.GAME_STATUS_WAIT || view == null) {//这个排序的需求不做
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
//                    return;
                } else {
                    if (getActivity() instanceof MatchRoomActivity) {
                        ((MatchRoomActivity) getActivity()).currentPlayerSequence = 0;//排序的sequence是相互独立的
                        ((MatchRoomActivity) getActivity()).lastGetPlayerTime = 0;
                        ((MatchRoomActivity) getActivity()).mMatchRequestHelper.isRequestingPlayerList = false;
                    }
                }
                previousSortView = match_sort_one_container;
                rank_type = 1;
                tv_match_sort_type.setText("按名次排序");
                tryGetPlayListByRank();
                break;
            case R.id.match_sort_two_container://按猎头数排序    普通猎人赛  ko_mode = 0
                dismissSortPlayerPopupWindow(match_sort_two_container);
                if (previousSortView == match_sort_two_container) {
//                    return;
                } else {
                    if (getActivity() instanceof MatchRoomActivity) {
                        ((MatchRoomActivity) getActivity()).currentPlayerSequence = 0;//排序的sequence是相互独立的
                        ((MatchRoomActivity) getActivity()).lastGetPlayerTime = 0;
                        ((MatchRoomActivity) getActivity()).mMatchRequestHelper.isRequestingPlayerList = false;
                    }
                }
                previousSortView = match_sort_two_container;
                rank_type = 2;
                tv_match_sort_type.setText("按猎头数排序");
                tryGetPlayListByRank();
                break;
            case R.id.match_sort_three_container://按身价排序    超级猎人赛  ko_mode = 1
                dismissSortPlayerPopupWindow(match_sort_three_container);
                if (previousSortView == match_sort_three_container) {
//                    return;
                } else {
                    if (getActivity() instanceof MatchRoomActivity) {
                        ((MatchRoomActivity) getActivity()).currentPlayerSequence = 0;//排序的sequence是相互独立的
                        ((MatchRoomActivity) getActivity()).lastGetPlayerTime = 0;
                        ((MatchRoomActivity) getActivity()).mMatchRequestHelper.isRequestingPlayerList = false;
                    }
                }
                previousSortView = match_sort_three_container;
                rank_type = 4;
                tv_match_sort_type.setText("按身价排序");
                tryGetPlayListByRank();
                break;
            case R.id.match_sort_four_container://按赏金排序     超级猎人赛  ko_mode = 1
                dismissSortPlayerPopupWindow(match_sort_four_container);
                if (previousSortView == match_sort_four_container) {
//                    return;
                } else {
                    if (getActivity() instanceof MatchRoomActivity) {
                        ((MatchRoomActivity) getActivity()).currentPlayerSequence = 0;//排序的sequence是相互独立的
                        ((MatchRoomActivity) getActivity()).lastGetPlayerTime = 0;
                        ((MatchRoomActivity) getActivity()).mMatchRequestHelper.isRequestingPlayerList = false;
                    }
                }
                previousSortView = match_sort_four_container;
                rank_type = 3;
                tv_match_sort_type.setText("按赏金排序");
                tryGetPlayListByRank();
                break;
        }
    }

    private void tryGetPlayListByRank() {
        if (adapter != null) {
            adapter.rank_type = this.rank_type;
        }
        if (getActivity() instanceof MatchRoomActivity) {
            ((MatchRoomActivity) getActivity()).getPlayerList();//开赛前只请求一次，开赛后10秒一次请求  on排序
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
        if (check_in_count_container != null && search_and_sort_container != null) {
            check_in_count_container.setBackgroundColor(ChessApp.sAppContext.getResources().getColor(R.color.bg_pineapple_mtt));
            search_and_sort_container.setBackgroundColor(ChessApp.sAppContext.getResources().getColor(R.color.bg_pineapple_mtt_deep));
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
