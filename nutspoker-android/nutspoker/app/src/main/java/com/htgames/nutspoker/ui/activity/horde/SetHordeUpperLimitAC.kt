package com.htgames.nutspoker.ui.activity.horde

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.Toast
import com.htgames.nutspoker.R
import com.htgames.nutspoker.interfaces.GameRequestCallback
import com.htgames.nutspoker.ui.action.HordeAction
import com.htgames.nutspoker.ui.activity.horde.util.HordeUpdateManager
import com.htgames.nutspoker.ui.activity.horde.util.UpdateItem
import com.htgames.nutspoker.ui.base.BaseActivity
import com.htgames.nutspoker.ui.inputfilter.NameLengthFilter
import com.htgames.nutspoker.ui.inputfilter.NameRuleFilter
import com.netease.nim.uikit.chesscircle.entity.HordeEntity
import com.netease.nim.uikit.chesscircle.entity.TeamEntity
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper
import com.netease.nim.uikit.common.ui.imageview.HeadImageView
import com.netease.nim.uikit.common.util.string.StringUtil
import com.netease.nim.uikit.session.constant.Extras
import kotlinx.android.synthetic.main.activity_set_horde_upper_limit.*
import org.json.JSONObject

class SetHordeUpperLimitAC : BaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_set_team_score_submit -> {
                if (StringUtil.isSpace(et_team_score_num.text.toString())) {
                    Toast.makeText(this@SetHordeUpperLimitAC, "请设置合理的分数", Toast.LENGTH_SHORT).show()
                    return
                }
                trySetScore()
            }
        }
    }

    companion object {
        fun start(activity: Activity, teamEntity: TeamEntity, hordeEntity: HordeEntity) {
            val intent: Intent = Intent(activity, SetHordeUpperLimitAC::class.java)
            intent.putExtra(Extras.EXTRA_CUSTOMIZATION, hordeEntity)
            intent.putExtra(Extras.EXTRA_DATA, teamEntity)
            activity.startActivity(intent)
        }
    }

    private lateinit var mHordeEntity: HordeEntity
    private lateinit var mTeam: TeamEntity
    private lateinit var mHordeAction: HordeAction
    override fun onCreate(savedInstanceState: Bundle?) {
        mHordeEntity = intent.getSerializableExtra(Extras.EXTRA_CUSTOMIZATION) as HordeEntity
        mTeam = intent.getSerializableExtra(Extras.EXTRA_DATA) as TeamEntity
        super.onCreate(savedInstanceState)
        mHordeAction = HordeAction(this, null)
        setContentView(R.layout.activity_set_horde_upper_limit)
        iv_club_info_head.loadClubAvatarByUrl(mTeam.id, /*ClubConstant.getClubExtAvatar(extServer)*/mTeam.avatar, HeadImageView.DEFAULT_AVATAR_THUMB_SIZE)
        tv_team_name.text = mTeam.name
        tv_team_vid.text = "ID: ${mTeam.vid}"
        tv_team_score.text = "当前分: ${mTeam.score}"
        btn_set_team_score_submit.setOnClickListener(this)
        et_team_score_num.filters = arrayOf(NameLengthFilter(6), NameRuleFilter())
        setHeadTitle("匹配上分")
    }

    internal var topUpDialog: EasyAlertDialog? = null
    fun trySetScore() {
        if (topUpDialog == null) {
            topUpDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "",
                    "确认设置俱乐部“${mTeam.name}”上分为${et_team_score_num.text}?", getString(R.string.ok), getString(R.string.cancel), true,
                    object : EasyAlertDialogHelper.OnDialogActionListener {

                        override fun doCancelAction() {
                            topUpDialog?.dismiss()
                        }

                        override fun doOkAction() {
                            mHordeAction.setScore(mHordeEntity.horde_id, mTeam.id, et_team_score_num.text.toString(), object : GameRequestCallback {
                                override fun onFailed(code: Int, response: JSONObject?) {
                                }

                                override fun onSuccess(response: JSONObject?) {
                                    HordeUpdateManager.getInstance().execludeCallback(UpdateItem(UpdateItem.UPDATE_TYPE_SET_SCORE).setTid(mTeam.id).setScore(et_team_score_num.text.toString().toInt()))
                                    this@SetHordeUpperLimitAC.finish()
                                }

                            })
                        }
                    })
        }
        if (!isFinishing && !isDestroyedCompatible) {
            topUpDialog?.setMessage("确认设置俱乐部“${mTeam.name}”上分为${et_team_score_num.text}?")
            topUpDialog?.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mHordeAction != null) {
            mHordeAction.onDestroy()
        }
    }
}
