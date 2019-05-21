package com.htgames.nutspoker.chat.main.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.contact.PhoneContactTools;
import com.htgames.nutspoker.chat.contact.adapter.PhoneContactAdapter;
import com.htgames.nutspoker.chat.contact.bean.PhoneContactEntity;
import com.htgames.nutspoker.chat.contact.model.ContactRelation;
import com.htgames.nutspoker.db.ContactDBHelper;
import com.htgames.nutspoker.ui.action.SearchAction;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.view.ResultDataView;
import com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon;
import com.htgames.nutspoker.view.widget.CustomListView;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 高级搜索
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener{
    private final static String TAG = "SearchActivity";
    public Team mTeam;//从"俱乐部详情页"添加成员进入到搜索页时附带的"俱乐部"Team信息
    TextView tv_cancel;
    ClearableEditTextWithIcon edit_search;
    ResultDataView mResultDataView;
    //手机联系人
    LinearLayout ll_phonecontact;
    CustomListView lv_phonecontact;
    ArrayList<PhoneContactEntity> phoneContactList;
    ArrayList<String> phoneContactAccountList;
    PhoneContactAdapter mPhoneContactAdapter;
    Map<String , ArrayList<String>> registeredPhoneUidMap = new HashMap<String , ArrayList<String>>();
    SearchAction mSearchAction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mTeam = (Team) getIntent().getSerializableExtra(Extras.EXTRA_TEAM_DATA);
        initView();
        registeredPhoneUidMap = ContactDBHelper.getContactMap(getApplicationContext());
        initResultDataView();
        edit_search.requestFocus();
        edit_search.setIconResource(R.mipmap.icon_search);
        mSearchAction = new SearchAction(this , null);
        sHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                edit_search.setFocusable(true);
                edit_search.setFocusableInTouchMode(true);
                edit_search.requestFocus();
                showKeyboard(true);
            }
        } , 200L);
    }

    public void searchPhoneContact(final String key){
        phoneContactAccountList = new ArrayList<String>();
        phoneContactList = PhoneContactTools.getPhoneContactListByWord(getApplicationContext() , key);
        int size = phoneContactList == null ? 0 : phoneContactList.size();
        for(int i = 0 ; i < size ; i++){
            PhoneContactEntity phoneContactEntity = phoneContactList.get(i);
            String phone = phoneContactEntity.getPhone();
            phoneContactAccountList.add(phone);
            if(!TextUtils.isEmpty(phoneContactEntity.getPhone()) && registeredPhoneUidMap.containsKey(phone)){
                ArrayList<String> uids = registeredPhoneUidMap.get(phone);
                if(uids != null && uids.size() != 0) {
                    String uid = uids.get(0);
                    phoneContactEntity.setUid(uid);
                }
//                if(NIMClient.getService(FriendService.class).isMyFriend(uid)){
//                    phoneContactList.get(i).setRelation(PhoneContactEntity.ContactRelation.FRIEND);
//                }else{
                phoneContactList.get(i).setRelation(ContactRelation.REGISTERED);
//                }
            }
            LogUtil.i(TAG , phoneContactList.get(i).getName() + ":" + phoneContactList.get(i).getPhone());
            phoneContactList.get(i).setSignature(String.format(getString(R.string.contact_from_phone), phone));
        }
        if(phoneContactAccountList != null && phoneContactAccountList.size() != 0){
//            ll_phonecontact.setVisibility(View.VISIBLE);//todo 改版后搜索结果只保留个人，通讯录和俱乐部删除
            ll_phonecontact.setVisibility(View.GONE);
            mPhoneContactAdapter = new PhoneContactAdapter(this , phoneContactList , false , false);
            mPhoneContactAdapter.setKeyWord(key);//关键字
            lv_phonecontact.setAdapter(mPhoneContactAdapter);
        } else{
            ll_phonecontact.setVisibility(View.GONE);
        }
    }

    private void initResultDataView() {
        mResultDataView = (ResultDataView)findViewById(R.id.mResultDataView);
        mResultDataView.setReloadDataCallBack(new ResultDataView.ReloadDataCallBack() {
            @Override
            public void onReloadData() {
                searchKey(edit_search.getText().toString());
            }
        });
        mResultDataView.successShow();
    }

    private void initView() {
        //手机联系人
        ll_phonecontact = (LinearLayout) findViewById(R.id.ll_phonecontact);
        lv_phonecontact = (CustomListView) findViewById(R.id.lv_phonecontact);
        //
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        edit_search = (ClearableEditTextWithIcon) findViewById(R.id.edit_search);
        tv_cancel.setOnClickListener(this);
        edit_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String key = edit_search.getText().toString();
                    key = key.trim();
                    if (TextUtils.isEmpty(key)) {
                        Toast.makeText(getApplicationContext(), R.string.not_allow_empty, Toast.LENGTH_SHORT).show();
                    } else {
                        ll_phonecontact.setVisibility(View.GONE);
                        searchKey(key);
                    }
                    return true;
                }
                return false;
            }
        });
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String word = s.toString();
                mResultDataView.successShow();
                if (!TextUtils.isEmpty(word)) {
//                    searchPhoneContact(word);// TODO: 16/12/20 手机联系人暂时去掉，否则会弹出申请权限对话框
                } else {
                    ll_phonecontact.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 搜索
     */
    public void searchKey(final String word) {
        if (!NetworkUtil.isNetAvailable(this)) {
            mResultDataView.showError(getApplicationContext(), R.string.network_is_not_available);
            return;
        }
        mSearchAction.searchKey(word, new com.htgames.nutspoker.interfaces.RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if (code == 0) {
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

    public void getFailed() {
        ll_phonecontact.setVisibility(View.GONE);
        mResultDataView.showError(getApplicationContext(), R.string.search_failed);
    }

    @Override
    protected void onDestroy() {
        if(mSearchAction != null){
            mSearchAction.onDestroy();
            mSearchAction = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.tv_user_more:
//                SearchUserActivity.start(this , (ArrayList)userInfoList);
//                break;
//            case R.id.tv_team_more:
//                ClubSearchActivity.startTeamList(this , (ArrayList)teamList);
//                break;
            case R.id.tv_cancel:
                finish();
                break;
        }
    }
}
