package com.api.GestionaFacilRestaurants.repositories;

import java.util.Map;

public interface RucRepository {
    Map<String,Object> findPersonbyRuc(String ruc);
}
