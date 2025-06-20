package com.zorge_string;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CoderGCM {

		private String splitLines(final String line) {
			
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
	    

		private byte[] getKey(final String psw, final byte noise) throws Exception {

			// Following is done to make dictionary hack a little more complicated.
			// 1. Get MD5 of password.
			// 2. Have a noise byte as random value 0..0xFF
			// 3. Make switch based on noise byte.
			// 4. Get MD5 of "noised" MD5.
			
			byte[] md51 = UtilsCoder.calcMD5(psw.getBytes());
			
			int switchIndex = UtilsCoder.getSwitchIndex(md51, noise);
			if(switchIndex < 0)
				switchIndex *= -1;
			if(switchIndex == 0)
				switchIndex = 1; // I could not comes up with something smarter, because index is f() of md5.length and noise
			
			byte tmp = md51[0];
			md51[0] = md51[switchIndex];
			md51[switchIndex] = tmp;
			
			byte[] md52 = UtilsCoder.calcMD5(md51);
			return md52;
		}

		private String encrypt(final String input, final String psw) throws Exception {
			
			byte noise = UtilsCoder.getRandomByte();
			final byte[] byteKey = getKey(psw, noise);

	        byte[] iv = new byte[16];
	        System.arraycopy(byteKey, 0, iv, 0, iv.length); // Did you notice I am loosing two bytes ?

	        SecretKey key = new SecretKeySpec(byteKey, "AES");	        
	        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
	        GCMParameterSpec gps = new GCMParameterSpec(128, iv); // tag length = 128
	        cipher.init(Cipher.ENCRYPT_MODE, key, gps);

	        byte[] byteOut = cipher.doFinal(input.getBytes());
	        byteOut = UtilsCoder.prePendByte(noise, byteOut);
	       	return splitLines(UtilsCoder.toHexString(byteOut));
		}		
		
		// kudos to https://medium.com/@pravallikayakkala123/understanding-aes-encryption-and-aes-gcm-mode-an-in-depth-exploration-using-java-e03be85a3faa
		private String decrypt(final String input, final String psw) throws Exception {
		
			byte noise = UtilsCoder.fromHexStringFirstByte(input);
			final byte[] byteKey = getKey(psw, noise);

	        byte[] iv = new byte[16];
	        System.arraycopy(byteKey, 0, iv, 0, iv.length);
	        
	        SecretKey key = new SecretKeySpec(byteKey, "AES");
	        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
	        GCMParameterSpec gps = new GCMParameterSpec(128, iv); // tag length = 128
	        cipher.init(Cipher.DECRYPT_MODE, key, gps);
	        
	        byte[] byteInput = UtilsCoder.fromHexString(UtilsCoder.removeLines(input.substring(2)));
	        byte[] plainBytes = cipher.doFinal(byteInput);
	        return new String(plainBytes);
		}
		
	    /**
	     * Encrypt or decrypt message.
	     * 
	     * @param input
	     * @param psw
	     * @param isEncrypt
	     * @return
	     * @throws Exception
	     */
		public String doIt(final String input, final String psw, boolean isEncrypt) throws Exception {
			
			if(input == null || input.isEmpty() || psw == null || psw.isEmpty())
				throw new Exception("Empty input.");

			if(isEncrypt)
				return encrypt(input, psw);
			else
				return decrypt(input, psw);
		}	    
}
