package com.htgames.nutspoker.chat.session.viewholder;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiCode;
import com.htgames.nutspoker.game.model.GameStatus;
import com.netease.nim.uikit.bean.GameBillEntity;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.bean.GameNormalConfig;
import com.netease.nim.uikit.bean.GameSngConfigEntity;
import com.htgames.nutspoker.chat.msg.helper.MessageStatusHelper;
import com.htgames.nutspoker.chat.session.activity.BaseMessageActivity;
import com.htgames.nutspoker.chat.session.extension.GameAttachment;
import com.netease.nim.uikit.bean.PineappleConfig;
import com.netease.nim.uikit.bean.PineappleConfigMtt;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.db.GameRecordDBHelper;
import com.htgames.nutspoker.hotupdate.HotUpdateHelper;
import com.htgames.nutspoker.hotupdate.interfaces.CheckHotUpdateCallback;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalyticsEventConstants;
import com.htgames.nutspoker.ui.action.GameAction;
import com.htgames.nutspoker.ui.activity.Record.RecordDetailsActivity;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.session.emoji.MoonUtil;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 周智慧 on 16/12/4.
 * 俱乐部聊天页面 的 自定义消息：牌局
 * 重构代码----->来自MsgViewHolderGame.java(这个废弃掉)     and      R.layout.message_game_item(这个废弃掉)
 */

public class MsgVHGame extends MsgViewHolderBase {
    public Resources resources;
    //5种小icon背景
//    public Drawable icon_club_chat_member;
//    public Drawable icon_club_chat_chip;
//    public Drawable icon_club_chat_checkin_fee;
//    public Drawable icon_club_chat_time;
//    public Drawable icon_club_chat_time_blind;
//    //底部的三个leftdrawable
//    public Drawable icon_club_chat_mtt_color;
//    public Drawable icon_club_chat_sng_color;
//    public Drawable icon_club_chat_game_item_insurance;
//    public View club_chat_game_item_root;
//    //top
//    public View club_chat_game_item_top;
//    public TextView club_chat_game_item_top_name;
//    public ImageView club_chat_game_item_top_not_normal_type_name;
//    //middle
//    public View club_chat_game_item_middle;
//    public TextView club_chat_game_item_middle_left_tv;
//    public TextView club_chat_game_item_middle_middle_tv;
//    public TextView club_chat_game_item_middle_right_tv;
//    //bottom
//    public View club_chat_game_item_bottom;
//    public TextView club_chat_game_item_bottom_left_tv;
//    public TextView club_chat_game_item_bottom_right_tv;
//    public final static String TAG = "GameAction";
    GameAction mGameAction;
    @Override
    protected int getContentResId() {
        return R.layout.message_game_item_new;
    }

    @Override
    protected void inflateContentView() {
    }

    @Override
    protected void bindContentView() {
        if (context == null) {
            return;
        }
        Drawable bg_gold = context.getResources().getDrawable(R.mipmap.icon_club_chat_checkin_fee);
        bg_gold.setBounds(0, 0, bg_gold.getIntrinsicWidth(), bg_gold.getIntrinsicHeight());
        Drawable bg_diamond = context.getResources().getDrawable(R.mipmap.icon_mtt_record_diamond);
        bg_diamond.setBounds(0, 0, bg_diamond.getIntrinsicWidth(), bg_diamond.getIntrinsicHeight());
        View club_chat_game_item_normal_divider = findViewById(R.id.club_chat_game_item_normal_divider);
        club_chat_game_item_normal_divider.setVisibility(View.GONE);
        resources = context.getResources();
        //5种小icon背景
        Drawable drawable_pineapple_ante = resources.getDrawable(R.mipmap.di_icon_2);
        drawable_pineapple_ante.setBounds(0, 0, drawable_pineapple_ante.getIntrinsicWidth(), drawable_pineapple_ante.getIntrinsicHeight());
        Drawable icon_club_chat_member = resources.getDrawable(R.mipmap.icon_club_chat_member);
        icon_club_chat_member.setBounds(0, 0, icon_club_chat_member.getIntrinsicWidth(), icon_club_chat_member.getIntrinsicHeight());
        Drawable icon_club_chat_chip = resources.getDrawable(R.mipmap.icon_club_chat_chip);
        icon_club_chat_chip.setBounds(0, 0, icon_club_chat_chip.getIntrinsicWidth(), icon_club_chat_chip.getIntrinsicHeight());
        Drawable icon_club_chat_checkin_fee = resources.getDrawable(R.mipmap.icon_club_chat_checkin_fee);
        icon_club_chat_checkin_fee.setBounds(0, 0, icon_club_chat_checkin_fee.getIntrinsicWidth(), icon_club_chat_checkin_fee.getIntrinsicHeight());
        Drawable icon_club_chat_time = resources.getDrawable(R.mipmap.icon_club_chat_time);
        icon_club_chat_time.setBounds(0, 0, icon_club_chat_time.getIntrinsicWidth(), icon_club_chat_time.getIntrinsicHeight());
        Drawable icon_club_chat_time_blind = resources.getDrawable(R.mipmap.icon_club_chat_time_blind);
        icon_club_chat_time_blind.setBounds(0, 0, icon_club_chat_time_blind.getIntrinsicWidth(), icon_club_chat_time_blind.getIntrinsicHeight());
        //底部的三个leftdrawable
        Drawable icon_club_chat_mtt_color = resources.getDrawable(R.mipmap.icon_club_chat_mtt_color);
        icon_club_chat_mtt_color.setBounds(0, 0, icon_club_chat_mtt_color.getIntrinsicWidth(), icon_club_chat_mtt_color.getIntrinsicHeight());
        Drawable icon_club_chat_sng_color = resources.getDrawable(R.mipmap.icon_club_chat_sng_color);
        icon_club_chat_sng_color.setBounds(0, 0, icon_club_chat_sng_color.getIntrinsicWidth(), icon_club_chat_sng_color.getIntrinsicHeight());
        Drawable icon_club_chat_game_item_insurance = resources.getDrawable(R.mipmap.icon_club_chat_game_item_insurance);
        icon_club_chat_game_item_insurance.setBounds(0, 0, icon_club_chat_game_item_insurance.getIntrinsicWidth(), icon_club_chat_game_item_insurance.getIntrinsicHeight());

        View club_chat_game_item_root = findViewById(R.id.club_chat_game_item_root);
        //top
        View club_chat_game_item_top = findViewById(R.id.club_chat_game_item_top);
        TextView club_chat_game_item_top_name = findViewById(R.id.club_chat_game_item_top_name);
        ImageView club_chat_game_item_top_not_normal_type_name = findViewById(R.id.club_chat_game_item_top_not_normal_type_name);
        //middle
        View club_chat_game_item_middle = findViewById(R.id.club_chat_game_item_middle);
        TextView club_chat_game_item_middle_left_tv = findViewById(R.id.club_chat_game_item_middle_left_tv);
        TextView club_chat_game_item_middle_middle_tv = findViewById(R.id.club_chat_game_item_middle_middle_tv);
        TextView club_chat_game_item_middle_right_tv = findViewById(R.id.club_chat_game_item_middle_right_tv);
        //bottom
        View club_chat_game_item_bottom = findViewById(R.id.club_chat_game_item_bottom);
        TextView club_chat_game_item_bottom_left_tv = findViewById(R.id.club_chat_game_item_bottom_left_tv);
        TextView club_chat_game_item_bottom_right_tv = findViewById(R.id.club_chat_game_item_bottom_right_tv);
        GameAttachment attachment = (GameAttachment) message.getAttachment();
        final GameEntity gameInfo = attachment.getValue();
        MoonUtil.identifyFaceExpression(NimUIKit.getContext(), club_chat_game_item_top_name, message.getContent(), ImageSpan.ALIGN_BOTTOM);
        long passTime = DemoCache.getCurrentServerSecondTime() - gameInfo.createTime;//过去的时间
        int remainingTime = 60 * 60 * 24 * 7;//剩余的时间
        club_chat_game_item_top_name.setMovementMethod(LinkMovementMethod.getInstance());
        club_chat_game_item_top_name.setText(gameInfo.name);
        club_chat_game_item_top_not_normal_type_name.setVisibility(gameInfo.play_mode < GameConstants.PLAY_MODE_PINEAPPLE && gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL ? View.GONE : View.VISIBLE);
        View club_chat_gamr_hunter_iv = findViewById(R.id.club_chat_gamr_hunter_iv);
        club_chat_gamr_hunter_iv.setVisibility(View.GONE);
        if (gameInfo.play_mode == GameConstants.PLAY_MODE_PINEAPPLE && gameInfo.gameConfig instanceof PineappleConfig) {
            club_chat_game_item_top_not_normal_type_name.setImageDrawable(null);
            club_chat_game_item_bottom.setVisibility(View.GONE);
            club_chat_game_item_root.setBackgroundDrawable(resources.getDrawable(isReceivedMessage() ? R.drawable.bg_msg_game_pineapple_left : R.drawable.bg_msg_game_pineapple_right));
            club_chat_game_item_middle_left_tv.setText(" " + ((PineappleConfig) gameInfo.gameConfig).getAnte());
            club_chat_game_item_middle_left_tv.setCompoundDrawables(drawable_pineapple_ante, null, null, null);
            ((View) club_chat_game_item_middle_left_tv.getParent()).setVisibility(View.VISIBLE);
            club_chat_game_item_middle_middle_tv.setText(" " + ((PineappleConfig) gameInfo.gameConfig).getChips());//小盲乘以200就是普通局的记分牌
            club_chat_game_item_middle_middle_tv.setCompoundDrawables(icon_club_chat_chip, null, null, null);
            club_chat_game_item_middle_right_tv.setText(" " + GameConstants.getGameDurationShow(((PineappleConfig) gameInfo.gameConfig).getDuration()));
            club_chat_game_item_middle_right_tv.setCompoundDrawables(icon_club_chat_time, null, null, null);
        } else if (gameInfo.play_mode == GameConstants.PLAY_MODE_PINEAPPLE && gameInfo.gameConfig instanceof PineappleConfigMtt) {
            PineappleConfigMtt gameMttConfig = (PineappleConfigMtt) gameInfo.gameConfig;
            club_chat_game_item_root.setBackgroundDrawable(resources.getDrawable(isReceivedMessage() ? R.drawable.bg_club_chat_mtt_left : R.drawable.bg_club_chat_mtt_right));
            club_chat_game_item_bottom.setVisibility(View.VISIBLE);
            ((View) club_chat_game_item_bottom_right_tv.getParent()).setVisibility(View.GONE);
            //top
            club_chat_game_item_top_not_normal_type_name.setImageResource(R.mipmap.icon_paiju_item_mtt);
            //middle
            club_chat_game_item_bottom_left_tv.setVisibility(View.VISIBLE);
            club_chat_game_item_middle_left_tv.setText(" " + gameMttConfig.matchChips);
            club_chat_game_item_middle_left_tv.setCompoundDrawables(icon_club_chat_chip, null, null, null);
            club_chat_game_item_middle_middle_tv.setText(" " + gameMttConfig.matchCheckinFee);
            club_chat_game_item_middle_middle_tv.setCompoundDrawables(gameInfo.match_type == 1 ? bg_diamond : bg_gold, null, null, null);
            club_chat_game_item_middle_right_tv.setText(" " + GameConstants.getGameSngDurationMinutesShow(gameMttConfig.matchDuration));
            club_chat_game_item_middle_right_tv.setCompoundDrawables(icon_club_chat_time_blind, null, null, null);
            //bottom
            SimpleDateFormat sdf = new SimpleDateFormat("MM" + "月" + "dd" + "日" + "HH : mm");
            String beginTime = " " + sdf.format(new Date(gameMttConfig.beginTime * 1000));
            if (gameMttConfig.beginTime == -1) {
                beginTime = "";
            }
            club_chat_game_item_bottom_left_tv.setText(beginTime);
            club_chat_game_item_bottom_left_tv.setCompoundDrawables(icon_club_chat_mtt_color, null, null, null);
            club_chat_gamr_hunter_iv.setVisibility(gameMttConfig.ko_mode == 0 ? View.GONE : View.VISIBLE);
        } else if (gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL && gameInfo.gameConfig instanceof GameNormalConfig) { //**************************************************************普通模式
            GameNormalConfig gameConfig = (GameNormalConfig) gameInfo.gameConfig;
            //top
            //middle
            int seatCount = gameConfig.matchPlayer != 0 ? gameConfig.matchPlayer : 9;//几人桌
            club_chat_game_item_middle_left_tv.setText(" " + gameInfo.gamerCount + "/" + seatCount);
            club_chat_game_item_middle_left_tv.setCompoundDrawables(icon_club_chat_member, null, null, null);
            ((View) club_chat_game_item_middle_left_tv.getParent()).setVisibility(View.GONE);
            club_chat_game_item_middle_middle_tv.setText(" " + GameConstants.getGameBlindsShow(gameConfig.blindType));//小盲乘以200就是普通局的记分牌
            club_chat_game_item_middle_middle_tv.setCompoundDrawables(icon_club_chat_chip, null, null, null);
            club_chat_game_item_middle_right_tv.setText(" " + GameConstants.getGameDurationShow(gameConfig.timeType));
            club_chat_game_item_middle_right_tv.setCompoundDrawables(icon_club_chat_time, null, null, null);
            //bottom
            int ante = GameConstants.getGameAnte(gameConfig);
            boolean hasInsurance = gameConfig.tiltMode != GameConstants.GAME_MODE_INSURANCE_NOT;
            club_chat_game_item_root.setBackgroundDrawable(resources.getDrawable(isReceivedMessage() ? R.drawable.bg_club_chat_left : R.drawable.bg_club_chat_right));
            if (ante > 0 || hasInsurance) {//只要有一个条件满足，就显示bottom的view
                club_chat_game_item_normal_divider.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) club_chat_game_item_normal_divider.getLayoutParams();
                int fiveDp = ScreenUtil.dp2px(context, 5);
                if (isReceivedMessage()) {
                    lp.setMargins(fiveDp * 2, 0, fiveDp, 0);
                } else {
                    lp.setMargins(fiveDp, 0, fiveDp * 2, 0);
                }
                club_chat_game_item_bottom.setVisibility(View.VISIBLE);
                //"保险"模式----------------
                club_chat_game_item_bottom_left_tv.setVisibility(hasInsurance ? View.VISIBLE : View.GONE);
                club_chat_game_item_bottom_left_tv.setText(" " + resources.getString(R.string.game_create_config_insurance));
                club_chat_game_item_bottom_left_tv.setCompoundDrawables(icon_club_chat_game_item_insurance, null, null, null);
                //ante前注----------------
                ((View) club_chat_game_item_bottom_right_tv.getParent()).setVisibility(ante > 0 ? View.VISIBLE : View.GONE);
                club_chat_game_item_bottom_right_tv.setText("Ante: " + ante);
            } else {
                club_chat_game_item_normal_divider.setVisibility(View.GONE);
                club_chat_game_item_bottom.setVisibility(View.GONE);
            }
        } else if (gameInfo.gameMode == GameConstants.GAME_MODE_SNG && gameInfo.gameConfig instanceof GameSngConfigEntity) { //**************************************************************SNG模式
            GameSngConfigEntity gameConfig = (GameSngConfigEntity) gameInfo.gameConfig;
            club_chat_game_item_root.setBackgroundDrawable(resources.getDrawable(isReceivedMessage() ? R.drawable.bg_club_chat_sng_left : R.drawable.bg_club_chat_sng_right));
            club_chat_game_item_bottom.setVisibility(View.VISIBLE);
            ((View) club_chat_game_item_bottom_right_tv.getParent()).setVisibility(View.GONE);
            //top
            club_chat_game_item_top_not_normal_type_name.setImageResource(R.mipmap.icon_paiju_item_sng);
            //middle
            club_chat_game_item_middle_left_tv.setText(" " + gameConfig.getChips());
            club_chat_game_item_middle_left_tv.setCompoundDrawables(icon_club_chat_chip, null, null, null);
            ((View) club_chat_game_item_middle_left_tv.getParent()).setVisibility(View.VISIBLE);
            club_chat_game_item_middle_middle_tv.setText(" " + gameConfig.getCheckInFee());
            club_chat_game_item_middle_middle_tv.setCompoundDrawables(icon_club_chat_checkin_fee, null, null, null);
            club_chat_game_item_middle_right_tv.setText(" " + GameConstants.getGameSngDurationMinutesShow(gameConfig.getDuration()));
            club_chat_game_item_middle_right_tv.setCompoundDrawables(icon_club_chat_time_blind, null, null, null);
            //bottom
            club_chat_game_item_bottom_left_tv.setVisibility(View.VISIBLE);
            club_chat_game_item_bottom_left_tv.setText(" " + gameConfig.getPlayer() + "人赛");
            club_chat_game_item_bottom_left_tv.setCompoundDrawables(icon_club_chat_sng_color, null, null, null);
        } else if (gameInfo.gameMode == GameConstants.GAME_MODE_MTT && gameInfo.gameConfig instanceof GameMttConfig) { //**************************************************************MTT模式
            GameMttConfig gameMttConfig = (GameMttConfig) gameInfo.gameConfig;
            club_chat_game_item_root.setBackgroundDrawable(resources.getDrawable(isReceivedMessage() ? R.drawable.bg_club_chat_mtt_left : R.drawable.bg_club_chat_mtt_right));
            club_chat_game_item_bottom.setVisibility(View.VISIBLE);
            ((View) club_chat_game_item_bottom_right_tv.getParent()).setVisibility(View.GONE);
            //top
            club_chat_game_item_top_not_normal_type_name.setImageResource(R.mipmap.icon_paiju_item_mtt);
            //middle
            club_chat_game_item_bottom_left_tv.setVisibility(View.VISIBLE);
            club_chat_game_item_middle_left_tv.setText(" " + gameMttConfig.matchChips);
            club_chat_game_item_middle_left_tv.setCompoundDrawables(icon_club_chat_chip, null, null, null);
            club_chat_game_item_middle_middle_tv.setText(" " + gameMttConfig.matchCheckinFee);
            club_chat_game_item_middle_middle_tv.setCompoundDrawables(gameInfo.match_type == 1 ? bg_diamond : bg_gold, null, null, null);
            club_chat_game_item_middle_right_tv.setText(" " + GameConstants.getGameSngDurationMinutesShow(gameMttConfig.matchDuration));
            club_chat_game_item_middle_right_tv.setCompoundDrawables(icon_club_chat_time_blind, null, null, null);
            //bottom
            SimpleDateFormat sdf = new SimpleDateFormat("MM" + "月" + "dd" + "日" + "HH : mm");
            String beginTime = " " + sdf.format(new Date(gameMttConfig.beginTime * 1000));
            if (gameMttConfig.beginTime == -1) {
                beginTime = "";
            }
            club_chat_game_item_bottom_left_tv.setText(beginTime);
            club_chat_game_item_bottom_left_tv.setCompoundDrawables(icon_club_chat_mtt_color, null, null, null);
            club_chat_gamr_hunter_iv.setVisibility(gameMttConfig.ko_mode == 0 ? View.GONE : View.VISIBLE);
        }
        boolean isFinished = false;
        if ((gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL && remainingTime <= 0) || gameInfo.status == GameStatus.GAME_STATUS_FINISH) {
            //已经结束
            club_chat_game_item_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GameBillEntity gameBillEntity = GameRecordDBHelper.getGameRecordByGid(context, gameInfo.gid);
                    if (context instanceof Activity && gameBillEntity != null) {
                        RecordDetailsActivity.start((Activity) context, gameBillEntity, RecordDetailsActivity.FROM_CHAT);
                    } else if (context instanceof BaseMessageActivity) {
                        ((BaseMessageActivity) context).getRecordDetail(gameInfo.gid);
                    }
                }
            });
            isFinished = true;
            //UI
        } else {
            isFinished = false;
            //UI
            club_chat_game_item_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (HotUpdateHelper.isGameUpdateIng()) {
                        return;
                    }
                    if (context instanceof BaseMessageActivity) {
                        if (HotUpdateHelper.isNeedToCheckVersion()) {
                            ((BaseMessageActivity) context).checkGameVersion(new CheckHotUpdateCallback() {
                                @Override
                                public void notUpdate() {
                                    //不需要更新
                                    doJoinGame(gameInfo);
                                }
                            });
                        } else {
                            //不需要更新
                            doJoinGame(gameInfo);
                        }
                    }
                }
            });
        }
        club_chat_game_item_root.setOnLongClickListener(longClickListener);
        layoutDirection(gameInfo.gameMode, isFinished);
    }

    public void doJoinGame(final GameEntity gameEntity) {
        if (mGameAction == null && context instanceof BaseMessageActivity) {
            mGameAction = ((BaseMessageActivity) context).mGameAction;
        }
        if (mGameAction == null || gameEntity == null) {
            return;
        }
        if (gameEntity.type == GameConstants.GAME_TYPE_CLUB) {
            //群牌局
            String joinWay = UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_GROUP;
            if (!TextUtils.isEmpty(gameEntity.tid) && TeamDataCache.getInstance().getTeamById(gameEntity.tid) != null) {
                //如果是俱乐部牌局，统计在这边进行
                Team team = TeamDataCache.getInstance().getTeamById(gameEntity.tid);
                if (team.getType() == TeamTypeEnum.Advanced) {
                    joinWay = UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_CLUB;
                } else {
                    joinWay = UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_GROUP;
                }
            }
            //mGameAction.executeJoinClubGame(joinWay, gameEntity, new GameRequestCallback() {
            mGameAction.joinGame(joinWay, gameEntity.code, new GameRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                }

                @Override
                public void onFailed(int code, JSONObject response) {
                    if (code == ApiCode.CODE_GAME_NONE_EXISTENT || code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_CHANNEL_NOT_FOUND) {
                        doGameIsFinish(gameEntity);
                    }
                }
            });
        } else if (gameEntity.type == GameConstants.GAME_TYPE_GAME) {
            //私人牌局
            final String joinWay = UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_INVITE;
            mGameAction.joinGame(joinWay, gameEntity.code, new GameRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                }

                @Override
                public void onFailed(int code, JSONObject response) {
                    if (code == ApiCode.CODE_GAME_NONE_EXISTENT || code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_CHANNEL_NOT_FOUND) {
                        doGameIsFinish(gameEntity);
                    }
                }
            });
        }
    }

    /**
     * 将游戏变更为结束状态（UI和数据库）
     *
     * @param gameEntity
     */
    public void doGameIsFinish(GameEntity gameEntity) {
        //牌局结束
        if (gameEntity != null && message != null && !TextUtils.isEmpty(gameEntity.gid)) {
            MessageStatusHelper.updateGameMessageStatus(message, GameStatus.GAME_STATUS_FINISH, false);
            gameEntity.status = (GameStatus.GAME_STATUS_FINISH);
            if (getAdapter() != null) {
                getAdapter().notifyDataSetChanged();
            }
        }
    }

    private void layoutDirection(int gameMode, boolean isFinished) {
        final View club_chat_game_item_root = findViewById(R.id.club_chat_game_item_root);
        final FrameLayout rl_game_finish_mask = findViewById(R.id.rl_game_finish_mask);
        //计算出高度
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        club_chat_game_item_root.measure(w, h);
        rl_game_finish_mask.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, club_chat_game_item_root.getMeasuredHeight()));
        if (isReceivedMessage()) {
            if (gameMode == GameConstants.GAME_MODE_SNG || gameMode == GameConstants.GAME_MODE_MT_SNG) {
//                club_chat_game_item_root.setBackgroundResource(R.drawable.chatroom_sng_friend_item_bg);
            } else if (gameMode == GameConstants.GAME_MODE_MTT) {
//                club_chat_game_item_root.setBackgroundResource(R.drawable.chatroom_mtt_friend_item_bg);
            } else {
//                club_chat_game_item_root.setBackgroundResource(R.drawable.chatroom_game_friend_item_bg);
            }
//            rl_game_finish_mask.setBackgroundResource(R.drawable.chatroom_game_mask_friend_item_bg);
            rl_game_finish_mask.setBackgroundResource(R.drawable.bg_club_chat_end_right);
            if (isFinished) {
                rl_game_finish_mask.setVisibility(View.VISIBLE);
            } else {
                rl_game_finish_mask.setVisibility(View.INVISIBLE);
            }
        } else {
            if (gameMode == GameConstants.GAME_MODE_SNG || gameMode == GameConstants.GAME_MODE_MT_SNG) {
//                club_chat_game_item_root.setBackgroundResource(R.drawable.chatroom_sng_my_item_bg);
            } else if (gameMode == GameConstants.GAME_MODE_MTT) {
//                club_chat_game_item_root.setBackgroundResource(R.drawable.chatroom_mtt_my_item_bg);
            } else {
//                club_chat_game_item_root.setBackgroundResource(R.drawable.chatroom_game_my_item_bg);
            }
//            rl_game_finish_mask.setBackgroundResource(R.drawable.chatroom_game_mask_my_item_bg);
            rl_game_finish_mask.setBackgroundResource(R.drawable.bg_club_chat_end_left);//左右名字反了
            if (isFinished) {
                rl_game_finish_mask.setVisibility(View.VISIBLE);
            } else {
                rl_game_finish_mask.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected int leftBackground() {
        return 0;
    }

    @Override
    protected int rightBackground() {
        return 0;
    }
}
