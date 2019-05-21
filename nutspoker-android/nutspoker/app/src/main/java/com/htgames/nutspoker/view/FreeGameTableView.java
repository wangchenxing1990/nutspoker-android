package com.htgames.nutspoker.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.config.GameConfigData;
import com.htgames.nutspoker.game.match.activity.FreeRoomAC;
import com.netease.nim.uikit.AnimUtil;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameNormalConfig;
import com.netease.nim.uikit.bean.GameSngConfigEntity;
import com.netease.nim.uikit.bean.PineappleConfig;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nimlib.sdk.team.model.Team;

import static com.htgames.nutspoker.config.GameConfigData.pineappleIconIdsReady;

/**
 * Created by 周智慧 on 17/3/3.
 */

public class FreeGameTableView extends RelativeLayout implements View.OnClickListener {
    GameEntity gameInfo;
    Context mContext;
    public boolean omahaAnimationDone = false;
    public FreeGameTableView(Context context) {
        this(context, null);
    }

    public FreeGameTableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FreeGameTableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FreeGameTableView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.layout_game_table_free, this, true);
        mContext = context;
        setPadding(0, ScreenUtil.dp2px(context, 10), 0, 0);
        initView();
    }

    TextView tv_game_share_code;
    //普通局
    View free_normal_game_container;
    TextView free_normal_blinds;
    TextView tv_discovery_match_duration;
    TextView tv_gamedesk_insurance;
    View gamedesk_insurance_container;
    TextView tv_gamedesk_ante;
    View gamedesk_ante_container;
    TextView tv_gamedesk_ip_and_gps;
    TextView tv_gamedesk_ip_and_gps_sng;
    //sng局
    View ll_sng_rule;
    View free_sng_game_container;
    TextView tv_discovery_member;
    TextView free_game_sng_chips;
    TextView tv_discovery_match_checkin_fee;
    TextView free_sng_sng_blind_time;
    View game_code_container;
    View game_code_share;
    ImageView iv_omaha_icon;
    //大菠萝
    View pineapple_container;
    private void initView() {
        tv_game_share_code = (TextView) findViewById(R.id.tv_game_share_code);
        ll_sng_rule = findViewById(R.id.ll_sng_rule);
        //普通局
        free_normal_game_container = findViewById(R.id.free_normal_game_container);
        free_normal_blinds = (TextView) findViewById(R.id.free_normal_blinds);
        tv_discovery_match_duration = (TextView) findViewById(R.id.tv_discovery_match_duration);
        tv_gamedesk_insurance = (TextView) findViewById(R.id.tv_gamedesk_insurance);
        gamedesk_insurance_container = findViewById(R.id.gamedesk_insurance_container);
        tv_gamedesk_ante = (TextView) findViewById(R.id.tv_gamedesk_ante);
        gamedesk_ante_container = findViewById(R.id.gamedesk_ante_container);
        tv_gamedesk_ip_and_gps = (TextView) findViewById(R.id.tv_gamedesk_ip_and_gps);
        tv_gamedesk_ip_and_gps_sng = (TextView) findViewById(R.id.tv_gamedesk_ip_and_gps_sng);
        pineapple_container = findViewById(R.id.pineapple_container);
        //sng局
        free_sng_game_container = findViewById(R.id.free_sng_game_container);
        tv_discovery_member = (TextView) findViewById(R.id.tv_discovery_member);
        free_game_sng_chips = (TextView) findViewById(R.id.free_game_sng_chips);
        tv_discovery_match_checkin_fee = (TextView) findViewById(R.id.tv_discovery_match_checkin_fee);
        free_sng_sng_blind_time = (TextView) findViewById(R.id.free_sng_sng_blind_time);
        game_code_container = findViewById(R.id.game_code_container);
        game_code_container.setOnClickListener(this);
        game_code_share = findViewById(R.id.game_code_share);
        iv_omaha_icon = (ImageView) findViewById(R.id.iv_omaha_icon);
        normalDrawable = new GradientDrawable();
        float radius = ScreenUtil.dp2px(getContext(), 145) / 2f;
        normalDrawable.setCornerRadii(new float[]{0, 0,
                0, 0,
                radius, radius,
                radius, radius});
        normalDrawable.setColor(getContext().getResources().getColor(R.color.bg_club_game_bottom));
    }

    public void setGameInfo(GameEntity gameInfo) {
        if (gameInfo == null) {
            return;
        }
        this.gameInfo = gameInfo;
        tv_game_share_code.setText(gameInfo.joinCode + "");
        game_code_share.setVisibility(VISIBLE);
        game_code_container.setBackgroundDrawable(getContext().getResources().getDrawable(R.mipmap.bg_invite_share_code));
        pineapple_container.setVisibility(gameInfo.play_mode < GameConstants.PLAY_MODE_PINEAPPLE ? GONE : VISIBLE);
        if (gameInfo.play_mode < GameConstants.PLAY_MODE_PINEAPPLE) {
            boolean isNormalGame = gameInfo.gameMode == 0;
            setBackgroundColor(mContext.getResources().getColor(isNormalGame ? R.color.bg_normal : R.color.bg_sng));
            ll_sng_rule.setVisibility(isNormalGame ? GONE : VISIBLE);
            free_normal_game_container.setVisibility(isNormalGame ? VISIBLE : GONE);
            free_sng_game_container.setVisibility(isNormalGame ? GONE : VISIBLE);
            if (isNormalGame) {
                GameNormalConfig gameNormalConfig = (GameNormalConfig) gameInfo.gameConfig;
                free_normal_blinds.setText("" + gameNormalConfig.blindType + "/" + (gameNormalConfig.blindType * 2));
                tv_discovery_match_duration.setText(GameConstants.getGameDurationShow(gameNormalConfig.timeType));
                boolean isInsurance = (gameNormalConfig.tiltMode == GameConstants.GAME_MODE_INSURANCE_NOT ? false : true);
                int ante = GameConstants.getGameAnte(gameNormalConfig);
                gamedesk_insurance_container.setVisibility(isInsurance ? VISIBLE : GONE);
                gamedesk_ante_container.setVisibility(ante != GameConstants.ANTE_TYPE_0 ? VISIBLE : GONE);
                tv_gamedesk_ante.setText("Ante: " + ante);
                tv_gamedesk_ip_and_gps.setVisibility(gameNormalConfig.check_ip == 0 && gameNormalConfig.check_gps == 0 ? GONE : VISIBLE);
                if (gameNormalConfig.check_ip != 0 && gameNormalConfig.check_gps != 0) {
                    tv_gamedesk_ip_and_gps.setText("IP限制      GPS限制");
                } else if (gameNormalConfig.check_ip != 0) {
                    tv_gamedesk_ip_and_gps.setText("IP限制");
                } else if (gameNormalConfig.check_gps != 0) {
                    tv_gamedesk_ip_and_gps.setText("GPS限制");
                }
            } else if (gameInfo.gameMode == 1) {
                GameSngConfigEntity gameSngConfigEntity = (GameSngConfigEntity) gameInfo.gameConfig;
                tv_discovery_member.setText(gameSngConfigEntity.getPlayer() + "");
                free_game_sng_chips.setText("" + gameSngConfigEntity.getChips());
                int checkinFee = gameSngConfigEntity.getCheckInFee();
                int serviceFee = (int) (checkinFee / GameConfigData.SNGServiceRate);
                tv_discovery_match_checkin_fee.setText(checkinFee + "+" + serviceFee);
                free_sng_sng_blind_time.setText(GameConstants.getGameSngDurationMinutesShow(gameSngConfigEntity.getDuration()));
                tv_gamedesk_ip_and_gps_sng.setVisibility(gameSngConfigEntity.check_ip == 0 && gameSngConfigEntity.check_gps == 0 ? GONE : VISIBLE);
                if (gameSngConfigEntity.check_ip != 0 && gameSngConfigEntity.check_gps != 0) {
                    tv_gamedesk_ip_and_gps_sng.setText("IP限制      GPS限制");
                } else if (gameSngConfigEntity.check_ip != 0) {
                    tv_gamedesk_ip_and_gps_sng.setText("IP限制");
                } else if (gameSngConfigEntity.check_gps != 0) {
                    tv_gamedesk_ip_and_gps_sng.setText("GPS限制");
                }
            }
        } else if (gameInfo.play_mode == GameConstants.PLAY_MODE_PINEAPPLE && gameInfo.gameConfig instanceof PineappleConfig) {
            setBackgroundColor(mContext.getResources().getColor(R.color.bg_pineapple));
            TextView tv_pineapple_ante_num = (TextView) findViewById(R.id.tv_pineapple_ante_num);
            TextView pineapple_chips = (TextView) findViewById(R.id.pineapple_chips);
            TextView tv_pineapple_duration = (TextView) findViewById(R.id.tv_pineapple_duration);
            TextView tv_pineapple_ip_and_gps_sng = (TextView) findViewById(R.id.tv_pineapple_ip_and_gps_sng);
            PineappleConfig pineappleConfig = (PineappleConfig) gameInfo.gameConfig;
            tv_pineapple_ante_num.setText(pineappleConfig.getAnte() + "");
            pineapple_chips.setText(pineappleConfig.getChips() + "");
            tv_pineapple_duration.setText(GameConstants.getGameDurationShow(pineappleConfig.getDuration()));
            tv_pineapple_ip_and_gps_sng.setVisibility(pineappleConfig.getIp_limit() == 0 && pineappleConfig.getGps_limit() == 0 ? GONE : VISIBLE);
            if (pineappleConfig.getIp_limit() != 0 && pineappleConfig.getGps_limit() != 0) {
                tv_pineapple_ip_and_gps_sng.setText("IP限制      GPS限制");
            } else if (pineappleConfig.getIp_limit() != 0) {
                tv_pineapple_ip_and_gps_sng.setText("IP限制");
            } else if (pineappleConfig.getGps_limit() != 0) {
                tv_pineapple_ip_and_gps_sng.setText("GPS限制");
            }
            final ImageView iv_pineapple_icon = (ImageView) findViewById(R.id.iv_pineapple_icon);
            iv_pineapple_icon.setImageResource(pineappleIconIdsReady[pineappleConfig.getPlay_type()]);
            iv_pineapple_icon.setVisibility(VISIBLE);
            if (omahaAnimationDone) {
                return;
            }
            omahaAnimationDone = true;
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    AnimUtil.translateX(iv_pineapple_icon, -ScreenUtil.dp2px(getContext(), 82), ScreenUtil.dp2px(getContext(), 20), 400);//图片的宽度是42dp,多移动20dp
                }
            }, 100);
        }
        if (gameInfo.joinCode.length() > 6) {
            String teamId = gameInfo.joinCode.substring(6, gameInfo.joinCode.length());
            tv_game_share_code.setPadding(ScreenUtil.dp2px(mContext, 12), ScreenUtil.dp2px(mContext, 5), ScreenUtil.dp2px(mContext, 12), 0);
            game_code_container.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.bg_invite_share_code));
            game_code_share.setVisibility(GONE);
            game_code_container.setOnClickListener(null);
            if (TeamDataCache.getInstance().getTeamById(teamId) != null) {
                Team team = TeamDataCache.getInstance().getTeamById(teamId);
                String clubName = team.getName();
                String extServer = team.getExtServer();
                tv_game_share_code.setText(team.getName());
            } else {
                TeamDataCache.getInstance().fetchTeamById(teamId, new SimpleCallback<Team>() {
                    @Override
                    public void onResult(boolean success, Team result) {
                        if (success && result != null) {
                            tv_game_share_code.setText(result.getName());
                        } else {
                        }
                    }
                });
            }
        }
        if (gameInfo.play_mode == GameConstants.PLAY_MODE_TEXAS_HOLDEM) {
            iv_omaha_icon.setVisibility(View.GONE);
        } else if (gameInfo.play_mode == GameConstants.PLAY_MODE_OMAHA) {
            if (omahaAnimationDone) {
                return;
            }
            omahaAnimationDone = true;
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    iv_omaha_icon.setVisibility(View.VISIBLE);
                    AnimUtil.translateX(iv_omaha_icon, -ScreenUtil.dp2px(getContext(), 62), 0, 400);//图片的宽度是40dp
                }
            }, 100);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.game_code_container:
                ((FreeRoomAC) mContext).postShare();
                break;
            case R.id.iv_sng_rule:
//                showRulesDialog(iv_sng_rule);// TODO: 16/12/7暂时不做
                break;
        }
    }

    GradientDrawable normalDrawable;
    float starY = -ScreenUtil.dp2px(ChessApp.sAppContext, 130);
    float height = -ScreenUtil.dp2px(ChessApp.sAppContext, 130);
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        if (starY > 0) {
            starY = 0;
        }
        float progress = (height + starY) / height;
        canvas.scale(1, progress);
        normalDrawable.setBounds(0, (int) starY, getWidth(), (int) (starY + ScreenUtil.dp2px(getContext(), 130)));
        normalDrawable.draw(canvas);
        starY += 15;
        canvas.restore();

        if (starY < 0) {
            invalidate();
        }
    }
}
