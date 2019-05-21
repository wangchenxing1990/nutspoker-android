package com.htgames.nutspoker.net.websocket;

import com.htgames.nutspoker.ChessApp;
import com.netease.nim.uikit.api.ApiConfig;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.common.util.MD5;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by 20150726 on 2016/6/4.
 */
public class TexasSocketConstants {

    public static final URI getRoomWebSocketUri(String uid , GameEntity gameEntity, String websocketUrl) {//MatchRoomActivity
        String randomStr = "jhiugytrDDt6hbvSDFEknyjbct";
        String code = gameEntity == null ? "" : gameEntity.code;
        String token = UserPreferences.getInstance(ChessApp.sAppContext).getUserToken() + uid + code + randomStr;
        String url = StringUtil.isSpace(websocketUrl) ? ApiConfig.getHostWebsocket() : "ws://" + websocketUrl + "/";
        try {
            StringBuffer socketUrl = new StringBuffer();
            socketUrl.append(url)
                    .append("ws?uid=")
                    .append(uid)
                    .append("&code=")
                    .append(code)
                    .append("&token=")
                    .append(MD5.toMD5(token));
            URI uri = new URI(socketUrl.toString());
            LogUtil.i("TexasSocketConstants", socketUrl.toString());
            return uri;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
