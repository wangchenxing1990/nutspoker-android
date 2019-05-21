package com.htgames.nutspoker.ui.activity.Club;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.helper.MessageTipHelper;
import com.htgames.nutspoker.chat.picker.PickImageHelper;
import com.htgames.nutspoker.chat.region.RegionConstants;
import com.htgames.nutspoker.chat.region.RegionEntity;
import com.htgames.nutspoker.chat.region.activity.RegionActivity;
import com.htgames.nutspoker.chat.region.db.RegionDBTools;
import com.htgames.nutspoker.data.common.UserConstant;
import com.netease.nim.uikit.constants.VipConstants;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalyticsEvent;
import com.htgames.nutspoker.ui.fragment.main.RecentContactsFragment;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.htgames.nutspoker.ui.action.ClubAction;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.htgames.nutspoker.ui.activity.System.ShopActivity;
import com.htgames.nutspoker.ui.base.BaseTeamActivity;
import com.htgames.nutspoker.ui.helper.team.TeamCreateHelper;
import com.htgames.nutspoker.ui.inputfilter.NameLengthFilter;
import com.htgames.nutspoker.ui.inputfilter.NoSpaceAndEnterInputFilter;
import com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.session.actions.PickImageAction;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.nos.NosService;
import com.netease.nimlib.sdk.team.constant.VerifyTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * 创建俱乐部
 */
public class ClubCreateActivity extends BaseTeamActivity implements View.OnClickListener {
    private final static String TAG = "ClubCreateActivity";
    private ClearableEditTextWithIcon edt_club_create_name;
    private Button btn_club_create_area;
    private TextView tv_club_own;
    private LinearLayout ll_club_create_limit;
    Button btn_finish;
    Button btn_upgrade_vip;
    public final static int REQUESTCODE_AREA = 1;
    RegionEntity regionEntity;
    String clubAvatar = "";
    HeadImageView iv_club_head;
    int ownerClubLimit = VipConstants.LEVEL_NOT_CLUBCOUNT;
    TextView tv_club_create_limit;
    TextView tv_vip_is_top;
    LinearLayout ll_club_create_white_guide;
    LinearLayout ll_club_create_black_guide;
    ClubAction mClubAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_create);
        initHead();
        initView();
        mClubAction = new ClubAction(this, null);
//        initRegion();
    }

    public void initRegion() {
        regionEntity = RegionDBTools.getRegionChina();
        if (regionEntity != null) {
            btn_club_create_area.setText(regionEntity.name);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            String show = intent.getStringExtra(Extras.EXTRA_REGION_SHOW);
            regionEntity = (RegionEntity) intent.getSerializableExtra(Extras.EXTRA_REGION_DATA);
            btn_club_create_area.setTextColor(getResources().getColor(android.R.color.white));
            if (!TextUtils.isEmpty(show)) {
                btn_club_create_area.setText(show);
            }
        }
    }

    private void initView() {
        tv_club_own = (TextView) findViewById(R.id.tv_club_own);
        tv_club_create_limit = (TextView) findViewById(R.id.tv_club_create_limit);
        btn_finish = (Button) findViewById(R.id.btn_finish);
        btn_upgrade_vip = (Button) findViewById(R.id.btn_upgrade_vip);
        edt_club_create_name = (ClearableEditTextWithIcon) findViewById(R.id.edt_club_create_name);
        btn_club_create_area = (Button) findViewById(R.id.btn_club_create_area);
        ll_club_create_limit = (LinearLayout) findViewById(R.id.ll_club_create_limit);
        iv_club_head = (HeadImageView) findViewById(R.id.iv_club_head);
        //
        ll_club_create_white_guide = (LinearLayout) findViewById(R.id.ll_club_create_white_guide);
        ll_club_create_black_guide = (LinearLayout) findViewById(R.id.ll_club_create_black_guide);
        tv_vip_is_top = (TextView) findViewById(R.id.tv_vip_is_top);
        //
        btn_club_create_area.setOnClickListener(this);
        btn_finish.setOnClickListener(this);
        btn_upgrade_vip.setOnClickListener(this);
        edt_club_create_name.setFilters(new InputFilter[]{new NoSpaceAndEnterInputFilter(), new NameLengthFilter(ClubConstant.MAX_CLUB_NAME_LENGTH)});
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateClubLimitUI();
    }

    public void updateClubLimitUI() {
        ownerClubLimit = UserConstant.getMyCreateClubLimit();
        tv_club_own.setText(String.format(getString(R.string.club_create_already), RecentContactsFragment.myOwnClubCount + "/" + ownerClubLimit));
        if (RecentContactsFragment.myOwnClubCount < ownerClubLimit) {
            //未达创建上限
            btn_finish.setEnabled(true);
            ll_club_create_limit.setVisibility(View.GONE);
            iv_club_head.setOnClickListener(this);
        } else {
            btn_finish.setEnabled(false);
            ll_club_create_limit.setVisibility(View.GONE);//ll_club_create_limit.setVisibility(View.VISIBLE);
            iv_club_head.setOnClickListener(null);
            StringBuffer limitBuffer = new StringBuffer();
            limitBuffer.append(getString(R.string.club_create_count_is_limit_normal));
            ll_club_create_white_guide.setVisibility(View.VISIBLE);
            ll_club_create_black_guide.setVisibility(View.VISIBLE);
            if (UserConstant.getMyVipLevel() == VipConstants.VIP_LEVEL_BALCK) {
                limitBuffer.append("\n").append(getString(R.string.vip_black)).append(getString(R.string.club_create_count_is_limit_vip, RecentContactsFragment.myOwnClubCount));
                tv_club_create_limit.setText(limitBuffer.toString());
                ll_club_create_white_guide.setVisibility(View.GONE);
                ll_club_create_black_guide.setVisibility(View.GONE);
                btn_upgrade_vip.setVisibility(View.GONE);
                tv_vip_is_top.setVisibility(View.VISIBLE);
            } else if (UserConstant.getMyVipLevel() == VipConstants.VIP_LEVEL_WHITE) {
                limitBuffer.append("\n").append(getString(R.string.vip_white)).append(getString(R.string.club_create_count_is_limit_vip, RecentContactsFragment.myOwnClubCount));
                tv_club_create_limit.setText(limitBuffer.toString());
                ll_club_create_white_guide.setVisibility(View.GONE);
                ll_club_create_black_guide.setVisibility(View.VISIBLE);
            } else {
                tv_club_create_limit.setText(limitBuffer.toString());
            }
        }
    }

    public void initHead() {
        setHeadTitle(R.string.club_create);
    }

    public void createTeam(final String clubName, final int clubAreaId) {
        mClubAction.createTeam(clubName, clubAreaId, clubAvatar, new com.htgames.nutspoker.interfaces.RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                LogUtil.i(TAG, result);
                try {
                    JSONObject json = new JSONObject(result);
                    if (code == 0) {
                        String tid = json.getJSONObject("data").optString("tid");
                        onCreateSuccess(clubName, tid);
                    } else if (code == 3026) {
                        Toast.makeText(getApplicationContext(), R.string.club_create_count_is_limit, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.club_create_failed, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed() {

            }
        });
    }

    /**
     * 创建群组,设置群上限
     *
     * @param teamName
     * @param teamIntroduce
     * @param verifyType
     * @param accounts
     */
    public void creteTeam(String teamName, String teamIntroduce, VerifyTypeEnum verifyType, List<String> accounts) {
        DialogMaker.showProgressDialog(this, getString(com.netease.nim.uikit.R.string.empty), false);
        TeamCreateHelper.creteAdvancedTeam(teamName, teamIntroduce, verifyType, accounts, new RequestCallback<Team>() {
            @Override
            public void onSuccess(Team team) {
                DialogMaker.dismissProgressDialog();
                onCreateSuccess(team);
            }

            @Override
            public void onFailed(int code) {
                DialogMaker.dismissProgressDialog();
                if (code == 801) {
                    int teamCapacity = 40;
                    String tip = getString(com.netease.nim.uikit.R.string.over_team_member_capacity, teamCapacity);
                    Toast.makeText(getApplicationContext(), tip, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.club_create_failed, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onException(Throwable throwable) {
                DialogMaker.dismissProgressDialog();
            }
        });
    }

    /**
     * 创建成功回调
     */
    private void onCreateSuccess(Team team) {
        if (team == null) {
            return;
        }
        TeamDataCache.getInstance().addOrUpdateTeam(team);
        MessageTipHelper.saveCreateClubTipMessage(team.getId(), team.getName(), SessionTypeEnum.Team);
        MainActivity.startSession(this, team.getId(), MainActivity.TYPE_SESSION_TEAM);
        UmengAnalyticsEvent.onEventClubCount(getApplicationContext());
        finish();
    }

    /**
     * 创建成功回调
     */
    private void onCreateSuccess(String teamName, String teamId) {
        if (TextUtils.isEmpty(teamId)) {
            return;
        }
//        TeamDataCache.getInstance().addOrUpdateTeam(team);
        MessageTipHelper.saveCreateClubTipMessage(teamId, teamName, SessionTypeEnum.Team);
        MainActivity.startSession(this, teamId, MainActivity.TYPE_SESSION_TEAM);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTCODE_AREA) {
            if (resultCode == ClubAreaActivity.RESULT_AREA_CLICK && data != null) {
                String area = data.getStringExtra(ClubAreaActivity.KEY_AREA);
                btn_club_create_area.setText(area);
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_AVATAR_REQUEST) {
                LogUtil.i(TAG, "上传头像");
                //上传头像
                //处理选取图片
                String path = data.getStringExtra(com.netease.nim.uikit.session.constant.Extras.EXTRA_FILE_PATH);
                LogUtil.i(TAG, "pick avatar:" + path);
                updateAvatar(path);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mClubAction != null) {
            mClubAction.onDestroy();
            mClubAction = null;
        }
    }

    public static final int PICK_AVATAR_REQUEST = 100;
    private static final int AVATAR_TIME_OUT = 30000;

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
//        option.cropOutputImageWidth = 720;
//        option.cropOutputImageHeight = 720;
        PickImageHelper.pickImage(this, requestCode, option);
    }

    // data
    AbortableFuture<String> uploadAvatarFuture;

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
                    onUpdateDone();
                    LogUtil.i(TAG, "upload avatar success, url =" + url);
                    LogUtil.i(TAG, "url : " + url);
                    clubAvatar = url;
                    iv_club_head.loadClubAvatarByUrl("0", clubAvatar, HeadImageView.DEFAULT_AVATAR_THUMB_SIZE);
                } else {
                    Toast.makeText(getApplicationContext(), "设置头像失败！", Toast.LENGTH_SHORT).show();
                    onUpdateDone();
                }
            }
        });
    }

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_finish:
                String teamName = edt_club_create_name.getText().toString();
                if (TextUtils.isEmpty(teamName)) {
                    Toast.makeText(getApplicationContext(), R.string.club_create_name_not_null, Toast.LENGTH_SHORT).show();
                    return;
                }
//                if (regionEntity == null) {
//                    Toast.makeText(getApplicationContext(), R.string.club_create_region_not_null, Toast.LENGTH_SHORT).show();
//                    return;
//                }
                createTeam(teamName, /*regionEntity.getId()*/0);
                break;
            case R.id.btn_upgrade_vip:
                ShopActivity.start(this, ShopActivity.TYPE_SHOP_VIP);
                break;
            case R.id.btn_club_create_area:
                RegionActivity.start(ClubCreateActivity.this, RegionConstants.TYPE_COUNTY, null, null, null, RegionActivity.FROM_CREATE_CLUB);
                break;
            case R.id.iv_club_head:
                pickImage(PICK_AVATAR_REQUEST);
                break;
        }
    }
}
