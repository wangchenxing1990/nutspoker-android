package com.htgames.nutspoker.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.htgames.nutspoker.ChessApp;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by 周智慧 on 16/12/27.
 */

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = WXPayEntryActivity.class.getName();
    private static final String KEY_APPID = "key_payrmb_appid";
    private static final String KEY_PAYRMB_PARTNERID = "key_payrmb_partnerid";
    private static final String KEY_PAYRMB_PREPAYID = "key_payrmb_prepayid";
    private static final String KEY_PAYRMB_PACKAGEVALUE = "key_payrmb_packagevalue";
    private static final String KEY_PAYRMB_NONCESTR = "key_payrmb_noncestr";
    private static final String KEY_PAYRMB_TIMESTAMP = "key_payrmb_timestamp";
    private static final String KEY_PAYRMB_SIGN = "key_payrmb_sign";
    private static final String KEY_PAYRMB_EXTRA_DATA = "key_payrmb_extra_data";

    private static PayCallback payCallback;

    private IWXAPI wxapi;

    public interface PayCallback {
        public void onPaySuccess();
    }

    public static void startWxPay(Activity activity, String appId, String partnerId, String prepayId, String packageValue, String nonceStr, String timeStamp, String sign, String extraData, PayCallback cb) {
        payCallback = cb;
        Intent intent = new Intent(activity, WXPayEntryActivity.class);
        intent.putExtra(KEY_APPID, appId);
        intent.putExtra(KEY_PAYRMB_PARTNERID, partnerId);
        intent.putExtra(KEY_PAYRMB_PREPAYID, prepayId);
        intent.putExtra(KEY_PAYRMB_PACKAGEVALUE, packageValue);
        intent.putExtra(KEY_PAYRMB_NONCESTR, nonceStr);
        intent.putExtra(KEY_PAYRMB_TIMESTAMP, timeStamp);
        intent.putExtra(KEY_PAYRMB_SIGN, sign);
        intent.putExtra(KEY_PAYRMB_EXTRA_DATA, extraData);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String appId = getIntent().getStringExtra(KEY_APPID);
        String partnerId = getIntent().getStringExtra(KEY_PAYRMB_PARTNERID);
        String prepayId = getIntent().getStringExtra(KEY_PAYRMB_PREPAYID);
        String packageValue = getIntent().getStringExtra(KEY_PAYRMB_PACKAGEVALUE);
        String nonceStr = getIntent().getStringExtra(KEY_PAYRMB_NONCESTR);
        String timeStamp = getIntent().getStringExtra(KEY_PAYRMB_TIMESTAMP);
        String sign = getIntent().getStringExtra(KEY_PAYRMB_SIGN);
        String extraData = getIntent().getStringExtra(KEY_PAYRMB_EXTRA_DATA);
        wxapi = WXAPIFactory.createWXAPI(this, appId, false);
        wxapi.registerApp(appId);
        wxapi.handleIntent(getIntent(), this);
        weixinPay(appId, partnerId, prepayId, packageValue, nonceStr, timeStamp, sign, extraData);
    }

    public void weixinPay(String appId, String partnerId, String prepayId, String packageValue, String nonceStr, String timeStamp, String sign, String extData) {
        if (appId == null) {
            finish();
            return;
        }
        PayReq request = new PayReq();
        request.appId = appId;
        request.partnerId = partnerId;
        request.prepayId = prepayId;
        request.packageValue = packageValue;
        request.nonceStr = nonceStr;
        request.timeStamp = timeStamp;
        request.sign = sign;
        request.extData = extData;
        wxapi.sendReq(request);
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    Toast.makeText(ChessApp.sAppContext, "微信支付成功", Toast.LENGTH_SHORT).show();
                    if (payCallback != null) {
                        payCallback.onPaySuccess();
                    }
                    break;
                case BaseResp.ErrCode.ERR_COMM:
                    Toast.makeText(ChessApp.sAppContext, "微信支付失败", Toast.LENGTH_SHORT).show();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    Toast.makeText(ChessApp.sAppContext, "微信支付取消了", Toast.LENGTH_SHORT).show();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    Toast.makeText(ChessApp.sAppContext, "微信支付无权限", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(ChessApp.sAppContext, "微信支付出错", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        finish();
    }
}
