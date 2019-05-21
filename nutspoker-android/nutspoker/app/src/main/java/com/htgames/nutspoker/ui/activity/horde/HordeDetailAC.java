package com.htgames.nutspoker.ui.activity.horde;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiCode;
import com.htgames.nutspoker.chat.contact.activity.EditUserItemActivity;
import com.htgames.nutspoker.chat.main.activity.AddVerifyActivity;
import com.htgames.nutspoker.data.common.UserConstant;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.ui.action.HordeAction;
import com.htgames.nutspoker.ui.activity.horde.util.HordeUpdateManager;
import com.htgames.nutspoker.ui.activity.horde.util.UpdateItem;
import com.htgames.nutspoker.ui.adapter.team.HordeViewAdap;
import com.htgames.nutspoker.ui.adapter.team.HordeViewHead;
import com.htgames.nutspoker.ui.adapter.team.HordeViewItem;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.view.ResultDataView;
import com.htgames.nutspoker.view.TouchableRecyclerView;
import com.htgames.nutspoker.view.switchbutton.SwitchButton;
import com.netease.nim.uikit.chesscircle.entity.HordeEntity;
import com.netease.nim.uikit.chesscircle.entity.TeamEntity;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.interfaces.IClick;
import com.netease.nim.uikit.session.constant.Extras;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 周智慧 on 17/3/21.
 */

public class HordeDetailAC extends BaseActivity implements View.OnClickListener, IClick {
    //头部信息
    @BindView(R.id.horde_name) TextView horde_name;
    @BindView(R.id.horde_vid) TextView horde_vid;
    //酋长功能  开局限制   和  编辑部落信息功能
    @BindView(R.id.rl_create_game_limit) View rl_create_game_limit;
    @BindView(R.id.rl_horde_upper_score_limit) View rl_horde_upper_score_limit;
    @BindView(R.id.rl_horde_upper_score_value) View rl_horde_upper_score_value;
    @BindView(R.id.tv_my_club_score) TextView tv_my_club_score;
    @BindView(R.id.switch_just_creator_create) SwitchButton switch_just_creator_create;
    @BindView(R.id.horde_edit_container) View horde_edit_container;
    //进入部落 或者 申请加入部落
    @BindView(R.id.search_horde_action) Button search_horde_action;

    @BindView(R.id.lv_result) TouchableRecyclerView lv_result;
    @BindView(R.id.mResultDataView) ResultDataView mResultDataView;
    TeamEntity creatorTeamEntity;//部落创建者的俱乐部
    String searchWord;
//    HordeViewAdapter mAdapter;
    public HordeEntity mHordeEntity;
    ArrayList<TeamEntity> teamsFromDetail = new ArrayList<>();
    ArrayList<TeamEntity> teamsFromSearch = new ArrayList<>();
    private ArrayList mDatas = new ArrayList<HordeViewItem>();
    private HordeViewAdap mAdapter;
    public boolean isMeIn;//我是否在这个部落中
    String teamId;//搜索部落成功后加入部落需要teamId字段
    public boolean isMeHordeChief;//我是否是部落的创建者    我们称之为  酋长
    public boolean isMeClubCreator;//我是否是俱乐部部长
    public boolean isMeClubManager;//我是否是俱乐部管理员
    String mClubScore = "";//我的俱乐部在当前部落中的上分
    HordeAction mHordeAction;
    public static void startBySearch(Activity activity, String searchWord, String tid) {
        Intent intent = new Intent(activity, HordeDetailAC.class);
        intent.putExtra(Extras.EXTRA_SEARCH_CLUB_KEY, searchWord);
        intent.putExtra(Extras.EXTRA_TEAM_ID, tid);
        activity.startActivity(intent);
    }

    public static void startByDetail(Activity activity, HordeEntity hordeEntity, boolean isCreator, boolean isManager, String tid) {
        Intent intent = new Intent(activity, HordeDetailAC.class);
        intent.putExtra(Extras.EXTRA_CUSTOMIZATION, hordeEntity);
        intent.putExtra(Extras.EXTRA_TEAM_ID, tid);
        intent.putExtra(Extras.EXTRA_GAME_IS_CREATOR, isCreator);//是否是俱乐部的创建者
        intent.putExtra(Extras.EXTRA_GAME_CREATE_IS_CLUB_MANAGER, isManager);//是否是俱乐部的管理员
        activity.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_horde_list);
        mUnbinder = ButterKnife.bind(this);
        setHeadTitle(R.string.horde_detail);
        mHordeAction = new HordeAction(this, null);
        mAdapter = new HordeViewAdap(mDatas, this, true);
        mAdapter.setAnimateChangesWithDiffUtil(true)
                .setAnimateToLimit(10000)
                .setNotifyMoveOfFilteredItems(false)
                .setNotifyChangeOfUnfilteredItems(true)
                .setAnimationDelay(40)
                .setAnimationOnScrolling(true)
                .setAnimationOnReverseScrolling(true)
                .setOnlyEntryAnimation(true);
        lv_result.setAdapter(mAdapter);
        lv_result.setHasFixedSize(true);
        lv_result.setItemViewCacheSize(0);
        mAdapter.setLongPressDragEnabled(false)
                .setHandleDragEnabled(true)
                .setSwipeEnabled(false)
                .setUnlinkAllItemsOnRemoveHeaders(true)
                .setDisplayHeadersAtStartUp(true)
                .setStickyHeaders(true);
        if (!StringUtil.isSpace(searchWord)) {//来自搜索
//            mAdapter = new HordeViewAdapter(this, teamsFromSearch, HordeViewAdapter.TYPE_SEARCH);
            searchHorde();
        } else if (mHordeEntity != null) {//来自部落大厅
            mResultDataView.setVisibility(View.GONE);
//            mAdapter = new HordeViewAdapter(this, teamsFromDetail, HordeViewAdapter.TYPE_DETAIL);
//            mAdapter.isMeHordeChief = isMeHordeChief;
            updateUI();
            getViewsDetail();
        }
//        lv_result.setAdapter(mAdapter);
        registerObservers(true);
    }

    private void getViewsDetail() {
        mHordeAction.hordeView(mHordeEntity.horde_id, new GameRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {/*{"code":0,"message":"ok","data":[{"id":"4","tid":"25622697","tname":"wsx","vid":"4490316","avatar":"","is_owner":1}]}*/
                teamsFromDetail.clear();
                mDatas.clear();
                JSONArray dataArray = response.optJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++) {
                    try {
                        TeamEntity teamEntity = new TeamEntity();
                        JSONObject itemObject = dataArray.getJSONObject(i);
                        teamEntity.id = itemObject.optString("tid");
                        teamEntity.name = itemObject.optString("tname");
                        teamEntity.vid = itemObject.optString("vid");
                        teamEntity.score = itemObject.optInt("score");
                        teamEntity.is_owner = itemObject.optInt("is_owner");
                        teamEntity.avatar = itemObject.optString("avatar");
                        if (teamEntity.is_owner == 0) {
                            teamsFromDetail.add(teamEntity);
                        } else {
                            creatorTeamEntity = teamEntity;
                        }
                        //计算我的俱乐部在当前部落中的上分值
                        if (teamEntity.id.equals(teamId)) {
                            mClubScore = String.valueOf(teamEntity.score);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (creatorTeamEntity != null) {
                    teamsFromDetail.add(0, creatorTeamEntity);
                }
                for (int i = 0; i < teamsFromDetail.size(); i++) {
                    HordeViewHead head = null;
                    if (i <= 1) {
                        head = new HordeViewHead(i == 0 ? "部落创建者" : "加入的俱乐部");
                    }
                    TeamEntity entity = teamsFromDetail.get(i);
                    HordeViewItem item = new HordeViewItem(entity, isMeHordeChief, head);
                    item.setSwipable(isMeHordeChief && i > 0);
                    item.setItemType(HordeViewItem.Companion.getITEM_TYPE_DETAIL());
                    mDatas.add(item);
                }
//                if (mAdapter != null) {
//                    mAdapter.notifyDataSetChanged();
//                }
                if (mAdapter != null) {
                    mAdapter.updateDataSet(mDatas);
                    if (mAdapter.mAnimatorNotifierObserver != null) {
                        mAdapter.mAnimatorNotifierObserver.notified = false;
                    }
                }
                updateUI();
            }
            @Override
            public void onFailed(int code, JSONObject response) {
                updateUI();
            }
        });
    }

    private void getViewsSearch() {
        mHordeAction.hordeView(mHordeEntity.horde_id, new GameRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {/*{"code":0,"message":"ok","data":[{"id":"4","tid":"25622697","tname":"wsx","vid":"4490316","avatar":"","is_owner":1}]}*/
                teamsFromSearch.clear();
                mDatas.clear();
                JSONArray dataArray = response.optJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++) {
                    try {
                        TeamEntity teamEntity = new TeamEntity();
                        JSONObject itemObject = dataArray.getJSONObject(i);
                        teamEntity.id = itemObject.optString("tid");
                        teamEntity.name = itemObject.optString("tname");
                        teamEntity.score = itemObject.optInt("score");
                        teamEntity.vid = itemObject.optString("vid");
                        teamEntity.is_owner = itemObject.optInt("is_owner");
                        teamEntity.avatar = itemObject.optString("avatar");
                        if (teamEntity.is_owner == 0) {
                            teamsFromSearch.add(teamEntity);
                        } else {
                            creatorTeamEntity = teamEntity;
                        }
                        //计算我的俱乐部在当前部落中的上分值
                        if (teamEntity.id.equals(teamId)) {
                            mClubScore = String.valueOf(teamEntity.score);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (creatorTeamEntity != null) {
                    teamsFromSearch.add(0, creatorTeamEntity);
                }
                if (!isMeIn && creatorTeamEntity != null) {//不在部落的话只显示第一个俱乐部(部落创建的俱乐部)
                    teamsFromSearch.clear();
                    teamsFromSearch.add(creatorTeamEntity);
                }
                for (int i = 0; i < teamsFromSearch.size(); i++) {
                    HordeViewHead head = null;
                    if (i <= 1) {
                        head = new HordeViewHead(i == 0 ? "部落创建者" : "加入的俱乐部");
                    }
                    TeamEntity entity = teamsFromSearch.get(i);
                    HordeViewItem item = new HordeViewItem(entity, isMeHordeChief, head);
                    item.setSwipable(isMeHordeChief && i > 0);
                    item.setItemType(HordeViewItem.Companion.getITEM_TYPE_SEARCH());
                    mDatas.add(item);
                }
//                if (mAdapter != null) {
//                    mAdapter.isMeHordeChief = isMeHordeChief;
//                    mAdapter.isMeIn = isMeIn;
//                    mAdapter.notifyDataSetChanged();
//                }
                if (mAdapter != null) {
                    mAdapter.updateDataSet(mDatas);
                    if (mAdapter.mAnimatorNotifierObserver != null) {
                        mAdapter.mAnimatorNotifierObserver.notified = false;
                    }
                }
                updateUI();
            }
            @Override
            public void onFailed(int code, JSONObject response) {
                updateUI();
            }
        });
    }

    private void initData() {
        teamId = getIntent().getStringExtra(Extras.EXTRA_TEAM_ID);
        isMeClubCreator = getIntent().getBooleanExtra(Extras.EXTRA_GAME_IS_CREATOR, false);
        isMeClubManager = getIntent().getBooleanExtra(Extras.EXTRA_GAME_CREATE_IS_CLUB_MANAGER, false);
        searchWord = getIntent().getStringExtra(Extras.EXTRA_SEARCH_CLUB_KEY);
        if (StringUtil.isSpace(searchWord)) {//搜索关键词为空表示不是从搜索页进来的，而是从部落大厅进来的，显示部落详情，那么isMeIn肯定是true
            isMeIn = true;
        }
        mHordeEntity = (HordeEntity) getIntent().getSerializableExtra(Extras.EXTRA_CUSTOMIZATION);
        if (mHordeEntity != null) {
            if (mHordeEntity.owner.equals(DemoCache.getAccount()) && mHordeEntity.tid.equals(teamId)) {
                isMeHordeChief = true;//我是酋长
            }
        }
    }

    private void searchHorde() {
        mHordeAction.searchHorde(teamId, searchWord, new GameRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (response != null && !response.isNull("data") && !StringUtil.isSpace(response.optString("data")) && response.optJSONObject("data") != null) {
                    mHordeEntity = new HordeEntity();
                    JSONObject dataObject = response.optJSONObject("data");
                    isMeIn = dataObject.optInt("is_inside") == 1;
                    mHordeEntity.horde_vid = dataObject.optString("horde_vid");
                    mHordeEntity.name = dataObject.optString("name");
                    mHordeEntity.ctime = dataObject.optLong("ctime");
                    mHordeEntity.tid = dataObject.optString("tid");
                    mHordeEntity.owner = dataObject.optString("owner");
                    mHordeEntity.is_score = dataObject.optInt("is_score");
                    mHordeEntity.is_control = dataObject.optInt("is_control");
                    mHordeEntity.horde_id = dataObject.optString("horde_id");
                    mHordeEntity.is_modify = dataObject.optInt("is_modify");
                    if (mHordeEntity.owner.equals(DemoCache.getAccount()) && mHordeEntity.tid.equals(teamId)) {
                        isMeHordeChief = true;//我是酋长
                    }
                }
                afterSearch();
            }
            @Override
            public void onFailed(int code, JSONObject response) {
                afterSearch();
            }
        });
    }

    private void afterSearch() {
        if (mHordeEntity == null) {
            mResultDataView.nullDataShow(R.string.no_data);
        } else {
            mResultDataView.successShow();
            getViewsSearch();
        }
        updateUI();
    }

    private void updateUI() {
        if (mHordeEntity == null) {
            return;
        }
        switch_just_creator_create.setChecked(mHordeEntity.is_control == 1);
        horde_name.setText(mHordeEntity.name);
        horde_vid.setText("ID：" + mHordeEntity.horde_vid);
        rl_create_game_limit.setVisibility(isMeHordeChief ? View.VISIBLE : View.GONE);
        //只有酋长才能看到"上分控制"的入口
        rl_horde_upper_score_limit.setVisibility(isMeHordeChief ? View.VISIBLE : View.GONE);
        rl_horde_upper_score_limit.setOnClickListener(isMeHordeChief ? this : null);
        //不是酋长 并且 此部落的"上分控制"打开的情况下才显示这个ui组件，否则gone
        rl_horde_upper_score_value.setVisibility((!StringUtil.isSpace(searchWord) && !isMeIn) || (mHordeEntity != null && mHordeEntity.is_score == 0) || isMeHordeChief ? View.GONE : View.VISIBLE);
        tv_my_club_score.setText(mClubScore);

        switch_just_creator_create.setOnClickListener(this);
        search_horde_action.setOnClickListener(this);
        horde_edit_container.setVisibility(isMeHordeChief ? View.VISIBLE : View.GONE);
        horde_edit_container.setOnClickListener(this);
        if (!StringUtil.isSpace(searchWord)) {//来自搜索的话就显示"进入部落"或者"申请加入部落"这两种情况
            search_horde_action.setText(isMeIn ? R.string.horde_enter : R.string.horde_apply);
        } else {//来自部落大厅   1酋长 解散部落   2部长 退出部落   3gone掉
            if (isMeHordeChief) {
                search_horde_action.setText(R.string.horde_dismiss);
                search_horde_action.setBackgroundResource(R.drawable.bg_login_btn_warning);
            } else {
                if (isMeClubCreator) {
                    search_horde_action.setText(R.string.horde_quite);
                    search_horde_action.setBackgroundResource(R.drawable.bg_login_btn_warning);
                } else {
                    search_horde_action.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mHordeAction != null) {
            mHordeAction.onDestroy();
            mHordeAction = null;
        }
        registerObservers(false);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //修改部落信息回调
        if (requestCode == EditUserItemActivity.REQUESTCODE_EDIT_HORDE && resultCode == RESULT_OK && data != null) {
            String newHordeName = data.getStringExtra(Extras.EXTRA_DATA);
            HordeUpdateManager.getInstance().execludeCallback(new UpdateItem(UpdateItem.UPDATE_TYPE_NAME)
                    .setHordeId(mHordeEntity == null ? "" : mHordeEntity.horde_id)
                    .setName(newHordeName)
                    .setIsControl(mHordeEntity == null ? 0 : mHordeEntity.is_control));
            if (!StringUtil.isSpace(newHordeName) && mHordeEntity != null) {
                if (horde_name != null) {
                    horde_name.setText(newHordeName);
                }
                mHordeEntity.name = newHordeName;
                mHordeEntity.is_modify = 1;
            }
        }
    }

    private void registerObservers(boolean register) {
        if (register) {
            HordeUpdateManager.getInstance().registerCallback(hordeUpdateCallback);
        } else {
            HordeUpdateManager.getInstance().unregisterCallback(hordeUpdateCallback);
        }
    }

    HordeUpdateManager.HordeUpdateCallback hordeUpdateCallback = new HordeUpdateManager.HordeUpdateCallback() {
        @Override
        public void onUpdated(UpdateItem item) {
            if (item == null) {
                return;
            }
            if (item.updateType == UpdateItem.UPDATE_TYPE_NAME && !StringUtil.isSpace(item.name) && horde_name != null && mHordeEntity != null) {
                horde_name.setText(item.name);
                mHordeEntity.name = item.name;
            }
            if (item.updateType == UpdateItem.UPDATE_TYPE_IS_CONTROL) {
                mHordeEntity.is_control = item.is_control;
            }
            if (item.updateType == UpdateItem.UPDATE_TYPE_IS_SCORE) {
                if (mHordeEntity != null && item.horde_id.equals(mHordeEntity.horde_id)) {
                    mHordeEntity.is_score = item.is_score;
                }
            }
            if (item.updateType == UpdateItem.UPDATE_TYPE_SET_SCORE) {
                if (!StringUtil.isSpace(searchWord)) {//来自搜索
                    getViewsSearch();
                } else if (mHordeEntity != null) {
                    getViewsDetail();
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.switch_just_creator_create) {
            mHordeAction.updateHordeCreateGameLimit(mHordeEntity == null ? "" : mHordeEntity.horde_id, switch_just_creator_create.isChecked() ? 1 : 0, new GameRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    int is_control = switch_just_creator_create.isChecked() ? 1 : 0;
                    if (mHordeEntity != null) {
                        mHordeEntity.is_control = is_control;
                    }
                    HordeUpdateManager.getInstance().execludeCallback(new UpdateItem(UpdateItem.UPDATE_TYPE_IS_CONTROL)
                            .setHordeId(mHordeEntity == null ? "" : mHordeEntity.horde_id)
                            .setName(mHordeEntity == null ? "" : mHordeEntity.name)
                            .setIsControl(is_control));
                }
                @Override
                public void onFailed(int code, JSONObject response) {
                    boolean isChecked = switch_just_creator_create.isChecked();
                    switch_just_creator_create.setChecked(!isChecked);//复位
                }
            });
        } else if (viewId == R.id.search_horde_action && mHordeEntity != null) {
            if (!StringUtil.isSpace(searchWord)) {//搜索模式
                if (isMeIn) {
                    HordeHallAC.start(HordeDetailAC.this, mHordeEntity, isMeClubCreator, isMeClubManager, teamId);
                } else {
                    AddVerifyActivity.startForApplyHorde(HordeDetailAC.this, mHordeEntity, teamId, AddVerifyActivity.TYPE_VERIFY_HORDE);
                }
            } else {//部落详情模式
                if (isMeHordeChief) {//解散部落
                    showDismissHordeDialog(0);
                } else {
                    if (isMeClubCreator) {//退出部落
                        showQuitHordeDialog(0);
                    }
                }
            }
        } else if (viewId == R.id.horde_edit_container) {
            EditUserItemActivity.startEditHordeByResult(HordeDetailAC.this, mHordeEntity, UserConstant.KEY_EDIT_HORDE_NAME, EditUserItemActivity.REQUESTCODE_EDIT_HORDE);//修改部落信息，目前只支持修改部落名称
        } else if (viewId == R.id.rl_horde_upper_score_limit) {
            if (!StringUtil.isSpace(searchWord)) {//来自搜索
                HordeUpperLimitAC.Companion.start(this, mHordeEntity, teamsFromSearch);
            } else if (mHordeEntity != null) {
                HordeUpperLimitAC.Companion.start(this, mHordeEntity, teamsFromDetail);
            }
        }
    }

    @Override
    public void onDelete(int position) {
        showRemoveClubDialog(position);
    }

    @Override
    public void onClick(int position) {
    }

    @Override
    public void onLongClick(final int position) {
        TeamEntity teamEntity = position < mAdapter.getItemCount() && mAdapter.getItem(position) instanceof HordeViewItem ? ((HordeViewItem) mAdapter.getItem(position)).getMTeamEntity() : null;
//        if (StringUtil.isSpace(searchWord)) {
//            teamEntity = teamsFromDetail == null || teamsFromDetail.size() <= position ? null : teamsFromDetail.get(position);
//        } else {
//            teamEntity = teamsFromSearch == null || teamsFromSearch.size() <= position ? null : teamsFromSearch.get(position);
//        }
        if (teamEntity == null) {
            return;
        }
        if (teamEntity.id.equals(teamId)) {
            return;//酋长的俱乐部不移除
        }
        CustomAlertDialog alertDialog = new CustomAlertDialog(this);
        alertDialog.setTitle(R.string.delete_tip);
        String title = "确定移除俱乐部" + "“" + teamEntity.name +  "”";
        alertDialog.addItem(title, new CustomAlertDialog.onSeparateItemClickListener() {
            @Override
            public void onClick() {
                showRemoveClubDialog(position);
            }
        });
        alertDialog.show();
    }

    EasyAlertDialog removeClubDialog;
    private void showRemoveClubDialog(final int position) {
        TeamEntity teamEntity = position < mAdapter.getItemCount() && mAdapter.getItem(position) instanceof HordeViewItem ? ((HordeViewItem) mAdapter.getItem(position)).getMTeamEntity() : null;
//        if (StringUtil.isSpace(searchWord)) {
//            teamEntity = teamsFromDetail == null || teamsFromDetail.size() <= position ? null : teamsFromDetail.get(position);
//        } else {
//            teamEntity = teamsFromSearch == null || teamsFromSearch.size() <= position ? null : teamsFromSearch.get(position);
//        }
        if (teamEntity == null) {
            return;
        }
        String message = "您确定移除俱乐部" + "“" + teamEntity.name +  "”";
        final TeamEntity finalTeamEntity = teamEntity;
        removeClubDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "",
                message, getString(R.string.ok), getString(R.string.cancel), true,
                new EasyAlertDialogHelper.OnDialogActionListener() {

                    @Override
                    public void doCancelAction() {
                        removeClubDialog.dismiss();
                    }

                    @Override
                    public void doOkAction() {
                        mHordeAction.hordeKill(finalTeamEntity.id, mHordeEntity.horde_id, new GameRequestCallback() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                for (int i = 0; i < teamsFromDetail.size(); i++) {
                                    TeamEntity entity = teamsFromDetail.get(i);
                                    if (entity.id.equals(finalTeamEntity.id)) {
                                        teamsFromDetail.remove(entity);
                                        break;
                                    }
                                }
                                for (int i = 0; i < teamsFromSearch.size(); i++) {
                                    TeamEntity entity = teamsFromSearch.get(i);
                                    if (entity.id.equals(finalTeamEntity.id)) {
                                        teamsFromSearch.remove(entity);
                                        break;
                                    }
                                }
//                                View itemView = lv_result.getLayoutManager().findViewByPosition(position);
//                                if (itemView != null) {
//                                    View view = itemView.findViewById(R.id.item_contact_swipe_root);
//                                    if (view instanceof SwipeItemLayout && mAdapter != null) {
//                                        mAdapter.notifyItemRemovedCustom((SwipeItemLayout) view);
//                                        mAdapter.notifyItemRemoved(position);
//                                    }
//                                }
                                if (mDatas.size() > position) {
                                    mDatas.remove(position);
                                }
                                if (mAdapter != null) {
                                    mAdapter.closeOpenedSwipeItemLayoutWithAnim();
                                    mAdapter.removeItem(position);
                                    mAdapter.notifyItemRemoved(position);
                                }
                            }

                            @Override
                            public void onFailed(int code, JSONObject response) {
                                if (code == ApiCode.CODE_QUITE_HORDE_HAVING_GAME) {
                                    Toast.makeText(HordeDetailAC.this, "有正在进行的游戏，无法移除", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            removeClubDialog.setMessage(message);
            removeClubDialog.show();
        }
    }

    EasyAlertDialog quitHordeDialog;
    private void showQuitHordeDialog(int position) {
        if (mHordeEntity == null) {
            return;
        }
        if (quitHordeDialog == null) {
            quitHordeDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "",
                    "您确定退出部落" + "“" + mHordeEntity.name +  "”", getString(R.string.ok), getString(R.string.cancel), true,
                    new EasyAlertDialogHelper.OnDialogActionListener() {

                        @Override
                        public void doCancelAction() {
                            quitHordeDialog.dismiss();
                        }

                        @Override
                        public void doOkAction() {
                            mHordeAction.hordeQuit(teamId, mHordeEntity.horde_id, new GameRequestCallback() {
                                @Override
                                public void onSuccess(JSONObject response) {
                                    HordeUpdateManager.getInstance().execludeCallback(new UpdateItem(UpdateItem.UPDATE_TYPE_CANCEL_HORDE)
                                            .setHordeId(mHordeEntity == null ? "" : mHordeEntity.horde_id));
                                    Intent intent = new Intent(HordeDetailAC.this, HordeAC.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    HordeDetailAC.this.startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onFailed(int code, JSONObject response) {

                                }
                            });
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            quitHordeDialog.show();
        }
    }

    EasyAlertDialog dismissHordeDialog;
    private void showDismissHordeDialog(int position) {
        if (mHordeEntity == null) {
            return;
        }
        if (dismissHordeDialog == null) {
            dismissHordeDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "",
                    "您确定解散部落" + "“" + mHordeEntity.name +  "”", getString(R.string.ok), getString(R.string.cancel), true,
                    new EasyAlertDialogHelper.OnDialogActionListener() {

                        @Override
                        public void doCancelAction() {
                            dismissHordeDialog.dismiss();
                        }

                        @Override
                        public void doOkAction() {
                            mHordeAction.hordeCancel(mHordeEntity.horde_id, teamId, new GameRequestCallback() {
                                @Override
                                public void onSuccess(JSONObject response) {
                                    HordeUpdateManager.getInstance().execludeCallback(new UpdateItem(UpdateItem.UPDATE_TYPE_CANCEL_HORDE)
                                            .setHordeId(mHordeEntity == null ? "" : mHordeEntity.horde_id));
                                    Intent intent = new Intent(HordeDetailAC.this, HordeAC.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    HordeDetailAC.this.startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onFailed(int code, JSONObject response) {
                                    if (code == ApiCode.CODE_HORDE_CANCEL_WITH_CLUB) {
                                        showCancelHordeWithClubDialog();
                                    }
                                }
                            });
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            dismissHordeDialog.show();
        }
    }

    EasyAlertDialog topUpDialog;
    private void showCancelHordeWithClubDialog() {
        if (topUpDialog == null) {
            topUpDialog = EasyAlertDialogHelper.createOneButtonDiolag(this, "",
                    "部落中存在俱乐部，无法解散", getString(R.string.ok), true,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            topUpDialog.show();
        }
    }

}
