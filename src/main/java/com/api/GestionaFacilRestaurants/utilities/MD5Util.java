package com.api.GestionaFacilRestaurants.utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    public static String toMD5(String input) {
        try {
            // Crear una instancia de MessageDigest para MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            
            // Calcular el hash del input
            byte[] digest = md.digest(input.getBytes());
            
            // Convertir el array de bytes en representación hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            // Retornar la cadena hexadecimal
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Enviar mensaje de error si el algoritmo MD5 no está disponible
            throw new RuntimeException("Error while hashing in MD5", e);
        }
    }
}
