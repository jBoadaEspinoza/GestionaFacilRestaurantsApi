package com.api.GestionaFacilRestaurants.services;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.GestionaFacilRestaurants.models.Person;
import com.api.GestionaFacilRestaurants.models.PersonalDocumentType;
import com.api.GestionaFacilRestaurants.models.PostalCode;
import com.api.GestionaFacilRestaurants.repositories.DniRepository;
import com.api.GestionaFacilRestaurants.repositories.PersonRepository;
import com.api.GestionaFacilRestaurants.repositories.PersonalDocumentTypeRepository;
import com.api.GestionaFacilRestaurants.repositories.RucRepository;
import com.api.GestionaFacilRestaurants.responses.ErrorResponse;
import com.api.GestionaFacilRestaurants.responses.SuccessResponse;
import com.api.GestionaFacilRestaurants.utilities.JwtUtil;

@Service
public class PersonService {

    @Autowired
    private JwtUtil jwt;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonalDocumentTypeRepository personalDocumentTypeRepository;

    @Autowired
    private DniRepository dniRepository;

    @Autowired
    private RucRepository rucRepository;

    public Object get(String token, String documentType, String documentNumber) {
        String tokenUpdated = jwt.extendTokenExpiration(token);

        Optional<PersonalDocumentType> personalDocumentTypeOpt = personalDocumentTypeRepository.findByDenominationShort(documentType);
        if (personalDocumentTypeOpt.isEmpty()) {
            return new ErrorResponse("personal_document_type_not_found", "Documento personal no encontrado");
        }
        PersonalDocumentType personalDocumentType = personalDocumentTypeOpt.get();

        Person person = personRepository.findByDocumentNumberAndPersonalDocumentTypeDenominationShort(
                documentNumber, personalDocumentType.getDenominationShort()).orElse(null);

        if (person != null) {
            return new SuccessResponse(person, tokenUpdated);
        }

        switch (personalDocumentType.getDenominationShort().toLowerCase()) {
            case "dni":
                return handleDni(documentNumber, personalDocumentType ,tokenUpdated);
            case "ruc":
                return handleRuc(documentNumber, personalDocumentType , tokenUpdated);
            default:
                return new ErrorResponse("unsupported_document_type", "Tipo de documento no soportado");
        }
    }

    private Object handleDni(String documentNumber, PersonalDocumentType documentType, String tokenUpdated) {
        Map<String, Object> mapDni = dniRepository.findPersonByDni(documentNumber);

        if (!isSuccessResponse(mapDni)) {
            return new ErrorResponse("person_not_found", "Persona no encontrada en el registro de DNIs");
        }

        Map<String, Object> data = getDataSafely(mapDni);

        Person person = Person.builder()
                .personalDocumentType(documentType)
                .documentNumber(documentNumber)
                .surnames(Objects.toString(data.get("nombres"), "").toUpperCase())
                .lastnames(formatLastnames(data.get("apellido_paterno"), data.get("apellido_materno")))
                .postalCode(PostalCode.builder().id(73L).build())
                .birthCountryId(81L)
                .residenceCountryId(81L)
                .build();

        return new SuccessResponse(personRepository.save(person), tokenUpdated);
    }

    private Object handleRuc(String documentNumber, PersonalDocumentType documentType, String tokenUpdated) {
        Map<String, Object> mapRuc = rucRepository.findPersonbyRuc(documentNumber);

        if (!isSuccessResponse(mapRuc)) {
            return new ErrorResponse("person_not_found", "Persona no encontrada en el registro de RUCs");
        }

        Map<String, Object> data = getDataSafely(mapRuc);

        Person person = Person.builder()
                .personalDocumentType(documentType)
                .documentNumber(documentNumber)
                .surnames("")
                .lastnames("")
                .razonSocial(Objects.toString(data.get("nombre_o_razon_social"), "").toUpperCase())
                .address(Objects.toString(data.get("direccion_completa"), "").toUpperCase())
                .postalCode(PostalCode.builder().id(73L).build())
                .build();

        return new SuccessResponse(personRepository.save(person), tokenUpdated);
    }

    private boolean isSuccessResponse(Map<String, Object> response) {
        return response != null && Boolean.TRUE.equals(response.get("success"));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getDataSafely(Map<String, Object> response) {
        Object data = response.get("data");
        if (data instanceof Map) {
            return (Map<String, Object>) data;
        }
        throw new IllegalStateException("Formato de datos inesperado en la respuesta del API");
    }

    private String formatLastnames(Object apellidoPaterno, Object apellidoMaterno) {
        return (apellidoPaterno != null ? apellidoPaterno.toString().toUpperCase() : "") + " " +
                (apellidoMaterno != null ? apellidoMaterno.toString().toUpperCase() : "");
    }
}
