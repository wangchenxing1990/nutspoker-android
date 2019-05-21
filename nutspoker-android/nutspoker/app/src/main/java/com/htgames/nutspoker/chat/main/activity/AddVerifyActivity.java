package com.htgames.nutspoker.chat.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiCode;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.ui.action.HordeAction;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.chesscircle.entity.HordeEntity;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;

import org.json.JSONObject;

/**
 * 好友验证/俱乐部验证
 */
public class AddVerifyActivity extends BaseActivity {
    private TextView mHeadRightTV;
    public static final int Request_CodeClubAA = 2;

    private final static String TAG = "AddVerifyActivity";
    private ClearableEditTextWithIcon edt_verify_message;
    public final static int RESULT_CODE_SUCCESS = 1;
    /**
     * 验证类型：好友验证
     */
    public final static int TYPE_VERIFY_FRIEND = 1;
    /**
     * 验证类型：俱乐部验证
     */
    public final static int TYPE_VERIFY_CLUB = 2;
    public final static int TYPE_VERIFY_HORDE = 3;//申请加入部落
    private int verifyType = TYPE_VERIFY_FRIEND;
    private String sessionId;
    private String nickname = "";
    private TextView verify_des;
    HordeAction mHordeAction;
    public HordeEntity mHordeEntity;

    public static void start(Activity activity, String sessionId, int verifyType){
        Intent intent = new Intent(activity, AddVerifyActivity.class);
        intent.putExtra(Extras.EXTRA_VERIFY_TYPE, verifyType);
        intent.putExtra(Extras.EXTRA_SESSION_ID, sessionId);
        activity.startActivity(intent);
    }

    public static void startForApplyHorde(Activity activity, HordeEntity hordeEntity, String tid, int verifyType) {
        Intent intent = new Intent(activity, AddVerifyActivity.class);
        intent.putExtra(Extras.EXTRA_CUSTOMIZATION, hordeEntity);
        intent.putExtra(Extras.EXTRA_VERIFY_TYPE, verifyType);
        intent.putExtra(Extras.EXTRA_SESSION_ID, tid);
        activity.startActivity(intent);
    }

    public static void StartForResult(Activity activity , String sessionId , int verifyType){
        Intent intent = new Intent(activity, AddVerifyActivity.class);
        intent.putExtra(Extras.EXTRA_VERIFY_TYPE, verifyType);
        intent.putExtra(Extras.EXTRA_SESSION_ID, sessionId);
        activity.startActivityForResult(intent,Request_CodeClubAA);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mHordeEntity = (HordeEntity) getIntent().getSerializableExtra(Extras.EXTRA_CUSTOMIZATION);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_verify);
        mHordeAction = new HordeAction(this, null);
        verifyType = getIntent().getIntExtra(Extras.EXTRA_VERIFY_TYPE, TYPE_VERIFY_FRIEND);
        sessionId = getIntent().getStringExtra(Extras.EXTRA_SESSION_ID);
//        nickname = getIntent().getStringExtra(EXTRA_NICKNAME);
        if(NimUserInfoCache.getInstance().hasUser(DemoCache.getAccount())){
            nickname = NimUserInfoCache.getInstance().getUserDisplayName(DemoCache.getAccount());
        }else{
            nickname = "";
        }
        initHead();
        initView();
        setResult(RESULT_CANCELED);
        mHeadRightTV.setEnabled(TextUtils.isEmpty(edt_verify_message.getText().toString()) ? false : true);
        mHeadRightTV.setTextColor(getResources().getColor(TextUtils.isEmpty(edt_verify_message.getText().toString()) ? R.color.login_grey_color : R.color.white));
    }

    private void initHead() {
        mHeadRightTV = (TextView) findViewById(R.id.tv_head_right);
        mHeadRightTV.setText(R.string.send);
        mHeadRightTV.setVisibility(View.VISIBLE);
        mHeadRightTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyType == TYPE_VERIFY_FRIEND) {
                    doAddFriendByVerify(sessionId, edt_verify_message.getText().toString(), true);
                } else if (verifyType == TYPE_VERIFY_CLUB) {
                    applyJoinTeam(sessionId, edt_verify_message.getText().toString());
                } else if (verifyType == TYPE_VERIFY_HORDE) {
                    if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
                        Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
                        return;
                    }
                    String content = ((ClearableEditTextWithIcon) findViewById(R.id.edt_verify_message)).getText().toString();
                    if (StringUtil.isSpace(content)) {
                        content = String.format(getString(R.string.verify_send_message), nickname);
                    }
                    mHordeAction.hordeApply(mHordeEntity == null ? "" : mHordeEntity.horde_id, sessionId, content, new GameRequestCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Toast.makeText(AddVerifyActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        @Override
                        public void onFailed(int code, JSONObject response) {
                            if (code == ApiCode.CODE_HORDE_JOIN_LIMIT) {
                                showJoinHordeLimitDialog();
                            }
                        }
                    });
                }
            }
        });
        if (verifyType == TYPE_VERIFY_FRIEND) {
            setHeadTitle(R.string.verify_add_friend);
        } else if (verifyType == TYPE_VERIFY_CLUB) {
            setHeadTitle(R.string.verify_add_club);
        } else if (verifyType == TYPE_VERIFY_HORDE) {
            setHeadTitle(R.string.invalid_action_apply_club);
        }
    }

    EasyAlertDialog joinHordeLimitDialog;
    private void showJoinHordeLimitDialog() {
        if (joinHordeLimitDialog == null) {
            joinHordeLimitDialog = EasyAlertDialogHelper.createOneButtonDiolag(this, "", "最多加入三个部落", getResources().getString(R.string.ok), true, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            joinHordeLimitDialog.show();
        }
    }

    private void initView() {
        edt_verify_message = (ClearableEditTextWithIcon) findViewById(R.id.edt_verify_message);
        if(verifyType == TYPE_VERIFY_FRIEND){
            edt_verify_message.setText(String.format(getString(R.string.verify_send_message), nickname));
        } else if (verifyType == TYPE_VERIFY_CLUB) {
            edt_verify_message.setText(String.format(getString(R.string.verify_send_message), nickname));
        } else if (verifyType == TYPE_VERIFY_HORDE) {
            edt_verify_message.setText(String.format(getString(R.string.verify_send_message), nickname));
        }
        edt_verify_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mHeadRightTV.setEnabled(edt_verify_message.getText().toString().length() <= 0 ? false : true);
                mHeadRightTV.setTextColor(getResources().getColor(edt_verify_message.getText().toString().length() <= 0 ? R.color.login_grey_color : R.color.white));
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 申请加入teamId
     * @param teamId
     * @param reason
     */
    public void applyJoinTeam(String teamId , String reason) {
        if (!NetworkUtil.isNetAvailable(this)) {
            Toast.makeText(getApplicationContext(), R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
            return;
        }
        DialogMaker.showProgressDialog(this, getString(R.string.team_apply_to_join_send_ing), false);
        NIMClient.getService(TeamService.class)
                .applyJoinTeam(teamId, reason)
                .setCallback(new RequestCallback<Team>() {
                    @Override
                    public void onSuccess(Team team) {
//                        DialogMaker.showProgressDialog(getApplicationContext(), getString(R.string.team_apply_to_join_send_success), false);
                        DialogMaker.dismissProgressDialog();
                        Toast.makeText(getApplicationContext(), R.string.team_apply_to_join_send_success, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailed(int code) {
                        DialogMaker.dismissProgressDialog();
                        if (code == ResponseCode.RES_TEAM_APPLY_SUCCESS) {
                            Toast.makeText(getApplicationContext(), R.string.team_apply_to_join_send_success, Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else if (code == ResponseCode.RES_TEAM_ALREADY_IN) {
                            Toast.makeText(getApplicationContext(), R.string.has_exist_in_team, Toast.LENGTH_SHORT).show();
                        } else if (code == ResponseCode.RES_TEAM_ENOTEXIST) {
                            //俱乐部已经解散
                            Toast.makeText(getApplicationContext(), R.string.team_already_dismiss, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.team_apply_to_join_send_failed , Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        DialogMaker.dismissProgressDialog();
                    }
                });
    }

    /**
     * 添加好友
     * @param account
     * @param msg
     * @param addDirectly
     */
    public void doAddFriendByVerify(final String account , String msg, boolean addDirectly) {
        if (!NetworkUtil.isNetAvailable(this)) {
            Toast.makeText(getApplicationContext(), R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
            return;
        }
        final VerifyType verifyType = VerifyType.VERIFY_REQUEST; // 发起好友验证请求
//        final VerifyType verifyType = addDirectly ? VerifyType.DIRECT_ADD : VerifyType.VERIFY_REQUEST;
        DialogMaker.showProgressDialog(this, getString(R.string.add_friend_verify_send_ing), false);
        NIMClient.getService(FriendService.class).addFriend(new AddFriendData(account, verifyType, msg))
                .setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DialogMaker.dismissProgressDialog();
                        if (VerifyType.DIRECT_ADD == verifyType) {
                            Toast.makeText(getApplicationContext(), R.string.add_friend_success, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.add_friend_verify_send_prompt, Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }

                    @Override
                    public void onFailed(int code) {
                        DialogMaker.dismissProgressDialog();
                        if (code == ResponseCode.RES_ETIMEOUT) {
                            Toast.makeText(getApplicationContext(), R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.add_friend_verify_send_failed , Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        DialogMaker.dismissProgressDialog();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (mHordeAction != null) {
            mHordeAction.onDestroy();
            mHordeAction = null;
        }
        super.onDestroy();
    }
}
