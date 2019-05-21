package com.htgames.nutspoker.chat.app_msg.model;

public enum AppMessageStatus {
    init(0),
    passed(1),
    declined(2),
    ignored(3),
    expired(4),
    extension1(100),
    extension2(101),
    extension3(102),
    extension4(103),
    extension5(104);

    private int value;

    private AppMessageStatus(int var3) {
        this.value = var3;
    }

    public final int getValue() {
        return this.value;
    }

    public static AppMessageStatus statusOfValue(int var0) {
        AppMessageStatus[] var1;
        int var2 = (var1 = values()).length;

        for (int var3 = 0; var3 < var2; ++var3) {
            AppMessageStatus var4;
            if ((var4 = var1[var3]).getValue() == var0) {
                return var4;
            }
        }

        return init;
    }
}
