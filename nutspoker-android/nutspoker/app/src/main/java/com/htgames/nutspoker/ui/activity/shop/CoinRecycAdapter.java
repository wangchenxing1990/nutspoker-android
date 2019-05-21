package com.htgames.nutspoker.ui.activity.shop;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiCode;
import com.netease.nim.uikit.bean.GameGoodsEntity;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalyticsEvent;
import com.htgames.nutspoker.ui.action.ShopAction;
import com.htgames.nutspoker.ui.activity.System.ShopActivity;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 周智慧 on 16/11/24.
 */

public class CoinRecycAdapter extends RecyclerView.Adapter<CoinRecycAdapter.CoinVH> {
    ArrayList<GameGoodsEntity> goodsList = new ArrayList<GameGoodsEntity>();
    private final LayoutInflater mLayoutInflater;
    private final Activity mContext;

    public CoinRecycAdapter(Activity context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }
    @Override
    public CoinVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.shop_recyc_item, parent, false);
        return new CoinVH(mContext, view);
    }

    @Override
    public void onBindViewHolder(CoinVH holder, int position) {
        GameGoodsEntity itemData = goodsList.get(position);
        holder.bind(position, itemData);
    }

    @Override
    public int getItemCount() {
        return goodsList == null ? 0 : goodsList.size();
    }

    public void setData(List list) {
        goodsList.clear();
        if (list != null) {
            goodsList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onViewDetachedFromWindow(CoinVH holder) {
        holder.destroy();
        LogUtil.i("zzh", "CoinRecycAdapter onViewDetachedFromWindow ");
        super.onViewDetachedFromWindow(holder);
    }


    public static class CoinVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ShopAction mShopAction;
        private Activity mContext;
        private GameGoodsEntity mData;
        private ImageView leftIcon;
        private TextView chip;//额度
        private TextView present;//赠送
        private TextView price;//价格
        public CoinVH(Activity context, View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mShopAction = new ShopAction(context, null);
            mContext = context;
            leftIcon = (ImageView) itemView.findViewById(R.id.shop_recycler_item_icon);
            chip = (TextView) itemView.findViewById(R.id.shop_recycler_item_chip);
            present = (TextView) itemView.findViewById(R.id.shop_recycler_item_present);
            price = (TextView) itemView.findViewById(R.id.shop_recycler_item_price);
            itemView.findViewById(R.id.shop_recycler_item_price_bg).setOnClickListener(this);
        }
        public void bind(int position, GameGoodsEntity data) {
            if (data == null) {
                return;
            }
            mData = data;
            leftIcon.setImageResource(R.mipmap.icon_me_coin);
            chip.setText(" " + mData.chip);
            present.setText("(送" + mData.present + ")");
//            Drawable leftDrawable = mContext.getResources().getDrawable(R.mipmap.diamond_white);
//            leftDrawable.setBounds(0, 0, ScreenUtil.dp2px(mContext, 15), ScreenUtil.dp2px(mContext, 15));
            price.setText(" " + mData.diamond);
//            price.setCompoundDrawables(leftDrawable, null, null, null);
        }

        public void destroy() {
            if (mShopAction != null) {
                mShopAction.onDestroy();
                mShopAction = null;
            }
        }

        @Override
        public void onClick(View v) {
            if (v == itemView || v.getId() == R.id.shop_recycler_item_price_bg) {
                showConfimDialog(mData, false);
            }
        }

        public void showConfimDialog(final GameGoodsEntity gameGoodsEntity , final boolean isBuyVip) {
            String message = mContext.getResources().getString(R.string.buy_goods_confirm , gameGoodsEntity.diamond , gameGoodsEntity.name);
//            String message = mContext.getResources().getString(R.string.buy_goods_confirm , gameGoodsEntity.getDiamond() , "兑换");
            EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(mContext, null,
                    message, null, null, true, R.layout.dialog_easy_alert_new,
                    new EasyAlertDialogHelper.OnDialogActionListener() {
                        @Override
                        public void doCancelAction() {
                        }
                        @Override
                        public void doOkAction() {
                            buyGoods(gameGoodsEntity , isBuyVip);
                        }
                    });
            dialog.show();
        }

        /**
         * 购买商品
         */
        public void buyGoods(final GameGoodsEntity gameGoodsEntity , final boolean isBuyVip) {
            mShopAction.buyGoods(String.valueOf(gameGoodsEntity.id), true, new RequestCallback() {
                @Override
                public void onResult(int code, String result, Throwable var3) {
                    if (code == 0) {
                        try {
                            JSONObject json = new JSONObject(result);
                            int diamond = json.getJSONObject("data").optInt("diamond");
                            int coins = json.getJSONObject("data").optInt("coins");
                            buyGoodsSuccess(coins, diamond, isBuyVip);
                            UmengAnalyticsEvent.onEventBuyGoods(mContext, gameGoodsEntity); //加入统计
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (code == ApiCode.CODE_BALANCE_INSUFFICIENT) {
                        Toast.makeText(mContext, R.string.shop_buy_failure_balance_insufficient, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, R.string.shop_buy_failure, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailed() {
                }
            });
        }

        /**
         * 购买商品成功
         * @param coins
         * @param diamong
         */
        public void buyGoodsSuccess(int coins , int diamong ,boolean isBuyVip){
            UserPreferences.getInstance(mContext).setCoins(coins);
            UserPreferences.getInstance(mContext).setDiamond(diamong);
            if (mContext instanceof ShopActivity) {
                ((ShopActivity) mContext).updateCoinDiamondUI();
            }
            if(isBuyVip){
                //是购买VIP，获取用户信息
                NimUserInfoCache.getInstance().getUserInfoFromRemote(DemoCache.getAccount(), new com.netease.nimlib.sdk.RequestCallback<NimUserInfo>() {
                    @Override
                    public void onSuccess(NimUserInfo userInfo) {
                        //更新VIP状态
                    }
                    @Override
                    public void onFailed(int i) {
                    }
                    @Override
                    public void onException(Throwable throwable) {
                    }
                });
            }
        }
    }
}
