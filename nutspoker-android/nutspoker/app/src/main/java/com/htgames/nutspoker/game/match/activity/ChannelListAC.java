package com.htgames.nutspoker.game.match.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.ui.action.GameAction;
import com.htgames.nutspoker.ui.adapter.channel.ChannelAdapter;
import com.htgames.nutspoker.ui.adapter.channel.ChannelSection;
import com.htgames.nutspoker.ui.adapter.channel.ChannelType;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.view.TouchableRecyclerView;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.api.ApiCode;
import com.netease.nim.uikit.api.ApiConstants;
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.api.SignStringRequest;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMgrBean;
import com.netease.nim.uikit.bean.UserEntity;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.customview.SwipeItemLayout;
import com.netease.nim.uikit.interfaces.IClick;
import com.netease.nim.uikit.nav.UrlConstants;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 * Created by 周智慧 on 17/3/9.  添加自由局的赛事管理员
 */

public class ChannelListAC extends BaseActivity implements IClick {
    GameEntity gameInfo;
    GameAction mGameAction;
    ChannelAdapter channelAdapter;
    @BindView(R.id.ll_addgame_mgr_column) View ll_addgame_mgr_column;
    @BindView(R.id.channel_recyc) TouchableRecyclerView channel_recyc;
    @BindView(R.id.tv_add_channel_des) TextView tv_add_channel_des;
    //下面的组件要废弃掉
    public static void start(Activity activity, GameEntity gameEntity) {
        Intent intent = new Intent(activity, ChannelListAC.class);
        intent.putExtra(UrlConstants.ADD_GAME_MANAGER_GAME_INFO, gameEntity);
        activity.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gameInfo = (GameEntity) getIntent().getSerializableExtra(UrlConstants.ADD_GAME_MANAGER_GAME_INFO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game_manager);
        setHeadTitle(R.string.game_manager);
        mGameAction = new GameAction(this, null);
        mUnbinder = ButterKnife.bind(this);
        initView();
        initAdapter();
        mGameAction.getMgrList(gameInfo.gid, gameInfo.creatorInfo.account, gameInfo.code, new GameRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                generateDataList();
            }
            @Override
            public void onFailed(int code, JSONObject response) {
            }
        });//获取赛事管理员列表
        tv_add_channel_des.setText(gameInfo.gameMode < GameConstants.GAME_MODE_SNG ? R.string.add_normalgamemgr : R.string.add_mttgamemgr);
    }

    private void initView() {
        ll_addgame_mgr_column.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddChannelAC.Companion.start(ChannelListAC.this);
            }
        });
    }

    private void initAdapter() {
        channelAdapter = new ChannelAdapter(this, false);
        channelAdapter.gameInfo = gameInfo;
        channel_recyc.setAdapter(channelAdapter);
        channel_recyc.setHasFixedSize(true);
    }

    public static boolean dataTrigger = false;
    @Override
    protected void onResume() {
        super.onResume();
        if (dataTrigger) {
            dataTrigger = false;
            generateDataList();
        }
    }

    ArrayList<GameMgrBean> clubChannel = new ArrayList<GameMgrBean>();
    ArrayList<GameMgrBean> creatorChannel = new ArrayList<GameMgrBean>();
    ArrayList<GameMgrBean> personalChannel = new ArrayList<GameMgrBean>();
    private void generateDataList() {
        ArrayList<GameMgrBean> originalMgrList = ChessApp.gameChannels.get(gameInfo.gid);
        if (originalMgrList == null) {
            originalMgrList = new ArrayList<GameMgrBean>();
        }
        clubChannel.clear();
        creatorChannel.clear();
        personalChannel.clear();
        String creator = gameInfo.creatorInfo.account;
        for (int i = 0; i < originalMgrList.size(); i++) {
            GameMgrBean gameMgrBean = originalMgrList.get(i);
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
                if (gameInfo.club_channel == 0 && gameMgrBean.mgrList.size() == 1 && ((UserEntity) gameMgrBean.mgrList.get(0)).account.equals(creator) && gameCreatedClub) {
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
                gameMgrBean.tid = gameMgrBean.channel.substring(6, gameMgrBean.channel.length());
            }
        }
        if (gameInfo.club_channel == 0) {
            channelAdapter.addSection(new ChannelSection(ChannelType.creator), creatorChannel);
            channelAdapter.addSection(new ChannelSection(ChannelType.club), clubChannel);
        } else if (gameInfo.club_channel == 1) {
            channelAdapter.addSection(new ChannelSection(ChannelType.club), clubChannel);
            channelAdapter.addSection(new ChannelSection(ChannelType.creator), creatorChannel);
        }
        channelAdapter.addSection(new ChannelSection(ChannelType.personal), personalChannel);
        channelAdapter.datasetChanged();
    }

    @Override
    public void onDelete(int position) {
        if (channelAdapter.getItemCount() > position && channelAdapter.getItem(position) instanceof GameMgrBean) {
            GameMgrBean mgr = (GameMgrBean) channelAdapter.getItem(position);
            showDeleteMgrDialog(mgr, position);
        }
    }

    private void showDeleteMgrDialog(final GameMgrBean bean, final int position) {
        String msg = getResources().getString(bean.block == 0 ? R.string.sure_disable_checkin : R.string.sure_enable_checkin);
        EasyAlertDialog backDialog = EasyAlertDialogHelper.createOkCancelDiolag(ChannelListAC.this, "", msg, true,
                new EasyAlertDialogHelper.OnDialogActionListener() {
                    @Override
                    public void doCancelAction() {
                    }
                    @Override
                    public void doOkAction() {
                        if (bean.channel.length() == 6) {
                            changeChannelBlock(bean, position);
                        } else {
                            changeClubChannelBlock(bean, position);
                        }
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            backDialog.show();
        }
    }

    @Override
    public void onClick(int position) {
        gotoMgrListPage(position);
    }

    @Override
    public void onLongClick(final int position) {
        if (channelAdapter.getItemCount() > position && channelAdapter.getItem(position) instanceof GameMgrBean) {
            final GameMgrBean mgr = (GameMgrBean) channelAdapter.getItem(position);
            CustomAlertDialog alertDialog = new CustomAlertDialog(ChannelListAC.this);
            alertDialog.setTitle(NimUserInfoCache.getInstance().getUserDisplayName(mgr.uid));
            String title = getResources().getString(mgr.block == 0 ? R.string.disable_checkin : R.string.enable_checkin);
            alertDialog.addItem(title, new CustomAlertDialog.onSeparateItemClickListener() {
                @Override
                public void onClick() {
                    if (mgr.channel.length() == 6) {
                        changeChannelBlock(mgr, position);
                    } else {
                        changeClubChannelBlock(mgr, position);
                    }
                }
            });
            alertDialog.show();
        }
    }

    String requestDeleteMttMgrUrl;
    private void changeChannelBlock(final GameMgrBean bean, final int position) {
        if (gameInfo == null || TextUtils.isEmpty(gameInfo.code)) {
            return;
        }
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        final String creatorId = gameInfo.creatorInfo.account;
        DialogMaker.showProgressDialog(this, "", false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("uid", creatorId);
        paramsMap.put("code", bean.channel);
        paramsMap.put("admin_id", bean.uid);
        paramsMap.put("block", String.valueOf(1 - bean.block));
        requestDeleteMttMgrUrl = getHost() + ApiConstants.URL_MTT_DEL_MGR + NetWork.getRequestParams(paramsMap);
        LogUtil.i("GameAction", requestDeleteMttMgrUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestDeleteMttMgrUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i("GameAction", response);
                DialogMaker.dismissProgressDialog();
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        Toast.makeText(ChessApp.sAppContext, "更改成功", Toast.LENGTH_LONG).show();
//                        channelAdapter.closeOpenedSwipeItemLayoutWithAnim();//zhegz这个方法会close所有的view
                        SwipeItemLayout itemView = (SwipeItemLayout) channel_recyc.getLayoutManager().findViewByPosition(position);
                        if (itemView != null) {
                            channelAdapter.notifyItemRemovedCustom(itemView);
                        }
                        bean.block = 1 - bean.block;
                        channelAdapter.notifyItemChanged(position);
                    } else {
                        String failMsg = ApiCode.SwitchCode(code, response);
                        Toast.makeText(ChessApp.sAppContext, failMsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    DialogMaker.dismissProgressDialog();
                    String failMsg = ApiCode.SwitchCode(ApiCode.CODE_JSON_ERROR, response);
                    Toast.makeText(ChessApp.sAppContext, failMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                String failMsg = ApiCode.SwitchCode(ApiCode.CODE_NETWORD_ERROR, null);
                Toast.makeText(ChessApp.sAppContext, failMsg, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestDeleteMttMgrUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    private void changeClubChannelBlock(final GameMgrBean bean, final int position) {
        mGameAction.deleteClubChannel(bean.tid, bean.channel, String.valueOf(1- bean.block), new GameRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                android.widget.Toast.makeText(ChannelListAC.this, "更改成功", Toast.LENGTH_SHORT).show();
                SwipeItemLayout itemView = (SwipeItemLayout) channel_recyc.getLayoutManager().findViewByPosition(position);
                if (itemView != null) {
                    channelAdapter.notifyItemRemovedCustom(itemView);
                }
                bean.block = 1 - bean.block;
                channelAdapter.notifyItemChanged(position);
            }
            @Override
            public void onFailed(int code, JSONObject response) {
                String failMsg = ApiCode.SwitchCode(code, response == null ? "" : response.toString());
                Toast.makeText(ChessApp.sAppContext, failMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogDelAdminFailedCauseByHasCheckin() {
        EasyAlertDialog backDialog = EasyAlertDialogHelper.createOneButtonDiolag(ChannelListAC.this, "", "该渠道已有报名请求，无法移除", getString(R.string.ok), true,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            backDialog.show();
        }
    }

    private void gotoMgrListPage(int position) {
        gameInfo.channel = gameInfo.code;
        if (position >= 0 && channelAdapter.getItemCount() > position && channelAdapter.getItem(position) instanceof GameMgrBean) {
            GameMgrBean mgr = (GameMgrBean) channelAdapter.getItem(position);
            gameInfo.channel = mgr.channel;
        }
//        Bundle data = new Bundle();
//        data.putString(UrlConstants.MTT_MY_MGR_GAME_ID, gameInfo.gid);
//        data.putString(UrlConstants.MTT_MY_MGR_CREATOR_ID, gameInfo.creatorInfo.account);
//        data.putString(UrlConstants.MTT_MY_MGR_GAME_CODE, channel);
//        Nav.from(this).withExtras(data).toUri(UrlConstants.MTT_MY_MGR);
        UserByChannelAC.startByGameCreator(this, gameInfo);
    }
}
