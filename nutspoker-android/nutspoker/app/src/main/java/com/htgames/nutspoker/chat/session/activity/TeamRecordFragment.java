package com.htgames.nutspoker.chat.session.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.db.GameRecordDBHelper;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.net.RequestTimeLimit;
import com.htgames.nutspoker.tool.json.RecordJsonTools;
import com.htgames.nutspoker.ui.action.GameAction;
import com.htgames.nutspoker.ui.action.RecordAction;
import com.htgames.nutspoker.ui.activity.Record.RecordDetailsActivity;
import com.htgames.nutspoker.ui.activity.Record.RecordListAC;
import com.htgames.nutspoker.ui.activity.Record.RecordSearchAC;
import com.htgames.nutspoker.ui.adapter.record.RecordListAdap;
import com.htgames.nutspoker.ui.adapter.record.RecordListItem;
import com.htgames.nutspoker.ui.items.FooterLoadingItem;
import com.htgames.nutspoker.view.ResultDataView;
import com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.bean.GameBillEntity;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.framework.ThreadUtil;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.interfaces.IClick;
import com.netease.nim.uikit.session.constant.Extras;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import eu.davidea.fastscroller.FastScroller;

import static com.htgames.nutspoker.ui.activity.Record.RecordListAC.recordComp;

/**
 * Created by 周智慧 on 17/3/19.
 */

public class TeamRecordFragment extends Fragment implements IClick {
    String sessionId;
    private View mView;
    FooterLoadingItem mFooterLoadingItem;
    ResultDataView mResultDataView;
    SwipeRefreshLayout refresh_layout;
    FastScroller fastScroller;
    RecyclerView recyclerview_team_record;
    RecordAction mRecordAction;
    List mDatas = new ArrayList<RecordListItem>();
    RecordListAdap mAdapter;
    boolean haveLocalLastRecord = false;//本地数据库存在最后条数据
    List<GameBillEntity> recordList = new ArrayList<>();
    public static final String TAG = TeamMessageAC.TAG;
    //搜索
    ClearableEditTextWithIcon edit_search_record;
    public static TeamRecordFragment newInstance(String teamId) {
        TeamRecordFragment mInstance = new TeamRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Extras.EXTRA_SHOW_DIVIDER, true);
        bundle.putString(Extras.EXTRA_ACCOUNT, teamId);
        mInstance.setArguments(bundle);
        return mInstance;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtil.i(TAG, TAG + "  TeamRecordFragment onAttach " + getActivity() + " " + isAdded());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionId = getActivity().getIntent().getStringExtra(Extras.EXTRA_ACCOUNT);
        mRecordAction = new RecordAction(getActivity(), null);
        LogUtil.i(TAG, TAG + "  TeamRecordFragment onCreate " + getActivity() + " " + isAdded());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_team_record, container, false);
        initSearch();
        initResultDataView();
        refresh_layout = (SwipeRefreshLayout) mView.findViewById(R.id.refresh_layout);
        refresh_layout.setColorSchemeResources(R.color.login_solid_color, R.color.login_solid_color, R.color.login_solid_color, R.color.login_solid_color);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mFooterLoadingItem.setMState(mFooterLoadingItem.Companion.getSTATE_PRE());
                mAdapter.notifyItemChanged(mDatas.size() - 1);
                getNetRecordList("");//刷新
            }
        });
//        recordList.addAll(GameRecordDBHelper.getGameRecordListByTid(getActivity(), sessionId));
        recyclerview_team_record = (RecyclerView) mView.findViewById(R.id.recyclerview_team_record);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            recyclerview_team_record.setNestedScrollingEnabled(false);
        }
        recyclerview_team_record.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (isLoadMore || refresh_layout.isRefreshing() || mFooterLoadingItem == null) {
                    return;
                }
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerview_team_record.getLayoutManager();
                int firstVisibleItemPos = linearLayoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPos = linearLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = linearLayoutManager.getItemCount();
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && totalItemCount >= 1 && lastVisibleItemPos + 5 >= totalItemCount && (lastVisibleItemPos - firstVisibleItemPos) != totalItemCount) {
                    if (recordList != null && recordList.size() != 0) {
                        String lastGid = recordList != null && recordList.size() > 0 ? recordList.get(recordList.size() - 1).gameInfo.gid : "";
                        long endTime = recordList.get(recordList.size() - 1).gameInfo.endTime;
                        if (mFooterLoadingItem.getMState() != mFooterLoadingItem.Companion.getSTATE_LOADING()) {
                            mFooterLoadingItem.setMState(mFooterLoadingItem.Companion.getSTATE_LOADING());
                            mAdapter.notifyItemChanged(mDatas.size() - 1);
                        }
                        getMoreRecordList(lastGid, endTime);
                    }
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        initAdapter();
        if (!NetworkUtil.isNetworkConnected(getActivity()) && recordList != null && recordList.size() != 0) {
            haveLocalLastRecord = true;
            setLocalRecordList();
        } else {
            getNetRecordList("");//oncreateview
        }
        return mView;
    }

    private void initSearch() {
        edit_search_record = (ClearableEditTextWithIcon) mView.findViewById(R.id.edit_search_record);
        edit_search_record.setFocusableInTouchMode(false);
        edit_search_record.setText("");
        edit_search_record.setIconResource(R.mipmap.icon_search);
        edit_search_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordSearchAC.Companion.startByMyList(getActivity(), edit_search_record, RecordSearchAC.Companion.getSEARCH_TYPE_TEAM_LIST(), sessionId, "");
            }
        });
    }

    public void setLocalRecordList() {
        mResultDataView.successShow();
        setDatas();
    }

    private void initResultDataView() {
        mResultDataView = (ResultDataView) mView.findViewById(R.id.mResultDataView);
        mResultDataView.setReloadDataCallBack(new ResultDataView.ReloadDataCallBack() {
            @Override
            public void onReloadData() {
                getNetRecordList("");//reload
            }
        });
    }

    public void getMoreRecordList(String lastGid , long endTime) {
        if (!isLoadMore) {
            isLoadMore = true;
//            if (haveLocalLastRecord && GameRecordDBHelper.haveMoreRecordByTid(getApplicationContext(), endTime)) {
            if (haveLocalLastRecord) {
                //Log.d(TAG, "本地还有缓存");
                getLocalRecordList(lastGid, endTime);
            } else {
                //Log.d(TAG, "本地没有缓存，读取网络");
                getNetRecordList(lastGid);//追加
            }
        }
    }

    public synchronized void getLocalRecordList(final String lastGid , long endTime) {
        ArrayList<GameBillEntity> list = GameRecordDBHelper.getGameRecordListByTid(getActivity(), lastGid, endTime, sessionId);
        setMoreRecordList(list);
        long currentLastEndTime = recordList.get(recordList.size() - 1).gameInfo.endTime;
//        haveLocalLastRecord = GameRecordDBHelper.haveMoreRecordByTid(getActivity(), currentLastEndTime, sessionId);
        isLoadMore = false;
    }

    public void setMoreRecordList(final ArrayList<GameBillEntity> moreList) {
        final ArrayList<GameBillEntity> newList = filterData(moreList);
        if (newList.isEmpty()) {
            mFooterLoadingItem.setMState(mFooterLoadingItem.Companion.getSTATE_FINISH());
            mAdapter.notifyItemChanged(mDatas.size() - 1);
            return;
        }
        recordList.addAll(newList);
        mFooterLoadingItem.setMState(mFooterLoadingItem.Companion.getSTATE_PRE());
        mAdapter.notifyItemChanged(mDatas.size() - 1);
//        List newItems = new ArrayList<RecordListItem>();
        int positionInsert = mDatas.size();
        for (int i = 0; i < newList.size(); i++) {
            GameBillEntity lastEntity = recordList.size() - newList.size() + i - 1 >= 0 ? recordList.get(recordList.size() - newList.size() + i - 1) : null;
            GameBillEntity entity = newList.get(i);
            RecordListItem item = new RecordListItem(entity, lastEntity);
            item.setItemType(RecordListItem.Companion.getITEM_TYPE_TEAM_HORDE());
            mDatas.add(positionInsert - 1, item);//多了个底部的loading条，所以-1
//            newItems.add(item);
            if (mAdapter != null) {
                mAdapter.addItem(positionInsert - 1, item);//多了个底部的loading条，所以-1
            }
            positionInsert++;
        }
        if (mAdapter != null) {
            mAdapter.setMMaxNum(mDatas.size() - 1);
            mAdapter.notifyItemInserted(mDatas.size());
//            mAdapter.onLoadMoreComplete(newItems);
        }
    }

    private ArrayList<GameBillEntity> filterData(final ArrayList<GameBillEntity> moreList) {
        final ArrayList<GameBillEntity> newList = new ArrayList<GameBillEntity>();
        HashSet<String> alreadyGid = new HashSet<>();
        for (int i = 0; i < recordList.size(); i++) {
            alreadyGid.add(recordList.get(i).gameInfo.gid);
        }
        for (int i = 0;i < moreList.size(); i++) {
            if (alreadyGid.contains(moreList.get(i).gameInfo.gid)) {
                continue;
            } else {
                newList.add(moreList.get(i));
            }
        }
        return newList;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initAdapter() {
        mFooterLoadingItem = new FooterLoadingItem();
        fastScroller = (FastScroller) mView.findViewById(R.id.fast_scroller);
        fastScroller.setBubbleAndHandleColor(Color.RED);
        mAdapter = new RecordListAdap(mDatas, this, true);
        mAdapter.setAnimateChangesWithDiffUtil(true)
                .setAnimateToLimit(10000)
                .setNotifyChangeOfUnfilteredItems(false)
                .setAnimationDelay(70)
                .setAnimationOnScrolling(true)
                .setAnimationOnReverseScrolling(true)
                .setOnlyEntryAnimation(true);
        mAdapter.DEBUG = CacheConstant.debugBuildType;
        recyclerview_team_record.setAdapter(mAdapter);
        recyclerview_team_record.setHasFixedSize(true);
        recyclerview_team_record.setItemViewCacheSize(0);
        mAdapter.setFastScroller(fastScroller);
        mAdapter.setLongPressDragEnabled(false)
                .setHandleDragEnabled(false)
                .setSwipeEnabled(false);
    }

    private void setDatas() {
        mDatas.clear();
        for (int i = 0; i < recordList.size(); i++) {
            GameBillEntity entity = recordList.get(i);
            RecordListItem item = new RecordListItem(entity, i > 0 ? recordList.get(i - 1) : null);
            item.setItemType(RecordListItem.Companion.getITEM_TYPE_TEAM_HORDE());
            mDatas.add(item);
        }
        mDatas.add(mFooterLoadingItem);
        if (mAdapter != null) {
            mAdapter.setMMaxNum(mDatas.size() - 1);
            mAdapter.updateDataSet(mDatas);
        }
    }

    private boolean isLoadMore;
    public void getNetRecordList(final String lastGid) {
        if (true) {
            getNetRecordGids(lastGid);
            return;
        }
        if (mView == null || mResultDataView == null) {
            if (refresh_layout != null) {
                refresh_layout.setRefreshing(false);
            }
            return;
        }
        if (recordList == null || recordList.size() == 0) {
            mResultDataView.showLoading();
        }
        isLoadMore = true;
        mRecordAction.getRecordListForTeam(sessionId, lastGid, new GameRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (refresh_layout != null) {
                    refresh_layout.setRefreshing(false);
                }
                isLoadMore = false;
                RequestTimeLimit.lastTeamRecord = DemoCache.getCurrentServerSecondTime();
                try {
                    JSONObject dataJson = response.getJSONObject("data");
                    ArrayList<GameBillEntity> list = RecordJsonTools.getRecordGameBillList(response);
                    //判断最后条数据库是否存在，存在下次请求就读取数据库，不然就读取网络-----------------------
//                    if (list != null && list.size() != 0) {
//                        String newLastGid = list.get(list.size() - 1).gameInfo.gid;
//                        haveLocalLastRecord = GameRecordDBHelper.isLocalRecord(getActivity(), newLastGid);
//                    } else {
//                        haveLocalLastRecord = false;
//                    }
                    if (TextUtils.isEmpty(lastGid)) {
                        if (list != null) {
                            recordList.clear();
                            recordList.addAll(list);
                        }
                        setRecordList();
                    } else {
                        setMoreRecordList(list);
                    }
                    if (list != null && list.size() != 0) {
                        saveGameRecordToDB(list);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                afterGetData();
            }
            @Override
            public void onFailed(int code, JSONObject response) {
                isLoadMore = false;
                refresh_layout.setRefreshing(false);
                mFooterLoadingItem.setMState(mFooterLoadingItem.Companion.getSTATE_LOAD_FAIL());
                mAdapter.notifyItemChanged(mDatas.size() - 1);
                afterGetData();
            }
        });
    }

    public void setRecordList() {
        if (recordList == null || recordList.size() == 0) {
            recyclerview_team_record.setVisibility(View.GONE);
            mResultDataView.nullDataShow(R.string.record_null, R.mipmap.img_record_null);
        } else {
            recyclerview_team_record.setVisibility(View.VISIBLE);
            refresh_layout.setVisibility(View.VISIBLE);
            setDatas();
            mResultDataView.successShow();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestNewData();
    }

    public void requestNewData() {//有新战绩的时候重新请求
        if (mView == null) {
            return;
        }
        long currentTime = DemoCache.getCurrentServerSecondTime();
        if (currentTime - RequestTimeLimit.lastTeamRecord < RequestTimeLimit.GET_TEAM_RECORD) {
            LogUtil.i(GameAction.TAG, "获取俱乐部战绩数据时间未到");
            if (refresh_layout != null) {
                refresh_layout.setRefreshing(false);
            }
            return;
        }
        String lastGid = recordList != null && recordList.size() > 0 ? recordList.get(recordList.size() - 1).gameInfo.gid : "";
        long endTime = recordList != null && recordList.size() > 0 ? recordList.get(recordList.size() - 1).gameInfo.endTime : 0;
        getMoreRecordList(lastGid, endTime);
    }

    private void afterGetData() {
        if (mView == null) {
            return;
        }
        if (recordList == null || recordList.size() <= 0) {
            mResultDataView.nullDataShow(R.string.data_null);
        } else {
            mResultDataView.successShow();
            recyclerview_team_record.setVisibility(View.VISIBLE);
        }
    }

    public void saveGameRecordToDB(final ArrayList<GameBillEntity> list) {
        ThreadUtil.Execute(new Runnable() {
            @Override
            public void run() {
                GameRecordDBHelper.saveGameRecordList(getActivity(), list);//保存到数据库
            }
        });
    }

    @Override
    public void onDestroy() {
        if (mRecordAction != null) {
            mRecordAction.onDestroy();
            mRecordAction = null;
        }
        super.onDestroy();
    }

    @Override
    public void onDelete(int position) {

    }

    @Override
    public void onClick(int position) {
        if (recordList == null || recordList.size() <= position) {
            return;
        }
        GameBillEntity billEntity = recordList.get(position);
        RecordDetailsActivity.start(getActivity(), billEntity.gameInfo.gid);
    }

    @Override
    public void onLongClick(int position) {
    }

    ArrayList<GameBillEntity> pageRecordList = new ArrayList<GameBillEntity>();//每一页追加的网络战绩
    public synchronized void getNetRecordGids(final String lastGid) {
        if (mView == null || mResultDataView == null) {
            if (refresh_layout != null) {
                refresh_layout.setRefreshing(false);
            }
            return;
        }
        if (recordList == null || recordList.size() == 0) {
            mResultDataView.showLoading();
        }
        isLoadMore = true;
        mRecordAction.getRecordListGidsTeam(lastGid, sessionId, "", "", new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                LogUtil.i(TAG, result);
                if (code == 0) {
                    RequestTimeLimit.lastTeamRecord = DemoCache.getCurrentServerSecondTime();
                    pageRecordList.clear();
                    try {
                        JSONObject json = new JSONObject(result);
                        JSONArray dataJson = json.getJSONArray("data");
                        StringBuilder fetchParams = new StringBuilder();
                        for (int i = 0; i < dataJson.length(); i++) {
                            String gid = dataJson.optString(i);
                            GameBillEntity gameBillEntity = GameRecordDBHelper.getGameRecordByGid(getActivity(), gid);
                            if (gameBillEntity == null) {
                                fetchParams.append(gid + ",");//请求参数/game/gameView?gids=6782,6781,6773
                            } else {
                                pageRecordList.add(gameBillEntity);
                            }
                        }
                        if (fetchParams.length() > 1 && mRecordAction != null) {
                            mRecordAction.getRecordListByGid(fetchParams.substring(0, fetchParams.length() - 1)/*出去最后一个,*/, new RequestCallback() {
                                @Override
                                public void onResult(int code, String result, Throwable var3) {
                                    if (refresh_layout != null) {
                                        refresh_layout.setRefreshing(false);
                                    }
                                    isLoadMore = false;
                                    if (code == 0) {
                                        try {
                                            JSONObject jsonNewApi = new JSONObject(result);
                                            final ArrayList<GameBillEntity> list = RecordJsonTools.getRecordGameBillListByGids(jsonNewApi);
                                            if (TextUtils.isEmpty(lastGid)) {
                                                if (list != null) {
                                                    pageRecordList.addAll(list);
                                                    Collections.sort(pageRecordList, recordComp);
                                                    recordList.clear();
                                                    recordList.addAll(pageRecordList);
                                                }
                                                setRecordList();
                                            } else {
                                                pageRecordList.addAll(list);
                                                Collections.sort(pageRecordList, recordComp);
                                                setMoreRecordList(pageRecordList);
                                            }
                                            if (list != null && list.size() != 0) {
                                                saveGameRecordToDB(list);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {

                                    }
                                }

                                @Override
                                public void onFailed() {
                                    isLoadMore = false;
                                    refresh_layout.setRefreshing(false);
                                    mFooterLoadingItem.setMState(mFooterLoadingItem.Companion.getSTATE_LOAD_FAIL());
                                    mAdapter.notifyItemChanged(mDatas.size() - 1);
                                    afterGetData();
                                }
                            });
                        } else {
                            if (refresh_layout != null) {
                                refresh_layout.setRefreshing(false);
                            }
                            Collections.sort(pageRecordList, recordComp);
                            isLoadMore = false;
                            if (TextUtils.isEmpty(lastGid)) {
                                recordList.clear();
                                recordList.addAll(pageRecordList);
                                setRecordList();
                            } else {
                                setMoreRecordList(pageRecordList);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    isLoadMore = false;
                    if (refresh_layout != null) {
                        refresh_layout.setRefreshing(false);
                    }
                    mFooterLoadingItem.setMState(mFooterLoadingItem.Companion.getSTATE_LOAD_FAIL());
                    mAdapter.notifyItemChanged(mDatas.size() - 1);
                    afterGetData();
                }
            }

            @Override
            public void onFailed() {
                isLoadMore = false;
                if (refresh_layout != null) {
                    refresh_layout.setRefreshing(false);
                }
                mFooterLoadingItem.setMState(mFooterLoadingItem.Companion.getSTATE_LOAD_FAIL());
                mAdapter.notifyItemChanged(mDatas.size() - 1);
                afterGetData();
            }
        });
    }
}
