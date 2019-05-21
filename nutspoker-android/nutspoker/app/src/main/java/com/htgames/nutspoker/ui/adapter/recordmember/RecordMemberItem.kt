package com.htgames.nutspoker.ui.adapter.recordmember

import android.animation.Animator
import android.graphics.drawable.Drawable
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.htgames.nutspoker.R
import com.htgames.nutspoker.chat.contact.activity.UserProfileActivity
import com.htgames.nutspoker.game.match.reward.RewardAllot
import com.htgames.nutspoker.view.record.RankViewV
import com.netease.nim.uikit.bean.BaseMttConfig
import com.netease.nim.uikit.bean.GameBillEntity
import com.netease.nim.uikit.bean.GameMemberEntity
import com.netease.nim.uikit.bean.GameMttConfig
import com.netease.nim.uikit.cache.NimUserInfoCache
import com.netease.nim.uikit.common.DemoCache
import com.netease.nim.uikit.common.ui.imageview.HeadImageView
import com.netease.nim.uikit.common.util.string.StringUtil
import com.netease.nim.uikit.constants.GameConstants
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.helpers.AnimatorHelper
import eu.davidea.flexibleadapter.items.AbstractHeaderItem
import eu.davidea.flexibleadapter.items.AbstractItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.ISectionable
import eu.davidea.viewholders.FlexibleViewHolder

/**
 * Created by 周智慧 on 2017/7/24.
 */
class RecordMemberItem(var mGameMemberEntity: GameMemberEntity, var idStr: String) : AbstractItem<RecordMemberItem.BaseVH>(idStr), ISectionable<RecordMemberItem.BaseVH, AbstractHeaderItem<*>> {
    companion object {
        val TYPE_NORMAL = 0
        val TYPE_INSURANCE = 1
    }
    var bill: GameBillEntity? = null
    var mType = TYPE_NORMAL
    var mHeader: AbstractHeaderItem<*>? = null
    override fun getHeader(): AbstractHeaderItem<*>? {
        return mHeader
    }

    override fun setHeader(header: AbstractHeaderItem<*>?) {
        mHeader = header
    }

    override fun getLayoutRes(): Int {
        return R.layout.list_record_member
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): BaseVH {
        return BaseVH(view, adapter, bill)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: BaseVH, position: Int, payloads: MutableList<Any?>?) {
        var isChecked: Boolean = adapter is RecordMemberAdap && adapter.checkPosition == position
        holder.bind(mGameMemberEntity, position, isChecked, mType == TYPE_INSURANCE)
    }

    open class BaseVH(view: View, adapter: FlexibleAdapter<*>, var bill: GameBillEntity? = null) : FlexibleViewHolder(view, adapter) {
        var mClick = (adapter as? RecordMemberAdap)?.mListener
        var record_detail_player_info_container = itemView.findViewById(R.id.record_detail_player_info_container)
        var iv_game_userhead = itemView.findViewById(R.id.iv_game_userhead) as HeadImageView
        var iv_insurance_tag = itemView.findViewById(R.id.iv_insurance_tag) as ImageView
        var mRankView = itemView.findViewById(R.id.mRankView) as RankViewV
        var tv_game_earnings = itemView.findViewById(R.id.tv_game_earnings) as TextView
        var tv_game_name = itemView.findViewById(R.id.tv_game_name) as TextView
        var tv_game_buys = itemView.findViewById(R.id.tv_game_buys) as TextView
        var record_member_hunter_info = itemView.findViewById(R.id.record_member_hunter_info) as TextView
        var record_member_hunter_info_container = itemView.findViewById(R.id.record_member_hunter_info_container)
        var record_member_hunter_reward = itemView.findViewById(R.id.record_member_hunter_reward) as TextView
        var tv_rebuy_cnt = itemView.findViewById(R.id.tv_rebuy_cnt) as TextView
        var tv_addon_cnt = itemView.findViewById(R.id.tv_addon_cnt) as TextView
        var tv_uuid = itemView.findViewById(R.id.tv_uuid) as TextView
        var context = DemoCache.getContext()
        var ko_head_cnt_container = itemView.findViewById(R.id.ko_head_cnt_container)
        var ko_reward_container = itemView.findViewById(R.id.ko_reward_container)
        var tv_opt_user = itemView.findViewById(R.id.tv_opt_user) as TextView
        fun bind(mGameMemberEntity: GameMemberEntity, position: Int, isChecked: Boolean, isInsurance: Boolean) {
            itemView.setOnClickListener {
                mClick?.onClick(position)
            }
            iv_game_userhead.setOnClickListener {
                var account = mGameMemberEntity.userInfo.account
                if (!StringUtil.isEmpty(account) && DemoCache.getAccount() != account) {
                    UserProfileActivity.start((mAdapter as RecordMemberAdap).mActivity, account)
                }
            }
            var gameEntity = bill?.gameInfo ?: (mAdapter as RecordMemberAdap).mGameInfo
            var gameMode = gameEntity?.gameMode
            record_member_hunter_info_container.visibility = View.GONE
            val userInfo = mGameMemberEntity.userInfo
            tv_uuid.text = "ID: " + mGameMemberEntity.userInfo.uuid
            tv_uuid.visibility = if (StringUtil.isSpace(mGameMemberEntity.userInfo.uuid) || "0" == mGameMemberEntity.userInfo.uuid) View.GONE else View.VISIBLE
            tv_game_name.text = NimUserInfoCache.getInstance().getUserDisplayName(mGameMemberEntity.userInfo.account)//(/*UserInfoShowHelper.getUserNickname(userInfo)*/userInfo.name)
            iv_insurance_tag.visibility = if (mGameMemberEntity.insurance != 0) View.VISIBLE else View.GONE
            if (isInsurance) {
                tv_game_buys.text = ""//tv_game_buys.setText("" + mGameMemberEntity.getPremium());投保不显示了
                record_detail_player_info_container.setBackgroundResource(R.drawable.common_column_bg)
                if (mAdapter is RecordMemberAdap && !mAdapter.isMeGameMgr) {
                    itemView.visibility = View.GONE//只有房主和管理员才能看到保险部分的列表
                } else {
                    itemView.visibility = View.VISIBLE//只有房主和管理员才能看到保险部分的列表
                }
            } else {
                tv_game_buys.text = "" + mGameMemberEntity.totalBuy
                if (isChecked) {
                    record_detail_player_info_container.setBackgroundColor(context.resources.getColor(R.color.list_item_bg_press))
                } else {
                    record_detail_player_info_container.setBackgroundResource(R.drawable.common_column_bg)
                }
            }
            var winChip = mGameMemberEntity.winChip + mGameMemberEntity.insurance
            if (gameMode == GameConstants.GAME_MODE_SNG || gameMode == GameConstants.GAME_MODE_MTT || gameMode == GameConstants.GAME_MODE_MT_SNG) {
                winChip = mGameMemberEntity.reward
            } else if (isInsurance) {
                winChip = mGameMemberEntity.insurance //- mGameMemberEntity.getPremium();投保不显示了
            }
            tv_game_buys.visibility = if (gameMode == GameConstants.GAME_MODE_NORMAL) View.VISIBLE else View.GONE
            if (gameMode == GameConstants.GAME_MODE_NORMAL) {
                mRankView.visibility = View.GONE
            } else if (gameMode == GameConstants.GAME_MODE_SNG || gameMode == GameConstants.GAME_MODE_MTT || gameMode == GameConstants.GAME_MODE_MT_SNG) {
                mRankView.visibility = View.VISIBLE
                val rank = mGameMemberEntity.ranking
                mRankView.setRankTagView(rank)
            }
            iv_game_userhead.loadBuddyAvatarByUrl(userInfo.account, userInfo.avatar, HeadImageView.DEFAULT_AVATAR_THUMB_SIZE)
            //自己
            tv_game_name.setTextColor(context.resources.getColor(if (DemoCache.getAccount() == userInfo.account) R.color.login_solid_color else R.color.text_select_color))
            if (gameMode == GameConstants.GAME_MODE_MTT) {
                val rebuyCnt = mGameMemberEntity.rebuyCnt
                val addonCnt = mGameMemberEntity.addonCnt
                //增购，重购
                if (rebuyCnt > 0) {
                    tv_rebuy_cnt.visibility = View.VISIBLE
                    tv_rebuy_cnt.text = "R" + rebuyCnt
                } else {
                    tv_rebuy_cnt.visibility = View.GONE
                }
                if (addonCnt > 0) {
                    tv_addon_cnt.visibility = View.GONE//tv_addon_cnt.setVisibility(View.VISIBLE);
                    tv_addon_cnt.text = "A"
                } else {
                    tv_addon_cnt.visibility = View.GONE
                }
                //猎人赛
                if (gameEntity != null && gameEntity.gameConfig is BaseMttConfig && (gameEntity.gameConfig as BaseMttConfig).ko_mode != 0) {
                    //猎人赛总container
                    record_member_hunter_info_container.visibility = View.VISIBLE
                    //普通猎人赛
                    if (mAdapter is RecordMemberAdap && mAdapter.mGameInfo != null && mAdapter.mGameInfo!!.match_type != GameConstants.MATCH_TYPE_NORMAL) {
                        record_member_hunter_info.text = mGameMemberEntity.ko_head_cnt
                    } else {
                        record_member_hunter_info.text = mGameMemberEntity.ko_head_cnt + "/" + mGameMemberEntity.ko_head_reward
                    }
                    ko_head_cnt_container.visibility = if ((gameEntity.gameConfig as BaseMttConfig).ko_mode == 1) View.VISIBLE else View.GONE
                    //超级猎人赛
                    record_member_hunter_reward.text = "" + mGameMemberEntity.ko_head_reward
                    ko_reward_container.visibility = if ((gameEntity.gameConfig as BaseMttConfig).ko_mode == 2) View.VISIBLE else View.GONE
                    winChip = winChip + mGameMemberEntity.ko_head_reward//猎人赛的话需要加上猎头赏金
                }
            } else {
                tv_rebuy_cnt.visibility = View.GONE
                tv_addon_cnt.visibility = View.GONE
            }
            tv_game_earnings.text = "" + (if (winChip > 0) "+" else "") + winChip
            tv_game_earnings.setTextColor(DemoCache.getContext().resources.getColor(if (winChip >= 0) R.color.record_list_earn_yes else R.color.record_list_earn_no))
            tv_opt_user.setText(mGameMemberEntity.opt_user)
            //新需求，1：房主能看到所有的通过人，2：但是管理员只能看到奖励圈之内的通过人并且不能看到uuid（tips: 只有mtt有这个需求）
            if (DemoCache.getAccount() == gameEntity!!.creatorInfo.account) {
                tv_opt_user.visibility = if (StringUtil.isSpace(mGameMemberEntity.opt_user)) View.GONE else View.VISIBLE
            } else if (gameMode == GameConstants.GAME_MODE_MTT) {
                val rewardList = RewardAllot.getListSize(gameEntity!!.checkinPlayerCount)//奖励圈的人数
                tv_opt_user.visibility = if (StringUtil.isSpace(mGameMemberEntity.opt_user)) View.GONE else View.VISIBLE
                if (position >= rewardList) {//mtt牌局对于管理员来说，在奖励圈之外的玩家，看不到通过人
                    tv_opt_user.visibility = View.GONE
                }
            }

            //下面是"钻石赛"的特殊处理
            if (mAdapter is RecordMemberAdap && mAdapter.mGameInfo != null && mAdapter.mGameInfo!!.match_type != GameConstants.MATCH_TYPE_NORMAL) {//只有mtt才有钻石赛
                mRankView.visibility = View.GONE
                var bg_rank_1: Drawable = mAdapter.mActivity.resources.getDrawable(R.mipmap.mtt_room_rank_1)
                bg_rank_1.setBounds(0, 0, bg_rank_1.intrinsicWidth, bg_rank_1.intrinsicHeight)
                var bg_rank_2: Drawable = mAdapter.mActivity.resources.getDrawable(R.mipmap.mtt_room_rank_2)
                bg_rank_2.setBounds(0, 0, bg_rank_2.intrinsicWidth, bg_rank_2.intrinsicHeight)
                var bg_rank_3: Drawable = mAdapter.mActivity.resources.getDrawable(R.mipmap.mtt_room_rank_3)
                bg_rank_3.setBounds(0, 0, bg_rank_3.intrinsicWidth, bg_rank_3.intrinsicHeight)
                tv_game_earnings.text = ""
                if (mGameMemberEntity.ranking == 1) {
                    tv_game_earnings.setCompoundDrawables(null, null, bg_rank_1, null)
                } else if (mGameMemberEntity.ranking == 2) {
                    tv_game_earnings.setCompoundDrawables(null, null, bg_rank_2, null)
                } else if (mGameMemberEntity.ranking == 3) {
                    tv_game_earnings.setCompoundDrawables(null, null, bg_rank_3, null)
                } else {
                    tv_game_earnings.setCompoundDrawables(null, null, null, null)
                    tv_game_earnings.text = "${mGameMemberEntity.ranking}"
                }
                tv_game_earnings.setTextColor(DemoCache.getContext().resources.getColor(if (winChip >= 0) R.color.record_list_earn_yes else R.color.record_list_earn_no))
                if (gameEntity != null && gameEntity.gameConfig is BaseMttConfig && (gameEntity.gameConfig as BaseMttConfig).ko_mode != 0) {
                    //猎人赛总container
                    record_member_hunter_info_container.visibility = View.VISIBLE
                    //普通猎人赛
                    record_member_hunter_info.setText(mGameMemberEntity.ko_head_cnt)
                    ko_head_cnt_container.visibility = if ((gameEntity.gameConfig as BaseMttConfig).ko_mode == 1) View.VISIBLE else View.GONE
                    //超级猎人赛
                    record_member_hunter_reward.text = "" + mGameMemberEntity.ko_head_reward
                    ko_reward_container.visibility = if ((gameEntity.gameConfig as BaseMttConfig).ko_mode == 2) View.VISIBLE else View.GONE
                }
            }
        }

        override fun scrollAnimators(animators: MutableList<Animator>, position: Int, isForward: Boolean) {
            if (mAdapter.recyclerView.layoutManager is GridLayoutManager || mAdapter.recyclerView.layoutManager is StaggeredGridLayoutManager) {
                if (position % 2 != 0)
                    AnimatorHelper.slideInFromRightAnimator(animators, itemView, mAdapter.recyclerView, 0.5f)
                else
                    AnimatorHelper.slideInFromLeftAnimator(animators, itemView, mAdapter.recyclerView, 0.5f)
            } else {
                if (isForward)
                    AnimatorHelper.slideInFromBottomAnimator(animators, itemView, mAdapter.recyclerView)
                else
                    AnimatorHelper.slideInFromTopAnimator(animators, itemView, mAdapter.recyclerView)
            }
        }
    }
}