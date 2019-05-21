package com.htgames.nutspoker.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.htgames.nutspoker.R;

import java.util.ArrayList;

/**
 */
public class RecordCalculateAdapter extends ListBaseAdapter<Float> {


    public RecordCalculateAdapter(Context context, ArrayList<Float> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_record_calculate_item, null);
            holder.tv_calculate = (TextView) view.findViewById(R.id.tv_calculate);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        int calculat = (int) ((float) getItem(position) * 100);
        holder.tv_calculate.setText(calculat + "%");
        return view;
    }

    protected final class ViewHolder {
        public TextView tv_calculate;
    }
}
