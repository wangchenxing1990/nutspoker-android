package com.htgames.nutspoker.circle.mvp.view;

import com.netease.nim.uikit.bean.UserEntity;
import com.htgames.nutspoker.circle.bean.CommentItem;

/**
 * 
* @ClassName: ICircleViewUpdateListener 
* @Description: view,服务器响应后更新界面 
*
 */
public interface ICircleViewUpdate {
	/**
	 * 发布评论
	 */
	public static final int TYPE_PUBLIC_COMMENT = 0;
	/**
	 * 回复评论
	 */
	public static final int TYPE_REPLY_COMMENT = 1;
	
	public void update2DeleteCircle(String circleId);
	public void update2AddFavorite(int circlePosition);
	public void update2DeleteFavort(int circlePosition, String favortId);
	public void update2AddComment(int circlePosition, int type, UserEntity replyUser , CommentItem commentItem);//type: 0 发布评论  1 回复评论
	public void update2DeleteComment(int circlePosition, String commentId);
}
