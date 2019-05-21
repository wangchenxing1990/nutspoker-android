package com.htgames.nutspoker.config;

import com.htgames.nutspoker.R;

/**
 * 数据分析配置表
 */
public class AnalysisConfig {
    public final static int ANALYSIS_NAME_ABBREVIATION = 1;//英文缩写
    public final static int ANALYSIS_NAME = 2;//名称
    public final static int ANALYSIS_DESC = 3;//名称

    //标准区间（最小值）
    public final static int[] data_analysis_good_min= new int[]{50 , 25 ,40 , 2 , 40 , 10 , 7 , 49 , 3 , 14 , 68 , 2 , 2};
    //标准区间（最大值）
    public final static int[] data_analysis_good_max= new int[]{67 , 35 ,50 , 3 , 54 , 22 , 13 , 87 ,5 , 39 , 87 , 11 , 12};
    //一般区间（最小值）
    public final static int[] data_analysis_improve_min= new int[]{36 , 20 ,32 , 1 ,36 , 8 , 5 , 36 , 2 , 9 , 60 , 2 , 0};
    //一般区间（最大值）
    public final static int[] data_analysis_improve_max= new int[]{ 69 , 40 , 52 , 4 ,56 , 26 , 14 , 91 , 6 , 43 , 93 , 14 , 14};

    public enum Column {
        WSD, WTSD, WWSF, AF, AFQ, VPIP, PFR, PFR_VPIP, BET, STL, FOLD_STL, SB_BET, BB_BET;
    }

    /**
     * 缩写
     * @param column
     * @return
     */
    public static int getAnalysisColumnAbbreviationName(Column column) {
        switch (column) {
            case WSD:
                return R.string.data_analysis_wsd;
            case WTSD:
                return R.string.data_analysis_wtsd;
            case WWSF:
                return R.string.data_analysis_wwsf;
            case AF:
                return R.string.data_analysis_af;
            case AFQ:
                return R.string.data_analysis_afq;
            case VPIP:
                return R.string.data_analysis_vpip;
            case PFR:
                return R.string.data_analysis_prf;
            case PFR_VPIP:
                return R.string.data_analysis_prfvpip;
            case BET:
                return R.string.data_analysis_3bet;
            case STL:
                return R.string.data_analysis_stl;
            case FOLD_STL:
                return R.string.data_analysis_fold;
            case SB_BET:
                return R.string.data_analysis_sb;
            case BB_BET:
                return R.string.data_analysis_bb;
        }
        return 0;
    }

    /**
     * 分析名称
     * @param column
     * @return
     */
    public static int getAnalysisColumnName(Column column) {
        switch (column) {
            case WSD:
                return R.string.data_analysis_wsd_name;
            case WTSD:
                return R.string.data_analysis_wtsd_name;
            case WWSF:
                return R.string.data_analysis_wwsf_name;
            case AF:
                return R.string.data_analysis_af_name;
            case AFQ:
                return R.string.data_analysis_afq_name;
            case VPIP:
                return R.string.data_analysis_vpip_name;
            case PFR:
                return R.string.data_analysis_prf_name;
            case PFR_VPIP:
                return R.string.data_analysis_prfvpip_name;
            case BET:
                return R.string.data_analysis_3bet_name;
            case STL:
                return R.string.data_analysis_stl_name;
            case FOLD_STL:
                return R.string.data_analysis_fold_name;
            case SB_BET:
                return R.string.data_analysis_sb_name;
            case BB_BET:
                return R.string.data_analysis_bb_name;
        }
        return 0;
    }

    /**
     * 描述
     * @param column
     * @return
     */
    public static int getAnalysisColumnDesc(Column column) {
        switch (column) {
            case WSD:
                return R.string.data_analysis_wsd_desc;
            case WTSD:
                return R.string.data_analysis_wtsd_desc;
            case WWSF:
                return R.string.data_analysis_wwsf_desc;
            case AF:
                return R.string.data_analysis_af_desc;
            case AFQ:
                return R.string.data_analysis_afq_desc;
            case VPIP:
                return R.string.data_analysis_vpip_desc;
            case PFR:
                return R.string.data_analysis_prf_desc;
            case PFR_VPIP:
                return R.string.data_analysis_prfvpip_desc;
            case BET:
                return R.string.data_analysis_3bet_desc;
            case STL:
                return R.string.data_analysis_stl_desc;
            case FOLD_STL:
                return R.string.data_analysis_fold_desc;
            case SB_BET:
                return R.string.data_analysis_sb_desc;
            case BB_BET:
                return R.string.data_analysis_bb_desc;
        }
        return 0;
    }

    /**
     * 分析名称
     * @param column
     * @return
     */
    public static int getAnalysisColumnGood(Column column) {
        switch (column) {
            case WSD:
                return R.string.data_analysis_wsd_name;
            case WTSD:
                return R.string.data_analysis_wtsd_name;
            case WWSF:
                return R.string.data_analysis_wwsf_name;
            case AF:
                return R.string.data_analysis_af_name;
            case AFQ:
                return R.string.data_analysis_afq_name;
            case VPIP:
                return R.string.data_analysis_vpip_name;
            case PFR:
                return R.string.data_analysis_prf_name;
            case PFR_VPIP:
                return R.string.data_analysis_prfvpip_name;
            case BET:
                return R.string.data_analysis_3bet_name;
            case STL:
                return R.string.data_analysis_stl_name;
            case FOLD_STL:
                return R.string.data_analysis_fold_name;
            case SB_BET:
                return R.string.data_analysis_sb_name;
            case BB_BET:
                return R.string.data_analysis_bb_name;
        }
        return 0;
    }

    /**
     * 分析类别
     * @param column
     * @return
     */
    public static int getAnalysisColumnType(Column column) {
        switch (column) {
            case WSD:
                return 0;
            case WTSD:
                return 1;
            case WWSF:
                return 2;
            case AF:
                return 3;
            case AFQ:
                return 4;
            case VPIP:
                return 5;
            case PFR:
                return 6;
            case PFR_VPIP:
                return 7;
            case BET:
                return 8;
            case STL:
                return 9;
            case FOLD_STL:
                return 10;
            case SB_BET:
                return 11;
            case BB_BET:
                return 12;
        }
        return 0;
    }
}
