package com.htgames.nutspoker.ui.adapter

import android.app.Activity
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.htgames.nutspoker.ChessApp
import com.htgames.nutspoker.R
import com.htgames.nutspoker.config.GameConfigData.pineappleIconIdsHistory
import com.htgames.nutspoker.game.model.GameStatus
import com.netease.nim.uikit.AnimUtil
import com.netease.nim.uikit.bean.*
import com.netease.nim.uikit.common.ui.imageview.HeadImageView
import com.netease.nim.uikit.common.util.log.LogUtil
import com.netease.nim.uikit.common.util.string.StringUtil
import com.netease.nim.uikit.common.util.sys.ScreenUtil
import com.netease.nim.uikit.constants.GameConstants
import com.netease.nim.uikit.interfaces.IClickPayload

class RecentGameAdap : RecyclerView.Adapter<RecentGameAdap.ThisVH> {
    var omahaAnimationDone = false
    var mActivity: Activity? = null
    var mClickListener: IClickPayload? = null
    constructor(datas: ArrayList<GameEntity>?, listeners: Any) {
        if (datas != null) {
            mDatas.clear()
            mDatas.addAll(datas)
        }
        if (listeners is Fragment) {
            mActivity = listeners.activity
        }
        if (listeners is Activity) {
            mActivity = listeners
        }
        if (listeners is IClickPayload) {
            mClickListener = listeners
        }
    }
    var mDatas: ArrayList<GameEntity> = ArrayList()
    fun updateData(datas: ArrayList<GameEntity>?) {
        mDatas.clear()
        if (datas != null) {
            mDatas.addAll(datas)
        }
    }
    override fun getItemCount(): Int {
        LogUtil.i("test recyclerview adapter", "getItemCount: ${mDatas.size}")
        return mDatas.size
    }

    override fun getItemViewType(position: Int): Int {
        LogUtil.i("test recyclerview adapter", "getItemViewType: ${mDatas.size}")
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: ThisVH?, position: Int) {
        if (holder == null || position < 0 || position >= mDatas.size) {
            return
        }
        holder.bind(mDatas.get(position), this, position)
        LogUtil.i("test recyclerview adapter", "onBindViewHolder: $position")
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ThisVH {
        LogUtil.i("test recyclerview adapter", "onCreateViewHolder")
        val view = LayoutInflater.from(mActivity).inflate(R.layout.list_recent_game, parent, false)
        return ThisVH(view)
    }

    class ThisVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var drawable_horde: Drawable
        var icon_club_chat_time: Drawable
        var icon_club_chat_time_blind: Drawable
        var bg_pineapple_deal_order: GradientDrawable
        var iv_game_creator_userhead: HeadImageView = itemView.findViewById(R.id.iv_game_creator_userhead) as HeadImageView
        var tv_game_name: TextView = itemView.findViewById(R.id.tv_game_name) as TextView
        var tv_game_member: TextView = itemView.findViewById(R.id.tv_game_member) as TextView
        var tv_game_chickin_fee: TextView = itemView.findViewById(R.id.tv_game_chickin_fee) as TextView
        var tv_game_blind: TextView = itemView.findViewById(R.id.tv_game_blind) as TextView
        var tv_game_time: TextView = itemView.findViewById(R.id.tv_game_time) as TextView
        var tv_game_ante: TextView = itemView.findViewById(R.id.tv_game_ante) as TextView
        var tv_game_remaining_title: TextView = itemView.findViewById(R.id.tv_game_remaining_title) as TextView
        var iv_game_insurance: View = itemView.findViewById(R.id.iv_game_insurance)
        var tv_game_remaining_time: TextView = itemView.findViewById(R.id.tv_game_remaining_time) as TextView
        var tv_recentgame_checkin: TextView = itemView.findViewById(R.id.tv_recentgame_checkin) as TextView
        var game_history_hunter_iv: ImageView = itemView.findViewById(R.id.game_history_hunter_iv) as ImageView
        internal var tv_game_mode_status: TextView = itemView.findViewById(R.id.tv_game_mode_status) as TextView
        internal var iv_game_mode: ImageView = itemView.findViewById(R.id.iv_game_mode) as ImageView
        var iv_omaha_icon: ImageView = itemView.findViewById(R.id.iv_omaha_icon) as ImageView
        var iv_pineapple_icon: ImageView = itemView.findViewById(R.id.iv_pineapple_icon) as ImageView
        var tv_pineapple_ante: TextView = itemView.findViewById(R.id.tv_pineapple_ante) as TextView
        var tv_pineapple_deal_order: TextView = itemView.findViewById(R.id.tv_pineapple_deal_order) as TextView//普通大菠萝的发牌顺序
        init {
            icon_club_chat_time = itemView.context.getResources().getDrawable(R.mipmap.icon_club_chat_time)
            icon_club_chat_time.setBounds(0, 0, icon_club_chat_time.intrinsicWidth, icon_club_chat_time.intrinsicHeight)
            icon_club_chat_time_blind = itemView.context.getResources().getDrawable(R.mipmap.icon_club_chat_time_blind)
            icon_club_chat_time_blind.setBounds(0, 0, icon_club_chat_time_blind.intrinsicWidth, icon_club_chat_time_blind.intrinsicHeight)
            bg_pineapple_deal_order = GradientDrawable()
            bg_pineapple_deal_order.setCornerRadius(ScreenUtil.dp2px(itemView.context, 4f).toFloat())
            bg_pineapple_deal_order.setStroke(ScreenUtil.dp2px(itemView.context, 1f), itemView.context.getResources().getColor(R.color.login_solid_color))
            drawable_horde = itemView.context.getResources().getDrawable(R.mipmap.icon_clan)
            drawable_horde.setBounds(0, 0, drawable_horde.intrinsicWidth, drawable_horde.intrinsicHeight)
        }
        
        fun bind(gameInfo: GameEntity?, adapter: RecentGameAdap, position: Int) {
            if (gameInfo == null) {
                return
            }
            tv_game_name.setCompoundDrawables(null, null, if (StringUtil.isSpace(gameInfo.horde_id) || gameInfo.horde_id == "0") null else drawable_horde, null)
            val creatorInfo = gameInfo.creatorInfo
            tv_recentgame_checkin.setVisibility(if (gameInfo.isCheckin) View.VISIBLE else View.GONE)
            tv_recentgame_checkin.setText(R.string.match_player_status_checkin)//新需求，普通局和sng显示"游戏中"，mtt显示"已报名"
            if (!TextUtils.isEmpty(creatorInfo.avatar)) {
                iv_game_creator_userhead.loadBuddyAvatarByUrl(creatorInfo.account, creatorInfo.avatar, HeadImageView.DEFAULT_AVATAR_THUMB_SIZE)
            } else {
                iv_game_creator_userhead.loadBuddyAvatar(creatorInfo.account)
            }
            tv_game_name.setText(gameInfo.name)
            //游戏时间，比如 持续时间 涨盲时间 升底时间==
            tv_game_time.setCompoundDrawables(if (gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL || gameInfo.gameConfig is PineappleConfig) icon_club_chat_time else icon_club_chat_time_blind, null, null, null)
            var maxMemberCount = 9
            game_history_hunter_iv?.setVisibility(View.GONE)
            tv_pineapple_deal_order?.setVisibility(if (gameInfo.play_mode == GameConstants.PLAY_MODE_PINEAPPLE && gameInfo.gameConfig is PineappleConfig) View.VISIBLE else View.GONE)
            iv_game_mode.setVisibility(if (gameInfo.gameMode == GameConstants.GAME_MODE_SNG || gameInfo.gameMode == GameConstants.GAME_MODE_MTT) View.VISIBLE else View.GONE)
            if (gameInfo.play_mode == GameConstants.PLAY_MODE_PINEAPPLE && gameInfo.gameConfig is PineappleConfig) {
                //大菠萝普通    发牌顺序的设置
                tv_pineapple_deal_order.setBackgroundDrawable(bg_pineapple_deal_order)
                tv_pineapple_deal_order.setText(if ((gameInfo.gameConfig as PineappleConfig).deal_order == GameConstants.DEAL_ORDER_MEANWHILE) "同步发牌" else "顺序发牌")
                iv_game_insurance.setVisibility(View.GONE)
                tv_game_ante.setVisibility(View.GONE)
                tv_game_member.setText(gameInfo.gamerCount.toString() + "/" + (gameInfo.gameConfig as PineappleConfig).match_player)
                tv_pineapple_ante.setText((gameInfo.gameConfig as PineappleConfig).ante.toString() + "")
                tv_game_blind.setText((gameInfo.gameConfig as PineappleConfig).chips.toString() + "")
                tv_game_chickin_fee.setVisibility(View.GONE)
                tv_game_mode_status.setVisibility(View.GONE)
                tv_game_time.setText(GameConstants.getGameDurationShow((gameInfo.gameConfig as PineappleConfig).duration))
                //游戏中的标记
                tv_recentgame_checkin.setVisibility(if (gameInfo.activity == GameConstants.ACTIVITY_SIT) View.VISIBLE else View.GONE)
                tv_recentgame_checkin.setText("游戏中")
                if (gameInfo.status == GameStatus.GAME_STATUS_WAIT) {
                    tv_game_remaining_title.setVisibility(View.GONE)
                    tv_game_remaining_time.setVisibility(View.VISIBLE)
                    tv_game_remaining_time.setText(R.string.game_status_ready)
                } else {
                    tv_game_remaining_time.setVisibility(View.VISIBLE)
                    var passTime = gameInfo.currentServerTime - gameInfo.start_time//gameInfo.getCreateTime();//过去的时间
                    if (gameInfo.start_time <= 0) {//虽然status是1但是游戏内还未点击开始
                        passTime = 0
                    }
                    val remainingTime = (gameInfo.gameConfig as PineappleConfig).duration / 60 - (passTime / 60).toInt()//剩余的时间
                    if (remainingTime <= 0) {
                        tv_game_remaining_title.setVisibility(View.GONE)
                        tv_game_remaining_time.setText(R.string.game_status_finished)
                    } else {
                        tv_game_remaining_title.setVisibility(View.VISIBLE)
                        tv_game_remaining_time.setText(GameConstants.getShowRecentGameRemainTime(remainingTime))
                    }
                }
            } else if (gameInfo.play_mode == GameConstants.PLAY_MODE_PINEAPPLE && gameInfo.gameConfig is PineappleConfigMtt) {
                //大菠萝MTT模式
                val gameConfig = gameInfo.gameConfig as PineappleConfigMtt
                game_history_hunter_iv.setVisibility(if (gameConfig.ko_mode == 0) View.GONE else View.VISIBLE)
                tv_game_chickin_fee.setVisibility(View.VISIBLE)
                iv_game_mode.setImageResource(R.mipmap.icon_control_mtt)
                tv_game_mode_status.setVisibility(View.VISIBLE)
                tv_game_remaining_time.setVisibility(View.GONE)
                tv_game_remaining_title.setVisibility(View.GONE)
                iv_game_insurance.setVisibility(View.GONE)
                tv_game_ante.setVisibility(View.GONE)
                tv_game_blind.setText(gameConfig.matchChips.toString() + "")
                tv_game_chickin_fee.setText(gameConfig.matchCheckinFee.toString())
                val bg_gold = ChessApp.sAppContext.resources.getDrawable(R.mipmap.icon_club_chat_checkin_fee)
                bg_gold.setBounds(0, 0, bg_gold.intrinsicWidth, bg_gold.intrinsicHeight)
                val bg_diamond = ChessApp.sAppContext.resources.getDrawable(R.mipmap.icon_mtt_record_diamond)
                bg_diamond.setBounds(0, 0, bg_diamond.intrinsicWidth, bg_diamond.intrinsicHeight)
                //只有钻石赛才用"钻石"背景，"常规赛"和"金币赛"使用老的背景
                tv_game_chickin_fee.setCompoundDrawables(if (gameInfo.match_type == GameConstants.MATCH_TYPE_DIAMOND) bg_diamond else bg_gold, null, null, null)
                tv_game_time.setText(GameConstants.getGameSngDurationMinutesShow(gameConfig.matchDuration))
                //时间
                if (gameInfo.status == GameStatus.GAME_STATUS_WAIT) {
                    tv_game_mode_status.setText(R.string.checkin_ing)
                } else {
                    tv_game_mode_status.setText(R.string.game_ing)
                }
                tv_game_member.setText(gameInfo.checkinPlayerCount.toString())
            } else if (gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL && gameInfo.gameConfig is GameNormalConfig) {
                //普通模式
                val gameConfig = gameInfo.gameConfig as GameNormalConfig
                tv_game_chickin_fee.setVisibility(View.GONE)
                tv_game_mode_status.setVisibility(View.GONE)
                tv_game_blind.setText(GameConstants.getGameBlindsShow(gameConfig.blindType))
                tv_game_time.setText(GameConstants.getGameDurationShow(gameConfig.timeType))
                tv_recentgame_checkin.setVisibility(if (gameInfo.activity == GameConstants.ACTIVITY_SIT) View.VISIBLE else View.GONE)
                tv_recentgame_checkin.setText("游戏中")
                //保险模式
                if (gameConfig.tiltMode == GameConstants.GAME_MODE_INSURANCE_NOT) {
                    iv_game_insurance.setVisibility(View.INVISIBLE)
                } else {
                    iv_game_insurance.setVisibility(View.VISIBLE)
                }
                val ante = GameConstants.getGameAnte(gameConfig)
                if (ante == GameConstants.ANTE_TYPE_0) {
                    tv_game_ante.setVisibility(View.GONE)
                } else {
                    tv_game_ante.setVisibility(View.VISIBLE)
                    tv_game_ante.setText(String.format(itemView.context.getString(R.string.game_message_ante), ante))
                }
                //时间
                if (gameInfo.status == GameStatus.GAME_STATUS_WAIT) {
                    tv_game_remaining_title.setVisibility(View.GONE)
                    tv_game_remaining_time.setVisibility(View.VISIBLE)
                    tv_game_remaining_time.setText(R.string.game_status_ready)
                } else {
                    tv_game_remaining_time.setVisibility(View.VISIBLE)
                    var passTime = gameInfo.currentServerTime - gameInfo.start_time//gameInfo.getCreateTime();//过去的时间
                    if (gameInfo.start_time <= 0) {//虽然status是1但是游戏内还未点击开始
                        passTime = 0
                    }
                    val remainingTime = gameConfig.timeType / 60 - (passTime / 60).toInt()//剩余的时间
                    if (remainingTime <= 0) {
                        tv_game_remaining_title.setVisibility(View.GONE)
                        tv_game_remaining_time.setText(R.string.game_status_finished)
                    } else {
                        tv_game_remaining_title.setVisibility(View.VISIBLE)
                        tv_game_remaining_time.setText(GameConstants.getShowRecentGameRemainTime(remainingTime))
                    }
                }
                if (gameConfig.matchPlayer != 0) {
                    maxMemberCount = gameConfig.matchPlayer
                }
                tv_game_member.setText(gameInfo.gamerCount.toString() + "/" + maxMemberCount)
            } else if (gameInfo.gameMode == GameConstants.GAME_MODE_MTT && gameInfo.gameConfig is GameMttConfig) {
                //MTT模式
                val gameConfig = gameInfo.gameConfig as GameMttConfig
                game_history_hunter_iv.setVisibility(if (gameConfig.ko_mode == 0) View.GONE else View.VISIBLE)
                tv_game_chickin_fee.setVisibility(View.VISIBLE)
                iv_game_mode.setImageResource(R.mipmap.icon_control_mtt)
                tv_game_mode_status.setVisibility(View.VISIBLE)
                tv_game_remaining_time.setVisibility(View.GONE)
                tv_game_remaining_title.setVisibility(View.GONE)
                iv_game_insurance.setVisibility(View.GONE)
                tv_game_ante.setVisibility(View.GONE)
                tv_game_blind.setText(gameConfig.matchChips.toString() + "")
                tv_game_chickin_fee.setText(gameConfig.matchCheckinFee.toString())
                val bg_gold = ChessApp.sAppContext.resources.getDrawable(R.mipmap.icon_club_chat_checkin_fee)
                bg_gold.setBounds(0, 0, bg_gold.intrinsicWidth, bg_gold.intrinsicHeight)
                val bg_diamond = ChessApp.sAppContext.resources.getDrawable(R.mipmap.icon_mtt_record_diamond)
                bg_diamond.setBounds(0, 0, bg_diamond.intrinsicWidth, bg_diamond.intrinsicHeight)
                //只有钻石赛才用"钻石"背景，"常规赛"和"金币赛"使用老的背景
                tv_game_chickin_fee.setCompoundDrawables(if (gameInfo.match_type == GameConstants.MATCH_TYPE_DIAMOND) bg_diamond else bg_gold, null, null, null)
                tv_game_time.setText(GameConstants.getGameSngDurationMinutesShow(gameConfig.matchDuration))
                //时间
                if (gameInfo.status == GameStatus.GAME_STATUS_WAIT) {
                    tv_game_mode_status.setText(R.string.checkin_ing)
                } else {
                    tv_game_mode_status.setText(R.string.game_ing)
                }
                tv_game_member.setText(gameInfo.checkinPlayerCount.toString())
            } else if (gameInfo.gameMode == GameConstants.GAME_MODE_SNG && gameInfo.gameConfig is GameSngConfigEntity) {
                //SNG模式
                val gameConfig = gameInfo.gameConfig as GameSngConfigEntity
                maxMemberCount = gameConfig.getPlayer()
                tv_game_chickin_fee.setVisibility(View.VISIBLE)
                tv_game_mode_status.setVisibility(View.VISIBLE)
                tv_game_remaining_time.setVisibility(View.GONE)
                tv_game_remaining_title.setVisibility(View.GONE)
                iv_game_mode.setImageResource(R.mipmap.icon_control_sng)
                iv_game_insurance.setVisibility(View.GONE)
                tv_game_ante.setVisibility(View.GONE)
                tv_game_blind.setText(gameConfig.getChips().toString())
                tv_game_chickin_fee.setText(gameConfig.getCheckInFee().toString())
                tv_game_time.setText(GameConstants.getGameSngDurationMinutesShow(gameConfig.getDuration()))
                tv_recentgame_checkin.setVisibility(if (gameInfo.activity == GameConstants.ACTIVITY_SIT) View.VISIBLE else View.GONE)
                tv_recentgame_checkin.setText("游戏中")
                //时间
                if (gameInfo.status == GameStatus.GAME_STATUS_WAIT) {
                    tv_game_mode_status.setText(R.string.game_status_ready)
                } else {
                    tv_game_mode_status.setText(R.string.game_status_ing)
                }
                tv_game_member.setText(gameInfo.gamerCount.toString() + "/" + maxMemberCount)
            }
            tv_pineapple_ante.setVisibility(if (gameInfo.play_mode == GameConstants.PLAY_MODE_PINEAPPLE && gameInfo.gameConfig is PineappleConfig) View.VISIBLE else View.GONE)
            iv_pineapple_icon.setVisibility(if (gameInfo.play_mode == GameConstants.PLAY_MODE_PINEAPPLE) View.VISIBLE else View.GONE)
            if (gameInfo.play_mode != GameConstants.PLAY_MODE_OMAHA) {
                iv_omaha_icon.setVisibility(View.GONE)
            } else if (gameInfo.play_mode == GameConstants.PLAY_MODE_OMAHA) {
                iv_omaha_icon.setVisibility(View.VISIBLE)
                if (!adapter.omahaAnimationDone) {
                    AnimUtil.translateX(iv_omaha_icon, -ScreenUtil.dp2px(itemView.context, 40f), 0, 300)//图片的宽度是40dp
                }
            }
            if (gameInfo.play_mode == GameConstants.PLAY_MODE_PINEAPPLE) {
                if (gameInfo.gameConfig is PineappleConfig) {
                    iv_pineapple_icon.setImageResource(pineappleIconIdsHistory[(gameInfo.gameConfig as PineappleConfig).play_type])
                } else if (gameInfo.gameConfig is PineappleConfigMtt) {
                    iv_pineapple_icon.setImageResource(pineappleIconIdsHistory[(gameInfo.gameConfig as PineappleConfigMtt).play_type])
                }
                if (!adapter.omahaAnimationDone) {
                    AnimUtil.translateX(iv_pineapple_icon, -ScreenUtil.dp2px(itemView.context, 49f), 0, 300)//图片的宽度是49dp
                }
            }
            itemView.setOnClickListener { adapter.mClickListener?.onClick(position, gameInfo) }
            itemView.setOnLongClickListener {
                adapter.mClickListener?.onLongClick(position, gameInfo)
                true
            }
        }
    }
    class DiffCallback(var oldList: ArrayList<GameEntity>?, var newList: ArrayList<GameEntity>?) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            if (oldItemPosition < 0 || oldItemPosition >= (oldList?.size ?: -100) ||
                    newItemPosition < 0 || newItemPosition >= (newList?.size ?: -100)) {
                return true//两个数据源为空的话这个条件也会满足
            } else {
                return oldList!!.get(oldItemPosition).gid.equals(newList!!.get(newItemPosition).gid)
            }
        }

        override fun getOldListSize(): Int {
            return oldList?.size ?: 0
        }

        override fun getNewListSize(): Int {
            return newList?.size ?: 0
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            var isSame = true
            if (oldItemPosition < 0 || oldItemPosition >= (oldList?.size ?: -100) ||
                    newItemPosition < 0 || newItemPosition >= (newList?.size ?: -100)) {
                isSame = true//两个数据源为空的话这个条件也会满足
            } else {
                val oldEntity = oldList!!.get(oldItemPosition)
                val newEntity = newList!!.get(newItemPosition)
                if (oldEntity.status != newEntity.status ||
                        oldEntity.activity != newEntity.activity ||
                        oldEntity.isCheckin != newEntity.isCheckin ||
                        oldEntity.gamerCount != newEntity.gamerCount ||
                        oldEntity.isInvited != newEntity.isInvited ||
//                        oldEntity.tag != newEntity.tag ||
                        oldEntity.checkinPlayerCount != newEntity.checkinPlayerCount ||
                        oldEntity.start_time != newEntity.start_time ||
                        !oldEntity.gameConfig::class.java.name.equals(newEntity.gameConfig::class.java.name)) {
                    isSame = false
                }
                if (oldEntity.gameMode == GameConstants.GAME_MODE_NORMAL) {
                    if (oldEntity.status == GameStatus.GAME_STATUS_START) {
                        if (oldEntity.start_time > 0) {//满足喆三个条件表示游戏已经开始, 这时候只要时间在流逝那么显示内容就不一样
                            isSame = false
                        }
                    }
                }
            }
            return isSame
        }
    }

}