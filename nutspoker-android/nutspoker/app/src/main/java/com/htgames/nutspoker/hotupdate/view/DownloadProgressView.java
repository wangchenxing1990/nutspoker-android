package com.htgames.nutspoker.hotupdate.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.htgames.nutspoker.R;

/**
 * 下载进度条
 */
public class DownloadProgressView extends LinearLayout {
    ProgressBar downloadProBar;
    TextView downloadProgressTv;

    public DownloadProgressView(Context context) {
        super(context);
        init(context);
    }

    public DownloadProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DownloadProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.download_progress_view, null);
        downloadProBar = (ProgressBar) view.findViewById(R.id.proBar_download);
        downloadProgressTv = (TextView) view.findViewById(R.id.tv_download_progress);
        addView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        updateDownloadProgress(0);
    }

    public void updateDownloadProgress(int progress) {
        downloadProBar.setProgress(progress);
        downloadProgressTv.setText(getContext().getResources().getString(R.string.game_update_progress, progress) + "%");
    }
}
