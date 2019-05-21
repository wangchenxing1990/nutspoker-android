package com.netease.nim.uikit.interfaces

interface IOperationClick {
    fun onAgree(position: Int, payload: Any?)
    fun onReject(position: Int, payload: Any?)
    fun onOtherOperation(position: Int, payload: Any?)
}