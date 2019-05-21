package com.htgames.nutspoker.ui.activity.shop;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiConfig;
import com.netease.nim.uikit.bean.DiamondGoodsEntity;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.thirdPart.billing.util.IabHelper;
import com.htgames.nutspoker.thirdPart.billing.util.IabResult;
import com.htgames.nutspoker.thirdPart.billing.util.Inventory;
import com.htgames.nutspoker.thirdPart.billing.util.Purchase;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalyticsEvent;
import com.htgames.nutspoker.ui.activity.System.ShopActivity;
import com.htgames.nutspoker.ui.helper.PayHelp;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.PayRmbView;
import com.netease.nim.uikit.UIConstants;
import com.netease.nim.uikit.common.util.log.LogUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 周智慧 on 16/11/24.
 */

public class DiamondRecycAdapter extends RecyclerView.Adapter<DiamondRecycAdapter.DiamondVH> {
    public static final String TAG = DiamondRecycAdapter.class.getSimpleName();
    ArrayList<DiamondGoodsEntity> diamondGoodsList = new ArrayList<DiamondGoodsEntity>();
    private final LayoutInflater mLayoutInflater;
    private final Activity mContext;

    public DiamondRecycAdapter(Activity context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }
    @Override
    public DiamondVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.shop_recyc_item, parent, false);
        return new DiamondVH(mContext, view);
    }

    @Override
    public void onBindViewHolder(DiamondVH holder, int position) {
        DiamondGoodsEntity itemData = diamondGoodsList.get(position);
        holder.bind(position, itemData);
    }

    @Override
    public int getItemCount() {
        return diamondGoodsList == null ? 0 : diamondGoodsList.size();
    }

    public void setData(List list) {
        diamondGoodsList.clear();
        if (list != null) {
            diamondGoodsList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onViewDetachedFromWindow(DiamondVH holder) {
//        holder.destroy();
        LogUtil.i("zzh", "DiamondRecycAdapter onViewDetachedFromWindow ");
        super.onViewDetachedFromWindow(holder);
    }

    public static class DiamondVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        //谷歌支付
        boolean mIsPremium = false; // Does the user have the premium upgrade?
        static final int RC_REQUEST = 10001;
        IabHelper mHelper;
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgjqkgCFM2gkGGaUFavINbGsIH6SlvhfUgByZ0U3cC4IHGku32h4M/VD7ZrriX2M6Mn5sofyrajyv4ahJDhPMWZgfqqtET00USnJdQGcVcBylrQTEZGCBNS2Q7OasXR47iDFZdLj75hq8DiaWhvjpOS2j6g+SgwoMPHfx5NUfdJ/N5pJGS7lGq7wCEpfJJmtw1cmChoYUorzDR+iAaB7VpGyXA7WrBDnZa2vkmfbifYDfo/lFlQFcWOS5Vtoch95hsJAtGfCXPNZ031JpmNxDMlFOdFjGDj7P9nLm9EbL0XPW1of8dBpXQrNjNRphmD1GxOqIpnX8G9pmKKlKOOmZ4wIDAQAB";
        //台湾的KEY
        String base64EncodedPublicTwKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnGs/ly8w/zYYqSSyqAhTekpnsL171eLBMfOSG1Q6aXKWFDebHwv2V5RTo3J9tU6oz9Sq+9Z0LI/DAJWEBF65QGZ0w46ij4rE+VmQFbprLJPB8LYn1vHyK0/7PvIQJqN5qQOBEvpJ2rkve+ukBOOw2uV1y36n/wsSVCpOX73Pm+7hOUlRg2F0/c6ZEGI5V9jsb7U/Z0JURxR80f2y5vOkvd+Rf6LHCuMOZaJeJOEOW1Oi6erh0xX9j+gUr1NnF8tIW7tDItEALb2U5mv0F/uA603Z1jmj1e1w6hqhqB8yQtbBzErQ4VfagNo6QP09Qi5Ch54FM3vAdnPDC6iyxreDUQIDAQAB";
        /**
         * 谷歌支付是否无效
         */
        boolean isBillingUnavailable = false;
        private WeakReference<Activity> weakActivity;
        private DiamondGoodsEntity mData;
        private ImageView leftIcon;
        private TextView chip;//额度
        private TextView present;//赠送
        private TextView price;//价格
        public DiamondVH(Activity context, View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            weakActivity = new WeakReference<Activity>(context);
            leftIcon = (ImageView) itemView.findViewById(R.id.shop_recycler_item_icon);
            chip = (TextView) itemView.findViewById(R.id.shop_recycler_item_chip);
            present = (TextView) itemView.findViewById(R.id.shop_recycler_item_present);
            price = (TextView) itemView.findViewById(R.id.shop_recycler_item_price);
            itemView.findViewById(R.id.shop_recycler_item_price_bg).setOnClickListener(this);
        }
        public void bind(int position, DiamondGoodsEntity data) {
            if (data == null) {
                return;
            }
            mData = data;
            leftIcon.setImageResource(R.mipmap.icon_me_diamond);
            chip.setText(" " + mData.diamond);
            present.setVisibility(View.GONE);
            price.setText("￥" + mData.price);
            price.setCompoundDrawables(null, null, null, null);
            initBilling();
        }

        private void initBilling() {
            if (weakActivity == null || weakActivity.get() == null) {
                return;
            }
            mHelper = new IabHelper(weakActivity.get(), base64EncodedPublicTwKey);
            mHelper.enableDebugLogging(true);
            // Start setup. This is asynchronous and the specified listener will be called once setup completes.
            LogUtil.i(TAG, "检查是否有权限和连接到Google Billing service是否成功.");
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    LogUtil.i(TAG, "Setup finished.");
                    if (!result.isSuccess()) {
                        LogUtil.i(TAG, "result : " + result + ";" + result.getResponse());
                        if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE) {
                            isBillingUnavailable = true;
                        }
                        return;
                    }
                    // Have we been disposed of in the meantime? If so, quit.
                    if (mHelper == null)
                        return;
                    // IAB is fully set up. Now, let's get an inventory of stuff we own.
                    LogUtil.i(TAG, "设置成功，查询库存.");
                    List<String> productsIds = new ArrayList<String>();
                    if (!TextUtils.isEmpty(mData.skuId)) {
                        productsIds.add(mData.skuId);
                    }
                    LogUtil.i(TAG, "productsIds :" + productsIds.size());
                    if (productsIds != null && productsIds.size() != 0) {
                        mHelper.queryInventoryAsync(true, productsIds, mGotInventoryListener);
                    }
                }
            });
        }

        Inventory mInventory = null;
        IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                LogUtil.i(TAG, "查询库存结束.");
                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null)
                    return;
                // Is it a failure?
                if (result.isFailure()) {
                    LogUtil.i(TAG, "无法查询库存." + result);
                    return;
                }
                LogUtil.i(TAG, "查询库存成功的.");
                LogUtil.i(TAG, "初始庫存查詢完畢;使主UI.");
                mInventory = new Inventory();
                mInventory.mSkuMap.putAll(inventory.mSkuMap);
                mInventory.mPurchaseMap.putAll(inventory.mPurchaseMap);
                //
                LogUtil.i(TAG, "size :" + mInventory.mPurchaseMap.entrySet().size() + mInventory.mSkuMap.entrySet().size());
                for (Map.Entry<String, Purchase> entry : inventory.mPurchaseMap.entrySet()) {
                    LogUtil.i(TAG, entry.getValue().getOriginalJson() + ":" + entry.getValue().getSignature());
                }
            }
        };

        /**
         * Verifies the developer payload of a purchase.
         */
        boolean verifyDeveloperPayload(Purchase p) {
            String payload = p.getDeveloperPayload();
            return true;
        }

        //购买商品时的通信过程
        IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result, final Purchase purchase) {
                LogUtil.i(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
                // if we were disposed of in the meantime, quit.
                if (mHelper == null) {
                    return;
                }
                if (result.isFailure()) {
                    if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {
                        //已经拥有
                        String skuId = mData.skuId;
                        if (mInventory.mPurchaseMap != null && mInventory.mPurchaseMap.containsKey(skuId)) {
                            LogUtil.i(TAG, "已经拥有 ,消耗掉 :" + skuId);
                            mHelper.consumeAsync(mInventory.mPurchaseMap.get(skuId), mConsumeFinishedListener);
                        } else {
                        }
                    } else {
                    }
                    return;
                }
                if (!verifyDeveloperPayload(purchase)) {
                    return;
                }
                String signatureData = purchase.getOriginalJson();
                final String signature = purchase.getSignature();
                LogUtil.i(TAG , "signatureData :" + signatureData);
                LogUtil.i(TAG , "signature :" + signature);
                LogUtil.i(TAG , "orderId :" + purchase.getOrderId());
                if (mData != null) {
                    UmengAnalyticsEvent.onEventTopUpPrice(weakActivity.get(), mData); //加入统计
                }
                ((ShopActivity)weakActivity.get()).checkPayment(signatureData, signature, new RequestCallback() {
                    @Override
                    public void onResult(int code, String result, Throwable var3) {
                        if (code == 0) {
                            LogUtil.i(TAG, "通过校验");
                            LogUtil.i(TAG, "Purchase successful.");
                        } else {
                            LogUtil.i(TAG, "校验失败");
                        }
                        mHelper.consumeAsync(purchase, mConsumeFinishedListener);//消耗掉商品
                    }
                    @Override
                    public void onFailed() {
                        LogUtil.i(TAG, "校验失败");
                        mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                    }
                });
            }
        };

        // Called when consumption is complete,消耗产品时的通信过程
        IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
            public void onConsumeFinished(Purchase purchase, IabResult result) {
                LogUtil.i(TAG, "Consumption finished. Purchase: " + purchase  + ", result: " + result);
                //在IabHelper.OnConsumeFinishedListener的回调用于处理成功调用成功支付的逻辑(这里可能还需要一步去调用远程服务器验证)
                // if we were disposed of in the meantime, quit.
                if (mHelper == null){
                    return;
                }
                if (result.isSuccess()) {
                    // successfully consumed, so we apply the effects of the item in
                    // our
                    // game world's logic, which in our case means filling the gas
                    // tank a bit
                    LogUtil.i(TAG, "Consumption successful. Provisioning.");
//                mTank = mTank == TANK_MAX ? TANK_MAX : mTank + 1;
//                saveData();
//                alert("You filled 1/4 tank. Your tank is now " + String.valueOf(mTank) + "/4 full!");
                } else {
                }
                LogUtil.i(TAG, "End consumption flow.");
            }
        };

        @Override
        public void onClick(View v) {
            if (v == itemView || v.getId() == R.id.shop_recycler_item_price_bg) {
                byDiamond();//购买钻石
            }
        }

        public void byDiamond() {
            if (weakActivity == null || weakActivity.get() == null) {
                return;
            }
            String payload = "";
            LogUtil.i(TAG, mData.skuId);
            if (ApiConfig.AppVersion.isTaiwanVersion) {
                if (isBillingUnavailable) {
                    //谷歌支付无效
                    Toast.makeText(ChessApp.sAppContext, R.string.billing_unavailable , Toast.LENGTH_SHORT).show();
                } else {
                    mHelper.launchPurchaseFlow(weakActivity.get(), mData.skuId, RC_REQUEST, mPurchaseFinishedListener, payload);
                }
            } else {

//                    dismiss();
//                    if(mPayDialog == null)
//                        mPayDialog = new PayDialog(activity);
//                    mPayDialog.showUiMoney(buyDiamondGoodsEntity.getPrice(),buyDiamondGoodsEntity.getDiamond());

//                Toast.makeText(mContext, R.string.topup_function_not_opened, Toast.LENGTH_SHORT).show();
            }



            PayRmbView payRmbView = new PayRmbView(weakActivity.get(), "￥" + mData.price, new PayRmbView.Callback() {
                @Override
                public void onSure(int payType) {
                    if (payType == UIConstants.PAY_WX) {
                        PayHelp.doWxPayNew(weakActivity, mData.id);
                    } else if (payType == UIConstants.PAY_ALI) {
                        PayHelp.doAliPay(weakActivity.get(), mData.id);
                    }
                }
            });
            payRmbView.showAtLocation(itemView, Gravity.BOTTOM, 0, 0);
        }
    }
}
