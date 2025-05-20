package com.api.GestionaFacilRestaurants.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.GestionaFacilRestaurants.models.Authorization;
import com.api.GestionaFacilRestaurants.requests.LoginRequest;
import com.api.GestionaFacilRestaurants.responses.AuthResponse;
import com.api.GestionaFacilRestaurants.responses.ErrorResponse;
import com.api.GestionaFacilRestaurants.utilities.ApiUtil;
import com.api.GestionaFacilRestaurants.utilities.JwtUtil;

@Service
public class AuthService {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    public Object login(LoginRequest input){
        if(!ApiUtil.isValidRuc(input.getBusinessRuc().toString())){
            return new ErrorResponse("invalid_ruc","Ingrese un correo ruc valido");
        }
        //Si rol = Admin
        if(input.getRoleId()==0){
            if(!ApiUtil.isEmail(input.getUserName())){
                return new ErrorResponse("invalid_email","Ingrese un correo electronico valido");
            }  
        }else{
            if(input.getUserName().length()==0){
                return new ErrorResponse("invalid_username","Ingrese un nombre de usuario valido");
            }
        }

        Authorization user = (Authorization) customUserDetailsService.loadUserByEstablishmentRucAndUserNameAndRoleIdAndModuleId(
            input.getBusinessRuc(),
            input.getUserName(),
            Long.parseLong(String.valueOf(input.getRoleId())), 
            Long.parseLong(String.valueOf(input.getModuleId()))
        );

        if(user==null){
            return new ErrorResponse("invalid_user","Usuario no encontrado");
        }

        if(!user.isBusinessActive()){
            return new ErrorResponse("inactive_establishment","Establecimiento inactivo");
        }

        if(!user.isActive()){
            return new ErrorResponse("inactive_user","Usuario inactivo");
        }

        if(!user.getPassword().equals(input.getUserPassword())){
            return new ErrorResponse("invalid_password","Contraseña incorrecta");
        }

        String token = jwtUtil.generateToken(user);

        return new AuthResponse(token); 
    }
}
