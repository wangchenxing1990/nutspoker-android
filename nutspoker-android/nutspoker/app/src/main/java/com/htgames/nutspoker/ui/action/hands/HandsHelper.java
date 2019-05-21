package com.htgames.nutspoker.ui.action.hands;

import java.io.File;
import java.util.Comparator;

public class HandsHelper {
    private final static String TAG = "HandsHelper";

    /**
     * 获取手牌网络（阿里云）路径
     *
     * @param paipuEntity
     * @return
     */
//    public static String getHandsNetFilePath(PaipuEntity paipuEntity) {
//        if (paipuEntity == null || paipuEntity.getGameEntity() == null) {
//            return null;
//        }
//        StringBuffer stringBuffer = new StringBuffer();
//        long gameCreateTime = paipuEntity.getGameEntity().getCreateTime();
//        stringBuffer.append(getHandsNetFoler(String.valueOf(gameCreateTime)));
//        stringBuffer.append(paipuEntity.getFileName());
//        LogUtil.i(TAG, stringBuffer.toString());
//        return stringBuffer.toString();
//    }

//    public static String getHandsNetFoler(String cc_time) {
//        String re_StrTime = null;
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/");
//        // 例如：cc_time=1291778220
//        long lcc_time = Long.valueOf(cc_time);
//        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
//        return re_StrTime;
//    }

    /**
     * 获取本地牌局记录文件列表
     *
     * @param context
     * @return
     */
//    public static File[] getLocalHandFilList(Context context) {
//        File paipuLocalDir = PaipuConstants.getLocalPaipuUserDir(context);
//        if (paipuLocalDir.exists()) {
//            File[] files = paipuLocalDir.listFiles();
//            if (files != null) {
//                Arrays.sort(files, fileNameComparator);
//                return files;
//            }
//        }
//        return null;
//    }

    static Comparator fileNameComparator = new Comparator<File>() {
        @Override
        public int compare(File o1, File o2) {
            if (o1.isDirectory() && o2.isFile())
                return -1;
            if (o1.isFile() && o2.isDirectory())
                return 1;
            return o2.getName().compareTo(o1.getName());
        }
    };

    /**
     * 获取本地牌局记录文件列表
     *
     * @param context
     * @return
     */
//    public static String[] getLocalHandFileNameList(Context context) {
//        File paipuLocalDir = PaipuConstants.getLocalPaipuUserDir(context);
//        if (paipuLocalDir.exists()) {
//            String[] files = paipuLocalDir.list();
//            if (files != null) {
//                Arrays.sort(files, fileNameStrComparator);
//                return files;
//            }
//        }
//        return null;
//    }

    static Comparator fileNameStrComparator = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            int length1 = o1.length();
            int length2 = o2.length();
            int endPoint1 = 0;
            int endPoint2 = 0;
            for (int i = (length1 - 1); i >= 0; i--) {
                if (!isDigit(o1.charAt(i))) {
                    endPoint1 = i;
                    break;
                }
            }
            for (int i = (length2 - 1); i >= 0; i--) {
                if (!isDigit(o2.charAt(i))) {
                    endPoint2 = i;
                    break;
                }
            }
            String startStr1 = o1.substring(0, endPoint1);
            String startStr2 = o2.substring(0, endPoint2);
            int hand1 = 0;
            int hand2 = 0;
            try {
                hand1 = Integer.valueOf(o1.substring(endPoint1 + 1, length1));
                hand2 = Integer.valueOf(o2.substring(endPoint2 + 1, length2));
            }catch (Exception ex) {

            }
            if (startStr1.equals(startStr2)) {
                int hand = hand1 - hand2;
                return hand == 0 ? 0 : (hand > 0 ? -1 : 1);
            }
            return o2.compareTo(o1);
        }
    };

    private static final boolean isDigit(char ch) {
        return ch >= 48 && ch <= 57;
    }
}
