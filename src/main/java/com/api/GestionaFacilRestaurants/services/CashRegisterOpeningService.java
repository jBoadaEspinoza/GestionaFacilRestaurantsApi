package com.api.GestionaFacilRestaurants.services;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.api.GestionaFacilRestaurants.models.Business;
import com.api.GestionaFacilRestaurants.models.CashRegister;
import com.api.GestionaFacilRestaurants.models.CashRegisterOpening;
import com.api.GestionaFacilRestaurants.models.CashRegisterOpeningItems;
import com.api.GestionaFacilRestaurants.models.CashRegisterOpeningView;
import com.api.GestionaFacilRestaurants.models.User;
import com.api.GestionaFacilRestaurants.repositories.BusinessRepository;
import com.api.GestionaFacilRestaurants.repositories.CashRegisterOpeningItemsRepository;
import com.api.GestionaFacilRestaurants.repositories.CashRegisterOpeningRepository;
import com.api.GestionaFacilRestaurants.repositories.CashRegisterOpeningViewRepository;
import com.api.GestionaFacilRestaurants.repositories.CashRegisterRepository;
import com.api.GestionaFacilRestaurants.repositories.UserRepository;
import com.api.GestionaFacilRestaurants.requests.CashRegisterClosingRequest;
import com.api.GestionaFacilRestaurants.requests.CashRegisterOpeningRequest;
import com.api.GestionaFacilRestaurants.responses.ErrorResponse;
import com.api.GestionaFacilRestaurants.responses.SuccessResponse;
import com.api.GestionaFacilRestaurants.utilities.ApiUtil;
import com.api.GestionaFacilRestaurants.utilities.DateTimeUtil;
import com.api.GestionaFacilRestaurants.utilities.JwtUtil;
import com.api.GestionaFacilRestaurants.utilities.KeyUtil;

@Service
public class CashRegisterOpeningService {
    @Value("${api.host}")
    private String host;

    @Value("${api.base.path}")
    private String apiBasePath;

    @Autowired
    private CashRegisterOpeningItemsRepository cashRegisterOpeningItemsRepository;
    @Autowired
    private CashRegisterOpeningViewRepository cashRegisterOpeningViewRepository;

    @Autowired
    private CashRegisterOpeningRepository cashRegisterOpeningRepository;

    @Autowired
    private JwtUtil jwt;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CashRegisterRepository cashRegisterRepository;

    public Object get(String token){
        Long ruc = jwt.extractBusinessRuc(token);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
       
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }
        List<CashRegisterOpeningView> data = cashRegisterOpeningViewRepository.findAllByBusinessId(businessFinded.getId());
        String tokenUpdate = jwt.extendTokenExpiration(token);
        return new SuccessResponse(data,tokenUpdate);
    }

    public Object open(String token,CashRegisterOpeningRequest input){
        String tokenUpdated = jwt.extendTokenExpiration(token);
        Long ruc = jwt.extractBusinessRuc(token);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
       
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }

        User cashier = userRepository.findById(input.getCashierId()).orElse(null);
        if(cashier==null){
            return new ErrorResponse("cashier_not_found","Cajero no encontrado");
        }
        
        if(cashier.getBusinessId()!=businessFinded.getId()){
            return new ErrorResponse("cashier_not_associated_to_business","Cajero no asociado a establecimiento");
        }

        if(cashier.getRole().getRoleDenomination().getId()!=2){
            return new ErrorResponse("cashierId_invalid", "Cajero ID no valido");
        }

        CashRegister cashRegisterFinded = cashRegisterRepository.findById(input.getCashRegisterId()).orElse(null);
        if(cashRegisterFinded==null){
            return new ErrorResponse("cashRegister_not_found","Caja no encontrada");
        }

       
        if(cashRegisterFinded.getBusinessId()!=businessFinded.getId()){
            return new ErrorResponse("cashRegister_not_associate_to_business","Caja no asociada al establecimiento");
        }

        CashRegisterOpening cashRegisterOpeningToInsert = CashRegisterOpening.builder()
            .openingDate(DateTimeUtil.getFormattedCurrentUtcDateTime())
            .cashierId(cashier.getId())
            .cashRegisterId(cashRegisterFinded.getId())
            .build();

        
        CashRegisterOpening cashRegisterOpeningCreated = cashRegisterOpeningRepository.save(cashRegisterOpeningToInsert);
        
        //Agregamos el monto inicial
        CashRegisterOpeningItems cashRegisterOpeningItemsToInsert = CashRegisterOpeningItems.builder()
            .cashRegisterOpeningId(cashRegisterOpeningCreated.getId())
            .itemDescription("m.inicial")
            .creationDate(DateTimeUtil.getFormattedCurrentUtcDateTime())
            .currency(input.getCurrency())
            .amount(input.getAmount())
            .type("input")
            .businessId(businessFinded.getId())
            .build();

        cashRegisterOpeningItemsRepository.save(cashRegisterOpeningItemsToInsert);
        return new SuccessResponse(null,tokenUpdated,"Caja aperturada con exito");
    }
    public Object close(String token,CashRegisterClosingRequest input){
        String tokenUpdated = jwt.extendTokenExpiration(token);
        Long ruc = jwt.extractBusinessRuc(token);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
        
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }

        CashRegisterOpeningView cashRegisterOpeningFinded = cashRegisterOpeningViewRepository.findByCashRegisterId(input.getCashRegisterId()).orElse(null);
       if(cashRegisterOpeningFinded==null){
            return new ErrorResponse("cash_register_opening_not_found","La caja ya se encuentra cerrado");
        }
        
        CashRegisterOpening cashRegisterOpeningToClose = cashRegisterOpeningRepository.findById(cashRegisterOpeningFinded.getId()).orElse(null);
        if(cashRegisterOpeningToClose==null){
            return new ErrorResponse("cash_register_opening_not_found","registro no encontrado");
        }

        cashRegisterOpeningToClose.setClosingDate(DateTimeUtil.getFormattedCurrentUtcDateTime());
        CashRegisterOpening cashRegisterOpeningClosed = cashRegisterOpeningRepository.save(cashRegisterOpeningToClose);

        String pattern = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = LocalDateTime.parse(cashRegisterOpeningClosed.getClosingDate(), formatter);

        String fileName = businessFinded.getRuc()+"-"+ApiUtil.lzeros(cashRegisterOpeningClosed.getId(), 10);

        Map<String,Object> data = new LinkedHashMap<>();
            data.put("issueTime",DateTimeUtil.convertToUnixMillis(localDateTime));
            data.put("fileName",fileName);
            data.put("pdfUrl",this.getFullPath()+"/reports/cash-opening/"+KeyUtil.encrypt(cashRegisterOpeningClosed.getId().toString())+"/getPDF/58mm/"+fileName+".pdf");
        return new SuccessResponse(data,tokenUpdated,"Caja cerrada con exito");
    }
    private String getFullPath() {
        return host + apiBasePath;
    }
}
