package com.htgames.nutspoker.chat.app_msg.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.app_msg.attach.MatchBuyChipsResultNotify;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageStatus;
import com.netease.nim.uikit.constants.GameConstants;

/**
 * 牌局结束通知
 */
public class MatchBuyChipsResultView extends LinearLayout implements View.OnClickListener {
    View view;
    Context context;
    TextView tv_gameover_content;
    ImageView iv_game_mode;

    public MatchBuyChipsResultView(Context context) {
        super(context);
        init(context);
    }

    public MatchBuyChipsResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MatchBuyChipsResultView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.view_app_match_buychips_result_item, null);
        initView();
        addView(view);
    }

    private void initView() {
        tv_gameover_content = (TextView) view.findViewById(R.id.tv_gameover_content);
        iv_game_mode = (ImageView) view.findViewById(R.id.iv_game_mode);
        tv_gameover_content.setSingleLine(false);
    }

    public void setInfo(MatchBuyChipsResultNotify resultNotify, AppMessageStatus status) {
        if (status == AppMessageStatus.passed) {
            tv_gameover_content.setText(context.getString(R.string.app_message_match_buychips_result_content_agree, resultNotify.gameName));
        } else if (status == AppMessageStatus.declined) {
            tv_gameover_content.setText(context.getString(R.string.app_message_match_buychips_result_content_rejcet, resultNotify.gameName));
        }
        if (resultNotify.gameMode == GameConstants.GAME_MODE_MTT) {
            iv_game_mode.setImageResource(R.mipmap.icon_mtt_blue);
            iv_game_mode.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
