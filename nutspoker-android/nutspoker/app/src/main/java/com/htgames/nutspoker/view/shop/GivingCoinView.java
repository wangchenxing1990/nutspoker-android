package com.htgames.nutspoker.view.shop;

import android.content.Context;
import android.util.AttributeSet;
import com.netease.nim.uikit.common.util.log.LogUtil;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.htgames.nutspoker.R;

/**
 * 盈利视图
 */
public class GivingCoinView extends LinearLayout {
    private final static String TAG = "GivingCoinView";
    View view;
    LinearLayout ll_giving_coin;

    public GivingCoinView(Context context) {
        super(context);
        init(context);
    }

    public GivingCoinView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GivingCoinView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.view_giving_coin, null);
        initView();
        addView(view);
    }

    private void initView() {
        ll_giving_coin = (LinearLayout) view.findViewById(R.id.ll_giving_coin);
//        ll_giving_coin = new LinearLayout(context);
//        ll_giving_coin.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT));
//        ll_giving_coin.setOrientation(HORIZONTAL);
    }

    public void setNum(int winchip) {
        ll_giving_coin.removeAllViews();
        String winchipStr = String.valueOf(winchip);
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setImageResource(R.mipmap.icon_me_coin);
        ll_giving_coin.addView(imageView);
        ImageView givingImageView = new ImageView(getContext());
        givingImageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        givingImageView.setImageResource(R.mipmap.icon_me_coin);
        ll_giving_coin.addView(givingImageView);
        int length = winchipStr.length();
        if(length > 4 ){
            //判断是否破万
            for(int i = 0; i < length ; i++){
                if(i < (length - 2)){
                    int currentNum = Integer.valueOf(winchipStr.substring(i , i + 1));
                    LogUtil.i(TAG, "currentNum " + i + ":" + currentNum);
//                    if(currentNum != 0){
                        ll_giving_coin.addView(getImageView(currentNum));
                        if(i == (length - 5)){
                            //万 位
                            ImageView dian = new ImageView(getContext());
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                            dian.setLayoutParams(params);
                            dian.setImageResource(R.mipmap.icon_me_coin);
                            ll_giving_coin.addView(dian);
                        }
//                    }
                }
            }
            ImageView wanImageView = new ImageView(getContext());
            wanImageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            wanImageView.setImageResource(R.mipmap.message_control);
            ll_giving_coin.addView(wanImageView);
        } else{
            for(int i = 0; i <length ; i++) {
                int currentNum = Integer.valueOf(winchipStr.substring(i, i + 1));
                LogUtil.i(TAG, "currentNum " + i + ":" + currentNum);
                ll_giving_coin.addView(getImageView(currentNum));
            }
        }
    }

    public ImageView getImageView(int i){
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setImageResource(getCoinIcon(i));
        return imageView;
    }

    public int getCoinIcon(int i) {
        switch (i) {
//            case 0:
//                return R.mipmap.icon_coin_0;
//            case 1:
//                return R.mipmap.icon_coin_1;
//            case 2:
//                return R.mipmap.icon_coin_2;
//            case 3:
//                return R.mipmap.icon_coin_3;
//            case 4:
//                return R.mipmap.icon_coin_4;
//            case 5:
//                return R.mipmap.icon_coin_5;
//            case 6:
//                return R.mipmap.icon_coin_6;
//            case 7:
//                return R.mipmap.icon_coin_7;
//            case 8:
//                return R.mipmap.icon_coin_8;
//            case 9:
//                return R.mipmap.icon_coin_9;
//            default:
//                return R.mipmap.icon_coin_0;
        }
        return 100;
    }
}

