package com.htgames.nutspoker.chat.contact.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
import com.htgames.nutspoker.ui.activity.Club.ClubMemberACNew;
import com.htgames.nutspoker.util.word.WordFilter;
import com.netease.nim.uikit.api.ApiCode;
import com.htgames.nutspoker.util.word.CHTool;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.htgames.nutspoker.data.common.UserConstant;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.net.RequestTimeLimit;
import com.htgames.nutspoker.ui.action.EditUserInfoAction;
import com.htgames.nutspoker.ui.action.HordeAction;
import com.htgames.nutspoker.ui.activity.System.ShopActivity;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.ui.inputfilter.NameLengthFilter;
import com.htgames.nutspoker.ui.inputfilter.NameRuleFilter;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.cache.FriendDataCache;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.chesscircle.entity.HordeEntity;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.interfaces.SensitiveFilter;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.FriendFieldEnum;
import com.netease.nimlib.sdk.friend.model.Friend;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 修改用户信息
 */
public class EditUserItemActivity extends BaseActivity {
    private static SensitiveFilter _filter = new SensitiveFilter() {
        @Override
        public String doFilter(String message) {
            return WordFilter.doFilter(message);
        }
    };
    private ClearableEditTextWithIcon edt_name;
    public final static int FROM_EDIT_INFO = 0;
    public final static int FROM_PERFECT_INFO = 1;//完善用户信息
    public final static int REQUESTCODE_EDIT_HORDE = 4;//从4开始  修改部落信息 目前仅支持修改部落昵称
    private String sessionId;
    private String data;
    //
    private TextView mHeadRightTV;
    private int key;
    EditUserInfoAction mEditUserInfoAction;
    private int maxEditLength = UserConstant.MAX_NICKNAME_LENGTH;
    int from = FROM_EDIT_INFO;
    //修改昵称相关
    View nickname_times_container;
    TextView consume_num;
    TextView remain_num;
    TextView modify_name_bottom_desc;

    public static void start(Activity activity , String sessionId , int key , String data) {
        Intent intent = new Intent(activity , EditUserItemActivity.class);
        intent.putExtra(Extras.EXTRA_SESSION_ID, sessionId);
        intent.putExtra(Extras.EXTRA_EDIT_KEY, key);
        intent.putExtra(Extras.EXTRA_DATA, data);
        activity.startActivity(intent);
    }

    public static void start(Activity activity , String sessionId , int from, int key , String data) {
        Intent intent = new Intent(activity, EditUserItemActivity.class);
        intent.putExtra(Extras.EXTRA_SESSION_ID, sessionId);
        intent.putExtra(Extras.EXTRA_EDIT_KEY, key);
        intent.putExtra(Extras.EXTRA_DATA, data);
        intent.putExtra(Extras.EXTRA_FROM, from);
        activity.startActivity(intent);
    }

    public static void start(Activity activity , String sessionId , int key , String data , int requestCode){
        Intent intent = new Intent(activity , EditUserItemActivity.class);
        intent.putExtra(Extras.EXTRA_SESSION_ID, sessionId);
        intent.putExtra(Extras.EXTRA_EDIT_KEY, key);
        intent.putExtra(Extras.EXTRA_DATA, data);
        activity.startActivityForResult(intent, requestCode);
    }

    public HordeEntity mHordeEntity;
    HordeAction mHordeAction;
    public static void startEditHordeByResult(Activity activity , HordeEntity hordeEntity , int key, int requestCode) {
        Intent intent = new Intent(activity , EditUserItemActivity.class);
        intent.putExtra(Extras.EXTRA_CUSTOMIZATION, hordeEntity);
        intent.putExtra(Extras.EXTRA_EDIT_KEY, key);
        intent.putExtra(Extras.EXTRA_DATA, hordeEntity.name);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        from = getIntent().getIntExtra(Extras.EXTRA_FROM, FROM_EDIT_INFO);
        mHordeEntity = (HordeEntity) getIntent().getSerializableExtra(Extras.EXTRA_CUSTOMIZATION);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        initHead();
        sessionId = getIntent().getStringExtra(Extras.EXTRA_SESSION_ID);
        data = getIntent().getStringExtra(Extras.EXTRA_DATA);
        key = getIntent().getIntExtra(Extras.EXTRA_EDIT_KEY, UserConstant.KEY_ALIAS);
        initData();
        initView();
        mEditUserInfoAction = new EditUserInfoAction(this, null);
        mHordeAction = new HordeAction(this, null);
        UpdateUIByKey();
        if (key == UserConstant.KEY_EDIT_HORDE_NAME) {//修改部落昵称特殊处理
            setHordenameRelated();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setNicknameRelated();
    }

    private void initHead() {
        mHeadRightTV = (TextView) findViewById(R.id.tv_head_right);
        mHeadRightTV.setText(R.string.finish);
        mHeadRightTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edt_name.getText().toString().trim();//去除前后的空格，文字中间允许有空格
                if (name.equals(data)) {
                    //如果为发生过任何变化，提示修改成功
                    Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                name = CHTool.tirmValue(name);
                if (!CHTool.isInvalidString(name)) {
                    Toast.makeText(EditUserItemActivity.this, "内容不能含有非法字符", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }
                if (key == UserConstant.KEY_NICKNAME) {
                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(getApplicationContext(), R.string.nickname_empty, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (UserPreferences.getInstance(ChessApp.sAppContext).getNicknameTimes() > 0 && UserPreferences.getInstance(ChessApp.sAppContext).getDiamond() < UserConstant.NICKNAME_CONSUME) {
                        showTopUpDialog();
                        return;
                    }
                    mEditUserInfoAction.updateUserInfo(name, null, null, null, null, mUpdateRequestCallback);
                } else if (key == UserConstant.KEY_SIGNATURE) {
                    if(TextUtils.isEmpty(name)){
                        name = " ";
                    }
                    mEditUserInfoAction.updateUserInfo(null, null, null, name, null, mUpdateRequestCallback);
                } else if (key == UserConstant.KEY_ALIAS) {
                    if (StringUtil.isSpace(name)) {
                        Toast.makeText(getApplicationContext(), "备注不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    setAlias(name);
                } else if (key == UserConstant.KEY_EDIT_HORDE_NAME && mHordeEntity != null) {
                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(getApplicationContext(), "部落名称不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (UserPreferences.getInstance(ChessApp.sAppContext).getDiamond() < UserConstant.HORDE_NAME_CONSUME) {
                        showTopUpDialog();
                        return;
                    }
                    showUpdateHordeNameDialog(name);
                }
            }
        });
    }

    EasyAlertDialog updateHordeNameDialog;
    private void showUpdateHordeNameDialog(final String name) {
        if (updateHordeNameDialog == null) {
            updateHordeNameDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "修改名称",
                    "部落名称只能修改一次，请再次确认\n新部落名称：" + name, getString(R.string.ok), getString(R.string.cancel), true,
                    new EasyAlertDialogHelper.OnDialogActionListener() {

                        @Override
                        public void doCancelAction() {
                        }

                        @Override
                        public void doOkAction() {
                            mHordeAction.updateHorde(mHordeEntity.horde_id, name, mUpdateRequestCallback);
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            updateHordeNameDialog.show();
        }
    }

    private void initData() {
        switch (key) {
            case UserConstant.KEY_NICKNAME:
                maxEditLength = UserConstant.MAX_NICKNAME_LENGTH;
                break;
            case UserConstant.KEY_SIGNATURE:
                maxEditLength = UserConstant.MAX_SIGNATURE_LENGTH;
                break;
            case UserConstant.KEY_ALIAS:
                maxEditLength = UserConstant.MAX_ALIAS_LENGTH;
                break;
            case UserConstant.KEY_EDIT_HORDE_NAME:
                maxEditLength = UserConstant.MAX_HORDE_NAME_LENGTH;
                break;
        }
    }

    /**
     * 根据KEY更新UI
     */
    public void UpdateUIByKey(){
        int headID = R.string.edit_alias_head;
        int descID = R.string.edit_userinfo_alias_desc;
        String content = "";
        switch (key){
            case UserConstant.KEY_NICKNAME:
                headID = R.string.edit_userinfo_nickname;
                descID = R.string.edit_userinfo_nickname_desc;
                content = data;
                edt_name.setIconResource(R.mipmap.icon_people);
                edt_name.setHint(R.string.edit_userinfo_nickname_hint);
                edt_name.setSingleLine(true);
                edt_name.setShowDeleteBtn(true);
                edt_name.setGravity(Gravity.CENTER);
                break;
            case UserConstant.KEY_SIGNATURE:
                headID = R.string.edit_userinfo_signature;
                descID = R.string.edit_userinfo_signature_desc;
                content = data;
                edt_name.setSingleLine(false);
                edt_name.setShowDeleteBtn(false);
                edt_name.setLines(3);
                edt_name.setGravity(Gravity.LEFT | Gravity.TOP);
                mHeadRightTV.setEnabled(true);
                mHeadRightTV.setTextColor(getResources().getColor(R.color.white));
                break;
            case UserConstant.KEY_ALIAS:
                headID = R.string.edit_alias_head;
                descID = R.string.edit_userinfo_alias_desc;
                content = data;
                Friend friend = FriendDataCache.getInstance().getFriendByAccount(sessionId);
                //默认没有备注  颜色为灰色
                edt_name.setTextColor(getResources().getColor(R.color.shop_text_no_select_color));
                if (friend != null && !TextUtils.isEmpty(friend.getAlias())) {
                    content = friend.getAlias();
                    edt_name.setTextColor(Color.BLACK);
                }
                edt_name.setHint(R.string.edit_userinfo_nickname_hint);
                edt_name.setSingleLine(true);
                edt_name.setShowDeleteBtn(true);
                edt_name.setGravity(Gravity.CENTER);
                break;
            case UserConstant.KEY_EDIT_HORDE_NAME:
                headID = R.string.horde_edit_name;
                content = mHordeEntity == null ? "" : mHordeEntity.name;
                edt_name.setSingleLine(true);
                edt_name.setShowDeleteBtn(true);
                edt_name.setGravity(Gravity.CENTER);
                break;
        }
        setHeadTitle(headID);
        edt_name.setText(content);
        if (key != UserConstant.KEY_SIGNATURE) {
            mHeadRightTV.setEnabled(TextUtils.isEmpty(content) ? false : true);
            mHeadRightTV.setTextColor(getResources().getColor(TextUtils.isEmpty(content) ? R.color.login_grey_color : R.color.white));
        }
        //判断来源
        if(from == FROM_PERFECT_INFO) {
            setSwipeBackEnable(false);
            setHeadLeftButtonGone();
            setHeadTitle(R.string.perfect_userinfo);
        }
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                LogUtil.i("edt_name.addTextChangedListener", "beforeTextChanged: " + s.toString());
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LogUtil.i("edt_name.addTextChangedListener", "onTextChanged: " + s.toString());
                if (key == UserConstant.KEY_SIGNATURE) {
                    return;//签名允许内容为空，但是""要改成" ",否则云心报错
                }
                mHeadRightTV.setEnabled(edt_name.getText().toString().length() <= 0 ? false : true);
                mHeadRightTV.setTextColor(getResources().getColor(edt_name.getText().toString().length() <= 0 ? R.color.login_grey_color : R.color.white));
            }
            @Override
            public void afterTextChanged(Editable s) {
                edt_name.setTextColor(Color.BLACK);
                String toSendStr = s.toString();
                String text = _filter == null ? toSendStr : _filter.doFilter(toSendStr);
                int sectionPosStart = edt_name.getSelectionStart();
                int sectionPosEnd = edt_name.getSelectionStart();
                LogUtil.i("edt_name.addTextChangedListener", "afterTextChanged: " + s.toString() + "  start: " + sectionPosStart + "  end: " + sectionPosEnd);
                edt_name.removeTextChangedListener(this);
                edt_name.setText(text);
                edt_name.setSelection(sectionPosStart <= edt_name.getText().length() ? sectionPosStart : edt_name.getText().length());
                edt_name.addTextChangedListener(textWatcher);
            }
        };
        edt_name.addTextChangedListener(textWatcher);
    }
    TextWatcher textWatcher;

    private void initView() {
        edt_name = (ClearableEditTextWithIcon) findViewById(R.id.edt_group_name);
        if (key == UserConstant.KEY_SIGNATURE) {
            edt_name.setFilters(new InputFilter[]{new NameLengthFilter(maxEditLength)});
        } else {
            edt_name.setFilters(new InputFilter[]{new NameLengthFilter(maxEditLength), new NameRuleFilter()});
        }
//        edt_name.setFilters(new InputFilter[]{new NameLengthFilter(maxEditLength), new NoEmojiInputFilter()});
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
        nickname_times_container = findViewById(R.id.nickname_times_container);
        consume_num = (TextView) findViewById(R.id.consume_num);
        remain_num = (TextView) findViewById(R.id.remain_num);
        modify_name_bottom_desc = (TextView) findViewById(R.id.modify_name_bottom_desc);
    }

    private void setNicknameRelated() {
        if (key == UserConstant.KEY_NICKNAME) {
            nickname_times_container.setVisibility(key == UserConstant.KEY_NICKNAME ? View.VISIBLE : View.GONE);
            consume_num.setText(UserPreferences.getInstance(ChessApp.sAppContext).getNicknameTimes() <= 0 ? "免费" : (UserConstant.NICKNAME_CONSUME + ""));
            remain_num.setText(UserPreferences.getInstance(ChessApp.sAppContext).getDiamond() + "");
        }
    }

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
//            //隐藏键盘
//            showKeyboard(false);
//            return true;
//        }
//        return super.dispatchKeyEvent(event);
//    }

    GameRequestCallback mUpdateRequestCallback = new GameRequestCallback() {
        @Override
        public void onSuccess(JSONObject jsonObject) {
            if (key == UserConstant.KEY_NICKNAME) {//如果是修改昵称的话，重新获取amount
                RequestTimeLimit.lastGetAmontTime = (0);//user/amount接口的时间限制重新复置
                if (UserPreferences.getInstance(ChessApp.sAppContext).getNicknameTimes() >= 1) {//满足这个条件减去钻石
                    int diamond = UserPreferences.getInstance(ChessApp.sAppContext).getDiamond() - UserConstant.NICKNAME_CONSUME;
                    UserPreferences.getInstance(ChessApp.sAppContext).setDiamond(diamond);
                }
                int nickname_times = 1;//表示修改昵称成功1次
                UserPreferences.getInstance(ChessApp.sAppContext).setNicknameTimes(nickname_times);
                setNicknameRelated();
            } else if (key == UserConstant.KEY_EDIT_HORDE_NAME) {
                RequestTimeLimit.lastGetAmontTime = (0);//user/amount接口的时间限制重新复置
                int diamond = UserPreferences.getInstance(ChessApp.sAppContext).getDiamond() - UserConstant.HORDE_NAME_CONSUME;
                UserPreferences.getInstance(ChessApp.sAppContext).setDiamond(diamond);
            }
            onUpdateCompleted();
        }

        @Override
        public void onFailed(int code, JSONObject response) {
            if (code == ApiCode.CODE_NICKNAME_EXISTED || code == ApiCode.CODE_UPDATE_HORDE_NAME_EXISTED || code == ApiCode.CODE_MODIFY_CLUB_NAME_ALREADY_EXISTED) {
                showNicknameExistedDialog();//昵称已经被占用
            }
            if (key == UserConstant.KEY_NICKNAME) {
                if (code == ApiCode.CODE_UPDATE_NICKNAME_UBSUFFICIENT_DIAMOND && key == UserConstant.KEY_NICKNAME) {//修改昵称钻石不足
                    showTopUpDialog();
                }
            } else if (code == UserConstant.KEY_EDIT_HORDE_NAME) {
                if (code == ApiCode.CODE_MODIFY_HORDE_NAME_ALREADY) {
                    Toast.makeText(EditUserItemActivity.this, "部落名称只能修改一次", Toast.LENGTH_SHORT).show();
                }
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
                            ShopActivity.start(EditUserItemActivity.this, ShopActivity.TYPE_SHOP_DIAMOND);
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            topUpDialog.show();
        }
    }

    private void onUpdateCompleted() {
        showKeyboard(false);
        if (from == FROM_PERFECT_INFO) {
            NimUserInfoCache.getInstance().getUserInfoFromRemote(sessionId, null);
        }
        //编辑资料成功
        Intent result = new Intent();
        result.putExtra(Extras.EXTRA_DATA, edt_name.getText().toString());
        setResult(RESULT_OK, result);
        finish();
    }

    /**
     * 设置备注
     * @param alias
     */
    public void setAlias(String alias) {
        if (!NetworkUtil.isNetAvailable(this)) {
            Toast.makeText(this, R.string.network_is_not_available, com.htgames.nutspoker.widget.Toast.LENGTH_LONG).show();
            return;
        }
        if (true) {
            setAliasByOwnInterface(alias);//现在使用自己的接口设置备注
            return;
        }
        DialogMaker.showProgressDialog(this, getString(R.string.edit_userinfo_alias_ing), false);
        Map<FriendFieldEnum, Object> map = new HashMap<>();
        map.put(FriendFieldEnum.ALIAS, alias);
        NIMClient.getService(FriendService.class).updateFriendFields(sessionId, map).setCallback(callback);
    }

    private void setAliasByOwnInterface(String alias) {
        if (mEditUserInfoAction == null) {
            return;
        }
        mEditUserInfoAction.clubUserAlias(sessionId, alias, new GameRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Toast.makeText(EditUserItemActivity.this, "设置备注成功", Toast.LENGTH_SHORT).show();
                ClubMemberACNew.aliasHasChanged = true;
                finish();
            }
            @Override
            public void onFailed(int code, JSONObject response) {
                Toast.makeText(EditUserItemActivity.this, ApiCode.SwitchCode(code, response == null ? "" : response.toString()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    RequestCallbackWrapper callback = new RequestCallbackWrapper() {
        @Override
        public void onResult(int code, Object result, Throwable exception) {
            DialogMaker.dismissProgressDialog();
            if (code == ResponseCode.RES_SUCCESS) {
                onUpdateCompleted();
            } else if (code == ResponseCode.RES_ETIMEOUT) {
                Toast.makeText(getApplicationContext(), R.string.user_info_update_failed, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void setHordenameRelated() {
        boolean canModifyName = mHordeEntity != null && mHordeEntity.is_modify == 0;//是否能修改部落名称
        consume_num.setText(UserConstant.HORDE_NAME_CONSUME + "");
        mHeadRightTV.setVisibility(canModifyName ? View.VISIBLE : View.GONE);
        nickname_times_container.setVisibility(canModifyName ? View.VISIBLE : View.GONE);
        edt_name.setEnabled(canModifyName);
        edt_name.setShowDeleteBtn(canModifyName);
        modify_name_bottom_desc.setText("提示：部落名称只能修改一次");
        remain_num.setText(UserPreferences.getInstance(ChessApp.sAppContext).getDiamond() + "");
        if (canModifyName) {

        } else {
            edt_name.removeClearButton();
        }
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return true;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        if (from == FROM_PERFECT_INFO) {
            return TransitionMode.BOTTOM;
        } else {
            return TransitionMode.RIGHT;
        }
    }

    @Override
    public void onBackPressed() {
        if (from != FROM_PERFECT_INFO) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mEditUserInfoAction != null){
            mEditUserInfoAction.onDestroy();
            mEditUserInfoAction = null;
        }
        if(mHordeAction != null){
            mHordeAction.onDestroy();
            mHordeAction = null;
        }
    }
}
