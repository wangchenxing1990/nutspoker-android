package com.htgames.nutspoker.game.match.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.Callback
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.htgames.nutspoker.ChessApp
import com.htgames.nutspoker.R
import com.htgames.nutspoker.chat.helper.UserUpdateHelper
import com.htgames.nutspoker.game.match.adapter.PicListAdap
import com.htgames.nutspoker.interfaces.GameRequestCallback
import com.htgames.nutspoker.ui.action.GameAction
import com.htgames.nutspoker.ui.base.BaseActivity
import com.netease.nim.uikit.api.ApiCode
import com.netease.nim.uikit.common.media.picker.model.PhotoInfo
import com.netease.nim.uikit.common.ui.dialog.DialogMaker
import com.netease.nim.uikit.common.util.log.LogUtil
import com.netease.nim.uikit.common.util.string.StringUtil
import com.netease.nim.uikit.common.util.sys.ScreenUtil
import com.netease.nim.uikit.interfaces.IClickPayload
import com.netease.nim.uikit.session.actions.PickImageAction
import com.netease.nim.uikit.session.constant.Extras
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallbackWrapper
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.nos.NosService
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum
import kotlinx.android.synthetic.main.activity_edit_match_state.*
import org.json.JSONObject
import java.io.File
import java.util.*

/**
 * Created by 周智慧 on 2017/8/23.
 * http://blog.csdn.net/iamdingruihaha/article/details/73274010
 * 500秒太长了。官方里的操作执行时间如下：
private long mAddDuration = 120;
private long mRemoveDuration = 120;
private long mMoveDuration = 250;
private long mChangeDuration = 250;
 */
class EditMatchStateAC : BaseActivity(), View.OnClickListener, IClickPayload {
    override fun onDelete(position: Int, payload: Any?) {
        LogUtil.i("test_recyclerview_ControlCenterAdap", "onDelete: $position")
        mPicDatas.remove(payload)
        mAdapter.mPicDatas.remove(payload)
        mAdapter.notifyItemRemoved(position)
    }
    override fun onClick(position: Int, payload: Any?) {
        LogUtil.i("test_recyclerview_ControlCenterAdap", "click $position")
    }
    override fun onLongClick(position: Int, payload: Any?) {
    }

    var gameCode: String = ""
    val rightBtnEnabledColor by lazyColor(R.color.white)
    val rightBtnDisenabledColor by lazyColor(R.color.login_grey_color)
    var mGameAction: GameAction? = null
    lateinit var mAdapter: PicListAdap
    var mPicDatas: MutableList<PhotoInfo> = ArrayList<PhotoInfo>()
    var mAddDatas: MutableList<Any> = ArrayList<Any>()
    companion object {
        val MATCH_STATE_MAX_LENGTH = 300//最长600个汉字
        val REQUEST_CODE: Int = 45231
        val KEY_PREVIOUS_STATE = "key_previous_state"
        val KEY_PREVIOUS_PIC_LIST = "key_previous_pic_list"
        fun startForResult(context: Activity, previousState: String, prevoiusPicList: String, codeP: String) {
            var intent = Intent(context, EditMatchStateAC::class.java)
            intent.putExtra(KEY_PREVIOUS_STATE, previousState)
            intent.putExtra(KEY_PREVIOUS_PIC_LIST, prevoiusPicList)
            intent.putExtra(Extras.EXTRA_GAME_CODE, codeP)
            context.startActivityForResult(intent, REQUEST_CODE)
        }
    }

    fun Context.lazyColor(@ColorRes colorResId: Int) : Lazy<Int> = lazy(LazyThreadSafetyMode.NONE) {
        ResourcesCompat.getColor(resources, colorResId, theme)
    }

    var originalPicSize = 0
    var mPreviousState = ""
    var mPreviousPicList = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        mPreviousState = intent.getStringExtra(KEY_PREVIOUS_STATE) as? String ?: ""
        mPreviousPicList = intent.getStringExtra(KEY_PREVIOUS_PIC_LIST) as? String ?: ""
        gameCode = intent.getStringExtra(Extras.EXTRA_GAME_CODE) as? String ?: ""
        super.onCreate(savedInstanceState)
        mGameAction = GameAction(this, null)
        setContentView(R.layout.activity_edit_match_state)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        //文字字数指示器
        tv_text_num.setTextColor(resources.getColor(R.color.shop_text_no_select_color))
        tv_text_num.gravity = Gravity.RIGHT
        tv_text_num.setPadding(0, ScreenUtil.dp2px(this, 10f), ScreenUtil.dp2px(this, 10f), ScreenUtil.dp2px(this, 5f))
        //比赛说明
        tv_match_state.setPadding(ScreenUtil.dp2px(this, 10f), 0, ScreenUtil.dp2px(this, 10f), ScreenUtil.dp2px(this, 5f))
        tv_match_state.setBackgroundDrawable(null)
        tv_match_state.filters = arrayOf(InputFilter.LengthFilter(MATCH_STATE_MAX_LENGTH))
        tv_match_state.gravity = Gravity.LEFT
        tv_match_state.setText(mPreviousState)
        tv_match_state.setHighlightColor(resources.getColor(R.color.login_solid_color))
        tv_text_num.text = "${tv_match_state.text.length}/$MATCH_STATE_MAX_LENGTH"
        initHead()
        initAdapter()
    }

    fun initHead() {
        setHeadTitle("比赛说明")
        val tv_head_right = findViewById(R.id.tv_head_right) as TextView
        tv_match_state.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                val isEnabled = !StringUtil.isSpace(tv_match_state.text.toString())
//                tv_head_right.isEnabled = isEnabled
//                tv_head_right.setTextColor(if (isEnabled) rightBtnEnabledColor else rightBtnDisenabledColor)
                tv_text_num.text = "${tv_match_state.text.length}/$MATCH_STATE_MAX_LENGTH"
            }

        })
        setHeadRightButton(R.string.finish, this)
    }

    fun initAdapter() {
        val itemWidth = (ScreenUtil.screenMin - resources.getDimension(R.dimen.mtt_pic_list_padding_left) * 2 - resources.getDimension(R.dimen.mtt_pic_list_item_margin) * 6) / 3
        val itemHeight = itemWidth
        mPreviousPicList.split(",").forEach {
            val photoInfo = PhotoInfo(-111)
            photoInfo.webUrl = it
            mPicDatas.add(photoInfo)
        }
        originalPicSize = mPicDatas.size
        mAddDatas.add("add")
        mAdapter = PicListAdap(mPicDatas, mAddDatas, this, recycler_view_pic_list)
        mAdapter.mItemWidth = itemWidth.toInt()
        mAdapter.mItemHeight = itemHeight.toInt()
        recycler_view_pic_list.adapter = mAdapter
        recycler_view_pic_list.setHasFixedSize(true)
        val callback = object : Callback() {
            override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
                val dragFlag = ItemTouchHelper.LEFT or ItemTouchHelper.DOWN or ItemTouchHelper.UP or ItemTouchHelper.RIGHT
                val swipeFlag = 0//ItemTouchHelper.START or ItemTouchHelper.END
                return makeMovementFlags(dragFlag, swipeFlag)
            }
            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
                val fromPosition = viewHolder?.getAdapterPosition()
                val toPosition = target?.getAdapterPosition()
                mAdapter.onMove(fromPosition, toPosition)
//                if (fromPosition != null && toPosition != null && fromPosition < mPicDatas.size && toPosition < mPicDatas.size) {
//                    Collections.swap(mPicDatas, fromPosition, toPosition)
//                }
                mPicDatas.clear()
                mPicDatas.addAll(mAdapter.mPicDatas)
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
            }
            override fun isLongPressDragEnabled(): Boolean {
                return true
            }
        }
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(recycler_view_pic_list)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_head_right -> {
                val txtContentChanged = tv_match_state.text.toString() != mPreviousState//文本公告是否改变
                var picListContentChanged = false//picListDeleted && picListAdded//图片公告是否改变
                run tag@ {
                    mPicDatas.forEach traverseTag@ {
                        if (StringUtil.isSpace(it.webUrl)) {//表示有本地图片文件，那么需要上传到云信nos
                            picListContentChanged = true
                            return@tag
                        }
                    }
                }
                picListContentChanged = picListContentChanged || originalPicSize != mPicDatas.size
                if (!txtContentChanged && !picListContentChanged) {
                    Toast.makeText(this@EditMatchStateAC, "修改比赛说明成功", Toast.LENGTH_SHORT).show()
                    var intent = Intent()
                    intent.putExtra(KEY_PREVIOUS_STATE, tv_match_state?.text.toString())
                    intent.putExtra(KEY_PREVIOUS_PIC_LIST, mPreviousPicList)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                    return
                }
                var successNum = 0
                var picListStr = StringBuilder()
                DialogMaker.showProgressDialog(this, "", false)
                mPicDatas.indices
                        .map { File(mPicDatas[it].absolutePath) }
                        .map { NIMClient.getService(NosService::class.java).upload(it, PickImageAction.JPG) }
                        .forEach {
                            it.setCallback(object : RequestCallbackWrapper<String>() {
                                override fun onResult(code: Int, url: String, exception: Throwable) {
                                    if (code == ResponseCode.RES_SUCCESS.toInt() && !TextUtils.isEmpty(url)) {
                                        successNum++
                                        LogUtil.i("EditMatchStateAC", "successNum: $successNum   upload avatar success, url = $url")
                                        picListStr.append("$url,")
                                        if (successNum == mPicDatas.size) {//表示全部上传成功
                                            postRemark(picListStr.substring(0, picListStr.length - 1))
                                        }
                                    } else {
                                    }
                                }
                            })
                        }
                if (mPicDatas.size <= 0) {
                    postRemark("")
                }
            }
        }
    }

    fun postRemark(picListStr: String) {
        mPreviousPicList = picListStr
        mGameAction?.mttRemark(true, gameCode, tv_match_state.text.toString(), picListStr, object : GameRequestCallback {
            override fun onSuccess(response: JSONObject?) {
                Toast.makeText(this@EditMatchStateAC, "修改比赛说明成功", Toast.LENGTH_SHORT).show()
                var intent = Intent()
                intent.putExtra(KEY_PREVIOUS_STATE, tv_match_state?.text.toString())
                intent.putExtra(KEY_PREVIOUS_PIC_LIST, mPreviousPicList)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            override fun onFailed(code: Int, response: JSONObject?) {
                var failMsg = ApiCode.SwitchCode(code, response?.toString())
                Toast.makeText(this@EditMatchStateAC, "$failMsg. code: $code", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == PickImageAC.PICK_IMAGE_REQUEST_CODE) {
            val selectedPics = data?.getSerializableExtra(PickImageAC.KEY_RESULT_OK_DATA)
            if (selectedPics is ArrayList<*> && selectedPics.size > 0) {
                selectedPics.forEach {
                    if (it is PhotoInfo) {
                        mPicDatas.add(it)
                        mAdapter.mPicDatas.add(it)
                    }
                }
                if (mAdapter.mPicDatas.size < PicListAdap.MAX_COUNT) {
                    mAdapter.notifyItemRangeInserted(mAdapter.mPicDatas.size - selectedPics.size, selectedPics.size)
                } else {
                    mAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onDestroy() {
        mGameAction?.onDestroy()
        mGameAction = null
        super.onDestroy()
    }
}