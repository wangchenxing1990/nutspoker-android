package com.htgames.nutspoker.ui.action;

import android.app.Activity;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.htgames.nutspoker.ChessApp;
import com.netease.nim.uikit.common.util.log.LogUtil;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiCode;
import com.netease.nim.uikit.api.ApiConstants;
import com.htgames.nutspoker.api.ApiResultHelper;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.api.SignStringRequest;
import com.htgames.nutspoker.ui.base.BaseAction;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 */
public class ShopAction extends BaseAction{
    private final static String TAG = "ShopAction";
    String requestShopListUrl;
    String requestBuyGoodsUrl;
    String requestPaymentCheckUrl;

    public ShopAction(Activity activity, View baseView) {
        super(activity, baseView);
    }

    /**
     * 根据商品类型获取商品列表
     */
    public void getShopGoodsList(final RequestCallback requestCallback) {
        cancelAll(requestShopListUrl);
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            if (requestCallback != null) {
                requestCallback.onFailed();
            }
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
//        if(ApiConfig.AppVersion.isTaiwanVersion) {
//            paramsMap.put("area", ApiConfig.AppVersion.AREA_TW);
//        }
        requestShopListUrl = getHost() + ApiConstants.URL_SHOP + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestShopListUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestShopListUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new org.json.JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        if (requestCallback != null) {
                            requestCallback.onResult(code, response, null);
                        }
                    } else {
                        if (requestCallback != null) {
                            requestCallback.onFailed();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (requestCallback != null) {
                        requestCallback.onFailed();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() != null) {
                    LogUtil.i(TAG, error.getMessage());
                }
                if (requestCallback != null) {
                    requestCallback.onFailed();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestShopListUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 购买商品
     */
    public void buyGoods(final String goodsId , final boolean isShowDialog ,  final RequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            if (requestCallback != null) {
                requestCallback.onFailed();
            }
            return;
        }
        if (isShowDialog) {
            DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        }
        requestBuyGoodsUrl = getHost() + ApiConstants.URL_SHOP_BUY;
        LogUtil.i(TAG, requestBuyGoodsUrl);
        cancelAll(requestBuyGoodsUrl);//先取消之前购买的，重新发起购买
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestBuyGoodsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new org.json.JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        if (isShowDialog) {
                            Toast.makeText(ChessApp.sAppContext, R.string.buy_success, Toast.LENGTH_SHORT).show();
                        }
                        if (requestCallback != null) {
                            requestCallback.onResult(code, response, null);
                        }
                    } else if (code == ApiCode.CODE_BALANCE_INSUFFICIENT) {
                        if (isShowDialog) {
                            Toast.makeText(ChessApp.sAppContext, R.string.buy_failure_balance_insufficient, Toast.LENGTH_SHORT).show();
                        }
                        if (requestCallback != null) {
                            requestCallback.onFailed();
                        }
                    } else {
                        if (isShowDialog) {
                            String message = ApiResultHelper.getShowMessage(json);
                            if (TextUtils.isEmpty(message)) {
                                message = ChessApp.sAppContext.getString(R.string.buy_failure);
                            }
                            Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
                        }
                        if (requestCallback != null) {
                            requestCallback.onFailed();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (requestCallback != null) {
                        requestCallback.onFailed();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                if (error.getMessage() != null) {
                    LogUtil.i(TAG, error.getMessage());
                }
                if (requestCallback != null) {
                    requestCallback.onFailed();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                HashMap<String, String> paramsMap = new HashMap<String, String>();
//                paramsMap.put("uid", UserPreferences.getInstance(ChessApp.sAppContext).getUserId());
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("goodsid", goodsId);
//                if (ApiConfig.AppVersion.isTaiwanVersion) {
//                    paramsMap.put("area", ApiConfig.AppVersion.AREA_TW);
//                }
                LogUtil.i(TAG, paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestBuyGoodsUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    int maxTryCount = 3;
    int tryCount = 0;

    //重置尝试次数
    public void resetTryCount() {
        tryCount = 0;
    }

    public void doCheckPayment(final String signture_data, final String signture, final RequestCallback requestCallback) {
//        if (tryCount < maxTryCount) {
//            Log.d(TAG, "重试:" + tryCount);
            if (!DialogMaker.isShowing()) {
                DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
            }
//            tryCount = tryCount + 1;
            checkPayment(signture_data, signture, new RequestCallback() {
                @Override
                public void onResult(int code, String result, Throwable var3) {
                    DialogMaker.dismissProgressDialog();
                    requestCallback.onResult(code, result, var3);
                }

                @Override
                public void onFailed() {
                    DialogMaker.dismissProgressDialog();
                    requestCallback.onFailed();
//                    doCheckPayment(signture_data, signture, requestCallback);
                }
            });
//        } else {
//            DialogMaker.dismissProgressDialog();
//            requestCallback.onFailed();
//        }
    }

    /**
     * 购买校验
     */
    public void checkPayment(final String signture_data ,final String signture , final RequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            if (requestCallback != null) {
                requestCallback.onFailed();
            }
            return;
        }
        requestPaymentCheckUrl = getHost() + ApiConstants.URL_SHOP_PAYMENT;
        LogUtil.i(TAG, requestPaymentCheckUrl);
        cancelAll(requestPaymentCheckUrl);//先取消之前购买的，重新发起购买
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestPaymentCheckUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new org.json.JSONObject(response);
                    int code = json.getInt("code");
                    JSONObject dataJson = json.optJSONObject("data");
                    if (code == 0) {
                        if (dataJson != null && dataJson.has("diamond")) {
                            int diamond = dataJson.optInt("diamond");
                            UserPreferences.getInstance(ChessApp.sAppContext).setDiamond(diamond);
                        }
                        if (requestCallback != null) {
                            requestCallback.onResult(code, response, null);
                        }
                    } else if (code == ApiCode.CODE_SHOP_GOODS_BUY_CHECK_ALREADY_USED) {
                        if (requestCallback != null) {
                            requestCallback.onResult(code, response, null);
                        }
                    } else {
//                        String message = ApiResultHelper.getShowMessage(json);
//                        if (TextUtils.isEmpty(message)) {
//                            message = ChessApp.sAppContext.getString(R.string.buy_failure);
//                        }
//                        Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
                        if (requestCallback != null) {
                            requestCallback.onResult(code, response, null);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (requestCallback != null) {
                        requestCallback.onFailed();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                if (error.getMessage() != null) {
                    LogUtil.i(TAG, error.getMessage());
                }
                if (requestCallback != null) {
                    requestCallback.onFailed();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("signture_data", signture_data);
                paramsMap.put("signture", signture);
//                if (ApiConfig.AppVersion.isTaiwanVersion) {
//                    paramsMap.put("area", ApiConfig.AppVersion.AREA_TW);
//                }
                LogUtil.i(TAG, paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestPaymentCheckUrl);
        //设置重试
        signRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ChessApp.sRequestQueue.add(signRequest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAll(requestShopListUrl);
        cancelAll(requestBuyGoodsUrl);
        cancelAll(requestPaymentCheckUrl);
    }
}
