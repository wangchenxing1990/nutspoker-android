package com.htgames.nutspoker.hotupdate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.Html;
import android.text.TextUtils;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.hotupdate.tool.HotUpdateItem;
import com.htgames.nutspoker.hotupdate.tool.HotUpdateManager;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.util.log.LogUtil;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiConstants;
import com.htgames.nutspoker.hotupdate.config.HotUpdateConfig;
import com.htgames.nutspoker.hotupdate.interfaces.CheckHotUpdateCallback;
import com.htgames.nutspoker.hotupdate.interfaces.UpdateRequestCallback;
import com.htgames.nutspoker.hotupdate.model.UpdateFileEntity;
import com.htgames.nutspoker.hotupdate.model.UpdateInfoEntity;
import com.htgames.nutspoker.hotupdate.preference.HotUpdatePreferences;
import com.htgames.nutspoker.hotupdate.receiver.UpdateReceiver;
import com.htgames.nutspoker.hotupdate.service.DownloadService;
import com.htgames.nutspoker.hotupdate.tool.FileTools;
import com.netease.nim.uikit.common.util.VersionTools;
import com.htgames.nutspoker.hotupdate.view.DownloadProgressView;
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.api.SignStringRequest;
import com.netease.nim.uikit.common.util.BaseTools;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.htgames.nutspoker.ui.base.BaseAction;
import com.htgames.nutspoker.util.CpuInfoUtil;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.string.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 * 热更新
 */
public class HotUpdateAction extends BaseAction{
    public static final String TAG = "HotUpdateAction";
    String requestHotUpdateUrl;
    UpdateRequestCallback mUpdateRequestCallback;
    EasyAlertDialog hotUpdateDialog;
    UpdateInfoEntity updateInfoEntity;
    EasyAlertDialog updateResultDialog = null;
    DownloadProgressView mDownloadProgressView;
    HotUpdatePreferences mHotUpdatePreferences;
    boolean isArm64 = false;//是否是64位

    public HotUpdateAction(Activity activity, View baseView) {
        super(activity, baseView);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mActivity != null && mActivity instanceof MainActivity) {
            mDownloadProgressView = (DownloadProgressView) mActivity.findViewById(R.id.mDownloadProgressView);
            registerNewGameReceiver(true);
        }
        isArm64 = CpuInfoUtil.isArm64();
        LogUtil.i(TAG, "是否是64位:" + isArm64);
        mHotUpdatePreferences = HotUpdatePreferences.getInstance(ChessApp.sAppContext);
    }

    public long lastDownTime = Long.MAX_VALUE;
    Timer timer;
    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mActivity != null) {
                    mActivity.runOnUiThread(updateRunnable);
                }
            }
        }, 0, 400L);
    }
    Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            LogUtil.i(TAG, "DemoCache.getCurrentServerSecondTime(): " + DemoCache.getCurrentServerSecondTime() + "  lastDownTime: " + lastDownTime + "  差值：" + (DemoCache.getCurrentServerSecondTime() - lastDownTime));
            if (DemoCache.getCurrentServerSecondTime() - lastDownTime >= 5) {//上个文件下载后5秒没有反应的话就认为下载失败，在模拟器上会出现下载游戏资源卡死的情况
                lastDownTime = Long.MAX_VALUE;
                showUpdateResultDialog(false);
                HotUpdateHelper.setGameUpdateIng(false);//设置不在下载中
                timerHasStarted = false;
                mHotUpdatePreferences.setCheckVersionTime(0);
                HotUpdateManager.getInstance().execludeCallback(new HotUpdateItem(HotUpdateItem.UPDATE_TYPE_STUCK));
                if (mDownloadProgressView != null) {
                    mDownloadProgressView.setVisibility(View.GONE);
                }
                destroyTimer();
            }
        }
    };

    public void destroyTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    //初始化：检测版本，重新安装，需要清除热更新目录
    public void initHotUpdate() {
        String lastAppVersion = HotUpdatePreferences.getInstance(ChessApp.sAppContext).getAppVersion();
        String nowAppVersion = VersionTools.getAppVersion(ChessApp.sAppContext);//当前APP版本号
        LogUtil.i(TAG, "lastVersion :" + lastAppVersion + "; nowVersion :" + nowAppVersion);
        LogUtil.i(TAG, "upd :" + HotUpdateConstants.getUpdateResPath());
        LogUtil.i(TAG, "upd_tmp :" + HotUpdateConstants.getUpdateTemporaryResPath());
        if (TextUtils.isEmpty(lastAppVersion) || !lastAppVersion.equals(nowAppVersion)) {
            //（第一次安装，版本更新）删除本地热更新（正式，临时）文件
            FileTools.deleteFile(new File(HotUpdateConstants.getUpdateResPath()));
            FileTools.deleteFile(new File(HotUpdateConstants.getUpdateTemporaryResPath()));
            HotUpdatePreferences.getInstance(ChessApp.sAppContext).setAppVersion(nowAppVersion);
            HotUpdatePreferences.getInstance(ChessApp.sAppContext).setGameVersion(HotUpdateConfig.getGameVersion(ChessApp.sAppContext));
        }
        doHotUpdate(false, null);
    }

    /** 修复游戏 */
    public void repairGame() {
        FileTools.deleteFile(new File(HotUpdateConstants.getUpdateResPath()));
        FileTools.deleteFile(new File(HotUpdateConstants.getUpdateTemporaryResPath()));
        HotUpdatePreferences.getInstance(ChessApp.sAppContext).setGameVersion(HotUpdateConfig.getGameVersion(ChessApp.sAppContext));
        HotUpdatePreferences.getInstance(ChessApp.sAppContext).setCheckVersionTime(0);
        //删除本地完毕，马上进行检测热更新
        doHotUpdate(true, new CheckHotUpdateCallback() {
            @Override
            public void notUpdate() {
                Toast.makeText(ChessApp.sAppContext, R.string.repair_game_success, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 执行热更新数据下载(接口成功才算有更新，其他一律作为无更新处理)
     */
    public void doHotUpdate(boolean showDialog , final CheckHotUpdateCallback checkHotUpdateCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            if (checkHotUpdateCallback != null) {
                checkHotUpdateCallback.notUpdate();
            }
            return;
        }
        String oldVersion = HotUpdatePreferences.getInstance(ChessApp.sAppContext).getGameVersion();
//        oldVersion = "1.0.1";
        String appVer = BaseTools.getAppVersionName(ChessApp.sAppContext);
//        appVer = "1.0.10";
//        final HashMap<String, String> paramsMap = new HashMap<String, String>();
//        paramsMap.put("uid", UserPreferences.getInstance(ChessApp.sAppContext).getUserId());
//        paramsMap.put("os", ApiConstants.OS_ANDROID);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("app_ver", appVer);
        paramsMap.put("game_ver", oldVersion);
        requestHotUpdateUrl = getHost() + ApiConstants.URL_HOT_UPDATE + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, "oldVersion :" + oldVersion);
        LogUtil.i(TAG, requestHotUpdateUrl);
        if (showDialog) {
            DialogMaker.showProgressDialog(mActivity, "", false).setCanceledOnTouchOutside(false);
        }
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestHotUpdateUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                if (!TextUtils.isEmpty(response)) {
                    LogUtil.i(TAG, response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int code = jsonObject.getInt("code");
                        if (code == 0) {
                            String data = jsonObject.optString("data");
                            if (TextUtils.isEmpty(data) || data.length() < 10) {//有时候data={}
                                //说明无更新，记录上次检测节点
                                if (mHotUpdatePreferences == null) {
                                    mHotUpdatePreferences = HotUpdatePreferences.getInstance(ChessApp.sAppContext);
                                }
                                mHotUpdatePreferences.setCheckVersionCurrentTime();
                                if (checkHotUpdateCallback != null) {
                                    checkHotUpdateCallback.notUpdate();
                                }
                            } else {
                                updateInfoEntity = HotUpdateConstants.parseUpdateInfoEntity(data, isArm64);
                                if (updateInfoEntity == null || updateInfoEntity.newVersion.equals(updateInfoEntity.oldVersion) || updateInfoEntity.diffFileList == null || updateInfoEntity.diffFileList.size() <= 0) {
                                    if (checkHotUpdateCallback != null) {
                                        checkHotUpdateCallback.notUpdate();
                                    }
                                    return;
                                }
                                LogUtil.i(TAG, "size :" + updateInfoEntity.diffFileList.size());
                                showHotUpdateDialog();
                            }
                        } else {
                            if (checkHotUpdateCallback != null) {
                                checkHotUpdateCallback.notUpdate();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (checkHotUpdateCallback != null) {
                            checkHotUpdateCallback.notUpdate();
                        }
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
                if (checkHotUpdateCallback != null) {
                    checkHotUpdateCallback.notUpdate();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestHotUpdateUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    public boolean timerHasStarted = false;
    UpdateReceiver mUpdateReceiver = new UpdateReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                lastDownTime = DemoCache.getCurrentServerSecondTime();
                if (!timerHasStarted) {
                    startTimer();
                    timerHasStarted = true;
                }
                int downloadFileCount = intent.getIntExtra(UpdateReceiver.EXTRA_FILE_ALL_COUNT, 0);
                int finishFileCount = intent.getIntExtra(UpdateReceiver.EXTRA_FILE_FINISH_COUNT, 0);
                int successFileCount = intent.getIntExtra(UpdateReceiver.EXTRA_FILE_SUCCESS_COUNT, 0);
                String newVersion = intent.getStringExtra(UpdateReceiver.EXTRA_VERSION_NEW);
                UpdateFileEntity updateFileEntity = (UpdateFileEntity) intent.getSerializableExtra("zzh");
                LogUtil.i(TAG, "downloadFileCount :" + downloadFileCount + ";finishFileCount :" + finishFileCount + "; successFileCount:" + successFileCount + "   fileCode:" +
                        (updateFileEntity == null ? "null" : updateFileEntity.fileCode) + "  fileName:" + (updateFileEntity == null ? "null" : updateFileEntity.fileName) +
                        ("  fileUrl" + (updateFileEntity == null ? "null" : HotUpdateConstants.getHotUpdateResUrl(newVersion ,updateFileEntity.fileName)))
                );
                if (downloadFileCount == finishFileCount) {
                    lastDownTime = Long.MAX_VALUE;
                    destroyTimer();
                    HotUpdateHelper.setGameUpdateIng(false);//设置不在下载中
                    mDownloadProgressView.setVisibility(View.GONE);
                    timerHasStarted = false;
                    if (finishFileCount == successFileCount) {
                        mDownloadProgressView.updateDownloadProgress(100);
                        if (!TextUtils.isEmpty(newVersion)) {
                            //记录本地为最新版本
                            HotUpdatePreferences.getInstance(ChessApp.sAppContext).setGameVersion(newVersion);
                            mHotUpdatePreferences.setCheckVersionCurrentTime();
                            LogUtil.i(TAG, "更新版本为：" + newVersion);
                        }
                        showUpdateResultDialog(true);
                    } else {
                        showUpdateResultDialog(false);
                        mHotUpdatePreferences.setCheckVersionTime(0);
                    }
                } else {
                    mDownloadProgressView.setVisibility(View.VISIBLE);
                    mDownloadProgressView.updateDownloadProgress((int) (successFileCount / (float) downloadFileCount * 100));
                }
                HotUpdateManager.getInstance().execludeCallback(new HotUpdateItem(HotUpdateItem.UPDATE_TYPE_ING)
                        .setUpdateFileEntity(updateFileEntity)
                        .setNewVersion(newVersion)
                        .setDownloadFileCount(downloadFileCount)
                        .setFinishFileCount(finishFileCount)
                        .setSuccessFileCount(successFileCount));
            }
        }
    };

    public void showHotUpdateDialog() {
        long updateSize = 0;
        if (updateInfoEntity.diffFileList != null) {
            for (UpdateFileEntity file : updateInfoEntity.diffFileList) {
                updateSize = updateSize + file.fileSize;
            }
        }
        float updateMSize = updateSize / 1024f / 1024f;
        BigDecimal mBigDecimal = new BigDecimal(updateMSize);
        updateMSize = mBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        String updateSizeStr = String.valueOf(updateMSize);
        //
        if (hotUpdateDialog == null) {
            String title = getString(R.string.game_hot_update_title);
            String message = getString(R.string.game_hot_update_message, updateInfoEntity.newVersion ,updateSizeStr);
            // "\n更新内容:\n1.修复牌局信息统计异常的BUG\n2.优化快速保险模式的表现方式\n3.新增俱乐部管理员";
            String updateInfo = updateInfoEntity.updateInfo;
//            String newStr = updateInfo.replace("\\n","\n");
            LogUtil.i(TAG, updateInfo);
            String[] strs = updateInfo.split("\\n");
            StringBuilder message2 = new StringBuilder();
            for (int i = 0; i < strs.length; i++) {
                if (strs[i].equals("\\n") || strs[i].equals("\n") || StringUtil.isSpace(strs[i])) {
                    continue;
                }
                message2.append(strs[i] + (i == strs.length - 1 ? "" : "\n"));
            }
            //第三个参数不能为空字符串，否则
            hotUpdateDialog = EasyAlertDialogHelper.createOkCancelDiolag(mActivity, title, title,
                    getString(R.string.update) , getString(R.string.update_not),false,
                    new EasyAlertDialogHelper.OnDialogActionListener() {

                        @Override
                        public void doCancelAction() {
                            //目前先用于测试热更新，用于删除游戏热更新目录文件
//                            FileTools.deleteFile(new File(HotUpdateConstants.getUpdateResPath()));
                        }

                        @Override
                        public void doOkAction() {
                            downUpdateFile();
                        }
                    });
//            hotUpdateDialog.setMessage2(Html.fromHtml(message));
            hotUpdateDialog.setMessage(Html.fromHtml(message));
            if(!TextUtils.isEmpty(updateInfo)) {
                hotUpdateDialog.setMessage2(message2);
            }
        }
        if (mActivity != null && !mActivity.isFinishing()) {
            hotUpdateDialog.show();
        }
    }

    public void showUpdateResultDialog(boolean isSuccess) {
        if (HotUpdateManager.getInstance().getCallbacks().size() > 0) {
            return;
        }
        if(isSuccess){
            updateResultDialog = EasyAlertDialogHelper.createOneButtonDiolag(mActivity , "",
                    getString(R.string.game_update_success), getString(R.string.ok), false, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
        } else {
            updateResultDialog = EasyAlertDialogHelper.createOkCancelDiolag(mActivity, "",
                    getString(R.string.game_update_failure), getString(R.string.reget), getString(R.string.cancel), false,
                    new EasyAlertDialogHelper.OnDialogActionListener() {
                        @Override
                        public void doCancelAction() {
                            mHotUpdatePreferences.setCheckVersionTime(0);
                        }

                        @Override
                        public void doOkAction() {
                            downUpdateFile();
                        }
                    });
        }
        if (!mActivity.isFinishing() && mActivity instanceof MainActivity && !((MainActivity)mActivity).isDestroyedCompatible()) {
            updateResultDialog.show();
        }
    }

    public void setUpdateRequestCallback(UpdateRequestCallback callback){
        mUpdateRequestCallback = callback;
    }

    public void downUpdateFile() {
        if(mActivity != null && updateInfoEntity != null ){
            HotUpdateHelper.setGameUpdateIng(true);
            if(mDownloadProgressView != null){
                mDownloadProgressView.setVisibility(View.VISIBLE);
                mDownloadProgressView.updateDownloadProgress(0);
            }
            Intent serviceIntent = new Intent(ChessApp.sAppContext, DownloadService.class);
            serviceIntent.putExtra(HotUpdateConstants.EXTRA_KEY_DATA , updateInfoEntity);
            mActivity.startService(serviceIntent);
        }
    }

    public void registerNewGameReceiver(boolean register){
        if(register){
            IntentFilter filter = new IntentFilter();
            filter.addAction(UpdateReceiver.ACTION_UPDATE);
            mActivity.registerReceiver(mUpdateReceiver, filter);
        }else{
            mActivity.unregisterReceiver(mUpdateReceiver);
        }
    }

    @Override
    public void onDestroy() {
        cancelAll(requestHotUpdateUrl);
        if(mActivity instanceof MainActivity){
            registerNewGameReceiver(false);
        }
        if(mDownloadProgressView != null){
            mDownloadProgressView.setVisibility(View.GONE);
            mDownloadProgressView = null;
        }
        HotUpdateHelper.setGameUpdateIng(false);//设置不在热更新中
        mUpdateRequestCallback = null;
        hotUpdateDialog = null;
        updateInfoEntity = null;
        updateResultDialog = null;
        mHotUpdatePreferences = null;
        destroyTimer();
        super.onDestroy();
    }
}
