package com.htgames.nutspoker.ui.activity.Club;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.picker.PickImageHelper;
import com.htgames.nutspoker.chat.region.RegionConstants;
import com.htgames.nutspoker.chat.region.RegionEntity;
import com.htgames.nutspoker.chat.region.activity.RegionActivity;
import com.htgames.nutspoker.chat.region.db.RegionDBTools;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.ui.base.BaseTeamActivity;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.htgames.nutspoker.tool.NameTrimTools;
import com.htgames.nutspoker.ui.action.EditClubInfoAction;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.session.actions.PickImageAction;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.nos.NosService;
import com.netease.nimlib.sdk.team.model.Team;

import org.json.JSONObject;

import java.io.File;

/**
 * 俱乐部设置
 */
public class ClubEditActivity extends BaseTeamActivity implements View.OnClickListener {
    public final static String TAG = "ClubEditActivity";
    public final static int REQUESTCODE_EDIT = 3;
    Team team;
    private RelativeLayout rl_club_head;
    private RelativeLayout rl_club_name;
    private RelativeLayout rl_club_area;
    private RelativeLayout rl_club_introduce;
    String requestUpdateUrl;
    HeadImageView iv_club_head;
    TextView tv_club_name;
    TextView tv_club_area;
    TextView tv_club_introduce;
    int currentAreaId = 0;
    String newArea = "";
    String clubName = "";
    String clubIntroduce = "";
    EditClubInfoAction mEditClubInfoAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_edit);
        team = (Team) getIntent().getSerializableExtra(Extras.EXTRA_TEAM_DATA);
        mEditClubInfoAction = new EditClubInfoAction(this, null);
        initHead();
        initView();
        getTeamInfo();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            RegionEntity regionEntity = (RegionEntity) intent.getSerializableExtra(Extras.EXTRA_REGION_DATA);
            if (regionEntity != null) {
                if (currentAreaId == regionEntity.id) {
                    return;
                }
                currentAreaId = regionEntity.id;
                newArea = RegionDBTools.getShowRegionContent(Integer.valueOf(regionEntity.id), " ");
                if (!TextUtils.isEmpty(newArea)) {
                    LogUtil.i(TAG, newArea);
                    tv_club_area.setText(newArea);
                }
            }
        }
    }

    private void initView() {
        rl_club_head = (RelativeLayout) findViewById(R.id.rl_club_head);
        rl_club_name = (RelativeLayout) findViewById(R.id.rl_club_name);
        rl_club_area = (RelativeLayout) findViewById(R.id.rl_club_area);
        rl_club_introduce = (RelativeLayout) findViewById(R.id.rl_club_introduce);
        iv_club_head = (HeadImageView) findViewById(R.id.iv_club_head);
        tv_club_name = (TextView) findViewById(R.id.tv_club_name);
        tv_club_area = (TextView) findViewById(R.id.tv_club_area);
        tv_club_introduce = (TextView) findViewById(R.id.tv_club_introduce);
        rl_club_head.setOnClickListener(this);
        rl_club_name.setOnClickListener(this);
        rl_club_area.setOnClickListener(this);
        rl_club_introduce.setOnClickListener(this);
//        btn_save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String teamName = edt_club_name.getText().toString();
//                String teamIntroduce = edt_club_introduce.getText().toString();
//                String teamNameGBK = null;
//                String teamIntroduceGBK = null;
//                try {
//                    //转换汉字编码
//                    teamNameGBK = new String(teamName.getBytes("GBK"), "ISO8859_1");
//                    teamIntroduceGBK = new String(teamIntroduce.getBytes("GBK"), "ISO8859_1");
//                } catch (UnsupportedEncodingException ex) {
//                }
//                if (TextUtils.isEmpty(teamName)) {
//                    Toast.makeText(getApplicationContext(), R.string.club_name_empty, Toast.LENGTH_SHORT).show();
//                    return;
//                } else if (!TextUtils.isEmpty(teamNameGBK) && teamNameGBK.length() > 20) {
//                    Toast.makeText(getApplicationContext(), R.string.club_name_is_long , Toast.LENGTH_SHORT).show();
//                    return;
//                } else if (!TextUtils.isEmpty(teamIntroduceGBK) && teamIntroduceGBK.length() > 60) {
//                    Toast.makeText(getApplicationContext(), R.string.club_introduce_is_long, Toast.LENGTH_SHORT).show();
//                    return;
//                }
////                doUpdateTeamInfo(teamName , teamIntroduce);
//                updateClubInfo(teamName, teamIntroduce);
//            }
//        });
//        //
//        edt_club_name.setFilters(new InputFilter[]{new NoSpaceAndEnterInputFilter(), new NameLengthFilter(20), new NoEmojiInputFilter()});
//        edt_club_introduce.setFilters(new InputFilter[]{new NoSpaceAndEnterInputFilter(), new NameLengthFilter(60), new NoEmojiInputFilter()});
    }

    private void initHead() {
        setHeadTitle(R.string.club_edit_head);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void getTeamInfo() {
        Team teamInfo = TeamDataCache.getInstance().getTeamById(team.getId());
        if (teamInfo != null) {
            team = teamInfo;
            clubIntroduce = team.getIntroduce();
            clubIntroduce = NameTrimTools.getNameTrim(clubIntroduce);
            clubName = team.getName();
            tv_club_name.setText(team.getName());
            if (TextUtils.isEmpty(clubIntroduce)) {
//                tv_club_introduce.setText("暂无介绍");
                tv_club_introduce.setVisibility(View.GONE);
            } else {
                tv_club_introduce.setVisibility(View.VISIBLE);
                tv_club_introduce.setText(clubIntroduce);
            }
            //区域
            String extension = team.getExtServer();
            LogUtil.i(TAG, "extension : " + extension);
            if (TextUtils.isEmpty(newArea)) {
                currentAreaId = ClubConstant.getClubExtAreaId(extension);
                if (currentAreaId != 0) {
                    String area = RegionDBTools.getShowRegionContent(currentAreaId, " ");
                    if (!TextUtils.isEmpty(area)) {
                        LogUtil.i(TAG, area);
                        tv_club_area.setText(area);
                    }
                }
                String avatar = ClubConstant.getClubExtAvatar(extension);
                iv_club_head.loadClubAvatarByUrl(team.getId(), avatar, HeadImageView.DEFAULT_AVATAR_THUMB_SIZE);
                LogUtil.i(TAG, "avatar :" + avatar);
            } else {
                tv_club_area.setText(newArea);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelAll(requestUpdateUrl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_club_head:
                pickImage(PICK_AVATAR_REQUEST);
                break;
            case R.id.rl_club_name:
                EditClubItemActivity.start(this, team.getId(), ClubConstant.KEY_NAME, clubName, REQUESTCODE_EDIT);
                break;
            case R.id.rl_club_area:
                RegionActivity.start(this, RegionConstants.TYPE_COUNTY, team.getId(), null, null, currentAreaId, RegionActivity.FROM_EDIT_CLUB);
                break;
            case R.id.rl_club_introduce:
                EditClubItemActivity.start(this, team.getId(), ClubConstant.KEY_INTRODUCE, clubIntroduce, REQUESTCODE_EDIT);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_EDIT) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                int editKey = data.getIntExtra(Extras.EXTRA_EDIT_KEY, 0);
                String editContent = data.getStringExtra(Extras.EXTRA_DATA);
                if (editKey == ClubConstant.KEY_NAME) {
                    clubName = editContent;
                    tv_club_name.setText(clubName);
                } else if (editKey == ClubConstant.KEY_INTRODUCE) {
                    clubIntroduce = editContent;
                    clubIntroduce = NameTrimTools.getNameTrim(clubIntroduce);
                    if (TextUtils.isEmpty(editContent)) {
                        tv_club_introduce.setVisibility(View.GONE);
                    } else {
                        tv_club_introduce.setVisibility(View.VISIBLE);
                        tv_club_introduce.setText(editContent);
                    }
                }
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
        PickImageHelper.pickImage(ClubEditActivity.this, requestCode, option);
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
                    mEditClubInfoAction.updateClubInfo(team.getId(), null, null, null, url, new GameRequestCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            if (iv_club_head != null) {
                                iv_club_head.loadClubAvatarByUrl(team.getId(), url, HeadImageView.DEFAULT_AVATAR_THUMB_SIZE);
                            }
                        }
                        @Override
                        public void onFailed(int code, JSONObject response) {
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), R.string.user_info_update_failed, Toast.LENGTH_SHORT).show();
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
}
