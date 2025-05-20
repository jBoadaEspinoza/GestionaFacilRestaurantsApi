package com.api.GestionaFacilRestaurants.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {
    @Builder.Default
    private Long id = null;
    private String name;
    private String password;
    @Builder.Default
    private String photoUrl = null;
    private Long personId;
    private Long roleId;
    @Builder.Default
    private boolean active = true;
}
