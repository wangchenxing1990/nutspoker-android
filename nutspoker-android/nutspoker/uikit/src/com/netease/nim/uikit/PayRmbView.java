package com.netease.nim.uikit;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
/**
 */
public class PayRmbView extends PopupWindow {


    private Activity activity;
    private Callback callback;
    private View view;
    private View iconClose;
    private TextView textRmb;
    private LinearLayout layoutAlipay;
    private LinearLayout layoutWeixin;
    private ImageView iconAlipay;
    private ImageView iconWeixin;
    private Button btnSure;

    private int payType;


    public interface Callback {
        public void onSure(int payType);
    }

    public PayRmbView(Activity activity, String rmb, Callback callback) {
        super(activity);
        this.activity = activity;
        this.callback = callback;
        view = View.inflate(activity, R.layout.pop_pay_menu, null);
        iconClose =  view.findViewById(R.id.pop_pay_menu_close);
        textRmb = (TextView) view.findViewById(R.id.pop_pay_menu_rmb);
        layoutAlipay = (LinearLayout) view.findViewById(R.id.pop_pay_menu_alipay);
        layoutWeixin = (LinearLayout) view.findViewById(R.id.pop_pay_menu_weixin);
        iconAlipay = (ImageView) view.findViewById(R.id.pop_pay_menu_alipay_icon);
        iconWeixin = (ImageView) view.findViewById(R.id.pop_pay_menu_weixin_icon);
        btnSure = (Button) view.findViewById(R.id.pop_pay_menu_sure);
        /**
         *
         */
        textRmb.setText(rmb);
        payType = UIConstants.PAY_ALI;
        iconAlipay.setImageResource(R.drawable.nim_contact_checkbox_checked_green);
        iconWeixin.setImageResource(R.drawable.nim_contact_checkbox_unchecked);
        setContentView(view);//设置SelectPicPopupWindow的View
        setWidth(LayoutParams.MATCH_PARENT);//设置SelectPicPopupWindow弹出窗体的宽
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);//设置SelectPicPopupWindow弹出窗体可点击
        ColorDrawable dw = new ColorDrawable(0x00000000); //实例化一个ColorDrawable颜色为半透明
        setBackgroundDrawable(dw); //设置SelectPicPopupWindow弹出窗体的背景
        setAnimationStyle(android.R.style.Animation_InputMethod); //设置SelectPicPopupWindow弹出窗体动画效果

        //设置popwindow如果点击外面区域，便关闭。
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(0));


        initListener();
    }

    private void initListener() {
        iconClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        layoutAlipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payType = UIConstants.PAY_ALI;
                iconAlipay.setImageResource(R.drawable.nim_contact_checkbox_checked_green);
                iconWeixin.setImageResource(R.drawable.nim_contact_checkbox_unchecked);
            }
        });
        layoutWeixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payType = UIConstants.PAY_WX;
                iconAlipay.setImageResource(R.drawable.nim_contact_checkbox_unchecked);
                iconWeixin.setImageResource(R.drawable.nim_contact_checkbox_checked_green);
            }
        });
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onSure(payType);
                    dismiss();
                }
            }
        });
    }


    public void showBy(View parent) {
        showAtLocation(parent, Gravity.NO_GRAVITY, 0, 0); //设置layout在PopupWindow中显示的位置
    }

}
