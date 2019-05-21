package com.htgames.nutspoker.view.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiConfig;
import com.htgames.nutspoker.thirdPart.billing.util.IabHelper;
import com.htgames.nutspoker.thirdPart.billing.util.IabResult;
import com.htgames.nutspoker.thirdPart.billing.util.Inventory;
import com.htgames.nutspoker.thirdPart.billing.util.Purchase;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.ui.activity.System.ShopActivity;
import com.htgames.nutspoker.ui.adapter.shop.DiamondGoodsAdapter;
import com.netease.nim.uikit.bean.DiamondGoodsEntity;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalyticsEvent;
import com.htgames.nutspoker.view.ResultDataView;
import com.htgames.nutspoker.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class DiamondDialog extends Dialog implements View.OnClickListener {
    private final static String TAG = "IabHelper";
    private Activity activity;
    ListView lv_list;
    View view;
//    RequestQueue mRequestQueue;
    ImageView btn_close;
    ArrayList<DiamondGoodsEntity> diamondGoodsList;
    DiamondGoodsAdapter mDiamondGoodsAdapter;
    ResultDataView mResultDataView;
    //谷歌支付
    boolean mIsPremium = false; // Does the user have the premium upgrade?
//    boolean mSubscribedToInfiniteGas = false; // Does the user have an active subscription to the infinite gas plan?
    // SKUs for our products: the premium upgrade (non-consumable) and gas (consumable)
    //SKU唯一商品 ID
//    static final String SKU_PREMIUM = "premium";//库存额外费用
//    static final String SKU_GAS = "gas";//
//    static final String SKU_GAS1 = "com.htgames.chesscircle.2001";
    // SKU for our subscription (infinite gas) 无限气体
//    static final String SKU_INFINITE_GAS = "caryinfinite_gas";
    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;
    // Graphics for the gas gauge
//    static int[] TANK_RES_IDS = {R.mipmap.icon_diamond_goods_2001,
//            R.mipmap.icon_diamond_goods_2002, R.mipmap.icon_diamond_goods_2003,
//            R.mipmap.icon_diamond_goods_2004, R.mipmap.icon_diamond_goods_2005};
    // How many units (1/4 tank is our unit) fill in the tank.
//    static final int TANK_MAX = 5;
    // Current amount of gas in tank, in units
//    int mTank;
    // The helper object
    IabHelper mHelper;
    String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgjqkgCFM2gkGGaUFavINbGsIH6SlvhfUgByZ0U3cC4IHGku32h4M/VD7ZrriX2M6Mn5sofyrajyv4ahJDhPMWZgfqqtET00USnJdQGcVcBylrQTEZGCBNS2Q7OasXR47iDFZdLj75hq8DiaWhvjpOS2j6g+SgwoMPHfx5NUfdJ/N5pJGS7lGq7wCEpfJJmtw1cmChoYUorzDR+iAaB7VpGyXA7WrBDnZa2vkmfbifYDfo/lFlQFcWOS5Vtoch95hsJAtGfCXPNZ031JpmNxDMlFOdFjGDj7P9nLm9EbL0XPW1of8dBpXQrNjNRphmD1GxOqIpnX8G9pmKKlKOOmZ4wIDAQAB";
    //台湾的KEY
    String base64EncodedPublicTwKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnGs/ly8w/zYYqSSyqAhTekpnsL171eLBMfOSG1Q6aXKWFDebHwv2V5RTo3J9tU6oz9Sq+9Z0LI/DAJWEBF65QGZ0w46ij4rE+VmQFbprLJPB8LYn1vHyK0/7PvIQJqN5qQOBEvpJ2rkve+ukBOOw2uV1y36n/wsSVCpOX73Pm+7hOUlRg2F0/c6ZEGI5V9jsb7U/Z0JURxR80f2y5vOkvd+Rf6LHCuMOZaJeJOEOW1Oi6erh0xX9j+gUr1NnF8tIW7tDItEALb2U5mv0F/uA603Z1jmj1e1w6hqhqB8yQtbBzErQ4VfagNo6QP09Qi5Ch54FM3vAdnPDC6iyxreDUQIDAQAB";
    DiamondGoodsEntity buyDiamondGoodsEntity;
    /**
     * 谷歌支付是否无效
     */
    boolean isBillingUnavailable = false;

    private void initBilling() {
        mHelper = new IabHelper(activity, base64EncodedPublicTwKey);
        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);
        // Start setup. This is asynchronous and the specified listener will be called once setup completes.
        LogUtil.i(TAG, "检查是否有权限和连接到Google Billing service是否成功.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                LogUtil.i(TAG, "Setup finished.");

                if (!result.isSuccess()) {
//                    complain("应用付费发生问题: " + result);
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
                for (DiamondGoodsEntity diamondGoodsEntity : diamondGoodsList) {
                    if (!TextUtils.isEmpty(diamondGoodsEntity.skuId)) {
                        productsIds.add(diamondGoodsEntity.skuId);
                    }
                }
                LogUtil.i(TAG, "productsIds :" + productsIds.size());
                if (productsIds != null && productsIds.size() != 0) {
                    setWaitScreen(true);
                    mHelper.queryInventoryAsync(true, productsIds, mGotInventoryListener);
                }
//                mHelper.queryInventoryAsync(mGotInventoryListener);
//                requestItemInfo(SKU_GAS1);
            }
        });
    }

//    public void requestItemInfo(final String strItemTypeIdSet) {
////        List lstStrTypeId = java.util.Arrays.asList(strItemTypeIdSet.split(" "));
//        ArrayList<String> itemList = new ArrayList<>();
//        itemList.add(SKU_GAS1);
//        Log.d(TAG, "Querying inventory.");
//        try {
//            mHelper.queryInventoryAsync(true, itemList, mGotInventoryListener);
//        } catch (Exception e) {
//            Log.e(TAG, "Querying inventory Error.");
//        }
//    }

    void complain(String message) {
        LogUtil.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(activity);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        LogUtil.i(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }
    Inventory mInventory = null;
    // Listener that's called when we finish querying the items and
    // subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            LogUtil.i(TAG, "查询库存结束.");
            // Have we been disposed of in the meantime? If so, quit.
            setWaitScreen(false);
            if (mHelper == null)
                return;
            // Is it a failure?
            if (result.isFailure()) {
                complain("无法查询库存: " + result);
                return;
            }
            LogUtil.i(TAG, "查询库存成功的.");
            /*
             * Check for items we own. Notice that for each purchase, we check
			 * the developer payload to see if it's correct! See
			 * verifyDeveloperPayload().
			 */
            // Do we have the premium upgradge?
            //检测我们自己的项目，请注意，对于每一个购买，我们检测开发人员有效载荷
//            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
//            Purchase premiumPurchase = inventory.getPurchase(SKU_GAS1);
//            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
//            Log.d(TAG, "用戶 " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));//用户是否需要额外费用

            // Do we have the infinite gas plan?
//            Purchase infiniteGasPurchase = inventory.getPurchase(SKU_INFINITE_GAS);
//            mSubscribedToInfiniteGas = (infiniteGasPurchase != null && verifyDeveloperPayload(infiniteGasPurchase));
//            Log.d(TAG, "用戶 " + (mSubscribedToInfiniteGas ? "HAS" : "不具有") + " 無限氣認購.");
//            Log.d(TAG, "User " + (mSubscribedToInfiniteGas ? "HAS" : "DOES NOT HAVE") + " infinite gas subscription.");
//            if (mSubscribedToInfiniteGas)
//                mTank = TANK_MAX;

            // Check for gas delivery -- if we own gas, we should fill up the tank immediately
//            Purchase gasPurchase = inventory.getPurchase(SKU_GAS1);
//            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
//                Log.d(TAG, "We have gas. Consuming it. （我们有库存，消耗它）");
////                mHelper.consumeAsync(gasPurchase, mConsumeFinishedListener);
//                //購買成功,調用消耗產品
////                return;
//            }
//            updateUi();
            setWaitScreen(false);
            LogUtil.i(TAG, "初始庫存查詢完畢;使主UI.");

            mInventory = new Inventory();
            mInventory.mSkuMap.putAll(inventory.mSkuMap);
            mInventory.mPurchaseMap.putAll(inventory.mPurchaseMap);
            //
            LogUtil.i(TAG, "size :" + mInventory.mPurchaseMap.entrySet().size() + mInventory.mSkuMap.entrySet().size());
            for (Map.Entry<String, Purchase> entry : inventory.mPurchaseMap.entrySet()) {
                LogUtil.i(TAG, entry.getValue().getOriginalJson() + ":" + entry.getValue().getSignature());
            }
//            SkuDetails purchaseGas1 = inventory.getSkuDetails(SKU_GAS1);
//            if (purchaseGas1 != null) {
//                Log.d(TAG, SKU_GAS1 + ":" + purchaseGas1.getSku());//用户是否需要额外费用
//                Log.d(TAG, SKU_GAS1 + ":" + purchaseGas1.getPrice());//用户是否需要额外费用
//                Log.d(TAG, SKU_GAS1 + ":" + purchaseGas1.getType());//用户是否需要额外费用
//            }
        }
    };

    // User clicked the "Buy Gas" button
//    public void onBuyGasButtonClicked(View arg0) {
//        Log.d(TAG, "Buy gas button clicked.");
//        if (mSubscribedToInfiniteGas) {
//            complain("No need! You're subscribed to infinite gas. Isn't that awesome?");
//            return;
//        }
//        if (mTank >= TANK_MAX) {
//            complain("Your tank is full. Drive around a bit!");
//            return;
//        }
//        // launch the gas purchase UI flow.
//        // We will be notified of completion via mPurchaseFinishedListener
//        setWaitScreen(true);
//        Log.d(TAG, "Launching purchase flow for gas.");
//
//		/*
//         * TODO: for security, generate your payload here for verification. See
//		 * the comments on verifyDeveloperPayload() for more info. Since this is
//		 * a SAMPLE, we just use an empty string, but on a production app you
//		 * should carefully generate this.
//		 */
//        String payload = "";
//        mHelper.launchPurchaseFlow(activity, SKU_GAS, RC_REQUEST, mPurchaseFinishedListener, payload);
//    }

//    public void onUpgradeAppButtonClicked(View arg0) {
//        Log.d(TAG, "Upgrade button clicked; launching purchase flow for upgrade.");
//        setWaitScreen(true);
//
//		/*
//		 * TODO: for security, generate your payload here for verification. See
//		 * the comments on verifyDeveloperPayload() for more info. Since this is
//		 * a SAMPLE, we just use an empty string, but on a production app you
//		 * should carefully generate this.
//		 */
//        String payload = "";
//
//        mHelper.launchPurchaseFlow(activity, SKU_PREMIUM, RC_REQUEST, mPurchaseFinishedListener, payload);
//    }

    // "Subscribe to infinite gas" button clicked. Explain to user, then start
    // purchase
    // �q�\infinite gas
//    public void onInfiniteGasButtonClicked(View arg0) {
//        if (!mHelper.subscriptionsSupported()) {
//            complain("Subscriptions not supported on your device yet. Sorry!");
//            return;
//        }
//
//		/*
//		 * TODO: for security, generate your payload here for verification. See
//		 * the comments on verifyDeveloperPayload() for more info. Since this is
//		 * a SAMPLE, we just use an empty string, but on a production app you
//		 * should carefully generate this.
//		 */
//        String payload = "";
//
//        setWaitScreen(true);
//        Log.d(TAG, "Launching purchase flow for infinite gas subscription.");
//        mHelper.launchPurchaseFlow(activity, SKU_INFINITE_GAS, IabHelper.ITEM_TYPE_SUBS, RC_REQUEST, mPurchaseFinishedListener, payload);
//    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.i(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper != null && requestCode == RC_REQUEST) {
            LogUtil.i(TAG, "mHelper != null");
            // Pass on the activity result to the helper for handling
            if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
                LogUtil.i(TAG, "handleActivityResult :" + mHelper.handleActivityResult(requestCode, resultCode, data));
                // not handled, so handle it ourselves (here's where you'd perform any handling of activity results not related to in-app  billing...
                if (resultCode == 0 && data != null) {
//                    try {
//                        int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
//                        String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
//                        String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
//                        Log.d(TAG, "purchaseData ：" + purchaseData);
//                        Log.d(TAG, "dataSignature ：" + dataSignature);
//                        JSONObject jo = new JSONObject(purchaseData);
//                        String sku = jo.getString("productId");
//                        alert("You have bought the " + sku + ". Excellent choice, adventurer!");
//                    } catch (Exception e) {
//                        Log.d(TAG, "Failed to parse purchase data.");
//                        e.printStackTrace();
//                    }
                }
                return true;
            } else {
                LogUtil.i(TAG, "onActivityResult handled by IABUtil.");
            }
        }
        return false;
    }

    /**
     * Verifies the developer payload of a purchase.
     */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */
        return true;
    }

    //购买商品时的通信过程
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, final Purchase purchase) {
            LogUtil.i(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) {
                setWaitScreen(false);
                return;
            }

            if (result.isFailure()) {
                if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {
                    //已经拥有
                    String skuId = buyDiamondGoodsEntity.skuId;
                    if (mInventory.mPurchaseMap != null && mInventory.mPurchaseMap.containsKey(skuId)) {
                        LogUtil.i(TAG, "已经拥有 ,消耗掉 :" + skuId);
                        mHelper.consumeAsync(mInventory.mPurchaseMap.get(skuId), mConsumeFinishedListener);
                    } else {
                        setWaitScreen(false);
                    }
                } else {
//                    complain("Error purchasing: " + result);
                    setWaitScreen(false);
                }
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                setWaitScreen(false);
                return;
            }

            String signatureData = purchase.getOriginalJson();
            final String signature = purchase.getSignature();
            LogUtil.i(TAG , "signatureData :" + signatureData);
            LogUtil.i(TAG , "signature :" + signature);
            LogUtil.i(TAG , "orderId :" + purchase.getOrderId());
            if (buyDiamondGoodsEntity != null) {
                UmengAnalyticsEvent.onEventTopUpPrice(getContext(), buyDiamondGoodsEntity); //加入统计
            }
            ((ShopActivity)activity).checkPayment(signatureData, signature, new RequestCallback() {
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
//            if (purchase.getSku().equals(SKU_GAS)) {
//                // bought 1/4 tank of gas. So consume it.
//                Log.d(TAG, "Purchase is gas. Starting gas consumption.");
//                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
//            } else if (purchase.getSku().equals(SKU_PREMIUM)) {
//                // bought the premium upgrade!
//                Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
//                alert("Thank you for upgrading to premium!");
//                mIsPremium = true;
//                updateUi();
//                setWaitScreen(false);
//            } else if (purchase.getSku().equals(SKU_INFINITE_GAS)) {
//                // bought the infinite gas subscription
//                Log.d(TAG, "Infinite gas subscription purchased.");
//                alert("Thank you for subscribing to infinite gas!");
//                mSubscribedToInfiniteGas = true;
//                mTank = TANK_MAX;
//                updateUi();
//                setWaitScreen(false);
//            }
        }
    };

    // Called when consumption is complete,消耗产品时的通信过程
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            LogUtil.i(TAG, "Consumption finished. Purchase: " + purchase  + ", result: " + result);
            //在IabHelper.OnConsumeFinishedListener的回调用于处理成功调用成功支付的逻辑(这里可能还需要一步去调用远程服务器验证)
            // if we were disposed of in the meantime, quit.
            if (mHelper == null){
                setWaitScreen(false);
                return;
            }

            // We know this is the "gas" sku because it's the only one we
            // consume,
            // so we don't check which sku was consumed. If you have more than
            // one
            // sku, you probably should check...
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
//                complain("Error while consuming: " + result);
            }
//            updateUi();
            setWaitScreen(false);
            LogUtil.i(TAG, "End consumption flow.");
        }
    };

    // Drive button clicked. Burn gas!
//    public void onDriveButtonClicked(View arg0) {
//        Log.d(TAG, "Drive button clicked.");
//        if (!mSubscribedToInfiniteGas && mTank <= 0)
//            alert("Oh, no! You are out of gas! Try buying some!");
//        else {
//            if (!mSubscribedToInfiniteGas)
//                --mTank;
////            saveData();
//            alert("Vroooom, you drove a few miles.");
//            updateUi();
//            Log.d(TAG, "Vrooom. Tank is now " + mTank);
//        }
//    }

    // updates UI to reflect model
    public void updateUi() {
        // update the car color to reflect premium status or lack thereof
        // ((ImageView)findViewById(R.id.free_or_premium)).setImageResource(mIsPremium
        // ? R.drawable.premium : R.drawable.free);

        // "Upgrade" button is only visible if the user is not premium
        // findViewById(R.id.upgrade_button).setVisibility(mIsPremium ?
        // View.GONE : View.VISIBLE);

        // "Get infinite gas" button is only visible if the user is not
        // subscribed yet
        // findViewById(R.id.infinite_gas_button).setVisibility(mSubscribedToInfiniteGas
        // ?
        // View.GONE : View.VISIBLE);

        // update gas gauge to reflect tank status
//        if (mSubscribedToInfiniteGas) {
//            // ((ImageView)findViewById(R.id.gas_gauge)).setImageResource(R.drawable.gas_inf);
//        } else {
//            int index = mTank >= TANK_RES_IDS.length ? TANK_RES_IDS.length - 1
//                    : mTank;
//            // ((ImageView)findViewById(R.id.gas_gauge)).setImageResource(TANK_RES_IDS[index]);
//        }
    }

    // Enables or disables the "please wait" screen.
    void setWaitScreen(boolean set) {
//        findViewById(R.id.screen_main).setVisibility(set ? View.GONE : View.VISIBLE);
        findViewById(R.id.screen_wait).setVisibility(set ? View.VISIBLE : View.GONE);
    }

    public void onDestroy() {
        // very important:
        LogUtil.i(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    public DiamondDialog(Activity activity) {
        super(activity, R.style.dialog_custom_prompt);
        this.activity = activity;
        init(activity);
    }

    public void init(Context context) {
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        view = LayoutInflater.from(context).inflate(R.layout.dialog_diamond, null);
        initView();
        setContentView(view);
        mResultDataView.showLoading();
    }

    private void initView() {
        mResultDataView = (ResultDataView) view.findViewById(R.id.mResultDataView);
        lv_list = (ListView) view.findViewById(R.id.lv_list);
        btn_close = (ImageView) view.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(this);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String payload = "";
                buyDiamondGoodsEntity = (DiamondGoodsEntity) parent.getItemAtPosition(position);
                LogUtil.i(TAG, buyDiamondGoodsEntity.skuId);
                if (ApiConfig.AppVersion.isTaiwanVersion) {
                    if (isBillingUnavailable) {
                        //谷歌支付无效
                        Toast.makeText(getContext() , R.string.billing_unavailable , Toast.LENGTH_SHORT).show();
                    } else {
                        setWaitScreen(true);
                        mHelper.launchPurchaseFlow(activity, buyDiamondGoodsEntity.skuId, RC_REQUEST, mPurchaseFinishedListener, payload);
                    }
                } else {

//                    dismiss();
//                    if(mPayDialog == null)
//                        mPayDialog = new PayDialog(activity);
//                    mPayDialog.showUiMoney(buyDiamondGoodsEntity.getPrice(),buyDiamondGoodsEntity.getDiamond());

                    Toast.makeText(getContext(), R.string.topup_function_not_opened, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    PayDialog mPayDialog;

    public void setDiamondList(ArrayList<DiamondGoodsEntity> list) {
        diamondGoodsList = list;
        if (diamondGoodsList == null || diamondGoodsList.size() == 0) {
//            mResultDataView.nullDataShow();
        } else {
            mResultDataView.successShow();
            mDiamondGoodsAdapter = new DiamondGoodsAdapter(getContext(), diamondGoodsList);
            lv_list.setAdapter(mDiamondGoodsAdapter);
            initBilling();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                dismiss();
                break;
        }
    }
}
