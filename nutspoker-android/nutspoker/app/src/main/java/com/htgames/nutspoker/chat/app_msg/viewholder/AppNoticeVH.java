package com.htgames.nutspoker.chat.app_msg.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.app_msg.attach.AppNotify;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.app_msg.view.image.AppNoticeImageView;
import com.htgames.nutspoker.config.UniversalImageLoaderUtil;
import com.netease.nim.uikit.common.util.BaseTools;
import com.htgames.nutspoker.view.roundedimageView.SelectableRoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;

/**
 * Created by 周智慧 on 16/12/6.
 * 代码拷贝来自AppNoticeViewHolder.java 新版本样式改版
 */

public class AppNoticeVH extends BaseAppViewHolder {
    ImageView iv_app_message_type;
    TextView tv_message_time;
    //
    TextView tv_app_message_title;
    TextView tv_app_message_content;
    AppNoticeImageView iv_app_notice_show;
    SelectableRoundedImageView mSelectableRoundedImageView;
    View view_app_message_divider;
    View notice_click_area;
    View top_pad;
    View bottom_pad;
    //图片展示比例：1：2
    private int imageWidth = 70;

    public static AppNoticeVH createViewHolder(Context context) {
        return new AppNoticeVH(LayoutInflater.from(context).inflate(R.layout.viewholder_app_notice_new, null));
    }

    public AppNoticeVH(View itemView) {
        super(itemView);
        this.itemView = itemView;
        iv_app_message_type = (ImageView) itemView.findViewById(R.id.iv_app_message_type);
        tv_message_time = (TextView) itemView.findViewById(R.id.tv_message_time);
        notice_click_area = itemView.findViewById(R.id.notice_click_area);
        top_pad = itemView.findViewById(R.id.top_pad);
        bottom_pad = itemView.findViewById(R.id.bottom_pad);
        view_app_message_divider = itemView.findViewById(R.id.view_app_message_divider);
        tv_app_message_title = (TextView) itemView.findViewById(R.id.tv_app_message_title);
        tv_app_message_content = (TextView) itemView.findViewById(R.id.tv_app_message_content);
        iv_app_notice_show = (AppNoticeImageView) itemView.findViewById(R.id.iv_app_notice_show);
        mSelectableRoundedImageView = (SelectableRoundedImageView) itemView.findViewById(R.id.mSelectableRoundedImageView);
        //设置IMA的初始化大小
        int width = BaseTools.getWindowWidth(DemoCache.getContext());// - BaseTools.dip2px(DemoCache.getContext(), imageWidth);
        LinearLayout.LayoutParams img_layout = (LinearLayout.LayoutParams) mSelectableRoundedImageView.getLayoutParams();
        img_layout.width = width;
        img_layout.height = (int) (width / 2.5f);
        mSelectableRoundedImageView.setLayoutParams(img_layout);

        LinearLayout.LayoutParams img_layout_two = (LinearLayout.LayoutParams) iv_app_notice_show.getLayoutParams();
        img_layout.width = width;
        img_layout.height = (int) (width / 2.5f);
        iv_app_notice_show.setLayoutParams(img_layout_two);
    }

    public void refresh(Context context, AppMessage appMessage, boolean showTopPad, int position) {
        top_pad.setVisibility(showTopPad ? View.VISIBLE : View.GONE);
        notice_click_area.setOnClickListener(new OnClick(appMessage, position));
        notice_click_area.setOnLongClickListener(new OnLongClick(appMessage, position));
        mSelectableRoundedImageView.setOnClickListener(new OnClick(appMessage, position));
        SimpleDateFormat sdf = new SimpleDateFormat("MM" + "." + "dd" + " " + "HH:mm");
        tv_message_time.setText(sdf.format(appMessage.time * 1000L));
        AppNotify appNotify = (AppNotify) appMessage.attachObject;
//        iv_app_message_type.setImageResource(R.mipmap.icon_app_message_appnotice);
        //
        String url = appNotify.url;
        String image = appNotify.showImage;
        view_app_message_divider.setVisibility(TextUtils.isEmpty(appNotify.title) ? View.GONE : View.VISIBLE);
        tv_app_message_title.setText(appNotify.title);
        tv_app_message_title.setVisibility(TextUtils.isEmpty(appNotify.title) ? View.GONE : View.VISIBLE);
        tv_app_message_content.setText(appNotify.content);
        tv_app_message_content.setVisibility(TextUtils.isEmpty(appNotify.content) ? View.GONE : View.VISIBLE);
        if (!TextUtils.isEmpty(url)) {
//            view_app_message_divider.setVisibility(View.VISIBLE);
//            rl_app_message_detail.setOnClickListener(this);
        } else {
//            view_app_message_divider.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(image)) {
            ImageLoader.getInstance().displayImage(image, mSelectableRoundedImageView, UniversalImageLoaderUtil.getAppNoticeImageOptions());
            mSelectableRoundedImageView.setVisibility(View.GONE);//iv_app_notice_show.setVisibility(View.VISIBLE);
            iv_app_notice_show.setVisibility(View.VISIBLE);
            iv_app_notice_show.load(image);
        } else {
            mSelectableRoundedImageView.setVisibility(View.GONE);
            iv_app_notice_show.setVisibility(View.GONE);
        }
    }
}
