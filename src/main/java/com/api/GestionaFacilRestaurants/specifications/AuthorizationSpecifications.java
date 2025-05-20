package com.api.GestionaFacilRestaurants.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.api.GestionaFacilRestaurants.models.Authorization;

public class AuthorizationSpecifications {
    public static Specification<Authorization> hasBusinessRuc(Long ruc) {
        return (root, query, builder) -> builder.equal(root.get("businessRuc"), ruc);
    }

    public static Specification<Authorization> hasUserModel(String model) {
        return (root, query, builder) -> builder.equal(root.get("userModel"), model);
    }

    public static Specification<Authorization> hasRoleId(Long roleId) {
        return (root, query, builder) -> builder.equal(root.get("roleId"), roleId);
    }

    public static Specification<Authorization> hasUserOwnerFullnameContaining(String denomination) {
        return (root, query, builder) -> builder.like(
                builder.lower(root.get("userOwnerFullname")),
                "%" + denomination.toLowerCase() + "%"
        );
    }
}
