package com.htgames.nutspoker.ui.adapter.shop;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.VipConfigEntity;
import com.htgames.nutspoker.ui.adapter.ListBaseAdapter;

import java.util.ArrayList;

/**
 * Created by 20150726 on 2015/12/21.
 */
public class ShopVipAdapter extends ListBaseAdapter<VipConfigEntity>{

    public ShopVipAdapter(Context context, ArrayList<VipConfigEntity> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_shop_vip_item, null);
            holder.ll_vip_item = (LinearLayout) view.findViewById(R.id.ll_vip_item);
            holder.tv_vip_desc = (TextView) view.findViewById(R.id.tv_vip_desc);
            holder.tv_vip_white = (TextView) view.findViewById(R.id.tv_vip_white);
            holder.tv_vip_black = (TextView) view.findViewById(R.id.tv_vip_black);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        VipConfigEntity vipConfigEntity = (VipConfigEntity)getItem(position);
        if(position % 2 == 0){
            holder.ll_vip_item.setBackgroundColor(context.getResources().getColor(R.color.shop_vip_item_black_bg_color));
        }else{
            holder.ll_vip_item.setBackgroundColor(context.getResources().getColor(R.color.shop_vip_item_white_bg_color));
        }
        holder.tv_vip_desc.setText(vipConfigEntity.desc);
        holder.tv_vip_white.setText(vipConfigEntity.whiteContent);
        holder.tv_vip_black.setText(vipConfigEntity.balckContent);
        return view;
    }

    protected final class ViewHolder {
        public LinearLayout ll_vip_item;
        public TextView tv_vip_desc;
        public TextView tv_vip_white;
        public TextView tv_vip_black;
    }
}
