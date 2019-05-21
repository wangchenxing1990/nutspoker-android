package com.htgames.nutspoker.ui.action;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiConstants;
import com.htgames.nutspoker.ui.base.BaseAction;
import com.htgames.nutspoker.widget.Toast;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 */
public class ShareSocialAction extends BaseAction {
    UMShareAPI mShareAPI = null;
    Bitmap shareBitmap;

    public ShareSocialAction(Activity activity, View baseView) {
        super(activity, baseView);
        initShare();
    }

    public void initShare() {
        //
        mShareAPI = UMShareAPI.get(mActivity);
    }

    public boolean isInstall(SHARE_MEDIA shareMedia) {
        return mShareAPI.isInstall(mActivity, shareMedia);
    }

    public void doWeixinShare() {
        if (isInstall(SHARE_MEDIA.WEIXIN)) {
            //安装了客户端
            performShare(SHARE_MEDIA.WEIXIN);
        } else {
            Toast.makeText(ChessApp.sAppContext, R.string.werixin_not_install, Toast.LENGTH_SHORT).show();
        }
    }

    public void doWeixinCircleShare() {
        if (isInstall(SHARE_MEDIA.WEIXIN)) {
            performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
        } else {
            Toast.makeText(ChessApp.sAppContext, R.string.werixin_not_install, Toast.LENGTH_SHORT).show();
        }
    }

    public void doSinaShare() {
        performShare(SHARE_MEDIA.SINA);
    }

    public void performShare(SHARE_MEDIA shareMedia) {
        ShareAction shareAction = new ShareAction(mActivity)
                .setPlatform(shareMedia)
                .setCallback(umShareListener);
        UMImage shareImage = new UMImage(mActivity, getHost() + ApiConstants.URL_SHARE_URL_IMAGE);
        if (shareBitmap != null) {
            shareImage = new UMImage(mActivity, shareBitmap);
            shareAction.withMedia(shareImage);
            if (shareMedia == SHARE_MEDIA.SINA) {
                String message = ChessApp.sAppContext.getString(R.string.share_message_sina) + " " + getHost() + ApiConstants.URL_SHARE_URL;
                shareAction.withText(message);
            }
        } else {
            shareAction.withTitle(ChessApp.sAppContext.getString(R.string.share_title))
                    .withText(ChessApp.sAppContext.getString(R.string.share_message))
                    .withMedia(shareImage)
                    .withTargetUrl(getHost() + ApiConstants.URL_SHARE_URL);
        }
        shareAction.share();
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(ChessApp.sAppContext, R.string.share_success, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(ChessApp.sAppContext, R.string.share_failure, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
//            Toast.makeText(ChessApp.sAppContext , platform + " 分享取消", Toast.LENGTH_SHORT).show();
        }
    };

    //如果有使用任一平台的SSO授权或者集成了facebook平台, 则必须在对应的activity中实现onActivityResult方法, 并添加如下代码
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    public void onDestroy() {
        if (mShareAPI != null) {
            mShareAPI = null;
        }
    }
}
