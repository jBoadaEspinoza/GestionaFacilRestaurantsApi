package com.api.GestionaFacilRestaurants.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.api.GestionaFacilRestaurants.models.DocumentPayTypeHasPersonalDocumentType;
import com.api.GestionaFacilRestaurants.models.PersonalDocumentType;
import com.api.GestionaFacilRestaurants.repositories.DocumentPayTypeHasPersonalDocumentTypeRepository;
import com.api.GestionaFacilRestaurants.responses.SuccessResponse;
import com.api.GestionaFacilRestaurants.utilities.JwtUtil;

@Service
public class DocumentPayTypeService {
     @Autowired
    private JwtUtil jwt;

    @Autowired
    private DocumentPayTypeHasPersonalDocumentTypeRepository documentPayTypeHasPersonalDocumentTypeRepository;

    public Object getPersonalDocumentTypes(String token, Long documentPayTypeId){
        String tokenUpdated  = jwt.extendTokenExpiration(token);
        List<DocumentPayTypeHasPersonalDocumentType> types = documentPayTypeHasPersonalDocumentTypeRepository.findAllByDocumentPayTypeId(documentPayTypeId);
        List<PersonalDocumentType> data = types.stream().map(d -> {
            return d.getPersonalDocumentType();
        }).collect(Collectors.toList());
        return new SuccessResponse(data,tokenUpdated);
    }
    
}
