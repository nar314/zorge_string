package com.zorge_string;

import java.security.MessageDigest;
import java.util.Random;

public class UtilsCoder {

	public static byte getRandomByte() {

		Random rnd = new Random();
        return (byte)rnd.nextInt(0xFF - 0);		
	}

	public static String removeLines(final String input) {
		
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < input.length(); i++) {
			char ch = input.charAt(i);
			if(ch != '\n')
				sb.append(ch);
		}
			
		return sb.toString();
	}

	// https://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
	public static byte[] fromHexString(final String s) {
		
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2)
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
	    
	    return data;
	}

	public static int getSwitchIndex(final byte[] input, final byte noise) {

		int len = input.length;
		int out = noise;
		if(out < 0)
			out *= -1;
		if(out < len)
			return noise;
		
		while(out >= len)
			out /= 2;
		
		return out;
	}
	
	public static byte[] calcMD5(final byte[] input) throws Exception {
		
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(input, 0, input.length);
		return md5.digest();
	}
	
	public static byte keyToByte(final String key) {
		
		byte out = 0;
		byte[] data = key.getBytes();
		for(int i = 0; i < data.length; ++i)
			out += data[i];
		return out;
	}
	
	public static String toHexString(final byte[] input) {
		
		StringBuffer sb = new StringBuffer();
	    for(int i = 0; i < input.length; ++i) // do I trust optimizer ?
	    	sb.append(String.format("%02X", input[i]));	    
	    return sb.toString();		
	}

	public static String getKey(final String psw, final byte noise) throws Exception {

		// Following is done to make dictionary hack a little more complicated.
		// 1. Get MD5 of password.
		// 2. Have a noise byte as random value 0..0xFF
		// 3. Make switch based on noise byte.
		// 4. Get MD5 of "noised" MD5.
		
		byte[] md51 = calcMD5(psw.getBytes());
		
		int switchIndex = getSwitchIndex(md51, noise);
		if(switchIndex < 0)
			switchIndex *= -1;
		if(switchIndex == 0)
			switchIndex = 1; // I could not comes up with something smarter, because index is f() of md5.length and noise
		
		byte tmp = md51[0];
		md51[0] = md51[switchIndex];
		md51[switchIndex] = tmp;
		
		byte[] md52 = calcMD5(md51);
		
		return toHexString(md52);
	}
	
	public static byte[] prePendByte(final byte b, byte[] ar) {
		
		byte[] out = new byte[ar.length + 1];
		out[0] = b;
		for(int i = 0; i < ar.length; i++)
			out[i + 1] = ar[i];
		return out;
	}
	
	public static String splitLines(final String line) {
		
		int maxBytesPerLine = 32;
		int charsPerLine = maxBytesPerLine * 2;
		
		int len = line.length();
		if(len < charsPerLine + (charsPerLine / 4))
			return line;
		
		StringBuilder sb = new StringBuilder();
		int curLine = 0;
		for(int i = 0; i < len; i++, curLine++) {
			if(curLine == charsPerLine) {
				sb.append("\n");
				curLine = 0;
			}
			sb.append(line.charAt(i));			
		}
		return sb.toString();
	}
	
	public static byte fromHexStringFirstByte(final String s) {
		
	    byte[] data = new byte[1];
        data[0] = (byte) ((Character.digit(s.charAt(0), 16) << 4) + Character.digit(s.charAt(1), 16));	    
	    return data[0];
	}	
}
