package com.htgames.nutspoker.game.match.reward;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 奖励圈分配
 */
public class RewardAllot {

    static RewardAllot sInstance = new RewardAllot();

    Map<Integer,List<Float>> sMap = new HashMap<>();

    public static RewardAllot getInstance(){
        return sInstance;
    }

    String[] mRank = {
            "1", "2", "3", "4", "5",
            "6", "7-9", "10-12", "13-18", "19-24",
            "25-30", "31-36", "37-42", "43-48", "49-54",
            "55-60", "61-66", "67-72", "73-78", "79-84",
            "85-90", "91-96", "97-102", "103-108", "109-114",
            "115-120", "121-132", "133-144", "145-156", "157-168",
            "169-180", "181-192"
    };

    int[] mRewardCount = {
            1, 2, 3, 4, 5,
            6, 9, 12, 18, 24,
            30, 36, 42, 48, 54,
            60, 66, 72, 78, 84,
            90, 96, 102, 108, 114,
            120, 132, 144, 156, 168,
            180, 192
    };

    public String getRewardRank(int index) {
        if (index >= 0 && index <= 32)
            return mRank[index];
        return "";
    }

    //获取奖励圈人数
    public int getRewardPlayerCount(int playerCount) {
        int rewardPlayerCount = 0;
        try {
            int index = getListSize(playerCount) - 1;
            rewardPlayerCount = mRewardCount[index];
        } catch (Exception e) {
        }
        return rewardPlayerCount;
    }

    //获取指定位置的百分比
    public float getRewardPercent(int index,int count) {
        if (count < 1)
            return 0.0f;
        int listSize = getListSize(count);
        float ret = 0.0f;
        try {
            ret = sMap.get(listSize).get(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /***
     * @param count 游戏参与人数
     * @return 队列容量
     */
    public static int getListSize(int count) {
        if (count < 1)
            return 0;
        else if (count >= 1 && count <= 6)
            return 1;
        else if (count >= 7 && count <= 11)
            return 2;
        else if (count >= 12 && count <= 17)
            return 3;
        else if (count >= 18 && count <= 22)
            return 4;
        else if (count >= 23 && count <= 28)
            return 5;
        else if (count >= 29 && count <= 33)
            return 6;
        else if (count >= 34 && count <= 50)
            return 7;
        else if (count >= 51 && count <= 66)
            return 8;
        else if (count >= 67 && count <= 99)
            return 9;
        else if (count >= 100 && count <= 132)
            return 10;
        else if (count >= 133 && count <= 165)
            return 11;
        else if (count >= 166 && count <= 198)
            return 12;
        else if (count >= 199 && count <= 231)
            return 13;
        else if (count >= 232 && count <= 264)
            return 14;
        else if (count >= 265 && count <= 297)
            return 15;
        else if (count >= 298 && count <= 330)
            return 16;
        else if (count >= 331 && count <= 363)
            return 17;
        else if (count >= 364 && count <= 396)
            return 18;
        else if (count >= 397 && count <= 429)
            return 19;
        else if (count >= 430 && count <= 462)
            return 20;
        else if (count >= 463 && count <= 495)
            return 21;
        else if (count >= 496 && count <= 528)
            return 22;
        else if (count >= 529 && count <= 561)
            return 23;
        else if (count >= 562 && count <= 594)
            return 24;
        else if (count >= 595 && count <= 627)
            return 25;
        else if (count >= 628 && count <= 660)
            return 26;
        else if (count >= 661 && count <= 726)
            return 27;
        else if (count >= 727 && count <= 792)
            return 28;
        else if (count >= 793 && count <= 858)
            return 29;
        else if (count >= 859 && count <= 924)
            return 30;
        else if (count >= 925 && count <= 990)
            return 31;
        else if (count >= 991 && count <= 1056)
            return 32;
        else
            return 32;
    }

    private RewardAllot(){
        List<Float> tmpList = new ArrayList<>();
        tmpList.add(100.000f);
        sMap.put(1,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(65.000f);
        tmpList.add(35.000f);
        sMap.put(2,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(50.000f);
        tmpList.add(30.000f);
        tmpList.add(20.000f);
        sMap.put(3,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(45.000f);
        tmpList.add(30.000f);
        tmpList.add(15.000f);
        tmpList.add(10.000f);
        sMap.put(4,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(40.000f);
        tmpList.add(27.000f);
        tmpList.add(15.000f);
        tmpList.add(10.000f);
        tmpList.add(8.000f);
        sMap.put(5,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(37.000f);
        tmpList.add(25.000f);
        tmpList.add(15.000f);
        tmpList.add(10.000f);
        tmpList.add(7.500f);
        tmpList.add(5.500f);
        sMap.put(6,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(30.000f);
        tmpList.add(20.000f);
        tmpList.add(15.000f);
        tmpList.add(10.000f);
        tmpList.add(7.500f);
        tmpList.add(5.500f);
        tmpList.add(4.000f);
        sMap.put(7,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(27.250f);
        tmpList.add(18.250f);
        tmpList.add(14.000f);
        tmpList.add(9.500f);
        tmpList.add(7.000f);
        tmpList.add(5.250f);
        tmpList.add(3.750f);
        tmpList.add(2.500f);
        sMap.put(8,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(26.000f);
        tmpList.add(17.500f);
        tmpList.add(13.000f);
        tmpList.add(8.500f);
        tmpList.add(6.500f);
        tmpList.add(4.500f);
        tmpList.add(3.250f);
        tmpList.add(2.150f);
        tmpList.add(1.300f);
        sMap.put(9,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(24.00f);
        tmpList.add(16.550f);
        tmpList.add(12.500f);
        tmpList.add(8.500f);
        tmpList.add(6.500f);
        tmpList.add(4.500f);
        tmpList.add(3.000f);
        tmpList.add(1.750f);
        tmpList.add(1.250f);
        tmpList.add(0.950f);
        sMap.put(10,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(23.000f);
        tmpList.add(16.000f);
        tmpList.add(12.000f);
        tmpList.add(8.000f);
        tmpList.add(6.000f);
        tmpList.add(4.100f);
        tmpList.add(2.850f);
        tmpList.add(1.750f);
        tmpList.add(1.150f);
        tmpList.add(0.900f);
        tmpList.add(0.800f);
        sMap.put(11,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(22.000f);
        tmpList.add(15.350f);
        tmpList.add(11.500f);
        tmpList.add(8.000f);
        tmpList.add(6.000f);
        tmpList.add(4.000f);
        tmpList.add(2.700f);
        tmpList.add(1.550f);
        tmpList.add(1.100f);
        tmpList.add(0.900f);
        tmpList.add(0.750f);
        tmpList.add(0.650f);
        sMap.put(12,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(21.000f);
        tmpList.add(15.300f);
        tmpList.add(11.500f);
        tmpList.add(8.000f);
        tmpList.add(6.000f);
        tmpList.add(4.000f);
        tmpList.add(2.500f);
        tmpList.add(1.400f);
        tmpList.add(1.050f);
        tmpList.add(0.850f);
        tmpList.add(0.700f);
        tmpList.add(0.600f);
        tmpList.add(0.550f);
        sMap.put(13,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(20.500f);
        tmpList.add(15.000f);
        tmpList.add(11.250f);
        tmpList.add(7.750f);
        tmpList.add(5.750f);
        tmpList.add(3.750f);
        tmpList.add(2.300f);
        tmpList.add(1.400f);
        tmpList.add(1.000f);
        tmpList.add(0.800f);
        tmpList.add(0.700f);
        tmpList.add(0.600f);
        tmpList.add(0.550f);
        tmpList.add(0.500f);
        sMap.put(14,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(20.000f);
        tmpList.add(14.800f);
        tmpList.add(11.000f);
        tmpList.add(7.600f);
        tmpList.add(5.550f);
        tmpList.add(3.550f);
        tmpList.add(2.200f);
        tmpList.add(1.400f);
        tmpList.add(0.950f);
        tmpList.add(0.750f);
        tmpList.add(0.650f);
        tmpList.add(0.600f);
        tmpList.add(0.550f);
        tmpList.add(0.500f);
        tmpList.add(0.450f);
        sMap.put(15,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(19.350f);
        tmpList.add(14.350f);
        tmpList.add(10.750f);
        tmpList.add(7.400f);
        tmpList.add(5.300f);
        tmpList.add(3.400f);
        tmpList.add(2.100f);
        tmpList.add(1.350f);
        tmpList.add(0.950f);
        tmpList.add(0.750f);
        tmpList.add(0.650f);
        tmpList.add(0.600f);
        tmpList.add(0.550f);
        tmpList.add(0.500f);
        tmpList.add(0.450f);
        tmpList.add(0.400f);
        sMap.put(16,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(19.000f);
        tmpList.add(14.000f);
        tmpList.add(10.250f);
        tmpList.add(7.000f);
        tmpList.add(5.000f);
        tmpList.add(3.200f);
        tmpList.add(2.100f);
        tmpList.add(1.350f);
        tmpList.add(0.950f);
        tmpList.add(0.750f);
        tmpList.add(0.650f);
        tmpList.add(0.600f);
        tmpList.add(0.550f);
        tmpList.add(0.500f);
        tmpList.add(0.450f);
        tmpList.add(0.400f);
        tmpList.add(0.350f);
        sMap.put(17,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(19.000f);
        tmpList.add(14.000f);
        tmpList.add(10.250f);
        tmpList.add(7.000f);
        tmpList.add(5.000f);
        tmpList.add(3.200f);
        tmpList.add(2.100f);
        tmpList.add(1.350f);
        tmpList.add(0.930f);
        tmpList.add(0.700f);
        tmpList.add(0.600f);
        tmpList.add(0.550f);
        tmpList.add(0.500f);
        tmpList.add(0.460f);
        tmpList.add(0.420f);
        tmpList.add(0.380f);
        tmpList.add(0.340f);
        tmpList.add(0.320f);
        sMap.put(18,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(19.000f);
        tmpList.add(14.000f);
        tmpList.add(10.250f);
        tmpList.add(7.000f);
        tmpList.add(5.000f);
        tmpList.add(3.110f);
        tmpList.add(2.090f);
        tmpList.add(1.350f);
        tmpList.add(0.930f);
        tmpList.add(0.700f);
        tmpList.add(0.600f);
        tmpList.add(0.520f);
        tmpList.add(0.460f);
        tmpList.add(0.400f);
        tmpList.add(0.360f);
        tmpList.add(0.340f);
        tmpList.add(0.320f);
        tmpList.add(0.300f);
        tmpList.add(0.290f);
        sMap.put(19,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(18.500f);
        tmpList.add(13.800f);
        tmpList.add(10.250f);
        tmpList.add(7.000f);
        tmpList.add(5.000f);
        tmpList.add(3.000f);
        tmpList.add(2.050f);
        tmpList.add(1.300f);
        tmpList.add(0.900f);
        tmpList.add(0.700f);
        tmpList.add(0.590f);
        tmpList.add(0.520f);
        tmpList.add(0.460f);
        tmpList.add(0.400f);
        tmpList.add(0.350f);
        tmpList.add(0.330f);
        tmpList.add(0.310f);
        tmpList.add(0.290f);
        tmpList.add(0.280f);
        tmpList.add(0.270f);
        sMap.put(20,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(18.000f);
        tmpList.add(13.500f);
        tmpList.add(10.000f);
        tmpList.add(7.000f);
        tmpList.add(5.000f);
        tmpList.add(3.000f);
        tmpList.add(2.040f);
        tmpList.add(1.300f);
        tmpList.add(0.900f);
        tmpList.add(0.700f);
        tmpList.add(0.580f);
        tmpList.add(0.500f);
        tmpList.add(0.430f);
        tmpList.add(0.380f);
        tmpList.add(0.350f);
        tmpList.add(0.330f);
        tmpList.add(0.310f);
        tmpList.add(0.290f);
        tmpList.add(0.280f);
        tmpList.add(0.270f);
        tmpList.add(0.260f);
        sMap.put(21,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(17.750f);
        tmpList.add(13.150f);
        tmpList.add(10.000f);
        tmpList.add(7.000f);
        tmpList.add(5.000f);
        tmpList.add(3.000f);
        tmpList.add(2.000f);
        tmpList.add(1.300f);
        tmpList.add(0.890f);
        tmpList.add(0.690f);
        tmpList.add(0.560f);
        tmpList.add(0.480f);
        tmpList.add(0.410f);
        tmpList.add(0.360f);
        tmpList.add(0.340f);
        tmpList.add(0.320f);
        tmpList.add(0.300f);
        tmpList.add(0.290f);
        tmpList.add(0.280f);
        tmpList.add(0.270f);
        tmpList.add(0.260f);
        tmpList.add(0.250f);
        sMap.put(22,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(17.630f);
        tmpList.add(13.000f);
        tmpList.add(10.000f);
        tmpList.add(7.000f);
        tmpList.add(5.000f);
        tmpList.add(3.000f);
        tmpList.add(1.950f);
        tmpList.add(1.300f);
        tmpList.add(0.870f);
        tmpList.add(0.670f);
        tmpList.add(0.550f);
        tmpList.add(0.470f);
        tmpList.add(0.400f);
        tmpList.add(0.350f);
        tmpList.add(0.330f);
        tmpList.add(0.310f);
        tmpList.add(0.290f);
        tmpList.add(0.280f);
        tmpList.add(0.270f);
        tmpList.add(0.260f);
        tmpList.add(0.250f);
        tmpList.add(0.240f);
        tmpList.add(0.230f);
        sMap.put(23,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(17.630f);
        tmpList.add(13.000f);
        tmpList.add(10.000f);
        tmpList.add(7.000f);
        tmpList.add(5.000f);
        tmpList.add(3.000f);
        tmpList.add(1.910f);
        tmpList.add(1.300f);
        tmpList.add(0.860f);
        tmpList.add(0.650f);
        tmpList.add(0.530f);
        tmpList.add(0.450f);
        tmpList.add(0.380f);
        tmpList.add(0.340f);
        tmpList.add(0.320f);
        tmpList.add(0.300f);
        tmpList.add(0.280f);
        tmpList.add(0.270f);
        tmpList.add(0.260f);
        tmpList.add(0.250f);
        tmpList.add(0.240f);
        tmpList.add(0.230f);
        tmpList.add(0.220f);
        tmpList.add(0.210f);
        sMap.put(24,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(17.500f);
        tmpList.add(12.750f);
        tmpList.add(9.740f);
        tmpList.add(6.750f);
        tmpList.add(4.750f);
        tmpList.add(3.000f);
        tmpList.add(1.910f);
        tmpList.add(1.300f);
        tmpList.add(0.850f);
        tmpList.add(0.650f);
        tmpList.add(0.530f);
        tmpList.add(0.450f);
        tmpList.add(0.380f);
        tmpList.add(0.340f);
        tmpList.add(0.320f);
        tmpList.add(0.300f);
        tmpList.add(0.280f);
        tmpList.add(0.270f);
        tmpList.add(0.260f);
        tmpList.add(0.250f);
        tmpList.add(0.240f);
        tmpList.add(0.230f);
        tmpList.add(0.220f);
        tmpList.add(0.210f);
        tmpList.add(0.200f);
        sMap.put(25,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(17.000f);
        tmpList.add(12.750f);
        tmpList.add(9.700f);
        tmpList.add(6.750f);
        tmpList.add(4.750f);
        tmpList.add(3.000f);
        tmpList.add(1.850f);
        tmpList.add(1.300f);
        tmpList.add(0.850f);
        tmpList.add(0.640f);
        tmpList.add(0.520f);
        tmpList.add(0.440f);
        tmpList.add(0.370f);
        tmpList.add(0.330f);
        tmpList.add(0.310f);
        tmpList.add(0.290f);
        tmpList.add(0.280f);
        tmpList.add(0.270f);
        tmpList.add(0.260f);
        tmpList.add(0.250f);
        tmpList.add(0.240f);
        tmpList.add(0.230f);
        tmpList.add(0.220f);
        tmpList.add(0.210f);
        tmpList.add(0.200f);
        tmpList.add(0.190f);
        sMap.put(26,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(16.750f);
        tmpList.add(12.500f);
        tmpList.add(9.500f);
        tmpList.add(6.500f);
        tmpList.add(4.500f);
        tmpList.add(3.000f);
        tmpList.add(1.800f);
        tmpList.add(1.260f);
        tmpList.add(0.850f);
        tmpList.add(0.640f);
        tmpList.add(0.510f);
        tmpList.add(0.430f);
        tmpList.add(0.360f);
        tmpList.add(0.320f);
        tmpList.add(0.300f);
        tmpList.add(0.285f);
        tmpList.add(0.275f);
        tmpList.add(0.265f);
        tmpList.add(0.255f);
        tmpList.add(0.245f);
        tmpList.add(0.235f);
        tmpList.add(0.225f);
        tmpList.add(0.215f);
        tmpList.add(0.205f);
        tmpList.add(0.195f);
        tmpList.add(0.185f);
        tmpList.add(0.175f);
        sMap.put(27,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(16.750f);
        tmpList.add(12.500f);
        tmpList.add(9.500f);
        tmpList.add(6.500f);
        tmpList.add(4.290f);
        tmpList.add(3.000f);
        tmpList.add(1.750f);
        tmpList.add(1.250f);
        tmpList.add(0.840f);
        tmpList.add(0.630f);
        tmpList.add(0.500f);
        tmpList.add(0.420f);
        tmpList.add(0.350f);
        tmpList.add(0.310f);
        tmpList.add(0.290f);
        tmpList.add(0.270f);
        tmpList.add(0.260f);
        tmpList.add(0.250f);
        tmpList.add(0.240f);
        tmpList.add(0.230f);
        tmpList.add(0.220f);
        tmpList.add(0.210f);
        tmpList.add(0.200f);
        tmpList.add(0.190f);
        tmpList.add(0.180f);
        tmpList.add(0.170f);
        tmpList.add(0.165f);
        tmpList.add(0.160f);
        sMap.put(28,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(16.750f);
        tmpList.add(12.500f);
        tmpList.add(9.500f);
        tmpList.add(6.500f);
        tmpList.add(4.260f);
        tmpList.add(3.000f);
        tmpList.add(1.750f);
        tmpList.add(1.250f);
        tmpList.add(0.840f);
        tmpList.add(0.630f);
        tmpList.add(0.500f);
        tmpList.add(0.420f);
        tmpList.add(0.340f);
        tmpList.add(0.300f);
        tmpList.add(0.270f);
        tmpList.add(0.250f);
        tmpList.add(0.240f);
        tmpList.add(0.230f);
        tmpList.add(0.220f);
        tmpList.add(0.210f);
        tmpList.add(0.200f);
        tmpList.add(0.190f);
        tmpList.add(0.180f);
        tmpList.add(0.170f);
        tmpList.add(0.165f);
        tmpList.add(0.160f);
        tmpList.add(0.155f);
        tmpList.add(0.150f);
        tmpList.add(0.145f);
        sMap.put(29,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(16.750f);
        tmpList.add(12.500f);
        tmpList.add(9.450f);
        tmpList.add(6.450f);
        tmpList.add(4.220f);
        tmpList.add(2.750f);
        tmpList.add(1.700f);
        tmpList.add(1.150f);
        tmpList.add(0.825f);
        tmpList.add(0.625f);
        tmpList.add(0.495f);
        tmpList.add(0.415f);
        tmpList.add(0.335f);
        tmpList.add(0.295f);
        tmpList.add(0.265f);
        tmpList.add(0.245f);
        tmpList.add(0.235f);
        tmpList.add(0.225f);
        tmpList.add(0.215f);
        tmpList.add(0.205f);
        tmpList.add(0.195f);
        tmpList.add(0.185f);
        tmpList.add(0.175f);
        tmpList.add(0.165f);
        tmpList.add(0.160f);
        tmpList.add(0.155f);
        tmpList.add(0.150f);
        tmpList.add(0.145f);
        tmpList.add(0.140f);
        tmpList.add(0.135f);
        sMap.put(30,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(16.500f);
        tmpList.add(12.400f);
        tmpList.add(9.250f);
        tmpList.add(6.400f);
        tmpList.add(4.100f);
        tmpList.add(2.750f);
        tmpList.add(1.600f);
        tmpList.add(1.050f);
        tmpList.add(0.815f);
        tmpList.add(0.610f);
        tmpList.add(0.490f);
        tmpList.add(0.405f);
        tmpList.add(0.335f);
        tmpList.add(0.295f);
        tmpList.add(0.265f);
        tmpList.add(0.245f);
        tmpList.add(0.235f);
        tmpList.add(0.225f);
        tmpList.add(0.215f);
        tmpList.add(0.205f);
        tmpList.add(0.195f);
        tmpList.add(0.185f);
        tmpList.add(0.175f);
        tmpList.add(0.165f);
        tmpList.add(0.160f);
        tmpList.add(0.155f);
        tmpList.add(0.150f);
        tmpList.add(0.145f);
        tmpList.add(0.140f);
        tmpList.add(0.135f);
        tmpList.add(0.130f);
        sMap.put(31,tmpList);

        tmpList = new ArrayList<>();
        tmpList.add(16.500f);
        tmpList.add(12.400f);
        tmpList.add(9.250f);
        tmpList.add(6.400f);
        tmpList.add(4.100f);
        tmpList.add(2.720f);
        tmpList.add(1.500f);
        tmpList.add(1.000f);
        tmpList.add(0.800f);
        tmpList.add(0.600f);
        tmpList.add(0.480f);
        tmpList.add(0.400f);
        tmpList.add(0.330f);
        tmpList.add(0.290f);
        tmpList.add(0.260f);
        tmpList.add(0.240f);
        tmpList.add(0.230f);
        tmpList.add(0.220f);
        tmpList.add(0.210f);
        tmpList.add(0.200f);
        tmpList.add(0.190f);
        tmpList.add(0.180f);
        tmpList.add(0.170f);
        tmpList.add(0.160f);
        tmpList.add(0.155f);
        tmpList.add(0.150f);
        tmpList.add(0.145f);
        tmpList.add(0.140f);
        tmpList.add(0.135f);
        tmpList.add(0.130f);
        tmpList.add(0.125f);
        tmpList.add(0.120f);
        sMap.put(32,tmpList);
    }
}
