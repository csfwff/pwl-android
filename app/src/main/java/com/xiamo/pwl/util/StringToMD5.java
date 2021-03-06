package com.xiamo.pwl.util;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * Created by x1c on 2017/6/7 0007.
 */

public class StringToMD5 {
    /**
     * 将字符串转成MD5值
     * @param string 需要转换的字符串
     * @return 字符串的MD5值
     */
    public static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    public static String stringToMD5Short(String string){
        return stringToMD5(string).substring(8,24).toUpperCase();
    }
}