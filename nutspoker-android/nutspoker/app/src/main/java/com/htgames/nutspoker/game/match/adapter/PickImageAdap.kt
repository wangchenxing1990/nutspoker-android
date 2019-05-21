package com.htgames.nutspoker.game.match.adapter
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.util.LruCache
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.htgames.nutspoker.R
import com.netease.nim.uikit.common.media.picker.loader.PickerlImageLoadTool
import com.netease.nim.uikit.common.media.picker.loader.RotateImageViewAware
import com.netease.nim.uikit.common.media.picker.model.PhotoInfo
import com.netease.nim.uikit.common.util.sys.ScreenUtil
import com.netease.nim.uikit.interfaces.IClickPayload
import java.io.File


class PickImageAdap : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    override fun getItemCount(): Int {
        return mDatas.size
    }
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return PickImageVH(LayoutInflater.from(mActivity).inflate(R.layout.item_pick_image, parent, false))
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is PickImageVH) {
            holder.bind(mDatas.get(position), this)
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int, payloads: MutableList<Any>?) {
        Log.i("mAlbumInfolist", "$holder $position  ${payloads?.toString()}")
        if (holder !is PickImageVH) {
            return
        }
        if (payloads == null || payloads.size <= 0) {
            onBindViewHolder(holder, position)
        } else {
            val item = if (position < payloads.size) payloads[position] else payloads[0]
            if (item is Int) {
                holder.changeSelectNum(item)
            }
        }
    }

    var mActivity: Activity? = null
    var mClick: IClickPayload? = null
    var mDatas = ArrayList<PhotoInfo>()
    var mSelectedDatas = ArrayList<PhotoInfo>()
    // Get max available VM memory, exceeding this amount will throw an
    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
    // int in its constructor.
    var maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    // Use 1/8th of the available memory for this memory cache.
    var cacheSize = maxMemory / 8
    var memeryHasUsed = 0
    lateinit private var mMemoryCache: LruCache<String, Bitmap>
    constructor(datas: List<PhotoInfo>?, listener: Any?) {
        if (datas != null) {
            mDatas.addAll(datas)
        }
        if (listener is Activity) {
            mActivity = listener
        }
        if (listener is IClickPayload) {
            mClick = listener
        }
        //////内存管理
        maxMemory = 141374//(Runtime.getRuntime().maxMemory() / 1024).toInt()
        cacheSize = maxMemory / 8
        mMemoryCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String?, bitmap: Bitmap?): Int {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap!!.byteCount / 1024
            }
        }
    }

    fun updateData(datas: List<PhotoInfo>?) {
        mDatas.clear()
        if (datas != null) {
            mDatas.addAll(datas)
        }
    }

    fun addBitmapToMemoryCache(key: String, bitmap: Bitmap?) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap)
        }
    }

    fun getBitmapFromMemCache(key: String): Bitmap? {
        return mMemoryCache.get(key)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder?) {
        if (holder is PickImageVH) {
            holder.image.setImageBitmap(null)
            if (!holder.firstBind && !holder.firstRecycled) {
                holder.firstRecycled = true
            }
            Log.i("dddddfweg", "${holder.adapterPosition}   ${holder.mBitmap?.isRecycled}")
            if (holder.mBitmap?.isRecycled == false) {
                holder.mBitmap?.recycle()
                holder.mBitmap = null
            } else {
            }
        }
        super.onViewRecycled(holder)
    }









    class PickImageVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var firstBind = true
        var firstRecycled = false
        var height = 0
        var mBitmap: Bitmap? = null
        var image = itemView.findViewById(R.id.image) as ImageView
        var select_click_area = itemView.findViewById(R.id.select_click_area)
        var tv_select_index = itemView.findViewById(R.id.tv_select_index) as TextView
        init {
            height = (ScreenUtil.screenMin / 3f).toInt()
            itemView.layoutParams.height = height
            Log.i("dddddfweg", "oncreateviewholder ${adapterPosition}  $firstBind")
        }
        fun bind(photoInfo: PhotoInfo, adap: PickImageAdap) {
            Log.i("dddddfweg", "onbindviewholder ${adapterPosition}  $firstBind")
            Log.i("dddddfweg", "--------------------------------------------------------")
            if (firstBind || firstRecycled) {//这个if语句保证只执行一次
                val remainder = adapterPosition % 3
                val margin = ScreenUtil.dp2px(itemView.context, 3f)
                if (remainder == 0) {
                    (itemView.layoutParams as? RecyclerView.LayoutParams)?.leftMargin = 0
                    (itemView.layoutParams as? RecyclerView.LayoutParams)?.rightMargin = (margin * 2 / 3f).toInt()
                    (itemView.layoutParams as? RecyclerView.LayoutParams)?.bottomMargin = margin
                } else if (remainder == 1) {
                    (itemView.layoutParams as? RecyclerView.LayoutParams)?.leftMargin = (margin * 1 / 3f).toInt()
                    (itemView.layoutParams as? RecyclerView.LayoutParams)?.rightMargin = (margin * 1 / 3f).toInt()
                    (itemView.layoutParams as? RecyclerView.LayoutParams)?.bottomMargin = margin
                } else if (remainder == 2) {
                    (itemView.layoutParams as? RecyclerView.LayoutParams)?.leftMargin = (margin * 2 / 3f).toInt()
                    (itemView.layoutParams as? RecyclerView.LayoutParams)?.rightMargin = 0
                    (itemView.layoutParams as? RecyclerView.LayoutParams)?.bottomMargin = margin
                }
            }
            changeSelectNum(photoInfo.chooseIndex)
            itemView.setOnClickListener { adap.mClick?.onClick(adapterPosition, photoInfo) }
            select_click_area.setOnClickListener { adap.mClick?.onDelete(adapterPosition, photoInfo) }
            firstBind = false
            firstRecycled = false
            //
            if (adap.memeryHasUsed >= adap.maxMemory) {
                return
            }
            val imgFile = File(photoInfo.filePath.substring("file://".length))
//                 Log.i("mAlbumInfolist", "${photoInfo.filePath}   ${imgFile.path}   ${imgFile.getAbsolutePath()}")
            //    file:///storage/emulated/0/DCIM/Camera/IMG_20170730_101922.jpg
            //    /storage/emulated/0/DCIM/Camera/IMG_20170730_101922.jpg
            //    /storage/emulated/0/DCIM/Camera/IMG_20170730_101922.jpg
//            if (imgFile.exists()) {
                val options = BitmapFactory.Options()
//                mBitmap = adap.getBitmapFromMemCache(photoInfo.filePath)
//                mBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options)
//                adap.memeryHasUsed += (mBitmap!!.byteCount / 1024)
//                Log.i("mAlbumInfolist", "${adapterPosition}  $firstBind  before compress: ${mBitmap!!.byteCount / 1024}KB  ${imgFile.getAbsolutePath()}  ${adap.memeryHasUsed}KB  ${adap.maxMemory}KB")
//                adap.addBitmapToMemoryCache(photoInfo.filePath, mBitmap)
//                NativeUtil.compressBitmap(mBitmap!!, File(adap.mActivity?.externalCacheDir, photoInfo.name).getAbsolutePath())
//                val compressFile = File("${adap.mActivity?.externalCacheDir}/${photoInfo.name}")
//                mBitmap = MediaStore.Images.Media.getBitmap(adap.mActivity?.contentResolver, Uri.fromFile(compressFile))
//                mBitmap = BitmapFactory.decodeFile(compressFile.getAbsolutePath(), options)
//                Log.i("mAlbumInfolist", "after compress: ${mBitmap!!.byteCount / 1024}KB  ${compressFile.absolutePath}")
//                if (mBitmap != null) {
//                    image.setImageBitmap(mBitmap)
//                } else {
//                    image.setImageResource(R.drawable.nim_image_default)
//                }
//                  image.setImageURI(Uri.fromFile(imgFile))
//            }
            PickerlImageLoadTool.disPlay(photoInfo.filePath, RotateImageViewAware(image, photoInfo.absolutePath), R.drawable.nim_image_default)
        }

        fun changeSelectNum(num: Int) {
            tv_select_index.text = if (num <= 0) "" else "$num"
        }
    }
}