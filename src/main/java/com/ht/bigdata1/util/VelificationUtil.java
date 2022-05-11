package com.ht.bigdata1.util;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Description: VelificationUtil
 * @Author: yjs
 * @createTime: 2022年05月11日 13:11:39
 * @version: 1.0
 */
@Slf4j
public class VelificationUtil {
    private static final String MD5 = "MD5";
    private static final String EMPTY = "";
    private static final byte[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final byte LOW = 0xf;

    /**
     * 获取byte数组的md5
     *
     * @param bytes byte数组
     * @return md5
     */
    public static String getMD5Hex(byte[] bytes) {
        try {
            MessageDigest md5 = MessageDigest.getInstance(MD5);
            md5.update(bytes);
            byte[] digest = md5.digest();
            return byte2Hex(digest);
        } catch (NoSuchAlgorithmException e) {
            log.error("加密方法{}不支持：{}", MD5, e);
        }
        return EMPTY;
    }

    /**
     * byte数组转16进制字符串
     *
     * @param bytes byte数组
     * @return 16进制字符串
     */
    private static String byte2Hex(byte[] bytes) {
        int length = bytes.length;
        byte[] hexs = new byte[length << 1];
        for (int index = 0; index < length; index++) {
            hexs[index << 1] = HEX[bytes[index] >>> 4 & LOW];
            hexs[(index << 1) + 1] = HEX[bytes[index] & LOW];
        }
        return new String(hexs);
    }
}
