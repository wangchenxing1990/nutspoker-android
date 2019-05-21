package com.htgames.nutspoker.chat.contact.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ListView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.contact.adapter.UserAdapter;
import com.htgames.nutspoker.tool.JsonResolveUtil;
import com.htgames.nutspoker.ui.action.SearchAction;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.htgames.nutspoker.view.ResultDataView;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索用户
 */
public class SearchUserActivity extends BaseActivity {
    private final static String TAG = "SearchUserActivity";
    private ListView lv_result;
    List<String> accountList;
    List<NimUserInfo> userInfoList;
    UserAdapter adapter;
    String key;
    ResultDataView mResultDataView;
    SearchAction mSearchAction;

    public static void start(Activity activity , ArrayList<NimUserInfo> list){
        Intent intent = new Intent(activity , SearchUserActivity.class);
        intent.putExtra(Extras.EXTRA_USER_LIST, list);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);
        setHeadTitle(R.string.club_search_head);
        initView();
        initResultDataView();
        mSearchAction = new SearchAction(this , null);
        key = getIntent().getStringExtra(Extras.EXTRA_SEARCH_KEY);
        userInfoList = (ArrayList)getIntent().getSerializableExtra(Extras.EXTRA_USER_LIST);
        if(!TextUtils.isEmpty(key)){
            searchUser(key);
        } else if(userInfoList != null && userInfoList.size() != 0){
            mResultDataView.successShow();
            setListView(userInfoList);
        }
    }

    private void initResultDataView() {
        mResultDataView = (ResultDataView)findViewById(R.id.mResultDataView);
        mResultDataView.setReloadDataCallBack(new ResultDataView.ReloadDataCallBack() {
            @Override
            public void onReloadData() {
                searchUser(key);
            }
        });
    }

    private void initView() {
        lv_result = (ListView)findViewById(R.id.lv_result);
    }

    /**
     * 创建创建
     */
    public void searchUser(final String word) {
        if (!NetworkUtil.isNetAvailable(this)) {
            mResultDataView.showError(getApplicationContext() ,R.string.network_is_not_available);
            return;
        }
        mResultDataView.showLoading();
//        DialogMaker.showProgressDialog(this, getString(com.netease.nim.uikit.R.string.empty), true);
        mSearchAction.searchUser(word, new com.htgames.nutspoker.interfaces.RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if (code == 0) {
                    accountList = JsonResolveUtil.getSearchUserList(result);
                    getUserListByNetease();
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

    public void getUserListByNetease(){
        if(accountList != null && accountList.size() != 0){
            //获取顺便构建缓存
            NimUserInfoCache.getInstance().getUserInfoFromRemote(accountList, new RequestCallback<List<NimUserInfo>>() {
                @Override
                public void onSuccess(List<NimUserInfo> nimUserInfos) {
                    mResultDataView.successShow();
                    setListView(nimUserInfos);
                }

                @Override
                public void onFailed(int code) {
                    getFailed();
                    if (code == 408) {
                        Toast.makeText(getApplicationContext(), R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "on failed:" + code, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onException(Throwable throwable) {

                }
            });
        } else{
            getDataNull();
        }
    }

    public void getFailed(){
        mResultDataView.showError(getApplicationContext() ,R.string.friend_search_failed);
    }

    public void getDataNull(){
        mResultDataView.nullDataShow(R.string.friend_search_null);
    }

    public void setListView(List<NimUserInfo> nimUserInfos){
        if(nimUserInfos != null && nimUserInfos.size() != 0){
            adapter = new UserAdapter(this , (ArrayList)nimUserInfos);
            lv_result.setAdapter(adapter);
        }else{
//            Toast.makeText(getApplicationContext() , "搜索结果为空！" , Toast.LENGTH_SHORT).show();
            getDataNull();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        VerifyPromptDialog mVerifyPromptDialog = new VerifyPromptDialog(this);
//        mVerifyPromptDialog.setInfo(account);
//        mVerifyPromptDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSearchAction != null){
            mSearchAction.onDestroy();
            mSearchAction = null;
        }
    }
}
