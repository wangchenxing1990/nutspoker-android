package com.netease.nim.uikit.common.util;

import com.netease.nim.uikit.chesscircle.DealerConstant;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MD5 {
//	public static String toMD5(String str) {
//
//		MessageDigest md5 = null;
//		try {
//			md5 = MessageDigest.getInstance("MD5");
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "";
//		}
//		char[] charArray = str.toCharArray();
//		byte[] byteArray = new byte[charArray.length];
//		for (int i = 0; i < charArray.length; i++) {
//			byteArray[i] = (byte) charArray[i];
//		}
//		byte[] md5Bytes = md5.digest(byteArray);
//		StringBuffer hexValue = new StringBuffer();
//		for (int i = 0; i < md5Bytes.length; i++)
//		{
//			int val = ((int) md5Bytes[i]) & 0xff;
//			if (val < 16)
//			{
//				hexValue.append("0");
//			}
//			hexValue.append(Integer.toHexString(val));
//		}
//		return hexValue.toString();
//	}

    public static String MSG_VFC = "MsgVFC";

    public static Map getP2PExtension(String sessionId) {
        Map extraKey = new HashMap();
        extraKey.put(MSG_VFC, toMD5(sessionId + "/HTG?"));
        return extraKey;
    }

    public static boolean isVerifiedMsg(String sessionId, List<IMMessage> messages) {//是否是我们客户端加密过的p2p消息
        if (DealerConstant.isDealer(sessionId)) {
            return true;
        }
        if (messages == null || messages.size() <= 0) {
            return false;
        }
        for (int i = 0; i < messages.size(); i++) {
            IMMessage imMessage = messages.get(i);
            if (imMessage.getRemoteExtension() != null && imMessage.getRemoteExtension().containsKey(MSG_VFC) && imMessage.getRemoteExtension().get(MSG_VFC).equals(getP2PExtension(sessionId).get(MSG_VFC))) {
                return true;
            }
        }
        return false;
    }

    public static String toMD5(String str) {
        String md5Str = null;
        if (str != null && str.length() != 0) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                try {
                    md.update(str.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                byte b[] = md.digest();

                StringBuffer buf = new StringBuffer("");
                for (int offset = 0; offset < b.length; offset++) {
                    int i = b[offset];
                    if (i < 0)
                        i += 256;
                    if (i < 16)
                        buf.append("0");
                    buf.append(Integer.toHexString(i));
                }
                // 32λ
                md5Str = buf.toString();
                // 16λ
                // md5Str = buf.toString().substring(8, 24);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return md5Str;
    }

    public static String encryptmd5(String str) {

        char[] a = str.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] ^ 'l');
        }
        String s = new String(a);
        return s;
    }

    private static String key = "a6U&1$Ip[Jr/sed]Rfvn=O>Mz+}lXN*%-gLcGD|0";

    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs;
    }

    //SHA1 加密实例
    public static String encryptToSHA(String info) {
        byte[] digesta = null;
        try {
            // 得到一个SHA-1的消息摘要
            MessageDigest alga = MessageDigest.getInstance("SHA-1");
            // 添加要进行计算摘要的信息
            alga.update(info.getBytes());
            // 得到该摘要
            digesta = alga.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // 将摘要转为字符串
        String rs = byte2hex(digesta);
        return rs + key;
    }

    public static String MD5_P2S(String sort) {
        // TODO Auto-generated method stub
        return null;
    }
}
