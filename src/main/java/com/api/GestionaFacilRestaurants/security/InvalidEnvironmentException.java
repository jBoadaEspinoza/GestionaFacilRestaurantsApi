package com.api.GestionaFacilRestaurants.security;

public class InvalidEnvironmentException extends RuntimeException{
    public InvalidEnvironmentException(String message){
        super(message);
    }
}
