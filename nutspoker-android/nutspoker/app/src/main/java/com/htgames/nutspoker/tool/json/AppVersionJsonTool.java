package com.htgames.nutspoker.tool.json;

import android.text.TextUtils;

import com.netease.nim.uikit.api.ApiConstants;
import com.netease.nim.uikit.bean.AppVersionEntity;

import org.json.JSONObject;

/**
 */
public class AppVersionJsonTool {

    public static AppVersionEntity getAppVersionEntity(JSONObject dataJson) {
        AppVersionEntity appVersionEntity = null;
        if (dataJson != null) {
            appVersionEntity = new AppVersionEntity();
            int is_mandatory = dataJson.optInt("is_update");//是否强制更新
            int is_show = dataJson.optInt("is_show");//是否强制更新
            boolean isMandatory = false;
            boolean isShow = false;
            if (is_mandatory == ApiConstants.UPGRADE_IS_MANDTORY) {
                isMandatory = true;
            }
            if (is_show == ApiConstants.UPGRADE_IS_SHOW) {
                isShow = true;
            }
            appVersionEntity.isMandatory = (isMandatory);
            appVersionEntity.isShow = (isShow);
            appVersionEntity.newVersion = (dataJson.optString("ver"));
            appVersionEntity.downloadUrl = (dataJson.optString("url"));
            String content = dataJson.optString("content");
            if (TextUtils.isEmpty(content)) {
                appVersionEntity.content = ("");
            } else {
                appVersionEntity.content = (content.replace("\\n", "\n"));
            }
        }
        return appVersionEntity;
    }
}
