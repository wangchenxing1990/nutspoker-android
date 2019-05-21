package com.netease.nim.uikit.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 牌谱相关参数
 */
public class PaipuEntity implements Serializable {

    public List<Integer> poolCards;//地池

    public int winChip;
    public int cardType;//牌型
    public List<Integer> cardTypeCards;//我的牌型
    public List<Integer> handCards;//手牌
    public int handsCnt;//第几手牌
    public String handsId;
    public boolean isCollect;

    public GameEntity gameEntity;
    public String node;//节点

    public String jsonDataStr;//json格式的数据

    public String fileName;
    public String fileNetPath;
    public String fileLocalPath;
    public String sheetUid;
    public int collectCount;//收藏的牌局数量
    public long collectTime;//收藏时间
}