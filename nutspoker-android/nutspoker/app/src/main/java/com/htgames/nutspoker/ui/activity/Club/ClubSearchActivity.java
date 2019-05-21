package com.htgames.nutspoker.ui.activity.Club;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.region.RegionEntity;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.tool.JsonResolveUtil;
import com.htgames.nutspoker.ui.action.SearchAction;
import com.htgames.nutspoker.ui.adapter.team.ClubSearchAdapter;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.htgames.nutspoker.view.ResultDataView;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.chesscircle.entity.TeamEntity;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 20150726 on 2015/10/10.
 */
public class ClubSearchActivity extends BaseActivity {
    private final static String TAG = "ClubSearchActivity";
    private ListView lv_club;
    int searchType = 0;
    String searchWord = "";
    String searchId = "";
    RegionEntity searchRegion = null;
    ArrayList<TeamEntity> teamList;
    ClubSearchAdapter mClubAdapter;
    //1->按区域id 2->按关键字 3->显示效果（群id 或者 群名称）
    public final static int SEARCH_TYPE_ARE = 1;
    public final static int SEARCH_TYPE_TEAM_WORD = 2;
    public final static int SEARCH_SHOW_CLUB_LIST = 3;//显示效果
    ResultDataView mResultDataView;
    SearchAction mSearchAction;

    public static void start(Activity activity, int searchType, String clubWord, RegionEntity clubRegion) {
        Intent intent = new Intent(activity, ClubSearchActivity.class);
        intent.putExtra(Extras.EXTRA_SEARCH_CLUB_TYPE, searchType);
        if (!TextUtils.isEmpty(clubWord)) {
            intent.putExtra(Extras.EXTRA_SEARCH_CLUB_KEY, clubWord);
        }
        if (clubRegion != null) {
            intent.putExtra(Extras.EXTRA_SEARCH_CLUB_AREA, clubRegion);
        }
        activity.startActivity(intent);
    }

    public static void startTeamList(Activity activity, ArrayList<TeamEntity> clubList) {
        Intent intent = new Intent(activity, ClubSearchActivity.class);
        intent.putExtra(Extras.EXTRA_SEARCH_CLUB_TYPE, SEARCH_SHOW_CLUB_LIST);
        intent.putExtra(Extras.EXTRA_TEAM_LIST, clubList);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);
        setHeadTitle(R.string.club_search_head);
        searchType = getIntent().getIntExtra(Extras.EXTRA_SEARCH_CLUB_TYPE, SEARCH_TYPE_TEAM_WORD);
        if (searchType == SEARCH_TYPE_ARE) {
            searchRegion = (RegionEntity) getIntent().getSerializableExtra(Extras.EXTRA_SEARCH_CLUB_AREA);
        } else if (searchType == SEARCH_TYPE_TEAM_WORD) {
            searchWord = getIntent().getStringExtra(Extras.EXTRA_SEARCH_CLUB_KEY);
        } else if(searchType == SEARCH_SHOW_CLUB_LIST){

        }
        mSearchAction = new SearchAction(this , null);
        initView();
        initResultDataView();
        if(searchType == SEARCH_SHOW_CLUB_LIST){
            teamList = (ArrayList<TeamEntity>) getIntent().getSerializableExtra(Extras.EXTRA_TEAM_LIST);
            setTeamList();
        }else{
            searchTeam();
        }
    }

    private void initResultDataView() {
        mResultDataView = (ResultDataView) findViewById(R.id.mResultDataView);
        mResultDataView.setReloadDataCallBack(new ResultDataView.ReloadDataCallBack() {
            @Override
            public void onReloadData() {
                searchTeam();
            }
        });
    }

    private void initView() {
        lv_club = (ListView) findViewById(R.id.lv_result);
        lv_club.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TeamEntity team = (TeamEntity) mClubAdapter.getItem(position);
                ClubInfoActivity.start(ClubSearchActivity.this, team.id, ClubInfoActivity.FROM_TYPE_LIST);
            }
        });
    }

    public void searchTeam() {
        if (!NetworkUtil.isNetAvailable(this)) {
            mResultDataView.showError(getApplicationContext(), R.string.network_is_not_available);
            return;
        }
        mResultDataView.showLoading();
        String content = "";
        if (searchType == SEARCH_TYPE_ARE) {
            content = String.valueOf(searchRegion == null ? "" : searchRegion.id);
        } else if (searchType == SEARCH_TYPE_TEAM_WORD) {
            content = searchWord;
        }
        mSearchAction.searchTeam(searchType, content, new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if (code == 0) {
                    teamList = JsonResolveUtil.getTeamList(result);
                    fetchUserInfo();//更新用户信息
                    setTeamList();
                } else {
                    getFailed();
                }
            }

            @Override
            public void onFailed() {
                getFailed();
            }
        });
    }

    private void fetchUserInfo() {
        if (teamList == null || teamList.size() <= 0) {
            return;
        }
        ArrayList<String> uids = new ArrayList<>();
        for (int i = 0; i < teamList.size(); i++) {
            uids.add(teamList.get(0).creator);
        }
        getUserListByNeteaseEx(uids, 0);
    }

    void getUserListByNeteaseEx(final List<String> list, int curIndex){
        if (list != null && !list.isEmpty()) {
            List<String> tempList = new ArrayList<>();
            while(tempList.size() < NimUIKit.NeteaseAccountLimit && list.size() > curIndex){
                tempList.add(list.get(curIndex));
                curIndex ++;
            }
            final int theNewIndex = curIndex;
            NimUserInfoCache.getInstance().getUserInfoFromRemote(tempList, new com.netease.nimlib.sdk.RequestCallback<List<NimUserInfo>>() {
                @Override
                public void onSuccess(List<NimUserInfo> nimUserInfos) {
                    if(theNewIndex+1 < list.size()) { //未获取完毕继续获取
                        getUserListByNeteaseEx(list,theNewIndex);
                    } else {
                    }
                }
                @Override
                public void onFailed(int code) {
                    if(theNewIndex+1 < list.size()) { //未获取完毕继续获取
                        getUserListByNeteaseEx(list,theNewIndex);
                    } else {
                    }
                }
                @Override
                public void onException(Throwable throwable) {
                    if(theNewIndex+1 < list.size()) { //未获取完毕继续获取
                        getUserListByNeteaseEx(list,theNewIndex);
                    } else {
                    }
                }
            });
        } else {
        }
    }

    public void setTeamList() {
        mResultDataView.successShow();
        if (teamList != null && teamList.size() != 0) {
            mClubAdapter = new ClubSearchAdapter(this, teamList);
            lv_club.setAdapter(mClubAdapter);
        } else {
            mResultDataView.nullDataShow(R.string.no_data);
        }
    }

    public void getFailed() {
        mResultDataView.showError(getApplicationContext(), R.string.club_join_search_failed);
    }
//    public void getTeamList(){
//        if(teamIdList != null && teamIdList.size() != 0){
//            NIMClient.getService(TeamService.class).queryTeamListById(teamIdList).setCallback(new RequestCallback<List<Team>>() {
//                @Override
//                public void onSuccess(List<Team> teams) {
//                    Log.d(TAG, "getTeamList onSuccess:" + teams.size());
//                    teamList = (ArrayList) teams;
//                    mClubAdapter = new ClubAdapter(ClubSearchActivity.this, teamList);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            lv_club.setAdapter(mClubAdapter);
//                        }
//                    });
//                }
//
//                @Override
//                public void onFailed(int i) {
//                    Log.d(TAG , "getTeamList onFailed");
//                }
//
//                @Override
//                public void onException(Throwable throwable) {
//
//                }
//            });
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSearchAction != null){
            mSearchAction.onDestroy();
            mSearchAction = null;
        }
    }
}
