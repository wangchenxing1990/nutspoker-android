package com.htgames.nutspoker.chat.session;

/**
 * 自定义消息提醒类型
 * @deprecated 已经弃用
 */
public enum TeamTipType {
    undefined(-1),
    JoinGame(0);

    private int value;

    private TeamTipType(int var3) {
        this.value = var3;
    }

    public final int getValue() {
        return this.value;
    }

    public static TeamTipType typeOfValue(int var0) {
        TeamTipType[] var1;
        int var2 = (var1 = values()).length;

        for (int var3 = 0; var3 < var2; ++var3) {
            TeamTipType var4;
            if ((var4 = var1[var3]).getValue() == var0) {
                return var4;
            }
        }

        return undefined;
    }
}
