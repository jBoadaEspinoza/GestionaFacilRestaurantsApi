package com.api.GestionaFacilRestaurants.repositories;

import java.util.Map;

public interface DniRepository {
    Map<String,Object> findPersonByDni(String dni);
}
