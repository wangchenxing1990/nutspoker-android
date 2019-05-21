package com.htgames.nutspoker.ui.helper;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.reflect.TypeToken;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiCode;
import com.netease.nim.uikit.api.ApiConstants;
import com.netease.nim.uikit.bean.CommonBeanT;
import com.netease.nim.uikit.bean.PayAli;
import com.netease.nim.uikit.bean.PayWX;
import com.netease.nim.uikit.common.gson.GsonUtils;
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.api.HttpApi;
import com.htgames.nutspoker.net.observable.ResultException;
import com.htgames.nutspoker.ui.activity.System.ShopActivity;
import com.htgames.nutspoker.ui.helper.Alipay.PayResult;
import com.htgames.nutspoker.wxapi.WXEntryActivity;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.log.LogUtil;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by glp on 2016/8/17.
 */

public class PayHelp {
    public static final String TAG = PayHelp.class.getSimpleName();
    static final String Tag = "PayHelp";

    public static final String API_WXSign = "";

    //支付宝配置参数 start
    // 商户PID
    public static final String PARTNER = "2088421700816850";
    // 商户收款账号
    public static final String SELLER = "zygames_vip@163.com";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMb1QNkW48ArAAGU"
            + "8AswGGGaxnKOMXbDVcFmwHpj4owbJq0utfp0RzEw7ASfEBT5tTg5m12QXDE86n70"
            + "AWOpO6pVzj7u3cPjXjNZV3BGkEcQpcfrUL+ttzxdgwTQ8Ybly7as9RRPrH4IH3Aq"
            + "HrjeHPyQfZXa+ERnGuPUrisDkXBBAgMBAAECgYAQxdpKsvBOYhGlEH5QIyTbDaZP"
            + "QAeRgJQ5MsVlTAhsUVmoXfi/fZAG6J3tIc/EROzvKYQcli//gqguPbnkQf8ijfoF"
            + "RiTQd4TW09csI9LN1+C+98NN8INwyJXVNaXdwD+MZd3+C+98TRmqCKHtSTLmtGsZ"
            + "+Xg1WuNl8zCuzH9UwQJBAOgeAVV2uPtBoilrDPk40QQEdfBTLAFxNfcq8hNUMAB+"
            + "qXALuyprtn0tUQ3E+eKJeLk6a/rTUCy0QHnEILImyOkCQQDbbdTIPjF8+KQ+47lk"
            + "dx+OknQyw9uAGUxRX0O9nHDKpQL6iryJfzpVVoXeH+yGJ7LDZMjS7jHBng/j8YAv"
            + "flWZAkBuQkqJ6VJnb77zA7nu7NFEokXPugJuUPO8jDbffZ8rrP8ZjUkEFpRiE9Or"
            + "wcSdmoWxHxJJ3HT6N0llTgfl+Ex5AkAtPLRcX+4F7KC30mzbGG/qDalk+dnby9te"
            + "/zvQ8FqUfHZjvoxl8FPPWhoZFA3IDpEdFT8NtUuOtg5imNwrbmkpAkBtjB+ZOJ5X"
            + "j+Xt74Bd2picPmC9pntiPc6PsUExeYZQ8pzEv7SMp7ZbpEmmXO1i/7EkTwVeASc7"
            + "K9gpZYf+PvQp";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "";
    //支付宝配置参数 end

    public static void doAliPay(final Activity activity, int type) {
        if (activity == null)
            return;

        HttpApi.Builder builder = new HttpApi.Builder()
                .methodUrl(1, ApiConstants.METHOD_PAY_ALI)
                .mapParams(NetWork.getRequestCommonParams(ChessApp.sAppContext, true, true))
                .param("product_id", "" + type);
        HttpApi.GetNetObservable(builder
                , new Action0() {
                    @Override
                    public void call() {
                        DialogMaker.showProgressDialog(ChessApp.sAppContext, "", false);
                    }
                })
                .map(new Func1<String, CommonBeanT<PayAli>>() {
                    @Override
                    public CommonBeanT<PayAli> call(String s) {
                        LogUtil.i(TAG, "s: " + s);
                        CommonBeanT<PayAli> ret = null;
                        try {
                            Type type = new TypeToken<CommonBeanT<PayAli>>() {
                            }.getType();
                            ret = GsonUtils.getGson().fromJson(s, type);
                            return ret;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return ret;
                    }
                })
                .map(new Func1<CommonBeanT<PayAli>, PayResult>() {
                    @Override
                    public PayResult call(CommonBeanT<PayAli> payAliCommonBeanT) {
                        if (payAliCommonBeanT == null) {
                            throw new ResultException(-1, ChessApp.sAppContext.getString(R.string.pay_request_failed));
                        } else if (payAliCommonBeanT.code == ApiCode.CODE_SUCCESS) {
                            //for test 本地 start
                            String orderInfo = payAliCommonBeanT.data.orderId;//OrderInfoUtil.getOrderInfo("测试的商品", "该测试商品的详细描述", "0.01");
                            /**
                             * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
                             */
//                            String sign = OrderInfoUtil.sign(orderInfo);
//                            try {
//                                /**
//                                 * 仅需对sign 做URL编码
//                                 */
//                                sign = URLEncoder.encode(sign, "UTF-8");
//                            } catch (UnsupportedEncodingException e) {
//                                e.printStackTrace();
//                            }
                            /**
                             * 完整的符合支付宝参数规范的订单信息
                             */
//                            String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + OrderInfoUtil.getSignType();
                            PayTask alipay = new PayTask(activity);
                            Map<String, String> result = alipay.payV2(orderInfo, true);
                            /**
                             对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                             */
                            PayResult payResult = new PayResult(result);
                            LogUtil.i(TAG, result.toString());
                            return payResult;
                        } else {
                            throw new ResultException(payAliCommonBeanT.code, ApiCode.SwitchCode(payAliCommonBeanT.code, ChessApp.sAppContext.getString(R.string.pay_request_failed)));
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PayResult>() {
                    @Override
                    public void onCompleted() {
                        DialogMaker.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        DialogMaker.dismissProgressDialog();
                        if (e instanceof ResultException) {
                            ResultException re = (ResultException) e;
                            Toast.makeText(ChessApp.sAppContext, re.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(PayResult payResult) {
                        DialogMaker.dismissProgressDialog();
                        /**
                         * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                         * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                         * docType=1) 建议商户依赖异步通知
                         */
                        String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                        String resultStatus = payResult.getResultStatus();
                        LogUtil.i(TAG, "resultInfo=" + resultInfo + "; resultStatus=" + resultStatus);
                        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                        if (TextUtils.equals(resultStatus, "9000")) {
                            Toast.makeText(ChessApp.sAppContext, ChessApp.GetString(R.string.pay_success), Toast.LENGTH_SHORT).show();
                            if (activity instanceof ShopActivity) {
                                ((ShopActivity) activity).getAmount();
                            }
                        } else {
                            // 判断resultStatus 为非"9000"则代表可能支付失败
                            // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                            if (TextUtils.equals(resultStatus, "8000")) {
                                //显示对话框提示等待中,每隔1秒向服务器查询，5秒后判定支付失败
                                Toast.makeText(ChessApp.sAppContext, ChessApp.GetString(R.string.pay_wait_result), Toast.LENGTH_SHORT).show();
                            } else if ("4000".equals(resultStatus)) {
                                Toast.makeText(ChessApp.sAppContext, "未安装支付宝", Toast.LENGTH_SHORT).show();
                            } else {
                                // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                                Toast.makeText(ChessApp.sAppContext, ChessApp.GetString(R.string.pay_request_failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

//        Observable.just(type)
//                .observeOn(Schedulers.io())
//                .map(new Func1<Integer, String>() {
//                    @Override
//                    public String call(Integer integer) {
//
//                        Map<String,String> paramCustom = NetWork.getRequestCommonParams(activity,false);
//                        paramCustom.put("gid","5387");
//                        //paramCustom.put("product_id",""+type);
//
//                        String url = "http://tcapi.htgames.cn/game/view" + NetWork.getRequestParams(paramCustom);
//                        //同步 OKHttp Get
//                        OkHttpClient client = new OkHttpClient();
//                        Request request = new Request.Builder()
//                                .headers(OkHttpHeadsHelp.getHeads(SignStringRequest.getCommAuth(paramCustom)))
//                                .url(url)
//                                //.post()
//                                .build();
//
//                        Response response;
//                        try {
//                            response = client.newCall(request).execute();
//
//                            Headers responseHeaders = response.headers();
//                            for (int i = 0; i < responseHeaders.size(); i++) {
//                                Log.d(Tag, "Get Headers : " + responseHeaders.name(i) + ": " + responseHeaders.value(i));
//                            }
//
//                            String retStr = response.body().string();
//                            Log.d(Tag, "Get Body : " + retStr);
//
//                            Type type = new TypeToken<CommonBeanT<PayWX>>() {
//                            }.getType();
//                            return GsonUtils.getGson().fromJson(retStr, type);
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                        //for test 本地
//                        String orderInfo = OrderInfoUtil.getOrderInfo("测试的商品", "该测试商品的详细描述", "0.01");
//
//                        /**
//                         * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
//                         */
//                        String sign = OrderInfoUtil.sign(orderInfo);
//                        try {
//                            /**
//                             * 仅需对sign 做URL编码
//                             */
//                            sign = URLEncoder.encode(sign, "UTF-8");
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//
//                        /**
//                         * 完整的符合支付宝参数规范的订单信息
//                         */
//                        String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + OrderInfoUtil.getSignType();
//
//                        return payInfo;
//                    }
//                })
//                .map(new Func1<String, PayResult>() {
//                    @Override
//                    public PayResult call(String s) {
//                        PayTask alipay = new PayTask(activity);
//                        String result = alipay.pay(s, true);
//                        Log.i("msp", result.toString());
//
//                        PayResult payResult = new PayResult(result);
//                        return payResult;
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<PayResult>() {
//                    @Override
//                    public void call(PayResult payResult) {
//                        /**
//                         * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
//                         * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
//                         * docType=1) 建议商户依赖异步通知
//                         */
//                        String resultInfo = payResult.getResult();// 同步返回需要验证的信息
//
//                        String resultStatus = payResult.getResultStatus();
//                        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
//                        if (TextUtils.equals(resultStatus, "9000")) {
//                            Toast.makeText(activity, "支付成功", Toast.LENGTH_SHORT).show();
//                        } else {
//                            // 判断resultStatus 为非"9000"则代表可能支付失败
//                            // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
//                            if (TextUtils.equals(resultStatus, "8000")) {
//                                Toast.makeText(activity, "支付结果确认中", Toast.LENGTH_SHORT).show();
//                            } else {
//                                // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
//                                Toast.makeText(activity, "支付失败", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                });
    }

    public static void doWxPayNew(final WeakReference<Activity> activity, int type) {
        if (activity == null || activity.get() == null) {
            return;
        }
        HttpApi.Builder builder = new HttpApi.Builder()
                .methodUrl(1, ApiConstants.METHOD_PAY_WEIXIN)
                .mapParams(NetWork.getRequestCommonParams(ChessApp.sAppContext, true, true))
                .param("product_id", "" + type);
        HttpApi.GetNetObservable(builder
                , new Action0() {
                    @Override
                    public void call() {
                        DialogMaker.showProgressDialog(ChessApp.sAppContext, "", false);
                    }
                })
                .map(new Func1<String, CommonBeanT<PayWX>>() {
                    @Override
                    public CommonBeanT<PayWX> call(String s) {
                        LogUtil.i("zzh", "s:" + s);
                        CommonBeanT<PayWX> ret = null;
                        try {
                            Type type = new TypeToken<CommonBeanT<PayWX>>() {
                            }.getType();
                            ret = GsonUtils.getGson().fromJson(s, type);
                            return ret;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return ret;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CommonBeanT<PayWX>>() {
                    @Override
                    public void call(CommonBeanT<PayWX> wxPayCommonBeanT) {
                        if (wxPayCommonBeanT == null) {
                            throw new ResultException(-1, ChessApp.sAppContext.getString(R.string.pay_request_failed));
                        } else if (wxPayCommonBeanT.code == ApiCode.CODE_SUCCESS) {
                            PayWX wxPay = wxPayCommonBeanT.data;
                            if (activity != null && activity.get() != null) {
                                WXEntryActivity.startWxPay(activity.get(),
                                        wxPay.appid,
                                        wxPay.partnerid,
                                        wxPay.prepayid,
                                        wxPay.packag,
                                        wxPay.noncestr,
                                        wxPay.timestamp,
                                        wxPay.sign, wxPay.extData, new WXEntryActivity.PayCallback() {
                                            @Override
                                            public void onPaySuccess() {
                                                if (activity != null && activity.get() instanceof ShopActivity) {
                                                    ((ShopActivity) activity.get()).getAmount();
                                                }
                                            }
                                        });
                            }

                        } else {
                            Toast.makeText(ChessApp.sAppContext, R.string.network_exception_please_try, Toast.LENGTH_SHORT).show();
                        }
                        DialogMaker.dismissProgressDialog();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(ChessApp.sAppContext, ChessApp.sAppContext.getString(R.string.pay_request_failed), Toast.LENGTH_SHORT).show();
                        DialogMaker.dismissProgressDialog();
                    }
                });

    }
}
