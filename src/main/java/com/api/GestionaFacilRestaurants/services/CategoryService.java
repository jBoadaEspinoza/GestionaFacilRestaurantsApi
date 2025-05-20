package com.api.GestionaFacilRestaurants.services;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.api.GestionaFacilRestaurants.models.Business;
import com.api.GestionaFacilRestaurants.models.Category;
import com.api.GestionaFacilRestaurants.repositories.BusinessRepository;
import com.api.GestionaFacilRestaurants.repositories.CategoryRepository;
import com.api.GestionaFacilRestaurants.requests.CategoryRequest;
import com.api.GestionaFacilRestaurants.responses.CategoryResponse;
import com.api.GestionaFacilRestaurants.responses.ErrorResponse;
import com.api.GestionaFacilRestaurants.responses.SuccessResponse;
import com.api.GestionaFacilRestaurants.utilities.JwtUtil;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

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
        Pageable pageable=PageRequest.of(skip,limit,Sort.by(sortDirection,"denominationPerGroup"));
        List<CategoryResponse> data = categoryRepository
                    .findAllByBusinessId(businessFinded.getId(),pageable)
                    .getContent()
                    .stream()
                    .map(c->{
                        return new CategoryResponse(
                            c.getId(),
                            c.getDenominationPerUnit(),
                            c.getDenominationPerGroup(),
                            c.getUrl()
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
        Pageable pageable=PageRequest.of(skip,limit,Sort.by(sortDirection,"denominationPerGroup"));
        List<CategoryResponse> data = categoryRepository
                                    .findAllByBusinessIdAndDenominationPerGroupContainingIgnoreCase(businessFinded.getId(),denomination,pageable)
                                    .getContent()
                                    .stream()
                                    .map(c->{
                                        return new CategoryResponse(
                                            c.getId(),
                                            c.getDenominationPerUnit(),
                                            c.getDenominationPerGroup(),
                                            c.getUrl()
                                        );
                                    }).collect(Collectors.toList());
        
        return new SuccessResponse(data,tokenUpdated);
    }  
    
    public Object save(String token, CategoryRequest input){
        Long ruc = jwt.extractBusinessRuc(token);
        String tokenUpdated = jwt.extendTokenExpiration(token);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }
        if(input.getDenominationPerUnit().isEmpty()){
            return new ErrorResponse("required", "La denominacion por unidad es requerida");
        }
        if(input.getDenominationPerGroup().isEmpty()){
            return new ErrorResponse("required", "La denominacion por grupo es requerida");
        }
        Category categoryToInsert = Category.builder()
            .id(input.getId())
            .denominationPerUnit(input.getDenominationPerUnit())
            .denominationPerGroup(input.getDenominationPerGroup())
            .url(input.getImageUrl())
            .business(businessFinded)
            .build();

        Category categoryInserted = categoryRepository.save(categoryToInsert);
        CategoryResponse data = CategoryResponse.builder()
            .id(categoryInserted.getId())
            .denominationPerUnit(categoryInserted.getDenominationPerUnit())
            .denominationPerGroup(categoryInserted.getDenominationPerGroup())
            .url(categoryInserted.getUrl())
            .build();
        return new SuccessResponse(data,tokenUpdated,input.getId()==null ? "Registro creado con exito" : "Registro actualizado con exito");
    }
}
