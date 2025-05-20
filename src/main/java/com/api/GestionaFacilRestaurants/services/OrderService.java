package com.api.GestionaFacilRestaurants.services;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.api.GestionaFacilRestaurants.models.Authorization;
import com.api.GestionaFacilRestaurants.models.Business;
import com.api.GestionaFacilRestaurants.models.CashRegisterOpening;
import com.api.GestionaFacilRestaurants.models.CashRegisterOpeningItems;
import com.api.GestionaFacilRestaurants.models.CashRegisterOpeningView;
import com.api.GestionaFacilRestaurants.models.DocumentPayIssued;
import com.api.GestionaFacilRestaurants.models.DocumentPaySerie;
import com.api.GestionaFacilRestaurants.models.MenuItem;
import com.api.GestionaFacilRestaurants.models.Order;
import com.api.GestionaFacilRestaurants.models.OrderDetail;
import com.api.GestionaFacilRestaurants.models.PaymentMethod;
import com.api.GestionaFacilRestaurants.models.Person;
import com.api.GestionaFacilRestaurants.repositories.ApiSunatRepository;
import com.api.GestionaFacilRestaurants.repositories.AuthRepository;
import com.api.GestionaFacilRestaurants.repositories.BusinessRepository;
import com.api.GestionaFacilRestaurants.repositories.CashRegisterOpeningItemsRepository;
import com.api.GestionaFacilRestaurants.repositories.CashRegisterOpeningViewRepository;
import com.api.GestionaFacilRestaurants.repositories.DocumentPayIssuedRepository;
import com.api.GestionaFacilRestaurants.repositories.DocumentPaySerieRepository;
import com.api.GestionaFacilRestaurants.repositories.DocumentPayTypeHasPersonalDocumentTypeRepository;
import com.api.GestionaFacilRestaurants.repositories.OrderDetailRepository;
import com.api.GestionaFacilRestaurants.repositories.OrderRepository;
import com.api.GestionaFacilRestaurants.repositories.PaymentMethodRepository;
import com.api.GestionaFacilRestaurants.repositories.PersonRepository;
import com.api.GestionaFacilRestaurants.requests.CheckoutRequest;
import com.api.GestionaFacilRestaurants.requests.ComandaDetailRequest;
import com.api.GestionaFacilRestaurants.requests.ComandaRequest;
import com.api.GestionaFacilRestaurants.responses.CategoryResponse;
import com.api.GestionaFacilRestaurants.responses.ErrorResponse;
import com.api.GestionaFacilRestaurants.responses.MenuItemResponse;
import com.api.GestionaFacilRestaurants.responses.OrderDetailResponse;
import com.api.GestionaFacilRestaurants.responses.OrderResponse;
import com.api.GestionaFacilRestaurants.responses.PresentationResponse;
import com.api.GestionaFacilRestaurants.responses.SuccessResponse;
import com.api.GestionaFacilRestaurants.specifications.OrderSpecifications;
import com.api.GestionaFacilRestaurants.utilities.ApiUtil;
import com.api.GestionaFacilRestaurants.utilities.DateTimeUtil;
import com.api.GestionaFacilRestaurants.utilities.JwtUtil;
import com.api.GestionaFacilRestaurants.utilities.KeyUtil;

@Service
public class OrderService {
    @Value("${api.host}")
    private String host;

    @Value("${api.base.path}")
    private String apiBasePath;

    @Autowired
    private JwtUtil jwt;
    
    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private CashRegisterOpeningViewRepository cashRegisterOpeningViewRepository;

    @Autowired
    private DocumentPaySerieRepository documentPaySerieRepository;

    @Autowired
    private DocumentPayTypeHasPersonalDocumentTypeRepository documentPayTypeHasPersonalDocumentTypeRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private DocumentPayIssuedRepository documentPayIssuedRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private CashRegisterOpeningItemsRepository cashRegisterOpeningItemsRepository;

    @Autowired
    private ApiSunatRepository apiSunatRepository;
    
    public Object addItemDetail(String token,Long orderId, ComandaDetailRequest input){
        Long ruc = jwt.extractBusinessRuc(token);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
        String tokenUpdate = jwt.extendTokenExpiration(token);
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }

        Order orderFinded = orderRepository.findById(orderId).orElse(null);
        if(!orderFinded.getBusiness().getId().equals(businessFinded.getId())){
            return new ErrorResponse("order_not_associated", "Order Id no asociada a establecimiento");
        }

        OrderDetail od = OrderDetail.builder()
            .orderId(orderId)
            .menuItem(MenuItem.builder().id(input.getMenuItemId()).build())
            .quantity(input.getQuantity())
            .unitPricePen(input.getUnitPricePen())
            .suggestion(input.getSuggestion())
            .build();
        
        OrderDetail data = orderDetailRepository.save(od);
        return new SuccessResponse(data,tokenUpdate,"Registro creado exitosamente");
    }
    public Object beforeCheckout(String token, Long orderId){
        Long ruc = jwt.extractBusinessRuc(token);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
        String tokenUpdate = jwt.extendTokenExpiration(token);
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }

        Order orderFinded = orderRepository.findById(orderId).orElse(null);
        if(!orderFinded.getBusiness().getId().equals(businessFinded.getId())){
            return new ErrorResponse("order_not_associated", "Order Id no asociada a establecimiento");
        }

        //Evaluamos si pedido esta cerrado
        if(orderFinded.isClosed()){
            return new ErrorResponse("order_is_closed","El pedido se encuentra cerrado");
        }

        Map<String,Object> data = new LinkedHashMap<>();

        List<CashRegisterOpeningView> listCashRegisterOpeningViews = cashRegisterOpeningViewRepository.findAllByBusinessId(businessFinded.getId());
        CashRegisterOpeningView cashRegisterOpening = listCashRegisterOpeningViews.size()==0 ? null : listCashRegisterOpeningViews.get(0);
        data.put("cashOpening",cashRegisterOpening);
        
        
        List<DocumentPaySerie> listDocumentPaySeries = documentPaySerieRepository.findAllByBusinessIdOrderByIdDesc(businessFinded.getId());
        DocumentPaySerie documentPaySerie = listDocumentPaySeries.size()==0 ? null : listDocumentPaySeries.get(0);
        data.put("documentPaySerie",documentPaySerie);

        
        List<PaymentMethod> listPaymentMethods = paymentMethodRepository.findAll();
        PaymentMethod paymentMethod=listPaymentMethods.size()==0 ? null : listPaymentMethods.get(0);
        data.put("paymentMethod",paymentMethod);

        return new SuccessResponse(data,tokenUpdate);
    }
    public Object updateTipAmount(String token, Long orderId, Map<String,String> input){
        if(!ApiUtil.containsKey(input, "tipAmount")){
            return new ErrorResponse("tip_amount_required","Propina es requerida");
        }
        String tokenUpdated = jwt.extendTokenExpiration(token);
        Long ruc = jwt.extractBusinessRuc(token);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }

        //Buscamos pedido por Id
        Order orderFinded = orderRepository.findByIdAndBusinessId(orderId,businessFinded.getId()).orElse(null);
        if(orderFinded==null){
            return new ErrorResponse("order_not_found","Pedido ID no encontrado");
        }
        
        //Evaluamos si pedido esta cerrado
        if(orderFinded.isClosed()){
            return new ErrorResponse("order_is_closed","El pedido se encuentra cerrado");
        }

        orderFinded.setMetadata(ApiUtil.buildQueryString(orderFinded.getMetadata(),input));
        Order orderUpdated = orderRepository.save(orderFinded); // Guardar los cambios
        orderRepository.findById(orderUpdated.getId()).orElse(null);
        

        return new SuccessResponse(null,tokenUpdated, "registro actualizado con exito");

    }

    @SuppressWarnings("unused")
    public Object checkout(String token,Long orderId,CheckoutRequest input){
        String tokenUpdated = jwt.extendTokenExpiration(token);
        Long ruc = jwt.extractBusinessRuc(token);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
       
        if(input.getIssueDate()!=null){
            Long ndays = ApiUtil.getDaysSinceDate(input.getIssueDate());
            if(ndays<0){
                return new ErrorResponse("date_issue_invalid","Fecha de emisión no válida, debe ser menor o igual a la fecha actual");
            }else{
                if(ndays>3){
                    return new ErrorResponse("date_issue_invalid","Fecha de emisión no válida, debe ser menor o igual a 3 días");
                }
            }
        }
        

        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }

        //Buscamos pedido por Id
        Order orderFinded = orderRepository.findByIdAndBusinessId(orderId,businessFinded.getId()).orElse(null);
        if(orderFinded==null){
            return new ErrorResponse("order_not_found","Pedido ID no encontrado");
        }
        
        //Evaluamos si pedido esta cerrado
        if(orderFinded.isClosed()){
            return new ErrorResponse("order_is_closed","El pedido se encuentra cerrado");
        }

        //Verificamos si el pedido tiene items asociados
        List<OrderDetail> orderDetailsFinded = orderDetailRepository.findAllByOrderId(orderFinded.getId());
        if(orderDetailsFinded.size()==0){
            return new ErrorResponse("order_has_not_details","La order requerida no tiene detalle asociada"); 
        }

        //Verificamos si caja se encuentra aperturada
        CashRegisterOpeningView cashOpeningFinded = cashRegisterOpeningViewRepository.findByIdAndBusinessId(input.getCashOpeningId(),businessFinded.getId()).orElse(null);
        if(cashOpeningFinded==null){
            return new ErrorResponse("cash_register_opening_not_found","Ingrese una ID caja válido ó la caja requerida no esta aperturada");
        }

        //Buscamos serie por id
        DocumentPaySerie documentPaySerieFinded = documentPaySerieRepository.findByIdAndBusinessId(input.getDocumentPaySerieId(),businessFinded.getId()).orElse(null);
        if(documentPaySerieFinded==null){
            return new ErrorResponse("document_pay_serie_not_found","Serie no encontrada");
        }
        
        //Obtenemos el cliente
        Person customerFinded = null;
        if(input.getCustomerId()!=null){
            if(input.getCustomerId()>0){
                customerFinded = personRepository.findById(input.getCustomerId()).orElse(null);
                if(customerFinded==null){
                    return new ErrorResponse("customer_not_found","Cliente no encontrado");
                }
            }
            Long documentPayTypeId = documentPaySerieFinded.getDocumentPayType().getId();
            @SuppressWarnings("null")
            Long personalDocumentTypeId =customerFinded.getPersonalDocumentType().getId();
            if(!documentPayTypeHasPersonalDocumentTypeRepository.existsByDocumentPayTypeIdAndPersonalDocumentTypeId(documentPayTypeId,personalDocumentTypeId)){
                return new ErrorResponse("serie_require_personal_document_type_valid","Se requiere un documento personal asociado a la serie");
            }


        }else{
            //Si serie es factura debe responder con mensaje donde se requiere ruc
            if(documentPaySerieFinded.getDocumentPayType().getId()==1){
                return new ErrorResponse("serie_require_personal_document_type_valid","Se requiere un documento personal asociado a la serie");
            }
        }
        
        //Buscamos modalidad de pago por id
        PaymentMethod paymentMethodFinded = paymentMethodRepository.findById(input.getPaymentMethodId()).orElse(null);
        if(paymentMethodFinded==null){
            return new ErrorResponse("payment_method_not_found","Modalidad de pago no encontrado");
        }

        //Tipo de documento: Nota de pedido
        Double amount = 0.0;
        String metadata="";
        if(documentPaySerieFinded.getDocumentPayType().getId()==100){
            //NOTA DE PEDIDO
            Long numbering = 0L;
            DocumentPayIssued lastDocumentPayIssued = documentPayIssuedRepository.findLatestByOrderIdAndDocumentPaySerieId(documentPaySerieFinded.getId()).orElse(null);        
            if(lastDocumentPayIssued==null){
                numbering=numbering+1;
            }else{
                numbering=lastDocumentPayIssued.getNumbering()+1;
            }
            
            //Calculamos el monto total incluyendo propina
            amount = orderDetailsFinded.stream().mapToDouble(dt->dt.getQuantity()*dt.getUnitPricePen()).sum();
            amount = amount + input.getTipAmount();
            
            

            DocumentPayIssued documentPayIssuedToInsert = DocumentPayIssued.builder()
                .issueDate(DateTimeUtil.getFormattedCurrentUtcDateTime())
                .order(orderFinded)
                .documentPaySerie(documentPaySerieFinded)
                .numbering(numbering)
                .paymentMethod(paymentMethodFinded)
                .customer(customerFinded)
                .cashRegisterOpening(CashRegisterOpening.builder().id(cashOpeningFinded.getId()).build())
                .amountTotal(amount)
                .amountTip(input.getTipAmount())
                .metadata(metadata)
                .build();

            DocumentPayIssued documentPayIssuedInserted = documentPayIssuedRepository.save(documentPayIssuedToInsert);

            Map<String, String> params = Map.of(
                "referencia_id", documentPayIssuedInserted.getId().toString(),
                "referencia_modelo", "DocumentPayIssued"
            );

            String metadataQuery = ApiUtil.buildQueryString(params);
            
            CashRegisterOpeningItems cashOpeningItemToInsert = CashRegisterOpeningItems.builder()
                .cashRegisterOpeningId(cashOpeningFinded.getId())
                .creationDate(DateTimeUtil.getFormattedCurrentUtcDateTime())
                .itemDescription("v.directa")
                .amount(amount)
                .businessId(businessFinded.getId())
                .metadata(metadataQuery)
                .build();
            CashRegisterOpeningItems cashRegisterOpeningItemsInserted = cashRegisterOpeningItemsRepository.save(cashOpeningItemToInsert);
            orderFinded.setClosed(true);
            orderFinded.setClosingDate(DateTimeUtil.getFormattedCurrentUtcDateTime());
            orderFinded.setCustomer(customerFinded==null ? null : customerFinded);
            Order orderClosed = orderRepository.save(orderFinded);
            
            String pattern = "yyyy-MM-dd HH:mm:ss";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime localDateTime = LocalDateTime.parse(documentPayIssuedInserted.getIssueDate(), formatter);

            String fileName = businessFinded.getRuc()+"-"+ApiUtil.lzeros(documentPayIssuedInserted.getDocumentPaySerie().getDocumentPayType().getId(), 2)+"-"+documentPayIssuedInserted.getDocumentPaySerie().getNumbering()+"-"+ApiUtil.lzeros(documentPayIssuedInserted.getNumbering(), 8);
            
            Map<String,Object> data = new LinkedHashMap<>();
            data.put("orderId",orderClosed.getId());
            data.put("issueTime",DateTimeUtil.convertToUnixMillis(localDateTime));
            data.put("fileName",fileName);
            data.put("pdfUrl",this.getFullPath()+"/documents/"+KeyUtil.encrypt(documentPayIssuedInserted.getId().toString())+"/getPDF/ticket58mm/"+fileName+".pdf");
            return new SuccessResponse(data,tokenUpdated,"Pedido cerrado exitosamente");
        }else{
            //BOLETA O FACTURA
            Long numbering = Long.parseLong(apiSunatRepository.findLastNumberingFromInvoice(documentPaySerieFinded, businessFinded)); 
            amount = orderDetailsFinded.stream().mapToDouble(dt->dt.getQuantity()*dt.getUnitPricePen()).sum();
            amount = amount + input.getTipAmount();
            DocumentPayIssued documentPayIssuedToInsert = DocumentPayIssued.builder()
                .issueDate(DateTimeUtil.getFormattedCurrentUtcDateTime())
                .order(orderFinded)
                .documentPaySerie(documentPaySerieFinded)
                .cashRegisterOpening(CashRegisterOpening.builder().id(cashOpeningFinded.getId()).build())
                .numbering(numbering)
                .paymentMethod(paymentMethodFinded)
                .customer(customerFinded)
                .amountTotal(amount)
                .amountTip(input.getTipAmount())
                .metadata(metadata)
                .build();

            DocumentPayIssued documentPayIssuedInserted = documentPayIssuedRepository.save(documentPayIssuedToInsert);
            
            Map<String, String> params = Map.of(
                "referencia_id", documentPayIssuedInserted.getId().toString(),
                "referencia_modelo", "DocumentPayIssued"
            );
            
            String metadataQuery = ApiUtil.buildQueryString(params);

            CashRegisterOpeningItems cashOpeningItemToInsert = CashRegisterOpeningItems.builder()
                .cashRegisterOpeningId(cashOpeningFinded.getId())
                .creationDate(DateTimeUtil.getFormattedCurrentUtcDateTime())
                .itemDescription("v.directa")
                .amount(amount)
                .businessId(businessFinded.getId())
                .metadata(metadataQuery)
                .build();

            CashRegisterOpeningItems cashRegisterOpeningItemsInserted = cashRegisterOpeningItemsRepository.save(cashOpeningItemToInsert);
            orderFinded.setClosed(true);
            orderFinded.setClosingDate(DateTimeUtil.getFormattedCurrentUtcDateTime());
            orderFinded.setCustomer(customerFinded==null ? null : customerFinded);
            Order orderClosed = orderRepository.save(orderFinded);

            @SuppressWarnings("unchecked")
            Map<String,Object>  responseBill = (Map<String, Object>) apiSunatRepository.sendBill(documentPaySerieFinded, businessFinded, orderFinded, orderDetailsFinded, numbering,input.getTipAmount(),input.getNote(),input.getIssueDate());
            String documentId = (String) responseBill.get("documentId");

            documentPayIssuedInserted.setMetadata("documentId="+documentId);
            DocumentPayIssued documentPayUpdated = documentPayIssuedRepository.save(documentPayIssuedInserted);
            
            String pattern = "yyyy-MM-dd HH:mm:ss";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime localDateTime = LocalDateTime.parse(documentPayUpdated.getIssueDate(), formatter);

            String fileName = businessFinded.getRuc()+"-"+ApiUtil.lzeros(documentPayUpdated.getDocumentPaySerie().getDocumentPayType().getId(), 2)+"-"+documentPayUpdated.getDocumentPaySerie().getNumbering()+"-"+ApiUtil.lzeros(documentPayUpdated.getNumbering(), 8);
            
            Map<String,Object> data = new LinkedHashMap<>();
            data.put("orderId",documentPayUpdated.getOrder().getId());
            data.put("issueTime",DateTimeUtil.convertToUnixMillis(localDateTime));
            data.put("fileName",fileName);
            data.put("pdfUrl","https://apisunat.com/pdf/"+documentId+"/58mm/"+fileName+".pdf");
            return new SuccessResponse(data,tokenUpdated,"Documento creado con exito");
        } 
    }
    
    public Object delete(String token,Long orderId){
        Long ruc = jwt.extractBusinessRuc(token);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
        String tokenUpdate = jwt.extendTokenExpiration(token);
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }

        Order orderFinded = orderRepository.findById(orderId).orElse(null);
        if(!orderFinded.getBusiness().getId().equals(businessFinded.getId())){
            return new ErrorResponse("order_not_associated", "Order Id no asociada a establecimiento");
        }

        try{
            orderRepository.deleteById(orderFinded.getId());
            return new SuccessResponse(null,tokenUpdate,"Se elimino el registro exitosamente");
        }catch(Exception e){
            return new ErrorResponse("order_not_deleted", "No se pudo eliminar el registro");
        }
    }

    public Object insert(String token,ComandaRequest input){
        Long ruc = jwt.extractBusinessRuc(token);
        Long uid = jwt.extractUid(token);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
        
        if(businessFinded==null){
            return new ErrorResponse("business_not_found", "Establecimiento no encontrado");
        }

        Authorization auth = authRepository.findById(uid).orElse(null);
        if(auth==null){
            return new ErrorResponse("auth_not_found", "Usuario no encontrado");
        }

        String metadata="venta=establecimiento&modo=presencial&mesa_id="+String.valueOf(input.getTableId());
        
        
        Person customerFinded =null;
        if(input.getCustomerId()!=null){
            customerFinded= personRepository.findById(input.getCustomerId()).orElse(null);
        }
        
        Order lastOrder = orderRepository.findTopByBusinessIdOrderByNumberingDesc(businessFinded.getId()).orElse(null);
        Long numbering = lastOrder!=null ? (lastOrder.getNumbering()+1) : 1L;

        Order orderToInsert = Order.builder()
            .issueDate(DateTimeUtil.getFormattedCurrentUtcDateTime())
            .customer(customerFinded)
            .numbering(numbering)
            .waiterId(auth.getUserOwnerId())
            .business(businessFinded)
            .metadata(metadata)
            .build();

        Order orderInserted = orderRepository.save(orderToInsert);

       
        Map<String, String> renameRules = new HashMap<>();
        renameRules.put("venta", "transaction");
        renameRules.put("modo", "mode");
        renameRules.put("mesa_id", "tableId");

        Map<String, String> parseMetadata = ApiUtil.buildMetadata(orderInserted.getMetadata());
        String tipAmountString = !ApiUtil.containsKey(parseMetadata, "tipAmount") ? null : parseMetadata.get("tipAmount"); 
        double tipAmount = tipAmountString != null ? Double.parseDouble(tipAmountString) : 0.00;

        OrderResponse data = OrderResponse.builder()
            .id(orderInserted.getId())
            .customerId(orderInserted.getCustomer()==null ? null : orderInserted.getCustomer().getId())
            .waiterId(orderInserted.getWaiterId())
            .issueDate(orderInserted.getIssueDate())
            .closing(orderInserted.isClosed())
            .closingDate(orderInserted.getClosingDate())
            .tipAmount(tipAmount)
            .metadata(parseQueryString(orderInserted.getMetadata(),renameRules))
            .numbering(orderInserted.getNumbering())
            .orderDetails(new ArrayList<>())
            .build();

        String tokenUpdated = jwt.extendTokenExpiration(token);
        return new SuccessResponse(data,tokenUpdated,"Registro creado exitosamente");
    }
    public Object get(String token,boolean closed,Integer skip,Integer limit,Sort.Direction sortDirection){
        try {
            // Extract and validate token information
            Long ruc = jwt.extractBusinessRuc(token);
            Long uid = jwt.extractUid(token);
            
            // Validate pagination parameters
            if (skip == null || limit == null || skip < 0 || limit <= 0) {
                return new ErrorResponse("invalid_pagination", "Parámetros de paginación inválidos");
            }
    
            // Prepare pagination
            Pageable pageable = PageRequest.of(skip, limit, Sort.by(sortDirection, "closingDate"));
            String tokenUpdated = jwt.extendTokenExpiration(token);
    
            // Find business and user
            Business business = businessRepository.findByRuc(ruc)
                    .orElseThrow(() -> new RuntimeException("Establecimiento no encontrado"));
            
            Authorization user = authRepository.findById(uid)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
            // Build query specification
            Specification<Order> spec = Specification.where(OrderSpecifications.hasBusinessId(business.getId()))
                    .and(OrderSpecifications.isClosed(closed));
    
            if (!user.getRoleId().equals(0L)) {
                spec = spec.and(OrderSpecifications.hasWaiterId(user.getUserOwnerId()));
            }
    
            // Fetch and transform data
            List<Map<String,Object>> data = orderRepository.findAll(spec, pageable)
                    .map(order -> {
                        DocumentPayIssued documentPayIssued = documentPayIssuedRepository.findByOrderId(order.getId())
                                .orElseThrow(() -> new RuntimeException("Documento no encontrado para la orden"));
                        
                        String fileName = String.format("%s-%s-%s-%s",
                                business.getRuc(),
                                ApiUtil.lzeros(documentPayIssued.getDocumentPaySerie().getDocumentPayType().getId(), 2),
                                documentPayIssued.getDocumentPaySerie().getNumbering(),
                                ApiUtil.lzeros(documentPayIssued.getNumbering(), 8));
                        
                        String pattern = "yyyy-MM-dd HH:mm:ss";
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                        LocalDateTime localDateTime = LocalDateTime.parse(order.getIssueDate(), formatter);

                        Map<String, Object> responseData = new LinkedHashMap<>();
                        responseData.put("id", order.getId());
                        responseData.put("issueTime", DateTimeUtil.convertToUnixMillis(localDateTime));
                        responseData.put("fileName", fileName);
                        responseData.put("type",documentPayIssued.getDocumentPaySerie().getDocumentPayType().getDenomination().toUpperCase());
                        if(documentPayIssued.getDocumentPaySerie().getDocumentPayType().getId()==100){
                            responseData.put("urlComanda",String.format("%s/documents/%s/getPDF/ticket58mm/%s.pdf",
                                     this.getFullPath(),
                                     KeyUtil.encrypt(documentPayIssued.getId().toString()),
                                     fileName
                            ));        
                        }else{
                            String metadata = documentPayIssued.getMetadata();
                            String documentId = metadata != null ? metadata.replace("documentId=", "") : "";
                            responseData.put("urlComanda",String.format("https://apisunat.com/pdf/%s/58mm/%s.pdf", 
                                    documentId,
                                    fileName
                            ));
                        } 
                        responseData.put("currency", documentPayIssued.getCurrency());

                        Map<String, String> metadata = ApiUtil.buildMetadata(order.getMetadata());
                        String tipAmountString = !ApiUtil.containsKey(metadata, "tipAmount") ? null : metadata.get("tipAmount"); 
                        double tipAmount = tipAmountString != null ? Double.parseDouble(tipAmountString) : 0.00;

                        responseData.put("tipAmount", ApiUtil.formatToTwoDecimals(tipAmount));
                        responseData.put("totalAmount", ApiUtil.formatToTwoDecimals(documentPayIssued.getAmountTotal()));
                        responseData.put("methodOfPay", documentPayIssued.getPaymentMethod().getDenominationEs().toUpperCase());

                        return responseData;
                    }).getContent();
    
            return new SuccessResponse(data, tokenUpdated);
    
        } catch (RuntimeException e) {
            return new ErrorResponse("processing_error", e.getMessage());
        } catch (Exception e) {
            return new ErrorResponse("server_error", "Error interno del servidor");
        }

    }
    public Object getById(String token, Long orderId){
        Long ruc = jwt.extractBusinessRuc(token);
        String tokenUpdated = jwt.extendTokenExpiration(token);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }
        Order orderFinded = orderRepository.findByIdAndBusinessId(orderId, businessFinded.getId()).orElse(null);
        List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrderId(orderFinded.getId());
        List<OrderDetailResponse> listOrderDetailsResponse = orderDetails
                    .stream().map(
                        dt->{
                            CategoryResponse categoryResponse = CategoryResponse.builder()
                                .id(dt.getMenuItem().getCategory().getId())
                                .denominationPerUnit(dt.getMenuItem().getCategory().getDenominationPerUnit())
                                .denominationPerGroup(dt.getMenuItem().getCategory().getDenominationPerGroup())
                                .build();
                            
                            PresentationResponse presentationResponse = PresentationResponse.builder()
                                .id(dt.getMenuItem().getPresentation().getId())
                                .denomination(dt.getMenuItem().getPresentation().getDenomination())
                                .build();

                            MenuItemResponse menuItemResponse = MenuItemResponse.builder()
                                .id(dt.getMenuItem().getId())
                                .denomination(dt.getMenuItem().getDenomination())
                                .denominationFull(ApiUtil.capitalizeEachWord(dt.getMenuItem().getDenomination()+" - "+dt.getMenuItem().getPresentation().getDenomination()))
                                .category(categoryResponse)
                                .presentation(presentationResponse)
                                .imageUrl(dt.getMenuItem().getUrlImage())
                                .build();

                            return OrderDetailResponse.builder()
                                .id(dt.getId())
                                .menuItem(menuItemResponse)
                                .quantity(dt.getQuantity())
                                .unitPrice(dt.getUnitPricePen())
                                .suggestion(dt.getSuggestion())
                                .build();
                        }
                    ).collect(Collectors.toList());
        
        
        Map<String, String> metadata = ApiUtil.buildMetadata(orderFinded.getMetadata());
        String tipAmountString = !ApiUtil.containsKey(metadata, "tipAmount") ? null : metadata.get("tipAmount"); 
        double tipAmount = tipAmountString != null ? Double.parseDouble(tipAmountString) : 0.00;

        OrderResponse data = OrderResponse.builder()
            .id(orderFinded.getId())
            .waiterId(orderFinded.getWaiterId())
            .customerId(orderFinded.getCustomer()==null ? null : orderFinded.getCustomer().getId())
            .issueDate(orderFinded.getIssueDate())
            .closingDate(orderFinded.getClosingDate())
            .closing(orderFinded.isClosed())
            .numbering(orderFinded.getNumbering())
            .tipAmount(tipAmount)
            .orderDetails(listOrderDetailsResponse)
            .build();
        return new SuccessResponse(data,tokenUpdated,"Registro creado exitosamente");
    }
    public static Map<String, String> parseQueryString(String queryString, Map<String, String> renameRules) {
        Map<String, String> metadata = new HashMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];

                if (renameRules.containsKey(key)) {
                    String newKey = renameRules.get(key);
                    if (!"ignore".equals(newKey)) {
                        metadata.put(newKey, value);
                    }
                } else {
                    metadata.put(key, value);
                }
            }
        }
        return metadata;
    }
    private String getFullPath() {
        return host + apiBasePath;
    }
}
