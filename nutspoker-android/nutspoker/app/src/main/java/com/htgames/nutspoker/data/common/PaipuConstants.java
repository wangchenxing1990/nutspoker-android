package com.htgames.nutspoker.data.common;

import android.content.Context;
import android.text.TextUtils;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.chesscircle.CacheConstant;

import java.io.File;

/**
 * 牌谱
 */
public class PaipuConstants {
    private final static String TAG = "PaipuConstants";

    //手牌
    public final static String KEY_HID = "hid";//上传的牌谱对应的牌谱id
    public final static String KEY_WIN_CHIPS = "win_chips";//盈利
    public final static String KEY_HANDS_CNT = "hands_cnt";//第几手牌
    public final static String KEY_SHEET_UID = "uid";//谁玩的手牌
    public final static String KEY_CARD_TYPE = "card_type";//牌型记录
    public final static String KEY_HAND_CARDS = "hand_cards";//手牌卡组
    public final static String KEY_POOL_CARDS = "pool_cards";//底池卡组
    public final static String KEY_CARDTYPE_CARDS = "cardtype_cards";//我的牌型卡组
    public final static String KEY_FILE_NET_PATH = "file_path";//文件路径
    public final static String KEY_FILE_NAME = "file_name";//文件网络路径
    public final static String KEY_COLLECT_COUNT = "count";//收藏的牌局数量
    public final static String KEY_COLLECT_TIME = "collect_time";//收藏牌谱的时间

    /** 牌型*/
    public static int[] cardCategories = new int[]{
            R.string.hands_category_fold ,//弃牌
            R.string.hands_category_high_card ,//高牌
            R.string.hands_category_on_pair ,//一对
            R.string.hands_category_two_pair ,//两对
            R.string.hands_category_three_of_a_kind ,//三条
            R.string.hands_category_straight ,//顺子
            R.string.hands_category_flush ,//同花
            R.string.hands_category_full_house ,//葫芦
            R.string.hands_category_four_of_a_kind ,//四条
            R.string.hands_category_straight_flush ,//同花顺
            R.string.hands_category_royal_flush //皇家同花顺
    };
    //开局：底池内无牌
    //翻牌：底池内3张牌
    //转牌：底池内4张牌
    //合牌：底池内5张牌

    public static String getCardCategoriesDesc(Context context ,int cardCategory){
        if(cardCategory == 0){
            //弃牌
            return context.getString(cardCategories[0]);
        }
        return context.getString(cardCategories[cardCategory >= cardCategories.length ? cardCategories.length - 1 : cardCategory]);
    }

    public static int getCardTypeTextColor(Context context , int cardType){
        if(cardType == 0){
            //弃牌
            return context.getResources().getColor(R.color.shop_text_no_select_color);
        }
        return context.getResources().getColor(R.color.text_select_color);
    }

    /**
     * 判断牌谱有效性，无效的话删除文件
     * 错误情况：（1）底池如果是1，2张牌  （2）手牌不为2张  （3）数据为空  (4)手牌牌型牌组不为2或者5
     * @param paipuEntity
     */
//    public static boolean checkPaipuValidity(PaipuEntity paipuEntity){
//        if(paipuEntity == null){
//            return false;
//        }
//        List<Integer> cardTypeCards = paipuEntity.getCardTypeCards();
//        if(cardTypeCards == null || (cardTypeCards.size() != 2 && cardTypeCards.size() != 5)){
//            LogUtil.i(TAG , "cardTypeCards :" + cardTypeCards.size());
//            return false;
//        }
//        if(paipuEntity.getHandCards() == null
//                || paipuEntity.getHandCards().size() != 2
//                || (paipuEntity.getPoolCards() != null && (paipuEntity.getPoolCards().size() == 1 || (paipuEntity.getPoolCards().size() == 2 )))){
//            return false;
//        }
//        return true;
//    }

//    public static File getUserPaipuCollectFile(Context context , String fileName){
//        String filePath = getUserPaipuCollectPath(context) + fileName;
//        File paipuFile = new File(filePath);
//        return paipuFile;
//    }

//    public static String getUserPaipuCollectPath(Context context){
//        String paipuCollectPath = CacheConstant.getAppHandCollectPath();
//        paipuCollectPath = paipuCollectPath + UserPreferences.getInstance(context).getUserId() + "/";
//        LogUtil.i(TAG , "paipuCollectPath :" + paipuCollectPath);
//        return paipuCollectPath;
//    }

    /**
     * 获取我的牌谱的本地路径
     * @param context
     * @return
     */
//    public static File getLocalPaipuUserDir(Context context){
//        File paipuDir = StorageUtils.getOwnCacheDirectory(context, CacheConstant.APP_FOLDER_NAME + "/handrecord/" + UserPreferences.getInstance(context).getUserId() + "/");
//        return paipuDir;
//    }

    /**
     * 获取我的牌谱文件名
     * @param context
     * @param handsId
     * @return
     */
//    public static File getPaipuFileMyNew(Context context,String handsId){
//        return getPaipuFileNew(context,UserPreferences.getInstance(context).getUserId(),handsId);
//    }

    /**
     * 获取指定账号的牌谱文件名
     * @param context
     * @param userId
     * @param handsId
     * @return
     */
    public static File getPaipuFileNew(Context context,String userId,String handsId){
        if(context == null || TextUtils.isEmpty(userId) || TextUtils.isEmpty(handsId))
            return null;
        String fileName = "TexasSheet-" + handsId;
//        File file = StorageUtils.getOwnCacheDirectory(context, CacheConstant.APP_FOLDER_NAME + "/handrecord/" + userId);
//        File file = StorageUtils.getOwnCacheDirectory(context, context.getPackageName() + "/handrecord/" + userId);
        File file = new File(CacheConstant.getAppHandRecordPath() + userId + "/" + fileName);
        return  file;
    }

//    public static String getLocalPaipuFilePath(Context context , String fileName){
//        File paipuFile = StorageUtils.getOwnCacheDirectory(context, CacheConstant.APP_FOLDER_NAME + "/handrecord/" + UserPreferences.getInstance(context).getUserId() + "/" + fileName);
//        return paipuFile.getPath();
//    }

//    public static File getPaipuCacheFile(Context context , String fileName){
//        String paipuCachePath = CacheConstant.getHandCachePath();
//        String filePath = paipuCachePath + "/" + fileName;
//        return new File(filePath);
//    }

    /**
     * 获取本地牌谱列表
     * @param context
     * @return
     */
//    public static ArrayList<PaipuEntity> getLocallPaipuList(Context context) {
//        ArrayList<PaipuEntity> paipuList = null;
//        File paipuLocalDir = getLocalPaipuUserDir(context);
//        if (paipuLocalDir.exists()) {
//            File[] files = paipuLocalDir.listFiles();
//            if (files != null && files.length != 0) {
//                LogUtil.i(TAG, "file  size:" + files.length);
//                //排序
//                List<File> fileList = new ArrayList<File>();
//                for (File f : files) {
//                    fileList.add(f);
//                }
//                Collections.sort(fileList, fileNameComparator);
//                //
//                paipuList = new ArrayList<PaipuEntity>();
//                for (File file : fileList) {
//                    PaipuEntity paipuEntity = getFileHandInfo(file);
//                    if (paipuEntity != null) {
//                        paipuList.add(paipuEntity);
//                    } else {
//                        dealInvalidFile(file);
//                    }
//                }
//            } else {
//                LogUtil.i(TAG, "file  size:" + 0);
//            }
//        }
//        return paipuList;
//    }

    /**
     * 获取本地牌谱列表(根据指定列表)
     * @param context
     * @return
     */
//    public static ArrayList<PaipuEntity> getLocallPaipuList(Context context , File[] files) {
//        ArrayList<PaipuEntity> paipuList = null;
//        if (files != null && files.length != 0) {
//            LogUtil.i(TAG, "file  size:" + files.length);
//            //
//            paipuList = new ArrayList<PaipuEntity>();
//            for (File file : files) {
//                PaipuEntity paipuEntity = getFileHandInfo(file);
//                if (paipuEntity != null) {
//                    paipuList.add(paipuEntity);
//                } else {
//                    dealInvalidFile(file);
//                }
//            }
//        } else {
//            LogUtil.i(TAG, "file  size:" + 0);
//        }
//        return paipuList;
//    }

    /**
     * 获取本地牌谱列表(根据指定列表)
     * @param context
     * @return
     */
//    public static ArrayList<PaipuEntity> getLocallPaipuList(Context context , String[] fileNames) {
//        ArrayList<PaipuEntity> paipuList = null;
//        if (fileNames != null && fileNames.length != 0) {
//            LogUtil.i(TAG, "file  size:" + fileNames.length);
//            //
//            paipuList = new ArrayList<PaipuEntity>();
//            for (String fileName : fileNames) {
//                File file = new File(PaipuConstants.getLocalPaipuFilePath(context, fileName));
//                PaipuEntity paipuEntity = getFileHandInfo(file);
//                if (paipuEntity != null) {
//                    paipuList.add(paipuEntity);
//                } else {
//                    dealInvalidFile(file);
//                }
//            }
//        } else {
//            LogUtil.i(TAG, "file  size:" + 0);
//        }
//        return paipuList;
//    }

//    public static PaipuEntity getFileHandInfo(File file) {
//        String content = readTxtFile(file);
//        if (!TextUtils.isEmpty(content)) {
//            LogUtil.i(TAG, "file :" + file.getName() + "\ncontent :" + content);
//            try {
//                PaipuEntity paipuEntity = HandJsonTools.getPaipuEntity(content);
//                if (checkPaipuValidity(paipuEntity)) {
//                    paipuEntity.setFileName(file.getName());
//                    paipuEntity.setFileNetPath(HandsHelper.getHandsNetFilePath(paipuEntity));
//                    LogUtil.i(TAG, HandsHelper.getHandsNetFilePath(paipuEntity));
//                    paipuEntity.setFileLocalPath(file.getPath());
//                    //解析新的内容
//                    paipuEntity.setJsonDataStr(PaipuAttachment.packToJsonData(paipuEntity).toJSONString());
//                    return paipuEntity;
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }

//    static Comparator fileNameComparator = new Comparator<File>() {
//        @Override
//        public int compare(File o1, File o2) {
//            if (o1.isDirectory() && o2.isFile())
//                return -1;
//            if (o1.isFile() && o2.isDirectory())
//                return 1;
//            return o2.getName().compareTo(o1.getName());
//        }
//    };

    //处理无效文件
//    public static void dealInvalidFile(File file) {
//        if (file != null) {
//            LogUtil.i(TAG, "dealInvalidFile :" + file.getName());
//            FileUtil.deleteFile(file);
//        }
//    }


//    public static ArrayList<File> getLocallPaipuFiles(Context context) {
//        File[] files = null;
//        File paipuLocalDir = getLocalPaipuUserDir(context);
//        if (paipuLocalDir.exists()) {
//            files = paipuLocalDir.listFiles();
//            if(files != null && files.length != 0){
//                int size = files.length;
//                LogUtil.i(TAG , "file  size:" + size);
//                for (int i = 0 ; i < size ; i++){
//                    LogUtil.i(TAG , "file :" + files[i].getName());
//                    LogUtil.i(TAG , "content :" + readTxtFile(files[i]));
//                }
//            } else{
//                LogUtil.i(TAG , "file  size:" + 0);
//            }
//        }
//        return null;
//    }

    //取前4个字节，转为int
//    public static String readTxtFile(File file) {
//        String content = ""; //文件内容字符串
//        //如果path是传递过来的参数，可以做一个非目录的判断
//        if (file.isDirectory()) {
//            LogUtil.i(TAG, "The File doesn't not exist.");
//        } else {
//            InputStream instream = null;
//            try {
//                instream = new FileInputStream(file);
//                if (instream != null) {
//                    byte[] bytes = new byte[4];
//                    byte[] contentBytes = new byte[1024 * 10];
//                    instream.read(bytes);
//                    int contentRows = BinaryTools.byte2int(bytes);
//                    LogUtil.i(TAG, "contentRows :" + contentRows);
//                    instream.skip(contentRows - 4);
//                    int size = 0;
//                    int len; // 读取到的数据长度
//                    // 开始读取
//                    while ((len = instream.read(contentBytes)) != -1) {
//                        size += len;
//                    }
//                    content = new String(contentBytes, 0, size);
//                }
//            } catch (java.io.FileNotFoundException e) {
//                LogUtil.i(TAG, "The File doesn't not exist.");
//            } catch (IOException e) {
//                LogUtil.i(TAG, e.getMessage());
//            } catch (Exception e) {
//                LogUtil.i(TAG, e.getMessage());
//            } finally {
//                try {
//                    if (instream != null) {
//                        instream.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return content;
//    }

//    public static void deleteLocallPaipuList(Context context , String[] fileNames) {
//        if (fileNames != null && fileNames.length != 0) {
//            LogUtil.i(TAG, "delete file  size:" + fileNames.length);
//            File file = null;
//            for (String fileName : fileNames) {
//                file = new File(PaipuConstants.getLocalPaipuFilePath(context, fileName));
//                if (file != null && file.exists()) {
//                    file.delete();
//                    LogUtil.i("PaipuRecordActivity", "删除多余的 ： " + fileName);
//                }
//            }
//        }
//    }

//    public static String getPaipuCollectFilePathUrl(String filePath){
//        return ApiConstants.HOST_FILE + filePath;
//    }
}
