package com.htgames.nutspoker.push;

import android.content.Context;

import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.common.util.VersionTools;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.Tag;
import com.netease.nim.uikit.common.util.string.StringUtil;

/**
 * 个推帮助类
 */
public class GeTuiHelper {
    private final static String TAG = "GeTuiHelper";

    /**
     * 绑定别名（UID）
     *
     * @param context
     */
    public static void bindAlias(Context context, String uid) {
        if (StringUtil.isSpace(uid)) {
            return;
        }
        String alias = uid;
        GeTuiTools.bindAlias(context, alias);
        LogUtil.i(TAG, "绑定别名：" + alias);
    }

    /**
     * 绑定别名（UID）
     *
     * @param context
     */
    public static void bindAliasUid(Context context) {
        String alias = UserPreferences.getInstance(context).getUserId();
        GeTuiTools.bindAlias(context, alias);
        LogUtil.i(TAG, "绑定别名：" + alias);
    }

    public static void unBindAliasUid(Context context) {
        String alias = UserPreferences.getInstance(context).getUserId();
        GeTuiTools.unBindAlias(context, alias, true);
        LogUtil.i(TAG, "解绑别名：" + alias);
    }

    //nameId 字段只支持：中文、英文字母（大小写）、数字、除英文逗号以外的其他特殊符号, 具体请看代码示例
    //tag一天只能成功设置一次的  超过一次的话 我们服务端不会更新的
    public static void setTag(Context context) {
        String appVersion = VersionTools.getAppVersion(context);
        String[] tags = new String[]{appVersion};
        Tag[] tagParam = new Tag[tags.length];
        for (int i = 0; i < tags.length; i++) {
            Tag t = new Tag();
            t.setName(tags[i]);
            tagParam[i] = t;
        }
        int tagCode = GeTuiTools.setTag(context, tagParam);
        LogUtil.i(TAG, "appVersion : " + appVersion + ";tag:" + tags[0] + ";tagCode :" + tagCode);
    }

    public static void getTagResult(int i) {
        String text = "设置标签失败,未知异常";
        switch (i) {
            case PushConsts.SETTAG_SUCCESS:
                text = "设置标签成功";
                break;

            case PushConsts.SETTAG_ERROR_COUNT:
                text = "设置标签失败, tag数量过大, 最大不能超过200个";
                break;

            case PushConsts.SETTAG_ERROR_FREQUENCY:
                text = "设置标签失败, 频率过快, 两次间隔应大于1s";
                break;

            case PushConsts.SETTAG_ERROR_REPEAT:
                text = "设置标签失败, 标签重复";
                break;

            case PushConsts.SETTAG_ERROR_UNBIND:
                text = "设置标签失败, 服务未初始化成功";
                break;

            case PushConsts.SETTAG_ERROR_EXCEPTION:
                text = "设置标签失败, 未知异常";
                break;

            case PushConsts.SETTAG_ERROR_NULL:
                text = "设置标签失败, tag 为空";
                break;

            default:
                break;
        }
    }
}
