package com.htgames.nutspoker.ui.activity.horde;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiCode;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.ui.action.HordeAction;
import com.htgames.nutspoker.ui.activity.horde.util.HordeUpdateManager;
import com.htgames.nutspoker.ui.activity.horde.util.UpdateItem;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.ui.inputfilter.NameLengthFilter;
import com.htgames.nutspoker.ui.inputfilter.NoSpaceAndEnterInputFilter;
import com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.session.constant.Extras;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 周智慧 on 17/3/20.
 */

public class HordeCreateAC extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.edt_horde_create_name) ClearableEditTextWithIcon edt_horde_create_name;
    @BindView(R.id.btn_finish) Button btn_finish;
    HordeAction mHordeAction;
    String teamId;
    public static void startForResult(Activity activity, String tid, int requestCode) {
        Intent intent = new Intent(activity, HordeCreateAC.class);
        intent.putExtra(Extras.EXTRA_TEAM_ID, tid);
        activity.startActivityForResult(intent, requestCode);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_horde);
        mUnbinder = ButterKnife.bind(this);
        mHordeAction = new HordeAction(this, null);
        teamId = getIntent().getStringExtra(Extras.EXTRA_TEAM_ID);
        btn_finish.setOnClickListener(this);
        edt_horde_create_name.setFilters(new InputFilter[]{new NoSpaceAndEnterInputFilter(), new NameLengthFilter(ClubConstant.MAX_HORDE_NAME_LENGTH)});
        setHeadTitle(R.string.text_horde_create);
    }

    @Override
    protected void onDestroy() {
        if (mHordeAction != null) {
            mHordeAction.onDestroy();
            mHordeAction = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btn_finish) {
            String teamName = edt_horde_create_name.getText().toString();
            if (TextUtils.isEmpty(teamName)) {
                Toast.makeText(getApplicationContext(), R.string.horde_create_name_not_null, Toast.LENGTH_SHORT).show();
                return;
            }
            createHorde();
        }
    }

    private void createHorde() {
        mHordeAction.createHorde(teamId, edt_horde_create_name.getText().toString(), new GameRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {//{"code":0,"message":"ok","data":{"horde_id":"1","horde_vid":2513096}}
                HordeUpdateManager.getInstance().execludeCallback(new UpdateItem(UpdateItem.UPDATE_TYPE_CREATE_HORDE));
                setResult(RESULT_OK);
                Toast.makeText(ChessApp.sAppContext, R.string.create_team_success, Toast.LENGTH_SHORT).show();
                finish();
            }
            @Override
            public void onFailed(int code, JSONObject response) {
                if (code == ApiCode.CODE_NICKNAME_EXISTED || code == ApiCode.CODE_UPDATE_HORDE_NAME_EXISTED || code == ApiCode.CODE_MODIFY_CLUB_NAME_ALREADY_EXISTED) {
                    showNicknameExistedDialog();//昵称已经被占用
                } else if (code == ApiCode.CODE_GAME_NAME_LENGTH_LONG) {//名称太长
                    Toast.makeText(ChessApp.sAppContext, "部落名称不能超过20个字符", Toast.LENGTH_SHORT).show();
                } else if (code == ApiCode.CODE_CREATE_HORDE_LIMIT) {//一个俱乐部只能创建一个联盟
                    Toast.makeText(ChessApp.sAppContext, R.string.team_cannot_create_more_horde, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChessApp.sAppContext, R.string.create_team_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    EasyAlertDialog nicknameExistedDialog;
    private void showNicknameExistedDialog() {
        if (nicknameExistedDialog == null) {
            nicknameExistedDialog = EasyAlertDialogHelper.createOneButtonDiolag(this, "",
                    "部落名称已经存在", getString(R.string.ok), true,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            nicknameExistedDialog.show();
        }
    }
}
