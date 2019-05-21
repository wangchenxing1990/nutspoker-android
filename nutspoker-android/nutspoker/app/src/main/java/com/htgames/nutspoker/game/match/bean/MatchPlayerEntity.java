package com.htgames.nutspoker.game.match.bean;

import com.google.gson.annotations.SerializedName;
import com.htgames.nutspoker.game.match.constants.MatchConstants;

import java.io.Serializable;

/**
 * Created by 20150726 on 2016/6/13.
 */
public class MatchPlayerEntity implements Serializable {
    @SerializedName(MatchConstants.KEY_PLAYER_UID) public String uid;
    @SerializedName(MatchConstants.KEY_PLAYER_NICKNAME) public String nickname;
    @SerializedName(MatchConstants.KEY_PLAYER_CHIPS) public int chips;//mtt和普通局共用的     普通局的总盈利
    @SerializedName(MatchConstants.KEY_PLAYER_TABLE_NO) public int tableNo;
    @SerializedName(MatchConstants.KEY_PLAYER_RANK) public int rank;
    @SerializedName(MatchConstants.KEY_REBUY_CNT) public int rebuyCnt;
    @SerializedName(MatchConstants.KEY_ADDON_CNT) public int addonCnt;
    @SerializedName(MatchConstants.KO_HEAD_CNT) public String ko_head_cnt;//mtt大厅玩家列表的人头数 ------ 普通猎人赛   以前是float现在改为String，因为4.00要显示为4，3.43要显示带小数，就用string

    @SerializedName(MatchConstants.KO_HEAD_REWARD) public int ko_head_reward;//mtt大厅玩家列表的人头奖金-----只有超级猎人赛才有
    @SerializedName(MatchConstants.KO_HEAD_WORTH) public int ko_worth;//mtt大厅玩家列表的人头奖金-----只有超级猎人赛才有
    @SerializedName(MatchConstants.KEY_OPT_USER) public String opt_user;//这个玩家是谁批准报名的，私人或者俱乐部
    @SerializedName(MatchConstants.KEY_OPT_USER_REAL) public String opt_user_real;//这个字段只给"管理"页面使用，"玩家"页面还是使用opt_user字段
    @SerializedName(MatchConstants.KEY_OPT_USER_TYPE) public int opt_user_type;///这个玩家是谁批准报名的，0私人1俱乐部
    @SerializedName(MatchConstants.KEY_PLAYER_UUID) public String uuid;
    public String avatar;//这个解析不到的，websocket不传这个值
    public int total_buy;//普通局的的总买入   不是长连接返回的，短连接返回的
    public int reward;//普通局的的总盈利   不是长连接返回的，短连接返回的
    public MatchPlayerEntity() {
    }

    public static MatchPlayerEntity copyMatchPlayer(MatchPlayerEntity player) {
        MatchPlayerEntity playerEntity = new MatchPlayerEntity();
        if (player == null) {
            return playerEntity;
        }
        playerEntity.uid = player.uid;
        playerEntity.nickname = player.nickname;
        playerEntity.chips = player.chips;//mtt和普通局共用的     普通局的总盈利
        playerEntity.tableNo = player.tableNo;
        playerEntity.rank = player.rank;
        playerEntity.rebuyCnt = player.rebuyCnt;
        playerEntity.addonCnt = player.addonCnt;
        playerEntity.ko_head_cnt = player.ko_head_cnt;
        playerEntity.ko_head_reward = player.ko_head_reward;//mtt大厅玩家列表的人头奖金-----只有超级猎人赛才有
        playerEntity.ko_worth = player.ko_worth;//mtt大厅玩家列表的人头奖金-----只有超级猎人赛才有
        playerEntity.uuid = player.uuid;//
        if ("0".equals(playerEntity.uuid)) {
            playerEntity.uuid = "";
        }
        playerEntity.opt_user = player.opt_user;
        playerEntity.opt_user_real = player.opt_user_real;
        playerEntity.opt_user_type = player.opt_user_type;
//            playerEntity.avatar;//这个解析不到的，websocket不传这个值
//            playerEntity.total_buy;//普通局的的总买入   不是长连接返回的，短连接返回的
//            playerEntity.reward;//普通局的的总盈利   不是长连接返回的，短连接返回的
        return playerEntity;
    }
}
