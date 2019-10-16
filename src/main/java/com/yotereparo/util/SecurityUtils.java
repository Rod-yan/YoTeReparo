package com.yotereparo.util;

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang.RandomStringUtils;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

public class SecurityUtils {
	
	public static String encryptPassword(String input) {
	    SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
		return Hex.toHexString(digestSHA3.digest(input.getBytes(StandardCharsets.UTF_8)));
	}
	
	public static String saltGenerator() {
		return RandomStringUtils.randomAlphanumeric(16);
	}
}
