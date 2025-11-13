package com.securetransfer.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import java.util.Base64;

public class RSAUtils {

    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;

    /**
     * Generate and return a new pair of keys (public and private) - static method
     */
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(KEY_SIZE);
        return keyGen.generateKeyPair();
    }

    /**
     * Encrypts bytes using RSA public key
     */
    public static byte[] encryptRSA(byte[] plainBytes, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plainBytes);
    }

    /**
     * Decrypts bytes using RSA private key
     */
    public static byte[] decryptRSA(byte[] encryptedBytes, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedBytes);
    }

    /**
     * Returns the public key in Base64 format (for debugging/display)
     */
    public static String publicKeyToBase64(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * Encrypts a text using the public key and returns Base64
     */
    public static String encryptWithPublic(String plainText, PublicKey publicKey) throws Exception {
        byte[] encryptedBytes = encryptRSA(plainText.getBytes(), publicKey);
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Decrypts Base64 text using the private key
     */
    public static String decryptWithPrivate(String encryptedText, PrivateKey privateKey) throws Exception {
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = decryptRSA(decodedBytes, privateKey);
        return new String(decryptedBytes);
    }
}
