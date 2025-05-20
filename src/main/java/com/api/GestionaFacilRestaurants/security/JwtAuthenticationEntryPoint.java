package com.api.GestionaFacilRestaurants.security;

import java.io.IOException;
import java.util.List;
import java.io.OutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.api.GestionaFacilRestaurants.responses.ApiResponse;
import com.api.GestionaFacilRestaurants.responses.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint{
    @Value("${api.base.path}")
    private String apiBasePath;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");

        // Obtener las URLs permitidas de alguna forma (ejemplo: desde el contexto de la aplicación)
        List<String> permittedUrls = List.of(apiBasePath+"/auth/**");

        String requestUri = request.getRequestURI();
        if (isPermittedUrl(requestUri, permittedUrls)) {
            // No token required for permitted URLs
            return;
        }
        writeErrorResponse(response,new ErrorResponse("token_missing", "Esta operación requiere un TOKEN valido"));
    }
    private void writeErrorResponse(HttpServletResponse response, ErrorResponse errorResponse) throws IOException {
        OutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, new ApiResponse(errorResponse));
        out.flush();
    }
    private boolean isPermittedUrl(String requestUri, List<String> permittedUrls) {
        return permittedUrls.stream().anyMatch(url -> requestUri.startsWith(url.replace("**", "")));
    }
}
