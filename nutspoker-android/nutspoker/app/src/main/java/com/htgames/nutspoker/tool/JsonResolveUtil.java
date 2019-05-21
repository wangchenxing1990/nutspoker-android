package com.htgames.nutspoker.tool;

import android.text.TextUtils;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.circle.bean.CircleItem;
import com.htgames.nutspoker.circle.bean.CommentItem;
import com.htgames.nutspoker.data.common.CircleConstant;
import com.htgames.nutspoker.data.common.ContactConstants;
import com.htgames.nutspoker.game.match.activity.AddChannelAC;
import com.htgames.nutspoker.game.match.bean.MatchPlayerEntity;
import com.htgames.nutspoker.tool.json.RecordJsonTools;
import com.netease.nim.uikit.bean.*;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.chesscircle.DealerConstant;
import com.netease.nim.uikit.chesscircle.entity.TeamEntity;
import com.netease.nim.uikit.common.util.log.LogUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 */
public class JsonResolveUtil {

    /**
     * 获取好友游戏在线状态
     * @param result
     * @return
     */
    public final static ArrayList<FriendStatusEntity> getFriendStatusList(String result){
        ArrayList<FriendStatusEntity> friendStatusList = new ArrayList<FriendStatusEntity>();
        try {
            JSONObject response = new JSONObject(result);
            JSONArray array = response.getJSONArray("data");
            int size = array.length();
            for(int i = 0 ; i < size ; i++){
                JSONObject gameJson = array.getJSONObject(i);
                FriendStatusEntity friendStatusEntity = new FriendStatusEntity();
                friendStatusEntity.username = (gameJson.getString("username"));
                friendStatusEntity.gid = (gameJson.getString("gid"));
                friendStatusList.add(friendStatusEntity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return friendStatusList;
    }

    /**
     * 获取搜索结果
     */
    public final static ArrayList<String> getSearchUserList(String result){
        ArrayList<String> userList = new ArrayList<String>();
        try {
            JSONObject response = new JSONObject(result);
            JSONArray array = response.getJSONArray("data");
            int size = array.length();
            for(int i = 0 ; i < size ; i++){
//                JSONObject gameJson = array.getJSONObject(i);
                userList.add(array.getString(i));
            }
            HashSet<String> set = new HashSet<String>(userList);
            //Constructing listWithoutDuplicateElements using set，过滤掉重复的元素
            userList = new ArrayList<String>(set);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userList;
    }

    /**
     * 获取俱乐部列表
     */
    public final static ArrayList<TeamEntity> getTeamList(String result){
        ArrayList<TeamEntity> teamList = new ArrayList<TeamEntity>();
        try {
            JSONObject response = new JSONObject(result);
            if(response.isNull("data") || TextUtils.isEmpty(response.optString("data"))) {
                return teamList;
            }
            JSONArray array = response.getJSONArray("data");
            int size = array.length();
            for(int i = 0 ; i < size ; i++){
                JSONObject itemJson = array.getJSONObject(i);
                TeamEntity teamEntity = new TeamEntity();
                teamEntity.announcement = (itemJson.optString("announcement"));
                teamEntity.creator = (itemJson.optString("owner"));
                teamEntity.name = (itemJson.optString("tname"));
                teamEntity.id = (itemJson.optString("tid"));
                teamEntity.introduce = (itemJson.optString("intro"));
                String extension = itemJson.optString("custom");
                if(!TextUtils.isEmpty(extension)){
                    extension.replace("\\" , "");
                }
                teamEntity.extension = (extension);
                teamEntity.memberLimit = (itemJson.optInt("maxusers"));
                teamEntity.verifyType = (itemJson.optInt("joinmode"));
                teamEntity.memberCount = (itemJson.optInt("size"));
                teamList.add(teamEntity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return teamList;
    }

    /**
     * 解析数据分析
     * @return
     */
    public final static DataAnalysisEntity getDataAnalysisEntity(String result){
        DataAnalysisEntity mDataAnalysisEntity = null;
        try {
            JSONObject response = new JSONObject(result);
            JSONObject data = response.getJSONObject("data");
            mDataAnalysisEntity = new DataAnalysisEntity();
            mDataAnalysisEntity.wsd = (data.optInt("wsd"));
            mDataAnalysisEntity.wtsd = (data.optInt("wtsd"));
            mDataAnalysisEntity.wwsf = (data.optInt("wwsf"));
            mDataAnalysisEntity.af = ((float)data.optDouble("af"));
            mDataAnalysisEntity.afq = (data.optInt("afq"));
            mDataAnalysisEntity.vpip = (data.optInt("vpip"));
            mDataAnalysisEntity.pfr = (data.optInt("pfr"));
            mDataAnalysisEntity.pfr_vpip = (data.optInt("pfr_vpip"));
            mDataAnalysisEntity.three_bet = (data.optInt("three_bet"));
            mDataAnalysisEntity.att_to_stl_lp = (data.optInt("att_to_stl_lp"));
            mDataAnalysisEntity.fold_blind_to_stl = (data.optInt("fold_blind_to_stl"));
            mDataAnalysisEntity.sb_three_bet_steal_att = ((float)data.optDouble("sb_three_bet_steal_att"));
            mDataAnalysisEntity.bb_three_bet_steal_att = ((float)data.optDouble("bb_three_bet_steal_att"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mDataAnalysisEntity;
    }

    public final static ArrayList<GameMgrBean> getChannelList(String gid, String result) {
        ArrayList<GameMgrBean> channelList = new ArrayList<GameMgrBean>();
        ArrayList<UserEntity> allManagers = new ArrayList<UserEntity>();
        try {
            JSONObject response = new JSONObject(result);
            if(response.isNull("data") || TextUtils.isEmpty(response.optString("data"))) {
                return channelList;
            }
            JSONArray array = response.getJSONArray("data");
            int size = array.length();
            for (int i = 0; i < size; i++) {
                JSONObject channel = (JSONObject) array.get(i);
                GameMgrBean bean = new GameMgrBean();
                bean.mgrList = new ArrayList();
                bean.channel = channel.optString("channel");
                bean.block = channel.optInt("block");
                if (bean.channel.length() > 6) {
                    bean.tid = bean.channel.substring(6, bean.channel.length());
                }
                JSONArray usersArray = channel.getJSONArray("users");
                for (int j = 0; j < usersArray.length(); j++) {
                    UserEntity userEntity = new UserEntity();
                    JSONObject inner = usersArray.getJSONObject(j);
                    userEntity.account = inner.optString("uid");
                    userEntity.uuid = inner.optString("uuid");
                    userEntity.nickname = inner.optString("nickname");
                    userEntity.channel = bean.channel;
                    bean.mgrList.add(userEntity);
                }
                channelList.add(bean);
                allManagers.addAll(bean.mgrList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (ChessApp.gameManagers.get(gid) != null) {
            ChessApp.gameManagers.get(gid).clear();
        }
        if (ChessApp.gameChannels.get(gid) != null) {
            ChessApp.gameChannels.get(gid).clear();
        }
        ChessApp.gameChannels.put(gid, channelList);
        ChessApp.gameManagers.put(gid, allManagers);
        return channelList;
    }

    /**
     * 解析获取mtt比赛某个渠道channel下的玩家列表
     * @param data
     * @return
     */
    public static ArrayList<MatchPlayerEntity> getUserByMgr(JSONObject data, final NimUserInfoCache.IFetchCallback callback) {
        final ArrayList<MatchPlayerEntity> playerList = new ArrayList<MatchPlayerEntity>();
        List<String> accounts = new ArrayList<>();
        try {
            JSONArray users = data.getJSONArray("users");
            for (int i = 0; i < users.length(); i++) {
                JSONObject bean = users.getJSONObject(i);
                String uid = bean.optString("uid");
                int rebuy_cnt = bean.optInt("rebuy_cnt");
                MatchPlayerEntity playerEntity = new MatchPlayerEntity();
                playerEntity.rebuyCnt = rebuy_cnt;
                playerEntity.uuid = bean.optString("uuid");
                if ("0".equals(playerEntity.uuid)) {
                    playerEntity.uuid = "";
                }
                playerEntity.uid = uid;
                playerEntity.nickname = NimUserInfoCache.getInstance().getUserDisplayName(uid);
                playerEntity.rank = bean.optInt("ranking");
                playerEntity.chips = bean.optInt("reward");//mtt的记分牌  和普通局的reward解析同一个json字段

                DecimalFormat df = new DecimalFormat("#.##");
                playerEntity.ko_head_cnt = df.format(bean.optInt("ko_head_cnt") / 100f);

                playerEntity.ko_worth = bean.optInt("ko_worth");
                playerEntity.ko_head_reward = bean.optInt("ko_head_reward");
                playerEntity.total_buy = bean.optInt("total_buy");//自由局总买入
                playerEntity.reward = bean.optInt("reward");//自由局总盈利
                playerEntity.opt_user = bean.optString("opt_user");
                playerEntity.opt_user_type = bean.optInt("opt_user_type");
                ////下面的是自由局的两个字段
                playerList.add(playerEntity);
                accounts.add(uid);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NimUserInfoCache.getInstance().getUserListByNeteaseEx(accounts, 0, new NimUserInfoCache.IFetchCallback() {
            @Override
            public void onFetchFinish(List list) {
                for (int i = 0; i < playerList.size(); i++) {
                    MatchPlayerEntity playerEntity = playerList.get(i);
                    playerEntity.nickname = NimUserInfoCache.getInstance().getUserDisplayName(playerEntity.uid);
                }
                if (callback != null) {
                    callback.onFetchFinish(playerList);
                }
            }
        });
        return playerList;
    }

    /**
     * 解析获取mtt比赛某个渠道channel下的玩家列表(游戏已经结束，在战绩里面用到)
     * @param data
     * @return
     */
    public static ArrayList<GameMemberEntity> getUserByMgrOff(JSONObject data, final NimUserInfoCache.IFetchCallback callback) {
        final ArrayList<GameMemberEntity> memberList = new ArrayList<GameMemberEntity>();
        List<String> accounts = new ArrayList<>();
        try {
            JSONArray users = data.getJSONArray("users");
            for (int i = 0; i < users.length(); i++) {
                JSONObject bean = users.getJSONObject(i);
                String uid = bean.optString("uid");
                int rebuy_cnt = bean.optInt("rebuy_cnt");
                int ranking = bean.optInt("ranking");
                int reward = bean.optInt("reward");
                int ko_head_cnt = bean.optInt("ko_head_cnt");///MatchConstants.KO_HEAD_CNT;
                int ko_head_reward = bean.optInt("ko_head_reward");//猎人赛人头奖金   MatchConstants.KO_HEAD_REWARD;
                GameMemberEntity entity = new GameMemberEntity();
                UserEntity userInfo = new UserEntity();
                userInfo.uuid = bean.optString("uuid");
                if ("0".equals(userInfo.uuid)) {
                    userInfo.uuid = "";
                }
                userInfo.account = uid;
                userInfo.name = NimUserInfoCache.getInstance().getUserDisplayName(uid);
                entity.userInfo = userInfo;
                entity.rebuyCnt = rebuy_cnt;
                entity.ranking = ranking;
                entity.reward = reward;

                DecimalFormat df = new DecimalFormat("#.##");
                entity.ko_head_cnt = df.format(ko_head_cnt / 100f);

                entity.ko_head_reward = ko_head_reward;
                entity.totalBuy = bean.optInt("total_buy");//自由局总买入
                entity.winChip = entity.reward;//userbychanneloff接口普通局的盈利就是reward
                entity.insurance = (bean.optInt("insurance"));
                entity.opt_user = bean.optString("opt_user");
                entity.opt_user_type = bean.optInt("opt_user_type");
                //下面是测试的
//                entity.totalBuy = i + 1;
//                entity.premium = i + 1;
//                entity.insurance = i + 1;
//                entity.joinCnt = i + 1;
//                entity.enterPotCnt = i + 1;
//                entity.winCnt = i + 1;
//                entity.addonCnt = i + 1;
//                entity.blindsIndex = i + 1;
                memberList.add(entity);
                accounts.add(uid);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NimUserInfoCache.getInstance().getUserListByNeteaseEx(accounts, 0, new NimUserInfoCache.IFetchCallback() {
            @Override
            public void onFetchFinish(List list) {
                for (int i = 0; i < memberList.size(); i++) {
                    GameMemberEntity playerEntity = memberList.get(i);
                    playerEntity.userInfo.name = NimUserInfoCache.getInstance().getUserDisplayName(playerEntity.userInfo.account);
                }
                if (callback != null) {
                    callback.onFetchFinish(memberList);
                }
            }
        });
        return memberList;
    }

    /**
     * 获取高级搜索用户结果
     */
    public final static ArrayList<SearchUserBean> getAdvSearchUserList(String result) {
        ArrayList<SearchUserBean> userList = new ArrayList<SearchUserBean>();
        try {
            JSONObject response = new JSONObject(result);
            if(response.isNull("data") || TextUtils.isEmpty(response.optString("data"))) {
                return userList;
            }
            JSONObject data = response.getJSONObject("data");
            if(!data.has("user") || data.isNull("user")){
                return userList;
            }
            JSONArray array = data.getJSONArray("user");
            int size = array.length();
            for (int i = 0; i < size; i++) {
                JSONObject inner = array.getJSONObject(i);
                String uid = inner.optString("id");
                String uuid = inner.optString("uuid");
                if(TextUtils.isEmpty(uid) || DealerConstant.isDealer(uid)){
                    continue;
                }
                SearchUserBean bean = new SearchUserBean();
                bean.id = uid;
                bean.uuid = uuid;
                bean.channelType = AddChannelAC.Companion.getCHANNEL_TYPE_PERSONAL();
                userList.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userList;
    }

    public static ArrayList<SearchUserBean> getSearchClubChannelList(JSONObject response) {
        ArrayList<SearchUserBean> userList = new ArrayList<SearchUserBean>();
        if(response == null || response.isNull("data") || TextUtils.isEmpty(response.optString("data"))) {
            return userList;
        }
        try {
            JSONArray data = response.optJSONArray("data");
            int size = data.length();
            for (int i = 0; i < size; i++) {
                JSONObject inner = data.getJSONObject(i);
                String uid = inner.optString("id");
                String uuid = inner.optString("uuid");
                String tid = inner.optString("tid");
                String vid = inner.optString("vid");
                SearchUserBean bean = new SearchUserBean();
                bean.id = uid;
                bean.uuid = uuid;
                bean.tid = tid;
                bean.vid = vid;
                bean.channelType = AddChannelAC.Companion.getCHANNEL_TYPE_CLUB();
                userList.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userList;
    }

    /**
     * 获取高级搜索群结果
     */
    public final static ArrayList<TeamEntity> getAdvSearchTeamList(String result){
        ArrayList<TeamEntity> teamList = new ArrayList<TeamEntity>();
        try {
            JSONObject response = new JSONObject(result);
            if(response.isNull("data") || TextUtils.isEmpty(response.optString("data"))) {
                return teamList;
            }
            JSONObject data = response.getJSONObject("data");
            if(!data.has("team") || data.isNull("team")){
                return teamList;
            }
            JSONArray array = data.getJSONArray("team");
            int size = array.length();
            for(int i = 0 ; i < size ; i++){
                JSONObject itemJson = array.getJSONObject(i);
                TeamEntity teamEntity = new TeamEntity();
                teamEntity.announcement = (itemJson.optString("announcement"));
                teamEntity.creator = (itemJson.optString("owner"));
                teamEntity.name = (itemJson.optString("tname"));
                teamEntity.id = (itemJson.optString("tid"));
                teamEntity.introduce = (itemJson.optString("intro"));
                String custom = itemJson.optString("custom");
                if(!TextUtils.isEmpty(custom)){
//                    custom = custom.replace("\\" , "");
                    LogUtil.i("team" , custom);
                }
                teamEntity.extension = (custom);
                teamEntity.memberLimit = (itemJson.optInt("maxusers"));
                teamEntity.verifyType = (itemJson.optInt("joinmode"));
                teamEntity.memberCount = (itemJson.optInt("size"));
                teamList.add(teamEntity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return teamList;
    }

    /**
     * 获取朋友圈列表
     */
    public final static ArrayList<CircleItem> getCircleList(JSONObject response){
        ArrayList<CircleItem> circleList = new ArrayList<CircleItem>();
        try {
            JSONArray array = response.getJSONArray("data");
            int size = array.length();
            LogUtil.i("Circle" , "size :" + size);
            for(int i = 0 ; i < size ; i++){
                JSONObject itemJson = array.getJSONObject(i);
                CircleItem circleItem = new CircleItem();
                UserEntity publisherInfo = new UserEntity();
                int type = itemJson.optInt("type");
                circleItem.setSid(itemJson.optString("sid"));
                //
                publisherInfo.account = (itemJson.optString("uid"));
                publisherInfo.name = (itemJson.optString("nickname"));
                publisherInfo.avatar = (itemJson.optString("avatar"));
                circleItem.setPublisherInfo(publisherInfo);
//                circleItem.setUid(itemJson.optString("uid"));
//                circleItem.setNickname(itemJson.optString("nickname"));
//                circleItem.setAvatar(itemJson.optString("avatar"));
                if(!itemJson.isNull("content") && !TextUtils.isEmpty(itemJson.optString("content"))){
                    circleItem.setContent(itemJson.optString("content"));
                } else{
                    circleItem.setContent("");
                }
                if(itemJson.optInt("i_like") == 0){
                    circleItem.setIsLiked(false);
                }else{
                    circleItem.setIsLiked(true);
                }
                circleItem.setType(type);
                circleItem.setCreateTime(itemJson.optString("entry_time"));
                circleItem.setLikeCount(itemJson.optInt("like"));
                circleItem.setVersion(itemJson.optLong("version"));
                //分享的内容，根据TYPE判断
                if(!itemJson.isNull("share") && !TextUtils.isEmpty(itemJson.optString("share"))){
                    JSONObject shareContentJson = itemJson.getJSONObject("share");
                    if(type == CircleConstant.TYPE_PAIJU){
                        GameBillEntity gameBillEntity = RecordJsonTools.getGameBillEntity(shareContentJson);
                        circleItem.setShareContent(gameBillEntity);
                    }
                }
                //评论
                ArrayList<CommentItem> commentList = new ArrayList<CommentItem>();
                JSONArray commentArray = itemJson.optJSONArray("reply");
                if(commentArray != null && commentArray.length() != 0) {
                    int commentSize = commentArray.length();
                    for (int j = 0; j < commentSize; j++) {
                        JSONObject commentItemJson = commentArray.getJSONObject(j);
                        CommentItem commentItem = new CommentItem();
                        commentItem.setCid(commentItemJson.optString("cid"));
                        commentItem.setContent(commentItemJson.optString("content"));
                        //回复者
                        UserEntity userEntity = new UserEntity();
                        userEntity.account = (commentItemJson.optString("uid"));
                        userEntity.avatar = ("");
                        userEntity.name = (commentItemJson.optString("nickname"));
                        commentItem.setUser(userEntity);
                        if (commentItemJson.has("reply_uid")) {
                            //存在被回复者
                            UserEntity replyEntity = new UserEntity();
                            replyEntity.account = (commentItemJson.optString("reply_uid"));
                            replyEntity.avatar = ("");
                            replyEntity.name = (commentItemJson.optString("reply_nickname"));
                            commentItem.setToReplyUser(replyEntity);
                        }
                        //
                        commentList.add(commentItem);
                    }
                }
                circleItem.setComments(commentList);
                circleList.add(circleItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return circleList;
    }

    /**
     * 解析朋友圈Item
     * @param response
     * @param shareContent
     * @return
     */
    public static CircleItem getCircleItem(JSONObject response , Object shareContent){
        CircleItem circleItem = null;
        try {
            JSONObject dataObject = response.getJSONObject("data");
            circleItem = new CircleItem();
            int type = dataObject.optInt("type");
            circleItem.setSid(dataObject.optString("sid"));
            //
            UserEntity publisherInfo = new UserEntity();
            publisherInfo.account = (dataObject.optString("uid"));
            publisherInfo.name = (dataObject.optString("nickname"));
            publisherInfo.avatar = (dataObject.optString("avatar"));
            circleItem.setPublisherInfo(publisherInfo);
//            circleItem.setUid(dataObject.optString("uid"));
//            circleItem.setNickname(dataObject.optString("nickname"));
//            circleItem.setAvatar(dataObject.optString("avatar"));
            circleItem.setType(type);
            if(!dataObject.isNull("content") && !TextUtils.isEmpty(dataObject.optString("content"))){
                circleItem.setContent(dataObject.optString("content"));
            } else{
                circleItem.setContent("");
            }
            circleItem.setCreateTime(dataObject.optString("entry_time"));
            circleItem.setComments(new ArrayList<CommentItem>());
            circleItem.setLikeCount(0);
            circleItem.setIsLiked(false);
            if(type == CircleConstant.TYPE_PAIJU){
                circleItem.setShareContent(shareContent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return circleItem;
    }

    /**
     * 解析朋友圈评论
     * @param result
     * @return
     */
    public static CommentItem getCircleCommentItem(String result){
        CommentItem commentItem = null;
        try {
            JSONObject response = new JSONObject(result);
            JSONObject commentItemJson = response.getJSONObject("data");
            commentItem = new CommentItem();
            commentItem.setCid(commentItemJson.optString("cid"));
            commentItem.setContent(commentItemJson.optString("content"));
            //回复者
            UserEntity userEntity = new UserEntity();
            userEntity.account = (commentItemJson.optString("uid"));
            userEntity.avatar = ("");
            userEntity.name = (commentItemJson.optString("nickname"));
            commentItem.setUser(userEntity);
            if (commentItemJson.has("reply_uid")) {
                //存在被回复者
                UserEntity replyEntity = new UserEntity();
                replyEntity.account = (commentItemJson.optString("reply_uid"));
                replyEntity.avatar = ("");
                replyEntity.name = (commentItemJson.optString("reply_nickname"));
                commentItem.setToReplyUser(replyEntity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return commentItem;
    }

    /**
     * 获取已经注册的手机对应的UID
     * @param result
     * @return
     */
    public static ArrayList<PhoneUidEntity> getRegisteredPhoneUidList(String result) {
        ArrayList<PhoneUidEntity> phoneUidList = new ArrayList<PhoneUidEntity>();
        try {
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.optInt("code") == 0) {
                JSONObject data = jsonObject.optJSONObject("data");
                if (data != null) {
                    //IOS
                    JSONObject iosObj = data.optJSONObject("ios");
                    if(iosObj != null){
                        Iterator<String> iosIterator = iosObj.keys();
                        if (iosIterator != null && iosIterator.hasNext()) {
                            while (iosIterator.hasNext()) {
                                String phone = iosIterator.next();
                                String uid = iosObj.optString(phone);
                                if (!DealerConstant.isDealer(uid)) {
                                    //不是客服
                                    LogUtil.i("UserAction", phone + ":" + uid);
                                    phoneUidList.add(new PhoneUidEntity(phone, uid, ContactConstants.OS_IOS));
                                }
                            }
                        }
                    }
                    //android
                    JSONObject androidObj = data.optJSONObject("android");
                    if(androidObj != null) {
                        Iterator<String> androidIterator = androidObj.keys();
                        if (androidIterator != null && androidIterator.hasNext()) {
                            while (androidIterator.hasNext()) {
                                String phone = androidIterator.next();
                                String uid = androidObj.optString(phone);
                                if (!DealerConstant.isDealer(uid)) {
                                    //不是客服
                                    LogUtil.i("UserAction", phone + ":" + uid);
                                    phoneUidList.add(new PhoneUidEntity(phone, uid, ContactConstants.OS_ANDROID));
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return phoneUidList;
    }
}
