package com.htgames.nutspoker.chat.app_msg.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.app_msg.attach.GameOverNotify;
import com.htgames.nutspoker.chat.app_msg.contact.AppMessageConstants;
import com.htgames.nutspoker.chat.app_msg.helper.AppMessageHelper;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.game.model.GameMatchStatus;
import com.htgames.nutspoker.game.model.GameStatus;
import com.htgames.nutspoker.ui.helper.RecordHelper;
import com.htgames.nutspoker.view.record.RankView;
import com.netease.nim.uikit.common.util.sys.TimeUtil;

/**
 * Created by 周智慧 on 16/12/6.
 * 代码来自GameStatusViewHolder.java 新版本需求样式调整
 */

public class GameStatusVH extends BaseAppViewHolder {
    public boolean showInfoHead = true;
    LinearLayout ll_message_time;
    ImageView iv_app_message_type;
    TextView timeText;
    //
    TextView tv_gameover_content;
    TextView tv_gameover_gain_title;
    TextView tv_gameover_gain;
    TextView tv_app_gameover_read;
    TextView tv_game_status;
    ImageView iv_game_mode;
    RankView mRankView;

    public static GameStatusVH createViewHolder(Context context) {
        return new GameStatusVH(LayoutInflater.from(context).inflate(R.layout.viewholder_app_game_status_new, null));
    }

    public GameStatusVH(View itemView) {
        super(itemView);
        ll_message_time = (LinearLayout) itemView.findViewById(R.id.ll_message_time);
        iv_app_message_type = (ImageView) itemView.findViewById(R.id.iv_app_message_type);
        timeText = (TextView) itemView.findViewById(R.id.tv_message_time);
        //
        tv_gameover_content = (TextView) itemView.findViewById(R.id.tv_gameover_content);
        tv_gameover_gain_title = (TextView) itemView.findViewById(R.id.tv_gameover_gain_title);
        tv_gameover_gain = (TextView) itemView.findViewById(R.id.tv_gameover_gain);
        mRankView = (RankView) itemView.findViewById(R.id.mRankView);
        tv_app_gameover_read = (TextView) itemView.findViewById(R.id.tv_app_gameover_read);
        tv_game_status = (TextView) itemView.findViewById(R.id.tv_game_status);
        iv_game_mode = (ImageView) itemView.findViewById(R.id.iv_game_mode);
//        tv_gameover_content.setSingleLine(false);
    }

    public void refresh(Context context, AppMessage appMessage, boolean showTime, int position) {
        itemView.setOnClickListener(new OnClick(appMessage, position));
        itemView.setOnLongClickListener(new OnLongClick(appMessage, position));
        tv_app_gameover_read.setOnClickListener(new OnClick(appMessage, position));
        //
        tv_game_status.setVisibility(View.GONE);
        GameOverNotify gameOverNotify = (GameOverNotify) appMessage.attachObject;
        iv_app_message_type.setImageResource(R.mipmap.message_system);
        if (showTime) {
            timeText.setVisibility(View.VISIBLE);
            timeText.setText(TimeUtil.getTimeShowString(appMessage.time * 1000L, false));
        } else {
            timeText.setVisibility(View.GONE);
        }
        //
        if (gameOverNotify.gameMode == GameConstants.GAME_MODE_NORMAL) {
            iv_game_mode.setVisibility(View.GONE);
        } else if (gameOverNotify.gameMode == GameConstants.GAME_MODE_SNG) {
            iv_game_mode.setVisibility(View.VISIBLE);
            iv_game_mode.setImageResource(R.mipmap.icon_control_sng);
        } else if (gameOverNotify.gameMode == GameConstants.GAME_MODE_MTT) {
            iv_game_mode.setVisibility(View.VISIBLE);
            iv_game_mode.setImageResource(R.mipmap.icon_control_mtt);
        } else if (gameOverNotify.gameMode == GameConstants.GAME_MODE_MT_SNG) {
            iv_game_mode.setVisibility(View.VISIBLE);
            iv_game_mode.setImageResource(R.mipmap.icon_control_mtsng);
        }
        //
        tv_gameover_content.setText(AppMessageHelper.getAppMessageContent(context, appMessage));
        int winChip = gameOverNotify.userWinChips;

        //开始比赛，或者暂停后重新开始...
        if (gameOverNotify.gameStatus == GameStatus.GAME_STATUS_START || gameOverNotify.gameStatus == GameMatchStatus.GAME_STATUS_RESUME) {
            mRankView.setVisibility(View.GONE);
            tv_gameover_gain_title.setVisibility(View.GONE);
            tv_gameover_gain.setVisibility(View.GONE);
//            tv_app_gameover_read.setVisibility(View.GONE);//不需要展示了，新版本还是需要显示
            tv_app_gameover_read.setText("进入比赛");
            tv_app_gameover_read.setVisibility(View.VISIBLE);//不需要展示了，新版本还是需要显示
//            tv_game_status.setVisibility(View.VISIBLE);
//            tv_game_status.setText(R.string.game_status_ing);// TODO: 16/12/26 这个状态不要了
//            if(gameOverNotify.gameMode == GameConstants.GAME_MODE_MTT)
//                tv_game_status.setBackgroundResource(R.drawable.recent_game_mtt_tag_bg);
//            else
//                tv_game_status.setBackgroundResource(R.drawable.recent_game_sng_tag_bg);
        } else if (gameOverNotify.gameStatus == GameStatus.GAME_STATUS_FINISH) {
            tv_app_gameover_read.setText(R.string.app_message_gameover_record_read);
            tv_app_gameover_read.setVisibility(View.VISIBLE);
            if (gameOverNotify.gameMode == GameConstants.GAME_MODE_NORMAL) {
                winChip = gameOverNotify.userWinChips;
                mRankView.setVisibility(View.GONE);
                tv_gameover_gain_title.setVisibility(View.VISIBLE);
                tv_gameover_gain.setVisibility(View.VISIBLE);
                tv_app_gameover_read.setVisibility(View.VISIBLE);
                tv_gameover_gain_title.setText(R.string.app_message_gameover_gain);
            } else if (gameOverNotify.gameMode == GameConstants.GAME_MODE_SNG) {
                winChip = gameOverNotify.userReward;
                mRankView.setVisibility(View.GONE);
                tv_gameover_gain_title.setVisibility(View.GONE);
                tv_gameover_gain.setVisibility(View.GONE);
                tv_gameover_gain_title.setText(R.string.app_message_gameover_bonus);
                int rank = gameOverNotify.userRank;
                int balance = gameOverNotify.userBalance;
                int endType = gameOverNotify.gameEndType;//结束类型
                if (endType == AppMessageConstants.TYPE_GAMEOVER_END_NORMAL) {
                    //正常结束
                    mRankView.setVisibility(View.VISIBLE);
                    tv_app_gameover_read.setVisibility(View.VISIBLE);
                    tv_gameover_gain.setVisibility(View.VISIBLE);
                    tv_gameover_gain_title.setVisibility(View.VISIBLE);
//                    SngHelper.setSngRankTagView(tv_record_sng_rank_tag, true , rank);
                    mRankView.setRankTagView(rank);
                } else if (endType == AppMessageConstants.TYPE_GAMEOVER_END_BY_CREATOR) {
                    tv_app_gameover_read.setVisibility(View.GONE);
                } else if (endType == AppMessageConstants.TYPE_GAMEOVER_END_TIMEOUT) {
                    tv_app_gameover_read.setVisibility(View.GONE);
                } else if (endType == AppMessageConstants.TYPE_GAMEOVER_END_PLAYER_NOT_ENOUGH) {
                    tv_app_gameover_read.setVisibility(View.GONE);
                }
            } else if (gameOverNotify.gameMode == GameConstants.GAME_MODE_MTT) {
                winChip = gameOverNotify.userReward;
                mRankView.setVisibility(View.GONE);
                tv_gameover_gain_title.setVisibility(View.GONE);
                tv_gameover_gain.setVisibility(View.GONE);
                tv_gameover_gain_title.setText(R.string.app_message_gameover_bonus);
                int rank = gameOverNotify.userRank;
                int balance = gameOverNotify.userBalance;
                int endType = gameOverNotify.gameEndType;//结束类型
                if (endType == AppMessageConstants.TYPE_GAMEOVER_END_NORMAL) {
                    //正常结束
//                    mRankView.setVisibility(View.VISIBLE);mtt不显示排名，黄老师没有返回"排名"和"奖金"的信息，因为mtt人太多是群发的，数据都一样
                    tv_app_gameover_read.setVisibility(View.VISIBLE);
//                    tv_gameover_gain.setVisibility(View.VISIBLE);mtt不显示排名，黄老师没有返回"排名"和"奖金"的信息，因为mtt人太多是群发的，数据都一样
//                    tv_gameover_gain_title.setVisibility(View.VISIBLE);mtt不显示排名，黄老师没有返回"排名"和"奖金"的信息，因为mtt人太多是群发的，数据都一样
//                    SngHelper.setSngRankTagView(tv_record_sng_rank_tag, true , rank);
                    mRankView.setRankTagView(rank);
                } else if (endType == AppMessageConstants.TYPE_GAMEOVER_END_BY_CREATOR) {
                    tv_app_gameover_read.setVisibility(View.GONE);
                } else if (endType == AppMessageConstants.TYPE_GAMEOVER_END_BY_CREATOR) {
                    tv_app_gameover_read.setVisibility(View.GONE);
                } else if (endType == AppMessageConstants.TYPE_GAMEOVER_END_PLAYER_NOT_ENOUGH) {
                    tv_app_gameover_read.setVisibility(View.GONE);
                }
            } else if (gameOverNotify.gameMode == GameConstants.GAME_MODE_MT_SNG) {
                winChip = gameOverNotify.userReward;
                mRankView.setVisibility(View.GONE);
                tv_gameover_gain_title.setVisibility(View.GONE);
                tv_gameover_gain.setVisibility(View.GONE);
                tv_gameover_gain_title.setText(R.string.app_message_gameover_bonus);
                int rank = gameOverNotify.userRank;
                int balance = gameOverNotify.userBalance;
                int endType = gameOverNotify.gameEndType;//结束类型
                if (endType == AppMessageConstants.TYPE_GAMEOVER_END_NORMAL) {
                    //正常结束
                    mRankView.setVisibility(View.VISIBLE);
                    tv_app_gameover_read.setVisibility(View.VISIBLE);
                    tv_gameover_gain.setVisibility(View.VISIBLE);
                    tv_gameover_gain_title.setVisibility(View.VISIBLE);
//                    SngHelper.setSngRankTagView(tv_record_sng_rank_tag, true , rank);
                    mRankView.setRankTagView(rank);
                } else if (endType == AppMessageConstants.TYPE_GAMEOVER_END_BY_CREATOR) {
                    tv_app_gameover_read.setVisibility(View.GONE);
                } else if (endType == AppMessageConstants.TYPE_GAMEOVER_END_BY_CREATOR) {
                    tv_app_gameover_read.setVisibility(View.GONE);
                } else if (endType == AppMessageConstants.TYPE_GAMEOVER_END_PLAYER_NOT_ENOUGH) {
                    tv_app_gameover_read.setVisibility(View.GONE);
                }
            }
            RecordHelper.setRecordGainView(tv_gameover_gain, winChip, gameOverNotify.gameMode);
        } else if (gameOverNotify.gameStatus == GameMatchStatus.GAME_STATUS_REST_FINISH) {
            tv_gameover_gain_title.setVisibility(View.GONE);
            mRankView.setVisibility(View.GONE);
            tv_app_gameover_read.setText("进入比赛");
//            if (gameOverNotify.gameMode == GameConstants.GAME_MODE_MTT) {
//                iv_game_mode.setVisibility(View.VISIBLE);
//                iv_game_mode.setImageResource(R.mipmap.icon_paiju_item_mtt);
//            }
        } else if(gameOverNotify.gameStatus == GameMatchStatus.GAME_STATUS_WILL_START) {
            //即将开始
            tv_app_gameover_read.setText("进入比赛");
//            tv_game_status.setVisibility(View.VISIBLE);
            tv_game_status.setText(R.string.game_status_will_start);
//            tv_game_status.setBackgroundResource(R.drawable.recent_game_mtt_tag_bg);
        } else if(gameOverNotify.gameStatus == GameMatchStatus.GAME_STATUS_PAUSE) {
            //暂停比赛
            tv_gameover_gain_title.setVisibility(View.GONE);
            mRankView.setVisibility(View.GONE);
            tv_app_gameover_read.setText("进入比赛");
//            tv_game_status.setVisibility(View.VISIBLE);
            tv_game_status.setText(R.string.game_status_pause);
//            tv_game_status.setBackgroundResource(R.drawable.recent_game_mtt_tag_bg);
        }
    }

    public void showInfoHead(boolean isShow) {
        if (isShow) {
            ll_message_time.setVisibility(View.VISIBLE);
        } else {
            ll_message_time.setVisibility(View.GONE);
        }
    }
}
