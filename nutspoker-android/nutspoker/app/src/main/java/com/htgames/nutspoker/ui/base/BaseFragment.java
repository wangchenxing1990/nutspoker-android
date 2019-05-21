package com.htgames.nutspoker.ui.base;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalytics;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.fragment.TabFragment;

import butterknife.Unbinder;

/**
 * Created by zjy on 2015/9/14.
 */
public class BaseFragment extends TabFragment {
    String fragmentName = "BaseFragment";
    public boolean isFragmentCreated = false;
    /** Fragment当前状态是否可见 */
    protected boolean isVisible;

    protected Unbinder mFragmentUnbinder = null;

    public void setFragmentName(String fragmentName){
        this.fragmentName = fragmentName;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtil.i("BaseFragment", "onDestroyView");
        if (mFragmentUnbinder != null)
            mFragmentUnbinder.unbind();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        UmengAnalytics.onPageStart(fragmentName);
    }

    @Override
    public void onPause() {
        super.onPause();
        UmengAnalytics.onPageEnd(fragmentName);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void setHeadTitle(View view , int resId){
        ((TextView)view.findViewById(R.id.btn_head_back)).setVisibility(View.GONE);
        ((TextView)view.findViewById(R.id.tv_head_title)).setText(resId);
    }

    public void setHeadRightButton(View view ,int resId, View.OnClickListener onClickListener) {
        TextView tv_head_right = ((TextView) view.findViewById(R.id.tv_head_right));
        tv_head_right.setVisibility(View.VISIBLE);
        tv_head_right.setText(resId);
        tv_head_right.setOnClickListener(onClickListener);
    }
//    protected <T extends View> T findView(int resId) {
//        return (T) (getView().findViewById(resId));
//    }

    /**
     * 可见
     */
    protected void onVisible() {
    }


    /**
     * 不可见
     */
    protected void onInvisible() {
    }
}
