package com.htgames.nutspoker.ui.activity.Record;

/**
 * Created by 周智慧 on 2017/7/19.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.app_msg.attach.GameOverNotify;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageType;
import com.htgames.nutspoker.chat.app_msg.receiver.AppMessageReceiver;
import com.htgames.nutspoker.circle.activity.PublishActivity;
import com.htgames.nutspoker.data.common.CircleConstant;
import com.htgames.nutspoker.db.GameRecordDBHelper;
import com.htgames.nutspoker.game.model.GameStatus;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.tool.json.RecordJsonTools;
import com.htgames.nutspoker.ui.action.RecordAction;
import com.htgames.nutspoker.ui.adapter.GameRecordGridAdapter;
import com.htgames.nutspoker.ui.adapter.record.RecordListAdap;
import com.htgames.nutspoker.ui.adapter.record.RecordListItem;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.ui.items.FooterLoadingItem;
import com.htgames.nutspoker.view.ResultDataView;
import com.htgames.nutspoker.view.TouchableRecyclerView;
import com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.bean.GameBillEntity;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.common.framework.ThreadUtil;
import com.netease.nim.uikit.common.util.BaseTools;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.interfaces.IClick;
import com.netease.nim.uikit.session.constant.Extras;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import eu.davidea.fastscroller.FastScroller;

/**
 * 牌局信息
 */
public class RecordListAC extends BaseActivity implements View.OnClickListener, IClick {//代码拷贝自RecordListActivity以后要替换掉老的
    private final static String TAG = "RecordListAC";

    public final static int FROM_NORMAL = 0;//来自正常逻辑
    public final static int FROM_SHARE = 1;//来自分享界面
    public final static int FROM_SEND_BY_SESSION = 2;//发送：聊天界面
    public final static int FROM_SUPER_CLUB = 3;//来自超级俱乐部的牌局信息

    TouchableRecyclerView lv_list;
    RecordListAdap mAdapter;
    FooterLoadingItem mFooterLoadingItem;
    ArrayList<GameBillEntity> recordList = new ArrayList<GameBillEntity>();
    List mDatas = new ArrayList<RecordListItem>();
    ResultDataView mResultDataView;
    int from = FROM_NORMAL;
    SwipeRefreshLayout mSwipeRefreshLayout;
    boolean isLoadMore = false;
    boolean haveLocalLastRecord = false;//本地数据库存在最后条数据
    RecordAction mRecordAction;
    FastScroller fastScroller;
    LinearLayout ll_select_mode;
    HorizontalScrollView selectHorizontalScrollView;
    TextView tv_select_mode_prompt;
    GridView gv_select_game;
    Button btn_select_confim;
    //选中的
    ArrayList<GameEntity> selectGameList = new ArrayList<GameEntity>();
    GameRecordGridAdapter mGameRecordGridAdapter;
    int windowWidth = 0;
    //搜索
    ClearableEditTextWithIcon edit_search_record;

    public static void StartActivityFor(Activity activity, int from, int requestCode){
        Intent intent = new Intent(activity,RecordListAC.class);
        intent.putExtra(Extras.EXTRA_FROM,from);
        activity.startActivityForResult(intent,requestCode);
    }

    public static void StartActivity(Context context, int from){
        Intent intent = new Intent(context,RecordListAC.class);
        intent.putExtra(Extras.EXTRA_FROM,from);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list_new);
        windowWidth = BaseTools.getWindowWidth(getApplicationContext());
        from = getIntent().getIntExtra(Extras.EXTRA_FROM, FROM_NORMAL);
        initSearch();
        initHead();
        initView();
        initSelectGameList();
        mRecordAction = new RecordAction(this, null);
        initSwipeRefreshLayout();
//        initListFooterView();
        initResultDataView();
        recordList.addAll(GameRecordDBHelper.getGameRecordList(getApplicationContext()));
        initAdapter();
        if (!NetworkUtil.isNetworkConnected(getApplicationContext()) && recordList != null && recordList.size() != 0) {
            haveLocalLastRecord = true;
            setLocalRecordList();
        } else {
            getNetRecordList("");//oncreate初始化
        }
        registerObservers(true);
    }

    private void initSearch() {
        edit_search_record = (ClearableEditTextWithIcon) findViewById(R.id.edit_search_record);
        edit_search_record.setFocusableInTouchMode(false);
        edit_search_record.setText("");
        edit_search_record.setIconResource(R.mipmap.icon_search);
        edit_search_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordSearchAC.Companion.startByMyList(RecordListAC.this, edit_search_record, RecordSearchAC.Companion.getSEARCH_TYPE_MY_LIST(), "", "");
            }
        });
    }

    public void registerObservers(boolean register) {
        if (register) {
            IntentFilter intentFilter = new IntentFilter(AppMessageReceiver.ACTION_APP_MESSAGE);
            registerReceiver(mAppMessageReceiver, intentFilter);
        } else {
            unregisterReceiver(mAppMessageReceiver);
        }
    }

    AppMessageReceiver mAppMessageReceiver = new AppMessageReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            //App消息
            if (intent != null) {
                AppMessage appMessage = (AppMessage) intent.getSerializableExtra(Extras.EXTRA_APP_MESSAGE);
                if (appMessage.type == AppMessageType.GameOver && appMessage.attachObject instanceof GameOverNotify && ((GameOverNotify) appMessage.attachObject).gameStatus == GameStatus.GAME_STATUS_FINISH) {//有游戏结束
                    getNetRecordList("");//有新战绩消息来到
                }
            }
        }
    };

    private void initAdapter() {
        mFooterLoadingItem = new FooterLoadingItem();
        fastScroller = (FastScroller) findViewById(R.id.fast_scroller);
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
        lv_list.setAdapter(mAdapter);
        lv_list.setHasFixedSize(true);
        lv_list.setItemViewCacheSize(0);
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
            item.setItemType(RecordListItem.Companion.getITEM_TYPE_NORMAL());
            mDatas.add(item);
        }
        mDatas.add(mFooterLoadingItem);
        if (mAdapter != null) {
            mAdapter.setMMaxNum(mDatas.size() - 1);
            mAdapter.updateDataSet(mDatas);
        }
    }

    private void initSelectGameList() {
        mGameRecordGridAdapter = new GameRecordGridAdapter(getApplicationContext(), selectGameList);
        gv_select_game.setAdapter(mGameRecordGridAdapter);
    }

    private void notifyGameDataSetChanged() {
        int converViewWidth = windowWidth / 4;
        int converViewHeight = (int) getResources().getDimension(R.dimen.grid_record_game_select_height);
        ViewGroup.LayoutParams layoutParams = gv_select_game.getLayoutParams();
        layoutParams.width = converViewWidth * mGameRecordGridAdapter.getCount();
        layoutParams.height = converViewHeight;
        gv_select_game.setLayoutParams(layoutParams);
        gv_select_game.setNumColumns(mGameRecordGridAdapter.getCount());
        //Log.d(TAG, "mGameRecordGridAdapter.getCount() :" + mGameRecordGridAdapter.getCount());
        try {
            final int x = layoutParams.width;
            final int y = layoutParams.height;
            sHandler.post(new Runnable() {
                @Override
                public void run() {
                    selectHorizontalScrollView.scrollTo(x, y);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        mGameRecordGridAdapter.notifyDataSetChanged();
    }

    private void initHead() {
        setHeadTitle(R.string.record_paiju);
        if (from == FROM_NORMAL) {
//            setHeadRightButton(R.string.settlement, this);//
        }
    }

    public void updateSelectModeUI(boolean isSelectMode) {
//        mGameInfoListAdapter.setSelectedMode(isSelectMode);
        if (isSelectMode) {
            setHeadLeftButtonGone();
//            setHeadRightButtonText(R.string.cancel);
            ll_select_mode.setVisibility(View.VISIBLE);
            setSwipeBackEnable(false);
            mSwipeRefreshLayout.setEnabled(false);
        } else {
            setHeadLeftButton(R.string.back);
//            setHeadRightButtonText(R.string.settlement);
            ll_select_mode.setVisibility(View.GONE);
            selectGameList.clear();
            selectHorizontalScrollView.setVisibility(View.GONE);
            tv_select_mode_prompt.setVisibility(View.VISIBLE);
            btn_select_confim.setText(getString(R.string.confim_num, 0));
//            mGameInfoListAdapter.resetCheckedStatus();
            setSwipeBackEnable(true);
            mSwipeRefreshLayout.setEnabled(true);
        }
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mSwipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.login_solid_color, R.color.login_solid_color, R.color.login_solid_color, R.color.login_solid_color);
        // 这句话是为了，第一次进入页面的时候显示加载进度条
//        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isLoadMore) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    mFooterLoadingItem.setMState(mFooterLoadingItem.Companion.getSTATE_PRE());
                    mAdapter.notifyItemChanged(mDatas.size() - 1);
                    getNetRecordList("");//刷新
                }
            }
        });
        //加载更多
        lv_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (isLoadMore || mSwipeRefreshLayout.isRefreshing() || mFooterLoadingItem == null) {
                    return;
                }
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) lv_list.getLayoutManager();
                int firstVisibleItemPos = linearLayoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPos = linearLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = linearLayoutManager.getItemCount();
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && totalItemCount >= 1 && lastVisibleItemPos + 5 >= totalItemCount && (lastVisibleItemPos - firstVisibleItemPos) != totalItemCount) {
                    if (recordList != null && recordList.size() != 0) {
                        final String lastGid = recordList.get(recordList.size() - 1).gameInfo.gid;
                        final long endTime = recordList.get(recordList.size() - 1).gameInfo.endTime;
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
    }

    private void initResultDataView() {
        mResultDataView = (ResultDataView) findViewById(R.id.mResultDataView);
        mResultDataView.setReloadDataCallBack(new ResultDataView.ReloadDataCallBack() {
            @Override
            public void onReloadData() {
                getNetRecordList("");//reload
            }
        });
    }

    private void initView() {
        lv_list = (TouchableRecyclerView) findViewById(R.id.lv_list);
        lv_list.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, 0, 0, 0);
            }
        });
        recordList = new ArrayList<GameBillEntity>();
        ll_select_mode = (LinearLayout) findViewById(R.id.ll_select_mode);
        tv_select_mode_prompt = (TextView) findViewById(R.id.tv_select_mode_prompt);
        selectHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.selectHorizontalScrollView);
        gv_select_game = (GridView) findViewById(R.id.gv_select_game);
        btn_select_confim = (Button) findViewById(R.id.btn_select_confim);
        btn_select_confim.setText(getString(R.string.confim_num, 0));
        btn_select_confim.setOnClickListener(this);
    }

    /**
     * 获取游戏战绩列表接口
     */
    public synchronized void getNetRecordList(final String lastGid) {
        if (true) {
            getNetRecordGids(lastGid);
            return;
        }
        if (recordList == null || recordList.size() == 0) {
            mResultDataView.showLoading();
        }
        mRecordAction.getRecordList(lastGid, new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                LogUtil.i(TAG, result.substring(0, result.length() / 2));
                LogUtil.i(TAG, result.substring(result.length() / 2, result.length()));//data太长，log显示不全，分开显示
                mSwipeRefreshLayout.setRefreshing(false);
                isLoadMore = false;
                if (code == 0) {
                    try {
                        JSONObject json = new JSONObject(result);
                        JSONObject dataJson = json.getJSONObject("data");
                        final ArrayList<GameBillEntity> list = RecordJsonTools.getRecordGameBillList(json);
                        //判断最后条数据库是否存在，存在下次请求就读取数据库，不然就读取网络-----------------------
                        if (list != null && list.size() != 0) {
                            String newLastGid = list.get(list.size() - 1).gameInfo.gid;
                            haveLocalLastRecord = GameRecordDBHelper.isLocalRecord(getApplicationContext(), newLastGid);
                        } else {
                            haveLocalLastRecord = false;
                        }
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
                } else {
                    if (TextUtils.isEmpty(lastGid)) {
                        getRecordFailed();
                    } else {
                        getRecordMoreFailed();
                    }
                }
            }

            @Override
            public void onFailed() {
                isLoadMore = false;
                mSwipeRefreshLayout.setRefreshing(false);
                if (TextUtils.isEmpty(lastGid)) {
                    getRecordFailed();
                } else {
                    getRecordMoreFailed();
                }
            }
        });
    }

    public synchronized void getLocalRecordList(final String lastGid , long endTime) {
        ArrayList<GameBillEntity> list = GameRecordDBHelper.getGameRecordList(getApplicationContext(), lastGid, endTime);
        setMoreRecordList(list);
        long currentLastEndTime = recordList.get(recordList.size() - 1).gameInfo.endTime;
//        haveLocalLastRecord = GameRecordDBHelper.haveMoreRecord(getApplicationContext(), currentLastEndTime);
        isLoadMore = false;
    }

    public void getMoreRecordList(String lastGid , long endTime) {
        if (!isLoadMore) {
            isLoadMore = true;
//            if (haveLocalLastRecord && GameRecordDBHelper.haveMoreRecord(getApplicationContext(), endTime)) {
            if (haveLocalLastRecord) {
                //Log.d(TAG, "本地还有缓存");
                getLocalRecordList(lastGid, endTime);
            } else {
                //Log.d(TAG, "本地没有缓存，读取网络");
                getNetRecordList(lastGid);//分页追加
            }
        }
    }

    public void getRecordFailed() {
        lv_list.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
        mResultDataView.showError(getApplicationContext(), R.string.load_failed);
    }

    public void getRecordMoreFailed() {
        mFooterLoadingItem.setMState(mFooterLoadingItem.Companion.getSTATE_LOAD_FAIL());
        mAdapter.notifyItemChanged(mDatas.size() - 1);
    }

    public void setLocalRecordList() {
        mResultDataView.successShow();
        setDatas();
    }

    public void saveGameRecordToDB(final ArrayList<GameBillEntity> list) {
        ThreadUtil.Execute(new Runnable() {
            @Override
            public void run() {
                GameRecordDBHelper.saveGameRecordList(getApplicationContext(), list);//保存到数据库
            }
        });
    }

    public void setRecordList() {
        if (recordList == null || recordList.size() == 0) {
            lv_list.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.GONE);
            mResultDataView.nullDataShow(R.string.no_data, R.mipmap.img_record_null);
            setHeadRightButtonGone();
        } else {
            if (from == FROM_NORMAL) {
//                setHeadRightButtonText(R.string.settlement);// // TODO: 16/12/15 "统计功能"先去掉
            }
            lv_list.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
            setDatas();
            mResultDataView.successShow();
        }
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
            item.setItemType(RecordListItem.Companion.getITEM_TYPE_NORMAL());
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
    protected void onDestroy() {
        super.onDestroy();
        if (mRecordAction != null) {
            mRecordAction.onDestroy();
            mRecordAction = null;
        }
        lv_list.setOnScrollListener(null);
        registerObservers(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_head_right:
//                if (mGameInfoListAdapter != null) {
//                    boolean currentSelectMode = mGameInfoListAdapter.isSelectShowMode();
//                    updateSelectModeUI(!currentSelectMode);
//                }
                break;
            case R.id.btn_select_confim:
//                if (mGameInfoListAdapter != null) {
//                    ArrayList<GameBillEntity> selectList = (ArrayList<GameBillEntity>) mGameInfoListAdapter.getSelectList();
//                    if (selectList == null || selectList.size() == 0) {
//                    } else {
//                        RecordDetailsInfoActivity.start(this, selectList);
//                    }
//                }
                break;
        }
    }

    @Override
    public void onDelete(int position) {

    }

    @Override
    public void onClick(int position) {
        if (!(mAdapter.getItem(position) instanceof RecordListItem)) {
            return;
        }
        GameBillEntity billEntity = ((RecordListItem) mAdapter.getItem(position)).getMBill();
        if (from == FROM_NORMAL) {
            if (false/*mGameInfoListAdapter.isSelectShowMode()*/) {
//                if (billEntity != null && billEntity.gameInfo != null && billEntity.gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL) {
//                    mGameInfoListAdapter.clickCheckBox(position);
//                }
            } else {
                RecordDetailsActivity.start(RecordListAC.this, billEntity.gameInfo.gid);
            }
        } else if (from == FROM_SHARE) {
            PublishActivity.start(RecordListAC.this, CircleConstant.TYPE_PAIJU, billEntity, RecordDetailsActivity.FROM_CIRCLE);
        } else if (from == FROM_SEND_BY_SESSION) {
            Intent intent = new Intent();
            intent.putExtra(Extras.EXTRA_BILL, billEntity);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onLongClick(int position) {
    }

    /**
     * 获取gid列表，本地有gid的话通过读取数据库读取游戏record信息，没有gid的话请求第二个接口获取record详情
     */
    ArrayList<GameBillEntity> pageRecordList = new ArrayList<GameBillEntity>();//每一页追加的网络战绩
    ArrayList<GameBillEntity> pageRecordListFromNet = new ArrayList<GameBillEntity>();//每一页追加的网络战绩
    ArrayList<GameBillEntity> pageRecordListFromDB = new ArrayList<GameBillEntity>();//每一页追加的本地战绩
    public synchronized void getNetRecordGids(final String lastGid) {
        if (recordList == null || recordList.size() == 0) {
            mResultDataView.showLoading();
        }
        if (mRecordAction == null) {//可能执行了ondestroy导致mRecordAction为null
            return;
        }
        mRecordAction.getRecordListGids(lastGid, "", "", "", new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                LogUtil.i(TAG, result);
                if (code == 0) {
                    pageRecordList.clear();
                    try {
                        JSONObject json = new JSONObject(result);
                        JSONArray dataJson = json.getJSONArray("data");
//                        ArrayList<String> pageGidLisAll = new ArrayList<String>();//服务端当前页返回的所有gid
                        StringBuilder fetchParams = new StringBuilder();
                        for (int i = 0; i < dataJson.length(); i++) {
                            String gid = dataJson.optString(i);
//                            pageGidLisAll.add(gid);
                            GameBillEntity gameBillEntity = GameRecordDBHelper.getGameRecordByGid(getApplicationContext(), gid);
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
                                    mSwipeRefreshLayout.setRefreshing(false);
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
                                    if (TextUtils.isEmpty(lastGid)) {
                                        getRecordFailed();
                                    } else {
                                        getRecordMoreFailed();
                                    }
                                }
                            });
                        } else {
                            mSwipeRefreshLayout.setRefreshing(false);
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
                    if (TextUtils.isEmpty(lastGid)) {
                        getRecordFailed();
                    } else {
                        getRecordMoreFailed();
                    }
                }
            }

            @Override
            public void onFailed() {
                isLoadMore = false;
                mSwipeRefreshLayout.setRefreshing(false);
                if (TextUtils.isEmpty(lastGid)) {
                    getRecordFailed();
                } else {
                    getRecordMoreFailed();
                }
            }
        });
    }

    public static Comparator<GameBillEntity> recordComp = new Comparator<GameBillEntity>() {
        @Override
        public int compare(GameBillEntity o1, GameBillEntity o2) {
            long timeOffset = o1.gameInfo.endTime - o2.gameInfo.endTime;
            return timeOffset == 0 ? 0 : (timeOffset > 0 ? -1 : 1);
        }
    };
}

