package com.htgames.nutspoker.game.match.fragment;

import android.os.Bundle;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.game.model.GameStatus;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.bean.PineappleConfigMtt;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.game.match.activity.MatchRoomActivity;
import com.netease.nim.uikit.common.DateTools;
import com.netease.nim.uikit.common.util.log.LogUtil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.game.match.adapter.MatchTableAdapter;
import com.htgames.nutspoker.game.match.bean.MatchTableEntity;
import com.htgames.nutspoker.ui.base.BaseFragment;
import com.netease.nim.uikit.session.constant.Extras;

import java.util.ArrayList;

/**
 * 比赛牌桌页面
 */
public class MatchTableFragment extends BaseFragment implements View.OnClickListener {
    private final static String TAG = "MatchTableFragment";
    View view;
    View desk_count_container;
    View desk_info_container;
    LinearLayout ll_table_list;
    ListView listview;
    TextView tv_mtt_count;
    TextView tv_mtt_table_count;
    TextView tv_match_table_status;
    MatchTableAdapter mMttGameTableAdapter;
    ArrayList<MatchTableEntity> tableList;
    int gameStatus = GameStatus.GAME_STATUS_WAIT;
    int matchTablePlayer = 0;
    public long lastGetTableTime = 0;
    private GameEntity mGameInfo;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        final long currentTime = DemoCache.getCurrentServerSecondTime();
        if (currentTime - lastGetTableTime < 10) {
            return;
        }
        if (getActivity() instanceof MatchRoomActivity && ((MatchRoomActivity) getActivity()).mMatchRequestHelper != null) {
            ((MatchRoomActivity) getActivity()).getTableList();
            lastGetTableTime = currentTime;
        }
    }

    public static MatchTableFragment newInstance(int matchPlayer) {
        MatchTableFragment mInstance = new MatchTableFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Extras.EXTRA_GAME_MATCH_PLAYER, matchPlayer);
        mInstance.setArguments(bundle);
        return mInstance;
    }

    public void setGameInfo(GameEntity gameEntity) {
        if (gameEntity != null) {
            mGameInfo = gameEntity;
        }
        setViewInfo();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFragmentName("MatchTableFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_match_table, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isFragmentCreated = true;
        setViewInfo();
    }

    private void setViewInfo() {
        if (mGameInfo == null || view == null || !isFragmentCreated) {
            return;
        }
        matchTablePlayer = mGameInfo.gameConfig instanceof GameMttConfig ? ((GameMttConfig) mGameInfo.gameConfig).matchPlayer : 0;
        gameStatus = ((MatchRoomActivity) getActivity()).getGameInfo().status;
        initView();
        tableList = new ArrayList<>();
        mMttGameTableAdapter = new MatchTableAdapter(getContext(), tableList);
        listview.setAdapter(mMttGameTableAdapter);
        tv_mtt_table_count.setText(getString(R.string.game_share_code_match_player, matchTablePlayer));
        tv_mtt_table_count.setVisibility(mGameInfo.play_mode == GameConstants.PLAY_MODE_PINEAPPLE ? View.GONE : View.VISIBLE);
        if (gameStatus == GameStatus.GAME_STATUS_WAIT) {
//            ll_table_list.setVisibility(View.GONE);
            listview.setVisibility(View.GONE);
            tv_match_table_status.setText(R.string.match_game_not_start);
            tv_match_table_status.setVisibility(View.VISIBLE);
        } else {
            tv_match_table_status.setVisibility(View.GONE);
        }
        tv_mtt_count.setText(getString(R.string.game_mtt_game_table_count, 0));
        if (mGameInfo.gameConfig instanceof PineappleConfigMtt) {
            changePineappleBg();
        }
    }

    public void updateTableList(ArrayList<MatchTableEntity> list) {
        if (view == null) {
            return;
        }
        tv_match_table_status.setVisibility(View.GONE);
        LogUtil.i(TAG, "updateTableList");
//        ll_table_list.setVisibility(View.VISIBLE);
        listview.setVisibility(View.VISIBLE);
        tableList.clear();
        tableList.addAll(list);
        mMttGameTableAdapter.notifyDataSetChanged();
        updateTable();
    }

    private void initView() {
        desk_count_container = view.findViewById(R.id.desk_count_container);
        desk_info_container = view.findViewById(R.id.desk_info_container);
        ll_table_list = (LinearLayout) view.findViewById(R.id.ll_table_list);
        listview = (ListView) view.findViewById(R.id.listview);
        tv_match_table_status = (TextView) view.findViewById(R.id.tv_match_table_status);
        tv_mtt_count = (TextView) view.findViewById(R.id.tv_mtt_count);
        tv_mtt_table_count = (TextView) view.findViewById(R.id.tv_mtt_table_count);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getAdapter() == null || position >= parent.getAdapter().getCount()) {
                    return;
                }
                MatchTableEntity tableEntity = (MatchTableEntity) parent.getItemAtPosition(position);
                ((MatchRoomActivity) getActivity()).openGame("", tableEntity.index + "", true);
            }
        });
    }

    public void updateTable() {
        tv_mtt_count.setText(ChessApp.sAppContext.getString(R.string.game_mtt_game_table_count, tableList.size()));
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
        }
    }

    public void changePineappleBg() {
        if (desk_count_container != null && desk_info_container != null) {
            desk_count_container.setBackgroundColor(ChessApp.sAppContext.getResources().getColor(R.color.bg_pineapple_mtt));
            desk_info_container.setBackgroundColor(ChessApp.sAppContext.getResources().getColor(R.color.bg_pineapple_mtt_deep));
        }
    }
}

