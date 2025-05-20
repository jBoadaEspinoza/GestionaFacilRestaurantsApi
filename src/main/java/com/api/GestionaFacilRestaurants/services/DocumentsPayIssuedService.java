package com.api.GestionaFacilRestaurants.services;

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
import com.api.GestionaFacilRestaurants.utilities.ApiUtil;
import com.api.GestionaFacilRestaurants.utilities.DateTimeUtil;
import com.api.GestionaFacilRestaurants.utilities.JwtUtil;
import com.api.GestionaFacilRestaurants.utilities.KeyUtil;
import com.api.GestionaFacilRestaurants.models.Business;
import com.api.GestionaFacilRestaurants.models.DocumentPayIssued;
import com.api.GestionaFacilRestaurants.repositories.BusinessRepository;
import com.api.GestionaFacilRestaurants.repositories.DocumentPayIssuedRepository;
import com.api.GestionaFacilRestaurants.responses.ErrorResponse;
import com.api.GestionaFacilRestaurants.responses.SuccessResponse;
import com.api.GestionaFacilRestaurants.specifications.DocumentsPayIssuedSpecifications;

@Service
public class DocumentsPayIssuedService {
    @Value("${api.host}")
    private String host;

    @Value("${api.base.path}")
    private String apiBasePath;

    @Autowired
    private JwtUtil jwt;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private DocumentPayIssuedRepository documentsPayIssuedRepository;

    public Object getAll(String token, Integer skip, Integer limit, Sort.Direction sortDirection, Long userId,Long paymentMethodId) {
        Long ruc = jwt.extractBusinessRuc(token);
        // Validate pagination parameters
        if (skip == null || limit == null || skip < 0 || limit <= 0) {
            return new ErrorResponse("invalid_pagination", "Parámetros de paginación inválidos");
        }

        // Prepare pagination
        Pageable pageable = PageRequest.of(skip, limit, Sort.by(sortDirection, "issueDate"));
        String tokenUpdated = jwt.extendTokenExpiration(token);

        // Find business and user
        Business business = businessRepository.findByRuc(ruc)
                .orElseThrow(() -> new RuntimeException("Establecimiento no encontrado"));
        
        // Construcción de la especificación
        Specification<DocumentPayIssued> specification = Specification.where(
            DocumentsPayIssuedSpecifications.hasBusinessId(business.getId())).and(DocumentsPayIssuedSpecifications.isNotCancelled())
            .and(DocumentsPayIssuedSpecifications.opened());

        if(userId != null) {
            specification = specification.and(DocumentsPayIssuedSpecifications.hasUserId(userId));
        }

        // Add other filters as needed
        if(paymentMethodId != null) {
            specification = specification.and(DocumentsPayIssuedSpecifications.hasPaymentMethod(paymentMethodId));
        }

        List<Map<String,Object>> data = documentsPayIssuedRepository.findAll(specification, pageable).stream()
                .map(document -> {
                    
                    String fileName = String.format("%s-%s-%s-%s",
                                business.getRuc(),
                                ApiUtil.lzeros(document.getDocumentPaySerie().getDocumentPayType().getId(), 2),
                                document.getDocumentPaySerie().getNumbering(),
                                ApiUtil.lzeros(document.getNumbering(), 8));
                    
                    
                    
                    Map<String, Object> documentData = new LinkedHashMap<>();
                    documentData.put("id", document.getId());
                    documentData.put("issueDate", DateTimeUtil.convertUtcToLima(document.getIssueDate()));
                    documentData.put("customerFullName",document.getCustomer() != null ?  (
                        document.getCustomer().getPersonalDocumentType().getSunatId()==6 ? 
                            String.format("%s%s - %s",
                            document.getCustomer().getPersonalDocumentType().getDenominationShort().toUpperCase(),
                            document.getCustomer().getDocumentNumber(),
                            document.getCustomer().getRazonSocial()) : 
                            String.format("%s%s - %s %s",
                            document.getCustomer().getPersonalDocumentType().getDenominationShort().toUpperCase(),
                            document.getCustomer().getDocumentNumber(),
                            document.getCustomer().getLastnames(),
                            document.getCustomer().getSurnames())
                    ) : null);

                    documentData.put("fileName",fileName);
                    if(document.getDocumentPaySerie().getDocumentPayType().getId()==100){
                        documentData.put("pdfUrl",String.format("%s/documents/%s/getPDF/ticket58mm/%s.pdf",
                                    this.getFullPath(),
                                    KeyUtil.encrypt(document.getId().toString()),
                                    fileName
                        ));        
                    }else{
                        Map<String,String> metadata = ApiUtil.buildMetadata(document.getMetadata());
                        documentData.put("pdfUrl",String.format("https://apisunat.com/pdf/%s/58mm/%s.pdf", 
                                metadata.get("documentId"),
                                fileName
                        ));
                    } 
                    documentData.put("documentType", document.getDocumentPaySerie().getDocumentPayType().getDenomination().toUpperCase());
                    documentData.put("paymentMethod", document.getPaymentMethod().getDenominationEs().toUpperCase());
                    documentData.put("paymentMethodHexadecimalColor", document.getPaymentMethod().getColorHexadecimal());
                    documentData.put("totalAmount", document.getAmountTotal());
                    // Add other fields as needed
                    return documentData;
                })
                .collect(Collectors.toList()); 
        
        return new SuccessResponse(data, tokenUpdated);
    }
    private String getFullPath() {
        return host + apiBasePath;
    }
}
