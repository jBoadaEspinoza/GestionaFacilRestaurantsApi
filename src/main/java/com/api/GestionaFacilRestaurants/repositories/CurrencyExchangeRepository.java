package com.api.GestionaFacilRestaurants.repositories;

public interface CurrencyExchangeRepository {
    Double findExchange(String fecha,String moneda);
}
