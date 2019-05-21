package com.netease.nim.uikit.interfaces;


import com.netease.nim.uikit.interfaces.OnPasswordChangedListener;
import com.netease.nim.uikit.interfaces.PasswordType;

/**
 * @author Jungly
 * @mail jungly.ik@gmail.com
 * @date 15/3/21 16:20
 */
public interface PasswordView {

    //void setError(String error);

    String getPassWord();

    void clearPassword();

    void setPassword(String password);

    void setPasswordVisibility(boolean visible);

    void togglePasswordVisibility();

    void setOnPasswordChangedListener(OnPasswordChangedListener listener);

    void setPasswordType(PasswordType passwordType);
}
