package com.htgames.nutspoker.ui.activity.horde;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.ui.action.HordeAction;
import com.htgames.nutspoker.ui.activity.horde.util.HordeUpdateManager;
import com.htgames.nutspoker.ui.activity.horde.util.UpdateItem;
import com.htgames.nutspoker.ui.adapter.team.HordeAdapter;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.netease.nim.uikit.chesscircle.entity.HordeEntity;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.interfaces.IClick;
import com.netease.nim.uikit.session.constant.Extras;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 周智慧 on 17/3/20.
 */

public class HordeAC extends BaseActivity implements View.OnClickListener, IClick {
    public static int REQUEST_CODE_CREATE_HORDE = 0;
    boolean isMeClubCreator = false;
    boolean isMeClubManager = false;
    String teamId;
    HordeAction mHordeAction;
    HordeEntity myCreatedHorde;
    List<HordeEntity> mList = new ArrayList<>();
    //部落为空的情况相关view
    @BindView(R.id.ll_club_list_null) View ll_club_list_null;
    @BindView(R.id.horde_big_icon) TextView horde_big_icon;
    @BindView(R.id.btn_club_create) Button btn_club_create;
    @BindView(R.id.btn_club_join) Button btn_club_join;
    @BindView(R.id.tv_your_club_none_horde) TextView tv_your_club_none_horde;
    @BindView(R.id.mRecyclerView) RecyclerView mRecyclerView;
    HordeAdapter mAdapter;
    ArrayList<HordeEntity> costList;

    public static void start(Activity activity, String tid, boolean isCreator, boolean isManager, ArrayList<HordeEntity> hordeList, ArrayList<HordeEntity> costList) {
        Intent intent = new Intent();
        intent.putExtra(Extras.EXTRA_TEAM_ID, tid);
        intent.putExtra(Extras.EXTRA_GAME_IS_CREATOR, isCreator);//是否是俱乐部的创建者
        intent.putExtra(Extras.EXTRA_GAME_CREATE_IS_CLUB_MANAGER, isManager);//是否是俱乐部的管理员
        intent.putExtra(Extras.EXTRA_PHOTO_LISTS, hordeList);
        intent.putExtra(Extras.EXTRA_HORDE_COST_LSIT, costList);
        intent.setClass(activity, HordeAC.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isMeClubCreator = getIntent().getBooleanExtra(Extras.EXTRA_GAME_IS_CREATOR, false);
        isMeClubManager = getIntent().getBooleanExtra(Extras.EXTRA_GAME_CREATE_IS_CLUB_MANAGER, false);
        teamId = getIntent().getStringExtra(Extras.EXTRA_TEAM_ID);
        mList = (List<HordeEntity>) getIntent().getSerializableExtra(Extras.EXTRA_PHOTO_LISTS);
        if (mList == null) {
            mList = new ArrayList<>();//防止空指针
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_club);
        mUnbinder = ButterKnife.bind(this);
        initHead();
        initNullData();//初始化并显示没有部落相关ui
        mAdapter = new HordeAdapter(this, mList, this);
        mRecyclerView.setAdapter(mAdapter);
        mHordeAction = new HordeAction(this, null);
        updateUI();
        registerObservers(true);
        costList = (ArrayList<HordeEntity>) getIntent().getSerializableExtra(Extras.EXTRA_HORDE_COST_LSIT);
        if (costList == null || costList.size() <= 0) {
            getCostlist();
        }
    }

    private void getCostlist() {
        mHordeAction.getCostList(teamId, new GameRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (response == null || response.optJSONArray("data") == null || response.optJSONArray("data").length() <= 0) {
                    return;
                }
                costList = new ArrayList<HordeEntity>();
                JSONArray dataArray = response.optJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++) {
                    HordeEntity hordeEntity = new HordeEntity();
                    JSONObject item = dataArray.optJSONObject(i);
                    hordeEntity.name = item.optString("name");
                    hordeEntity.horde_id = item.optString("horde_id");
                    hordeEntity.money = item.optInt("money");
                    hordeEntity.is_my = item.optInt("is_my");
                    costList.add(hordeEntity);
                }
            }
            @Override
            public void onFailed(int code, JSONObject response) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getHordeList();
    }

    private void initHead() {
        setHeadTitle(R.string.text_horde);
        if (isMeClubCreator) {
            Drawable tv_head_right_drawable = getResources().getDrawable(R.mipmap.btn_club_more);
            tv_head_right_drawable.setBounds(0, 0, tv_head_right_drawable.getIntrinsicWidth(), tv_head_right_drawable.getIntrinsicHeight());
            TextView tv_head_right = ((TextView) findViewById(R.id.tv_head_right));//右边"更多"
            tv_head_right.setVisibility(View.VISIBLE);
            tv_head_right.setText("");
            tv_head_right.setCompoundDrawables(tv_head_right_drawable, null, null, null);
            tv_head_right.setOnClickListener(this);
        }
    }

    private void initNullData() {
        if (isMeClubManager) {
            btn_club_create.setVisibility(View.GONE);
            btn_club_join.setVisibility(View.GONE);
            tv_your_club_none_horde.setVisibility(View.VISIBLE);
        }
        if (isMeClubCreator) {
            btn_club_create.setVisibility(View.VISIBLE);
            btn_club_join.setVisibility(View.VISIBLE);
            tv_your_club_none_horde.setVisibility(View.GONE);
        }
        Drawable iconTopDrawable = getResources().getDrawable(R.mipmap.icon_horde_null);
        iconTopDrawable.setBounds(0, 0, iconTopDrawable.getIntrinsicWidth(), iconTopDrawable.getIntrinsicHeight());
        horde_big_icon.setCompoundDrawables(null, iconTopDrawable, null, null);
        ((FrameLayout.LayoutParams) horde_big_icon.getLayoutParams()).setMargins(0, ScreenUtil.dp2px(this, 100), 0, 0);
        btn_club_create.setText(R.string.text_horde_create);
        btn_club_create.setOnClickListener(this);
        btn_club_join.setText(R.string.text_horde_join);
        btn_club_join.setOnClickListener(this);
    }
    private void getHordeList() {
        mHordeAction.hordeList(teamId, new GameRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {//{"code":0,"message":"ok","data":[{"id":"4","horde_vid":"3939732","name":"dgtf","ctime":"1490010504","tid":"25622697","owner":"10033","is_my":1,"horde_id":"4"}]}
                mList.clear();
                myCreatedHorde = null;
                try {
                    JSONArray data = response.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject gameJson = data.getJSONObject(i);
                        HordeEntity hordeEntity = new HordeEntity();
                        hordeEntity.horde_id = gameJson.optString("horde_id");
                        hordeEntity.horde_vid = gameJson.optString("horde_vid");
                        hordeEntity.name = gameJson.optString("name");
                        hordeEntity.ctime = gameJson.optLong("ctime");
                        hordeEntity.tid = gameJson.optString("tid");//创建者的俱乐部id
                        hordeEntity.owner = gameJson.optString("owner");//创建者的uid
                        hordeEntity.is_my = gameJson.optInt("is_my");
                        hordeEntity.playing_count = gameJson.optInt("playing_count");
                        hordeEntity.is_score = gameJson.optInt("is_score");
                        hordeEntity.is_control = gameJson.optInt("is_control");
                        hordeEntity.is_modify = gameJson.optInt("is_modify");
//                        if (!hordeEntity.tid.equals(teamId)) {
                        if (hordeEntity.is_my == 0) {
                            mList.add(hordeEntity);
                        } else {
                            myCreatedHorde = hordeEntity;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (myCreatedHorde != null) {
                    mList.add(0, myCreatedHorde);
                }
                if (mList.size() > 0 && mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
                updateUI();
            }
            @Override
            public void onFailed(int code, JSONObject response) {
            }
        });
    }

    public void updateUI() {
        if (mRecyclerView == null || ll_club_list_null == null || tv_your_club_none_horde == null) {
            return;
        }
        if (mList.size() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            ll_club_list_null.setVisibility(View.GONE);
            tv_your_club_none_horde.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            ll_club_list_null.setVisibility(View.VISIBLE);
            initHead();
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.tv_head_right) {
            if (clubPopupWindow != null && clubPopupWindow.isShowing()) {
                dissmissClubPopWindow();
            } else {
                showClubPopWindow();
            }
        } else if (viewId == R.id.ll_pop_club_create || viewId == R.id.btn_club_create) {
            dissmissClubPopWindow();
            HordeCreateAC.startForResult(this, teamId, REQUEST_CODE_CREATE_HORDE);
        } else if (viewId == R.id.ll_pop_club_join || viewId == R.id.btn_club_join) {
            dissmissClubPopWindow();
            int joinedHordeNum = mList == null ? 0 : (myCreatedHorde == null ? mList.size() : mList.size() - 1);
            if (joinedHordeNum >= 3) {
                showJoinHordeLimitDialog();
                return;
            }
            HordeJoinAC.start(this, teamId);
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

    /**
     * 显示popWindow
     */
    PopupWindow clubPopupWindow = null;
    View ll_pop_club_create;
    View ll_pop_club_join;

    private void showClubPopWindow() {
        if (clubPopupWindow == null) {
            View popView = LayoutInflater.from(this).inflate(R.layout.pop_club_add, null);
            clubPopupWindow = new PopupWindow(popView);
            //获取popwindow焦点
            clubPopupWindow.setFocusable(true);
            //设置popwindow如果点击外面区域，便关闭。
            clubPopupWindow.setOutsideTouchable(true);
            clubPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
            clubPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            clubPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            //设置popwindow出现和消失动画
//        clubPopupWindow.setAnimationStyle(R.style.PopMenuAnimation);
            ll_pop_club_create = popView.findViewById(R.id.ll_pop_club_create);
            ll_pop_club_create.setOnClickListener(this);
            ll_pop_club_join = popView.findViewById(R.id.ll_pop_club_join);
            ll_pop_club_join.setOnClickListener(this);
            Drawable leftDrawble = getResources().getDrawable(R.mipmap.icon_horde_popup);
            leftDrawble.setBounds(0, 0, leftDrawble.getIntrinsicWidth(), leftDrawble.getIntrinsicHeight());
            TextView tv_pop_club_create = ((TextView) popView.findViewById(R.id.tv_pop_club_create));
            tv_pop_club_create.setText(getResources().getString(R.string.text_horde_create));
            tv_pop_club_create.setCompoundDrawables(leftDrawble, null, null, null);
            ((TextView) popView.findViewById(R.id.tv_pop_club_join)).setText(getResources().getString(R.string.text_horde_join));
        }
        clubPopupWindow.showAsDropDown(findViewById(R.id.tv_head_right), -ScreenUtil.dp2px(this, 9), -ScreenUtil.dp2px(this, 16));
    }

    public void dissmissClubPopWindow() {
        if (clubPopupWindow != null && clubPopupWindow.isShowing()) {
            clubPopupWindow.dismiss();
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

    private void registerObservers(boolean register) {
        if (register) {
            HordeUpdateManager.getInstance().registerCallback(hordeUpdateCallback);
        } else {
            HordeUpdateManager.getInstance().unregisterCallback(hordeUpdateCallback);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_HORDE && resultCode ==RESULT_OK) {
            //do nothing, 观察者已经做了  item.updateType == UpdateItem.UPDATE_TYPE_CREATE_HORDE  重新获取列表
        }
    }

    HordeUpdateManager.HordeUpdateCallback hordeUpdateCallback = new HordeUpdateManager.HordeUpdateCallback() {
        @Override
        public void onUpdated(UpdateItem item) {
            if (item == null || mAdapter == null) {
                return;
            }
            if (item.updateType == UpdateItem.UPDATE_TYPE_CREATE_HORDE) {
                getHordeList();
                return;
            }
            if (mList != null && mList.size() > 0) {
                for (int i = 0; i < mList.size(); i++) {
                    HordeEntity hordeEntity = mList.get(i);
                    if (hordeEntity.horde_id.equals(item.horde_id)) {
                        if (item.updateType == UpdateItem.UPDATE_TYPE_NAME && !StringUtil.isSpace(item.name)) {
                            hordeEntity.name = item.name;
                            hordeEntity.is_modify = 1;//表示已经修改过部落名称
                            mAdapter.notifyItemChanged(i);
                        }
                        if (item.updateType == UpdateItem.UPDATE_TYPE_IS_CONTROL) {
                            hordeEntity.is_control = item.is_control;//这个不需要更新adapter
                            mAdapter.notifyItemChanged(i);
                        }
                        if (item.updateType == UpdateItem.UPDATE_TYPE_CANCEL_HORDE) {
                            mList.remove(hordeEntity);
                            mAdapter.notifyDataSetChanged();
                        }
                        if (item.updateType == UpdateItem.UPDATE_TYPE_IS_SCORE) {
                            hordeEntity.is_score = item.is_score;
                        }
                        break;
                    }
                }
            }
        }
    };

    @Override
    public void onDelete(int position) {

    }

    @Override
    public void onClick(int position) {
        HordeEntity hordeEntity = mList == null || mList.size() <= position ? null : mList.get(position);
        if (costList != null && costList.size() > 0) {
            for (HordeEntity costItem : costList) {
                if (costItem.horde_id.equals(hordeEntity.horde_id)) {
                    hordeEntity.money = costItem.money;
                    break;
                }
            }
        }
        HordeHallAC.start(this, hordeEntity, isMeClubCreator, isMeClubManager, teamId);
    }

    @Override
    public void onLongClick(int position) {

    }
}
