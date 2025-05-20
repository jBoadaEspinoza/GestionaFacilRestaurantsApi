package com.api.GestionaFacilRestaurants.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.api.GestionaFacilRestaurants.models.Business;
import com.api.GestionaFacilRestaurants.models.TableBusiness;
import com.api.GestionaFacilRestaurants.models.TableView;
import com.api.GestionaFacilRestaurants.repositories.BusinessRepository;
import com.api.GestionaFacilRestaurants.repositories.TableBusinessRepository;
import com.api.GestionaFacilRestaurants.repositories.TableViewRepository;
import com.api.GestionaFacilRestaurants.requests.TableRequest;
import com.api.GestionaFacilRestaurants.responses.ErrorResponse;
import com.api.GestionaFacilRestaurants.responses.SuccessResponse;
import com.api.GestionaFacilRestaurants.utilities.JwtUtil;

@Service
public class TableService {
    @Autowired
    private JwtUtil jwt;
    
    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private TableBusinessRepository tableBusinessRepository;

    @Autowired
    private TableViewRepository tableViewRepository;

    public Object get(String token,String denomination,Integer skip,Integer limit,boolean active,Sort.Direction sortDirection){
        Pageable pageable=PageRequest.of(skip,limit,Sort.by(sortDirection,"id"));
        
        Long businessRuc = jwt.extractBusinessRuc(token);
        
        Business businessFinded = businessRepository.findByRuc(businessRuc).orElse(null);
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }
        Long businessId = businessFinded.getId();
        Page<TableView> page = null;
        if(denomination==null){
            page = tableViewRepository.findAllByBusinessIdAndActive(businessId,active,pageable);
        }else{
            if(denomination.length()>0){
                page = tableViewRepository.findAllByDenominationAndBusinessIdAndActive(denomination,businessId,active,pageable);
            }else{
                page = tableViewRepository.findAllByBusinessIdAndActive(businessId,active,pageable);
            }
        }
        
        List<TableView> data = page.getContent();
        Long totalTablesFinded = tableViewRepository.countByBusinessId(businessId);
        String tokenUpdated = jwt.extendTokenExpiration(token);

        return new SuccessResponse(data, tokenUpdated,totalTablesFinded);
    }
    public Object changeStatus(String token, Long id, boolean status){
        String tokenUpdate = jwt.extendTokenExpiration(token);
        Long ruc = jwt.extractBusinessRuc(tokenUpdate);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
        
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }
        TableBusiness tableFinded = tableBusinessRepository.findById(id).orElse(null);
        if(tableFinded==null){
            return new ErrorResponse("table_not_found","Mesa no encontrada");
        }
        tableFinded.setActive(status);
        TableBusiness tableUpdated = tableBusinessRepository.save(tableFinded);
        return new SuccessResponse(tableUpdated,tokenUpdate,"Estado de mesa actualizado exitosamente");
    }
    public Object save(String token,TableRequest input){
        String tokenUpdate = jwt.extendTokenExpiration(token);
        Long ruc = jwt.extractBusinessRuc(tokenUpdate);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
        
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }

        if(input.getDenomination().length()==0){
            return new ErrorResponse("field_required", "Campo denominacion requerido");
        }
        TableBusiness tableToInsert;
        if(input.getId()!=null){
            TableBusiness tableFinded = tableBusinessRepository.findById(input.getId()).orElse(null);
            if(tableFinded==null){
                return new ErrorResponse("table_not_found","Mesa no encontrada");
            }
            tableToInsert = TableBusiness.builder()
                .id(input.getId())
                .denomination(input.getDenomination())
                .businessId(businessFinded.getId())
                .active(input.isActive())
                .build();
        }else{
            tableToInsert = TableBusiness.builder()
                .denomination(input.getDenomination())
                .businessId(businessFinded.getId())
                .active(input.isActive())
                .build();
        }
        TableBusiness tableInserted = tableBusinessRepository.save(tableToInsert);
        return new SuccessResponse(tableInserted,tokenUpdate,input.getId()==null ? "Registro creado exitosamente" : "Registro actualizado exitosamente");
    }
}
