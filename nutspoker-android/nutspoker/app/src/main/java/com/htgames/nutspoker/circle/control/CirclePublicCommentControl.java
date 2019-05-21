package com.htgames.nutspoker.circle.control;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import com.netease.nim.uikit.common.util.log.LogUtil;
import android.view.View;
import android.widget.EditText;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.UserEntity;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.circle.activity.CircleActivity;
import com.htgames.nutspoker.circle.bean.CommentItem;
import com.htgames.nutspoker.circle.mvp.presenter.CirclePresenter;
import com.htgames.nutspoker.circle.mvp.view.ICircleViewUpdate;
import com.htgames.nutspoker.circle.utils.CommonUtils;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.tool.JsonResolveUtil;
import com.htgames.nutspoker.tool.NameTrimTools;
import com.htgames.nutspoker.ui.action.CircleAction;
import com.htgames.nutspoker.view.widget.CustomListView;
import com.htgames.nutspoker.widget.Toast;

/**
 * 控制EdittextView的显示和隐藏，以及发布动作，根据回复的位置调整listview的位置
 */
public class CirclePublicCommentControl {
    private final static String TAG = "CirclePublicComment";
    private View mEditTextBody;
    private EditText mEditText;
    private int mCirclePosition;
    private int mCommentType;
    private int mCommentPosition;
    //    private ListView mListView;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private View mSendBt;
    private UserEntity mReplyUser;
    private CirclePresenter mCirclePresenter;
    /**
     * 选择动态条目的高
     */
    private int mSelectCircleItemH;
    /**
     * 选择的commentItem距选择的CircleItem底部的距离
     */
    private int mSelectCommentItemBottom;

    CircleAction mCircleAction;
    String cid;

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setRecyclerView(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
    }

    public CirclePublicCommentControl(final Context context, View editTextBody, EditText editText, View sendBt ,CircleAction circleAction) {
        mContext = context;
        mEditTextBody = editTextBody;
        mEditText = editText;
        mSendBt = sendBt;
        mCircleAction = circleAction;
        mSendBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mEditText.getText().toString();
                content = NameTrimTools.getNameTrim(content);
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(context, R.string.comment_content_null, Toast.LENGTH_SHORT).show();
                    return;
                }
                String replyUid = "";
                if (mReplyUser != null && !TextUtils.isEmpty(mReplyUser.account)) {
                    replyUid = mReplyUser.account;
                }
                mCircleAction.doComment(content, cid, replyUid, new RequestCallback() {
                    @Override
                    public void onResult(int code, String result, Throwable var3) {
                        if (code == 0) {
                            CommentItem commentItem = JsonResolveUtil.getCircleCommentItem(result);
                            if (mCirclePresenter != null && commentItem != null) {
                                //发布评论
                                mCirclePresenter.addComment(mCirclePosition, mCommentType, mReplyUser, commentItem);
                            }
                        }
                    }

                    @Override
                    public void onFailed() {

                    }
                });
                editTextBodyVisible(View.GONE);
            }
        });
    }

    /**
     * @param visibility
     * @param mCirclePresenter
     * @param mCirclePosition
     * @param commentType      0:发布评论   1：回复评论
     * @param replyUser
     * @return void    返回类型
     * @throws
     * @Title: editTextBodyVisible
     * @Description: 评论时显示发布布局，评论完隐藏，根据不同位置调节listview的滑动
     */
    public void editTextBodyVisible(int visibility, CirclePresenter mCirclePresenter, int mCirclePosition, int commentType, UserEntity replyUser, int commentPosition , String cid) {
        this.mCirclePosition = mCirclePosition;
        this.mCirclePresenter = mCirclePresenter;
        this.mCommentType = commentType;
        this.mReplyUser = replyUser;
        this.mCommentPosition = commentPosition;
        this.cid = cid;
        editTextBodyVisible(visibility);

        measure(mCirclePosition + 1, commentType);//加1是因为有头部，而mCirclePosition定位的位置是列表中的位置
    }

    private void measure(int mCirclePosition, int commentType) {
        if (mRecyclerView != null) {
            int firstPosition = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();//获取当前屏幕第一的position
            LogUtil.i(TAG, "firstPosition:" + firstPosition + ";mCirclePosition :" + mCirclePosition);
            View selectCircleItem = mRecyclerView.getChildAt(mCirclePosition - firstPosition);//获取选中的朋友圈ITEM
//            mSelectCircleItemH = selectCircleItem.getHeight();
            mSelectCircleItemH = selectCircleItem.getHeight();
            LogUtil.i(TAG, "mSelectCircleItemH:" + mSelectCircleItemH);
            if (commentType == ICircleViewUpdate.TYPE_REPLY_COMMENT) {//回复评论的情况
//                AppNoScrollerListView commentLv = (AppNoScrollerListView) selectCircleItem.findViewById(R.id.commentList);
                CustomListView commentLv = (CustomListView) selectCircleItem.findViewById(R.id.mCommentListView);
                if (commentLv != null) {
                    int firstCommentPosition = commentLv.getFirstVisiblePosition();
                    //找到要回复的评论view,计算出该view距离所属动态底部的距离
                    View selectCommentItem = commentLv.getChildAt(mCommentPosition - firstCommentPosition);
                    if (selectCommentItem != null) {
                        mSelectCommentItemBottom = 0;
                        View parentView = selectCommentItem;
                        do {
                            int subItemBottom = parentView.getBottom();
                            parentView = (View) parentView.getParent();
                            if (parentView != null) {
                                mSelectCommentItemBottom += (parentView.getHeight() - subItemBottom);
                            }
                        } while (parentView != null && parentView != selectCircleItem);
                    }
                }
            }
        }else{
            LogUtil.i(TAG, "mRecyclerView == null");
        }
    }

    public void handleListViewScroll() {
        int keyH = ChessApp.mKeyBoardH;//键盘的高度
        int editTextBodyH = ((CircleActivity) mContext).getEditTextBodyHeight();//整个EditTextBody的高度
        int screenlH = ((CircleActivity) mContext).getScreenHeight();//整个应用屏幕的高度
//        int listviewOffset = screenlH - mSelectCircleItemH - keyH - editTextBodyH;//偏移量：屏幕高度 - 当前选中的朋友圈高度 -
        int listviewOffset = screenlH - mSelectCircleItemH - keyH - editTextBodyH * 2;//偏移量：屏幕高度 - 当前选中的朋友圈高度 -
        LogUtil.i(TAG, "offset=" + listviewOffset + " &mSelectCircleItemH=" + mSelectCircleItemH + " &keyH=" + keyH + " &editTextBodyH=" + editTextBodyH);
        LogUtil.i(TAG, "mSelectCommentItemBottom:" + mSelectCommentItemBottom);
        LogUtil.i(TAG, "mCirclePosition:" + mCirclePosition);
        if (mCommentType == ICircleViewUpdate.TYPE_REPLY_COMMENT) {
            listviewOffset = listviewOffset + mSelectCommentItemBottom;
        }
        if (mRecyclerView != null) {
//            mRecyclerView.setSelectionFromTop(mCirclePosition, listviewOffset);
//            mRecyclerView.scrollToPosition(mCirclePosition);
            ((LinearLayoutManager)mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(mCirclePosition + 1, listviewOffset);
        }

    }

    public void editTextBodyVisible(int visibility) {
        if (mEditTextBody != null) {
            mEditTextBody.setVisibility(visibility);
            if (View.VISIBLE == visibility) {
                //
                if (mReplyUser != null && !DemoCache.getAccount().equals(mReplyUser.account)) {
                    mEditText.setHint(mContext.getString(R.string.circle_reply) + mReplyUser.name + ":");
                }else{
                    mEditText.setHint(mContext.getString(R.string.circle_comment));
                }
                //
                mEditText.requestFocus();
                //弹出键盘
                CommonUtils.showSoftInput(mEditText.getContext(), mEditText);
            } else if (View.GONE == visibility) {
                //隐藏键盘
                CommonUtils.hideSoftInput(mEditText.getContext(), mEditText);
            }
        }
    }

    public String getEditTextString() {
        String result = "";
        if (mEditText != null) {
            result = mEditText.getText().toString();
        }
        return result;
    }

    public void clearEditText() {
        if (mEditText != null) {
            mEditText.setText("");
        }
    }
}
