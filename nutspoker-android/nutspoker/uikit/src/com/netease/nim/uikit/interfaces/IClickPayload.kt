package com.netease.nim.uikit.interfaces

interface IClickPayload {
    fun onDelete(position: Int, payload: Any?)
    fun onClick(position: Int, payload: Any?)
    fun onLongClick(position: Int, payload: Any?)
    interface IOnlyClick {
        fun onClick(position: Int, payload: Any?)
    }
}