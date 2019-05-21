package com.htgames.nutspoker.game.match.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.game.match.helper.RemaindTimeHelper;

/**
 * 比赛提示
 */
public class MatchTipView extends RelativeLayout {
    Context context;
//    RelativeLayout rl_match_center_tip;
    LinearLayout ll_match_will_begin;
    TextView tv_will_begin_second;
    TextView tv_match_center_tip;
    TextView iv_match_game_continue;
    String creatorId;

    View ll_pause_tip;
    TextView tv_match_center_tip_head;

    public MatchTipView(Context context) {
        super(context);
        init(context);
    }

    public MatchTipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MatchTipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.view_match_center_tip, this, true);
//        rl_match_center_tip = (RelativeLayout) findViewById(R.id.rl_match_center_tip);
        ll_match_will_begin = (LinearLayout) findViewById(R.id.ll_match_will_begin);
        tv_will_begin_second = (TextView) findViewById(R.id.tv_will_begin_second);
        tv_match_center_tip = (TextView) findViewById(R.id.tv_match_center_tip);
        iv_match_game_continue = (TextView) findViewById(R.id.iv_match_game_continue);
        ll_pause_tip = findViewById(R.id.ll_pause_tip);
        tv_match_center_tip_head = (TextView) findViewById(R.id.tv_match_center_tip_head);
        iv_match_game_continue.setVisibility(GONE);
    }

    /**
     * 显示即将开始的倒计时
     *
     * @param remainTime
     */
    public void showWillBeginRemainTime(int remainTime) {
        setVisibility(View.VISIBLE);
        ll_match_will_begin.setVisibility(View.VISIBLE);
        ll_pause_tip.setVisibility(View.GONE);
        tv_will_begin_second.setText("" + remainTime);
    }

    /**
     * 显示暂停状态
     */
    public void showPauseStatus(int remaindPauseTime) {
        setVisibility(View.VISIBLE);
        ll_match_will_begin.setVisibility(GONE);
        ll_pause_tip.setVisibility(View.VISIBLE);

        tv_match_center_tip_head.setText(R.string.match_pause_by_creator_ing);
        if (remaindPauseTime > 0) {
            tv_match_center_tip.setText(RemaindTimeHelper.getRemaindTimeShow(remaindPauseTime));
        } else {
            tv_match_center_tip.setText("");
        }
        if (creatorId.equals(DemoCache.getAccount())) {
            iv_match_game_continue.setVisibility(VISIBLE);
        } else {
            iv_match_game_continue.setVisibility(GONE);
        }
    }

    public void showPauseBefore(){
        setVisibility(View.VISIBLE);
        ll_match_will_begin.setVisibility(GONE);
        ll_pause_tip.setVisibility(View.VISIBLE);

        tv_match_center_tip_head.setText(R.string.pause_match_ok_tip_1);
        tv_match_center_tip.setText(R.string.pause_match_ok_tip_2);

        if (creatorId.equals(DemoCache.getAccount())) {
            iv_match_game_continue.setVisibility(VISIBLE);
        } else {
            iv_match_game_continue.setVisibility(GONE);
        }
    }

    public void setInfo(String creatorId){
        this.creatorId = creatorId;
    }

    public void setMatchContinueClick(OnClickListener onClickListener) {
        iv_match_game_continue.setOnClickListener(onClickListener);
    }
}
