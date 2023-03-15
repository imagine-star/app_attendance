package com.jigong.app_attendance.longminggong;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HexUtil {

    public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || "".equals(hexString)) {
			return null;
		}
		// toUpperCase将字符串中的所有字符转换为大写
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		// toCharArray将此字符串转换为一个新的字符数组。
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}
    //charToByte返回在指定字符的第一个发生的字符串中的索引，即返回匹配字符
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}


	public static String BinaryToHexString(byte[] bytes) {
		String hexStr = "0123456789ABCDEF";
		String result = "";
		String hex = "";
		for (byte b : bytes) {
			hex = String.valueOf(hexStr.charAt((b & 0xF0) >> 4));
			hex += String.valueOf(hexStr.charAt(b & 0x0F));
			result += hex ;
		}
		return result;
	}

	/**
	 * 4字节
	 * 低位在前，高位在后
	 *
	 * @param n
	 * @return
	 */
	public static byte[] unlong2H4bytes(long n) {
		byte[] b = new byte[4];
		b[0] = (byte) (n & 0xff);
		b[1] = (byte) (n >> 8 & 0xff);
		b[2] = (byte) (n >> 16 & 0xff);
		b[3] = (byte) (n >> 24 & 0xff);
		return b;
	}

	/**
	 * byte数组转为十六进制字符串
	 *
	 * @param bytes
	 * @return
	 */
	public static String byte2Hex(byte[] bytes) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xff & bytes[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

	//两位一字符，倒序排序
	public static String reverseString(String str) {

    	List<String> strlist=new ArrayList();

		char[] chr = str.toCharArray();

		for (int i = 0 ; i < chr.length; i=i+2) {

			String s=chr[i]+""+chr[i+1];

			strlist.add(s);

		}
		Collections.reverse(strlist);

		String result="";

		for(String v:strlist){

			result+=v;
		}
		return result;

	}

	/**
	 * 16进制转换成为string类型字符串
	 * @param s
	 * @return
	 */
	public static String hexStringToString(String s) {
		if (s == null || "".equals(s)) {
			return null;
		}
		s = s.replace(" ", "");
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(baKeyword, "UTF-8");
			new String();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}


	/**
	 * 字符串转化成为16进制字符串
	 * @param s
	 * @return
	 */
	public static String strTo16(String s) {
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			int ch = (int) s.charAt(i);
			String s4 = Integer.toHexString(ch);
			str = str + s4;
		}
		return str;
	}

	//将16进制字符串自动补全到8位 并且倒序排序
	public static String full8(String lenth) {

		int a = lenth.getBytes().length;

		int b = 8 - a;

		for (int i = 0; i < b; i++) {

			lenth = "0" + lenth;

		}

		return reverseString(lenth);
	}

	public static String fullLength(String lenth, Integer length) {
		int a = lenth.getBytes().length;
		int b = length - a;

		for(int i = 0; i < b; ++i) {
			lenth = lenth + "0";
		}

		return lenth;
	}

	/**
	 * xor运算
	 *
	 * @param data
	 * @return
	 */
	public static String getBCC(byte[] data) {

		String ret = "";
		byte[] BCC = new byte[1];
		for (int i = 0; i < data.length; i++) {
			BCC[0] = (byte) (BCC[0] ^ data[i]);
		}
		String hex = Integer.toHexString(BCC[0] & 0xFF);
		if (hex.length() == 1) {
			hex = '0' + hex;
		}
		ret += hex.toUpperCase();
		return ret;
	}

	public static String strTo16FullLength(String s, int length) {
		String str = "";

		for(int i = 0; i < s.length(); ++i) {
			int ch = s.charAt(i);
			String s4 = Integer.toHexString(ch);
			str = str + s4;
		}

		while(str.length() < length) {
			str = str + '0';
		}

		return str;
	}
}