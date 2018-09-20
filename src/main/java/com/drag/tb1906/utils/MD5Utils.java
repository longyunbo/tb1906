package com.drag.tb1906.utils;

import java.security.MessageDigest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MD5Utils {
	
	 public final static String encrypt(String s) {
	        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	        try {
	            byte[] btInput = s.getBytes();
	            // 获得MD5摘要算法的 MessageDigest 对象
	            MessageDigest mdInst = MessageDigest.getInstance("MD5");
	            // 使用指定的字节更新摘要
	            mdInst.update(btInput);
	            // 获得密文
	            byte[] md = mdInst.digest();
	            // 把密文转换成十六进制的字符串形式
	            int j = md.length;
	            char str[] = new char[j * 2];
	            int k = 0;
	            for (int i = 0; i < j; i++) {
	                byte byte0 = md[i];
	                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
	                str[k++] = hexDigits[byte0 & 0xf];
	            }
	            return new String(str);
	        } catch (Exception e) {
	            log.error("generate md5 error, {}", s, e);
	            return null;
	        }
	    }
    
//    public static void main(String args[]) {
//    	 String encryptStr = "888888";
//    	 try {
//    		 System.out.println(MD5Utils.encrypt(encryptStr));
//    	 } catch(Exception e) {
//    		 
//    	 }
//    	 
//    }
}