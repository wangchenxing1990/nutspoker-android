package com.htgames.nutspoker.ui.activity.Game;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.config.GameConfigData;
import com.htgames.nutspoker.game.bean.BlindStuctureEntity;
import com.htgames.nutspoker.game.mtt.adapter.MttBlindsStructureAdapter;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.ui.fragment.CreateMTTFrg;
import com.htgames.nutspoker.view.NodeSeekBar;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.nav.UIUrl;
import com.netease.nim.uikit.nav.UrlConstants;

import java.util.ArrayList;

/**
 * Created by 周智慧 on 17/1/6.
 */
@UIUrl(urls = {UrlConstants.MTT_BLIND_STRUCTURE})
public class MttBlindStructureAC extends BaseActivity implements View.OnClickListener {
    public int mPlayMode;//游戏模式，0="德州扑克"或者1="奥马哈"
    long time;
    TextView tv_normal_mtt_structure;
    TextView tv_quick_mtt_structure;
    ViewPager mtt_blinds_view_pager;
    NodeSeekBar mtt_start_blind_seekbar;
    TextView mtt_start_sblinds_num;
    //mtt普通表
    GridView gv_blinds_stucture;
    MttBlindsStructureAdapter mBlindsStructureAdapter;
    ArrayList<BlindStuctureEntity> blindStuctureList = new ArrayList<>();
    //mtt快速表
    GridView gv_blinds_stucture_quick;
    MttBlindsStructureAdapter mBlindsStructureAdapterQuick;
    ArrayList<BlindStuctureEntity> blindStuctureListQuick = new ArrayList<>();
    private int mode;
    private int mStartIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        time = System.currentTimeMillis();
        mBlindsStructureAdapter = new MttBlindsStructureAdapter(this, blindStuctureList ,false);
        mBlindsStructureAdapter.notifyDataSetChanged();
        gv_blinds_stucture = new GridView(this);
        gv_blinds_stucture.setNumColumns(1);
        gv_blinds_stucture.setAdapter(mBlindsStructureAdapter);

        mBlindsStructureAdapterQuick = new MttBlindsStructureAdapter(this, blindStuctureListQuick ,false);
        mBlindsStructureAdapterQuick.notifyDataSetChanged();
        gv_blinds_stucture_quick = new GridView(this);
        gv_blinds_stucture_quick.setNumColumns(1);
        gv_blinds_stucture_quick.setAdapter(mBlindsStructureAdapterQuick);
        LogUtil.i("before onCreate" + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        setContentView(R.layout.activity_mtt_structure);
        initViews();
        LogUtil.i("after onCreate" + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        initHead();
    }

    private void initViews() {
        BlindStructureAdapter adapter = new BlindStructureAdapter(this);
        mtt_start_blind_seekbar = (NodeSeekBar) findViewById(R.id.mtt_start_blind_seekbar);
        if (mode == 1) {
            mtt_start_blind_seekbar.setData(GameConfigData.mtt_sblins, false, false, R.mipmap.game_chip_thumb, "");
        } else if (mode == 2) {
            mtt_start_blind_seekbar.setData(GameConfigData.mtt_sblins_quick, false, false, R.mipmap.game_chip_thumb, "");
        }
        mtt_start_blind_seekbar.setProgress(mStartIndex);
        mtt_start_blind_seekbar.setOnNodeChangeListener(new NodeSeekBar.OnNodeChangeListener() {
            @Override
            public void onNodeChanged(int progress) {
                mStartIndex = progress;
                notifyDataSetChanged();
                setStartBlind();
            }
        });
        mtt_start_sblinds_num = (TextView) findViewById(R.id.mtt_structure_start_blind);
        setStartBlind();
        tv_normal_mtt_structure = (TextView) findViewById(R.id.tv_normal_mtt_structure);
        tv_normal_mtt_structure.setOnClickListener(this);
        tv_normal_mtt_structure.setSelected(mode == 1 ? true : false);
        tv_quick_mtt_structure = (TextView) findViewById(R.id.tv_quick_mtt_structure);
        tv_quick_mtt_structure.setOnClickListener(this);
        tv_quick_mtt_structure.setSelected(mode == 2 ? true : false);
        mtt_blinds_view_pager = (ViewPager) findViewById(R.id.mtt_blinds_view_pager);
        mtt_blinds_view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                mode = position + 1;
                MttBlindStructureAC.this.setSwipeBackEnable(position == 0 ? true : false);
                tv_normal_mtt_structure.setSelected(position == 0 ? true : false);
                tv_quick_mtt_structure.setSelected(position == 1 ? true : false);
                onTableChanged();
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mtt_blinds_view_pager.setAdapter(adapter);
        mtt_blinds_view_pager.setCurrentItem(mode - 1);
    }

    private void initHead() {
        ((TextView) findViewById(R.id.tv_head_title)).setText(R.string.game_create_mtt_blinds_relation);
        TextView tv_head_right = ((TextView) findViewById(R.id.tv_head_right));//右边"更多"
        tv_head_right.setVisibility(View.VISIBLE);
        tv_head_right.setText("完成");
        tv_head_right.setOnClickListener(this);
    }

    //设置起始盲注值
    private void setStartBlind() {
        int smallBlind = 0;
        if (mode == 1) {//普通表
            int startIndex = mStartIndex >= GameConfigData.mtt_sblins.length ? GameConfigData.mtt_sblins.length - 1 : mStartIndex;
            smallBlind = (int) (GameConfigData.mtt_sblinds_multiple[0] * GameConfigData.mtt_sblins[startIndex]);
        } else if (mode == 2) {//快速表
            int startIndex = mStartIndex >= GameConfigData.mtt_sblins_quick.length ? GameConfigData.mtt_sblins_quick.length - 1 : mStartIndex;
            smallBlind = (int) (GameConfigData.mtt_sblinds_multiple_quick[0] * GameConfigData.mtt_sblins_quick[startIndex]);
        }
        int bigBlind = smallBlind * 2;
        mtt_start_sblinds_num.setText(smallBlind + "/" + bigBlind);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i("after onResume" + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
    }

    private void initData() {
        mPlayMode = getIntent().getIntExtra(GameConstants.KEY_PLAY_MODE, GameConstants.PLAY_MODE_TEXAS_HOLDEM);
        blindStuctureList.clear();
        blindStuctureListQuick.clear();
        mode = CreateMTTFrg.mtt_sblinds_mode;
        mStartIndex = CreateMTTFrg.mtt_start_sblinds_index;
        if (mPlayMode == GameConstants.PLAY_MODE_TEXAS_HOLDEM) {
            //只初始化viewpager两个页面中的其中一个，如果两个页面都初始化activity启动很慢
            if (mode == 1) {
                for (int i = 0; i < GameConfigData.mtt_ante_multiple.length; i++) {
                    int startIndex = mStartIndex >= GameConfigData.mtt_sblins.length ? GameConfigData.mtt_sblins.length - 1 : mStartIndex;
                    int currentBlinnd = (int) (GameConfigData.mtt_sblinds_multiple[i] * GameConfigData.mtt_sblins[startIndex]);
                    int currentAnte = (int) (GameConfigData.mtt_ante_multiple[i] * GameConfigData.mtt_sblins[startIndex]);//默认是乘以mtt_sblins[0]，拖动拖动条时乘数会变
                    blindStuctureList.add(new BlindStuctureEntity(i + 1, currentBlinnd, currentAnte));
                }
            } else if (mode == 2) {
                for (int i = 0; i < GameConfigData.mtt_ante_multiple_quick.length; i++) {
                    int startIndex = mStartIndex >= GameConfigData.mtt_sblins_quick.length ? GameConfigData.mtt_sblins_quick.length - 1 : mStartIndex;
                    int currentBlinnd = (int) (GameConfigData.mtt_sblinds_multiple_quick[i] * GameConfigData.mtt_sblins_quick[startIndex]);
                    int currentAnte = (int) (GameConfigData.mtt_ante_multiple_quick[i] * GameConfigData.mtt_sblins_quick[startIndex]);//默认是乘以mtt_sblins[0]，拖动拖动条时乘数会变
                    blindStuctureListQuick.add(new BlindStuctureEntity(i + 1, currentBlinnd, currentAnte));
                }
            }
        } else if (mPlayMode == GameConstants.PLAY_MODE_OMAHA) {
            //只初始化viewpager两个页面中的其中一个，如果两个页面都初始化activity启动很慢
            if (mode == 1) {
                for (int i = 0; i < GameConfigData.omaha_mtt_ante_multiple.length; i++) {
                    int startIndex = mStartIndex >= GameConfigData.omaha_mtt_sblins.length ? GameConfigData.omaha_mtt_sblins.length - 1 : mStartIndex;
                    int currentBlinnd = (int) (GameConfigData.omaha_mtt_sblinds_multiple[i] * GameConfigData.omaha_mtt_sblins[startIndex]);
                    int currentAnte = (int) (GameConfigData.omaha_mtt_ante_multiple[i] * GameConfigData.omaha_mtt_sblins[startIndex]);//默认是乘以mtt_sblins[0]，拖动拖动条时乘数会变
                    blindStuctureList.add(new BlindStuctureEntity(i + 1, currentBlinnd, currentAnte));
                }
            } else if (mode == 2) {
                for (int i = 0; i < GameConfigData.omaha_mtt_ante_multiple_quick.length; i++) {
                    int startIndex = mStartIndex >= GameConfigData.omaha_mtt_sblins_quick.length ? GameConfigData.omaha_mtt_sblins_quick.length - 1 : mStartIndex;
                    int currentBlinnd = (int) (GameConfigData.omaha_mtt_sblinds_multiple_quick[i] * GameConfigData.omaha_mtt_sblins_quick[startIndex]);
                    int currentAnte = (int) (GameConfigData.omaha_mtt_ante_multiple_quick[i] * GameConfigData.omaha_mtt_sblins_quick[startIndex]);//默认是乘以mtt_sblins[0]，拖动拖动条时乘数会变
                    blindStuctureListQuick.add(new BlindStuctureEntity(i + 1, currentBlinnd, currentAnte));
                }
            }
        }
    }

    //普通表和快速表的数据源改变
    private void notifyDataSetChanged() {
        if (mPlayMode == GameConstants.PLAY_MODE_TEXAS_HOLDEM) {
            if (mode == 1) {//只更新普通表数据源
                blindStuctureList.clear();
                ///*mtt_ante_multiple   mtt_sblinds_multiple  mtt_sblins   mtt盲注结构表  1普通*/
                for (int i = 0; i < GameConfigData.mtt_ante_multiple.length; i++) {
                    int currentBlinnd = (int) (GameConfigData.mtt_sblinds_multiple[i] * GameConfigData.mtt_sblins[mStartIndex]);
                    int currentAnte = (int) (GameConfigData.mtt_ante_multiple[i] * GameConfigData.mtt_sblins[mStartIndex]);//默认是乘以mtt_sblins[0]，拖动拖动条时乘数会变
                    blindStuctureList.add(new BlindStuctureEntity(i + 1, currentBlinnd, currentAnte));
                }
                mBlindsStructureAdapter.notifyDataSetChanged();
            } else if (mode == 2) {//只更新快速表数据源
                blindStuctureListQuick.clear();
                ///*mtt_ante_multiple   mtt_sblinds_multiple  mtt_sblins   mtt盲注结构表  1普通*/
                for (int i = 0; i < GameConfigData.mtt_ante_multiple_quick.length; i++) {
                    int currentBlinnd = (int) (GameConfigData.mtt_sblinds_multiple_quick[i] * GameConfigData.mtt_sblins_quick[mStartIndex]);
                    int currentAnte = (int) (GameConfigData.mtt_ante_multiple_quick[i] * GameConfigData.mtt_sblins_quick[mStartIndex]);//默认是乘以mtt_sblins[0]，拖动拖动条时乘数会变
                    blindStuctureListQuick.add(new BlindStuctureEntity(i + 1, currentBlinnd, currentAnte));
                }
                mBlindsStructureAdapterQuick.notifyDataSetChanged();
            }
        } else if (mPlayMode == GameConstants.PLAY_MODE_OMAHA) {
            if (mode == 1) {//只更新普通表数据源
                blindStuctureList.clear();
                ///*mtt_ante_multiple   mtt_sblinds_multiple  mtt_sblins   mtt盲注结构表  1普通*/
                for (int i = 0; i < GameConfigData.omaha_mtt_ante_multiple.length; i++) {
                    int currentBlinnd = (int) (GameConfigData.omaha_mtt_sblinds_multiple[i] * GameConfigData.omaha_mtt_sblins[mStartIndex]);
                    int currentAnte = (int) (GameConfigData.omaha_mtt_ante_multiple[i] * GameConfigData.omaha_mtt_sblins[mStartIndex]);//默认是乘以mtt_sblins[0]，拖动拖动条时乘数会变
                    blindStuctureList.add(new BlindStuctureEntity(i + 1, currentBlinnd, currentAnte));
                }
                mBlindsStructureAdapter.notifyDataSetChanged();
            } else if (mode == 2) {//只更新快速表数据源
                blindStuctureListQuick.clear();
                ///*mtt_ante_multiple   mtt_sblinds_multiple  mtt_sblins   mtt盲注结构表  1普通*/
                for (int i = 0; i < GameConfigData.omaha_mtt_ante_multiple_quick.length; i++) {
                    int currentBlinnd = (int) (GameConfigData.omaha_mtt_sblinds_multiple_quick[i] * GameConfigData.omaha_mtt_sblins_quick[mStartIndex]);
                    int currentAnte = (int) (GameConfigData.omaha_mtt_ante_multiple_quick[i] * GameConfigData.omaha_mtt_sblins_quick[mStartIndex]);//默认是乘以mtt_sblins[0]，拖动拖动条时乘数会变
                    blindStuctureListQuick.add(new BlindStuctureEntity(i + 1, currentBlinnd, currentAnte));
                }
                mBlindsStructureAdapterQuick.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.tv_normal_mtt_structure) {
            mode = 1;
            mtt_blinds_view_pager.setCurrentItem(0);
            onTableChanged();
        } else if (viewId == R.id.tv_quick_mtt_structure) {
            mode = 2;
            mtt_blinds_view_pager.setCurrentItem(1);
            onTableChanged();
        } else if (viewId == R.id.tv_head_right) {
            CreateMTTFrg.mtt_sblinds_mode = mode;
            CreateMTTFrg.mtt_start_sblinds_index = mStartIndex;
            finish();
        }
    }

    /**
     * 普通表和快速表切换事件
     */
    private void onTableChanged() {
        //先更新拖动条的数据源
        int[] sblinds_array = mPlayMode == GameConstants.PLAY_MODE_TEXAS_HOLDEM ?
                (mode == 1 ? GameConfigData.mtt_sblins : GameConfigData.mtt_sblins_quick) :
                (mode == 1 ? GameConfigData.omaha_mtt_sblins : GameConfigData.omaha_mtt_sblins_quick);
        mtt_start_blind_seekbar.onlyUpdateData(sblinds_array);
        mStartIndex = mtt_start_blind_seekbar.currentPosition;
        notifyDataSetChanged();
        setStartBlind();
    }

    public class BlindStructureAdapter extends PagerAdapter {
        public BlindStructureAdapter(Activity activity) {
        }
        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LogUtil.i("instantiateItem");
            if (position == 0) {
                container.addView(gv_blinds_stucture);
            } else if (position == 1) {
                container.addView(gv_blinds_stucture_quick);
            }
            return position == 0 ? gv_blinds_stucture : gv_blinds_stucture_quick;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
