package com.htgames.nutspoker.game.tools;

import com.htgames.nutspoker.game.match.Match;
import com.htgames.nutspoker.game.match.bean.MatchPlayerEntity;
import com.htgames.nutspoker.game.match.bean.MatchStatusEntity;
import com.htgames.nutspoker.game.match.bean.MatchTableEntity;
import com.htgames.nutspoker.game.model.GameStatus;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMtSngConfig;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.bean.UserEntity;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.constants.GameConstants;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 */
public class GameMatchJsonTools {
    public static final String TAG = GameMatchJsonTools.class.getSimpleName();

    public static MatchStatusEntity getMatchStatusEntity(Match.MatchSelfStatusRep rep) {
        if (rep == null) {
            return null;
        }
        MatchStatusEntity matchStatusEntity = new MatchStatusEntity();
        matchStatusEntity.currentLevel = rep.getCurrSblindsIndex();
        matchStatusEntity.leftPlayer = rep.getCurrLeftPlayers();
        matchStatusEntity.allReward = rep.getAllReward();
        matchStatusEntity.maxChips = rep.getMaxChips();
        matchStatusEntity.minChips = rep.getMinChips();
        matchStatusEntity.myCheckInStatus = rep.getCheckin();
        matchStatusEntity.checkInPlayer = rep.getCheckinPlayer();
        matchStatusEntity.buyTypeStatus = rep.getBuyType();
        matchStatusEntity.matchStatus = rep.getMatchStatus();
        matchStatusEntity.gameStatus = rep.getGameStatus();
        matchStatusEntity.score = rep.getScore();//分数（为0的时候为被淘汰）
        matchStatusEntity.isControl = rep.getIsControl();
        matchStatusEntity.beginTime = rep.getBeginTime();
        matchStatusEntity.matchInRest = rep.getMatchInRest();
        matchStatusEntity.matchPauseTime = rep.getMatchPauseTime();
        matchStatusEntity.rebuy_cnt = rep.getRebuyCnt();//自己的重构次数
        matchStatusEntity.manul_begin_game_success = rep.getStartTime();//手动开赛成功
        matchStatusEntity.checkin_time = rep.getCheckinTime();
        return matchStatusEntity;
    }

    public static ArrayList<MatchPlayerEntity> parseMatchPlayerList(Match.SearchMatchUserRep rep, int gameStatus , int matchChips) {
        ArrayList<MatchPlayerEntity> list = new ArrayList<>();
        if (rep == null || rep.getUserListList() == null || rep.getUserListList().size() <= 0) {
            return list;
        }
        for (Match.UserInfo player : rep.getUserListList()) {
            MatchPlayerEntity playerEntity = parseMatchPlayer(player);
            if (gameStatus == GameStatus.GAME_STATUS_WAIT) {
                playerEntity.chips = (matchChips);
            }
            list.add(playerEntity);
        }
        return list;
    }

    public static ArrayList<MatchPlayerEntity> parseMatchPlayerList(Match.MatchUserListRep rep, int gameStatus , int matchChips) {
        ArrayList<MatchPlayerEntity> list = new ArrayList<>();
        if (rep == null || rep.getUserListList() == null || rep.getUserListList().size() <= 0) {
            return list;
        }
        for (Match.UserInfo player : rep.getUserListList()) {
            MatchPlayerEntity playerEntity = parseMatchPlayer(player);
            if (gameStatus == GameStatus.GAME_STATUS_WAIT) {
                playerEntity.chips = (matchChips);
            }
            list.add(playerEntity);
        }
        return list;
    }

    public static MatchPlayerEntity parseMatchPlayer(Match.UserInfo player) {
        MatchPlayerEntity playerEntity = new MatchPlayerEntity();
        if (player == null) {
            return playerEntity;
        }
        playerEntity.uid = player.getUid() + "";
        playerEntity.nickname = player.getNickname().toStringUtf8();
        playerEntity.chips = player.getChips();//mtt和普通局共用的     普通局的总盈利
        playerEntity.tableNo = 0;//player.getTableNo();
        playerEntity.rank = player.getRanking();
        playerEntity.rebuyCnt = player.getRebuyCnt();
        playerEntity.addonCnt = 0;//player.getAddonCnt();

        DecimalFormat df = new DecimalFormat("#.##");
        playerEntity.ko_head_cnt = df.format(player.getKoHeadCnt() / 100f);

        playerEntity.ko_head_reward = player.getKoHeadReward();//mtt大厅玩家列表的人头奖金-----只有超级猎人赛才有
        playerEntity.ko_worth = player.getKoWorth();//mtt大厅玩家列表的人头奖金-----只有超级猎人赛才有
        playerEntity.uuid = player.getUuid() + "";//
        if ("0".equals(playerEntity.uuid)) {
            playerEntity.uuid = "";
        }
        playerEntity.opt_user_real = player.getOptUserReal().toStringUtf8();
        playerEntity.opt_user = player.getOptUser().toStringUtf8();
        playerEntity.opt_user_type = player.getOptUserType();
//            playerEntity.avatar;//这个解析不到的，websocket不传这个值
//            playerEntity.total_buy;//普通局的的总买入   不是长连接返回的，短连接返回的
//            playerEntity.reward;//普通局的的总盈利   不是长连接返回的，短连接返回的
        return playerEntity;
    }

    public static ArrayList<MatchTableEntity> parseMatchTableList(Match.MatchTableListRep rep) {
        ArrayList<MatchTableEntity> tablesList = new ArrayList<>();
        if (rep == null || rep.getTableListList() == null || rep.getTableListList().size() <= 0) {
            return tablesList;
        }
        for (Match.MatchTableListRep.TableInfo tableInfo : rep.getTableListList()) {
            MatchTableEntity tableEntity = new MatchTableEntity();
            tableEntity.index = tableInfo.getIndex();
            tableEntity.memberCount = tableInfo.getCount();
            tableEntity.maxChips = tableInfo.getMaxChips();
            tableEntity.minChips = tableInfo.getMinChips();
            tablesList.add(tableEntity);
        }
        return tablesList;
    }
}
