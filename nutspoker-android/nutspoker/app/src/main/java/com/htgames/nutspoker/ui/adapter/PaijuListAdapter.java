package com.htgames.nutspoker.ui.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.game.model.GameStatus;
import com.htgames.nutspoker.interfaces.ICheckGameVersion;
import com.netease.nim.uikit.AnimUtil;
import com.netease.nim.uikit.bean.BaseMttConfig;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.bean.GameNormalConfig;
import com.netease.nim.uikit.bean.GameSngConfigEntity;
import com.netease.nim.uikit.bean.PineappleConfig;
import com.netease.nim.uikit.bean.PineappleConfigMtt;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.constants.GameConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.htgames.nutspoker.config.GameConfigData.pineappleModeStrIdsTeam;

/**
 * Created by 周智慧 on 16/12/1.
 */

public class PaijuListAdapter extends RecyclerView.Adapter<PaijuListAdapter.PaijuListVH> {
    public boolean omahaAnimationDone = false;
    public static final String TAG = "zzh";//PaijuListAdapter.class.getSimpleName();
    ArrayList<GameEntity> gamePlayingList = new ArrayList<GameEntity>();
    public Activity mActivity;
    public PaijuListAdapter(Activity activity) {
        mActivity = activity;
    }

    private void calculateBgSize(View rootView) {
        int marginLeftRight = ScreenUtil.dp2px(mActivity, 11);
        int marginTopBottom = ScreenUtil.dp2px(mActivity, 7f);
        RecyclerView.LayoutParams rootLayoutParams = (RecyclerView.LayoutParams) rootView.getLayoutParams();
        //bg_club_me图片比例是360 * 120，上部深色补丁的比例是360 * 36
        float screenWidth = ScreenUtil.getScreenWidth(mActivity);
        float bgWidth = screenWidth;
        float bgHeight = mActivity.getResources().getDimension(R.dimen.paiju_list_item);
        rootLayoutParams.width = (int) (bgWidth - marginLeftRight * 2f);
        rootLayoutParams.height = (int) bgHeight;
        rootLayoutParams.setMargins((int) marginLeftRight, 0, 0, marginTopBottom);
        rootView.setLayoutParams(rootLayoutParams);

    }

    @Override
    public PaijuListVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.vh_paiju_list_item, parent, false);
        calculateBgSize(view);
        return new PaijuListVH(mActivity, view);
    }

    @Override
    public void onBindViewHolder(PaijuListVH holder, int position) {
        GameEntity gameEntity = gamePlayingList.get(position);
        holder.bind(gameEntity, omahaAnimationDone);
    }

    @Override
    public int getItemCount() {
        return gamePlayingList.size();
    }

    public void setData(ArrayList<GameEntity> list) {
        if (list == null) {
            return;
        }
        gamePlayingList.clear();
        gamePlayingList.addAll(list);
        notifyDataSetChanged();
    }











    public static class PaijuListVH extends RecyclerView.ViewHolder {
        public Drawable bg_club_game_mormal;
        public Drawable bg_club_game_sng;
        public Drawable bg_club_game_mtt;
        public Drawable bg_club_game_pineapple;
        public Activity mVHActivity;
        public Drawable icon_club_paiju_time;//普通牌局"持续时间"的leftdrawable----------------icon_club_paiju_time
        public Drawable icon_club_paiju_time_blind;//sng、mtt、mt-sng"涨盲时间"的leftdrawable--------------icon_club_paiju_time_blind
//        public Drawable icon_paiju_item_bottom_extra;//sng、mtt、mt-sng底部文案的leftdrawable--------------icon_paiju_item_bottom_extra
        //根布局
        public View rl_game_desk;
        public ImageView iv_fore_normal_or_mtt;
        public TextView tv_game_status;
        //top
        public View rl_paiju_item_top;
        public HeadImageView paiju_item_logo_iv;//牌局创建者头像
        public TextView tv_discovery_name;//牌局名称
        public TextView tv_gamedesk_ante;//高级设置的"ante前注"
        public TextView iv_gamedesk_insurance;//高级设置的是否买"保险"
        public ImageView paiju_list_item_not_normal_type_name;//不是普通牌局的image（sng、mtt、mt-sng）
        public View club_game_list_hunter_iv;
        TextView tv_pineapple_deal_order;//大菠萝普通局的"同步发牌"
        TextView tv_pineapple_mode;//大菠萝普通局的"常规模式"
        //middle
        public View rl_paiju_item_middle;
        public View tv_discovery_member_container;//牌局人数父布局
        public TextView tv_discovery_member;//牌局人数
        public View tv_discovery_match_chips_container;//牌局初始记分牌父布局
        public TextView tv_discovery_match_chips;//牌局初始记分牌
        public View tv_discovery_match_checkin_fee_container;//牌局费用父布局
        public TextView tv_discovery_match_checkin_fee;//牌局费用
        public View tv_discovery_match_duration_container;//牌局时间父布局
        public TextView tv_discovery_match_duration;//牌局时间（普通牌局是指"牌局时间"，SNG和MTT是指"牌局开始时间"，两者的drawableleft不同）
        public View tv_pineapple_ante_container;
        public TextView tv_pineapple_ante_num;
        //bottom
        public TextView tv_paiju_item_bottom_extra;
        public View horde_paiju_container;
        ImageView iv_omaha_icon;
        //钻石赛
        Drawable bg_gold;
        Drawable bg_diamond;
        GradientDrawable bg_pineapple_deal_order;//"发牌顺序"的背景
        GradientDrawable bg_pineapple_mode_yoriko;//"赖子模式"的背景
        GradientDrawable bg_horde_game;//"赖子模式"的背景
        public PaijuListVH(Activity activity, View itemView) {
            super(itemView);
            mVHActivity = activity;
            icon_club_paiju_time = activity.getResources().getDrawable(R.mipmap.icon_club_paiju_time);
            icon_club_paiju_time.setBounds(0, 0, icon_club_paiju_time.getIntrinsicWidth(), icon_club_paiju_time.getIntrinsicHeight());
            icon_club_paiju_time_blind = activity.getResources().getDrawable(R.mipmap.icon_club_paiju_time_blind);
            icon_club_paiju_time_blind.setBounds(0, 0, icon_club_paiju_time_blind.getIntrinsicWidth(), icon_club_paiju_time_blind.getIntrinsicHeight());
//            icon_paiju_item_bottom_extra = activity.getResources().getDrawable(R.mipmap.icon_paiju_item_bottom_extra);
//            icon_paiju_item_bottom_extra.setBounds(0, 0, icon_paiju_item_bottom_extra.getIntrinsicWidth(), icon_paiju_item_bottom_extra.getIntrinsicHeight());
            bg_club_game_mormal = activity.getResources().getDrawable(R.drawable.bg_club_game_normal);
            bg_club_game_sng = activity.getResources().getDrawable(R.drawable.bg_club_game_sng);
            bg_club_game_mtt = activity.getResources().getDrawable(R.drawable.bg_club_game_mtt);
            bg_club_game_pineapple = activity.getResources().getDrawable(R.drawable.bg_club_game_pineapple);
            rl_game_desk = itemView.findViewById(R.id.rl_game_desk);
            iv_fore_normal_or_mtt = (ImageView) itemView.findViewById(R.id.iv_fore_normal_or_mtt);
            tv_game_status = (TextView) itemView.findViewById(R.id.tv_game_status);
            //top
            rl_paiju_item_top = itemView.findViewById(R.id.rl_paiju_item_top);
            paiju_item_logo_iv = (HeadImageView) itemView.findViewById(R.id.paiju_item_logo_iv);//牌局创建者头像
            tv_discovery_name = (TextView) itemView.findViewById(R.id.tv_discovery_name);//牌局名称
            tv_gamedesk_ante = (TextView) itemView.findViewById(R.id.tv_gamedesk_ante);//高级设置的"ante前注"
            iv_gamedesk_insurance = (TextView) itemView.findViewById(R.id.iv_gamedesk_insurance);//高级设置的是否买"保险"
            paiju_list_item_not_normal_type_name = (ImageView) itemView.findViewById(R.id.paiju_list_item_not_normal_type_name);
            club_game_list_hunter_iv = itemView.findViewById(R.id.club_game_list_hunter_iv);
            tv_pineapple_deal_order = (TextView) itemView.findViewById(R.id.tv_pineapple_deal_order);//大菠萝普通局的"同步发牌"
            //middle
            rl_paiju_item_middle = itemView.findViewById(R.id.rl_paiju_item_middle);
            tv_discovery_member_container = itemView.findViewById(R.id.tv_discovery_member_container);//牌局人数父布局
            tv_discovery_member = (TextView) itemView.findViewById(R.id.tv_discovery_member);//牌局人数
            tv_discovery_match_chips_container = itemView.findViewById(R.id.tv_discovery_match_chips_container);//牌局初始记分牌父布局
            tv_discovery_match_chips = (TextView) itemView.findViewById(R.id.tv_discovery_match_chips);//牌局初始记分牌
            tv_discovery_match_checkin_fee_container = itemView.findViewById(R.id.tv_discovery_match_checkin_fee_container);//牌局费用父布局
            tv_discovery_match_checkin_fee = (TextView) itemView.findViewById(R.id.tv_discovery_match_checkin_fee);//牌局费用
            tv_discovery_match_duration_container = itemView.findViewById(R.id.tv_discovery_match_duration_container);//牌局时间父布局
            tv_discovery_match_duration = (TextView) itemView.findViewById(R.id.tv_discovery_match_duration);//牌局时间（普通牌局是指"牌局时间"，SNG和MTT是指"牌局开始时间"，两者的drawableleft不同）
            tv_pineapple_ante_container = itemView.findViewById(R.id.tv_pineapple_ante_container);
            tv_pineapple_ante_num = (TextView) itemView.findViewById(R.id.tv_pineapple_ante_num);
            //bottom
            tv_paiju_item_bottom_extra = (TextView) itemView.findViewById(R.id.tv_paiju_item_bottom_extra);
            horde_paiju_container = itemView.findViewById(R.id.horde_paiju_container);
            iv_omaha_icon = (ImageView) itemView.findViewById(R.id.iv_omaha_icon);
            tv_pineapple_mode = (TextView) itemView.findViewById(R.id.tv_pineapple_mode);
            bg_gold = itemView.getContext().getResources().getDrawable(R.mipmap.icon_club_paiju_checkin_fee);
            bg_gold.setBounds(0, 0, bg_gold.getIntrinsicWidth(), bg_gold.getIntrinsicHeight());
            bg_diamond = itemView.getContext().getResources().getDrawable(R.mipmap.icon_mtt_room_diamond_white);
            bg_diamond.setBounds(0, 0, bg_diamond.getIntrinsicWidth(), bg_diamond.getIntrinsicHeight());
            bg_pineapple_deal_order = new GradientDrawable();
            bg_pineapple_deal_order.setCornerRadius(ScreenUtil.dp2px(activity, 4));
            bg_pineapple_deal_order.setStroke(ScreenUtil.dp2px(activity, 1), activity.getResources().getColor(R.color.club_paiju_list_insurance));
            bg_pineapple_mode_yoriko = new GradientDrawable();
            bg_pineapple_mode_yoriko.setCornerRadius(ScreenUtil.dp2px(activity, 4));
            bg_pineapple_mode_yoriko.setColor(activity.getResources().getColor(R.color.club_paiju_list_insurance));
            bg_horde_game = new GradientDrawable();
            bg_horde_game.setCornerRadius(ScreenUtil.dp2px(activity, 4));
            bg_horde_game.setStroke(ScreenUtil.dp2px(activity, 1), activity.getResources().getColor(R.color.icon_horde_game));
        }
        public void bind(final GameEntity gameEntity, boolean omahaAnimationDone) {
            if (gameEntity == null) {
                return;
            }
            setGameStatus(gameEntity);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mVHActivity instanceof ICheckGameVersion) {
                        ((ICheckGameVersion) mVHActivity).checkGameVersionJoin(gameEntity);
                    }
                }
            });
            tv_discovery_match_checkin_fee.setCompoundDrawables(gameEntity.match_type == 1 ? bg_diamond : bg_gold , null, null, null);
            horde_paiju_container.setVisibility(StringUtil.isSpace(gameEntity.horde_id) || gameEntity.horde_id.equals("0") ? View.GONE : View.VISIBLE);
            horde_paiju_container.setBackgroundDrawable(bg_horde_game);
            tv_pineapple_ante_container.setVisibility(gameEntity.gameConfig instanceof PineappleConfig ? View.VISIBLE : View.GONE);//只有普通大菠萝才显示底注
            LogUtil.i(TAG, "gameEntity.getStatus(): " + gameEntity.status);
            LogUtil.i(TAG, "gameEntity.getCreateTime() :" + gameEntity.createTime * 1000);
            LogUtil.i(TAG, "mtt gameEntity.getCurrentServerTime() :" + gameEntity.currentServerTime * 1000);
            //GameConfig 参考类GameAttachment.java
            paiju_item_logo_iv.loadBuddyAvatar(gameEntity.uid);//设置头像
            tv_discovery_name.setText(gameEntity.name + "");
            tv_pineapple_mode.setVisibility(gameEntity.play_mode == GameConstants.PLAY_MODE_PINEAPPLE ? View.VISIBLE : View.GONE);
            int[] game_mode_ids = new int[]{R.mipmap.room_texas_icon, R.mipmap.room_omaha_icon, R.mipmap.room_pinapple_icon, R.mipmap.room_texas_icon, R.mipmap.room_texas_icon, R.mipmap.room_texas_icon};
            iv_omaha_icon.setImageResource(game_mode_ids[gameEntity.play_mode]);
            if (gameEntity.play_mode == GameConstants.PLAY_MODE_TEXAS_HOLDEM) {
                if (!omahaAnimationDone) {
                    AnimUtil.translateX(iv_omaha_icon, -ScreenUtil.dp2px(mVHActivity, 58), 0, 300);
                }
            } else if (gameEntity.play_mode == GameConstants.PLAY_MODE_OMAHA) {
                if (!omahaAnimationDone) {
                    AnimUtil.translateX(iv_omaha_icon, -ScreenUtil.dp2px(mVHActivity, 44), 0, 300);
                }
            } else if (gameEntity.play_mode == GameConstants.PLAY_MODE_PINEAPPLE) {
                if (!omahaAnimationDone) {
                    AnimUtil.translateX(iv_omaha_icon, -ScreenUtil.dp2px(mVHActivity, 44), 0, 300);
                }
            }
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) iv_fore_normal_or_mtt.getLayoutParams();
            lp.gravity = gameEntity.gameMode == GameConstants.GAME_MODE_NORMAL ? Gravity.CENTER : Gravity.LEFT;
            iv_fore_normal_or_mtt.setImageResource(gameEntity.gameMode == GameConstants.GAME_MODE_NORMAL ? R.mipmap.room_normal_bg : R.mipmap.room_mtt_bg);
            tv_pineapple_deal_order.setVisibility(gameEntity.play_mode == GameConstants.PLAY_MODE_PINEAPPLE && gameEntity.gameConfig instanceof PineappleConfig ? View.VISIBLE : View.GONE);
            if (gameEntity.play_mode == GameConstants.PLAY_MODE_TEXAS_HOLDEM || gameEntity.play_mode == GameConstants.PLAY_MODE_OMAHA) {
                if (gameEntity.gameMode == GameConstants.GAME_MODE_NORMAL && gameEntity.gameConfig instanceof GameNormalConfig) { //**************************************************************普通模式
                    rl_game_desk.setBackgroundDrawable(bg_club_game_mormal);
                    paiju_list_item_not_normal_type_name.setVisibility(View.GONE);
                    club_game_list_hunter_iv.setVisibility(View.GONE);
                    tv_discovery_match_checkin_fee_container.setVisibility(View.GONE);//普通牌局的"费用"文案不显示，以后的版本可能会加上
                    GameNormalConfig gameConfig = (GameNormalConfig) gameEntity.gameConfig;
                    int ante = GameConstants.getGameAnte(gameConfig);
                    //top
                    String insurance = mVHActivity.getResources().getString(R.string.game_create_config_insurance);
                    iv_gamedesk_insurance.setVisibility(gameConfig.tiltMode == GameConstants.GAME_MODE_INSURANCE_NOT ? View.GONE : View.VISIBLE);
                    if (gameConfig.tiltMode == GameConstants.GAME_MODE_INSURANCE_POOL) {//保险模式：分池购买
                        iv_gamedesk_insurance.setText("" + insurance);//这个版本两种保险模式的文案一样都是"保险"
                    } else if (gameConfig.tiltMode == GameConstants.GAME_MODE_INSURANCE_QUICK) {//;//保险模式：快速购买
                        iv_gamedesk_insurance.setText("" + insurance);//这个版本两种保险模式的文案一样都是"保险"
                    }
//                    iv_gamedesk_insurance.setBackgroundDrawable(bg_pineapple_deal_order);
                    tv_gamedesk_ante.setText(ante <= 0 ? "" : ("Ante: " + ante));
                    tv_gamedesk_ante.setVisibility(ante <= 0 ? View.GONE : View.VISIBLE);
//                    tv_gamedesk_ante.setBackgroundDrawable(bg_pineapple_deal_order);
                    //middle
                    int seatCount = gameConfig.matchPlayer != 0 ? gameConfig.matchPlayer : 9;//几人桌
                    tv_discovery_member.setText(" " + gameEntity.gamerCount + "/" + seatCount);
                    tv_discovery_match_chips.setText(" " + GameConstants.getGameBlindsShow(gameConfig.blindType));//小盲乘以200就是普通局的记分牌
                    tv_discovery_match_duration.setText(" " + GameConstants.getGameDurationShow(gameConfig.timeType));//牌局总的持续时间：分钟、小时
                    tv_discovery_match_duration.setCompoundDrawables(icon_club_paiju_time, null, null, null);
                    //bottom
                    if (gameEntity.currentServerTime != 0) {
                        long passTime = gameEntity.currentServerTime - gameEntity.start_time;//gameEntity.getCreateTime();//过去的时间
                        if (gameEntity.start_time <= 0) {//虽然status是1但是游戏内还未点击开始
                            passTime = 0;
                        }
                        int remainingTime = gameConfig.timeType / 60 - (int) (passTime / 60);//剩余的时间
                        if (remainingTime <= 0) {
                            tv_paiju_item_bottom_extra.setText("已经结束");
                        } else {
                            //比赛进行中
                            tv_paiju_item_bottom_extra.setText(mVHActivity.getResources().getString(R.string.remaining) + GameConstants.getShowRemainTime(remainingTime, false));
                        }
                        LogUtil.i(TAG, "remainingTime 普通牌局:" + remainingTime);
                    } else {
                        //如果没有返回服务器时间（比如创建成功接口不会带上服务器时间）
                        tv_paiju_item_bottom_extra.setText(mVHActivity.getResources().getString(R.string.remaining) + GameConstants.getShowRemainTime(gameConfig.timeType / 60, false));
                    }
                    if (gameEntity.status == GameStatus.GAME_STATUS_WAIT) {
                        tv_paiju_item_bottom_extra.setText(mVHActivity.getResources().getString(R.string.remaining) + GameConstants.getShowRemainTime(gameConfig.timeType / 60, false));
                    }
                    tv_paiju_item_bottom_extra.setCompoundDrawables(null, null, null, null);
                } else if (gameEntity.gameMode == GameConstants.GAME_MODE_SNG && gameEntity.gameConfig instanceof GameSngConfigEntity) { //**************************************************************SNG模式
                    rl_game_desk.setBackgroundDrawable(bg_club_game_sng);
                    club_game_list_hunter_iv.setVisibility(View.GONE);
                    paiju_list_item_not_normal_type_name.setVisibility(View.VISIBLE);
                    paiju_list_item_not_normal_type_name.setImageResource(R.mipmap.room_sng_icon);
                    tv_gamedesk_ante.setVisibility(View.GONE);
                    iv_gamedesk_insurance.setVisibility(View.GONE);
                    tv_discovery_match_checkin_fee_container.setVisibility(View.VISIBLE);//普通牌局的"费用"文案不显示，以后的版本可能会加上
                    GameSngConfigEntity gameConfig = (GameSngConfigEntity) gameEntity.gameConfig;
                    //top
                    //middle
                    tv_discovery_member.setText(" " + gameEntity.gamerCount + "/" + gameConfig.getPlayer());
                    tv_discovery_match_chips.setText(" " + gameConfig.chips);
                    tv_discovery_match_checkin_fee.setText(" " + gameConfig.getCheckInFee());
                    tv_discovery_match_duration.setText(" " + GameConstants.getGameSngDurationMinutesShow(gameConfig.getDuration()));
                    tv_discovery_match_duration.setCompoundDrawables(icon_club_paiju_time_blind, null, null, null);
                    //bottom
                    if (gameEntity.start_time > 0) {
                        long passTime = (gameEntity.currentServerTime - gameEntity.start_time) / 60;
                        tv_paiju_item_bottom_extra.setText("进行中：" + GameConstants.getShowRemainTime((int) passTime, false));
                    } else {
                        //如果没有返回服务器时间（比如创建成功接口不会带上服务器时间）
                        tv_paiju_item_bottom_extra.setText(R.string.checkin_ing);
                    }
//                    tv_paiju_item_bottom_extra.setCompoundDrawables(icon_paiju_item_bottom_extra, null, null, null);
                } else if (gameEntity.gameMode == GameConstants.GAME_MODE_MTT && gameEntity.gameConfig instanceof GameMttConfig) { //**************************************************************MTT模式
                    rl_game_desk.setBackgroundDrawable(bg_club_game_mtt);
                    paiju_list_item_not_normal_type_name.setVisibility(View.VISIBLE);
                    paiju_list_item_not_normal_type_name.setImageResource(R.mipmap.room_mtt_icon);
                    tv_gamedesk_ante.setVisibility(View.GONE);
                    iv_gamedesk_insurance.setVisibility(View.GONE);
                    tv_discovery_match_checkin_fee_container.setVisibility(View.VISIBLE);//普通牌局的"费用"文案不显示，以后的版本可能会加上
                    GameMttConfig gameConfig = (GameMttConfig) gameEntity.gameConfig;
                    //top
                    //middle
                    tv_discovery_match_chips.setText(" " + gameConfig.matchChips);
                    tv_discovery_match_checkin_fee.setText(" " + gameConfig.matchCheckinFee);
                    tv_discovery_match_checkin_fee.setCompoundDrawables(gameEntity.match_type == 1 ? bg_diamond : bg_gold , null, null, null);
                    tv_discovery_match_duration.setText(" " + GameConstants.getGameSngDurationMinutesShow(gameConfig.matchDuration));
                    tv_discovery_match_duration.setCompoundDrawables(icon_club_paiju_time_blind, null, null, null);
                    //bottom
                    long currentServerTime = DemoCache.getCurrentServerSecondTime();//单位是秒，不是毫秒
                    long mttBeginTime = gameConfig.beginTime;
                    LogUtil.i(TAG, "mtt gameConfig.getBeginTime() :" + gameConfig.beginTime);
                    tv_discovery_member.setText(" " + gameEntity.checkinPlayerCount);
                    if (mttBeginTime < currentServerTime && mttBeginTime != -1) {
                        long passTime = (gameEntity.currentServerTime - mttBeginTime) / 60;
                        tv_paiju_item_bottom_extra.setText("进行中：" + GameConstants.getShowRemainTime((int) passTime, false));
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM" + "月" + "dd" + "日" + "HH : mm");
                        tv_paiju_item_bottom_extra.setText("比赛时间: " + sdf.format(new Date(mttBeginTime * 1000)));//时间是秒，乘以1000转换成毫秒
                        if (mttBeginTime == -1) {//未设置开赛时间
                            tv_paiju_item_bottom_extra.setText("比赛时间: 手动");
                        }
                    }
                    club_game_list_hunter_iv.setVisibility(gameConfig.ko_mode == 0 ? View.GONE : View.VISIBLE);
                }
            } else if (gameEntity.play_mode == GameConstants.PLAY_MODE_PINEAPPLE && gameEntity.gameConfig instanceof PineappleConfig) {
                //发牌顺序的设置
                tv_pineapple_deal_order.setText(((PineappleConfig) gameEntity.gameConfig).getDeal_order() == GameConstants.DEAL_ORDER_MEANWHILE ? R.string.pineapple_order_type_sync : R.string.pineapple_order_type_sequence);
//                tv_pineapple_deal_order.setBackgroundDrawable(bg_pineapple_deal_order);
                rl_game_desk.setBackgroundDrawable(bg_club_game_pineapple);
                club_game_list_hunter_iv.setVisibility(View.GONE);
                iv_gamedesk_insurance.setVisibility(View.GONE);
                tv_gamedesk_ante.setVisibility(View.GONE);
                paiju_list_item_not_normal_type_name.setVisibility(View.GONE);
                tv_discovery_match_checkin_fee_container.setVisibility(View.GONE);
                tv_discovery_member.setText(" " + gameEntity.gamerCount + "/" + ((PineappleConfig) gameEntity.gameConfig).getMatch_player());
                tv_pineapple_ante_num.setText(" " + ((PineappleConfig) gameEntity.gameConfig).getAnte());
                tv_discovery_match_chips.setText(" " + ((PineappleConfig) gameEntity.gameConfig).getChips());
                tv_discovery_match_duration.setText(" " + GameConstants.getGameDurationShow(((PineappleConfig) gameEntity.gameConfig).getDuration()));
                tv_discovery_match_duration.setCompoundDrawables(icon_club_paiju_time, null, null, null);
                //bottom
                if (gameEntity.currentServerTime != 0) {
                    long passTime = gameEntity.currentServerTime - gameEntity.start_time;//gameEntity.getCreateTime();//过去的时间
                    if (gameEntity.start_time <= 0) {//虽然status是1但是游戏内还未点击开始
                        passTime = 0;
                    }
                    int remainingTime = ((PineappleConfig) gameEntity.gameConfig).getDuration() / 60 - (int) (passTime / 60);//剩余的时间
                    if (remainingTime <= 0) {
                        tv_paiju_item_bottom_extra.setText("已经结束");
                    } else {
                        //比赛进行中
                        tv_paiju_item_bottom_extra.setText(mVHActivity.getResources().getString(R.string.remaining) + GameConstants.getShowRemainTime(remainingTime, false));
                    }
                    LogUtil.i(TAG, "remainingTime 普通牌局:" + remainingTime);
                } else {
                    //如果没有返回服务器时间（比如创建成功接口不会带上服务器时间）
                    tv_paiju_item_bottom_extra.setText(mVHActivity.getResources().getString(R.string.remaining) + GameConstants.getShowRemainTime(((PineappleConfig) gameEntity.gameConfig).getDuration() / 60, false));
                }
                if (gameEntity.status == GameStatus.GAME_STATUS_WAIT) {
                    tv_paiju_item_bottom_extra.setText(mVHActivity.getResources().getString(R.string.remaining) + GameConstants.getShowRemainTime(((PineappleConfig) gameEntity.gameConfig).getDuration() / 60, false));
                }
                tv_paiju_item_bottom_extra.setCompoundDrawables(null, null, null, null);
                setPineappleMode(((PineappleConfig) gameEntity.gameConfig).getPlay_type());
            } else if (gameEntity.play_mode == GameConstants.PLAY_MODE_PINEAPPLE && gameEntity.gameConfig instanceof PineappleConfigMtt) {
                rl_game_desk.setBackgroundDrawable(bg_club_game_pineapple);
                paiju_list_item_not_normal_type_name.setVisibility(View.VISIBLE);
                paiju_list_item_not_normal_type_name.setImageResource(R.mipmap.room_mtt_icon);
                tv_gamedesk_ante.setVisibility(View.GONE);
                iv_gamedesk_insurance.setVisibility(View.GONE);
                tv_discovery_match_checkin_fee_container.setVisibility(View.VISIBLE);//普通牌局的"费用"文案不显示，以后的版本可能会加上
                PineappleConfigMtt gameConfig = (PineappleConfigMtt) gameEntity.gameConfig;
                //top
                //middle
                tv_discovery_match_chips.setText(" " + gameConfig.matchChips);
                tv_discovery_match_checkin_fee.setText(" " + gameConfig.matchCheckinFee);
                tv_discovery_match_checkin_fee.setCompoundDrawables(gameEntity.match_type == 1 ? bg_diamond : bg_gold , null, null, null);
                tv_discovery_match_duration.setText(" " + GameConstants.getGameSngDurationMinutesShow(gameConfig.matchDuration));
                tv_discovery_match_duration.setCompoundDrawables(icon_club_paiju_time_blind, null, null, null);
                //bottom
                long currentServerTime = DemoCache.getCurrentServerSecondTime();//单位是秒，不是毫秒
                long mttBeginTime = gameConfig.beginTime;
                LogUtil.i(TAG, "mtt gameConfig.getBeginTime() :" + gameConfig.beginTime);
                tv_discovery_member.setText(" " + gameEntity.checkinPlayerCount);
                if (mttBeginTime < currentServerTime && mttBeginTime != -1) {
                    long passTime = (gameEntity.currentServerTime - mttBeginTime) / 60;
                    tv_paiju_item_bottom_extra.setText("进行中：" + GameConstants.getShowRemainTime((int) passTime, false));
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM" + "月" + "dd" + "日" + "HH : mm");
                    tv_paiju_item_bottom_extra.setText("比赛时间: " + sdf.format(new Date(mttBeginTime * 1000)));//时间是秒，乘以1000转换成毫秒
                    if (mttBeginTime == -1) {//未设置开赛时间
                        tv_paiju_item_bottom_extra.setText("比赛时间: 手动");
                    }
                }
                club_game_list_hunter_iv.setVisibility(gameConfig.ko_mode == 0 ? View.GONE : View.VISIBLE);
                setPineappleMode(gameConfig.getPlay_type());
            }
        }

        private void setPineappleMode(int type) {
            if (tv_pineapple_mode == null || type >= pineappleModeStrIdsTeam.length || mVHActivity == null) {
                return;
            }
            tv_pineapple_mode.setText(pineappleModeStrIdsTeam[type]);
            tv_pineapple_mode.setTextColor(mVHActivity.getResources().getColor(type == GameConstants.PINEAPPLE_MODE_YORIKO ? R.color.text_select_color : R.color.club_paiju_list_insurance));
            tv_pineapple_mode.setBackgroundDrawable(type == GameConstants.PINEAPPLE_MODE_YORIKO ? bg_pineapple_mode_yoriko : bg_pineapple_deal_order);
        }

        private void setGameStatus(final GameEntity gameEntity) {
            int[] statusStrIds = new int[]{R.string.game_status_ready, R.string.checkin_ing, R.string.game_goon_ing, R.string.game_me_ing};
            int[] statusStrColors = new int[]{R.color.game_status_ready, R.color.checkin_ing, R.color.game_goon_ing, R.color.game_me_ing};
            int status_index = 0;//0准备中  1报名中 2进行中  3参与中
            if (gameEntity.gameMode == GameConstants.GAME_MODE_NORMAL) {//0准备中  2进行中  3参与中
                status_index = gameEntity.status == GameStatus.GAME_STATUS_WAIT ? 0 : gameEntity.activity == GameConstants.ACTIVITY_SIT ? 3 : 2;
            } else if (gameEntity.gameMode == GameConstants.GAME_MODE_SNG) {//1报名中  2进行中  3参与中
                GameSngConfigEntity gameConfig = (GameSngConfigEntity) gameEntity.gameConfig;
                status_index = gameEntity.start_time <= 0 ? 1 : gameEntity.activity == GameConstants.ACTIVITY_SIT ? 3 : 2;
            } else if (gameEntity.gameMode == GameConstants.GAME_MODE_MTT) {//1报名中  2进行中  3参与中
                BaseMttConfig gameConfig = (BaseMttConfig) gameEntity.gameConfig;
                long currentServerTime = DemoCache.getCurrentServerSecondTime();
                long mttBeginTime = gameConfig.beginTime;
                status_index = mttBeginTime >= currentServerTime || mttBeginTime == -1 ? 1 : gameEntity.activity == GameConstants.ACTIVITY_SIT ? 3 : 2;
            }
            tv_game_status.setText(statusStrIds[status_index]);
            tv_game_status.setTextColor(mVHActivity.getResources().getColor(statusStrColors[status_index]));
        }
    }
}
