package com.htgames.nutspoker.chat.msg.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.app_msg.helper.HordeMessageHelper;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageStatus;
import com.htgames.nutspoker.chat.helper.MessageHelper;
import com.htgames.nutspoker.chat.msg.model.HordeMessageType;
import com.htgames.nutspoker.chat.msg.model.SystemMessage;
import com.htgames.nutspoker.chat.notification.constant.CustomNotificationConstants;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.htgames.nutspoker.ui.adapter.ListBaseAdapter;
import com.htgames.nutspoker.ui.interfs.RejectInterface;
import com.htgames.nutspoker.view.widget.FocusTextVuew;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.common.util.sys.TimeUtil;
import com.netease.nimlib.sdk.msg.constant.SystemMessageStatus;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;

import java.util.ArrayList;

public class SystemMessageAdapter extends ListBaseAdapter<SystemMessage> {
    private final static String TAG = "SystemMessageAdapter";

    public SystemMessageAdapter(Context context, ArrayList<SystemMessage> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_system_message_item, null);
            holder.headImageView = (HeadImageView) view.findViewById(R.id.from_account_head_image);
            holder.fromAccountText = (TextView) view.findViewById(R.id.from_account_text);
            holder.contentText = (FocusTextVuew) view.findViewById(R.id.content_text);
            holder.remark = (TextView) view.findViewById(R.id.tv_remark);
            holder.timeText = (TextView) view.findViewById(R.id.notification_time);
            holder.operatorLayout = view.findViewById(R.id.operator_layout);
            holder.agreeButton = (TextView) view.findViewById(R.id.btn_action_agree);
            holder.rejectButton = (TextView) view.findViewById(R.id.btn_action_reject);
            holder.operatorResultText = (TextView) view.findViewById(R.id.operator_result);
            holder.btn_action_apply_again = (TextView) view.findViewById(R.id.btn_action_apply_again);
            holder.iv_club_head_mask = (ImageView) view.findViewById(R.id.iv_club_head_mask);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final SystemMessage message = (SystemMessage)getItem(position);
        if (message == null) {
            return view;
        }
        String fromAccount = message.getFromAccount();
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    listener.onLongPressed(message);
                }
                return true;
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(message);
                }
            }
        });
        holder.headImageView.loadBuddyAvatar(fromAccount);
        if(NimUserInfoCache.getInstance().hasUser(fromAccount)){
            holder.fromAccountText.setText(NimUserInfoCache.getInstance().getUserDisplayNameEx(fromAccount));
        }
        if(message.getContent().isEmpty() || message.getType() == SystemMessageType.TeamInvite)
            holder.remark.setVisibility(View.GONE);//邀请加入俱乐部content是乱码   擦 不显示了
        else {
            holder.remark.setVisibility(View.VISIBLE);
            holder.remark.setText(message.getContent());//邀请加入俱乐部content是乱码
        }
        holder.contentText.setText(MessageHelper.getVerifyNotificationText(message));
        holder.timeText.setText(TimeUtil.getTimeShowString(message.getTime(), false));
        holder.timeText.setVisibility(View.GONE);
        LogUtil.i(TAG, message.getFromAccount() + ":" + message.getStatus());
        if (!MessageHelper.isVerifyMessageNeedDeal(message)) {
            holder.operatorLayout.setVisibility(View.GONE);
            if (message.getType() == SystemMessageType.RejectTeamApply) {
                holder.btn_action_apply_again.setVisibility(View.VISIBLE);
            }
        } else {
            if (message.getStatus() == SystemMessageStatus.init) {
                // 未处理
                holder.operatorResultText.setVisibility(View.GONE);
                holder.operatorLayout.setVisibility(View.VISIBLE);
                holder.agreeButton.setVisibility(View.VISIBLE);
                holder.rejectButton.setVisibility(View.VISIBLE);
            } else {//已经处理
                // 处理结果
                holder.agreeButton.setVisibility(View.GONE);
                holder.rejectButton.setVisibility(View.GONE);
                holder.operatorLayout.setVisibility(View.VISIBLE);
                holder.operatorResultText.setVisibility(View.VISIBLE);
                holder.operatorResultText.setText(MessageHelper.getVerifyNotificationDealResult(message));
            }
        }
        holder.agreeButton.setOnClickListener(new OnAgreeClick(position , holder));
        holder.rejectButton.setOnClickListener(new OnRejectClick(position, holder));
        holder.btn_action_apply_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(message);
                }
            }
        });
        holder.iv_club_head_mask.setVisibility(View.GONE);
        if (message.custom_outer_type == CustomNotificationConstants.NOTIFICATION_TYPE_HORDE) {//-------------------------------------------------------------------------部落相关消息
            if (message.custom_inner_type == HordeMessageType.HORDE_MESSAGE_TYPE_APPLY) {//-------------------------------------------------------------------------申请加入部落
                holder.iv_club_head_mask.setVisibility(View.VISIBLE);
                String content = message.content;
                if (StringUtil.isSpace(content) || "null".equals(content)) {
                    String nickname = NimUserInfoCache.getInstance().getUserDisplayName(message.fromAccount);
                    content = String.format(getString(R.string.verify_send_message), nickname);
                }
                holder.headImageView.loadClubAvatarByUrl(message.tid, /*ClubConstant.getClubExtAvatar(extServer)*/message.tavatar, HeadImageView.DEFAULT_AVATAR_THUMB_SIZE);
                holder.fromAccountText.setText(message.tname);
                holder.contentText.setText("申请加入" + "“" + message.horde_name + "”");
                holder.remark.setText(content);
                if (message.horde_status == AppMessageStatus.init.getValue()) {
                    // 未处理
                    holder.operatorResultText.setVisibility(View.GONE);
                    holder.operatorLayout.setVisibility(View.VISIBLE);
                    holder.agreeButton.setVisibility(View.VISIBLE);
                    holder.rejectButton.setVisibility(View.VISIBLE);
                } else {//已经处理
                    // 处理结果
                    holder.agreeButton.setVisibility(View.GONE);
                    holder.rejectButton.setVisibility(View.GONE);
                    holder.operatorLayout.setVisibility(View.VISIBLE);
                    holder.operatorResultText.setVisibility(View.VISIBLE);
                    holder.operatorResultText.setText(HordeMessageHelper.getVerifyNotificationDealResult(message));
                }
            }
        }
        return view;
    }

    public void refreshDirectly(Object tag ,final SystemMessage message) {
        if (message != null) {
            ViewHolder holder = (ViewHolder) tag;
            SystemMessageType type = message.getType();
            if(type == SystemMessageType.AddFriend){

            }
            holder.headImageView.loadBuddyAvatar(message.getFromAccount());
            if(NimUserInfoCache.getInstance().hasUser(message.getFromAccount())){
                holder.fromAccountText.setText(NimUserInfoCache.getInstance().getUserDisplayNameEx(message.getFromAccount()));
            }
            holder.contentText.setText(MessageHelper.getVerifyNotificationText(message));
            holder.timeText.setText(TimeUtil.getTimeShowString(message.getTime(), false));
            if (!MessageHelper.isVerifyMessageNeedDeal(message)) {
                holder.operatorLayout.setVisibility(View.GONE);
                if (message.getType() == SystemMessageType.RejectTeamApply) {
//                    holder.btn_action_apply_again.setVisibility(View.VISIBLE);// TODO: 16/12/6暂时不做
                }
            } else {
                if (message.getStatus() == SystemMessageStatus.init) {
                    // 未处理,已读
                    holder.operatorResultText.setVisibility(View.GONE);
                    holder.agreeButton.setVisibility(View.VISIBLE);
//                    holder.rejectButton.setVisibility(View.VISIBLE);
                    holder.rejectButton.setVisibility(View.GONE);
                } else {
                    // 处理结果
                    holder.agreeButton.setVisibility(View.GONE);
                    holder.rejectButton.setVisibility(View.GONE);
                    holder.operatorResultText.setVisibility(View.VISIBLE);
                    holder.operatorResultText.setText(MessageHelper.getVerifyNotificationDealResult(message));
                }
            }
        }
    }

    public void refreshHordeJoin(Object tag ,final SystemMessage message) {
        if (message == null) {
            return;
        }
        ViewHolder holder = (ViewHolder) tag;
        if (message.horde_status == AppMessageStatus.init.getValue()) {
            // 未处理,已读
            holder.operatorResultText.setVisibility(View.GONE);
            holder.agreeButton.setVisibility(View.VISIBLE);
            holder.rejectButton.setVisibility(View.VISIBLE);
        } else {
            // 处理结果
            holder.agreeButton.setVisibility(View.GONE);
            holder.rejectButton.setVisibility(View.GONE);
            holder.operatorResultText.setVisibility(View.VISIBLE);
            holder.operatorResultText.setText(HordeMessageHelper.getVerifyNotificationDealResult(message));
        }
    }

    private SystemMessageListener listener;

    public void setSystemMessageListener(SystemMessageListener listener){
        this.listener = listener;
    }

    public class OnAgreeClick implements View.OnClickListener{
        int position;
        ViewHolder holder;

        public OnAgreeClick(int position , ViewHolder holder) {
            this.position = position;
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            SystemMessage message = list.get(position);
            if (message.custom_outer_type == 0) {
                if(message.getType() == SystemMessageType.ApplyJoinTeam){
                    //申请加入
                    if(ClubConstant.isClubMemberIsFull(message.getTargetId())){
                        Toast.makeText(context, R.string.club_invite_member_count_limit, Toast.LENGTH_SHORT).show();
                    }else{
                        setReplySending(holder);
                        if(listener != null){
                            listener.onAgree(message);
                        }
                    }
                } else if(message.getType() == SystemMessageType.TeamInvite) {
                    //俱乐部邀请
                    setReplySending(holder);
                    if (listener != null) {
                        listener.onAgree(message);
                    }
                } else {
                    setReplySending(holder);
                    if(listener != null){
                        listener.onAgree(message);
                    }
                }
            } else if (message.custom_outer_type == CustomNotificationConstants.NOTIFICATION_TYPE_HORDE) {
                if (message.custom_inner_type == HordeMessageType.HORDE_MESSAGE_TYPE_APPLY) {//-------------------------------------------------------------------------申请加入部落
                    setReplySending(holder);
                    if(listener != null){
                        listener.onAgree(message);
                    }
                }
            }
        }
    }

    public class OnRejectClick implements View.OnClickListener,RejectInterface {
        int position;
        ViewHolder holder;

        public OnRejectClick(int position , ViewHolder holder) {
            this.position = position;
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            //setReplySending(holder);//点击拒绝还有第二步操作
            SystemMessage message = list.get(position);
            if(listener != null){
                listener.onReject(message,this);
            }
        }

        @Override
        public void onChangeStatus(boolean change) {
            if(change)
                setReplySending(holder);
        }
    }

    public class OnApplyAgainClick implements View.OnClickListener {
        int position;
        ViewHolder holder;

        public OnApplyAgainClick(int position , ViewHolder holder) {
            this.position = position;
            this.holder = holder;
        }
        @Override
        public void onClick(View v) {
            android.widget.Toast.makeText(context, "todo再次申请加入俱乐部", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 等待服务器返回状态设置
     */
    private void setReplySending(ViewHolder holder) {
        holder.agreeButton.setVisibility(View.GONE);
        holder.rejectButton.setVisibility(View.GONE);
        holder.operatorResultText.setVisibility(View.VISIBLE);
        holder.operatorResultText.setText(R.string.team_apply_sending);
    }

    public final class ViewHolder {
        private HeadImageView headImageView;
        private TextView fromAccountText;
        private TextView timeText;
        private FocusTextVuew contentText;
        TextView remark;
        private View operatorLayout;
        private TextView agreeButton;
        private TextView rejectButton;
        private TextView operatorResultText;
        public TextView btn_action_apply_again;//再次申请按钮
        public ImageView iv_club_head_mask;
    }

    /**
     * 消息监听接口
     */
    public interface SystemMessageListener {
        void onAgree(SystemMessage message);
        void onReject(SystemMessage message,RejectInterface listener);
        void onClick(SystemMessage message);
        void onLongPressed(SystemMessage message);
    }
}
