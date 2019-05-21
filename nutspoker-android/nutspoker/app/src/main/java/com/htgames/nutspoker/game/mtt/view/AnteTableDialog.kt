package com.htgames.nutspoker.game.mtt.view

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.htgames.nutspoker.R
import com.htgames.nutspoker.config.GameConfigData
import com.htgames.nutspoker.game.bean.BlindStuctureEntity
import com.htgames.nutspoker.game.mtt.adapter.MttBlindsStructureAdapter
import com.netease.nim.uikit.bean.GameEntity
import com.netease.nim.uikit.common.util.BaseTools
import com.netease.nim.uikit.common.util.sys.ScreenUtil
import com.netease.nim.uikit.constants.GameConstants
import java.util.*

/**
 * Created by 周智慧 on 2017/8/28.
 */
class AnteTableDialog(mContext: Context, mGameInfo: GameEntity) : Dialog(mContext, R.style.MyDialog) {
    private var rootView: View
    lateinit var gv_blinds_stucture: GridView
    lateinit var mBlindsStructureAdapter: MttBlindsStructureAdapter
    var blindStuctureList = ArrayList<BlindStuctureEntity>()
    lateinit var btn_close: ImageView
    lateinit var tv_create_mtt_blinds_relation_title: TextView
    lateinit var tv_blinds_stucture_level: TextView
    lateinit var tv_blinds_stucture_ante: TextView
    lateinit var ll_mtt_blinds_stucture_desc: LinearLayout
    lateinit var tv_game_match_blinds_rebuy_desc: TextView
    lateinit var tv_game_match_blinds_addon_desc: TextView
    lateinit var tv_game_match_blinds_termination_join_desc: TextView
    var gameMode = GameConstants.GAME_MODE_MTT
    init {
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = BaseTools.getWindowHeigh(context) * 2 / 3
        //        setAnimationStyle(R.style.PopupAnimation);
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        //
        val win = this.window
        win!!.setGravity(Gravity.CENTER)
        win.decorView.setPadding(0, 0, 0, 0)
        val lp = win.attributes
        lp.width = width   //宽度填满
        lp.height = height  //高度自适应
        win.attributes = lp
        //
        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_mtt_blinds_structure_view, null)
        val padding = ScreenUtil.dp2px(context, 20f)//setPadding(padding, 0, padding, 0)
        initView(context)
        setContentView(rootView!!)
        (rootView!!.layoutParams as FrameLayout.LayoutParams).setMargins(padding, 0, padding, 0)
    }

    private fun initView(context: Context) {
        ll_mtt_blinds_stucture_desc = rootView!!.findViewById(R.id.ll_mtt_blinds_stucture_desc) as LinearLayout
        tv_game_match_blinds_rebuy_desc = rootView!!.findViewById(R.id.tv_game_match_blinds_rebuy_desc) as TextView
        tv_game_match_blinds_addon_desc = rootView!!.findViewById(R.id.tv_game_match_blinds_addon_desc) as TextView
        tv_game_match_blinds_termination_join_desc = rootView!!.findViewById(R.id.tv_game_match_blinds_termination_join_desc) as TextView
        gv_blinds_stucture = rootView!!.findViewById(R.id.gv_blinds_stucture) as GridView
        tv_create_mtt_blinds_relation_title = rootView!!.findViewById(R.id.tv_create_mtt_blinds_relation_title) as TextView
        rootView!!.findViewById(R.id.tv_blinds_stucture_blinds).visibility = View.GONE
        tv_blinds_stucture_level = rootView!!.findViewById(R.id.tv_blinds_stucture_level) as TextView
        tv_blinds_stucture_ante = rootView!!.findViewById(R.id.tv_blinds_stucture_ante) as TextView
        tv_blinds_stucture_ante.text = "底注"
        btn_close = rootView!!.findViewById(R.id.btn_close) as ImageView
        btn_close.setOnClickListener { this@AnteTableDialog.dismiss() }
        if (gameMode == GameConstants.GAME_MODE_MTT) {
            tv_create_mtt_blinds_relation_title.setText(R.string.pineapple_mtt_ante_table)
        } else if (gameMode == GameConstants.GAME_MODE_MT_SNG) {
            tv_create_mtt_blinds_relation_title.setText(R.string.game_create_mtsng_blinds_relation)
        }
        //
        mBlindsStructureAdapter = MttBlindsStructureAdapter(context, blindStuctureList, true)
        mBlindsStructureAdapter.showBlind = false
        gv_blinds_stucture.adapter = mBlindsStructureAdapter
    }

    fun setData(ante_table_type: Int) {
        mBlindsStructureAdapter.notifyDataSetChanged()
        val datas = if (ante_table_type == GameConstants.PINEAPPLE_MTT_ANTE_TABLE_NORMAL) GameConfigData.pineapple_mtt_ante else GameConfigData.pineapple_mtt_ante_quick
        for (i in datas.indices) {
            val entity = BlindStuctureEntity()
            entity.level = i + 1
            entity.ante = datas[i]
            blindStuctureList.add(entity)
        }
        mBlindsStructureAdapter.notifyDataSetChanged()
    }

    fun setMttBlindLevel(matchLevel: Int, isRebuyMode: Boolean, isAddonMode: Boolean) {
        if (matchLevel > 0) {
            mBlindsStructureAdapter.setMatchRoomInfo(matchLevel, isRebuyMode, isAddonMode)
            ll_mtt_blinds_stucture_desc.visibility = View.VISIBLE
            //
            if (isRebuyMode) {
                tv_game_match_blinds_rebuy_desc.visibility = View.VISIBLE
                tv_game_match_blinds_rebuy_desc.text = context.getString(R.string.game_match_blinds_rebuy_desc, matchLevel - 1)
            } else {
                tv_game_match_blinds_rebuy_desc.visibility = View.GONE
            }
            if (isAddonMode) {
                tv_game_match_blinds_addon_desc.visibility = View.VISIBLE
                tv_game_match_blinds_addon_desc.text = context.getString(R.string.game_match_blinds_addon_desc, matchLevel)
            } else {
                tv_game_match_blinds_addon_desc.visibility = View.GONE
            }
            //            tv_game_match_blinds_termination_join_desc.setText(SpannableUtils.getSpannableString(context.getString(R.string.match_room_blind_level), context.getString(R.string.match_room_blind_level_desc, matchLevel)));
            tv_game_match_blinds_termination_join_desc.text = "终止报名：" + "第" + matchLevel + "底注级别"
        } else {
            ll_mtt_blinds_stucture_desc.visibility = View.GONE
        }
    }
}