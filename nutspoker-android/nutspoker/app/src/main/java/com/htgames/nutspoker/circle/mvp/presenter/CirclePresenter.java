package com.htgames.nutspoker.circle.mvp.presenter;


import com.netease.nim.uikit.bean.UserEntity;
import com.htgames.nutspoker.circle.bean.CommentItem;
import com.htgames.nutspoker.circle.mvp.modle.CircleModel;
import com.htgames.nutspoker.circle.mvp.modle.IDataRequestListener;
import com.htgames.nutspoker.circle.mvp.view.ICircleViewUpdate;

/**
 * 
* @ClassName: CirclePresenter 
* @Description: 通知model请求服务器和通知view更新
 */
public class CirclePresenter {
	private CircleModel mCircleModel;
	private ICircleViewUpdate mCircleView;
	
	public CirclePresenter(ICircleViewUpdate view){
		this.mCircleView = view;
		mCircleModel = new CircleModel();
	}
	/**
	 * 
	* @Title: deleteCircle 
	* @Description: 删除动态 
	* @param  circleId     
	* @return void    返回类型 
	* @throws
	 */
	public void deleteCircle(final String circleId){
		mCircleModel.deleteCircle(new IDataRequestListener() {
			
			@Override
			public void loadSuccess(Object object) {
				mCircleView.update2DeleteCircle(circleId);
			}
		});
	}
	/**
	 * 
	* @Title: addFavort 
	* @Description: 点赞
	* @param  circlePosition     
	* @return void    返回类型 
	* @throws
	 */
	public void addFavort(final int circlePosition){
		mCircleModel.addFavort(new IDataRequestListener() {
			
			@Override
			public void loadSuccess(Object object) {
				mCircleView.update2AddFavorite(circlePosition);
			}
		});
	}
	/**
	 * 
	* @Title: deleteFavort 
	* @Description: 取消点赞 
	* @param @param circlePosition
	* @param @param favortId     
	* @return void    返回类型 
	* @throws
	 */
	public void deleteFavort(final int circlePosition, final String favortId){
		mCircleModel.deleteFavort(new IDataRequestListener() {
				
				@Override
				public void loadSuccess(Object object) {
					mCircleView.update2DeleteFavort(circlePosition, favortId);
				}
			});
	}
	
	/**
	 * 
	* @Title: addComment 
	* @Description: 增加评论
	* @param  circlePosition
	* @param  type  0：发布评论  1：回复评论
	* @param  replyUser  回复评论时对谁的回复   
	* @param  commentItem  服务器返回的数据
	* @throws
	 */
	public void addComment(final int circlePosition, final int type, final UserEntity replyUser , final CommentItem commentItem){
		mCircleModel.addComment(new IDataRequestListener(){

			@Override
			public void loadSuccess(Object object) {
				mCircleView.update2AddComment(circlePosition, type, replyUser , commentItem);
			}
		});
	}
	
	/**
	 * 
	* @Title: deleteComment 
	* @Description: 删除评论 
	* @param @param circlePosition
	* @param @param commentId     
	* @return void    返回类型 
	* @throws
	 */
	public void deleteComment(final int circlePosition, final String commentId){
		mCircleModel.addComment(new IDataRequestListener(){

			@Override
			public void loadSuccess(Object object) {
				mCircleView.update2DeleteComment(circlePosition, commentId);
			}
		});
	}
}
