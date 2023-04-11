package com.jigong.app_attendance.meishan;

import cn.hutool.crypto.symmetric.SM4;

import java.nio.charset.StandardCharsets;

/**
 * SM4 加密解密
 */
public class SM4IotUtil {

    /**
     * 加密
     *
     * @param secret  密钥
     * @param content 内容
     * @return 密文
     */
    public static String encrypt(String secret, String content) {
        SM4 sm4 = new SM4("CBC", "PKCS7Padding", secret.substring(0, 16).getBytes(StandardCharsets.UTF_8),
                secret.substring(0, 16).getBytes(StandardCharsets.UTF_8));
        return sm4.encryptHex(content, "UTF-8");
    }

    /**
     * 解密
     *
     * @param secret  密钥
     * @param content 密文
     * @return 内容
     */
    public static String decrypt(String secret, String content) {
        SM4 sm4 = new SM4("CBC", "PKCS7Padding", secret.substring(0, 16).getBytes(StandardCharsets.UTF_8),
                secret.substring(0, 16).getBytes(StandardCharsets.UTF_8));
        return sm4.decryptStr(content);
    }

}