package com.htgames.nutspoker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.htgames.nutspoker.chat.MessageDataCache;
import com.htgames.nutspoker.chat.contact.ContactHelper;
import com.htgames.nutspoker.chat.main.util.crash.AppCrashHandler;
import com.htgames.nutspoker.chat.notification.CustomNotificationCache;
import com.htgames.nutspoker.chat.session.SessionHelper;
import com.htgames.nutspoker.config.UniversalImageLoaderUtil;
import com.htgames.nutspoker.data.DataManager;
import com.htgames.nutspoker.game.helper.GameConfigHelper;
import com.htgames.nutspoker.net.HttpManager;
import com.htgames.nutspoker.push.GeTuiHelper;
import com.htgames.nutspoker.push.GeTuiTools;
import com.htgames.nutspoker.thirdPart.umeng.UmengConfig;
import com.htgames.nutspoker.tool.AssetsDatabaseManager;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.htgames.nutspoker.ui.activity.System.WelcomeActivity;
import com.htgames.nutspoker.util.SystemUtil;
import com.htgames.nutspoker.util.word.WordFilter;
import com.netease.nim.uikit.ImageLoaderKit;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.api.ApiConfig;
import com.netease.nim.uikit.api.HostManager;
import com.netease.nim.uikit.bean.GameMgrBean;
import com.netease.nim.uikit.bean.UserEntity;
import com.netease.nim.uikit.cache.FriendDataCache;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.framework.ThreadUtil;
import com.netease.nim.uikit.common.preference.GamePreferences;
import com.netease.nim.uikit.common.preference.SettingsPreferences;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.contact.ContactProvider;
import com.netease.nim.uikit.contact.core.query.PinYin;
import com.netease.nim.uikit.interfaces.SensitiveFilter;
import com.netease.nim.uikit.session.module.input.InputPanel;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderThumbBase;
import com.netease.nim.uikit.team.helper.TeamHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimStrings;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.MessageNotifierCustomization;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * MultiDexApplication
 *  *
 * Java / Android OOM编程纪要:
 * 1.少用Activity的Context，除了必须要用的如Dialog等 可以使用 {@link ChessApp.sAppContext}
 * 2.对于用到的Context需要及时释放置为null
 * 3.线程的无节制使用也会耗尽内存，尽量使用 {@link ThreadUtil.Execute}函数
 * 4.对于占有内存较大的变量，我们需要在销毁的时候把它置为null
 * 5.在一个Activity的基类上定义一个static final Handler，以后都用这个Handler，而不是无节制的new Handler
 * 6.对于一些经常用到的类，我们可以把它声明到Application中的静态变量，这样不会引发无节制的生成新的，占用空间
 * 7.对于一些需要单独创建的单列类，我们需要对其中引用到的的Context、View等大内存对象及时释放，不然内存会一泄千里
 * 8.尽量少用final关键字，每当你用一次，意味着就消耗一段固定的内存，基础常量或一些必须的字符串可以用，但是其他Object类型尽量少用
 */
public class ChessApp extends MultiDexApplication {
    SettingsPreferences mSettingsPreferences;
    public static int mKeyBoardH = 0;
    public static boolean isGameIng = false;//是否是德州游戏中

    public static RequestQueue sRequestQueue;
    public static Context sAppContext;
    public static DataManager sDataManager;
    //当前CPU数量*2+1
    private RefWatcher mRefWatcher;

    public static HashMap<String, Integer> unreadChatNumPerTeam = new HashMap<String, Integer>();//<teamId, 这个俱乐部的未读消息个数>       ========列表包含游戏群(即所有高级群)，但是计算俱乐部消息红点数目时会过滤掉游戏群
    public static HashMap<String, String> newestMsgContentPerTeam = new HashMap<String, String>();//<teamId, 这个俱乐部的最新消息内容>            ========列表包含游戏群(即所有高级群)，但是计算俱乐部消息红点数目时会过滤掉游戏群
    public static ArrayList<Team> teamList = new ArrayList<>();//俱乐部群list
    public static ArrayList<Team> allList = new ArrayList<Team>();//俱乐部群list + 游戏群list

    public static HashMap<String, RecentContact> unreadP2PMsg = new HashMap<String, RecentContact>();//<uid, 未读的p2p消息>
    //上面5个列表保存到缓存即可，云信也保存的有

    public static HashMap<String, ArrayList<GameMgrBean>> gameChannels = new HashMap<>();//<gid, 牌局管理员列表>  每个牌局(都是mtt比赛)的管理员列表
    public static HashMap<String, ArrayList<UserEntity>> gameManagers = new HashMap<>();//<gid, 牌局管理员列表>  每个牌局(都是mtt比赛)的管理员列表
    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = getApplicationContext();
        mSettingsPreferences = SettingsPreferences.getInstance(getApplicationContext());
        initEnvConfig();
        if (CacheConstant.debugBuildType) {
            mRefWatcher = LeakCanary.install(this);
        }
        CacheConstant.init(this, sAppContext);
        AppCrashHandler.getInstance(this);//捕获崩溃 // crash handler
        //test dump
        //mSettingsPreferences.getAuthcodeTime();
        sRequestQueue = HttpManager.getInstance(this);
//        FrescoUtil.init(getApplicationContext());
//        Stetho.initializeWithDefaults(this);
        UmengConfig.init();
        DemoCache.setContext(this.getApplicationContext());
        initNIM();
        if (inMainProcess()) {
            PinYin.init(this);// init pinyin
            PinYin.validate();
            initUiKit();// 初始化UIKit模块(注册必要的观察者 都在这个里面实现)
            // 注册消息监听&缓存
            initSystemMessageCache();
            CustomNotificationCache.getInstance().registerObservers(true);//注册自定义通知
            NIMClient.toggleNotification(mSettingsPreferences.isMessageNotice());// 开启/关闭通知栏消息提醒
            registerLocaleReceiver(true);// 注册语言变化监听
        }
        // 数据库管理初始化
        AssetsDatabaseManager.initManager(getApplicationContext());
        GeTuiTools.init(getApplicationContext());//初始化个推数据
//        GeTuiHelper.setTag(getApplicationContext());
        //下面代码是为了app被回收的时候一些全局变量重置的crash问题
        GameConfigHelper.dealGameConfig(GamePreferences.getInstance(getApplicationContext()).getGameConfigData());
        GameConfigHelper.dealGameConfigOmaha(GamePreferences.getInstance(getApplicationContext()).getGameConfigDataOmaha());
        GameConfigHelper.dealGameConfigPineapple(GamePreferences.getInstance(getApplicationContext()).getGameConfigDataPineapple());
        initWordFilter();
        //https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
    }

    //初始化系统消息缓存
    private void initSystemMessageCache() {
        MessageDataCache.getInstance().registerObservers(true);
        if (!TextUtils.isEmpty(NimUIKit.getAccount())) {
//            MessageDataCache.getInstance().clearCache();
//            MessageDataCache.getInstance().buildCache();
        }
    }

    public static RequestQueue getRequestQueue() {
        return sRequestQueue;
    }

    /**
     * 初始化UiKit
     */
    private void initUiKit() {
        // 设置用户资料提供者（必须）,通讯录提供者（必须）
        NimUIKit.init(this, infoProvider, contactProvider, UniversalImageLoaderUtil.getDefaultConfig(getApplicationContext()), BuildConfig.DEBUG);
        // 设置地理位置提供者。如果需要发送地理位置消息，该参数必须提供。如果不需要，可以忽略。
//        NimUIKit.setLocationProvider(new NimDemoLocationProvider());
        //会话窗口的定制初始化
        SessionHelper.init();
        // 通讯录列表定制初始化
        ContactHelper.init();
    }

    /**
     * 初始化云信
     */
    private void initNIM() {
        NIMClient.init(this, getLoginInfo(), getOptions());//自动登录
    }

    // 如果返回值为null，则全部使用默认参数。
    private SDKOptions getOptions() {
        //glp test   http://dev.netease.im/docs?doc=android&#初始化 SDK
        SDKOptions options = new SDKOptions();
        /*注意：从SDK 2.2.0版本开始， LoginInfo 中添加了可选属性 AppKey，支持在登录的时候设置 AppKey；如果不填，则优先使用 SDKOptions 中配置的 AppKey；如果也没有，则使用 AndroidManifest.xml 中配置的 AppKey（默认方式）。建议使用默认方式。*/
        String appKey = ApiConfig.getYxinKeyValue(sAppContext);//release版本把key字符串置空，放到gradle里面初始化
//        if (!StringUtil.isSpace(appKey)) {
//            options.appKey = appKey;
//        }
        // 如果将新消息通知提醒托管给SDK完成，需要添加以下配置。否则无需设置。
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        if (!TextUtils.isEmpty(NimUIKit.getAccount())) {
            config.notificationEntrance = MainActivity.class; // 点击通知需要跳转到的界面
        } else {
            config.notificationEntrance = WelcomeActivity.class; // 点击通知需要跳转到的界面
        }
        config.downTimeToggle = false;
        config.notificationSmallIconId = R.mipmap.icon;
        config.vibrate = mSettingsPreferences.isMessageShake();//开启震动
        config.ring = mSettingsPreferences.isMessageSound();//开启声音提醒
        // 通知铃声的uri字符串
//        config.notificationSound = "android.resource://" + getPackageName() + "/raw/msg";  //就用系统的提示音就好了，否则一个msg.wav增加apk大小
        options.statusBarNotificationConfig = config;
        DemoCache.setNotificationConfig(config);

        // 定制通知栏提醒文案（可选，如果不定制将采用SDK默认文案）
        options.messageNotifierCustomization = messageNotifierCustomization;

        // 配置数据库加密秘钥
//        options.databaseEncryptKey = "NETEASE";

        // 配置保存图片，文件，log等数据的目录
        // 如果options中没有设置这个值，SDK会使用下面代码示例中的位置作为sdk的数据目录。
        // 该目录目前包含log, file, image, audio, video, thumb这6个目录。
        // 如果第三方APP需要缓存清理功能， 清理这个目录下面个子目录的内容即可。
        String sdkPath = CacheConstant.getNimPath();
//        String sdkPath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/nim";
        options.sdkStorageRootPath = sdkPath;
        // 配置是否需要预下载附件缩略图，默认为true
        options.preloadAttach = true;
        // 配置附件缩略图的尺寸大小，该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2
        options.thumbnailSize = MsgViewHolderThumbBase.getImageMaxEdge();
//        options.thumbnailSize = BaseTools.getWindowWidth(getApplicationContext()) / 2;
        // 用户信息提供者, 目前主要用于新消息通知栏中，显示消息来源的头像和昵称，此项如果为null，则显示程序图标
        options.userInfoProvider = infoProvider;
        return options;
    }

    private MessageNotifierCustomization messageNotifierCustomization = new MessageNotifierCustomization() {
        @Override
        public String makeNotifyContent(String nick, IMMessage message) {
            return null;// 采用SDK默认文案
        }

        @Override
        public String makeTicker(String nick, IMMessage message) {
            return null;// 采用SDK默认文案
        }
    };

    // 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
    private LoginInfo getLoginInfo() {
        String account = UserPreferences.getInstance(getApplicationContext()).getUserId();
        String token = UserPreferences.getInstance(getApplicationContext()).getUserToken();
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            DemoCache.setAccount(account.toLowerCase());
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }

    /**
     * 用户资料提供者
     * 网易云信不托管用户资料数据，用户资料由第三方 APP 服务器自行管理，当 UI 组件显示需要用到用户资料（UserInfo）时，会通过 UserInfoProvider 来获取
     */
    private UserInfoProvider infoProvider = new UserInfoProvider() {
        @Override
        public UserInfo getUserInfo(String account) {
            //根据用户帐号返回用户资料，一般APP的设计会先从本地缓存中获取，
            // 当本地缓存中没有时，则需要异步请求远程服务器， 当数据返回时需要通知UIKit刷新界面（重新载入用户资料数据），接口为NimUIKit.notifyUserInfoChanged(accounts);
            UserInfo user = NimUserInfoCache.getInstance().getUserInfo(account);
            if (user == null) {
                NimUserInfoCache.getInstance().getUserInfoFromRemote(account, null);
            }
            return user;
        }

        @Override
        public int getDefaultIconResId() {
            return R.mipmap.default_male_head;
        }

        @Override
        public Bitmap getAvatarForMessageNotifier(String account) {
            //获取广播通知头像通知
            /**
             * 注意：这里最好从缓存里拿，如果读取本地头像可能导致UI进程阻塞，导致通知栏提醒延时弹出。
             */
            UserInfo user = getUserInfo(account);
            return (user != null) ? ImageLoaderKit.getNotificationBitmapFromCache(user) : null;
        }

        @Override
        public String getDisplayNameForMessageNotifier(String account, String sessionId, SessionTypeEnum sessionType) {
            String displayName = null;
            if (sessionType == SessionTypeEnum.P2P) {
                displayName = NimUserInfoCache.getInstance().getAlias(account);
            } else if (sessionType == SessionTypeEnum.Team) {
                Team team = TeamDataCache.getInstance().getTeamById(sessionId);
                if (team != null && (TextUtils.isEmpty(team.getName()) || ClubConstant.GROUP_IOS_DEFAULT_NAME.equals(team.getName()))) {
                    displayName = TeamHelper.getTeamNameByMember(sessionId);
                } else {
                    displayName = TeamDataCache.getInstance().getTeamNick(sessionId, account);
                    if (TextUtils.isEmpty(displayName)) {
                        displayName = NimUserInfoCache.getInstance().getAlias(account);
                    }
                }
            }
            // 返回null，交给sdk处理。如果对方有设置nick，sdk会显示nick
            if (TextUtils.isEmpty(displayName)) {
                return null;
            }
            //Log.d("Chess" , "空：" + displayName);
            return displayName;
        }

        @Override
        public Bitmap getTeamIcon(String tid) {
            //获取群组的ICON
            Drawable drawable = getResources().getDrawable(R.mipmap.default_club_head);
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }
            return null;
        }
    };

    /**
     * 通讯录提供者
     *
     * @return
     */
    private ContactProvider contactProvider = new ContactProvider() {
        @Override
        public List<UserInfoProvider.UserInfo> getUserInfoOfMyFriends() {
            List<NimUserInfo> nimUsers = NimUserInfoCache.getInstance().getAllUsersOfMyFriend();
            List<UserInfoProvider.UserInfo> users = new ArrayList<>(nimUsers.size());
            if (!nimUsers.isEmpty()) {
                users.addAll(nimUsers);
            }
            return users;
        }

        @Override
        public int getMyFriendsCount() {
            return FriendDataCache.getInstance().getMyFriendCounts();
        }

        @Override
        public String getUserDisplayName(String account) {
            return NimUserInfoCache.getInstance().getUserDisplayName(account);
        }
    };

    /**
     * 是否在当前进程中
     *
     * @return
     */
    public boolean inMainProcess() {
        String packageName = getPackageName();
        String processName = SystemUtil.getProcessName(this);
        return packageName.equals(processName);
    }

    private void registerLocaleReceiver(boolean register) {
        if (register) {
            updateLocale();
            IntentFilter filter = new IntentFilter(Intent.ACTION_LOCALE_CHANGED);
            registerReceiver(localeReceiver, filter);
        } else {
            unregisterReceiver(localeReceiver);
        }
    }

    private BroadcastReceiver localeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
                updateLocale();
            }
        }
    };

    private void updateLocale() {
        NimStrings strings = new NimStrings();
        strings.status_bar_multi_messages_incoming = getString(R.string.nim_status_bar_multi_messages_incoming);
        strings.status_bar_image_message = getString(R.string.nim_status_bar_image_message);
        strings.status_bar_audio_message = getString(R.string.nim_status_bar_audio_message);
        strings.status_bar_custom_message = getString(R.string.nim_status_bar_custom_message);
        strings.status_bar_file_message = getString(R.string.nim_status_bar_file_message);
        strings.status_bar_location_message = getString(R.string.nim_status_bar_location_message);
        strings.status_bar_notification_message = getString(R.string.nim_status_bar_notification_message);
        strings.status_bar_ticker_text = getString(R.string.nim_status_bar_ticker_text);
        strings.status_bar_unsupported_message = getString(R.string.nim_status_bar_unsupported_message);
        strings.status_bar_video_message = getString(R.string.nim_status_bar_video_message);
        strings.status_bar_hidden_message_content = getString(R.string.nim_status_bar_hidden_msg_content);
        NIMClient.updateStrings(strings);
    }

    /**
     * 打开StrictMode
     */
    public void openStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() // 构造StrictMode
                .detectDiskReads() // 当发生磁盘读操作时输出
                .detectDiskWrites()// 当发生磁盘写操作时输出
                .detectNetwork() // 访问网络时输出，这里可以替换为detectAll() 就包括了磁盘读写和网络I/O
                .penaltyLog() // 以日志的方式输出
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects() // 探测SQLite数据库操作
                .penaltyLog() // 以日志的方式输出
                .penaltyDeath().build());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /**
     * 更新消息提醒设置
     */
    public void updateStatusBarNotificationConfig() {
        // 开启/关闭通知栏消息提醒
        NIMClient.toggleNotification(mSettingsPreferences.isMessageNotice());
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        config.notificationEntrance = MainActivity.class;
        config.notificationSmallIconId = R.mipmap.icon;
        config.vibrate = mSettingsPreferences.isMessageShake();//开启震动
        config.ring = mSettingsPreferences.isMessageSound();//开启声音提醒
        // 更新消息提醒设置
        NIMClient.updateStatusBarNotificationConfig(config);
    }

    /**
     * 开启秒免打扰模式：
     * 运用场景：进入游戏后
     */
    public void setNotificationNoDisturbing() {
        // 开启/关闭通知栏消息提醒
        NIMClient.toggleNotification(false);
    }

    /**
     * 设置是否在游戏中
     *
     * @param gameIng
     */
    public void setGameIng(boolean gameIng) {
        if (isGameIng != gameIng) {
            this.isGameIng = gameIng;
            if (isGameIng) {
                setNotificationNoDisturbing();//游戏中的时候，设置消息静音
            } else {
                updateStatusBarNotificationConfig();//恢复消息提醒机制
            }
        }
    }

    private void initWordFilter() {
        WordFilter.sign = '*';
        WordFilter.load();
        InputPanel.setFilter(new SensitiveFilter() {
            @Override
            public String doFilter(String message) {
                return WordFilter.doFilter(message);
            }
        });
//
//        CommonRecentViewHolder.setFilter(new SensitiveFilter() {
//            @Override
//            public String doFilter(String message) {
//                return WordFilter.doFilter(message);
//            }
//        });
//
//        CommonRecentViewHolder1.setFilter(new SensitiveFilter() {
//            @Override
//            public String doFilter(String message) {
//                return WordFilter.doFilter(message);
//            }
//        });
    }

    public static String GetString(int resId){
        return sAppContext.getResources().getString(resId);
    }

    private void initEnvConfig() {
//        try {
//            ApplicationInfo info= getApplicationInfo();
//            debugBuildType = ((info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
//        } catch (Exception e) {
//        }
        if ("release".equals(BuildConfig.BUILD_TYPE)) {
            CacheConstant.debugBuildType = false;
        } else if ("debug".equals(BuildConfig.BUILD_TYPE)) {
            CacheConstant.debugBuildType = true;
        }
        ApiConfig.isTestVersion = mSettingsPreferences.sp_setting.getBoolean(SettingsPreferences.KEY_APP_IS_TEST_VERSION, true);
        if (!CacheConstant.debugBuildType) {
            ApiConfig.isTestVersion = CacheConstant.debugBuildType;
        }
        LogUtil.i("zzh", "debugBuildType:" + CacheConstant.debugBuildType + "     ApiConfig.isTestVersion：" + ApiConfig.isTestVersion);
        HostManager.init();
    }

    /**
     * 获取application中指定的meta-data
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    public static String getAppMetaData(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = sAppContext.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(sAppContext.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return resultData;
    }

    public static void showDebugView() {
        if (!CacheConstant.debugBuildType) {
            return;
        }
        try {
            Class<?> debugClazz = Class.forName("com.htgames.nutspoker.debug.MiExToast");//完整类名
            if (debugObject == null) {
                Constructor<?> constructor = debugClazz.getDeclaredConstructor(Context.class);
                constructor.setAccessible(true);
                debugObject = constructor.newInstance(sAppContext);//获得实例
            }
            Method getAuthor = debugClazz.getDeclaredMethod("show");//获得私有方法
            getAuthor.setAccessible(true);//调用方法前，设置访问标志
            getAuthor.invoke(debugObject);//使用方法
        } catch (Exception e) {
            LogUtil.i("MiExToast", e == null ? "e=null" : e.toString());
        }
    }

    public static Object debugObject;
    public static void hideDebugView() {
        if (!CacheConstant.debugBuildType) {
            return;
        }
        try {
            Class<?> debugClazz = Class.forName("com.htgames.nutspoker.debug.MiExToast");//完整类名
            if (debugObject == null) {
                Constructor<?> constructor = debugClazz.getDeclaredConstructor(Context.class);
                constructor.setAccessible(true);
                debugObject = constructor.newInstance(sAppContext);//获得实例
            }
            Method getAuthor = debugClazz.getDeclaredMethod("hide");//获得私有方法
            getAuthor.setAccessible(true);//调用方法前，设置访问标志
            getAuthor.invoke(debugObject);//使用方法
        } catch (Exception e) {
            LogUtil.i("MiExToast", e == null ? "e=null" : e.toString());
        }
    }

    public static void showNewDebugViewContent(String content) {
        if (!CacheConstant.debugBuildType) {
            return;
        }
        try {
            Object[] params = new Object[1];
            params[0] = content;
            Class<?> debugClazz = Class.forName("com.htgames.nutspoker.debug.MiExToast");//完整类名
            if (debugObject == null) {
                Constructor<?> constructor = debugClazz.getDeclaredConstructor(Context.class);
                constructor.setAccessible(true);
                debugObject = constructor.newInstance(sAppContext);//获得实例
            }
            Method getAuthor = debugClazz.getDeclaredMethod("showNewDebugViewContent", String.class);//获得私有方法
            getAuthor.setAccessible(true);//调用方法前，设置访问标志
            getAuthor.invoke(debugObject, params);//使用方法
        }
        catch (Exception e) {
            LogUtil.i("MiExToast", e == null ? "e=null" : e.toString());
        }
    }
}
