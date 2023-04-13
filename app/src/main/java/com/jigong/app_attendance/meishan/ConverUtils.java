package com.jigong.app_attendance.meishan;

import android.text.TextUtils;
import android.util.Base64;

import com.jigong.app_attendance.info.GlobalCode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;


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
    public static byte[] netSourceToByte(String srcUrl, String requestMethod) {
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

    public static String netSourceToBase64(String srcUrl, String requestMethod) {
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
        return "data:image/jpg;base64," + new String(Base64.encode(outPut.toByteArray(), Base64.NO_WRAP));
    }

    public static String netSourceToFile(String srcUrl, String requestMethod) {
        if (TextUtils.isEmpty(srcUrl)) {
            return null;
        }
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
        String fileName = new Date().getTime() + "worker.jpg";
        String filePath = GlobalCode.WORKER_PIC_PATH;
        File file = new File(filePath, "");
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            OutputStream os = new FileOutputStream(filePath + "/" + fileName);
            os.write(outPut.toByteArray(), 0, outPut.toByteArray().length);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
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

