package com.htgames.nutspoker.game.match.adapter
import android.app.Activity
import android.graphics.BitmapFactory
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
import com.netease.nim.uikit.common.media.picker.model.AlbumInfo
import com.netease.nim.uikit.common.media.picker.model.PhotoInfo
import com.netease.nim.uikit.interfaces.IClickPayload
import java.io.File

/**
 * Created by 周智慧 on 2017/10/22.
 */
class AlbumAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    override fun getItemCount(): Int {
        return mDatas.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return AlbumItemVH(LayoutInflater.from(mActivity).inflate(R.layout.item_album, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is AlbumItemVH) {
            holder.bind(mDatas.get(position), this)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int, payloads: MutableList<Any>?) {
        Log.i("mAlbumInfolist", "$holder $position  ${payloads?.toString()}")
        if (holder !is AlbumItemVH) {
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
    var mDatas = ArrayList<AlbumInfo>()
    constructor(datas: List<AlbumInfo>?, listener: Any?, allDatas: List<PhotoInfo>?) {
        if (datas != null) {
            mDatas.addAll(datas)
        }
        if (listener is Activity) {
            mActivity = listener
        }
        if (listener is IClickPayload) {
            mClick = listener
        }
        if (allDatas != null && mDatas.size > 1) {
            val albumInfo = mDatas[0].copyContent()
            albumInfo.albumName = "所有图片"
            albumInfo.selected = 1
            albumInfo.list?.addAll(allDatas)
            mDatas.add(0, albumInfo)
        }
    }


    class AlbumItemVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var album_img = itemView.findViewById(R.id.album_img) as ImageView
        var album_name = itemView.findViewById(R.id.album_name) as TextView
        var album_num = itemView.findViewById(R.id.album_num) as TextView
        var iv_selected = itemView.findViewById(R.id.iv_selected) as ImageView
        fun bind(albumInfo: AlbumInfo, adapter: AlbumAdapter) {
            album_name.text = "${albumInfo.albumName}"
            album_num.text = "${albumInfo.list?.size}"
            changeSelectNum(albumInfo.selected)
            //显示图片
            if (albumInfo.list != null && albumInfo.list!!.size > 0) {
//                val imgFile = File(albumInfo.list!!.get(0).filePath.substring("file://".length))
//                if (imgFile.exists()) {
//                    val options = BitmapFactory.Options()
//                    options.inSampleSize = 8
//                    var mBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options)
//                }
                PickerlImageLoadTool.disPlay(albumInfo.list!!.get(0).filePath, RotateImageViewAware(album_img, albumInfo.list!!.get(0).absolutePath), R.drawable.nim_image_default)
            }
            itemView.setOnClickListener {
                adapter.mClick?.onClick(adapterPosition, albumInfo)
                if (albumInfo.selected == 0) {
                    for (i in adapter.mDatas.indices) {
                        if (adapter.mDatas.get(i).selected == 1) {
                            adapter.mDatas.get(i).selected = 0
                            adapter.notifyItemChanged(i, adapter.mDatas.get(i).selected)
                            break
                        }
                    }
                    albumInfo.selected = 1
                    changeSelectNum(albumInfo.selected)
                } else {
                }
            }
        }

        fun changeSelectNum(num: Int) {
            iv_selected.visibility = if (num == 1) View.VISIBLE else View.GONE
        }
    }
}