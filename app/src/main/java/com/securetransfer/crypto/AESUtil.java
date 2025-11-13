package com.securetransfer.crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES Utility class for generating, encrypting and decrypting with AES-256.
 */
public class AESUtil {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_CIPHER = "AES/CBC/PKCS5Padding";

    /**
     * Generates a random 256-bit AES key.
     */
    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
        keyGen.init(256); // 256-bit AES key
        return keyGen.generateKey();
    }

    /**
     * Encrypts data using AES-256 with a random IV.
     * Returns the IV + ciphertext concatenated.
     */
    public static byte[] encrypt(byte[] data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_CIPHER);
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] encrypted = cipher.doFinal(data);

        // concatenate IV and encrypted data
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        return combined;
    }

    /**
     * Decrypts data encrypted with AES-256, expecting IV + ciphertext concatenated.
     */
    public static byte[] decrypt(byte[] encryptedData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_CIPHER);

        // Extract the IV (first 16 bytes)
        byte[] iv = new byte[16];
        byte[] ciphertext = new byte[encryptedData.length - 16];
        System.arraycopy(encryptedData, 0, iv, 0, 16);
        System.arraycopy(encryptedData, 16, ciphertext, 0, ciphertext.length);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        return cipher.doFinal(ciphertext);
    }

    /**
     * Converts a byte array to a Base64 string (for debugging or transfer).
     */
    public static String toBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * Converts a Base64 string to a byte array.
     */
    public static byte[] fromBase64(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    /**
     * Converts AES key bytes to a SecretKey (to reconstruct from the server).
     */
    public static SecretKey rebuildKey(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, AES_ALGORITHM);
    }
}
