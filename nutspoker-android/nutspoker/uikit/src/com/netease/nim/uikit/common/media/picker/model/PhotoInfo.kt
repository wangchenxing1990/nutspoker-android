package com.netease.nim.uikit.common.media.picker.model

import java.io.Serializable

data class PhotoInfo(var imageId: Int) : Serializable {

    var filePath: String = ""
    var absolutePath: String = ""
    var size: Long = 0
    var chooseIndex = 0//0表示没有选中，1表示选中的第一张
    var name = ""

    fun setFilePathP(pathP: String) {
        filePath = pathP
        name = filePath.split("/").last()
    }

    //只拷贝内容，不拷贝引用
    fun copyContent(): PhotoInfo {
        val result = PhotoInfo(imageId)
        result.filePath = filePath
        result.absolutePath = absolutePath
        result.size = size
        result.chooseIndex = chooseIndex
        result.name = name
        return result
    }

    companion object {
        const val serialVersionUID = 1L
    }

    override fun toString(): String {
        return "imageId=$imageId size=${size / 1024}KB name=$name"
    }


    //虾米年的字段是网络的图片url地址
    var webUrl = ""
}
