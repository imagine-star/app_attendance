package com.jigong.app_attendance.foshan;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * @author zhujialin
 * @date 2020/7/28
 */
public class ConverUtils {
    /**
     * 根据文件url获取文件并转换为base64编码
     *
     * @param srcUrl        文件地址
     * @param requestMethod 请求方式（"GET","POST"）
     * @return 文件base64编码
     */
    public static byte[] netSourceToBase64(String srcUrl, String requestMethod) {
        ByteArrayOutputStream outPut = new ByteArrayOutputStream();
        byte[] data = new byte[1024 * 8];
        try {
            // 创建URL
            URL url = new URL(srcUrl);
            // 创建链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(requestMethod);
            conn.setConnectTimeout(10 * 1000);

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                //连接失败/链接失效/文件不存在
                return null;
            }
            InputStream inStream = conn.getInputStream();
            int len = -1;
            while (-1 != (len = inStream.read(data))) {
                outPut.write(data, 0, len);
            }
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        return outPut.toByteArray();
    }

    /**
     * 把base64转化文件流
     *
     * @param base64 base64
     * @return byte[] 文件流
     */
//    public static byte[] decryptByBase64(String base64) {
//
//        if (Strings.isNullOrEmpty(base64)) {
//            return null;
//        }
//        return Base64.decodeBase64(base64.substring(base64.indexOf(",") + 1));
//    }

    /**
     * inputStream转化为byte[]数组
     *
     * @param input InputStream
     * @return byte[]
     * @throws IOException
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

}

