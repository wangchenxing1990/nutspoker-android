package com.htgames.nutspoker.chat.app_msg.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.app_msg.attach.GameOverNotify;
import com.htgames.nutspoker.chat.app_msg.contact.AppMessageConstants;
import com.htgames.nutspoker.game.model.GameStatus;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.view.record.RankView;
import com.htgames.nutspoker.ui.helper.RecordHelper;

/**
 * 牌局结束通知
 */
public class GameOverView extends LinearLayout implements View.OnClickListener {
    View view;
    Context context;
    TextView tv_gameover_content;
    TextView tv_gameover_gain_title;
    TextView tv_gameover_gain;
    TextView tv_app_gameover_read;
    ImageView iv_gameover_sng;
    RankView mRankView;

    public GameOverView(Context context) {
        super(context);
        init(context);
    }

    public GameOverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameOverView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.view_app_gameover_item, null);
        initView();
        addView(view);
    }

    private void initView() {
        tv_gameover_content = (TextView) view.findViewById(R.id.tv_gameover_content);
        tv_gameover_gain_title = (TextView) view.findViewById(R.id.tv_gameover_gain_title);
        tv_gameover_gain = (TextView) view.findViewById(R.id.tv_gameover_gain);
        mRankView = (RankView) view.findViewById(R.id.mRankView);
        tv_app_gameover_read = (TextView) view.findViewById(R.id.tv_app_gameover_read);
        iv_gameover_sng = (ImageView) view.findViewById(R.id.iv_gameover_sng);
        tv_gameover_content.setSingleLine(false);
    }

    public void setInfo(GameOverNotify gameOverNotify) {
        int winChip = gameOverNotify.userWinChips;
        if (gameOverNotify.gameStatus == GameStatus.GAME_STATUS_START) {
            tv_gameover_content.setText(context.getString(R.string.app_message_gamestatus_start_content, gameOverNotify.gameName));
            tv_app_gameover_read.setText(R.string.app_message_gameover_join_game);
        } else if (gameOverNotify.gameStatus == GameStatus.GAME_STATUS_FINISH) {
            tv_app_gameover_read.setText(R.string.app_message_gameover_record_read);
            tv_gameover_content.setText(context.getString(R.string.app_message_gamestatus_match_content, gameOverNotify.gameName));
            if (gameOverNotify.gameMode == GameConstants.GAME_MODE_NORMAL) {
                winChip = gameOverNotify.userWinChips;
                iv_gameover_sng.setVisibility(GONE);
                mRankView.setVisibility(GONE);
                tv_gameover_gain_title.setVisibility(VISIBLE);
                tv_gameover_gain_title.setText(R.string.app_message_gameover_gain);
            } else if (gameOverNotify.gameMode == GameConstants.GAME_MODE_SNG) {
                winChip = gameOverNotify.userReward;
                iv_gameover_sng.setVisibility(VISIBLE);
                mRankView.setVisibility(VISIBLE);
                tv_gameover_gain_title.setVisibility(GONE);
                tv_gameover_gain_title.setText(R.string.app_message_gameover_bonus);
                int rank = gameOverNotify.userRank;
                mRankView.setRankTagView(rank);
                int balance = gameOverNotify.userBalance;
                int endType = gameOverNotify.gameEndType;//结束类型
                if (endType == AppMessageConstants.TYPE_GAMEOVER_END_NORMAL) {
                    tv_gameover_content.setText(context.getString(R.string.app_message_gamestatus_match_content, gameOverNotify.gameName));
                } else if (endType == AppMessageConstants.TYPE_GAMEOVER_END_BY_CREATOR) {
                    tv_gameover_content.setText(context.getString(R.string.app_message_gamestatus_match_end_by_creator_content,
                            gameOverNotify.gameName, balance));
                } else if (endType == AppMessageConstants.TYPE_GAMEOVER_END_TIMEOUT) {
                    tv_gameover_content.setText(context.getString(R.string.app_message_gamestatus_match_end_by_timeout_content,
                            gameOverNotify.gameName, balance));
                }
            }
            RecordHelper.setRecordGainView(tv_gameover_gain, winChip, gameOverNotify.gameMode);
        }
//        if (winChip > 0) {
//            tv_gameover_gain.setText("+" + winChip);
//            tv_gameover_gain.setTextColor(context.getResources().getColor(R.color.record_item_earnings_gain_color));
//        } else if (winChip < 0) {
//            tv_gameover_gain.setText(String.valueOf(winChip));
//            tv_gameover_gain.setTextColor(context.getResources().getColor(R.color.record_item_earnings_lose_color));
//        } else {
//            tv_gameover_gain.setText("" + winChip);
//            tv_gameover_gain.setTextColor(context.getResources().getColor(R.color.record_item_earnings_gain_color));
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
