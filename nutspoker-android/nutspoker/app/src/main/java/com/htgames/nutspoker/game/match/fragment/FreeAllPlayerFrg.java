package com.htgames.nutspoker.game.match.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.game.match.activity.FreeRoomAC;
import com.htgames.nutspoker.game.match.adapter.MatchPlayerAdapter;
import com.htgames.nutspoker.game.match.bean.MatchPlayerEntity;
import com.htgames.nutspoker.game.match.item.MatchPlayerItemType;
import com.htgames.nutspoker.game.match.item.PlayerSection;
import com.htgames.nutspoker.ui.base.BaseFragment;
import com.htgames.nutspoker.ui.recycler.MeRecyclerView;
import com.htgames.nutspoker.view.ResultDataView;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 周智慧 on 17/3/2.
 */

public class FreeAllPlayerFrg extends BaseFragment implements View.OnClickListener {
    public static String TAG = FreeAllPlayerFrg.class.getSimpleName();
    String roomId;
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
//            adapter.notifyDataSetChanged();
        }
    }
    @Override
    protected void onVisible() {
        super.onVisible();
        LogUtil.i(TAG, "FreeAllPlayerFrg onVisible");
    }

    public static FreeAllPlayerFrg newInstance() {
        FreeAllPlayerFrg mInstance = new FreeAllPlayerFrg();
        Bundle bundle = new Bundle();
        mInstance.setArguments(bundle);
        return mInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFragmentName(TAG);
        adapter = new MatchPlayerAdapter(getActivity(), null, null);
        adapter.addSection(MatchPlayerItemType.NormalMgrPlayer, new PlayerSection(MatchPlayerItemType.NormalMgrPlayer, playerList));
        if (gameInfo != null) {
            adapter.gameInfo = gameInfo;
            adapter.gameStatus = gameInfo.status;
        }
        setFragmentName(TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_free_player_list, container, false);
        initView();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isFragmentCreated = true;
        if (getActivity() instanceof FreeRoomAC) {
            roomId = ((FreeRoomAC) getActivity()).roomId;
            getListData(true);
        }
    }


    private void initView() {
        refresh_layout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refresh_layout.setColorSchemeResources(R.color.login_solid_color, R.color.login_solid_color, R.color.login_solid_color, R.color.login_solid_color);
        refresh_layout.setEnabled(false);
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
//                        getListData(false);
//                        if (adapter != null) {
//                            adapter.notifyDataSetChanged();
//                        }
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

    /**
     * 获取聊天室成员信息
     *
     * @param roomId          聊天室id
     * @param memberQueryType 成员查询类型。见{@link MemberQueryType}
     * @param time            查询固定成员列表用ChatRoomMember.getUpdateTime,
     *                        查询游客列表用ChatRoomMember.getEnterTime，
     *                        填0会使用当前服务器最新时间开始查询，即第一页，单位毫秒
     * @param limit           条数限制
     * @return InvocationFuture 可以设置回调函数。回调中返回成员信息列表
     */
    private boolean isLoading = false;//是否在请求数据
    private boolean isFinished = false;//数据是否已经请求完
    private int pagesize = -1;//分页查询的总页数
    private int curPage = 0;
    private void getListData(final boolean clearOld) {
        isLoading = true;
        NIMClient.getService(ChatRoomService.class).fetchRoomMembers(roomId, MemberQueryType.NORMAL, new ChatRoomMember().getUpdateTime(), 100)
                .setCallback(new RequestCallbackWrapper<List<ChatRoomMember>>() {
                    @Override
                    public void onResult(int code, List<ChatRoomMember> result, Throwable exception) {
                        boolean success = code == ResponseCode.RES_SUCCESS && result != null;
                        if (!success) {
                            return;
                        }
                        if (clearOld) {
                            playerList.clear();
                        }
                        List<String> nimRefreshList = new ArrayList<>();
                        for (int i = 0; i < result.size(); i++) {
                            ChatRoomMember member = result.get(i);
                            MatchPlayerEntity playerEntity = new MatchPlayerEntity();
                            playerEntity.uid = member.getAccount();
                            playerEntity.nickname = member.getNick();
                            playerEntity.avatar = member.getAvatar();
                            playerList.add(playerEntity);
                            nimRefreshList.add(member.getAccount());
                        }
                        NIMClient.getService(UserService.class).fetchUserInfo(nimRefreshList).setCallback(new RequestCallbackWrapper<List<NimUserInfo>>() {
                            @Override
                            public void onResult(int code, List<NimUserInfo> users, Throwable exception) {
                                NimUserInfo user = null;
                                if (code == ResponseCode.RES_SUCCESS && users != null && !users.isEmpty()) {
                                    for (int i = 0; i < playerList.size(); i++) {
                                        MatchPlayerEntity playerEntity = playerList.get(i);
                                        playerEntity.nickname = NimUserInfoCache.getInstance().getUserDisplayName(playerEntity.uid);
                                    }
                                }
                                notifyDataSetChanged();
                                isLoading = false;
                                refresh_layout.setRefreshing(false);
                            }
                        });
                    }
                });
    }

    private void notifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        if (getActivity() instanceof FreeRoomAC) {
            ((FreeRoomAC) getActivity()).updatePlayerCount(0, playerList.size());
        }
    }

    public void onChatMemberIn(ChatRoomMessage message) {
        LogUtil.i("zzh", "freeallplayerfrg onChatMemberIn uid: " + (message == null ? "null" : message.getFromAccount()) + " nickname: " + (message == null ? "null" : message.getFromNick()));
        final MatchPlayerEntity playerEntity = new MatchPlayerEntity();
        playerEntity.uid = message.getFromAccount();
        playerEntity.nickname = message.getFromNick();
        if (StringUtil.isSpace(playerEntity.nickname)) {
            playerEntity.nickname = NimUserInfoCache.getInstance().getUserDisplayName(playerEntity.uid);
        }
        for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).uid.equals(playerEntity.uid)) {
                return;
            }
        }
        playerList.add(playerEntity);
        List<String> nimRefreshList = new ArrayList<>();
        nimRefreshList.add(message.getFromAccount());
        NIMClient.getService(UserService.class).fetchUserInfo(nimRefreshList).setCallback(new RequestCallbackWrapper<List<NimUserInfo>>() {
            @Override
            public void onResult(int code, List<NimUserInfo> users, Throwable exception) {
//                NimUserInfo user = null;
//                if (code == ResponseCode.RES_SUCCESS && users != null && !users.isEmpty()) {
//                }
                for (int i = 0; i < playerList.size(); i++) {
                    MatchPlayerEntity item = playerList.get(i);
                    item.nickname = NimUserInfoCache.getInstance().getUserDisplayName(item.uid);
                }
                notifyDataSetChanged();
            }
        });
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
        }
    }
}
