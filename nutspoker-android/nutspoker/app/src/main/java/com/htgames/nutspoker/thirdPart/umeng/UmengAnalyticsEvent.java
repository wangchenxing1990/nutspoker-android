package com.htgames.nutspoker.thirdPart.umeng;

import android.content.Context;

import com.netease.nim.uikit.bean.GameNormalConfig;
import com.netease.nim.uikit.bean.GameSngConfigEntity;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.bean.DiamondGoodsEntity;
import com.netease.nim.uikit.bean.GameGoodsEntity;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * 友盟事件统计
 */
public class UmengAnalyticsEvent {

    /**
     * 统计：创建牌局（1组局,2俱乐部,3圈子）
     *
     * @param context
     * @param type
     */
    public static void onEventGameCreate(Context context, String type, Object gameConfig) {
        String evenId = UmengAnalyticsEventConstants.ID_GAME_CREATE;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(UmengAnalyticsEventConstants.KEY_GAME_TYPE, type);
        if (gameConfig instanceof GameNormalConfig) {
            evenId = UmengAnalyticsEventConstants.ID_GAME_CREATE;
            GameNormalConfig gameNormalConfig = (GameNormalConfig) gameConfig;
            map.put(UmengAnalyticsEventConstants.KEY_GAME_BLIND, String.valueOf(gameNormalConfig.blindType));
            map.put(UmengAnalyticsEventConstants.KEY_GAME_DURATION, String.valueOf(gameNormalConfig.timeType));
        } else if (gameConfig instanceof GameSngConfigEntity) {
            evenId = UmengAnalyticsEventConstants.ID_GAME_CREATE_SNG;
            GameSngConfigEntity gameSngConfigEntity = (GameSngConfigEntity) gameConfig;
            map.put(UmengAnalyticsEventConstants.KEY_GAME_BLIND, String.valueOf(gameSngConfigEntity.getPlayer()));
            map.put(UmengAnalyticsEventConstants.KEY_GAME_DURATION, String.valueOf(gameSngConfigEntity.getDuration()));
            map.put(UmengAnalyticsEventConstants.KEY_GAME_CHIPS, String.valueOf(gameSngConfigEntity.getChips()));
        }
        UmengAnalyticsEvent.onEvent(context, evenId, map);
    }

    /**
     * 统计：加入牌局（1码,2历,3俱,4圈）
     *
     * @param context
     * @param way
     */
    public static void onEventGameJoin(Context context, String way , int gameMode) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(UmengAnalyticsEventConstants.KEY_GAME_JOIN_WAY, way);
        String evenId = UmengAnalyticsEventConstants.ID_GAME_JOIN;
        if (gameMode == GameConstants.GAME_MODE_SNG) {
            evenId = UmengAnalyticsEventConstants.ID_GAME_JOIN_SNG;
        }
        UmengAnalyticsEvent.onEvent(context, evenId, map);
    }

    /**
     * 统计：邀请好友加入
     *
     * @param context
     */
    public static void onEventGameInvite(Context context , int gameMode) {
        String evenId = UmengAnalyticsEventConstants.ID_GAME_INVITE;
        if (gameMode == GameConstants.GAME_MODE_SNG) {
            evenId = UmengAnalyticsEventConstants.ID_GAME_INVITE_SNG;
        }
        UmengAnalyticsEvent.onEvent(context, evenId);
    }

    /**
     * 统计：分享
     *
     * @param context
     */
    public static void onEventShare(Context context) {
        UmengAnalyticsEvent.onEvent(context, UmengAnalyticsEventConstants.ID_SHARE_COUNT);
    }

    /**
     * 统计：评论
     *
     * @param context
     */
    public static void onEventComment(Context context) {
        UmengAnalyticsEvent.onEvent(context, UmengAnalyticsEventConstants.ID_COMMENT_COUNT);
    }

    /**
     * 统计：点赞
     *
     * @param context
     */
    public static void onEventLike(Context context) {
        UmengAnalyticsEvent.onEvent(context, UmengAnalyticsEventConstants.ID_LIKE_COUNT);
    }

    /**
     * 统计：创建的俱乐部数量
     *
     * @param context
     */
    public static void onEventClubCount(Context context) {
        UmengAnalyticsEvent.onEvent(context, UmengAnalyticsEventConstants.ID_CLUB_COUNT);
    }

    /**
     * 统计：创建的圈子数量
     *
     * @param context
     */
    public static void onEventGroupCount(Context context) {
        UmengAnalyticsEvent.onEvent(context, UmengAnalyticsEventConstants.ID_GROUP_COUNT);
    }

    /**
     * 统计：充值次数
     *
     * @param context
     * @param gameGoodsEntity
     */
    public static void onEventBuyGoods(Context context, GameGoodsEntity gameGoodsEntity) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(UmengAnalyticsEventConstants.KEY_SHOP_GOODS_ID, String.valueOf(gameGoodsEntity.id));
        UmengAnalyticsEvent.onEvent(context, UmengAnalyticsEventConstants.ID_SHOP_BUYGOODS, map);
    }

    /**
     * 统计：购买德州币商品
     *
     * @param context
     * @param diamondGoodsEntity
     */
    public static void onEventTopUpPrice(Context context, DiamondGoodsEntity diamondGoodsEntity) {
        //计算事件
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(UmengAnalyticsEventConstants.KEY_SHOP_TOPUP_ID, String.valueOf(diamondGoodsEntity.id));
//        map.put(UmengAnalyticsEventConstants.KEY_SHOP_TOPUP_PRICE, String.valueOf(diamondGoodsEntity.getPrice()));
        UmengAnalyticsEvent.onEvent(context, UmengAnalyticsEventConstants.ID_SHOP_TOPUP, map);
    }

    /**
     * 统计发生次数
     *
     * @param context
     * @param eventId 当前统计的事件ID
     */
    public static void onEvent(Context context, String eventId) {
        MobclickAgent.onEvent(context, eventId);
    }

    /**
     * 统计点击行为各属性被触发的次数
     *
     * @param context
     * @param eventId
     * @param map
     */
    public static void onEvent(Context context, String eventId, HashMap map) {
        MobclickAgent.onEvent(context, eventId, map);
    }

    /**
     * 统计数值型变量的值的分布
     * 统计一个数值类型的连续变量（该变量必须为整数），用户每次触发的数值的分布情况，如事件持续时间、每次付款金额等，可以调用如下方法
     *
     * @param context
     * @param id      事件ID
     * @param map     为当前事件的属性和取值
     * @param du      为当前事件的数值为当前事件的数值，取值范围是-2,147,483,648 到 +2,147,483,647 之间的有符号整数，即int 32类型，如果数据超出了该范围，会造成数据丢包，影响数据统计的准确性。
     */
    public static void onEventValue(Context context, String id, Map<String, String> map, int du) {
        MobclickAgent.onEventValue(context, id, map, du);
    }
}
