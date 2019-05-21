package com.htgames.nutspoker.ui.activity.System;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.DiamondGoodsEntity;
import com.netease.nim.uikit.bean.GameGoodsEntity;
import com.htgames.nutspoker.chat.dealer.DealerHelper;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.htgames.nutspoker.config.shop.ShopGoodsConfig;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.tool.shop.ShopDataCache;
import com.htgames.nutspoker.tool.shop.ShopJsonHelper;
import com.htgames.nutspoker.ui.action.AmountAction;
import com.htgames.nutspoker.ui.action.ShopAction;
import com.htgames.nutspoker.ui.activity.shop.CoinRecycAdapter;
import com.htgames.nutspoker.ui.activity.shop.DiamondRecycAdapter;
import com.htgames.nutspoker.ui.activity.shop.ShopAdapter;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.ui.recycler.MeRecyclerView;
import com.htgames.nutspoker.view.ResultDataView;
import com.netease.nim.uikit.chesscircle.DealerConstant;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.session.constant.Extras;

import java.util.ArrayList;

/**
 * 游戏商城
 */
public class ShopActivity extends BaseActivity implements View.OnClickListener{
    private final static String TAG = "ShopActivity";
    private AmountAction mAmountAction;
    private ResultDataView mResultDataView;
    private MeRecyclerView mCoinRecyc;
    private CoinRecycAdapter mCoinAdapter;
    private MeRecyclerView mDiamondRecyc;
    private DiamondRecycAdapter mDiamondAdapter;
    ArrayList<GameGoodsEntity> goodsList = new ArrayList<GameGoodsEntity>();
    ArrayList<DiamondGoodsEntity> diamondGoodsList = new ArrayList<DiamondGoodsEntity>();
    private TextView mCoinText;
    private View mCoinTextClickArea;
    private TextView mDiamondText;
    private View mDiamondTextClickArea;
    public final static int TYPE_SHOP_COIN = 0;
    public final static int TYPE_SHOP_DIAMOND = 1;
    public final static int TYPE_SHOP_VIP = 2;
    private int shopType = TYPE_SHOP_COIN;
    private ViewPager mViewPager;
    private ShopAdapter mAdapter;
    ShopAction mShopAction;

    public static void start(Activity activity , int type) {
        Intent intent = new Intent(activity , ShopActivity.class);
        intent.putExtra(Extras.EXTRA_SHOP_TYPE, type);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAmoutAction();//支付宝 和 微信 支付成功  后 要请求 用户余额接口 user/amount
        mShopAction = new ShopAction(this , null);
//        initShopCache();//先获取"金币"和"钻石"数据再初始化view
        setContentView(R.layout.activity_shop);
        shopType = getIntent().getIntExtra(Extras.EXTRA_SHOP_TYPE , TYPE_SHOP_COIN);
        initHead();
        initView();
        getShopGoodsList();
    }

    private void initAmoutAction() {
        mAmountAction = new AmountAction(this, null);
        mAmountAction.setRequestCallback(new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                updateCoinDiamondUI();
            }
            @Override
            public void onFailed() {
            }
        });
    }

    public void getAmount() {
        LogUtil.i("PayHelp", "shopactivity getAmount: mAmountAction: " + mAmountAction);
        if (mAmountAction != null) {
            mAmountAction.getAmount(true);
        }
    }

//    private void initShopCache() {
//        if(ShopDataCache.getInstance().getGoodsList() != null){
//            for (GameGoodsEntity goodsEntity : ShopDataCache.getInstance().getGoodsList()) {
//                if (!ShopGoodsConfig.getVipIdMap().contains(goodsEntity.getId())) {
//                    goodsList.add(goodsEntity);
//                }
//            }
//        }
//        if(ShopDataCache.getInstance().getDiamondGoodsList() != null){
//            diamondGoodsList.addAll(ShopDataCache.getInstance().getDiamondGoodsList());
//        }
//        if (goodsList.size() <=0 || diamondGoodsList.size() <= 0) {
//            getShopGoodsList();
//        }
//    }

    private void getShopDataFromDB() {
        goodsList.clear();
        diamondGoodsList.clear();
        if(ShopDataCache.getInstance().getGoodsList() != null){
            for (GameGoodsEntity goodsEntity : ShopDataCache.getInstance().getGoodsList()) {
                if (!ShopGoodsConfig.getVipIdMap().contains(goodsEntity.id)) {
                    goodsList.add(goodsEntity);
                }
            }
        }
        if(ShopDataCache.getInstance().getDiamondGoodsList() != null){
            diamondGoodsList.addAll(ShopDataCache.getInstance().getDiamondGoodsList());
        }
        mCoinAdapter.setData(goodsList);
        mDiamondAdapter.setData(diamondGoodsList);
        if (goodsList.size() <=0 || diamondGoodsList.size() <= 0) {
            Toast.makeText(ShopActivity.this, R.string.shop_goodslist_get_failure, Toast.LENGTH_SHORT).show();
            mResultDataView.nullDataShow(R.string.shop_null_data, R.mipmap.buy_list_default, View.VISIBLE);
        } else {
            mResultDataView.successShow();
        }
    }

    private void initHead() {
        TextView titleView = (TextView) findViewById(R.id.tv_head_title);
        titleView.setText(R.string.me_column_shop);
        titleView.setTextColor(Color.WHITE);
        TextView btn_head_back = ((TextView) findViewById(R.id.btn_head_back));
        btn_head_back.setVisibility(View.VISIBLE);
        btn_head_back.setText("");
    }

    private void initView() {
        mResultDataView = (ResultDataView) findViewById(R.id.mResultDataView);
        mResultDataView.setReloadDataCallBack(new ResultDataView.ReloadDataCallBack() {
            @Override
            public void onReloadData() {
                getShopGoodsList();
            }
        });
        final Drawable coinDrawable = getResources().getDrawable(R.mipmap.icon_me_coin);
        coinDrawable.setBounds(0, 0, coinDrawable.getIntrinsicWidth(), coinDrawable.getIntrinsicHeight());
        final Drawable coinDrawableGrey = getResources().getDrawable(R.mipmap.icon_me_coin_grey);
        coinDrawableGrey.setBounds(0, 0, coinDrawableGrey.getIntrinsicWidth(), coinDrawableGrey.getIntrinsicHeight());
        final Drawable diamondDrawable = getResources().getDrawable(R.mipmap.icon_me_diamond);
        diamondDrawable.setBounds(0, 0, diamondDrawable.getIntrinsicWidth(), diamondDrawable.getIntrinsicHeight());
        final Drawable diamondDrawableGrey = getResources().getDrawable(R.mipmap.icon_me_diamond_grey);
        diamondDrawableGrey.setBounds(0, 0, diamondDrawableGrey.getIntrinsicWidth(), diamondDrawableGrey.getIntrinsicHeight());
        mCoinText = (TextView) findViewById(R.id.shop_coin_text);
        mCoinText.setText(" " + UserPreferences.getInstance(getApplicationContext()).getCoins());
        mCoinText.setTextColor(getResources().getColor(shopType == TYPE_SHOP_COIN ? R.color.shop_text_select_color_coin : R.color.shop_text_no_select_color));
        mCoinText.setCompoundDrawables(shopType == TYPE_SHOP_COIN ? coinDrawable : coinDrawableGrey, null, null, null);
        mCoinTextClickArea = findViewById(R.id.shop_coin_click_area);
        mCoinTextClickArea.setOnClickListener(this);
        mDiamondText = (TextView) findViewById(R.id.shop_diamond_text);
        mDiamondText.setText(" " + UserPreferences.getInstance(getApplicationContext()).getDiamond());
        mDiamondText.setTextColor(getResources().getColor(shopType == TYPE_SHOP_DIAMOND ? R.color.shop_text_select_color_diamond : R.color.shop_text_no_select_color));
        mDiamondText.setCompoundDrawables(shopType == TYPE_SHOP_DIAMOND ? diamondDrawable : diamondDrawableGrey, null, null, null);
        mDiamondTextClickArea = findViewById(R.id.shop_diamond_click_area);
        mDiamondTextClickArea.setOnClickListener(this);
        //金币
        mCoinAdapter = new CoinRecycAdapter(this);
        mCoinAdapter.setData(goodsList);
        mCoinRecyc = new MeRecyclerView(this);
        mCoinRecyc.setAdapter(mCoinAdapter);
        //钻石
        mDiamondAdapter = new DiamondRecycAdapter(this);
        mDiamondAdapter.setData(diamondGoodsList);
        mDiamondRecyc = new MeRecyclerView(this);
        mDiamondRecyc.setAdapter(mDiamondAdapter);
        //viewpager
        ArrayList pagerData = new ArrayList<String>();
        pagerData.add(ShopAdapter.COIN_PAGE);
        pagerData.add(ShopAdapter.DIAMOND_PAGE);
        mAdapter = new ShopAdapter(this);
        mAdapter.mCoinRecyc = mCoinRecyc;
        mAdapter.mDiamondRecyc = mDiamondRecyc;
        mAdapter.setData(pagerData);
        mViewPager = (ViewPager) findViewById(R.id.shop_view_pager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                mCoinText.setTextColor(getResources().getColor(position == 0 ? R.color.shop_text_select_color_coin : R.color.shop_text_no_select_color));
                mCoinText.setCompoundDrawables(position == 0 ? coinDrawable : coinDrawableGrey, null, null, null);
                mDiamondText.setTextColor(getResources().getColor(position == 1 ? R.color.shop_text_select_color_diamond : R.color.shop_text_no_select_color));
                mDiamondText.setCompoundDrawables(position == 1 ? diamondDrawable : diamondDrawableGrey, null, null, null);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(shopType);
    }

    /**
     * 根据商品类型获取商品列表
     */
    public void getShopGoodsList() {
        mResultDataView.showLoading();
        mShopAction.getShopGoodsList(new com.htgames.nutspoker.interfaces.RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if (code == 0) {
                    goodsList.clear();
                    diamondGoodsList.clear();
                    ShopDataCache.getInstance().buildCache(result);
                    ArrayList<GameGoodsEntity> goodsResultList = ShopJsonHelper.getGameGoodsList(result);
                    diamondGoodsList = ShopJsonHelper.getDiamondGoodsList(result);
                    for (int i = 0; i < goodsResultList.size(); i++) {
                        GameGoodsEntity goodsEntity = goodsResultList.get(i);
                        if (ShopGoodsConfig.getVipIdMap().contains(goodsEntity.id)) {
                        } else {
                            goodsList.add(goodsEntity);
                        }
                    }
                    mResultDataView.successShow();
                    mCoinAdapter.setData(goodsList);
                    mDiamondAdapter.setData(diamondGoodsList);
                } else{
                    getShopDataFromDB();
                }
            }
            @Override
            public void onFailed() {
                getShopDataFromDB();
            }
        });
    }

    public void updateCoinDiamondUI() {
        mCoinText.setText(" " + UserPreferences.getInstance(getApplicationContext()).getCoins());
        mDiamondText.setText(" " + UserPreferences.getInstance(getApplicationContext()).getDiamond());
    }

    public void checkPayment(String signature_data, String signature, final com.htgames.nutspoker.interfaces.RequestCallback requestCallback) {
        mShopAction.resetTryCount();
        mShopAction.doCheckPayment(signature_data, signature, new com.htgames.nutspoker.interfaces.RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                requestCallback.onResult(code, result, var3);
                if (code == 0) {
                    android.widget.Toast toast = android.widget.Toast.makeText(getApplicationContext(), R.string.payment_success, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    showPaymentFailureDialog();
                }
            }

            @Override
            public void onFailed() {
                requestCallback.onFailed();
                showPaymentFailureDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i("zzh", "shopactivity onResume");
    }

    @Override
    protected void onDestroy() {
        if (mShopAction != null) {
            mShopAction.onDestroy();
            mShopAction = null;
        }
        if (mAmountAction != null) {
            mAmountAction.onDestroy();
            mAmountAction = null;
        }
        super.onDestroy();
    }

    EasyAlertDialog paymentDialog;

    public void showPaymentFailureDialog() {
        String message = getString(R.string.buy_failure);
        paymentDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "", message,
                getString(R.string.contact_service), getString(R.string.cancel), false, new EasyAlertDialogHelper.OnDialogActionListener() {
                    @Override
                    public void doCancelAction() {

                    }

                    @Override
                    public void doOkAction() {
                        DealerHelper.startDealerChatting(ShopActivity.this, DealerConstant.dealer123456Uid);
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            paymentDialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.shop_coin_click_area:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.shop_diamond_click_area:
                mViewPager.setCurrentItem(1);
                break;
        }
    }
}
