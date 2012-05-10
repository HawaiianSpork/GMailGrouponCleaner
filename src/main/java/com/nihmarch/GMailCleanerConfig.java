package com.nihmarch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class GMailCleanerConfig {
	
	private static final String PROPS_FILE_NAME = ".gmailCleanConfig.properties";
	private static final char[] KS_PASSWORD = {'f','o','o',';','4','5'};
	private static final byte[] SALT = {'f','o','3','4','5','(','9', (byte) 0x95};
	
	private String username;
	private char[] password;
	
	
    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public char[] getPassword() {
		return password;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

	public static GMailCleanerConfig load() throws IOException, GeneralSecurityException {
		Properties props = new Properties();
		FileInputStream fis = null;
	    try {
	        fis = new FileInputStream(System.getProperty("user.home") + File.separator + PROPS_FILE_NAME);
	        props.load(fis);
	        GMailCleanerConfig config = new GMailCleanerConfig();
	        config.setUsername(props.getProperty("username"));
	        config.setPassword(decrypt(props.getProperty("password")));
	        return config;
	    } finally {
	        if (fis != null) {
	            fis.close();
	        }
	    }
	}
	
	public void store() throws IOException, GeneralSecurityException {
		store(this);
	}
	
	private static void store(GMailCleanerConfig config) throws IOException, GeneralSecurityException {
		Properties props = new Properties();
		props.setProperty("username", config.getUsername());
		props.setProperty("password", encrypt(config.getPassword()));
		
		FileOutputStream fos = null;
	    try {
	        fos = new FileOutputStream(System.getProperty("user.home") + File.separator + PROPS_FILE_NAME);
	        props.store(fos, null);
	    } finally {
	        if (fos != null) {
	            fos.close();
	        }
	    }
	}

	private static String encrypt(char[] property) throws GeneralSecurityException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(KS_PASSWORD));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return base64Encode(pbeCipher.doFinal(new String(property).getBytes()));
    }
    
    private static char[] decrypt(String property) throws GeneralSecurityException, IOException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(KS_PASSWORD));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return new String(pbeCipher.doFinal(base64Decode(property))).toCharArray();
    }
	
    private static String base64Encode(byte[] bytes) {
        return new BASE64Encoder().encode(bytes);
    }
    
    private static byte[] base64Decode(String property) throws IOException {
        return new BASE64Decoder().decodeBuffer(property);
    }
}
