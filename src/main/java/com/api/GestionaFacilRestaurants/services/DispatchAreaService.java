package com.api.GestionaFacilRestaurants.services;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.api.GestionaFacilRestaurants.models.Business;
import com.api.GestionaFacilRestaurants.repositories.BusinessRepository;
import com.api.GestionaFacilRestaurants.repositories.DispatchAreaRepository;
import com.api.GestionaFacilRestaurants.responses.ErrorResponse;
import com.api.GestionaFacilRestaurants.responses.DispatchAreaResponse;
import com.api.GestionaFacilRestaurants.responses.SuccessResponse;
import com.api.GestionaFacilRestaurants.utilities.JwtUtil;

@Service
public class DispatchAreaService {
    @Autowired
    private DispatchAreaRepository dispatchAreaRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private JwtUtil jwt;

    public Object get(String token,Integer skip,Integer limit,Sort.Direction sortDirection){
        String tokenUpdated = jwt.extendTokenExpiration(token);
        Business businessFinded = businessRepository.findByRuc(jwt.extractBusinessRuc(token)).orElse(null);
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Negocio no encontrado");
        }
        Pageable pageable=PageRequest.of(skip,limit,Sort.by(sortDirection,"denominationPluralEs"));
        List<DispatchAreaResponse> data = dispatchAreaRepository
                    .findAll(pageable)
                    .getContent()
                    .stream()
                    .map(p->{
                        return new DispatchAreaResponse(
                            p.getId(),
                            p.getDenominationSingularEs(),
                            p.getDenominationPluralEs(),
                            p.isActive()
                        );
                    }).collect(Collectors.toList());
        return new SuccessResponse(data,tokenUpdated);
    }
    
    public Object getByDenomination(String token, String denomination, Integer skip,Integer limit,Sort.Direction sortDirection){
        String tokenUpdated = jwt.extendTokenExpiration(token);
        Business businessFinded = businessRepository.findByRuc(jwt.extractBusinessRuc(token)).orElse(null);
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Negocio no encontrado");
        }
        Pageable pageable=PageRequest.of(skip,limit,Sort.by(sortDirection,"denominationPluralEs"));
        List<DispatchAreaResponse> data = dispatchAreaRepository
                                    .findAllByDenominationPluralEsContainingIgnoreCase(denomination,pageable)
                                    .getContent()
                                    .stream()
                                    .map(p->{
                                        return new DispatchAreaResponse(
                                            p.getId(),
                                            p.getDenominationSingularEs(),
                                            p.getDenominationPluralEs(),
                                            p.isActive()
                                        );
                                    }).collect(Collectors.toList());
        
        return new SuccessResponse(data,tokenUpdated);
    }    
}
