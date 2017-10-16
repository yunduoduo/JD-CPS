package com.raincc.robot.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	public static String Encryption(String oldPass, int leng) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
		md.update(oldPass.getBytes());
		byte b[] = md.digest();

		int i;

		StringBuffer buf = new StringBuffer();
		for (int offset = 0; offset < b.length; offset++) {
			i = b[offset];
			if (i < 0) {
				i += 256;
			}
			if (i < 16) {
				buf.append("0");
			}
			buf.append(Integer.toHexString(i));
		}
		String pass32 = buf.toString();
		String pass16 = buf.toString().substring(8, 24);
		return leng == 32 ? pass32 : pass16;
	}
	
	public static void main(String[] args) {
		String s = MD5.Encryption("{\"payType\":\"alipy\",\"sid\":\"用户登录的sessionID\",\"info\":[{\"count\":1,\"type\":1,\"id\":8},{\"count\":2,\"type\":1,\"id\":9},{\"count\":1,\"type\":2,\"id\":3},{\"count\":2,\"type\":2,\"id\":21}]}qzDlqji0KJZsvdz9dSVteKS1yY4D6cIWNaYC8N3glmMMVbr1t9FYcfC1j19KLD0EcubFiboS68Or2fby9CR8m6X5o6", 32);
		System.out.println(s.toUpperCase());
	}
	
}
