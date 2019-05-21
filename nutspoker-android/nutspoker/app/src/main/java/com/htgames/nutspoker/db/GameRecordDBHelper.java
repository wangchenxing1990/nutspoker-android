package com.htgames.nutspoker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.netease.nim.uikit.bean.CardTypeEy;
import com.netease.nim.uikit.bean.GameMtSngConfig;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.bean.NetCardRecordEy;
import com.netease.nim.uikit.bean.PineappleConfig;
import com.netease.nim.uikit.bean.PineappleConfigMtt;
import com.netease.nim.uikit.db.BaseHelp;
import com.netease.nim.uikit.db.DBUtil;
import com.netease.nim.uikit.db.table.NetCardRecordTable;
import com.netease.nim.uikit.common.gson.GsonUtils;
import com.netease.nim.uikit.common.util.log.LogUtil;

import com.netease.nim.uikit.bean.GameBillEntity;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMemberEntity;
import com.netease.nim.uikit.bean.GameNormalConfig;
import com.netease.nim.uikit.bean.GameSngConfigEntity;
import com.netease.nim.uikit.bean.UserEntity;
import com.htgames.nutspoker.chat.session.extension.BillAttachment;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.db.table.GameRecordTable;
import com.htgames.nutspoker.tool.json.RecordJsonTools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 牌局信息数据库Helper
 */
public class GameRecordDBHelper extends BaseHelp {
    private final static String TAG = "GameRecordDBHelper";

    /**
     * 保存牌局信息
     * @param context
     * @param recordList
     */
    public static void saveGameRecordList(Context context , ArrayList<GameBillEntity> recordList) {
        synchronized(BaseHelp.sHelp) {
            if(recordList == null || recordList.size() == 0){
                return;
            }
            DBUtil mDBUtil = DBUtil.getInstance(context, GameRecordTable.TABLE_GAME_RECORD);
            Cursor cursor = null;
            try {
                for(GameBillEntity gameBillEntity : recordList) {
                    String selection = GameRecordTable.COLUMN_GAME_GID + "= ?";
                    String[] selectionArgs = new String[]{gameBillEntity.gameInfo.gid};
//                Log.d("record", gameBillEntity.getGameInfo().getGid());
                    cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, null);
                    ContentValues values = new ContentValues();
                    values.put(GameRecordTable.COLUMN_GAME_NAME, gameBillEntity.gameInfo.name);
                    values.put(GameRecordTable.COLUMN_GAME_PLAY_MODE, gameBillEntity.gameInfo.play_mode);
                    values.put(GameRecordTable.COLUMN_GAME_TEADID, gameBillEntity.gameInfo.tid);
                    values.put(GameRecordTable.COLUMN_GAME_GID, gameBillEntity.gameInfo.gid);
                    values.put(GameRecordTable.COLUMN_GAME_CODE, gameBillEntity.gameInfo.code);
                    values.put(GameRecordTable.COLUMN_GAME_TYPE, gameBillEntity.gameInfo.type);
                    values.put(GameRecordTable.COLUMN_GAME_STATUS, gameBillEntity.gameInfo.status);
                    values.put(GameRecordTable.COLUMN_GAME_MODE_PUBLIC, gameBillEntity.gameInfo.publicMode);
                    values.put(GameRecordTable.COLUMN_MATCH_TYPE, gameBillEntity.gameInfo.match_type);
                    long createTime = gameBillEntity.gameInfo.createTime;
                    values.put(GameRecordTable.COLUMN_GAME_CREATE_TIME, createTime);
                    values.put(GameRecordTable.COLUMN_GAME_CREATOR_ID, gameBillEntity.gameInfo.creatorInfo.account);
                    long endTime = gameBillEntity.gameInfo.endTime;
                    if (endTime == 0) {
                        endTime = createTime;
                    }
                    values.put(GameRecordTable.COLUMN_GAME_END_TIME, endTime);
                    values.put(GameRecordTable.COLUMN_GAME_TOTAL_PLAYER, gameBillEntity.totalPlayer);//总人数
                    GameEntity gameInfo = gameBillEntity.gameInfo;
                    values.put(GameRecordTable.COLUMN_GAME_MODE, gameInfo.gameMode);
                    if (gameBillEntity.gameInfo.play_mode < GameConstants.PLAY_MODE_PINEAPPLE) {
                        if (gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL && gameInfo.gameConfig instanceof GameNormalConfig) {
                            //普通模式
                            GameNormalConfig gameConfig = (GameNormalConfig) gameInfo.gameConfig;
                            values.put(GameRecordTable.COLUMN_GAME_SMALL_BLINDS, gameConfig.blindType);
                            values.put(GameRecordTable.COLUMN_GAME_DURATIONS, gameConfig.timeType);
                            values.put(GameRecordTable.COLUMN_GAME_MODE_ANTE, gameConfig.anteMode);
                            values.put(GameRecordTable.COLUMN_GAME_ANTE, gameConfig.ante);
                            values.put(GameRecordTable.COLUMN_GAME_MODE_TILT, gameConfig.tiltMode);
                            String gameConfigStr = GsonUtils.getGson().toJson(gameConfig);
                            values.put(GameRecordTable.COLUMN_GAME_CONFIG, gameConfigStr);
                        } else if (gameInfo.gameMode == GameConstants.GAME_MODE_SNG && gameInfo.gameConfig instanceof GameSngConfigEntity) {
                            //SNG模式
                            GameSngConfigEntity gameConfig = (GameSngConfigEntity) gameInfo.gameConfig;
                            values.put(GameRecordTable.COLUMN_GAME_MATCH_CHIPS, gameConfig.chips);
                            values.put(GameRecordTable.COLUMN_GAME_MATCH_PLAYER, gameConfig.player);
                            values.put(GameRecordTable.COLUMN_GAME_MATCH_DURATION, gameConfig.duration);
                            values.put(GameRecordTable.COLUMN_GAME_MATCH_CHECKIN_FEE, gameConfig.checkInFee);
                            String gameConfigStr = GsonUtils.getGson().toJson(gameConfig);
                            values.put(GameRecordTable.COLUMN_GAME_CONFIG, gameConfigStr);
                        } else if (gameInfo.gameMode == GameConstants.GAME_MODE_MTT && gameInfo.gameConfig instanceof GameMttConfig) {
                            //MTT模式
                            GameMttConfig gameConfig = (GameMttConfig) gameInfo.gameConfig;
                            values.put(GameRecordTable.COLUMN_GAME_MATCH_CHIPS, gameConfig.matchChips);
                            values.put(GameRecordTable.COLUMN_GAME_MATCH_PLAYER, gameConfig.matchPlayer);
                            values.put(GameRecordTable.COLUMN_GAME_MATCH_DURATION, gameConfig.matchDuration);
                            values.put(GameRecordTable.COLUMN_GAME_MATCH_CHECKIN_FEE, gameConfig.matchCheckinFee);
                            values.put(GameRecordTable.COLUMN_GAME_KO_MODE, gameConfig.ko_mode);
                            values.put(GameRecordTable.COLUMN_GAME_KO_REWARD_RATE, gameConfig.ko_reward_rate);
                            values.put(GameRecordTable.COLUMN_GAME_KO_HEAD_RATE, gameConfig.ko_head_rate);
                            String gameConfigStr = GsonUtils.getGson().toJson(gameConfig);
                            values.put(GameRecordTable.COLUMN_GAME_CONFIG, gameConfigStr);
                        } else if (gameInfo.gameMode == GameConstants.GAME_MODE_MT_SNG && gameInfo.gameConfig instanceof GameMtSngConfig) {
                            //MT-SNG模式
                            GameMtSngConfig gameConfig = (GameMtSngConfig) gameInfo.gameConfig;
                            values.put(GameRecordTable.COLUMN_GAME_MATCH_CHIPS, gameConfig.matchChips);
                            values.put(GameRecordTable.COLUMN_GAME_MATCH_PLAYER, gameConfig.matchPlayer);
                            values.put(GameRecordTable.COLUMN_GAME_MATCH_DURATION, gameConfig.matchDuration);
                            values.put(GameRecordTable.COLUMN_GAME_MATCH_CHECKIN_FEE, gameConfig.matchCheckinFee);
                            values.put(GameRecordTable.COLUMN_GAME_TOTAL_PLAYER, gameConfig.totalPlayer);
                            String gameConfigStr = GsonUtils.getGson().toJson(gameConfig);
                            values.put(GameRecordTable.COLUMN_GAME_CONFIG, gameConfigStr);
                        }
                    } else if (gameBillEntity.gameInfo.play_mode == GameConstants.PLAY_MODE_PINEAPPLE) {
                        if (gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL) {
                            PineappleConfig gameConfig = (PineappleConfig) gameInfo.gameConfig;
                            String gameConfigStr = GsonUtils.getGson().toJson(gameConfig);
                            values.put(GameRecordTable.COLUMN_GAME_CONFIG, gameConfigStr);
                        } else if (gameInfo.gameMode == GameConstants.GAME_MODE_MTT) {
                            PineappleConfigMtt gameConfig = (PineappleConfigMtt) gameInfo.gameConfig;
                            String gameConfigStr = GsonUtils.getGson().toJson(gameConfig);
                            values.put(GameRecordTable.COLUMN_GAME_CONFIG, gameConfigStr);
                        }
                    }
                    values.put(GameRecordTable.COLUMN_GAME_MAX_POT, gameBillEntity.maxPot);
                    values.put(GameRecordTable.COLUMN_GAME_ALL_BUYS, gameBillEntity.allBuys);
                    values.put(GameRecordTable.COLUMN_GAME_BOUNTS, gameBillEntity.bouts);
                    values.put(GameRecordTable.COLUMN_GAME_WIN_CHIPS, gameBillEntity.winChip);
                    values.put(GameRecordTable.COLUMN_GAME_MVP, gameBillEntity.mvp);
                    values.put(GameRecordTable.COLUMN_GAME_FISH, gameBillEntity.fish);
                    values.put(GameRecordTable.COLUMN_GAME_RICHEST, gameBillEntity.richest);
                    values.put(GameRecordTable.COLUMN_GAME_MEMBERS, gameBillEntity.jsonMemberStr);
                    values.put(GameRecordTable.COLUMN_IS_PARTICIPATION, true);
                    values.put(GameRecordTable.COLUMN_GAME_ALL_REWARD, gameBillEntity.allReward);
                    values.put(GameRecordTable.COLUMN_GAME_TOTAL_TIME, gameBillEntity.totalTime);
                    values.put(GameRecordTable.COLUMN_GAME_MY_UID, gameBillEntity.myUid);
                    values.put(GameRecordTable.COLUMN_GAME_SBLINDS_INDEX, gameBillEntity.endSblindsIndex);
                    if (cursor != null) {
                        if (cursor.getCount() == 0) {
                            mDBUtil.insertData(values);
                        } else {
                            mDBUtil.updateData(values, selection, selectionArgs);
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
            mDBUtil.close();
        }

    }

    /**
     * 获取牌局信息记录
     * @param context
     * @return
     */
    public static ArrayList<GameBillEntity> getGameRecordList(Context context) {
        synchronized(BaseHelp.sHelp) {
            DBUtil mDBUtil = DBUtil.getInstance(context, GameRecordTable.TABLE_GAME_RECORD);
            String orderBy = GameRecordTable.COLUMN_GAME_END_TIME + " DESC limit " + GameConstants.MAX_SQL_PAGE_SIZE;
            Cursor cursor = mDBUtil.selectData(null, null, null, null, null, orderBy);
            ArrayList<GameBillEntity> gameRecordList = new ArrayList<GameBillEntity>();
            if (cursor != null) {
                GameBillEntity gameBillEntity = null;
                while (cursor.moveToNext()) {
                    gameBillEntity = getGameRecordItem(cursor);
                    gameRecordList.add(gameBillEntity);
                }
                cursor.close();
            }
            mDBUtil.close();
            return gameRecordList;
        }
    }

    public static ArrayList<GameBillEntity> getGameRecordListByTid(Context context, String tid) {
        synchronized(BaseHelp.sHelp) {
            DBUtil mDBUtil = DBUtil.getInstance(context, GameRecordTable.TABLE_GAME_RECORD);
            String selection = GameRecordTable.COLUMN_GAME_TEADID + "= ?";
            String[] selectionArgs = new String[]{tid};
            String orderBy = GameRecordTable.COLUMN_GAME_END_TIME + " DESC limit " + GameConstants.MAX_SQL_PAGE_SIZE;
            Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, orderBy);
            ArrayList<GameBillEntity> gameRecordList = new ArrayList<GameBillEntity>();
            if (cursor != null) {
                GameBillEntity gameBillEntity = null;
                while (cursor.moveToNext()) {
                    gameBillEntity = getGameRecordItem(cursor);
                    gameRecordList.add(gameBillEntity);
                }
                cursor.close();
            }
            mDBUtil.close();
            return gameRecordList;
        }
    }

    public static ArrayList<GameBillEntity> getGameRecordList(Context context , String lastGid , long endTime) {
        synchronized(BaseHelp.sHelp) {
            DBUtil mDBUtil = DBUtil.getInstance(context, GameRecordTable.TABLE_GAME_RECORD);
            String selection = GameRecordTable.COLUMN_GAME_END_TIME + "< ?";
            String[] selectionArgs = new String[]{String.valueOf(endTime)};
            String orderBy = GameRecordTable.COLUMN_GAME_END_TIME + " DESC limit " + GameConstants.MAX_SQL_PAGE_SIZE;
            Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, orderBy);
            ArrayList<GameBillEntity> gameRecordList = new ArrayList<>();
            LogUtil.i(TAG, "endTime:" + endTime);
            if (cursor != null) {
                GameBillEntity gameBillEntity = null;
                while (cursor.moveToNext()) {
//                if (cursor.getPosition() < LOAD_COUNT) {
                    gameBillEntity = getGameRecordItem(cursor);
                    LogUtil.i(TAG, "gid:" + gameBillEntity.gameInfo.gid + ";endTime:" + gameBillEntity.gameInfo.endTime + ";position:" + cursor.getPosition());
                    gameRecordList.add(gameBillEntity);
//                } else {
//                    break;
//                }
                }
                cursor.close();
            }
            mDBUtil.close();
            return gameRecordList;
        }
    }

    public static ArrayList<GameBillEntity> getGameRecordListByTid(Context context, String lastGid, long endTime, String tid) {
        synchronized(BaseHelp.sHelp) {
            DBUtil mDBUtil = DBUtil.getInstance(context, GameRecordTable.TABLE_GAME_RECORD);
            String selection = GameRecordTable.COLUMN_GAME_END_TIME + "< ? and " + GameRecordTable.COLUMN_GAME_TEADID + "= ?";
            String[] selectionArgs = new String[]{String.valueOf(endTime), tid};
            String orderBy = GameRecordTable.COLUMN_GAME_END_TIME + " DESC limit " + GameConstants.MAX_SQL_PAGE_SIZE;
            Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, orderBy);
            ArrayList<GameBillEntity> gameRecordList = new ArrayList<>();
            LogUtil.i(TAG, "endTime:" + endTime);
            if (cursor != null) {
                GameBillEntity gameBillEntity = null;
                while (cursor.moveToNext()) {
//                if (cursor.getPosition() < LOAD_COUNT) {
                    gameBillEntity = getGameRecordItem(cursor);
                    LogUtil.i(TAG, "gid:" + gameBillEntity.gameInfo.gid + ";endTime:" + gameBillEntity.gameInfo.endTime + ";position:" + cursor.getPosition());
                    gameRecordList.add(gameBillEntity);
//                } else {
//                    break;
//                }
                }
                cursor.close();
            }
            mDBUtil.close();
            return gameRecordList;
        }
    }

    /**
     * 本地是否有指定的GID数据
     * @param context
     * @return
     */
    public static boolean isLocalRecord(Context context , String lastGid) {
        synchronized(BaseHelp.sHelp) {
            DBUtil mDBUtil = DBUtil.getInstance(context, GameRecordTable.TABLE_GAME_RECORD);
            String selection = GameRecordTable.COLUMN_GAME_GID + "= ?";
            String[] selectionArgs = new String[]{lastGid};
            Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, null);
            boolean haveMore = false;
            if (cursor != null) {
                if (cursor.getCount() != 0) {
                    haveMore = true;
                }
                cursor.close();
            }
            mDBUtil.close();
            return haveMore;
        }

    }

    /**
     * 本地是否有更多数据
     * @param context
     * @return
     */
    public static boolean haveMoreRecord(Context context , long endTime) {
        synchronized(BaseHelp.sHelp) {
            DBUtil mDBUtil = DBUtil.getInstance(context, GameRecordTable.TABLE_GAME_RECORD);
            String selection = GameRecordTable.COLUMN_GAME_END_TIME + "< ?";
            String[] selectionArgs = new String[]{String.valueOf(endTime)};
            String orderBy = GameRecordTable.COLUMN_GAME_END_TIME + " DESC";
            Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, orderBy);
            boolean haveMore = false;
            if (cursor != null) {
                if (cursor.getCount() != 0) {
                    haveMore = true;
                }
                cursor.close();
            }
            mDBUtil.close();
            return haveMore;
        }
    }

    public static boolean haveMoreRecordByTid(Context context, long endTime, String tid) {
        synchronized(BaseHelp.sHelp) {
            DBUtil mDBUtil = DBUtil.getInstance(context, GameRecordTable.TABLE_GAME_RECORD);
            String selection = GameRecordTable.COLUMN_GAME_END_TIME + "< ? and " + GameRecordTable.COLUMN_GAME_TEADID + "= ?";
            String[] selectionArgs = new String[]{String.valueOf(endTime), tid};
            String orderBy = GameRecordTable.COLUMN_GAME_END_TIME + " DESC";
            Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, orderBy);
            boolean haveMore = false;
            if (cursor != null) {
                if (cursor.getCount() != 0) {
                    haveMore = true;
                }
                cursor.close();
            }
            mDBUtil.close();
            return haveMore;
        }
    }

    public static void saveGameRecord(DBUtil dbUtil , GameBillEntity gameBillEntity){

    }

    /**
     * 获取指定的牌局信息
     * @param context
     * @param gid
     * @return
     */
    public static GameBillEntity getGameRecordByGid(Context context ,String gid) {
        synchronized(BaseHelp.sHelp) {
            DBUtil mDBUtil = DBUtil.getInstance(context, GameRecordTable.TABLE_GAME_RECORD);
            String selection = GameRecordTable.COLUMN_GAME_GID + "= ?";
            String[] selectionArgs = new String[]{gid};
            Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, null);
            GameBillEntity gameBillEntity = null;
            if (cursor != null) {
                if (cursor.getCount() != 0 && cursor.moveToNext()) {
                    gameBillEntity = getGameRecordItem(cursor);
                }
                cursor.close();
            }
            mDBUtil.close();
            return gameBillEntity;
        }

    }

    /**
     * 获取数据库中单条GameRecord
     * @param cursor
     * @return
     */
    private static GameBillEntity getGameRecordItem(Cursor cursor) {
        synchronized(BaseHelp.sHelp) {
            GameBillEntity gameBillEntity = new GameBillEntity();
            GameEntity gameInfo = new GameEntity();
            //牌局信息
            String gid = cursor.getString(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_GID));
            gameInfo.gid = (gid);
            gameInfo.tid = (cursor.getString(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_TEADID)));
            gameInfo.name = (cursor.getString(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_NAME)));
            //
            UserEntity creatorInfo = new UserEntity();
            creatorInfo.account = (cursor.getString(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_CREATOR_ID)));
            //头像和昵称自己去成员列表中取
            gameInfo.creatorInfo  =(creatorInfo);
            //
            gameInfo.publicMode = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_MODE_PUBLIC)));
            gameInfo.type = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_TYPE)));
            gameInfo.status = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_STATUS)));
            long createTime = cursor.getLong(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_CREATE_TIME));
            gameInfo.createTime = (createTime);
            //结束时间
            long endTime = cursor.getLong(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_END_TIME));
            if(endTime == 0) {
                endTime = createTime;
            }
            gameInfo.endTime = (endTime);
            //普通模式和SNG
            int gameMode = cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_MODE));
            gameInfo.play_mode = cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_PLAY_MODE));
            gameInfo.gameMode = (gameMode);
            gameInfo.match_type = cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_MATCH_TYPE));
            String gameConfigStr = cursor.getString(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_CONFIG));
            if (gameInfo.play_mode < GameConstants.PLAY_MODE_PINEAPPLE) {
                if (gameMode == GameConstants.GAME_MODE_NORMAL) {
                    //普通模式
                    GameNormalConfig gameConfig = GsonUtils.getGson().fromJson(gameConfigStr, GameNormalConfig.class);//new GameNormalConfig();
                    if (gameConfig == null) {
                        gameConfig = new GameNormalConfig();
                        gameConfig.blindType = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_SMALL_BLINDS)));
                        gameConfig.timeType = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_DURATIONS)));
                        gameConfig.anteMode = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_MODE_ANTE)));
                        gameConfig.ante = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_ANTE)));
                        gameConfig.tiltMode = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_MODE_TILT)));
                    }
                    gameInfo.horde_id = gameConfig.horde_id;
                    gameInfo.horde_name = gameConfig.horde_name;
                    gameInfo.club_channel = gameConfig.club_channel;
                    gameInfo.gameConfig = (gameConfig);
                } else if (gameMode == GameConstants.GAME_MODE_SNG) {
                    //SNG模式
                    GameSngConfigEntity gameConfig = GsonUtils.getGson().fromJson(gameConfigStr, GameSngConfigEntity.class);//new GameSngConfigEntity();
                    if (gameConfig == null) {
                        gameConfig = new GameSngConfigEntity();
                        gameConfig.setChips(cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_MATCH_CHIPS)));
                        gameConfig.setPlayer(cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_MATCH_PLAYER)));
                        gameConfig.setDuration(cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_MATCH_DURATION)));
                        gameConfig.setCheckInFee(cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_MATCH_CHECKIN_FEE)));
                    }
                    gameInfo.horde_id = gameConfig.horde_id;
                    gameInfo.horde_name = gameConfig.horde_name;
                    gameInfo.club_channel = gameConfig.club_channel;
                    gameInfo.gameConfig = (gameConfig);
                } else if (gameMode == GameConstants.GAME_MODE_MTT) {
                    //MTT模式
                    GameMttConfig gameConfig = GsonUtils.getGson().fromJson(gameConfigStr, GameMttConfig.class);//new GameMttConfig();
                    if (gameConfig == null) {
                        gameConfig = new GameMttConfig();
                        gameConfig.matchChips = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_MATCH_CHIPS)));
                        gameConfig.matchPlayer = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_MATCH_PLAYER)));
                        gameConfig.matchDuration = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_MATCH_DURATION)));
                        gameConfig.matchCheckinFee = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_MATCH_CHECKIN_FEE)));
                        gameConfig.ko_mode = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_KO_MODE)));
                        gameConfig.ko_reward_rate = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_KO_REWARD_RATE)));
                        gameConfig.match_type = gameInfo.match_type;
                        gameConfig.ko_head_rate = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_KO_HEAD_RATE)));
                    }
                    gameInfo.horde_id = gameConfig.horde_id;
                    gameInfo.horde_name = gameConfig.horde_name;
                    gameInfo.club_channel = gameConfig.club_channel;
                    gameInfo.gameConfig = (gameConfig);
                } else if (gameMode == GameConstants.GAME_MODE_MT_SNG) {
                    //MT-SNG模式
                    GameMtSngConfig gameConfig = GsonUtils.getGson().fromJson(gameConfigStr, GameMtSngConfig.class);//new GameMtSngConfig();
                    if (gameConfig == null) {
                        gameConfig = new GameMtSngConfig();
                        gameConfig.matchChips = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_MATCH_CHIPS)));
                        gameConfig.matchPlayer = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_MATCH_PLAYER)));
                        gameConfig.matchDuration = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_MATCH_DURATION)));
                        gameConfig.matchCheckinFee = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_MATCH_CHECKIN_FEE)));
                        gameConfig.totalPlayer = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_TOTAL_PLAYER)));
                    }
                    gameInfo.horde_id = gameConfig.horde_id;
                    gameInfo.horde_name = gameConfig.horde_name;
                    gameInfo.club_channel = gameConfig.club_channel;
                    gameInfo.gameConfig = (gameConfig);
                }
            } else if (gameInfo.play_mode == GameConstants.PLAY_MODE_PINEAPPLE) {
                if (gameMode == GameConstants.GAME_MODE_NORMAL) {
                    PineappleConfig pineappleConfig = GsonUtils.getGson().fromJson(gameConfigStr, PineappleConfig.class);
                    gameInfo.horde_id = pineappleConfig.getHorde_id();
                    gameInfo.horde_name = pineappleConfig.getHorde_name();
                    gameInfo.club_channel = pineappleConfig.getClub_channel();
                    gameInfo.gameConfig = (pineappleConfig);
                } else if (gameMode == GameConstants.GAME_MODE_MTT) {
                    PineappleConfigMtt configMtt = GsonUtils.getGson().fromJson(gameConfigStr, PineappleConfigMtt.class);
                    gameInfo.horde_id = configMtt.horde_id;
                    gameInfo.horde_name = configMtt.horde_name;
                    gameInfo.club_channel = configMtt.club_channel;
                    gameInfo.gameConfig = (configMtt);
                }
            }
            gameBillEntity.gameInfo = (gameInfo);
            gameBillEntity.totalPlayer = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_TOTAL_PLAYER)));
            //数据清单
            gameBillEntity.maxPot = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_MAX_POT)));
            gameBillEntity.allBuys = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_ALL_BUYS)));
            gameBillEntity.bouts = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_BOUNTS)));
            gameBillEntity.winChip = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_WIN_CHIPS)));
            //MVP，大鱼，土豪
            gameBillEntity.mvp = (cursor.getString(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_MVP)));
            gameBillEntity.fish = (cursor.getString(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_FISH)));
            gameBillEntity.richest = (cursor.getString(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_RICHEST)));
            String memberJsonStr = cursor.getString(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_MEMBERS));
            gameBillEntity.jsonMemberStr = (memberJsonStr);
            //
            gameBillEntity.totalTime = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_TOTAL_TIME)));
            gameBillEntity.allReward = (cursor.getInt(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_ALL_REWARD)));
            String myUid = cursor.getString(cursor.getColumnIndex(GameRecordTable.COLUMN_GAME_MY_UID));
            gameBillEntity.myUid = (myUid);
            //
            try {
                if (!TextUtils.isEmpty(memberJsonStr)) {
                    JSONArray membersArray = new JSONArray(memberJsonStr);
                    if (membersArray != null) {
                        int memberSize = membersArray.length();
                        ArrayList<GameMemberEntity> memberList = new ArrayList<GameMemberEntity>(memberSize);
                        for (int j = 0; j < memberSize; j++) {
                            JSONObject memberJson = membersArray.getJSONObject(j);
                            GameMemberEntity gameMemberEntity = RecordJsonTools.getGameMemberEntity(memberJson);
                            if (gameMemberEntity.userInfo.account.equals(myUid)) {
                                gameBillEntity.myMemberInfo = (gameMemberEntity);
                            }
                            memberList.add(gameMemberEntity);
                        }
                        gameBillEntity.gameMemberList = (memberList);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            gameBillEntity.jsonStr = (BillAttachment.toPackData(gameBillEntity));
            return gameBillEntity;
        }
    }

    /**
     *
     * @param context
     * @param list
     */
    public static void SetNetRecordList(Context context,List<NetCardRecordEy> list){
        if(list == null || list.isEmpty())
            return;

        synchronized (sHelp) {
            DBUtil dbUtil = DBUtil.getInstance(context, NetCardRecordTable.TABLE_NAME);

            for(NetCardRecordEy data : list){
                String selection = NetCardRecordTable.id + " =? ";
                String[] selectionArgs = { data.id };
                Cursor cursor = dbUtil.selectData(null, selection, selectionArgs, null, null, null);

                //如果没有该数据
                if(!cursor.moveToFirst()){
                    ContentValues values = new ContentValues();


                    values.put(NetCardRecordTable.code, data.code);
                    values.put(NetCardRecordTable.win_chips, data.win_chips);
                    values.put(NetCardRecordTable.card_type, data.card_type);
                    values.put(NetCardRecordTable.time, data.time);
                    values.put(NetCardRecordTable.gid, data.gid);

                    //to Json
                    values.put(NetCardRecordTable.pool_cards, GsonUtils.getGson().toJson(data.pool_cards));
                    values.put(NetCardRecordTable.cardtype_cards, GsonUtils.getGson().toJson(data.cardtype_cards));
                    values.put(NetCardRecordTable.hand_cards, GsonUtils.getGson().toJson(data.hand_cards));

                    values.put(NetCardRecordTable.hands_cnt, data.hands_cnt);
                    values.put(NetCardRecordTable.id, data.id);
                    values.put(NetCardRecordTable.is_collect, data.is_collect);

                    dbUtil.insertData(values);
                }
            }
        }
    }

    public static void DeleteNetRecord(Context context,String id){
        if(TextUtils.isEmpty(id))
            return;

        synchronized (sHelp){
            DBUtil dbUtil = DBUtil.getInstance(context, NetCardRecordTable.TABLE_NAME);
            String whereClause = NetCardRecordTable.id + " = ? ";
            String[] whereArgs = {id};

            //更新牌谱收藏
            dbUtil.deleteData(whereClause,whereArgs);
        }
    }

    public static void SetNetRecordCollect(Context context,String id,boolean isCollect){
        if(TextUtils.isEmpty(id))
            return;

        synchronized (sHelp){
            DBUtil dbUtil = DBUtil.getInstance(context, NetCardRecordTable.TABLE_NAME);
            ContentValues values = new ContentValues();
            values.put(NetCardRecordTable.is_collect,isCollect?1:0);
            String whereClause = NetCardRecordTable.id + " = ? ";
            String[] whereArgs = {id};

            //更新牌谱收藏
            dbUtil.updateData(values,whereClause,whereArgs);
        }
    }

    /**
     *
     * @param context
     * @param recordTime 如果为0，则认为是取最近最多20条
     * @return
     */
    public static List<NetCardRecordEy> GetNetRecordList(Context context, long recordTime){
        synchronized (sHelp) {
            DBUtil dbUtil = DBUtil.getInstance(context, NetCardRecordTable.TABLE_NAME);

            String section = null;
            String[] sectionArg = null;
            if(recordTime != 0){
                section = NetCardRecordTable.time + " < ? ";
                sectionArg = new String[]{String.valueOf(recordTime)};
            }

            String orderBy = NetCardRecordTable.time + " DESC ";
            Cursor cursor = dbUtil.selectData(null, section, sectionArg, null, null, orderBy);

            List<NetCardRecordEy> list = new ArrayList<>();
            int i = 0;
            for (cursor.moveToFirst(); i < PAGE_COOUNT && !cursor.isAfterLast(); cursor.moveToNext(),i++) {
                NetCardRecordEy data = new NetCardRecordEy();


                data.code = cursor.getString(cursor.getColumnIndex(NetCardRecordTable.code));
                data.win_chips = cursor.getInt(cursor.getColumnIndex(NetCardRecordTable.win_chips));
                data.card_type = cursor.getInt(cursor.getColumnIndex(NetCardRecordTable.card_type));
                data.time = cursor.getLong(cursor.getColumnIndex(NetCardRecordTable.time));
                data.gid = cursor.getString(cursor.getColumnIndex(NetCardRecordTable.gid));

                //to Json
                Type type = new TypeToken<List<List<Integer>>>(){}.getType();
                data.pool_cards = GsonUtils.getGson().fromJson(cursor.getString(cursor.getColumnIndex(NetCardRecordTable.pool_cards)),type);
                type = new TypeToken<HashMap<String, CardTypeEy>>(){}.getType();
                data.cardtype_cards = GsonUtils.getGson().fromJson(cursor.getString(cursor.getColumnIndex(NetCardRecordTable.cardtype_cards)),type);
                type = new TypeToken<List<Integer>>(){}.getType();
                data.hand_cards = GsonUtils.getGson().fromJson(cursor.getString(cursor.getColumnIndex(NetCardRecordTable.hand_cards)),type);

                data.hands_cnt = cursor.getInt(cursor.getColumnIndex(NetCardRecordTable.hands_cnt));
                data.id = cursor.getString(cursor.getColumnIndex(NetCardRecordTable.id));
                data.is_collect = cursor.getInt(cursor.getColumnIndex(NetCardRecordTable.is_collect));

                list.add(data);
            }
            return list;
        }
    }

    /**
     * 查询数据库条目数
     * @param context
     * @return
     */
    public static int GetNetRecordCount(Context context){
        synchronized (sHelp) {
            DBUtil dbUtil = DBUtil.getInstance(context, NetCardRecordTable.TABLE_NAME);
            Cursor cursor = dbUtil.selectData(null, null, null, null, null, null);

            return cursor.getCount();
        }
    }
}
