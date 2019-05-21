package com.htgames.nutspoker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.config.GameConfigData;
import com.will.common.view.phasedseekbar.PhasedInteractionListener;
import com.will.common.view.phasedseekbar.PhasedListener;
import com.will.common.view.phasedseekbar.PhasedSeekBar;
import com.will.common.view.phasedseekbar.SimplePhasedAdapter;

import java.util.ArrayList;

/**
 * ante模式拖动
 */
public class AnteSeekBar extends LinearLayout {
    View view;
    protected PhasedSeekBar seekBar;
    ArrayList<TextView> tvList;
    PhasedListener mPhasedListener;

    public AnteSeekBar(Context context) {
        super(context);
        init(context);
    }

    public AnteSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AnteSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.view_ante_seekbar, null);
        addView(view);
        initView();
        initSeekBar();
    }

    private void initView() {
        tvList = new ArrayList<TextView>(4);
        tvList.add((TextView) view.findViewById(R.id.tv_ante_0));
        tvList.add((TextView) view.findViewById(R.id.tv_ante_1));
        tvList.add((TextView) view.findViewById(R.id.tv_ante_2));
        tvList.add((TextView) view.findViewById(R.id.tv_ante_3));
    }

    public void updateSBlinds(int sblinds) {
        final int size = tvList.size();
        for (int i = 0; i < size; i++) {
            int ante = sblinds * GameConfigData.anteMode[i];
            tvList.get(i).setText(String.valueOf(ante));
        }
    }

    private void initSeekBar() {
        seekBar = (PhasedSeekBar) view.findViewById(R.id.psb_star);
        seekBar.setAdapter(new SimplePhasedAdapter(getResources(), new int[]{
                R.drawable.game_create_config_ante_thumb,
                R.drawable.game_create_config_ante_thumb,
                R.drawable.game_create_config_ante_thumb,
                R.drawable.game_create_config_ante_thumb}));
        seekBar.setPosition(0);
        final int size = tvList.size();
        seekBar.setListener(new PhasedListener() {
            @Override
            public void onPositionSelected(int position) {
                if (mPhasedListener != null) {
                    mPhasedListener.onPositionSelected(position);
                }
                for (int i = 0; i < size; i++) {
                    if (i == position) {
                        tvList.get(i).setTextColor(getContext().getResources().getColor(R.color.game_create_ante_bg_color));
                    } else {
                        tvList.get(i).setTextColor(getContext().getResources().getColor(R.color.game_create_ante_text_normal_color));
                    }
                }
            }
        });
        //
    }

    public void setAnteChangeListener(PhasedListener listener) {
        this.mPhasedListener = listener;
    }

    public int getAnteValue(int position) {
        int value = GameConfigData.anteMode[position];
        return value;
    }

    public void setInteractionListener(PhasedInteractionListener listener) {
        if (seekBar != null) {
            seekBar.setInteractionListener(listener);
        }
    }
}
