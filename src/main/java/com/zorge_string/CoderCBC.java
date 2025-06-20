package com.zorge_string;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CoderCBC {

	public CoderCBC() {		
	}

	private byte[] keyToIV(final String key) {
		
		byte byteKey = UtilsCoder.keyToByte(key);
		
        byte[] iv = new byte[16];        
        for(int i = 0; i < 16; ++i, ++byteKey)
        	iv[i] = byteKey;
		return iv;
	}
	
	private byte[] IVtoSecretKey(final byte[] iv) {
		
		// there is no way iv[] will have size different from 16
        byte[] keyBytes = new byte[32];
        for(int i = 0; i < 16; ++i) { 
        	keyBytes[i] = iv[i];
        	keyBytes[i + 16] = iv[i];
        }
        return keyBytes;
	}

	private String encrypt(final String input, final String psw) throws Exception {
		
		byte noise = UtilsCoder.getRandomByte();
		final String key = UtilsCoder.getKey(psw, noise);
		
		byte[] iv = keyToIV(key);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
		byte[] keyBytes = IVtoSecretKey(iv);
		
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
       	cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        
        byte[] byteOut = cipher.doFinal(input.getBytes());
        byteOut = UtilsCoder.prePendByte(noise, byteOut);
       	return UtilsCoder.splitLines(UtilsCoder.toHexString(byteOut));
	}

	private String decrypt(final String input, final String psw) throws Exception {

		byte noise = UtilsCoder.fromHexStringFirstByte(input);
		final String key = UtilsCoder.getKey(psw, noise);
		
		byte[] iv = keyToIV(key);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
		byte[] keyBytes = IVtoSecretKey(iv);
		
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
       	cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        
        byte[] byteInput = UtilsCoder.fromHexString(UtilsCoder.removeLines(input.substring(2)));
        	
        byte[] byteOut = cipher.doFinal(byteInput);
        return new String(byteOut);
	}
	
	/**
	 * Encrypt or decrypt input.
	 * 
	 * @param input
	 * @param key
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
