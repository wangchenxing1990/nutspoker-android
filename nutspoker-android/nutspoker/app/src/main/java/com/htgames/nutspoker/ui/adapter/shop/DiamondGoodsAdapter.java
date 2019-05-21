package com.htgames.nutspoker.ui.adapter.shop;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiConfig;
import com.netease.nim.uikit.bean.DiamondGoodsEntity;
import com.htgames.nutspoker.config.shop.ShopGoodsConfig;
import com.htgames.nutspoker.view.shop.DiamondView;
import com.htgames.nutspoker.ui.adapter.ListBaseAdapter;

import java.util.ArrayList;
import java.util.Map;

/**
 * 宝石商品适配器
 */
public class DiamondGoodsAdapter extends ListBaseAdapter<DiamondGoodsEntity> {
    Map<Integer, Integer> diamondGoodsList;

    public DiamondGoodsAdapter(Context context, ArrayList<DiamondGoodsEntity> list) {
        super(context, list);
        diamondGoodsList = ShopGoodsConfig.getDiamondGoodsList();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_shop_diamond_item, null);
            holder.iv_diamond_icon = (ImageView) view.findViewById(R.id.iv_diamond_icon);
            holder.tv_diamond_name = (TextView) view.findViewById(R.id.tv_diamond_name);
            holder.tv_diamond_price = (TextView) view.findViewById(R.id.tv_diamond_price);
            holder.mDiamondView = (DiamondView) view.findViewById(R.id.mDiamondView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        DiamondGoodsEntity diamondGoodsEntity = (DiamondGoodsEntity) getItem(position);
        if (!TextUtils.isEmpty(diamondGoodsEntity.name)) {
            holder.tv_diamond_name.setText(diamondGoodsEntity.name);
        }
        holder.mDiamondView.setNum(diamondGoodsEntity.diamond);
        String coinSymbol = "￥";
        if(ApiConfig.AppVersion.isTaiwanVersion){
            coinSymbol = "NT$";
        }
        holder.tv_diamond_price.setText(coinSymbol + diamondGoodsEntity.price);//现价
        if (diamondGoodsList.containsKey(diamondGoodsEntity.id)) {
            holder.iv_diamond_icon.setImageResource(diamondGoodsList.get(diamondGoodsEntity.id));
        }
        return view;
    }

    protected final class ViewHolder {
        public ImageView iv_diamond_icon;
        public TextView tv_diamond_name;
        public TextView tv_diamond_price;
        public DiamondView mDiamondView;
    }
}
