package com.api.GestionaFacilRestaurants.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.GestionaFacilRestaurants.models.Business;
import com.api.GestionaFacilRestaurants.repositories.BusinessRepository;
import com.api.GestionaFacilRestaurants.repositories.RoleRepository;
import com.api.GestionaFacilRestaurants.responses.ErrorResponse;
import com.api.GestionaFacilRestaurants.responses.RoleResponse;
import com.api.GestionaFacilRestaurants.responses.SuccessResponse;
import com.api.GestionaFacilRestaurants.utilities.JwtUtil;

@Service
public class RoleService {
    @Autowired
    private JwtUtil jwt;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BusinessRepository businessRepository;

    public Object get(String token){
        String tokenUpdated = jwt.extendTokenExpiration(token);
        Long ruc = jwt.extractBusinessRuc(token);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
        
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }

        List<RoleResponse> data = roleRepository.findAllByModuleId(businessFinded.getType().getModule().getId())
                .stream()
                .map(r->{
                    return RoleResponse.builder()
                        .id(r.getId())
                        .denominationEs(r.getRoleDenomination().getDenominationEs())
                        .denominationEn(r.getRoleDenomination().getDenominationEn())
                        .dispatchAreaAccess(r.isAccessControlDispatch())
                        .active(r.isActive())
                        .build();
                }).collect(Collectors.toList()); 
        return new SuccessResponse(data,tokenUpdated);
    }
}
