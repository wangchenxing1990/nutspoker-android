package com.htgames.nutspoker.chat.region.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.region.RegionConstants;
import com.htgames.nutspoker.chat.region.RegionEntity;
import com.htgames.nutspoker.chat.region.adapter.RegionAdapter;
import com.htgames.nutspoker.chat.region.db.RegionDBTools;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.ui.activity.Club.ClubCreateActivity;
import com.htgames.nutspoker.ui.activity.Club.ClubEditActivity;
import com.htgames.nutspoker.ui.activity.Club.ClubSearchActivity;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.data.location.AddressEntity;
import com.htgames.nutspoker.data.location.HtLocationManager;
import com.htgames.nutspoker.data.location.LocationRegionListener;
import com.htgames.nutspoker.ui.helper.permission.PermissionHelper;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.htgames.nutspoker.ui.action.EditClubInfoAction;
import com.htgames.nutspoker.ui.action.EditUserInfoAction;
import com.htgames.nutspoker.chat.contact.activity.EditUserInfoActivity;
import com.htgames.nutspoker.data.location.LocationAction;
import com.htgames.nutspoker.ui.activity.Login.RegisterActivity;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.htgames.nutspoker.view.widget.CustomListView;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.session.constant.Extras;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 区域选择
 */
public class RegionActivity extends BaseActivity {
    private final static String TAG = "RegionActivity";
    RegionAdapter mRegionAdapter;
    CustomListView lv_region;
    TextView tv_region_tip;
//    TextView iv_region_current;
    ArrayList<RegionEntity> regionList;//区域列表
    private int type = RegionConstants.TYPE_COUNTY;
    RegionEntity regionEntity;
    String choiceRegion = "";
    public final static int FROM_REGUSTER = 0;//来自：注册
    public final static int FROM_EDIT_USER = 1;//来自：编辑个人信息
    public final static int FROM_EDIT_CLUB = 2;//来自：编辑俱乐部信息
    public final static int FROM_CREATE_CLUB = 3;//来自：创建俱乐部
    public final static int FROM_SEARCH_CLUB = 4;//来自：搜索俱乐部
    public int from = FROM_EDIT_USER;
    EditUserInfoAction mEditUserInfoAction;
    EditClubInfoAction mEditClubInfoAction;
    String sessionId = "";//编辑俱乐部信息的时候用到
    int myAreaId = 0;
    LinearLayout ll_region_current;
    TextView tv_region_current;
    ProgressBar probar_region_ing;
    LocationAction mLocationAction;
    PermissionHelper mPermissionHelper;
    String requestPermission  = PermissionHelper.ACCESS_FINE_LOCATION;
    String currentRegionShowStr = "";
    private int PERMISSINO_REQUESTCODE = 100;

    public static void start(Activity activity , int type , String sessionId ,RegionEntity regionEntity , String choiceRegion , int from){
        Intent intent = new Intent(activity , RegionActivity.class);
        intent.putExtra(Extras.EXTRA_REGION_TYPE, type);
        intent.putExtra(Extras.EXTRA_REGION_DATA, regionEntity);
        intent.putExtra(Extras.EXTRA_SESSION_ID, sessionId);
        intent.putExtra(Extras.EXTRA_REGION_CHOICE, choiceRegion);
        intent.putExtra(Extras.EXTRA_FROM, from);
        activity.startActivity(intent);
    }

    public static void start(Activity activity , int type , String sessionId ,RegionEntity regionEntity , String choiceRegion , int myAreaId ,int from){
        Intent intent = new Intent(activity , RegionActivity.class);
        intent.putExtra(Extras.EXTRA_REGION_TYPE, type);
        intent.putExtra(Extras.EXTRA_REGION_DATA, regionEntity);
        intent.putExtra(Extras.EXTRA_SESSION_ID, sessionId);
        intent.putExtra(Extras.EXTRA_REGION_CHOICE, choiceRegion);
        intent.putExtra(Extras.EXTRA_MY_AREA_ID, myAreaId);
        intent.putExtra(Extras.EXTRA_FROM, from);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region);
        setHeadTitle(R.string.region_head);
        initView();
        initData();
        getRegionList();
        mPermissionHelper = new PermissionHelper(getApplicationContext());
        if (type == RegionConstants.TYPE_COUNTY) {
            if (!mPermissionHelper.hasPermission(requestPermission)) {
                //没有权限
                probar_region_ing.setVisibility(View.GONE);
                tv_region_current.setText(R.string.region_current_location_failuer);
                Drawable drawable = getResources().getDrawable(R.mipmap.icon_systemtip_mark);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_region_current.setCompoundDrawables(drawable, null, null, null);
                ll_region_current.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPermissionHelper.requestPermission(RegionActivity.this, requestPermission, PERMISSINO_REQUESTCODE);
                    }
                });
            } else {
                getLocationRegion();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSINO_REQUESTCODE) {
            LogUtil.i(TAG, "onRequestPermissionsResult");
        }
    }

    public void getLocationRegion() {
        if (mLocationAction == null) {
            mLocationAction = new LocationAction(this, null);
            mLocationAction.setLocationRegionListener(new LocationRegionListener() {

                @Override
                public void onLocationRegionSuccess(AddressEntity address) {
                    if (address != null) {
                        final RegionEntity currentRegion = RegionDBTools.getRegionByKey(address.getCityName());
                        if (currentRegion != null) {
                            tv_region_current.setCompoundDrawables(null, null, null, null);
                            probar_region_ing.setVisibility(View.GONE);
                            StringBuffer stringBuffer = new StringBuffer();
                            stringBuffer.append(address.getCountryName());
                            stringBuffer.append(" ");
                            stringBuffer.append(address.getProvinceName());
                            stringBuffer.append(" ");
                            stringBuffer.append(address.getCityName());
                            currentRegionShowStr = stringBuffer.toString();
                            tv_region_current.setText(currentRegionShowStr);
                            ll_region_current.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    doIntentAction(currentRegion);
                                }
                            });
                        } else {
                            getLocationRegionFailure();
                        }
                    } else {
                        getLocationRegionFailure();
                    }
                }

                @Override
                public void onLocationRegionFailure(final boolean isLocationNotOpen) {
                    getLocationRegionFailure();
                }
            });
        }
        tv_region_current.setCompoundDrawables(null, null, null, null);
        tv_region_current.setText(R.string.region_current_location_ing);
        probar_region_ing.setVisibility(View.VISIBLE);
        ll_region_current.setOnClickListener(null);
        mLocationAction.getLocation();
    }

    public void getLocationRegionFailure() {
        probar_region_ing.setVisibility(View.GONE);
        tv_region_current.setText(R.string.region_current_location_failuer);
        Drawable drawable = getResources().getDrawable(R.mipmap.icon_systemtip_mark);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tv_region_current.setCompoundDrawables(drawable, null, null, null);
        ll_region_current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!HtLocationManager.isLocationOpen(getApplicationContext())) {
                    showSettingsDialog();
                } else {
                    getLocationRegion();
                }
            }
        });
    }

    private void initData() {
        type = getIntent().getIntExtra(Extras.EXTRA_REGION_TYPE, RegionConstants.TYPE_COUNTY);
        regionEntity = (RegionEntity) getIntent().getSerializableExtra(Extras.EXTRA_REGION_DATA);
        choiceRegion = getIntent().getStringExtra(Extras.EXTRA_REGION_CHOICE);
        myAreaId = getIntent().getIntExtra(Extras.EXTRA_MY_AREA_ID, 0);
        from = getIntent().getIntExtra(Extras.EXTRA_FROM, FROM_EDIT_USER);
        sessionId = getIntent().getStringExtra(Extras.EXTRA_SESSION_ID);
        if (type == RegionConstants.TYPE_COUNTY) {
            selection = RegionConstants.COLUMN_TYPE + "= ?";
            selectionArgs = new String[]{String.valueOf(RegionConstants.TYPE_COUNTY)};
            //UI
            tv_region_tip.setText(getString(R.string.region_county_tip));
            ll_region_current.setVisibility(View.VISIBLE);
        } else if (type == RegionConstants.TYPE_PROVINCE) {
            selection = RegionConstants.COLUMN_TYPE + "= ?" + " and " + RegionConstants.COLUMN_PID + "= ?";
            selectionArgs = new String[]{String.valueOf(RegionConstants.TYPE_PROVINCE), String.valueOf(regionEntity.id)};
            //UI
            tv_region_tip.setText(getString(R.string.region_province_tip));
            ll_region_current.setVisibility(View.GONE);
        } else if (type == RegionConstants.TYPE_CITY) {
            selection = RegionConstants.COLUMN_TYPE + "= ?" + " and " + RegionConstants.COLUMN_PID + "= ?";
            selectionArgs = new String[]{String.valueOf(RegionConstants.TYPE_CITY), String.valueOf(regionEntity.id)};
            //UI
            tv_region_tip.setText(getString(R.string.region_city_tip));
            ll_region_current.setVisibility(View.GONE);
        }
//        if (TextUtils.isEmpty(choiceRegion)) {
//            iv_region_current.setVisibility(View.GONE);
//        } else {
//            iv_region_current.setVisibility(View.VISIBLE);
//            iv_region_current.setText(String.format(getString(R.string.region_city_current), choiceRegion));
//        }
    }

    String selection = null;
    String[] selectionArgs = null;

    public void getRegionList() {
        regionList = RegionDBTools.getRegionList(selection , selectionArgs);
        setRegionList();
    }

    private void initView() {
        lv_region = (CustomListView) findViewById(R.id.lv_region);
        ll_region_current = (LinearLayout) findViewById(R.id.ll_region_current);
        probar_region_ing = (ProgressBar) findViewById(R.id.probar_region_ing);
        tv_region_current = (TextView) findViewById(R.id.tv_region_current);
        tv_region_tip = (TextView) findViewById(R.id.tv_region_tip);
        lv_region.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RegionEntity regionEntity = (RegionEntity) parent.getItemAtPosition(position);
                if (type == RegionConstants.TYPE_COUNTY) {
                    if (from == FROM_REGUSTER) {
                        Intent intent = new Intent(RegionActivity.this, RegisterActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(Extras.EXTRA_REGION_SHOW, regionEntity.name);
                        intent.putExtra(Extras.EXTRA_REGION_DATA, regionEntity);
                        startActivity(intent);
                    } else {
                        if (myAreaId == 0) {
                            RegionActivity.start(RegionActivity.this, RegionConstants.TYPE_PROVINCE, sessionId, regionEntity, regionEntity.name, from);
                        } else {
                            RegionActivity.start(RegionActivity.this, RegionConstants.TYPE_PROVINCE, sessionId, regionEntity, regionEntity.name, myAreaId, from);
                        }
                    }
                } else if (type == RegionConstants.TYPE_PROVINCE) {
                    if (RegionConstants.isProvinceCity(regionEntity.name)) {
                        //是省级直辖市
                        doIntentAction(regionEntity);
                    } else {
                        if (myAreaId == 0) {
                            RegionActivity.start(RegionActivity.this, RegionConstants.TYPE_CITY, sessionId, regionEntity, choiceRegion + " " + regionEntity.name, from);
                        } else {
                            RegionActivity.start(RegionActivity.this, RegionConstants.TYPE_CITY, sessionId, regionEntity, choiceRegion + " " + regionEntity.name, myAreaId, from);
                        }
                    }
                } else if (type == RegionConstants.TYPE_CITY) {
                    doIntentAction(regionEntity);
                }
            }
        });
    }

    /**
     * 处理调整意图
     * @param regionEntity
     */
    public void doIntentAction(RegionEntity regionEntity){
        if (from == FROM_REGUSTER) {
            Intent intent = new Intent(RegionActivity.this, RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(Extras.EXTRA_REGION_SHOW, choiceRegion + " " + regionEntity.name);
            intent.putExtra(Extras.EXTRA_REGION_SHOW, regionEntity);
            startActivity(intent);
        } else if (from == FROM_EDIT_USER) {
            editUserInfoRegion(regionEntity);
        } else if (from == FROM_EDIT_CLUB) {
            editClubInfoRegion(regionEntity);
        } else if (from == FROM_CREATE_CLUB) {
            Intent intent = new Intent(RegionActivity.this, ClubCreateActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if (type == RegionConstants.TYPE_COUNTY) {
                intent.putExtra(Extras.EXTRA_REGION_SHOW, currentRegionShowStr);
            } else {
                intent.putExtra(Extras.EXTRA_REGION_SHOW, choiceRegion + " " + regionEntity.name);
            }
            intent.putExtra(Extras.EXTRA_REGION_DATA, regionEntity);
            startActivity(intent);
        } else if (from == FROM_SEARCH_CLUB) {
//            Intent intent = new Intent(RegionActivity.this, ClubJoinActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.putExtra(EXTRA_SHOW, choiceRegion + " " + regionEntity.getName());
//            intent.putExtra(EXTRA_DATA, regionEntity);
//            startActivity(intent);
            ClubSearchActivity.start(this, ClubSearchActivity.SEARCH_TYPE_ARE, "", regionEntity);
        }
    }

    GameRequestCallback requestEditUserInfoCallback;

    public void editUserInfoRegion(final RegionEntity regionEntity){
        if(myAreaId == regionEntity.id) {
            //如果为发生过任何变化，提示修改成功
            Toast.makeText(getApplicationContext(), R.string.user_info_update_success, android.widget.Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegionActivity.this, EditUserInfoActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(Extras.EXTRA_REGION_DATA, choiceRegion + " " + regionEntity.name);
            startActivity(intent);
            LogUtil.i(TAG, "区域未发生过任何变化！");
            return;
        }
        if(mEditUserInfoAction == null){
            mEditUserInfoAction = new EditUserInfoAction(this , null);
        }
        if(requestEditUserInfoCallback == null) {
            requestEditUserInfoCallback = new GameRequestCallback() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    Intent intent = new Intent(RegionActivity.this, EditUserInfoActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(Extras.EXTRA_REGION_DATA, choiceRegion + " " + regionEntity.name);
                    startActivity(intent);
                }

                @Override
                public void onFailed(int code, JSONObject response) {
                    Toast.makeText(getApplicationContext() , R.string.user_info_update_failed , Toast.LENGTH_SHORT).show();
                }
            };
        }
        mEditUserInfoAction.updateUserInfo(null , null ,null ,null , String.valueOf(regionEntity.id) , requestEditUserInfoCallback);
    }

    GameRequestCallback requestEditClubInfoCallback;

    public void editClubInfoRegion(final RegionEntity regionEntity){
        if(myAreaId == regionEntity.id) {
            //如果为发生过任何变化，提示修改成功
            Toast.makeText(getApplicationContext(), R.string.club_edit_success, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegionActivity.this, ClubEditActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(Extras.EXTRA_REGION_DATA, regionEntity);
            startActivity(intent);
            LogUtil.i(TAG , "区域未发生过任何变化！");
            return;
        }
        if(mEditClubInfoAction == null){
            mEditClubInfoAction = new EditClubInfoAction(this , null);
        }
        if(requestEditClubInfoCallback == null) {
            requestEditClubInfoCallback = new GameRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    Intent intent = new Intent(RegionActivity.this, ClubEditActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.putExtra(EXTRA_DATA, choiceRegion + " " + regionEntity.getName());
                    intent.putExtra(Extras.EXTRA_REGION_DATA, regionEntity);
                    startActivity(intent);
                }

                @Override
                public void onFailed(int code, JSONObject response) {
                    Toast.makeText(getApplicationContext() , R.string.club_edit_failed , Toast.LENGTH_SHORT).show();
                }
            };
        }
        mEditClubInfoAction.updateClubInfo(sessionId ,null ,null, String.valueOf(regionEntity.id) , null , requestEditClubInfoCallback);
    }


    public void setRegionList() {
        if(regionList != null){
            mRegionAdapter = new RegionAdapter(getApplicationContext(), regionList);
            lv_region.setAdapter(mRegionAdapter);
        }
    }

    private EasyAlertDialog goSettingsDialog;

    public void showSettingsDialog() {
        String title = getString(R.string.region_current_location_dialog_title);
        String message = getString(R.string.region_current_location_dialog_message);
        if (goSettingsDialog == null) {
            goSettingsDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, title,
                    message, getString(R.string.go_setting), getString(R.string.cancel), true,
                    new EasyAlertDialogHelper.OnDialogActionListener() {

                        @Override
                        public void doCancelAction() {
                        }

                        @Override
                        public void doOkAction() {
                            openGPSSettings();
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            goSettingsDialog.show();
        }
    }

    /**
     * 首先判断GPS模块是否存在或者是开启：
     */
    protected void openGPSSettings() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        // 此为设置完成后返回到获取界面
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mEditUserInfoAction != null){
            mEditUserInfoAction.onDestroy();
            mEditUserInfoAction = null;
        }
        if(mEditClubInfoAction != null){
            mEditClubInfoAction.onDestroy();
            mEditClubInfoAction = null;
        }
        if(mLocationAction != null){
            mLocationAction.onDestroy();
            mLocationAction = null;
        }
        mPermissionHelper = null;
    }
}
