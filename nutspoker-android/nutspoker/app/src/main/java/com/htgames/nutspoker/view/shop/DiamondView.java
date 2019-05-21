package com.htgames.nutspoker.view.shop;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.htgames.nutspoker.R;

/**
 * 宝石
 */
public class DiamondView extends LinearLayout {
    private final static String TAG = "DiamondView";
    View view;
    LinearLayout ll_giving_coin;

    public DiamondView(Context context) {
        super(context);
        init(context);
    }

    public DiamondView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DiamondView(Context context, AttributeSet attrs, int defStyleAttr) {
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
    }

    public void setNum(int winchip) {
        ll_giving_coin.removeAllViews();
        ImageView zengImageView = new ImageView(getContext());
        zengImageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        zengImageView.setImageResource(R.mipmap.message_control);
        ll_giving_coin.addView(zengImageView);
        ImageView songImageView = new ImageView(getContext());
        songImageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        songImageView.setImageResource(R.mipmap.icon_me_diamond);
        ll_giving_coin.addView(songImageView);
        //
        String winchipStr = String.valueOf(winchip);
        int length = winchipStr.length();
        if(length > 4 ){
            //判断是否破万
            for(int i = 0; i < length ; i++){
                if(i < (length - 2)){
                    int currentNum = Integer.valueOf(winchipStr.substring(i , i + 1));
//                    Log.d(TAG, "currentNum " + i + ":" + currentNum);
//                    if(currentNum != 0){
                        ll_giving_coin.addView(getImageView(currentNum));
                        if(i == (length - 5)){
                            //万 位
                            ImageView dian = new ImageView(getContext());
                            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                            dian.setLayoutParams(params);
                            dian.setImageResource(R.mipmap.icon_me_coin);//icon_coin_dian);
                            ll_giving_coin.addView(dian);
                        }
//                    }getContext()
                }
            }
            ImageView wanImageView = new ImageView(getContext());
            wanImageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            wanImageView.setImageResource(R.mipmap.message_system);
            ll_giving_coin.addView(wanImageView);
        } else{
            for(int i = 0; i <length ; i++) {
                int currentNum = Integer.valueOf(winchipStr.substring(i, i + 1));
//                Log.d(TAG, "currentNum " + i + ":" + currentNum);
                ll_giving_coin.addView(getImageView(currentNum));
            }
        }
        ImageView zuanIv = new ImageView(getContext());
        zuanIv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        zuanIv.setImageResource(R.mipmap.message_system);
        ll_giving_coin.addView(zuanIv);
        ImageView siIv = new ImageView(getContext());
        siIv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        siIv.setImageResource(R.mipmap.icon_me_diamond);
        ll_giving_coin.addView(siIv);
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

