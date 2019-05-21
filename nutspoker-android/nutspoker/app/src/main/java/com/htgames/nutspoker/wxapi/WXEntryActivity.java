package com.htgames.nutspoker.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import yd.util.db.KeyGen;

//微信指定支付回调Activity exported
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {
    private static final String TAG = WXEntryActivity.class.getSimpleName();
    private static final String KEY_TYPE = "key_type";
    private static final String KEY_TYPE_AUTH = "key_type_auth";//微信登录
    private static final String KEY_TYPE_SHARE = "key_type_share";//微信分享
    private static final String KEY_TYPE_SHARE_IMAGE = "key_type_share_image";//微信图片分享
    private static final String KEY_TYPE_PAY = "key_type_pay";//微信支付
    private static final String KEY_APPID = "key_appid";//appid

    //微信分享
    private static final String KEY_WEBPAGE_TITLE = "key_webpage_title";
    private static final String KEY_WEBPAGE_DESC = "key_webpage_desc";
    private static final String KEY_WEBPAGE_URL = "key_webpage_url";
    private static final String KEY_WEBPAGE_IMGURL = "key_webpage_imgurl";
    private static final String KEY_WEBPAGE_SCENE = "key_webpage_scene";

    //微信图片分享
    private static final String KEY_IMAGE_PATH = "key_image_path";
    private static final String KEY_IMAGE_SCENE = "key_image_scene";

    //微信支付
    private static final String KEY_PAYRMB_PARTNERID = "key_payrmb_partnerid";
    private static final String KEY_PAYRMB_PREPAYID = "key_payrmb_prepayid";
    private static final String KEY_PAYRMB_PACKAGEVALUE = "key_payrmb_packagevalue";
    private static final String KEY_PAYRMB_NONCESTR = "key_payrmb_noncestr";
    private static final String KEY_PAYRMB_TIMESTAMP = "key_payrmb_timestamp";
    private static final String KEY_PAYRMB_SIGN = "key_payrmb_sign";
    private static final String KEY_PAYRMB_EXTRA_DATA = "key_payrmb_extra_data";
    private IWXAPI wxapi;
    //微信登录
    public interface AuthCallback {
        public void onCodeReturn(String code);
    }
    private static AuthCallback authCallback;
    //微信支付回调
    public interface PayCallback {
        public void onPaySuccess();
    }
    private static PayCallback payCallback;
    //微信分享回调
    public interface ShareCallback {
        public void onShareSuccess();
    }
    private static ShareCallback shareCallback;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String appId = getIntent().getStringExtra(KEY_APPID);
        wxapi = WXAPIFactory.createWXAPI(this, appId, false);
        wxapi.registerApp(appId);
        if (wxapi.isWXAppInstalled()) {
            wxapi.handleIntent(getIntent(), this);
        } else {
            Toast.makeText(ChessApp.sAppContext , R.string.werixin_not_install , com.htgames.nutspoker.widget.Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String key_type = getIntent().getStringExtra(KEY_TYPE);
        if (KEY_TYPE_PAY.equals(key_type)) {
            String partnerId = getIntent().getStringExtra(KEY_PAYRMB_PARTNERID);
            String prepayId = getIntent().getStringExtra(KEY_PAYRMB_PREPAYID);
            String packageValue = getIntent().getStringExtra(KEY_PAYRMB_PACKAGEVALUE);
            String nonceStr = getIntent().getStringExtra(KEY_PAYRMB_NONCESTR);
            String timeStamp = getIntent().getStringExtra(KEY_PAYRMB_TIMESTAMP);
            String sign = getIntent().getStringExtra(KEY_PAYRMB_SIGN);
            String extraData = getIntent().getStringExtra(KEY_PAYRMB_EXTRA_DATA);
            weixinPay(appId, partnerId, prepayId, packageValue, nonceStr, timeStamp, sign, extraData);
        } else if (KEY_TYPE_SHARE.equals(key_type)) {
            String title = getIntent().getStringExtra(KEY_WEBPAGE_TITLE);
            String desc = getIntent().getStringExtra(KEY_WEBPAGE_DESC);
            String url = getIntent().getStringExtra(KEY_WEBPAGE_URL);
            String imgUrl = getIntent().getStringExtra(KEY_WEBPAGE_IMGURL);
            int scene = getIntent().getIntExtra(KEY_WEBPAGE_SCENE, SendMessageToWX.Req.WXSceneSession);
            weixinShare(title, desc, url, imgUrl, scene);
        } else if (KEY_TYPE_SHARE_IMAGE.equals(key_type)) {
            String path = getIntent().getStringExtra(KEY_IMAGE_PATH);
            int scene = getIntent().getIntExtra(KEY_IMAGE_SCENE, SendMessageToWX.Req.WXSceneSession);
//            weixinShare(path, scene);暂时不做
        } else if (KEY_TYPE_AUTH.equals(key_type)) {
//            weixinAuth();微信登录暂时不做
        }
    }

    public static void startWxShare(Activity activity, String appId, String title, String desc, String url, String imgUrl, int scene, ShareCallback cb) {
        shareCallback = cb;
        Intent intent = new Intent(activity, WXEntryActivity.class);
        intent.putExtra(KEY_TYPE, KEY_TYPE_SHARE);
        intent.putExtra(KEY_APPID, appId);
        intent.putExtra(KEY_WEBPAGE_TITLE, title);
        intent.putExtra(KEY_WEBPAGE_DESC, desc);
        intent.putExtra(KEY_WEBPAGE_URL, url);
        intent.putExtra(KEY_WEBPAGE_IMGURL, imgUrl);
        intent.putExtra(KEY_WEBPAGE_SCENE, scene);
        activity.startActivity(intent);
    }

    public static void startWxShare(Activity activity, String appId, String path, int scene, ShareCallback cb) {
        shareCallback = cb;
        Intent intent = new Intent(activity, WXEntryActivity.class);
        intent.putExtra(KEY_TYPE, KEY_TYPE_SHARE_IMAGE);
        intent.putExtra(KEY_APPID, appId);
        intent.putExtra(KEY_IMAGE_PATH, path);
        intent.putExtra(KEY_IMAGE_SCENE, scene);
        activity.startActivity(intent);
    }

    public static void startWxPay(Activity activity, String appId, String partnerId, String prepayId, String packageValue, String nonceStr, String timeStamp, String sign, String extraData, PayCallback cb) {
        payCallback = cb;
        Intent intent = new Intent(activity, WXEntryActivity.class);
        intent.putExtra(KEY_TYPE, KEY_TYPE_PAY);
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
        Toast.makeText(this, "进入微信支付...", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void weixinShare(String title, String desc, String url, String imgUrl, int scene) {
        WXWebpageObject page = new WXWebpageObject();
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        WXMediaMessage msg = new WXMediaMessage(page);
        page.webpageUrl = url;
        msg.title = title;
        msg.description = desc;
        req.transaction = KeyGen.genUUID32();
        req.message = msg;
        req.scene = scene;
        wxapi.sendReq(req);
        Toast.makeText(this, "进入微信分享...", Toast.LENGTH_SHORT).show();
        //***
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    String code = ((SendAuth.Resp) resp).code; //即为所需的code
                    LogUtil.i(TAG, "WXEntryActivity()->code=" + code + ",resType=" + resp.getType());
                    if (authCallback != null) {
                        authCallback.onCodeReturn(code);
                    }
                    break;
                case BaseResp.ErrCode.ERR_COMM:
                    Toast.makeText(this, "微信登录失败", Toast.LENGTH_SHORT).show();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    Toast.makeText(this, "微信登录取消了", Toast.LENGTH_SHORT).show();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    Toast.makeText(this, "微信登录无权限", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(this, "微信登录出错", Toast.LENGTH_SHORT).show();
                    break;
            }
            authCallback = null;//释放掉
        } else if (resp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    Toast.makeText(this, "微信分享成功", Toast.LENGTH_SHORT).show();
                    if (shareCallback != null) {
                        shareCallback.onShareSuccess();
                    }
                    break;
                case BaseResp.ErrCode.ERR_SENT_FAILED:
                    Toast.makeText(this, "微信分享失败", Toast.LENGTH_SHORT).show();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    Toast.makeText(this, "微信分享取消了", Toast.LENGTH_SHORT).show();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    Toast.makeText(this, "微信分享无权限", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(this, "微信分享出错", Toast.LENGTH_SHORT).show();
                    break;
            }
            shareCallback = null;//释放掉
        } else if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
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
            payCallback = null;//释放掉
        }
        finish();
    }
}