package com.htgames.nutspoker.ui.activity.Club;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiCode;
import com.htgames.nutspoker.data.common.UserConstant;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.net.RequestTimeLimit;
import com.htgames.nutspoker.tool.NameTrimTools;
import com.htgames.nutspoker.ui.action.EditClubInfoAction;
import com.htgames.nutspoker.ui.activity.System.ShopActivity;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.ui.inputfilter.NameLengthFilter;
import com.htgames.nutspoker.ui.inputfilter.NameRuleFilter;
import com.htgames.nutspoker.util.word.CHTool;
import com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.team.model.Team;

import org.json.JSONObject;

/**
 * 修改俱乐部
 */
public class EditClubItemActivity extends BaseActivity {
    private ClearableEditTextWithIcon edt_name;
    private String sessionId;
    private String data;
    private TextView mHeadRightTV;
    private int key;
    private EditClubInfoAction mEditClubInfoAction;
    private int maxEditLength = ClubConstant.MAX_CLUB_NAME_LENGTH;
    private View club_name_can_modify_container;
    View nickname_times_container;
    TextView consume_num;
    TextView remain_num;
    TextView modify_name_bottom_desc;
    TextView tv_can_modify_club_name_time;

    public static void start(Activity activity , String sessionId , int key , String data) {
        Intent intent = new Intent(activity , EditClubItemActivity.class);
        intent.putExtra(Extras.EXTRA_SESSION_ID, sessionId);
        intent.putExtra(Extras.EXTRA_EDIT_KEY, key);
        intent.putExtra(Extras.EXTRA_DATA, data);
        activity.startActivity(intent);
    }

    public static void start(Activity activity , String sessionId , int key , String data , int requestCode) {
        Intent intent = new Intent(activity , EditClubItemActivity.class);
        intent.putExtra(Extras.EXTRA_SESSION_ID, sessionId);
        intent.putExtra(Extras.EXTRA_EDIT_KEY, key);
        intent.putExtra(Extras.EXTRA_DATA, data);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        sessionId = getIntent().getStringExtra(Extras.EXTRA_SESSION_ID);
        data = getIntent().getStringExtra(Extras.EXTRA_DATA);
        data = NameTrimTools.getNameTrim(data);
        key = getIntent().getIntExtra(Extras.EXTRA_EDIT_KEY, UserConstant.KEY_ALIAS);
        initData();
        initView();
        mEditClubInfoAction = new EditClubInfoAction(this , null);
        UpdateUIByKey();
        if (key == ClubConstant.KEY_NAME) {//修改俱乐部名称需要特殊处理，30天之内只能修改一次俱乐部名称
            initModifyClubNameUI();
        }
    }

    private void initData() {
        switch (key) {
            case ClubConstant.KEY_NAME:
                maxEditLength = ClubConstant.MAX_CLUB_NAME_LENGTH;//最大长度
                break;
            case ClubConstant.KEY_INTRODUCE:
                maxEditLength = ClubConstant.MAX_CLUB_INTRODUCE_LENGTH;//最大长度
                break;
        }
    }

    /**
     * 根据KEY更新UI
     */
    public void UpdateUIByKey(){
        int headID = R.string.club_create_name;
        String content = "";
        switch (key) {
            case ClubConstant.KEY_NAME:
                headID = R.string.club_create_name;
                content = data;
                edt_name.setSingleLine(true);
                edt_name.setShowDeleteBtn(true);
                edt_name.setGravity(Gravity.CENTER);
                break;
            case ClubConstant.KEY_INTRODUCE:
                headID = R.string.club_edit_introduce;
                content = data;
                edt_name.setSingleLine(false);
                edt_name.setShowDeleteBtn(false);
                edt_name.setLines(3);
                edt_name.setGravity(Gravity.LEFT | Gravity.TOP);
                break;
        }
        setHeadTitle(headID);
        edt_name.setText(content);
        edt_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mHeadRightTV.setEnabled(edt_name.getText().toString().length() <= 0 ? false : true);
                mHeadRightTV.setTextColor(getResources().getColor(edt_name.getText().toString().length() <= 0 ? R.color.login_grey_color : R.color.white));
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initView() {
        edt_name = (ClearableEditTextWithIcon) findViewById(R.id.edt_group_name);
        mHeadRightTV = (TextView) findViewById(R.id.tv_head_right);
        mHeadRightTV.setText(R.string.finish);
        mHeadRightTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edt_name.getText().toString();
//                String groupNameGBK = null;
//                try {
//                    //转换汉字编码
//                    groupNameGBK = new String(aliasName.getBytes("GBK"), "ISO8859_1");
//                } catch (UnsupportedEncodingException ex) {
//                }
//                if (!TextUtils.isEmpty(groupNameGBK) && groupNameGBK.length() > 20) {
//                    return;
//                }
                name = NameTrimTools.getNameTrim(name);//去除空格
                name = CHTool.tirmValue(name);
                if (!CHTool.isInvalidString(name)) {
                    Toast.makeText(EditClubItemActivity.this, "昵称不能含有非法字符", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }
                if (name.equals(data)) {
                    //如果为发生过任何变化，提示修改成功
                    Toast.makeText(getApplicationContext(), R.string.club_edit_success, Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                if (key == ClubConstant.KEY_NAME) {
                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(getApplicationContext(), R.string.club_name_empty, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (UserPreferences.getInstance(ChessApp.sAppContext).getDiamond() < ClubConstant.NICKNAME_CONSUME) {
                        showTopUpDialog();
                        return;
                    }
                    mEditClubInfoAction.updateClubInfo(sessionId, name, null, null, null, mUpdateRequestCallback);
                } else if (key == ClubConstant.KEY_INTRODUCE) {
                    if (TextUtils.isEmpty(name)) {
                        //俱乐部介绍云信不支持为空（为空时候他会作为未变更），所以给个空格
                        name = ClubConstant.GROUP_IOS_DEFAULT_NAME;
                    }
                    mEditClubInfoAction.updateClubInfo(sessionId, null, name, null, null, mUpdateRequestCallback);
                }
            }
        });
//        edt_name.setFilters(new InputFilter[]{new NameLengthFilter(maxEditLength), new NoEmojiInputFilter()});
        if(key != ClubConstant.KEY_INTRODUCE){
            edt_name.setFilters(new InputFilter[]{new NameLengthFilter(maxEditLength), new NameRuleFilter()});
        }
//        edt_name.setFilters(new InputFilter[]{new NoSpaceAndEnterInputFilter(), new NameLengthFilter(maxEditLength), new NoEmojiInputFilter()});
        edt_name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    //隐藏键盘
                    showKeyboard(false);
                    return true;
                }
                return false;
            }
        });
        club_name_can_modify_container = findViewById(R.id.club_name_can_modify_container);
        nickname_times_container = findViewById(R.id.nickname_times_container);
        consume_num = (TextView) findViewById(R.id.consume_num);
        consume_num.setText(ClubConstant.NICKNAME_CONSUME + "");
        remain_num = (TextView) findViewById(R.id.remain_num);
        remain_num.setText(UserPreferences.getInstance(ChessApp.sAppContext).getDiamond() + "");
        modify_name_bottom_desc = (TextView) findViewById(R.id.modify_name_bottom_desc);
        modify_name_bottom_desc.setText(R.string.modify_name_bottom_desc);
        tv_can_modify_club_name_time = (TextView) findViewById(R.id.tv_can_modify_club_name_time);
    }

    private void initModifyClubNameUI() {
        Team team = TeamDataCache.getInstance().getTeamById(sessionId);
        long currentTime = DemoCache.getCurrentServerSecondTime();
        int canModifyDay = 10;//能修改俱乐部名称剩余天数
        boolean canModifyName = true;//是否能修改俱乐部名称
        if (team != null) {
            String extStr = team.getExtServer();
            long modifyTime = ClubConstant.getClubNameLastModifyTime(extStr);
            if (modifyTime <= currentTime) {//90天后能修改昵称
                canModifyName = true;
                canModifyDay = 0;
            } else {
                canModifyName = false;
                canModifyDay = (int) ((modifyTime - currentTime) / 24f / 60 / 60);
            }
        }
        mHeadRightTV.setVisibility(canModifyName ? View.VISIBLE : View.GONE);
        club_name_can_modify_container.setVisibility(canModifyName ? View.GONE : View.VISIBLE);
        nickname_times_container.setVisibility(canModifyName ? View.VISIBLE : View.GONE);
        tv_can_modify_club_name_time.setText("下次修改时间：" + canModifyDay + "天后");
        edt_name.setEnabled(canModifyName);
        edt_name.setShowDeleteBtn(canModifyName);
        if (canModifyName) {

        } else {
            edt_name.removeClearButton();
        }
    }

    GameRequestCallback mUpdateRequestCallback = new GameRequestCallback() {
        @Override
        public void onSuccess(JSONObject response) {
            onUpdateCompleted();
        }

        @Override
        public void onFailed(int code, JSONObject response) {
            if (code == ApiCode.CODE_NICKNAME_EXISTED || code == ApiCode.CODE_UPDATE_HORDE_NAME_EXISTED || code == ApiCode.CODE_MODIFY_CLUB_NAME_ALREADY_EXISTED) {
                showNicknameExistedDialog();//昵称已经被占用
            }
            if (key == ClubConstant.KEY_NAME) {
                RequestTimeLimit.lastGetAmontTime = (0);//user/amount接口的时间限制重新复置
            } else if (key == ClubConstant.KEY_INTRODUCE) {
            }
            if (code == ApiCode.CODE_GAME_NAME_LENGTH_LONG) {
                Toast.makeText(ChessApp.sAppContext, R.string.club_name_long, Toast.LENGTH_SHORT).show();
            } else if (code == ApiCode.CODE_GAME_NAME_FORMAT_WRONG) {
                Toast.makeText(ChessApp.sAppContext, R.string.club_name_format_wrong, Toast.LENGTH_SHORT).show();
            } else if (code == ApiCode.CODE_MODIFY_CLUB_NAME_TIME_FORBIDDEN) {//修改俱乐部名称允许修改时间还没到 90天
                android.widget.Toast.makeText(EditClubItemActivity.this, "修改俱乐部名称时间未到，3个月内只能修改一次", Toast.LENGTH_SHORT).show();
            }
        }
    };

    EasyAlertDialog nicknameExistedDialog;
    private void showNicknameExistedDialog() {
        if (nicknameExistedDialog == null) {
            nicknameExistedDialog = EasyAlertDialogHelper.createOneButtonDiolag(this, "",
                    getResources().getString(R.string.nickname_existed), getString(R.string.ok), true,
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

    EasyAlertDialog topUpDialog;
    private void showTopUpDialog() {
        if (topUpDialog == null) {
            topUpDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "",
                    "您的钻石不足，请购买", getString(R.string.buy), getString(R.string.cancel), true,
                    new EasyAlertDialogHelper.OnDialogActionListener() {

                        @Override
                        public void doCancelAction() {
                            topUpDialog.dismiss();
                        }

                        @Override
                        public void doOkAction() {
                            ShopActivity.start(EditClubItemActivity.this, ShopActivity.TYPE_SHOP_DIAMOND);
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            topUpDialog.show();
        }
    }

    private void onUpdateCompleted() {
        showKeyboard(false);
        Intent intent = new Intent(this , ClubEditActivity.class);
        intent.putExtra(Extras.EXTRA_EDIT_KEY , key);
        intent.putExtra(Extras.EXTRA_DATA , edt_name.getText().toString());
        //编辑资料成功
        setResult(Activity.RESULT_OK , intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mEditClubInfoAction != null){
            mEditClubInfoAction.onDestroy();
            mEditClubInfoAction = null;
        }
    }
}
