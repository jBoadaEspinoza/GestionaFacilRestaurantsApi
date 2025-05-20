package com.api.GestionaFacilRestaurants.utilities;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    // Función para obtener la fecha y hora actual en UTC formateada
    public static String getDateTodayInLima() {
        // Obtener la zona horaria de América/Lima
        ZoneId limaZone = ZoneId.of("America/Lima");

        // Obtener la fecha y hora actual en América/Lima
        ZonedDateTime limaDateTime = ZonedDateTime.now(limaZone);

        // Definir el formato deseado para la fecha y hora
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Formatear la fecha y hora en América/Lima
        return limaDateTime.format(formatter);
    }
    public static String getTimeTodayInLima() {
        // Obtener la zona horaria de América/Lima
        ZoneId limaZone = ZoneId.of("America/Lima");

        // Obtener la fecha y hora actual en América/Lima
        ZonedDateTime limaDateTime = ZonedDateTime.now(limaZone);

        // Definir el formato deseado para la fecha y hora
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Formatear la fecha y hora en América/Lima
        return limaDateTime.format(formatter);
    }
    public static String convertUtcToLima(String utcDateTime) {
        // Definir el formato del UTC proporcionado
        DateTimeFormatter utcFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"));

        // Analizar la fecha y hora UTC proporcionada
        Instant utcInstant = Instant.from(utcFormatter.parse(utcDateTime));

        // Convertir la fecha y hora UTC a la zona horaria de Lima
        ZonedDateTime limaDateTime = utcInstant.atZone(ZoneId.of("America/Lima"));

        // Formatear la fecha y hora en la zona horaria de Lima
        DateTimeFormatter limaFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return limaDateTime.format(limaFormatter);
    }
    public static String getDateTimeTodayInLima() {
        // Obtener la zona horaria de América/Lima
        ZoneId limaZone = ZoneId.of("America/Lima");

        // Obtener la fecha y hora actual en América/Lima
        ZonedDateTime limaDateTime = ZonedDateTime.now(limaZone);

        // Definir el formato deseado para la fecha y hora
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Formatear la fecha y hora en América/Lima
        return limaDateTime.format(formatter);
    }
    public static String getFormattedCurrentUtcDateTime() {
        // Obtener la fecha y hora local actual
        LocalDateTime now = LocalDateTime.now();

        // Convertir la fecha y hora local a UTC
        ZonedDateTime utcDateTime = now.atZone(ZoneOffset.systemDefault()).withZoneSameInstant(ZoneOffset.UTC);

        // Definir el formato deseado para la fecha y hora
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Formatear la fecha y hora en UTC
        return utcDateTime.format(formatter);
    }
    public static long convertToUnixMillis(LocalDateTime dateTime) {
        // Convertir LocalDateTime a milisegundos desde la época Unix
        return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
