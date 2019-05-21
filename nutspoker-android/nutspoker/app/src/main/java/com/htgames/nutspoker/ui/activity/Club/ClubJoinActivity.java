package com.htgames.nutspoker.ui.activity.Club;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.region.RegionConstants;
import com.htgames.nutspoker.chat.region.RegionEntity;
import com.htgames.nutspoker.chat.region.activity.RegionActivity;
import com.htgames.nutspoker.chat.region.adapter.RegionAdapter;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon;
import com.htgames.nutspoker.view.widget.CustomListView;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.session.constant.Extras;

import java.util.ArrayList;

/**
 * Created by 20150726 on 2015/10/10.
 */
public class ClubJoinActivity extends BaseActivity implements View.OnClickListener{
    ClearableEditTextWithIcon edit_search_club;
    private final static String TAG = "ClubJoinActivity";
    TextView join_club_cancel_tv;
    CustomListView lv_region;
    ArrayList<RegionEntity> provinceList;
    RegionAdapter mRegionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_join);
        setHeadTitle(R.string.club_join);
        initView();
//        provinceList = RegionDBTools.getChinaProvinceList();
//        setRegionList();
        sHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                edit_search_club.setFocusable(true);
                edit_search_club.setFocusableInTouchMode(true);
                edit_search_club.requestFocus();
                showKeyboard(true);
            }
        }, 200L);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent != null){
            String show = intent.getStringExtra(Extras.EXTRA_REGION_SHOW);
            RegionEntity regionEntity = (RegionEntity)intent.getSerializableExtra(Extras.EXTRA_REGION_DATA);
            ClubSearchActivity.start(ClubJoinActivity.this, ClubSearchActivity.SEARCH_TYPE_ARE, "", regionEntity);
        }
    }

    public void setRegionList() {
        if(provinceList != null){
            mRegionAdapter = new RegionAdapter(getApplicationContext(), provinceList);
            lv_region.setAdapter(mRegionAdapter);
        }
    }

    private void initView() {
        join_club_cancel_tv = (TextView) findViewById(R.id.join_club_cancel_tv);
        join_club_cancel_tv.setOnClickListener(this);
        edit_search_club = (ClearableEditTextWithIcon) findViewById(R.id.edit_search_club);
        edit_search_club.setText("");
        edit_search_club.setIconResource(R.mipmap.icon_search);
        lv_region = (CustomListView) findViewById(R.id.lv_region);
        lv_region.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RegionEntity regionEntity = (RegionEntity) parent.getItemAtPosition(position);
                if (regionEntity.type == RegionConstants.TYPE_PROVINCE) {
                    if (RegionConstants.isProvinceCity(regionEntity.name)) {
                        //是省级直辖市
                        ClubSearchActivity.start(ClubJoinActivity.this, ClubSearchActivity.SEARCH_TYPE_ARE, "", regionEntity);
                    } else {
                        RegionActivity.start(ClubJoinActivity.this, RegionConstants.TYPE_CITY, null, regionEntity, "", RegionActivity.FROM_SEARCH_CLUB);
                    }
                }
            }
        });
        edit_search_club.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    searchKey();
                    return true;
                }
                return false;
            }
        });
    }

    public void searchKey() {
        String key = edit_search_club.getText().toString();
        key = key.trim();
        if (TextUtils.isEmpty(key)) {
            Toast.makeText(getApplicationContext(), R.string.not_allow_empty, android.widget.Toast.LENGTH_SHORT).show();
        } else {
            ClubSearchActivity.start(ClubJoinActivity.this, ClubSearchActivity.SEARCH_TYPE_TEAM_WORD, key, null);
        }
    }

//    public void queryClubList(String clubName) {
//        ClubInfoActivity.start(ClubJoinActivity.this, clubName, ClubInfoActivity.FROM_TYPE_LIST);
//    }

//    private void queryTeamById(final String clubName) {
//        NIMClient.getService(TeamService.class).searchTeam(clubName).setCallback(new RequestCallback<Team>() {
//            @Override
//            public void onSuccess(Team team) {
//                ClubInfoActivity.start(ClubJoinActivity.this, clubName, ClubInfoActivity.FROM_TYPE_LIST);
//            }
//
//            @Override
//            public void onFailed(int code) {
//                if (code == 803) {
//                    Toast.makeText(getApplicationContext(), R.string.club_join_search_notexist_content, Toast.LENGTH_SHORT).show();
//                } else {
//                    LogUtil.i(TAG, "search team failed: " + code);
//                    Toast.makeText(getApplicationContext(), R.string.club_join_search_failed, Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onException(Throwable exception) {
//                LogUtil.i(TAG, "search team exception：" + exception.getMessage());
//                Toast.makeText(getApplicationContext(), "search team exception：" + exception.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.join_club_cancel_tv:
                finish();
                break;
        }
    }
}
