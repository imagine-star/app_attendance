package com.jigong.app_attendance.hunan;

import com.jigong.app_attendance.encoder.BASE64Decoder;
import com.jigong.app_attendance.encoder.BASE64Encoder;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.Security;

/**
 * @author hailunZhao
 * @date 2020/6/16 14:32
 */
public class AESUtil {

    /**
     * 密钥算法
     */
    private static final String ALGORITHM = "AES";
    /**
     * 加密/解密算法-工作模式-填充模式
     */
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";
    /**
     * 默认编码
     */
    private static final String CHARSET = "utf-8";

    //设置java支持PKCS7Padding
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Des：加密
     *
     * @param cleartext 待加密字符串
     * @param secretKey 秘钥
     * @param ivPara    初始化向量
     * @return java.lang.String
     * @author hailunZhao
     * @date 2020/8/24 15:24
     */
    public static String encrypt(String cleartext, String secretKey, String ivPara) {
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(ivPara.getBytes(CHARSET));
            //两个参数，第一个为私钥字节数组， 第二个为加密方式 AES或者DES
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(CHARSET), ALGORITHM);
            //实例化加密类，参数为加密方式，要写全
            //PKCS5Padding比PKCS7Padding效率高，PKCS7Padding可支持IOS加解密
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            //初始化，此方法可以采用三种方式，按加密算法要求来添加。
            //（1）无第三个参数
            //（2）第三个参数为SecureRandom random = new SecureRandom();中random对象，随机数。(AES不可采用这种方法)
            //（3）采用此代码中的IVParameterSpec
            //加密时使用:ENCRYPT_MODE;  解密时使用:DECRYPT_MODE;
            //CBC类型的可以在第三个参数传递偏移量zeroIv,ECB没有偏移量
            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
            //加密操作,返回加密后的字节数组，然后需要编码。主要编解码方式有Base64, HEX, UUE,7bit等等。此处看服务器需要什么编码方式
            byte[] encryptedData = cipher.doFinal(cleartext.getBytes(CHARSET));
            return new BASE64Encoder().encode(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Des：解密
     *
     * @param encrypted 加密字符串
     * @param secretKey 秘钥
     * @param ivPara    初始化向量
     * @return java.lang.String
     * @author hailunZhao
     * @date 2020/8/24 15:26
     */
    public static String decrypt(String encrypted, String secretKey, String ivPara) {
        try {
            byte[] byteMi = new BASE64Decoder().decodeBuffer(encrypted);
            IvParameterSpec zeroIv = new IvParameterSpec(ivPara.getBytes());
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            //与加密时不同MODE:Cipher.DECRYPT_MODE
            //CBC类型的可以在第三个参数传递偏移量zeroIv,ECB没有偏移量
            cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
            byte[] decryptedData = cipher.doFinal(byteMi);
            return new String(decryptedData, CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
