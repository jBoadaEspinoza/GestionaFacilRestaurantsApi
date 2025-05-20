package com.api.GestionaFacilRestaurants.utilities;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class ApiUtil {
    private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static String formatToTwoDecimals(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("#.00"); // Formato para dos decimales
        return decimalFormat.format(number);
    }
    
    public static boolean validateSixDigitString(String input) {
        // Verificar que la cadena no sea nula y cumpla la expresión regular de 6 dígitos
        return input != null && input.matches("^\\d{6}$");
    }
    public static long getDaysSinceDate(String dateStr) {
       try {
            LocalDate inputDate = LocalDate.parse(dateStr, DATE_FORMATTER);
            LocalDate today = LocalDate.now();
            
            // Retorna positivo para fechas pasadas, negativo para futuras
            return ChronoUnit.DAYS.between(inputDate, today);
            
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Formato de fecha inválido. Use yyyy-MM-dd", e);
        }
    }
    
    public static String toFixedDecimal(double value, int decimalPlaces, boolean stripTrailingZeros) {
        BigDecimal bigDecimal = new BigDecimal(value)
                .setScale(decimalPlaces, RoundingMode.HALF_UP);
        
        if (stripTrailingZeros) {
            bigDecimal = bigDecimal.stripTrailingZeros();
        }
        
        return bigDecimal.toPlainString();
    }
    public static int generateSixDigitNumber() {
        Random random = new Random();
        // Generar un número entre 100000 y 999999 (ambos inclusive)
        return 100000 + random.nextInt(900000);
    }
    public static boolean isInteger(String value) {
        if (value == null || value.isEmpty()) {
            return false; // Manejo de nulos o cadenas vacías
        }

        // Expresión regular para un número entero (opcionalmente con signo)
        String regex = "^[+-]?\\d+$";
        Pattern pattern = Pattern.compile(regex);

        return pattern.matcher(value).matches();
    }
    public static boolean isValidRuc(String ruc) {
        String rucPattern = "^(10|20)\\d{9}$";
        Pattern pattern = Pattern.compile(rucPattern);
        Matcher matcher = pattern.matcher(ruc);
        return matcher.matches();
    }

    public static boolean isEmail(String email){
        String emailPatter="^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailPatter);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String lzeros(Long number, int num_ceros) {
        // Usa String.format para agregar ceros a la izquierda
        return String.format("%0" + num_ceros + "d", number);
    }
    
    public static String capitalizeEachWord(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        String[] words = input.split("\\s+"); // Divide por espacios en blanco

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0))) // Primera letra en mayúscula
                      .append(word.substring(1).toLowerCase())      // Resto en minúscula
                      .append(" ");                                // Añade un espacio
            }
        }

        // Elimina el último espacio sobrante
        return result.toString().trim();
    }
    public static String buildQueryString(Map<String, String> params) {
        if (params == null) {
            throw new IllegalArgumentException("El mapa de parámetros no puede ser nulo");
        }

        if (params.isEmpty()) {
            return "";
        }

        StringBuilder queryString = new StringBuilder();

        boolean firstParam = true;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                String encodedKey = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString());
                String encodedValue = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString());

                if (!firstParam) {
                    queryString.append("&");
                }

                queryString.append(encodedKey)
                          .append("=")
                          .append(encodedValue);

                firstParam = false;
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Error al codificar los parámetros", e);
            }
        }

        return queryString.toString();
    }
    public static boolean containsKey(Map<String, String> map, String key) {
        // Check if the map is null to avoid NullPointerException
        if (map == null) {
            return false;
        }
        
        // The containsKey() method tells us if the key exists in the map
        return map.containsKey(key);
    }
    public static String buildQueryString(String query, Map<String, String> params) {
        Map<String, String> combinedParams = new HashMap<>();
        
        // Parsear el query existente (si no es nulo o vacío)
        if (query != null && !query.trim().isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    combinedParams.put(keyValue[0], keyValue[1]);
                }
            }
        }
        
        // Agregar/sobrescribir con los nuevos parámetros
        if (params != null) {
            combinedParams.putAll(params);
        }
        
        // Convertir el Map combinado de vuelta a un query string
        return combinedParams.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }
    public static MultiValueMap<String, String> parseMetadata(String metadata) {
        MultiValueMap<String, String> queryMap = new LinkedMultiValueMap<>();
        // Dividir la cadena en pares clave-valor
        Arrays.stream(metadata.split("&"))
              .forEach(pair -> {
                  String[] keyValue = pair.split("=", 2); // Divide cada par en clave y valor
                  String key = keyValue[0];
                  String value = keyValue.length > 1 ? keyValue[1] : ""; // Si no hay valor, usa cadena vacía
                  queryMap.add(key, value);
              });
        return queryMap;
    }
    public static Map<String, String> buildMetadata(String metadata) {
        Map<String, String> queryMap = new LinkedHashMap<>();
        if (metadata == null || metadata.isEmpty()) {
            return queryMap;
        }
        
        // Dividir la cadena en pares clave-valor
        Arrays.stream(metadata.split("&"))
            .forEach(pair -> {
                String[] keyValue = pair.split("=", 2); // Divide cada par en clave y valor
                String key = keyValue[0];
                String value = keyValue.length > 1 ? keyValue[1] : ""; // Si no hay valor, usa cadena vacía
                
                // En un Map regular, si la clave ya existe, se sobrescribe el valor
                queryMap.put(key, value);
            });
        
        return queryMap;
    }
}
