package com.netease.nim.uikit.common.media.picker.model

import java.io.Serializable

data class AlbumInfo(var imageId: Int) : Serializable {
    var absolutePath: String? = null
    var filePath: String? = null
    var albumName: String? = null
    var list: ArrayList<PhotoInfo>? = null

    companion object {
        const val serialVersionUID = 1L
    }

    //只拷贝内容，不拷贝引用
    fun copyContent(): AlbumInfo {
        val result = AlbumInfo(imageId)
        result.filePath = filePath
        result.absolutePath = absolutePath
        result.albumName = albumName
        result.list = ArrayList<PhotoInfo>()//不要给list赋值
        return result
    }


    //下面的字段不是从数据库读取的，自定义的
    var selected = 0//表示该相册是否被选中，0否1是

}
