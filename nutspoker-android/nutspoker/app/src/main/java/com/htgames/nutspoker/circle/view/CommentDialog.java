package com.htgames.nutspoker.circle.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.circle.bean.CommentItem;
import com.htgames.nutspoker.circle.mvp.presenter.CirclePresenter;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.ui.action.CircleAction;

/**
 * 评论长按对话框，保护复制和删除
 */
public class CommentDialog extends Dialog implements android.view.View.OnClickListener {

	private Context mContext;
	private CirclePresenter mPresenter;
	private CommentItem mCommentItem;
	private int mCirclePosition;
	CircleAction circleAction;

	public CommentDialog(Context context, CirclePresenter presenter,  CommentItem commentItem, int circlePosition , CircleAction circleAction) {
		super(context, R.style.comment_dialog);
		mContext = context;
		this.mPresenter = presenter;
		this.mCommentItem = commentItem;
		this.mCirclePosition = circlePosition;
		this.circleAction = circleAction;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_comment);
		initWindowParams();
		initView();
	}

	private void initWindowParams() {
		Window dialogWindow = getWindow();
		// 获取屏幕宽、高用
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = (int) (display.getWidth() * 0.65); // 宽度设置为屏幕的0.65

		dialogWindow.setGravity(Gravity.CENTER);
		dialogWindow.setAttributes(lp);
	}

	private void initView() {
		TextView copyTv = (TextView) findViewById(R.id.copyTv);
		copyTv.setOnClickListener(this);
		TextView deleteTv = (TextView) findViewById(R.id.deleteTv);
		if (mCommentItem != null && UserPreferences.getInstance(mContext).getUserId().equals(mCommentItem.getUser().account)) {
			deleteTv.setVisibility(View.VISIBLE);
		} else {
			deleteTv.setVisibility(View.GONE);
		}
		deleteTv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.copyTv:
			if (mCommentItem != null) {
				ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
				clipboard.setText(mCommentItem.getContent());
			}
			dismiss();
			break;
		case R.id.deleteTv:
			//删除评论
			circleAction.revokeShareOrComment(mCommentItem.getCid(), new RequestCallback() {
				@Override
				public void onResult(int code, String result, Throwable var3) {
					if(code == 0){
						if (mPresenter != null && mCommentItem != null) {
							mPresenter.deleteComment(mCirclePosition, mCommentItem.getCid());
						}
					}
				}

				@Override
				public void onFailed() {

				}
			});
			dismiss();
			break;
		default:
			break;
		}
	}

}
