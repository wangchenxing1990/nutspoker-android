package com.htgames.nutspoker.ui.activity.Club;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiCode;
import com.htgames.nutspoker.config.GameConfigData;
import com.htgames.nutspoker.data.DataManager;
import com.htgames.nutspoker.net.RequestTimeLimit;
import com.htgames.nutspoker.ui.action.ClubAction;
import com.htgames.nutspoker.ui.adapter.clubmember.ClubMemberAdapNew;
import com.htgames.nutspoker.ui.adapter.clubmember.ClubMemberItem;
import com.htgames.nutspoker.ui.base.BaseTeamActivity;
import com.htgames.nutspoker.view.TouchableRecyclerView;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.BaseTools;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.customview.SwipeItemLayout;
import com.netease.nim.uikit.interfaces.IClick;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;

public class ManagerSetActivity extends BaseTeamActivity implements View.OnClickListener, IClick {

    public static final int RequestCodeMgr = 22;
    static final boolean IsUserYX = false;
    public static int TotalMgrCount = GameConfigData.TEAM_NUM <= 0 ? 20 : GameConfigData.TEAM_NUM;
    ClubMemberAdapNew mTeamMemberAdapter;
    public Team mTeam;
    ClubAction mClubAction;
    List<AbstractFlexibleItem> datas = new ArrayList<>();
    @BindView(R.id.iv_add_club_manager_guide) View iv_add_club_manager_guide;
    @BindView(R.id.tv_mgr_count) TextView mUiMgrCount;
    @BindView(R.id.ll_addmgr_disable) View mUiAddDisable;
    @BindView(R.id.ll_addmgr_normal) View mUiAddEnable;
    @BindView(R.id.lv_members) TouchableRecyclerView mRecyclerView;

    @OnClick(R.id.ll_addmgr_normal) void clickAddMgr() {
        if(mTeamMemberAdapter.isShowRemove) {
            showRemoveMode(false);
        }
        showAddMgrDialog();
    }

    public static void Start(Activity activity,Team team){
        Intent intent = new Intent(activity, ManagerSetActivity.class);
        intent.putExtra(Extras.EXTRA_TEAM_DATA,team);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_set);
        mUnbinder = ButterKnife.bind(this);
        mClubAction = new ClubAction(this,null);
        mTeam = (Team)getIntent().getSerializableExtra(Extras.EXTRA_TEAM_DATA);
        initManagerData();
        initAdapter();
        setHeadTitle(R.string.clubmanager_set);
        setHeadRightButton(R.string.text_mgr,this);
        initBtnStatus();
        iv_add_club_manager_guide.setOnClickListener(this);
    }

    private void initManagerData() {
        datas.clear();
        for (int i = 0; i < DataManager.getInstance().sMgrList.size(); i++) {
            ClubMemberItem clubMemberItem = new ClubMemberItem(DataManager.getInstance().sMgrList.get(i));
            clubMemberItem.adapterType = ClubMemberItem.TYPE_SET_MANAGER_AC;
            datas.add(clubMemberItem);
        }
    }

    private void initAdapter() {
        mTeamMemberAdapter = new ClubMemberAdapNew(datas, this, true);
        mTeamMemberAdapter.DEBUG = CacheConstant.debugBuildType;
        mTeamMemberAdapter.isMeCreator = true;
        mTeamMemberAdapter.isSelfAdmin = true;
        mTeamMemberAdapter.setAnimationEntryStep(true)
                .setOnlyEntryAnimation(false)
                .setAnimationInitialDelay(100L)
                .setAnimationDelay(70L)
                .setAnimationOnScrolling(true)
                .setAnimationDuration(300L);
        mRecyclerView.setAdapter(mTeamMemberAdapter);
//        mRecyclerView.setHasFixedSize(true); //Size of RV will not change
        mRecyclerView.setItemViewCacheSize(0); //Setting ViewCache to 0 (default=2) will animate items better while scrolling down+up with LinearLayout
        mTeamMemberAdapter.setLongPressDragEnabled(false)
                .setHandleDragEnabled(true)
                .setSwipeEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mClubAction != null) {
            mClubAction.onDestroy();
            mClubAction = null;
        }
    }

    void initBtnStatus(){
        //如果列表为空，则回到正常模式
        int mgrNum = mTeamMemberAdapter == null ? 0 : mTeamMemberAdapter.getItemCount();
        if(mgrNum <= 0 && mTeamMemberAdapter != null && mTeamMemberAdapter.isShowRemove) {
            showRemoveMode(false);
        }
        mUiMgrCount.setText("管理员(" + mgrNum + "/" + TotalMgrCount + ")");
        if(mgrNum < TotalMgrCount) {
            mUiAddDisable.setVisibility(View.GONE);
            mUiAddEnable.setVisibility(View.VISIBLE);
        }
        else {
            mUiAddDisable.setVisibility(View.VISIBLE);
            mUiAddEnable.setVisibility(View.GONE);
        }
        initPopupBtnStatus();
    }

    void initPopupBtnStatus() {
        int mgrNum = mTeamMemberAdapter == null ? 0 : mTeamMemberAdapter.getItemCount();
        //修改Popup
        if(mPopHolder.mUiMgrRemove!=null) {
            mPopHolder.mUiMgrRemove.setEnabled(mgrNum > 0);//可减
            int colorId = (mgrNum > 0) ? R.color.black : R.color.shop_text_no_select_color;
            mPopHolder.mUiTextRemove.setTextColor(ContextCompat.getColor(this, colorId));
            mPopHolder.mUiTextRemove.setText(R.string.remove_clubmgr);
        }
        if(mPopHolder.mUiMgrAdd!=null) {
            mPopHolder.mUiMgrAdd.setEnabled(mgrNum < TotalMgrCount);//可加
            int colorId = mgrNum < TotalMgrCount ? R.color.black : R.color.shop_text_no_select_color;
            mPopHolder.mUiTextAdd.setTextColor(ContextCompat.getColor(this, colorId));
            mPopHolder.mUiTextAdd.setText(R.string.add_clubmgr);
        }
    }

    EasyAlertDialog addMgrDialog;
    private void showAddMgrDialog() {
        int currentMgrSize = mTeamMemberAdapter == null ? 0 : mTeamMemberAdapter.getItemCount();
        String message = "添加3-5个管理员，\n" + "每添加一个管理员消耗800钻石";
        if (currentMgrSize <= 1) {
            gotoSelectMgr();
            return;
        } else if (currentMgrSize >= 2 && currentMgrSize <= 4) {
            message = "添加3-5个管理员，\n" + "每添加一个管理员消耗800钻石";
        } else if (currentMgrSize >= 5 && currentMgrSize <= 9) {
            message = "添加6-10个管理员，\n" + "每添加一个管理员消耗1000钻石";
        } else if (currentMgrSize >= 10 && currentMgrSize <= 19) {
            message = "添加11-20个管理员，\n" + "每添加一个管理员消耗1500钻石";
        }
        if (addMgrDialog == null) {
            addMgrDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "",
                    message, getString(R.string.ok), getString(R.string.cancel), true,
                    new EasyAlertDialogHelper.OnDialogActionListener() {
                        @Override
                        public void doCancelAction() {
                            addMgrDialog.dismiss();
                        }
                        @Override
                        public void doOkAction() {
                            gotoSelectMgr();
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            addMgrDialog.setMessage(message);
            addMgrDialog.show();
        }
    }

    void gotoSelectMgr() {
        SelectClubMgrACNew.startActivityForResult(this, mTeam.getId(), RequestCodeMgr);
        //过滤当前的管理员
//        ContactSelectHelper.startContactSelect(ManagerSetActivity.this
//                ,ContactSelectHelper.getMgrSelectOption(DataManager.getInstance().sManagerList, ClubInfoActivity.creator, TotalMgrCount
//                        , mTeam.getId(),getString(R.string.add_clubmgr)
//                        ,String.format(getString(R.string.clubmgr_limit_mgr),TotalMgrCount)), RequestCodeMgr);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.iv_add_club_manager_guide) {
            ClubAddMgrGuide.start(this);
        } else if (viewId == R.id.tv_head_right) {
            if(mTeamMemberAdapter.isShowRemove)
                showRemoveMode(false);
            else {
                if(morePopupWindow != null && morePopupWindow.isShowing()){
                    dissmissMorePopWindow();
                }else{
                    showMorePopWindow();
                }
            }
        }
    }

    PopupWindow morePopupWindow = null;

    @Override
    public void onDelete(int position) {
        if (!(mTeamMemberAdapter.getItem(position) instanceof ClubMemberItem)) {
            return;
        }
        TeamMember member = ((ClubMemberItem) mTeamMemberAdapter.getItem(position)).mTeamMember;
        if (member == null) {
            return;
        }
        onRemoveClubMemberMgr(member.getAccount(), position);
    }

    @Override
    public void onClick(int position) {
        if (!(mTeamMemberAdapter.getItem(position) instanceof ClubMemberItem)) {
            return;
        }
        TeamMember member = ((ClubMemberItem) mTeamMemberAdapter.getItem(position)).mTeamMember;
        if (member == null) {
            return;
        }
        if(mTeamMemberAdapter.isShowRemove && !DemoCache.getAccount().equals(member.getAccount())){
            onRemoveClubMemberMgr(member.getAccount(), position);
        }
    }

    @Override
    public void onLongClick(int position) {

    }

    class PopupHolder {
        @BindView(R.id.ll_pop_club_create) View mUiMgrAdd;
        @BindView(R.id.ll_pop_club_join) View mUiMgrRemove;
        @BindView(R.id.tv_pop_club_create) TextView mUiTextAdd;
        @BindView(R.id.tv_pop_club_join) TextView mUiTextRemove;
        @OnClick(R.id.ll_pop_club_create) void clickPopMgrAdd(){
            dissmissMorePopWindow();
            showAddMgrDialog();
        }
        @OnClick(R.id.ll_pop_club_join) void clickPopMgrRemove(){
            dissmissMorePopWindow();
            showRemoveMode(true);
        }
    }
    PopupHolder mPopHolder = new PopupHolder();

    /**
     * 显示popWindow
     * */
    private void showMorePopWindow() {
        if(morePopupWindow == null){
            View popView = LayoutInflater.from(this).inflate(R.layout.pop_club_add, null);//pop_club_add
//            View popView = LayoutInflater.from(this).inflate(R.layout.pop_app_message_delete, null);//
            //            View popView = LayoutInflater.from(this).inflate(R.layout.popup_set_mgr, null);
            mUnbinder = ButterKnife.bind(mPopHolder,popView);
            initPopupBtnStatus();
            morePopupWindow = new PopupWindow(popView);
            //获取popwindow焦点
            morePopupWindow.setFocusable(true);
            //设置popwindow如果点击外面区域，便关闭。
            morePopupWindow.setOutsideTouchable(true);
            morePopupWindow.setBackgroundDrawable(new ColorDrawable(0));
            morePopupWindow.setWidth(BaseTools.dip2px(getApplicationContext(), 160));
            morePopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView addText = (TextView) popView.findViewById(R.id.tv_pop_club_create);
            ((FrameLayout.LayoutParams) addText.getLayoutParams()).gravity = Gravity.CENTER;
            addText.setText(R.string.add_clubmgr);
            addText.setCompoundDrawables(null, null, null, null);

            TextView pauseText = (TextView) popView.findViewById(R.id.tv_pop_club_join);
            ((FrameLayout.LayoutParams) pauseText.getLayoutParams()).gravity = Gravity.CENTER;
            pauseText.setText(R.string.remove_clubmgr);
            pauseText.setCompoundDrawables(null, null, null, null);
        }
//        morePopupWindow.showAsDropDown(findViewById(R.id.tv_head_right), 0, -BaseTools.dip2px(getApplicationContext(), 9));
        morePopupWindow.showAsDropDown(findViewById(R.id.tv_head_right), -ScreenUtil.dp2px(this, 9), -ScreenUtil.dp2px(this, 16));
    }

    public void dissmissMorePopWindow(){
        if(morePopupWindow != null && morePopupWindow.isShowing()){
            morePopupWindow.dismiss();
        }
    }

    public void showRemoveMode(boolean show){
        mTeamMemberAdapter.showRemoveMode(show);
        if(show){
            setHeadRightButtonText(R.string.finish);
        } else{
            setHeadRightButtonText(R.string.text_mgr);
        }
    }

    @Override
    public void onBackPressed() {
        if (mTeamMemberAdapter != null && mTeamMemberAdapter.isShowRemove) {
            showRemoveMode(false);
            return;
        }
        super.onBackPressed();
    }

    /**
     * 移除管理员
     */
    private void onRemoveClubMemberMgr(final String memberAccount, final int position) {
        if (!NetworkUtil.isNetAvailable(this)) {
            Toast.makeText(getApplicationContext(), R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
            return;
        }
        EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(this, null,
                getString(R.string.text_del_mgr_format , NimUserInfoCache.getInstance().getUserDisplayName(memberAccount)), true,
                new EasyAlertDialogHelper.OnDialogActionListener() {

                    @Override
                    public void doCancelAction() {

                    }

                    @Override
                    public void doOkAction() {
                        doRemoveMember(memberAccount, position);
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RequestCodeMgr && resultCode == RESULT_OK) {
            addMgr((TeamMember) data.getSerializableExtra(SelectClubMgrACNew.RESULT_DATA));
        }
    }

    public void doRemoveMember(final String account, final int position) {
        mClubAction.deletedmin(mTeam.getId(), account,true, new com.htgames.nutspoker.interfaces.RequestCallback() {
                @Override
                public void onResult(int code, String result, Throwable var3) {
                    if(code == 0) {
                        SwipeItemLayout itemView = (SwipeItemLayout) mRecyclerView.getLayoutManager().findViewByPosition(position);
                        LogUtil.i("zzh", "mClubAction.deletedmin(mTeam.ge itemView: " + itemView + "    position: " + position);
                        if (datas.size() > position) {
                            datas.remove(position);
                        }
                        if (mTeamMemberAdapter != null) {
                            mTeamMemberAdapter.closeOpenedSwipeItemLayoutWithAnim();
                            mTeamMemberAdapter.removeItem(position);
                            LogUtil.i("zzh", "removeItem mClubAction.deletedmin(mTeam.ge itemView: " + mTeamMemberAdapter.getItemCount());
                            mTeamMemberAdapter.notifyItemRemoved(position);
                        }
                        initBtnStatus();//删除管理员
                    }
                }

                @Override
                public void onFailed() {
                }
            });
    }

    public void addMgr(final TeamMember member) {
        if (member == null) {
            return;
        }
        RequestTimeLimit.lastGetAmontTime = 0;
        mClubAction.addAdmin(mTeam.getId(), member.getAccount(), new com.htgames.nutspoker.interfaces.RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if (code == ApiCode.CODE_SUCCESS) {
                    ClubMemberItem clubMemberItem = new ClubMemberItem(member);
                    clubMemberItem.adapterType = ClubMemberItem.TYPE_SET_MANAGER_AC;
                    datas.add(clubMemberItem);
                    if (mTeamMemberAdapter != null) {
                        mTeamMemberAdapter.addItem(clubMemberItem);
                        LogUtil.i("zzh", "addItem mClubAction.deletedmin(mTeam.ge itemView: " + mTeamMemberAdapter.getItemCount());
                        mTeamMemberAdapter.notifyItemInserted(mTeamMemberAdapter.getItemCount());
                    }
                    initBtnStatus();//增加管理员
                }
            }

            @Override
            public void onFailed() {

            }
        });
    }
}
