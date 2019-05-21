package com.htgames.nutspoker.view.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.htgames.nutspoker.R;

/**
 * 带有图标和删除符号的可编辑输入框，用户可以自定义传入的显示图标
 */
public class ClearableEditTextWithIcon extends EditText implements View.OnTouchListener, TextWatcher ,View.OnFocusChangeListener{
    public final static String TAG = ClearableEditTextWithIcon.class.getSimpleName();
    public final static int TYPE_RIGHT_DEL = 0;
    public final static int TYPE_RIGHT_EYE = 1;
    // 删除符号
    Drawable deleteImage = getResources().getDrawable(R.mipmap.icon_edit_delete);
    Drawable icon;
    public boolean isShowDeleteBtn = true;
    private int rightType = TYPE_RIGHT_DEL;

    public ClearableEditTextWithIcon(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ClearableEditTextWithIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClearableEditTextWithIcon(Context context) {
        super(context);
        init();
    }

    /**
     * 设置是否显示删除按钮
     * @param isShow
     */
    public void setShowDeleteBtn(boolean isShow){
        isShowDeleteBtn = isShow;
    }

    private void init() {
        ClearableEditTextWithIcon.this.setOnTouchListener(this);
        ClearableEditTextWithIcon.this.addTextChangedListener(this);
        setOnFocusChangeListener(this);
        deleteImage.setBounds(0, 0, deleteImage.getIntrinsicWidth(), deleteImage.getIntrinsicHeight());
        manageClearButton();
    }

    /**
     * 传入显示的图标资源id
     *
     * @param id
     */
    public void setIconResource(int id) {
        icon = getResources().getDrawable(id);
        icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        manageClearButton();
    }

    /**
     * 传入删除图标资源id
     * @param id
     */
    public void setDeleteImage(int id) {
        deleteImage = getResources().getDrawable(id);
        deleteImage.setBounds(0, 0, deleteImage.getIntrinsicWidth(), deleteImage.getIntrinsicHeight());
        manageClearButton();
    }

    void manageClearButton() {
        if(isShowDeleteBtn){
            if (this.getText().toString().equals("") || !isFocused())//增加是否是当前焦点
                removeClearButton();
            else
                addClearButton();
        }else{
            removeClearButton();
        }
    }

    public void removeClearButton() {
        this.setCompoundDrawables(this.icon, this.getCompoundDrawables()[1], null, this.getCompoundDrawables()[3]);
    }

    void addClearButton() {
        this.setCompoundDrawables(this.icon, this.getCompoundDrawables()[1], deleteImage, this.getCompoundDrawables()[3]);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ClearableEditTextWithIcon et = ClearableEditTextWithIcon.this;
        if (et.getCompoundDrawables()[2] == null)
            return false;
        if (event.getAction() != MotionEvent.ACTION_UP)
            return false;
        if (event.getX() > et.getWidth() - et.getPaddingRight() - deleteImage.getIntrinsicWidth()) {
            clickRightBtn();
            return true;//点击右边的icon消耗掉此次事件
        }
        return false;
    }

    public void clickRightBtn(){
        if(rightType == TYPE_RIGHT_DEL){
            //删除
            this.setText("");
            ClearableEditTextWithIcon.this.removeClearButton();
        }else if(rightType == TYPE_RIGHT_EYE){
            //输入密码可见
            if(this.getTransformationMethod() == PasswordTransformationMethod.getInstance()){
                this.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                setDeleteImage(R.mipmap.icon_password_eye_close);
            }else{
                this.setTransformationMethod(PasswordTransformationMethod.getInstance());
                setDeleteImage(R.mipmap.icon_password_eye_open);
            }
            this.postInvalidate();
        }
    }

    public void setRightBtnType(int type){
        rightType = type;
        if(rightType == TYPE_RIGHT_EYE){
            setDeleteImage(R.mipmap.icon_password_eye_open);
        } else{
            setDeleteImage(R.mipmap.icon_edit_delete);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //ClearableEditTextWithIcon.this.manageClearButton();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        manageClearButton();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus){
            manageClearButton();
        } else{
            removeClearButton();
        }
    }
}
