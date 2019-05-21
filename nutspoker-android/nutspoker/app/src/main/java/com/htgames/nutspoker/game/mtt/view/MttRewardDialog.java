package com.htgames.nutspoker.game.mtt.view;

import android.app.Dialog;
import android.content.Context;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.game.match.adapter.MatchRewardAdapter;
import com.htgames.nutspoker.game.match.bean.MatchStatusEntity;
import com.htgames.nutspoker.game.match.reward.RewardAllot;
import com.netease.nim.uikit.bean.BaseMttConfig;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.common.util.BaseTools;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.constants.GameConstants;

/**
 * Created by 周智慧 on 17/2/28.
 */

public class MttRewardDialog extends Dialog implements View.OnClickListener {
    private Context context;
    GridView gv_blinds_stucture;
    TextView tv_create_mtt_blinds_relation_title;
    MatchRewardAdapter mBlindsStructureAdapter;
    //标题
    TextView tv_blinds_stucture_level;
    TextView tv_blinds_stucture_blinds;
    TextView tv_blinds_stucture_ante;
    RewardAllot mRewardAllot;
    GameEntity gameInfo;
    TextView tv_mtt_all_reward;
    TextView tv_mtt_all_reward_hunter;
    TextView tv_mtt_reward_person_count;
    private View rootView;
    public MttRewardDialog(Context context, GameEntity gameInfo) {
        super(context, R.style.MyDialog);
        mRewardAllot = RewardAllot.getInstance();
        this.gameInfo = gameInfo;
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        int padding = ScreenUtil.dp2px(context, 20);//setPadding(padding, 0, padding, 0)
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = BaseTools.getWindowHeigh(context) * 2 / 3;
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        Window win = this.getWindow();
        win.setGravity(Gravity.CENTER);
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = width;   //宽度填满
        lp.height = height;//WindowManager.LayoutParams.WRAP_CONTENT;  //高度自适应
        win.setAttributes(lp);
        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_mtt_blinds_structure_view, null);
        initView(context);
        setContentView(rootView);
        ((FrameLayout.LayoutParams) rootView.getLayoutParams()).setMargins(padding, 0, padding, 0);
    }

    private void initView(Context context) {
        rootView.findViewById(R.id.match_reward_top_info).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.ll_mtt_blinds_stucture_desc).setVisibility(View.GONE);
        gv_blinds_stucture = (GridView) rootView.findViewById(R.id.gv_blinds_stucture);
        tv_create_mtt_blinds_relation_title = (TextView) rootView.findViewById(R.id.tv_create_mtt_blinds_relation_title);
        tv_create_mtt_blinds_relation_title.setText(R.string.mtt_match_reward_table);
        tv_blinds_stucture_level = (TextView) rootView.findViewById(R.id.tv_blinds_stucture_level);
        tv_blinds_stucture_level.setText(R.string.game_match_rank);
        tv_blinds_stucture_blinds = (TextView) rootView.findViewById(R.id.tv_blinds_stucture_blinds);
        tv_blinds_stucture_blinds.setText(R.string.game_match_proportion);
        tv_blinds_stucture_ante = (TextView) rootView.findViewById(R.id.tv_blinds_stucture_ante);
        tv_blinds_stucture_ante.setText(R.string.game_match_reward);
        tv_mtt_all_reward = (TextView) rootView.findViewById(R.id.tv_mtt_all_reward);
        tv_mtt_all_reward_hunter = (TextView) rootView.findViewById(R.id.tv_mtt_all_reward_hunter);
        tv_mtt_all_reward_hunter.setVisibility(gameInfo != null && gameInfo.gameConfig instanceof BaseMttConfig && ((BaseMttConfig) gameInfo.gameConfig).ko_mode != 0 ? View.VISIBLE : View.GONE);
        tv_mtt_reward_person_count = (TextView) rootView.findViewById(R.id.tv_mtt_reward_person_count);
        rootView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MttRewardDialog.this.dismiss();
            }
        });
        mBlindsStructureAdapter = new MatchRewardAdapter(context, mRewardAllot, 0, 0);
        gv_blinds_stucture.setAdapter(mBlindsStructureAdapter);
    }

    public void updateData(MatchStatusEntity matchStatusEntity, String match_state_str) {
        if (matchStatusEntity == null) {
            return;
        }
        int checkInCount = matchStatusEntity.checkInPlayer;
        float ko_reward_rate = (gameInfo != null && gameInfo.gameConfig instanceof BaseMttConfig && ((BaseMttConfig) gameInfo.gameConfig).ko_mode != 0) ? ((BaseMttConfig) gameInfo.gameConfig).ko_reward_rate / 100.0f : 0;
        int original_ko_reward_rate = (gameInfo != null && gameInfo.gameConfig instanceof BaseMttConfig && ((BaseMttConfig) gameInfo.gameConfig).ko_mode != 0) ? ((BaseMttConfig) gameInfo.gameConfig).ko_reward_rate : 0;
        int allReward = (int) (matchStatusEntity.allReward * (100.0f - original_ko_reward_rate)) / 100;
        String allRewardStr = GameConstants.getGameChipsShow(allReward);//超过1万显示k
        String allRewardHunterStr = GameConstants.getGameChipsShow(matchStatusEntity.allReward - allReward);//超过1万显示k
        tv_mtt_all_reward.setText(context.getResources().getString(R.string.game_mtt_all_reward, allRewardStr));

        if (gameInfo != null && gameInfo.gameConfig instanceof BaseMttConfig && ((BaseMttConfig) gameInfo.gameConfig).ko_mode != 0) {//表示是猎人赛
            tv_mtt_all_reward_hunter.setText(context.getResources().getString(R.string.game_mtt_all_reward_hunter, allRewardHunterStr));
        }

        int rewardPlayerCount = mRewardAllot.getRewardPlayerCount(checkInCount);
        if (gameInfo != null && gameInfo.match_type != GameConstants.MATCH_TYPE_NORMAL) {
            tv_mtt_reward_person_count.setText(ChessApp.sAppContext.getResources().getString(R.string.game_mtt_checkin_count, matchStatusEntity.checkInPlayer));
        } else {
            tv_mtt_reward_person_count.setText(context.getResources().getString(R.string.game_mtt_reward_person_count, rewardPlayerCount));
        }
        mBlindsStructureAdapter.updateData(checkInCount, allReward);
    }

    @Override
    public void onClick(View v) {
    }
}
