package com.htgames.nutspoker.chat.region.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.region.RegionConstants;
import com.htgames.nutspoker.chat.region.RegionEntity;
import com.htgames.nutspoker.ui.adapter.ListBaseAdapter;

import java.util.ArrayList;

/**
 * 区域Adapter
 */
public class RegionAdapter extends ListBaseAdapter<RegionEntity> {
    public RegionAdapter(Context context, ArrayList<RegionEntity> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_region_item, null);
            holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            holder.iv_region_arrow = (ImageView) view.findViewById(R.id.iv_region_arrow);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        RegionEntity regionEntity = (RegionEntity) getItem(position);
        holder.tv_name.setText(regionEntity.name);
        if(RegionConstants.isProvinceCity(regionEntity.name) || regionEntity.type == RegionConstants.TYPE_CITY){
            //省级市不显示箭头
            holder.iv_region_arrow.setVisibility(View.GONE);
        } else{
            holder.iv_region_arrow.setVisibility(View.VISIBLE);
        }
        return view;
    }

    protected final class ViewHolder {
        public TextView tv_name;
        public ImageView iv_region_arrow;
    }
}
