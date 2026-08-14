package com.example.audit.util;

import java.time.Instant;

/**
 * Utility for constructing a deterministic, canonical representation of an
 * audit record suitable for hashing. This class is stateless and provides
 * simple string-based canonicalization used by the service layer.
 *
 * Note: callers that include structured payloads (JSON) should ensure
 * payload canonicalization (stable key ordering) before passing the payload
 * string to this utility to guarantee cross-platform determinism.
 */
public final class CanonicalRecordUtil {

    private CanonicalRecordUtil() {
        // utility class
    }

    /**
     * Build a deterministic canonical string for an audit record. Fields are
     * appended in a fixed order with explicit separators.
     *
     * @param eventType    event type
     * @param actorId      actor identifier
     * @param resourceType resource type
     * @param resourceId   resource identifier
     * @param payload      payload as text (caller should canonicalize structured payloads)
     * @param timestamp    event timestamp (non-null)
     * @param sequence     sequence number
     * @param previousHash previous record hash or genesis value
     * @return canonical string suitable for hashing
     */
    public static String buildCanonicalString(String eventType,
                                              String actorId,
                                              String resourceType,
                                              String resourceId,
                                              String payload,
                                              Instant timestamp,
                                              long sequence,
                                              String previousHash) {
        StringBuilder sb = new StringBuilder();
        sb.append("eventType:").append(nullSafe(eventType)).append('|');
        sb.append("actorId:").append(nullSafe(actorId)).append('|');
        sb.append("resourceType:").append(nullSafe(resourceType)).append('|');
        sb.append("resourceId:").append(nullSafe(resourceId)).append('|');
        sb.append("payload:").append(nullSafe(payload)).append('|');
        sb.append("timestamp:").append(timestamp.toString()).append('|');
        sb.append("sequence:").append(sequence).append('|');
        sb.append("previousHash:").append(nullSafe(previousHash));
        return sb.toString();
    }

    /**
     * Normalize null strings to empty string for deterministic concatenation.
     *
     * @param s input string
     * @return non-null string
     */
    public static String nullSafe(String s) {
        return s == null ? "" : s;
    }
}
