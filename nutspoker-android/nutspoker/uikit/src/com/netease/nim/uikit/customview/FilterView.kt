package com.netease.nim.uikit.customview

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.netease.nim.uikit.AnimUtil
import com.netease.nim.uikit.R
import com.netease.nim.uikit.common.util.sys.ScreenUtil

/**
 * Created by 周智慧 on 2017/9/26.
 */
class FilterView : LinearLayout {
    var arrow_type = 0//<!-- 0无箭头  1只有上箭头  2只有下箭头  3有上下两个箭头 -->
    var mMmode = 0//0 全灰   1上亮   2下亮  3全亮
    var id_iv_right = -1
    var colorNormal: Int = 0
    var colorSelected: Int = 0
    var id_arrow: Int = -1
    var textStrId = -1
    var filter_view_is_selected = false
    //四个view
    var tv_content: TextView? = null//<!-- 最左边的文案 -->
    var iv_arrow: ImageView? = null
    var iv_right: ImageView? = null//<!-- 最右边的图片 -->
    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.FilterView, defStyleAttr, 0)
        arrow_type = ta.getInt(R.styleable.FilterView_arrow_type, 3)
        filter_view_is_selected = ta.getBoolean(R.styleable.FilterView_filter_view_is_selected, false)
        id_iv_right = ta.getResourceId(R.styleable.FilterView_iv_right, -1)
        id_arrow = ta.getResourceId(R.styleable.FilterView_id_arrow, -1)
        colorNormal = ContextCompat.getColor(context, ta.getResourceId(R.styleable.FilterView_color_normal, R.color.login_grey_color))
        colorSelected = ContextCompat.getColor(context, ta.getResourceId(R.styleable.FilterView_color_selected, R.color.login_solid_color))
        textStrId = ta.getResourceId(R.styleable.FilterView_text, -1)
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        tv_content = TextView(context)
        tv_content?.setText((if (textStrId == -1) R.string.paiju_filter_all else textStrId))
        tv_content?.setPadding(0, 0, ScreenUtil.dp2px(context, 5f), 0)
        iv_arrow = ImageView(context)
        iv_arrow?.setImageResource(id_arrow)
        addView(tv_content)
        if (id_arrow != -1) {
            addView(iv_arrow)
        }
        if (id_iv_right > -1) {
            iv_right = ImageView(context)
            iv_right?.setImageResource(id_iv_right)
            addView(iv_right)
        }
        if (filter_view_is_selected) {
            changeMode(1)
        }
    }

    fun changeMode(mode: Int) {
        mMmode = mode
        tv_content?.setTextColor(if (mMmode == 0) colorNormal else colorSelected)
        if (arrow_type == 0) {
        } else if (arrow_type == 1 || arrow_type == 2) {
            iv_arrow?.setColorFilter(if (mMmode == 0) colorNormal else colorSelected)
        } else if (arrow_type == 3) {
            iv_arrow?.setImageResource(if (mMmode == 0) R.mipmap.room_arrow_nil else if (mMmode == 1) R.mipmap.room_arrow_up else R.mipmap.room_arrow_down)
        }
    }

    fun rotateAnimation() {//只对只有一个箭头的view有效，旋转180度
        var mRotateAnimation = RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        mRotateAnimation.setInterpolator(LinearInterpolator())
        mRotateAnimation.setDuration(300)
        mRotateAnimation.setFillAfter(true)
        AnimUtil.startRotate(iv_arrow, 0, 180, 300, Animation.RESTART)
    }

    fun resetAnimation() {
        var mResetRotateAnimation = RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        mResetRotateAnimation.setInterpolator(LinearInterpolator())
        mResetRotateAnimation.setDuration(300)
        mResetRotateAnimation.setFillAfter(true)
        AnimUtil.startRotate(iv_arrow, 180, 0, 300, Animation.RESTART)
    }

    fun changeContent(strId: Int) {
        tv_content?.setText(strId)
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
    }
}