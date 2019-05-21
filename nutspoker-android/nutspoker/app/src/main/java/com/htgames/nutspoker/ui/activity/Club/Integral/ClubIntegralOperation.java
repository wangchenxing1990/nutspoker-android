package com.htgames.nutspoker.ui.activity.Club.Integral;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.team.model.Team;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 分配，捐赠
 */
public class ClubIntegralOperation extends BaseActivity {

    //分配
    public static final int Type_Dispatch = 0;
    //贡献
    public static final int Type_Contribution = 1;

    @BindView(R.id.hiv_head) HeadImageView mUiHeadImage;
    @BindView(R.id.tv_name) TextView mUiName;
    @BindView(R.id.tv_zyh) TextView mUiZYH;
    @BindView(R.id.edit_integral) ClearableEditTextWithIcon mUiEdit;
    @BindView(R.id.tv_current_integral) TextView mUiCurrentIntegral;
    @BindView(R.id.tv_tip) TextView mUiTip;

    @OnClick(R.id.btn_sure) void clickSure() {
        int theCount = Integer.parseInt(mUiEdit.getEditableText().toString());

        if(mType == Type_Contribution){
            if(theCount > MyIntegralActivity.mMyIntegral)
                showNotEnough();
            else{
                //捐赠给俱乐部
            }
        } else {
            //分配
        }
    }

    public static void StartActivity(Context context, String id, int type,@Nullable Team team) {
        if (type != Type_Dispatch && type != Type_Contribution)
            return;

        Intent intent = new Intent(context, ClubIntegralOperation.class);
        intent.putExtra(Extras.EXTRA_ACCOUNT, id);
        intent.putExtra(Extras.EXTRA_TYPE, type);
        if(team != null)
            intent.putExtra(Extras.EXTRA_TEAM_DATA,team);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_integral_operation);
        mUnbinder = ButterKnife.bind(this);

        mId = getIntent().getStringExtra(Extras.EXTRA_ACCOUNT);
        mType = getIntent().getIntExtra(Extras.EXTRA_TYPE, Type_Dispatch);
        mTeam = (Team) getIntent().getSerializableExtra(Extras.EXTRA_TEAM_DATA);

        if (mType == Type_Dispatch) {//分配
            mUiHeadImage.loadBuddyAvatar(mId);
            mUiName.setText(NimUserInfoCache.getInstance().getUserDisplayName(mId));

            //缺少战鱼号
            mUiZYH.setText(getString(R.string.virtual_id) + "1234567");
            mUiCurrentIntegral.setVisibility(View.GONE);
            mUiTip.setText(R.string.dispatch_integral);

            setHeadTitle(R.string.text_dispatch);
        } else {//捐赠
            String avatar = ClubConstant.getClubExtAvatar(mTeam.getExtServer());
            mUiHeadImage.loadClubAvatarByUrl(mTeam.getId(), avatar, HeadImageView.DEFAULT_AVATAR_THUMB_SIZE);
            mUiName.setText(mTeam.getName());

            mUiZYH.setText("ID: " + ClubConstant.getClubVirtualId(mTeam.getId(), mTeam.getExtServer()));//改成俱乐部虚拟ID
            mUiCurrentIntegral.setText(getString(R.string.integral_now)+":"+MyIntegralActivity.mMyIntegral);
            mUiCurrentIntegral.setVisibility(View.VISIBLE);
            mUiTip.setText(R.string.contribution_club_integral);

            setHeadTitle(R.string.integral_contribution);
        }
    }

    public void showNotEnough() {
        if (mDialog == null) {
            mDialog = EasyAlertDialogHelper.createOneButtonDiolag(this, "",
                    getString(R.string.integral_not_enough),
                    getString(R.string.ok), true,
                    null);
        }
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    EasyAlertDialog mDialog;

    //data
    String mId;//用戶賬號，或者群ID
    int mType;//类型
    Team mTeam;//群ID数据
}
