package io.cojam.web.utils;



import org.apache.commons.lang3.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

/**
 * 양방향 암호화 알고리즘인 AES256 암호화를 지원하는 클래스
 */
public class AES256Util {

	public static final String SEC_KEY = "rlatlswh#ssdf";

	private final String iv;
	private final Key keySpec;

	public AES256Util() throws UnsupportedEncodingException {
		this.iv = SEC_KEY.substring(0, 16);
		byte[] keyBytes = new byte[16];
		byte[] b = SEC_KEY.getBytes(StandardCharsets.UTF_8);
		int len = b.length;
		if (len > keyBytes.length) {
			len = keyBytes.length;
		}
		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

		this.keySpec = keySpec;
	}

	/**
	 * 16자리의 키값을 입력하여 객체를 생성한다.
	 *
	 * @param key 암/복호화를 위한 키값
	 * @throws UnsupportedEncodingException 키값의 길이가 16이하일 경우 발생
	 */
	public AES256Util(String key) throws UnsupportedEncodingException {

		if(key == null)
			key = SEC_KEY;

		this.iv = key.substring(0, 16);
		byte[] keyBytes = new byte[16];
		byte[] b = key.getBytes(StandardCharsets.UTF_8);
		int len = b.length;
		if (len > keyBytes.length) {
			len = keyBytes.length;
		}
		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

		this.keySpec = keySpec;
	}

	/**
	 * AES256 으로 암호화 한다.
	 *
	 * @param str 암호화할 문자열
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws GeneralSecurityException
	 * @throws UnsupportedEncodingException
	 */
	public String encrypt(String str) throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException {
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
		byte[] encrypted = c.doFinal(str.getBytes(StandardCharsets.UTF_8));
		String enStr = Base64.getUrlEncoder().withoutPadding().encodeToString(encrypted);
		return enStr;
	}

	/**
	 * AES256으로 암호화된 txt 를 복호화한다.
	 *
	 * @param str 복호화할 문자열
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws GeneralSecurityException
	 * @throws UnsupportedEncodingException
	 */
	public String decrypt(String str) throws Exception {
		if(str==null) {
			str="Const.DEFAULT_SALT";
		}
		try {
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
			byte[] byteStr = Base64.getUrlDecoder().decode(str);
			return new String(c.doFinal(byteStr), StandardCharsets.UTF_8);
		}catch (Exception e){
			return str;
		}

	}
	
	/**
	 * 파일 암호화
	 * @param originalFilePath
	 * @return
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public byte[] encryptFile(String originalFilePath) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

		if (StringUtils.isBlank(originalFilePath))
			return null;

		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));

		File originalFile = new File(originalFilePath);

		byte[] inputBytes = null;
		byte[] outputBytes = null;
		try (FileInputStream inputStream = new FileInputStream(originalFile)) {

			inputBytes = new byte[(int) originalFile.length()];
			inputStream.read(inputBytes);
			outputBytes = c.doFinal(inputBytes);

		} catch (IOException e) {

		}

		return outputBytes;
	}

	/**
	 * 파일 복호화
	 * @param originalFilePath
	 * @return
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public byte[] decryptFile(String originalFilePath) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

		if (StringUtils.isBlank(originalFilePath))
			return null;

		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));

		File originalFile = new File(originalFilePath);

		byte[] inputBytes = null;
		byte[] outputBytes = null;
		try (FileInputStream inputStream = new FileInputStream(originalFile)) {

			inputBytes = new byte[(int) originalFile.length()];
			inputStream.read(inputBytes);
			outputBytes = c.doFinal(inputBytes);

		} catch (IOException e) {

		}

		return outputBytes;
	}

}
