package com.htgames.nutspoker.push;

import android.content.Context;
import android.text.TextUtils;

import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.util.log.LogUtil;

import com.igexin.sdk.PushManager;
import com.igexin.sdk.Tag;
import com.netease.nim.uikit.common.util.string.StringUtil;

/**
 * 个推工具类
 */
public class GeTuiTools {
    private final static String TAG = GeTuiTools.class.getSimpleName();

    public static void init(Context context) {
        PushManager.getInstance().initialize(context, com.igexin.sdk.PushService.class);
        if(!TextUtils.isEmpty(getClientid(context))){
            LogUtil.i(DemoIntentService.TAG , "init后获取clintId：" + getClientid(context));
        }
        PushManager.getInstance().registerPushIntentService(context, DemoIntentService.class);
    }

    public static void stop(Context context){
        PushManager.getInstance().stopService(context);
    }

    /**
     * 获取当前SDK的服务状态
     * @param context
     * @return
     */
    public static boolean isPushTurnedOn(Context context){
        return PushManager.getInstance().isPushTurnedOn(context);
    }

    /**
     * 开启Push推送, 默认是开启状态, 关闭状态则收不到推送。turnOnPush 默认打开。
     * 如果已经调用了 stopService 接口停止了 SDK 服务，调用 turnOnPush 或者重新调用 initialize 之后即可正常推送。
       如果已经调用了 turnOffPush 接口关闭了推送, 只有调用 turnOnPush 之后才能正常推送。
     * @param context
     */
    public static void turnOnPush(Context context) {
        PushManager.getInstance().turnOnPush(context);
    }

    /**
     * 关闭Push推送, 关闭后则无法收到推送消息。
     * @param context
     */
    public static void turnOffPush(Context context) {
        PushManager.getInstance().turnOffPush(context);
    }

    public static String getVersion(Context context){
        return PushManager.getInstance().getVersion(context);
    }

    /**
     * 获取当前用户的clientid
     * @param context
     * @return
     */
    public static String getClientid(Context context){
        return PushManager.getInstance().getClientid(context);
    }

    /**
     * 为当前用户设置一组标签，后续推送可以指定标签名进行定向推送。
     * @param context
     * @param tag 用户标签, 具体参考 setName 接口。
     *            @param sn：用户自定义的序列号，用来唯一标识该动作, 自定义 receiver 中会回执该结果
     * @return
     */
    public static int setTag(Context context , Tag[] tag) {
        return PushManager.getInstance().setTag(context, tag, DemoCache.getAccount());
    }

//    public static void setName(Context context ,String nameId){
//        Tag.
//    }

    /**
     * 绑定别名
     * @param context
     * @param alias
     */
    public static void bindAlias(Context context , String alias) {
        if (StringUtil.isSpace(alias)) {
            return;
        }
        boolean result = PushManager.getInstance().bindAlias(context, alias);
        LogUtil.i(DemoIntentService.TAG, "绑定别名result: " + result);
    }

    /**
     * 解绑定别名
     * @param context
     * @param alias 别名名称
     * @param isSelf 是否只对当前cid有效，如果是true，只对当前cid做解绑；如果是false，对所有绑定该别名的cid列表做解绑。
     */
    public static void unBindAlias(Context context , String alias , boolean isSelf){
        //true为只解绑自己，false为解绑全部
        boolean result = PushManager.getInstance().unBindAlias(context, alias, true);
        LogUtil.i(DemoIntentService.TAG, "解绑别名result: " + result);
    }
}
