package com.htgames.nutspoker.view.record;

import android.content.Context;
import android.util.AttributeSet;
import com.netease.nim.uikit.common.util.log.LogUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.config.AnalysisConfig;
import com.htgames.nutspoker.view.CircularProgressBar;

/**
 * 数据分析ITEM UI
 */
public class DataAnalysisItemView extends RelativeLayout {
    private final static String TAG = "DataAnalysisItemView";
    View view;
    LinearLayout ll_data_analysis;
    LinearLayout ll_data_analysis_head;
    LinearLayout ll_data_analysis_content;
    TextView tv_data_analysis_name;
    TextView tv_data_analysis_circular_name;
    TextView tv_data_analysis_rate;
    TextView tv_data_analysis_circular_rate;
    TextView tv_analysis_content_head;
    TextView tv_analysis_content_desc;
    TextView tv_analysis_evaluation;
    ImageView iv_data_analysis_arrow;
    TextView btn_analysis_share;
    CircularProgressBar circularProgressbar;
    //
    LinearLayout ll_analysis_line;
    RelativeLayout rl_analysis_progress;
    View view_analysis_improve;
    View view_analysis_good;

    public DataAnalysisItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public DataAnalysisItemView(Context context) {
        super(context);
        init(context);
    }

    public DataAnalysisItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.layout_data_analysis_item, null);
        initView(context);
        addView(view);
    }

    private void initView(Context context) {
        ll_data_analysis = (LinearLayout) view.findViewById(R.id.ll_data_analysis);
        ll_data_analysis_head = (LinearLayout) view.findViewById(R.id.ll_data_analysis_head);
        ll_data_analysis_content = (LinearLayout) view.findViewById(R.id.ll_data_analysis_content);
        tv_data_analysis_name = (TextView) view.findViewById(R.id.tv_data_analysis_name);
        tv_data_analysis_circular_name = (TextView) view.findViewById(R.id.tv_data_analysis_circular_name);
        tv_data_analysis_rate = (TextView) view.findViewById(R.id.tv_data_analysis_rate);
        tv_data_analysis_circular_rate = (TextView) view.findViewById(R.id.tv_data_analysis_circular_rate);
        tv_analysis_content_head = (TextView) view.findViewById(R.id.tv_analysis_content_head);
        tv_analysis_content_desc = (TextView) view.findViewById(R.id.tv_analysis_content_desc);
        tv_analysis_evaluation = (TextView) view.findViewById(R.id.tv_analysis_evaluation);
        iv_data_analysis_arrow = (ImageView) view.findViewById(R.id.iv_data_analysis_arrow);
        btn_analysis_share = (TextView) view.findViewById(R.id.btn_analysis_share);
        circularProgressbar = (CircularProgressBar) view.findViewById(R.id.circularProgressbar);
        //
        ll_analysis_line = (LinearLayout) view.findViewById(R.id.ll_analysis_line);
        rl_analysis_progress = (RelativeLayout) view.findViewById(R.id.rl_analysis_progress);
        view_analysis_improve = (View) view.findViewById(R.id.view_analysis_improve);
        view_analysis_good = (View) view.findViewById(R.id.view_analysis_good);
        ll_data_analysis_head.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ll_data_analysis_content.getVisibility() == VISIBLE) {
                    //关闭
                    ll_data_analysis_content.setVisibility(GONE);
                    iv_data_analysis_arrow.setImageResource(R.mipmap.icon_common_arrow_normal);
                    ll_data_analysis.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
                } else {
                    //展开
                    ll_data_analysis_content.setVisibility(VISIBLE);
                    iv_data_analysis_arrow.setImageResource(R.mipmap.icon_common_arrow_normal);
                    ll_data_analysis.setBackgroundResource(R.drawable.data_analysis_item_bg);
                }
            }
        });
        iv_data_analysis_arrow.setImageResource(R.mipmap.icon_common_arrow_normal);
        ll_data_analysis_content.setVisibility(GONE);
        ll_data_analysis.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
    }

    /**
     * 显示Item内容
     * @param isShow
     */
    public void showDataAnalysis(boolean isShow){
        if (!isShow) {
            //关闭
            ll_data_analysis_content.setVisibility(GONE);
            iv_data_analysis_arrow.setImageResource(R.mipmap.icon_common_arrow_normal);
            ll_data_analysis.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
        } else {
            //展开
            ll_data_analysis_content.setVisibility(VISIBLE);
            iv_data_analysis_arrow.setImageResource(R.mipmap.icon_common_arrow_normal);
            ll_data_analysis.setBackgroundResource(R.drawable.data_analysis_item_bg);
        }
    }

    public void setDataColumnType(final AnalysisConfig.Column column){
        tv_data_analysis_name.setText(AnalysisConfig.getAnalysisColumnAbbreviationName(column));
        tv_data_analysis_circular_name.setText(AnalysisConfig.getAnalysisColumnAbbreviationName(column));
        tv_analysis_content_head.setText(AnalysisConfig.getAnalysisColumnName(column));
        tv_analysis_content_desc.setText(AnalysisConfig.getAnalysisColumnDesc(column));
        tv_analysis_evaluation.setText(R.string.data_analysis_poor);
        rl_analysis_progress.post(new Runnable() {
            @Override
            public void run() {
                int width = rl_analysis_progress.getWidth();
                int average = 100;//段数
                if (column == AnalysisConfig.Column.AF) {
                    average = 10;
                }
                int type = AnalysisConfig.getAnalysisColumnType(column);
                //判断是否是优秀
                final int goodMin = AnalysisConfig.data_analysis_good_min[type];
                final int goodMax = AnalysisConfig.data_analysis_good_max[type];
                final int improveMin = AnalysisConfig.data_analysis_improve_min[type];
                final int improveMax = AnalysisConfig.data_analysis_improve_max[type];
                LogUtil.i(TAG, "rl_analysis_progress : " + width);
                LinearLayout.LayoutParams improveParams = (LinearLayout.LayoutParams) view_analysis_improve.getLayoutParams();
                improveParams.width = width / average * (improveMax - improveMin);
                improveParams.setMargins(width / average * improveMin, 0, 0, 0);
                view_analysis_improve.setLayoutParams(improveParams);

                LinearLayout.LayoutParams goodParams = (LinearLayout.LayoutParams) view_analysis_good.getLayoutParams();
                goodParams.width = width / average * (goodMax - goodMin);
                goodParams.setMargins(width / average * goodMin, 0, 0, 0);
                view_analysis_good.setLayoutParams(goodParams);
            }
        });
        setData(column , 0);
    }

    public void setData(final AnalysisConfig.Column column , final float rateF){
        int rate = 0;
        if(column == AnalysisConfig.Column.AF){
            tv_data_analysis_rate.setText(String.valueOf(rateF));
            tv_data_analysis_circular_rate.setText(String.valueOf(rateF));
            circularProgressbar.setProgressWithAnimation(rateF * 10);
        }else{
            rate = (int)rateF;
            if (column == AnalysisConfig.Column.SB_BET || column == AnalysisConfig.Column.BB_BET) {
                tv_data_analysis_rate.setText(rateF + "%");
                tv_data_analysis_circular_rate.setText(rateF + "%");
            } else{
                tv_data_analysis_rate.setText(rate + "%");
                tv_data_analysis_circular_rate.setText(rate + "%");
            }
            circularProgressbar.setProgressWithAnimation(rate);
        }
        int type = AnalysisConfig.getAnalysisColumnType(column);
        //判断是否是优秀
        final int goodMin = AnalysisConfig.data_analysis_good_min[type];
        final int goodMax = AnalysisConfig.data_analysis_good_max[type];
        final int improveMin = AnalysisConfig.data_analysis_improve_min[type];
        final int improveMax = AnalysisConfig.data_analysis_improve_max[type];
        if (rate >= goodMin && rate <= goodMax){
            tv_analysis_evaluation.setText(R.string.data_analysis_good);
        } else if(rate >= improveMin && improveMin <= improveMax){
            tv_analysis_evaluation.setText(R.string.data_analysis_improve);
        } else{
            tv_analysis_evaluation.setText(R.string.data_analysis_poor);
        }
        rl_analysis_progress.post(new Runnable() {
            @Override
            public void run() {
                int width = rl_analysis_progress.getWidth();
                int average = 100;//段数
                int progress = width / average * (int)rateF;
                if(column == AnalysisConfig.Column.AF){
                    average = 100;//AF的最大为10，数值*10
                    progress = (int)(width / average * (rateF * 10));
                }
//                Log.d(TAG, "rl_analysis_progress : " + width);
//                LinearLayout.LayoutParams improveParams = (LinearLayout.LayoutParams) view_analysis_improve.getLayoutParams();
//                improveParams.width = width / average * (improveMax - improveMin);
//                improveParams.setMargins(width / average * improveMin, 0, 0, 0);
//                view_analysis_improve.setLayoutParams(improveParams);
//
//                LinearLayout.LayoutParams goodParams = (LinearLayout.LayoutParams) view_analysis_good.getLayoutParams();
//                goodParams.width = width / average * (goodMax - goodMin);
//                goodParams.setMargins(width / average * goodMin, 0, 0, 0);
//                view_analysis_good.setLayoutParams(goodParams);
                RelativeLayout.LayoutParams lineParams = (RelativeLayout.LayoutParams)ll_analysis_line.getLayoutParams();
                lineParams.setMargins(progress, 0 , 0 , 0);
                ll_analysis_line.setLayoutParams(lineParams);
            }
        });
    }
}
