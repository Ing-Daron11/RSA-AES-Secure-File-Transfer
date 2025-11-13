package com.securetransfer.crypto;

import java.security.MessageDigest;

public class HashUtils {
    
    /**
     * Calculates SHA-256 hash of the given byte array
     */
    public static byte[] sha256(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(data);
    }

    /**
     * Calculates SHA-256 hash and returns as hex string (for display)
     */
    public static String sha256Hex(byte[] data) throws Exception {
        byte[] hash = sha256(data);
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
