package com.netease.nim.uikit.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.uikit.R;

/**
 * Created by 周智慧 on 17/1/10.
 */

public class HomeTabItem extends RelativeLayout {
    ImageView home_tab_top_iv;//最上边的图片
    TextView home_tab_bottom_tv;//图片下边的文案
    TextView tv_chat_new_notify;//带文字的红点
    ImageView iv_chat_new_notify;//不带文字的红点
    public HomeTabItem(Context context) {
        this(context, null);
    }
    public HomeTabItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public HomeTabItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HomeTabItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.view_home_tab_item, this, true);
        home_tab_top_iv = (ImageView) findViewById(R.id.home_tab_top_iv);
        home_tab_bottom_tv = (TextView) findViewById(R.id.home_tab_bottom_tv);
        tv_chat_new_notify = (TextView) findViewById(R.id.tv_chat_new_notify);
        iv_chat_new_notify = (ImageView) findViewById(R.id.iv_chat_new_notify);
    }

    public void setResources(int textId, int imageId) {
        home_tab_top_iv.setImageResource(imageId);
        home_tab_bottom_tv.setText(textId);
    }

    /**
     * 更新子view样式，参数是5个子view的其中一个
     * @param unreadCount
     */
    public void updateUnreadCount(int unreadCount) {
        if (unreadCount <= 0) {
            tv_chat_new_notify.setVisibility(View.GONE);
            iv_chat_new_notify.setVisibility(View.GONE);
        } else {
            if (unreadCount > 99) {
                tv_chat_new_notify.setText(R.string.new_message_count_max);
            } else if (unreadCount > 1) {
                tv_chat_new_notify.setText("" + unreadCount);
            }
            tv_chat_new_notify.setVisibility(unreadCount > 1 ? View.VISIBLE : View.GONE);
            iv_chat_new_notify.setVisibility(unreadCount == 1 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        home_tab_top_iv.setEnabled(enabled);
        home_tab_bottom_tv.setEnabled(enabled);
        tv_chat_new_notify.setEnabled(enabled);
        iv_chat_new_notify.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    @Override
    public void setSelected(boolean selected) {
        home_tab_top_iv.setSelected(selected);
        home_tab_bottom_tv.setSelected(selected);
        tv_chat_new_notify.setSelected(selected);
        iv_chat_new_notify.setSelected(selected);
        super.setSelected(selected);
    }

}
