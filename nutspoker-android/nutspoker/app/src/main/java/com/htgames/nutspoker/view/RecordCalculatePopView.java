package com.htgames.nutspoker.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.PopupWindow;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.data.common.RecordConstans;
import com.htgames.nutspoker.ui.adapter.RecordCalculateAdapter;
import com.htgames.nutspoker.view.widget.CustomListView;

import java.util.ArrayList;

/**
 */
public class RecordCalculatePopView extends PopupWindow {
    View popView;
    CustomListView lv_calculate;
    RecordCalculateAdapter mRecordCalculateAdapter;
    ArrayList<Float> calculateList = new ArrayList<Float>();
    OnChoiceCalculateListener mOnChoiceCalculateListener;

    public RecordCalculatePopView(Context context) {
        super(context);
        init(context);
        initList(context);
    }

    public void init(Context context) {
        popView = LayoutInflater.from(context).inflate(R.layout.pop_calculate_view, null);
        //获取popwindow焦点
        setFocusable(true);
        //设置popwindow如果点击外面区域，便关闭。
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(0));
        setWidth(context.getResources().getDimensionPixelSize(R.dimen.pop_calculate_width));
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        initView();
        setContentView(popView);
    }

    private void initView() {
        lv_calculate = (CustomListView) popView.findViewById(R.id.lv_calculate);
    }

    private void initList(Context context) {
        float[] calculates = RecordConstans.commissions;
        for (float calculate : calculates) {
            calculateList.add(calculate);
        }
        mRecordCalculateAdapter = new RecordCalculateAdapter(context, calculateList);
        lv_calculate.setAdapter(mRecordCalculateAdapter);
        lv_calculate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                float calculate = (float)parent.getItemAtPosition(position);
                dismiss();
                if (mOnChoiceCalculateListener != null) {
                    mOnChoiceCalculateListener.onChoice(calculate);
                }
            }
        });
    }

    public void setOnChoiceCalculateListener(OnChoiceCalculateListener listener) {
        mOnChoiceCalculateListener = listener;
    }

    public interface OnChoiceCalculateListener {
        public void onChoice(float calculate);
    }
}
