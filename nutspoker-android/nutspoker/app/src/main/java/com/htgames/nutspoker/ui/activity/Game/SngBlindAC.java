package com.htgames.nutspoker.ui.activity.Game;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.config.GameConfigData;
import com.htgames.nutspoker.game.bean.BlindStuctureEntity;
import com.htgames.nutspoker.game.mtt.adapter.BlindAdapter;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.nav.UIUrl;
import com.netease.nim.uikit.nav.UrlConstants;

import java.util.ArrayList;

/**
 * Created by 周智慧 on 17/1/12.
 */
@UIUrl(urls = {UrlConstants.SNG_BLIND_STRUCTURE})
public class SngBlindAC extends BaseActivity {
    public int mPlayMode;//游戏模式，0="德州扑克"或者1="奥马哈"
    RecyclerView mRecycView;
    BlindAdapter mBlindsStructureAdapter;
    ArrayList<BlindStuctureEntity> blindStuctureList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initData();
        mBlindsStructureAdapter = new BlindAdapter(this, blindStuctureList);
        mBlindsStructureAdapter.setData(blindStuctureList);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blind_structure);
        ((TextView)findViewById(R.id.tv_head_title)).setText(R.string.sng_blinds_title);
        initRecyclerView();
        mRecycView = (RecyclerView) findViewById(R.id.blinds_recyclerview);
        mRecycView.setAdapter(mBlindsStructureAdapter);
    }

    private void initData() {
        mPlayMode = getIntent().getIntExtra(GameConstants.KEY_PLAY_MODE, GameConstants.PLAY_MODE_TEXAS_HOLDEM);
        if (mPlayMode == GameConstants.PLAY_MODE_TEXAS_HOLDEM) {
            ///*sng_ante_multiple   sng_sblinds_multiple  sng_sblins   sng盲注结构表  1普通*/
            for (int i = 0; i < GameConfigData.sng_ante_multiple.length; i++) {
                int currentBlinnd = (int) (GameConfigData.sng_sblinds_multiple[i] * GameConfigData.sng_sblins[0]);
                int currentAnte = (int) (GameConfigData.sng_ante_multiple[i] * GameConfigData.sng_sblins[0]);//默认是乘以sng_sblins[0]，拖动拖动条时乘数会变
                blindStuctureList.add(new BlindStuctureEntity(i + 1, currentBlinnd, currentAnte));
            }
        } else if (mPlayMode == GameConstants.PLAY_MODE_OMAHA) {
            ///*sng_ante_multiple   sng_sblinds_multiple  sng_sblins   sng盲注结构表  1普通*/
            for (int i = 0; i < GameConfigData.omaha_sng_ante_multiple.length; i++) {
                int currentBlinnd = (int) (GameConfigData.omaha_sng_sblinds_multiple[i] * GameConfigData.omaha_sng_sblins[0]);
                int currentAnte = (int) (GameConfigData.omaha_sng_ante_multiple[i] * GameConfigData.omaha_sng_sblins[0]);//默认是乘以sng_sblins[0]，拖动拖动条时乘数会变
                blindStuctureList.add(new BlindStuctureEntity(i + 1, currentBlinnd, currentAnte));
            }
        }
    }

    private void initRecyclerView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecycView = (RecyclerView) findViewById(R.id.blinds_recyclerview);
        mRecycView.setLayoutManager(layoutManager);
        RecyclerView.ItemDecoration decorationThin = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
                    outRect.set(0, 0, 0, 0);
                } else {
                    outRect.set(0, 0, 0, 0);
                }
            }
        };
        mRecycView.addItemDecoration(decorationThin);
        mRecycView.setItemAnimator(new DefaultItemAnimator());
        mRecycView.setAdapter(mBlindsStructureAdapter);
    }

    private int getOrientation(RecyclerView parent) {
        LinearLayoutManager layoutManager;
        try {
            layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        } catch (ClassCastException e) {
            throw new IllegalStateException("DividerDecoration can only be used with a " + "LinearLayoutManager.", e);
        }
        return layoutManager.getOrientation();
    }
}
