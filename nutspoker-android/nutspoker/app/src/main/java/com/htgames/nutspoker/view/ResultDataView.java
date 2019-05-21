package com.htgames.nutspoker.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.tool.NetworkTools;

/**
 * 没有数据View
 */
public class ResultDataView extends RelativeLayout implements View.OnClickListener {
    public View view;
    RelativeLayout rl_loading;
    LinearLayout ll_null_data;
    TextView tv_nulldata_text;
    private ReloadDataCallBack mRetrieveDataCallBack;
    private Button reloadBtn;

    public ResultDataView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public ResultDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ResultDataView(Context context) {
        super(context);
        init(context);
    }

    public void init(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.view_results_data, null);
        tv_nulldata_text = (TextView) view.findViewById(R.id.tv_nulldata_text);
        rl_loading = (RelativeLayout) view.findViewById(R.id.rl_loading);
        ll_null_data = (LinearLayout) view.findViewById(R.id.ll_null_data);
        reloadBtn = (Button) view.findViewById(R.id.retrieve_btn);
        reloadBtn.setOnClickListener(this);
        addView(view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retrieve_btn:
                if (mRetrieveDataCallBack != null) {
                    showLoading();
                    mRetrieveDataCallBack.onReloadData();
                }
                break;

            default:
                break;
        }
    }

    /**
     * 是否在加载中
     */
    public boolean isLoading() {
        if (rl_loading.getVisibility() == View.GONE) {
            return false;
        }
        return true;
    }

    /**
     * 设置空字符
     */
    public void nullDataShow(int resid) {
        this.setVisibility(View.VISIBLE);
        ll_null_data.setVisibility(View.VISIBLE);
        rl_loading.setVisibility(View.GONE);
        tv_nulldata_text.setText(resid);
        setReloadBtnVisibility(GONE);
    }

    /**
     * 设置空字符
     */
    public void nullDataShow(int resid , int resImgId) {
        setVisibility(View.VISIBLE);
        ll_null_data.setVisibility(View.VISIBLE);
        rl_loading.setVisibility(View.GONE);
        tv_nulldata_text.setText(resid);
        if (resImgId != 0) {
            Drawable drawable = getResources().getDrawable(resImgId);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv_nulldata_text.setCompoundDrawables(null, drawable, null, null);
        }
        setReloadBtnVisibility(GONE);
    }

    public void nullDataShowNotImage(int resid) {
        this.setVisibility(View.VISIBLE);
        ll_null_data.setVisibility(View.VISIBLE);
        rl_loading.setVisibility(View.GONE);
        tv_nulldata_text.setText(resid);
        tv_nulldata_text.setCompoundDrawables(null, null, null, null);
        setReloadBtnVisibility(GONE);
    }

    public void nullDataShowNotImage(String str, int colorId) {
        this.setVisibility(View.VISIBLE);
        ll_null_data.setVisibility(View.VISIBLE);
        rl_loading.setVisibility(View.GONE);
        tv_nulldata_text.setText(str);
        tv_nulldata_text.setCompoundDrawables(null, null, null, null);
        tv_nulldata_text.setTextColor(getContext().getResources().getColor(colorId));
        setReloadBtnVisibility(GONE);
    }

    public void nullDataShow(int resid , int resImgId, int visibilityReloadBtn) {
        nullDataShow(resid , resImgId);
        setReloadBtnVisibility(visibilityReloadBtn);
    }

    /**
     * 用于网络数据获取结果出错时的提示，显示重新获取按钮
     *
     * @param context
     * @param resid   错误时的提示，如果没有网络则显示没有网络的提示
     */
    public void showError(Context context, int resid) {
        int id = 0;
        if (NetworkTools.isNetConnect(context)) {
            id = resid;
        } else {
            id = R.string.no_internet;
        }
        nullDataShow(id , R.mipmap.img_null_data);
        reloadBtn.setVisibility(View.VISIBLE);
    }

    public void nullDataShow(String str) {
        setVisibility(View.VISIBLE);
        ll_null_data.setVisibility(View.VISIBLE);
        rl_loading.setVisibility(View.GONE);
        tv_nulldata_text.setText(str);
    }

    /**
     * 获取成功
     */
    public void successShow() {
        setVisibility(View.GONE);
        ll_null_data.setVisibility(View.GONE);
        rl_loading.setVisibility(View.GONE);
    }

    public void showLoading() {
        setVisibility(View.VISIBLE);
        ll_null_data.setVisibility(View.GONE);
        rl_loading.setVisibility(View.VISIBLE);
    }

    public void setReloadDataCallBack(ReloadDataCallBack retrieveDataCallBack) {
        mRetrieveDataCallBack = retrieveDataCallBack;
    }

    public Button getReloadBtn() {
        return reloadBtn;
    }

    public void setReloadBtnVisibility(int visibility) {
        reloadBtn.setVisibility(visibility);
    }

    public interface ReloadDataCallBack {
        public void onReloadData();
    }
}
