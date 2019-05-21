package com.htgames.nutspoker.config.shop;

import android.content.Context;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.constants.VipConstants;
import com.netease.nim.uikit.bean.VipConfigEntity;

import java.util.ArrayList;

/**
 * Created by 20150726 on 2015/12/21.
 */
public class ShopVipConfig {
    public static ArrayList<VipConfigEntity> list = null;

    public final static int[] descId = new int[]{R.string.shop_vip_column_giving , R.string.shop_vip_column_create_count ,
            R.string.shop_vip_column_club_count ,R.string.shop_vip_column_record ,R.string.shop_vip_column_collection ,R.string.shop_vip_column_anlysis};

    public final static String[] whiteContent = new String[]{
            String.valueOf(VipConstants.LEVEL_WHITE_GAIN) ,
            String.valueOf(VipConstants.LEVEL_WHITE_GAMECOUNT),
            String.valueOf(VipConstants.LEVEL_WHITE_CLUBCOUNT),
            DemoCache.getContext().getString(R.string.days_count , VipConstants.LEVEL_WHITE_RECORD_HANDCOUNT),
            DemoCache.getContext().getString(R.string.hands_count , VipConstants.LEVEL_WHITE_COLLECT_HANDCOUNT),
            DemoCache.getContext().getString(R.string.shop_vip_routine_anlysis)};
    public final static String[] blackContent = new String[]{
            String.valueOf(VipConstants.LEVEL_BLACK_GAIN)  ,
            String.valueOf(VipConstants.LEVEL_BLACK_GAMECOUNT),
            String.valueOf(VipConstants.LEVEL_BLACK_CLUBCOUNT) ,
            DemoCache.getContext().getString(R.string.days_count , VipConstants.LEVEL_BLACK_RECORD_HANDCOUNT),
            DemoCache.getContext().getString(R.string.vip_no_upper_limit),
            DemoCache.getContext().getString(R.string.shop_vip_professional_anlysis)};

    public static ArrayList<VipConfigEntity> getVipConfigList(Context context){
        if(list == null){
            list = new ArrayList<VipConfigEntity>();
            int size = descId.length;
            for(int i = 0 ; i < size ; i ++){
                VipConfigEntity vipConfigEntity = new VipConfigEntity(context.getString(descId[i]) , whiteContent[i] ,blackContent[i]);
                list.add(vipConfigEntity);
            }
        }
        return list;
    }
}
