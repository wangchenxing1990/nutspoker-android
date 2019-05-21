package com.netease.nim.uikit.bean;

/**
 * Created by Administrator on 2016/7/31.
 */
public class MatchEntity {

    public int games_mtt;//mtt参赛总场数
    public int games_sng;//sng参赛总场数
    public int games_sng_mtt;//mt－sng参赛总场数
    public int match_fee_mtt;//mtt参赛费
    public int match_fee_sng;//sng参赛费
    public int match_fee_sng_mtt;//mt－sng参赛费
    //盈利＝奖金－参赛费
    public int reward_mtt;//mtt 奖金
    public int reward_sng;//sng 奖金
    public int reward_sng_mtt;//mt－sng 奖金
    public int in_reward_mtt;//mtt进入奖励圈场数
    public int in_reward_sng;//sng进入奖励圈场数
    public int in_reward_sng_mtt;//mtt－sng进入奖励圈场数
    public int in_finals_mtt;//mtt 决赛桌
    public int in_finals_sng;//sng 决赛桌
    public int in_finals_sng_mtt;//mt－sng 决赛桌

}
