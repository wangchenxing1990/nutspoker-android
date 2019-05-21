package com.htgames.nutspoker.chat.app_msg.model;

/**
 */
public enum AppMessageType {
    undefined(-1),
    GameBuyChips(1),//申请买入
    AppNotice(2),//系统公告
    GameOver(3),
    MatchBuyChips(4),//MT模式买入请求
    MatchBuyChipsResult(5);//MT模式买入申请状态结果

    private int value;

    private AppMessageType(int var3) {
        this.value = var3;
    }

    public final int getValue() {
        return this.value;
    }

    public static AppMessageType typeOfValue(int var0) {
        AppMessageType[] var1;
        int var2 = (var1 = values()).length;

        for (int var3 = 0; var3 < var2; ++var3) {
            AppMessageType var4;
            if ((var4 = var1[var3]).getValue() == var0) {
                return var4;
            }
        }

        return undefined;
    }
}
