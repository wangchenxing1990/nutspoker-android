package com.htgames.nutspoker.circle.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.GameBillEntity;
import com.netease.nim.uikit.bean.UserEntity;
import com.htgames.nutspoker.chat.helper.UserInfoShowHelper;
import com.htgames.nutspoker.circle.activity.CircleActivity;
import com.htgames.nutspoker.circle.bean.CommentItem;
import com.htgames.nutspoker.circle.bean.LikeItem;
import com.htgames.nutspoker.circle.control.CirclePublicCommentControl;
import com.htgames.nutspoker.circle.mvp.presenter.CirclePresenter;
import com.htgames.nutspoker.circle.mvp.view.ICircleViewUpdate;
import com.htgames.nutspoker.circle.spannable.ISpanClick;
import com.htgames.nutspoker.circle.view.CommentDialog;
import com.htgames.nutspoker.circle.viewholder.FooterViewHolder;
import com.htgames.nutspoker.circle.viewholder.HeaderViewHolder;
import com.htgames.nutspoker.data.common.CircleConstant;
import com.htgames.nutspoker.circle.bean.CircleItem;
import com.htgames.nutspoker.circle.viewholder.CircleViewHolder;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.netease.nim.uikit.common.DateTools;
import com.htgames.nutspoker.ui.action.CircleAction;
import com.htgames.nutspoker.ui.activity.Record.RecordDetailsActivity;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CircleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ICircleViewUpdate{
    private final String TAG = "CircleAdapter";
    private Context context;
    private LayoutInflater mLayoutInflater;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;//ITEM类型：头部
    private static final int TYPE_FOOTER = 2;//ITEM类型：尾部
    ArrayList<CircleItem> circleList;
    /** 内容最小行数*/
    public int contentMinLines = 4;
    /** 内容最大行数*/
    public int contentMaxLines = 100;
    /** 头部界面*/
    private View mHeaderView;
    /** 底部界面*/
    private View mFooterView;
    //
    private CirclePresenter mPresenter;
    private CirclePublicCommentControl mCirclePublicCommentControl;
    CircleAction circleAction;

    public CircleAdapter(Context context, ArrayList<CircleItem> circleList, CircleAction circleAction) {
        this.context = context;
        this.circleList = circleList;
        mLayoutInflater = LayoutInflater.from(context);
        this.circleAction = circleAction;
        mPresenter = new CirclePresenter(this);
    }

    public void setCirclePublicCommentControl(CirclePublicCommentControl mCirclePublicCommentControl){
        this.mCirclePublicCommentControl = mCirclePublicCommentControl;
    }

    /**
     * 添加头部
     */
    public void addHeaderView(View view){
        mHeaderView = view;
    }

    /**
     * 添加底部
     */
    public void addFooterView(View view){
        mFooterView = view;
    }

    /**
     * 删除底部
     */
    public void removeFooterView(View view){
        mFooterView = null;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mHeaderView != null) {
            return TYPE_HEADER;
        }
        if (position + 1 == getItemCount() && mFooterView != null) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_HEADER && mHeaderView != null) {
            mHeaderView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new HeaderViewHolder(mHeaderView);
        }
        if (viewType == TYPE_FOOTER && mFooterView != null) {
            mFooterView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new FooterViewHolder(mFooterView);
        }
        return new CircleViewHolder(mLayoutInflater.inflate(R.layout.list_circle_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof HeaderViewHolder) {

        }
        if (viewHolder instanceof FooterViewHolder) {

        }
        if (viewHolder instanceof CircleViewHolder) {
            final int circlePosition = position - (mHeaderView == null ? 0 : 1);
            final CircleItem circleEntity = circleList.get(circlePosition);
            final CircleViewHolder holder = (CircleViewHolder)viewHolder;
            final UserEntity publisherInfo = circleEntity.getPublisherInfo();
            holder.tv_circle_nickname.setText(UserInfoShowHelper.getUserNickname(publisherInfo));
//            holder.tv_circle_nickname.setText(publisherInfo.getName());
            holder.tv_circle_createtime.setText(DateTools.formatDisplayTime(circleEntity.getCreateTime(), null));
            holder.iv_circle_userhead.loadBuddyAvatarByUrl(publisherInfo.account, publisherInfo.avatar, HeadImageView.DEFAULT_AVATAR_THUMB_SIZE);
            holder.iv_circle_userhead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mAvaterClickListener != null){
                        mAvaterClickListener.click(publisherInfo.account);
                    }
                }
            });
            holder.btn_comment.setOnClickListener(new OnCommentClick(circlePosition));
            if(circleEntity.getType() == CircleConstant.TYPE_PAIJU){
//                holder.viewstub_content.setLayoutResource(R.layout.layout_circle_paiju_item);
//                holder.viewstub_content.inflate();
                //点击进入牌局
                GameBillEntity gameBillEntity = (GameBillEntity)circleEntity.getShareContent();
                if(gameBillEntity != null){
                    holder.mCirclePaijuView.setVisibility(View.VISIBLE);
                    holder.mCirclePaijuView.setData(gameBillEntity);
                    holder.mCirclePaijuView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(context instanceof Activity){
                                RecordDetailsActivity.start((Activity)context , (GameBillEntity)circleEntity.getShareContent() ,RecordDetailsActivity.FROM_CIRCLE);
                            }
                        }
                    });
                }else{
                    holder.mCirclePaijuView.setVisibility(View.GONE);
                }
            }else if(circleEntity.getType() == CircleConstant.TYPE_PAIPU){
//                holder.viewstub_content.setLayoutResource(R.layout.layout_circle_paipu_item);
//                holder.viewstub_content.inflate();
                holder.mCirclePaijuView.setVisibility(View.GONE);
            } else{
                holder.mCirclePaijuView.setVisibility(View.GONE);
            }
            //内容行数量大于4行
//            holder.tv_circle_content.setText(circleEntity.getContent());
//            TextPaint mTextPaint = holder.tv_circle_content.getPaint();
//            int mTextViewWidth = (int)mTextPaint.measureText(circleEntity.getContent());
//            Log.d(TAG , "mTextViewWidth : " + mTextViewWidth);

            if(!TextUtils.isEmpty(circleEntity.getContent())){
                holder.mContentTextView.setVisibility(View.VISIBLE);
                holder.mContentTextView.setText(circleEntity.getContent());
            } else{
                holder.mContentTextView.setVisibility(View.GONE);
            }

//            if (mTextViewWidth > contentMinLines) {
//                holder.tv_circle_content_show.setVisibility(View.VISIBLE);
//                holder.tv_circle_content_show.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        onShowAllContent(holder.tv_circle_content, holder.tv_circle_content_show, holder.tv_circle_content.getText().equals(context.getString(R.string.circle_content_all)));
//                    }
//                });
//                onShowAllContent(holder.tv_circle_content, holder.tv_circle_content_show, false);
//            } else {
//                holder.tv_circle_content_show.setVisibility(View.GONE);
//            }

            //是否已经赞过
            boolean isLiked = circleEntity.isLiked();
            holder.ck_like.setChecked(isLiked);
            //
            holder.ck_like.setText(String.format(context.getString(R.string.circle_like_count) , circleEntity.getLikeCount()));
            holder.ck_like.setOnClickListener(new OnLikeClick(circlePosition));

            //评论和赞
            ArrayList<LikeItem> likeList = (ArrayList)circleEntity.getLikes(); //like
            final ArrayList<CommentItem> commentList =  (ArrayList)circleEntity.getComments();//评论
            if((commentList != null && commentList.size() != 0)
                    || (likeList != null && likeList.size() != 0)){
                holder.ll_comment_like_list.setVisibility(View.VISIBLE);
                if((likeList == null || likeList.size() == 0) || (commentList == null || commentList.size() == 0)){
                    holder.view_line.setVisibility(View.GONE);
                }else {
                    //共同有数据，才显示线
                    holder.view_line.setVisibility(View.VISIBLE);
                }
            }else{
                holder.ll_comment_like_list.setVisibility(View.GONE);
            }
            if(likeList == null || likeList.size() == 0){
                holder.mLikeListView.setVisibility(View.GONE);
            }else{
                holder.mLikeListView.setVisibility(View.VISIBLE);
                holder.mLikeListAdapter = new LikeListAdapter(likeList);
                holder.mLikeListView.setAdapter(holder.mLikeListAdapter);
                holder.mLikeListView.setSpanClickListener(new ISpanClick() {
                    @Override
                    public void onClick(int position) {

                    }
                });
                holder.mLikeListAdapter.notifyDataSetChanged();
            }
            boolean hasComment = circleEntity.hasComment();
            if(hasComment){
                //处理评论列表
                holder.mCommentListView.setVisibility(View.VISIBLE);
                holder.mCommentAdapter = new CommentAdapter(context ,commentList);
                holder.mCommentListView.setAdapter(holder.mCommentAdapter);
                holder.mCommentAdapter.setCommentClickListener(new CommentAdapter.ICommentItemClickListener() {
                    @Override
                    public void onItemClick(int commentPosition) {
                        CommentItem commentItem = commentList.get(commentPosition);
                        if(commentItem.getUser().account.equals(UserPreferences.getInstance(context).getUserId())){
                            //复制或者删除自己的评论
                            CommentDialog dialog = new CommentDialog(context, mPresenter, commentItem, circlePosition ,circleAction);
//                            CommentDialog dialog = new CommentDialog(context, commentItem, commentPosition);
                            dialog.show();
                        } else{
                            //回复别人的评论
                            if(mCirclePublicCommentControl!=null){
                                mCirclePublicCommentControl.editTextBodyVisible(View.VISIBLE, mPresenter, circlePosition, ICircleViewUpdate.TYPE_REPLY_COMMENT, commentItem.getUser(), commentPosition , circleEntity.getSid());
                            }
                        }
                    }
                });
                holder.mCommentAdapter.notifyDataSetChanged();
                holder.mCommentListView.setOnItemClickListener(null);
                holder.mCommentListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> arg0, View view, final int commentPosition, long id) {
                        //长按进行复制或者删除
                        CommentItem commentItem = commentList.get(commentPosition);
                        CommentDialog dialog = new CommentDialog(context, mPresenter, commentItem, circlePosition ,circleAction);
                        dialog.show();
                        return true;
                    }
                });
            }else{
                holder.mCommentListView.setVisibility(View.GONE);
            }
            if(publisherInfo.account.equals(UserPreferences.getInstance(context).getUserId())){
                holder.btn_circle_delete.setVisibility(View.VISIBLE);
            }else{
                holder.btn_circle_delete.setVisibility(View.GONE);
            }
            holder.btn_circle_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCircle(circlePosition , circleEntity.getSid());
                }
            });
        }
    }

    /**
     * 删除分享
     */
    private void deleteCircle(final int position , final String circleId) {
        if (!NetworkUtil.isNetAvailable(context)) {
            Toast.makeText(context, R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
            return;
        }
        EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(context, null, context.getString(R.string.circle_delete_circle_tip), true,
                new EasyAlertDialogHelper.OnDialogActionListener() {

                    @Override
                    public void doCancelAction() {

                    }

                    @Override
                    public void doOkAction() {
                        circleAction.revokeShareOrComment(circleId, new RequestCallback() {
                            @Override
                            public void onResult(int code, String result, Throwable var3) {
                                if (code == 0) {
                                    if (mPresenter != null) {
                                        mPresenter.deleteCircle(circleId);
                                    }
                                }
                            }
                            @Override
                            public void onFailed() {

                            }
                        });

                    }
                });
        if (!((Activity)context).isFinishing() && !((CircleActivity)context).isDestroyedCompatible()) {
            dialog.show();
        }
    }

//    public void onShowAllContent(TextView tv_content ,TextView tv_show , boolean showAll){
//        if(showAll){
//            //显示全部
//            tv_content.setMaxLines(contentMaxLines);
//            tv_show.setText(R.string.circle_content_packup);
//        }else{
//            tv_content.setMaxLines(contentMinLines);
//            tv_show.setText(R.string.circle_content_all);
//        }
//    }

//    public class OnShowContentClick implements View.OnClickListener{
//        public TextView tv_content;
//        public TextView tv_show;
//
//        public OnShowContentClick(TextView tv_content , TextView tv_show){
//            this.tv_content = tv_content;
//            this.tv_show = tv_show;
//        }
//
//        @Override
//        public void onClick(View v) {
//            onShowAllContent(tv_content, tv_show, false);
//        }
//    }

    public class OnLikeClick implements View.OnClickListener{
        private int position;

        public OnLikeClick(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if(mCircleListener != null){
                mCircleListener.doLike(position , true);
            }
        }
    }

    public class OnCommentClick implements View.OnClickListener{
        private int mCirclePosition;

        public OnCommentClick(int position){
            this.mCirclePosition = position;
        }

        @Override
        public void onClick(View v) {
            if(mCirclePublicCommentControl!=null){
                mCirclePublicCommentControl.editTextBodyVisible(View.VISIBLE, mPresenter, mCirclePosition, ICircleViewUpdate.TYPE_PUBLIC_COMMENT, null, 0 , circleList.get(mCirclePosition).getSid());
            }
        }
    }

    CircleListener mCircleListener;
    AvaterClickListener mAvaterClickListener;

    public void setCircleListener(CircleListener mCircleListener){
        this.mCircleListener = mCircleListener;
    }

    public interface CircleListener{
        void doLike(int position, boolean like);
    }

    public void setAvaterClickListener(AvaterClickListener mAvaterClickListener){
        this.mAvaterClickListener = mAvaterClickListener;
    }

    public interface AvaterClickListener{
        void click(String account);
    }

    @Override
    public int getItemCount() {
        int newsItemCount = 0;
        if(circleList != null && circleList.size() != 0){
            newsItemCount = circleList.size();
        }
        if(mHeaderView != null){
            newsItemCount = newsItemCount + 1;
        }
        if(mFooterView != null){
            newsItemCount = newsItemCount + 1;
        }
        return newsItemCount;
    }

    @Override
    public void update2DeleteCircle(String circleId) {
        for(int i=0; i < circleList.size(); i++){
            if(circleId.equals(circleList.get(i).getSid())){
                circleList.remove(i);
                notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public void update2AddFavorite(int circlePosition) {

    }

    @Override
    public void update2DeleteFavort(int circlePosition, String favortId) {

    }

    @Override
    public void update2AddComment(int circlePosition, int type, UserEntity replyUser , CommentItem commentItem) {
//        CommentItem newItem = new CommentItem();
//        String content = "";
//        if (mCirclePublicCommentControl != null) {
//            content = mCirclePublicCommentControl.getEditTextString();
//        }
//        newItem.setUser(new UserEntity(UserPreferences.getInstance(context).getUserId() , NimUserInfoCache.getInstance().getUserName(DemoCache.account), ""));
//        newItem.setContent(content);
//        if (type == TYPE_PUBLIC_COMMENT) {
//        } else if (type == TYPE_REPLY_COMMENT) {
//            newItem.setToReplyUser(replyUser);
//        }
        List<CommentItem> commentList =  circleList.get(circlePosition).getComments();
        if(commentList == null){
            commentList = new ArrayList<CommentItem>();
            circleList.get(circlePosition).setComments(commentList);
        }
        circleList.get(circlePosition).getComments().add(commentItem);
        notifyDataSetChanged();
        if (mCirclePublicCommentControl != null) {
            mCirclePublicCommentControl.clearEditText();
        }
    }

    @Override
    public void update2DeleteComment(int circlePosition, String commentId) {
        List<CommentItem> items = circleList.get(circlePosition).getComments();
        int size = items == null ? 0 : items.size();
        for(int i = 0; i < size; i++){
            if(commentId.equals(items.get(i).getCid())){
                circleList.get(circlePosition).getComments().remove(i);
                notifyDataSetChanged();
                return;
            }
        }
    }
}
