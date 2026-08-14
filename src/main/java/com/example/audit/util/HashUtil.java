package com.example.audit.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility for computing SHA-256 hashes.
 *
 * Design notes:
 * - Uses Java's built-in MessageDigest with algorithm "SHA-256".
 * - A new MessageDigest instance is created per call to ensure thread-safety.
 * - Null input is treated as an empty string (this simplifies callers and keeps
 *   the hashing semantics deterministic). Empty string input returns the
 *   SHA-256 of the empty byte sequence.
 */
public final class HashUtil {
    private static final String ALGORITHM = "SHA-256";

    private HashUtil() {
        // utility class; prevent instantiation
    }

    /**
     * Compute the SHA-256 hash of the supplied input string and return it
     * as a lowercase hexadecimal string.
     *
     * Behavior for null/empty:
     * - null is treated the same as an empty string (hash of zero bytes).
     *
     * @param input the input string to hash (may be null)
     * @return lowercase hex-encoded SHA-256 digest
     * @throws IllegalStateException if SHA-256 algorithm is not available (very unlikely)
     */
    public static String sha256Hex(String input) {
        final byte[] bytes = (input == null ? "" : input).getBytes(StandardCharsets.UTF_8);
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            byte[] digest = md.digest(bytes);
            return bytesToHexLower(digest);
        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Convert a byte array to a lowercase hexadecimal string.
     *
     * @param bytes input byte array
     * @return lowercase hex representation
     */
    private static String bytesToHexLower(byte[] bytes) {
        char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
