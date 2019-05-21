package com.htgames.nutspoker.circle.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.contact.activity.UserProfileActivity;
import com.htgames.nutspoker.circle.adapter.CircleAdapter;
import com.htgames.nutspoker.circle.bean.CircleItem;
import com.htgames.nutspoker.circle.control.CirclePublicCommentControl;
import com.htgames.nutspoker.circle.utils.CommonUtils;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.tool.JsonResolveUtil;
import com.htgames.nutspoker.ui.action.CircleAction;
import com.htgames.nutspoker.ui.activity.Record.RecordListAC;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.view.ListFooterView;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.session.constant.Extras;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 德州圈（朋友圈）
 */
public class CircleActivity extends BaseActivity implements View.OnClickListener , CircleAdapter.CircleListener {
    private final static String TAG = "CircleActivity";
    RecyclerView mDiscoveryRecyclerView;
    CircleAdapter mCircleAdapter;
    LinearLayoutManager layoutManager;
    ArrayList<CircleItem> circleList;
    //
    View headerView = null;
    //新的朋友圈消息
    LinearLayout ll_new_message;
    HeadImageView iv_new_message_userhead;
    TextView tv_new_message_count;
    //
    SwipeRefreshLayout mSwipeRefreshLayout;
    CircleAction mCircleAction;
    //
    private LinearLayout ll_comment_input;
    private EditText edt_comment;
    private Button btn_send;
    private int mScreenHeight;
    private int mEditTextBodyHeight;
    private CirclePublicCommentControl mCirclePublicCommentControl;
    //
    ListFooterView mListFooterView;
    boolean isLoadMore = false;
    String lastCircleId = "";

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            //发表的新分享
            CircleItem circleItem = (CircleItem)intent.getSerializableExtra(Extras.EXTRA_DATA);
            if(circleItem != null){
                circleList.add(0 , circleItem);
                mCircleAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle);
        setHeadTitle(R.string.discovery_circle);
        setHeadRightButton(R.string.share, this);
        mCircleAction = new CircleAction(this , null);
        initView();
        initRecyclerView();
        initSwipeRefreshLayout();
//        initData();
        initRecyclerViewHeader();
        initRecyclerFooterView();
        circleList = new ArrayList<CircleItem>();
        mCircleAdapter = new CircleAdapter(this, circleList , mCircleAction);
        mCircleAdapter.setAvaterClickListener(new CircleAdapter.AvaterClickListener() {
            @Override
            public void click(String account) {
                UserProfileActivity.start(CircleActivity.this , account);
            }
        });
        mCircleAdapter.addHeaderView(headerView);
        mCircleAdapter.addFooterView(mListFooterView);
        mCircleAdapter.setCircleListener(this);
        //
        mCircleAdapter.setCirclePublicCommentControl(mCirclePublicCommentControl);
        //
        mDiscoveryRecyclerView.setAdapter(mCircleAdapter);
        setViewTreeObserver();
        updateNewMessageUI(0);
        getFindList("");
    }

    /**
     * 设置视图监听
     */
    private void setViewTreeObserver() {
        final ViewTreeObserver swipeRefreshLayoutVTO = mSwipeRefreshLayout.getViewTreeObserver();
        swipeRefreshLayoutVTO.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                mSwipeRefreshLayout.getWindowVisibleDisplayFrame(r);
                int screenH = mSwipeRefreshLayout.getRootView().getHeight();
                int keyH = screenH - (r.bottom - r.top);
                if (keyH == ChessApp.mKeyBoardH) {//有变化时才处理，否则会陷入死循环
                    return;
                }
                LogUtil.i(TAG, "keyH = " + keyH + " &r.bottom=" + r.bottom + " &top=" + r.top);
                ChessApp.mKeyBoardH = keyH;
                mScreenHeight = screenH;//应用屏幕的高度
                mEditTextBodyHeight = ll_comment_input.getHeight();
                if (mCirclePublicCommentControl != null && mScreenHeight != r.bottom) {
                    LogUtil.i("CirclePublicComment", "mCirclePublicCommentControl != null");
                    mCirclePublicCommentControl.handleListViewScroll();
                } else {
                    ll_comment_input.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.mSwipeRefreshLayout);
        // 这句话是为了，第一次进入页面的时候显示加载进度条
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()));
//        mSwipeRefreshLayout.setColorScheme(R.color.color1, R.color.color2, R.color.color3, R.color.color4);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isLoadMore){
                    mSwipeRefreshLayout.setRefreshing(true);
                    getFindList("");
                }
            }
        });
    }

    public synchronized void getFindList(final String lastId){
//        HashMap<String, String> paramsMap = new HashMap<String, String>();
//        paramsMap.put("uid", UserPreferences.getInstance(getApplicationContext()).getUserId());
//        TexasClient.api()
//                .getFindList(UserPreferences.getInstance(getApplicationContext()).getUserId())
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<ResponseBody>() {
//                    @Override
//                    public void onCompleted() {
//                        Log.d(TAG, "onCompleted");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.d(TAG, "onError");
//                    }
//
//                    @Override
//                    public void onNext(ResponseBody responseBody) {
//                        try {
//                            Log.d(TAG, responseBody.string());
//                            circleList.clear();
//                            circleList.addAll(JsonResolveUtil.getCircleList(new JSONObject(responseBody.string())));
//                            setCircleList();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });

        mCircleAction.getFindList(lastId , new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                mSwipeRefreshLayout.setRefreshing(false);
                if(code == 0){
                    try {
                        if(TextUtils.isEmpty(lastId)){
                            circleList.clear();
                            circleList.addAll(JsonResolveUtil.getCircleList(new JSONObject(result)));
                            setCircleList();
                        } else{
                            isLoadMore = false;
                            lastCircleId = lastId;
                            setMoreCircleList(JsonResolveUtil.getCircleList(new JSONObject(result)));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else{
                    isLoadMore = false;
                    if(TextUtils.isEmpty(lastId)){

                    }else{
                        getMoreFailure();
                    }
                }
            }

            @Override
            public void onFailed() {
                mSwipeRefreshLayout.setRefreshing(false);
                isLoadMore = false;
                if (TextUtils.isEmpty(lastId)) {

                } else {
                    getMoreFailure();
                }
            }
        });
    }

    public void setCircleList(){
        if(circleList !=  null && circleList.size() != 0){
            if(circleList.size() > 3){
                mCircleAdapter.addFooterView(mListFooterView);
                mListFooterView.showLoad();
            }else{
                mCircleAdapter.removeFooterView(mListFooterView);
            }
            mCircleAdapter.notifyDataSetChanged();
        }else{
            mCircleAdapter.removeFooterView(mListFooterView);
            mCircleAdapter.notifyDataSetChanged();
        }
    }

    private void setMoreCircleList(ArrayList<CircleItem> moreList) {
        if(!moreList.isEmpty()){
            circleList.addAll(moreList);
            mCircleAdapter.addFooterView(mListFooterView);
            mListFooterView.showLoad();
            mCircleAdapter.notifyDataSetChanged();
        }else{
            mCircleAdapter.removeFooterView(mListFooterView);
            mCircleAdapter.notifyDataSetChanged();
        }
    }

    public void getMoreFailure(){
        mListFooterView.showError(getApplicationContext() , 0);
    }

    //更新新消息数量
    public void updateNewMessageUI(int newMessageCount){
        if(newMessageCount > 0){
            ll_new_message.setVisibility(View.VISIBLE);
            iv_new_message_userhead.loadBuddyAvatar("13738090123", 30);
            tv_new_message_count.setText(String.format(getString(R.string.circle_new_message), newMessageCount));
        } else{
            ll_new_message.setVisibility(View.GONE);
        }
    }

    private void initRecyclerViewHeader() {
        //header
        headerView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_circle_header , null);
        HeadImageView iv_userhead = (HeadImageView)headerView.findViewById(R.id.iv_userhead);
        TextView tv_nickname = (TextView)headerView.findViewById(R.id.tv_nickname);
        TextView tv_sign = (TextView)headerView.findViewById(R.id.tv_sign);
        ll_new_message = (LinearLayout)headerView.findViewById(R.id.ll_new_message);
        iv_new_message_userhead = (HeadImageView)headerView.findViewById(R.id.iv_new_message_userhead);
        tv_new_message_count = (TextView)headerView.findViewById(R.id.tv_new_message_count);
        ll_new_message.setOnClickListener(this);
        //
        iv_userhead.loadBuddyAvatar(DemoCache.getAccount() , 80);
        tv_nickname.setText(NimUserInfoCache.getInstance().getUserDisplayName(DemoCache.getAccount()));
        tv_sign.setText(NimUserInfoCache.getInstance().getUserInfo(DemoCache.getAccount()).getSignature());
    }

    private void initRecyclerFooterView() {
        mListFooterView = new ListFooterView(getApplicationContext());
        mListFooterView.setFooterViewCallBack(new ListFooterView.FooterViewCallBack() {
            @Override
            public void onClickFooter() {
                if(circleList != null && circleList.size() != 0){
                    getFindList(circleList.get(circleList.size() - 1).getSid());
                }
            }
        });
    }

    private void initView() {
        ll_comment_input = (LinearLayout) findViewById(R.id.ll_discovery_edit);
        edt_comment = (EditText) findViewById(R.id.edt_discovery_edit);
        btn_send = (Button) findViewById(R.id.btn_comment_send);
//        btn_send.setOnClickListener(this);
    }

    public void initRecyclerView() {
        mDiscoveryRecyclerView = (RecyclerView) findViewById(R.id.mDiscoveryRecyclerView);
//        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mDiscoveryRecyclerView.setLayoutManager(layoutManager);
        mDiscoveryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mDiscoveryRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastVisibleItem = 0;
            private int firstVisibleItem = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mDiscoveryRecyclerView != null && mCircleAdapter != null
                        && (lastVisibleItem + 1 == mCircleAdapter.getItemCount()) && !isLoadMore) {
                    //
                    if(circleList != null && circleList.size() != 0 && !mSwipeRefreshLayout.isRefreshing()){
                        LogUtil.i("Circle" , "加载更多");
                        isLoadMore = true;
                        getFindList(circleList.get(circleList.size() - 1).getSid());
                    }
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mSwipeRefreshLayout.setEnabled(layoutManager.findFirstCompletelyVisibleItemPosition() == 0);//如果第一个视图在界面中
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
//                if (ll_discovery_edit.getVisibility() == View.VISIBLE) {
//                    ll_discovery_edit.setVisibility(View.GONE);
//                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(edt_discovery_edit.getWindowToken(), 0);
//                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        mDiscoveryRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (ll_comment_input.getVisibility() == View.VISIBLE) {
                    ll_comment_input.setVisibility(View.GONE);
                    CommonUtils.hideSoftInput(CircleActivity.this, edt_comment);
                    return true;
                }
                return false;
            }
        });
        //
        mCirclePublicCommentControl = new CirclePublicCommentControl(this , ll_comment_input, edt_comment, btn_send , mCircleAction);
        mCirclePublicCommentControl.setRecyclerView(mDiscoveryRecyclerView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_head_right:
//                Intent intent = new Intent(CircleActivity.this, RecordListActivity.class);
//                intent.putExtra(RecordListActivity.EXTRA_FROM, RecordListActivity.FROM_SHARE);
//                startActivity(intent);
                showPublishChoiceDialog();
                break;
        }
    }

    CustomAlertDialog publishChoiceDialog;

    public void showPublishChoiceDialog() {
        if(publishChoiceDialog == null){
            publishChoiceDialog = new CustomAlertDialog(this);
            publishChoiceDialog.addItem(getString(R.string.circle_publish_paiju), new CustomAlertDialog.onSeparateItemClickListener() {
                @Override
                public void onClick() {
                    RecordListAC.StartActivity(CircleActivity.this, RecordListAC.FROM_SHARE);
                }
            });
//            publishChoiceDialog.addItem(getString(R.string.circle_publish_paipu), new CustomAlertDialog
//                    .onSeparateItemClickListener() {
//                @Override
//                public void onClick() {
//
//                }
//            });
        }
        publishChoiceDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCircleAction != null){
            mCircleAction.onDestroy();
            mCircleAction = null;
        }
        if(mCircleAdapter != null){
            mCircleAdapter = null;
        }
        mDiscoveryRecyclerView.setOnScrollListener(null);
    }

    public int getScreenHeight(){
        return mScreenHeight;
    }

    public int getEditTextBodyHeight(){
        return mEditTextBodyHeight;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if(ll_comment_input != null && ll_comment_input.getVisibility() == View.VISIBLE){
                ll_comment_input.setVisibility(View.GONE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void doLike(final int position, final boolean like) {
//        final CircleItem circleItem = circleList.get(position);
        mCircleAction.doLike(circleList.get(position).getSid(), !circleList.get(position).isLiked(),new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if(code == 0){
                    if(!circleList.get(position).isLiked()){
                        //点赞
                        circleList.get(position).setLikeCount(circleList.get(position).getLikeCount() + 1);
                        circleList.get(position).setIsLiked(true);
                    } else {
                        //取消赞
                        circleList.get(position).setLikeCount(circleList.get(position).getLikeCount() - 1);
                        circleList.get(position).setIsLiked(false);
                    }
                    mCircleAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailed() {

            }
        });
    }
}
