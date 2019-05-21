package com.netease.nim.uikit.interfaces

abstract class IControlComming {
    open fun showStatusbar(show: Boolean) {}
    open fun click() {}
    open fun handle() {}
}