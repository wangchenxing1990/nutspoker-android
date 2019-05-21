package com.htgames.nutspoker.chat.app_msg.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.app_msg.helper.AppMessageHelper;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.helper.MessageHelper;
import com.htgames.nutspoker.chat.msg.model.HordeMessageType;
import com.htgames.nutspoker.chat.msg.model.SystemMessage;
import com.htgames.nutspoker.chat.notification.constant.CustomNotificationConstants;
import com.htgames.nutspoker.db.AppMsgDBHelper;
import com.netease.nim.uikit.common.DateTools;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.chesscircle.view.GroupHeadView;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

/**
 * Created by 20150726 on 2016/4/20.
 */
public class AppMessageRecentView extends LinearLayout {
    public int type = AppMsgDBHelper.TYPE_NOTICE;//默认是系统消息,//系统消息，在"发现"tab页有两类消息,/控制中心
    Context context;
    View view;
    protected FrameLayout portraitPanel;
    protected HeadImageView imgHead;
    protected TextView tvNickname;
    protected TextView tvMessage;
    protected TextView tvUnread;
    protected View unreadIndicator;
    protected TextView tvDatetime;
    // 消息发送错误状态标记，目前没有逻辑处理
    protected ImageView imgMsgStatus;
    //快速游戏的状态
    protected ImageView iv_recent_new_notify;
    protected ImageView imgGameStatus;
    protected ImageView iv_icon_game_quick;
    private RelativeLayout rl_club_head;
    protected HeadImageView iv_club_head;
    TextView tv_game_count;
    ImageView iv_mute;
    //圈子头像
    GroupHeadView mGroupHeadView;
    boolean isSelectMode = false;
    LinearLayout ll_recent_nickname;
    RelativeLayout rl_recent_message;
    protected AppMessage lastAppMessage;

    public AppMessageRecentView(Context context) {
        super(context);
        init(context);
    }

    public AppMessageRecentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AppMessageRecentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.nim_recent_contact_list_item, null);
        inflate();
        addView(view);
    }

    public void updateLastAppMessageUI() {
        lastAppMessage = AppMsgDBHelper.getLastAppMessage(context);
        if (lastAppMessage != null) {
            refresh(lastAppMessage);
            setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
        }
    }

    public void inflate() {
        this.portraitPanel = (FrameLayout) view.findViewById(com.netease.nim.uikit.R.id.portrait_panel);
        this.imgHead = (HeadImageView) view.findViewById(com.netease.nim.uikit.R.id.img_head);
        this.tvNickname = (TextView) view.findViewById(com.netease.nim.uikit.R.id.tv_nickname);
        this.tvMessage = (TextView) view.findViewById(com.netease.nim.uikit.R.id.tv_message);
        this.tvUnread = (TextView) view.findViewById(com.netease.nim.uikit.R.id.unread_number_tip);
        this.unreadIndicator = view.findViewById(com.netease.nim.uikit.R.id.new_message_indicator);
        this.tvDatetime = (TextView) view.findViewById(com.netease.nim.uikit.R.id.tv_date_time);
        this.imgMsgStatus = (ImageView) view.findViewById(com.netease.nim.uikit.R.id.img_msg_status);
        //
        this.rl_club_head = (RelativeLayout) view.findViewById(com.netease.nim.uikit.R.id.rl_club_head);
        this.iv_club_head = (HeadImageView) view.findViewById(com.netease.nim.uikit.R.id.iv_club_head);
        this.iv_recent_new_notify = (ImageView) view.findViewById(com.netease.nim.uikit.R.id.iv_recent_new_notify);//消息免打扰的小红点
        this.imgGameStatus = (ImageView) view.findViewById(com.netease.nim.uikit.R.id.iv_game_status);//
        this.iv_icon_game_quick = (ImageView) view.findViewById(com.netease.nim.uikit.R.id.iv_icon_game_quick);//
        this.tv_game_count = (TextView) view.findViewById(com.netease.nim.uikit.R.id.tv_game_count);//
        this.iv_mute = (ImageView) view.findViewById(com.netease.nim.uikit.R.id.iv_mute);//
        this.mGroupHeadView = (GroupHeadView) view.findViewById(com.netease.nim.uikit.R.id.mGroupHeadView);//
        this.ll_recent_nickname = (LinearLayout) view.findViewById(com.netease.nim.uikit.R.id.ll_recent_nickname);//
        this.rl_recent_message = (RelativeLayout) view.findViewById(com.netease.nim.uikit.R.id.rl_recent_message);//
        //
        this.unreadIndicator.setVisibility(GONE);
        this.imgMsgStatus.setVisibility(GONE);
        this.rl_club_head.setVisibility(GONE);
        this.iv_recent_new_notify.setVisibility(GONE);
        this.imgGameStatus.setVisibility(GONE);
        this.iv_icon_game_quick.setVisibility(GONE);
        this.tv_game_count.setVisibility(GONE);
        this.iv_mute.setVisibility(GONE);
        this.mGroupHeadView.setVisibility(GONE);
//        this.ll_recent_nickname.setVisibility(GONE);
//        this.rl_recent_message.setVisibility(GONE);
    }

    public void refresh(AppMessage appMessage) {
        lastAppMessage = appMessage;
//        this.imgHead.setImageResource(R.mipmap.icon_app_message);
        this.tvMessage.setText(AppMessageHelper.getAppMessageContent(context, appMessage));
        this.tvDatetime.setText(DateTools.getDailyTime_ymd(lastAppMessage.time));
    }

    public void nullData(int num) {
        if (num == 0) {
            this.tvMessage.setText("");
            this.tvDatetime.setText("");
        }
    }

    public void refreshClubInfo(SystemMessage systemMessage) {
        if (systemMessage.custom_outer_type == 0) {//云信通知
            this.tvMessage.setText(MessageHelper.getVerifyNotificationText(systemMessage));
        } else if (systemMessage.custom_outer_type == CustomNotificationConstants.NOTIFICATION_TYPE_HORDE) {
            if (systemMessage.custom_inner_type == HordeMessageType.HORDE_MESSAGE_TYPE_APPLY) {//-------------------------------------------------------------------------申请加入部落
                String nickname = "“" + NimUserInfoCache.getInstance().getUserDisplayName(systemMessage.fromAccount) + "”";
                this.tvMessage.setText(nickname + "请求加入部落" + "“" + systemMessage.horde_name + "”");
            }
        }

        this.tvDatetime.setText(DateTools.getDailyTime_ymd(systemMessage.getTime()));
    }

    public void setTitle(int stringId) {
        this.tvNickname.setText(stringId);
    }

    public void setBigIcon(int resId) {
        this.imgHead.setImageResource(resId);
    }

    /**
     * 更新未读数量
     */
    public void updateNewIndicator(int unreadNum) {
        iv_recent_new_notify.setVisibility(unreadNum == 2147483647 ? View.VISIBLE : View.GONE);
        tvUnread.setVisibility(unreadNum > 0 ? View.VISIBLE : View.GONE);
        tvUnread.setText(unreadCountShowRule(unreadNum));
        if (unreadNum > 99) {
            tvUnread.setText(com.netease.nim.uikit.R.string.new_message_count_max);
        }
    }

    public static String unreadCountShowRule(int unread) {
        unread = Math.min(unread, 99);
        return "" + unread;
    }
}
