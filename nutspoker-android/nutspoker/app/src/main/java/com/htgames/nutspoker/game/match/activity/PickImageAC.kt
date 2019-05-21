package com.htgames.nutspoker.game.match.activity
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.view.ViewStub
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.htgames.nutspoker.R
import com.htgames.nutspoker.game.match.adapter.AlbumAdapter
import com.htgames.nutspoker.game.match.adapter.PickImageAdap
import com.htgames.nutspoker.ui.base.BaseActivity
import com.netease.nim.uikit.common.media.dao.MediaDAO
import com.netease.nim.uikit.common.media.picker.model.AlbumInfo
import com.netease.nim.uikit.common.media.picker.model.PhotoInfo
import com.netease.nim.uikit.interfaces.IClickPayload
import kotlinx.android.synthetic.main.activity_pick_image_ac.*
import kotlinx.android.synthetic.main.dialog_album_list.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers
import java.io.File
import java.util.*

class PickImageAC : BaseActivity(), IClickPayload {
    override fun onClick(position: Int, payload: Any?) {
        if (payload is PhotoInfo) {
        } else if (payload is AlbumInfo) {
            if (payload.selected == 0) {
                mAdapter.updateData(payload.list)
                mAdapter.notifyDataSetChanged()
            }
            showAlbumList()
        }
    }
    override fun onLongClick(position: Int, payload: Any?) {
    }
    override fun onDelete(position: Int, payload: Any?) {
        if (!(payload is PhotoInfo)) {
            return
        }
        if (payload.chooseIndex <= 0) {
            if (mAdapter.mSelectedDatas.size >= MAX_SELECTED_NUM) {
                Toast.makeText(this, "最多选中${MAX_SELECTED_NUM}张", Toast.LENGTH_SHORT).show()
                return
            }
            payload.chooseIndex = mAdapter.mSelectedDatas.size + 1
            mAdapter.mSelectedDatas.add(payload)
        } else {
            for (i in 0 until mAdapter.mDatas.size) {
                if (mAdapter.mDatas[i].chooseIndex > payload.chooseIndex) {
                    mAdapter.mDatas[i].chooseIndex--
                    mAdapter.notifyItemChanged(i, mAdapter.mDatas[i].chooseIndex)
                }
            }
            payload.chooseIndex = 0
            mAdapter.mSelectedDatas.remove(payload)
        }
        mAdapter.notifyItemChanged(position, payload.chooseIndex)
        tv_bottom_preview.isEnabled = mAdapter.mSelectedDatas.size > 0
        if (mAdapter.mSelectedDatas.size > 0) {
            tv_bottom_preview.isEnabled = true
            tv_bottom_preview.text = "完成(${mAdapter.mSelectedDatas.size})"
        } else {
            tv_bottom_preview.isEnabled = false
            tv_bottom_preview.text = "完成"
        }
    }

    companion object {
        val FILE_PREFIX = "file://"
        val PICK_IMAGE_REQUEST_CODE = 68
        val KEY_RESULT_OK_DATA = "key_result_ok_data"
        val KEY_ALREADY_PIC_NUM = "key_already_pic_num"
        fun startForResult(activity: Activity?, alreadyNum: Int) {
            val intent = Intent(activity, PickImageAC::class.java)
            intent.putExtra(KEY_ALREADY_PIC_NUM, alreadyNum)
            activity?.startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
        }
    }
    lateinit var mAdapter: PickImageAdap
    var mAlbumAdapter: AlbumAdapter? = null
    var mAlbumInfolist: MutableList<AlbumInfo> = ArrayList()
    var mAllPics: MutableList<PhotoInfo> = ArrayList()
    var MAX_SELECTED_NUM = 6
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_image_ac)
        MAX_SELECTED_NUM -= intent.getIntExtra(KEY_ALREADY_PIC_NUM, 0)
        Observable.just(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(object : Func1<Context, MutableList<AlbumInfo>> {
                    override fun call(context: Context): MutableList<AlbumInfo> {
                        var cursorPhotos: Cursor? = null
                        cursorPhotos = MediaDAO.getAllMediaPhotos(context)
                        val hash = HashMap<String, AlbumInfo>()
                        if (cursorPhotos != null && cursorPhotos.moveToFirst()) {
                            do {
                                var index = 0
                                val _id = cursorPhotos.getInt(cursorPhotos.getColumnIndex(MediaStore.Images.Media._ID))
                                val path = cursorPhotos.getString(cursorPhotos.getColumnIndex(MediaStore.Images.Media.DATA))
                                val name = cursorPhotos.getString(cursorPhotos.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                                val size = cursorPhotos.getLong(cursorPhotos.getColumnIndex(MediaStore.Images.Media.SIZE))
                                if (!isValidImageFile(path)) {
                                    continue
                                }
                                val photoList = ArrayList<PhotoInfo>()
                                var albumInfo: AlbumInfo? = null
                                var photoInfo = PhotoInfo(_id)
                                photoInfo.setFilePathP((FILE_PREFIX + path))
                                photoInfo.absolutePath = (path)
                                photoInfo.size = (size)
                                mAllPics.add(photoInfo)
                                if (hash.containsKey(name)) run {
                                    albumInfo = hash.remove(name)
                                    if (mAlbumInfolist.contains(albumInfo))
                                        index = mAlbumInfolist.indexOf(albumInfo)
                                    albumInfo?.list?.add(photoInfo)
                                    mAlbumInfolist.set(index, albumInfo!!)
                                    hash.put(name, albumInfo!!)
                                } else {
                                    albumInfo = AlbumInfo(_id)
                                    photoList.add(photoInfo)
                                    albumInfo?.filePath = (FILE_PREFIX + path)
                                    albumInfo?.absolutePath = (path)
                                    albumInfo?.albumName = (name)
                                    albumInfo?.list = (photoList)
                                    mAlbumInfolist.add(albumInfo!!)
                                    hash.put(name, albumInfo!!)
                                }
                            } while (cursorPhotos.moveToNext())
                            cursorPhotos.close()
                        }
                        return mAlbumInfolist
                    }
                })
                .subscribe {
                    mAdapter = PickImageAdap(mAllPics, this@PickImageAC)
                    recycler_view.adapter = mAdapter
                    tv_bottom_type.setOnClickListener { showAlbumList() }
                    tv_bottom_preview.setOnClickListener {
                        val intent = Intent()
                        intent.putExtra(KEY_RESULT_OK_DATA, mAdapter.mSelectedDatas)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
    }


    var dialogShowing = false
    fun showAlbumList() {
        if (anim_container == null) {
            (view_stub as ViewStub).inflate()
            //下面是初始化AlbumAdapter
            mAlbumAdapter = AlbumAdapter(mAlbumInfolist, this, mAllPics)
            album_recycler.adapter = mAlbumAdapter
            //初始化完adapter后再执行动画
            val shakeInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_bottom)
            anim_container.startAnimation(shakeInAnimation)
            dialogShowing = true
        } else {
            if (!dialogShowing) {
                album_recycler.visibility = View.VISIBLE
                val shakeInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_bottom)
                shakeInAnimation.fillAfter = true
                anim_container.startAnimation(shakeInAnimation)
                dialogShowing = true
            } else {
                val shakeInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_bottom)
                shakeInAnimation.fillAfter = true
                anim_container.startAnimation(shakeInAnimation)
                dialogShowing = false
                shakeInAnimation?.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {
                    }
                    override fun onAnimationStart(animation: Animation?) {
                        album_recycler.visibility = View.VISIBLE
                    }
                    override fun onAnimationEnd(animation: Animation?) {
                        album_recycler.visibility = View.INVISIBLE
                    }
                })
            }
        }
        anim_container.isClickable = dialogShowing
    }

    private fun isValidImageFile(filePath: String): Boolean {
        if (TextUtils.isEmpty(filePath)) {
            return false
        }
        return File(filePath).exists()
    }

    override fun onBackPressed() {
        if (dialogShowing) {
            showAlbumList()
            return
        }
        super.onBackPressed()
    }
}
