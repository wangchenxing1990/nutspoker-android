package com.netease.nim.uikit.nim;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.ui.imageview.ImageViewEx;
import com.netease.nim.uikit.nim.ChatRoomMemberCache;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hzxuwen on 2016/1/19.
 */
public class ChatRoomHelper {
    private static final int[] imageRes = {R.mipmap.icon};

    private static Map<String, Integer> roomCoverMap = new HashMap<>();
    private static int index = 0;

    public static void init() {
        ChatRoomMemberCache.getInstance().clear();
        ChatRoomMemberCache.getInstance().registerObservers(true);
    }

    public static void logout() {
        ChatRoomMemberCache.getInstance().registerObservers(false);
        ChatRoomMemberCache.getInstance().clear();
    }

    public static void setCoverImage(String roomId, ImageViewEx coverImage) {
        if (roomCoverMap.containsKey(roomId)) {
            coverImage.setImageResource(roomCoverMap.get(roomId));
        } else {
            if (index > imageRes.length) {
                index = 0;
            }
            roomCoverMap.put(roomId, imageRes[index]);
            coverImage.setImageResource(imageRes[index]);
            index++;
        }
    }
}
