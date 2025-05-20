package com.api.GestionaFacilRestaurants.security;
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}