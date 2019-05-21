package com.htgames.nutspoker.chat.session.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.GameBillEntity;
import com.netease.nim.uikit.bean.PaipuEntity;
import com.htgames.nutspoker.hotupdate.HotUpdateAction;
import com.htgames.nutspoker.hotupdate.interfaces.CheckHotUpdateCallback;
import com.htgames.nutspoker.tool.json.RecordJsonTools;
import com.htgames.nutspoker.ui.action.GameAction;
import com.htgames.nutspoker.ui.action.HandCollectAction;
import com.htgames.nutspoker.ui.action.RecordAction;
import com.htgames.nutspoker.ui.activity.Record.RecordDetailsActivity;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalytics;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.session.constant.Extras;

import org.json.JSONObject;

/**
 * Created by 20150726 on 2016/4/13.
 */
public abstract class BaseMessageActivity extends com.netease.nim.uikit.session.activity.BaseMessageActivity {
    RecordAction mRecordAction;
    HandCollectAction mHandCollectAction;
    public GameAction mGameAction;
    HotUpdateAction mHotUpdateAction;
    int newMessageNum = 0;//新消息数量
    public TextView btn_head_back;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        newMessageNum = getIntent().getIntExtra(Extras.EXTRA_NEW_MESSAGE_NUM , 0);
        super.onCreate(savedInstanceState);
        initAction();
    }

    public void initAction() {
        mRecordAction = new RecordAction(this, null);
        mHandCollectAction = new HandCollectAction(this , null);
        mHotUpdateAction = new HotUpdateAction(this , null);
        mHotUpdateAction.onCreate();
        mGameAction = new GameAction(this , null);
    }

    public void openHandPlay(PaipuEntity paipuEntity){
        mHandCollectAction.openHandPlay(paipuEntity);
    }

    public void getRecordDetail(String gid) {
        mRecordAction.getRecordDetail(gid, new com.htgames.nutspoker.interfaces.RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                DialogMaker.dismissProgressDialog();
                if (code == 0) {
                    try {
                        JSONObject dataOBJ = new JSONObject(result).optJSONObject("data");
                        if (dataOBJ != null && dataOBJ.has("games")) {
                            JSONObject gameOBJ = dataOBJ.optJSONObject("games");
                            if (gameOBJ != null && !TextUtils.isEmpty(dataOBJ.toString())) {
                                GameBillEntity gameBillEntity = RecordJsonTools.getGameBillEntity(gameOBJ);
                                if (gameBillEntity != null) {
                                    RecordDetailsActivity.start(BaseMessageActivity.this, gameBillEntity, RecordDetailsActivity.FROM_CHAT);
                                    return;
                                }
                            }
                            Toast.makeText(getApplicationContext(), R.string.record_details_noexist, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BaseMessageActivity.this, R.string.get_failuer, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(BaseMessageActivity.this, R.string.get_failuer, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailed() {
                DialogMaker.dismissProgressDialog();
            }
        });
    }

    public void checkGameVersion(CheckHotUpdateCallback callback){
        mHotUpdateAction.doHotUpdate(true , callback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UmengAnalytics.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengAnalytics.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecordAction != null) {
            mRecordAction.onDestroy();
            mRecordAction = null;
        }
        if (mHandCollectAction != null) {
            mHandCollectAction.onDestroy();
            mHandCollectAction = null;
        }
        if (mHotUpdateAction != null) {
            mHotUpdateAction.onDestroy();
            mHotUpdateAction = null;
        }
        if (mGameAction != null) {
            mGameAction.onDestroy();
            mGameAction = null;
        }
    }

    public void setHeadTitle(int resId) {
        ((TextView) findViewById(com.htgames.nutspoker.R.id.tv_head_title)).setText(resId);
    }

    public void setHeadTitle(String title) {
        ((TextView) findViewById(com.htgames.nutspoker.R.id.tv_head_title)).setText(title);
    }

    /**
     * 设置头部右边按钮
     *
     * @param resId
     * @param onClickListener
     */
    public void setHeadRightButton(int resId, View.OnClickListener onClickListener) {
        TextView tv_head_right = ((TextView) findViewById(com.htgames.nutspoker.R.id.tv_head_right));
        tv_head_right.setVisibility(View.VISIBLE);
        tv_head_right.setText(resId);
        tv_head_right.setOnClickListener(onClickListener);
    }

    public void setHeadRightButtonGone() {
        TextView tv_head_right = ((TextView) findViewById(com.htgames.nutspoker.R.id.tv_head_right));
        tv_head_right.setVisibility(View.GONE);
    }

    public void setHeadRightButtonVisible() {
        TextView tv_head_right = ((TextView) findViewById(com.htgames.nutspoker.R.id.tv_head_right));
        tv_head_right.setVisibility(View.VISIBLE);
    }

    public void onBack(View view) {
//        onBackPressed();
        finish();
    }


    /**
     * 设置返回按钮上面的未读消息数量
     * @param textView
     * @param unreadCount
     */
    public void updateNewChatUI(TextView textView ,int unreadCount) {
        if (unreadCount > 0) {
            if (unreadCount > 99) {
                textView.setText(String.format(getString(R.string.back_unread), "99+"));
            } else {
                textView.setText(String.format(getString(R.string.back_unread), String.valueOf(unreadCount)));
            }
        } else {
            textView.setText(com.netease.nim.uikit.R.string.back);
        }
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return true;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.RIGHT;
    }
}
