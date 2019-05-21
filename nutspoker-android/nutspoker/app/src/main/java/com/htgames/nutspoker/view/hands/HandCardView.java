package com.htgames.nutspoker.view.hands;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.htgames.nutspoker.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 最大手牌
 */
public class HandCardView extends FrameLayout {
    int pools_type;//0--手牌；1--底牌
    ArrayList<View> views = new ArrayList<>();
    public HandCardView(Context context) {
        super(context);
        init(context, null);
    }

    public HandCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HandCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PokerView);
        int viewsNum = a.getInteger(R.styleable.PokerView_viewsNum, 2);
        pools_type = a.getInteger(R.styleable.PokerView_pools_type, 0);
        float singlePokerMargin = a.getDimension(R.styleable.PokerView_singleViewMarginLeft, context.getResources().getDimension(R.dimen.single_poker_margin));
        float singlePokerWidth = a.getDimension(R.styleable.PokerView_singleViewWidth, context.getResources().getDimension(R.dimen.single_poker_width));
        float singlePokerHeight = a.getDimension(R.styleable.PokerView_singleViewHeight, context.getResources().getDimension(R.dimen.single_poker_height));
        views.clear();
        for (int i = 0; i < viewsNum; i++) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int) singlePokerWidth, (int) singlePokerHeight);
            lp.setMargins((int) singlePokerMargin * i, 0, 0, 0);
            View pokerView = new View(context);
            pokerView.setBackgroundResource(R.mipmap.poker_bg);
            addView(pokerView, lp);
            views.add(pokerView);
        }

    }

    public void setHandCard(List<Integer> handCardList) {
        for (int i = 0; i < views.size(); i++) {
            views.get(i).setBackgroundResource(R.mipmap.poker_bg);
            if (pools_type == 0) {
                views.get(i).setVisibility(GONE);
            }
        }
        if (handCardList != null && handCardList.size() != 0) {
            int size = handCardList.size();
            for (int i = 0; i < size; i++) {
                if (i < views.size()) {
                    int pokerId = handCardList.get(i);
                    int flower = (pokerId - 1) % 4;
                    int num = (pokerId - 1) / 4 - 1;
                    if (flower >= 0 && flower <= 3 && num >= 0 && num <= 12) {
                        views.get(i).setBackgroundResource(pokerResIds[flower][num]);
                        views.get(i).setVisibility(VISIBLE);
                    }
                }
            }
        }
    }

    /**
     * 1红桃
     * 2方块
     * 3梅花
     * 4黑桃
     */
    public static int[][] pokerResIds = {
            {R.mipmap.card_12, R.mipmap.card_13, R.mipmap.card_14, R.mipmap.card_15, R.mipmap.card_16, R.mipmap.card_17, R.mipmap.card_18, R.mipmap.card_19, R.mipmap.card_110, R.mipmap.card_111, R.mipmap.card_112, R.mipmap.card_113, R.mipmap.card_114, 100, 100, 100},
            {R.mipmap.card_22, R.mipmap.card_23, R.mipmap.card_24, R.mipmap.card_25, R.mipmap.card_26, R.mipmap.card_27, R.mipmap.card_28, R.mipmap.card_29, R.mipmap.card_210, R.mipmap.card_211, R.mipmap.card_212, R.mipmap.card_213, R.mipmap.card_214, 100, 100, 100},
            {R.mipmap.card_32, R.mipmap.card_33, R.mipmap.card_34, R.mipmap.card_35, R.mipmap.card_36, R.mipmap.card_37, R.mipmap.card_38, R.mipmap.card_39, R.mipmap.card_310, R.mipmap.card_311, R.mipmap.card_312, R.mipmap.card_313, R.mipmap.card_314, 100, 100, 100},
            {R.mipmap.card_42, R.mipmap.card_43, R.mipmap.card_44, R.mipmap.card_45, R.mipmap.card_46, R.mipmap.card_47, R.mipmap.card_48, R.mipmap.card_49, R.mipmap.card_410, R.mipmap.card_411, R.mipmap.card_412, R.mipmap.card_413, R.mipmap.card_414, 100, 100, 100}
    };
}
