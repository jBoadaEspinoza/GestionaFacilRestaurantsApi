package com.api.GestionaFacilRestaurants.requests;

import com.api.GestionaFacilRestaurants.utilities.MD5Util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    private Long businessRuc;
    private String userName;
    private String userPassword;
    private Integer roleId = 0;
    private Integer moduleId = 1;

    public String getUserPassword(){
        return MD5Util.toMD5(this.userPassword);
    }
}
