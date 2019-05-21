package com.htgames.nutspoker.view;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.contact.activity.EditUserInfoActivity;
import com.htgames.nutspoker.chat.picker.PickImageHelper;
import com.htgames.nutspoker.chat.region.db.RegionDBTools;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.htgames.nutspoker.data.common.UserConstant;
import com.htgames.nutspoker.ui.activity.System.ShopActivity;
import com.htgames.nutspoker.ui.helper.WealthHelper;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.nav.Nav;
import com.netease.nim.uikit.nav.UrlConstants;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;

import static com.netease.nim.uikit.common.ui.imageview.HeadImageView.DEFAULT_AVATAR_THUMB_SIZE;

/**
 * Created by 20150726 on 2015/11/9.
 */
public class UserInfoView extends RelativeLayout implements View.OnClickListener {
    private final static String TAG = "UserInfoView";
    AlbumView mAlbumView;
    private TextView tv_nickname;
    private ImageView iv_sex;
    private TextView tv_signature;
    private TextView tv_extension;//拓展：城市，昵称
    private TextView idTextView;
    HeadImageView iv_userhead;
    RelativeLayout ll_userinfo;
    //下面的"金币"和"钻石"
    private View mWealthContainer;//"金币"和"钻石"的父布局
    private TextView coinTextView;
    private TextView diamondTextView;

    public UserInfoView(Context context) {
        super(context);
        init(context);
    }

    public UserInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UserInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_userinfo_head_view, this, true);
        setBackgroundDrawable(context.getResources().getDrawable(R.drawable.user_photo_defualt));
        initAlbumView();
        initView();
        initWealthView();//初始化"金币"和"钻石"
    }

    private void initAlbumView() {
        mAlbumView = (AlbumView) findViewById(R.id.mAlbumView);
        ArrayList<String> imgList = new ArrayList<String>();
        imgList.add("");
        imgList.add("");
        mAlbumView.setAlbumInfo(imgList, AlbumView.ALBUM_TYPE_USER);
    }

    public void initView() {
        iv_userhead = (HeadImageView) findViewById(R.id.iv_userhead);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        iv_sex = (ImageView) findViewById(R.id.iv_sex);
        tv_signature = (TextView) findViewById(R.id.tv_signature);
        tv_extension = (TextView) findViewById(R.id.tv_extension);
        ll_userinfo = (RelativeLayout) findViewById(R.id.ll_userinfo);
        idTextView = (TextView) findViewById(R.id.me_userinfo_id_tv);
    }

    private void initWealthView() {
        float screenWidth = ScreenUtil.getScreenWidth(getContext());
//        float coinDiamondWidth = (screenWidth - ScreenUtil.dp2px(getContext(), 1 + 30 + 30)) / 2;//减去"金币"和"钻石"中间1dp的竖线和左右边的30dp
        mWealthContainer = findViewById(R.id.me_wealth_container);
        coinTextView = (TextView) findViewById(R.id.me_userinfo_coin_tv);
//        coinTextView.getLayoutParams().width = (int) coinDiamondWidth;
        findViewById(R.id.me_userinfo_coin_tv_useless).setOnClickListener(this);
        diamondTextView = (TextView) findViewById(R.id.me_userinfo_diamond_tv);
//        diamondTextView.getLayoutParams().width = (int) coinDiamondWidth;
        findViewById(R.id.me_userinfo_diamond_tv_useless).setOnClickListener(this);
        /*会员等级
        * int level = UserConstant.getMyVipLevel();
        tv_vip.setText(UserConstant.getVipLevelShowRes(level));
        if(level  == VipConstants.VIP_LEVEL_BALCK){
            iv_vip.setImageResource(R.mipmap.icon_vip_black);
        } else if(level == VipConstants.VIP_LEVEL_WHITE){
            iv_vip.setImageResource(R.mipmap.icon_vip_white);
        } else {
            iv_vip.setImageResource(R.mipmap.icon_vip_not);
        }
        * */
    }

    public void updateUserInfo(final NimUserInfo userInfo) {
        //设置id
        String userId = UserPreferences.getInstance(getContext()).getZYId();
        if (TextUtils.isEmpty(userId) || !userInfo.getAccount().equals(DemoCache.getAccount())) {//只有自己的头像显示站鱼id即uuid
            idTextView.setVisibility(GONE);
        } else {
            idTextView.setVisibility(VISIBLE);
            idTextView.setText("ID: " + userId);
        }
        //设置nickname
        tv_nickname.setText(NimUserInfoCache.getInstance().getUserDisplayName(userInfo.getAccount()));
        String  alias= NimUserInfoCache.getInstance().getAlias(userInfo.getAccount());
        String signature = userInfo.getSignature() == null ? "" : userInfo.getSignature().trim();
        if(TextUtils.isEmpty(signature) || TextUtils.isEmpty(signature)) {
            tv_signature.setText(getContext().getString(R.string.userinfo_signature_null));
        }else{
            tv_signature.setText(signature);
        }
        String areaShow = "";
        String extension = userInfo.getExtension();
        LogUtil.i(TAG, "extension :" + extension);
        int areaId = UserConstant.getUserExtAreaId(extension);
//        areaId = 1;//china
        if(areaId != 0) {
            areaShow =  RegionDBTools.getShowRegionContent(Integer.valueOf(areaId), " | ");
        }
        if(!TextUtils.isEmpty(alias) && !TextUtils.isEmpty(areaShow)){
            tv_extension.setVisibility(VISIBLE);
            tv_extension.setText(areaShow + "  " + getContext().getString(R.string.user_nickname , userInfo.getName()));
        } else if(!TextUtils.isEmpty(alias) && TextUtils.isEmpty(areaShow)){
            tv_extension.setVisibility(VISIBLE);
            tv_extension.setText(getContext().getString(R.string.user_nickname , userInfo.getName()));
        } else if(TextUtils.isEmpty(alias) && !TextUtils.isEmpty(areaShow)){
            tv_extension.setVisibility(VISIBLE);
            tv_extension.setText(areaShow);
        } else{
            tv_extension.setVisibility(GONE);
        }
        if (userInfo.getGenderEnum() == GenderEnum.MALE) {
            iv_sex.setImageResource(R.mipmap.icon_me_male);
            iv_userhead.setImageResource(R.mipmap.default_male_head);
            iv_userhead.loadBuddyAvatarByMeFrg(userInfo.getAccount(), DEFAULT_AVATAR_THUMB_SIZE, R.mipmap.default_male_head);//设置头像
        } else if (userInfo.getGenderEnum() == GenderEnum.FEMALE) {
            iv_sex.setImageResource(R.mipmap.icon_me_female);
            iv_userhead.setImageResource(R.mipmap.default_female_head);
            iv_userhead.loadBuddyAvatarByMeFrg(userInfo.getAccount(), DEFAULT_AVATAR_THUMB_SIZE, R.mipmap.default_female_head);//设置头像
        } else {
            iv_sex.setImageResource(R.mipmap.icon_me_male);
            iv_userhead.setImageResource(R.mipmap.default_male_head);
            iv_userhead.loadBuddyAvatarByMeFrg(userInfo.getAccount(), DEFAULT_AVATAR_THUMB_SIZE, R.mipmap.default_male_head);//设置头像
        }
        if(!DemoCache.getAccount().equals(userInfo.getAccount())) {
            mWealthContainer.setVisibility(GONE);
            ll_userinfo.setOnClickListener(null);
            iv_userhead.setOnClickListener(new OnClickListener() {//不是自己的话点击查看大图
                @Override
                public void onClick(View v) {
                    Bundle data = new Bundle();
                    data.putSerializable(UrlConstants.WATCH_PIC_AC_USER_INFO, userInfo);
                    Nav.from(getContext()).withExtras(data).toUri(UrlConstants.WATCH_PIC_AC);
                }
            });
        } else {
            mWealthContainer.setVisibility(VISIBLE);
            updataWealthView();
            ll_userinfo.setOnClickListener(this);
            iv_userhead.setOnClickListener(this);
        }
    }

    public void updataWealthView() {
        coinTextView.setText(" " + WealthHelper.getWealthShow(UserPreferences.getInstance(getContext()).getCoins()));
        diamondTextView.setText(" " + WealthHelper.getWealthShow(UserPreferences.getInstance(getContext()).getDiamond()));
    }

    public void onDestroy() {
    }

    OnUserFuntionClick mOnUserFuntionClick;

    public void setOnUserFuntionClick(OnUserFuntionClick mOnUserFuntionClick){
        this.mOnUserFuntionClick = mOnUserFuntionClick;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_userhead:
                pickImage(EditUserInfoActivity.PICK_AVATAR_REQUEST);
            case R.id.ll_userinfo:
                break;
            case R.id.me_userinfo_coin_tv_useless:
                if (mOnUserFuntionClick != null) {
                    mOnUserFuntionClick.onGoShop(ShopActivity.TYPE_SHOP_COIN);
                }
                break;
            case R.id.me_userinfo_diamond_tv_useless:
                if (mOnUserFuntionClick != null) {
                    mOnUserFuntionClick.onGoShop(ShopActivity.TYPE_SHOP_DIAMOND);
                }
                break;
        }
    }

    public interface OnUserFuntionClick{
        public void onEditInfo();
        public void onGoShop(int shopType);
    }

    /**
     * 选取图片
     */
    public void pickImage(int requestCode) {//参考EditUserInfoActivity代码一样的
        PickImageHelper.PickImageOption option = new PickImageHelper.PickImageOption();
        option.titleResId = R.string.set_head_image;
        option.crop = true;
        option.multiSelect = false;
        option.cropOutputImageWidth = 360;
        option.cropOutputImageHeight = 360;
//        option.cropOutputImageWidth = 720;
//        option.cropOutputImageHeight = 720;
        PickImageHelper.pickImage(getContext(), requestCode, option);
    }
}
