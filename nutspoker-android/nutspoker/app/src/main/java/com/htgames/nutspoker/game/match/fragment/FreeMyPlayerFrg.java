package com.htgames.nutspoker.game.match.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.game.match.activity.FreeRoomAC;
import com.htgames.nutspoker.game.match.adapter.MatchPlayerAdapter;
import com.htgames.nutspoker.game.match.bean.MatchPlayerEntity;
import com.htgames.nutspoker.game.match.item.MatchPlayerItemType;
import com.htgames.nutspoker.game.match.item.PlayerSection;
import com.htgames.nutspoker.interfaces.VolleyCallback;
import com.htgames.nutspoker.tool.JsonResolveUtil;
import com.htgames.nutspoker.ui.base.BaseFragment;
import com.htgames.nutspoker.ui.recycler.MeRecyclerView;
import com.htgames.nutspoker.view.ResultDataView;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.constants.GameConstants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 周智慧 on 17/3/2.
 */

public class FreeMyPlayerFrg extends BaseFragment implements View.OnClickListener {
    public static String TAG = FreeMyPlayerFrg.class.getSimpleName();
    ArrayList<MatchPlayerEntity> playerList = new ArrayList<MatchPlayerEntity>();
    View view;
    ResultDataView mResultDataView;
    SwipeRefreshLayout refresh_layout;
    MeRecyclerView mRecyclerView;
    MatchPlayerAdapter adapter;
    GameEntity gameInfo;
    public void updateGameInfo(GameEntity gameInfo) {
        if (gameInfo == null) {
            return;
        }
        this.gameInfo = gameInfo;
        if (adapter != null) {
            adapter.gameInfo = gameInfo;
            adapter.gameStatus = gameInfo.status;
            if (!adapterHasInited) {
                if (gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL) {
                    adapter.addSection(MatchPlayerItemType.NormalMgrPlayer, new PlayerSection(MatchPlayerItemType.NormalMgrPlayer, playerList));
                } else if (gameInfo.gameMode == GameConstants.GAME_MODE_SNG) {
                    adapter.addSection(MatchPlayerItemType.SngMgrPlayer, new PlayerSection(MatchPlayerItemType.SngMgrPlayer, playerList));
                }
                adapterHasInited = true;
            }
            adapter.notifyDataSetChanged();
        }
        getListData(true);
    }
    @Override
    protected void onVisible() {
        super.onVisible();
        LogUtil.i(TAG, "FreeMyPlayerFrg onVisible");
    }

    public static FreeMyPlayerFrg newInstance() {
        FreeMyPlayerFrg mInstance = new FreeMyPlayerFrg();
        Bundle bundle = new Bundle();
        mInstance.setArguments(bundle);
        return mInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFragmentName(TAG);
        adapter = new MatchPlayerAdapter(getActivity(), null, null);
        adapter.isAllPlayer = false;
        if (gameInfo != null) {
            adapter.gameInfo = gameInfo;
            adapter.gameStatus = gameInfo.status;
            if (!adapterHasInited) {
                if (gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL) {
                    adapter.addSection(MatchPlayerItemType.NormalMgrPlayer, new PlayerSection(MatchPlayerItemType.NormalMgrPlayer, playerList));
                } else if (gameInfo.gameMode == GameConstants.GAME_MODE_SNG) {
                    adapter.addSection(MatchPlayerItemType.SngMgrPlayer, new PlayerSection(MatchPlayerItemType.SngMgrPlayer, playerList));
                }
                adapterHasInited = true;
            }
        }
        setFragmentName(TAG);
    }
    private boolean adapterHasInited = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_free_player_list, container, false);
        initView();
        getListData(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isFragmentCreated = true;
    }

    private void initView() {
        refresh_layout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refresh_layout.setColorSchemeResources(R.color.login_solid_color, R.color.login_solid_color, R.color.login_solid_color, R.color.login_solid_color);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
        mResultDataView = (ResultDataView) view.findViewById(R.id.mResultDataView);
        mRecyclerView = (MeRecyclerView) view.findViewById(R.id.recycler);
        mRecyclerView.setAdapter(adapter);
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
    }

    //重新刷新界面，从第一页开始请求
    private void refreshList() {
        pagesize = -1;//分页查询的总页数
        curPage = 0;
        getListData(true);
    }

    private boolean isLoading = false;//是否在请求数据
    private boolean isFinished = false;//数据是否已经请求完
    private int pagesize = -1;//分页查询的总页数
    private int curPage = 0;
    private void getListData(final boolean clearOld) {
        if (pagesize > -1 && curPage >= pagesize) {
            refresh_layout.setRefreshing(false);
            return;//表示分页查询已经查询结束，即数据请求完了
        }
        isLoading = true;
        if (gameInfo == null || getActivity() == null) {
            refresh_layout.setRefreshing(false);
            return;
        }
        ((FreeRoomAC) getActivity()).mGameAction.getChannelPlayerList(gameInfo.channel, curPage, new VolleyCallback() {
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
                        ArrayList<MatchPlayerEntity> newData = JsonResolveUtil.getUserByMgr(data, new NimUserInfoCache.IFetchCallback() {
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
                                notifyDataSetChanged();
                            }
                        });
                    } else {//code==5010表示channel不存在
                        LogUtil.i(TAG, "获取数据失败code:" + code);
                    }
                } catch(Exception e) {
                    LogUtil.i(TAG, "获取数据失败code:" + e == null ? "e==null" : e.toString());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                refresh_layout.setRefreshing(false);
                isLoading = false;
            }
        });
    }

    private void notifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        if (getActivity() instanceof FreeRoomAC) {
            ((FreeRoomAC) getActivity()).updatePlayerCount(1, playerList.size());
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
        if (playerList.size() <= 0 || curPage <= 1) {//从游戏里面出来请求接口刷新数据与页面
            refreshList();
        }
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
        }
    }
}
