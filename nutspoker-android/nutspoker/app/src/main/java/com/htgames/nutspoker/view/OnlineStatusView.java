package com.htgames.nutspoker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nimlib.sdk.StatusCode;

/**
 * 云信连接状态View
 */
public class OnlineStatusView extends LinearLayout {
    private View view;
    TextView mStatusText;
    ProgressBar mProgressBar;

    public OnlineStatusView(Context context) {
        super(context);
        init(context);
    }

    public OnlineStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OnlineStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.layout_online_status, null);
        mStatusText = (TextView) view.findViewById(R.id.mStatusText);
        mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressBar);
        int height = context.getResources().getDimensionPixelSize(R.dimen.action_bar_height);
        addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
    }

    public void updateStatus(StatusCode status) {
        if (status == StatusCode.NET_BROKEN) {
            //未连接
            this.setVisibility(View.VISIBLE);
            mStatusText.setText(R.string.notconnected);
            mProgressBar.setVisibility(GONE);
        } else if (status == StatusCode.UNLOGIN) {
            //未登录
            this.setVisibility(View.VISIBLE);
            mStatusText.setText(R.string.notconnected);
            mProgressBar.setVisibility(GONE);
        } else if (status == StatusCode.LOGINING) {
            this.setVisibility(View.VISIBLE);
            mStatusText.setText(R.string.connecting);
            mProgressBar.setVisibility(VISIBLE);
        } else if (status == StatusCode.LOGINED) {
            this.setVisibility(View.GONE);
            mProgressBar.setVisibility(GONE);
        }
    }
}
