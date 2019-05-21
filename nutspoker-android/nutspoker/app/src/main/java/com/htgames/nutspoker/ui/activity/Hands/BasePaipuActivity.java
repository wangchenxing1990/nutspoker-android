package com.htgames.nutspoker.ui.activity.Hands;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.htgames.nutspoker.ui.base.BaseActivity;
import com.netease.nim.uikit.pulltorefresh.BottomLoadingView;

/**
 * Created by Administrator on 2016/2/28.
 */
public class BasePaipuActivity extends BaseActivity{
    public final static String EXTRA_FROM = "from";
    public final static int FROM_NORMAL = 0;//来自正常逻辑
    public final static int FROM_SHARE = 1;//来自分享界面
    public final static int FROM_SESSION_BY_P2P = 2;//发送：聊天界面：个人
    public final static int FROM_SESSION_BY_TEAM = 3;//发送：聊天界面：群
    public int from = FROM_NORMAL;
    SwipeRefreshLayout paipu_record_and_collect_pulltorefresh_lv;
    BottomLoadingView bottomLoadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bottomLoadingView = new BottomLoadingView(this);
        bottomLoadingView.setVisibility(View.INVISIBLE);
    }
}
