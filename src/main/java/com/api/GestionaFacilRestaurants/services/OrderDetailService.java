package com.api.GestionaFacilRestaurants.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.api.GestionaFacilRestaurants.models.Business;
import com.api.GestionaFacilRestaurants.models.Order;
import com.api.GestionaFacilRestaurants.models.OrderDetail;
import com.api.GestionaFacilRestaurants.repositories.BusinessRepository;
import com.api.GestionaFacilRestaurants.repositories.OrderDetailRepository;
import com.api.GestionaFacilRestaurants.repositories.OrderRepository;
import com.api.GestionaFacilRestaurants.responses.ErrorResponse;
import com.api.GestionaFacilRestaurants.responses.SuccessResponse;
import com.api.GestionaFacilRestaurants.utilities.JwtUtil;

@Service
public class OrderDetailService {

    @Autowired
    private JwtUtil jwt;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private BusinessRepository businessRepository;

    public Object updateOrderDetailQuantity(String token, Long id, Long quantity) {
        Long ruc = jwt.extractBusinessRuc(token);
        String tokenUpdated = jwt.extendTokenExpiration(token);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }

        OrderDetail orderDetailFinded = orderDetailRepository.findById(id).orElse(null);
        if(orderDetailFinded==null){
            return new ErrorResponse("orderdetail_not_found","OrderDetail no encontrado");
        }

        Order orderFinded = orderRepository.findById(orderDetailFinded.getOrderId()).orElse(null);
        if(orderFinded==null){
            return new ErrorResponse("order_not_found", "Order no encontrado");
        }

        if(!orderFinded.getBusiness().getId().equals(businessFinded.getId())){
            return new ErrorResponse("order_not_associated", "Order Id no asociada a establecimiento");
        }
        
        orderDetailFinded.setQuantity(quantity);
        OrderDetail data = orderDetailRepository.save(orderDetailFinded);

        return new SuccessResponse(data,tokenUpdated,"Registro actualizado exitosamente");
    }
    public Object updateUnitPrice(String token, Long id, Double unitPrice) {
        Long ruc = jwt.extractBusinessRuc(token);
        String tokenUpdated = jwt.extendTokenExpiration(token);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }

        OrderDetail orderDetailFinded = orderDetailRepository.findById(id).orElse(null);
        if(orderDetailFinded==null){
            return new ErrorResponse("orderdetail_not_found","OrderDetail no encontrado");
        }

        Order orderFinded = orderRepository.findById(orderDetailFinded.getOrderId()).orElse(null);
        if(orderFinded==null){
            return new ErrorResponse("order_not_found", "Order no encontrado");
        }

        if(!orderFinded.getBusiness().getId().equals(businessFinded.getId())){
            return new ErrorResponse("order_not_associated", "Order Id no asociada a establecimiento");
        }
        
        orderDetailFinded.setUnitPricePen(unitPrice);
        OrderDetail data = orderDetailRepository.save(orderDetailFinded);

        return new SuccessResponse(data,tokenUpdated,"Registro actualizado exitosamente");
    }

    public Object deleteItem(String token, Long id){
        Long ruc = jwt.extractBusinessRuc(token);
        String tokenUpdated = jwt.extendTokenExpiration(token);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }

        OrderDetail orderDetailFinded = orderDetailRepository.findById(id).orElse(null);
        if(orderDetailFinded==null){
            return new ErrorResponse("orderdetail_not_found","OrderDetail no encontrado");
        }

        try{
            orderDetailRepository.deleteById(id);
            return new SuccessResponse(null,tokenUpdated,"Se elimino el registro correctamente");
        }catch(Exception e){
            return new ErrorResponse("OrderDetailItem_not_deleted","Error al eliminar el registro");
        }

    }
}
