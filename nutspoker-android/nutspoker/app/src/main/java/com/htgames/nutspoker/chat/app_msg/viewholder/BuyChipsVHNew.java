package com.htgames.nutspoker.chat.app_msg.viewholder;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.app_msg.attach.BuyChipsNotify;
import com.htgames.nutspoker.chat.app_msg.attach.MatchBuyChipsNotify;
import com.htgames.nutspoker.chat.app_msg.helper.AppMessageHelper;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageStatus;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.db.AppMsgDBHelper;
import com.htgames.nutspoker.game.match.interfaces.IMsgExpired;
import com.htgames.nutspoker.game.model.GameMatchBuyType;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.common.util.sys.TimeUtil;

/**
 * Created by 周智慧 on 16/12/5.
 */

public class BuyChipsVHNew extends RecyclerView.ViewHolder {
    public static int FROM_TYPE_MATCH_ROOM = 0;//在mtt比赛大厅收到  这里要显示头像
    public static int FROM_TYPE_MATCH_CONTROL_CENTER = 1;//在控制中心收到
    private final static String TAG = "BuyChipsVHNew";
    TextView tv_message_time;
    View ll_buychips_head;
    TextView tv_game_name;
    ImageView iv_buychips_game_mode;
    ImageView iv_buychips_game_sign;
    TextView tv_game_member;
    TextView tv_app_message_from_nickname;
    TextView tv_buychips_weekout;
    TextView tv_rebuy_cnt;
    LinearLayout operator_layout;
    TextView iv_action_agree;
    TextView iv_action_reject;
    TextView operator_result;
    TextView tv_app_message_chips;
    View buy_chips_can_longpress_content;
    String rejectStr = "";
    TextView tv_uuid;
    public CountDownTimer mCountDownTimers;


    public static BuyChipsVHNew createViewHolder(Context context) {
        return new BuyChipsVHNew(context, LayoutInflater.from(context).inflate(R.layout.viewholder_app_buychips_new, null));
    }

    public BuyChipsVHNew(Context context, View itemView) {
        super(itemView);
        tv_message_time = (TextView) itemView.findViewById(R.id.tv_message_time);
        tv_game_name = (TextView) itemView.findViewById(R.id.tv_game_name);
        tv_game_member = (TextView) itemView.findViewById(R.id.tv_game_member);
        tv_app_message_from_nickname = (TextView) itemView.findViewById(R.id.tv_app_message_from_nickname);
        tv_buychips_weekout = (TextView) itemView.findViewById(R.id.tv_buychips_weekout);
        tv_rebuy_cnt = (TextView) itemView.findViewById(R.id.tv_rebuy_cnt);
        operator_layout = (LinearLayout) itemView.findViewById(R.id.operator_layout);
        iv_action_agree = (TextView) itemView.findViewById(R.id.iv_action_agree);
        iv_action_reject = (TextView) itemView.findViewById(R.id.iv_action_reject);
        iv_buychips_game_mode = (ImageView) itemView.findViewById(R.id.iv_buychips_game_mode);
        iv_buychips_game_sign = (ImageView) itemView.findViewById(R.id.iv_buychips_game_sign);
        ll_buychips_head = itemView.findViewById(R.id.ll_buychips_head);
        operator_result = (TextView) itemView.findViewById(R.id.operator_result);
        tv_app_message_chips = (TextView) itemView.findViewById(R.id.tv_app_message_chips);
        buy_chips_can_longpress_content = itemView.findViewById(R.id.buy_chips_can_longpress_content);
        tv_uuid = (TextView) itemView.findViewById(R.id.tv_uuid);
        rejectStr = context.getResources().getString(R.string.reject);
    }

    public void refresh(Context context, AppMessage message, boolean showTime) {
        tv_message_time.setText(TimeUtil.getTimeShowString(message.time * 1000L, false));
        if (showTime) {
            tv_message_time.setVisibility(View.VISIBLE);
        } else {
            tv_message_time.setVisibility(View.GONE);
        }
        String fromAccount = message.fromId;
        if (!TextUtils.isEmpty(message.targetId)) {
            fromAccount = message.targetId;
        }
        String fromNickname = "";
        int gameMode = GameConstants.GAME_MODE_NORMAL;
        tv_app_message_chips.setVisibility(View.GONE);
        tv_uuid.setVisibility(View.GONE);
        if (message.attachObject instanceof BuyChipsNotify) {
            tv_rebuy_cnt.setVisibility(View.GONE);
            BuyChipsNotify buyChipsNotify = (BuyChipsNotify) message.attachObject;
            fromNickname = buyChipsNotify.userNickname;
            gameMode = buyChipsNotify.gameMode;
            tv_uuid.setVisibility(StringUtil.isSpace(buyChipsNotify.uuid) ? View.GONE : View.VISIBLE);
            tv_uuid.setText("ID: " + buyChipsNotify.uuid);
            tv_buychips_weekout.setVisibility(View.GONE);
            tv_game_name.setText(buyChipsNotify.gameName);
            if(gameMode == GameConstants.GAME_MODE_NORMAL) {
                tv_game_member.setText( "" + buyChipsNotify.gamePlayerCount + "/" + (buyChipsNotify.matchPlayer != 0 ? buyChipsNotify.matchPlayer : 9));
                String winchips = "" + buyChipsNotify.userWinChips;
                if (buyChipsNotify.userWinChips > 0) {
                    winchips = "+" + winchips;
                }
                tv_app_message_chips.setVisibility(View.VISIBLE);
                tv_app_message_chips.setText("+" + buyChipsNotify.userBuyChips);
            } else if(gameMode == GameConstants.GAME_MODE_SNG) {
                tv_game_member.setText(buyChipsNotify.checkinPlayer + "/" + buyChipsNotify.matchPlayer);
            }
        } else if (message.attachObject instanceof MatchBuyChipsNotify) {
            MatchBuyChipsNotify buyChipsNotify = (MatchBuyChipsNotify) message.attachObject;
            fromNickname = buyChipsNotify.userNickname;
            gameMode = buyChipsNotify.gameMode;
            tv_game_name.setText(buyChipsNotify.gameName);
            tv_uuid.setVisibility(StringUtil.isSpace(buyChipsNotify.uuid) ? View.GONE : View.VISIBLE);
            tv_uuid.setText("ID: " + buyChipsNotify.uuid);
            if (gameMode == GameConstants.GAME_MODE_MTT) {
                tv_game_member.setText("" + buyChipsNotify.checkinPlayer);
            } else if (gameMode == GameConstants.GAME_MODE_MT_SNG) {
                tv_game_member.setText(buyChipsNotify.checkinPlayer + "/" + buyChipsNotify.totalPlayer);
            }
            tv_buychips_weekout.setVisibility(View.GONE);
            tv_rebuy_cnt.setVisibility(View.GONE);
            int buyType = buyChipsNotify.buyType;
            int rebuyCnt = (message.status == AppMessageStatus.init) ? (buyChipsNotify.rebuyCnt + 1) : (buyChipsNotify.rebuyCnt);//玩家重构时房主和管理员会受到消息这时rebuyCnt要加1，点击同意时也会收到消息，这时rebuyCnt不加1，但是拒绝是也要加1
            if (rebuyCnt > 0) {
                if (buyType == GameMatchBuyType.TYPE_BUY_CHECKIN) {
//                tv_buychips_action_status.setText(R.string.app_message_buychips_checkin);
//                tv_buychips_action_status.setBackgroundResource(R.drawable.app_message_buychips_into_tag_bg);
                } else if (buyType == GameMatchBuyType.TYPE_BUY_REBUY) {
                    tv_rebuy_cnt.setVisibility(View.VISIBLE);
                    tv_rebuy_cnt.setText("R" + rebuyCnt);
                    tv_rebuy_cnt.setBackgroundResource(R.drawable.app_message_buychips_rebuy_tag_bg);
                } else if (buyType == GameMatchBuyType.TYPE_BUY_REBUY_WEEDOUT) {
                    tv_rebuy_cnt.setVisibility(View.VISIBLE);
                    tv_rebuy_cnt.setText("R" + rebuyCnt);
                    tv_rebuy_cnt.setBackgroundResource(R.drawable.app_message_buychips_rebuy_tag_bg);
                    tv_buychips_weekout.setVisibility(View.VISIBLE);
                } else if (buyType == GameMatchBuyType.TYPE_BUY_REBUY_REVIVAL) {
                    tv_rebuy_cnt.setVisibility(View.VISIBLE);
                    tv_rebuy_cnt.setText("R" + rebuyCnt);
                    tv_rebuy_cnt.setBackgroundResource(R.drawable.app_message_buychips_rebuy_tag_bg);
                    tv_buychips_weekout.setVisibility(View.VISIBLE);
                }
                else if (buyType == GameMatchBuyType.TYPE_BUY_ADDON) {
//                tv_addon_cnt.setVisibility(View.VISIBLE);
//                tv_addon_cnt.setText("A");
//                tv_addon_cnt.setBackgroundResource(R.drawable.app_message_buychips_addon_tag_bg);
                } else if (buyType == GameMatchBuyType.TYPE_BUY_ADDON_WEEDOUT) {
//                tv_addon_cnt.setVisibility(View.VISIBLE);
//                tv_addon_cnt.setText("A");
//                tv_addon_cnt.setBackgroundResource(R.drawable.app_message_buychips_addon_tag_bg);
                    tv_buychips_weekout.setVisibility(View.VISIBLE);
                }
            }
        }
        if (TextUtils.isEmpty(fromNickname)) {
            if (NimUserInfoCache.getInstance().hasUser(fromAccount)) {
                tv_app_message_from_nickname.setText(NimUserInfoCache.getInstance().getUserDisplayNameEx(fromAccount));
            }
        } else {
            tv_app_message_from_nickname.setText(fromNickname);
        }
        iv_buychips_game_sign.setVisibility(gameMode == GameConstants.GAME_MODE_NORMAL ? View.GONE : View.VISIBLE);
        if (gameMode == GameConstants.GAME_MODE_NORMAL) {
            iv_buychips_game_mode.setImageResource(R.mipmap.icon_control_in);
        } else if (gameMode == GameConstants.GAME_MODE_SNG) {
            iv_buychips_game_mode.setImageResource(R.mipmap.icon_control_sng);
        } else if (gameMode == GameConstants.GAME_MODE_MTT) {
            iv_buychips_game_mode.setImageResource(R.mipmap.icon_control_mtt);
        } else if (gameMode == GameConstants.GAME_MODE_MT_SNG) {
            iv_buychips_game_mode.setImageResource(R.mipmap.icon_control_mtsng);
        }
        //
        updateStatus(context, message);
    }

    public void updateStatus(Context context, final AppMessage message) {
//        tv_remaind_time.setVisibility(View.GONE);
        iv_action_reject.setText(rejectStr);
        if (!AppMessageHelper.isVerifyMessageNeedDeal(message)) {
            operator_layout.setVisibility(View.GONE);
            operator_result.setVisibility(View.VISIBLE);
            operator_result.setText(AppMessageHelper.getVerifyNotificationDealResult(message));
        } else {
            if (message.attachObject instanceof BuyChipsNotify) {
                BuyChipsNotify buyChipsNotify = (BuyChipsNotify) message.attachObject;
                if (message.status == AppMessageStatus.init) {
                    //是否失效
                    long currentTime = DemoCache.getCurrentServerSecondTime();
                    long buyApplyTime = buyChipsNotify.buyStarttime;
//                    int checkTime = DemoCache.getCheckTimeValue();
                    int timeout = buyChipsNotify.buyTimeout;
                    int remainTime = (int) (currentTime - buyApplyTime /*- checkTime*/);
                    if (remainTime >= timeout) {
                        //失效
                        if (mCountDownTimers != null) {
                            mCountDownTimers.cancel();
                            mCountDownTimers = null;
                        }
                        operator_layout.setVisibility(View.GONE);
                        message.status = AppMessageStatus.expired;//倒计时结束时 状态置为"过期"
                        operator_result.setVisibility(View.VISIBLE);
                        operator_result.setText(AppMessageHelper.getVerifyNotificationDealResult(message));
                        AppMsgDBHelper.setSystemMessageStatus(context, message.type, message.checkinPlayerId, message.key, AppMessageStatus.expired);
                        if (iMsgExpired != null) {
                            iMsgExpired.onExpired(message);
                        }
                    } else {
                        int remaindTime = timeout - remainTime;
                        operator_layout.setVisibility(View.VISIBLE);
//                        tv_remaind_time.setVisibility(View.VISIBLE);
                        operator_result.setVisibility(View.GONE);
//                        tv_remaind_time.setText(remaindTime + "s");
                        iv_action_reject.setText(rejectStr + "(" + remaindTime + ")");
                    }
                } else {
                    // 处理结果
                    operator_layout.setVisibility(View.VISIBLE);
                    operator_result.setVisibility(View.VISIBLE);
                    operator_result.setText(AppMessageHelper.getVerifyNotificationDealResult(message));
                }
            } else if (message.attachObject instanceof MatchBuyChipsNotify) {
                MatchBuyChipsNotify buyChipsNotify = (MatchBuyChipsNotify) message.attachObject;
                if (message.status == AppMessageStatus.init) {
                    if (buyChipsNotify.buyType == GameMatchBuyType.TYPE_BUY_CHECKIN) {
                        //报名，报名没有失效时间
//                        tv_remaind_time.setVisibility(View.GONE);
                        iv_action_reject.setText(rejectStr);
                        operator_layout.setVisibility(View.VISIBLE);
                        operator_result.setVisibility(View.GONE);
                    } else {
                        //重购，增购
                        long currentTime = DemoCache.getCurrentServerSecondTime();
                        long buyApplyTime = buyChipsNotify.buyStarttime;
//                        int checkTime = DemoCache.getCheckTimeValue();
                        int timeout = buyChipsNotify.buyTimeout;
                        int remainTime = (int) (currentTime - buyApplyTime /*- checkTime*/);
                        if (remainTime >= timeout) {
                            //失效
                            if (mCountDownTimers != null) {
                                mCountDownTimers.cancel();
                                mCountDownTimers = null;
                            }
                            operator_layout.setVisibility(View.GONE);
                            operator_result.setVisibility(View.VISIBLE);
                            message.status = AppMessageStatus.expired;//倒计时结束时 状态置为"过期"
                            operator_result.setText(AppMessageHelper.getVerifyNotificationDealResult(message));
                            AppMsgDBHelper.setSystemMessageStatus(context, message.type, message.checkinPlayerId, message.key, AppMessageStatus.expired);
                            if (iMsgExpired != null) {
                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //不要在onbindviewholder中notifydatasetchanged，Cannot call this method while RecyclerView is computing a layout or scrolling，放到handler中
                                        iMsgExpired.onExpired(message);
                                    }
                                });
                            }
                        } else {
                            int remaindTime = timeout - remainTime;
                            operator_layout.setVisibility(View.VISIBLE);
//                            tv_remaind_time.setVisibility(View.VISIBLE);
                            operator_result.setVisibility(View.GONE);
//                            tv_remaind_time.setText(remaindTime + "s");
                            iv_action_reject.setText(rejectStr + "(" + remaindTime + ")");
                        }
                        LogUtil.i(TAG, "remainTime :" + remainTime + "; timeout:" + timeout);
                    }
                } else {
                    // 处理结果
                    if (mCountDownTimers != null) {
                        mCountDownTimers.cancel();
                        mCountDownTimers = null;
                    }
                    operator_layout.setVisibility(View.GONE);
                    operator_result.setVisibility(View.VISIBLE);
                    operator_result.setText(AppMessageHelper.getVerifyNotificationDealResult(message));
                }
            }
        }
    }

    public void showInfoHead(boolean isShow) {
        if (isShow) {
            ll_buychips_head.setVisibility(View.VISIBLE);
        } else {
            ll_buychips_head.setVisibility(View.GONE);
        }
    }

    public void showTime(boolean isShow) {
        if (isShow) {
            tv_message_time.setVisibility(View.VISIBLE);
        } else {
            tv_message_time.setVisibility(View.GONE);
        }
    }

    public void setActionClickListener(View.OnClickListener agreeClick, View.OnClickListener rejectClick) {
        iv_action_agree.setOnClickListener(agreeClick);
        iv_action_reject.setOnClickListener(rejectClick);
    }

    public void setLongPressListener(View.OnLongClickListener longPressListener) {
        this.longPressListener = longPressListener;
        buy_chips_can_longpress_content.setClickable(true);
        buy_chips_can_longpress_content.setLongClickable(false);
//        buy_chips_can_longpress_content.setOnLongClickListener(longPressListener);
        gd = new GestureDetector(itemView.getContext(), new MyGestureDetector());
        otl = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gd.onTouchEvent(event);
            }
        };
        buy_chips_can_longpress_content.setOnTouchListener(otl);
    }

    //长按事件一直无效，用下面的方法试着解决（效果不是很好）
    View.OnLongClickListener longPressListener;
    private GestureDetector gd;
    View.OnTouchListener otl;
    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            if (longPressListener != null) {
                longPressListener.onLongClick(buy_chips_can_longpress_content);
            }
//            buy_chips_can_longpress_content.performLongClick();
        }
    }

    public IMsgExpired iMsgExpired;

    public static boolean shouldShowCountDown(AppMessage message, BuyChipsVHNew holder) {//显示倒计时
        boolean result = false;
        if (message == null || holder == null) {
            return result;
        }
        if ((message.attachObject instanceof BuyChipsNotify)) {
            if (message.status == AppMessageStatus.init) {
                BuyChipsNotify buyChipsNotify = (BuyChipsNotify) message.attachObject;
                //是否失效
                long currentTime = DemoCache.getCurrentServerSecondTime();
                long buyApplyTime = buyChipsNotify.buyStarttime;
                int timeout = buyChipsNotify.buyTimeout;
                int remainTime = (int) (currentTime - buyApplyTime /*- checkTime*/);
                if (remainTime < timeout) {
                    if (holder.mCountDownTimers != null) {
                        holder.mCountDownTimers.cancel();
                        holder.mCountDownTimers = null;
                    }
                    result = true;
                }
            }
        } else if (message.attachObject instanceof MatchBuyChipsNotify) {
            MatchBuyChipsNotify buyChipsNotify = (MatchBuyChipsNotify) message.attachObject;
            if (buyChipsNotify.buyType != GameMatchBuyType.TYPE_BUY_CHECKIN) {
                //重购，增购
                long currentTime = DemoCache.getCurrentServerSecondTime();
                long buyApplyTime = buyChipsNotify.buyStarttime;
                int timeout = buyChipsNotify.buyTimeout;
                int remainTime = (int) (currentTime - buyApplyTime /*- checkTime*/);
                if (remainTime < timeout) {
                    if (holder.mCountDownTimers != null) {
                        holder.mCountDownTimers.cancel();
                        holder.mCountDownTimers = null;
                    }
                    result = true;
                }
            }
        }
        return result;
    }
}
