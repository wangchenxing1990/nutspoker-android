package com.htgames.nutspoker.chat.contact.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.PhoneUidEntity;
import com.htgames.nutspoker.chat.contact.PhoneContactTools;
import com.htgames.nutspoker.chat.contact.adapter.PhoneContactAdapter;
import com.htgames.nutspoker.chat.contact.bean.PhoneContactEntity;
import com.htgames.nutspoker.chat.contact.model.ContactRelation;
import com.htgames.nutspoker.db.ContactDBHelper;
import com.htgames.nutspoker.ui.helper.permission.PermissionHelper;
import com.htgames.nutspoker.tool.JsonResolveUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.htgames.nutspoker.ui.action.UserAction;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.netease.nim.uikit.common.framework.ThreadUtil;
import com.htgames.nutspoker.view.ResultDataView;
import com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.cache.FriendDataCache;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.ui.liv.LetterIndexView;
import com.netease.nim.uikit.common.ui.liv.LivIndex;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 手机联系人
 */
public class PhoneContactActivity extends BaseActivity {
    private final static String TAG = "PhoneContactActivity";
    ListView lv_phone_contact;
    ListView lv_search_result;
    private ArrayList<PhoneContactEntity> phoneContactList;
    private ArrayList<PhoneContactEntity> searchContactList;
    private List<String> phoneList;
    PhoneContactAdapter mPhoneContactAdapter = null;
    PhoneContactAdapter mSearchContactAdapter = null;
    public static final int FROM_FRIEND = 0;
    public static final int FROM_REGISTER = 1;
    private int from = FROM_FRIEND;
    ResultDataView mResultDataView;
    RelativeLayout rl_search;
    //
    private LivIndex litterIdx;
    ClearableEditTextWithIcon edit_search_friend;
    ImageView iv_search_icon;
    RelativeLayout rl_phonecontact_contact;
    UserAction mUserAction;
    Map<String , ArrayList<String>> registeredContactMap = new HashMap<String , ArrayList<String>>();//注册过的APP用户列表
    boolean isRegister = false;
    PermissionHelper mPermissionHelper;
    private int PERMISSINO_REQUESTCODE = 100;

    public static void start(Activity activity, int from) {
        Intent intent = new Intent(activity, PhoneContactActivity.class);
        intent.putExtra(Extras.EXTRA_FROM, from);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonecontact_list);
        from = getIntent().getIntExtra(Extras.EXTRA_FROM, FROM_FRIEND);
        mUserAction = new UserAction(this, null);
        initHeadView();
        initView();
        initResultDataView();
        buildLitterIdx();
        registeredContactMap = ContactDBHelper.getContactMap(getApplicationContext());
        registerObserver(true);
        initPermission();
    }

    private void initPermission() {
        mPermissionHelper = new PermissionHelper(getApplicationContext());
        if (!mPermissionHelper.hasPermission(PermissionHelper.ACCESS_READ_CONTACTS)) {
            mPermissionHelper.requestPermission(this, PermissionHelper.ACCESS_READ_CONTACTS, PERMISSINO_REQUESTCODE);
            mResultDataView.nullDataShow(R.string.permission_denied_phone_contacts, 0, View.GONE);
        } else {
            getPhoneContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogUtil.i(TAG, "onRequestPermissionsResult");
        if(requestCode == PERMISSINO_REQUESTCODE) {
            if (mPermissionHelper.hasPermission(PermissionHelper.ACCESS_READ_CONTACTS)) {
            }
        }
    }

    private void registerObserver(boolean register) {
        FriendDataCache.getInstance().registerFriendDataChangedObserver(friendDataChangedObserver, register);
    }

    FriendDataCache.FriendDataChangedObserver friendDataChangedObserver = new FriendDataCache.FriendDataChangedObserver() {
        @Override
        public void onAddedOrUpdatedFriends(List<String> accounts) {
            updataFriendData(accounts);
        }

        @Override
        public void onDeletedFriends(List<String> accounts) {
            updataFriendData(accounts);
        }

        @Override
        public void onAddUserToBlackList(List<String> accounts) {
            updataFriendData(accounts);
        }

        @Override
        public void onRemoveUserFromBlackList(List<String> accounts) {
        }
    };

    public void updataFriendData(List<String> accounts) {
        if (mPhoneContactAdapter != null) {
            mPhoneContactAdapter.notifyDataSetChanged();
        }
        if (mSearchContactAdapter != null) {
            mSearchContactAdapter.notifyDataSetChanged();
        }
    }

    private void buildLitterIdx() {
        LetterIndexView livIndex = (LetterIndexView) findViewById(R.id.liv_index);
        livIndex.setNormalColor(getResources().getColor(R.color.contacts_letters_color));
        TextView litterHit = (TextView)findViewById(R.id.tv_hit_letter);
        litterIdx = mPhoneContactAdapter.createLivIndex(lv_phone_contact, livIndex, litterHit);
        litterIdx.show();
    }

    private void initResultDataView() {
        mResultDataView = (ResultDataView) findViewById(R.id.mResultDataView);
    }

    private void initHeadView() {
        setHeadTitle(R.string.phone_contact_head);
        if (from == FROM_REGISTER) {
            setHeadLeftButtonGone();
            setHeadRightButton(R.string.finish, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.start(PhoneContactActivity.this);
                    finish();
                }
            });
            isRegister = true;
        }
    }

    private void initView() {
        phoneContactList = new ArrayList<PhoneContactEntity>();
        searchContactList = new ArrayList<PhoneContactEntity>();
        edit_search_friend = (ClearableEditTextWithIcon) findViewById(R.id.edit_search_friend);
        lv_phone_contact = (ListView) findViewById(R.id.lv_contact);
        lv_search_result = (ListView) findViewById(R.id.lv_search_result);
        rl_search = (RelativeLayout) findViewById(R.id.rl_search);
        rl_phonecontact_contact = (RelativeLayout) findViewById(R.id.rl_phonecontact_contact);
        iv_search_icon = (ImageView) findViewById(R.id.iv_search_icon);
        //
        mPhoneContactAdapter = new PhoneContactAdapter(this, phoneContactList , true , isRegister);
        lv_phone_contact.setAdapter(mPhoneContactAdapter);
        mSearchContactAdapter = new PhoneContactAdapter(this, searchContactList , false , isRegister);
        lv_search_result.setAdapter(mSearchContactAdapter);
        //
        edit_search_friend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    lv_phone_contact.setVisibility(View.VISIBLE);
                    lv_search_result.setVisibility(View.GONE);
                    iv_search_icon.setVisibility(View.VISIBLE);
                    litterIdx.show();
                } else {
                    lv_phone_contact.setVisibility(View.GONE);
                    lv_search_result.setVisibility(View.VISIBLE);
                    iv_search_icon.setVisibility(View.GONE);
                    litterIdx.hide();
                    searcheContactByKey(s.toString());
                }
            }
        });
    }

    public void searcheContactByKey(String key){
        searchContactList.clear();
        for(PhoneContactEntity phoneContactEntity : phoneContactList){
            if(phoneContactEntity.getName().contains(key)){
                searchContactList.add(phoneContactEntity);
            }
        }
        mSearchContactAdapter.notifyDataSetChanged();
    }

    /**
     * 得到手机通讯录联系人信息
     **/
    private void getPhoneContacts() {
        mResultDataView.showLoading();
        phoneList = new ArrayList<String>();
        ThreadUtil.Execute(new Runnable() {
            @Override
            public void run() {
                phoneContactList.clear();
                phoneContactList.addAll(PhoneContactTools.getPhoneContactList(getApplicationContext()));
                if (phoneContactList != null) {
                    int size = phoneContactList.size();
                    for (int i = size - 1; i >= 0; i--) {
                        String phone = phoneContactList.get(i).getPhone();
                        if (registeredContactMap != null && registeredContactMap.containsKey(phone)) {
                            ArrayList<String> uids = registeredContactMap.get(phone);
                            if (uids != null && uids.size() != 0) {
                                boolean isTwice = false;
                                for (String uid : uids) {
                                    //已经注册
                                    if (isTwice) {
                                        PhoneContactEntity phoneContactEntity = new PhoneContactEntity();
                                        NimUserInfo nimUserInfo = NimUserInfoCache.getInstance().getUserInfo(uid);
                                        phoneContactEntity.setRelation(ContactRelation.REGISTERED);
                                        if (nimUserInfo != null && !TextUtils.isEmpty(nimUserInfo.getName())) {
                                            phoneContactEntity.setSignature(String.format(getString(R.string.contact_app_name), nimUserInfo.getName()));
                                        }
                                        phoneContactEntity.setUid(uid);
                                        phoneContactEntity.setPhone(phone);
                                        phoneContactEntity.setPinyin(phoneContactList.get(i).getPinyin());
                                        phoneContactEntity.setName(phoneContactList.get(i).getName());
                                        phoneContactEntity.setSortKey(phoneContactList.get(i).getSortKey());
                                        //add
                                        phoneContactList.add(phoneContactEntity);
                                    } else {
                                        phoneContactList.get(i).setUid(uid);
                                        NimUserInfo nimUserInfo = NimUserInfoCache.getInstance().getUserInfo(uid);
                                        phoneContactList.get(i).setRelation(ContactRelation.REGISTERED);
                                        if (nimUserInfo != null && !TextUtils.isEmpty(nimUserInfo.getName())) {
                                            phoneContactList.get(i).setSignature(String.format(getString(R.string.contact_app_name), nimUserInfo.getName()));
                                        }
                                    }
                                    LogUtil.i(TAG, phone + "：" + uid);
                                    isTwice = true;
                                }
                            }
                        }
//                    phoneContactEntity.setSignature(phone);
                        phoneList.add(phone);//添加手机号码列表
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (registeredContactMap == null || registeredContactMap.size() == 0) {
                            //数据库中为空，进行加载界面
                            getUidsByPhone(true);
                        } else {
                            //数据库中不为空,直接取本地数据判断
                            getUidsByPhone(false);
                            setContactList();
                        }
                    }
                });
            }
        });
    }

    ArrayList<PhoneUidEntity> phoneUidList = new ArrayList<PhoneUidEntity>();

    public void getUidsByPhone(final boolean isUiUpdate) {
        if (phoneContactList == null || phoneContactList.size() == 0) {
            if(isUiUpdate){
                mResultDataView.nullDataShow(R.string.phone_contact_null, 0, View.GONE);
            }
        } else {
            mUserAction.getUids((ArrayList)phoneList, new com.htgames.nutspoker.interfaces.RequestCallback() {
                @Override
                public void onResult(int code, String result, Throwable var3) {
                    LogUtil.i(TAG , result);
                    if(code == 0){
                        phoneUidList = JsonResolveUtil.getRegisteredPhoneUidList(result);
                        if(phoneUidList != null && phoneUidList.size() != 0){
                            LogUtil.i(TAG , "phoneUidList != 0");
                            ArrayList<String> registeredPhoneUidList = new ArrayList<String>();
                            for(PhoneUidEntity phoneUidEntity : phoneUidList) {
                                String phone = phoneUidEntity.phone;
                                String uid = phoneUidEntity.uid;
                                registeredPhoneUidList.add(uid);
                                if (!registeredContactMap.containsKey(phone)) {
                                    ArrayList<String> uids = new ArrayList<String>();
                                    uids.add(uid);
                                    registeredContactMap.put(phone, uids);
                                } else {
                                    ArrayList<String> uids = registeredContactMap.get(phone);
                                    if (!uids.contains(uid)) {
                                        uids.add(uid);
                                    }
                                    registeredContactMap.put(phone, uids);
                                }
                                LogUtil.i(TAG, phoneUidEntity.phone + ":" + phoneUidEntity.uid);
                            }
                            getUserInfoList(registeredPhoneUidList);
                        }else{
                            if(isUiUpdate){
                                mResultDataView.successShow();
                            }
                        }
                    }
                }

                @Override
                public void onFailed() {
                    if(isUiUpdate){
                        mResultDataView.successShow();
                    }
                }
            });
        }
    }

    public void getUserInfoList(ArrayList<String> registeredPhoneUidList) {
        NimUserInfoCache.getInstance().getUserInfoFromRemote(registeredPhoneUidList, new RequestCallbackWrapper<List<NimUserInfo>>() {
            @Override
            public void onResult(int code, List<NimUserInfo> nimUserInfos, Throwable throwable) {
                mResultDataView.successShow();
                LogUtil.i(TAG, "code :" + code);
                if (code == 200) {
                    //存入数据库联系人
                    if(phoneUidList != null && phoneUidList.size() != 0) {
                        ContactDBHelper.updateContactList(getApplicationContext(), phoneUidList);
                        //排序查重
                        processContactRelation();
                    }
                }
            }
        });
    }

    /**
     * 处理用户关系
     */
    public void processContactRelation() {
        int size = phoneContactList.size();
        NimUserInfo nimUserInfo = null;
        Set<String> processPhoneMap = new HashSet<String>();
        for (int i = 0; i < size; i++) {
            String phone = phoneContactList.get(i).getPhone();
//            if (!NimUserInfoCache.getInstance().hasUser(uid)) {
            if (!registeredContactMap.containsKey(phone)) {
                //不存在这个帐号：未注册
                phoneContactList.get(i).setRelation(ContactRelation.UNREGISTERED);
                phoneContactList.get(i).setUid("");
            } else {
                //已经注册
                ArrayList<String> uids = registeredContactMap.get(phone);
                if (uids != null && uids.size() != 0) {
                    String uid = uids.get(0);
                    if (processPhoneMap.contains(phone) && uids.size() > 1) {
                        //已经加入过，加新的
                        uid = uids.get(1);
                    } else {
                        uid = uids.get(0);
                        processPhoneMap.add(phone);
                    }
                    phoneContactList.get(i).setUid(uid);
                    nimUserInfo = NimUserInfoCache.getInstance().getUserInfo(uid);
                    phoneContactList.get(i).setRelation(ContactRelation.REGISTERED);
                    if (nimUserInfo != null) {
                        phoneContactList.get(i).setSignature(String.format(getString(R.string.contact_app_name), nimUserInfo.getName()));
                    }
                }
            }
        }
        setContactList();
    }

    public void setContactList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (phoneContactList == null || phoneContactList.size() == 0) {
                    mResultDataView.nullDataShow(R.string.phone_contact_null, 0 ,View.GONE);
                } else {
                    //排序
                    Collections.sort(phoneContactList , comparator);
                    mResultDataView.successShow();
                    if (mPhoneContactAdapter != null) {
                        mPhoneContactAdapter.notifyDataSetChanged();
                        mPhoneContactAdapter.updateIndexes();
                    }
                }
            }
        });
    }

    Comparator<PhoneContactEntity> comparator = new Comparator<PhoneContactEntity>() {
        @Override
        public int compare(PhoneContactEntity o1, PhoneContactEntity o2) {
            boolean isRelationSame = (o1.getRelation() == o2.getRelation());
            if (!isRelationSame) {
                return o1.getRelation() == ContactRelation.REGISTERED ? -1 : 1;
            }
//            else if(o1.getSortKey() != o2.getSortKey()) {
//                boolean isSame = (o1.getSortKey().charAt(0) == o2.getSortKey().charAt(0));
//                boolean isSmal = o1.getSortKey().charAt(0) < o2.getSortKey().charAt(0);
//                return isSame ? 0 : (isSmal ? -1 : 1);
//            }
//            return 0;
            return o1.getSortKey().compareTo(o2.getSortKey());
        }
    };

    @Override
    public void onBackPressed() {
        if (from == FROM_REGISTER) {
            MainActivity.start(this);
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPhoneContactAdapter = null;
        if(mUserAction != null){
            mUserAction.onDestroy();
            mUserAction = null;
        }
        registerObserver(false);
    }
}
