package com.htgames.nutspoker.game.match.adapter

import android.app.Activity
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.htgames.nutspoker.R
import com.htgames.nutspoker.game.match.activity.PickImageAC
import com.htgames.nutspoker.game.match.activity.PickImageAC.Companion.FILE_PREFIX
import com.netease.nim.uikit.ImageLoaderKit
import com.netease.nim.uikit.NativeUtil
import com.netease.nim.uikit.common.media.picker.loader.PickerlImageLoadTool
import com.netease.nim.uikit.common.media.picker.loader.RotateImageViewAware
import com.netease.nim.uikit.common.media.picker.model.PhotoInfo
import com.netease.nim.uikit.common.ui.imageview.HeadImageView
import com.netease.nim.uikit.common.util.log.LogUtil
import com.netease.nim.uikit.common.util.string.StringUtil
import com.netease.nim.uikit.interfaces.IClickPayload
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.ImageSize
import com.nostra13.universalimageloader.core.assist.ViewScaleType
import com.nostra13.universalimageloader.core.imageaware.NonViewAware
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import java.io.File
import java.util.*

class PicListAdap : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    var recycler_view_pic_list: RecyclerView
    var mPicDatas = ArrayList<PhotoInfo>()
    var mAddDatas = ArrayList<Any>()
    var mActivity: Activity? = null
    var mClick: IClickPayload? = null
    var mItemWidth = 0
    var mItemHeight = 0
    companion object {
        val MAX_COUNT = 6//最多6个
        val VIEW_TYPE_ITEM = 0
        val VIEW_TYPE_ADD = 1
    }

    constructor(picDatas: List<PhotoInfo>?, addDatas: List<Any>?, listener: Any?, recyclerView: RecyclerView) {
        if (picDatas != null) {
            mPicDatas.clear()
            mPicDatas.addAll(picDatas)
        }
        if (addDatas != null) {
            mAddDatas.clear()
            mAddDatas.addAll(addDatas)
        }
        if (listener is Activity) {
            mActivity = listener
        }
        if (listener is IClickPayload) {
            mClick = listener
        }
        recycler_view_pic_list = recyclerView
    }

    fun updateData(picDatas: List<PhotoInfo>?, addDatas: List<Any>?) {
        mPicDatas.clear()
        if (picDatas != null) {
            mPicDatas.addAll(picDatas)
        }
        mAddDatas.clear()
        if (addDatas != null) {
            mAddDatas.addAll(addDatas)
        }
    }

    override fun getItemViewType(position: Int): Int {
        var result = VIEW_TYPE_ITEM
        LogUtil.i("test_recyclerview_ControlCenterAdap", "getItemViewType: $position $result")
        if ((position < itemCount - mAddDatas.size && MAX_COUNT > mPicDatas.size) || MAX_COUNT <= mPicDatas.size) {
            result = VIEW_TYPE_ITEM
        } else {
            result = VIEW_TYPE_ADD
        }
        return result
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        LogUtil.i("test_recyclerview_ControlCenterAdap", "onCreateViewHolder: viewType： $viewType")
        if (viewType == VIEW_TYPE_ITEM) {
            val root = LayoutInflater.from(mActivity).inflate(R.layout.item_pic_list, parent, false)
            root.layoutParams.height = mItemHeight
            return VHItemPic(root)
        } else if (viewType == VIEW_TYPE_ADD) {
            var root = LayoutInflater.from(mActivity).inflate(R.layout.item_pic_list_add, parent, false)
            root.layoutParams.height = mItemHeight
            return VHItemAdd(root)
        }
        return VHItemPic(LayoutInflater.from(mActivity).inflate(R.layout.item_pic_list, parent, false))
    }

    override fun getItemCount(): Int {
        LogUtil.i("test_recyclerview_ControlCenterAdap", "getItemCount")
        return if (mPicDatas.size + mAddDatas.size < MAX_COUNT) (mPicDatas.size + mAddDatas.size) else MAX_COUNT
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        LogUtil.i("test_recyclerview_ControlCenterAdap", "onBindViewHolder: $position")
        if (holder is VHItemPic) {
            if (position >= 0 && position < mPicDatas.size) {
                holder.bind(position, mPicDatas.get(position), this)
            }
        } else if (holder is VHItemAdd) {
            if (position - mPicDatas.size >= 0 && position - mPicDatas.size < mAddDatas.size) {
                holder.bind(position, mAddDatas.get(position - mPicDatas.size).toString(), this)
            }
        }
    }

    fun onMove(fromPosition: Int?, toPosition: Int?) {
        if (fromPosition == null || toPosition == null || fromPosition >= mPicDatas.size || toPosition >= mPicDatas.size) {
            return
        }
        LogUtil.i("test_recyclerview_ControlCenterAdap", "onMove: $fromPosition   $toPosition")
//        Collections.swap(mPicDatas, fromPosition, toPosition)
        val backupData = ArrayList<PhotoInfo>()
        mPicDatas.forEach { backupData.add(it.copyContent()) }
        notifyItemMoved(fromPosition, toPosition)
        for (i in Math.min(fromPosition, toPosition)..Math.max(fromPosition, toPosition)) {
            val viewHolder = recycler_view_pic_list.findViewHolderForAdapterPosition(i)
            if (viewHolder is VHItemPic) {
                mPicDatas.set(i, backupData[viewHolder.layoutPosition])
            }
            LogUtil.i("test_recyclerview_ControlCenterAdap", "onMove: newPos: $i oldPos:${viewHolder.oldPosition}  pos: ${viewHolder.position}  adapterPosition: ${viewHolder.adapterPosition}  layoutPosition: ${viewHolder.layoutPosition}  tag: ${viewHolder.itemView.tag}")
            /*
            * if you've called notifyItemInserted(0), the getAdapterPosition() of ViewHolder which was previously at position 0 will start returning 1 immediately.
            * So as long as you are dispatching granular notify events, you are always in good state (we know adapter position even though new layout is not calculated yet).
            * */
            /*
            * onMove: 3   0
              onMove: newPos: 0 oldPos:-1  pos: 3  adapterPosition: 0  layoutPosition: 3
              onMove: newPos: 1 oldPos:-1  pos: 0  adapterPosition: 1  layoutPosition: 0
              onMove: newPos: 2 oldPos:-1  pos: 1  adapterPosition: 2  layoutPosition: 1
              onMove: newPos: 3 oldPos:-1  pos: 2  adapterPosition: 3  layoutPosition: 2
            * */
        }
//        val itemView = recycler_view_pic_list.getLayoutManager().findViewByPosition(fromPosition)这个根据位置获取view的方法也是不准的，view是变化之前的
//        LogUtil.i("recycler_view_pic_list", "fromPosition: $fromPosition  tag: ${itemView.tag}   text: ${(itemView.findViewById(R.id.tv_delete) as TextView).text}")
//
//        val itemViewTo = recycler_view_pic_list.getLayoutManager().findViewByPosition(toPosition)
//        LogUtil.i("recycler_view_pic_list", "toPosition: $toPosition  tag: ${itemViewTo.tag}   text: ${(itemViewTo.findViewById(R.id.tv_delete) as TextView).text}")
    }









    class VHItemPic(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var item_pic_list_img = itemView.findViewById(R.id.item_pic_list_img) as ImageView
        var item_pic_list_close = itemView.findViewById(R.id.item_pic_list_close)
        fun bind(positionP: Int, photoInfo: PhotoInfo, adap: PicListAdap) {
            itemView.tag = adapterPosition
            val needLoad = ImageLoaderKit.isImageUriValid(photoInfo.webUrl)
            if (needLoad && StringUtil.isSpace(photoInfo.filePath)) {
                ImageLoader.getInstance().displayImage(photoInfo.webUrl, NonViewAware(ImageSize(adap.mItemWidth.toInt(), adap.mItemHeight.toInt()), ViewScaleType.fromImageView(item_pic_list_img)), HeadImageView.createImageOptions(),
                        object : SimpleImageLoadingListener() {
                            override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                                if (itemView.getTag() != null && itemView.getTag() == adapterPosition && loadedImage != null) {
                                    item_pic_list_img.setImageBitmap(loadedImage)
                                    val startPos = if ((imageUri?.length ?: 0) >= 25) ((imageUri?.length ?: 25) - 25) else 0
                                    val fileName = imageUri?.substring(startPos)
                                    val file = File(itemView.context.externalCacheDir, "$fileName.jpg")
                                    NativeUtil.compressImageToFile(loadedImage, file, 100, true)
                                    photoInfo.setFilePathP("$FILE_PREFIX${file.path}")
                                    photoInfo.absolutePath = file.path
                                }
                            }
                        })
            } else {
                PickerlImageLoadTool.disPlay(photoInfo.filePath, RotateImageViewAware(item_pic_list_img, photoInfo.absolutePath), R.drawable.nim_image_default)
            }
            itemView.setOnClickListener { adap.mClick?.onClick(adapterPosition, photoInfo) }
            item_pic_list_close.setOnClickListener { adap.mClick?.onDelete(adapterPosition, photoInfo) }
        }
    }





    class VHItemAdd(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var item_pic_list_add = itemView.findViewById(R.id.item_pic_list_add) as ImageView
        fun bind(positionP: Int, url: String, adap: PicListAdap) {
            itemView.setOnClickListener { PickImageAC.Companion.startForResult(itemView.context as? Activity, adap.mPicDatas.size) }
        }
    }
}