package com.htgames.nutspoker.chat.contact.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiConfig;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.main.activity.SearchActivity;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.view.ShareView;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

/**
 * Created by 20150726 on 2015/9/29.
 */
public class AddFriendActivity extends BaseActivity implements View.OnClickListener{
    private ClearableEditTextWithIcon edit_search_friend;
    private RelativeLayout rl_addfriends_by_phone_constact;
    private RelativeLayout rl_addfriends_by_social_constact;
    private RelativeLayout rl_addfriends_by_qrcode;
    private RelativeLayout rl_search;
    ShareView mShareView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_add);
        setHeadTitle(R.string.add);
        initView();
    }

    private void initView() {
        rl_search = (RelativeLayout) findViewById(R.id.rl_search);
        rl_addfriends_by_phone_constact = (RelativeLayout) findViewById(R.id.rl_addfriends_by_phone_constact);
        rl_addfriends_by_social_constact = (RelativeLayout) findViewById(R.id.rl_addfriends_by_social_constact);
        rl_addfriends_by_qrcode = (RelativeLayout) findViewById(R.id.rl_addfriends_by_qrcode);
        edit_search_friend = (ClearableEditTextWithIcon) findViewById(R.id.edit_search_friend);
        edit_search_friend.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String key = edit_search_friend.getText().toString();
                    if (TextUtils.isEmpty(key)) {
                        Toast.makeText(getApplicationContext(), R.string.not_allow_empty, Toast.LENGTH_SHORT).show();
                    } else if (key.equals(DemoCache.getAccount())) {
                        Toast.makeText(getApplicationContext(), R.string.add_friend_self_tip, Toast.LENGTH_SHORT).show();
                    } else {
//                        query();
                        startActivity(new Intent(AddFriendActivity.this, SearchUserActivity.class).putExtra(Extras.EXTRA_SEARCH_KEY, key));
                    }
                    return true;
                }
                return false;
            }
        });
        edit_search_friend.setOnClickListener(this);
        rl_search.setOnClickListener(this);
        rl_addfriends_by_phone_constact.setOnClickListener(this);
        rl_addfriends_by_qrcode.setOnClickListener(this);
        if(ApiConfig.AppVersion.isTaiwanVersion) {
            rl_addfriends_by_social_constact.setVisibility(View.GONE);
        }else {
            rl_addfriends_by_social_constact.setVisibility(View.VISIBLE);
            rl_addfriends_by_social_constact.setOnClickListener(this);
        }
    }

    /**
     * 调用postShare分享。跳转至分享编辑页，然后再分享。</br> [注意]<li>
     * 对于新浪，豆瓣，人人，腾讯微博跳转到分享编辑页，其他平台直接跳转到对应的客户端
     */
    public void postShare(){
        if (mShareView == null) {
            mShareView = new ShareView(AddFriendActivity.this);
            mShareView.setOnCallOverback(new ShareView.OnCallOverback() {
                @Override
                public void showShadow(boolean show) {
                    if(show){
                        findViewById(R.id.view_shadow).setVisibility(View.VISIBLE);
                    } else{
                        findViewById(R.id.view_shadow).setVisibility(View.GONE);
                    }
                }
            });
        }
        mShareView.show();
    }

    private void query() {
        DialogMaker.showProgressDialog(this, null, false);
        final String account = edit_search_friend.getText().toString().toLowerCase();
        NimUserInfoCache.getInstance().getUserInfoFromRemote(account, new RequestCallback<NimUserInfo>() {
            @Override
            public void onSuccess(NimUserInfo user) {
                DialogMaker.dismissProgressDialog();
                if (user == null) {
                    EasyAlertDialogHelper.showOneButtonDiolag(AddFriendActivity.this, R.string.user_not_exsit, R.string.user_tips, R.string.ok, false, null);
                } else {
                    UserProfileActivity.start(AddFriendActivity.this, account);
                }
            }

            @Override
            public void onFailed(int code) {
                DialogMaker.dismissProgressDialog();
                if (code == 408) {
                    Toast.makeText(AddFriendActivity.this, R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddFriendActivity.this, "on failed:" + code, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onException(Throwable exception) {
                DialogMaker.dismissProgressDialog();
            }
        });
    }

    public void queryByHost(){
        //        ContactHttpClient.getInstance().getUserInfo(account, new IContactHttpCallback<User>() {
//            @Override
//            public void onSuccess(User user) {
//                DialogMaker.dismissProgressDialog();
//                if (user == null) {
//                    EasyAlertDialogHelper.showOneButtonDiolag(AddFriendActivity.this, R.string.user_not_exsit,
//                            R.string.user_tips, R.string.ok, false, null);
//                } else {
//                    UserProfileActivity.start(AddFriendActivity.this, account);
//                }
//            }
//
//            @Override
//            public void onFailed(int code, String errorMsg) {
//                DialogMaker.dismissProgressDialog();
//                if (code == 408) {
//                    Toast.makeText(AddFriendActivity.this, R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(AddFriendActivity.this, "on failed:" + code, Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(mShareView != null){
            mShareView.onActivityResult(requestCode , resultCode , data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if(mShareView != null){
            mShareView.onDestroy();
            mShareView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_search:
                startActivity(new Intent(AddFriendActivity.this, SearchActivity.class));
                break;
            case R.id.edit_search_friend:
                startActivity(new Intent(AddFriendActivity.this, SearchActivity.class));
                break;
            case R.id.rl_addfriends_by_phone_constact:
                startActivity(new Intent(AddFriendActivity.this, PhoneContactActivity.class));
                break;
            case R.id.rl_addfriends_by_qrcode:
                break;
            case R.id.rl_addfriends_by_social_constact:
                postShare();
                break;
        }
    }
}
