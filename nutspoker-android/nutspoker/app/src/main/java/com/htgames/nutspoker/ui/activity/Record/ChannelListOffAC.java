package com.htgames.nutspoker.ui.activity.Record;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMgrBean;
import com.netease.nim.uikit.bean.UserEntity;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.interfaces.VolleyCallback;
import com.htgames.nutspoker.ui.action.GameAction;
import com.htgames.nutspoker.ui.adapter.channel.ChannelAdapter;
import com.htgames.nutspoker.ui.adapter.channel.ChannelSection;
import com.htgames.nutspoker.ui.adapter.channel.ChannelType;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.view.TouchableRecyclerView;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.interfaces.IClick;
import com.netease.nim.uikit.nav.UIUrl;
import com.netease.nim.uikit.nav.UrlConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 周智慧 on 17/1/20.
 */

@UIUrl(urls = UrlConstants.RECORD_DETAIL_MGR_LSIT)
public class ChannelListOffAC extends BaseActivity implements IClick {
    GameAction mGameAction;
    private GameEntity gameInfo;
    @BindView(R.id.channel_recyc) TouchableRecyclerView channel_recyc;
    public int clubIdentity = 1;//0俱乐部普通成员1俱乐部管理员包括会长   默认是管理员
    ChannelAdapter channelAdapter;
    public static void start(Activity activity, GameEntity gameInfo, int clubIdentity) {
        if (gameInfo == null) {
            Toast.makeText(activity, "信息不全", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(activity, ChannelListOffAC.class);
        intent.putExtra(GameConstants.KEY_GAME_INFO, gameInfo);//不要传bill，intent传输数据大小有限制,bill太大会crash
        intent.putExtra(GameConstants.KEY_GAME_IS_CLUB_CHANNEL, clubIdentity);
        activity.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameInfo = (GameEntity) getIntent().getSerializableExtra(GameConstants.KEY_GAME_INFO);
        clubIdentity = getIntent().getIntExtra(GameConstants.KEY_GAME_IS_CLUB_CHANNEL, 1);
        setContentView(R.layout.activity_add_game_manager);
        mGameAction = new GameAction(this, null);
        mUnbinder = ButterKnife.bind(this);
        initView();
        setHeadTitle("赛事管理员");
        initAdapter();
        getMgrListOff();
    }

    private void initView() {
        findViewById(R.id.ll_addgame_mgr_column).setVisibility(View.GONE);
    }

    private void initAdapter() {
        channelAdapter = new ChannelAdapter(this, true);
        channelAdapter.mSwipeable = false;
        channelAdapter.gameInfo = gameInfo;
        channel_recyc.setAdapter(channelAdapter);
    }

    private void gotoMgrListPage(int position) {
        String channel = gameCreatorChannel;
        String pageTitle = "我的管理";
        String mgrAccount = DemoCache.getAccount();
        Bundle data = new Bundle();
        if (position >= 0 && channelAdapter.getItemCount() > position && channelAdapter.getItem(position) instanceof GameMgrBean) {
            GameMgrBean mgr = (GameMgrBean) channelAdapter.getItem(position);
            gameInfo.channel = mgr.channel;
            channel = mgr.channel;
        }
        UserByChannelOffAC.startByGameCreator(ChannelListOffAC.this, gameInfo, channel, pageTitle, mgrAccount);//渠道玩家
    }

    ArrayList<GameMgrBean> originalChannelList = new ArrayList<GameMgrBean>();
    ArrayList<UserEntity> allManagers = new ArrayList<>();
    ArrayList<GameMgrBean> clubChannel = new ArrayList<GameMgrBean>();
    ArrayList<GameMgrBean> creatorChannel = new ArrayList<GameMgrBean>();
    ArrayList<GameMgrBean> personalChannel = new ArrayList<GameMgrBean>();
    private void generateDataList() {
        clubChannel.clear();
        creatorChannel.clear();
        personalChannel.clear();
        String creator = gameInfo.creatorInfo.account;
        for (int i = 0; i < originalChannelList.size(); i++) {
            GameMgrBean gameMgrBean = originalChannelList.get(i);
            if (gameMgrBean.channel.length() == 6) {
                UserEntity userEntity = (UserEntity) gameMgrBean.mgrList.get(0);//6位code的渠道里面肯定只有一个玩家
                gameMgrBean.uid = userEntity.account;
                gameMgrBean.uuid = userEntity.uuid;
                gameMgrBean.nickname = userEntity.nickname;
                if (userEntity.account.equals(creator)) {
                    gameMgrBean.channelType = ChannelType.creator;
                    creatorChannel.add(gameMgrBean);
                } else {
                    gameMgrBean.channelType = ChannelType.personal;
                    personalChannel.add(gameMgrBean);
                }
            } else {
                gameMgrBean.tid = gameMgrBean.channel.substring(6, gameMgrBean.channel.length());
                boolean gameCreatedClub = !StringUtil.isSpace(gameMgrBean.tid) && !gameMgrBean.tid.equals("0") && gameMgrBean.tid.equals(gameInfo.tid);
                if (clubIdentity == 0 && gameMgrBean.mgrList.size() == 1 && ((UserEntity) gameMgrBean.mgrList.get(0)).account.equals(creator) && gameCreatedClub) {
                    UserEntity userEntity = (UserEntity) gameMgrBean.mgrList.get(0);
                    gameMgrBean.channelType = ChannelType.creator;
                    gameMgrBean.uid = userEntity.account;
                    gameMgrBean.uuid = userEntity.uuid;
                    gameMgrBean.nickname = userEntity.nickname;
                    creatorChannel.add(gameMgrBean);
                } else {
                    clubChannel.add(gameMgrBean);
                    gameMgrBean.channelType = ChannelType.club;
                }
            }
        }
        if (clubIdentity == 0) {
            channelAdapter.addSection(new ChannelSection(ChannelType.creator), creatorChannel);
            channelAdapter.addSection(new ChannelSection(ChannelType.club), clubChannel);
        } else if (clubIdentity == 1) {
            channelAdapter.addSection(new ChannelSection(ChannelType.club), clubChannel);
            channelAdapter.addSection(new ChannelSection(ChannelType.creator), creatorChannel);
        }
        channelAdapter.addSection(new ChannelSection(ChannelType.personal), personalChannel);
        channelAdapter.datasetChanged();
    }
    private String gameCreatorChannel;//游戏创建者的channel
    private void getMgrListOff() {
        if (gameInfo == null) {
            return;
        }
        mGameAction.getMgrListOff(gameInfo.gid, new VolleyCallback() {
            @Override
            public void onResponse(String result) {
                try {
                    JSONObject response = new JSONObject(result);
                    int code = response.getInt("code");
                    if (code == 0) {
                        allManagers.clear();
                        originalChannelList.clear();
                        if(!response.isNull("data")) {
                            JSONArray array = response.getJSONArray("data");
                            int size = array.length();
                            for (int i = 0; i < size; i++) {
                                JSONObject channel = (JSONObject) array.get(i);
                                GameMgrBean bean = new GameMgrBean();
                                bean.mgrList = new ArrayList();
                                bean.channel = channel.optString("channel");
                                bean.players = channel.optInt("players");
                                bean.channelType = bean.channel.length() == 6 ? ChannelType.personal : (clubIdentity != 1 ? ChannelType.creator : ChannelType.club);
                                JSONArray usersArray = channel.getJSONArray("users");
                                for (int j = 0; j < usersArray.length(); j++) {
                                    UserEntity userEntity = new UserEntity();
                                    JSONObject inner = usersArray.getJSONObject(j);
                                    userEntity.account = inner.optString("uid");
                                    userEntity.uuid = inner.optString("uuid");
                                    userEntity.nickname = inner.optString("nickname");
                                    userEntity.channel = bean.channel;
                                    userEntity.channelType = bean.channelType;
                                    bean.mgrList.add(userEntity);
                                    if (userEntity.account.equals(gameInfo.creatorInfo.account)) {
                                        gameCreatorChannel = userEntity.channel;
                                    }
                                    if (clubIdentity != 1 && userEntity.account.equals(gameInfo.creatorInfo.account)) {
                                        bean.uid = userEntity.account;
                                        bean.uuid = userEntity.uuid;
                                        bean.nickname = userEntity.nickname;
                                    }
                                }
                                originalChannelList.add(bean);
                                allManagers.addAll(bean.mgrList);
                            }
                        }
                        generateDataList();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });//获取赛事管理员列表
    }

    @Override
    protected void onDestroy() {
        if (mGameAction != null) {
            mGameAction.onDestroy();
            mGameAction = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(int position) {
        gotoMgrListPage(position);
    }

    @Override
    public void onDelete(int position) {
    }

    @Override
    public void onLongClick(int position) {
    }

}
