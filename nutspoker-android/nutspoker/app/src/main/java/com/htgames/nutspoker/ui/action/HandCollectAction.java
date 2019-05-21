package com.htgames.nutspoker.ui.action;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiCode;
import com.netease.nim.uikit.api.ApiConstants;
import com.htgames.nutspoker.api.ApiResultHelper;
import com.netease.nim.uikit.bean.PaipuEntity;
import com.htgames.nutspoker.cocos2d.PokerActivity;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.htgames.nutspoker.data.DataManager;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.data.common.PaipuConstants;
import com.htgames.nutspoker.data.common.UserConstant;
import com.htgames.nutspoker.db.HandsCollectDBHelper;
import com.htgames.nutspoker.interfaces.DownloadCallback;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.api.SignStringRequest;
import com.htgames.nutspoker.thirdPart.aliyun.AliyunHelper;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.htgames.nutspoker.ui.activity.System.ShopActivity;
import com.htgames.nutspoker.ui.base.BaseAction;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.netease.nim.uikit.api.HostManager.getHost;

public class HandCollectAction extends BaseAction {
    public final static String TAG = "HandCollectAction";

    UserPreferences mUserPreferences;
    EasyAlertDialog shopDialog;

    public HandCollectAction(Activity activity, View baseView) {
        super(activity, baseView);
        mUserPreferences = UserPreferences.getInstance(ChessApp.sAppContext);
    }

    //更新收藏牌局数量
    public void updateCollectHandNum(int num) {
        mUserPreferences.setCollectHandNum(num);
    }
    //增加减少收藏牌谱数量
    public void updateCollectHandNum(boolean isAdd) {
        int collectHandNum = mUserPreferences.getCollectHandNum();
        if(isAdd){
            collectHandNum = collectHandNum + 1;
        } else{
            collectHandNum = collectHandNum - 1;
            if(collectHandNum < 0){
                collectHandNum = 0;
            }
        }
        mUserPreferences.setCollectHandNum(collectHandNum);
    }
    public int getCollectHandNum(){
        return mUserPreferences.getCollectHandNum();
    }

    /**
     * 从网络拉取牌谱收藏的数量
     * @param callback
     */
    @Deprecated
    public void getCollectHandNumFromNet(RequestCallback callback){
        HashMap<String, String> paramsMap = getRequestCommonMap();
        paramsMap.put("count", "1");//获取牌谱收藏数量(count:1)
        addRequestGet(getHost() + ApiConstants.URL_HAND_HISTORY,paramsMap,callback);
    }

    public void getGameInfo(String gid,RequestCallback callback){
        HashMap<String, String> paramsMap = getRequestCommonMap();
        paramsMap.put("gid", gid);
        addRequestGet(getHost() + ApiConstants.URL_GAMEINFO_FROM_GID,paramsMap,callback);
    }

    /**
     * 获取收藏列表
     */
    @Deprecated
    public void getHandCollectList(String last_hid ,RequestCallback callback) {
        HashMap<String, String> paramsMap = getRequestCommonMap();
        if(!TextUtils.isEmpty(last_hid))
            paramsMap.put("last_hid", last_hid);
        addRequestGet(getHost() + ApiConstants.URL_HAND_HISTORY,paramsMap,callback);
    }

    public void doHandsCollect(final PaipuEntity paipuEntity, final boolean isShow,final RequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            if(isShow)
                Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        //
        final String handCollectJsonStr = getHandsJsonStr(paipuEntity);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        String requestUrl = getHost() + ApiConstants.URL_HAND_HISTORY_COLOECT + NetWork.getRequestParams(paramsMap);

        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == ApiCode.CODE_SUCCESS || code == ApiCode.CODE_HANDS_COLLECT_AREADY) {
                        if(isShow){
                            Toast.makeText(ChessApp.sAppContext, R.string.collect_success, Toast.LENGTH_SHORT).show();
                        }
                        if(code == ApiCode.CODE_SUCCESS ){
                            updateCollectHandNum(true);//收藏成功数量自增
                        }
                        if(requestCallback != null){
                            requestCallback.onResult(code , response , null);
                        }
                    } else {
                        if(isShow) {
                            String message = ApiResultHelper.getShowMessage(json);
                            if (TextUtils.isEmpty(message))
                                message = ChessApp.sAppContext.getString(R.string.collect_failure);
                            Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!TextUtils.isEmpty(error.getMessage())) {
                    LogUtil.i(TAG, error.getMessage());
                }
                if(isShow)
                    Toast.makeText(ChessApp.sAppContext, R.string.collect_failure, Toast.LENGTH_SHORT).show();
                if(requestCallback != null){
                    requestCallback.onFailed();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return handCollectJsonStr.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "multipart/mixed; charset=" + ";boundary=" + UUID.randomUUID().toString();
            }

            @Override
            public RetryPolicy getRetryPolicy() {
                return super.getRetryPolicy();
            }
        };

        addRequest(signRequest,requestUrl);
    }

    @Deprecated
    public void uploadFile(final PaipuEntity paipuEntity , final boolean isShowDialog ,final RequestCallback requestCallback) {
        if (UserConstant.isHandCollectLimit()) {
            if (isShowDialog) {
                showShopDialog();
            }
            uploadFailure(requestCallback, isShowDialog);
            return;
        }
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            if(isShowDialog){
                Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            }
            uploadFailure(requestCallback, isShowDialog);
            return;
        }
        if(isShowDialog){
            DialogMaker.showProgressDialog(mActivity, getString(R.string.collect_ing), false).setCanceledOnTouchOutside(false);
        }
        String bucketName = AliyunHelper.BUCKET_NAME;
        String objectKey = paipuEntity.fileNetPath;
        String uploadFilePath = paipuEntity.fileLocalPath;
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucketName, objectKey, uploadFilePath);
        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                LogUtil.i(TAG, "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });
        OSSAsyncTask task = AliyunHelper.getOSSClient(ChessApp.sAppContext).asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                LogUtil.i(TAG, "UploadSuccess");
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doHandsCollect(paipuEntity, isShowDialog ,new RequestCallback() {
                            @Override
                            public void onResult(final int code, final String result, Throwable var3) {
                                if(isShowDialog) {
                                    DialogMaker.dismissProgressDialog();
                                }
                                if(code == 0 || code == ApiCode.CODE_HANDS_COLLECT_AREADY){
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                JSONObject jsonObject = new JSONObject(result);
                                                JSONObject dataObject = jsonObject.getJSONObject("data");
                                                String hid = dataObject.optString(PaipuConstants.KEY_HID);
                                                long collectTime = dataObject.optLong(PaipuConstants.KEY_COLLECT_TIME);
                                                paipuEntity.handsId = (hid);
                                                paipuEntity.collectTime = (collectTime);
                                                paipuEntity.isCollect = (true);
                                                if(!TextUtils.isEmpty(hid)){
                                                    HandsCollectDBHelper.addCollectHands(ChessApp.sAppContext, paipuEntity);//记录到本地数据库
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            if(requestCallback != null){
                                                requestCallback.onResult(code , result , null);
                                            }
                                        }
                                    });
                                } else if (code == ApiCode.CODE_HANDS_COLLECT_LIMIT) {
                                    if (isShowDialog) {
                                        showShopDialog();
                                    }
                                    uploadFailure(requestCallback, isShowDialog);
                                } else {
                                    uploadFailure(requestCallback, isShowDialog);
                                }
                            }

                            @Override
                            public void onFailed() {
                                uploadFailure(requestCallback, isShowDialog);
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                uploadFailure(requestCallback , isShowDialog);
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    LogUtil.e("ErrorCode", serviceException.getErrorCode());
                    LogUtil.e("RequestId", serviceException.getRequestId());
                    LogUtil.e("HostId", serviceException.getHostId());
                    LogUtil.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
        // task.cancel(); // 可以取消任务
        // task.waitUntilFinished(); // 可以等待直到任务完成
    }

    @Deprecated
    public void uploadFailure(final RequestCallback requestCallback ,final boolean isShowDialog){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogMaker.dismissProgressDialog();
                if(isShowDialog){
                    Toast.makeText(ChessApp.sAppContext, R.string.collect_failure, Toast.LENGTH_SHORT).show();
                }
                if(requestCallback != null){
                    requestCallback.onFailed();
                }
            }
        });
    }

    /**
     * 下载文件
     * @param paipuEntity
     * @param requestCallback
     */
    public void downloadFile(final PaipuEntity paipuEntity,final File downloadFile , final RequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(R.string.get_ing), false).setCanceledOnTouchOutside(false);
        String bucketName = AliyunHelper.BUCKET_NAME;
        String objectKey = paipuEntity.fileNetPath;
        // 构造下载文件请求
        GetObjectRequest get = new GetObjectRequest(bucketName, objectKey);
        OSSAsyncTask task = AliyunHelper.getOSSClient(ChessApp.sAppContext).asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功
                LogUtil.i("Content-Length", "" + result.getContentLength());
                InputStream inputStream = result.getObjectContent();
                FileOutputStream fileOutputStream = null;
                if(downloadFile.exists()){
                    downloadFile.delete();
                }
                // 输出的文件流
                try {
                    fileOutputStream = new FileOutputStream(downloadFile);
                    byte[] buffer = new byte[2048];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        // 处理下载的数据
                        fileOutputStream.write(buffer, 0, len);
                    }
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogMaker.dismissProgressDialog();
                            if (requestCallback != null) {
                                requestCallback.onResult(0, "", null);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    downloadPaipuFailure(requestCallback ,downloadFile);
                } finally {
                    try {
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                downloadPaipuFailure(requestCallback ,downloadFile);
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    LogUtil.e("ErrorCode", serviceException.getErrorCode());
                    LogUtil.e("RequestId", serviceException.getRequestId());
                    LogUtil.e("HostId", serviceException.getHostId());
                    LogUtil.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
        // task.cancel(); // 可以取消任务
        // task.waitUntilFinished(); // 如果需要等待任务完成
    }

    @Deprecated
    public void downloadPaipuFailure(final RequestCallback requestCallback,final File downloadFile){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogMaker.dismissProgressDialog();
                if(downloadFile.exists()){
                    downloadFile.delete();
                }
                Toast.makeText(ChessApp.sAppContext , R.string.get_hand_failure , Toast.LENGTH_SHORT).show();
                if(requestCallback != null){
                    requestCallback.onFailed();
                }
            }
        });
    }

    public String getHandsJsonStr(PaipuEntity paipuEntity) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(PaipuConstants.KEY_SHEET_UID, paipuEntity.sheetUid);
            jsonObject.put(PaipuConstants.KEY_CARD_TYPE, paipuEntity.cardType);
            jsonObject.put(GameConstants.KEY_GAME_GID, paipuEntity.gameEntity.gid);
            jsonObject.put(PaipuConstants.KEY_HANDS_CNT, paipuEntity.handsCnt);
            jsonObject.put(PaipuConstants.KEY_WIN_CHIPS, paipuEntity.winChip);
            jsonObject.put(PaipuConstants.KEY_FILE_NAME, paipuEntity.fileName);
            jsonObject.put(PaipuConstants.KEY_FILE_NET_PATH, paipuEntity.fileNetPath);
            JSONArray handsArray = new JSONArray();
            JSONArray poolArray = new JSONArray();
            JSONArray cardTypeArray = new JSONArray();
            if (paipuEntity.handCards != null && paipuEntity.handCards.size() != 0) {
                for (int card : paipuEntity.handCards) {
                    handsArray.put(card);
                }
            }
            if (paipuEntity.poolCards != null && paipuEntity.poolCards.size() != 0) {
                for (int card : paipuEntity.poolCards) {
                    poolArray.put(card);
                }
            }
            if (paipuEntity.cardTypeCards != null && paipuEntity.cardTypeCards.size() != 0) {
                for (int card : paipuEntity.cardTypeCards) {
                    cardTypeArray.put(card);
                }
            }
            jsonObject.put(PaipuConstants.KEY_HAND_CARDS, handsArray);
            jsonObject.put(PaipuConstants.KEY_POOL_CARDS, poolArray);
            jsonObject.put(PaipuConstants.KEY_CARDTYPE_CARDS, cardTypeArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(jsonObject != null){
            LogUtil.i(TAG , jsonObject.toString());
            return jsonObject.toString();
        }
        return null;
    }

    public void openHandPlay(final PaipuEntity paipuEntity) {
        if(/*TextUtils.isEmpty(paipuEntity.sheetUid) || DataManager.IsOldPaipu(paipuEntity.handsId)*/false) {
//            //旧版本 ,去阿里云下载文件
//            File fileCollect = PaipuConstants.getUserPaipuCollectFile(ChessApp.sAppContext, paipuEntity.getFileName());
//            if (fileCollect.exists()) {
//                //存在，直接打开播放
//                PokerActivity.startGamePlayRecord(mActivity, paipuEntity.sheetUid, fileCollect.getPath());
//            } else {
//                File fileCache = PaipuConstants.getPaipuCacheFile(ChessApp.sAppContext, paipuEntity.getFileName());
//                if (fileCache.exists()) {
//                    //存在，直接打开播放
//                    PokerActivity.startGamePlayRecord(mActivity, paipuEntity.sheetUid, fileCache.getPath());
//                } else {
//                    final File downloadFile = PaipuConstants.getUserPaipuCollectFile(ChessApp.sAppContext, paipuEntity.getFileName());
//                    downloadFile(paipuEntity, downloadFile, new RequestCallback() {
//                        @Override
//                        public void onResult(int code, String result, Throwable var3) {
//                            PokerActivity.startGamePlayRecord(mActivity, paipuEntity.sheetUid, downloadFile.getPath());
//                        }
//
//                        @Override
//                        public void onFailed() {
//
//                        }
//                    });
//                }
//            }
        } else {
            //新版本
            //新版本播放牌谱
            String path = DataManager.GetSheetFile(paipuEntity.sheetUid, paipuEntity.handsId, this, new DataManager.OnDataFinish() {
                @Override
                public void onDataFinish(Object data) {
                    if (data != null)
                        PokerActivity.startGamePlayRecord(mActivity, paipuEntity.sheetUid, (String)data);
                }
            });
            if(path != null)
                PokerActivity.startGamePlayRecord(mActivity, paipuEntity.sheetUid, path);
        }
    }

    public void showShopDialog() {
        if (shopDialog == null) {
            shopDialog = EasyAlertDialogHelper.createOkCancelDiolag(mActivity, "",
                    getString(R.string.hand_collect_count_is_limit), getString(R.string.buy), getString(R.string.cancel), true,
                    new EasyAlertDialogHelper.OnDialogActionListener() {

                        @Override
                        public void doCancelAction() {
                        }

                        @Override
                        public void doOkAction() {
                            ShopActivity.start(mActivity, ShopActivity.TYPE_SHOP_VIP);
                        }
                    });
        }
        if (!mActivity.isFinishing()) {
            shopDialog.show();
        }
    }


    /**
     *获取牌谱记录总数
     * @param callback
     */
    public void getCRListCount(RequestCallback callback){
        HashMap<String, String> paramsMap = getRequestCommonMap();
        paramsMap.put("count","1");
        addRequestGet(getHost() + ApiConstants.URL_CARDRECORD_LIST,paramsMap,callback);
    }

    /**
     *获取牌谱记录列表   默认一页20手，
     * @param handId 如果为空，那么返回的是第一页数据，否则就是返回这手后面一页的牌谱数据
     * @param callback
     */
    public void getCRList(@Nullable String handId, RequestCallback callback){
        HashMap<String, String> paramsMap = getRequestCommonMap();
        if(handId != null)
            paramsMap.put("last_hid",handId);
        addRequestGet(getHost() + ApiConstants.URL_CARDRECORD_LIST,paramsMap,callback);
    }


    /**
     * 下载文件
     * @param uid 指定账号牌谱
     * @param handId 指定牌谱记录ID
     * @param callback
     */
    public void getCRFile(String uid,String handId, File downFile,DownloadCallback callback){
        HashMap<String, String> paramsMap = getRequestCommonMap();
        paramsMap.put("uid",uid);
        paramsMap.put("hid",handId);
        addRequestDown(getHost() + ApiConstants.URL_CARDRECORD_DOWN,paramsMap,downFile,callback);
    }

    /**
     * 下载文件
     * @param handId 指定牌谱记录ID
     * @param callback
     */
    public void getCRFile(String handId, File downFile,DownloadCallback callback){
        HashMap<String, String> paramsMap = getRequestCommonMap();
        paramsMap.put("hid",handId);
        addRequestDown(getHost() + ApiConstants.URL_CARDRECORD_DOWN,paramsMap,downFile,callback);
    }

    /**
     * 获取收藏总数
     * @param callback
     */
    public void getCardCollectListCount(RequestCallback callback){
        HashMap<String, String> paramsMap = getRequestCommonMap();
        paramsMap.put("count","1");
        addRequestGet(getHost() + ApiConstants.URL_CARDCOLLECT_LIST,paramsMap,callback);
    }

    /**
     * 获取收藏列表
     * @param lastId
     * @param callback
     */
    public void getCardCollectList(@Nullable String lastId, RequestCallback callback){
        HashMap<String, String> paramsMap = getRequestCommonMap();
        if(!TextUtils.isEmpty(lastId))
            paramsMap.put("last_id",lastId);
        addRequestGet(getHost() + ApiConstants.URL_CARDCOLLECT_LIST,paramsMap,callback);
    }

    /**
     * 收藏牌谱 1.2.2新版本接口
     * @param handId
     * @param callback
     */
    public void collectHand(String handId,RequestCallback callback){
        if (UserConstant.isHandCollectLimit()) {//如果超过牌谱收藏数量
            showShopDialog();
            return;
        }

        HashMap<String, String> paramsMap = getRequestCommonMap();
        paramsMap.put("hid",handId);
        addRequestPost(getHost() + ApiConstants.URL_CARDRECORD_COLLECT,paramsMap,callback);
    }

    public void doCancelHandsCollect(final String handId, final RequestCallback requestCallback) {
        DialogMaker.showProgressDialog(mActivity, getString(R.string.collect_cancel_ing), false);

        String requestUrl = getHost() + ApiConstants.URL_HAND_HISTORY_UNCOLOECT;
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("hid", handId);

        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                DialogMaker.dismissProgressDialog();
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == ApiCode.CODE_SUCCESS) {
                        //更新收藏数量，并且删除库
                        updateCollectHandNum(false);
                        HandsCollectDBHelper.cancelCollectHands(ChessApp.sAppContext , handId);
                        //
                        if(requestCallback != null){
                            requestCallback.onResult(code , response , null);
                        }
                        Toast.makeText(ChessApp.sAppContext, R.string.collect_cancel_success, Toast.LENGTH_SHORT).show();
                    } else {
                        if(requestCallback != null){
                            requestCallback.onFailed();
                        }
                        String message = ApiResultHelper.getShowMessage(json);
                        if (TextUtils.isEmpty(message)) {
                            message = ChessApp.sAppContext.getString(R.string.collect_cancel_failure);
                        }
                        Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!TextUtils.isEmpty(error.getMessage())) {
                    LogUtil.i(TAG, error.getMessage());
                }
                Toast.makeText(ChessApp.sAppContext, R.string.collect_cancel_failure, Toast.LENGTH_SHORT).show();
                DialogMaker.dismissProgressDialog();
                if(requestCallback != null){
                    requestCallback.onFailed();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };

        addRequest(signRequest,requestUrl);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shopDialog = null;
    }
}
