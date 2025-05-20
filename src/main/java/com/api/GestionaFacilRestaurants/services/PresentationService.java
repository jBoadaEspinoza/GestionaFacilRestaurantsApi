package com.api.GestionaFacilRestaurants.services;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.api.GestionaFacilRestaurants.models.Business;
import com.api.GestionaFacilRestaurants.models.Presentation;
import com.api.GestionaFacilRestaurants.repositories.BusinessRepository;
import com.api.GestionaFacilRestaurants.repositories.PresentationRepository;
import com.api.GestionaFacilRestaurants.requests.PresentationRequest;
import com.api.GestionaFacilRestaurants.responses.ErrorResponse;
import com.api.GestionaFacilRestaurants.responses.PresentationResponse;
import com.api.GestionaFacilRestaurants.responses.SuccessResponse;
import com.api.GestionaFacilRestaurants.utilities.JwtUtil;

@Service
public class PresentationService {
    @Autowired
    private PresentationRepository presentationRepository;

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
        Pageable pageable=PageRequest.of(skip,limit,Sort.by(sortDirection,"denomination"));
        List<PresentationResponse> data = presentationRepository
                    .findAllByBusinessId(businessFinded.getId(),pageable)
                    .getContent()
                    .stream()
                    .map(p->{
                        return new PresentationResponse(
                            p.getId(),
                            p.getDenomination()
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
        Pageable pageable=PageRequest.of(skip,limit,Sort.by(sortDirection,"denomination"));
        List<PresentationResponse> data = presentationRepository
                                    .findAllByBusinessIdAndDenominationContainingIgnoreCase(businessFinded.getId(),denomination,pageable)
                                    .getContent()
                                    .stream()
                                    .map(p->{
                                        return new PresentationResponse(
                                            p.getId(),
                                            p.getDenomination()
                                        );
                                    }).collect(Collectors.toList());
        
        return new SuccessResponse(data,tokenUpdated);
    }    

    public Object save(String token,PresentationRequest input){
        String tokenUpdated = jwt.extendTokenExpiration(token);
        Long ruc = jwt.extractBusinessRuc(token);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
        
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }
        
        if(input.getDenomination().isEmpty()){
            return new ErrorResponse("required","La denominacion es requerida");
        }
        
        Presentation presentationToInsert = Presentation.builder()
            .id(input.getId())
            .denomination(input.getDenomination())
            .business(businessFinded)
            .build(); 
        Presentation presentationInserted = presentationRepository.save(presentationToInsert);
        PresentationResponse data = PresentationResponse.builder()
            .id(presentationInserted.getId())
            .denomination(presentationInserted.getDenomination())
            .build();
        return new SuccessResponse(data,tokenUpdated,input.getId()==null ? "Registro creado con exito" : "Registro actualizado con exito");
    }
}
