package com.jigong.app_attendance.utils;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SignTest {

    public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    public static final String ENCODE_ALGORITHM = "SHA-256";

    public static final String PLAIN_TEXT = "test string";

    public static final String PRIMARI_KEY[] = {"MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMuuvdKDcbkI6IkD\n" +
            "zAnPEpb7P+hAggaC/Tu9e/yPJTtSMHZBFhbBMIUajMkC+UT4TY5m6CZvQEuK/CKL\n" +
            "3jpgRfL4HcSvvBit92mgn/8oQCfOv1solmFQ3GHRD1Je8oVW3628HWwcdo+qhC8Y\n" +
            "MoS7otB+A6cvsVNa4RIGGhhCE0ZhAgMBAAECgYEAyaJtDC9Pr2ugN7Nok/VIY+rK\n" +
            "MzNZBPU7l55XVYinxPfyPb7lDRWm4L2NzZiCB8L/lA9Plmzf62jlfZWVI5kW5FGb\n" +
            "prIab6u1HWu0/FoMDJBTGuBaPSeELJJONyAdG5XCzV+L4H0s2uioRtb07BXIet00\n" +
            "EYlBrG0+5Uemb8RtfgECQQD0CwG4VEIVmqmm3qz9lCh7iVJ2QidkV7WRJdBqg+Wt\n" +
            "0NAQcnVRFegaF0JAxvIoUN2rV95yh0eYpSwG1B7ZLT2xAkEA1amAG85EF6kpP4Q5\n" +
            "ER/utB1wRxuEqNtvKm1/yIWG1F3LaGDCBrHF+xfo096vaLnnX4759FamuaBz7Yip\n" +
            "ZmJPsQJAcygidqWjzWf++BQJU1svQyQIem87AVl1/vj2AN0ea0emdqxb+Zg5vt4M\n" +
            "0F5QdH+8Khc7i5WUTk5amRpNODPl8QJBALyRi+7+bDo1oaHdIEBs3k84bA9Vyg79\n" +
            "G29mlJB0yDpFTXAhdRxTaVfiEiprLoRPDlTX6uIqV5sccTQCmP8zZQECQQCJ53W4\n" +
            "V3lEsvTze6Dhj8+syEcFb1CGrgN0jRsTVG6b7KZ/JwvI1CiqWDRxJwLQcXLqFsqH\n" +
            "YVqBprI10Dj58Hlp", "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMuuvdKDcbkI6IkD\n" +
            "zAnPEpb7P+hAggaC/Tu9e/yPJTtSMHZBFhbBMIUajMkC+UT4TY5m6CZvQEuK/CKL\n" +
            "3jpgRfL4HcSvvBit92mgn/8oQCfOv1solmFQ3GHRD1Je8oVW3628HWwcdo+qhC8Y\n" +
            "MoS7otB+A6cvsVNa4RIGGhhCE0ZhAgMBAAECgYEAyaJtDC9Pr2ugN7Nok/VIY+rK\n" +
            "MzNZBPU7l55XVYinxPfyPb7lDRWm4L2NzZiCB8L/lA9Plmzf62jlfZWVI5kW5FGb\n" +
            "prIab6u1HWu0/FoMDJBTGuBaPSeELJJONyAdG5XCzV+L4H0s2uioRtb07BXIet00\n" +
            "EYlBrG0+5Uemb8RtfgECQQD0CwG4VEIVmqmm3qz9lCh7iVJ2QidkV7WRJdBqg+Wt\n" +
            "0NAQcnVRFegaF0JAxvIoUN2rV95yh0eYpSwG1B7ZLT2xAkEA1amAG85EF6kpP4Q5\n" +
            "ER/utB1wRxuEqNtvKm1/yIWG1F3LaGDCBrHF+xfo096vaLnnX4759FamuaBz7Yip\n" +
            "ZmJPsQJAcygidqWjzWf++BQJU1svQyQIem87AVl1/vj2AN0ea0emdqxb+Zg5vt4M\n" +
            "0F5QdH+8Khc7i5WUTk5amRpNODPl8QJBALyRi+7+bDo1oaHdIEBs3k84bA9Vyg79\n" +
            "G29mlJB0yDpFTXAhdRxTaVfiEiprLoRPDlTX6uIqV5sccTQCmP8zZQECQQCJ53W4\n" +
            "V3lEsvTze6Dhj8+syEcFb1CGrgN0jRsTVG6b7KZ/JwvI1CiqWDRxJwLQcXLqFsqH\n" +
            "YVqBprI10Dj58Hlp"};

    public static final String PUBLIC_KEY[] = {"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDLrr3Sg3G5COiJA8wJzxKW+z/o\n" +
            "QIIGgv07vXv8jyU7UjB2QRYWwTCFGozJAvlE+E2OZugmb0BLivwii946YEXy+B3E\n" +
            "r7wYrfdpoJ//KEAnzr9bKJZhUNxh0Q9SXvKFVt+tvB1sHHaPqoQvGDKEu6LQfgOn\n" +
            "L7FTWuESBhoYQhNGYQIDAQAB", "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDLrr3Sg3G5COiJA8wJzxKW+z/o\n" +
            "QIIGgv07vXv8jyU7UjB2QRYWwTCFGozJAvlE+E2OZugmb0BLivwii946YEXy+B3E\n" +
            "r7wYrfdpoJ//KEAnzr9bKJZhUNxh0Q9SXvKFVt+tvB1sHHaPqoQvGDKEu6LQfgOn\n" +
            "L7FTWuESBhoYQhNGYQIDAQAB"};

    public static void main(String[] args) throws Exception {
        PrivateKey privateKey = getPrivateKey(0);
        byte[] signBytes = sign(privateKey, PLAIN_TEXT);
        PublicKey publicKey = getPublicKey(0);
        System.out.println(verifySign(publicKey, PLAIN_TEXT, signBytes));
    }


    public static String getSign(int privateStr, String plainText) {
        try {
            byte[] keyBytes = com.jigong.app_attendance.utils.Base64.decode(PRIMARI_KEY[privateStr]);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PrivateKey priKey = factory.generatePrivate(keySpec);
            // 生成私钥
            // 用私钥对信息进行数字签名
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(priKey);
            signature.update(plainText.getBytes(StandardCharsets.UTF_8));
            return com.jigong.app_attendance.utils.Base64.encode(signature.sign());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 签名
     *
     * @param privateKey 私钥
     * @return
     */
    public static byte[] sign(PrivateKey privateKey, String plainText) {
        MessageDigest messageDigest;
        byte[] signed = null;
        try {
            messageDigest = MessageDigest.getInstance(ENCODE_ALGORITHM);
            messageDigest.update(plainText.getBytes());
            byte[] outputDigest_sign = messageDigest.digest();
            Signature Sign = Signature.getInstance(SIGNATURE_ALGORITHM);
            Sign.initSign(privateKey);
            Sign.update(outputDigest_sign);
            signed = Sign.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return signed;
    }

    /**
     * 验签
     *
     * @param publicKey  公钥
     * @param plain_text 明文
     * @param signed     签名
     */
    public static boolean verifySign(PublicKey publicKey, String plain_text, byte[] signed) {
        MessageDigest messageDigest;
        boolean SignedSuccess = false;
        try {
            messageDigest = MessageDigest.getInstance(ENCODE_ALGORITHM);
            messageDigest.update(plain_text.getBytes());
            byte[] outputDigest_verify = messageDigest.digest();
            //System.out.println("SHA-256加密后-----》" +bytesToHexString(outputDigest_verify));
            Signature verifySign = Signature.getInstance(SIGNATURE_ALGORITHM);
            verifySign.initVerify(publicKey);
            verifySign.update(outputDigest_verify);
            SignedSuccess = verifySign.verify(signed);
            System.out.println("验证成功？---" + SignedSuccess);

        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return SignedSuccess;
    }


    /**
     * bytes[]换成16进制字符串
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    /**
     * 解码PublicKey
     *
     * @param key
     * @return
     */
    public static PublicKey getPublicKey(int key) {
        try {
            byte[] byteKey = com.jigong.app_attendance.utils.Base64.decode(PUBLIC_KEY[key].replaceAll("\r", "").replaceAll("\n", ""));
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(byteKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(x509EncodedKeySpec);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解码PrivateKey
     *
     * @return
     */
    public static PrivateKey getPrivateKey(int index) {
        try {
            byte[] byteKey = Base64.decode(PRIMARI_KEY[index].replaceAll("\r", "").replaceAll("\n", ""));
            PKCS8EncodedKeySpec x509EncodedKeySpec = new PKCS8EncodedKeySpec(byteKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(x509EncodedKeySpec);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}