package com.htgames.nutspoker.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.ui.action.AmountAction;
import com.htgames.nutspoker.ui.activity.Game.GameCreateActivity;
import com.htgames.nutspoker.ui.base.BaseFragment;
import com.netease.nim.uikit.common.preference.UserPreferences;

/**
 * Created by 20150726 on 2016/5/11.
 */
public class BaseGameCreateFragment extends BaseFragment {
    public int coins = 0;
    public int diamonds = 0;
    private AmountAction mAmountAction;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAmountAction = new AmountAction(getActivity(), null);
        mAmountAction.setRequestCallback(new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                coins = UserPreferences.getInstance(getContext()).getCoins();
                diamonds = UserPreferences.getInstance(getContext()).getDiamond();
                afterGetAmount();
            }
            @Override
            public void onFailed() {
            }
        });
        mAmountAction.getAmount(false);
    }

    protected void afterGetAmount() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        coins = UserPreferences.getInstance(getContext()).getCoins();
        diamonds = UserPreferences.getInstance(getContext()).getDiamond();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        //可能购买了金币，回来要恢复
        coins = UserPreferences.getInstance(getContext()).getCoins();
        diamonds = UserPreferences.getInstance(getContext()).getDiamond();
    }

    public void showHordeControlDialog() {
        if (getActivity() != null && getActivity() instanceof GameCreateActivity) {
            ((GameCreateActivity) getActivity()).showHordeControlDialog();
        }
    }

    public void showTopUpDialog(int type) {
        if (getActivity() != null && getActivity() instanceof GameCreateActivity) {
            ((GameCreateActivity) getActivity()).showTopUpDialog(type);
        }
    }

    public void showCreateLimitDialog(int code) {
        if (getActivity() != null && getActivity() instanceof GameCreateActivity) {
            ((GameCreateActivity) getActivity()).showCreateLimitDialog(code);
        }
    }

    @Override
    public void onDestroy() {
        if (mAmountAction != null) {
            mAmountAction.onDestroy();
            mAmountAction = null;
        }
        super.onDestroy();
    }
}
