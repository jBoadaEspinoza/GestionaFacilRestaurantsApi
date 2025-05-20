package com.api.GestionaFacilRestaurants.repositories;

import java.util.List;

import com.api.GestionaFacilRestaurants.models.Business;
import com.api.GestionaFacilRestaurants.models.DocumentPaySerie;
import com.api.GestionaFacilRestaurants.models.Order;
import com.api.GestionaFacilRestaurants.models.OrderDetail;

public interface ApiSunatRepository {

    String findLastNumberingFromInvoice(DocumentPaySerie serie,Business business);
    Object sendBill(DocumentPaySerie serie, Business business,Order order,List<OrderDetail> details, Long correlativeNumber,Double tipAmount,String note,String issueDate);
}
