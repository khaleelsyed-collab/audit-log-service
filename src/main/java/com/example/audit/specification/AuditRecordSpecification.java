package com.example.audit.specification;

import com.example.audit.entity.AuditRecord;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Specifications for dynamic AuditRecord queries.
 */
public final class AuditRecordSpecification {

    private AuditRecordSpecification() {
    }

    public static Specification<AuditRecord> byFilters(String actorId,
                                                        String resourceType,
                                                        String resourceId,
                                                        String eventType,
                                                        Instant from,
                                                        Instant to) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (actorId != null && !actorId.isEmpty()) {
                predicates.add(cb.equal(root.get("actorId"), actorId));
            }
            if (resourceType != null && !resourceType.isEmpty()) {
                predicates.add(cb.equal(root.get("resourceType"), resourceType));
            }
            if (resourceId != null && !resourceId.isEmpty()) {
                predicates.add(cb.equal(root.get("resourceId"), resourceId));
            }
            if (eventType != null && !eventType.isEmpty()) {
                predicates.add(cb.equal(root.get("eventType"), eventType));
            }
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("timestamp"), from));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("timestamp"), to));
            }

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
