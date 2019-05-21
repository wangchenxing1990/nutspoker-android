package com.htgames.nutspoker.tool;

import java.math.BigInteger;

/**
 * Created by 20150726 on 2016/4/15.
 */
public class BinaryTools {

    /**
     * @param decimalSource
     * @return String
     * @Description: 十进制转换成二进制 ()
     */
    public static String decimalToBinary(int decimalSource) {
        BigInteger bi = new BigInteger(String.valueOf(decimalSource));  //转换成BigInteger类型
        return bi.toString(2);  //参数2指定的是转化成X进制，默认10进制
    }

    /**
     * @param binarySource
     * @return int
     * @Description: 二进制转换成十进制
     */
    public static int binaryToDecimal(String binarySource) {
        BigInteger bi = new BigInteger(binarySource, 2);    //转换为BigInteger类型
        return Integer.parseInt(bi.toString());     //转换成十进制
    }

    public static int byte2int(byte[] res) {
        // 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000
        int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) // | 表示安位或
                | ((res[2] << 24) >>> 8) | (res[3] << 24);
        return targets;
    }
}
