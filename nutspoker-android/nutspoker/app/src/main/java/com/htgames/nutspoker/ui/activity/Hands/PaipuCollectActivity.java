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
import com.htgames.nutspoker.R;
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
import com.htgames.nutspoker.ui.adapter.hands.CardCollectAdapter;
import com.htgames.nutspoker.util.ToolUtil;
import com.htgames.nutspoker.view.PaipuMoreView;
import com.htgames.nutspoker.view.ResultDataView;
import com.htgames.nutspoker.view.ShareView;
import com.netease.nim.uikit.api.ApiConfig;
import com.netease.nim.uikit.api.ApiConstants;
import com.netease.nim.uikit.api.HttpApi;
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.bean.CommonBeanT;
import com.netease.nim.uikit.bean.NetCardCollectBaseEy;
import com.netease.nim.uikit.bean.NetCardCountEy;
import com.netease.nim.uikit.bean.PaipuEntity;
import com.netease.nim.uikit.common.gson.GsonUtils;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.permission.PermissionUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 牌谱收藏
 */
public class PaipuCollectActivity extends BasePaipuActivity {
    private final static String TAG = "PaipuCollectActivity";
    public static int EACH_PAGE_COUNT_COLECT = 10;//每页包含的条数
    boolean mIsAll = false;
    CardCollectAdapter mCollectAdapter;
    List<NetCardCollectBaseEy> mNetCardCollectEys = new ArrayList<>();

    HandCollectAction mHandCollectAction;

    //    SwipeRefreshLayout mSwipeRefreshLayout;
    boolean isLoadMore = false;
    String lastHandId = "";
    private ListView lv_list;
    //    ListFooterView mListFooterView;
    private ResultDataView mResultDataView;
    ShareView shareView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        from = getIntent().getIntExtra(EXTRA_FROM, FROM_NORMAL);
        mHandCollectAction = new HandCollectAction(PaipuCollectActivity.this, null);
        shareView = new ShareView(this);
        initHead();
        initView();
        initSwipeRefreshLayout();
        initAdapter();
        mResultDataView.showLoading();
        getHandCollectList(null, 0);
        updateHead(0);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            PermissionUtils.requestPermission(this, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandCollectAction != null) {
            mHandCollectAction.onDestroy();
            mHandCollectAction = null;
        }
        if (shareView != null) {
            shareView.onDestroy();
            shareView = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //从服务器拉取最新数据,保持实时同步
        updateCollectCount();
    }

    void updateCollectCount() {
        HttpApi.Builder builder = new HttpApi.Builder()
                .methodGet(ApiConstants.METHOD_CARDCOLLECT_LIST)
                .mapParams(NetWork.getRequestCommonParams())
                .param("count", "1");
        HttpApi.GetNetObservable(builder, null)
                .map(new Func1<String, CommonBeanT<NetCardCountEy>>() {
                    @Override
                    public CommonBeanT<NetCardCountEy> call(String s) {
                        CommonBeanT<NetCardCountEy> ret = null;
                        try {
                            Type type = new TypeToken<CommonBeanT<NetCardCountEy>>() {
                            }.getType();
                            ret = GsonUtils.getGson().fromJson(s, type);
                            return ret;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return ret;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CommonBeanT<NetCardCountEy>>() {
                    @Override
                    public void call(CommonBeanT<NetCardCountEy> cbt) {
                        if (cbt != null) {
                            UserPreferences.getInstance(PaipuCollectActivity.this).setCollectHandNum(cbt.data.count);
                            updateHead(cbt.data.count);
                        }
                    }
                });
    }

    /**
     * @param id   指定最后一手收藏的牌谱ID
     * @param time 0代表第一次获取
     */
    public void getHandCollectList(@Nullable final String id, final long time) {
        //这里改成直接从网络获取最新的，如果网络获取失败，那么从本地数据中取数据
        mHandCollectAction.getCardCollectList(id, new RequestCallback() {
            @Override
            public void onResult(int code, final String result, Throwable var3) {
                if (code == 0) {
                    Observable.just(result)
                            .observeOn(Schedulers.io())
                            .map(new Func1<String, List<NetCardCollectBaseEy>>() {
                                @Override
                                public List<NetCardCollectBaseEy> call(String s) {
                                    try {
                                        JSONObject resultJson = new JSONObject(s);
                                        JSONArray array = resultJson.getJSONArray("data");
                                        List<NetCardCollectBaseEy> listRet = new ArrayList<>();
                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject jsonObject = array.getJSONObject(i);
                                            listRet.add(GsonUtils.getGson().fromJson(jsonObject.toString(), NetCardCollectBaseEy.class));
                                        }
                                        //入库
                                        //存数据库
                                        HandsCollectDBHelper.SetHandsCollectListEx(PaipuCollectActivity.this, listRet);
                                        return listRet;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<List<NetCardCollectBaseEy>>() {
                                @Override
                                public void call(List<NetCardCollectBaseEy> netCardCollectEys) {
                                    if (netCardCollectEys != null) {
                                        int nOldSize = mNetCardCollectEys.size();
                                        mNetCardCollectEys.addAll(netCardCollectEys);
                                        int nNewSize = filterCollectList(mNetCardCollectEys);
                                        if (nNewSize > nOldSize) {
                                            //有变动
                                            mCollectAdapter.notifyDataSetChanged();
                                        }
                                        updateListView();
                                    } else if (time == 0) { //第一次没有获取到数据,清空
                                        mNetCardCollectEys.clear();
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
                getDataFromDB(time);
            }
        });
    }

    void updateListView() {
        if (!mNetCardCollectEys.isEmpty()) {
            mResultDataView.successShow();
            lv_list.setVisibility(View.VISIBLE);
            mCollectAdapter.notifyDataSetChanged();
        } else {
            lv_list.setVisibility(View.GONE);
            mResultDataView.nullDataShow(R.string.paipu_collect_null, R.mipmap.img_paipu_null);
        }
    }

    private void initHead() {
        if (from != BasePaipuActivity.FROM_SESSION_BY_P2P && from != BasePaipuActivity.FROM_SESSION_BY_TEAM) {
            setHeadRightButton(R.string.record_handcard, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PaipuCollectActivity.this, PaipuRecordActivity.class);
                    intent.putExtra(EXTRA_FROM, from);
                    startActivity(intent);
                }
            });
        }
    }

    public void updateHead(int count) {
        if (count > 0) {
            setHeadTitle(getString(R.string.me_column_paipu) + "(" + ToolUtil.Number2Change(this,count) + ")");
        } else {
            setHeadTitle(R.string.me_column_paipu);
        }
    }

    private void initView() {
        paipu_record_and_collect_pulltorefresh_lv = (SwipeRefreshLayout) findViewById(R.id.paipu_record_and_collect_pulltorefresh_lv);
        paipu_record_and_collect_pulltorefresh_lv.setEnabled(false);
        lv_list = (ListView) findViewById(R.id.lv_list);
        lv_list.addFooterView(bottomLoadingView);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getAdapter() == null || position >= parent.getAdapter().getCount()) {
                    return;
                }
                PaipuEntity paipuEntity = new PaipuEntity();
                DataManager.ConvertData((NetCardCollectBaseEy) parent.getItemAtPosition(position), paipuEntity);
                if (from == FROM_NORMAL) {
                    DialogMaker.showProgressDialog(PaipuCollectActivity.this, "", false);
                    String path = DataManager.GetSheetFileOfMy(paipuEntity.handsId, mHandCollectAction, new DataManager.OnDataFinish() {
                        @Override
                        public void onDataFinish(Object data) {
                            DialogMaker.dismissProgressDialog();
                            if (ActivityCompat.checkSelfPermission(PaipuCollectActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                PermissionUtils.requestPermission(PaipuCollectActivity.this, PermissionUtils.CODE_READ_PHONE_STATE, null);
                                return;
                            }
                            if (data != null) {
                                PokerActivity.startGamePlayRecord(PaipuCollectActivity.this, UserPreferences.getInstance(getApplicationContext()).getUserId(), (String) data);
                            }
                        }
                    });
                    if (path != null) {
                        DialogMaker.dismissProgressDialog();
                        if (ActivityCompat.checkSelfPermission(PaipuCollectActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                            PermissionUtils.requestPermission(PaipuCollectActivity.this, PermissionUtils.CODE_READ_PHONE_STATE, null);
                            return;
                        }
                        PokerActivity.startGamePlayRecord(PaipuCollectActivity.this, UserPreferences.getInstance(getApplicationContext()).getUserId(), path);
                    }
                    //mHandCollectAction.openHandPlay(paipuEntity);
                } else if (from == FROM_SHARE) {
                } else if (from == FROM_SESSION_BY_P2P) {
                    Intent intent = new Intent(PaipuCollectActivity.this, P2PMessageActivity.class);
                    intent.putExtra("type", CircleConstant.TYPE_PAIPU);
                    intent.putExtra("data", paipuEntity);
                    startActivity(intent);
                } else if (from == FROM_SESSION_BY_TEAM) {
                    Intent intent = new Intent(PaipuCollectActivity.this, TeamMessageActivity.class);
                    intent.putExtra("type", CircleConstant.TYPE_PAIPU);
                    intent.putExtra("data", paipuEntity);
                    startActivity(intent);
                }
            }
        });
        initResultDataView();
    }

    private void initResultDataView() {
        mResultDataView = (ResultDataView) findViewById(R.id.mResultDataView);
        mResultDataView.setReloadDataCallBack(new ResultDataView.ReloadDataCallBack() {
            @Override
            public void onReloadData() {
                getHandCollectList(null, 0);
            }
        });
        mResultDataView.successShow();
    }

    private void initSwipeRefreshLayout() {
//        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mSwipeRefreshLayout);
//        // 这句话是为了，第一次进入页面的时候显示加载进度条
//        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()));
////        mSwipeRefreshLayout.setColorScheme(R.color.color1, R.color.color2, R.color.color3, R.color.color4);
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                if (!isLoadMore) {
//                    mSwipeRefreshLayout.setRefreshing(true);
//                    getHandCollectList("");
//                }
//            }
//        });
        lv_list.setVisibility(View.GONE);
        //加载更多
        lv_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int lastVisibleItem = 0;
            private int firstVisibleItem = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int listViewCount = lv_list.getCount() - 1;
                if ((mCollectAdapter != null && mCollectAdapter.getCount() >= mHandCollectAction.getCollectHandNum()) || mNetCardCollectEys.isEmpty()) {
                    mIsAll = true;
                }
                if (!mIsAll) {
                    bottomLoadingView.statePre();
                } else {
                    bottomLoadingView.stateAllDataLoadDone(true);
                }
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && (lastVisibleItem == (listViewCount)) && !isLoadMore) {//多加了个bottomloadingbiew，所以listViewCount要加1
                    //已经获取全部数据
                    if (mIsAll) {
                        bottomLoadingView.stateAllDataLoadDone(true);
                        return;
                    }
                    //已经达到最大条目数
                    if ((mCollectAdapter != null && mCollectAdapter.getCount() >= mHandCollectAction.getCollectHandNum()) || mNetCardCollectEys.isEmpty()) {
                        mIsAll = true;
                        return;
                    }
                    isLoadMore = true;
                    int last = mNetCardCollectEys.size() - 1;
                    NetCardCollectBaseEy netCardCollectEy = mNetCardCollectEys.get(last);
                    bottomLoadingView.stateLoading();
                    //获取更多
                    getHandCollectList(netCardCollectEy.id, netCardCollectEy.collect_time);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                mSwipeRefreshLayout.setEnabled(lv_list.getFirstVisiblePosition() == 0);//如果第一个视图在界面中
                lastVisibleItem = lv_list.getLastVisiblePosition();
                firstVisibleItem = lv_list.getFirstVisiblePosition();
            }
        });
    }

    public void initAdapter() {
        if (from == FROM_SESSION_BY_P2P || from == FROM_SESSION_BY_TEAM) {
            mCollectAdapter = new CardCollectAdapter(this, getWindow().getDecorView(), mNetCardCollectEys, mOnMoreListener, mOnCallOverback);
            mCollectAdapter.setIsShowMore(false);
        } else {
            mCollectAdapter = new CardCollectAdapter(this, getWindow().getDecorView(), mNetCardCollectEys, mOnMoreListener, mOnCallOverback);
//            mPaipuCollectAdapter = new PaipuCollectAdapter(getApplicationContext(), paipuCollectList, getWindow().getDecorView(), mOnMoreListener, mOnCallOverback);
        }
        lv_list.setAdapter(mCollectAdapter);
    }

    void getDataFromDB(final long time) {
        //从数据库中获取
        Observable.just(time)
                .observeOn(Schedulers.io())
                .map(new Func1<Long, List<NetCardCollectBaseEy>>() {
                    @Override
                    public List<NetCardCollectBaseEy> call(Long l) {
                        List<NetCardCollectBaseEy> list;
                        if (l == 0) {
                            list = HandsCollectDBHelper.GetHandsCollectListEx(PaipuCollectActivity.this);
                        } else {
                            list = HandsCollectDBHelper.GetHandsCollectListEx(PaipuCollectActivity.this, l);
                        }
                        return list;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<NetCardCollectBaseEy>>() {
                    @Override
                    public void call(List<NetCardCollectBaseEy> netCardCollectEys) {
                        int nOldSize = mNetCardCollectEys.size();
                        mNetCardCollectEys.addAll(netCardCollectEys);
                        int nNewSize = filterCollectList(mNetCardCollectEys);
                        if (time == 0) {
                            updateListView();
                        }
                        if (nNewSize > nOldSize) {
                            mCollectAdapter.notifyDataSetChanged();
                        }
                        if (mIsAll) {
                            bottomLoadingView.stateAllDataLoadDone(true);
                        } else {
                            bottomLoadingView.setVisibility(View.INVISIBLE);
                        }
                        isLoadMore = false;
                    }
                });
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

    PaipuMoreView.OnMoreListener mOnMoreListener = new PaipuMoreView.OnMoreListener() {
        @Override
        public void onSend(int position) {
            PaipuEntity paipuEntity = new PaipuEntity();
            DataManager.ConvertData(mNetCardCollectEys.get(position), paipuEntity);
            RecentContactSelectActivity.start(PaipuCollectActivity.this, CircleConstant.TYPE_PAIPU, paipuEntity, ContactSelectActivity.FROM_PAIPU_COLLECT);
        }

        @Override
        public void onShare(int position) {
            PaipuEntity paipuEntity = new PaipuEntity();
            DataManager.ConvertData(mNetCardCollectEys.get(position), paipuEntity);
            PublishActivity.start(PaipuCollectActivity.this, CircleConstant.TYPE_PAIPU, paipuEntity);
        }

        @Override
        public void onCollect(final int position) {
            final String handsId = mNetCardCollectEys.get(position).hid;
            //取消收藏
            mHandCollectAction.doCancelHandsCollect(handsId, new RequestCallback() {
                @Override
                public void onResult(int code, String result, Throwable var3) {
                    if (code == 0) {
                        PaipuRecordActivity.canceledCollectList.add(mNetCardCollectEys.get(position).hid);//"牌谱收藏"的hid和"牌谱记录"的id是一致的，乱七八糟
                        GameRecordDBHelper.SetNetRecordCollect(PaipuCollectActivity.this, handsId, false);
                        HandsCollectDBHelper.DeleteHandsCollectList(PaipuCollectActivity.this, handsId);
                        if (mNetCardCollectEys.size() > position) {
                            mNetCardCollectEys.remove(position);
                            mCollectAdapter.notifyDataSetChanged();
                        }
                        int collectNum = mHandCollectAction.getCollectHandNum();
                        updateHead(collectNum);
                        if (mNetCardCollectEys.isEmpty()) {
                            if (collectNum <= 0) {
                                updateListView();
                            } else {
                                getHandCollectList(null, 0);
                            }
                        }
                    }
                }
                @Override
                public void onFailed() {
                }
            });
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

    /**
     * 分享牌局到社交软件
     *
     * @param shareMedia
     * @param position
     */
    public void sharePaipuToSocial(SHARE_MEDIA shareMedia, int position, View view) {
        PaipuEntity paipuEntity = new PaipuEntity();
        DataManager.ConvertData(mNetCardCollectEys.get(position), paipuEntity);
        LogUtil.i(TAG, paipuEntity.handsId);
        String shareUrl = ApiConfig.getPaipuShareUrl(paipuEntity.handsId);
        shareView.shareHands(shareMedia, shareUrl ,view);
    }

    /**
     * 过滤超过VIP限制的手牌数量
     *
     * @param list
     */
    int filterCollectList(List<NetCardCollectBaseEy> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        int limit = UserConstant.getMyCollectHandLimit();
        if (limit < list.size()) {
            mIsAll = true;
            list = list.subList(0, limit - 1);
        }
        return list.size();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, null);
    }
}
