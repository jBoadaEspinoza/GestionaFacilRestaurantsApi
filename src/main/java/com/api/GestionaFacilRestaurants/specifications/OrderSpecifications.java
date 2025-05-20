package com.api.GestionaFacilRestaurants.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.api.GestionaFacilRestaurants.models.Order;

public class OrderSpecifications {
    public static Specification<Order> hasBusinessId(Long businessId) {
        return (root, query, builder) -> builder.equal(root.get("business").get("id"), businessId);
    }

    public static Specification<Order> hasWaiterId(Long waiterId) {
        return (root, query, builder) -> builder.equal(root.get("waiterId"), waiterId);
    }

    public static Specification<Order> isClosed(boolean closed) {
        return (root, query, builder) -> builder.equal(root.get("closed"), closed);
    }
    public static Specification<Order> orderByClosingDate(String asc) {
        return (root, query, builder) -> {
            if (asc.equals("asc")) {
                query.orderBy(builder.asc(root.get("closingDate")));
            } else {
                query.orderBy(builder.desc(root.get("closingDate")));
            }
            return query.getRestriction();
        };
    }
    public static Specification<Order> hasCustomerId(Long customerId) {
        return (root, query, builder) -> builder.equal(root.get("customer").get("id"), customerId);
    }
}
