package com.htgames.nutspoker.ui.fragment.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.*;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.data.CompareData;
import com.htgames.nutspoker.hotupdate.HotUpdateHelper;
import com.htgames.nutspoker.hotupdate.interfaces.CheckHotUpdateCallback;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.net.RequestTimeLimit;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalyticsEventConstants;
import com.htgames.nutspoker.tool.NetworkTools;
import com.htgames.nutspoker.tool.json.GameJsonTools;
import com.htgames.nutspoker.ui.action.GameAction;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.htgames.nutspoker.ui.adapter.RecentGameAdap;
import com.htgames.nutspoker.ui.base.BaseFragment;
import com.htgames.nutspoker.view.SystemTipView;
import com.htgames.nutspoker.view.TouchableRecyclerView;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.api.ApiCode;
import com.netease.nim.uikit.api.ApiConfig;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.preference.PaijuListPref;
import com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.interfaces.IClickPayload;
import com.netease.nim.uikit.permission.PermissionUtils;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by 周智慧 on 16/12/8.
 * copy from GameFragment.java
 */

public class GameFragmentNew extends BaseFragment implements View.OnClickListener, IClickPayload {
    @Override
    public void onDelete(int position, Object object) {
    }
    @Override
    public void onClick(final int position, final Object object) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(getActivity(), PermissionUtils.CODE_READ_PHONE_STATE, null);
            return;
        }
        if (position < 0 || position >= recentGameList.size()|| !(object instanceof GameEntity)) {
            return;
        }
        needGameUpdate(new CheckHotUpdateCallback() {
            @Override
            public void notUpdate() {
                GameEntity gameEntity = (GameEntity) object;//recentGameList.get(position);//没有执行onbind，position是老的位置，从数据源中取数据是错的
                for (int i = 0; i < recentGameList.size(); i++) {//这个for循环必须得有，否则排序不生效
                    GameEntity item = recentGameList.get(i);
                    if (item.gid.equals(gameEntity.gid)) {
                        recentGameList.set(i, gameEntity);
                        break;
                    }
                }
                int gameMode = gameEntity.gameMode;
                if (gameEntity.isInvited == GameConstants.GAME_ISINVITED) {
                    gameEntity.isInvited = (0);
                }
                doGameJoinByGameStatus(UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_RECENT, gameEntity.code);
            }
        });
    }
    @Override
    public void onLongClick(int position, Object object) {
        if (position < 0 || position >= recentGameList.size()|| !(object instanceof GameEntity)) {
            return;
        }
        GameEntity gameEntity = (GameEntity) object;//recentGameList.get(position);//没有执行onbind，position是老的位置，从数据源中取数据是错的
        for (int i = 0; i < recentGameList.size(); i++) {//这个for循环必须得有，否则排序不生效
            GameEntity item = recentGameList.get(i);
            if (item.gid.equals(gameEntity.gid)) {
                recentGameList.set(i, gameEntity);
                break;
            }
        }
        showLongClickMenu(gameEntity);
    }
    private final static String TAG = GameFragmentNew.class.getSimpleName();
    View view;
    TouchableRecyclerView lv_recent;
    ArrayList<GameEntity> recentGameList = new ArrayList<GameEntity>();
    TextView tv_recnet_game_null;
    RecentGameAdap mAdapter;
    SystemTipView mSystemTipView;
    ArrayList<String> stickList = new ArrayList<String>();//置顶列表
    public static final int TYPE_REQUEST_DATA = 0;
    public static final int TYPE_UPDATE_DATA = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TYPE_REQUEST_DATA:
                    RequestTimeLimit.lastGetRecentGameTime = (DemoCache.getCurrentServerSecondTime());
                    sortRecentGameList(GameJsonTools.getHistoryGameList((JSONObject) msg.obj));//网络数据
                    setHistoryList();//网络数据
                    break;
                case TYPE_UPDATE_DATA:
                    int firstVisiblePosition = ((LinearLayoutManager) lv_recent.getLayoutManager()).findFirstVisibleItemPosition();
                    //取出Result
                    DiffUtil.DiffResult diffResult = (DiffUtil.DiffResult) msg.obj;
                    diffResult.dispatchUpdatesTo(mAdapter);
                    mAdapter.updateData(recentGameList);//别忘了将新数据给Adapter
                    lv_recent.scrollToPosition(firstVisiblePosition);
                    break;
            }
        }
    };
    public static GameFragmentNew newInstance() {
        GameFragmentNew mInstance = new GameFragmentNew();
        return mInstance;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.i("GameFragmentNew onActivityCreated");
        updateCount();//onActivityCreated
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            updateCount();//onHiddenChanged
            setNetworkStatus();
            getAmount();
        } else {
            if (mAdapter != null) {
                    mAdapter.setOmahaAnimationDone(true);
            }
        }
        LogUtil.i("GameFragmentNew onHiddenChanged hidden: " + hidden);
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        LogUtil.i("GameFragmentNew onVisible");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFragmentName("GameFragmentNew");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_game, container, false);
        setHeadTitle(view, CacheConstant.debugBuildType ? (ApiConfig.isTestVersion ? R.string.head_title_game_test_server : R.string.head_title_game_common) : R.string.head_title_game);//ApiConfig.isTestVersion ? R.string.head_title_game_test_server : R.string.head_title_game);
        initHead();
        initView();
        setNetworkStatus();
        initAppBarLayout();
        return view;
    }

    private void initHead() {
        TextView btn_head_back = ((TextView) view.findViewById(R.id.btn_head_back));
        btn_head_back.setVisibility(View.INVISIBLE);
        btn_head_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showJoinCodeView(false);
                }
            }
        });
    }

    /**
     * 通知有新的游戏牌局，刷新列表
     * @param gid
     */
    public synchronized void notifyNewGame(String gid) {
        //通知有新游戏，新邀请(原列表中不存在)，提示刷新列表
        boolean isNewGame = true;
        if (!TextUtils.isEmpty(gid)) {
            for (GameEntity gameEntity : recentGameList) {
                if (gid.equals(gameEntity.gid)) {
                    isNewGame = false;
                    break;
                }
            }
        }
        if (isNewGame) {
            RequestTimeLimit.lastGetRecentGameTime = (0);
            getRecentGameList(false);//notifyNewGame
        }
    }

    public void notifyCancelGame(String gid) {
        if (!TextUtils.isEmpty(gid)) {
            for (int i = 0; i < recentGameList.size(); i++) {
                GameEntity gameEntity = recentGameList.get(i);
                if (gid.equals(gameEntity.gid)) {
                    recentGameList.remove(gameEntity);
                    setHistoryList();//notifyCancelGame
                    break;
                }
            }
        }
    }

    Map<String , Integer> unreadMap = new HashMap<String , Integer>();

    public void setRecentContact(List<RecentContact> messages) {
        boolean isUpdated = false;
        if (unreadMap != null) {
            for (RecentContact msg : messages) {
                if (msg.getSessionType() == SessionTypeEnum.Team && unreadMap.containsKey(msg.getContactId())) {
                    //有消息，未读
                    unreadMap.put(msg.getContactId(), msg.getUnreadCount());
                    for (GameEntity gameEntity : recentGameList) {
                        if (gameEntity.tid.equals(msg.getContactId())) {
                            gameEntity.unReadMsgCount = (msg.getUnreadCount());
                            break;
                        }
                    }
                    isUpdated = true;
                }
            }
            if (isUpdated) {
                updateDataSet();//setRecentContact
            }
        }
    }

    public void setNetworkStatus() {
        if (view == null) {
            return;
        }
        if (NetworkTools.isNetConnect(getActivity().getApplicationContext())) {
            if (mSystemTipView == null) {
                return;
            } else {
                mSystemTipView.setVisibility(View.GONE);
            }
        } else {
            if (mSystemTipView == null) {
                ((ViewStub) view.findViewById(R.id.view_stub_network_tip)).inflate();
                mSystemTipView = (SystemTipView) view.findViewById(R.id.mSystemTipView);//默认是visible
                mSystemTipView.setOnClickListener(this);
            } else {
                mSystemTipView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void afterRequestData(final JSONObject response) {
        Message message = mHandler.obtainMessage(TYPE_REQUEST_DATA);
        message.obj = response;
        message.sendToTarget();
    }

    private synchronized void getRecentGameList(boolean isLimit) {
        long currentTime = DemoCache.getCurrentServerSecondTime();
        long lastGetTime = RequestTimeLimit.lastGetRecentGameTime;
        if (isLimit) {
            if ((currentTime - lastGetTime) < RequestTimeLimit.GET_GAME_RECENT_TIME_LIMIT) {
                LogUtil.d(GameAction.TAG, "获取RecentGame时间未到");
                return;
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (PaijuListPref.firstLaunchMainList) {
                    PaijuListPref.firstLaunchMainList = false;
                    String jsonStr = PaijuListPref.getInstance().getMainList();
                    if (!StringUtil.isSpaceOrZero(jsonStr)) {
                        try {
                            afterRequestData(new JSONObject(jsonStr));//本地缓存
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                RequestTimeLimit.lastGetRecentGameTime = (DemoCache.getCurrentServerSecondTime());
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).mGameAction.getRecentGameList(new GameRequestCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            afterRequestData(response);//网络数据
                        }
                        @Override
                        public void onFailed(int code, JSONObject response) {
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 处理最近游戏列表数据
     * @param gameList
     */
    public void sortRecentGameList(ArrayList<GameEntity> gameList){
        if (gameList == null || gameList.size() == 0) {
            recentGameList.clear();
            return;
        }
        for(GameEntity gameEntity :gameList){
            if(isStickTid(gameEntity.gid)){
                gameEntity.tag = (GameConstants.GAME_RECENT_TAG_STICKY);
            }
        }
        Collections.sort(gameList, CompareData.sRecentComp);
        recentGameList.clear();
        recentGameList.addAll(gameList);
    }

    public void setHistoryList() {
        tv_recnet_game_null.setVisibility(recentGameList == null || recentGameList.size() == 0 ? View.VISIBLE : View.GONE);
        if (recentGameList == null || recentGameList.size() == 0) {
        } else {
            for(GameEntity gameEntity : recentGameList){
                if(unreadMap.containsKey(gameEntity.tid)){
                    gameEntity.unReadMsgCount = (unreadMap.get(gameEntity.tid));
                }
            }
        }
        updateDataSet();//setHistoryList
    }

    private void initView() {
        mAdapter = new RecentGameAdap(recentGameList, this);
        tv_recnet_game_null = (TextView) view.findViewById(R.id.tv_recnet_game_null);
        quick_create_bg = (FrameLayout) view.findViewById(R.id.quick_create_bg);
        quick_create_bg.setOnClickListener(this);
        quick_join_bg = (FrameLayout) view.findViewById(R.id.quick_join_bg);
        quick_join_bg.setOnClickListener(this);
        lv_recent = (TouchableRecyclerView) view.findViewById(R.id.lv_recent_new);
        lv_recent.setHasFixedSize(true);
        lv_recent.setAdapter(mAdapter);
    }

    private void updateDataSet() {//使用DiffUtil优化
        if (mAdapter == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                //放在子线程中计算DiffResult
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new RecentGameAdap.DiffCallback(mAdapter.getMDatas(), recentGameList), true);
                Message message = mHandler.obtainMessage(TYPE_UPDATE_DATA);
                message.obj = diffResult;//obj存放DiffResult
                message.sendToTarget();
            }
        }).start();
    }

    private void showLongClickMenu(final GameEntity gameEntity) {
        CustomAlertDialog alertDialog = new CustomAlertDialog(getActivity());
        alertDialog.setTitle(gameEntity.name);
        String title = (gameEntity.tag == GameConstants.GAME_RECENT_TAG_STICKY ? getString(R.string.recent_game_clear_on_top) : getString(R.string.recent_game_on_top));
        alertDialog.addItem(title, new CustomAlertDialog.onSeparateItemClickListener() {
            @Override
            public void onClick() {
                if (gameEntity.tag == GameConstants.GAME_RECENT_TAG_STICKY) {
                    gameEntity.tag = (GameConstants.GAME_RECENT_TAG_NORMAL);
                    updateStickList(gameEntity.gid , false);//取消置顶
                } else {
                    gameEntity.tag = (GameConstants.GAME_RECENT_TAG_STICKY);
                    updateStickList(gameEntity.gid, true);//置顶
                }
                Collections.sort(recentGameList, CompareData.sRecentComp);
                updateDataSet();//置顶和取消置顶
            }
        });
        if(gameEntity.isInvited == GameConstants.GAME_ISINVITED) {
            //邀请的牌局，可以忽略
            alertDialog.addItem(getString(R.string.recent_game_ignore), new CustomAlertDialog.onSeparateItemClickListener() {
                @Override
                public void onClick() {
                    if (getActivity() != null && getActivity() instanceof MainActivity) {
                        ((MainActivity)getActivity()).mGameAction.doIgnoreGameInvite(gameEntity.code);
                    }
                    gameEntity.isInvited = (0);
                    Collections.sort(recentGameList, CompareData.sRecentComp);
                    updateDataSet();//isInvited
                }
            });
        }
        alertDialog.show();
    }

    /**
     * 更新置顶列表
     * @param gid 牌局ID
     * @param isStick
     */
    public void updateStickList(String gid , boolean isStick) {
        if(stickList == null){
            return;
        }
        if(isStick){
            if(!stickList.contains(gid)){
                stickList.add(gid);
            }
        }else{
            //置顶列表中移出
            if(stickList.contains(gid)){
                stickList.remove(gid);
            }
        }
    }

    /**
     * 是否是置顶牌局
     * @param gid
     * @return
     */
    public boolean isStickTid(String gid){
        if(stickList != null && stickList.contains(gid)){
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(TAG, "gamefragment onresume: 启动速度" + System.currentTimeMillis());
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                LogUtil.i("zzh", " gamefragment  queueIdle begin 启动速度:" + (System.currentTimeMillis()));//310-350很耗时
                updateCount();//onResume  queueIdle
                return false;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.i("GameFragmentNew onPause onPause: ");
    }

    public void updateCount() {
        getRecentGameList(true);//onactivitycreated  onhiddenchanged onresume
    }

    public void getAmount(){
        if(getActivity() != null && getActivity() instanceof MainActivity){
            ((MainActivity)getActivity()).getAmount();
        }
    }

    /**
     * 获取对应的CODE的牌局状态
     */
    public void doGameJoinByGameStatus(final String joinWay ,final String code) {
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(getContext(), getString(R.string.game_join_code_notnull), Toast.LENGTH_SHORT).show();
            return;
        }
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).mGameAction.joinGame(joinWay, code, new GameRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    joinGameByCodeResult(true);
                }

                @Override
                public void onFailed(int resultCode, JSONObject response) {
                    if (resultCode == ApiCode.CODE_GAME_NONE_EXISTENT || resultCode == ApiCode.CODE_MATCH_CHECKIN_FAILURE_CHANNEL_NOT_FOUND) {
                        //牌局不存在
                        for (int i = 0; i  < recentGameList.size(); i++) {
                            GameEntity gameEntity = recentGameList.get(i);
                            if (gameEntity.code.equals(code)) {
                                recentGameList.remove(gameEntity);
                                setHistoryList();//点击不存在的牌局
                                break;
                            }
                        }
                        if (joinWay == UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_CODE) {
                            joinGameByCodeResult(false);
                            showJoinFailureByCodeDiolag(resultCode);
                            return;
                        }
                    }
                }
            });
        }
    }

    EasyAlertDialog joinFailureByCodeDiolag;
    public void showJoinFailureByCodeDiolag(int code) {
        String message = getString(ApiCode.getHttpResultPromptResId(code));
        joinFailureByCodeDiolag = EasyAlertDialogHelper.createOneButtonDiolag(getActivity(), null,
                message, getString(R.string.ok), false, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
        joinFailureByCodeDiolag.show();
    }

    //通过验证码加入成功，清空验证码
    public void joinGameByCodeResult(boolean success) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).joinGameByCodeResult(success);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mSystemTipView:
                if(getActivity() instanceof MainActivity){
                    ((MainActivity) getActivity()).intentSystemSettings();
                }
                break;
            case R.id.quick_create_bg:
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).tryCreateGame();
                }
                break;
            case R.id.quick_join_bg:
                needGameUpdate(new CheckHotUpdateCallback() {
                    @Override
                    public void notUpdate() {
                        //显示验证码View
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).showJoinCodeView(true);
                        }
                    }
                });
                break;
        }
    }

    public void needGameUpdate(CheckHotUpdateCallback callback) {
        if(HotUpdateHelper.isGameUpdateIng()) {
            return;
        }
        if (getActivity() instanceof MainActivity && HotUpdateHelper.isNeedToCheckVersion()) {
            ((MainActivity) getActivity()).checkGameVersion(callback);
        } else{
            callback.notUpdate();
        }
    }

    public void showBackTV(boolean isCodeviewShow) {
        if (view == null) {
            return;
        }
        view.findViewById(R.id.btn_head_back).setVisibility(isCodeviewShow ? View.VISIBLE : View.INVISIBLE);
    }

    AppBarLayout app_bar_layout;
    FrameLayout quick_create_bg;
    FrameLayout quick_join_bg;
    ViewGroup two_btn_container;
    View iv_create_icon;
    View iv_join_icon;
    TextView tv_create_top;
    TextView tv_join_top;
    TextView tv_create_bottom;
    TextView tv_join_bottom;
    float mOriginalHeight;
    float mMintHeight;
    float mTotalMove;
    int mPaddings;
    int mFinalTopPadding;

    int mOriginalTopLeftRightPadding;//初始值20dp最终值5dp
    int mFinalTopLeftRightPadding;//初始值20dp最终值5dp

    int mOriginalTopTxtPaddingLeft;//初始值0dp最终值30dp
    int mFinalTopTxtPaddingLeft;//初始值0dp最终值30dp

    int mOriginalImgPosY;//初始值不确定最终值3dp
    int mFinalImgPosY;//初始值不确定最终值3dp
    //两个按钮的初始宽度
    float mOriginalBtnWidth;
    float mFinalBtnWidth;
    //默认图片的高度51dp
    float imageHeight;
    AppBarLayout.OnOffsetChangedListener offsetChangedListener;
    private void initAppBarLayout() {
        mPaddings = ScreenUtil.dp2px(getActivity(), 8);
        mOriginalBtnWidth = ScreenUtil.screenMin - mPaddings * 2;
        mFinalBtnWidth = (ScreenUtil.screenMin - mPaddings * 3) / 2;
        mFinalTopPadding = ScreenUtil.dp2px(getActivity(), 91);

        mOriginalTopLeftRightPadding = ScreenUtil.dp2px(getActivity(), 20);
        mFinalTopLeftRightPadding = ScreenUtil.dp2px(getActivity(), 2);

        mOriginalTopTxtPaddingLeft = 0;
        mFinalTopTxtPaddingLeft = ScreenUtil.dp2px(getActivity(), 50);

        mOriginalImgPosY = 0;
        mFinalImgPosY = ScreenUtil.dp2px(getActivity(), 1);

        mOriginalHeight = ScreenUtil.dp2px(getActivity(), 174);
        mMintHeight = ScreenUtil.dp2px(getActivity(), 91);
        imageHeight = ScreenUtil.dp2px(getActivity(), 51);
        mTotalMove = mOriginalHeight - mMintHeight;//总共移动83dp
        app_bar_layout = (AppBarLayout) view.findViewById(R.id.app_bar_layout);
        two_btn_container = (ViewGroup) view.findViewById(R.id.two_btn_container);
        iv_create_icon = view.findViewById(R.id.iv_create_icon);
        iv_join_icon = view.findViewById(R.id.iv_join_icon);
        tv_create_top = (TextView) view.findViewById(R.id.tv_create_top);
        tv_join_top = (TextView) view.findViewById(R.id.tv_join_top);
        tv_create_bottom = (TextView) view.findViewById(R.id.tv_create_bottom);
        tv_join_bottom = (TextView) view.findViewById(R.id.tv_join_bottom);
        offsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float percentage = Math.abs(verticalOffset / mTotalMove);//已经移动的百分比例
                if (percentage > 1) {
                    percentage = 1;
                }
                two_btn_container.setPadding(mPaddings, (int) (mPaddings + (mFinalTopPadding - mPaddings) * percentage), mPaddings, mPaddings);
                //下面对两个按钮操作
                int currentWidth = (int) (mOriginalBtnWidth + (mFinalBtnWidth - mOriginalBtnWidth) * percentage);
                int currentPaddingLeftRight = (int) (mOriginalTopLeftRightPadding + (mFinalTopLeftRightPadding - mOriginalTopLeftRightPadding) * percentage);
                int currentTopTxtPaddingLeft = (int) (mOriginalTopTxtPaddingLeft + (mFinalTopTxtPaddingLeft - mOriginalTopTxtPaddingLeft) * percentage);
                float imgScale = 1 + (0.8f - 1) * percentage;
                float originalImgPosY = (quick_create_bg.getHeight() - iv_create_icon.getHeight()) / 2f;
                if (originalImgPosY <= 0) {
                    originalImgPosY = (ScreenUtil.dp2px(getActivity(), 75f) - imageHeight) / 2f;
                }
                float currentImgY = originalImgPosY + (mFinalImgPosY - originalImgPosY) * percentage;
                float currentScaleTop = 1 + (0.95f - 1) * percentage;
                float currentScaleBottom = 1 + (0.85f - 1) * percentage;
                //创建按钮
                FrameLayout.LayoutParams lpCreate = (FrameLayout.LayoutParams) quick_create_bg.getLayoutParams();
                lpCreate.width = currentWidth;
                quick_create_bg.setPadding(currentPaddingLeftRight, 0, currentPaddingLeftRight, 0);
                iv_create_icon.setScaleX(imgScale);
                iv_create_icon.setScaleY(imgScale);
                iv_create_icon.setY(currentImgY);
                tv_create_top.setPadding(currentTopTxtPaddingLeft, tv_create_top.getPaddingTop(), 0, 0);
                tv_create_top.setScaleX(currentScaleTop);
                tv_create_top.setScaleY(currentScaleTop);
                tv_create_bottom.setScaleX(currentScaleBottom);
                tv_create_bottom.setScaleY(currentScaleBottom);
                //加入按钮
                FrameLayout.LayoutParams lpJoin = (FrameLayout.LayoutParams) quick_join_bg.getLayoutParams();
                lpJoin.width = currentWidth;
                quick_join_bg.setPadding(currentPaddingLeftRight, 0, currentPaddingLeftRight, 0);
                iv_join_icon.setScaleX(imgScale);
                iv_join_icon.setScaleY(imgScale);
                iv_join_icon.setY(currentImgY);
                tv_join_top.setPadding(currentTopTxtPaddingLeft, tv_join_top.getPaddingTop(), 0, 0);
                tv_join_top.setScaleX(currentScaleTop);
                tv_join_top.setScaleY(currentScaleTop);
                tv_join_bottom.setScaleX(currentScaleBottom);
                tv_join_bottom.setScaleY(currentScaleBottom);
            }
        };
        app_bar_layout.addOnOffsetChangedListener(offsetChangedListener);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            lv_recent.setNestedScrollingEnabled(true);
//        } else {
////            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) app_bar_layout.getLayoutParams();
////            params.setBehavior(null);
////            app_bar_layout.setMinimumHeight(0);
//
//            //below two lines code has effections. While we has used Recyclerview replaced ListView, so we can annotation those two lines codes
//            AppBarLayout.LayoutParams paramsContainer = (AppBarLayout.LayoutParams) two_btn_container.getLayoutParams();
//            paramsContainer.setScrollFlags(0);//In order to clear flags
//
////            two_btn_container.setMinimumHeight(0);
//        }
    }

    @Override
    public void onDestroy() {
        if(stickList != null){
            stickList.clear();
            stickList = null;
        }
        app_bar_layout.removeOnOffsetChangedListener(offsetChangedListener);
        super.onDestroy();
    }
}
