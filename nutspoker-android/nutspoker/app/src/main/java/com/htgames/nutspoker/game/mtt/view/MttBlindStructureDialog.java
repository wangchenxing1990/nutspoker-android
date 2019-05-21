package com.htgames.nutspoker.game.mtt.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.config.GameConfigData;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.game.bean.BlindStuctureEntity;
import com.htgames.nutspoker.game.mtt.adapter.MttBlindsStructureAdapter;
import com.netease.nim.uikit.common.util.BaseTools;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

import java.util.ArrayList;

/**
 * 盲注结构(MTT)
 */
public class MttBlindStructureDialog extends Dialog {
    private final static String TAG = "ShareView";
    private View rootView;
    Context context;
    GridView gv_blinds_stucture;
    MttBlindsStructureAdapter mBlindsStructureAdapter;
    ArrayList<BlindStuctureEntity> blindStuctureList = new ArrayList<>();
    ImageView btn_close;
    TextView tv_create_mtt_blinds_relation_title;
    TextView tv_blinds_stucture_blinds;
    TextView tv_blinds_stucture_level;
    TextView tv_blinds_stucture_ante;
    LinearLayout ll_mtt_blinds_stucture_desc;
    TextView tv_game_match_blinds_rebuy_desc;
    TextView tv_game_match_blinds_addon_desc;
    TextView tv_game_match_blinds_termination_join_desc;
    int gameMode = GameConstants.GAME_MODE_MTT;
    boolean isMatchRoom = false;
    private GameEntity mGameInfo;

    public MttBlindStructureDialog(Context context , GameEntity gameEntity , boolean isMatchRoom) {
        super(context, R.style.MyDialog);
        this.gameMode = gameEntity.gameMode;
        mGameInfo = gameEntity;
        this.isMatchRoom = isMatchRoom;
        init(context);
        if (isMatchRoom) {
//            tv_create_mtt_blinds_relation_title.setTextColor(context.getResources().getColor(R.color.mtt_text_common_color));
//            tv_blinds_stucture_blinds.setTextColor(context.getResources().getColor(R.color.mtt_text_common_color));
//            tv_blinds_stucture_level.setTextColor(context.getResources().getColor(R.color.mtt_text_common_color));
//            tv_blinds_stucture_ante.setTextColor(context.getResources().getColor(R.color.mtt_text_common_color));
        }
    }

    /**
     * 根据盲注结构表类型和起始盲注(小盲)设置dialog的数据源
     * @param mtt_sblinds_mode 盲注结构表类型  1普通    2快速
     * @param startSmallBlind 起始盲注  （小盲）
     */
    public void setData(int mtt_sblinds_mode, int startSmallBlind) {
        blindStuctureList.clear();
        if (mGameInfo.play_mode == GameConstants.PLAY_MODE_TEXAS_HOLDEM) {
            if (mtt_sblinds_mode == 1) {
                int startIndex = 0;
                for (int i = 0; i < GameConfigData.mtt_sblins.length; i++) {
                    if (GameConfigData.mtt_sblinds_multiple[0] * GameConfigData.mtt_sblins[i] >= startSmallBlind) {
                        startIndex = i;
                        break;
                    }
                }
                for (int i = 0; i < GameConfigData.mtt_ante_multiple.length; i++) {
                    int currentBlinnd = (int) (GameConfigData.mtt_sblinds_multiple[i] * GameConfigData.mtt_sblins[startIndex]);
                    int currentAnte = (int) (GameConfigData.mtt_ante_multiple[i] * GameConfigData.mtt_sblins[startIndex]);//默认是乘以mtt_sblins[0]，拖动拖动条时乘数会变
                    blindStuctureList.add(new BlindStuctureEntity(i + 1, currentBlinnd, currentAnte));
                }
            } else if (mtt_sblinds_mode == 2) {
                int startIndex = 0;
                for (int i = 0; i < GameConfigData.mtt_sblins_quick.length; i++) {
                    if (GameConfigData.mtt_sblinds_multiple_quick[0] * GameConfigData.mtt_sblins_quick[i] >= startSmallBlind) {
                        startIndex = i;
                        break;
                    }
                }
                for (int i = 0; i < GameConfigData.mtt_ante_multiple_quick.length; i++) {
                    int currentBlinnd = (int) (GameConfigData.mtt_sblinds_multiple_quick[i] * GameConfigData.mtt_sblins_quick[startIndex]);
                    int currentAnte = (int) (GameConfigData.mtt_ante_multiple_quick[i] * GameConfigData.mtt_sblins_quick[startIndex]);//默认是乘以mtt_sblins[0]，拖动拖动条时乘数会变
                    blindStuctureList.add(new BlindStuctureEntity(i + 1, currentBlinnd, currentAnte));
                }
            }
        } else if (mGameInfo.play_mode == GameConstants.PLAY_MODE_OMAHA) {
            if (mtt_sblinds_mode == 1) {
                int startIndex = 0;
                for (int i = 0; i < GameConfigData.omaha_mtt_sblins.length; i++) {
                    if (GameConfigData.omaha_mtt_sblinds_multiple[0] * GameConfigData.omaha_mtt_sblins[i] >= startSmallBlind) {
                        startIndex = i;
                        break;
                    }
                }
                for (int i = 0; i < GameConfigData.omaha_mtt_ante_multiple.length; i++) {
                    int currentBlinnd = (int) (GameConfigData.omaha_mtt_sblinds_multiple[i] * GameConfigData.omaha_mtt_sblins[startIndex]);
                    int currentAnte = (int) (GameConfigData.omaha_mtt_ante_multiple[i] * GameConfigData.omaha_mtt_sblins[startIndex]);//默认是乘以mtt_sblins[0]，拖动拖动条时乘数会变
                    blindStuctureList.add(new BlindStuctureEntity(i + 1, currentBlinnd, currentAnte));
                }
            } else if (mtt_sblinds_mode == 2) {
                int startIndex = 0;
                for (int i = 0; i < GameConfigData.omaha_mtt_sblins_quick.length; i++) {
                    if (GameConfigData.omaha_mtt_sblinds_multiple_quick[0] * GameConfigData.omaha_mtt_sblins_quick[i] >= startSmallBlind) {
                        startIndex = i;
                        break;
                    }
                }
                for (int i = 0; i < GameConfigData.omaha_mtt_ante_multiple_quick.length; i++) {
                    int currentBlinnd = (int) (GameConfigData.omaha_mtt_sblinds_multiple_quick[i] * GameConfigData.omaha_mtt_sblins_quick[startIndex]);
                    int currentAnte = (int) (GameConfigData.omaha_mtt_ante_multiple_quick[i] * GameConfigData.omaha_mtt_sblins_quick[startIndex]);//默认是乘以mtt_sblins[0]，拖动拖动条时乘数会变
                    blindStuctureList.add(new BlindStuctureEntity(i + 1, currentBlinnd, currentAnte));
                }
            }
        }
        mBlindsStructureAdapter.notifyDataSetChanged();
    }

    public void setMttBlindLevel(int matchLevel ,boolean isRebuyMode ,boolean isAddonMode) {
        if (matchLevel > 0) {
            mBlindsStructureAdapter.setMatchRoomInfo(matchLevel, isRebuyMode, isAddonMode);
            ll_mtt_blinds_stucture_desc.setVisibility(View.VISIBLE);
            //
            if(isRebuyMode) {
                tv_game_match_blinds_rebuy_desc.setVisibility(View.VISIBLE);
                tv_game_match_blinds_rebuy_desc.setText(context.getString(R.string.game_match_blinds_rebuy_desc, matchLevel - 1));
            }else{
                tv_game_match_blinds_rebuy_desc.setVisibility(View.GONE);
            }
            if(isAddonMode) {
                tv_game_match_blinds_addon_desc.setVisibility(View.VISIBLE);
                tv_game_match_blinds_addon_desc.setText(context.getString(R.string.game_match_blinds_addon_desc, matchLevel));
            } else {
                tv_game_match_blinds_addon_desc.setVisibility(View.GONE);
            }
//            tv_game_match_blinds_termination_join_desc.setText(SpannableUtils.getSpannableString(context.getString(R.string.match_room_blind_level), context.getString(R.string.match_room_blind_level_desc, matchLevel)));
            tv_game_match_blinds_termination_join_desc.setText("终止报名：" + "第" + (matchLevel) + "盲注级别");
        } else {
            ll_mtt_blinds_stucture_desc.setVisibility(View.GONE);
        }
    }

    public void init(Context context) {
        this.context = context;
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = BaseTools.getWindowHeigh(context) * 2 / 3;
//        setAnimationStyle(R.style.PopupAnimation);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        //
        Window win = this.getWindow();
        win.setGravity(Gravity.CENTER);
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = width;   //宽度填满
        lp.height = height;  //高度自适应
        win.setAttributes(lp);
        //
        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_mtt_blinds_structure_view, null);
        int padding = ScreenUtil.dp2px(context, 20);//setPadding(padding, 0, padding, 0)
        initView(context);
        setContentView(rootView);
        ((FrameLayout.LayoutParams) rootView.getLayoutParams()).setMargins(padding, 0, padding, 0);
    }

    private void initView(Context context) {
        ll_mtt_blinds_stucture_desc = (LinearLayout) rootView.findViewById(R.id.ll_mtt_blinds_stucture_desc);
        tv_game_match_blinds_rebuy_desc = (TextView) rootView.findViewById(R.id.tv_game_match_blinds_rebuy_desc);
        tv_game_match_blinds_addon_desc = (TextView) rootView.findViewById(R.id.tv_game_match_blinds_addon_desc);
        tv_game_match_blinds_termination_join_desc = (TextView) rootView.findViewById(R.id.tv_game_match_blinds_termination_join_desc);
        gv_blinds_stucture = (GridView) rootView.findViewById(R.id.gv_blinds_stucture);
        tv_create_mtt_blinds_relation_title = (TextView) rootView.findViewById(R.id.tv_create_mtt_blinds_relation_title);
        tv_blinds_stucture_blinds = (TextView) rootView.findViewById(R.id.tv_blinds_stucture_blinds);
        tv_blinds_stucture_level = (TextView) rootView.findViewById(R.id.tv_blinds_stucture_level);
        tv_blinds_stucture_ante = (TextView) rootView.findViewById(R.id.tv_blinds_stucture_ante);
        btn_close = (ImageView) rootView.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MttBlindStructureDialog.this.dismiss();
            }
        });
        //
        if(gameMode == GameConstants.GAME_MODE_MTT) {
            tv_create_mtt_blinds_relation_title.setText(R.string.game_create_mtt_blinds_relation);
        } else if(gameMode == GameConstants.GAME_MODE_MT_SNG) {
            tv_create_mtt_blinds_relation_title.setText(R.string.game_create_mtsng_blinds_relation);
        }
        //
        mBlindsStructureAdapter = new MttBlindsStructureAdapter(context, blindStuctureList ,isMatchRoom);
        gv_blinds_stucture.setAdapter(mBlindsStructureAdapter);
    }

}
