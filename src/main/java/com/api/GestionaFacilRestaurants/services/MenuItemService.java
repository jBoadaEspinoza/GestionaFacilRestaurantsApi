package com.api.GestionaFacilRestaurants.services;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.api.GestionaFacilRestaurants.models.Business;
import com.api.GestionaFacilRestaurants.models.Category;
import com.api.GestionaFacilRestaurants.models.DispatchArea;
import com.api.GestionaFacilRestaurants.models.MenuItem;
import com.api.GestionaFacilRestaurants.models.MenuItemView;
import com.api.GestionaFacilRestaurants.models.Presentation;
import com.api.GestionaFacilRestaurants.repositories.BusinessRepository;
import com.api.GestionaFacilRestaurants.repositories.CategoryRepository;
import com.api.GestionaFacilRestaurants.repositories.DispatchAreaRepository;
import com.api.GestionaFacilRestaurants.repositories.MenuItemRepository;
import com.api.GestionaFacilRestaurants.repositories.MenuItemViewRepository;
import com.api.GestionaFacilRestaurants.repositories.PresentationRepository;
import com.api.GestionaFacilRestaurants.requests.MenuItemRequest;
import com.api.GestionaFacilRestaurants.responses.CategoryResponse;
import com.api.GestionaFacilRestaurants.responses.DispatchAreaResponse;
import com.api.GestionaFacilRestaurants.responses.ErrorResponse;
import com.api.GestionaFacilRestaurants.responses.MenuItemResponse;
import com.api.GestionaFacilRestaurants.responses.PresentationResponse;
import com.api.GestionaFacilRestaurants.responses.SuccessResponse;
import com.api.GestionaFacilRestaurants.specifications.MenuItemViewSpecifications;
import com.api.GestionaFacilRestaurants.utilities.JwtUtil;

@Service
public class MenuItemService {
    @Autowired
    private JwtUtil jwt;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PresentationRepository presentationRepository;

    @Autowired
    private DispatchAreaRepository dispatchAreaRepository;

    @Autowired
    private MenuItemViewRepository menuItemViewRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    public Object get(String token,String denomination,Long categoryId,Integer skip,Integer limit,Sort.Direction sortDirection){
         // Extiende la validez del token
        String tokenUpdated = jwt.extendTokenExpiration(token);
        
        // Configura la paginación
        Pageable pageable=PageRequest.of(skip,limit,Sort.by(sortDirection,"totalPedido"));
        
        // Extrae el RUC del negocio desde el token
        Long ruc = jwt.extractBusinessRuc(token);

        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
        
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }
        
        Specification<MenuItemView> spec = Specification.where(MenuItemViewSpecifications.hasBusinessId(businessFinded.getId()));

        if(denomination!=null && !denomination.isEmpty()){
            spec = spec.and(MenuItemViewSpecifications.hasFullDenominationContaining(denomination));
        }
        if(categoryId!=null){
            spec = spec.and(MenuItemViewSpecifications.hasCategoryId(categoryId));
        }

        List<MenuItemView> list = menuItemViewRepository.findAll(spec, pageable).getContent();
        List<MenuItemResponse> data = list
            .stream()
            .map(item ->{
                CategoryResponse categoryResponse=CategoryResponse.builder()
                    .id(item.getCategory().getId())
                    .denominationPerUnit(item.getCategory().getDenominationPerUnit())
                    .denominationPerGroup(item.getCategory().getDenominationPerGroup())
                    .build();
                
                PresentationResponse presentationResponse=PresentationResponse.builder()
                    .id(item.getPresentation().getId())
                    .denomination(item.getPresentation().getDenomination())
                    .build();
                
                DispatchAreaResponse dispatchAreaResponse = DispatchAreaResponse.builder()
                    .id(item.getDispatchArea().getId())
                    .denominationSingularEs(item.getDispatchArea().getDenominationSingularEs())
                    .denominationPluralEs(item.getDispatchArea().getDenominationPluralEs())
                    .active(item.getDispatchArea().isActive())
                    .build();

                return MenuItemResponse.builder()
                    .id(item.getId())
                    .barcode(item.getBarcode())
                    .denomination(item.getDenomination())
                    .denominationFull(item.getFullDenomination())
                    .description(item.getDescription())
                    .category(categoryResponse)
                    .presentation(presentationResponse)
                    .dispatchArea(dispatchAreaResponse)
                    .imageUrl(item.getUrlImage())
                    .pricePen(item.getPricePen())
                    .active(item.isActive())
                    .build();
            }).collect(Collectors.toList());

        Long  totalRegisters = menuItemViewRepository.countByBusinessId(businessFinded.getId());
        return new SuccessResponse(data,tokenUpdated, totalRegisters);
    }
    
    public Object save(String token, MenuItemRequest input){
        Long ruc = jwt.extractBusinessRuc(token);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }

        if(input.getDenomination().isEmpty()){
            return new ErrorResponse("required", "El campo denominacion es obligatorio");
        }

        if(input.getPricePen()==null || input.getPricePen()==0){
            return new ErrorResponse("required","El campo precio unitario es obligatorio");
        }

        //Validamos Category Id
        if(input.getCategoryId()==null){
            return new ErrorResponse("required","El campo categoria es obligatorio");
        }

        Category categoryFinded = categoryRepository.findById(input.getCategoryId()).orElse(null);
        if(categoryFinded==null){
            return new ErrorResponse("category_not_found","Categoria no encontrada");
        }

        //Validamos Presentation Id
        if(input.getPresentationId()==null){
            return new ErrorResponse("required","El campo presentacion es obligatorio");
        }

        Presentation presentationFinded = presentationRepository.findById(input.getPresentationId()).orElse(null);
        if(presentationFinded==null){
            return new ErrorResponse("presentation_not_found","Presentacion no encontrada");
        }

        //Validamos Dispatch Area Id
        if(input.getDispatchAreaId()==null){
            return new ErrorResponse("required","El campo area de despacho es obligatorio");
        }

        DispatchArea dispatchAreaFinded = dispatchAreaRepository.findById(input.getDispatchAreaId()).orElse(null);
        if(dispatchAreaFinded==null){
            return new ErrorResponse("presentation_not_found","Presentacion no encontrada");
        }
        MenuItem menuItemToSave = MenuItem.builder()
            .id(input.getId())
            .denomination(input.getDenomination())
            .description(input.getDescription())
            .Category(Category.builder().id(input.getCategoryId()).build())
            .presentation(Presentation.builder().id(input.getPresentationId()).build())
            .dispatchArea(DispatchArea.builder().id(input.getDispatchAreaId()).build())
            .pricePen(input.getPricePen())
            .urlImage(input.getImageUrl())
            .barcode(input.getBarcode())
            .business(Business.builder().id(businessFinded.getId()).build())
            .active(true)
            .build();
        
        MenuItem menuItemSaved = menuItemRepository.save(menuItemToSave);
        Long menuItemId = menuItemSaved.getId();
        MenuItemView menuItemCurrent = menuItemViewRepository.findById(menuItemId).orElse(null);
        String tokenUpdated = jwt.extendTokenExpiration(token);
        return new SuccessResponse(menuItemCurrent,tokenUpdated,input.getId()==null ? "Registro creado con exito" : "Registro actualizado con exito");
    }

    public Object rename(String token,Long id,String denomination){
        Long ruc = jwt.extractBusinessRuc(token);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
        
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }

        if(denomination.isEmpty()){
            return new ErrorResponse("required", "El campo denominacion es obligatorio");
        }

        MenuItemView menuItemFinded = menuItemViewRepository.findByIdAndBusinessId(id, businessFinded.getId()).orElse(null);
       
        if(menuItemFinded==null){
            return new ErrorResponse("menu_item_not_found","Menu item no encontrado");
        }
        
        MenuItem menuItemToUpdate = MenuItem.builder()
            .id(menuItemFinded.getId())
            .denomination(denomination)
            .description(menuItemFinded.getDescription())
            .Category(menuItemFinded.getCategory())
            .presentation(menuItemFinded.getPresentation())
            .dispatchArea(menuItemFinded.getDispatchArea())
            .pricePen(menuItemFinded.getPricePen())
            .urlImage(menuItemFinded.getUrlImage())
            .barcode(menuItemFinded.getBarcode())
            .business(menuItemFinded.getBusiness())
            .active(menuItemFinded.isActive())
            .build();

        MenuItem menuItemSaved = menuItemRepository.save(menuItemToUpdate);
        
        Long menuItemId = menuItemSaved.getId();
        MenuItemView menuItemCurrent = menuItemViewRepository.findById(menuItemId).orElse(null);

        String tokenUpdated = jwt.extendTokenExpiration(token);
        return new SuccessResponse(menuItemCurrent,tokenUpdated,"Registro actualizado con exito");
    }

    public Object changeCategory(String token,Long id,Long categoryId){
        Long ruc = jwt.extractBusinessRuc(token);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
        
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }

        MenuItemView menuItemFinded = menuItemViewRepository.findByIdAndBusinessId(id, businessFinded.getId()).orElse(null);
       
        if(menuItemFinded==null){
            return new ErrorResponse("menu_item_not_found","Menu item no encontrado");
        }

        Category categoryFinded = categoryRepository.findById(categoryId).orElse(null);
        if(categoryFinded==null){
            return new ErrorResponse("category_not_found","Categoria no encontrada");
        }

        MenuItem menuItemToUpdate = MenuItem.builder()
            .id(menuItemFinded.getId())
            .denomination(menuItemFinded.getDenomination())
            .description(menuItemFinded.getDescription())
            .Category(categoryFinded)
            .presentation(menuItemFinded.getPresentation())
            .dispatchArea(menuItemFinded.getDispatchArea())
            .pricePen(menuItemFinded.getPricePen())
            .urlImage(menuItemFinded.getUrlImage())
            .barcode(menuItemFinded.getBarcode())
            .business(menuItemFinded.getBusiness())
            .active(menuItemFinded.isActive())
            .build();

        MenuItem menuItemSaved = menuItemRepository.save(menuItemToUpdate);
        
        Long menuItemId = menuItemSaved.getId();
        MenuItemView menuItemCurrent = menuItemViewRepository.findById(menuItemId).orElse(null);

        String tokenUpdated = jwt.extendTokenExpiration(token);
        return new SuccessResponse(menuItemCurrent,tokenUpdated,"Registro actualizado con exito");
    }

    public Object changePresentation(String token,Long id,Long presentationId){
        Long ruc = jwt.extractBusinessRuc(token);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
        
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }

        MenuItemView menuItemFinded = menuItemViewRepository.findByIdAndBusinessId(id, businessFinded.getId()).orElse(null);
       
        if(menuItemFinded==null){
            return new ErrorResponse("menu_item_not_found","Menu item no encontrado");
        }

        Presentation presentationFinded = presentationRepository.findById(presentationId).orElse(null);
        if(presentationFinded==null){
            return new ErrorResponse("presentation_not_found","Presentacion no encontrada");
        }

        MenuItem menuItemToUpdate = MenuItem.builder()
            .id(menuItemFinded.getId())
            .denomination(menuItemFinded.getDenomination())
            .description(menuItemFinded.getDescription())
            .Category(menuItemFinded.getCategory())
            .presentation(presentationFinded)
            .dispatchArea(menuItemFinded.getDispatchArea())
            .pricePen(menuItemFinded.getPricePen())
            .urlImage(menuItemFinded.getUrlImage())
            .barcode(menuItemFinded.getBarcode())
            .business(menuItemFinded.getBusiness())
            .active(menuItemFinded.isActive())
            .build();

        MenuItem menuItemSaved = menuItemRepository.save(menuItemToUpdate);
        
        Long menuItemId = menuItemSaved.getId();
        MenuItemView menuItemCurrent = menuItemViewRepository.findById(menuItemId).orElse(null);

        String tokenUpdated = jwt.extendTokenExpiration(token);
        return new SuccessResponse(menuItemCurrent,tokenUpdated,"Registro actualizado con exito");
    }
    public Object editPrice(String token,Long id,Double price){
        Long ruc = jwt.extractBusinessRuc(token);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
        
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }

        MenuItemView menuItemFinded = menuItemViewRepository.findByIdAndBusinessId(id, businessFinded.getId()).orElse(null);
       
        if(menuItemFinded==null){
            return new ErrorResponse("menu_item_not_found","Menu item no encontrado");
        }

        MenuItem menuItemToUpdate = MenuItem.builder()
            .id(menuItemFinded.getId())
            .denomination(menuItemFinded.getDenomination())
            .description(menuItemFinded.getDescription())
            .Category(menuItemFinded.getCategory())
            .presentation(menuItemFinded.getPresentation())
            .dispatchArea(menuItemFinded.getDispatchArea())
            .pricePen(price)
            .urlImage(menuItemFinded.getUrlImage())
            .barcode(menuItemFinded.getBarcode())
            .business(menuItemFinded.getBusiness())
            .active(menuItemFinded.isActive())
            .build();

        MenuItem menuItemSaved = menuItemRepository.save(menuItemToUpdate);
        
        Long menuItemId = menuItemSaved.getId();
        MenuItemView menuItemCurrent = menuItemViewRepository.findById(menuItemId).orElse(null);

        String tokenUpdated = jwt.extendTokenExpiration(token);
        return new SuccessResponse(menuItemCurrent,tokenUpdated,"Registro actualizado con exito");
    }

    public Object changeDispatchArea(String token,Long id,Long dispatchAreaId){
        Long ruc = jwt.extractBusinessRuc(token);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }

        MenuItemView menuItemFinded = menuItemViewRepository.findByIdAndBusinessId(id, businessFinded.getId()).orElse(null);
       
        if(menuItemFinded==null){
            return new ErrorResponse("menu_item_not_found","Menu item no encontrado");
        }

        DispatchArea dispatchAreaFinded = dispatchAreaRepository.findById(dispatchAreaId).orElse(null);
        if(dispatchAreaFinded==null){
            return new ErrorResponse("dispatch_area_not_found","Area de despacho no encontrada");
        }

        MenuItem menuItemToUpdate = MenuItem.builder()
            .id(menuItemFinded.getId())
            .denomination(menuItemFinded.getDenomination())
            .description(menuItemFinded.getDescription())
            .Category(menuItemFinded.getCategory())
            .presentation(menuItemFinded.getPresentation())
            .dispatchArea(dispatchAreaFinded)
            .pricePen(menuItemFinded.getPricePen())
            .urlImage(menuItemFinded.getUrlImage())
            .barcode(menuItemFinded.getBarcode())
            .business(menuItemFinded.getBusiness())
            .active(menuItemFinded.isActive())
            .build();

        MenuItem menuItemSaved = menuItemRepository.save(menuItemToUpdate);
        
        Long menuItemId = menuItemSaved.getId();
        MenuItemView menuItemCurrent = menuItemViewRepository.findById(menuItemId).orElse(null);

        String tokenUpdated = jwt.extendTokenExpiration(token);
        return new SuccessResponse(menuItemCurrent,tokenUpdated,"Registro actualizado con exito");
        //return new SuccessResponse(businessFinded,token,"Registro actualizado con exito");
    }
}
