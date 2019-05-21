package com.netease.nim.uikit.constants;

import android.content.Context;
import android.content.res.Resources;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.bean.CountryEntity;
import com.netease.nim.uikit.contact.core.query.PinYin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 城市CODE
 */
public class CountryCodeConstants {

    public static final String CODE_CHINA = "86";
    public static final String CODE_CHINA_TAIWAN = "886";
    public static final String CODE_CHINA_HK = "852";

    /**
     * 获取国家列表
     *
     * @param context
     * @return
     */
    public static ArrayList<CountryEntity> getCountryList(Context context) {
        ArrayList<CountryEntity> countryList = new ArrayList<CountryEntity>();
        Resources res = context.getResources();
        String[] countries = res.getStringArray(R.array.country);
        String[] countryCodes = res.getStringArray(R.array.country_code);
        int size = countries.length;
        for (int i = 0; i < size; i++) {
            String countryName = countries[i];
            CountryEntity countryEntity = new CountryEntity();
            countryEntity.countryName = (countryName);
            countryEntity.countryCode = (countryCodes[i]);
            countryEntity.sortKey = (PinYin.getLeadingLo(countryName));
            countryEntity.pinyin = (PinYin.getPinYin(countryName));
            countryList.add(countryEntity);
        }
        return countryList;
    }

    public static Map<String, String> getCountryCodeMap(Context context) {
        HashMap<String, String> countryMap = new HashMap<String, String>();
        Resources res = context.getResources();
        String[] countries = res.getStringArray(R.array.country);
        String[] countryCodes = res.getStringArray(R.array.country_code);
        int size = countries.length;
        for (int i = 0; i < size; i++) {
            countryMap.put(countryCodes[i], countries[i]);
        }
        return countryMap;
    }
}
