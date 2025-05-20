package com.api.GestionaFacilRestaurants.specifications;

import org.springframework.data.jpa.domain.Specification;
import com.api.GestionaFacilRestaurants.models.MenuItemView;

public class MenuItemViewSpecifications {
    public static Specification<MenuItemView> hasBusinessId(Long businessId) {
        return (root, query, builder) -> builder.equal(root.get("business").get("id"), businessId);
    }
    public static Specification<MenuItemView> hasCategoryId(Long categoryId){
        return (root, query, builder) -> builder.equal(root.get("category").get("id"), categoryId);
    }
    public static Specification<MenuItemView> hasFullDenominationContaining(String denomination){
        return (root,query,builder) -> builder.like(
            builder.lower(root.get("fullDenomination")),
                "%" + denomination.toLowerCase() + "%"
        );
    }
}
