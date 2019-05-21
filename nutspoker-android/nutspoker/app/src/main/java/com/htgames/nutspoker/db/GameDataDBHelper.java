package com.htgames.nutspoker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.netease.nim.uikit.bean.CardGamesEy;
import com.netease.nim.uikit.bean.DataAnalysisEntity;
import com.netease.nim.uikit.bean.DataStatisticsEntity;
import com.netease.nim.uikit.bean.InsuranceEntity;
import com.netease.nim.uikit.bean.MatchEntity;
import com.netease.nim.uikit.bean.RecordEntity;
import com.netease.nim.uikit.db.BaseHelp;
import com.netease.nim.uikit.db.DBUtil;
import com.netease.nim.uikit.db.table.CardGamesTable;
import com.netease.nim.uikit.db.table.DataAnalysisTable;
import com.netease.nim.uikit.db.table.DataStatisticsTable;
import com.netease.nim.uikit.db.table.RecordEntityTable;

/**
 * 游戏统计数据数据库Helper
 */
public class GameDataDBHelper extends BaseHelp {
    /**
     * 获取数据分析
     * @param context
     * @return
     */
    public static DataAnalysisEntity getDataAnalysis(Context context){
        DBUtil mDBUtil = DBUtil.getInstance(context, DataAnalysisTable.TABLE_DATA_ANALYSIS);
        Cursor cursor = mDBUtil.selectData(null, null, null, null, null, null);
        DataAnalysisEntity dataAnalysisEntity = null;
        try {
            if(cursor != null && cursor.moveToNext()) {
                dataAnalysisEntity = new DataAnalysisEntity();
                dataAnalysisEntity.wsd = (cursor.getInt(cursor.getColumnIndex(DataAnalysisTable.COLUMN_DATA_WSD)));
                dataAnalysisEntity.wtsd = (cursor.getInt(cursor.getColumnIndex(DataAnalysisTable.COLUMN_DATA_WTSD)));
                dataAnalysisEntity.wwsf = (cursor.getInt(cursor.getColumnIndex(DataAnalysisTable.COLUMN_DATA_WWSF)));
                dataAnalysisEntity.af = (cursor.getInt(cursor.getColumnIndex(DataAnalysisTable.COLUMN_DATA_AF)));
                dataAnalysisEntity.afq = (cursor.getInt(cursor.getColumnIndex(DataAnalysisTable.COLUMN_DATA_AFQ)));
                dataAnalysisEntity.vpip = (cursor.getInt(cursor.getColumnIndex(DataAnalysisTable.COLUMN_DATA_VPIP)));
                dataAnalysisEntity.pfr = (cursor.getInt(cursor.getColumnIndex(DataAnalysisTable.COLUMN_DATA_PFR)));
                dataAnalysisEntity.pfr_vpip = (cursor.getInt(cursor.getColumnIndex(DataAnalysisTable.COLUMN_DATA_PFR_VPIP)));
                dataAnalysisEntity.three_bet = (cursor.getInt(cursor.getColumnIndex(DataAnalysisTable.COLUMN_DATA_THREE_BET)));
                dataAnalysisEntity.att_to_stl_lp = (cursor.getInt(cursor.getColumnIndex(DataAnalysisTable.COLUMN_DATA_ATT)));
                dataAnalysisEntity.fold_blind_to_stl = (cursor.getInt(cursor.getColumnIndex(DataAnalysisTable.COLUMN_DATA_FOLD_STL)));
                dataAnalysisEntity.sb_three_bet_steal_att = (cursor.getInt(cursor.getColumnIndex(DataAnalysisTable.COLUMN_DATA_SB)));
                dataAnalysisEntity.bb_three_bet_steal_att = (cursor.getInt(cursor.getColumnIndex(DataAnalysisTable.COLUMN_DATA_BB)));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        mDBUtil.close();
        return dataAnalysisEntity;
    }

    /**
     * 保存数据分析
     * @param context
     * @return
     */
    public static void savaDataAnalysis(Context context , DataAnalysisEntity dataAnalysisEntity){
        if(dataAnalysisEntity == null){
            return;
        }
        DBUtil mDBUtil = DBUtil.getInstance(context, DataAnalysisTable.TABLE_DATA_ANALYSIS);
        Cursor cursor = mDBUtil.selectData(null, null, null, null, null, null);
        try {
            ContentValues values = new ContentValues();
            values.put(DataAnalysisTable.COLUMN_DATA_WSD , dataAnalysisEntity.wsd);
            values.put(DataAnalysisTable.COLUMN_DATA_WTSD , dataAnalysisEntity.wtsd);
            values.put(DataAnalysisTable.COLUMN_DATA_WWSF , dataAnalysisEntity.wwsf);
            values.put(DataAnalysisTable.COLUMN_DATA_AF , dataAnalysisEntity.af);
            values.put(DataAnalysisTable.COLUMN_DATA_AFQ , dataAnalysisEntity.afq);
            values.put(DataAnalysisTable.COLUMN_DATA_VPIP , dataAnalysisEntity.vpip);
            values.put(DataAnalysisTable.COLUMN_DATA_PFR , dataAnalysisEntity.pfr);
            values.put(DataAnalysisTable.COLUMN_DATA_PFR_VPIP , dataAnalysisEntity.pfr_vpip);
            values.put(DataAnalysisTable.COLUMN_DATA_THREE_BET , dataAnalysisEntity.three_bet);
            values.put(DataAnalysisTable.COLUMN_DATA_ATT, dataAnalysisEntity.att_to_stl_lp);
            values.put(DataAnalysisTable.COLUMN_DATA_FOLD_STL, dataAnalysisEntity.fold_blind_to_stl);
            values.put(DataAnalysisTable.COLUMN_DATA_SB, dataAnalysisEntity.sb_three_bet_steal_att);
            values.put(DataAnalysisTable.COLUMN_DATA_BB, dataAnalysisEntity.bb_three_bet_steal_att);
            if(cursor != null){
                if(cursor.getCount() == 0){
                    mDBUtil.insertData(values);
                }else{
                    mDBUtil.updateData(values , null , null);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
        }
        mDBUtil.close();
    }

    /**
     * 获取数据统计
     * @param context
     * @return
     */
    public static DataStatisticsEntity getDataStatistics(Context context,int type){
        DBUtil mDBUtil = DBUtil.getInstance(context, DataStatisticsTable.TABLE_DATA_STATISTICS);
        String selection = DataStatisticsTable.COLUMN_DATA_TYPE + "= ?";
        String[] selectionArgs = new String[]{ String.valueOf(type) };
        Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, null);
        DataStatisticsEntity data = null;
        try {
            if(cursor != null && cursor.moveToNext()) {
                data = new DataStatisticsEntity();
                data.games = (cursor.getInt(cursor.getColumnIndex(DataStatisticsTable.COLUMN_DATA_GAMES)));
                data.hands = (cursor.getInt(cursor.getColumnIndex(DataStatisticsTable.COLUMN_DATA_HANDS)));
                data.hands_count_won = (cursor.getInt(cursor.getColumnIndex(DataStatisticsTable.COLUMN_DATA_HANDS_COUNT_WON)));
                data.wsd = (cursor.getInt(cursor.getColumnIndex(DataStatisticsTable.COLUMN_DATA_WSD)));
                data.wwsf = (cursor.getInt(cursor.getColumnIndex(DataStatisticsTable.COLUMN_DATA_WWSF)));
                data.wsd_after_river = (cursor.getInt(cursor.getColumnIndex(DataStatisticsTable.COLUMN_DATA_WSD_AFTER)));
                data.my_c_won = (cursor.getInt(cursor.getColumnIndex(DataStatisticsTable.COLUMN_DATA_MY_C_WON)));
                data.hundred_hands_win = (cursor.getInt(cursor.getColumnIndex(DataStatisticsTable.COLUMN_DATA_HUNDRED_HANDS_WIN)));
                data.big_blind_won_cnt = (cursor.getInt(cursor.getColumnIndex(DataStatisticsTable.COLUMN_DATA_BIG_BLIND_WON)));
                data.allin_chips_avg = (cursor.getInt(cursor.getColumnIndex(DataStatisticsTable.COLUMN_DATA_ALLIN_CHIPS)));

                data.type = cursor.getInt(cursor.getColumnIndex(DataStatisticsTable.COLUMN_DATA_TYPE));
//                data.setVpip(cursor.getInt(cursor.getColumnIndex(DataStatisticsTable.COLUMN_DATA_VPIP)));
//                data.setWaptmp(cursor.getInt(cursor.getColumnIndex(DataStatisticsTable.COLUMN_DATA_WAPTMP)));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        mDBUtil.close();
        return data;
    }

    /**
     * 保存数据统计
     * @param context
     * @return
     */
    public static void saveDataStatistics(Context context , DataStatisticsEntity data){
        if(data == null){
            return;
        }
        DBUtil mDBUtil = DBUtil.getInstance(context, DataStatisticsTable.TABLE_DATA_STATISTICS);
        String selection = DataStatisticsTable.COLUMN_DATA_TYPE + "= ?";
        String[] selectionArgs = new String[]{ String.valueOf(data.type) };
        Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, null);
        try {
            ContentValues values = new ContentValues();
            values.put(DataStatisticsTable.COLUMN_DATA_GAMES , data.games);
            values.put(DataStatisticsTable.COLUMN_DATA_HANDS , data.hands);
            values.put(DataStatisticsTable.COLUMN_DATA_HANDS_COUNT_WON , data.hands_count_won);
            values.put(DataStatisticsTable.COLUMN_DATA_WSD , data.wsd);
            values.put(DataStatisticsTable.COLUMN_DATA_WWSF , data.wwsf);
            values.put(DataStatisticsTable.COLUMN_DATA_WSD_AFTER , data.wsd_after_river);
            values.put(DataStatisticsTable.COLUMN_DATA_MY_C_WON, data.my_c_won);
            values.put(DataStatisticsTable.COLUMN_DATA_HUNDRED_HANDS_WIN, data.hundred_hands_win);
            values.put(DataStatisticsTable.COLUMN_DATA_BIG_BLIND_WON, data.big_blind_won_cnt);
            values.put(DataStatisticsTable.COLUMN_DATA_ALLIN_CHIPS, data.allin_chips_avg);

            values.put(DataStatisticsTable.COLUMN_DATA_TYPE,data.type);

//            values.put(DataStatisticsTable.COLUMN_DATA_VPIP, data.getVpip());
//            values.put(DataStatisticsTable.COLUMN_DATA_WAPTMP, data.getWaptmp());
            if(cursor != null){
                if(cursor.getCount() == 0){
                    mDBUtil.insertData(values);
                }else{
                    mDBUtil.updateData(values , null , null);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        mDBUtil.close();
    }

    //战绩首页数据缓存
    /**
     * 获取数据统计
     * @param context
     * @return
     */
    public static RecordEntity getRecordEntity(Context context){
        DBUtil mDBUtil = DBUtil.getInstance(context, RecordEntityTable.TABLE_NAME);
        Cursor cursor = mDBUtil.selectData(null, null, null, null, null, null);
        RecordEntity data = null;
        try {
            if(cursor != null && cursor.moveToNext()) {
                data = new RecordEntity();
                data.vpip = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.vpip)));
                data.waptmp = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.waptmp)));
                data.hands = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.hands)));
                data.my_c_won = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.my_c_won)));
                data.my_c_won_sng_mtt = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.my_c_won_sng_mtt)));
                data.my_c_won_sng = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.my_c_won_sng)));
                data.my_c_won_mtt = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.my_c_won_mtt)));
                data.games = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.games)));
                data.games_sng_mtt = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.games_sng_mtt)));
                data.games_sng = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.games_sng)));
                data.games_mtt = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.games_mtt)));
                data.hands_won_cnt = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.hands_won_cnt)));
                data.hands_count_won = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.hands_count_won)));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        mDBUtil.close();
        return data;
    }

    /**
     * 保存数据统计
     * @param context
     * @return
     */
    public static void setRecordEntity(Context context , RecordEntity data){
        if(data == null){
            return;
        }
        DBUtil mDBUtil = DBUtil.getInstance(context, RecordEntityTable.TABLE_NAME);
        Cursor cursor = mDBUtil.selectData(null, null, null, null, null, null);
        try {
            ContentValues values = new ContentValues();
            values.put(RecordEntityTable.vpip , data.vpip);
            values.put(RecordEntityTable.waptmp , data.waptmp);
            values.put(RecordEntityTable.hands , data.hands);
            values.put(RecordEntityTable.my_c_won , data.my_c_won);
            values.put(RecordEntityTable.my_c_won_sng_mtt , data.my_c_won_sng_mtt);
            values.put(RecordEntityTable.my_c_won_sng , data.my_c_won_sng);
            values.put(RecordEntityTable.my_c_won_mtt, data.my_c_won_mtt);
            values.put(RecordEntityTable.games, data.games);
            values.put(RecordEntityTable.games_sng_mtt, data.games_sng_mtt);
            values.put(RecordEntityTable.games_sng, data.games_sng);
            values.put(RecordEntityTable.games_mtt, data.games_mtt);
            values.put(RecordEntityTable.hands_won_cnt, data.hands_won_cnt);
            values.put(RecordEntityTable.hands_count_won, data.hands_count_won);
            if(cursor != null){
                if(cursor.getCount() == 0){
                    mDBUtil.insertData(values);
                }else{
                    mDBUtil.updateData(values , null , null);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        mDBUtil.close();
    }

    public static InsuranceEntity getInsurance(Context context){
        DBUtil mDBUtil = DBUtil.getInstance(context, RecordEntityTable.TABLE_NAME);
        Cursor cursor = mDBUtil.selectData(null, null, null, null, null, null);
        InsuranceEntity data = null;
        try {
            if(cursor.moveToFirst()) {
                data = new InsuranceEntity();
                data.trigger_count = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.trigger_count)));
                data.hit_count = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.hit_count)));
                data.buy_count = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.buy_count)));
                data.buy_sum = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.buy_sum)));
                data.pay_sum = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.pay_sum)));
                data.my_games = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.my_games)));
                data.my_buy_sum = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.my_buy_sum)));
                data.my_pay_sum = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.my_pay_sum)));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        mDBUtil.close();
        return data;
    }
    public static void setInsurance(Context context,InsuranceEntity data){
        if(data == null){
            return;
        }
        DBUtil mDBUtil = DBUtil.getInstance(context, RecordEntityTable.TABLE_NAME);
        Cursor cursor = mDBUtil.selectData(null, null, null, null, null, null);
        try {
            ContentValues values = new ContentValues();
            values.put(RecordEntityTable.trigger_count , data.trigger_count);
            values.put(RecordEntityTable.hit_count , data.hit_count);
            values.put(RecordEntityTable.buy_count , data.buy_count);
            values.put(RecordEntityTable.buy_sum , data.buy_sum);
            values.put(RecordEntityTable.pay_sum , data.pay_sum);
            values.put(RecordEntityTable.my_games , data.my_games);
            values.put(RecordEntityTable.my_buy_sum, data.my_buy_sum);
            values.put(RecordEntityTable.my_pay_sum, data.my_pay_sum);
            if(cursor != null){
                if(cursor.getCount() == 0){
                    mDBUtil.insertData(values);
                }else{
                    mDBUtil.updateData(values , null , null);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        mDBUtil.close();
    }

    public static MatchEntity getMatchData(Context context){
        DBUtil mDBUtil = DBUtil.getInstance(context, RecordEntityTable.TABLE_NAME);
        Cursor cursor = mDBUtil.selectData(null, null, null, null, null, null);
        MatchEntity data = null;
        try {
            if(cursor.moveToFirst()) {
                data = new MatchEntity();

                data.games_sng_mtt = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.games_sng_mtt)));
                data.games_sng = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.games_sng)));
                data.games_mtt = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.games_mtt)));

                data.match_fee_mtt = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.match_fee_mtt)));
                data.match_fee_sng = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.match_fee_sng)));
                data.match_fee_sng_mtt = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.match_fee_sng_mtt)));
                data.reward_mtt = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.reward_mtt)));
                data.reward_sng = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.reward_sng)));
                data.reward_sng_mtt = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.reward_sng_mtt)));
                data.in_reward_mtt = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.in_reward_mtt)));
                data.in_reward_sng = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.in_reward_sng)));
                data.in_reward_sng_mtt = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.in_reward_sng_mtt)));
                data.in_finals_mtt = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.in_finals_mtt)));
                data.in_finals_sng = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.in_finals_sng)));
                data.in_finals_sng_mtt = (cursor.getInt(cursor.getColumnIndex(RecordEntityTable.in_finals_sng_mtt)));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        mDBUtil.close();
        return data;
    }
    public static void setMatchData(Context context,MatchEntity data){
        if(data == null){
            return;
        }
        DBUtil mDBUtil = DBUtil.getInstance(context, RecordEntityTable.TABLE_NAME);
        Cursor cursor = mDBUtil.selectData(null, null, null, null, null, null);
        try {
            ContentValues values = new ContentValues();
            values.put(RecordEntityTable.games_sng_mtt, data.games_sng_mtt);
            values.put(RecordEntityTable.games_sng, data.games_sng);
            values.put(RecordEntityTable.games_mtt, data.games_mtt);

            values.put(RecordEntityTable.match_fee_mtt , data.match_fee_mtt);
            values.put(RecordEntityTable.match_fee_sng , data.match_fee_sng);
            values.put(RecordEntityTable.match_fee_sng_mtt , data.match_fee_sng_mtt);
            values.put(RecordEntityTable.reward_mtt , data.reward_mtt);
            values.put(RecordEntityTable.reward_sng , data.reward_sng);
            values.put(RecordEntityTable.reward_sng_mtt , data.reward_sng_mtt);
            values.put(RecordEntityTable.in_reward_mtt, data.in_reward_mtt);
            values.put(RecordEntityTable.in_reward_sng, data.in_reward_sng);
            values.put(RecordEntityTable.in_reward_sng_mtt , data.in_reward_sng_mtt);
            values.put(RecordEntityTable.in_finals_mtt , data.in_finals_mtt);
            values.put(RecordEntityTable.in_finals_sng, data.in_finals_sng);
            values.put(RecordEntityTable.in_finals_sng_mtt, data.in_finals_sng_mtt);
            if(cursor != null){
                if(cursor.getCount() == 0){
                    mDBUtil.insertData(values);
                }else{
                    mDBUtil.updateData(values , null , null);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        mDBUtil.close();
    }

    public static CardGamesEy getGameInfoData(Context context,String gid){
        DBUtil mDBUtil = DBUtil.getInstance(context, CardGamesTable.TABLE_NAME);

        String selection = CardGamesTable.gid +" = ?";
        String[] selectionArgs = { gid };
        Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, null);
        CardGamesEy data = null;
        try {
            if(cursor.moveToFirst()) {
                data = new CardGamesEy();

                data.gid = (cursor.getString(cursor.getColumnIndex(CardGamesTable.gid)));
                data.tid = (cursor.getString(cursor.getColumnIndex(CardGamesTable.tid)));
                data.name = (cursor.getString(cursor.getColumnIndex(CardGamesTable.name)));

                data.code = (cursor.getString(cursor.getColumnIndex(CardGamesTable.code)));
                data.blinds = (cursor.getInt(cursor.getColumnIndex(CardGamesTable.blinds)));
                data.sblinds = (cursor.getInt(cursor.getColumnIndex(CardGamesTable.sblinds)));
                data.duration = (cursor.getInt(cursor.getColumnIndex(CardGamesTable.duration)));
                data.owner = (cursor.getString(cursor.getColumnIndex(CardGamesTable.owner)));
                data.type = (cursor.getInt(cursor.getColumnIndex(CardGamesTable.type)));
                data.public_mode = (cursor.getInt(cursor.getColumnIndex(CardGamesTable.public_mode)));
                data.ante_mode = (cursor.getInt(cursor.getColumnIndex(CardGamesTable.ante_mode)));
                data.tilt_mode = (cursor.getInt(cursor.getColumnIndex(CardGamesTable.tilt_mode)));
                data.status = (cursor.getInt(cursor.getColumnIndex(CardGamesTable.status)));
                data.create_time = (cursor.getInt(cursor.getColumnIndex(CardGamesTable.create_time)));
                data.total_player = (cursor.getInt(cursor.getColumnIndex(CardGamesTable.total_player)));
                data.server = (cursor.getString(cursor.getColumnIndex(CardGamesTable.server)));

                data.game_mode = (cursor.getInt(cursor.getColumnIndex(CardGamesTable.game_mode)));
                data.match_chips = (cursor.getInt(cursor.getColumnIndex(CardGamesTable.match_chips)));
                data.match_player = (cursor.getInt(cursor.getColumnIndex(CardGamesTable.match_player)));
                data.match_duration = (cursor.getInt(cursor.getColumnIndex(CardGamesTable.match_duration)));
                data.end_time = (cursor.getInt(cursor.getColumnIndex(CardGamesTable.end_time)));
                data.match_checkin_fee = (cursor.getInt(cursor.getColumnIndex(CardGamesTable.match_checkin_fee)));
                data.room_id = (cursor.getString(cursor.getColumnIndex(CardGamesTable.room_id)));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        mDBUtil.close();
        return data;
    }
    public static void setGameInfoData(Context context, CardGamesEy data){
        if(data == null){
            return;
        }
        DBUtil mDBUtil = DBUtil.getInstance(context, CardGamesTable.TABLE_NAME);
        Cursor cursor = mDBUtil.selectData(null, null, null, null, null, null);
        try {
            ContentValues values = new ContentValues();
            values.put(CardGamesTable.gid, data.gid);
            values.put(CardGamesTable.tid, data.tid);
            values.put(CardGamesTable.name, data.name);

            values.put(CardGamesTable.code , data.code);
            values.put(CardGamesTable.blinds , data.blinds);
            values.put(CardGamesTable.sblinds , data.sblinds);
            values.put(CardGamesTable.duration , data.duration);
            values.put(CardGamesTable.owner , data.owner);
            values.put(CardGamesTable.type , data.type);
            values.put(CardGamesTable.public_mode , data.public_mode);
            values.put(CardGamesTable.ante_mode, data.ante_mode);
            values.put(CardGamesTable.tilt_mode, data.tilt_mode);
            values.put(CardGamesTable.status , data.status);
            values.put(CardGamesTable.create_time , data.create_time);
            values.put(CardGamesTable.total_player, data.total_player);
            values.put(CardGamesTable.server, data.server);

            values.put(CardGamesTable.game_mode , data.game_mode);
            values.put(CardGamesTable.match_chips, data.match_chips);
            values.put(CardGamesTable.match_player, data.match_player);
            values.put(CardGamesTable.match_duration , data.match_duration);
            values.put(CardGamesTable.end_time , data.end_time);
            values.put(CardGamesTable.match_checkin_fee, data.match_checkin_fee);
            values.put(CardGamesTable.room_id, data.room_id);
            if(cursor != null){
                if(cursor.getCount() == 0){
                    mDBUtil.insertData(values);
                }else{
                    mDBUtil.updateData(values , null , null);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        mDBUtil.close();
    }
}
