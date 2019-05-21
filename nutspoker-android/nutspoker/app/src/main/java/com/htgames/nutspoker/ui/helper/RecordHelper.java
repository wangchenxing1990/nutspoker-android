package com.htgames.nutspoker.ui.helper;

import android.widget.TextView;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.GameMemberEntity;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.util.log.LogUtil;

/**
 */
public class RecordHelper {
    public static int getRecordSngRankImage(int rank , boolean isBg) {
        int rankImageSrc = R.drawable.match_rank_common_bg;
//        if (rank == 1) {
//            rankImageSrc = R.mipmap.record_detail_sng_rank_1;
//        } else if (rank == 2) {
//            rankImageSrc = R.mipmap.record_detail_sng_rank_2;
//        } else if (rank == 3) {
//            rankImageSrc = R.mipmap.record_detail_sng_rank_3;
//        } else if (rank == 4 && !isBg) {
//            rankImageSrc = R.mipmap.record_detail_sng_rank_4;
//        }
        return rankImageSrc;
    }

    /**
     * 盈利
     * @param tv
     * @param winChips 盈利
     * @param gameMode 游戏模式
     */
    public static void setRecordGainView(TextView tv , int winChips , int gameMode) {
        LogUtil.i("zzh", "系统消息盈利和奖金：" + winChips + " gameMode: " + gameMode);// TODO: 16/12/6 delete
        if (winChips > 0) {
            tv.setText("+" + winChips);
//            tv.setTextColor(DemoCache.getContext().getResources().getColor(R.color.record_item_earnings_gain_color));
        } else if (winChips < 0) {
            tv.setText("" + winChips);
//            tv.setTextColor(DemoCache.getContext().getResources().getColor(R.color.record_item_earnings_lose_color));
        } else {
            tv.setText("" + winChips);
//            tv.setTextColor(DemoCache.getContext().getResources().getColor(R.color.record_item_earnings_gain_color));
        }
    }

    public static void setRecordGainView(TextView tv , int winChips) {
        if (winChips > 0) {
            tv.setText("+" + winChips);
            tv.setTextColor(DemoCache.getContext().getResources().getColor(R.color.record_item_earnings_gain_color));
        } else if (winChips < 0) {
            tv.setText(String.valueOf(winChips));
            tv.setTextColor(DemoCache.getContext().getResources().getColor(R.color.record_item_earnings_lose_color));
        } else {
            tv.setText("" + winChips);
            tv.setTextColor(DemoCache.getContext().getResources().getColor(R.color.record_item_earnings_gain_color));
        }
    }

    /**
     * 获取总盈利
     * @param mGameMemberEntity
     * @return
     */
    public static int getAllGain(GameMemberEntity mGameMemberEntity) {
        int totalBuy = mGameMemberEntity.totalBuy;
        int winChip = mGameMemberEntity.winChip;//盈利
        int insuranceBuy = mGameMemberEntity.premium;
        int insuranceGain = mGameMemberEntity.insurance - insuranceBuy;//保险盈利：保险赚的 - 投保
        int allGain = insuranceGain + winChip;//总盈利：盈利 + 保险盈利
        return allGain;
    }
}
