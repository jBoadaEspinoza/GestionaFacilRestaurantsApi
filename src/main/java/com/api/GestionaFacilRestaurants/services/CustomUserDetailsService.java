package com.api.GestionaFacilRestaurants.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.api.GestionaFacilRestaurants.models.Authorization;
import com.api.GestionaFacilRestaurants.repositories.AuthRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{
    private final AuthRepository authRepository;

    public CustomUserDetailsService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public UserDetails loadUserByEstablishmentRucAndUserNameAndRoleIdAndModuleId(Long businessRuc,String userName,Long roleId,Long moduleId)  {
        Authorization user=authRepository.findByBusinessRucAndUserNameAndRoleIdAndModuleIdAndActive(businessRuc,userName,roleId,moduleId,true).orElse(null);
        return user;
    }

    public UserDetails loadUserById(Long id){
        Authorization user = authRepository.findById(id).orElse(null);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadUserByUsername'");
    }

}
