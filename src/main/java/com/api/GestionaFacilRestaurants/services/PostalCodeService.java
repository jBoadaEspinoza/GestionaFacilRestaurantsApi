package com.api.GestionaFacilRestaurants.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.api.GestionaFacilRestaurants.models.PostalCode;
import com.api.GestionaFacilRestaurants.repositories.PostalCodeRepository;
import com.api.GestionaFacilRestaurants.responses.SuccessResponse;
import com.api.GestionaFacilRestaurants.utilities.JwtUtil;

@Service
public class PostalCodeService {
    @Autowired
    private PostalCodeRepository postalCodeRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public Object get(String token,Integer skip,Integer limit,Sort.Direction sortDirection){
        Pageable pageable=PageRequest.of(skip,limit,Sort.by(sortDirection,"countryNameEs"));
        String tokenUpdated = jwtUtil.extendTokenExpiration(token);
        Page<PostalCode> data = postalCodeRepository.findAll(pageable);

        Long total_register = postalCodeRepository.count();
        return new SuccessResponse(data.getContent(),tokenUpdated,total_register);
    }
}
