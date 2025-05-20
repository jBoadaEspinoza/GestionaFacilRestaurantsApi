package com.api.GestionaFacilRestaurants.specifications;
import org.springframework.data.jpa.domain.Specification;
import com.api.GestionaFacilRestaurants.models.DocumentPayIssued;

public class DocumentsPayIssuedSpecifications {
    public static Specification<DocumentPayIssued> hasBusinessId(Long businessId) {
        return (root, query, builder) -> builder.equal(root.get("order").get("business").get("id"), businessId);
    }
    public static Specification<DocumentPayIssued> isCancelled() {
        return (root, query, builder) -> builder.isTrue(root.get("cancelled"));
    }
    public static Specification<DocumentPayIssued> isNotCancelled() {
        return (root, query, builder) -> builder.isFalse(root.get("cancelled"));
    }
    public static Specification<DocumentPayIssued> hasUserId(Long userId) {
        return (root, query, builder) -> builder.equal(root.get("order").get("waiterId"), userId);
    }
    public static Specification<DocumentPayIssued> hasPaymentMethod(Long paymentMethodId) {
        return (root, query, builder) -> builder.equal(root.get("paymentMethod").get("id"), paymentMethodId);
    }
    public static Specification<DocumentPayIssued> opened(){
        return (root, query, builder) -> {
            // Join explícito no es necesario porque ya tenemos la relación mapeada
            return builder.or(
                builder.isNull(root.get("cashRegisterOpening").get("closingDate")),
                builder.equal(root.get("cashRegisterOpening").get("closingDate"), "0000-00-00 00:00:00"),
                builder.equal(root.get("cashRegisterOpening").get("closingDate"), "")
            );
        };
    }
}
