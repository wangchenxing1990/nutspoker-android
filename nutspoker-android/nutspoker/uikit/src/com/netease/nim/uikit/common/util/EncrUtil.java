package com.netease.nim.uikit.common.util;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Created by 周智慧 on 2017/8/15.
 * 可逆的加密算法  用于云信key的加密
 */

public class EncrUtil {
    private static final int RADIX = 16;
    private static final String SEED = "0933910847463829232312312";

    public static final String encrypt(String password) {
        if (password == null)
            return "";
        if (password.length() == 0)
            return "";

        BigInteger bi_passwd = new BigInteger(password.getBytes());

        BigInteger bi_r0 = new BigInteger(SEED);
        BigInteger bi_r1 = bi_r0.xor(bi_passwd);

        return bi_r1.toString(RADIX);
    }

    public static final String decrypt(String encrypted) {
        if (encrypted == null)
            return "";
        if (encrypted.length() == 0)
            return "";

        BigInteger bi_confuse = new BigInteger(SEED);

        try {
            BigInteger bi_r1 = new BigInteger(encrypted, RADIX);
            BigInteger bi_r0 = bi_r1.xor(bi_confuse);

            return new String(bi_r0.toByteArray());
        } catch (Exception e) {
            return "";
        }
    }

    public static void main(String args[]){
        System.out.println(Arrays.toString(args));
        if(args==null || args.length!=2) return;
        if("-e".equals(args[0])){
            System.out.println(args[1]+" encrypt password is "+encrypt(args[1]));
        }else if("-d".equals(args[0])){
            System.out.println(args[1]+" decrypt password is "+decrypt(args[1]));
        }else{
            System.out.println("args -e:encrypt");
            System.out.println("args -d:decrypt");
        }
    }

}
