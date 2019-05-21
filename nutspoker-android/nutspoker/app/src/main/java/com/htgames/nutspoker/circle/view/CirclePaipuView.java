package com.htgames.nutspoker.circle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMtSngConfig;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.bean.GameNormalConfig;
import com.netease.nim.uikit.bean.GameSngConfigEntity;
import com.netease.nim.uikit.bean.PaipuEntity;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.data.common.PaipuConstants;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.htgames.nutspoker.ui.helper.RecordHelper;
import com.htgames.nutspoker.view.hands.HandCardView;

/**
 * 牌谱
 */
public class CirclePaipuView extends LinearLayout {
    private final static String TAG = "CirclePaipuView";
    public Context context;
    View view;
    LinearLayout ll_circle_paipu;
    HandCardView mHandCardView;
    HandCardView mCardTypeView;
    TextView tv_paipu_cardtype;
    TextView tv_game_blind;
    //TextView tv_game_member;
    TextView tv_game_checkin_fee;
    TextView tv_game_earnings;
    //
    RelativeLayout rl_game_mode;
    ImageView iv_game_mode;

    public CirclePaipuView(Context context) {
        super(context);
        init(context);
    }

    public CirclePaipuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CirclePaipuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.layout_circle_paipu_item, null);
        initView();
        addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setBackground(int resId) {
        ll_circle_paipu.setBackgroundResource(resId);
    }

    private void initView() {
        ll_circle_paipu = (LinearLayout) view.findViewById(R.id.ll_circle_paipu);
        tv_paipu_cardtype = (TextView) view.findViewById(R.id.tv_paipu_cardtype);
        tv_game_blind = (TextView) view.findViewById(R.id.tv_game_blind);
        tv_game_checkin_fee = (TextView) view.findViewById(R.id.tv_game_checkin_fee);
        tv_game_earnings = (TextView) view.findViewById(R.id.tv_game_earnings);
        mHandCardView = (HandCardView) view.findViewById(R.id.mHandCardView);
        mCardTypeView = (HandCardView) view.findViewById(R.id.mCardTypeView);
        //
        rl_game_mode = (RelativeLayout) view.findViewById(R.id.rl_game_mode);
        iv_game_mode = (ImageView) view.findViewById(R.id.iv_game_mode);
    }

    public void setData(PaipuEntity paipuEntity) {
        GameEntity gameInfo = paipuEntity.gameEntity;
        if (gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL && gameInfo.gameConfig instanceof GameNormalConfig) {
            //普通模式
            GameNormalConfig gameConfig = (GameNormalConfig) gameInfo.gameConfig;
            tv_game_blind.setText(GameConstants.getGameBlindsShow(gameConfig.blindType));
            //tv_game_blind.setVisibility(VISIBLE);

            //tv_game_checkin_fee.setVisibility(GONE);
            rl_game_mode.setVisibility(GONE);
        } else if (gameInfo.gameMode == GameConstants.GAME_MODE_SNG && gameInfo.gameConfig instanceof GameSngConfigEntity) {
            //SNG模式
            GameSngConfigEntity gameConfig = (GameSngConfigEntity) gameInfo.gameConfig;
            //tv_game_blind.setVisibility(GONE);
            tv_game_blind.setText(""+gameConfig.getChips());
            //
            iv_game_mode.setImageResource(R.mipmap.icon_control_sng);
        } else if (gameInfo.gameMode == GameConstants.GAME_MODE_MTT && gameInfo.gameConfig instanceof GameMttConfig) {
            //MTT
            GameMttConfig gameMttConfig = (GameMttConfig) gameInfo.gameConfig;
            //tv_game_blind.setVisibility(GONE);
            tv_game_blind.setText(""+gameMttConfig.matchChips);
            //
            iv_game_mode.setImageResource(R.mipmap.icon_control_mtt);
        } else if (gameInfo.gameMode == GameConstants.GAME_MODE_MT_SNG && gameInfo.gameConfig instanceof GameMtSngConfig) {
            //MT-SNG
            GameMtSngConfig gameMtSngConfig = (GameMtSngConfig) gameInfo.gameConfig;
            //tv_game_blind.setVisibility(GONE);
            tv_game_blind.setText(""+gameMtSngConfig.matchChips);
            //
            iv_game_mode.setImageResource(R.mipmap.icon_control_mtsng);
        }
        //
        int winChip = paipuEntity.winChip;
        RecordHelper.setRecordGainView(tv_game_earnings, winChip);
        int cardType = paipuEntity.cardType;
        tv_paipu_cardtype.setText(PaipuConstants.getCardCategoriesDesc(context, cardType));
        tv_paipu_cardtype.setTextColor(PaipuConstants.getCardTypeTextColor(context, cardType));
        LogUtil.i(TAG, "paipuEntity :" + (paipuEntity.poolCards == null ? 0 : paipuEntity.poolCards.size()));
        //展示类型
        if (cardType == 0 || paipuEntity.poolCards == null || paipuEntity.poolCards.size() < 3) {
            //只显示2张牌
            mHandCardView.setVisibility(VISIBLE);
            mCardTypeView.setVisibility(GONE);
            mHandCardView.setHandCard(paipuEntity.handCards);
        } else {
            mHandCardView.setVisibility(GONE);
            mCardTypeView.setVisibility(VISIBLE);
            mCardTypeView.setHandCard(paipuEntity.cardTypeCards);
        }
    }
}
