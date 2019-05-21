package com.htgames.nutspoker.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

/**
 */
public class MatchCreateRulesPopView extends PopupWindow {
    public View popView;
    public RelativeLayout rl_game_create_rule;
    public TextView tv_match_rule_content;
    public static int TYPE_LEFT = 0;
    public static int TYPE_RIGHT = 1;
    private int type = TYPE_RIGHT;

    public MatchCreateRulesPopView(Context context , int type) {
        super(context);
        this.type = type;
        init(context);
        if (type == TYPE_RIGHT) {
            rl_game_create_rule.setBackgroundResource(R.drawable.bg_mtt_instructions);
        } else {
            rl_game_create_rule.setBackgroundResource(R.drawable.bg_mtt_instructions);
        }
    }

    public void setBackground(int bgId) {
        rl_game_create_rule.setBackgroundResource(bgId);
    }

    public void init(Context context) {
        popView = LayoutInflater.from(context).inflate(R.layout.pop_match_rules_view, null);
        rl_game_create_rule = (RelativeLayout) popView.findViewById(R.id.rl_game_create_rule);
        tv_match_rule_content = (TextView) popView.findViewById(R.id.tv_match_rule_content);
        //获取popwindow焦点
        setFocusable(true);
        //设置popwindow如果点击外面区域，便关闭。
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(0));
        //设置popwindow出现和消失动画
//        setAnimationStyle(R.style.PopMenuAnimation);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(popView);
    }

    public void show(View view, int content) {
        tv_match_rule_content.setText(content);
        if (!isShowing()) {
//            this.showAtLocation(view, Gravity.RIGHT, ScreenUtil.dip2px(context, 40), 0);
            if (type == TYPE_RIGHT) {
                this.showAsDropDown(view, 0, 2);
            } else {
                this.showAsDropDown(view, ScreenUtil.dip2px(view.getContext(), 34), 0);
            }
        }
    }

    //比赛大厅 报名费说明
    public void showMatchRoomFeeRules(View view, int content, int textColorId) {
        tv_match_rule_content.setText(content);
        tv_match_rule_content.setTextColor(view.getContext().getResources().getColor(textColorId));
        if (!isShowing()) {
            this.showAsDropDown(view, ScreenUtil.dip2px(view.getContext(), 150), ScreenUtil.dip2px(view.getContext(), 2));
        }
    }

    //比赛大厅 猎人赛说明
    public void showMatchHunterRulsPop(View view, int content, int textColorId) {
        tv_match_rule_content.setText(content);
        tv_match_rule_content.setTextColor(view.getContext().getResources().getColor(textColorId));//猎人赛宽75dp高21dp
        if (!isShowing()) {
            this.showAsDropDown(view, ScreenUtil.dip2px(view.getContext(), -124), ScreenUtil.dip2px(view.getContext(), 2));
            // 有参数的话，就是一view的左下角进行偏移，xoff正的向右，负的向左. yoff没测，也应该是正的向下，负的向上  即向右下角偏倚
        }
    }

    //快速创建比赛时
    public void showMTTInstructions(View view, int content, int textColorId) {
        tv_match_rule_content.setText(content);
        tv_match_rule_content.setTextColor(view.getContext().getResources().getColor(textColorId));
        if (!isShowing()) {
            this.showAsDropDown(view, ScreenUtil.dip2px(view.getContext(), -37), ScreenUtil.dip2px(view.getContext(), -15));
        }
    }

    public void showMTTInstructions(View view, int content, int textColorId, int xOffsetDp, int YOffsetDp) {
        tv_match_rule_content.setText(content);
        tv_match_rule_content.setTextColor(view.getContext().getResources().getColor(textColorId));
        if (!isShowing()) {
            this.showAsDropDown(view, ScreenUtil.dip2px(view.getContext(), xOffsetDp), ScreenUtil.dip2px(view.getContext(), YOffsetDp));
        }
    }
}
