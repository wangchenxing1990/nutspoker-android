package com.netease.nim.uikit.chesscircle;

import android.text.TextUtils;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.club.constant.ClubType;
import com.netease.nim.uikit.chesscircle.entity.TeamEntity;
import com.netease.nimlib.sdk.team.model.Team;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 20150726 on 2016/2/2.
 */
public class ClubConstant {
    public static int NICKNAME_CONSUME = 3000;//修改昵称消耗钻石
    public static final int KEY_NAME= 1;
    public static final int KEY_INTRODUCE = 2;

    public final static int MAX_HORDE_NAME_LENGTH = 20;//最大字符长度：圈子名称
    public final static int MAX_GROUP_NAME_LENGTH = 20;//最大字符长度：圈子名称
    public final static int MAX_CLUB_NAME_LENGTH = 20;//最大字符长度：俱乐部名称
    public final static int MAX_CLUB_INTRODUCE_LENGTH = 60;//最大字符长度：俱乐部介绍
    public final static String GROUP_IOS_DEFAULT_NAME = " ";//圈子默认名称名称，介绍，由于云信那边是不支持空

    public static final int TYPE_CLUB_EDIT_NOT_SHOW= 0;//俱乐部修改类型：默认不显示
    public static final int TYPE_CLUB_EDIT_AREA= 1;//俱乐部修改类型：区域
    public static final int TYPE_CLUB_EDIT_AVATAR = 2;//俱乐部修改类型：头像
    public static final int TYPE_CLUB_TEAM_UPGRADE = 3;//俱乐部修改成员上限
    public static final int TYPE_CLUB_FUND_UPGRADE = 4;//俱乐部修改基金

    private final static int GROUP_MEMBER_LIMIT = 50;//圈子人员上限
    public final static int CLUB_MEMBER_LIMIT = 2000;//   需求改版100改为1000 20161221  100;//俱乐部人员上限   新需求由1000改为2000 20170912

    public static final String KEY_EXT_CLUB_FUND = "fund";//拓展字段参数：修改类型
    public static final String KEY_EXT_UPDATE_TYPE = "update_type";//拓展字段参数：修改类型
    public static final String KEY_EXT_AREA_ID = "area_id";//拓展字段参数：区域ID
    public static final String KEY_EXT_AVATAR = "avatar";//拓展字段参数：区域头像
    public static final String KEY_EXT_COUNT = "count";//拓展字段参数：俱乐部人数上限数量
    public static final String KEY_EXT_LIMIT_END_DATE = "end_date";//拓展字段参数：俱乐部人数上限到期时间
    public static final String KEY_EXT_CREATE_GAME_BY_CREATOR_LIMIT = "is_owner";//拓展字段参数：只能房主开局
    public static final String KEY_EXT_IS_PRIVATE = "is_private";//拓展字段参数：是否是私有的（不可以被搜索）
    public static final String KEY_EXT_CLUB_TYPE = "club_type";//拓展字段参数：俱乐部类型 ： 0： 普通俱乐部 1 ：超级俱乐部
    public static final String KEY_LAST_NAME_MODIFY_TIME = "modify_time";//下次可以修改俱乐部名称的时间

    public static int getClubUpdateType(String ext){
        int editType = 0;
        if(TextUtils.isEmpty(ext)){
            return editType;
        }
        try {
            editType = new JSONObject(ext).optInt(KEY_EXT_UPDATE_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return editType;
    }

    public static int getClubExtAreaId(String ext){
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

    public static String getClubExtAvatar(String ext){
        String clubAvatar = "";
        if(TextUtils.isEmpty(ext)){
            return clubAvatar;
        }
        try {
            clubAvatar = new JSONObject(ext).optString(KEY_EXT_AVATAR);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return clubAvatar;
    }

    //获取俱乐部上限结束时间
    public static long getClubMemberLimitEndDate(String ext){
        long endDate = 0;
        if(!TextUtils.isEmpty(ext)) {
            try {
                endDate = new JSONObject(ext).optLong(KEY_EXT_LIMIT_END_DATE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return endDate;
    }

    public static long getClubNameLastModifyTime(String ext) {
        long modifyTime = 0;
        if(!TextUtils.isEmpty(ext)) {
            try {
                modifyTime = new JSONObject(ext).optLong(KEY_LAST_NAME_MODIFY_TIME);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return modifyTime;
    }

    public static int getGroupMemberLimit(){
        return GROUP_MEMBER_LIMIT;
    }

    public static int getClubMemberLimit(Team team){
        int memberLimitCount = CLUB_MEMBER_LIMIT;
        if(team != null && !TextUtils.isEmpty(team.getExtServer())) {
            try {
                memberLimitCount = new JSONObject(team.getExtServer()).optInt(KEY_EXT_COUNT);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(memberLimitCount <= CLUB_MEMBER_LIMIT){
            memberLimitCount = CLUB_MEMBER_LIMIT;
        }
        return memberLimitCount;
    }

//    //俱乐部成员上限是否有效
//    public static boolean isClubMemberLimitAvlid(Team team){
//        if(team != null){
//            if(System.currentTimeMillis() / 1000L < getClubMemberLimitEndDate(team.getExtServer())){
//                return true;
//            }
//        }
//        return false;
//    }

    //俱乐部成员上限是否有效
    public static int getClubMemberLimitRemainDay(Team team){
        int day = 0;
        if(team != null && !TextUtils.isEmpty(team.getExtServer())) {
            long currentTime = System.currentTimeMillis() / 1000L;
            long avlidTime = getClubMemberLimitEndDate(team.getExtServer());
            long remainTime = avlidTime - currentTime;
            day = (int) (remainTime / 60f / 60f / 24f);
        }
        return day;
    }

    /**
     * 是否俱乐部成员已满
     * @param teamId
     * @return
     */
    public static boolean isClubMemberIsFull(String teamId){
        Team team = TeamDataCache.getInstance().getTeamById(teamId);
        if(team == null){
            return true;
        }
        if(team.getMemberCount() < getClubMemberLimit(team)){
            return false;
        }
        return true;
    }

    /**
     * 是否俱乐部成员已满
     * @return
     */
    public static boolean isClubMemberIsFull(Team team){
        if(team == null){
            return true;
        }
        if(team.getMemberCount() < getClubMemberLimit(team)){
            return false;
        }
        return true;
    }

    public static int getClubMemberLimit(TeamEntity team) {
        int memberLimitCount = CLUB_MEMBER_LIMIT;
        if (team != null && !TextUtils.isEmpty(team.extension)) {
            try {
                memberLimitCount = new JSONObject(team.extension).optInt(KEY_EXT_COUNT);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (memberLimitCount <= CLUB_MEMBER_LIMIT) {
            memberLimitCount = CLUB_MEMBER_LIMIT;
        }
        return memberLimitCount;
    }

    /**
     * 是否只能房主开局
     * @param team
     * @return
     */
    public static boolean isClubCreateGameByCreatorLimit(Team team) {
        boolean limit = false;
        if (team != null && !TextUtils.isEmpty(team.getExtServer())) {
            try {
                int creator = new JSONObject(team.getExtServer()).optInt(KEY_EXT_CREATE_GAME_BY_CREATOR_LIMIT);
                if (creator == 1) {
                    limit = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return limit;
    }

    /**
     * 是否是私有的（不可以被搜索到）
     * @param team
     * @return
     */
    public static boolean isClubPrivate(Team team) {
        boolean isPrivate = false;
        if (team != null && !TextUtils.isEmpty(team.getExtServer())) {
            try {
                int result = new JSONObject(team.getExtServer()).optInt(KEY_EXT_IS_PRIVATE);
                if (result == 1) {
                    isPrivate = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return isPrivate;
    }

    /**
     * 是否显示开局按钮
     * @param team
     * @return
     */
    public static boolean isShowGameCreateBtn(Team team) {
        if (isClubCreateGameByCreatorLimit(team)) {
            if (team.getCreator().equals(NimUIKit.getAccount())) {
                //开启了只能会长开局，并且自己就是会长
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public static String getClubFund(Team team) {
        String fundNum = "0";
        if (team != null && !TextUtils.isEmpty(team.getExtServer())) {
            try {
                fundNum = new JSONObject(team.getExtServer()).optInt(KEY_EXT_CLUB_FUND) + "";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return fundNum;
    }

    /**
     * 获取俱乐部虚拟ID
     * @param teamId
     * @param extServer
     * @return
     */
    public static String getClubVirtualId(String teamId , String extServer) {
        //俱乐部虚拟ID
        String virtualId = "";
        if (TextUtils.isEmpty(extServer)) {
            return teamId;
        } else {
            try {
                virtualId = (new JSONObject(extServer)).optString("vid", "0");
                if (TextUtils.isEmpty(virtualId) || "0".equals(virtualId)) {
                    virtualId = teamId;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return virtualId;
        }
    }

    /**
     * 是否是超级俱乐部
     * @param team
     * @return
     */
    public static boolean isSuperClub(Team team) {
        boolean isSuper = false;
        if (team != null && !TextUtils.isEmpty(team.getExtServer())) {
            try {
                int clubType = new JSONObject(team.getExtServer()).optInt(KEY_EXT_CLUB_TYPE);
                if (clubType == ClubType.Super) {
                    isSuper = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return isSuper;
    }

    public static boolean isSuperClub(TeamEntity team) {
        boolean isSuper = false;
        if (team != null && !TextUtils.isEmpty(team.extension)) {
            try {
                int clubType = new JSONObject(team.extension).optInt(KEY_EXT_CLUB_TYPE);
                if (clubType == ClubType.Super) {
                    isSuper = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return isSuper;
    }
    //下面是传递intent数据使用的key
    public static final String KEY_TOTAL_FUND = "key_total_fund";
    public static final int FUND_TYPE_PURCHASE = 0;//充值
    public static final int FUND_TYPE_GRANT = 1;//发放
    //俱乐部成员信用分相关
    public static final String KEY_CREDIT_IN_EXTENSIONS = "credit";
}
