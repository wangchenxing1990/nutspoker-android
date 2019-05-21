package com.htgames.nutspoker.ui.adapter.record

import android.animation.Animator
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.htgames.nutspoker.R
import com.htgames.nutspoker.config.GameConfigData.pineappleIconIdsHistory
import com.htgames.nutspoker.ui.helper.RecordHelper
import com.htgames.nutspoker.view.record.RankView
import com.netease.nim.uikit.bean.*
import com.netease.nim.uikit.common.DateTools
import com.netease.nim.uikit.common.DemoCache
import com.netease.nim.uikit.common.ui.imageview.HeadImageView
import com.netease.nim.uikit.common.util.SpanUtils
import com.netease.nim.uikit.common.util.string.StringUtil
import com.netease.nim.uikit.constants.GameConstants
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.*
import eu.davidea.viewholders.FlexibleViewHolder
import java.io.Serializable
import java.text.SimpleDateFormat

/**
 * Created by 周智慧 on 2017/7/18.
 */
class RecordListItem(var mBill: GameBillEntity? = null, var mLastBill: GameBillEntity? = null) : AbstractItem<RecordListItem.BaseViewVH>(mBill?.gameInfo?.gid), ISectionable<RecordListItem.BaseViewVH, IHeader<*>>, IFilterable, Serializable {
    override fun getHeader(): IHeader<FlexibleViewHolder>? {
        return null
    }

    override fun setHeader(header: IHeader<*>?) {
    }

    override fun filter(constraint: String?): Boolean {
        return true
    }

    companion object {
        val ITEM_TYPE_NORMAL = 0//战绩进去的
        val ITEM_TYPE_TEAM_HORDE = 1//俱乐部战绩或者部落战绩

        val RECORD_TYPE_TEXAS_NORMAL = GameConstants.GAME_MODE_NORMAL//普通牌局
        val RECORD_TYPE_TEXAS_SNG = GameConstants.GAME_MODE_SNG//普通牌局
        val RECORD_TYPE_TEXAS_MTT = GameConstants.GAME_MODE_MTT//普通牌局

        val RECORD_TYPE_OMAHA_NORMAL = 4//普通牌局
        val RECORD_TYPE_OMAHA_SNG = 5//普通牌局
        val RECORD_TYPE_OMAHA_MTT = 7//普通牌局val RECORD_TYPE_TEXAS_NORMAL = 0//普通牌局

        val RECORD_TYPE_PINEAPPLE_NORMAL = 9//普通牌局
        val RECORD_TYPE_PINEAPPLE_MTT = 12//普通牌局
    }

    var itemType = ITEM_TYPE_NORMAL
    var recordType = RECORD_TYPE_TEXAS_NORMAL
    init {
        mBill?.let {//或者apply也行
            var gameEntity = mBill!!.gameInfo
            if (gameEntity.play_mode == GameConstants.PLAY_MODE_TEXAS_HOLDEM) {
                recordType = gameEntity.gameMode
            } else if (gameEntity.play_mode == GameConstants.PLAY_MODE_OMAHA) {
                recordType = gameEntity.gameMode + 4
            } else if (gameEntity.play_mode == GameConstants.PLAY_MODE_PINEAPPLE) {
                recordType = gameEntity.gameMode + 9
            }
        }
    }
    override fun getLayoutRes(): Int {
        return R.layout.list_game_record_item_new
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): BaseViewVH {
        return BaseViewVH(itemType, recordType, view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: BaseViewVH, position: Int, payloads: MutableList<Any?>?) {
        if (mBill == null) {
            return
        }
        holder.bind(mBill!!, mBill!!.gameInfo, position, mLastBill)
    }



    open class BaseViewVH(itemType: Int, recordType: Int, view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter) {
        var tv_game_date_node_day: TextView = itemView.findViewById(R.id.tv_game_date_node_day) as TextView
        var tv_game_date_node_month: TextView = itemView.findViewById(R.id.tv_game_date_node_month) as TextView
        var ck_select: CheckBox = itemView.findViewById(R.id.ck_select) as CheckBox
        var tv_game_create_time: TextView = itemView.findViewById(R.id.tv_game_create_time) as TextView
        var tv_game_match_chips: TextView = itemView.findViewById(R.id.tv_game_match_chips) as TextView
        var mRankView: RankView = itemView.findViewById(R.id.mRankView) as RankView
        var iv_game_creator_userhead: HeadImageView = itemView.findViewById(R.id.iv_game_creator_userhead) as HeadImageView
        var tv_game_name: TextView = itemView.findViewById(R.id.tv_game_name) as TextView
        var tv_game_blind: TextView = itemView.findViewById(R.id.tv_game_blind) as TextView
        var tv_game_member: TextView = itemView.findViewById(R.id.tv_game_member) as TextView
        var tv_game_duration: TextView = itemView.findViewById(R.id.tv_game_duration) as TextView
        var tv_game_ante: TextView = itemView.findViewById(R.id.tv_game_ante) as TextView
        var iv_game_insurance: TextView = itemView.findViewById(R.id.iv_game_insurance) as TextView
        var iv_game_mode: ImageView = itemView.findViewById(R.id.iv_game_mode) as ImageView
        var game_record_hunter_iv: ImageView = itemView.findViewById(R.id.game_record_hunter_iv) as ImageView
        var tv_game_earnings: TextView = itemView.findViewById(R.id.tv_game_earnings) as TextView
        var horde_marker: View = itemView.findViewById(R.id.horde_marker)//是否是部落牌局
        var icon_club_chat_time: Drawable = itemView.context.resources.getDrawable(R.mipmap.icon_club_chat_time)
        var icon_club_chat_time_blind: Drawable = itemView.context.resources.getDrawable(R.mipmap.icon_club_chat_time_blind)
        var arrowDrawable: Drawable = itemView.context.resources.getDrawable(R.drawable.icon_common_arrow)
        var iv_omaha_icon: ImageView = itemView.findViewById(R.id.iv_omaha_icon) as ImageView
        var tv_pineapple_ante: TextView = itemView.findViewById(R.id.tv_pineapple_ante) as TextView
        var iv_pineapple_icon: ImageView = itemView.findViewById(R.id.iv_pineapple_icon) as ImageView
        var showMemberInfo: GameMemberEntity? = null
        var winChip: Int = 0
        var itemType = itemType
        var recordType = recordType
        var mBill: GameBillEntity? = null
        var mGameInfo: GameEntity? = null
        //金币赛和钻石赛的背景
        var bg_gold: Drawable = itemView.context.resources.getDrawable(R.mipmap.icon_club_chat_checkin_fee)
        var bg_diamond: Drawable = itemView.context.resources.getDrawable(R.mipmap.icon_mtt_record_diamond)
        init {
            icon_club_chat_time.setBounds(0, 0, icon_club_chat_time.intrinsicWidth, icon_club_chat_time.intrinsicHeight)
            icon_club_chat_time_blind.setBounds(0, 0, icon_club_chat_time_blind.intrinsicWidth, icon_club_chat_time_blind.intrinsicHeight)
            bg_gold.setBounds(0, 0, bg_gold.intrinsicWidth, bg_gold.intrinsicHeight)
            bg_diamond.setBounds(0, 0, bg_diamond.intrinsicWidth, bg_diamond.intrinsicHeight)
        }
        open fun bind(billEntity: GameBillEntity, gameInfo: GameEntity, position: Int, lastBillEntity: GameBillEntity?) {
            mBill = billEntity
            mGameInfo = gameInfo
            val res: Resources = itemView.context.resources
            showMemberInfo = billEntity.myMemberInfo
            winChip = billEntity.winChip + if (showMemberInfo != null) showMemberInfo!!.insurance else 0
            tv_game_name.text = gameInfo.name
            val creatorInfo = gameInfo.creatorInfo
            iv_game_creator_userhead.loadBuddyAvatarByUrl(creatorInfo.account, creatorInfo.avatar, HeadImageView.DEFAULT_AVATAR_THUMB_SIZE)
            tv_game_duration.setCompoundDrawables(if (gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL || gameInfo.gameConfig is PineappleConfig) icon_club_chat_time else icon_club_chat_time_blind, null, null, null)
            tv_game_create_time.text = DateTools.getStrTime_hm("" + gameInfo.endTime)
            val showDay = DateTools.getRecordTimeNodeDayDisplay("" + gameInfo.endTime)
            val showMonth = DateTools.getRecordTimeNodeMonth("" + gameInfo.endTime)
            val sdf = SimpleDateFormat("MM/dd")
            val monthDay = sdf.format(gameInfo.endTime * 1000)
            if (position != 0) {
                if (lastBillEntity != null && DateTools.isTheSameDay(lastBillEntity.gameInfo.endTime, gameInfo.endTime)) {
                    //判断是否是同一天
                    tv_game_date_node_day.visibility = View.INVISIBLE
                    tv_game_date_node_month.visibility = View.GONE
                } else {
                    tv_game_date_node_day.visibility = View.VISIBLE
                    tv_game_date_node_day.text = monthDay//tv_game_date_node_day.setText(showDay);
                    tv_game_date_node_month.text = showMonth
                    if (showDay == res.getString(R.string.today) || showDay == res.getString(R.string.yesterday)) {
                        tv_game_date_node_month.visibility = View.GONE
                    } else {
                        //                    tv_game_date_node_month.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                tv_game_date_node_day.visibility = View.VISIBLE
                tv_game_date_node_month.text = showMonth
                tv_game_date_node_day.text = monthDay//tv_game_date_node_day.setText(showDay);
                if (showDay == res.getString(R.string.today) || showDay == res.getString(R.string.yesterday)) {
                    tv_game_date_node_month.visibility = View.GONE
                } else {
                    //                tv_game_date_node_month.setVisibility(View.VISIBLE);
                }
            }
            tv_pineapple_ante.visibility = if (gameInfo.play_mode == GameConstants.PLAY_MODE_PINEAPPLE && gameInfo.gameConfig is PineappleConfig) View.VISIBLE else View.GONE
            iv_pineapple_icon.visibility = if (gameInfo.play_mode == GameConstants.PLAY_MODE_PINEAPPLE) View.VISIBLE else View.GONE
            itemView.setOnClickListener {
                (mAdapter as RecordListAdap).mListener.onClick(position)
            }
            horde_marker.visibility = if (StringUtil.isSpace(gameInfo.horde_name)) View.GONE else View.VISIBLE
            iv_omaha_icon.visibility = if (gameInfo.play_mode == GameConstants.PLAY_MODE_OMAHA) View.VISIBLE else View.GONE
            game_record_hunter_iv.visibility = View.GONE
            iv_game_mode.visibility = View.GONE
            tv_game_member.visibility = View.GONE
            tv_game_match_chips.visibility = View.GONE
            mRankView.visibility = View.GONE
            ck_select.visibility = View.GONE
            tv_game_blind.visibility = View.GONE
            iv_game_insurance.visibility = View.GONE
            tv_game_ante.visibility = View.GONE
            if (gameInfo.play_mode == GameConstants.PLAY_MODE_PINEAPPLE && gameInfo.gameConfig is PineappleConfig) {
                bindPineappleNormal()
            } else if (gameInfo.play_mode == GameConstants.PLAY_MODE_PINEAPPLE && gameInfo.gameConfig is PineappleConfigMtt) {
                bindPineappleMtt()
            } else if (gameInfo.play_mode == GameConstants.PLAY_MODE_TEXAS_HOLDEM && gameInfo.gameConfig is GameNormalConfig) {
                bindNormal()
            } else if (gameInfo.play_mode == GameConstants.PLAY_MODE_TEXAS_HOLDEM && gameInfo.gameConfig is GameSngConfigEntity) {
                bindSng()
            } else if (gameInfo.play_mode == GameConstants.PLAY_MODE_TEXAS_HOLDEM && gameInfo.gameConfig is GameMttConfig) {
                bindMtt()
            } else if (gameInfo.play_mode == GameConstants.PLAY_MODE_OMAHA && gameInfo.gameConfig is GameNormalConfig) {
                bindNormal()
            } else if (gameInfo.play_mode == GameConstants.PLAY_MODE_OMAHA && gameInfo.gameConfig is GameSngConfigEntity) {
                bindSng()
            } else if (gameInfo.play_mode == GameConstants.PLAY_MODE_OMAHA && gameInfo.gameConfig is GameMttConfig) {
                bindMtt()
            }
            if (itemType == ITEM_TYPE_TEAM_HORDE) {
                tv_game_earnings.text = ""//俱乐部战绩不显示盈利
                arrowDrawable.setBounds(0, 0, arrowDrawable.intrinsicWidth, arrowDrawable.intrinsicHeight)
                tv_game_earnings.setCompoundDrawables(arrowDrawable, null, null, null)
            }
        }

        private fun bindPineappleNormal() {
            tv_game_blind.visibility = View.VISIBLE
            tv_game_blind.text = (mGameInfo!!.gameConfig as PineappleConfig).chips.toString() + ""
            tv_pineapple_ante.text = (mGameInfo!!.gameConfig as PineappleConfig).ante.toString() + ""
            tv_game_duration.text = GameConstants.getGameSngDurationMinutesShow((mGameInfo!!.gameConfig as PineappleConfig).duration)
            if (mGameInfo!!.gameConfig is PineappleConfig) {
                iv_pineapple_icon.setImageResource(pineappleIconIdsHistory[(mGameInfo!!.gameConfig as PineappleConfig).play_type])
            }
            if (itemType == ITEM_TYPE_NORMAL) {
                RecordHelper.setRecordGainView(tv_game_earnings, winChip, mGameInfo!!.gameMode)
                tv_game_earnings.setTextColor(if (winChip >= 0) DemoCache.getContext().resources.getColor(R.color.record_list_earn_yes) else DemoCache.getContext().resources.getColor(R.color.record_list_earn_no))
            }
        }

        private fun bindPineappleMtt() {
            val gameConfig = mGameInfo!!.gameConfig as PineappleConfigMtt
            iv_pineapple_icon.setImageResource(pineappleIconIdsHistory[gameConfig.play_type])
            game_record_hunter_iv.visibility = if (gameConfig.ko_mode == 0) View.GONE else View.VISIBLE
            tv_game_match_chips.visibility = View.VISIBLE
//            tv_record_sng_rank_tag.setVisibility(View.VISIBLE);
            mRankView.visibility = View.VISIBLE
            tv_game_member.visibility = View.VISIBLE
            iv_game_mode.setImageResource(R.mipmap.icon_control_mtt)
            iv_game_mode.visibility = View.VISIBLE
            tv_game_match_chips.text = "" + gameConfig.matchCheckinFee
            tv_game_member.text = "" + mBill!!.totalPlayer
            tv_game_duration.text = GameConstants.getGameSngDurationMinutesShow(gameConfig.matchDuration)
            if (showMemberInfo != null) {
                winChip = showMemberInfo!!.reward
                val rank = showMemberInfo!!.ranking
                //                SngHelper.setSngRankTagView(tv_record_sng_rank_tag, true , rank);
                mRankView.setRankTagView(rank)
            }
            setMatchType()
        }

        private fun bindNormal() {
            val gameConfig = mGameInfo!!.gameConfig as GameNormalConfig
            tv_game_blind.visibility = View.VISIBLE
            tv_game_blind.text = GameConstants.getGameBlindsShow(gameConfig.blindType)//     gameConfig.min_chips + ""
            tv_game_duration.text = GameConstants.getGameDurationShow(gameConfig.timeType)
            val ante = GameConstants.getGameAnte(gameConfig)
            if (ante == GameConstants.ANTE_TYPE_0) {
                tv_game_ante.visibility = View.GONE
            } else {
                tv_game_ante.visibility = View.VISIBLE
                tv_game_ante.text = String.format(itemView.context.resources.getString(R.string.game_message_ante), ante)
            }
            //保险模式
            if (gameConfig.tiltMode == GameConstants.GAME_MODE_INSURANCE_NOT) {
                iv_game_insurance.visibility = View.GONE
            } else {
                iv_game_insurance.visibility = View.VISIBLE
            }
            if (itemType == ITEM_TYPE_NORMAL) {
                RecordHelper.setRecordGainView(tv_game_earnings, winChip, mGameInfo!!.gameMode)
                tv_game_earnings.setTextColor(if (winChip >= 0) DemoCache.getContext().resources.getColor(R.color.record_list_earn_yes) else DemoCache.getContext().resources.getColor(R.color.record_list_earn_no))
            }
        }

        private fun bindSng() {
            val gameConfig = mGameInfo!!.gameConfig as GameSngConfigEntity
            tv_game_match_chips.visibility = View.VISIBLE
//            tv_record_sng_rank_tag.setVisibility(View.VISIBLE);
            mRankView.visibility = View.VISIBLE
            tv_game_member.visibility = View.VISIBLE
            iv_game_mode.setImageResource(R.mipmap.icon_control_sng)
            iv_game_mode.visibility = View.VISIBLE
            tv_game_match_chips.text = "" + gameConfig.checkInFee//显示报名费而不是记分牌
            tv_game_member.text = "" + gameConfig.getPlayer()
            tv_game_duration.text = GameConstants.getGameSngDurationMinutesShow(gameConfig.getDuration())
            if (showMemberInfo != null) {
                winChip = showMemberInfo!!.reward
                val rank = showMemberInfo!!.ranking
                mRankView.setRankTagView(rank)
            }
            if (itemType == ITEM_TYPE_NORMAL) {
                RecordHelper.setRecordGainView(tv_game_earnings, winChip, mGameInfo!!.gameMode)
                tv_game_earnings.setTextColor(if (winChip >= 0) DemoCache.getContext().resources.getColor(R.color.record_list_earn_yes) else DemoCache.getContext().resources.getColor(R.color.record_list_earn_no))
            }
        }

        private fun bindMtt() {
            val gameConfig = mGameInfo!!.gameConfig as GameMttConfig
            game_record_hunter_iv.visibility = if (gameConfig.ko_mode == 0) View.GONE else View.VISIBLE
            tv_game_match_chips.visibility = View.VISIBLE
//            tv_record_sng_rank_tag.setVisibility(View.VISIBLE);
            mRankView.visibility = View.VISIBLE
            tv_game_member.visibility = View.VISIBLE
            iv_game_mode.setImageResource(R.mipmap.icon_control_mtt)
            iv_game_mode.visibility = View.VISIBLE
            tv_game_match_chips.text = "" + gameConfig.matchCheckinFee
            tv_game_member.text = "" + mBill!!.totalPlayer
            tv_game_duration.text = GameConstants.getGameSngDurationMinutesShow(gameConfig.matchDuration)
            if (showMemberInfo != null) {
                winChip = showMemberInfo!!.reward
                val rank = showMemberInfo!!.ranking
                //                SngHelper.setSngRankTagView(tv_record_sng_rank_tag, true , rank);
                mRankView.setRankTagView(rank)
            }
            setMatchType()
        }

        fun setMatchType() {
            tv_game_match_chips.setCompoundDrawables(if (mGameInfo!!.match_type == GameConstants.MATCH_TYPE_DIAMOND) bg_diamond else bg_gold, null, null, null)
//            if (mGameInfo != null && mGameInfo!!.gameConfig is BaseMttConfig && (mGameInfo!!.gameConfig as BaseMttConfig).ko_mode != 0) {
//                winChip += showMemberInfo!!.ko_head_reward//猎人赛的话需要加上猎头赏金
//            }
            if (itemType == ITEM_TYPE_NORMAL) {
                if (mGameInfo!!.match_type == 0) {//普通的mtt(金币赛)
                    RecordHelper.setRecordGainView(tv_game_earnings, winChip, mGameInfo!!.gameMode)
                    tv_game_earnings.setTextColor(if (winChip >= 0) DemoCache.getContext().resources.getColor(R.color.record_list_earn_yes) else DemoCache.getContext().resources.getColor(R.color.record_list_earn_no))
                } else {//mtt(钻石赛) 或者 金币赛
                    var rankDiamondMatch: String = if (showMemberInfo!!.ranking <= 0) "无" else "${showMemberInfo!!.ranking}"
                    var color = itemView.context.resources.getColor(R.color.diamond_match_rank_color)
                    mRankView.visibility = View.GONE
                    tv_game_earnings.text = SpanUtils().append("排名：").setForegroundColor(color).setFontSize(11, true)
                            .append(rankDiamondMatch).setForegroundColor(color).setFontSize(20, true)
                            .create()
                }
            }
        }

        override fun scrollAnimators(animators: MutableList<Animator>, position: Int, isForward: Boolean) {
            super.scrollAnimators(animators, position, isForward)
        }
    }
}