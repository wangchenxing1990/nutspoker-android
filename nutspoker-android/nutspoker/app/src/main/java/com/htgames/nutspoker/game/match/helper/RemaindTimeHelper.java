package com.htgames.nutspoker.game.match.helper;

/**
 * Created by 20150726 on 2016/7/21.
 */
public class RemaindTimeHelper {

    public static String getRemaindTimeShow(int remainTime) {
        StringBuffer durationStr = new StringBuffer();
        int second = remainTime % 60;
        int min = remainTime / 60 % 60;
        int hour = remainTime / 60 / 60 % 60;
        if (hour != 0) {
            if (hour < 10) {
                durationStr.append("0");
            }
            durationStr.append(hour);
            durationStr.append(":");
        }
        if (min < 10) {
            durationStr.append("0");
        }
        durationStr.append(min);
        durationStr.append(":");
        if (second < 10) {
            durationStr.append("0");
        }
        durationStr.append(second);
        return durationStr.toString();
    }
}
