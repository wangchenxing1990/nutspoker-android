package com.htgames.nutspoker.game.helper;

import android.text.TextUtils;

import com.htgames.nutspoker.config.GameConfigData;
import com.htgames.nutspoker.game.config.GameConfigConstants;
import com.netease.nim.uikit.bean.GameSngConfigEntity;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.preference.GamePreferences;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.constants.GameConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by 20150726 on 2016/5/25.
 */
public class GameConfigHelper {
    private final static String TAG = "GameConfigHelper";

    /**
     * 获取游戏记录费
     * 小于等于2000  10%的记录费 ；大于2000，小于等于20000,7%记录费 ；大于20000   5%记录费
     * @param chips
     * @return
     */
    public static int getServiceFee(int chips) {
        float proportion = 1;
        if (chips <= 2000) {
            proportion = 0.1f;
        } else if (chips <= 20000) {
            proportion = 0.07f;
        } else {
            proportion = 0.05f;
        }
        int serviceFee = (int) (chips * proportion);
        return serviceFee;
    }

    /**
     * 处理游戏配置
     * @param data
     * @return
     */
    public static boolean dealGameConfig(String data) {
        if (StringUtil.isSpace(data)) {
            return false;
        }
        LogUtil.i(TAG + " json common:", data.substring(0, data.length() / 2));
        LogUtil.i(TAG + " json common:", data.substring(data.length() / 2, data.length()));//data太长，log显示不全，分开显示
        try {
            JSONObject dataJson = new JSONObject(data);
            if (dataJson != null) {
                int newVersion = dataJson.optInt("ver");
                float normalServiceRate = (float) dataJson.optDouble("normalServiceRate");
                float SNGServiceRate = (float) dataJson.optDouble("SNGServiceRate");
                float MTTServiceRate = (float) dataJson.optDouble("MTTServiceRate");
                if (normalServiceRate > 0) {
                    GameConfigData.normalServiceRate = normalServiceRate;
                }
                if (SNGServiceRate > 0) {
                    GameConfigData.SNGServiceRate = SNGServiceRate;
                }
                if (MTTServiceRate > 0) {
                    GameConfigData.MTTServiceRate = MTTServiceRate;
                }
                //俱乐部管理员上限
                GameConfigData.TEAM_NUM = dataJson.optInt("team_num");
                //小盲
                JSONArray sblindsArray = dataJson.optJSONArray(GameConfigConstants.KEY_SBLINDS);
                if (sblindsArray != null) {
                    int size = sblindsArray.length();
                    if (size > 0) {
                        int[] chipsNum = new int[size];
                        int[] blindInts = new int[size];
                        for (int i = 0; i < size; i++) {
                            blindInts[i] = sblindsArray.optInt(i);
                            chipsNum[i] = blindInts[i] * 200;//计算代码就在这里
                        }
                        GameConfigData.setNormalSBlinds(blindInts);
                        GameConfigData.setNormalChips(chipsNum);
                        LogUtil.i(TAG, "blindInts :" + blindInts.length);
                    }
                }
                //记分牌 原来是服务端算好返回给我们的，新需求 不需要这个字段，客户端根据大小盲计算     计算代码就在上面
//                JSONArray chipsArray = dataJson.optJSONArray(GameConfigConstants.KEY_CHIPS);
//                if (chipsArray != null) {
//                    int size = chipsArray.length();
//                    if (size != 0) {
//                        int[] chipsNum = new int[size];
//                        for (int i = 0; i < size; i++) {
//                            chipsNum[i] = chipsArray.optInt(i);
//                        }
//                        GameConfigData.setNormalChips(chipsNum);
////                        GameConstants.chipsNum = chipsNum;
//                        LogUtil.i(TAG, "chipsNum :" + chipsNum.length);
//                    }
//                }
                //sng普通的盲注结构表   3个数组  快速也是3个数组总共6个      mtt也是总共6个数组(普通&快速个3个)  sng_ante_multiple   sng_sblinds_multiple  sng_sblins
                JSONArray sng_sblinds_multiple = dataJson.optJSONArray(GameConfigConstants.KEY_SNG_SBLINDS_MULTIPLE);
                if (sng_sblinds_multiple != null) {
                    int size = sng_sblinds_multiple.length();
                    if (size > 0) {
                        GameConfigData.sng_sblinds_multiple = new float[size];
                        for (int i = 0; i < size; i++) {
                            GameConfigData.sng_sblinds_multiple[i] = (float) sng_sblinds_multiple.optDouble(i);
                        }
                    }
                }
                JSONArray sng_sblins = dataJson.optJSONArray(GameConfigConstants.KEY_SNG_SBLINS);
                if (sng_sblins != null) {
                    int size = sng_sblins.length();
                    if (size > 0) {
                        GameConfigData.sng_sblins = new int[size];
                        for (int i = 0; i < size; i++) {
                            GameConfigData.sng_sblins[i] = sng_sblins.optInt(i);
                        }
                    }
                }
                JSONArray sng_ante_multiple = dataJson.optJSONArray(GameConfigConstants.KEY_SNG_ANTE_MULTIPLE);
                if (sng_ante_multiple != null) {
                    int size = sng_ante_multiple.length();
                    if (size > 0) {
                        GameConfigData.sng_ante_multiple = new float[size];
                        for (int i = 0; i < size; i++) {
                            GameConfigData.sng_ante_multiple[i] = (float) sng_ante_multiple.optDouble(i);
                        }
                    }
                }
                //mtt普通的盲注结构表   3个数组  快速也是3个数组总共6个      mtt也是总共6个数组(普通&快速个3个)  mtt_ante_multiple   mtt_sblinds_multiple  mtt_sblins
                JSONArray mtt_sblinds_multiple = dataJson.optJSONArray(GameConfigConstants.KEY_MTT_SBLINDS_MULTIPLE);
                if (mtt_sblinds_multiple != null) {
                    int size = mtt_sblinds_multiple.length();
                    if (size > 0) {
                        GameConfigData.mtt_sblinds_multiple = new float[size];
                        for (int i = 0; i < size; i++) {
                            GameConfigData.mtt_sblinds_multiple[i] = (float) mtt_sblinds_multiple.optDouble(i);
                        }
                    }
                }
                JSONArray mtt_sblins = dataJson.optJSONArray(GameConfigConstants.KEY_MTT_SBLINS);
                if (mtt_sblins != null) {
                    int size = mtt_sblins.length();
                    if (size > 0) {
                        GameConfigData.mtt_sblins = new int[size];
                        for (int i = 0; i < size; i++) {
                            GameConfigData.mtt_sblins[i] = mtt_sblins.optInt(i);
                        }
                    }
                }
                JSONArray mtt_ante_multiple = dataJson.optJSONArray(GameConfigConstants.KEY_MTT_ANTE_MULTIPLE);
                if (mtt_ante_multiple != null) {
                    int size = mtt_ante_multiple.length();
                    if (size > 0) {
                        GameConfigData.mtt_ante_multiple = new float[size];
                        for (int i = 0; i < size; i++) {
                            GameConfigData.mtt_ante_multiple[i] = (float) mtt_ante_multiple.optDouble(i);
                        }
                    }
                }
                //mtt快速表,涉及到三个数组
                JSONArray mtt_sblinds_multiple_quick = dataJson.optJSONArray(GameConfigConstants.KEY_MTT_SBLINDS_MULTIPLE_QUICK);
                if (mtt_sblinds_multiple_quick != null) {
                    int size = mtt_sblinds_multiple_quick.length();
                    if (size > 0) {
                        GameConfigData.mtt_sblinds_multiple_quick = new float[size];
                        for (int i = 0; i < size; i++) {
                            GameConfigData.mtt_sblinds_multiple_quick[i] = (float) mtt_sblinds_multiple_quick.optDouble(i);
                        }
                    }
                }
                JSONArray mtt_sblins_quick = dataJson.optJSONArray(GameConfigConstants.KEY_MTT_SBLINS_QUICK);
                if (mtt_sblins_quick != null) {
                    int size = mtt_sblins_quick.length();
                    if (size > 0) {
                        GameConfigData.mtt_sblins_quick = new int[size];
                        for (int i = 0; i < size; i++) {
                            GameConfigData.mtt_sblins_quick[i] = mtt_sblins_quick.optInt(i);
                        }
                    }
                }
                JSONArray mtt_ante_multiple_quick = dataJson.optJSONArray(GameConfigConstants.KEY_MTT_ANTE_MULTIPLE_QUICK);
                if (mtt_ante_multiple_quick != null) {
                    int size = mtt_ante_multiple_quick.length();
                    if (size > 0) {
                        GameConfigData.mtt_ante_multiple_quick = new float[size];
                        for (int i = 0; i < size; i++) {
                            GameConfigData.mtt_ante_multiple_quick[i] = (float) mtt_ante_multiple_quick.optDouble(i);
                        }
                    }
                }
                //mtt参赛人数上限 数组   match_max_buy_cnt
                JSONArray mtt_checkin_limit = dataJson.optJSONArray(GameConfigConstants.KEY_MTT_CHECKIN_LIMIT);
                if (mtt_checkin_limit != null) {
                    int size = mtt_checkin_limit.length();
                    if (size > 0) {
                        GameConfigData.mtt_checkin_limit = new int[size];
                        for (int i = 0; i < size; i++) {
                            GameConfigData.mtt_checkin_limit[i] = mtt_checkin_limit.optInt(i);
                        }
                    }
                }
                //时间
                JSONArray durationArray = dataJson.optJSONArray(GameConfigConstants.KEY_DURATION);
                if (durationArray != null) {
                    int size = durationArray.length();
                    if (size != 0) {
                        int[] durationNum = new int[size];
                        for (int i = 0; i < size; i++) {
                            durationNum[i] = (int) (durationArray.optDouble(i));//服务端返回的单位是分钟
                        }
                        GameConfigData.setNormalDuration(durationNum);
//                        GameConstants.durationMinutes = durationNum;
                        LogUtil.i(TAG, "timeMinutes :" + durationNum.length);
                    }
                }
                //ANTE
                JSONArray anteArray = dataJson.optJSONArray(GameConfigConstants.KEY_ANTE_INDEX);
                if (anteArray != null) {
                    int size = anteArray.length();
                    if (size != 0) {
                        int[] anteNum = new int[size];
                        for (int i = 0; i < size; i++) {
                            anteNum[i] = anteArray.optInt(i);
                        }
                        GameConfigData.setNormalAnte(anteNum);
//                        GameConstants.anteInts = anteNum;
                        LogUtil.i(TAG, "anteInts :" + anteNum.length);
                    }
                }
                //SNG 记分牌
                JSONArray sngChipsArray = dataJson.optJSONArray(GameConfigConstants.KEY_SNG_CHIPS);
                if (sngChipsArray != null) {
                    int size = sngChipsArray.length();
                    if (size != 0) {
                        int[] sngChipsNum = new int[size];
                        for (int i = 0; i < size; i++) {
                            sngChipsNum[i] = sngChipsArray.optInt(i);
                        }
                        GameConstants.sngChipsNum = sngChipsNum;
                        LogUtil.i(TAG, "sngChipsNum :" + sngChipsNum.length);
                    }
                }
                //SNG 涨盲时间
                JSONArray sngDurationArray = dataJson.optJSONArray(GameConfigConstants.KEY_SNG_DURATION);
                if (sngDurationArray != null) {
                    int size = sngDurationArray.length();
                    if (size != 0) {
                        int[] sngDurationNum = new int[size];
                        for (int i = 0; i < size; i++) {
                            sngDurationNum[i] = sngDurationArray.optInt(i);
                        }
                        GameConstants.sngTimeMinutes = sngDurationNum;
                        LogUtil.i(TAG, "sngTimeMinutes :" + sngDurationNum.length);
                    }
                }
                //SNG 报名费
                JSONArray sngCheckInArray = dataJson.optJSONArray(GameConfigConstants.KEY_SNG_CHECKIN_FEE);
                if (sngCheckInArray != null) {
                    int size = sngCheckInArray.length();
                    if (size != 0) {
                        int[] sngCheckInFeeNum = new int[size];
                        for (int i = 0; i < size; i++) {
                            sngCheckInFeeNum[i] = sngCheckInArray.optInt(i);
                        }
                        GameConstants.sngCheckInFeeNum = sngCheckInFeeNum;
                        LogUtil.i(TAG, "sngCheckInFeeNum :" + sngCheckInFeeNum.length);
                    }
                }

                //MTT初始记分牌
                JSONArray mttChipsArray = dataJson.optJSONArray(GameConfigConstants.KEY_MTT_CHIPS);
                if (mttChipsArray != null) {
                    int size = mttChipsArray.length();
                    if (size != 0) {
                        float[] nums = new float[size];
                        for (int i = 0; i < size; i++) {
                            nums[i] = (float) mttChipsArray.optDouble(i);
                        }
                        GameConstants.mttChipsNum = nums;
                        LogUtil.i(TAG, "mttChipsArray :" + nums.length);
                    }
                }

                //MTT报名费
                JSONArray mttCheckinFeeArray = dataJson.optJSONArray(GameConfigConstants.KEY_MTT_CHECKIN_FEE);
                if (mttCheckinFeeArray != null) {
                    int size = mttCheckinFeeArray.length();
                    if (size != 0) {
                        int[] nums = new int[size];
                        for (int i = 0; i < size; i++) {
                            nums[i] = mttCheckinFeeArray.optInt(i);
                        }
                        GameConstants.mttCheckInFeeNum = nums;
                        LogUtil.i(TAG, "mttCheckinFeeArray :" + nums.length);
                    }
                }
                //金币赛报名费  mtt_checkin_gold
                JSONArray mtt_checkin_gold = dataJson.optJSONArray("mtt_checkin_gold");
                if (mtt_checkin_gold != null) {
                    int size = mtt_checkin_gold.length();
                    if (size != 0) {
                        int[] nums = new int[size];
                        for (int i = 0; i < size; i++) {
                            nums[i] = mtt_checkin_gold.optInt(i);
                        }
                        GameConfigData.mtt_checkin_gold = nums;
                    }
                }
                //钻石赛报名费 mtt_diamonds
                JSONArray mtt_diamonds = dataJson.optJSONArray("mtt_checkin_diamond");
                if (mtt_diamonds != null) {
                    int size = mtt_diamonds.length();
                    if (size != 0) {
                        int[] nums = new int[size];
                        for (int i = 0; i < size; i++) {
                            nums[i] = mtt_diamonds.optInt(i);
                        }
                        GameConfigData.mtt_checkin_diamond = nums;
                    }
                }
                //MTT终止参赛盲注等级
                JSONArray mttBlindLevelArray = dataJson.optJSONArray(GameConfigConstants.KEY_MTT_BLIND_LEVEL);
                if (mttBlindLevelArray != null) {
                    int size = mttBlindLevelArray.length();
                    if (size != 0) {
                        int[] nums = new int[size];
                        for (int i = 0; i < size; i++) {
                            nums[i] = mttBlindLevelArray.optInt(i);
                        }
                        GameConstants.mttBlindLevelInts = nums;
                        LogUtil.i(TAG, "mttBlindLevelArray :" + nums.length);
                    }
                }
                //最小带入小档位个数   最大带入小档位个数   总带入小档位个数（默认10 11 21）
                JSONArray chipsIndexArray = dataJson.optJSONArray(GameConfigConstants.KEY_CHIPS_INDEX);
                if (chipsIndexArray != null && chipsIndexArray.length() > 2) {
                    GameConfigData.minChipsIndexNum = chipsIndexArray.optInt(0);
                    GameConfigData.maxChipsIndexNum = chipsIndexArray.optInt(1);
                    GameConfigData.totalChipsIndexNum = chipsIndexArray.optInt(2);
                }
                //前注ante二维数组
                JSONArray antesDoubleArray = dataJson.optJSONArray(GameConfigConstants.KEY_ANTE);
                GameConfigData.antes = new int[antesDoubleArray.length()][];
                for(int i = 0; i < antesDoubleArray.length(); i ++) {
                    JSONArray Array2 = antesDoubleArray.getJSONArray(i);      //获取一维数组
                    GameConfigData.antes[i] = new int[Array2.length()];
                    for(int j = 0; j < Array2.length(); j ++)
                        GameConfigData.antes[i][j] = Array2.getInt(j);              //获取一维数组中的数据
                }
                //猎人赛和超级猎人赛的赏金比例
                JSONArray ko_reward_rate = dataJson.optJSONArray(GameConfigConstants.KO_REWARD_RATE);
                if (ko_reward_rate != null) {
                    int size = ko_reward_rate.length();
                    if (size > 0) {
                        int[] nums = new int[size];
                        for (int i = 0; i < size; i++) {
                            nums[i] = ko_reward_rate.optInt(i);
                        }
                        GameConfigData.ko_reward_rate = nums;
                    }
                }
                JSONArray ko_head_rate = dataJson.optJSONArray(GameConfigConstants.KO_HEAD_RATE);
                if (ko_head_rate != null) {
                    int size = ko_head_rate.length();
                    if (size > 0) {
                        int[] nums = new int[size];
                        for (int i = 0; i < size; i++) {
                            nums[i] = ko_head_rate.optInt(i);
                        }
                        GameConfigData.ko_head_rate = nums;
                    }
                }
                //共享部落的钻石消耗
                JSONArray create_game_fee = dataJson.optJSONArray("create_game_fee");
                if (create_game_fee != null) {
                    int size = create_game_fee.length();
                    if (size > 0) {
                        int[] nums = new int[size];
                        for (int i = 0; i < size; i++) {
                            nums[i] = create_game_fee.optInt(i);
                        }
                        GameConfigData.create_game_fee = nums;
                    }
                }
                JSONArray create_sng_fee = dataJson.optJSONArray("create_sng_fee");
                if (create_sng_fee != null) {
                    int size = create_sng_fee.length();
                    if (size > 0) {
                        int[] nums = new int[size];
                        for (int i = 0; i < size; i++) {
                            nums[i] = create_sng_fee.optInt(i);
                        }
                        GameConfigData.create_sng_fee = nums;
                    }
                }
                JSONArray create_mtt_fee = dataJson.optJSONArray("create_mtt_fee");
                if (create_mtt_fee != null) {
                    int size = create_mtt_fee.length();
                    if (size > 0) {
                        int[] nums = new int[size];
                        for (int i = 0; i < size; i++) {
                            nums[i] = create_mtt_fee.optInt(i);
                        }
                        GameConfigData.create_mtt_fee = nums;
                    }
                }
                //保存版本号
                if (newVersion != 0) {
                    GamePreferences.getInstance(DemoCache.getContext()).setConfigVersion(newVersion);
                    GamePreferences.getInstance(DemoCache.getContext()).setGameConfigData(data);
                }
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 处理奥马哈的游戏配置
     * @param data
     * @return
     */
    public static boolean dealGameConfigOmaha(String data) {
        if (StringUtil.isSpace(data)) {
            return false;
        }
        LogUtil.i(TAG + " json omaha:", data.substring(0, data.length() / 2));
        LogUtil.i(TAG + " json omaha:", data.substring(data.length() / 2, data.length()));//data太长，log显示不全，分开显示
        try {
            JSONObject dataJson = new JSONObject(data);
            if (dataJson != null) {
                int newVersion = dataJson.optInt("ver");
                //sng普通的盲注结构表   3个数组  快速也是3个数组总共6个      mtt也是总共6个数组(普通&快速个3个)  sng_ante_multiple   sng_sblinds_multiple  sng_sblins
                JSONArray sng_sblinds_multiple = dataJson.optJSONArray(GameConfigConstants.KEY_SNG_SBLINDS_MULTIPLE);
                if (sng_sblinds_multiple != null) {
                    int size = sng_sblinds_multiple.length();
                    if (size > 0) {
                        GameConfigData.omaha_sng_sblinds_multiple = new float[size];
                        for (int i = 0; i < size; i++) {
                            GameConfigData.omaha_sng_sblinds_multiple[i] = (float) sng_sblinds_multiple.optDouble(i);
                        }
                    }
                }
                JSONArray sng_sblins = dataJson.optJSONArray(GameConfigConstants.KEY_SNG_SBLINS);
                if (sng_sblins != null) {
                    int size = sng_sblins.length();
                    if (size > 0) {
                        GameConfigData.omaha_sng_sblins = new int[size];
                        for (int i = 0; i < size; i++) {
                            GameConfigData.omaha_sng_sblins[i] = sng_sblins.optInt(i);
                        }
                    }
                }
                JSONArray sng_ante_multiple = dataJson.optJSONArray(GameConfigConstants.KEY_SNG_ANTE_MULTIPLE);
                if (sng_ante_multiple != null) {
                    int size = sng_ante_multiple.length();
                    if (size > 0) {
                        GameConfigData.omaha_sng_ante_multiple = new float[size];
                        for (int i = 0; i < size; i++) {
                            GameConfigData.omaha_sng_ante_multiple[i] = (float) sng_ante_multiple.optDouble(i);
                        }
                    }
                }
                //mtt普通的盲注结构表   3个数组  快速也是3个数组总共6个      mtt也是总共6个数组(普通&快速个3个)  mtt_ante_multiple   mtt_sblinds_multiple  mtt_sblins
                JSONArray mtt_sblinds_multiple = dataJson.optJSONArray(GameConfigConstants.KEY_MTT_SBLINDS_MULTIPLE);
                if (mtt_sblinds_multiple != null) {
                    int size = mtt_sblinds_multiple.length();
                    if (size > 0) {
                        GameConfigData.omaha_mtt_sblinds_multiple = new float[size];
                        for (int i = 0; i < size; i++) {
                            GameConfigData.omaha_mtt_sblinds_multiple[i] = (float) mtt_sblinds_multiple.optDouble(i);
                        }
                    }
                }
                JSONArray mtt_sblins = dataJson.optJSONArray(GameConfigConstants.KEY_MTT_SBLINS);
                if (mtt_sblins != null) {
                    int size = mtt_sblins.length();
                    if (size > 0) {
                        GameConfigData.omaha_mtt_sblins = new int[size];
                        for (int i = 0; i < size; i++) {
                            GameConfigData.omaha_mtt_sblins[i] = mtt_sblins.optInt(i);
                        }
                    }
                }
                JSONArray mtt_ante_multiple = dataJson.optJSONArray(GameConfigConstants.KEY_MTT_ANTE_MULTIPLE);
                if (mtt_ante_multiple != null) {
                    int size = mtt_ante_multiple.length();
                    if (size > 0) {
                        GameConfigData.omaha_mtt_ante_multiple = new float[size];
                        for (int i = 0; i < size; i++) {
                            GameConfigData.omaha_mtt_ante_multiple[i] = (float) mtt_ante_multiple.optDouble(i);
                        }
                    }
                }
                //mtt快速表,涉及到三个数组
                JSONArray mtt_sblinds_multiple_quick = dataJson.optJSONArray(GameConfigConstants.KEY_MTT_SBLINDS_MULTIPLE_QUICK);
                if (mtt_sblinds_multiple_quick != null) {
                    int size = mtt_sblinds_multiple_quick.length();
                    if (size > 0) {
                        GameConfigData.omaha_mtt_sblinds_multiple_quick = new float[size];
                        for (int i = 0; i < size; i++) {
                            GameConfigData.omaha_mtt_sblinds_multiple_quick[i] = (float) mtt_sblinds_multiple_quick.optDouble(i);
                        }
                    }
                }
                JSONArray mtt_sblins_quick = dataJson.optJSONArray(GameConfigConstants.KEY_MTT_SBLINS_QUICK);
                if (mtt_sblins_quick != null) {
                    int size = mtt_sblins_quick.length();
                    if (size > 0) {
                        GameConfigData.omaha_mtt_sblins_quick = new int[size];
                        for (int i = 0; i < size; i++) {
                            GameConfigData.omaha_mtt_sblins_quick[i] = mtt_sblins_quick.optInt(i);
                        }
                    }
                }
                JSONArray mtt_ante_multiple_quick = dataJson.optJSONArray(GameConfigConstants.KEY_MTT_ANTE_MULTIPLE_QUICK);
                if (mtt_ante_multiple_quick != null) {
                    int size = mtt_ante_multiple_quick.length();
                    if (size > 0) {
                        GameConfigData.omaha_mtt_ante_multiple_quick = new float[size];
                        for (int i = 0; i < size; i++) {
                            GameConfigData.omaha_mtt_ante_multiple_quick[i] = (float) mtt_ante_multiple_quick.optDouble(i);
                        }
                    }
                }
                //保存版本号
                if (newVersion != 0) {
                    GamePreferences.getInstance(DemoCache.getContext()).setConfigVersionOmaha(newVersion);
                    GamePreferences.getInstance(DemoCache.getContext()).setGameConfigDataOmaha(data);
                }
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean dealGameConfigPineapple(String data) {
        if (StringUtil.isSpace(data)) {
            return false;
        }
        LogUtil.i(TAG + " json pineapple:", data.substring(0, data.length() / 2));
        LogUtil.i(TAG + " json pineapple:", data.substring(data.length() / 2, data.length()));//data太长，log显示不全，分开显示
        try {
            JSONObject dataJson = new JSONObject(data);
            if (dataJson != null) {
                int newVersion = dataJson.optInt("ver");
                //sng普通的盲注结构表   3个数组  快速也是3个数组总共6个      mtt也是总共6个数组(普通&快速个3个)  sng_ante_multiple   sng_sblinds_multiple  sng_sblins
                JSONArray pineapple_antes = dataJson.optJSONArray("pineapple_antes");
                if (pineapple_antes != null) {
                    GameConfigData.pineapple_antes = new int[pineapple_antes.length()];
                    for (int i = 0; i < pineapple_antes.length(); i++) {
                        GameConfigData.pineapple_antes[i] = pineapple_antes.optInt(i);
                    }
                }
                JSONArray pineapple_chips = dataJson.optJSONArray("pineapple_chips");
                if (pineapple_chips != null) {
                    GameConfigData.pineapple_chips = new int[pineapple_chips.length()][];
                    for (int i = 0; i < pineapple_chips.length(); i++) {
                        JSONArray itemArray = pineapple_chips.optJSONArray(i);
                        if (itemArray != null) {
                            GameConfigData.pineapple_chips[i] = new int[itemArray.length()];
                            for (int j = 0; j < itemArray.length(); j++) {
                                GameConfigData.pineapple_chips[i][j] = itemArray.optInt(j);
                            }
                        }
                    }
                }
                JSONArray pineapple_chips_limit_multiple = dataJson.optJSONArray("pineapple_chips_limit_multiple");
                if (pineapple_chips_limit_multiple != null) {
                    GameConfigData.pineapple_chips_limit_multiple = new int[pineapple_chips_limit_multiple.length()];
                    for (int i = 0; i < pineapple_chips_limit_multiple.length(); i++) {
                        GameConfigData.pineapple_chips_limit_multiple[i] = pineapple_chips_limit_multiple.optInt(i);
                    }
                }
                JSONArray pineapple_durations = dataJson.optJSONArray("pineapple_durations");
                if (pineapple_durations != null) {
                    GameConfigData.pineapple_durations = new int[pineapple_durations.length()];
                    for (int i = 0; i < pineapple_durations.length(); i++) {
                        GameConfigData.pineapple_durations[i] = (int) ((pineapple_durations.optDouble(i)));
                    }
                }
                //共享部落的钻石消耗
                JSONArray pineapple_fee = dataJson.optJSONArray("pineapple_fee");
                if (pineapple_fee != null) {
                    int size = pineapple_fee.length();
                    if (size > 0) {
                        int[] nums = new int[size];
                        for (int i = 0; i < size; i++) {
                            nums[i] = pineapple_fee.optInt(i);
                        }
                        GameConfigData.pineapple_fee = nums;
                    }
                }
                //常规赛报名费  pineapple_mtt_fees
                JSONArray pineapple_mtt_fees = dataJson.optJSONArray("pineapple_mtt_fees");
                if (pineapple_mtt_fees != null) {
                    int size = pineapple_mtt_fees.length();
                    if (size > 0) {
                        int[] nums = new int[size];
                        for (int i = 0; i < size; i++) {
                            nums[i] = pineapple_mtt_fees.optInt(i);
                        }
                        GameConfigData.pineapple_mtt_fees = nums;
                    }
                }
                //初始记分牌  pineapple_mtt_chips
                JSONArray pineapple_mtt_chips = dataJson.optJSONArray("pineapple_mtt_chips");
                if (pineapple_mtt_chips != null) {
                    int size = pineapple_mtt_chips.length();
                    if (size > 0) {
                        int[] nums = new int[size];
                        for (int i = 0; i < size; i++) {
                            nums[i] = pineapple_mtt_chips.optInt(i);
                        }
                        GameConfigData.pineapple_mtt_chips = nums;
                    }
                }
                //升底时间 pineapple_mtt_ante_time
                JSONArray pineapple_mtt_ante_time = dataJson.optJSONArray("pineapple_mtt_ante_time");
                if (pineapple_mtt_ante_time != null) {
                    int size = pineapple_mtt_ante_time.length();
                    if (size > 0) {
                        int[] nums = new int[size];
                        for (int i = 0; i < size; i++) {
                            nums[i] = (int) (pineapple_mtt_ante_time.optInt(i) / 60f);//升底时间 服务端返回的单位是秒s
                        }
                        GameConfigData.pineapple_mtt_ante_time = nums;
                    }
                }
                //大菠萝买入次数上限 pineapple_mtt_checkin_limit
                JSONArray pineapple_mtt_checkin_limit = dataJson.optJSONArray("pineapple_mtt_checkin_limit");
                if (pineapple_mtt_checkin_limit != null) {
                    int size = pineapple_mtt_checkin_limit.length();
                    if (size > 0) {
                        int[] nums = new int[size];
                        for (int i = 0; i < size; i++) {
                            nums[i] = pineapple_mtt_checkin_limit.optInt(i);
                        }
                        GameConfigData.pineapple_mtt_checkin_limit = nums;
                    }
                }
                //终止报名级别  pineapple_mtt_blind_level
                JSONArray pineapple_mtt_blind_level = dataJson.optJSONArray("pineapple_mtt_blind_level");
                if (pineapple_mtt_blind_level != null) {
                    int size = pineapple_mtt_blind_level.length();
                    if (size > 0) {
                        int[] nums = new int[size];
                        for (int i = 0; i < size; i++) {
                            nums[i] = pineapple_mtt_blind_level.optInt(i);
                        }
                        GameConfigData.pineapple_mtt_blind_level = nums;
                    }
                }
                //mtt大菠萝普通底注表 pineapple_mtt_ante
                JSONArray pineapple_mtt_ante = dataJson.optJSONArray("pineapple_mtt_ante");
                if (pineapple_mtt_ante != null) {
                    int size = pineapple_mtt_ante.length();
                    if (size > 0) {
                        int[] nums = new int[size];
                        for (int i = 0; i < size; i++) {
                            nums[i] = pineapple_mtt_ante.optInt(i);
                        }
                        GameConfigData.pineapple_mtt_ante = nums;
                    }
                }
                //mtt大菠萝快速底注表 pineapple_mtt_ante_quick
                JSONArray pineapple_mtt_ante_quick = dataJson.optJSONArray("pineapple_mtt_ante_quick");
                if (pineapple_mtt_ante_quick != null) {
                    int size = pineapple_mtt_ante_quick.length();
                    if (size > 0) {
                        int[] nums = new int[size];
                        for (int i = 0; i < size; i++) {
                            nums[i] = pineapple_mtt_ante_quick.optInt(i);
                        }
                        GameConfigData.pineapple_mtt_ante_quick = nums;
                    }
                }
                //保存版本号
                if (newVersion != 0) {
                    GamePreferences.getInstance(DemoCache.getContext()).setConfigVersionPineapple(newVersion);
                    GamePreferences.getInstance(DemoCache.getContext()).setGameConfigDataPineapple(data);
                }
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
