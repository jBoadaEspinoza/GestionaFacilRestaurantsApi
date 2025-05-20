package com.api.GestionaFacilRestaurants.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.api.GestionaFacilRestaurants.models.Business;
import com.api.GestionaFacilRestaurants.models.CashRegister;
import com.api.GestionaFacilRestaurants.models.CashRegisterView;
import com.api.GestionaFacilRestaurants.repositories.BusinessRepository;
import com.api.GestionaFacilRestaurants.repositories.CashRegisterRepository;
import com.api.GestionaFacilRestaurants.repositories.CashRegisterViewRepository;
import com.api.GestionaFacilRestaurants.requests.CashRegisterRequest;
import com.api.GestionaFacilRestaurants.responses.CashRegisterResponse;
import com.api.GestionaFacilRestaurants.responses.SuccessResponse;
import com.api.GestionaFacilRestaurants.utilities.DateTimeUtil;
import com.api.GestionaFacilRestaurants.utilities.JwtUtil;

@Service
public class CashRegisterService {

    @Autowired
    private JwtUtil jwt;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private CashRegisterRepository cashRegisterRepository;

    @Autowired
    private CashRegisterViewRepository cashRegisterViewRepository;

    public Object get(final String token, final String denomination, final int skip, final int limit, final Sort.Direction sortDirection) {
        final Long ruc = jwt.extractBusinessRuc(token);
        final String tokenUpdated = jwt.extendTokenExpiration(token);

        final Business business = businessRepository.findByRuc(ruc)
            .orElseThrow(() -> new IllegalArgumentException("Establecimiento no encontrado"));

        final Pageable pageable = PageRequest.of(skip, limit, Sort.by(sortDirection, "denomination"));
        final List<CashRegisterView> cashRegisterViews = (denomination == null || denomination.isEmpty()) ?
            cashRegisterViewRepository.findAllByBusinessId(business.getId(), pageable).getContent() :
            cashRegisterViewRepository.findAllByBusinessIdAndDenominationContainingIgnoreCase(business.getId(), denomination, pageable).getContent();

        final List<CashRegisterResponse> data = cashRegisterViews.stream()
            .map(this::mapToCashRegisterResponse)
            .collect(Collectors.toList());

        return new SuccessResponse(data, tokenUpdated);
    }

    public Object create(String token, CashRegisterRequest cashRegisterRequest) {
        final Long ruc = jwt.extractBusinessRuc(token);
        final String tokenUpdated = jwt.extendTokenExpiration(token);

        final Business business = businessRepository.findByRuc(ruc)
            .orElseThrow(() -> new IllegalArgumentException("Establecimiento no encontrado"));

        CashRegister cashRegister = (cashRegisterRequest.getId() == null) ?
            createNewCashRegister(cashRegisterRequest, business.getId()) :
            updateExistingCashRegister(cashRegisterRequest);

        CashRegister cashRegisterInserted = cashRegisterRepository.save(cashRegister);
        CashRegisterView cashRegisterView = cashRegisterViewRepository.findById(cashRegisterInserted.getId())
            .orElseThrow(() -> new IllegalArgumentException("Caja no encontrada"));
        CashRegisterResponse data = CashRegisterResponse.builder()
            .id(cashRegisterView.getId())
            .denomination(cashRegisterView.getDenomination())
            .opening(cashRegisterView.isOpening())
            .accumulatedAmount(cashRegisterView.getAccumulatedAmount())
            .active(cashRegisterView.isActive())
            .build();
        return new SuccessResponse(data, tokenUpdated, "Caja registrada exitosamente");
    }

    private CashRegister createNewCashRegister(final CashRegisterRequest request, final Long businessId) {
        return CashRegister.builder()
            .denomination(request.getDenomination())
            .creationDate(DateTimeUtil.getFormattedCurrentUtcDateTime())
            .active(true)
            .businessId(businessId)
            .build();
    }

    private CashRegister updateExistingCashRegister(final CashRegisterRequest request) {
        return cashRegisterRepository.findById(request.getId())
            .map(cashRegister -> {
                cashRegister.setDenomination(request.getDenomination());
                return cashRegister;
            })
            .orElseThrow(() -> new IllegalArgumentException("Caja no encontrada"));
    }

    private CashRegisterResponse mapToCashRegisterResponse(final CashRegisterView cashRegisterView) {
        return CashRegisterResponse.builder()
            .id(cashRegisterView.getId())
            .denomination(cashRegisterView.getDenomination())
            .opening(cashRegisterView.isOpening())
            .accumulatedAmount(cashRegisterView.getAccumulatedAmount())
            .active(cashRegisterView.isActive())
            .build();
    }
}
