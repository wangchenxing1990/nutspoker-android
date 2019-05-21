package com.htgames.nutspoker.ui.activity.Club;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.msg.helper.SystemMessageHelper;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.contact.activity.UserProfileActivity;
import com.htgames.nutspoker.chat.helper.MessageHelper;
import com.htgames.nutspoker.chat.main.activity.AddVerifyActivity;
import com.htgames.nutspoker.chat.msg.activity.MessageVerifyActivity;
import com.htgames.nutspoker.chat.msg.model.SystemMessage;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.constant.SystemMessageStatus;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.team.TeamService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClubApplyDetail extends BaseActivity {

    SystemMessage mSystemMessage;
    String mAccount;

    @BindView(R.id.ll_club_pane) View mUiClubView;
    @BindView(R.id.from_account_head_image) HeadImageView mUiHeadImageView;
    @BindView(R.id.from_account_text) TextView mUiName;
    @BindView(R.id.tv_club_into) TextView mUiClubInto;
    @BindView(R.id.tv_reason) TextView mUiReason;
    @BindView(R.id.tv_apply_again) View mUiApplyAgain;

    @OnClick(R.id.tv_reply_reject) void clickReplyReject(){
        //前往新的Activity
        ClubRejectActivity.StartActivityFor(this,mSystemMessage);
    }
    @OnClick(R.id.tv_reply_agree) void clickReplyAgree(){

        if(!ClubConstant.isClubMemberIsFull(mSystemMessage.getTargetId())){
            //通过俱乐部申请
            agreeClubApply(true);
        } else{
            Toast.makeText(getApplicationContext(), R.string.club_invite_member_count_limit, Toast.LENGTH_SHORT).show();
        }
    }
    @OnClick(R.id.rl_user) void clickUser(){
        if (mSystemMessage == null )
            return;
        UserProfileActivity.start(this , mSystemMessage.getFromAccount() , UserProfileActivity.FROM_OTHER_LIST , mSystemMessage);
    }
    @OnClick(R.id.tv_apply_again) void clickApplyAgain(){
        //再次申请加入
        AddVerifyActivity.StartForResult(this , mSystemMessage.getTargetId() , AddVerifyActivity.TYPE_VERIFY_CLUB);
    }

    public static void StartActivity(Activity activity, SystemMessage mSystemMessage) {
        if (mSystemMessage==null )
            return;
        String account = mSystemMessage.getFromAccount();
        if (DemoCache.getAccount().equals(account) || TextUtils.isEmpty(account))
            return;

        Intent intent = new Intent(activity, ClubApplyDetail.class);
        intent.putExtra(Extras.EXTRA_MESSAGE_DATA, mSystemMessage);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_apply_detail);
        mUnbinder = ButterKnife.bind(this);

        mAccount = getIntent().getStringExtra(Extras.EXTRA_ACCOUNT);
        mSystemMessage = (SystemMessage)getIntent().getSerializableExtra(Extras.EXTRA_MESSAGE_DATA);

        setHeadTitle(R.string.club_apply);
        if(mSystemMessage != null) {
            if(mSystemMessage.getType() == SystemMessageType.RejectTeamApply) {
                setHeadTitle(R.string.reject_apply);
                mUiApplyAgain.setVisibility(View.VISIBLE);
                mUiClubView.setVisibility(View.GONE);
            }
            else {
                setHeadTitle(R.string.club_apply);
                mUiApplyAgain.setVisibility(View.GONE);
                mUiClubView.setVisibility(View.VISIBLE);
            }
            mAccount = mSystemMessage.getFromAccount();
            mUiHeadImageView.loadBuddyAvatar(mAccount);
            if(NimUserInfoCache.getInstance().hasUser(mAccount)){
                mUiName.setText(NimUserInfoCache.getInstance().getUserDisplayNameEx(mAccount));
            }
            mUiClubInto.setText(MessageHelper.getVerifyNotificationText(mSystemMessage));
            if(mSystemMessage.getContent().isEmpty())
                mUiReason.setVisibility(View.GONE);
            else
                mUiReason.setText(getText(R.string.message)+" : "+mSystemMessage.getContent());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ClubRejectActivity.RequestCode_Reject) {
            boolean needRequest = resultCode == RESULT_OK;
            if (needRequest) {
                SystemMessage msg = (SystemMessage)data.getSerializableExtra(ClubRejectActivity.ResultData_Msg);
                String reason = data.getStringExtra(ClubRejectActivity.ResultData_Reason);
                if(msg != null) {
                    DialogMaker.showProgressDialog(this, getString(com.netease.nim.uikit.R.string.empty), false);
                    NIMClient.getService(TeamService.class).rejectApply(msg.getTargetId(), msg.getFromAccount(), reason).setCallback(new RequestCallback<Void>() {
                        @Override
                        public void onSuccess(Void param) {
                            DialogMaker.dismissProgressDialog();
                            Intent intentSuccess = new Intent(MessageVerifyActivity.ACTION_MESSAGE_DEAL);
                            intentSuccess.putExtra(Extras.EXTRA_MESSAGE_DATA, mSystemMessage);
                            intentSuccess.putExtra(MessageVerifyActivity.Extra_Pass,false);
                            sendBroadcast(intentSuccess);
                            finish();//结束自己
                        }

                        @Override
                        public void onFailed(int code) {
                            DialogMaker.dismissProgressDialog();
                            Toast.makeText(getApplicationContext(), R.string.taem_apply_agree_failure, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onException(Throwable exception) {
                            DialogMaker.dismissProgressDialog();
                        }
                    });
                }
            }
        } else if (requestCode == AddVerifyActivity.Request_CodeClubAA) {
            if(resultCode == RESULT_OK) {
                SystemMessageHelper.deleteSystemMessage(ClubApplyDetail.this, mSystemMessage);
                finish();
            }
        }
    }

    /**
     * 同意俱乐部申请
     * @param pass
     */
    public void agreeClubApply(final boolean pass) {
        if (mSystemMessage != null && mSystemMessage.getType() == SystemMessageType.ApplyJoinTeam) {
            //验证入群申请,用户发出申请后，所有管理员都会收到一条系统消息，类型为 SystemMessageType#TeamApply。管理员可选择同意或拒绝：
            if (pass) {
                DialogMaker.showProgressDialog(this, getString(com.netease.nim.uikit.R.string.empty), false);
                // 同意申请
                NIMClient.getService(TeamService.class).passApply(mSystemMessage.getTargetId(), mSystemMessage.getFromAccount()).setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DialogMaker.dismissProgressDialog();
                        Intent intentSuccess = new Intent(MessageVerifyActivity.ACTION_MESSAGE_DEAL);
                        intentSuccess.putExtra(Extras.EXTRA_MESSAGE_DATA, mSystemMessage);
                        sendBroadcast(intentSuccess);
                        finish();//结束自己
                    }

                    @Override
                    public void onFailed(int i) {
                        DialogMaker.dismissProgressDialog();
                        Toast.makeText(getApplicationContext(), R.string.taem_apply_agree_failure, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        DialogMaker.dismissProgressDialog();
                    }
                });
            }

        }
    }

}
