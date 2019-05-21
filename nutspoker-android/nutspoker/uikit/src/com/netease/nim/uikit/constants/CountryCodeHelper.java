package com.netease.nim.uikit.constants;

import com.netease.nim.uikit.api.ApiConfig;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.common.util.BaseTools;
import com.netease.nim.uikit.chesscircle.DealerConstant;

/**
 * Created by 20150726 on 2016/5/21.
 */
public class CountryCodeHelper {

    public static String getCurrentLocalCountryCode() {
        String countryCode = UserPreferences.getInstance(CacheConstant.sAppContext).getUserCountryCode();//优先从sharedpreference.xml里面取countryCode，取不到的话再根据语言取
        if (DealerConstant.isNumeric(countryCode)) {
            return countryCode;
        }

        String languae = BaseTools.getLanguage();
        countryCode = CountryCodeConstants.CODE_CHINA;


        if (ApiConfig.AppVersion.isTaiwanVersion) {
            countryCode = CountryCodeConstants.CODE_CHINA_TAIWAN;
        } else {
            if (languae.contains("TW") || languae.contains("tw")) {
                countryCode = CountryCodeConstants.CODE_CHINA_TAIWAN;
            } else if (languae.contains("HK") || languae.contains("hk")) {
                countryCode = CountryCodeConstants.CODE_CHINA_HK;
            }
        }
        return countryCode;
    }
}
