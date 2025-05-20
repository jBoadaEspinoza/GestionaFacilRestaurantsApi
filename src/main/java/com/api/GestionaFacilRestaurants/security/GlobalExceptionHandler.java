package com.api.GestionaFacilRestaurants.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.api.GestionaFacilRestaurants.responses.ApiResponse;
import com.api.GestionaFacilRestaurants.responses.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse> handleTokenExpiredException(TokenExpiredException ex) {
        return new ResponseEntity<>(new ApiResponse(new ErrorResponse("token_expired", ex.getMessage())), HttpStatus.OK);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse> handleInvalidTokenException(InvalidTokenException ex) {
        return new ResponseEntity<>(new ApiResponse(new ErrorResponse("invalid_token", ex.getMessage())), HttpStatus.OK);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        String parameterName = ex.getParameterName();
        String errorMessage = String.format("El parámetro '%s' es obligatorio y no debe estar vacío.", parameterName);
        return new ResponseEntity<>(new ApiResponse(new ErrorResponse("missing_parameter", errorMessage)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception ex) {
        return new ResponseEntity<>(new ApiResponse(new ErrorResponse("error", "Ocurrió un error interno")), HttpStatus.OK);
    }
}
