package com.htgames.nutspoker.ui.fragment

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.TextView
import android.widget.Toast
import com.google.gson.reflect.TypeToken
import com.htgames.nutspoker.ChessApp
import com.htgames.nutspoker.R
import com.netease.nim.uikit.api.ApiCode
import com.htgames.nutspoker.api.ApiResultHelper
import com.htgames.nutspoker.config.GameConfigData
import com.htgames.nutspoker.data.GameName
import com.htgames.nutspoker.game.match.activity.FreeRoomAC
import com.htgames.nutspoker.interfaces.GameRequestCallback
import com.htgames.nutspoker.net.RequestTimeLimit
import com.htgames.nutspoker.ui.activity.Game.GameCreateActivity
import com.htgames.nutspoker.ui.activity.System.ShopActivity
import com.htgames.nutspoker.ui.inputfilter.NameLengthFilter
import com.htgames.nutspoker.ui.inputfilter.NameRuleFilter
import com.htgames.nutspoker.ui.inputfilter.NoSpaceAndEnterInputFilter
import com.htgames.nutspoker.view.MatchCreateRulesPopView
import com.netease.nim.uikit.bean.PineappleConfig
import com.netease.nim.uikit.cache.NimUserInfoCache
import com.netease.nim.uikit.chesscircle.entity.HordeEntity
import com.netease.nim.uikit.common.DemoCache
import com.netease.nim.uikit.common.gson.GsonUtils
import com.netease.nim.uikit.common.preference.CreateGameConfigPref
import com.netease.nim.uikit.common.preference.GamePreferences
import com.netease.nim.uikit.common.util.NetworkUtil
import com.netease.nim.uikit.common.util.log.LogUtil
import com.netease.nim.uikit.common.util.string.StringUtil
import com.netease.nim.uikit.common.util.sys.ScreenUtil
import com.netease.nim.uikit.constants.GameConstants
import kotlinx.android.synthetic.main.fragment_game_create_pineapple_normal.*
import kotlinx.android.synthetic.main.layout_game_create_horde_config.*
import kotlinx.android.synthetic.main.layout_sure_horde.*
import org.json.JSONObject
import java.util.*

/**
 * Created by 周智慧 on 2017/6/21.
 */
class CreatePineappleNormalFrg : BaseGameCreateFragment(), View.OnClickListener {
    var play_type = GameConstants.PINEAPPLE_MODE_NORMAL

    companion object {
        val TAG = CreatePineappleNormalFrg::class.java.simpleName
        fun newInstance(): CreatePineappleNormalFrg {
            return CreatePineappleNormalFrg()
        }
    }

    var mRootView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getCreateGameConfig()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mRootView = LayoutInflater.from(context).inflate(R.layout.fragment_game_create_pineapple_normal, container, false)
        setFragmentName(CreatePineappleNormalFrg::class.java.simpleName)
        return mRootView
    }

    lateinit var mGameName: GameName
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        edt_game_name.filters = arrayOf(NoSpaceAndEnterInputFilter(), NameLengthFilter(GameConstants.MAX_GAMENAME_LENGTH), NameRuleFilter())
        var pineappleGamePrefix = GamePreferences.getInstance(ChessApp.sAppContext).pineappleGamePrefix
        var pineappleGameCount = GamePreferences.getInstance(ChessApp.sAppContext).pineappleGameCount
        mGameName = GameName("$pineappleGamePrefix$pineappleGameCount")
        switch_game_control_into.setOnClickListener(this)
        edt_game_name.setText(mGameName.name)
        edt_game_name.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                //隐藏键盘
                (activity as? GameCreateActivity)?.showKeyboard(false)
                return@OnKeyListener true
            }
            false
        })
        btn_game_start.setOnClickListener(this)
        initPineappleMode()
        iv_explain_pineapple_play_limit_chip.setOnClickListener(this)
        game_create_advanced_switch.setOnClickListener(this)
        mDeskNumSeekBarNew.setData(intArrayOf(2, 3), true, false, R.mipmap.game_chip_thumb, "")
        //发牌顺序
        pineapple_seek_bar_deal_order.showCustomTips = true
        pineapple_seek_bar_deal_order.customStrs = arrayOf("同时发牌", "顺序发牌")
        pineapple_seek_bar_deal_order.setData(intArrayOf(0, 1), true, false, R.mipmap.game_chip_thumb, "")
        //摆牌时间
        pineapple_set_poker_rate.showCustomTips = true
        pineapple_set_poker_rate.customStrs = arrayOf("慢速", "快速")
        pineapple_set_poker_rate.setData(intArrayOf(0, 1), true, false, R.mipmap.game_chip_thumb, "")
        initSureHordeView()
        initSelectHordeView()//初始化horde相关的配置
        initSeekbar()
        setGameConfig()//恢复并显示场次创建牌局的设置
    }

    fun initPineappleMode() {
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
            btn_pineapple_mode_normal?.setBackgroundDrawable(if (play_type == GameConstants.PINEAPPLE_MODE_NORMAL) selectDrawable else normalDrawable)
            btn_pineapple_mode_normal.setTextColor(if (play_type == GameConstants.PINEAPPLE_MODE_NORMAL) selectColor else normalColor)
            btn_pineapple_mode_blood?.setBackgroundDrawable(if (play_type == GameConstants.PINEAPPLE_MODE_BLOOD) selectDrawable else normalDrawable)
            btn_pineapple_mode_blood.setTextColor(if (play_type == GameConstants.PINEAPPLE_MODE_BLOOD) selectColor else normalColor)
            btn_pineapple_mode_blood_in_out?.setBackgroundDrawable(if (play_type == GameConstants.PINEAPPLE_MODE_BLOOD_IN_OUT) selectDrawable else normalDrawable)
            btn_pineapple_mode_blood_in_out.setTextColor(if (play_type == GameConstants.PINEAPPLE_MODE_BLOOD_IN_OUT) selectColor else normalColor)
            btn_pineapple_mode_yoriko?.setBackgroundDrawable(if (play_type == GameConstants.PINEAPPLE_MODE_YORIKO) selectDrawable else normalDrawable)
            btn_pineapple_mode_yoriko.setTextColor(if (play_type == GameConstants.PINEAPPLE_MODE_YORIKO) selectColor else normalColor)
        }
        normalDrawable.cornerRadius = ScreenUtil.dp2px(activity, 4f).toFloat()
        normalDrawable.setColor(resources.getColor(R.color.register_page_bg_color))
        normalDrawable.setStroke(ScreenUtil.dp2px(activity, 1f), resources.getColor(R.color.list_item_bg_press))
        btn_pineapple_mode_normal?.setBackgroundDrawable(if (play_type == GameConstants.PINEAPPLE_MODE_NORMAL) selectDrawable else normalDrawable)
        btn_pineapple_mode_normal?.setOnClickListener(pineapple_mode_click)
        btn_pineapple_mode_normal.setTextColor(if (play_type == GameConstants.PINEAPPLE_MODE_NORMAL) selectColor else normalColor)
        btn_pineapple_mode_blood?.setBackgroundDrawable(if (play_type == GameConstants.PINEAPPLE_MODE_BLOOD) selectDrawable else normalDrawable)
        btn_pineapple_mode_blood.setOnClickListener(pineapple_mode_click)
        btn_pineapple_mode_blood.setTextColor(if (play_type == GameConstants.PINEAPPLE_MODE_BLOOD) selectColor else normalColor)
        btn_pineapple_mode_blood_in_out?.setBackgroundDrawable(if (play_type == GameConstants.PINEAPPLE_MODE_BLOOD_IN_OUT) selectDrawable else normalDrawable)
        btn_pineapple_mode_blood_in_out.setOnClickListener(pineapple_mode_click)
        btn_pineapple_mode_blood_in_out.setTextColor(if (play_type == GameConstants.PINEAPPLE_MODE_BLOOD_IN_OUT) selectColor else normalColor)
        btn_pineapple_mode_yoriko?.setBackgroundDrawable(if (play_type == GameConstants.PINEAPPLE_MODE_YORIKO) selectDrawable else normalDrawable)
        btn_pineapple_mode_yoriko.setOnClickListener(pineapple_mode_click)
        btn_pineapple_mode_yoriko.setTextColor(if (play_type == GameConstants.PINEAPPLE_MODE_YORIKO) selectColor else normalColor)
    }

    lateinit var chips_array: IntArray
    fun initSeekbar() {
        //带入记分牌，根据底注的index从8个数组选择
        chips_array = GameConfigData.pineapple_chips[0]
        seek_bar_pineapple_chips.setData(chips_array, false, false, R.mipmap.game_chip_thumb, "")
        tv_pineapple_chip_num.text = seek_bar_pineapple_chips.getDataItem(seek_bar_pineapple_chips.currentPosition).toString()
        seek_bar_pineapple_chips.setOnNodeChangeListener {
            tv_pineapple_chip_num.text = chips_array[it].toString()
        }
        //入局限制
        seek_bar_pineapple_play_limit_chip.setData(GameConfigData.pineapple_chips_limit_multiple.map { it -> it * GameConfigData.pineapple_antes[0] }.toIntArray(), false, false, R.mipmap.game_chip_thumb, "")
        tv_pineapple_play_limit_chip.text = seek_bar_pineapple_play_limit_chip.getDataItem(seek_bar_pineapple_play_limit_chip.currentPosition).toString()
        seek_bar_pineapple_play_limit_chip.setOnNodeChangeListener {
            tv_pineapple_play_limit_chip.text = seek_bar_pineapple_play_limit_chip.getDataItem(it).toString()
        }
        seek_bar_pineapple_duration.setData(GameConfigData.pineapple_durations, true, true, R.mipmap.game_chip_thumb, "")
        //底注
        seek_bar_pineapple_ante.setData(GameConfigData.pineapple_antes, false, false, R.mipmap.game_chip_thumb, "")
        tv_pineapple_ante_num.text = GameConfigData.pineapple_antes[if (4 < GameConfigData.pineapple_antes.size) 4 else GameConfigData.pineapple_antes.size - 1].toString()
        seek_bar_pineapple_ante.setOnNodeChangeListener {
            val num = GameConfigData.pineapple_antes[it]
            tv_pineapple_ante_num.text = num.toString()
            seek_bar_pineapple_play_limit_chip.onlyUpdateData(GameConfigData.pineapple_chips_limit_multiple.map { it -> it * num }.toIntArray())
            tv_pineapple_play_limit_chip.text = seek_bar_pineapple_play_limit_chip.getDataItem(seek_bar_pineapple_play_limit_chip.currentPosition).toString()
            chips_array = GameConfigData.pineapple_chips[it]
            tv_pineapple_chip_num.text = chips_array[seek_bar_pineapple_chips.currentPosition].toString()
            seek_bar_pineapple_chips.onlyUpdateData(chips_array)
            //共享部落的钻石消耗
            val hordeDiamondCOnsume = GameConfigData.pineapple_fee[if (it < GameConfigData.pineapple_fee.size) it else GameConfigData.pineapple_fee.size - 1]
            if (sure_horde_consume_num != null) {
                sure_horde_consume_num.text = hordeDiamondCOnsume.toString() + ""
            }
            if (horde_consume_num != null) {
                horde_consume_num.text = hordeDiamondCOnsume.toString() + ""
            }
        }
        seek_bar_pineapple_ante.setProgress(4)
        seek_bar_pineapple_play_limit_chip.setProgress(1)
    }

    var mHordeEntity: HordeEntity? = null
    fun initSureHordeView() {
        mHordeEntity = (activity as? GameCreateActivity)?.mHordeEntity
        //从部落中创建牌局时要显示消耗钻石的view，但是选择共享部落的view隐藏
        if (mHordeEntity != null) {
            (mRootView?.findViewById(R.id.view_stub) as ViewStub).inflate()
            sure_horde_consume_num.text = "${GameConfigData.pineapple_fee[0]}"//mHordeEntity?.money.toString()
            sure_horde_remain_num.text = diamonds.toString()
        }
    }

    var teamId: String? = ""
    var costList: ArrayList<HordeEntity>? = null
    var mSelectedHordeEntity: HordeEntity? = null
    var previousHorde: View? = null
    fun initSelectHordeView() {
        teamId = (activity as? GameCreateActivity)?.teamId
        costList = (activity as? GameCreateActivity)?.costList
        if (StringUtil.isSpace(teamId) || mHordeEntity != null || costList == null || costList!!.size <= 0) {
            return
        }
        mSelectedHordeEntity = costList?.get(0)
        (mRootView?.findViewById(R.id.view_stub_select_horde) as ViewStub).inflate()
        //one
        horde_one_container.setOnClickListener(OnHordeClick(0))
        horde_one_container.visibility = if (costList!!.size < 1) View.GONE else View.VISIBLE
        iv_horde_one.setOnClickListener(OnHordeClick(0))
        iv_horde_one.setChecked(true)
        previousHorde = iv_horde_one
        tv_horde_one.text = if (costList == null || costList!!.size < 1) "" else costList!![0].name
        //two
        horde_two_container.setOnClickListener(OnHordeClick(1))
        horde_two_container.visibility = if (costList!!.size < 2) View.GONE else View.VISIBLE
        iv_horde_two.setOnClickListener(OnHordeClick(1))
        tv_horde_two.text = if (costList == null || costList!!.size < 2) "" else costList!![1].name
        //three
        horde_three_container.setOnClickListener(OnHordeClick(2))
        horde_three_container.visibility = if (costList == null || costList!!.size < 3) View.GONE else View.VISIBLE
        iv_horde_three.setOnClickListener(OnHordeClick(2))
        tv_horde_three.text = if (costList == null || costList!!.size < 3) "" else costList!![2].name
        //four
        horde_four_container.setOnClickListener(OnHordeClick(3))
        horde_four_container.visibility = if (costList!!.size < 4) View.GONE else View.VISIBLE
        iv_horde_four.setOnClickListener(OnHordeClick(3))
        tv_horde_four.text = if (costList == null || costList!!.size < 4) "" else costList!![3].name
        //创建部落牌局  消耗钻石相关
        horde_consume_num.text = "${GameConfigData.pineapple_fee[0]}"
        horde_remain_num.text = diamonds.toString()
        switch_horde.setOnClickListener({
            val hasChecked = switch_horde.isChecked
            horde_content.visibility = if (hasChecked) View.VISIBLE else View.GONE
        })
    }

    inner class OnHordeClick(internal var hordeIndex: Int) : View.OnClickListener {
        override fun onClick(v: View) {
            val isClickOne = v === horde_one_container || v === iv_horde_one
            val isClickTwo = v === horde_two_container || v === iv_horde_two
            val isClickTree = v === horde_three_container || v === iv_horde_three
            val isClickFour = v === horde_four_container || v === iv_horde_four
            if (costList != null && costList!!.size == 1) {
                iv_horde_one.setChecked(!iv_horde_one.isChecked)
            } else {
                iv_horde_one.setChecked(if ((previousHorde === horde_one_container || previousHorde === iv_horde_one) && isClickOne) !iv_horde_one.isChecked else isClickOne)
            }
            iv_horde_two.setChecked(if ((previousHorde === horde_two_container || previousHorde === iv_horde_two) && isClickTwo) !iv_horde_two.isChecked else isClickTwo)
            iv_horde_three.setChecked(if (previousHorde === horde_three_container || previousHorde === iv_horde_three && isClickTree) !iv_horde_three.isChecked else isClickTree)
            iv_horde_four.setChecked(if ((previousHorde === horde_four_container || previousHorde === iv_horde_four) && isClickFour) !iv_horde_four.isChecked else isClickFour)
            previousHorde = v
            if (costList == null || costList!!.size <= hordeIndex || horde_consume_num == null) {
                return
            }
            mSelectedHordeEntity = costList!![hordeIndex]
            val hordeSelectNum = 0 + (if (iv_horde_one.isChecked) 1 else 0) + (if (iv_horde_two.isChecked) 1 else 0) + (if (iv_horde_three.isChecked) 1 else 0) + if (iv_horde_four.isChecked) 1 else 0
            val it = seek_bar_pineapple_ante.currentPosition
            val hordeDiamondCOnsume = GameConfigData.pineapple_fee[if (it < GameConfigData.pineapple_fee.size) it else GameConfigData.pineapple_fee.size - 1]
            horde_consume_num.text = if (hordeSelectNum <= 0) "0" else "$hordeDiamondCOnsume"//mSelectedHordeEntity?.money.toString()
            if (hordeSelectNum <= 0) {
                mSelectedHordeEntity = null
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_explain_pineapple_play_limit_chip -> showPlayLimitDialog()
            R.id.game_create_advanced_switch -> showAdvancedConfig(!isAdvancedConfigShow)
            R.id.btn_game_start -> tryCreatePineappleGame()
        }
    }

    internal var instructionsPopView: MatchCreateRulesPopView? = null
    fun showPlayLimitDialog() {
        if (instructionsPopView == null) {
            instructionsPopView = MatchCreateRulesPopView(activity, MatchCreateRulesPopView.TYPE_LEFT)
            instructionsPopView?.setBackground(R.drawable.bg_mtt_instructions)
        }
        instructionsPopView?.tv_match_rule_content?.text = "当记分牌低于此值时，需要补充\n记分牌才能入局"
        instructionsPopView?.tv_match_rule_content?.setTextColor(activity.resources.getColor(R.color.text_select_color))
        if (!instructionsPopView!!.isShowing) {
            instructionsPopView?.showAsDropDown(iv_explain_pineapple_play_limit_chip, ScreenUtil.dip2px(activity, -42f), ScreenUtil.dip2px(activity, -4f))
        }
    }

    private var isAdvancedConfigShow: Boolean = false
    fun showAdvancedConfig(show: Boolean) {
        isAdvancedConfigShow = show
        game_create_advanced_switch_arrow.setImageResource(if (isAdvancedConfigShow) R.mipmap.arrow_advance_up else R.mipmap.arrow_advance_down)
        ll_advanced_config_new.visibility = if (isAdvancedConfigShow) View.VISIBLE else View.GONE
        advanced_switch_tv.text = resources.getString(if (isAdvancedConfigShow) R.string.circle_content_packup else R.string.game_create_config_advanced)
    }

    fun tryCreatePineappleGame() {
        val gameName = edt_game_name.text.toString()
        if (StringUtil.isSpace(gameName)) {
            com.htgames.nutspoker.widget.Toast.makeText(ChessApp.sAppContext, R.string.game_create_name_notnull, com.htgames.nutspoker.widget.Toast.LENGTH_SHORT).show()
            return
        }
        mGameName = GameName(gameName)
        val consumeEntity = if (mHordeEntity == null) {
            if (switch_horde != null && switch_horde.isChecked) mSelectedHordeEntity else null
        } else mHordeEntity//这两个hordeentity不能同时存在的，至少一个为null，mHordeEntity表示从部落大厅传过来的
        val hasSufficientDiamond = if (switch_horde != null && switch_horde.isChecked) {
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
        val pineappleObject = PineappleConfig()
        pineappleObject.play_type = play_type
        pineappleObject.buy_in_control = if (switch_game_control_into.isChecked) 1 else 0
        pineappleObject.ready_time = pineapple_set_poker_rate.getDataItem(pineapple_set_poker_rate.currentPosition)
        pineappleObject.limit_chips = seek_bar_pineapple_play_limit_chip.getDataItem(seek_bar_pineapple_play_limit_chip.currentPosition)
        pineappleObject.ante = seek_bar_pineapple_ante.getDataItem(seek_bar_pineapple_ante.currentPosition)
        pineappleObject.chips = seek_bar_pineapple_chips.getDataItem(seek_bar_pineapple_chips.currentPosition)
        pineappleObject.duration_index = seek_bar_pineapple_duration.currentPosition
        pineappleObject.deal_order = pineapple_seek_bar_deal_order.currentPosition
        pineappleObject.ip_limit = if (normal_game_ip_switch.isChecked) 1 else 0
        pineappleObject.gps_limit = if (normal_game_gps_switch.isChecked) 1 else 0
        pineappleObject.match_player = mDeskNumSeekBarNew.getDataItem(mDeskNumSeekBarNew.currentPosition)
        //下面的值不传给服务端，只是保存带本地
        pineappleObject.ante_index = seek_bar_pineapple_ante.currentPosition
        pineappleObject.chips_index = seek_bar_pineapple_chips.currentPosition
        pineappleObject.limit_chips_index = seek_bar_pineapple_play_limit_chip.currentPosition
        pineappleObject.match_player_index = mDeskNumSeekBarNew.currentPosition
        saveCreateGameConfig(pineappleObject)
        (activity as? GameCreateActivity)?.mGameAction?.doPineappleCreate(gameAreType, teamId, edt_game_name.text.toString(), pineappleObject, horde_id, GameConstants.PLAY_MODE_PINEAPPLE, object : GameRequestCallback {
            override fun onSuccess(json: JSONObject) {
                CreateGameConfigPref.getInstance(activity).playMode = GameConstants.PLAY_MODE_PINEAPPLE
                Toast.makeText(activity, "创建成功", Toast.LENGTH_SHORT).show()
                RequestTimeLimit.lastGetRecentGameTime = 0.toLong()
                RequestTimeLimit.lastGetGameListInHorde = 0
                RequestTimeLimit.lastGetAmontTime = 0
                val data = json.getJSONObject("data")
                val serverIp = data.optString(GameConstants.KEY_GAME_SERVER)
                val roomId = data.optString(GameConstants.KEY_ROOM_ID)
                val gameCode = data.optString(GameConstants.KEY_GAME_CODE)
                GamePreferences.getInstance(ChessApp.sAppContext).pineappleGamePrefix = mGameName.prefix
                GamePreferences.getInstance(ChessApp.sAppContext).pineappleGameCount = mGameName.gameCount + 1
                FreeRoomAC.startByCreate(activity, roomId, teamId, edt_game_name.text.toString(), gameCode, serverIp)
                activity.finish()
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
                    if (TextUtils.isEmpty(message)) {
                        message = ChessApp.sAppContext.getString(R.string.game_create_failed)
                    }
                    Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    private var mGameConfig: PineappleConfig? = null
    fun getCreateGameConfig() {
        val historyListStr = CreateGameConfigPref.getInstance(activity).pineappleConfig
        LogUtil.i("getCreateGameConfig: " + historyListStr)
        val type = object : TypeToken<PineappleConfig>() {
        }.type
        if (!StringUtil.isSpace(historyListStr)) {
            mGameConfig = GsonUtils.getGson().fromJson<PineappleConfig>(historyListStr, type)
        }
    }

    fun setGameConfig() {
        if (mGameConfig == null) {
            return
        }
        val typeTVs = arrayListOf<TextView>(btn_pineapple_mode_normal, btn_pineapple_mode_blood, btn_pineapple_mode_blood_in_out, btn_pineapple_mode_yoriko)
        typeTVs[if (mGameConfig!!.play_type < typeTVs.size) mGameConfig!!.play_type else 0 ].performClick()
        seek_bar_pineapple_ante.setProgress(if (mGameConfig!!.ante_index < seek_bar_pineapple_ante.datas.size) mGameConfig!!.ante_index else 0)
        seek_bar_pineapple_chips.setProgress(if (mGameConfig!!.chips_index < seek_bar_pineapple_chips.datas.size) mGameConfig!!.chips_index else 0)
        seek_bar_pineapple_play_limit_chip.setProgress(if (mGameConfig!!.limit_chips_index < seek_bar_pineapple_play_limit_chip.datas.size) mGameConfig!!.limit_chips_index else 0)
        mDeskNumSeekBarNew.setProgress(if (mGameConfig!!.match_player_index < mDeskNumSeekBarNew.datas.size) mGameConfig!!.match_player_index else 0)
        pineapple_seek_bar_deal_order.setProgress(if (mGameConfig!!.deal_order < pineapple_seek_bar_deal_order.datas.size) mGameConfig!!.deal_order else 0)//发牌顺序
        pineapple_set_poker_rate.setProgress(if (mGameConfig!!.ready_time < pineapple_set_poker_rate.datas.size) mGameConfig!!.ready_time else 0)//摆牌时间
        seek_bar_pineapple_duration.setProgress(if (mGameConfig!!.duration_index < seek_bar_pineapple_duration.datas.size) mGameConfig!!.duration_index else 0)
        switch_game_control_into.isChecked = mGameConfig!!.buy_in_control != 0
        normal_game_ip_switch.isChecked = mGameConfig!!.ip_limit != 0
        normal_game_gps_switch.isChecked = mGameConfig!!.gps_limit != 0
    }

    private fun saveCreateGameConfig(pineappleObject: PineappleConfig) {//保存创建牌局的设置
        val historyMgrListStr = GsonUtils.getGson().toJson(pineappleObject)
        LogUtil.i("saveCreateGameConfig: " + historyMgrListStr)
        CreateGameConfigPref.getInstance(activity).pineappleConfig = historyMgrListStr//把管理员历史记录写入到sharedpreference
    }

    override fun afterGetAmount() {
        super.afterGetAmount()
        if (sure_horde_remain_num != null) {
            sure_horde_remain_num.text = diamonds.toString() + ""
        }
        if (horde_remain_num != null) {
            horde_remain_num.text = diamonds.toString() + ""
        }
    }
}