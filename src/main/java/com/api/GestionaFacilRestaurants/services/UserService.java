package com.api.GestionaFacilRestaurants.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.api.GestionaFacilRestaurants.models.Authorization;
import com.api.GestionaFacilRestaurants.models.Business;
import com.api.GestionaFacilRestaurants.models.Person;
import com.api.GestionaFacilRestaurants.models.Role;
import com.api.GestionaFacilRestaurants.models.User;
import com.api.GestionaFacilRestaurants.repositories.AuthRepository;
import com.api.GestionaFacilRestaurants.repositories.BusinessRepository;
import com.api.GestionaFacilRestaurants.repositories.PersonRepository;
import com.api.GestionaFacilRestaurants.repositories.RoleRepository;
import com.api.GestionaFacilRestaurants.repositories.UserRepository;
import com.api.GestionaFacilRestaurants.requests.UserRequest;
import com.api.GestionaFacilRestaurants.responses.ErrorResponse;
import com.api.GestionaFacilRestaurants.responses.SuccessResponse;
import com.api.GestionaFacilRestaurants.responses.UserResponse;
import com.api.GestionaFacilRestaurants.specifications.AuthorizationSpecifications;
import com.api.GestionaFacilRestaurants.utilities.ApiUtil;
import com.api.GestionaFacilRestaurants.utilities.JwtUtil;
import com.api.GestionaFacilRestaurants.utilities.MD5Util;

@Service
public class UserService {
    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private JwtUtil jwt;
    
    public Object getMe(String token){
        Long uid = jwt.extractUid(token);
        String tokenUpdated = jwt.extendTokenExpiration(token);
        Authorization userFinded = authRepository.findById(uid).orElse(null);
        
        if(userFinded==null){
            return new ErrorResponse("invalid_token","token invalido");
        }
        
        UserResponse data = UserResponse.builder()
            .id(userFinded.getId())
            .referenceId(userFinded.getUserId())
            .referenceModel(userFinded.getUserModel())
            .ownerId(userFinded.getUserOwnerId())
            .ownerFirstname(userFinded.getUserOwnerFirstName())
            .ownerLastname(userFinded.getUserOwnerLastName())
            .ownerFullname(userFinded.getUserOwnerFullname())
            .ownerImg(userFinded.getUserOwnerImg())
            .businessRuc(userFinded.getBusinessRuc())
            .businessName(userFinded.getBusinessName())
            .businessTypeId(userFinded.getBusinessTypeId())
            .businessTypeSingularNameEs(userFinded.getBusinessTypeSingularNameEs())
            .businessTypeSingularNameEn(userFinded.getBusinessTypeSingularNameEn())
            .businessTypePluralNameEs(userFinded.getBusinessTypePluralNameEs())
            .businessTypePluralNameEn(userFinded.getBusinessTypePluralNameEn())
            .roleId(userFinded.getRoleId())
            .roleName(userFinded.getRoleNameEs())
            .moduleId(userFinded.getModuleId())
            .moduleSingularNameEs(userFinded.getModuleSingularNameEs())
            .modulePluralNameEs(userFinded.getModulePluralNameEs())
            .build();
        return new SuccessResponse(data,tokenUpdated);
    }
    /*public Object get(String token,Integer skip,Integer limit,Sort.Direction sortDirection){
        String tokenUpdated = jwt.extendTokenExpiration(token);
        Pageable pageable=PageRequest.of(skip,limit,Sort.by(sortDirection,"userOwnerFullname"));
        Long ruc = jwt.extractBusinessRuc(token);
        List<UserResponse> data = authRepository.findAllByBusinessRucAndUserModel(ruc, "usuarios", pageable)
            .getContent()
            .stream()
            .map(userFinded->{
                UserResponse userResponse = UserResponse.builder()
                .id(userFinded.getId())
                .referenceId(userFinded.getUserId())
                .referenceModel(userFinded.getUserModel())
                .ownerId(userFinded.getUserOwnerId())
                .ownerFirstname(userFinded.getUserOwnerFirstName())
                .ownerLastname(userFinded.getUserOwnerLastName())
                .ownerFullname(userFinded.getUserOwnerFullname())
                .ownerImg(userFinded.getUserOwnerImg())
                .businessRuc(userFinded.getBusinessRuc())
                .businessName(userFinded.getBusinessName())
                .businessTypeId(userFinded.getBusinessTypeId())
                .businessTypeSingularNameEs(userFinded.getBusinessTypeSingularNameEs())
                .businessTypeSingularNameEn(userFinded.getBusinessTypeSingularNameEn())
                .businessTypePluralNameEs(userFinded.getBusinessTypePluralNameEs())
                .businessTypePluralNameEn(userFinded.getBusinessTypePluralNameEn())
                .roleId(userFinded.getRoleId())
                .roleName(userFinded.getRoleNameEs())
                .moduleId(userFinded.getModuleId())
                .moduleSingularNameEs(userFinded.getModuleSingularNameEs())
                .modulePluralNameEs(userFinded.getModulePluralNameEs())
                .build();
                return userResponse;
        }).collect(Collectors.toList());;  
        return new SuccessResponse(data,tokenUpdated);
    }*/

    public Object get(String token, String denomination, Long rolId, Integer skip, Integer limit, Sort.Direction sortDirection) {
        // Extiende la validez del token
        String tokenUpdated = jwt.extendTokenExpiration(token);
    
        // Configura la paginación
        Pageable pageable = PageRequest.of(skip, limit, Sort.by(sortDirection, "userOwnerFullname"));
    
        // Extrae el RUC del negocio desde el token
        Long ruc = jwt.extractBusinessRuc(token);
    
        // Construye las especificaciones dinámicamente
        Specification<Authorization> spec = Specification.where(AuthorizationSpecifications.hasBusinessRuc(ruc))
                .and(AuthorizationSpecifications.hasUserModel("usuarios"));
    
        if (rolId != null) {
            spec = spec.and(AuthorizationSpecifications.hasRoleId(rolId));
        }
    
        if (denomination != null && !denomination.isEmpty()) {
            spec = spec.and(AuthorizationSpecifications.hasUserOwnerFullnameContaining(denomination));
        }
    
        // Obtiene y mapea los resultados
        List<UserResponse> data = authRepository.findAll(spec, pageable)
                .map(userFinded -> UserResponse.builder()
                        .id(userFinded.getId())
                        .referenceId(userFinded.getUserId())
                        .referenceModel(userFinded.getUserModel())
                        .ownerId(userFinded.getUserOwnerId())
                        .ownerFirstname(userFinded.getUserOwnerFirstName())
                        .ownerLastname(userFinded.getUserOwnerLastName())
                        .ownerFullname(userFinded.getUserOwnerFullname())
                        .ownerImg(userFinded.getUserOwnerImg())
                        .businessRuc(userFinded.getBusinessRuc())
                        .businessName(userFinded.getBusinessName())
                        .businessTypeId(userFinded.getBusinessTypeId())
                        .businessTypeSingularNameEs(userFinded.getBusinessTypeSingularNameEs())
                        .businessTypeSingularNameEn(userFinded.getBusinessTypeSingularNameEn())
                        .businessTypePluralNameEs(userFinded.getBusinessTypePluralNameEs())
                        .businessTypePluralNameEn(userFinded.getBusinessTypePluralNameEn())
                        .roleId(userFinded.getRoleId())
                        .roleName(userFinded.getRoleNameEs())
                        .moduleId(userFinded.getModuleId())
                        .moduleSingularNameEs(userFinded.getModuleSingularNameEs())
                        .modulePluralNameEs(userFinded.getModulePluralNameEs())
                        .build())
                .getContent();
    
        // Devuelve la respuesta con los datos y el token actualizado
        return new SuccessResponse(data, tokenUpdated);
    }
    
    public Object save(String token,UserRequest input){
        String tokenUpdated = jwt.extendTokenExpiration(token);
        Long ruc = jwt.extractBusinessRuc(token);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }
        if(input.getName()==null || input.getName().isEmpty()){
            return new ErrorResponse("field_required", "Campo nombre es requerido");
        }
        if(input.getName().length()<6){
            return new ErrorResponse("field_invalid","La contraseña debe contener como minimo 6 caracteres");
        }
        if(input.getPersonId()==null){
            return new ErrorResponse("field_required","Campo persona es requerido");
        }
        Person personFinded = personRepository.findById(input.getPersonId()).orElse(null);
        if(personFinded==null){
            return new ErrorResponse("person_not_found","Persona no encontrada");
        }
       
        if(personFinded.getPersonalDocumentType().getId()==2){
            return new ErrorResponse("person_invalid","Debe ser persona natural");
        }
        if(input.getRoleId()==null){
            return new ErrorResponse("field_required","Campo rol  es requerido");
        }
        Role roleFinded = roleRepository.findById(input.getRoleId()).orElse(null);
        if(roleFinded==null){
            return new ErrorResponse("role_not_found","Rol no encontrado");
        }
        Authorization authFinded = authRepository.findByBusinessRucAndUserNameAndRoleIdAndModuleIdAndActive(ruc, input.getName(),roleFinded.getId(), businessFinded.getType().getModule().getId(), true).orElse(null);
        if(authFinded!=null && input.getId()==null){
            return new ErrorResponse("user_registered","El usuario ya se encuentra registrado");
        }
        if(input.getPassword()==null || input.getPassword().isEmpty() || !ApiUtil.validateSixDigitString(input.getPassword())){
            return new ErrorResponse("password_required", "Ingrese una contraseña numerica de 6 digitos ");
        }   
        User userToInsert = User.builder()
            .id(input.getId())
            .name(input.getName())
            .pass(MD5Util.toMD5(input.getPassword()))
            .person(personFinded)
            .role(roleFinded)
            .businessId(businessFinded.getId())
            .active(input.isActive())
            .build();

        User userInserted = userRepository.save(userToInsert);
        Authorization userEmployeeFinded = authRepository.findByUserIdAndUserModel(userInserted.getId(), "usuarios").orElse(null);
        
        UserResponse data = UserResponse.builder()
                .id(userEmployeeFinded.getId())
                .referenceId(userEmployeeFinded.getUserId())
                .referenceModel(userEmployeeFinded.getUserModel())
                .ownerId(userEmployeeFinded.getUserOwnerId())
                .ownerFirstname(userEmployeeFinded.getUserOwnerFirstName())
                .ownerLastname(userEmployeeFinded.getUserOwnerLastName())
                .ownerFullname(userEmployeeFinded.getUserOwnerFullname())
                .ownerImg(userEmployeeFinded.getUserOwnerImg())
                .businessRuc(userEmployeeFinded.getBusinessRuc())
                .businessName(userEmployeeFinded.getBusinessName())
                .businessTypeId(userEmployeeFinded.getBusinessTypeId())
                .businessTypeSingularNameEs(userEmployeeFinded.getBusinessTypeSingularNameEs())
                .businessTypeSingularNameEn(userEmployeeFinded.getBusinessTypeSingularNameEn())
                .businessTypePluralNameEs(userEmployeeFinded.getBusinessTypePluralNameEs())
                .businessTypePluralNameEn(userEmployeeFinded.getBusinessTypePluralNameEn())
                .roleId(userEmployeeFinded.getRoleId())
                .roleName(userEmployeeFinded.getRoleNameEs())
                .moduleId(userEmployeeFinded.getModuleId())
                .moduleSingularNameEs(userEmployeeFinded.getModuleSingularNameEs())
                .modulePluralNameEs(userEmployeeFinded.getModulePluralNameEs())
                .build();
        return new SuccessResponse(data,tokenUpdated, input.getId()==null ? "Registro creado con exito" : "Registro actualizado con exito");
    }
}
