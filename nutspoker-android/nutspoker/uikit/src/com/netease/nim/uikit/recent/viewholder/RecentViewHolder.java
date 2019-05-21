package com.netease.nim.uikit.recent.viewholder;

import android.graphics.Color;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.chesscircle.DealerConstant;
import com.netease.nim.uikit.chesscircle.TeamGameStatusCache;
import com.netease.nim.uikit.chesscircle.helper.RecentContactHelp;
import com.netease.nim.uikit.chesscircle.view.GroupHeadView;
import com.netease.nim.uikit.common.adapter.TViewHolder;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.common.util.sys.TimeUtil;
import com.netease.nim.uikit.recent.RecentContactsCallback;
import com.netease.nim.uikit.session.emoji.MoonUtil;
import com.netease.nim.uikit.team.helper.TeamHelper;
import com.netease.nim.uikit.uinfo.UserInfoHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class RecentViewHolder extends TViewHolder implements OnClickListener {
    public final static String KEY_STATUS_TYPE = "status";//牌局状态
    public final static String KEY_GAME_TYPE = "type";//牌局群组类型（0：俱乐部 ， 1：游戏聊天室）

    public final static int GAME_STATUES_WAIT = 0;//游戏状态:等待
    public final static int GAME_STATUES_START = 1;//游戏状态:开始
    public final static int GAME_STATUES_FINISH = 2;//游戏状态:结束

    public final static int GAME_TYPE_CLUB = 0;//牌局类别：俱乐部，圈子
    public final static int GAME_TYPE_GAME = 1;//牌局类别：私人
    protected FrameLayout portraitPanel;

    protected HeadImageView imgHead;

    protected TextView tvNickname;

    protected TextView tvMessage;

    protected TextView tvUnread;

    protected View unreadIndicator;

    protected TextView tvDatetime;

    // 消息发送错误状态标记，目前没有逻辑处理
    protected ImageView imgMsgStatus;

    protected RecentContact recent;
    //快速游戏的状态
    protected ImageView iv_recent_new_notify;
    protected ImageView imgGameStatus;
    protected ImageView iv_icon_game_quick;
    private RelativeLayout rl_club_head;
    private View rl_club_head_bg;
    protected HeadImageView iv_club_head;
    TextView tv_game_count;
    ImageView iv_mute;
    //圈子头像
    GroupHeadView mGroupHeadView;
    boolean isSelectMode = false;
    LinearLayout ll_recent_nickname;
    RelativeLayout rl_recent_message;

    protected abstract String getContent();

    public void refresh(Object item) {
        recent = (RecentContact) item;

        updateBackground();

        loadPortrait();

        if(!isSelectMode){
            updateNewIndicator();
        }
        iv_mute.setVisibility(View.GONE);

        if(recent.getSessionType() == SessionTypeEnum.P2P){
            updateNickLabel(UserInfoHelper.getUserTitleName(recent.getContactId(), recent.getSessionType()));
            //判断是否静音
            if(NIMClient.getService(FriendService.class).isNeedMessageNotify(recent.getContactId())){
                //需要消息通知
                iv_mute.setVisibility(View.GONE);
            }else{
                iv_mute.setVisibility(View.VISIBLE);
            }
        } else{
            Team team = TeamDataCache.getInstance().getTeamById(recent.getContactId());
            if(team != null){
                //判断是否静音
                if(!team.mute()){
                    //需要消息通知
                    iv_mute.setVisibility(View.GONE);
                }else{
                    iv_mute.setVisibility(View.VISIBLE);
                }

                if(!TextUtils.isEmpty(team.getName()) && !ClubConstant.GROUP_IOS_DEFAULT_NAME.equals(team.getName())){
                    tvNickname.setText(team.getName());
                } else{
                    tvNickname.setText(TeamHelper.getTeamNameByMember(recent.getContactId()));
                }
            }else{
                tvNickname.setText(TeamDataCache.getInstance().getTeamName(recent.getContactId()));
            }
        }

        if(!isSelectMode){
            updateMsgLabel();
            //刷新游戏状态
            updateGameStatus();
        } else {
            iv_recent_new_notify.setVisibility(View.GONE);
            imgGameStatus.setVisibility(View.GONE);
            iv_icon_game_quick.setVisibility(View.GONE);
            tv_game_count.setVisibility(View.GONE);
            iv_mute.setVisibility(View.GONE);
            //
            imgMsgStatus.setVisibility(View.GONE);
            tvMessage.setVisibility(View.GONE);
            tvUnread.setVisibility(View.GONE);
            unreadIndicator.setVisibility(View.GONE);
            tvDatetime.setVisibility(View.GONE);
            rl_recent_message.setVisibility(View.GONE);
        }

        if(recent.getSessionType() == SessionTypeEnum.P2P && DealerConstant.isDealer(recent.getContactId())){
            tvNickname.setTextColor(context.getResources().getColor(R.color.head_title_color));
            if(!NimUserInfoCache.getInstance().hasUser(recent.getContactId())){
                tvNickname.setText(DealerConstant.getDealerNickname(recent.getContactId()));
            }
        } else{
            tvNickname.setTextColor(Color.WHITE);
        }
    }

    /**
     * 设置选择模式：不显示时间，内容，等
     */
    public void setSelectMode(boolean isSelectMode){
        this.isSelectMode = isSelectMode;
    }

    public void refreshCurrentItem() {
        if (recent != null) {
            refresh(recent);
        }
    }

    private void updateBackground() {
//        topLine.setVisibility(isFirstItem() ? View.GONE : View.VISIBLE);
//        bottomLine.setVisibility(isLastItem() ? View.VISIBLE : View.GONE);
        if ((recent.getTag() & 1/*RecentContactsFragment.RECENT_TAG_STICKY*/) == 0) {
//            vi|ew.setBackgroundResource(R.drawable.nim_list_item_selector);
            view.setBackgroundResource(R.drawable.list_item_bg);
        } else {
//            view.setBackgroundResource(R.drawable.nim_recent_contact_sticky_selecter);
            //置顶
            view.setBackgroundResource(R.drawable.list_item_top_bg);
        }
    }

    protected void loadPortrait() {
        // 设置头像
        if (recent.getSessionType() == SessionTypeEnum.P2P) {
            imgHead.loadBuddyAvatar(recent.getContactId());
        } else if (recent.getSessionType() == SessionTypeEnum.Team) {
//            imgHead.loadTeamIcon(recent.getContactId());
            //云信自带，2.4.0加入
//            Team team = TeamDataCache.getInstance().getTeamById(recent.getContactId());
//            imgHead.loadTeamIconByTeam(team);
            //区分圈子和俱乐部样式
            if(TeamDataCache.getInstance().getTeamById(recent.getContactId()) != null){
                Team team = TeamDataCache.getInstance().getTeamById(recent.getContactId());
                if(team.getType() == TeamTypeEnum.Advanced){
                    imgHead.setVisibility(View.GONE);
                    rl_club_head.setVisibility(View.VISIBLE);
                    if (ClubConstant.isSuperClub(team)) {
                        //超级俱乐部
                        this.rl_club_head_bg.setBackgroundResource(R.drawable.default_club_super_head_bg);
                    } else {
                        this.rl_club_head_bg.setBackgroundResource(R.drawable.default_club_head_bg);
                    }
                    //加载俱乐部头像
                    iv_club_head.loadClubAvatarByUrl(team.getId() , ClubConstant.getClubExtAvatar(team.getExtServer()), HeadImageView.DEFAULT_AVATAR_THUMB_SIZE);
                    mGroupHeadView.setVisibility(View.GONE);
                }else{
                    //圈子
                    imgHead.setVisibility(View.GONE);
                    rl_club_head.setVisibility(View.GONE);
//                    imgHead.setVisibility(View.VISIBLE);
//                    imgHead.loadTeamIcon(recent.getContactId());
                    mGroupHeadView.setVisibility(View.VISIBLE);
                    mGroupHeadView.setGroupId(recent.getContactId());
                }
            } else{
                mGroupHeadView.setVisibility(View.GONE);
                imgHead.setVisibility(View.VISIBLE);
                imgHead.loadTeamIcon(recent.getContactId());
            }
        }
    }

    /**
     * 更新未读数量
     */
    private void updateNewIndicator() {
        int unreadNum = recent.getUnreadCount();
        tvUnread.setVisibility(unreadNum > 0 ? View.VISIBLE : View.GONE);
        tvUnread.setText(unreadCountShowRule(unreadNum));
        iv_recent_new_notify.setVisibility(View.GONE);
        if(unreadNum > 99){
            tvUnread.setText(R.string.new_message_count_max);
        }

        if(RecentContactHelp.isRecentMute(recent)){
            //群免消息打扰
            if(unreadNum > 0){
                //有新消息
                tvUnread.setVisibility(View.GONE);
                iv_recent_new_notify.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateMsgLabel() {
        // 显示消息具体内容
        MoonUtil.identifyFaceExpressionAndTags(context, tvMessage, getContent(), ImageSpan.ALIGN_BOTTOM, 0.45f);
        //tvMessage.setText(getContent());

        MsgStatusEnum status = recent.getMsgStatus();
        switch (status) {
            case fail:
                imgMsgStatus.setImageResource(R.drawable.nim_g_ic_failed_small);
                imgMsgStatus.setVisibility(View.VISIBLE);
                break;
            case sending:
                imgMsgStatus.setImageResource(R.drawable.nim_recent_contact_ic_sending);
                imgMsgStatus.setVisibility(View.VISIBLE);
                break;
            default:
                imgMsgStatus.setVisibility(View.GONE);
                break;
        }

        String timeString = TimeUtil.getTimeShowString(recent.getTime(), true);
        tvDatetime.setText(timeString);
        if (!TextUtils.isEmpty(timeString) && timeString.equals("1970-01-01")) {
            tvDatetime.setVisibility(View.INVISIBLE);
        } else {
            tvDatetime.setVisibility(View.VISIBLE);
        }
    }

    protected void updateNickLabel(String nick) {
        int labelWidth = ScreenUtil.screenWidth;
        labelWidth -= ScreenUtil.dip2px(50 + 70); // 减去固定的头像和时间宽度

        if (labelWidth > 0) {
            tvNickname.setMaxWidth(labelWidth);
        }

        tvNickname.setText(nick);
    }

    protected int getResId() {
        return R.layout.nim_recent_contact_list_item;
    }

    public void inflate() {
        this.portraitPanel = (FrameLayout) view.findViewById(R.id.portrait_panel);
        this.imgHead = (HeadImageView) view.findViewById(R.id.img_head);
        this.tvNickname = (TextView) view.findViewById(R.id.tv_nickname);
        this.tvMessage = (TextView) view.findViewById(R.id.tv_message);
        this.tvUnread = (TextView) view.findViewById(R.id.unread_number_tip);
        this.unreadIndicator = view.findViewById(R.id.new_message_indicator);
        this.tvDatetime = (TextView) view.findViewById(R.id.tv_date_time);
        this.imgMsgStatus = (ImageView) view.findViewById(R.id.img_msg_status);
//        this.bottomLine = view.findViewById(R.id.bottom_line);
//        this.topLine = view.findViewById(R.id.top_line);
        //
        this.rl_club_head = (RelativeLayout) view.findViewById(R.id.rl_club_head);
        this.rl_club_head_bg = view.findViewById(R.id.rl_club_head_bg);
        this.iv_club_head = (HeadImageView) view.findViewById(R.id.iv_club_head);
        this.iv_recent_new_notify = (ImageView) view.findViewById(R.id.iv_recent_new_notify);//消息免打扰的小红点
        this.imgGameStatus = (ImageView) view.findViewById(R.id.iv_game_status);//
        this.iv_icon_game_quick = (ImageView) view.findViewById(R.id.iv_icon_game_quick);//
        this.tv_game_count = (TextView) view.findViewById(R.id.tv_game_count);//
        this.iv_mute = (ImageView) view.findViewById(R.id.iv_mute);//
        this.mGroupHeadView = (GroupHeadView) view.findViewById(R.id.mGroupHeadView);//
        this.ll_recent_nickname = (LinearLayout) view.findViewById(R.id.ll_recent_nickname);//
        this.rl_recent_message = (RelativeLayout) view.findViewById(R.id.rl_recent_message);//
    }

    private void updateGameStatus() {
        this.tv_game_count.setVisibility(View.GONE);
        if (recent.getSessionType() != SessionTypeEnum.Team) {
            this.rl_club_head.setVisibility(View.GONE);
            this.imgHead.setVisibility(View.VISIBLE);
            this.mGroupHeadView.setVisibility(View.GONE);
            this.imgGameStatus.setVisibility(View.GONE);
            this.tvDatetime.setVisibility(View.VISIBLE);
            this.tv_game_count.setVisibility(View.GONE);
            return;
        }
        //判断是不是游戏
        String teamId = recent.getContactId();
        updataGameCount(teamId);
        Team team = TeamDataCache.getInstance().getTeamById(teamId);
        if(team == null || team.getType() == TeamTypeEnum.Normal){
            LogUtil.d("TeamDataCache", "不在缓存中！teamId：" + teamId);
            this.rl_club_head.setVisibility(View.GONE);
//            this.imgHead.setVisibility(View.VISIBLE);
            this.imgGameStatus.setVisibility(View.GONE);
            this.iv_icon_game_quick.setVisibility(View.GONE);
            this.tvDatetime.setVisibility(View.VISIBLE);
            return;
        }
        LogUtil.d("TeamDataCache" , "在缓存中！teamId：" + teamId);
        String extServer = TeamDataCache.getInstance().getTeamById(teamId).getExtServer();
        LogUtil.d("TeamDataCache", "extServer : " + extServer);
        int gameType = GAME_TYPE_CLUB;
        int gameStatus = GAME_STATUES_WAIT;
        if (!TextUtils.isEmpty(extServer)) {
            try {
                JSONObject extensionJson = new JSONObject(extServer);
                gameType = extensionJson.optInt(KEY_GAME_TYPE);
                gameStatus = extensionJson.optInt(KEY_STATUS_TYPE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (gameType == GAME_TYPE_GAME) {
                this.rl_club_head.setVisibility(View.GONE);
                this.imgHead.setVisibility(View.VISIBLE);
                this.imgGameStatus.setVisibility(View.VISIBLE);
                this.iv_icon_game_quick.setVisibility(View.VISIBLE);
                imgHead.loadBuddyAvatar(TeamDataCache.getInstance().getTeamById(recent.getContactId()).getCreator());//设置头像
                this.tvDatetime.setVisibility(View.INVISIBLE);
                switch (gameStatus) {
                    case GAME_STATUES_WAIT:
                        this.imgGameStatus.setImageResource(R.drawable.quick_no_start);
                        break;
                    case GAME_STATUES_START:
                        this.imgGameStatus.setImageResource(R.drawable.quick_start);
                        break;
                    case GAME_STATUES_FINISH:
                        this.imgGameStatus.setImageResource(R.drawable.quick_finish);
                        break;
                }
            } else {
                this.rl_club_head.setVisibility(View.VISIBLE);
                if (ClubConstant.isSuperClub(team)) {
                    //超级俱乐部
                    this.rl_club_head_bg.setBackgroundResource(R.drawable.default_club_super_head_bg);
                } else {
                    this.rl_club_head_bg.setBackgroundResource(R.drawable.default_club_head_bg);
                }
                this.imgHead.setVisibility(View.GONE);
                this.imgGameStatus.setVisibility(View.GONE);
                this.iv_icon_game_quick.setVisibility(View.GONE);
                this.tvDatetime.setVisibility(View.VISIBLE);
                updataGameCount(teamId);
            }
        } else {
            this.rl_club_head.setVisibility(View.VISIBLE);
            //超级俱乐部
            if (ClubConstant.isSuperClub(team)) {
                this.rl_club_head_bg.setBackgroundResource(R.drawable.default_club_super_head_bg);
            } else {
                this.rl_club_head_bg.setBackgroundResource(R.drawable.default_club_head_bg);
            }
            this.imgHead.setVisibility(View.GONE);
            this.imgGameStatus.setVisibility(View.GONE);
            this.iv_icon_game_quick.setVisibility(View.GONE);
            this.tvDatetime.setVisibility(View.VISIBLE);
            updataGameCount(teamId);
        }
    }

    /**
     * 刷新群组中进行中的牌局数量UI
     * @param teamId
     */
    public void updataGameCount(String teamId){
        int gameCount = TeamGameStatusCache.getTeamGameCount(teamId);
        if(gameCount == 0){
            this.tv_game_count.setVisibility(View.GONE);
        }else{
            this.tv_game_count.setVisibility(View.VISIBLE);
            this.tv_game_count.setText(String.format(context.getString(R.string.game_ing_count), gameCount));
        }
        LogUtil.d("count" , "teamId : " + teamId + ";count:" + gameCount);
    }

    protected String unreadCountShowRule(int unread) {
        unread = Math.min(unread, 99);
        return String.valueOf(unread);
    }

    protected RecentContactsCallback getCallback() {
        return ((RecentContactAdapter)getAdapter()).getCallback();
    }

    @Override
    public void onClick(View v) {

    }
}
