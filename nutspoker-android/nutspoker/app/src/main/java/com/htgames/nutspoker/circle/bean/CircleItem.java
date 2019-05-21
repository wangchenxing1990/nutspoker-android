package com.htgames.nutspoker.circle.bean;

import android.text.TextUtils;

import com.netease.nim.uikit.bean.UserEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 发现实体类
 */
public class CircleItem implements Serializable {
    private String sid;
    //    private String uid;
//    private String nickname;
//    private String avatar;
    private UserEntity publisherInfo;//发布者信息
    private int likeCount;
    private boolean isLiked;
    private String content;
    private String createTime;
    private int type;//1:战绩  2:牌谱
    private Object shareContent;
    private List<LikeItem> likes;
    private List<CommentItem> comments;
    private long version;

    public boolean isLiked() {
        return isLiked;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

//    public String getUid() {
//        return uid;
//    }
//
//    public void setUid(String uid) {
//        this.uid = uid;
//    }
//
//    public String getNickname() {
//        return nickname;
//    }
//
//    public void setNickname(String nickname) {
//        this.nickname = nickname;
//    }
//
//    public String getAvatar() {
//        return avatar;
//    }
//
//    public void setAvatar(String avatar) {
//        this.avatar = avatar;
//    }


    public UserEntity getPublisherInfo() {
        return publisherInfo;
    }

    public void setPublisherInfo(UserEntity publisherInfo) {
        this.publisherInfo = publisherInfo;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public Object getShareContent() {
        return shareContent;
    }

    public void setShareContent(Object shareContent) {
        this.shareContent = shareContent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<LikeItem> getLikes() {
        return likes;
    }

    public void setLikes(List<LikeItem> likes) {
        this.likes = likes;
    }

    public List<CommentItem> getComments() {
        return comments;
    }

    public void setComments(List<CommentItem> comments) {
        this.comments = comments;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    /**
     * 是否有人赞过赞过（大于0）
     *
     * @return
     */
    public boolean hasLike() {
        if (likes != null && likes.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 是否有人评论过
     *
     * @return
     */
    public boolean hasComment() {
        if (comments != null && comments.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前用户赞过的ID
     *
     * @param curUserId
     * @return
     */
    public String getCurUserLikedId(String curUserId) {
        String likeId = "";
        if (!TextUtils.isEmpty(curUserId) && hasLike()) {
            for (LikeItem item : likes) {
                if (curUserId.equals(item.getUser().account)) {
                    likeId = item.getId();
                    return likeId;
                }
            }
        }
        return likeId;
    }
}
