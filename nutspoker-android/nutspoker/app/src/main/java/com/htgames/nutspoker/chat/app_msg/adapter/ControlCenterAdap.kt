package com.htgames.nutspoker.chat.app_msg.adapter

import android.app.Activity
import android.content.Context
import android.os.CountDownTimer
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.htgames.nutspoker.R
import com.htgames.nutspoker.chat.app_msg.attach.BuyChipsNotify
import com.htgames.nutspoker.chat.app_msg.attach.MatchBuyChipsNotify
import com.htgames.nutspoker.chat.app_msg.helper.AppMessageHelper
import com.htgames.nutspoker.chat.app_msg.model.AppMessage
import com.htgames.nutspoker.chat.app_msg.model.AppMessageStatus
import com.htgames.nutspoker.db.AppMsgDBHelper
import com.htgames.nutspoker.game.match.interfaces.IMsgExpired
import com.htgames.nutspoker.game.model.GameMatchBuyType
import com.netease.nim.uikit.cache.NimUserInfoCache
import com.netease.nim.uikit.common.DemoCache
import com.netease.nim.uikit.common.util.log.LogUtil
import com.netease.nim.uikit.common.util.string.StringUtil
import com.netease.nim.uikit.common.util.sys.TimeUtil
import com.netease.nim.uikit.constants.GameConstants
import com.netease.nim.uikit.interfaces.IClickPayload
import com.netease.nim.uikit.interfaces.IOperationClick

class ControlCenterAdap : RecyclerView.Adapter<ControlCenterAdap.AppControlVH> {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AppControlVH {
        LogUtil.i("test_recyclerview_ControlCenterAdap", "onCreateViewHolder")
        return AppControlVH(LayoutInflater.from(mActivity).inflate(R.layout.viewholder_app_buychips_new, parent, false))
    }

    override fun onBindViewHolder(holder: AppControlVH?, position: Int) {
        LogUtil.i("test_recyclerview_ControlCenterAdap", "onBindViewHolder: $position")
        if (position < 0 || position >= mDatas.size) {
            return
        }
        val appMsg = mDatas.get(position)
        val lastAppMsg = if (position > 0) mDatas.get(position - 1) else null
        var showTime = false
        if (position == 0 || lastAppMsg == null || lastAppMsg.sortKey == null || (!lastAppMsg.sortKey.equals(appMsg.sortKey) && Math.abs(lastAppMsg.time - appMsg.time) > showInterval)) {
            showTime = true
        }
        LogUtil.i("bind_info", "pos: $position  gidIndex: ${appMsg.gidGroupIndex} showTime: $showTime")
        holder?.bind(position, appMsg, showTime, this)
        holder?.mCountDownTimers?.cancel()
        holder?.mCountDownTimers = null
        val timeout = if (appMsg.attachObject is BuyChipsNotify)
            (appMsg.attachObject as BuyChipsNotify).buyTimeout
        else if (appMsg.attachObject is MatchBuyChipsNotify)
            (appMsg.attachObject as MatchBuyChipsNotify).buyTimeout
        else
            0//倒计时时间
        if (appMsg.status == AppMessageStatus.init && shouldShowCountDown(appMsg, holder)) {
            holder?.mCountDownTimers = object : CountDownTimer((timeout * 1000).toLong(), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    holder?.updateStatus(mActivity, appMsg, this@ControlCenterAdap)
                }
                override fun onFinish() {}
            }
            holder?.mCountDownTimers?.start()
        }
    }

    override fun getItemCount(): Int {
        LogUtil.i("test_recyclerview_ControlCenterAdap", "getItemCount: ${mDatas.size}")
        return mDatas.size
    }

    fun updateData(datas: ArrayList<AppMessage>?) {
        mDatas.clear()
        if (datas != null) {
            mDatas.addAll(datas)
        }
    }

//    var mDatas: MutableMap<String, ArrayList<AppMessage>> = Collections.synchronizedMap(LinkedHashMap<String, ArrayList<AppMessage>>())
    var mDatas: ArrayList<AppMessage> = ArrayList<AppMessage>()
    var mActivity: Activity? = null
    var mClickListener: IClickPayload? = null
    var mIOperationClick: IOperationClick? = null
    constructor(datas: ArrayList<AppMessage>?, listeners: Any) {
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
        if (listeners is IOperationClick) {
            mIOperationClick = listeners
        }
    }


























    class AppControlVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mCountDownTimers: CountDownTimer? = null
        var iMsgExpired: IMsgExpired? = null
        var mAppMsg: AppMessage? = null
        var tv_message_time = itemView.findViewById(R.id.tv_message_time) as TextView
        var tv_game_name = itemView.findViewById(R.id.tv_game_name) as TextView
        var tv_game_member = itemView.findViewById(R.id.tv_game_member) as TextView
        var tv_app_message_from_nickname = itemView.findViewById(R.id.tv_app_message_from_nickname) as TextView
        var tv_buychips_weekout = itemView.findViewById(R.id.tv_buychips_weekout) as TextView
        var tv_rebuy_cnt = itemView.findViewById(R.id.tv_rebuy_cnt) as TextView
        var operator_layout = itemView.findViewById(R.id.operator_layout) as LinearLayout
        var iv_action_agree = itemView.findViewById(R.id.iv_action_agree) as TextView
        var iv_action_reject = itemView.findViewById(R.id.iv_action_reject) as TextView
        var iv_buychips_game_mode = itemView.findViewById(R.id.iv_buychips_game_mode) as ImageView
        var iv_buychips_game_sign = itemView.findViewById(R.id.iv_buychips_game_sign) as ImageView
        var ll_buychips_head = itemView.findViewById(R.id.ll_buychips_head)
        var operator_result = itemView.findViewById(R.id.operator_result) as TextView
        var tv_app_message_chips = itemView.findViewById(R.id.tv_app_message_chips) as TextView
        var buy_chips_can_longpress_content = itemView.findViewById(R.id.buy_chips_can_longpress_content)
        var tv_uuid = itemView.findViewById(R.id.tv_uuid) as TextView
        var rejectStr = itemView.context.getResources().getString(R.string.reject)
        fun bind(position: Int, message: AppMessage?, showTime: Boolean, adapter: ControlCenterAdap) {
            if (message == null) {
                return
            }
            mAppMsg = message
            tv_message_time.text = TimeUtil.getTimeShowString(message.time * 1000L, false)
            showTime(showTime)
            showInfoHead(message.gidGroupIndex <= 0)
            var fromAccount = message.fromId
            if (!TextUtils.isEmpty(message.targetId)) {
                fromAccount = message.targetId
            }
            var fromNickname = ""
            var gameMode = GameConstants.GAME_MODE_NORMAL
            tv_app_message_chips.visibility = View.GONE
            tv_uuid.visibility = View.GONE
            if (message.attachObject is BuyChipsNotify) {
                tv_rebuy_cnt.visibility = View.GONE
                val buyChipsNotify = message.attachObject as BuyChipsNotify
                fromNickname = buyChipsNotify.userNickname
                gameMode = buyChipsNotify.gameMode
                tv_uuid.visibility = if (StringUtil.isSpace(buyChipsNotify.uuid)) View.GONE else View.VISIBLE
                tv_uuid.text = "ID: " + buyChipsNotify.uuid
                tv_buychips_weekout.visibility = View.GONE
                tv_game_name.text = buyChipsNotify.gameName
                if (gameMode == GameConstants.GAME_MODE_NORMAL) {
                    tv_game_member.text = "" + buyChipsNotify.gamePlayerCount + "/" + if (buyChipsNotify.matchPlayer != 0) buyChipsNotify.matchPlayer else 9
                    var winchips = "" + buyChipsNotify.userWinChips
                    if (buyChipsNotify.userWinChips > 0) {
                        winchips = "+" + winchips
                    }
                    tv_app_message_chips.visibility = View.VISIBLE
                    tv_app_message_chips.text = "+" + buyChipsNotify.userBuyChips
                } else if (gameMode == GameConstants.GAME_MODE_SNG) {
                    tv_game_member.text = buyChipsNotify.checkinPlayer.toString() + "/" + buyChipsNotify.matchPlayer
                }
            } else if (message.attachObject is MatchBuyChipsNotify) {
                val buyChipsNotify = message.attachObject as MatchBuyChipsNotify
                fromNickname = buyChipsNotify.userNickname
                gameMode = buyChipsNotify.gameMode
                tv_game_name.text = buyChipsNotify.gameName
                tv_uuid.visibility = if (StringUtil.isSpace(buyChipsNotify.uuid)) View.GONE else View.VISIBLE
                tv_uuid.text = "ID: " + buyChipsNotify.uuid
                if (gameMode == GameConstants.GAME_MODE_MTT) {
                    tv_game_member.text = "" + buyChipsNotify.checkinPlayer
                } else if (gameMode == GameConstants.GAME_MODE_MT_SNG) {
                    tv_game_member.text = buyChipsNotify.checkinPlayer.toString() + "/" + buyChipsNotify.totalPlayer
                }
                tv_buychips_weekout.visibility = View.GONE
                tv_rebuy_cnt.visibility = View.GONE
                val buyType = buyChipsNotify.buyType
                val rebuyCnt = if (message.status == AppMessageStatus.init) buyChipsNotify.rebuyCnt + 1 else buyChipsNotify.rebuyCnt//玩家重构时房主和管理员会受到消息这时rebuyCnt要加1，点击同意时也会收到消息，这时rebuyCnt不加1，但是拒绝是也要加1
                if (rebuyCnt > 0) {
                    if (buyType == GameMatchBuyType.TYPE_BUY_CHECKIN) {
                    } else if (buyType == GameMatchBuyType.TYPE_BUY_REBUY) {
                        tv_rebuy_cnt.visibility = View.VISIBLE
                        tv_rebuy_cnt.text = "R" + rebuyCnt
                        tv_rebuy_cnt.setBackgroundResource(R.drawable.app_message_buychips_rebuy_tag_bg)
                    } else if (buyType == GameMatchBuyType.TYPE_BUY_REBUY_WEEDOUT) {
                        tv_rebuy_cnt.visibility = View.VISIBLE
                        tv_rebuy_cnt.text = "R" + rebuyCnt
                        tv_rebuy_cnt.setBackgroundResource(R.drawable.app_message_buychips_rebuy_tag_bg)
                        tv_buychips_weekout.visibility = View.VISIBLE
                    } else if (buyType == GameMatchBuyType.TYPE_BUY_REBUY_REVIVAL) {
                        tv_rebuy_cnt.visibility = View.VISIBLE
                        tv_rebuy_cnt.text = "R" + rebuyCnt
                        tv_rebuy_cnt.setBackgroundResource(R.drawable.app_message_buychips_rebuy_tag_bg)
                        tv_buychips_weekout.visibility = View.VISIBLE
                    } else if (buyType == GameMatchBuyType.TYPE_BUY_ADDON) {
                    } else if (buyType == GameMatchBuyType.TYPE_BUY_ADDON_WEEDOUT) {
                        tv_buychips_weekout.visibility = View.VISIBLE
                    }
                }
            }
            if (TextUtils.isEmpty(fromNickname)) {
                if (NimUserInfoCache.getInstance().hasUser(fromAccount)) {
                    tv_app_message_from_nickname.text = NimUserInfoCache.getInstance().getUserDisplayNameEx(fromAccount)
                }
            } else {
                tv_app_message_from_nickname.text = fromNickname
            }
            iv_buychips_game_sign.visibility = if (gameMode == GameConstants.GAME_MODE_NORMAL) View.GONE else View.VISIBLE
            if (gameMode == GameConstants.GAME_MODE_NORMAL) {
                iv_buychips_game_mode.setImageResource(R.mipmap.icon_control_in)
            } else if (gameMode == GameConstants.GAME_MODE_SNG) {
                iv_buychips_game_mode.setImageResource(R.mipmap.icon_control_sng)
            } else if (gameMode == GameConstants.GAME_MODE_MTT) {
                iv_buychips_game_mode.setImageResource(R.mipmap.icon_control_mtt)
            } else if (gameMode == GameConstants.GAME_MODE_MT_SNG) {
                iv_buychips_game_mode.setImageResource(R.mipmap.icon_control_mtsng)
            }
            iv_action_agree.setOnClickListener { adapter.mIOperationClick?.onAgree(position, message) }
            iv_action_reject.setOnClickListener { adapter.mIOperationClick?.onReject(position, message) }
            buy_chips_can_longpress_content.setOnClickListener { adapter.mClickListener?.onClick(position, message) }
            buy_chips_can_longpress_content.setOnLongClickListener {
                adapter.mClickListener?.onLongClick(position, message)
                true
            }
            updateStatus(itemView.context, message, adapter)
        }

        fun updateStatus(context: Context?, message: AppMessage, adapter: ControlCenterAdap) {
            iv_action_reject.text = rejectStr
            if (!AppMessageHelper.isVerifyMessageNeedDeal(message)) {
                operator_layout.visibility = View.GONE
                operator_result.visibility = View.VISIBLE
                operator_result.text = AppMessageHelper.getVerifyNotificationDealResult(message)
            } else {
                if (message.attachObject is BuyChipsNotify) {
                    val buyChipsNotify = message.attachObject as BuyChipsNotify
                    if (message.status == AppMessageStatus.init) {
                        //是否失效
                        val currentTime = DemoCache.getCurrentServerSecondTime()
                        val buyApplyTime = buyChipsNotify.buyStarttime
                        val timeout = buyChipsNotify.buyTimeout
                        val remainTime = (currentTime - buyApplyTime /*- checkTime*/).toInt()
                        if (remainTime >= timeout) {
                            //失效
                            if (mCountDownTimers != null) {
                                mCountDownTimers!!.cancel()
                                mCountDownTimers = null
                            }
                            operator_layout.visibility = View.GONE
                            message.status = AppMessageStatus.expired//倒计时结束时 状态置为"过期"
                            operator_result.visibility = View.VISIBLE
                            operator_result.text = AppMessageHelper.getVerifyNotificationDealResult(message)
                            AppMsgDBHelper.setSystemMessageStatus(context, message.type, message.checkinPlayerId, message.key, AppMessageStatus.expired)
                            if (iMsgExpired != null) {
                                iMsgExpired!!.onExpired(message)
                            }
                            adapter.mIOperationClick?.onOtherOperation(position, message)
                        } else {
                            val remaindTime = timeout - remainTime
                            operator_layout.visibility = View.VISIBLE
                            operator_result.visibility = View.GONE
                            iv_action_reject.text = "$rejectStr($remaindTime)"
                        }
                    } else {
                        // 处理结果
                        operator_layout.visibility = View.VISIBLE
                        operator_result.visibility = View.VISIBLE
                        operator_result.text = AppMessageHelper.getVerifyNotificationDealResult(message)
                    }
                } else if (message.attachObject is MatchBuyChipsNotify) {
                    val buyChipsNotify = message.attachObject as MatchBuyChipsNotify
                    if (message.status == AppMessageStatus.init) {
                        if (buyChipsNotify.buyType == GameMatchBuyType.TYPE_BUY_CHECKIN) {
                            //报名，报名没有失效时间
                            iv_action_reject.text = rejectStr
                            operator_layout.visibility = View.VISIBLE
                            operator_result.visibility = View.GONE
                        } else {
                            //重购，增购
                            val currentTime = DemoCache.getCurrentServerSecondTime()
                            val buyApplyTime = buyChipsNotify.buyStarttime
                            val timeout = buyChipsNotify.buyTimeout
                            val remainTime = (currentTime - buyApplyTime /*- checkTime*/).toInt()
                            if (remainTime >= timeout) {
                                //失效
                                if (mCountDownTimers != null) {
                                    mCountDownTimers!!.cancel()
                                    mCountDownTimers = null
                                }
                                operator_layout.visibility = View.GONE
                                operator_result.visibility = View.VISIBLE
                                message.status = AppMessageStatus.expired//倒计时结束时 状态置为"过期"
                                operator_result.text = AppMessageHelper.getVerifyNotificationDealResult(message)
                                AppMsgDBHelper.setSystemMessageStatus(context, message.type, message.checkinPlayerId, message.key, AppMessageStatus.expired)
                                if (iMsgExpired != null) {
                                    Handler().post {
                                        //不要在onbindviewholder中notifydatasetchanged，Cannot call this method while RecyclerView is computing a layout or scrolling，放到handler中
                                        iMsgExpired!!.onExpired(message)
                                    }
                                }
                                adapter.mIOperationClick?.onOtherOperation(position, message)
                            } else {
                                val remaindTime = timeout - remainTime
                                operator_layout.visibility = View.VISIBLE
                                operator_result.visibility = View.GONE
                                iv_action_reject.text = "$rejectStr($remaindTime)"
                            }
                            LogUtil.i(TAG, "remainTime :$remainTime; timeout:$timeout")
                        }
                    } else {
                        // 处理结果
                        if (mCountDownTimers != null) {
                            mCountDownTimers!!.cancel()
                            mCountDownTimers = null
                        }
                        operator_layout.visibility = View.GONE
                        operator_result.visibility = View.VISIBLE
                        operator_result.text = AppMessageHelper.getVerifyNotificationDealResult(message)
                    }
                }
            }
        }

        fun showTime(isShow: Boolean) {
            if (isShow) {
                tv_message_time.visibility = View.VISIBLE
            } else {
                tv_message_time.visibility = View.GONE
            }
        }

        fun showInfoHead(isShow: Boolean) {
            if (isShow) {
                ll_buychips_head.visibility = View.VISIBLE
            } else {
                ll_buychips_head.visibility = View.GONE
            }
        }
    }

    companion object {
        val TAG = ControlCenterAdap::class.java.simpleName
        private val showInterval = 30 * 60//30分钟
        fun shouldShowCountDown(message: AppMessage?, holder: AppControlVH?): Boolean {//显示倒计时
            var result = false
            if (message == null || holder == null) {
                return result
            }
            if (message.attachObject is BuyChipsNotify) {
                if (message.status == AppMessageStatus.init) {
                    val buyChipsNotify = message.attachObject as BuyChipsNotify
                    //是否失效
                    val currentTime = DemoCache.getCurrentServerSecondTime()
                    val buyApplyTime = buyChipsNotify.buyStarttime
                    val timeout = buyChipsNotify.buyTimeout
                    val remainTime = (currentTime - buyApplyTime /*- checkTime*/).toInt()
                    if (remainTime < timeout) {
                        if (holder.mCountDownTimers != null) {
                            holder.mCountDownTimers!!.cancel()
                            holder.mCountDownTimers = null
                        }
                        result = true
                    }
                }
            } else if (message.attachObject is MatchBuyChipsNotify) {
                val buyChipsNotify = message.attachObject as MatchBuyChipsNotify
                if (buyChipsNotify.buyType != GameMatchBuyType.TYPE_BUY_CHECKIN) {
                    //重购，增购
                    val currentTime = DemoCache.getCurrentServerSecondTime()
                    val buyApplyTime = buyChipsNotify.buyStarttime
                    val timeout = buyChipsNotify.buyTimeout
                    val remainTime = (currentTime - buyApplyTime /*- checkTime*/).toInt()
                    if (remainTime < timeout) {
                        if (holder.mCountDownTimers != null) {
                            holder.mCountDownTimers!!.cancel()
                            holder.mCountDownTimers = null
                        }
                        result = true
                    }
                }
            }
            return result
        }
    }
}