/*
 * Copyright 2014 http://Bither.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netease.nim.uikit

import android.graphics.*
import android.graphics.Bitmap.Config
import android.util.Log

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

//NDK_PROJECT_PATH=.NDK_APPLICATION_MK=$SourcepathEntry$/jni/Application.mk APP_BUILD_SCRIPT=$SourcepathEntry$/jni/Android.mk
//http://www.qingpingshan.com/rjbc/az/329678.html
object NativeUtil {
    private val DEFAULT_QUALITY = 95

    /**
     * @param bitmap      bitmap对象
     * *
     * @param fileName 指定保存目录名
     * *
     * @param optimize 是否采用哈弗曼表数据计算 品质相差5-10倍
     * *
     * @Description: JNI基本压缩
     */
    fun compressBitmap(bitmap: Bitmap, fileName: String, optimize: Boolean) {
        saveBitmap(bitmap, DEFAULT_QUALITY, fileName, optimize)
    }

    /**
     * @param bitmap    bitmap对象
     * *
     * @param filePath 要保存的指定目录
     * *
     * @Description: 通过JNI图片压缩把Bitmap保存到指定目录
     */
    fun compressBitmap(bitmap: Bitmap, filePath: String) {
        val baos = ByteArrayOutputStream()
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        val options = 20
        // JNI调用保存图片到SD卡 这个关键
        NativeUtil.saveBitmap(bitmap, options, filePath, true)
    }

    /**
     * 计算缩放比

     * @param bitWidth  当前图片宽度
     * *
     * @param bitHeight 当前图片高度
     * *
     * @return
     * *
     * @Description:函数描述
     */
    fun getRatioSize(bitWidth: Int, bitHeight: Int): Int {
        // 图片最大分辨率
        val imageHeight = 1920
        val imageWidth = 1080
        // 缩放比
        var ratio = 1
        // 缩放比,由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        if (bitWidth > bitHeight && bitWidth > imageWidth) {
            // 如果图片宽度比高度大,以宽度为基准
            ratio = bitWidth / imageHeight
        } else if (bitWidth < bitHeight && bitHeight > imageHeight) {
            // 如果图片高度比宽度大，以高度为基准
            ratio = bitHeight / imageHeight
        }
        // 最小比率为1
        if (ratio <= 0)
            ratio = 1
        return ratio
    }

    /**
     * 调用native方法

     * @param bit
     * *
     * @param quality
     * *
     * @param fileName
     * *
     * @param optimize
     * *
     * @Description:函数描述
     */
    fun saveBitmap(bit: Bitmap, quality: Int, fileName: String, optimize: Boolean) {
        compressBitmap(bit, bit.width, bit.height, quality, fileName.toByteArray(), optimize)
    }

    /**
     * 调用底层 bitherlibjni.c中的方法

     * @param bit
     * *
     * @param w
     * *
     * @param h
     * *
     * @param quality
     * *
     * @param fileNameBytes
     * *
     * @param optimize
     * *
     * @return
     * *
     * @Description:函数描述
     */
    external fun compressBitmap(bit: Bitmap, w: Int, h: Int, quality: Int, fileNameBytes: ByteArray,
                                optimize: Boolean): String

    /**
     * 加载lib下两个so文件
     */
//    init {
//        System.loadLibrary("jpegbither")
//        System.loadLibrary("bitherjni")
//    }


    /**
     * 1. 质量压缩
     * 设置bitmap options属性，降低图片的质量，像素不会减少
     * 第一个参数为需要压缩的bitmap图片对象，第二个参数为压缩后图片保存的位置
     * 设置options 属性0-100，来实现压缩

     * @param bmp
     * *
     * @param file
     */
    fun compressImageToFile(bmp: Bitmap, file: File, options: Int, shouldSave: Boolean) {
        if (!shouldSave) {
            return
        }
        // 0-100 100为不压缩
        val baos = ByteArrayOutputStream()
        // 把压缩后的数据存放到baos中
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos)
        try {
            if (file.exists()) {
                file.delete()
            } else {
                file.createNewFile()
            }
            val fos = FileOutputStream(file)
            fos.write(baos.toByteArray())
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 2. 尺寸压缩
     * 通过缩放图片像素来减少图片占用内存大小

     * @param bmp
     * *
     * @param file
     */

    fun compressBitmapToFile(bmp: Bitmap, file: File): Bitmap {
        // 尺寸压缩倍数,值越大，图片尺寸越小
        val ratio = 8
        // 压缩Bitmap到对应尺寸
        val result = Bitmap.createBitmap(bmp.width / ratio, bmp.height / ratio, Config.ARGB_8888)
        val canvas = Canvas(result)
        val rect = Rect(0, 0, bmp.width / ratio, bmp.height / ratio)
        canvas.drawBitmap(bmp, null, rect, null)
        compressImageToFile(result, file, 100, false)
        return result
    }

    fun getResizedBitmap(bitmap: Bitmap?, newWidth: Int, newHeight: Int, file: File): Bitmap? {
        if (bitmap == null) {
            return null
        }
        val width = bitmap.width
        val height = bitmap.height
        Log.i("mAlbumInfolist", "  ${bitmap.byteCount / 1024}K  $width $height  " + "density:${bitmap.density}")
        val scaleWidth = 1f//.125f//0.01f//newWidth.toFloat() / width
        val scaleHeight = 1f//.125f//0.01f//newHeight.toFloat() / height
        // create a matrix for the manipulation
        val matrix = Matrix()
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight)
        // recreate the new Bitmap
        val resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)
        compressImageToFile(bitmap, file, 100, false)
        return resultBitmap
    }


    /**
     * 设置图片的采样率，降低图片像素

     * @param filePath
     * *
     * @param file
     */
    fun compressBitmap(filePath: String, file: File) {
        // 数值越高，图片像素越低
        val inSampleSize = 8
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = false
        //	        options.inJustDecodeBounds = true;//为true的时候不会真正加载图片，而是得到图片的宽高信息。
        //采样率
        options.inSampleSize = inSampleSize
        val bitmap = BitmapFactory.decodeFile(filePath, options)
        compressImageToFile(bitmap, file, 100, false)
    }
}
