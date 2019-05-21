package com.htgames.nutspoker.ui.activity.Hands;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiCode;
import com.htgames.nutspoker.chat.contact_selector.activity.ContactSelectActivity;
import com.htgames.nutspoker.chat.contact_selector.activity.RecentContactSelectActivity;
import com.htgames.nutspoker.chat.session.activity.P2PMessageActivity;
import com.htgames.nutspoker.chat.session.activity.TeamMessageActivity;
import com.htgames.nutspoker.circle.activity.PublishActivity;
import com.htgames.nutspoker.cocos2d.PokerActivity;
import com.htgames.nutspoker.data.DataManager;
import com.htgames.nutspoker.data.common.CircleConstant;
import com.htgames.nutspoker.data.common.UserConstant;
import com.htgames.nutspoker.db.GameRecordDBHelper;
import com.htgames.nutspoker.db.HandsCollectDBHelper;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.ui.action.HandCollectAction;
import com.htgames.nutspoker.ui.adapter.hands.CardRecordAdapter;
import com.htgames.nutspoker.util.TimeUtil;
import com.htgames.nutspoker.util.ToolUtil;
import com.htgames.nutspoker.view.PaipuMoreView;
import com.htgames.nutspoker.view.ResultDataView;
import com.htgames.nutspoker.view.ShareView;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.api.ApiConfig;
import com.netease.nim.uikit.bean.CardGamesEy;
import com.netease.nim.uikit.bean.CommonBeanT;
import com.netease.nim.uikit.bean.NetCardCountEy;
import com.netease.nim.uikit.bean.NetCardRecordEy;
import com.netease.nim.uikit.bean.PaipuEntity;
import com.netease.nim.uikit.common.gson.GsonUtils;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.permission.PermissionUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * 牌谱记录
 */
public class PaipuRecordActivity extends BasePaipuActivity {
    private final static String TAG = "PaipuRecordActivity";
    private ListView lv_list;
    private ResultDataView mResultDataView;
    public static HashSet<String> canceledCollectList = new HashSet<String>();//被"牌局收藏"页面取消收藏的手牌id列表
    CardRecordAdapter mCardAdapter;
    String[] handFileNames;
    ArrayList<PaipuEntity> paipuList = new ArrayList<>();
    Map<String, Integer> nodeMap = new HashMap<>();
    Map<String, Integer> nodePositionMap = new HashMap<>();
    public HandCollectAction mHandCollectAction;
    Map<String, String> collectHidMap = new HashMap<>();
    public static int EACH_PAGE_COUNT = 20;//每页包含的条数
    int currentHand = 0;
    int totalHandCount = 0;
    boolean isLoadMore = false;

    int mVip;
    //是否已经显示所有牌谱记录数据
    boolean mIsAll = false;
    //最近7天，最近30天，最近90天
    List<NetCardRecordEy> mListNetCardRecord = new ArrayList<>();

    //如果当前没网的状态进入，那么我就只拉数据库的数据，不再去拉去本地
    boolean mIsOnlyDB = false;
    ShareView shareView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            PermissionUtils.requestPermission(this, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE, null);
        }
        setContentView(R.layout.activity_list);
        mUnbinder = ButterKnife.bind(this);
        //data
        from = getIntent().getIntExtra(EXTRA_FROM, FROM_NORMAL);
        mVip = UserConstant.getMyVipLevel();
        mHandCollectAction = new HandCollectAction(this, null);
        shareView = new ShareView(this);
        initHead();
        initView();
        initList();
    }

    @Override
    protected void onDestroy() {
        if (mHandCollectAction != null) {
            mHandCollectAction.onDestroy();
            mHandCollectAction = null;
        }
        if (shareView != null) {
            shareView.onDestroy();
            shareView = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //如果在收藏页面有更改，那么回来的时候需要change
        boolean sIsChangedForRecordPage = false;
        for (int i = 0; i < mListNetCardRecord.size(); i++) {
            NetCardRecordEy data = mListNetCardRecord.get(i);
            if (canceledCollectList.contains(data.id)) {
                data.is_collect = 0;//"牌谱收藏"的hid和"牌谱记录"的id是一致的，乱七八糟
                sIsChangedForRecordPage = true;
            }
        }
        if (sIsChangedForRecordPage) {
            mCardAdapter.setData(mListNetCardRecord);
        }
        canceledCollectList.clear();
    }

    private void initList() {
        if (from == FROM_SESSION_BY_P2P || from == FROM_SESSION_BY_TEAM) {
            mCardAdapter = new CardRecordAdapter(this, getWindow().getDecorView(), mListNetCardRecord, mOnMoreListener, mOnCallOverback);
            mCardAdapter.setIsShowMore(false);
        } else {
            mCardAdapter = new CardRecordAdapter(this, getWindow().getDecorView(), mListNetCardRecord, mOnMoreListener, mOnCallOverback);
        }
        lv_list.setAdapter(mCardAdapter);
        getNetRecord();
        getNetRecordList(null, 0);
    }

    //新版本获取牌谱记录 第一次获取
    void getNetRecord() {
        mResultDataView.showLoading();

        //获取总数
        mHandCollectAction.getCRListCount(new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if (code == 0) {
                    try {
                        Type type = new TypeToken<CommonBeanT<NetCardCountEy>>() {
                        }.getType();
                        CommonBeanT<NetCardCountEy> cbt = GsonUtils.getGson().fromJson(result, type);

                        totalHandCount = cbt.data.count;
                        updateHead(totalHandCount);

                        if (totalHandCount == 0) {
                            mIsAll = true;
                            mResultDataView.nullDataShow(R.string.paipu_record_null, R.mipmap.img_paipu_null);
                        } else {
                            mIsAll = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //可能有救，继续向服务器拉数据
                    mResultDataView.nullDataShow(R.string.error_try_again, R.mipmap.img_paipu_null, View.VISIBLE);
                }
            }

            @Override
            public void onFailed() {
                //From DB
                mIsOnlyDB = true;

                Observable.just(0)
                        .observeOn(Schedulers.io())
                        .map(new Func1<Integer, Integer>() {
                            @Override
                            public Integer call(Integer integer) {
                                return GameRecordDBHelper.GetNetRecordCount(PaipuRecordActivity.this);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                totalHandCount = integer;
                                updateHead(totalHandCount);

                                if (totalHandCount == 0) {
                                    mIsAll = true;
                                    mResultDataView.nullDataShow(R.string.paipu_record_null, R.mipmap.img_paipu_null);
                                } else {
                                    mIsAll = false;
                                }

                            }
                        });
            }
        });
    }

    void getNetRecordList(@Nullable final String handId, final long time) {
        if (mIsAll)
            return;

        //获取列表
        mHandCollectAction.getCRList(handId, new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if (code == 0) {
                    Observable.just(result)
                            .observeOn(Schedulers.computation())
                            .map(new Func1<String, List<NetCardRecordEy>>() {
                                @Override
                                public List<NetCardRecordEy> call(String s) {
                                    List<NetCardRecordEy> tmpList = null;
                                    try {
                                        Type type = new TypeToken<CommonBeanT<List<NetCardRecordEy>>>() {
                                        }.getType();
                                        CommonBeanT<List<NetCardRecordEy>> cbt = GsonUtils.getGson().fromJson(s, type);
                                        tmpList = cbt.data;
                                        //过滤过期的牌谱
                                        tmpList = filterLimitRecord(tmpList);
                                        //缓存数据库
                                        GameRecordDBHelper.SetNetRecordList(PaipuRecordActivity.this, tmpList);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return tmpList;
                                }
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<List<NetCardRecordEy>>() {
                                @Override
                                public void call(List<NetCardRecordEy> netCardRecordEys) {
                                    //如果是第一次获取，那么需要清空以前的数据
                                    if (handId == null) {
                                        mListNetCardRecord.clear();
                                    }
                                    if (netCardRecordEys != null && !netCardRecordEys.isEmpty()) {
                                        mListNetCardRecord.addAll(netCardRecordEys);
                                        if (mCardAdapter != null) {
                                            mCardAdapter.setData(mListNetCardRecord);
                                        }
                                    }
                                    if (handId == null) {
                                        updateListView();
                                    }
                                    isLoadMore = false;
                                    if (mIsAll) {
                                        bottomLoadingView.stateAllDataLoadDone(true);
                                    } else {
                                        bottomLoadingView.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                    return;
                } else {
                    isLoadMore = false;
                    if (mIsAll) {
                        bottomLoadingView.stateAllDataLoadDone(true);
                    } else {
                        bottomLoadingView.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onFailed() {
                mIsOnlyDB = true;
                getListFromRecordFromDB(time);
            }
        });
    }

    void getListFromRecordFromDB(final long time) {
        Observable.just(time)
                .observeOn(Schedulers.io())
                .map(new Func1<Long, List<NetCardRecordEy>>() {
                    @Override
                    public List<NetCardRecordEy> call(Long aLong) {
                        List<NetCardRecordEy> tmpList = GameRecordDBHelper.GetNetRecordList(PaipuRecordActivity.this, aLong);

                        //过滤过期的牌谱
                        tmpList = filterLimitRecord(tmpList);
                        return tmpList;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<NetCardRecordEy>>() {
                    @Override
                    public void call(List<NetCardRecordEy> netCardRecordEys) {
                        mListNetCardRecord.addAll(netCardRecordEys);
                        if (time == 0) {
                            updateListView();
                        }
                        if (!netCardRecordEys.isEmpty()) {
                            mCardAdapter.setData(mListNetCardRecord);
                        }
                        isLoadMore = false;
                        if (mIsAll) {
                            bottomLoadingView.stateAllDataLoadDone(true);
                        } else {
                            bottomLoadingView.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    void updateListView() {
        if (!mListNetCardRecord.isEmpty()) {
            mResultDataView.successShow();
            lv_list.setVisibility(View.VISIBLE);
            mCardAdapter.setData(mListNetCardRecord);
        } else {
            lv_list.setVisibility(View.GONE);
            mResultDataView.nullDataShow(R.string.paipu_record_null, R.mipmap.img_paipu_null);
        }
    }

    public void nullDataShow() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mResultDataView.nullDataShow(R.string.paipu_record_null, R.mipmap.img_paipu_null);
            }
        });
    }

    public void addRecordList(ArrayList<PaipuEntity> moreList) {
        if (moreList == null || moreList.size() == 0) {
            return;
        }
        int size = moreList.size();
        for (int i = 0; i < size; i++) {
            String node = moreList.get(i).node;
            if (nodeMap.containsKey(node)) {
                //包含该节点
                nodeMap.put(node, nodeMap.get(node) + 1);
            } else {
                nodeMap.put(node, 1);
            }
            if (!nodePositionMap.containsKey(node)) {
                nodePositionMap.put(node, paipuList.size() + i);
            }
            if (collectHidMap.containsKey(moreList.get(i).fileName)) {
                moreList.get(i).isCollect = (true);
                moreList.get(i).handsId = (collectHidMap.get(moreList.get(i).fileName));
            }
        }
        paipuList.addAll(moreList);
    }

    private void initHead() {
        setHeadTitle(R.string.record_handcard);
        setHeadRightButton(R.string.my_collect, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaipuRecordActivity.this, PaipuCollectActivity.class);
                intent.putExtra(EXTRA_FROM, from);
                startActivity(intent);
            }
        });
    }

    public void updateHead(int count) {
        if (count > 0) {
            setHeadTitle(getString(R.string.record_handcard) + "(" + ToolUtil.Number2Change(this,count) + ")");
        } else {
            setHeadTitle(R.string.record_handcard);
        }
    }

    private void initView() {
        paipu_record_and_collect_pulltorefresh_lv = (SwipeRefreshLayout) findViewById(R.id.paipu_record_and_collect_pulltorefresh_lv);
        paipu_record_and_collect_pulltorefresh_lv.setEnabled(false);
        lv_list = (ListView) findViewById(R.id.lv_list);
        lv_list.addFooterView(bottomLoadingView);
        mResultDataView = (ResultDataView) findViewById(R.id.mResultDataView);
        mResultDataView.setReloadDataCallBack(new ResultDataView.ReloadDataCallBack() {
            @Override
            public void onReloadData() {
                getNetRecord();
                getNetRecordList(null, 0);
            }
        });
        //加载更多
        lv_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int lastVisibleItem = 0;
            private int firstVisibleItem = 0;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int listViewCount = lv_list.getCount() - 1;
                if (totalHandCount == mListNetCardRecord.size()) {
                    mIsAll = true;
                }
                if (!mIsAll) {
                    bottomLoadingView.statePre();
                } else {
                    bottomLoadingView.stateAllDataLoadDone(true);
                }
                if (!isLoadMore && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastVisibleItem == listViewCount) {//多加了个bottomloadingbiew，所以listViewCount要加1
                    if (mIsAll) {
                        bottomLoadingView.stateAllDataLoadDone(true);
                        return;
                    }
                    else if (totalHandCount == mListNetCardRecord.size()) {
                        mIsAll = true;
                        return;
                    }
                    isLoadMore = true;
                    NetCardRecordEy data = (NetCardRecordEy) view.getItemAtPosition(mCardAdapter.getCount() - 1);
                    if (data != null) {
                        bottomLoadingView.stateLoading();
                        if (mIsOnlyDB){
                            getListFromRecordFromDB(data.time);//如果只从数据库拉取
                        } else {
                            getNetRecordList(data.id, data.time);
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastVisibleItem = lv_list.getLastVisiblePosition();
                firstVisibleItem = lv_list.getFirstVisiblePosition();
            }
        });
        //
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //新版本牌谱接口变化
                if (mListNetCardRecord == null || position < 0 || position >= mListNetCardRecord.size()) {
                    return;
                }
                final NetCardRecordEy fNetCardCountEy = mListNetCardRecord.get(position);
                if (from == FROM_NORMAL) {//牌谱记录只保留新版本牌谱了，旧牌谱不再支持
                    DialogMaker.showProgressDialog(PaipuRecordActivity.this, "", false);
                    //mHandCollectAction.openHandPlay(paipuEntity);
                    //打开牌局播放
                    String path = DataManager.GetSheetFileOfMy(fNetCardCountEy.id, mHandCollectAction, new DataManager.OnDataFinish() {
                        @Override
                        public void onDataFinish(Object data) {
                            DialogMaker.dismissProgressDialog();
                            if (ActivityCompat.checkSelfPermission(PaipuRecordActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                PermissionUtils.requestPermission(PaipuRecordActivity.this, PermissionUtils.CODE_READ_PHONE_STATE, null);
                                return;
                            }
                            if (data != null) {
                                PokerActivity.startGamePlayRecord(PaipuRecordActivity.this, UserPreferences.getInstance(getApplicationContext()).getUserId(), (String) data);
                            }
                        }
                    });

                    if (path != null) {
                        DialogMaker.dismissProgressDialog();
                        if (ActivityCompat.checkSelfPermission(PaipuRecordActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                            PermissionUtils.requestPermission(PaipuRecordActivity.this, PermissionUtils.CODE_READ_PHONE_STATE, null);
                            return;
                        }
                        PokerActivity.startGamePlayRecord(PaipuRecordActivity.this, UserPreferences.getInstance(getApplicationContext()).getUserId(), path);
                    }
                } else if (from == FROM_SHARE) {
                } else if (from == FROM_SESSION_BY_P2P) {
                    CardGamesEy cardGamesEy = DataManager.getInstance().getCardGamesEy(fNetCardCountEy.gid, mHandCollectAction, new DataManager.OnDataFinish() {
                        @Override
                        public void onDataFinish(Object data) {
                            if (data != null) {
                                CardGamesEy cardGamesEy1 = (CardGamesEy) data;
                                PaipuEntity paipuEntity = new PaipuEntity();
                                DataManager.ConvertData(fNetCardCountEy, cardGamesEy1, paipuEntity);
                                P2PMessageActivity.StartActivity(PaipuRecordActivity.this,CircleConstant.TYPE_PAIPU,paipuEntity);
                            }
                        }
                    });

                    if (cardGamesEy != null) {
                        PaipuEntity paipuEntity = new PaipuEntity();
                        DataManager.ConvertData(fNetCardCountEy, cardGamesEy, paipuEntity);
                        P2PMessageActivity.StartActivity(PaipuRecordActivity.this,CircleConstant.TYPE_PAIPU,paipuEntity);
                    }

                } else if (from == FROM_SESSION_BY_TEAM) {
                    CardGamesEy cardGamesEy = DataManager.getInstance().getCardGamesEy(fNetCardCountEy.gid, mHandCollectAction, new DataManager.OnDataFinish() {
                        @Override
                        public void onDataFinish(Object data) {
                            if (data != null) {
                                CardGamesEy cardGamesEy1 = (CardGamesEy) data;
                                PaipuEntity paipuEntity = new PaipuEntity();
                                DataManager.ConvertData(fNetCardCountEy, cardGamesEy1, paipuEntity);
                                TeamMessageActivity.StartActivity(PaipuRecordActivity.this,CircleConstant.TYPE_PAIPU, paipuEntity);
                            }
                        }
                    });

                    if (cardGamesEy != null) {
                        PaipuEntity paipuEntity = new PaipuEntity();
                        DataManager.ConvertData(fNetCardCountEy, cardGamesEy, paipuEntity);
                        TeamMessageActivity.StartActivity(PaipuRecordActivity.this,CircleConstant.TYPE_PAIPU,paipuEntity);
                    }
                }
            }
        });
    }

    PaipuMoreView.OnMoreListener mOnMoreListener = new PaipuMoreView.OnMoreListener() {
        String mmTempFileName = "";
        @Override
        public void onSend(int position) {
            final NetCardRecordEy netCardRecordEy = mListNetCardRecord.get(position);
            CardGamesEy gamesEy = DataManager.getInstance().getCardGamesEy(netCardRecordEy.gid, mHandCollectAction, new DataManager.OnDataFinish() {
                @Override
                public void onDataFinish(Object data) {
                    PaipuEntity paipuEntity = new PaipuEntity();
                    DataManager.ConvertData(netCardRecordEy, (CardGamesEy) data, paipuEntity);
                    if (paipuEntity != null)
                        RecentContactSelectActivity.start(PaipuRecordActivity.this, CircleConstant.TYPE_PAIPU, paipuEntity, ContactSelectActivity.FROM_PAIPU_RECORD);
                }
            });
            if (gamesEy != null) {
                PaipuEntity paipuEntity = new PaipuEntity();
                DataManager.ConvertData(netCardRecordEy, gamesEy, paipuEntity);
                if (paipuEntity != null)
                    RecentContactSelectActivity.start(PaipuRecordActivity.this, CircleConstant.TYPE_PAIPU, paipuEntity, ContactSelectActivity.FROM_PAIPU_RECORD);
            }
        }

        @Override
        public void onShare(int position) {
            final NetCardRecordEy netCardRecordEy = mListNetCardRecord.get(position);
            CardGamesEy gamesEy = DataManager.getInstance().getCardGamesEy(netCardRecordEy.gid, mHandCollectAction, new DataManager.OnDataFinish() {
                @Override
                public void onDataFinish(Object data) {
                    PaipuEntity paipuEntity = new PaipuEntity();
                    DataManager.ConvertData(netCardRecordEy, (CardGamesEy) data, paipuEntity);
                    PublishActivity.start(PaipuRecordActivity.this, CircleConstant.TYPE_PAIPU, paipuEntity);
                }
            });
            if (gamesEy != null) {
                PaipuEntity paipuEntity = new PaipuEntity();
                DataManager.ConvertData(netCardRecordEy, gamesEy, paipuEntity);
                PublishActivity.start(PaipuRecordActivity.this, CircleConstant.TYPE_PAIPU, paipuEntity);
            }
        }

        @Override
        public void onCollect(final int position) {
            boolean isCollect = false;
            NetCardRecordEy data = mListNetCardRecord.get(position);
            final String handId = data.id;
            isCollect = data.is_collect == 1;

            if (isCollect) {
                //取消收藏
                mHandCollectAction.doCancelHandsCollect(handId, new RequestCallback() {
                    @Override
                    public void onResult(int code, String result, Throwable var3) {
                        if (code == 0) {
                            //删除容易，增加难，我们这里只对删除操作，增加操作的字段需要服务器给我们...
                            //尝试删除一下
                            HandsCollectDBHelper.DeleteHandsCollectList(PaipuRecordActivity.this, handId);
                            mListNetCardRecord.get(position).is_collect = 0;
                            mCardAdapter.setData(mListNetCardRecord);
                            Toast.makeText(ChessApp.sAppContext, R.string.collect_cancel_success, Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailed() {
                        Toast.makeText(ChessApp.sAppContext, R.string.collect_cancel_failure, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                NetCardRecordEy netCardRecordEy = mListNetCardRecord.get(position);
                //新版本需要请求服务器
                mHandCollectAction.collectHand(netCardRecordEy.id, new RequestCallback() {
                    @Override
                    public void onResult(int code, String result, Throwable var3) {
                        if (code == 0 || code == ApiCode.CODE_HANDS_COLLECT_AREADY) {
                            mListNetCardRecord.get(position).is_collect = 1;
                            mCardAdapter.setData(mListNetCardRecord);
                            Toast.makeText(ChessApp.sAppContext, R.string.collect_success, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailed() {
                        Toast.makeText(ChessApp.sAppContext, R.string.collect_failure, Toast.LENGTH_SHORT).show();
                    }
                });
//                } else
//                {
//                    PaipuEntity paipuEntity = paipuList.get(position);
//                    //老版本需要上传的阿里云服务器
//                    mHandCollectAction.uploadFile(paipuEntity, true ,new RequestCallback() {
//                        @Override
//                        public void onResult(int code, String result, Throwable var3) {
//                            if (code == 0 || code == ApiCode.CODE_HANDS_COLLECT_AREADY) {
//                                try {
//                                    JSONObject jsonObject = new JSONObject(result);
//                                    JSONObject dataObject = jsonObject.getJSONObject("data");
//                                    String hid = dataObject.optString(PaipuConstants.KEY_HID);
//                                    long collectTime = dataObject.optLong(PaipuConstants.KEY_COLLECT_TIME);
//                                    paipuList.get(position).setHandsId(hid);
//                                    paipuList.get(position).setCollectTime(collectTime);
//                                    paipuList.get(position).setIsCollect(true);
////                                HandsCollectDBHelper.addCollectHands(mContext, paipuList.get(position));//记录到本地数据库
//                                    if(!collectHidMap.containsKey(paipuList.get(position).getFileName())){
//                                        collectHidMap.put(paipuList.get(position).getFileName() ,hid);
//                                    }
//                                    mPaipuAdapter.notifyDataSetChanged();
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onFailed() {
//                            Toast.makeText(PaipuRecordActivity.this,R.string.collect_failure,Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
            }
        }

        @Override
        public void onShareWechat(int position, View view) {
            sharePaipuToSocial(SHARE_MEDIA.WEIXIN, position, view);
        }

        @Override
        public void onShareMoments(int position, View view) {
            sharePaipuToSocial(SHARE_MEDIA.WEIXIN_CIRCLE, position, view);
        }

        @Override
        public void onShareSinaweibo(int position, View view) {
            sharePaipuToSocial(SHARE_MEDIA.SINA, position, view);
        }
    };

    public void sharePaipuToSocial(SHARE_MEDIA shareMedia, int position, View view) {
        NetCardRecordEy data = mListNetCardRecord.get(position);
        final String handId = data.id;
        String shareUrl = ApiConfig.getPaipuShareUrl(handId);
        shareView.shareHands(shareMedia, shareUrl, view);
    }

    PaipuMoreView.OnCallOverback mOnCallOverback = new PaipuMoreView.OnCallOverback() {
        @Override
        public void showShadow(boolean show) {
            if (show) {
                findViewById(R.id.view_shadow).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.view_shadow).setVisibility(View.GONE);
            }
        }
    };

    //过滤老版本的牌谱记录,每次重新启动后第一次过滤一下，后面都不用过滤了
    static boolean sIsFiltered = false;

    static int filterLimitRecord(String[] tempFileList, int vipLevel) {
        if (sIsFiltered || tempFileList == null)
            return -1;
        sIsFiltered = true;

        //过滤掉超过期限的牌谱记录
        boolean isFind = false;
        int endFilePos = 0;
        for (String file : tempFileList) {
            //获取到文件的时间戳
            int startPos = file.indexOf("-");
            int endPos = file.indexOf("-", startPos + 1);
            String time = file.substring(startPos + 1, endPos - 1);
            switch (vipLevel) {
                case 2:
                    if (TimeUtil.IsInNinetyDay(Long.parseLong(time))) {
                        isFind = true;
                    }
                    break;
                case 1:
                    if (TimeUtil.IsInThirtyDay(Long.parseLong(time))) {
                        isFind = true;
                    }
                    break;
                case 0:
                case -1:
                    if (TimeUtil.IsInWeek(Long.parseLong(time))) {
                        isFind = true;
                    }
                    break;
            }
            if (isFind)
                break;
            endFilePos++;
        }
        return endFilePos;
    }

    //根据VIP过滤牌谱记录
    List<NetCardRecordEy> filterLimitRecord(List<NetCardRecordEy> list) {
        if (list == null || list.isEmpty())
            return list;

        boolean isFind = false;
        int delPos = 0;
        for (NetCardRecordEy data : list) {
            switch (mVip) {
                //黑金会员
                case 2:
                    if (!TimeUtil.IsInNinetyDay(data.time)) {
                        isFind = true;
                    }
                    break;
                //白金会员
                case 1:
                    if (!TimeUtil.IsInThirtyDay(data.time)) {
                        isFind = true;
                    }
                    break;
                //普通会员
                case 0:
                case -1:
                    if (!TimeUtil.IsInThirtyDay(data.time)) {//if (!TimeUtil.IsInWeek(data.time)) { 需求改版  普通会员 时间从7天改为30天   20161221
                        isFind = true;
                    }
                    break;
            }

            if (isFind)
                break;
            delPos++;
        }

        if (isFind) {
            List<NetCardRecordEy> deleteList = list.subList(delPos, list.size());
            List<NetCardRecordEy> ret = list.subList(0, delPos);

            //删掉过期的牌谱
            for (NetCardRecordEy netCardRecordEy : deleteList) {
                GameRecordDBHelper.DeleteNetRecord(PaipuRecordActivity.this, netCardRecordEy.id);
            }
            mIsAll = true;
            return ret;
        }
        return list;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, null);
    }
}
