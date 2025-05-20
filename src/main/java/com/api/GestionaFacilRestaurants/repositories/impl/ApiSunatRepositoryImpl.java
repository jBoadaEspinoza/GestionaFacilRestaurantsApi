package com.api.GestionaFacilRestaurants.repositories.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.api.GestionaFacilRestaurants.models.Business;
import com.api.GestionaFacilRestaurants.models.DocumentPaySerie;
import com.api.GestionaFacilRestaurants.models.Order;
import com.api.GestionaFacilRestaurants.models.OrderDetail;
import com.api.GestionaFacilRestaurants.repositories.ApiSunatRepository;
import com.api.GestionaFacilRestaurants.utilities.ApiUtil;
import com.api.GestionaFacilRestaurants.utilities.DateTimeUtil;

@Repository
public class ApiSunatRepositoryImpl implements ApiSunatRepository {
    private static final String API_URL = "https://back.apisunat.com/";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String findLastNumberingFromInvoice(DocumentPaySerie serie,Business business) {
        String url = API_URL+"personas/lastDocument";
        // Configurar encabezados
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Content-Type", "application/json");

        String typeId = ApiUtil.lzeros(serie.getDocumentPayType().getId(), 2);
        // Crear el cuerpo de la solicitud
        Map<String, String> body = new HashMap<>();
        body.put("personaId", business.getApiSunatPersonaId());
        body.put("personaToken",business.getApiSunatPersonaToken());
        body.put("type",typeId);
        body.put("serie",serie.getNumbering());

        // Crear la entidad de la solicitud (headers + body)
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

         // Hacer la solicitud POST y obtener la respuesta como Object
        ResponseEntity<Object> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                Object.class
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = (Map<String,Object>) response.getBody();
        @SuppressWarnings("null")
        String suggestedNumber = (String) responseMap.get("suggestedNumber");
        return suggestedNumber;
    }

    @Override
    public Object sendBill(DocumentPaySerie serie, Business business,Order order,List<OrderDetail> details, Long correlativeNumber,Double tipAmount,String note,String issueDate){
       
        String url = API_URL+"personas/v1/sendBill";
        String currency = "PEN";
        
        // Validar el número correlativo
        String correlativeNumber_str = ApiUtil.lzeros(correlativeNumber, 8); 
        String typeId = ApiUtil.lzeros(serie.getDocumentPayType().getId(), 2); //01 FACTURA 03 BOLETA
        
        // Configurar encabezados
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Content-Type", "application/json");

         // Crear el cuerpo de la solicitud
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("personaId", business.getApiSunatPersonaId());
        body.put("personaToken",business.getApiSunatPersonaToken());
        body.put("fileName",buildFileName(serie,business,typeId,correlativeNumber_str));
        body.put("documentBody",buildDocumentBody(serie,business,order,details,correlativeNumber_str,currency,tipAmount,note,issueDate));
        
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, Object.class
            );

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar el comprobante a la API de Sunat", e);
        }
    }

    private String buildFileName(DocumentPaySerie serie,Business business,String typeId,String correlativeNumber){
        return business.getRuc().toString()+"-"+typeId+"-"+serie.getNumbering()+"-"+correlativeNumber;
    }

    private Map<String,Object> buildDocumentBody(DocumentPaySerie serie,Business business,Order order,List<OrderDetail> details,String correlativeNumber,String currency,Double tipAmount,String note,String issueDate){
        int percent = !business.isNrus() ? 18 : 0; // Si no es NRUS, el IGV es 18%, si es NRUS, el IGV es 0%
        double percentTo18 = percent / 100.0;
        double percentTo118 = (100 + percent) / 100.0;
        Map<String, Object> documentBody = new LinkedHashMap<>();

        // cbc:UBLVersionID
        documentBody.put("cbc:UBLVersionID", Map.of("_text", "2.1"));

        // cbc:CustomizationID
        documentBody.put("cbc:CustomizationID", Map.of("_text", "2.0"));

        // cbc:ID
        documentBody.put("cbc:ID", Map.of("_text", serie.getNumbering()+ "-" + correlativeNumber));
        if(issueDate==null){
            documentBody.put("cbc:IssueDate", Map.of("_text", DateTimeUtil.getDateTodayInLima()));
        }else{
            if(ApiUtil.getDaysSinceDate(issueDate)<=3){
                documentBody.put("cbc:IssueDate", Map.of("_text", issueDate));
             }
        }

        if(issueDate==null || ApiUtil.getDaysSinceDate(issueDate)==0){
           documentBody.put("cbc:IssueTime", Map.of("_text", DateTimeUtil.getTimeTodayInLima()));
        }
        // cbc:InvoiceTypeCode with _attributes
        Map<String, Object> invoiceTypeCode = new LinkedHashMap<>();
        invoiceTypeCode.put("_attributes", Map.of("listID", !business.isNrus() ? "0101" : "0113"));
        invoiceTypeCode.put("_text", ApiUtil.lzeros(serie.getDocumentPayType().getId(), 2));
        documentBody.put("cbc:InvoiceTypeCode", invoiceTypeCode);

        // cbc:DocumentCurrencyCode
        documentBody.put("cbc:DocumentCurrencyCode", Map.of("_text", currency));

        // cac:AccountingSupplierParty
        Map<String, Object> accountingSupplierParty = new LinkedHashMap<>();
        Map<String, Object> supplierParty = new LinkedHashMap<>();

        // cac:PartyIdentification
        Map<String, Object> supplierPartyIdentification = Map.of(
                "cbc:ID", Map.of(
                        "_attributes", Map.of("schemeID", "6"),
                        "_text", business.getRuc().toString()
                )
        );
        supplierParty.put("cac:PartyIdentification", supplierPartyIdentification);

        // cac:PartyName
        Map<String, Object> partyName = new LinkedHashMap<>();
        partyName.put("cbc:Name", Map.of("_text", business.getName().toUpperCase()));
        supplierParty.put("cac:PartyName", partyName);

        // cac:PartyLegalEntity
        Map<String, Object> supplierPartyLegalEntity = new LinkedHashMap<>();
        supplierPartyLegalEntity.put("cbc:RegistrationName", Map.of("_text", business.getRazonSocial()));

        // cac:RegistrationAddress
        Map<String, Object> registrationAddress = new LinkedHashMap<>();
        registrationAddress.put("cbc:AddressTypeCode", Map.of("_text", "0000"));
        registrationAddress.put("cac:AddressLine", Map.of(
                "cbc:Line", Map.of("_text", business.getAddress())
        ));
        supplierPartyLegalEntity.put("cac:RegistrationAddress", registrationAddress);

        supplierParty.put("cac:PartyLegalEntity", supplierPartyLegalEntity);
        accountingSupplierParty.put("cac:Party", supplierParty);
        documentBody.put("cac:AccountingSupplierParty", accountingSupplierParty);

        // cac:AccountingCustomerParty
        Map<String, Object> accountingCustomerParty = new LinkedHashMap<>();
        Map<String, Object> customerParty = new LinkedHashMap<>();

        // cac:PartyIdentification
        String customerSchemeID = order.getCustomer() != null ? order.getCustomer().getPersonalDocumentType().getSunatId().toString() : "1";
        String customerDocumentNumber = order.getCustomer() != null ? order.getCustomer().getDocumentNumber().toString() : "00000000";
        Map<String, Object> customerPartyIdentification = Map.of(
                "cbc:ID", Map.of(
                        "_attributes", Map.of("schemeID", customerSchemeID),
                        "_text", customerDocumentNumber
                )
        );
        customerParty.put("cac:PartyIdentification", customerPartyIdentification);

        // cac:PartyLegalEntity
        String customerName = order.getCustomer() != null 
        ? (order.getCustomer().getPersonalDocumentType().getSunatId()!=6
        ? order.getCustomer().getSurnames() + " " + order.getCustomer().getLastnames() 
        : order.getCustomer().getRazonSocial()) 
        : "---";

        // Usamos LinkedHashMap para permitir modificaciones y mantener orden
        Map<String, Object> customerPartyLegalEntity = new LinkedHashMap<>();
        customerPartyLegalEntity.put("cbc:RegistrationName", Map.of("_text", customerName));

        // Verificamos que el cliente y su dirección no sean nulos
        if (order.getCustomer() != null && order.getCustomer().getAddress() != null) {
                Map<String, Object> customerRegistrationAddress = new LinkedHashMap<>();
                customerRegistrationAddress.put("cac:AddressLine", 
                Map.of("cbc:Line", Map.of("_text", order.getCustomer().getAddress())));

                customerPartyLegalEntity.put("cac:RegistrationAddress", customerRegistrationAddress);
        }

        customerParty.put("cac:PartyLegalEntity", customerPartyLegalEntity);
        accountingCustomerParty.put("cac:Party", customerParty);
        documentBody.put("cac:AccountingCustomerParty", accountingCustomerParty);

        // cac:TaxTotal
        Map<String, Object> taxTotal = new LinkedHashMap<>();
        double totalAmount = details.stream().mapToDouble(d -> d.getQuantity() * d.getUnitPricePen()).sum();
        taxTotal.put("cbc:TaxAmount", Map.of(
                "_attributes", Map.of("currencyID", currency),
                "_text", ApiUtil.toFixedDecimal(totalAmount/percentTo118*percentTo18, 2, true)
        ));

        // cac:TaxSubtotal
        Map<String, Object> taxSubtotal = new LinkedHashMap<>();
        taxSubtotal.put("cbc:TaxableAmount", Map.of(
                "_attributes", Map.of("currencyID", currency),
                "_text", ApiUtil.toFixedDecimal(totalAmount/percentTo118, 2, true)
        ));
        taxSubtotal.put("cbc:TaxAmount", Map.of(
                "_attributes", Map.of("currencyID", currency),
                "_text", ApiUtil.toFixedDecimal(totalAmount/percentTo118*percentTo18, 2, true)
        ));

        // cac:TaxCategory
        Map<String, Object> taxCategory = new LinkedHashMap<>();
        Map<String, Object> taxScheme = new LinkedHashMap<>();
                taxScheme.put("cbc:ID", Map.of("_text", "1000"));
                taxScheme.put("cbc:Name", Map.of("_text", "IGV"));
                taxScheme.put("cbc:TaxTypeCode", Map.of("_text", "VAT"));
        taxCategory.put("cac:TaxScheme", taxScheme);
        taxSubtotal.put("cac:TaxCategory", taxCategory);

        taxTotal.put("cac:TaxSubtotal", List.of(taxSubtotal));
        documentBody.put("cac:TaxTotal", taxTotal);

        // cac:AllowanceCharge
        List<Map<String, Object>> allowanceCharges = new ArrayList<>();

        Map<String, Object> allowanceCharge = new LinkedHashMap<>();
        allowanceCharge.put("cbc:ChargeIndicator", new LinkedHashMap<String, Object>() {{
                put("_text", "true");
        }});
        allowanceCharge.put("cbc:AllowanceChargeReasonCode", new LinkedHashMap<String, Object>() {{
                put("_text", "46");
        }});
        allowanceCharge.put("cbc:Amount", new LinkedHashMap<String, Object>() {{
                put("_attributes", new LinkedHashMap<String, Object>() {{
                put("currencyID", "PEN");
        }});
        put("_text", ApiUtil.toFixedDecimal(tipAmount, 2, true));
        }});
        allowanceCharge.put("cbc:BaseAmount", new LinkedHashMap<String, Object>() {{
                put("_attributes", new LinkedHashMap<String, Object>() {{
                put("currencyID", "PEN");
        }});

        put("_text", ApiUtil.toFixedDecimal(totalAmount, 2, true));
        }});
        allowanceCharges.add(allowanceCharge);
        documentBody.put("cac:AllowanceCharge", allowanceCharges);

        // cac:LegalMonetaryTotal
        Map<String, Object> legalMonetaryTotal = new LinkedHashMap<>();
        legalMonetaryTotal.put("cbc:LineExtensionAmount", Map.of(
                "_attributes", Map.of("currencyID", currency),
                "_text", ApiUtil.toFixedDecimal(totalAmount/percentTo118, 2, true)
        ));
        legalMonetaryTotal.put("cbc:TaxInclusiveAmount", Map.of(
                "_attributes", Map.of("currencyID", currency),
                "_text", ApiUtil.toFixedDecimal(totalAmount, 2, true)
        ));
        legalMonetaryTotal.put("cbc:ChargeTotalAmount", Map.of(
                "_attributes", Map.of("currencyID", currency),
                "_text", ApiUtil.toFixedDecimal(tipAmount, 2, true)
        ));
        legalMonetaryTotal.put("cbc:PayableAmount", Map.of(
                "_attributes", Map.of("currencyID", currency),
                "_text", ApiUtil.toFixedDecimal(totalAmount+tipAmount, 2, true)
        ));
        documentBody.put("cac:LegalMonetaryTotal", legalMonetaryTotal);

        if(serie.getDocumentPayType().getId()==1){
                // cac:PaymentTerms
                Map<String, Object> paymentTerms = new LinkedHashMap<>();
                paymentTerms.put("cbc:ID", Map.of("_text", "FormaPago"));
                paymentTerms.put("cbc:PaymentMeansID", Map.of("_text", "Contado"));
                documentBody.put("cac:PaymentTerms", paymentTerms);
        }
        // cac:InvoiceLine
        List<Map<String, Object>> invoiceLines = new ArrayList<>();
        int lineNumber = 1;
        for (OrderDetail detail : details) {
            Map<String, Object> invoiceLine = new LinkedHashMap<>();

            // cbc:ID
            invoiceLine.put("cbc:ID", Map.of("_text", lineNumber));

            // cbc:InvoicedQuantity
            invoiceLine.put("cbc:InvoicedQuantity", Map.of(
                    "_attributes", Map.of("unitCode", "NIU"),
                    "_text", detail.getQuantity()
            ));

            // cbc:LineExtensionAmount
            double lineExtensionAmount = detail.getQuantity() * detail.getUnitPricePen();
            invoiceLine.put("cbc:LineExtensionAmount", Map.of(
                    "_attributes", Map.of("currencyID", currency),
                    "_text", ApiUtil.toFixedDecimal(lineExtensionAmount/percentTo118, 2, true)
            ));

            // cac:PricingReference
            Map<String, Object> pricingReference = new LinkedHashMap<>();
            Map<String, Object> alternativeConditionPrice = new LinkedHashMap<>();
            alternativeConditionPrice.put("cbc:PriceAmount", Map.of(
                    "_attributes", Map.of("currencyID", currency),
                    "_text", detail.getUnitPricePen()
            ));
            alternativeConditionPrice.put("cbc:PriceTypeCode", Map.of("_text", "01"));
            pricingReference.put("cac:AlternativeConditionPrice", alternativeConditionPrice);
            invoiceLine.put("cac:PricingReference", pricingReference);

            // cac:TaxTotal
            Map<String, Object> lineTaxTotal = new LinkedHashMap<>();
            lineTaxTotal.put("cbc:TaxAmount", Map.of(
                    "_attributes", Map.of("currencyID", currency),
                    "_text", ApiUtil.toFixedDecimal(lineExtensionAmount/percentTo118*percentTo18,2,true)
            ));

            // cac:TaxSubtotal
            Map<String, Object> lineTaxSubtotal = new LinkedHashMap<>();
            lineTaxSubtotal.put("cbc:TaxableAmount", Map.of(
                    "_attributes", Map.of("currencyID", currency),
                    "_text", ApiUtil.toFixedDecimal(lineExtensionAmount/percentTo118, 2, true)
            ));
            lineTaxSubtotal.put("cbc:TaxAmount", Map.of(
                    "_attributes", Map.of("currencyID", currency),
                    "_text", ApiUtil.toFixedDecimal(lineExtensionAmount/percentTo118*percentTo18, 2, true)
            ));

            // cac:TaxCategory
            Map<String, Object> lineTaxCategory = new LinkedHashMap<>();
                lineTaxCategory.put("cbc:Percent", Map.of("_text", percent));
                lineTaxCategory.put("cbc:TaxExemptionReasonCode", Map.of("_text", "10"));
                lineTaxCategory.put("cac:TaxScheme", taxScheme);
            lineTaxSubtotal.put("cac:TaxCategory", lineTaxCategory);

            lineTaxTotal.put("cac:TaxSubtotal", List.of(lineTaxSubtotal));
            invoiceLine.put("cac:TaxTotal", lineTaxTotal);

            // cac:Item
            invoiceLine.put("cac:Item", Map.of(
                    "cbc:Description", Map.of("_text", ApiUtil.capitalizeEachWord(detail.getMenuItem().getDenomination()+" - "+detail.getMenuItem().getPresentation().getDenomination()))
            ));

            // cac:Price
            invoiceLine.put("cac:Price", Map.of(
                    "cbc:PriceAmount", Map.of(
                            "_attributes", Map.of("currencyID", currency),
                            "_text", ApiUtil.toFixedDecimal(detail.getUnitPricePen()/percentTo118,10,true)
                    )
            ));

            invoiceLines.add(invoiceLine);
            lineNumber++;
        }
        documentBody.put("cac:InvoiceLine", invoiceLines);

        return documentBody;
    }
}
