package com.htgames.nutspoker.ui.adapter.team;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.htgames.nutspoker.R;

/**
 * Created by 20150726 on 2015/10/12.
 */
public class AreaAdapter extends BaseAdapter{
    Context context;
    String[] areas;
    LayoutInflater inflater;
    public final static int TYPE_AREA_HOTCITY = 1;
    public final static int TYPE_AREA_PROVINCE = 2;
    private int type = TYPE_AREA_HOTCITY;

    public AreaAdapter(Context context, String[] areas , int type) {
        this.context = context;
        this.areas = areas;
        this.type = type;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return areas == null ? 0 : areas.length;
    }

    @Override
    public String getItem(int position) {
        if(areas != null){
            return areas[position];
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_area_item, null);
            holder.tv_area_name = (TextView) view.findViewById(R.id.tv_area_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tv_area_name.setText(areas[position]);
        if(type == TYPE_AREA_PROVINCE){
            holder.tv_area_name.setGravity(Gravity.LEFT);
        }
        return view;
    }

    protected final class ViewHolder {
        public TextView tv_area_name;
    }
}
