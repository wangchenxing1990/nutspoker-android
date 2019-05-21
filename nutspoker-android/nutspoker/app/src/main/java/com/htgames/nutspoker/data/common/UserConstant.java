package com.htgames.nutspoker.data.common;

import android.text.TextUtils;
import com.netease.nim.uikit.common.util.log.LogUtil;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.common.DateTools;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.constants.VipConstants;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 */
public class UserConstant {
    private final static String TAG = "UserConstant";
    public static int NICKNAME_CONSUME = 300;//第二次之后修改昵称消耗钻石
    public static int HORDE_NAME_CONSUME = 3000;//修改部落昵称消耗钻石
    public static final int KEY_NICKNAME= 1;
    public static final int KEY_GENDER= 2;
    public static final int KEY_SIGNATURE = 3;
    public static final int KEY_ALIAS = 10;

    public static final int KEY_EDIT_HORDE_NAME = 11;//修改部落名称

    public final static int MAX_PHONE_NUM_LENGTH = 11;//最大字符长度：手机号
    public final static int MAX_NICKNAME_LENGTH = 10;//最大字符长度：昵称
    public final static int MAX_HORDE_NAME_LENGTH = 20;//最大字符长度：昵称
    public final static int MAX_ALIAS_LENGTH = 10;//最大字符长度：备注
    public final static int MAX_SIGNATURE_LENGTH = 60;//最大字符长度：个性签名

    public static final String KEY_EXT_AREA_ID = "area_id";//拓展字段参数：区域ID
    public static final String KEY_EXT_VIP_LEVEL = "level";//拓展字段参数：VIP等级
    public static final String KEY_EXT_VIP_EXPIRE_DATA = "expire_date";//拓展字段参数：VIP过期时间

    /**
     * 获取用户所在区域
     * @param ext
     * @return
     */
    public static int getUserExtAreaId(String ext){
        int currentAreaId = 0;
        if(TextUtils.isEmpty(ext)){
            return currentAreaId;
        }
        try {
            currentAreaId = new JSONObject(ext).optInt(KEY_EXT_AREA_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return currentAreaId;
    }

    /**
     * 获取用户VIP等级
     * @param ext
     * @return
     */
    public static int getUserVipLevel(String ext) {
        int vipLevel = VipConstants.VIP_LEVEL_NOT;
        if (TextUtils.isEmpty(ext)) {
            LogUtil.i(TAG, "level:" + vipLevel);
            return vipLevel;
        }
        //先判断是否超时
        long serviceTime = DemoCache.getCurrentServerSecondTime();
        if (serviceTime > getUserVipExpireData(ext)) {
            LogUtil.i(TAG, "level:" + vipLevel);
            return vipLevel;
        }
        try {
            vipLevel = new JSONObject(ext).optInt(KEY_EXT_VIP_LEVEL);
            LogUtil.i(TAG, "level:" + vipLevel);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return vipLevel;
    }

    /**
     * 获取用户VIP失效时间
     * @param ext
     * @return
     */
    public static long getUserVipExpireData(String ext) {
        long vipExpireData = 0;
        if (TextUtils.isEmpty(ext)) {
            return vipExpireData;
        }
        try {
            vipExpireData = new JSONObject(ext).optInt(KEY_EXT_VIP_EXPIRE_DATA);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return vipExpireData;
    }

    public static int getMyVipLevel(){
        int level = VipConstants.VIP_LEVEL_NOT;
        NimUserInfo myUserinfo = NimUserInfoCache.getInstance().getUserInfo(DemoCache.getAccount());
        if(myUserinfo != null){
            level = getUserVipLevel(myUserinfo.getExtension());
        }
        return level;
    }

    /**
     * 获取我创建的俱乐部上限
     * @return
     */
    public static int getMyCreateClubLimit(){
        int clubLimit = VipConstants.LEVEL_NOT_CLUBCOUNT;
        NimUserInfo myUserinfo = NimUserInfoCache.getInstance().getUserInfo(DemoCache.getAccount());
        if(myUserinfo != null){
            int level = getUserVipLevel(myUserinfo.getExtension());
            clubLimit = VipConstants.getClubLimitByLevel(level);
            LogUtil.i(TAG , "level :" + level);
            LogUtil.i(TAG , "clubLimit :" + clubLimit);
        }
        return clubLimit;
    }

    /**
     * 获取我收藏的牌局上限
     * @return
     */
    public static int getMyCollectHandLimit(){
        int collectHandNum = VipConstants.LEVEL_NOT_COLLECT_HANDCOUNT;
        NimUserInfo myUserinfo = NimUserInfoCache.getInstance().getUserInfo(DemoCache.getAccount());
        if(myUserinfo != null){
            int level = getUserVipLevel(myUserinfo.getExtension());
            collectHandNum = VipConstants.getHandCollectLimitByLevel(level);
        }
        return collectHandNum;
    }

    public static int getVipLevelShowRes(int level){
        if(level == VipConstants.VIP_LEVEL_BALCK){
            return R.string.member_vip_black;
        } else if(level == VipConstants.VIP_LEVEL_WHITE){
            return R.string.member_vip_white;
        } else{
            return R.string.member_normal;
        }
    }

    public static int getMyVipLevelShowRes(){
        int level = getMyVipLevel();
        if(level == VipConstants.VIP_LEVEL_BALCK){
            return R.string.member_vip_black;
        } else if(level == VipConstants.VIP_LEVEL_WHITE){
            return R.string.member_vip_white;
        } else{
            return R.string.member_normal;
        }
    }

    //是否达到收藏上限
    public static boolean isHandCollectLimit() {
        int limitNum = getMyCollectHandLimit();
        int myCollectNum = UserPreferences.getInstance(DemoCache.getContext()).getCollectHandNum();
        if (myCollectNum < limitNum) {
            return false;
        }
        return true;
    }
}
