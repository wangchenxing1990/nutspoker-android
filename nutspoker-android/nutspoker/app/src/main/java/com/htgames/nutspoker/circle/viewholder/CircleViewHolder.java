package com.htgames.nutspoker.circle.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htgames.nutspoker.circle.adapter.CommentAdapter;
import com.htgames.nutspoker.circle.adapter.LikeListAdapter;
import com.htgames.nutspoker.circle.view.CirclePaijuView;
import com.htgames.nutspoker.circle.view.ExpandableTextView;
import com.htgames.nutspoker.circle.view.LikeListView;
import com.htgames.nutspoker.view.widget.CustomListView;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

/**
 * 德州圈ViewHolder
 */
public class CircleViewHolder extends RecyclerView.ViewHolder {
    //
    public HeadImageView iv_circle_userhead;
    public TextView btn_comment;
    public CheckBox ck_like;
    public TextView tv_circle_nickname;//昵称
//    public TextView tv_circle_content;//内容
    public TextView tv_circle_createtime;//发布时间
//    public TextView tv_circle_content_show;//收起/全文
    public ViewStub viewstub_content;
    public LinearLayout ll_comment_like_list;
    public LikeListView mLikeListView;//喜欢的列表
    public LikeListAdapter mLikeListAdapter;
    public CommentAdapter mCommentAdapter;
    public View view_line;//分割线
    public CustomListView mCommentListView;//评论的列表
    public CirclePaijuView mCirclePaijuView;
    public TextView btn_circle_delete;
    public ExpandableTextView mContentTextView;

    public CircleViewHolder(View itemView) {
        super(itemView);
//        iv_circle_userhead = (HeadImageView) itemView.findViewById(R.id.iv_circle_userhead);
//        btn_comment = (TextView) itemView.findViewById(R.id.btn_comment);
//        ck_like = (CheckBox) itemView.findViewById(R.id.ck_like);
//        tv_circle_nickname = (TextView) itemView.findViewById(R.id.tv_circle_nickname);
////        tv_circle_content = (TextView) itemView.findViewById(R.id.tv_circle_content);
//        tv_circle_createtime = (TextView) itemView.findViewById(R.id.tv_circle_createtime);
//        btn_circle_delete = (TextView) itemView.findViewById(R.id.btn_circle_delete);
////        tv_circle_content_show = (TextView) itemView.findViewById(R.id.tv_circle_content_show);
//        viewstub_content = (ViewStub) itemView.findViewById(R.id.viewstub_content);
//        ll_comment_like_list = (LinearLayout) itemView.findViewById(R.id.ll_comment_like_list);
//        mLikeListView = (LikeListView) itemView.findViewById(R.id.mLikeListView);
//        view_line = (View) itemView.findViewById(R.id.view_line);
//        mCommentListView = (CustomListView) itemView.findViewById(R.id.mCommentListView);
//        mCirclePaijuView = (CirclePaijuView) itemView.findViewById(R.id.mCirclePaijuView);
//        mContentTextView = (ExpandableTextView) itemView.findViewById(R.id.expand_text_view);
    }
}
