package com.htgames.nutspoker.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiConfig;
import com.netease.nim.uikit.api.ApiConstants;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMtSngConfig;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.bean.GameNormalConfig;
import com.netease.nim.uikit.bean.GameSngConfigEntity;
import com.netease.nim.uikit.bean.PineappleConfig;
import com.netease.nim.uikit.bean.PineappleConfigMtt;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.util.ToolUtil;
import com.htgames.nutspoker.widget.Toast;
import com.htgames.nutspoker.wxapi.WXEntryActivity;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 */
public class ShareView extends PopupWindow implements View.OnClickListener {
    private final static String TAG = "ShareView";
    // 首先在您的Activity中添加如下成员变量
    private Activity mActivity;
    public final static int TYPE_SHARE_INVITE = 1;//分享邀请好友
    public final static int TYPE_SHARE_CLUBCARD = 2;//分享群名片
    public final static int TYPE_SHARE_AUTHCODE = 3;//分享验证码
    public final static int TYPE_SHARE_RECORD = 4;//分享战绩
    OnCallOverback mOnCallOverback;
    UMShareAPI mShareAPI = null;
    Bitmap shareBitmap;
    int type = 0;
    public GameEntity gameEntity;

    public ShareView(Activity activity) {
        super(ChessApp.sAppContext);
        this.mActivity = activity;
        init();
    }

    public ShareView(Activity activity, int type) {
        super(ChessApp.sAppContext);
        this.mActivity = activity;
        this.type = type;
        init();
    }

    public void init() {
        initView(ChessApp.sAppContext);
        if (type == TYPE_SHARE_RECORD) {
            setAnimationStyle(R.style.PopupRightAnimation);
            setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            setAnimationStyle(R.style.PopupAnimation);
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        setOutsideTouchable(true);
        setFocusable(true);
        setTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //微博需要用activity初始化，否则分享回调crash：Unable to start activity ComponentInfo{com.htgames.nutspoker/com.htgames.nutspoker.thirdPart.umeng.share.WBShareActivity}: java.lang.ClassCastException: com.htgames.nutspoker.ChessApp cannot be cast to android.app.Activity
        mShareAPI = UMShareAPI.get(mActivity);
    }

    private void initView(Context context) {
        View rootView;
        if (type == TYPE_SHARE_RECORD) {
            rootView = LayoutInflater.from(context).inflate(R.layout.pop_share_right_view, null);
        } else {
            rootView = LayoutInflater.from(context).inflate(R.layout.pop_share_view, null);
            rootView.findViewById(R.id.btn_share_cancel).setOnClickListener(this);
        }
        rootView.findViewById(R.id.btn_share_wechat).setOnClickListener(this);
        rootView.findViewById(R.id.btn_share_moments).setOnClickListener(this);
//        rootView.findViewById(R.id.btn_share_sinaweibo).setOnClickListener(this);
//        rootView.findViewById(R.id.btn_share_sinaweibo).setVisibility(CacheConstant.debugBuildType ? View.VISIBLE : View.GONE);// TODO: 17/1/4  // TODO: 16/12/16 微博分享暂时去掉
        setContentView(rootView);
    }

    /**
     * 显示分享
     */
    public void show() {
        if (!isShowing() && mActivity != null && !mActivity.isFinishing()) {
            if (type == TYPE_SHARE_RECORD) {
                showAtLocation(mActivity.findViewById(android.R.id.content).getRootView(), Gravity.RIGHT, 0, 0);
            } else {
                showAtLocation(mActivity.findViewById(android.R.id.content).getRootView(), Gravity.CENTER | Gravity.BOTTOM, 0, 0);
            }
            if (mOnCallOverback != null) {
                mOnCallOverback.showShadow(true);
            }
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(mOnCallOverback != null){
            mOnCallOverback.showShadow(false);
        }
    }

    public void performShare(SHARE_MEDIA shareMedia) {
        String shareCode = gameEntity == null ? "" : gameEntity.code;
        ShareAction shareAction = new ShareAction(mActivity).setPlatform(shareMedia).setCallback(umShareListener);
        UMImage shareImage = new UMImage(ChessApp.sAppContext, getHost() + ApiConstants.URL_SHARE_URL_IMAGE);
        String title = mActivity.getString(R.string.share_title) + (TextUtils.isEmpty(shareCode) ? "德州扑克专家" : "邀请码" + shareCode);
        String message = "专为德州扑克专业玩家定制打造，SNG、MTT、保险模式、Ante局带您德州扑克全体验。";
        if (gameEntity != null) {
            String gameCreator = NimUserInfoCache.getInstance().getUserDisplayName(DemoCache.getAccount());
            String duration = "";
            if (gameEntity.gameMode == GameConstants.GAME_MODE_NORMAL && gameEntity.gameConfig instanceof GameNormalConfig) { //**************************************************************普通模式
                GameNormalConfig gameConfig = (GameNormalConfig) gameEntity.gameConfig;
                String normalBlind = gameConfig.blindType + "/" + gameConfig.blindType * 2;
                duration = GameConstants.getGameDurationShow(gameConfig.timeType);
                String insurance = gameConfig.tiltMode == GameConstants.GAME_MODE_INSURANCE_NOT ? "" : "，保险局";
                String ante = GameConstants.getGameAnte(gameConfig) <= 0 ? "" : ("，前注:" + GameConstants.getGameAnte(gameConfig));
                message = "“" + gameCreator + "”" + "邀请您加入牌局。 盲注：" + normalBlind + "，时长：" + duration + insurance + ante;
            } else if (gameEntity.gameMode == GameConstants.GAME_MODE_SNG && gameEntity.gameConfig instanceof GameSngConfigEntity) { //**************************************************************SNG模式
                GameSngConfigEntity gameConfig = (GameSngConfigEntity) gameEntity.gameConfig;
                String chips = "记分牌" + gameConfig.chips;
                duration = "涨盲时间" + GameConstants.getGameSngDurationMinutesShow(gameConfig.getDuration());
                String beginTime = "坐满" + gameConfig.getPlayer() + "人开赛";
                message = "“" + gameCreator + "”" + "邀请您加入SNG比赛。 " + chips + duration + beginTime;
            } else if (gameEntity.gameMode == GameConstants.GAME_MODE_MTT && gameEntity.gameConfig instanceof GameMttConfig) { //**************************************************************MTT模式
                GameMttConfig gameConfig = (GameMttConfig) gameEntity.gameConfig;
                String chips = "记分牌" + gameConfig.matchChips + "，";
                duration = "涨盲时间" + GameConstants.getGameSngDurationMinutesShow(gameConfig.matchDuration) + "，";
                SimpleDateFormat sdf = new SimpleDateFormat("MM" + "月" + "dd" + "日" + "HH:mm");
                String beginTime = "比赛时间: " + sdf.format(new Date(gameConfig.beginTime * 1000));
                message = "“" + gameCreator + "”" + "邀请您加入MTT比赛。 " + chips + duration + beginTime;
            } else if (gameEntity.gameMode == GameConstants.GAME_MODE_MT_SNG && gameEntity.gameConfig instanceof GameMtSngConfig) { //**************************************************************MT-SNG模式
                GameMtSngConfig gameConfig = (GameMtSngConfig) gameEntity.gameConfig;
                String chips = "记分牌" + gameConfig.matchChips + "，";
                duration = "涨盲时间" + GameConstants.getGameSngDurationMinutesShow(gameConfig.matchDuration) + "，";
                String beginTime = "坐满" + gameConfig.totalPlayer + "人开赛";
                message = "“" + gameCreator + "”" + "邀请您加入MT-SNG比赛。 " + chips + duration + beginTime;
            }
        }
        if(/*shareBitmap != null*/false) {//TODO: 16/12/27    图片分享去掉 //
            shareImage = new UMImage(ChessApp.sAppContext, shareBitmap);
            shareAction.withMedia(shareImage);
            if(shareMedia == SHARE_MEDIA.SINA) {
                message = mActivity.getString(R.string.share_message_sina) + " " + getHost() + ApiConstants.URL_SHARE_URL;
                shareAction.withText(message);
            } else {
            }
        } else {
            String url = getHost() + ApiConstants.URL_SHARE_URL;
            //*****如果是微信分享   加上code
            if (shareMedia == SHARE_MEDIA.WEIXIN) {
                url = ToolUtil.replaceOrAddParametor(getHost() + ApiConstants.URL_SHARE_URL, "code", shareCode);
            }
            //*****
            shareAction.withTitle(title)
                    .withText(message)
                    .withMedia(shareImage)
                    .withTargetUrl(url);
        }
        shareAction.share();
    }

    /**
     * 分享手牌
     * @param shareMedia
     * @param url
     */
    public void shareHands(SHARE_MEDIA shareMedia , String url, View view) {
        Bitmap shareBitmap = convertViewToBitmap(view);
        ShareAction shareAction = new ShareAction(mActivity)
                .setPlatform(shareMedia)
                .setCallback(umShareListener);
        UMImage shareImage = new UMImage(ChessApp.sAppContext, shareBitmap);
        shareAction.withTitle(mActivity.getString(R.string.app_name))
                .withText(mActivity.getString(R.string.share_hands_content))
                .withMedia(shareImage)
                .withTargetUrl(url);
        shareAction.share();
    }

    public void setShareBitmap(Bitmap bitmap) {
        this.shareBitmap = bitmap;
        double maxSize = 200.00;//图片允许最大空间   单位：KB
        imageZoom(maxSize, 100);//压缩
//        ThumbnailUtils.extractThumbnail(shareBitmap , 800 , 400);
    }

    public void setShareGameBillBitmap(Bitmap bitmap) {
        this.shareBitmap = bitmap;
        double maxSize = 400.00;
        imageZoom(maxSize, 80);
    }

    private void imageZoom(double maxSize , int quality) {
        //将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shareBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] b = baos.toByteArray();
        //将字节换成KB
        double mid = b.length / 1024;
        //判断bitmap占用空间是否大于允许最大空间  如果大于则压缩 小于则不压缩
        if (mid > maxSize) {
            //获取bitmap大小 是允许最大大小的多少倍
            double i = mid / maxSize;
            //开始压缩  此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
            shareBitmap = zoomImage(shareBitmap, shareBitmap.getWidth() / Math.sqrt(i), shareBitmap.getHeight() / Math.sqrt(i));
        }
    }

    public Bitmap convertViewToBitmap(View view) {
//        Bitmap bitmap = Bitmap.createBitmap(720, 1280, Bitmap.Config.RGB_565);
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //利用bitmap生成画布
        Canvas canvas = new Canvas(bitmap);
        //把view中的内容绘制在画布上
        view.draw(canvas);
        return bitmap;
    }

    /***
     * 图片的缩放方法
     * @param bgimage：源图片资源
     * @param newWidth：缩放后宽度
     * @param newHeight：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(ChessApp.sAppContext , R.string.share_success, Toast.LENGTH_SHORT).show();
            dismiss();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(ChessApp.sAppContext , R.string.share_failure, Toast.LENGTH_SHORT).show();
            dismiss();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
//            Toast.makeText(mContext , platform + " 分享取消", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_share_wechat:
//                if(mShareAPI.isInstall(mActivity, SHARE_MEDIA.WEIXIN)){
//                    //安装了客户端
//                    performShare(SHARE_MEDIA.WEIXIN);
//                } else{
//                    Toast.makeText(ChessApp.sAppContext , R.string.werixin_not_install , Toast.LENGTH_SHORT).show();
//                }
                shareWeixin(0);
                break;
            case R.id.btn_share_moments:
//                if(mShareAPI.isInstall(mActivity, SHARE_MEDIA.WEIXIN)){
//                    performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
//                } else{
//                    Toast.makeText(ChessApp.sAppContext , R.string.werixin_not_install , Toast.LENGTH_SHORT).show();
//                }
                shareWeixin(1);
                break;
//            case R.id.btn_share_sinaweibo:
//                performShare(SHARE_MEDIA.SINA);
//                break;
            case R.id.btn_share_cancel:
                dismiss();
                break;
        }
    }

    private WXEntryActivity.ShareCallback shareCallback = new WXEntryActivity.ShareCallback() {
        @Override
        public void onShareSuccess() {
            dismiss();
        }
    };

    //分享微信 type:0微信  type:1朋友圈
    private void shareWeixin(int type) {
        String url = getHost() + ApiConstants.URL_SHARE_URL;
        String shareImgUrl = getHost() + ApiConstants.URL_SHARE_URL_IMAGE;
        String shareCode = gameEntity == null ? "" : gameEntity.code;
        String title = mActivity.getString(R.string.share_title) + (TextUtils.isEmpty(shareCode) ? "德州扑克专家" : "邀请码" + shareCode);
        String message = getShareMsg();
        if (type == 0) {//微信分享
            url = ToolUtil.replaceOrAddParametor(getHost() + ApiConstants.URL_SHARE_URL, "code", shareCode);
            WXEntryActivity.startWxShare(mActivity, ApiConfig.APP_WEIXIN_DL, title, message, url, shareImgUrl, SendMessageToWX.Req.WXSceneSession, shareCallback);
        } else if (type == 1) {//朋友圈分享
            WXEntryActivity.startWxShare(mActivity, ApiConfig.APP_WEIXIN_DL, title, message, url, shareImgUrl, SendMessageToWX.Req.WXSceneTimeline, shareCallback);
        }
    }

    public void shareFromGame(int type, String url, String shareImgUrl, String title, String message, WXEntryActivity.ShareCallback callback) {
        String shareCode = gameEntity == null ? "" : gameEntity.code;
        shareImgUrl = StringUtil.isSpace(shareImgUrl) ? getHost() + ApiConstants.URL_SHARE_URL_IMAGE : shareImgUrl;
        title = StringUtil.isSpace(title) ? mActivity.getString(R.string.share_title) + (TextUtils.isEmpty(shareCode) ? "德州扑克专家" : "邀请码" + shareCode) : title;
        message = StringUtil.isSpace(message) ? getShareMsg() : message;
        if (type == 0) {//微信分享
            if (StringUtil.isSpace(url)) {
                url = ToolUtil.replaceOrAddParametor(getHost() + ApiConstants.URL_SHARE_URL, "code", shareCode);
            }
            WXEntryActivity.startWxShare(mActivity, ApiConfig.APP_WEIXIN_DL, title, message, url, shareImgUrl, SendMessageToWX.Req.WXSceneSession, callback);
        } else if (type == 1) {//朋友圈分享
            if (StringUtil.isSpace(url)) {
                url = getHost() + ApiConstants.URL_SHARE_URL;
            }
            WXEntryActivity.startWxShare(mActivity, ApiConfig.APP_WEIXIN_DL, title, message, url, shareImgUrl, SendMessageToWX.Req.WXSceneTimeline, callback);
        }
    }

    private String getShareMsg() {
        String message = "专为德州扑克专业玩家定制打造，SNG、MTT、保险模式、Ante局带您德州扑克全体验。";
        if (gameEntity != null) {
            String gameCreator = NimUserInfoCache.getInstance().getUserDisplayName(DemoCache.getAccount());
            String duration = "";
            if (gameEntity.gameMode == GameConstants.GAME_MODE_NORMAL && gameEntity.gameConfig instanceof GameNormalConfig) { //**************************************************************普通模式
                GameNormalConfig gameConfig = (GameNormalConfig) gameEntity.gameConfig;
                String normalBlind = gameConfig.blindType + "/" + gameConfig.blindType * 2;
                duration = GameConstants.getGameDurationShow(gameConfig.timeType);
                String insurance = gameConfig.tiltMode == GameConstants.GAME_MODE_INSURANCE_NOT ? "" : "，保险局";
                String ante = GameConstants.getGameAnte(gameConfig) <= 0 ? "" : ("，前注:" + GameConstants.getGameAnte(gameConfig));
                message = "“" + gameCreator + "”" + "邀请您加入牌局。 盲注：" + normalBlind + "，时长：" + duration + insurance + ante;
            } else if (gameEntity.gameMode == GameConstants.GAME_MODE_SNG && gameEntity.gameConfig instanceof GameSngConfigEntity) { //**************************************************************SNG模式
                GameSngConfigEntity gameConfig = (GameSngConfigEntity) gameEntity.gameConfig;
                String chips = "记分牌" + gameConfig.chips;
                duration = "涨盲时间" + GameConstants.getGameSngDurationMinutesShow(gameConfig.getDuration());
                String beginTime = "坐满" + gameConfig.getPlayer() + "人开赛";
                message = "“" + gameCreator + "”" + "邀请您加入SNG比赛。 " + chips + duration + beginTime;
            } else if (gameEntity.gameMode == GameConstants.GAME_MODE_MTT && gameEntity.gameConfig instanceof GameMttConfig) { //**************************************************************MTT模式
                GameMttConfig gameConfig = (GameMttConfig) gameEntity.gameConfig;
                String chips = "记分牌" + gameConfig.matchChips + "，";
                duration = "涨盲时间" + GameConstants.getGameSngDurationMinutesShow(gameConfig.matchDuration) + "，";
                SimpleDateFormat sdf = new SimpleDateFormat("MM" + "月" + "dd" + "日" + "HH:mm");
                String beginTime = "比赛时间: " + sdf.format(new Date(gameConfig.beginTime * 1000));
                if (gameConfig.beginTime == -1) {//未设置开赛时间
                    beginTime = "";
                }
                message = "“" + gameCreator + "”" + "邀请您加入MTT比赛。 " + chips + duration + beginTime;
            } else if (gameEntity.gameMode == GameConstants.GAME_MODE_MT_SNG && gameEntity.gameConfig instanceof GameMtSngConfig) { //**************************************************************MT-SNG模式
                GameMtSngConfig gameConfig = (GameMtSngConfig) gameEntity.gameConfig;
                String chips = "记分牌" + gameConfig.matchChips + "，";
                duration = "涨盲时间" + GameConstants.getGameSngDurationMinutesShow(gameConfig.matchDuration) + "，";
                String beginTime = "坐满" + gameConfig.totalPlayer + "人开赛";
                message = "“" + gameCreator + "”" + "邀请您加入MT-SNG比赛。 " + chips + duration + beginTime;
            } else if (gameEntity.gameMode == GameConstants.GAME_MODE_NORMAL && gameEntity.gameConfig instanceof PineappleConfig) { //***************************大菠萝普通
                PineappleConfig gameConfig = (PineappleConfig) gameEntity.gameConfig;
                int ante = gameConfig.getAnte();
                int chips = gameConfig.getChips();
                duration = GameConstants.getGameDurationShow(gameConfig.getDuration());
                message = "“" + gameCreator + "”" + "邀请您加入牌局。 底注：" + ante + "，记分牌：" + chips + "，时长：" + duration;
            } else if (gameEntity.gameMode == GameConstants.GAME_MODE_MTT && gameEntity.gameConfig instanceof PineappleConfigMtt) { //***************************大菠萝mtt
                PineappleConfigMtt gameConfig = (PineappleConfigMtt) gameEntity.gameConfig;
                String chips = "记分牌" + gameConfig.matchChips + "，";
                duration = "升底时间" + GameConstants.getGameSngDurationMinutesShow(gameConfig.matchDuration) + "，";
                SimpleDateFormat sdf = new SimpleDateFormat("MM" + "月" + "dd" + "日" + "HH:mm");
                String beginTime = "比赛时间: " + sdf.format(new Date(gameConfig.beginTime * 1000));
                if (gameConfig.beginTime == -1) {//未设置开赛时间
                    beginTime = "";
                }
                message = "“" + gameCreator + "”" + "邀请您加入MTT比赛。 " + chips + duration + beginTime;
            }
        }
        return message;
    }

    //如果有使用任一平台的SSO授权或者集成了facebook平台, 则必须在对应的activity中实现onActivityResult方法, 并添加如下代码
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    public void onDestroy(){
        if(mShareAPI != null){
            mShareAPI = null;
        }
    }

    public void setOnCallOverback(OnCallOverback listener){
        this.mOnCallOverback = listener;
    }

    public interface OnCallOverback{
        void showShadow(boolean show);
    }
}
