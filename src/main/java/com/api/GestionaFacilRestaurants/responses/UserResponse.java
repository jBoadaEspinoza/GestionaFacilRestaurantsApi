package com.api.GestionaFacilRestaurants.responses;
import com.api.GestionaFacilRestaurants.utilities.ApiUtil;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserResponse {
    private Long id;
    private Long referenceId;
    private String referenceModel;
    private Long ownerId;
    private String ownerFirstname;
    private String ownerLastname;
    private String ownerFullname;
    private String ownerImg;
    private Long businessRuc;
    private String businessName;
    private Long businessTypeId;
    private String businessTypeSingularNameEs;
    private String businessTypeSingularNameEn;
    private String businessTypePluralNameEs;
    private String businessTypePluralNameEn;
    private Long roleId;
    private String roleName;
    private Long moduleId;
    private String moduleSingularNameEs;
    private String modulePluralNameEs;

    public String getOwnerFirstname(){
        return ApiUtil.capitalizeEachWord(this.ownerFirstname);
    }
    public String getOwnerLastname(){
        return ApiUtil.capitalizeEachWord(this.ownerLastname);
    }
    public String getRoleName(){
        return ApiUtil.capitalizeEachWord(this.roleName);
    }
    public String getOwnerFullname(){
        return ApiUtil.capitalizeEachWord(this.ownerFullname);
    }
}
