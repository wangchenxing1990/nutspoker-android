package com.htgames.nutspoker.ui.activity.Club;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.ui.action.ClubAction;
import com.htgames.nutspoker.ui.action.EditClubInfoAction;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.htgames.nutspoker.view.switchbutton.SwitchButton;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.chesscircle.helper.RecentContactHelp;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManagerClubActivity extends BaseActivity {

    public static final String Extra_IsOwner = "com.htgames.chesscircle.chat.team.activity.ManagerClubActivity.Extra_IsOwner";

    EditClubInfoAction mEditClubInfoAction;
    ClubAction mClubAction;

    Team mTeam;

    //是不是群创建者
    private boolean isSelfAdmin = true;

    @BindView(R.id.switch_just_creator_create) SwitchButton mUiCreate;
    @BindView(R.id.switch_is_private) SwitchButton mUiNotSearch;
    @BindView(R.id.rl_club_remove) View mUiDissolution;
    @BindView(R.id.tv_clubmgr_limit_status) TextView mUiTextLimitTip;

    @OnClick(R.id.rl_manager_club) void clickMgrSet(){
        ManagerSetActivity.Start(this,mTeam);
    }
    @OnClick(R.id.rl_manager_upgrade) void clickUpgrade(){
        if(mTeam != null)
            ClubMemberLimitActivity.start(this , mTeam.getId());
    }
    @OnClick(R.id.rl_club_remove) void clickDissolution(){
        showConfirmDialog(isSelfAdmin);
    }

    public CompoundButton.OnCheckedChangeListener clubConfigChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            updateClubConfig();
        }
    };

    public static void Start(Activity activity, Team team, boolean isOwner){
        Intent intent = new Intent(activity,ManagerClubActivity.class);
        intent.putExtra(Extra_IsOwner,isOwner);
        intent.putExtra(Extras.EXTRA_TEAM_DATA,team);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_club);
        mUnbinder = ButterKnife.bind(this);

        mTeam = (Team) getIntent().getSerializableExtra(Extras.EXTRA_TEAM_DATA);
        isSelfAdmin = getIntent().getBooleanExtra(Extra_IsOwner,false);

        setHeadTitle(R.string.manager_club);

        int remainingDay = 0;
        int personLimit = 0;
        if(mTeam != null) {
            remainingDay = ClubConstant.getClubMemberLimitRemainDay(mTeam);
            personLimit = ClubConstant.getClubMemberLimit(mTeam);
        }
        if(remainingDay > 0) {
            mUiTextLimitTip.setText(String.format(getString(R.string.clubmanager_people_limit_tip),personLimit,remainingDay));
            mUiTextLimitTip.setVisibility(View.VISIBLE);
        }
        if(isSelfAdmin)
            mUiDissolution.setVisibility(View.VISIBLE);
        if (mTeam != null) {
            mUiCreate.setCheckedImmediately(ClubConstant.isClubCreateGameByCreatorLimit(mTeam));
            mUiNotSearch.setCheckedImmediately(ClubConstant.isClubPrivate(mTeam));
        }
        mUiCreate.setOnCheckedChangeListener(clubConfigChangeListener);
        mUiNotSearch.setOnCheckedChangeListener(clubConfigChangeListener);
    }

    @Override
    protected void onDestroy() {
        if(mClubAction != null){
            mClubAction.onDestroy();
            mClubAction = null;
        }
        if(mEditClubInfoAction != null) {
            mEditClubInfoAction.onDestroy();
            mEditClubInfoAction = null;
        }
        super.onDestroy();
    }

    public void updateClubConfig() {
        if (mEditClubInfoAction == null) {
            mEditClubInfoAction = new EditClubInfoAction(this, null);
        }
        mEditClubInfoAction.updateClubConfig(mTeam.getId(), mUiNotSearch.isChecked(),
                mUiCreate.isChecked(), new com.htgames.nutspoker.interfaces.RequestCallback() {
                    @Override
                    public void onResult(int code, String result, Throwable var3) {

                    }

                    @Override
                    public void onFailed() {

                    }
                });
    }

    EasyAlertDialog actionDialog;

    public void showConfirmDialog(final boolean isCreator) {
        String message = getString(R.string.quit_club_dialog_message);
        if(isCreator){
            if(ClubConstant.getClubMemberLimit(mTeam) > ClubConstant.CLUB_MEMBER_LIMIT){
                //购买过俱乐部上限
                message = getString(R.string.dismiss_club_club_upgrade_prompt);
            } else{
                message = getString(R.string.dismiss_club_dialog_message);
            }
        }
        actionDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, null, message, true,
                new EasyAlertDialogHelper.OnDialogActionListener() {

                    @Override
                    public void doCancelAction() {
                    }

                    @Override
                    public void doOkAction() {
                        if(isCreator){
                            doDismissTeam();
                        }else{
                            //doQuitTeam();
                        }
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            actionDialog.show();
        }
    }

    /**
     * 解散俱乐部：通过APP服务器
     */
    public void doDismissTeam() {
        if(mClubAction == null)
            mClubAction = new ClubAction(this,null);
        mClubAction.dismissTeam(mTeam.getId(), new com.htgames.nutspoker.interfaces.RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if(code == 0){
                    getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            RecentContactHelp.deleteRecentContact(mTeam.getId(), SessionTypeEnum.Team, true);
                            MainActivity.start(ManagerClubActivity.this);//打开主页
                            finish();
                        }
                    }, 200L);
                }
            }

            @Override
            public void onFailed() {

            }
        });
    }
}
