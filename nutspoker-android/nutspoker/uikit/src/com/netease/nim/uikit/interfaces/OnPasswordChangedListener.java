package com.netease.nim.uikit.interfaces;

/**
 * Created by 周智慧 on 16/11/17.
 */

public /**
 * Interface definition for a callback to be invoked when the password changed or is at the maximum length.
 */
interface OnPasswordChangedListener {
    /**
     * Invoked when the password changed.
     */
    public void onChanged(String psw);
    /**
     * Invoked when the password is at the maximum length.
     */
    public void onMaxLength(String psw);

}
