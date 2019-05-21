package com.htgames.nutspoker.data;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.htgames.nutspoker.ChessApp;
import com.netease.nim.uikit.bean.CardGamesEy;
import com.netease.nim.uikit.bean.CardTypeEy;
import com.netease.nim.uikit.bean.CommonBeanT;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMtSngConfig;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.bean.GameNormalConfig;
import com.netease.nim.uikit.bean.GameSngConfigEntity;
import com.netease.nim.uikit.bean.NetCardCollectBaseEy;
import com.netease.nim.uikit.bean.NetCardRecordEy;
import com.netease.nim.uikit.bean.PaipuEntity;
import com.netease.nim.uikit.bean.PaipuJsonEy;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.data.common.PaipuConstants;
import com.netease.nim.uikit.common.gson.GsonUtils;
import com.htgames.nutspoker.db.GameDataDBHelper;
import com.htgames.nutspoker.hotupdate.preference.HotUpdatePreferences;
import com.htgames.nutspoker.interfaces.DownloadCallback;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.ui.action.HandCollectAction;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by glp on 2016/8/9.
 */

public class DataManager {
    //只有一个账号
    public List<String> sManagerList = new ArrayList<>();
    //管理员列表
    public List<TeamMember> sMgrList = new ArrayList<>();

    public interface OnDataFinish{
        void onDataFinish(Object data);
    }

    public synchronized static DataManager getInstance(){
        if(sInstance == null)
            sInstance = new DataManager();
        return  sInstance;
    }

    /**
     * 返回我的指定牌谱记录的本地路径，如果为null，则会在后台从网络获取，并从listener回调
     * @param handsId
     * @param action
     * @param listener
     * @return
     */
    public static String GetSheetFileOfMy(String handsId,HandCollectAction action,final OnDataFinish listener){
        return GetSheetFile(UserPreferences.getInstance(ChessApp.sAppContext).getUserId(),handsId,action,listener);
    }

    /**
     * 返回指定账号的指定牌谱记录的本地路径，如果为null，则会在后台从网络获取，并从listener回调
     * @param account
     * @param handsId
     * @param action
     * @param listener
     * @return
     */
    public static String GetSheetFile(String account,String handsId,HandCollectAction action,final OnDataFinish listener){
        if(!TextUtils.isEmpty(handsId)){
            //先去查看本地文件
            final File file = PaipuConstants.getPaipuFileNew(ChessApp.sAppContext, account, handsId);
            if (file == null) {
                return null;
            }
            if (file.exists() && file.isFile()) {
                return file.getPath();
            } else {
                //然后请求服务器
                action.getCRFile(account, handsId, file, new DownloadCallback() {
                    @Override
                    public void onDownload(String s) {
                        if (listener != null) {
                            listener.onDataFinish(s);
                        }
                    }
                    @Override
                    public void onFailed() {
                        if (listener != null) {
                            listener.onDataFinish(null);
                        }
                    }
                });
            }
        }
        return null;
    }

    /**
     * 先查询本地数据库，如果有则直接返回，否则返回null，然后异步请求，通过listener返回
     * @param gid
     * @param listener
     * @return
     */
    public CardGamesEy getCardGamesEy(final String gid, final HandCollectAction action, final OnDataFinish listener){
        //查询内存
        CardGamesEy ret = null;
        synchronized (mMapGameInfo){
            if(mMapGameInfo.containsKey(gid))
                return  mMapGameInfo.get(gid);
        }
        //查询数据库
        Observable.just(gid)
                .observeOn(Schedulers.io())
                .map(new Func1<String, CardGamesEy>() {
                    @Override
                    public CardGamesEy call(String s) {
                        CardGamesEy ret = GameDataDBHelper.getGameInfoData(ChessApp.sAppContext,s);

                        if(ret != null){
                            synchronized (mMapGameInfo){
                                //读取放到内存中
                                mMapGameInfo.put(ret.gid, ret);
                            }
                        }
                        return ret;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CardGamesEy>() {
                    @Override
                    public void call(CardGamesEy cardGamesEy) {
                        //查询网络
                        if(cardGamesEy == null && action != null){
                            action.getGameInfo(gid, new RequestCallback() {
                                @Override
                                public void onResult(int code, String result, Throwable var3) {
                                    if(code == 0){
                                        try{
                                            Type type = new TypeToken< CommonBeanT<CardGamesEy>>(){}.getType();
                                            CommonBeanT<CardGamesEy> cbt = GsonUtils.getGson().fromJson(result,type);

                                            if(cbt != null) {
                                                setCardGamesEy(cbt.data);
                                                if(listener != null){
                                                    listener.onDataFinish(cbt.data);
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else if(listener != null){
                                        listener.onDataFinish(null);
                                    }
                                }

                                @Override
                                public void onFailed() {
                                    if(listener != null){
                                        listener.onDataFinish(null);
                                    }
                                }
                            });
                        } else {
                            if(listener != null)
                                listener.onDataFinish(cardGamesEy);
                        }
                    }
                });
        return ret;
    }

    public synchronized void setCardGamesEy(CardGamesEy data){
        if(data == null)
            return;
        if(!mMapGameInfo.containsKey(data.gid)) {
            mMapGameInfo.put(data.gid, data);

            //保存到本地数据库
            Observable.just(data)
                    .observeOn(Schedulers.io())
                    .subscribe(new Action1<CardGamesEy>() {
                        @Override
                        public void call(CardGamesEy cardGamesEy) {
                            GameDataDBHelper.setGameInfoData(ChessApp.sAppContext,cardGamesEy);
                        }
                    });
        }
    }

    /**
     * 是否是旧版牌谱
     * @param hid 牌谱ID
     * @return
     */
    public static boolean IsOldPaipu(String hid){
        if(TextUtils.isEmpty(hid) || hid.length() < 24)
            return true;
        return false;
    }
    /**
     * 牌谱记录列表的数据模型转换成以前版本的PaipuEntity
     * @param netCardRecordEy
     * @param cardGamesEy
     * @param paipuEntity
     */
    public static void ConvertData(NetCardRecordEy netCardRecordEy, CardGamesEy cardGamesEy, PaipuEntity paipuEntity){
        if(paipuEntity == null || netCardRecordEy==null || cardGamesEy==null)
            return;

        GameEntity gameInfo = new GameEntity();
        gameInfo.gid = (netCardRecordEy.gid);
        gameInfo.createTime = (netCardRecordEy.time);
        if(cardGamesEy.game_mode == GameConstants.GAME_MODE_NORMAL){
            GameNormalConfig gameConfig = new GameNormalConfig();
            gameConfig.blindType = (cardGamesEy.sblinds);
            gameInfo.gameConfig = (gameConfig);
        } else if(cardGamesEy.game_mode == GameConstants.GAME_MODE_SNG){
            GameSngConfigEntity gameConfig = new GameSngConfigEntity();
            gameConfig.setPlayer(cardGamesEy.match_player);
            gameConfig.setCheckInFee(cardGamesEy.match_checkin_fee);
            gameConfig.setChips(cardGamesEy.match_chips);
            gameInfo.gameConfig = (gameConfig);
        } else if(cardGamesEy.game_mode == GameConstants.GAME_MODE_MTT){
            GameMttConfig gameConfig = new GameMttConfig();
            gameConfig.matchPlayer = (cardGamesEy.match_player);
            gameConfig.matchCheckinFee = (cardGamesEy.match_checkin_fee);
            gameConfig.matchChips = (cardGamesEy.match_chips);
            gameInfo.gameConfig = (gameConfig);
        } else if(cardGamesEy.game_mode == GameConstants.GAME_MODE_MT_SNG){
            GameMtSngConfig gameConfig = new GameMtSngConfig();
            gameConfig.totalPlayer = (cardGamesEy.match_player);
            gameConfig.matchCheckinFee = (cardGamesEy.match_checkin_fee);
            gameConfig.matchChips = (cardGamesEy.match_chips);
            gameInfo.gameConfig = (gameConfig);
        }
        gameInfo.gameMode = (cardGamesEy.game_mode);
        paipuEntity.gameEntity = (gameInfo);

        paipuEntity.winChip = (netCardRecordEy.win_chips);
        paipuEntity.cardType = (netCardRecordEy.card_type);

        if(netCardRecordEy.pool_cards != null && !netCardRecordEy.pool_cards.isEmpty()){
            List<Integer> tmpList = new ArrayList<>();
            for(List<Integer> list : netCardRecordEy.pool_cards){
                tmpList.addAll(list);
            }
            paipuEntity.poolCards = (tmpList);
        } else
            paipuEntity.poolCards = (null);

        if(netCardRecordEy.cardtype_cards != null && !netCardRecordEy.cardtype_cards.isEmpty()){
            List<Integer> typeList = new ArrayList<>();
            for (Map.Entry<String, CardTypeEy> entry : netCardRecordEy.cardtype_cards.entrySet()) {
                CardTypeEy cardTypeEy = entry.getValue();
                /**
                 * (牌值-1)*4+花色
                 * 牌值 2-14
                 * 花色 1-4
                 */
                typeList.add((cardTypeEy.rank-1)*4+cardTypeEy.suit);
            }
            paipuEntity.cardTypeCards = (typeList);
        } else {
            paipuEntity.cardTypeCards = (null);
        }
        paipuEntity.handCards = (netCardRecordEy.hand_cards);
        paipuEntity.handsCnt = (netCardRecordEy.hands_cnt);
        paipuEntity.handsId = (netCardRecordEy.id);
        //牌局归属者
        paipuEntity.sheetUid = (UserPreferences.getInstance(ChessApp.sAppContext).getUserId());

        //翻译Json Data
        PaipuJsonEy jsonEy = new PaipuJsonEy();
        jsonEy.hand_cards = netCardRecordEy.hand_cards;
        jsonEy.tableIndex = 0;
        jsonEy.card_type = netCardRecordEy.card_type;
        jsonEy.gid = netCardRecordEy.gid;
        jsonEy.record_version = HotUpdatePreferences.getInstance(ChessApp.sAppContext).getGameVersion();
        jsonEy.match_checkin_fee = cardGamesEy.match_checkin_fee;
        jsonEy.uid = paipuEntity.sheetUid;
        jsonEy.tid = cardGamesEy.tid;
        jsonEy.ante_mode = cardGamesEy.ante_mode;
        jsonEy.match_duration = cardGamesEy.match_duration;
        jsonEy.match_player = cardGamesEy.match_duration;
        jsonEy.owner = cardGamesEy.owner;
        jsonEy.game_mode = cardGamesEy.game_mode;
        jsonEy.cardtype_cards = paipuEntity.cardTypeCards;
        jsonEy.create_time = cardGamesEy.create_time;
        jsonEy.titl_mode = cardGamesEy.tilt_mode;
        jsonEy.status = cardGamesEy.status;
        jsonEy.code = netCardRecordEy.code;
        jsonEy.sblinds = cardGamesEy.sblinds;
        jsonEy.public_mode = cardGamesEy.public_mode;
        jsonEy.pool_cards = paipuEntity.poolCards;
        jsonEy.durations = cardGamesEy.duration;
        jsonEy.hands_cnt = netCardRecordEy.hands_cnt;
        jsonEy.ante = 0;
        jsonEy.type = cardGamesEy.type;
        jsonEy.win_chips = netCardRecordEy.win_chips;
        jsonEy.name = cardGamesEy.name;
        jsonEy.match_chips = cardGamesEy.match_chips;
        jsonEy.hid = netCardRecordEy.id;
        String jsonData = GsonUtils.getGson().toJson(jsonEy);
        paipuEntity.jsonDataStr = (jsonData);
    }

    /**
     * 牌谱收藏列表的数据模型转换成以前版本的PaipuEntity
     * @param netCardCollectEy
     * @param paipuEntity
     */
    public static void ConvertData(NetCardCollectBaseEy netCardCollectEy, PaipuEntity paipuEntity){
        if(paipuEntity == null || netCardCollectEy==null)
            return;

        GameEntity gameInfo = new GameEntity();
        gameInfo.gid = (netCardCollectEy.gid);
        gameInfo.createTime = (netCardCollectEy.time);
        if(netCardCollectEy.game_mode == GameConstants.GAME_MODE_NORMAL){
            GameNormalConfig gameConfig = new GameNormalConfig();
            gameConfig.blindType = (netCardCollectEy.sblinds);
            gameInfo.gameConfig = (gameConfig);
        } else if(netCardCollectEy.game_mode == GameConstants.GAME_MODE_SNG){
            GameSngConfigEntity gameConfig = new GameSngConfigEntity();
            gameConfig.setPlayer(netCardCollectEy.match_player);
            gameConfig.setCheckInFee(netCardCollectEy.match_checkin_fee);
            gameConfig.setChips(netCardCollectEy.match_chips);
            gameInfo.gameConfig = (gameConfig);
        } else if(netCardCollectEy.game_mode == GameConstants.GAME_MODE_MTT){
            GameMttConfig gameConfig = new GameMttConfig();
            gameConfig.matchPlayer = (netCardCollectEy.match_player);
            gameConfig.matchCheckinFee = (netCardCollectEy.match_checkin_fee);
            gameConfig.matchChips = (netCardCollectEy.match_chips);
            gameInfo.gameConfig = (gameConfig);
        } else if(netCardCollectEy.game_mode == GameConstants.GAME_MODE_MT_SNG){
            GameMtSngConfig gameConfig = new GameMtSngConfig();
            gameConfig.totalPlayer = (netCardCollectEy.match_player);
            gameConfig.matchCheckinFee = (netCardCollectEy.match_checkin_fee);
            gameConfig.matchChips = (netCardCollectEy.match_chips);
            gameInfo.gameConfig = (gameConfig);
        }
        gameInfo.gameMode = (netCardCollectEy.game_mode);
        paipuEntity.gameEntity = (gameInfo);

        paipuEntity.winChip = (netCardCollectEy.win_chips);
        paipuEntity.cardType = (netCardCollectEy.card_type);

//        if(IsOldPaipu(netCardCollectEy.hid)){
//            NetCardCollectOldEy oldData = (NetCardCollectOldEy)netCardCollectEy;
//            if(oldData.pool_cards != null && !oldData.pool_cards.isEmpty())
//                paipuEntity.setPoolCards(oldData.pool_cards);
//            else
//                paipuEntity.setPoolCards(null);
//            paipuEntity.cardTypeCards = (oldData.cardtype_cards);
//        } else {
        NetCardCollectBaseEy newData = (NetCardCollectBaseEy)netCardCollectEy;
            if(newData.pool_cards != null && !newData.pool_cards.isEmpty()){
                List<Integer> tmpList = new ArrayList<>();
                for(List<Integer> list : newData.pool_cards){
                    tmpList.addAll(list);
                }
                paipuEntity.poolCards = (tmpList);
            } else
                paipuEntity.poolCards = (null);

            if(newData.cardtype_cards != null && !newData.cardtype_cards.isEmpty()) {
                List<Integer> typeList = new ArrayList<>();
                for (Map.Entry<String, CardTypeEy> entry : newData.cardtype_cards.entrySet()) {
                    CardTypeEy cardTypeEy = entry.getValue();
                    /**
                     * (牌值-1)*4+花色
                     * 牌值 2-14
                     * 花色 1-4
                     */
                    typeList.add((cardTypeEy.rank-1)*4+cardTypeEy.suit);
                }
                paipuEntity.cardTypeCards = (typeList);
            } else {
                paipuEntity.cardTypeCards = (null);
            }
//        }

        paipuEntity.handCards = (netCardCollectEy.hand_cards);
        paipuEntity.handsCnt = (netCardCollectEy.hands_cnt);
        paipuEntity.handsId = (netCardCollectEy.hid);
        //牌局归属者
        paipuEntity.sheetUid = (netCardCollectEy.uid);
        paipuEntity.fileNetPath = (netCardCollectEy.file_path);
        paipuEntity.fileName = (netCardCollectEy.file_name);

        //翻译Json Data
        PaipuJsonEy jsonEy = new PaipuJsonEy();
        jsonEy.hand_cards = netCardCollectEy.hand_cards;
        jsonEy.tableIndex = 0;
        jsonEy.card_type = netCardCollectEy.card_type;
        jsonEy.gid = netCardCollectEy.gid;
        jsonEy.record_version = HotUpdatePreferences.getInstance(ChessApp.sAppContext).getGameVersion();
        jsonEy.match_checkin_fee = netCardCollectEy.match_checkin_fee;
        jsonEy.uid = netCardCollectEy.uid;
        jsonEy.tid = netCardCollectEy.tid;
        jsonEy.ante_mode = netCardCollectEy.ante_mode;
        jsonEy.match_duration = netCardCollectEy.match_duration;
        jsonEy.match_player = netCardCollectEy.match_duration;
        jsonEy.owner = netCardCollectEy.owner;
        jsonEy.game_mode = netCardCollectEy.game_mode;
        jsonEy.cardtype_cards = paipuEntity.cardTypeCards;
        jsonEy.create_time = netCardCollectEy.create_time;
        jsonEy.titl_mode = netCardCollectEy.tilt_mode;
        jsonEy.status = netCardCollectEy.status;
        jsonEy.code = netCardCollectEy.code;
        jsonEy.sblinds = netCardCollectEy.sblinds;
        jsonEy.public_mode = netCardCollectEy.public_mode;
        jsonEy.pool_cards = paipuEntity.poolCards;
        jsonEy.durations = netCardCollectEy.durations;
        jsonEy.hands_cnt = netCardCollectEy.hands_cnt;
        jsonEy.ante = netCardCollectEy.ante;
        jsonEy.type = netCardCollectEy.type;
        jsonEy.win_chips = netCardCollectEy.win_chips;
        jsonEy.name = netCardCollectEy.name;
        jsonEy.match_chips = netCardCollectEy.match_chips;
        jsonEy.file_name = netCardCollectEy.file_name;
        jsonEy.file_path = netCardCollectEy.file_path;
        paipuEntity.fileNetPath = (netCardCollectEy.file_path);
        paipuEntity.fileName = (netCardCollectEy.file_name);
//        if(!IsOldPaipu(netCardCollectEy.hid))//如果不是以前的旧牌谱收藏
            jsonEy.hid = netCardCollectEy.hid;//需要放入新的字段，标记是新版本，去自己服务器下载

        String jsonData = GsonUtils.getGson().toJson(jsonEy);
        paipuEntity.jsonDataStr = (jsonData);
    }

    static DataManager sInstance;
    //内存保存一份
    Map<String,CardGamesEy> mMapGameInfo = new HashMap<>();
}
