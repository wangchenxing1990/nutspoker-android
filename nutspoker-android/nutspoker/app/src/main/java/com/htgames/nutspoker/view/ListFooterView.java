package com.htgames.nutspoker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.tool.NetworkTools;

/**
 * 列表底部加载更多
 */
public class ListFooterView extends RelativeLayout {
    LinearLayout mBaseLayout;
    ProgressBar mProgressBar;
    TextView mFooterHintTv;
    View view;
    FooterViewCallBack mFooterViewCallBack;

    public ListFooterView(Context context) {
        super(context);
        init(context);
    }

    public ListFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ListFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT);
        view = LayoutInflater.from(context).inflate(R.layout.view_list_footer, null);
        mBaseLayout = (LinearLayout)view.findViewById(R.id.ll_footer_view);
        mProgressBar = (ProgressBar)view.findViewById(R.id.proBar_footer);
        mFooterHintTv = (TextView)view.findViewById(R.id.tv_footer);
        mBaseLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFooterViewCallBack != null){
                    showLoad();
                    mFooterViewCallBack.onClickFooter();
                }
            }
        });
        mBaseLayout.setEnabled(false);
        addView(view , params);
    }

    public void showLoad(){
        mProgressBar.setVisibility(View.VISIBLE);
        mFooterHintTv.setText(R.string.loading);
        mBaseLayout.setEnabled(false);
    }

    public void showHint(int resid){
        mProgressBar.setVisibility(View.GONE);
        mFooterHintTv.setText(resid);
        mBaseLayout.setEnabled(true);
    }

    public void showError(Context context, int resid){
        int id;
        if(NetworkTools.isNetConnect(context)){
            id = R.string.error_try_again;
        }else{
            id = R.string.no_connection_try_again;
        }
        if(resid > 0){
            id = resid;
        }
        mProgressBar.setVisibility(View.GONE);
        mBaseLayout.setEnabled(true);
        mFooterHintTv.setText(id);
    }

    public void setFooterViewCallBack(FooterViewCallBack footerViewCallBack){
        mFooterViewCallBack = footerViewCallBack;
    }

    public void setFooterDividersEnabled(int visibility){
//        mFooterDivider.setVisibility(visibility);
    }

    public interface FooterViewCallBack {
        public void onClickFooter();
    }
}
