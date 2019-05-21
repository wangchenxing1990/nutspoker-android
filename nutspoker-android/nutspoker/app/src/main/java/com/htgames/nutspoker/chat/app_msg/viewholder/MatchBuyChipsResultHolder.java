package com.htgames.nutspoker.chat.app_msg.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.app_msg.attach.MatchBuyChipsResultNotify;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageStatus;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.game.model.GameMatchBuyType;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.TimeUtil;

/**
 */
public class MatchBuyChipsResultHolder extends BaseAppViewHolder {
    private final static String TAG = "MatchBuyChipsResultHolder";
    LinearLayout ll_message_time;
    ImageView iv_app_message_type;
    TextView timeText;
    TextView tv_game_name;
    TextView tv_gameover_content;
    ImageView iv_game_mode;
    ImageView iv_app_message_arrow;

    public static MatchBuyChipsResultHolder createViewHolder(Context context) {
        return new MatchBuyChipsResultHolder(LayoutInflater.from(context).inflate(R.layout.viewholder_app_match_buychips_result, null));
    }

    public MatchBuyChipsResultHolder(View itemView) {
        super(itemView);
        ll_message_time = (LinearLayout) itemView.findViewById(R.id.ll_message_time);
        iv_app_message_type = (ImageView) itemView.findViewById(R.id.iv_app_message_type);
        timeText = (TextView) itemView.findViewById(R.id.tv_message_time);
        tv_game_name = (TextView) itemView.findViewById(R.id.tv_game_name);
        tv_gameover_content = (TextView) itemView.findViewById(R.id.tv_gameover_content);
        iv_game_mode = (ImageView) itemView.findViewById(R.id.iv_game_mode);
        iv_app_message_arrow = (ImageView) itemView.findViewById(R.id.iv_app_message_arrow);
//        tv_gameover_content.setSingleLine(false);
    }

    public void refresh(Context context , AppMessage appMessage , boolean showTime, int position) {
        itemView.setOnClickListener(new OnClick(appMessage, position));
        itemView.setOnLongClickListener(new OnLongClick(appMessage, position));
        //
        MatchBuyChipsResultNotify resultNotify = (MatchBuyChipsResultNotify) appMessage.attachObject;
        iv_app_message_type.setImageResource(R.mipmap.message_system);
        if (showTime) {
            timeText.setVisibility(View.VISIBLE);
            timeText.setText(TimeUtil.getTimeShowString(appMessage.time * 1000L, false));
        } else {
            timeText.setVisibility(View.GONE);
        }
        //
        int gameMode = resultNotify.gameMode;
        int buyType = resultNotify.buyType;
        LogUtil.i(TAG, "gameMode :" + gameMode + ";buyType:" + buyType + "; staus:" + appMessage.status);
        tv_game_name.setText("“" + ((MatchBuyChipsResultNotify) appMessage.attachObject).gameName + "”");

        String agreeStr = "房主同意您的";
        String rejectStr = "房主拒绝您的";
        String buyTypeStr = context.getString(R.string.match);
        if (buyType == GameMatchBuyType.TYPE_BUY_CHECKIN) {
            buyTypeStr = context.getString(R.string.match);
        } else if (buyType == GameMatchBuyType.TYPE_BUY_REBUY || resultNotify.buyType == GameMatchBuyType.TYPE_BUY_REBUY_WEEDOUT) {
            buyTypeStr = context.getString(R.string.rebuy);
        } else if (buyType == GameMatchBuyType.TYPE_BUY_ADDON || resultNotify.buyType == GameMatchBuyType.TYPE_BUY_ADDON_WEEDOUT) {
            buyTypeStr = context.getString(R.string.addon);
        } else if (buyType == GameMatchBuyType.TYPE_BUY_REBUY_REVIVAL) {
            buyTypeStr = "复活请求";
        }
        if (appMessage.status == AppMessageStatus.passed) {
//            content = context.getString(R.string.app_message_match_buychips_result_content_agree, buyTypeStr, resultNotify.getGameName());
            tv_gameover_content.setText(agreeStr + buyTypeStr);
        } else if (appMessage.status == AppMessageStatus.declined) {
//            content = context.getString(R.string.app_message_match_buychips_result_content_rejcet, buyTypeStr, resultNotify.getGameName());
            tv_gameover_content.setText(rejectStr + buyTypeStr);
        }
//        String buyTypeStr = context.getString(R.string.match);
//        if(buyType == GameMatchBuyType.TYPE_BUY_CHECKIN) {
//            buyTypeStr = context.getString(R.string.match);
//        } else if(buyType == GameMatchBuyType.TYPE_BUY_REBUY || buyType == GameMatchBuyType.TYPE_BUY_REBUY_WEEDOUT) {
//            buyTypeStr = context.getString(R.string.rebuy);
//        } else if(buyType == GameMatchBuyType.TYPE_BUY_ADDON || buyType == GameMatchBuyType.TYPE_BUY_ADDON_WEEDOUT) {
//            buyTypeStr = context.getString(R.string.addon);
//        }
        if (appMessage.status == AppMessageStatus.passed) {
//            tv_gameover_content.setText(context.getString(R.string.app_message_match_buychips_result_content_agree, buyTypeStr, resultNotify.getGameName()));
            iv_app_message_arrow.setVisibility(View.VISIBLE);
        } else if (appMessage.status == AppMessageStatus.declined) {
//            tv_gameover_content.setText(context.getString(R.string.app_message_match_buychips_result_content_rejcet, buyTypeStr, resultNotify.getGameName()));
            iv_app_message_arrow.setVisibility(View.GONE);
        }
        if (gameMode == GameConstants.GAME_MODE_MTT) {
            iv_game_mode.setImageResource(R.mipmap.icon_control_mtt);
            iv_game_mode.setVisibility(View.VISIBLE);
        } else if (gameMode == GameConstants.GAME_MODE_MT_SNG) {
            iv_game_mode.setVisibility(View.VISIBLE);
            iv_game_mode.setImageResource(R.mipmap.icon_control_mtsng);
        }
    }
}
