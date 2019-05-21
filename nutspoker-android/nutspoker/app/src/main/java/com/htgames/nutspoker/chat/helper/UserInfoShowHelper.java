package com.htgames.nutspoker.chat.helper;

import android.text.TextUtils;

import com.netease.nim.uikit.bean.UserEntity;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.util.string.StringUtil;

/**
 * 用户数据显示
 */
public class UserInfoShowHelper {

    /**
     * 获取展示的用户昵称
     * @param userEntity
     * @return
     */
    public static String getUserNickname(UserEntity userEntity) {
        if (userEntity == null) {
            return "";
        }
        String nicknam = userEntity.name;
        if (!StringUtil.isSpace(nicknam)) {
            return nicknam;
        }
        if (userEntity != null) {
            if(!TextUtils.isEmpty(userEntity.account)) {
                if (NimUserInfoCache.getInstance().hasUser(userEntity.account)) {
                    nicknam = NimUserInfoCache.getInstance().getUserDisplayName(userEntity.account);
                } else {
                    NimUserInfoCache.getInstance().getUserInfoFromRemote(userEntity.account, null);
                    nicknam = userEntity.name;
                }
            } else{
                nicknam = userEntity.name;
            }
        }
        return nicknam;
    }

    /**
     * 获取展示的用户头像
     * @param userEntity
     * @return
     */
    public static String getUserAvatar(UserEntity userEntity){
        String avatar = "";
        if(userEntity != null){
            if(!TextUtils.isEmpty(userEntity.account) && NimUserInfoCache.getInstance().hasUser(userEntity.account)){
                avatar = NimUserInfoCache.getInstance().getUserInfo(userEntity.account).getAvatar();
            }
            if(TextUtils.isEmpty(avatar) && !TextUtils.isEmpty(userEntity.avatar)){
                avatar = userEntity.avatar;
            }
        }
        return avatar;
    }

    public static String getUserNickname(String uid , String name) {
        String nicknam = "";
        if (!TextUtils.isEmpty(uid) && NimUserInfoCache.getInstance().hasUser(uid)) {
            nicknam = NimUserInfoCache.getInstance().getUserDisplayName(uid);
        } else {
            nicknam = name;
        }
        return nicknam;
    }
}
