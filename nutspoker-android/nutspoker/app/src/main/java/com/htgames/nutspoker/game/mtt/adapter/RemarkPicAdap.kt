package com.htgames.nutspoker.game.mtt.adapter

import android.app.Activity
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.htgames.nutspoker.R
import com.netease.nim.uikit.ImageLoaderKit
import com.netease.nim.uikit.common.ui.imageview.HeadImageView
import com.netease.nim.uikit.common.util.string.StringUtil
import com.netease.nim.uikit.common.util.sys.ScreenUtil
import com.netease.nim.uikit.interfaces.IClickPayload
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.ImageSize
import com.nostra13.universalimageloader.core.assist.ViewScaleType
import com.nostra13.universalimageloader.core.imageaware.NonViewAware
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener

class RemarkPicAdap : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    companion object {
        val VIEW_TYPE_TEXT = 0
        val VIEW_TYPE_PIC_LIST = 1
    }
    var mRemarkStr: String? = ""
    var mDatas = ArrayList<String>()
    var mActivity: Activity? = null
    var mClick: IClickPayload? = null
    var mItemWidth = 0f
    var mItemHeight = 0f
    constructor(datas: ArrayList<String>?, listeners: Any?) {
        if (datas != null) {
            mDatas.clear()
            mDatas.addAll(datas)
        }
        if (listeners is Activity) {
            mActivity = listeners
        }
        if (listeners is IClickPayload) {
            mClick = listeners
        }
        mItemWidth = ScreenUtil.screenMin / 2f
        mItemHeight = mItemWidth
    }

    fun updateData(datas: List<String>?) {
        mDatas.clear()
        if (datas != null) {
            mDatas.addAll(datas)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_TEXT) {
            var textView = TextView(mActivity)
            return RemarkTextVH(textView)
        } else if (viewType == VIEW_TYPE_PIC_LIST) {
            return RemarkPicVH(LayoutInflater.from(mActivity).inflate(R.layout.item_mtt_remark_pic, parent, false), this)
        }
        return RemarkPicVH(LayoutInflater.from(mActivity).inflate(R.layout.item_mtt_remark_pic, parent, false), this)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder == null || position < 0 || position >= itemCount) {
            return
        }
        if (holder is RemarkTextVH) {
            holder.bind(position, mRemarkStr ?: "")
        }
        if (holder is RemarkPicVH) {
            holder.bind(position, mDatas.get(position - 1), this)
        }
    }

    override fun getItemCount(): Int {
        return mDatas.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        if (position <= 0) {
            return VIEW_TYPE_TEXT
        } else {
            return VIEW_TYPE_PIC_LIST
        }
    }














    class RemarkTextVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int, contentStr: String) {
            if (itemView is TextView) {
                itemView.text = contentStr
            }
            itemView.visibility = if (StringUtil.isSpace(contentStr)) View.GONE else View.VISIBLE
        }
    }

    class RemarkPicVH(itemView: View, adap: RemarkPicAdap) : RecyclerView.ViewHolder(itemView) {
        /*CENTER /center  按图片的原来size居中显示，当图片长/宽超过View的长/宽，则截取图片的居中部分显示
            CENTER_CROP / centerCrop  按比例扩大图片的size居中显示，使得图片长(宽)等于或大于View的长(宽)
            CENTER_INSIDE / centerInside  将图片的内容完整居中显示，通过按比例缩小或原来的size使得图片长/宽等于或小于View的长/宽
            FIT_CENTER / fitCenter  把图片按比例扩大/缩小到View的宽度，居中显示
            FIT_END / fitEnd   把图片按比例扩大/缩小到View的宽度，显示在View的下部分位置
            FIT_START / fitStart  把图片按比例扩大/缩小到View的宽度，显示在View的上部分位置
            FIT_XY / fitXY  把图片不按比例扩大/缩小到View的大小显示
            MATRIX / matrix 用矩阵来绘制，动态缩小放大图片来显示。*/
        var click_area = itemView.findViewById(R.id.click_area)
        var remark_img = itemView.findViewById(R.id.remark_img) as ImageView
        init {
            itemView.layoutParams.height = adap.mItemHeight.toInt()
            click_area.layoutParams.width = adap.mItemWidth.toInt()
        }
        fun bind(position: Int, url: String, adap: RemarkPicAdap) {
            val needLoad = ImageLoaderKit.isImageUriValid(url)
            if (needLoad) {
                itemView.tag = position
                ImageLoader.getInstance().displayImage(url, NonViewAware(ImageSize(adap.mItemWidth.toInt(), adap.mItemHeight.toInt()), ViewScaleType.fromImageView(remark_img)), HeadImageView.createImageOptions(),
                        object : SimpleImageLoadingListener() {
                    override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                        if (itemView.getTag() != null && itemView.getTag() == position) {
//                            click_area.setBackgroundDrawable(BitmapDrawable(loadedImage))
                            remark_img.setImageBitmap(loadedImage)
                        }
                    }
                })
            } else {
                itemView.tag = null
            }
            click_area.setOnClickListener { adap.mClick?.onClick(position, url) }
        }
    }
}