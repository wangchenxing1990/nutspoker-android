package com.netease.nim.uikit.chesscircle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 德州圈功能：好友状态缓存
 */
public class FriendStatusCache {

    static Set<String> gamingAccounts = new HashSet<>();

    public static void setGamingAccounts(ArrayList<String> accountList){
        if(accountList == null){
            return;
        }
        gamingAccounts.clear();
        for(String account : accountList){
            if(!gamingAccounts.contains(account)){
                gamingAccounts.add(account);
            }
        }
    }

    /**
     * 是否在游戏中
     * @param account
     * @return
     */
    public static boolean isGamingByAccount(String account){
        return false;
//        return gamingAccounts.contains(account);
    }

}
