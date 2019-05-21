/**
 * Created by 周智慧 on 2017/7/12.
 */

package com.htgames.nutspoker.game.match.fragment

import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.*
import android.widget.*
import com.google.gson.reflect.TypeToken
import com.htgames.nutspoker.ChessApp
import com.htgames.nutspoker.R
import com.htgames.nutspoker.api.ApiResultHelper
import com.htgames.nutspoker.config.GameConfigData
import com.htgames.nutspoker.data.GameName
import com.htgames.nutspoker.game.match.activity.MatchRoomActivity
import com.htgames.nutspoker.game.view.DateSelectPopDialog
import com.htgames.nutspoker.interfaces.GameRequestCallback
import com.htgames.nutspoker.net.RequestTimeLimit
import com.htgames.nutspoker.receiver.NewGameReceiver
import com.htgames.nutspoker.ui.activity.Game.AnteTableAC
import com.htgames.nutspoker.ui.activity.Game.GameCreateActivity
import com.htgames.nutspoker.ui.activity.System.ShopActivity
import com.htgames.nutspoker.ui.fragment.BaseGameCreateFragment
import com.htgames.nutspoker.ui.inputfilter.NameLengthFilter
import com.htgames.nutspoker.ui.inputfilter.NameRuleFilter
import com.htgames.nutspoker.ui.inputfilter.NoSpaceAndEnterInputFilter
import com.htgames.nutspoker.view.MatchCreateRulesPopView
import com.htgames.nutspoker.view.NodeSeekBar
import com.htgames.nutspoker.view.switchbutton.SwitchButton
import com.netease.nim.uikit.api.ApiCode
import com.netease.nim.uikit.bean.PineappleConfigMtt
import com.netease.nim.uikit.chesscircle.entity.HordeEntity
import com.netease.nim.uikit.common.DateTools
import com.netease.nim.uikit.common.DemoCache
import com.netease.nim.uikit.common.gson.GsonUtils
import com.netease.nim.uikit.common.preference.CreateGameConfigPref
import com.netease.nim.uikit.common.preference.GamePreferences
import com.netease.nim.uikit.common.preference.UserPreferences
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper
import com.netease.nim.uikit.common.ui.widget.EasySwitchBtn
import com.netease.nim.uikit.common.util.NetworkUtil
import com.netease.nim.uikit.common.util.log.LogUtil
import com.netease.nim.uikit.common.util.string.StringUtil
import com.netease.nim.uikit.common.util.sys.ScreenUtil
import com.netease.nim.uikit.constants.GameConstants
import com.netease.nim.uikit.session.constant.Extras
import org.json.JSONObject
import java.util.*

/**
 * 比赛牌桌页面
 */
class CreatePineappleMttFrg : BaseGameCreateFragment(), View.OnClickListener {
    private var ante_table_type = GameConstants.PINEAPPLE_MTT_ANTE_TABLE_NORMAL
    lateinit var mRootView: View
    var diamond_match: Int = GameConstants.MATCH_TYPE_NORMAL//是否是钻石赛
    var can_create_diamond_match = true//是否能创建钻石赛
    var play_type = GameConstants.PINEAPPLE_MODE_NORMAL
    lateinit var edt_game_name: EditText
    lateinit var btn_pineapple_mode_normal: TextView
    lateinit var btn_pineapple_mode_blood: TextView
    lateinit var btn_pineapple_mode_blood_in_out: TextView
    lateinit var btn_pineapple_mode_yoriko: TextView
    //下面是比赛配置
    lateinit var tv_game_checkin_fee: TextView//报名费显示数据
    var mFeeSeekBar: NodeSeekBar? = null//报名费拖动条

    lateinit var tv_game_chip: TextView//比赛带入记分牌
    lateinit var mChipsSeekBar: NodeSeekBar//带入记分牌拖动条

    lateinit var tv_match_blinds_time_num: TextView//升底时间 "显示"   德州和奥马哈对应的是涨盲时间
    lateinit var mTimeSeekBar: NodeSeekBar//升底时间拖动条
    //下面是其实底注的设置
    lateinit var tv_start_ante_enter: TextView
    lateinit var tv_ante_table_normal: TextView
    lateinit var tv_ante_table_quick: TextView
    //下面的是高级设置
    lateinit var game_create_advanced_switch: View
    lateinit var advanced_switch_tv: TextView
    lateinit var game_create_advanced_switch_arrow: ImageView
    lateinit var ll_advanced_config_new: View
    lateinit var iv_explain_pineapple_total_buy_num: View
    lateinit var checkin_player_limit_num: TextView
    lateinit var seekbar_total_buy: NodeSeekBar
    lateinit var tv_game_create_blind_level_new: TextView
    lateinit var mBlindsLevelSeekBar: NodeSeekBar
    lateinit var iv_rebuy: ImageView
    lateinit var mtt_rebuy_seekbar: NodeSeekBar
    lateinit var ck_game_control_into: SwitchButton
    lateinit var ck_game_match_rest: SwitchButton
    lateinit var iv_rest: ImageView
    //下面是猎人赛相关
    ////////////////////////////////////////////猎人赛相关begin////////////////////////////////////////////
    lateinit var hunter_switch: SwitchButton
    lateinit var ll_hunter_config_content: View
    lateinit var iv_hunter_normal: EasySwitchBtn
    lateinit var iv_hunter_super: EasySwitchBtn
    lateinit var tv_hunter_normal_des: TextView
    lateinit var tv_hunter_super_des: TextView
    lateinit var tv_normal_hunter_ratio: TextView
    lateinit var tv_super_hunter_ratio: TextView
    var seekbar_normal_hunter_ratio: NodeSeekBar? = null
    lateinit var seekbar_super_hunter_ratio: NodeSeekBar
    lateinit var tv_normal_hunter_ratio_click: ImageView
    lateinit var tv_super_hunter_ratio_click: ImageView
    private var mKoMode: Int = 0
    //下面是共享到部落的一些信息
    var teamId: String? = ""
    var mHordeEntity: HordeEntity? = null
    var mSelectedHordeEntity: HordeEntity? = null
    var previousHorde: View? = null
    var costList: ArrayList<HordeEntity>? = null
    var sure_horde_consume_num: TextView? = null
    var sure_horde_remain_num: TextView? = null
    var switch_horde: SwitchButton? = null
    var horde_content: View? = null
    //one
    var horde_one_container: View? = null
    var iv_horde_one: EasySwitchBtn? = null
    var tv_horde_one: TextView? = null
    //two
    var horde_two_container: View? = null
    var iv_horde_two: EasySwitchBtn? = null
    var tv_horde_two: TextView? = null
    //three
    var horde_three_container: View? = null
    var iv_horde_three: EasySwitchBtn? = null
    var tv_horde_three: TextView? = null
    //four
    var horde_four_container: View? = null
    var iv_horde_four: EasySwitchBtn? = null
    var tv_horde_four: TextView? = null
    var horde_consume_num: TextView? = null
    var horde_remain_num: TextView? = null
    //时间相关
    var mDateSelectPop: DateSelectPopDialog? = null
    lateinit var tv_match_start_date_select: TextView
    lateinit var tv_auto_start_des: TextView
    lateinit var time_set_des_container: View
    lateinit var switch_auto_start: SwitchButton
    var choiceTime: Long = -1
    //开始按钮
    lateinit var btn_game_start: View
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        can_create_diamond_match = UserPreferences.getInstance(activity).level == 100//只有level==100的时候才能创建钻石赛
        super.onCreate(savedInstanceState)
        setFragmentName("CreatePineappleMttFrg")
        getCreateGameConfig()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mRootView = LayoutInflater.from(context).inflate(R.layout.fragment_game_create_pineapple_mtt, container, false)
        setFragmentName(CreatePineappleMttFrg::class.java.simpleName)
        btn_game_start = mRootView.findViewById(R.id.btn_game_start)
        btn_game_start.setOnClickListener(View.OnClickListener {
            if (checkAutoStartDialog()) {//显示提示框的话返回
                return@OnClickListener
            }
            tryCreatePineappleGame()
        })
        initGameName()
        initMttTime()
        initPineappleMode()
        initMatchConfig()
        initMatchType()//设置比赛类型
        initStartAnte()
        initAdvancedConfig()
        initHunterRelated()//初始化猎人赛相关
        initSureHordeView()
        initSelectHordeView()//初始化horde相关的配置
        setGameConfig()
        return mRootView
    }

    lateinit var mGameName: GameName
    private fun initGameName() {
        edt_game_name = mRootView.findViewById(R.id.edt_game_name) as EditText
        edt_game_name.filters = arrayOf(NoSpaceAndEnterInputFilter(), NameLengthFilter(GameConstants.MAX_GAMENAME_LENGTH), NameRuleFilter())
        val pineappleGamePrefix = GamePreferences.getInstance(ChessApp.sAppContext).pineappleMttGamePrefix
        val pineappleGameCount = GamePreferences.getInstance(ChessApp.sAppContext).pineappleMttGameCount
        mGameName = GameName("$pineappleGamePrefix$pineappleGameCount")
        edt_game_name.setText(mGameName.name)
        edt_game_name.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                //隐藏键盘
                (activity as? GameCreateActivity)?.showKeyboard(false)
                return@OnKeyListener true
            }
            false
        })
    }

    private fun initMttTime() {
        tv_match_start_date_select = mRootView.findViewById(R.id.tv_match_start_date_select) as TextView
        tv_auto_start_des = mRootView.findViewById(R.id.tv_auto_start_des) as TextView
        time_set_des_container = mRootView.findViewById(R.id.time_set_des_container)
        switch_auto_start = mRootView.findViewById(R.id.switch_auto_start) as SwitchButton
        mRootView.findViewById(R.id.rl_match_start_date_select).setOnClickListener { showDateSelectPop() }
    }

    private fun showDateSelectPop() {
        if (mDateSelectPop == null) {
            mDateSelectPop = DateSelectPopDialog(activity)
            mDateSelectPop!!.setOnDateSelectListener{ date ->
                choiceTime = date
                tv_match_start_date_select.text = DateTools.getOtherStrTime_ymd_hm("" + date)
                tv_auto_start_des.visibility = View.GONE
                switch_auto_start.visibility = View.VISIBLE
                time_set_des_container.visibility = View.VISIBLE
            }
            mDateSelectPop!!.mOnDateClearListener = DateSelectPopDialog.OnDateClearListener {
                choiceTime = -1
                tv_match_start_date_select.setText(R.string.game_create_match_start_date_select)
                tv_auto_start_des.visibility = View.VISIBLE
                switch_auto_start.visibility = View.GONE
                time_set_des_container.visibility = View.GONE
            }
        }

        if (activity is GameCreateActivity && !activity.isFinishing && !(activity as GameCreateActivity).isDestroyedCompatible && !mDateSelectPop!!.isShowing) {
            mDateSelectPop!!.show()
        }
    }

    private fun initPineappleMode() {//初始化play_type
        btn_pineapple_mode_normal = mRootView.findViewById(R.id.btn_pineapple_mode_normal) as TextView
        btn_pineapple_mode_blood = mRootView.findViewById(R.id.btn_pineapple_mode_blood) as TextView
        btn_pineapple_mode_blood_in_out = mRootView.findViewById(R.id.btn_pineapple_mode_blood_in_out) as TextView
        btn_pineapple_mode_yoriko = mRootView.findViewById(R.id.btn_pineapple_mode_yoriko) as TextView
        val normalColor = resources.getColor(R.color.shop_text_no_select_color)
        val selectColor = resources.getColor(R.color.white)
        val normalDrawable = GradientDrawable()
        val selectDrawable: Drawable = resources.getDrawable(R.drawable.bg_login_btn)
        val pineapple_mode_click = View.OnClickListener { v ->
            play_type = if (v === btn_pineapple_mode_normal) GameConstants.PINEAPPLE_MODE_NORMAL
            else if (v === btn_pineapple_mode_blood) GameConstants.PINEAPPLE_MODE_BLOOD
            else if (v === btn_pineapple_mode_blood_in_out) GameConstants.PINEAPPLE_MODE_BLOOD_IN_OUT
            else if (v === btn_pineapple_mode_yoriko) GameConstants.PINEAPPLE_MODE_YORIKO
            else GameConstants.PINEAPPLE_MODE_NORMAL
            btn_pineapple_mode_normal.setBackgroundDrawable(if (play_type == GameConstants.PINEAPPLE_MODE_NORMAL) selectDrawable else normalDrawable)
            btn_pineapple_mode_normal.setTextColor(if (play_type == GameConstants.PINEAPPLE_MODE_NORMAL) selectColor else normalColor)
            btn_pineapple_mode_blood.setBackgroundDrawable(if (play_type == GameConstants.PINEAPPLE_MODE_BLOOD) selectDrawable else normalDrawable)
            btn_pineapple_mode_blood.setTextColor(if (play_type == GameConstants.PINEAPPLE_MODE_BLOOD) selectColor else normalColor)
            btn_pineapple_mode_blood_in_out.setBackgroundDrawable(if (play_type == GameConstants.PINEAPPLE_MODE_BLOOD_IN_OUT) selectDrawable else normalDrawable)
            btn_pineapple_mode_blood_in_out.setTextColor(if (play_type == GameConstants.PINEAPPLE_MODE_BLOOD_IN_OUT) selectColor else normalColor)
            btn_pineapple_mode_yoriko.setBackgroundDrawable(if (play_type == GameConstants.PINEAPPLE_MODE_YORIKO) selectDrawable else normalDrawable)
            btn_pineapple_mode_yoriko.setTextColor(if (play_type == GameConstants.PINEAPPLE_MODE_YORIKO) selectColor else normalColor)
        }
        normalDrawable.cornerRadius = ScreenUtil.dp2px(activity, 4f).toFloat()
        normalDrawable.setColor(resources.getColor(R.color.register_page_bg_color))
        normalDrawable.setStroke(ScreenUtil.dp2px(activity, 1f), resources.getColor(R.color.list_item_bg_press))
        btn_pineapple_mode_normal.setBackgroundDrawable(if (play_type == GameConstants.PINEAPPLE_MODE_NORMAL) selectDrawable else normalDrawable)
        btn_pineapple_mode_normal.setOnClickListener(pineapple_mode_click)
        btn_pineapple_mode_normal.setTextColor(if (play_type == GameConstants.PINEAPPLE_MODE_NORMAL) selectColor else normalColor)
        btn_pineapple_mode_blood.setBackgroundDrawable(if (play_type == GameConstants.PINEAPPLE_MODE_BLOOD) selectDrawable else normalDrawable)
        btn_pineapple_mode_blood.setOnClickListener(pineapple_mode_click)
        btn_pineapple_mode_blood.setTextColor(if (play_type == GameConstants.PINEAPPLE_MODE_BLOOD) selectColor else normalColor)
        btn_pineapple_mode_blood_in_out.setBackgroundDrawable(if (play_type == GameConstants.PINEAPPLE_MODE_BLOOD_IN_OUT) selectDrawable else normalDrawable)
        btn_pineapple_mode_blood_in_out.setOnClickListener(pineapple_mode_click)
        btn_pineapple_mode_blood_in_out.setTextColor(if (play_type == GameConstants.PINEAPPLE_MODE_BLOOD_IN_OUT) selectColor else normalColor)
        btn_pineapple_mode_yoriko.setBackgroundDrawable(if (play_type == GameConstants.PINEAPPLE_MODE_YORIKO) selectDrawable else normalDrawable)
        btn_pineapple_mode_yoriko.setOnClickListener(pineapple_mode_click)
        btn_pineapple_mode_yoriko.setTextColor(if (play_type == GameConstants.PINEAPPLE_MODE_YORIKO) selectColor else normalColor)
    }

    private fun setFee(it: Int?) {
        if (it == null) {
            return
        }
        var checkinFee = mFeeSeekBar!!.getDataItem(it)//GameConfigData.pineapple_mtt_fees[it]
        var serviceFee: Int = (checkinFee / GameConfigData.MTTServiceRate).toInt()
        if (diamond_match != GameConstants.MATCH_TYPE_NORMAL) {
            tv_game_checkin_fee.text = "$checkinFee"
        } else {
            tv_game_checkin_fee.text = "$checkinFee+$serviceFee"
        }
        if (checkinFee < 100) {
            changeHunterRewardRate(false)//更改猎人赛赏金比例，不允许出现小数  checkinFee * 赏金比例不允许出现小数，赏金比例过滤掉以5结尾的档位
        } else {
            changeHunterRewardRate(true)//显示全部的默认赏金比例的档位
        }
    }

    private fun initMatchConfig() {
        (mRootView.findViewById(R.id.tv_match_blinds_time) as TextView).text = "升底时间"
//        val ck_diamond_match = mRootView.findViewById(R.id.ck_diamond_match) as CheckBox
//        ck_diamond_match.visibility = if (can_create_diamond_match) View.VISIBLE else View.GONE
//        ck_diamond_match.setOnClickListener {
//            diamond_match = (it as CheckBox).isChecked
//            mFeeSeekBar?.onlyUpdateData(if (diamond_match) GameConfigData.mtt_checkin_diamond else GameConfigData.pineapple_mtt_fees)
//            setFee(mFeeSeekBar?.currentPosition)
//        }
//        ck_diamond_match.isChecked = diamond_match
        mRootView.findViewById(R.id.tv_mtt_relation).visibility = View.GONE
        tv_game_checkin_fee = mRootView.findViewById(R.id.tv_game_checkin_fee) as TextView
        tv_game_chip = mRootView.findViewById(R.id.tv_game_chip) as TextView
        tv_match_blinds_time_num = mRootView.findViewById(R.id.tv_match_blinds_time_num) as TextView
        //报名费
        mFeeSeekBar = mRootView.findViewById(R.id.mFeeSeekBar) as NodeSeekBar
        mFeeSeekBar!!.setData(if (diamond_match == GameConstants.MATCH_TYPE_NORMAL) GameConfigData.pineapple_mtt_fees
                              else if (diamond_match == GameConstants.MATCH_TYPE_GOLD) GameConfigData.mtt_checkin_gold
                              else GameConfigData.mtt_checkin_diamond, false, false, R.mipmap.game_chip_thumb, "")
        mFeeSeekBar!!.setOnNodeChangeListener {
            setFee(it)
            //共享部落的钻石消耗
            val hordeDiamondCOnsume = GameConfigData.create_mtt_fee[if (it < GameConfigData.create_mtt_fee.size) it else GameConfigData.create_mtt_fee.size - 1]
            if (sure_horde_consume_num != null) {
                sure_horde_consume_num?.text = "$hordeDiamondCOnsume"
            }
            if (horde_consume_num != null) {
                horde_consume_num?.text = "$hordeDiamondCOnsume"
            }
        }
        mFeeSeekBar!!.setProgress(0)
        setFee(mFeeSeekBar!!.currentPosition)
        //初始记分牌
        mChipsSeekBar = mRootView.findViewById(R.id.mChipsSeekBar) as NodeSeekBar
        mChipsSeekBar.setData(GameConfigData.pineapple_mtt_chips, false, false, R.mipmap.game_chip_thumb, "")
        mChipsSeekBar.setOnNodeChangeListener {
            tv_game_chip.text = "${GameConfigData.pineapple_mtt_chips[it]}"
        }
        mChipsSeekBar.setProgress(0)
        tv_game_chip.text = "${GameConfigData.pineapple_mtt_chips[mChipsSeekBar.currentPosition]}"
        //升底时间
        mTimeSeekBar = mRootView.findViewById(R.id.mTimeSeekBar) as NodeSeekBar
        mTimeSeekBar.setData(GameConfigData.pineapple_mtt_ante_time, false, false, R.mipmap.game_chip_thumb, "")
        mTimeSeekBar.setOnNodeChangeListener {
            tv_match_blinds_time_num.text = "${GameConfigData.pineapple_mtt_ante_time[it]}分钟"
        }
        mTimeSeekBar.setProgress(0)
        tv_match_blinds_time_num.text = "${GameConfigData.pineapple_mtt_ante_time[mTimeSeekBar.currentPosition]}分钟"
    }

    var match_type_normal: TextView? = null
    var match_type_gold: TextView? = null
    var match_type_diamond: TextView? = null
    var normalColor: Int = 0
    var selectColor: Int = 0
    lateinit var normalDrawable: GradientDrawable
    lateinit var selectDrawable: Drawable
    lateinit var iv_explain_checkin_fee: View
    private fun initMatchType() {
        iv_explain_checkin_fee = mRootView.findViewById(R.id.iv_explain_checkin_fee)
        iv_explain_checkin_fee.setOnClickListener(this)
        iv_explain_checkin_fee.visibility = if (diamond_match == GameConstants.MATCH_TYPE_NORMAL) View.INVISIBLE else View.VISIBLE
        if (can_create_diamond_match) {
            val resources = activity.resources
            normalColor = resources.getColor(R.color.shop_text_no_select_color)
            selectColor = resources.getColor(R.color.white)
            normalDrawable = GradientDrawable()
            normalDrawable.cornerRadius = ScreenUtil.dp2px(activity, 4f).toFloat()
            normalDrawable.setColor(resources.getColor(R.color.register_page_bg_color))
            normalDrawable.setStroke(ScreenUtil.dp2px(activity, 1f), resources.getColor(R.color.list_item_bg_press))
            selectDrawable = resources.getDrawable(R.drawable.bg_login_btn)
            val match_type_click = View.OnClickListener { v ->
                val viewId = v.id
                if (viewId == R.id.match_type_normal) {
                    diamond_match = GameConstants.MATCH_TYPE_NORMAL
                } else if (viewId == R.id.match_type_gold) {
                    diamond_match = GameConstants.MATCH_TYPE_GOLD
                } else if (viewId == R.id.match_type_diamond) {
                    diamond_match = GameConstants.MATCH_TYPE_DIAMOND
                }
                setMatchTypeUI()
            }
            (mRootView.findViewById(R.id.view_stub_match_type) as ViewStub).inflate()
            match_type_normal = mRootView.findViewById(R.id.match_type_normal) as TextView
            match_type_normal?.setOnClickListener(match_type_click)
            match_type_gold = mRootView.findViewById(R.id.match_type_gold) as TextView
            match_type_gold?.setOnClickListener(match_type_click)
            match_type_diamond = mRootView.findViewById(R.id.match_type_diamond) as TextView
            match_type_diamond?.setOnClickListener(match_type_click)
            setMatchTypeUI()
        }
    }

    private fun setMatchTypeUI() {
        if (!can_create_diamond_match) {
            return
        }
        match_type_normal?.setBackgroundDrawable(if (diamond_match == GameConstants.MATCH_TYPE_NORMAL) selectDrawable else normalDrawable)
        match_type_normal?.setTextColor(if (diamond_match == GameConstants.MATCH_TYPE_NORMAL) selectColor else normalColor)
        match_type_gold?.setBackgroundDrawable(if (diamond_match == GameConstants.MATCH_TYPE_GOLD) selectDrawable else normalDrawable)
        match_type_gold?.setTextColor(if (diamond_match == GameConstants.MATCH_TYPE_GOLD) selectColor else normalColor)
        match_type_diamond?.setBackgroundDrawable(if (diamond_match == GameConstants.MATCH_TYPE_DIAMOND) selectDrawable else normalDrawable)
        match_type_diamond?.setTextColor(if (diamond_match == GameConstants.MATCH_TYPE_DIAMOND) selectColor else normalColor)
        iv_explain_checkin_fee.visibility = if (diamond_match == GameConstants.MATCH_TYPE_NORMAL) View.INVISIBLE else View.VISIBLE
        if (mFeeSeekBar != null) {
            mFeeSeekBar!!.onlyUpdateData(if (diamond_match == GameConstants.MATCH_TYPE_NORMAL) GameConstants.mttCheckInFeeNum else if (diamond_match == GameConstants.MATCH_TYPE_GOLD) GameConfigData.mtt_checkin_gold else GameConfigData.mtt_checkin_diamond)
            setFee(mFeeSeekBar!!.currentPosition)
        }
    }

    private fun initStartAnte() {
        val anteTableSelectClick = View.OnClickListener {
            when (it.id) {
                R.id.tv_ante_table_normal -> {
                    ante_table_type = GameConstants.PINEAPPLE_MTT_ANTE_TABLE_NORMAL
                }
                R.id.tv_ante_table_quick -> {
                    ante_table_type = GameConstants.PINEAPPLE_MTT_ANTE_TABLE_QUICK
                }
            }
            tv_ante_table_normal.isSelected = ante_table_type == GameConstants.PINEAPPLE_MTT_ANTE_TABLE_NORMAL
            tv_ante_table_quick.isSelected = ante_table_type == GameConstants.PINEAPPLE_MTT_ANTE_TABLE_QUICK
        }
        tv_start_ante_enter = mRootView.findViewById(R.id.tv_start_ante_enter) as TextView
        tv_start_ante_enter.setOnClickListener {
            AnteTableAC.Companion.start(this@CreatePineappleMttFrg.activity, ante_table_type)
        }
        tv_ante_table_normal = mRootView.findViewById(R.id.tv_ante_table_normal) as TextView
        tv_ante_table_normal.setOnClickListener(anteTableSelectClick)
        tv_ante_table_normal.isSelected = ante_table_type == GameConstants.PINEAPPLE_MTT_ANTE_TABLE_NORMAL
        tv_ante_table_quick = mRootView.findViewById(R.id.tv_ante_table_quick) as TextView
        tv_ante_table_quick.setOnClickListener(anteTableSelectClick)
        tv_ante_table_quick.isSelected = ante_table_type == GameConstants.PINEAPPLE_MTT_ANTE_TABLE_QUICK
    }

    fun changeAnteTableType(type: Int) {
        ante_table_type = type
        tv_ante_table_normal.isSelected = ante_table_type == GameConstants.PINEAPPLE_MTT_ANTE_TABLE_NORMAL
        tv_ante_table_quick.isSelected = ante_table_type == GameConstants.PINEAPPLE_MTT_ANTE_TABLE_QUICK
    }

    private fun initAdvancedConfig() {
        game_create_advanced_switch = mRootView.findViewById(R.id.game_create_advanced_switch)
        game_create_advanced_switch.setOnClickListener { showAdvancedConfig(!isAdvancedConfigShow) }
        advanced_switch_tv = mRootView.findViewById(R.id.advanced_switch_tv) as TextView
        game_create_advanced_switch_arrow = mRootView.findViewById(R.id.game_create_advanced_switch_arrow) as ImageView
        ll_advanced_config_new = mRootView.findViewById(R.id.ll_advanced_config_new)
        iv_explain_pineapple_total_buy_num = mRootView.findViewById(R.id.iv_explain_pineapple_total_buy_num)
        iv_explain_pineapple_total_buy_num.setOnClickListener { showPlayLimitDialog(it, ChessApp.sAppContext.resources.getString(R.string.game_create_mtt_checkin_limit_instructions)) }
        checkin_player_limit_num = mRootView.findViewById(R.id.checkin_player_limit_num) as TextView
        //seekbar总买入次数上限
        seekbar_total_buy = mRootView.findViewById(R.id.seekbar_total_buy) as NodeSeekBar
        seekbar_total_buy.setData(GameConfigData.pineapple_mtt_checkin_limit, false, false, R.mipmap.game_chip_thumb, "")
        seekbar_total_buy.setOnNodeChangeListener { checkin_player_limit_num.text = "${GameConfigData.pineapple_mtt_checkin_limit[it]}" }
        seekbar_total_buy.setProgress(0)
        checkin_player_limit_num.text = "${GameConfigData.pineapple_mtt_checkin_limit[seekbar_total_buy.currentPosition]}"
        tv_game_create_blind_level_new = mRootView.findViewById(R.id.tv_game_create_blind_level_new) as TextView
        //seekbar终止报名级别
        mBlindsLevelSeekBar = mRootView.findViewById(R.id.mBlindsLevelSeekBar) as NodeSeekBar
        mBlindsLevelSeekBar.setData(GameConfigData.pineapple_mtt_blind_level, false, false, R.mipmap.game_chip_thumb, "")
        mBlindsLevelSeekBar.setOnNodeChangeListener {
            val blindLevel = GameConfigData.pineapple_mtt_blind_level[it]
            tv_game_create_blind_level_new.text = "$blindLevel"//ChessApp.sAppContext.resources.getString(R.string.game_create_blind_level_new, blindLevel)
        }
        mBlindsLevelSeekBar.setProgress(0)
        tv_game_create_blind_level_new.text = "${GameConfigData.pineapple_mtt_blind_level[mBlindsLevelSeekBar.currentPosition]}"
        iv_rebuy = mRootView.findViewById(R.id.iv_rebuy) as ImageView
        iv_rebuy.setOnClickListener { showPlayLimitDialog(it, ChessApp.sAppContext.resources.getString(R.string.game_create_mtt_rebuy_instructions)) }
        //seekbar重构次数
        mtt_rebuy_seekbar = mRootView.findViewById(R.id.mtt_rebuy_seekbar) as NodeSeekBar
        mtt_rebuy_seekbar.setData(GameConfigData.pineapple_mtt_rebuy_count, true, false, R.mipmap.game_chip_thumb, "")
        mtt_rebuy_seekbar.setProgress(0)
        ck_game_control_into = mRootView.findViewById(R.id.ck_game_control_into) as SwitchButton
        ck_game_match_rest = mRootView.findViewById(R.id.ck_game_match_rest) as SwitchButton
        iv_rest = mRootView.findViewById(R.id.iv_rest) as ImageView
        iv_rest.setOnClickListener { showPlayLimitDialog(it, ChessApp.sAppContext.resources.getString(R.string.game_create_mtt_rest_instructions)) }
    }

    internal var ko_reward_rate = GameConfigData.ko_reward_rate//这个数组过滤掉以5结尾的数字，checkinFee * 赏金比例不允许出现小数，赏金比例过滤掉以5结尾的档位
    private fun initHunterRelated() {
        ll_hunter_config_content = mRootView.findViewById(R.id.ll_hunter_config_content)
        iv_hunter_normal = mRootView.findViewById(R.id.iv_hunter_normal) as EasySwitchBtn
        iv_hunter_normal.setOnClickListener(this)
        iv_hunter_super = mRootView.findViewById(R.id.iv_hunter_super) as EasySwitchBtn
        iv_hunter_super.setOnClickListener(this)
        tv_hunter_normal_des = mRootView.findViewById(R.id.tv_hunter_normal_des) as TextView
        tv_hunter_normal_des.setOnClickListener(this)
        tv_hunter_super_des = mRootView.findViewById(R.id.tv_hunter_super_des) as TextView
        tv_hunter_super_des.setOnClickListener(this)
        tv_normal_hunter_ratio = mRootView.findViewById(R.id.tv_normal_hunter_ratio) as TextView
        tv_normal_hunter_ratio.text = GameConfigData.ko_reward_rate[0].toString() + "%"
        tv_normal_hunter_ratio_click = mRootView.findViewById(R.id.tv_normal_hunter_ratio_click) as ImageView
        tv_normal_hunter_ratio_click.setOnClickListener{ showPlayLimitDialog(it, ChessApp.sAppContext.resources.getString(R.string.game_create_mtt_normal_hunter_instructions)) }
        tv_super_hunter_ratio = mRootView.findViewById(R.id.tv_super_hunter_ratio) as TextView
        tv_super_hunter_ratio.text = GameConfigData.ko_head_rate[0].toString() + "%"
        tv_super_hunter_ratio_click = mRootView.findViewById(R.id.tv_super_hunter_ratio_click) as ImageView
        tv_super_hunter_ratio_click.setOnClickListener {
            showPlayLimitDialog(it, ChessApp.sAppContext.resources.getString(R.string.game_create_mtt_super_hunter_instructions))
        }
        seekbar_normal_hunter_ratio = mRootView.findViewById(R.id.seekbar_normal_hunter_ratio) as NodeSeekBar
        setFee(mFeeSeekBar?.currentPosition)
        seekbar_normal_hunter_ratio?.setData(ko_reward_rate, false, false, R.mipmap.game_chip_thumb, "")
        seekbar_normal_hunter_ratio?.setOnNodeChangeListener { tv_normal_hunter_ratio.text = "${ko_reward_rate[it]}%" }
        seekbar_normal_hunter_ratio?.setProgress(if (3 < ko_reward_rate.size) 3 else ko_reward_rate.size - 1)
        seekbar_super_hunter_ratio = mRootView.findViewById(R.id.seekbar_super_hunter_ratio) as NodeSeekBar
        seekbar_super_hunter_ratio.setData(GameConfigData.ko_head_rate, false, false, R.mipmap.game_chip_thumb, "")
        seekbar_super_hunter_ratio.isEnabled = mKoMode == 2
        seekbar_super_hunter_ratio.setThumbId(if (mKoMode == 2) R.mipmap.game_chip_thumb else R.mipmap.game_chip_thumb_unenable)
        seekbar_super_hunter_ratio.setOnNodeChangeListener { tv_super_hunter_ratio.text = "${GameConfigData.ko_head_rate[it]}%" }
        seekbar_super_hunter_ratio.setProgress(4)
        hunter_switch = mRootView.findViewById(R.id.hunter_switch) as SwitchButton
        hunter_switch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            ll_hunter_config_content.visibility = if (hunter_switch.isChecked) View.VISIBLE else View.GONE
            if (!hunter_switch.isChecked) {
                mKoMode = 0//重置复位
            } else {
                mKoMode = if (iv_hunter_normal.isChecked) 1 else if (iv_hunter_super.isChecked) 2 else 1
            }
            LogUtil.i("createfragment setOnCheckedChangeListener: " + hunter_switch.isChecked)
        })
        showSuperHunter(false)
    }

    private fun changeHunterMode(ko_mode: Int) {
        mKoMode = ko_mode
        iv_hunter_normal.setChecked(mKoMode == 1)
        iv_hunter_super.setChecked(mKoMode == 2)
        seekbar_super_hunter_ratio.isEnabled = mKoMode == 2
        seekbar_super_hunter_ratio.setThumbId(if (mKoMode == 2) R.mipmap.game_chip_thumb else R.mipmap.game_chip_thumb_unenable)
        if (!iv_hunter_normal.isChecked && !iv_hunter_super.isChecked) {//如果两个都没有选中，那么默认选中第一个
            iv_hunter_normal.setChecked(true)
        }
    }

    private fun showSuperHunter(show: Boolean) {//是否显示超级猎人赛
        mRootView.findViewById(R.id.super_hunter_divider).visibility = if (show) View.VISIBLE else View.GONE
        mRootView.findViewById(R.id.super_hunter_container).visibility = if (show) View.VISIBLE else View.GONE
        seekbar_super_hunter_ratio.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun changeHunterRewardRate(showAll: Boolean) {
        if (seekbar_normal_hunter_ratio == null) {
            return
        }
        if (showAll) {
            ko_reward_rate = GameConfigData.ko_reward_rate
            seekbar_normal_hunter_ratio?.onlyUpdateData(ko_reward_rate)
        } else {
            filterRewardRate()//checkinFee * 赏金比例不允许出现小数，赏金比例过滤掉以5结尾的档位
            seekbar_normal_hunter_ratio?.onlyUpdateData(ko_reward_rate)
        }
        tv_normal_hunter_ratio.text = "${ko_reward_rate[seekbar_normal_hunter_ratio!!.currentPosition]}%"
    }

    private fun filterRewardRate(): IntArray {
        val newRewardRateData = IntArray(GameConfigData.ko_reward_rate.size)
        var j = 0
        for (i in GameConfigData.ko_reward_rate.indices) {
            val ss = GameConfigData.ko_reward_rate[i].toString() + ""
            if (ss.endsWith("5")) {
                continue
            } else {
                newRewardRateData[j] = GameConfigData.ko_reward_rate[i]
                j++
            }
        }
        val result = IntArray(j)
        for (i in 0..j - 1) {
            result[i] = newRewardRateData[i]
        }
        ko_reward_rate = result
        return result
    }

    private var isAdvancedConfigShow: Boolean = false
    fun showAdvancedConfig(show: Boolean) {
        isAdvancedConfigShow = show
        game_create_advanced_switch_arrow.setImageResource(if (isAdvancedConfigShow) R.mipmap.arrow_advance_up else R.mipmap.arrow_advance_down)
        ll_advanced_config_new.visibility = if (isAdvancedConfigShow) View.VISIBLE else View.GONE
        advanced_switch_tv.text = resources.getString(if (isAdvancedConfigShow) R.string.circle_content_packup else R.string.game_create_config_advanced)
    }

    fun initSureHordeView() {
        mHordeEntity = (activity as? GameCreateActivity)?.mHordeEntity
        //从部落中创建牌局时要显示消耗钻石的view，但是选择共享部落的view隐藏
        if (mHordeEntity != null) {
            (mRootView.findViewById(R.id.view_stub) as ViewStub).inflate()
            val hordeDiamondCOnsume = GameConfigData.create_mtt_fee[0]
            sure_horde_consume_num = mRootView.findViewById(R.id.sure_horde_consume_num) as TextView
            sure_horde_consume_num?.text = "$hordeDiamondCOnsume"
            sure_horde_remain_num = mRootView.findViewById(R.id.sure_horde_remain_num) as TextView
            sure_horde_remain_num?.text = diamonds.toString() + ""
        }
    }

    fun initSelectHordeView() {
        teamId = (activity as? GameCreateActivity)?.teamId
        costList = (activity as? GameCreateActivity)?.costList
        if (StringUtil.isSpace(teamId) || mHordeEntity != null || costList == null || costList!!.size <= 0) {
            return
        }
        mSelectedHordeEntity = costList?.get(0)
        (mRootView.findViewById(R.id.view_stub_select_horde) as ViewStub).inflate()
        horde_content = mRootView.findViewById(R.id.horde_content)
        //one
        horde_one_container = mRootView.findViewById(R.id.horde_one_container)
        horde_one_container?.setOnClickListener(OnHordeClick(0))
        horde_one_container?.visibility = if (costList!!.size < 1) View.GONE else View.VISIBLE
        iv_horde_one = mRootView.findViewById(R.id.iv_horde_one) as EasySwitchBtn
        iv_horde_one?.setOnClickListener(OnHordeClick(0))
        iv_horde_one?.setChecked(true)
        previousHorde = iv_horde_one
        tv_horde_one = mRootView.findViewById(R.id.tv_horde_one) as TextView
        tv_horde_one?.text = if (costList!!.size < 1) "" else costList!![0].name
        //two
        horde_two_container = mRootView.findViewById(R.id.horde_two_container)
        horde_two_container?.setOnClickListener(OnHordeClick(1))
        horde_two_container?.visibility = if (costList!!.size < 2) View.GONE else View.VISIBLE
        iv_horde_two = mRootView.findViewById(R.id.iv_horde_two) as EasySwitchBtn
        iv_horde_two?.setOnClickListener(OnHordeClick(1))
        tv_horde_two = mRootView.findViewById(R.id.tv_horde_two) as TextView
        tv_horde_two?.text = if (costList == null || costList!!.size < 2) "" else costList!![1].name
        //three
        horde_three_container = mRootView.findViewById(R.id.horde_three_container)
        horde_three_container?.setOnClickListener(OnHordeClick(2))
        horde_three_container?.visibility = if (costList == null || costList!!.size < 3) View.GONE else View.VISIBLE
        iv_horde_three = mRootView.findViewById(R.id.iv_horde_three) as EasySwitchBtn
        iv_horde_three?.setOnClickListener(OnHordeClick(2))
        tv_horde_three = mRootView.findViewById(R.id.tv_horde_three) as TextView
        tv_horde_three?.text = if (costList == null || costList!!.size < 3) "" else costList!![2].name
        //four
        horde_four_container = mRootView.findViewById(R.id.horde_four_container)
        horde_four_container?.setOnClickListener(OnHordeClick(3))
        horde_four_container?.visibility = if (costList!!.size < 4) View.GONE else View.VISIBLE
        iv_horde_four = mRootView.findViewById(R.id.iv_horde_four) as EasySwitchBtn
        iv_horde_four?.setOnClickListener(OnHordeClick(3))
        tv_horde_four = mRootView.findViewById(R.id.tv_horde_four) as TextView
        tv_horde_four?.text = if (costList == null || costList!!.size < 4) "" else costList!![3].name
        //创建部落牌局  消耗钻石相关
        horde_consume_num = mRootView.findViewById(R.id.horde_consume_num) as TextView
        val it = mFeeSeekBar?.currentPosition ?: 0
        val hordeDiamondCOnsume = GameConfigData.create_mtt_fee[if (it < GameConfigData.create_mtt_fee.size) it else GameConfigData.create_mtt_fee.size - 1]
        horde_consume_num?.text = "$hordeDiamondCOnsume"
        horde_remain_num = mRootView.findViewById(R.id.horde_remain_num) as TextView
        horde_remain_num?.text = diamonds.toString()
        switch_horde = mRootView.findViewById(R.id.switch_horde) as SwitchButton
        switch_horde?.setOnClickListener({
            val hasChecked = switch_horde?.isChecked ?: false
            horde_content?.visibility = if (hasChecked) View.VISIBLE else View.GONE
        })
    }

    inner class OnHordeClick(internal var hordeIndex: Int) : View.OnClickListener {
        override fun onClick(v: View) {
            val isClickOne = v === horde_one_container || v === iv_horde_one
            val isClickTwo = v === horde_two_container || v === iv_horde_two
            val isClickTree = v === horde_three_container || v === iv_horde_three
            val isClickFour = v === horde_four_container || v === iv_horde_four
            if (costList != null && costList!!.size == 1) {
                iv_horde_one?.setChecked(!iv_horde_one!!.isChecked)
            } else {
                iv_horde_one?.setChecked(if ((previousHorde === horde_one_container || previousHorde === iv_horde_one) && isClickOne) !iv_horde_one!!.isChecked else isClickOne)
            }
            iv_horde_two?.setChecked(if ((previousHorde === horde_two_container || previousHorde === iv_horde_two) && isClickTwo) !iv_horde_two!!.isChecked else isClickTwo)
            iv_horde_three?.setChecked(if (previousHorde === horde_three_container || previousHorde === iv_horde_three && isClickTree) !iv_horde_three!!.isChecked else isClickTree)
            iv_horde_four?.setChecked(if ((previousHorde === horde_four_container || previousHorde === iv_horde_four) && isClickFour) !iv_horde_four!!.isChecked else isClickFour)
            previousHorde = v
            if (costList == null || costList!!.size <= hordeIndex || horde_consume_num == null) {
                return
            }
            mSelectedHordeEntity = costList!![hordeIndex]
            val hordeSelectNum = 0 + (if (iv_horde_one!!.isChecked) 1 else 0) + (if (iv_horde_two!!.isChecked) 1 else 0) + (if (iv_horde_three!!.isChecked) 1 else 0) + if (iv_horde_four!!.isChecked) 1 else 0
            val it = mFeeSeekBar?.currentPosition ?: 0
            val hordeDiamondCOnsume = GameConfigData.create_mtt_fee[if (it < GameConfigData.create_mtt_fee.size) it else GameConfigData.create_mtt_fee.size - 1]
            horde_consume_num?.text = if (hordeSelectNum <= 0) "0" else "$hordeDiamondCOnsume"//mSelectedHordeEntity?.money.toString()
            if (hordeSelectNum <= 0) {
                mSelectedHordeEntity = null
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onPause() {
        super.onPause()
        LogUtil.i(TAG, "onPause")
    }

    override fun onResume() {
        super.onResume()
        LogUtil.i(TAG, "onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_hunter_normal, R.id.tv_hunter_normal_des -> {
                changeHunterMode(1)
                showSuperHunter(false)
            }
            R.id.iv_hunter_super, R.id.tv_hunter_super_des -> {
                changeHunterMode(2)
                showSuperHunter(true)
            }
            R.id.iv_explain_checkin_fee -> {
                val explainStrId = if (diamond_match == GameConstants.MATCH_TYPE_NORMAL) R.string.data_null else if (diamond_match == GameConstants.MATCH_TYPE_DIAMOND) R.string.checkin_fee_diamond else R.string.checkin_fee_gold
                showPlayLimitDialog(v, ChessApp.sAppContext.resources.getString(explainStrId))
            }
        }
    }

    internal var instructionsPopView: MatchCreateRulesPopView? = null
    fun showPlayLimitDialog(view: View, content: String) {
        if (instructionsPopView == null) {
            instructionsPopView = MatchCreateRulesPopView(activity, MatchCreateRulesPopView.TYPE_LEFT)
            instructionsPopView?.setBackground(R.drawable.bg_mtt_instructions)
        }
        instructionsPopView?.tv_match_rule_content?.text = content
        instructionsPopView?.tv_match_rule_content?.setTextColor(activity.resources.getColor(R.color.text_select_color))
        if (!instructionsPopView!!.isShowing) {
            instructionsPopView?.showAsDropDown(view, ScreenUtil.dip2px(activity, -42f), ScreenUtil.dip2px(activity, -4f))
        }
    }

    fun isGameBeginTimeVaild(time: Long): Boolean {
        if (time == -1L) {
            return true
        }
        val currentTime = DemoCache.getCurrentServerSecondTime()
        if (time < currentTime + 60 * 0) {
            com.htgames.nutspoker.widget.Toast.makeText(context, R.string.game_create_mtt_begintime_early, com.htgames.nutspoker.widget.Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    private fun checkAutoStartDialog(): Boolean {////设置手动开赛需要用一个对话框提示，true表示显示dialog
        if (choiceTime > -1 && switch_auto_start.visibility == View.VISIBLE && switch_auto_start.isChecked) {
            return false
        }
        val autoStartDialog = EasyAlertDialogHelper.createOkCancelDiolag(activity, "", "该比赛需要手动开赛，保留15天，15天内未开赛，该比赛将自动解散",
                "开赛", getString(R.string.cancel), false,
                object : EasyAlertDialogHelper.OnDialogActionListener {
                    override fun doCancelAction() {}
                    override fun doOkAction() {
                        tryCreatePineappleGame()
                    }
                })
        if (activity != null && !activity.isFinishing) {
            autoStartDialog.show()
        }
        return true
    }
    fun tryCreatePineappleGame() {
        val gameName = edt_game_name.text.toString()
        if (StringUtil.isSpace(gameName)) {
            com.htgames.nutspoker.widget.Toast.makeText(ChessApp.sAppContext, R.string.game_create_name_notnull, com.htgames.nutspoker.widget.Toast.LENGTH_SHORT).show()
            return
        }
        mGameName = GameName(gameName)
        if (!isGameBeginTimeVaild(choiceTime)) {
            return
        }
        val consumeEntity = if (mHordeEntity == null) {
            if (switch_horde != null && switch_horde!!.isChecked) mSelectedHordeEntity else null
        } else mHordeEntity//这两个hordeentity不能同时存在的，至少一个为null，mHordeEntity表示从部落大厅传过来的
        val hasSufficientDiamond = if (switch_horde != null && switch_horde!!.isChecked) {
            if (consumeEntity == null) true else diamonds >= consumeEntity.money
        } else true
        val is_control = if (mHordeEntity == null) {
            consumeEntity?.is_control ?: 0
        } else mHordeEntity!!.is_control
        val is_my = if (mHordeEntity == null) {
            consumeEntity?.is_my ?: 0
        } else mHordeEntity!!.is_my
        if (is_my != 1 && is_control == 1) {//部落禁止建局，那么也禁止共享
            showHordeControlDialog()
            return
        }
        if (!hasSufficientDiamond) {//创建部落牌局需要花费钻石，钻石不足的提示对话框
            showTopUpDialog(ShopActivity.TYPE_SHOP_DIAMOND)
            return
        }
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            com.htgames.nutspoker.widget.Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, com.htgames.nutspoker.widget.Toast.LENGTH_LONG).show()
            return
        }
        val gameAreType: Int = (activity as? GameCreateActivity)?.gameAreType ?: 1
        val teamId = (activity as? GameCreateActivity)?.teamId ?: ""
        val horde_id = consumeEntity?.horde_id ?: ""
        val pineappleObject = PineappleConfigMtt(play_type)
        if (choiceTime == (-1).toLong()) {
            pineappleObject.is_auto = 0
        } else {
            pineappleObject.is_auto = if (switch_auto_start.isChecked) 1 else 0
        }
        pineappleObject.beginTime = choiceTime
        pineappleObject.matchCheckinFee = mFeeSeekBar!!.getDataItem(mFeeSeekBar!!.currentPosition)
        pineappleObject.matchChips = mChipsSeekBar.getDataItem(mChipsSeekBar.currentPosition)
        pineappleObject.matchDuration = mTimeSeekBar.getDataItem(mTimeSeekBar.currentPosition) * 60
        pineappleObject.match_max_buy_cnt = seekbar_total_buy.getDataItem(seekbar_total_buy.currentPosition)
        pineappleObject.matchLevel = mBlindsLevelSeekBar.getDataItem(mBlindsLevelSeekBar.currentPosition)
        pineappleObject.match_rebuy_cnt = mtt_rebuy_seekbar.getDataItem(mtt_rebuy_seekbar.currentPosition)
        pineappleObject.is_control = if (ck_game_control_into.isChecked) 1 else 0
        pineappleObject.restMode = if (ck_game_match_rest.isChecked) 1 else 0
        pineappleObject.ko_mode = mKoMode
        pineappleObject.match_type = diamond_match
        pineappleObject.ante_table_type = ante_table_type
        pineappleObject.ko_reward_rate = seekbar_normal_hunter_ratio!!.getDataItem(seekbar_normal_hunter_ratio!!.currentPosition)
        if (mKoMode == 2) {//最好做下判断，只有超级猎人赛才传这个比例
            pineappleObject.ko_head_rate = seekbar_super_hunter_ratio.getDataItem(seekbar_super_hunter_ratio.currentPosition)
        }
        //下面的值不传给服务端，只是保存带本地
        pineappleObject.match_checkin_fee_index = mFeeSeekBar!!.currentPosition
        pineappleObject.match_chips_index= mChipsSeekBar.currentPosition
        pineappleObject.match_duration_index = mTimeSeekBar.currentPosition
        pineappleObject.match_max_buy_cnt_index = seekbar_total_buy.currentPosition
        pineappleObject.match_level_index = mBlindsLevelSeekBar.currentPosition
        pineappleObject.match_rebuy_cnt_index = mtt_rebuy_seekbar.currentPosition
        pineappleObject.ko_reward_rate_index = seekbar_normal_hunter_ratio!!.currentPosition
        pineappleObject.ko_head_rate_index = seekbar_super_hunter_ratio.currentPosition
        saveCreateGameConfig(pineappleObject)
        (activity as? GameCreateActivity)?.mGameAction?.doPineappleMttCreate(gameAreType, teamId, edt_game_name.text.toString(), pineappleObject, horde_id, GameConstants.PLAY_MODE_PINEAPPLE, object : GameRequestCallback {
            override fun onSuccess(json: JSONObject) {
                CreateGameConfigPref.getInstance(activity).playMode = GameConstants.PLAY_MODE_PINEAPPLE
                GamePreferences.getInstance(ChessApp.sAppContext).pineappleMttGamePrefix = mGameName.prefix
                GamePreferences.getInstance(ChessApp.sAppContext).pineappleMttGameCount = mGameName.gameCount + 1
                if (activity is GameCreateActivity) {
                    (activity as GameCreateActivity).onCreateMatchSuccess(json.getJSONObject("data"))
                }
            }
            override fun onFailed(code: Int, json: JSONObject?) {
                if (code == ApiCode.CODE_GAME_IS_CREATED) {
                    Toast.makeText(ChessApp.sAppContext, R.string.game_create_failed_already, Toast.LENGTH_SHORT).show()
                } else if (code == ApiCode.CODE_BALANCE_INSUFFICIENT) {//余额不足，创建失败，提示购买
                    showTopUpDialog(ShopActivity.TYPE_SHOP_COIN)
                } else if (code == ApiCode.CODE_GAME_CREATE_FAILURE) {
                    Toast.makeText(ChessApp.sAppContext, R.string.game_create_failed, Toast.LENGTH_SHORT).show()
                } else if (code == ApiCode.CODE_GAME_NAME_LENGTH_LONG) {
                    Toast.makeText(ChessApp.sAppContext, R.string.game_create_name_long, Toast.LENGTH_SHORT).show()
                } else if (code == ApiCode.CODE_GAME_NAME_FORMAT_WRONG) {
                    Toast.makeText(ChessApp.sAppContext, R.string.game_create_name_invalid, Toast.LENGTH_SHORT).show()
                } else if (code == ApiCode.CODE_GAME_TEAM_COUNT_IS_LIMIT || code == ApiCode.CODE_GAME_PRIVATE_COUNT_IS_LIMIT) {//用户的俱乐部牌局创建已经达到上限3024      //用户的私人牌局创建已经达到上限3025
                    showCreateLimitDialog(code)
                } else if (code == ApiCode.CODE_CLUB_CREATE_BY_OWNER) {
                    Toast.makeText(ChessApp.sAppContext, R.string.game_create_by_club_creator, Toast.LENGTH_SHORT).show()
                } else if (code == ApiCode.CODE_UPDATE_NICKNAME_UBSUFFICIENT_DIAMOND) {
                    showTopUpDialog(ShopActivity.TYPE_SHOP_DIAMOND)//钻石不足
                } else {
                    var message = ApiResultHelper.getShowMessage(json)
                    if (StringUtil.isEmpty(message)) {
                        message = ChessApp.sAppContext.getString(R.string.game_create_failed)
                    }
                    Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    private var mGameConfig: PineappleConfigMtt? = null
    fun getCreateGameConfig() {
        val historyListStr = CreateGameConfigPref.getInstance(activity).pineappleConfigMtt
        LogUtil.i("getCreateGameConfig: " + historyListStr)
        val type = object : TypeToken<PineappleConfigMtt>() {
        }.type
        if (!StringUtil.isSpace(historyListStr)) {
            mGameConfig = GsonUtils.getGson().fromJson<PineappleConfigMtt>(historyListStr, type)
        }
    }

    fun setGameConfig() {
        if (mGameConfig == null) {
            return
        }
        val typeTVs = arrayListOf<TextView>(btn_pineapple_mode_normal, btn_pineapple_mode_blood, btn_pineapple_mode_blood_in_out, btn_pineapple_mode_yoriko)
        typeTVs[if (mGameConfig!!.play_type < typeTVs.size) mGameConfig!!.play_type else 0 ].performClick()
        mKoMode = mGameConfig!!.ko_mode
        mFeeSeekBar!!.setProgress(if (mGameConfig!!.match_checkin_fee_index >= mFeeSeekBar!!.datas.size) mFeeSeekBar!!.datas.size - 1 else mGameConfig!!.match_checkin_fee_index)//防止配置更改数组越界
//        mChipsSeekBar.setProgress(if (mGameConfig.match_chips_index >= mttChipsData.size) mttChipsData.size - 1 else mGameConfig.matchChips_index)
        mTimeSeekBar.setProgress(if (mGameConfig!!.match_duration_index >= GameConfigData.pineapple_mtt_ante_time.size) GameConstants.sngTimeMinutes.size - 1 else mGameConfig!!.match_duration_index)
        seekbar_total_buy.setProgress(if (mGameConfig!!.match_max_buy_cnt_index < seekbar_total_buy.datas.size) mGameConfig!!.match_max_buy_cnt_index else seekbar_total_buy.datas.size - 1)
        mBlindsLevelSeekBar.setProgress(if (mGameConfig!!.match_level_index < mBlindsLevelSeekBar.datas.size) mGameConfig!!.match_level_index else mBlindsLevelSeekBar.datas.size - 1)
        mtt_rebuy_seekbar.setProgress(if (mGameConfig!!.match_rebuy_cnt_index < mtt_rebuy_seekbar.datas.size) mGameConfig!!.match_rebuy_cnt_index else mtt_rebuy_seekbar.datas.size - 1)
        ck_game_control_into.isChecked = mGameConfig!!.is_control == 1
        ck_game_match_rest.isChecked = mGameConfig!!.restMode > 0
        //猎人赛
        iv_hunter_normal.setChecked(mKoMode == 1)
        iv_hunter_super.setChecked(mKoMode == 2)
        hunter_switch.isChecked = mGameConfig!!.ko_mode > 0
        ll_hunter_config_content.visibility = if (hunter_switch.isChecked) View.VISIBLE else View.GONE
        seekbar_normal_hunter_ratio!!.setProgress(if (mGameConfig!!.ko_reward_rate_index >= ko_reward_rate.size) ko_reward_rate.size - 1 else mGameConfig!!.ko_reward_rate_index)
        seekbar_super_hunter_ratio.setProgress(if (mGameConfig!!.ko_head_rate_index >= GameConfigData.ko_reward_rate.size) GameConfigData.ko_reward_rate.size - 1 else mGameConfig!!.ko_head_rate_index)
        changeHunterMode(mKoMode)
        showSuperHunter(mKoMode == 2)
        diamond_match = mGameConfig!!.match_type
        setMatchTypeUI()
    }

    private fun saveCreateGameConfig(pineappleObject: PineappleConfigMtt) {//保存创建牌局的设置
        val historyMgrListStr = GsonUtils.getGson().toJson(pineappleObject)
        LogUtil.i("saveCreateGameConfig: " + historyMgrListStr)
        CreateGameConfigPref.getInstance(activity).pineappleConfigMtt = historyMgrListStr//把管理员历史记录写入到sharedpreference
    }

    companion object {
        val TAG = CreatePineappleMttFrg::class.java.simpleName
        fun newInstance(): CreatePineappleMttFrg {
            val mInstance = CreatePineappleMttFrg()
            val bundle = Bundle()
            mInstance.arguments = bundle
            return mInstance
        }
    }

    override fun afterGetAmount() {
        super.afterGetAmount()
        if (sure_horde_remain_num != null) {
            sure_horde_remain_num?.text = (diamonds.toString() + "")
        }
        if (horde_remain_num != null) {
            horde_remain_num?.text = diamonds.toString() + ""
        }
    }
}

