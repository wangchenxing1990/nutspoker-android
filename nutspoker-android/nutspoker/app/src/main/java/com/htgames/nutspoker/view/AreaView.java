package com.htgames.nutspoker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.ui.adapter.team.AreaAdapter;
import com.htgames.nutspoker.interfaces.AreaClickListener;
import com.htgames.nutspoker.view.widget.CustomGridView;
import com.htgames.nutspoker.view.widget.CustomListView;

/**
 * Created by 20150726 on 2015/10/12.
 */
public class AreaView extends LinearLayout implements AdapterView.OnItemClickListener{
    View view;
    Context context;
    private CustomGridView gv_hotcity;
    private CustomListView lv_province;
    private AreaAdapter mHotCityAdapter;
    private AreaAdapter mProvinceAdapter;
    String[] hotCitys;
    String[] provinces;

    public AreaView(Context context) {
        super(context);
        init(context);
    }

    public AreaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context){
        this.context = context;
        hotCitys =  getResources().getStringArray(R.array.arae_hot_citys);
        provinces =  getResources().getStringArray(R.array.arae_provinces);
        view = LayoutInflater.from(context).inflate(R.layout.layout_area, null);
        initView();
        setAreaList();
        addView(view);
    }

    private void initView() {
        gv_hotcity = (CustomGridView)view.findViewById(R.id.gv_hotcity);
        lv_province = (CustomListView)view.findViewById(R.id.lv_province);
        gv_hotcity.setOnItemClickListener(this);
        lv_province.setOnItemClickListener(this);
    }

    private void setAreaList() {
        mHotCityAdapter = new AreaAdapter(context , hotCitys , AreaAdapter.TYPE_AREA_HOTCITY);
        mProvinceAdapter = new AreaAdapter(context , provinces , AreaAdapter.TYPE_AREA_PROVINCE);
        gv_hotcity.setAdapter(mHotCityAdapter);
        lv_province.setAdapter(mProvinceAdapter);
    }

    AreaClickListener mAreaClickListener;

    public void setAreaClickListener(AreaClickListener listener){
        this.mAreaClickListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String area = (String)parent.getItemAtPosition(position);
        if(mAreaClickListener != null){
            mAreaClickListener.onAreaClick(area);
        }
    }
}
