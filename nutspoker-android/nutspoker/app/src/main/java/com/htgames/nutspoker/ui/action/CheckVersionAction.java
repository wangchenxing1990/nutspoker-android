package com.htgames.nutspoker.ui.action;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.hotupdate.HotUpdateConstants;
import com.htgames.nutspoker.hotupdate.service.DownloadAppService;
import com.htgames.nutspoker.interfaces.CheckVersionListener;
import com.htgames.nutspoker.tool.DownloadTools;
import com.htgames.nutspoker.tool.json.AppVersionJsonTool;
import com.htgames.nutspoker.ui.base.BaseAction;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.api.ApiConfig;
import com.netease.nim.uikit.api.ApiConstants;
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.api.SignStringRequest;
import com.netease.nim.uikit.bean.AppVersionEntity;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.chesscircle.DealerConstant;
import com.netease.nim.uikit.common.preference.CheckVersionPref;
import com.netease.nim.uikit.common.preference.SettingsPreferences;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.BaseTools;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 * 版本检测动作
 */
public class CheckVersionAction extends BaseAction {
    private final String TAG = "CheckVersionAction";
    public String requestUpgradeUrl = "";
    /**
     * 是否需要强制更新
     */
    private DownloadTools mDownloadTool;
    EasyAlertDialog mVersionDialog;
    CheckVersionListener mCheckVersionListener;
    //
    AppVersionEntity appVersionEntity;

    public CheckVersionAction(Activity activity, View baseView) {
        super(activity, baseView);
    }

    /**
     * 获取新版本信息
     */
    public void getVersionInfo(final boolean isWelcome ,final boolean isShowDialog, final boolean isShowResult) {
        String versionStr = CheckVersionPref.Companion.getInstance(ChessApp.sAppContext).getVersionStr();
        CheckVersionPref.Companion.getInstance(ChessApp.sAppContext).setLastGoBackgroundTime(Long.MAX_VALUE);
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            if (versionStr.length() <= 10) {
                if (isWelcome) {
                    checkError();
                } else if (isShowDialog) {
                    Toast.makeText(mActivity, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
                }
            } else {
                jsonResult(versionStr, isWelcome, isShowResult);
            }
            return;
        }
        if (isShowDialog) {
            DialogMaker.showProgressDialog(mActivity, getString(R.string.authcode_get_ing), false).setCanceledOnTouchOutside(false);
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext, true);
        String paramsStr = NetWork.getRequestParams(paramsMap);
        requestUpgradeUrl = getHost() + ApiConstants.URL_APP_UPGRADE + paramsStr;
        LogUtil.i(TAG, requestUpgradeUrl);
        SignStringRequest request = new SignStringRequest(Request.Method.GET, requestUpgradeUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                jsonResult(response, isWelcome, isShowResult);
                CheckVersionPref.Companion.getInstance(ChessApp.sAppContext).setVersionStr(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                redirect(false);
                LogUtil.e(TAG, error.getMessage(), error);
                checkError();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        request.setTag(requestUpgradeUrl);
        ChessApp.sRequestQueue.add(request);
    }

    /**
     * 解析服务器数据
     */
    public void jsonResult(String result, boolean isWelcome ,boolean isShowDialog) {
        try {
            JSONObject response = new JSONObject(result);
            int code = response.getInt("code");
            if (code == 0) {
                //说明有新版本更新
                String currentVersion = BaseTools.getAppVersionName(ChessApp.sAppContext);
//                currentVersion = "1.0.1";
                JSONObject data = response.getJSONObject("data");
                appVersionEntity = AppVersionJsonTool.getAppVersionEntity(data);
                checkSuccess(appVersionEntity);//检测成功
                String lastVersion = appVersionEntity.newVersion;
                if (!TextUtils.isEmpty(lastVersion) && !lastVersion.equals(currentVersion) && isHaveNewVersion(currentVersion , lastVersion) && isShowDialog) {
                    if (appVersionEntity.isShow) {
                        //有新版本,并且提示更新
                        showUpdateDialog(appVersionEntity);
                    } else {
                        //有新版本,不提示更新
                        if (mCheckVersionListener != null) {
                            mCheckVersionListener.onCheckNotNew();
                        }
                        redirect(true);
                        if (!isWelcome && isShowDialog) {
//                            Toast.makeText(ChessApp.sAppContext, R.string.app_version_is_latest, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    //无更新
                    if (mCheckVersionListener != null) {
                        mCheckVersionListener.onCheckNotNew();
                    }
                    redirect(true);
                    if (!isWelcome && isShowDialog) {
//                        Toast.makeText(ChessApp.sAppContext, R.string.app_version_is_latest, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                checkError();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            checkError();
        }
    }

    /**
     * 下载新版本
     */
    public void download(String app_url, String newVersion) {
        mDownloadTool = new DownloadTools(mActivity, app_url, DownloadTools.getAppFileName(newVersion));
        String downloadPath = CacheConstant.getAppDownloadPath() + DownloadTools.getAppFileName(newVersion);
        File downloadFile = new File(downloadPath);
        if (downloadFile.exists()) {
            Uri fileUri = Uri.fromFile(downloadFile);
            LogUtil.i(TAG, "文件已经存在：" + downloadPath);
            //文件存在，打开
            Intent intent1 = new Intent();
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.setAction(Intent.ACTION_VIEW);
            intent1.setDataAndType(fileUri, "application/vnd.android.package-archive");
            ChessApp.sAppContext.startActivity(intent1);
        } else {
            LogUtil.i(TAG, "文件不存在，进行下载：" + downloadPath);
            mDownloadTool.download();
        }
    }

    //以前是直接下载的，现在通过service下载
    public void downloadNew(AppVersionEntity appVersionEntity, boolean checkFileExists) {
//        ChessApp.sAppContext.getPackageManager().setApplicationEnabledSetting("com.android.providers.downloads", PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, 2);
        if(!resolveEnable()) {
            dealDownloadNewAppByBrowser(appVersionEntity);// Cannot download using download manager
            return;
        }
        String newVersion = appVersionEntity.newVersion;
        String downloadPath = CacheConstant.getAppDownloadPath() + DownloadTools.getAppFileName(newVersion);
        File downloadFile = new File(downloadPath);
        if (downloadFile.exists() && checkFileExists) {
            DownloadManager downloadManager = (DownloadManager) ChessApp.sAppContext.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Query query = new DownloadManager.Query();
            String idStr = SettingsPreferences.getInstance(mActivity).getDownloadId();
            boolean isNum = DealerConstant.isNumeric(idStr);
            if (isNum) {
                query.setFilterById(Long.parseLong(idStr));
                Cursor c = downloadManager.query(query);
                if (c != null && c.moveToFirst()) {
                    int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_FAILED) {
                        Toast.makeText(ChessApp.sAppContext, ChessApp.sAppContext.getString(R.string.version_downloading), Toast.LENGTH_SHORT).show();
                        LogUtil.i(TAG, "文件存在，但是下载失败：" + downloadPath);
                        Intent serviceIntent = new Intent(ChessApp.sAppContext, DownloadAppService.class);
                        serviceIntent.putExtra(HotUpdateConstants.EXTRA_KEY_DATA , appVersionEntity);
                        ChessApp.sAppContext.startService(serviceIntent);
                        c.close();
                        return;
                    } else if (status == DownloadManager.STATUS_RUNNING || status == DownloadManager.STATUS_PAUSED) {
                        Toast.makeText(ChessApp.sAppContext, "“" + ChessApp.sAppContext.getResources().getString(R.string.app_name) + "”" + "正在下载中,请稍后...", Toast.LENGTH_SHORT).show();
                        c.close();
                        return;
                    } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        Uri fileUri = Uri.fromFile(downloadFile);
                        LogUtil.i(TAG, "文件已经存在：" + downloadPath);//文件存在，打开
                        Intent intent1 = new Intent();
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent1.setAction(Intent.ACTION_VIEW);
                        intent1.setDataAndType(fileUri, "application/vnd.android.package-archive");
                        ChessApp.sAppContext.startActivity(intent1);
                        c.close();
                    }
                }
                if (c != null) {
                    c.close();
                }
            } else {
                Uri fileUri = Uri.fromFile(downloadFile);
                LogUtil.i(TAG, "文件已经存在：" + downloadPath);//文件存在，打开
                Intent intent1 = new Intent();
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.setAction(Intent.ACTION_VIEW);
                intent1.setDataAndType(fileUri, "application/vnd.android.package-archive");
                ChessApp.sAppContext.startActivity(intent1);
            }
        } else {//如果文件村建再次下载的话就会重新下载覆盖老的文件
            if (downloadFile.exists()) {
                downloadFile.delete();
            }
            Toast.makeText(ChessApp.sAppContext, ChessApp.sAppContext.getString(R.string.version_downloading), android.widget.Toast.LENGTH_SHORT).show();
            LogUtil.i(TAG, "文件不存在，进行下载：" + downloadPath);
            Intent serviceIntent = new Intent(ChessApp.sAppContext, DownloadAppService.class);
            serviceIntent.putExtra(HotUpdateConstants.EXTRA_KEY_DATA , appVersionEntity);
            ChessApp.sAppContext.startService(serviceIntent);
        }
    }

    /**
     * 有更新Dialog
     */
    public void showUpdateDialog(final AppVersionEntity appVersionEntity) {
        if (mVersionDialog == null) {
            String canleStr = "";
            if (appVersionEntity.isMandatory) {
                //需要强制更新
                canleStr = getString(R.string.exit);
            } else {
                canleStr = getString(R.string.update_not);
            }
            mVersionDialog = EasyAlertDialogHelper.createOkCancelDiolag(mActivity,
                    null, getString(R.string.app_update_title), getString(R.string.update), canleStr, false, new EasyAlertDialogHelper.OnDialogActionListener() {
                        @Override
                        public void doCancelAction() {
                            mVersionDialog.dismiss();
                            if (appVersionEntity.isMandatory) {
                                mActivity.finish();
                            } else {
                                redirect(true);
                            }
                        }

                        @Override
                        public void doOkAction() {
                            if (mVersionDialog != null) {
                                mVersionDialog.dismiss();
                            }
                            if (!appVersionEntity.isMandatory) {
                                redirect(true);
                                dealDownloadNewApp(appVersionEntity, true);//普通强更接口检查文件是否存在，因为有版本号的后缀保证文件唯一，但是强更错误码ApiConstants.URL_APP_UPGRADE没有版本号后缀
//                                dealDownloadNewAppByBrowser(appVersionEntity);
                            } else {
                                dealDownloadNewApp(appVersionEntity, true);
//                                dealDownloadNewAppByBrowser(appVersionEntity);
                                mActivity.finish();
                            }
                        }
                    }, R.style.dialog_bg_transparent_style);
        }
        String updateContent = appVersionEntity.content;
        if (!TextUtils.isEmpty(updateContent)) {
            String goodUpdateContent = updateContent.replace(" ", "").replace("\t", "");//删除特殊字符
            mVersionDialog.setMessage2(goodUpdateContent);
            mVersionDialog.setMessage2GravityLeft();
        }
        mVersionDialog.setCancelable(false);
        mVersionDialog.setCanceledOnTouchOutside(false);
        if (mActivity != null && !mActivity.isFinishing()) {
            mVersionDialog.show();
        }
        Window windowTest = mVersionDialog.getWindow();
        android.view.WindowManager.LayoutParams lp = windowTest.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        windowTest.setGravity(Gravity.CENTER);
    }

    public void dealDownloadNewApp(AppVersionEntity appVersionEntity, boolean checkFileExists) {// SignStringRequest.java通过反射调用
        if (mActivity != null) {
            if (ApiConfig.AppVersion.isTaiwanVersion) {
                //台湾版本，跳转到GooglePlay商店
                gotoGooglePlayStore(appVersionEntity.downloadUrl);
            } else {
//                download(appVersionEntity.downloadUrl, appVersionEntity.newVersion);
                downloadNew(appVersionEntity, checkFileExists);
            }
        }
    }

    public void dealDownloadNewAppByBrowser(AppVersionEntity appVersionEntity) {//通过浏览器下载
        if (mActivity != null) {
            Uri uri = Uri.parse(appVersionEntity.downloadUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mActivity.startActivity(intent);
        }
    }

    /**
     * 版本检测成功
     */
    public void redirect(boolean toActivity) {
        if (mCheckVersionListener != null) {
            mCheckVersionListener.onRedirect(toActivity);
        }
    }

    public void checkSuccess(AppVersionEntity appVersionEntity) {
        if (mCheckVersionListener != null) {
            mCheckVersionListener.onCheckSuccess(appVersionEntity);
        }
    }

    /**
     * 版本检测失败
     */
    public void checkError() {
        if (mCheckVersionListener != null) {
            mCheckVersionListener.onCheckError();
        }
    }

    /**
     * 设置版本检测监听
     *
     * @param listner
     */
    public void setCheckVersionListener(CheckVersionListener listner) {
        this.mCheckVersionListener = listner;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!TextUtils.isEmpty(requestUpgradeUrl)) {
            ChessApp.sRequestQueue.cancelAll(requestUpgradeUrl);
        }
        if (mDownloadTool != null) {
            mDownloadTool.unregisterDownloadReceiver();
            mDownloadTool = null;
        }
        mVersionDialog = null;
        mCheckVersionListener = null;
        appVersionEntity = null;
    }

    public void gotoGooglePlayStore(String googlePlayUrl) {
        if (TextUtils.isEmpty(googlePlayUrl)) {
            return;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(googlePlayUrl));
            mActivity.startActivity(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 检测是否有新版本
     * @param currentVersion 当前版本
     * @param lastVersion 服务器最新版本
     * @return
     */
    public boolean isHaveNewVersion(String currentVersion , String lastVersion) {
        try {
            String[] lastVersions = lastVersion.split("\\.");
            String[] currentVersions = currentVersion.split("\\.");
            int size = lastVersions.length;
            LogUtil.i(TAG, "lastVersion:" + lastVersion);
            LogUtil.i(TAG, "currentVersion:" + currentVersion);
            LogUtil.i(TAG, "size:" + size);
            for (int i = 0; i < size; i++) {
                int last = Integer.parseInt(lastVersions[i]);
                int current = Integer.parseInt(currentVersions[i]);
                LogUtil.i(TAG, "last:" + last);
                LogUtil.i(TAG, "current:" + current);
                //当前版本比服务器返回的最后版本低，说明有更新
                if (current < last) {
                    LogUtil.i(TAG, "有新版本");
                    return true;
                } else if (current > last) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.i(TAG, "无新版本");
        return false;
    }

    private static boolean resolveEnable() {
        int state = ChessApp.sAppContext.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                    state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED);
        } else {
            return !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                    state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER);
        }
    }

//    public void gotoGooglePlayStore(String googlePlayUrl) {
//        final String appPackageName = ChessApp.sAppContext.getPackageName(); // getPackageName() from Context or Activity object
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        try {
//            mActivity.startActivity(intent);
//        } catch (android.content.ActivityNotFoundException anfe) {
//            mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//        }
//    }
}
