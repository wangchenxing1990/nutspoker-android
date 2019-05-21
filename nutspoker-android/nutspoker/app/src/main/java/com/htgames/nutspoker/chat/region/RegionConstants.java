package com.htgames.nutspoker.chat.region;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.DemoCache;

import java.util.HashSet;
import java.util.Set;

/**
 */
public class RegionConstants {
    public final static String DB_REGION = "region.db";
    public final static String TABLE_REGION = "region";
    public final static String COLUMN_ID = "id";
    public final static String COLUMN_NAME = "name";
    public final static String COLUMN_PID = "pid";
    public final static  String COLUMN_TYPE = "type";

    public final static int TYPE_COUNTY = 0;//type:国家级
    public final static int TYPE_PROVINCE = 1;//type:省级
    public final static int TYPE_CITY = 2;//type:市级

    public final static String COUNTY_CHINA_ID = "1";

    public static Set<String> provinceCities = null;

    /**
     * 判断是否是直辖市
     * @param cityName
     * @return
     */
    public static boolean isProvinceCity(String cityName) {
        if (provinceCities == null) {
            provinceCities = new HashSet<String>();
            provinceCities.add(DemoCache.getContext().getString(R.string.china_beijin));
            provinceCities.add(DemoCache.getContext().getString(R.string.china_tianjin));
            provinceCities.add(DemoCache.getContext().getString(R.string.china_shanghai));
            provinceCities.add(DemoCache.getContext().getString(R.string.china_chongqing));

            provinceCities.add(DemoCache.getContext().getString(R.string.china_hongkong));
            provinceCities.add(DemoCache.getContext().getString(R.string.china_macao));
            provinceCities.add(DemoCache.getContext().getString(R.string.china_taiwan));
        }
        return provinceCities.contains(cityName);
    }

    public static String getCityNameNoSuffix(String cityName) {
        return cityName.replace(DemoCache.getContext().getString(R.string.china_city), "");
    }
}
