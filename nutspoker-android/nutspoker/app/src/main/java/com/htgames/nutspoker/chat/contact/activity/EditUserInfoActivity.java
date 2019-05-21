package com.htgames.nutspoker.chat.contact.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.helper.UserUpdateHelper;
import com.htgames.nutspoker.chat.picker.PickImageHelper;
import com.htgames.nutspoker.chat.region.RegionConstants;
import com.htgames.nutspoker.chat.region.activity.RegionActivity;
import com.htgames.nutspoker.chat.region.db.RegionDBTools;
import com.htgames.nutspoker.data.common.UserConstant;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.tool.AssetsDatabaseManager;
import com.htgames.nutspoker.ui.action.EditUserInfoAction;
import com.htgames.nutspoker.ui.activity.Login.ChangePwd1Activity;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.PickerView;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.session.actions.PickImageAction;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.nos.NosService;
import com.netease.nimlib.sdk.nos.model.NosThumbParam;
import com.netease.nimlib.sdk.nos.util.NosThumbImageUtil;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.netease.nim.uikit.common.ui.imageview.HeadImageView.DEFAULT_AVATAR_THUMB_SIZE;

/**
 * 编辑资料界面 新增 uuid
 */
public class EditUserInfoActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = "EditUserInfoActivity";
    // constant
    public static final int PICK_AVATAR_REQUEST = 28431;
    public static final int PICK_ALBUM_REQUEST = 2;
    public static final int AVATAR_TIME_OUT = 30000;
    public final static int REQUESTCODE_EDIT = 3;
    String account;
    RelativeLayout rl_userhead;
    RelativeLayout rl_nickname;
    RelativeLayout rl_area;
    RelativeLayout rl_signature;
    RelativeLayout rl_edit_user_set_sex_container;
    HeadImageView iv_userhead;
    TextView tv_nickname;
    TextView tv_account;
    TextView tv_area;
    TextView tv_signature;
    TextView tv_sex;
    TextView tv_level;
    TextView tv_register_time;
    EditUserInfoAction mEditUserInfoAction;
    //数据库管理工具
    AssetsDatabaseManager mg = null;
    int currentAreaId = 0;

    @BindView(R.id.tv_virtual_id)
    TextView mVirtualId;

    @OnClick(R.id.rl_changepwd)
    void clickChangePwd() {
        startActivity(new Intent(this, ChangePwd1Activity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_userinfo);
        mUnbinder = ButterKnife.bind(this);

        account = UserPreferences.getInstance(getApplicationContext()).getUserId();
        initHead();
        initView();
        mEditUserInfoAction = new EditUserInfoAction(this, null);
        getUserInfo(false);

        //战鱼ID
        mVirtualId.setText(UserPreferences.getInstance(this).getZYId());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            String region = intent.getStringExtra(Extras.EXTRA_REGION_DATA);
            if (!TextUtils.isEmpty(region)) {
//                tv_area.setText(region);
                getUserInfo(true);
            }
        }
    }

    private void initHead() {
        //来自我的界面
        setHeadTitle(R.string.edit_userinfo_head);
    }

    private void initView() {
        rl_userhead = (RelativeLayout) findViewById(R.id.rl_userhead);
        rl_nickname = (RelativeLayout) findViewById(R.id.rl_nickname);
        rl_area = (RelativeLayout) findViewById(R.id.rl_area);
        rl_signature = (RelativeLayout) findViewById(R.id.rl_signature);
        iv_userhead = (HeadImageView) findViewById(R.id.iv_userhead);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        tv_account = (TextView) findViewById(R.id.tv_account);
        tv_area = (TextView) findViewById(R.id.tv_area);
        tv_signature = (TextView) findViewById(R.id.tv_signature);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_level = (TextView) findViewById(R.id.tv_level);
        tv_register_time = (TextView) findViewById(R.id.tv_register_time);
        rl_userhead.setOnClickListener(this);
        rl_nickname.setOnClickListener(this);
        rl_area.setOnClickListener(this);
        rl_signature.setOnClickListener(this);
        rl_edit_user_set_sex_container = (RelativeLayout) findViewById(R.id.rl_edit_user_set_sex_container);
        rl_edit_user_set_sex_container.setOnClickListener(this);
    }

    /**
     * 选取图片
     */
    public void pickImage(int requestCode) {
        PickImageHelper.PickImageOption option = new PickImageHelper.PickImageOption();
        option.titleResId = R.string.set_head_image;
        option.crop = true;
        option.multiSelect = false;
        option.cropOutputImageWidth = 360;
        option.cropOutputImageHeight = 360;
        PickImageHelper.pickImage(EditUserInfoActivity.this, requestCode, option);
    }

    public void updateUserInfo(String name, String signature, boolean isMale) {
        RequestCallbackWrapper<Void> requestCallbackWrapper = new RequestCallbackWrapper<Void>() {
            @Override
            public void onResult(int code, Void aVoid, Throwable throwable) {
                DialogMaker.dismissProgressDialog();
                if (code == ResponseCode.RES_SUCCESS) {
                } else if (code == ResponseCode.RES_ETIMEOUT) {
                    Toast.makeText(getApplicationContext(), R.string.user_info_update_failed, Toast.LENGTH_SHORT).show();
                }
            }
        };
        DialogMaker.showProgressDialog(this, null, false);

        Map<UserInfoFieldEnum, Object> fields = new HashMap<>();
        fields.put(UserInfoFieldEnum.Name, name);
        fields.put(UserInfoFieldEnum.SIGNATURE, signature);
        if (isMale) {
            fields.put(UserInfoFieldEnum.GENDER, GenderEnum.MALE.getValue());
        } else {
            fields.put(UserInfoFieldEnum.GENDER, GenderEnum.FEMALE.getValue());
        }
        //邮箱：必须为合法邮箱
        //手机号：必须为合法手机号 如13588888888、+(86)-13055555555
        //生日：必须为"yyyy-MM-dd"格式
        NIMClient.getService(UserService.class).updateUserInfo(fields).setCallback(requestCallbackWrapper);
//        UserUpdateHelper.update(fieldMap.get(key), content, callback);
    }

    /**
     * 更新用户信息
     * UserInfoFieldEnum，包括：昵称，性别，头像 URL，签名，手机，邮箱，生日以及扩展字段等
     * @param requestCallbackWrapper
     */
//    public void updateUserInfo(String nameId, RequestCallbackWrapper<Void> requestCallbackWrapper) {
//        Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
//        fields.put(UserInfoFieldEnum.Name, nameId);
//        //邮箱：必须为合法邮箱
//        //手机号：必须为合法手机号 如13588888888、+(86)-13055555555
//        //生日：必须为"yyyy-MM-dd"格式
//        NIMClient.getService(UserService.class).updateUserInfo(fields).setCallback(requestCallbackWrapper);
//    }

    /**
     * 用户资料变更观察者
     */
    public Observer<List<UserInfoProvider.UserInfo>> userInfoUpdateObserver = new Observer<List<UserInfoProvider.UserInfo>>() {
        @Override
        public void onEvent(List<UserInfoProvider.UserInfo> userInfos) {

        }
    };

    /**
     * 注册/注销观察者
     */
    public void observeUserInfoUpdate(boolean register) {
        // 注册/注销观察者
//        NIMClient.getService(UserServiceObserve.class).observeUserInfoUpdate(userInfoUpdateObserver, register);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_AVATAR_REQUEST) {
                LogUtil.i(TAG, "上传头像");
                //上传头像
                //处理选取图片
                String path = data.getStringExtra(com.netease.nim.uikit.session.constant.Extras.EXTRA_FILE_PATH);
                LogUtil.i(TAG, "pick avatar:" + path);
                updateAvatar(path);
            } else if (requestCode == PICK_ALBUM_REQUEST) {
                //上传相片册

            } else if (requestCode == REQUESTCODE_EDIT) {
                getUserInfo(true);
            }
        }
    }

    /**
     * 更新头像
     */
    private void updateAvatar(final String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        File file = new File(path);
        if (file == null) {
            return;
        }
        DialogMaker.showProgressDialog(this, null, null, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancelUpload(R.string.user_info_update_cancel);
            }
        }).setCanceledOnTouchOutside(true);
        LogUtil.i(TAG, "start upload avatar, local file path=" + file.getAbsolutePath());
        sHandler.postDelayed(outimeTask, AVATAR_TIME_OUT);
        uploadAvatarFuture = NIMClient.getService(NosService.class).upload(file, PickImageAction.MIME_JPEG);
        uploadAvatarFuture.setCallback(new RequestCallbackWrapper<String>() {
            @Override
            public void onResult(int code, final String url, Throwable exception) {
                if (code == ResponseCode.RES_SUCCESS && !TextUtils.isEmpty(url)) {
                    LogUtil.i(TAG, "upload avatar success, url =" + url);
                    LogUtil.i(TAG, "url : " + url);
                    UserUpdateHelper.update(UserInfoFieldEnum.AVATAR, url, new RequestCallbackWrapper<Void>() {
                        @Override
                        public void onResult(int code, Void result, Throwable exception) {
                            if (code == ResponseCode.RES_SUCCESS) {
                                Toast.makeText(getApplicationContext(), "提交成功，系统将在24小时内审核", Toast.LENGTH_LONG).show();
                                onUpdateDone();
                                updateUserHead(url);
                            } else {
                                Toast.makeText(getApplicationContext(), ChessApp.sAppContext.getResources().getString(R.string.head_update_failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }); // 更新资料
                } else {
                    Toast.makeText(getApplicationContext(), ChessApp.sAppContext.getResources().getString(R.string.user_info_update_failed), Toast.LENGTH_SHORT).show();
                    onUpdateDone();
                }
            }
        });
    }

    /**
     * 上次头像到APP的服务器
     *
     * @param url
     */
    public void updateUserHead(String url) {
        mEditUserInfoAction.updateUserInfo(null, url, null, null, null, null);
    }

    // data
    AbortableFuture<String> uploadAvatarFuture;
    private NimUserInfo userInfo;

    private void cancelUpload(int resId) {
        if (uploadAvatarFuture != null) {
            uploadAvatarFuture.abort();
            Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_SHORT).show();
            onUpdateDone();
        }
    }

    private Runnable outimeTask = new Runnable() {
        @Override
        public void run() {
            cancelUpload(R.string.user_info_update_failed);
        }
    };

    private void onUpdateDone() {
        uploadAvatarFuture = null;
        DialogMaker.dismissProgressDialog();
        getUserInfo(false);
    }

    private void getUserInfo(boolean isNet) {
        userInfo = NimUserInfoCache.getInstance().getUserInfo(account);
        if (userInfo == null || isNet) {
            NimUserInfoCache.getInstance().getUserInfoFromRemote(account, new RequestCallback<NimUserInfo>() {
                @Override
                public void onSuccess(NimUserInfo param) {
                    userInfo = param;
                    LogUtil.i(TAG, "userInfo :" + userInfo.getAvatar() + ";ex:" + userInfo.getExtension());
                    updateUI();
                }

                @Override
                public void onFailed(int code) {
                    Toast.makeText(getApplicationContext(), "getUserInfoFromRemote failed:" + code, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onException(Throwable exception) {
                    Toast.makeText(getApplicationContext(), "getUserInfoFromRemote exception:" + exception, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            updateUI();
        }
    }

    private void updateUI() {
        int defaultHeadId = R.mipmap.default_male_head;
        tv_nickname.setText(userInfo.getName());
        if (userInfo.getSignature() != null) {
            tv_signature.setText(userInfo.getSignature());
        }
        int level = UserConstant.getMyVipLevel();
        tv_level.setText(UserConstant.getVipLevelShowRes(level));
        tv_account.setText(UserPreferences.getInstance(getApplicationContext()).getUserPhone());
        if (userInfo.getGenderEnum() != null) {
            if (userInfo.getGenderEnum() == GenderEnum.MALE) {
                sexStr = "男";
                defaultHeadId = R.mipmap.default_male_head;
            } else if (userInfo.getGenderEnum() == GenderEnum.FEMALE) {
                sexStr = "女";
                defaultHeadId = R.mipmap.default_female_head;
            } else {
                sexStr = "男";
                defaultHeadId = R.mipmap.default_male_head;
            }
        }
        tv_sex.setText(sexStr);
        iv_userhead.loadBuddyAvatarByMeFrg(account, DEFAULT_AVATAR_THUMB_SIZE, defaultHeadId);//设置头像
        String extension = userInfo.getExtension();
        LogUtil.i(TAG, "extension : " + extension);
        int currentAreaId = UserConstant.getUserExtAreaId(extension);
        if (currentAreaId != 0) {
            String area = RegionDBTools.getShowRegionContent(currentAreaId, " ");
            if (!TextUtils.isEmpty(area)) {
                LogUtil.i(TAG, area);
                tv_area.setText(area);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_userhead:
                pickImage(PICK_AVATAR_REQUEST);
                break;
            case R.id.rl_nickname:
                EditUserItemActivity.start(this, "", UserConstant.KEY_NICKNAME, userInfo.getName(), REQUESTCODE_EDIT);
                break;
            case R.id.rl_area:
                RegionActivity.start(this, RegionConstants.TYPE_COUNTY, account, null, null, currentAreaId, RegionActivity.FROM_EDIT_USER);
                break;
            case R.id.rl_signature:
                EditUserItemActivity.start(this, "", UserConstant.KEY_SIGNATURE, userInfo.getSignature(), REQUESTCODE_EDIT);
                break;
            case R.id.rl_edit_user_set_sex_container:
                showSelectSexDialog(userInfo.getGenderEnum());
                break;
        }
    }

    GameRequestCallback requestCallback = new GameRequestCallback() {
        @Override
        public void onSuccess(JSONObject jsonObject) {
            LogUtil.i(TAG, "头像上传APP服务器成功");
            getUserInfo(true);
        }

        @Override
        public void onFailed(int code, JSONObject response) {

        }
    };

    String sexStr = "男";
    private void showSelectSexDialog(final GenderEnum currentSexType) {
        sexStr = currentSexType == GenderEnum.MALE ? "男" : "女";
        int textMargin = ScreenUtil.dp2px(this, 15);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog_default_style);
        final AlertDialog dialogTest = builder.create();
        dialogTest.show();

        RelativeLayout rootView = new RelativeLayout(this);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        rootView.setBackgroundColor(getResources().getColor(R.color.register_page_bg_color));

        TextView finishTV = new TextView(this);
        finishTV.setText(R.string.finish);
        finishTV.setId(R.id.item_position);
        finishTV.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        finishTV.setTextColor(getResources().getColor(R.color.login_solid_color));
        finishTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenderEnum genderEnum = sexStr.equals("男") ? GenderEnum.MALE : GenderEnum.FEMALE;
                if (selectDifferentSex(currentSexType)) {
                    mEditUserInfoAction.updateUserInfo("", "", genderEnum, null, null, requestCallback);//通过APP服务器
                }
                dialogTest.dismiss();
            }
        });
        RelativeLayout.LayoutParams finishTVLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        finishTVLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT | RelativeLayout.ALIGN_PARENT_TOP);
        finishTVLP.setMargins(textMargin, textMargin / 2, textMargin, textMargin / 2);
        rootView.addView(finishTV, finishTVLP);

        PickerView picker = new PickerView(this, ScreenUtil.dp2px(this, 15), ScreenUtil.dp2px(this, 20));
        List<String> data = new ArrayList<String>(2);
        data.add("男");data.add("女");
        picker.setData(data);
        picker.setSelected(currentSexType == GenderEnum.MALE ? 0 : 1);
        picker.setBackgroundColor(getResources().getColor(R.color.white));
        picker.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                sexStr = text;
            }
        });
        RelativeLayout.LayoutParams pickLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtil.dp2px(this, 190));
        pickLP.addRule(RelativeLayout.BELOW, finishTV.getId());
        rootView.addView(picker, pickLP);

        Window windowTest = dialogTest.getWindow();
        android.view.WindowManager.LayoutParams lp = windowTest.getAttributes();
        lp.width = ScreenUtil.getScreenWidth(this);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        windowTest.setWindowAnimations(R.style.PopupAnimation);
        windowTest.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        dialogTest.setContentView(rootView);
    }

    private boolean selectDifferentSex(GenderEnum currentSexType) {
        if ((currentSexType == GenderEnum.MALE && sexStr.equals("男") || (currentSexType == GenderEnum.FEMALE && sexStr.equals("女")))) {
            return false;
        }
        return true;
    }

    /**
     * 获取压缩的头像用作上传
     *
     * @param avatarUrl
     * @param thumbSize
     * @return
     */
    public String getThumbAvatar(String avatarUrl, int thumbSize) {
        /**
         * 若使用网易云信云存储，这里可以设置下载图片的压缩尺寸，生成下载URL
         * 如果图片来源是非网易云信云存储，请不要使用NosThumbImageUtil
         */
        final String thumbUrl = thumbSize > 0 ? NosThumbImageUtil.makeImageThumbUrl(userInfo.getAvatar(),
                NosThumbParam.ThumbType.Crop, thumbSize, thumbSize) : userInfo.getAvatar();
        return thumbUrl;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEditUserInfoAction != null) {
            mEditUserInfoAction.onDestroy();
            mEditUserInfoAction = null;
        }
    }
}
