package com.htgames.nutspoker.ui.activity.Club;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.msg.model.SystemMessage;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by glp on 2016/7/14.
 */

public class ClubRejectActivity extends BaseActivity {

    public static final int RequestCode_Reject = 1;
    public static final String ResultData_Msg = "com.htgames.chesscircle.chat.msg.activity.MessageVerifyActivity.ResultData_Msg";
    public static final String ResultData_Reason = "com.htgames.chesscircle.chat.msg.activity.MessageVerifyActivity.ResultData_Reason";

    public static final String Extra_Message = "com.htgames.chesscircle.ui.activity.Club.ClubRejectActivity.Message";

    SystemMessage mMsg;

    @BindView(R.id.edt_verify_message)
    ClearableEditTextWithIcon mEdit;

    @OnClick(/*R.id.btn_send*/R.id.tv_head_right) void clickSend(){
        Intent data = new Intent();
        data.putExtra(ResultData_Msg,mMsg);
        data.putExtra(ResultData_Reason,mEdit.getText().toString());
        setResult(RESULT_OK,data);

        finish();
    }

    public static void StartActivityFor(Activity activity, SystemMessage msg) {
        Intent intent = new Intent(activity,ClubRejectActivity.class);
        intent.putExtra(Extra_Message,msg);
        activity.startActivityForResult(intent, RequestCode_Reject);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_reject);
        mUnbinder = ButterKnife.bind(this);

        mMsg = (SystemMessage)getIntent().getSerializableExtra(Extra_Message);

        setHeadTitle(R.string.reject_request);
        setHeadRightButtonText(R.string.send);
        setResult(RESULT_CANCELED);//默认
    }
}
