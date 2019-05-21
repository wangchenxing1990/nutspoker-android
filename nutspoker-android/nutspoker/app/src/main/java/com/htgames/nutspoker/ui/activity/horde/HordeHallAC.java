package com.htgames.nutspoker.ui.activity.horde;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.game.match.adapter.MatchPagerAdapter;
import com.htgames.nutspoker.game.match.view.PagerSlidingTabStrip;
import com.htgames.nutspoker.hotupdate.HotUpdateAction;
import com.htgames.nutspoker.hotupdate.HotUpdateHelper;
import com.htgames.nutspoker.hotupdate.interfaces.CheckHotUpdateCallback;
import com.htgames.nutspoker.hotupdate.model.UpdateFileEntity;
import com.htgames.nutspoker.hotupdate.tool.HotUpdateItem;
import com.htgames.nutspoker.hotupdate.tool.HotUpdateManager;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.interfaces.ICheckGameVersion;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalyticsEventConstants;
import com.htgames.nutspoker.ui.action.GameAction;
import com.htgames.nutspoker.ui.action.HordeAction;
import com.htgames.nutspoker.ui.activity.horde.util.HordeUpdateManager;
import com.htgames.nutspoker.ui.activity.horde.util.UpdateItem;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.netease.nim.uikit.api.ApiCode;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.entity.HordeEntity;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.permission.PermissionUtils;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 周智慧 on 17/3/21.
 */

public class HordeHallAC extends BaseActivity implements ICheckGameVersion {
    public String teamId;
    boolean isMeClubCreator = false;
    boolean isMeClubManager = false;
    public static String TAG = HordeHallAC.class.getSimpleName();
    public HordeEntity mHordeEntity;
    public HordeAction mHordeAction;
    @BindView(R.id.horde_hall_name) TextView horde_hall_name;
    @BindView(R.id.horde_hall_vid) TextView horde_hall_vid;
    @BindView(R.id.horde_hall_notice) TextView horde_hall_notice;
    @BindView(R.id.horde_hall_tabs) PagerSlidingTabStrip horde_hall_tabs;
    @BindView(R.id.appbar_layout) AppBarLayout appbar_layout;
    @BindView(R.id.viewpager_horde_hall) ViewPager viewpager_horde_hall;
    @BindView(R.id.ll_scale_inner) View ll_scale_inner;
    @BindView(R.id.iv_horde_icon) View iv_horde_icon;
    List<Fragment> fragmentList = new ArrayList<>();
    HordePaijuFragment mPaijuFragment;
    HordeRecordFragment mRecordFragment;
    MatchPagerAdapter mMttPagerAdapter;
    public static void start(Activity activity, HordeEntity hordeEntity, boolean isCreator, boolean isManager, String tid) {
        Intent intent = new Intent(activity, HordeHallAC.class);
        intent.putExtra(Extras.EXTRA_CUSTOMIZATION, hordeEntity);
        intent.putExtra(Extras.EXTRA_TEAM_ID, tid);
        intent.putExtra(Extras.EXTRA_GAME_IS_CREATOR, isCreator);//是否是俱乐部的创建者
        intent.putExtra(Extras.EXTRA_GAME_CREATE_IS_CLUB_MANAGER, isManager);//是否是俱乐部的管理员
        activity.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mHordeEntity = (HordeEntity) getIntent().getSerializableExtra(Extras.EXTRA_CUSTOMIZATION);
        isMeClubCreator = getIntent().getBooleanExtra(Extras.EXTRA_GAME_IS_CREATOR, false);
        isMeClubManager = getIntent().getBooleanExtra(Extras.EXTRA_GAME_CREATE_IS_CLUB_MANAGER, false);
        teamId = getIntent().getStringExtra(Extras.EXTRA_TEAM_ID);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horde_hall);
        mHordeAction = new HordeAction(this, null);
        mUnbinder = ButterKnife.bind(this);
        initHead();
        initView();
        initFragments();
        registerObservers(true);
        mHotUpdateAction = new HotUpdateAction(this , null);
        mHotUpdateAction.onCreate();
        mGameAction = new GameAction(this , null);
        if (mHordeEntity.money <= 0) {//说明是从搜索页面进来的，这个时候不知道创建部落牌局的钻石花费，请求接口
            getCostlist();
        }
        mOriginalHeight = ScreenUtil.dp2px(HordeHallAC.this, 130);
        mMintHeight = ScreenUtil.dp2px(HordeHallAC.this, 80);
        mOriginalPaddingTop = ScreenUtil.dp2px(HordeHallAC.this, 20);
        mFinalPaddingTop = ScreenUtil.dp2px(HordeHallAC.this, 45);
        mHeadHeight = getResources().getDimension(R.dimen.action_bar_height);
        appbar_layout.addOnOffsetChangedListener(mOffsetChangedListener);
    }

    float mOriginalHeight;
    float mMintHeight;
    float mOriginalPaddingTop;//从20dp渐变到45dp
    float mFinalPaddingTop;//从20dp渐变到45dp
    float mHeadHeight;//头部48dp
    AppBarLayout.OnOffsetChangedListener mOffsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            float percentage = Math.abs(verticalOffset / (mOriginalHeight - mMintHeight + mHeadHeight));//已经移动的百分比例
            if (percentage > 1) {
                percentage = 1;
            }
            int currentPaddingTop = (int) (mOriginalPaddingTop + (mFinalPaddingTop - mOriginalPaddingTop) * percentage);
            float scale = 1 + (0.6f - 1) * percentage;
            ll_scale_inner.setY(currentPaddingTop);
            iv_horde_icon.setScaleX(scale);
            iv_horde_icon.setScaleY(scale);
            horde_hall_name.setScaleX(scale);
            horde_hall_name.setScaleY(scale);
            horde_hall_vid.setScaleX(scale);
            horde_hall_vid.setScaleY(scale);
        }
    };

    private void getCostlist() {
        mHordeAction.getCostList(teamId, new GameRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (response == null || response.optJSONArray("data") == null || response.optJSONArray("data").length() <= 0) {
                    return;
                }
                JSONArray dataArray = response.optJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++) {
                    HordeEntity hordeEntity = new HordeEntity();
                    JSONObject item = dataArray.optJSONObject(i);
                    hordeEntity.name = item.optString("name");
                    hordeEntity.horde_id = item.optString("horde_id");
                    hordeEntity.money = item.optInt("money");
                    if (hordeEntity.horde_id.equals(mHordeEntity.horde_id)) {
                        mHordeEntity.money = hordeEntity.money;
                        break;
                    }
                }
            }
            @Override
            public void onFailed(int code, JSONObject response) {
            }
        });
    }

    private void initHead() {
        setHeadTitle(R.string.text_horde);
        Drawable rightHeadButtonDrawable = getResources().getDrawable(R.mipmap.icon_chatroom_msg_member);
        rightHeadButtonDrawable.setBounds(0, 0, rightHeadButtonDrawable.getIntrinsicWidth(), rightHeadButtonDrawable.getIntrinsicHeight());
        TextView rightHeadButton = (TextView) findViewById(com.htgames.nutspoker.R.id.tv_head_right);
        rightHeadButton.setCompoundDrawables(rightHeadButtonDrawable, null, null, null);
        rightHeadButton.setText("");
        rightHeadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HordeDetailAC.startByDetail(HordeHallAC.this, mHordeEntity, isMeClubCreator, isMeClubManager, teamId);
            }
        });
    }

    private int horde_hall_name_width = 0;
    private void initView() {
        horde_hall_name.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                horde_hall_name_width = horde_hall_name.getWidth();
                LogUtil.i(TAG, "onPreDraw" + horde_hall_name_width);
                if (horde_hall_name_width > 0) {
                    horde_hall_name.getViewTreeObserver().removeOnPreDrawListener(this);
                    setHordeNameTextSize();
                }
                return true;
            }
        });
        setHordeNameTextSize();
        horde_hall_vid.setText(mHordeEntity == null ? "" : ("ID：" + mHordeEntity.horde_vid));
        horde_hall_notice.setText("");
    }

    private void initFragments() {
        mPaijuFragment = HordePaijuFragment.newIntance();
        mRecordFragment = HordeRecordFragment.newIntance();
        fragmentList.add(mPaijuFragment);
        fragmentList.add(mRecordFragment);
        ArrayList titleList = new ArrayList<>();
        titleList.add(getString(R.string.chatroom_msg_custom_type_paiju));
        titleList.add(getString(R.string.me_column_record));
        mMttPagerAdapter = new MatchPagerAdapter(getSupportFragmentManager(), fragmentList, titleList, null);
        viewpager_horde_hall.setAdapter(mMttPagerAdapter);
        viewpager_horde_hall.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setSwipeBackEnable(position == 0);
                if (position == 0) {
                    if (mPaijuFragment != null) {
                        mPaijuFragment.getGamePlayingList();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        horde_hall_tabs.setViewPager(viewpager_horde_hall);
    }

    @Override
    protected void onDestroy() {
        registerObservers(false);
        if (mHordeAction != null) {
            mHordeAction.onDestroy();
            mHordeAction = null;
        }
        if (mHotUpdateAction != null) {
            mHotUpdateAction.onDestroy();
            mHotUpdateAction = null;
        }
        if (mGameAction != null) {
            mGameAction.onDestroy();
            mGameAction = null;
        }
        appbar_layout.removeOnOffsetChangedListener(mOffsetChangedListener);
        super.onDestroy();
    }

    private void registerObservers(boolean register) {
        HotUpdateManager.getInstance().clearCallback();
        if (register) {
            HordeUpdateManager.getInstance().registerCallback(hordeUpdateCallback);
            HotUpdateManager.getInstance().registerCallback(hotUpdateCallback);
        } else {
            HordeUpdateManager.getInstance().unregisterCallback(hordeUpdateCallback);
            HotUpdateManager.getInstance().unregisterCallback(hotUpdateCallback);
        }
    }

    HotUpdateManager.HotUpdateCallback hotUpdateCallback = new HotUpdateManager.HotUpdateCallback() {
        @Override
        public void onUpdated(HotUpdateItem item) {
            int type = item.updateType;
            if (type == HotUpdateItem.UPDATE_TYPE_ING) {
                if (mHotUpdateAction != null) {
                    mHotUpdateAction.lastDownTime = DemoCache.getCurrentServerSecondTime();
                }
                int downloadFileCount = item.downloadFileCount;
                int finishFileCount = item.finishFileCount;
                int successFileCount = item.successFileCount;
                String newVersion = item.newVersion;
                UpdateFileEntity updateFileEntity = item.updateFileEntity;
                if (downloadFileCount == finishFileCount) {
                    if (mHotUpdateAction != null) {
                        mHotUpdateAction.lastDownTime = Long.MAX_VALUE;
                    }
                    if (finishFileCount == successFileCount) {
                        showUpdateResultDialog(true);
                    } else {
                        showUpdateResultDialog(false);
                    }
                } else {
                    //还在下载中
                }
            } else if (type == HotUpdateItem.UPDATE_TYPE_STUCK) {
                showUpdateResultDialog(false);
            }
        }
    };

    EasyAlertDialog updateResultDialog = null;
    public void showUpdateResultDialog(boolean isSuccess) {
        if (mHotUpdateAction != null) {
            mHotUpdateAction.destroyTimer();
        }
        if(isSuccess){
            updateResultDialog = EasyAlertDialogHelper.createOneButtonDiolag(this , "",
                    getString(R.string.game_update_success), getString(R.string.ok), false, new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                        }
                    });
        } else {
            updateResultDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "",
                    getString(R.string.game_update_failure), getString(R.string.reget), getString(R.string.cancel), false,
                    new EasyAlertDialogHelper.OnDialogActionListener() {

                        @Override
                        public void doCancelAction() {
                        }

                        @Override
                        public void doOkAction() {
                            if (mHotUpdateAction != null) {
                                mHotUpdateAction.downUpdateFile();
                            }
                        }
                    });
        }
        if (!this.isFinishing() && !this.isDestroyedCompatible()) {
            updateResultDialog.show();
        }
    }

    HordeUpdateManager.HordeUpdateCallback hordeUpdateCallback = new HordeUpdateManager.HordeUpdateCallback() {
        @Override
        public void onUpdated(UpdateItem item) {
            if (item == null) {
                return;
            }
            if (item.updateType == UpdateItem.UPDATE_TYPE_NAME && !StringUtil.isSpace(item.name) && horde_hall_name != null && mHordeEntity != null) {
                mHordeEntity.name = item.name;
                setHordeNameTextSize();
            }
            if (item.updateType == UpdateItem.UPDATE_TYPE_IS_CONTROL && mHordeEntity != null) {
                mHordeEntity.is_control = item.is_control;
            }
            if (item.updateType == UpdateItem.UPDATE_TYPE_CANCEL_HORDE) {
//                finish();不需要finish，因为启动activity的方式是cleartop和singletop
            }
            if (item.updateType == UpdateItem.UPDATE_TYPE_IS_SCORE) {
                if (mHordeEntity != null && item.horde_id.equals(mHordeEntity.horde_id)) {
                    mHordeEntity.is_score = item.is_score;
                }
            }
        }
    };

    private void setHordeNameTextSize() {
        if (horde_hall_name_width <= 0 || mHordeEntity == null) {
            return;
        }
        int paddingLeftOrRight = ScreenUtil.dip2px(10);
        float maxTextSize = ScreenUtil.dp2px(this, 20);//最大20dp
        TextPaint tp = horde_hall_name.getPaint();
        if (tp.getTextSize() >= maxTextSize) {
            maxTextSize = getDesiredTextSize(horde_hall_name, mHordeEntity.name, horde_hall_name_width - 2 * paddingLeftOrRight, ScreenUtil.dp2px(this, 20), true);//最大20dp
        } else {
            maxTextSize = getDesiredTextSize(horde_hall_name, mHordeEntity.name, horde_hall_name_width - 2 * paddingLeftOrRight, ScreenUtil.dp2px(this, 20), false);//最大20dp
        }
        horde_hall_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, ScreenUtil.px2dip(maxTextSize));
        horde_hall_name.setText(mHordeEntity == null ? "" : mHordeEntity.name);
        LogUtil.i(TAG, "maxTextSize: " + maxTextSize);
    }

    private float getDesiredTextSize(TextView textView, String text, int maxWidth, int maxTextSize, boolean toSmall) {
        TextPaint tp = textView.getPaint();
        if (toSmall) {
            while (tp.measureText(text) > maxWidth) {
                tp.setTextSize(tp.getTextSize() - ScreenUtil.density);
                getDesiredTextSize(textView, text, maxWidth, maxTextSize, toSmall);
            }
            return tp.getTextSize() > maxTextSize ? maxTextSize : tp.getTextSize();
        } else {
            while (tp.measureText(text) < maxWidth - 10) {
                tp.setTextSize(tp.getTextSize() + ScreenUtil.density);
                getDesiredTextSize(textView, text, maxWidth, maxTextSize, toSmall);
            }
            return tp.getTextSize() > maxTextSize ? maxTextSize : tp.getTextSize();
        }
    }

    public GameAction mGameAction;
    HotUpdateAction mHotUpdateAction;
    @Override
    public void checkGameVersionJoin(final GameEntity gameEntity) {
        if (HotUpdateHelper.isGameUpdateIng()) {
            return;
        }
        CheckHotUpdateCallback callback = new CheckHotUpdateCallback() {
            @Override
            public void notUpdate() {
                joinOrCreateGame(gameEntity);
            }
        };
        if (HotUpdateHelper.isNeedToCheckVersion()) {
            mHotUpdateAction.doHotUpdate(true , callback);
        } else{
            callback.notUpdate();
        }
    }

    @Override
    public void checkGameVersionCreate(CheckHotUpdateCallback callback) {
        mHotUpdateAction.doHotUpdate(true , callback);
    }

    public void joinOrCreateGame(final GameEntity game) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, PermissionUtils.CODE_READ_PHONE_STATE, null);
            return;
        }
        String joinWay = UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_GROUP;
        if (!TextUtils.isEmpty(teamId) && TeamDataCache.getInstance().getTeamById(teamId) != null) {
            Team team = TeamDataCache.getInstance().getTeamById(teamId);
            if (team.getType() == TeamTypeEnum.Advanced) {
                joinWay = UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_CLUB;
            }
        }
        mGameAction.joinGame(joinWay, game.code,  new GameRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
            }
            @Override
            public void onFailed(int resultCode, JSONObject response) {
                if (resultCode == ApiCode.CODE_GAME_NONE_EXISTENT || resultCode == ApiCode.CODE_MATCH_CHECKIN_FAILURE_CHANNEL_NOT_FOUND) {
                    //牌局不存在
                    if (mPaijuFragment != null) {
                        mPaijuFragment.removePaijuNotExisted(game.code);
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, null);
    }

}
