package com.htgames.nutspoker.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.ui.helper.PayHelp;
import com.htgames.nutspoker.view.shop.DiamondView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by glp on 2016/8/17.
 */

public class PayDialog extends Dialog {

    @BindView(R.id.tv_pay_money) TextView mUiMoney;
    @BindView(R.id.diamond_view_tip) DiamondView mUiDiamondView;

    @OnClick(R.id.btn_pay_weixin) void clickWx(){
        if(mMoney != 0) {
//            PayHelp.doWxPay(getContext(),mMoney);
        }
    }
    @OnClick(R.id.btn_pay_ali) void clickAli(){
        if(mMoney != 0)
            PayHelp.doAliPay(mActivity,mMoney);
    }
    @OnClick(R.id.btn_close) void clickClose(){
        mMoney = 0;
        dismiss();
    }

    int mMoney = 0;
    Activity mActivity;

    public PayDialog(Activity activity) {
        super(activity,R.style.dialog_custom_prompt);
        setContentView(R.layout.dialog_pay);
        ButterKnife.bind(this);

        mActivity = activity;

        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    public void showUiMoney(int money, int diamond){
        mMoney = money;
        mUiMoney.setText(getContext().getString(R.string.pay_money_format,money));
        mUiDiamondView.setNum(diamond);

        show();
    }
}
