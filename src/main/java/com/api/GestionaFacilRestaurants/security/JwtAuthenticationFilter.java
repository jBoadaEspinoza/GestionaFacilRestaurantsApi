package com.api.GestionaFacilRestaurants.security;

import java.io.IOException;

import com.api.GestionaFacilRestaurants.responses.ApiResponse;
import com.api.GestionaFacilRestaurants.responses.ErrorResponse;
import com.api.GestionaFacilRestaurants.services.CustomUserDetailsService;
import com.api.GestionaFacilRestaurants.utilities.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Value("${spring.profiles.active}")
    private String environment;

    @Autowired 
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = getTokenFromRequest(request);

            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Long uid = null;
                try {
                    uid = jwtUtil.extractUid(token);
                } catch (ExpiredJwtException e) {
                    throw new TokenExpiredException("El TOKEN ha caducado");
                } catch (JwtException e) {
                    throw new InvalidTokenException("El TOKEN esta inválido");
                }
                if(jwtUtil.extractEnvironment(token).equals(environment)){
                    if (uid != null) {
                        UserDetails userDetails = customUserDetailsService.loadUserById(uid);
                        UsernamePasswordAuthenticationToken authenticationToken = 
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }else{
                    throw new InvalidEnvironmentException("Ambiente invalido para token");
                }      
            }
            filterChain.doFilter(request, response);
        } catch (TokenExpiredException e) {
            handleException(response, "token_expired",e.getMessage(), HttpServletResponse.SC_OK);
        } catch (InvalidTokenException e){
            handleException(response, "invalid_token",e.getMessage(), HttpServletResponse.SC_OK);
        } catch (Exception e) {
            if(!jwtUtil.extractEnvironment(getTokenFromRequest(request)).equals(environment)){
                handleException(response, "invalid_environment","no autorizado", HttpServletResponse.SC_OK);
            }else{
                handleException(response, "unauthorized","No psss autorizado", HttpServletResponse.SC_OK);
            }
            
        }
    }
    private void handleException(HttpServletResponse response, String code,String message,int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(
            new ApiResponse(new ErrorResponse(code, message))
        ));
    }
    public static String getTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(org.springframework.http.HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
