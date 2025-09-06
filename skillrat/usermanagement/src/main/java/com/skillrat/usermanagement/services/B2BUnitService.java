package com.skillrat.usermanagement.services;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.skillrat.usermanagement.dto.AddressDTO;
import com.skillrat.usermanagement.dto.B2BUnitDTO;
import com.skillrat.usermanagement.dto.B2BUnitStatusDTO;
import com.skillrat.usermanagement.dto.request.B2BUnitRequest;
import com.skillrat.usermanagement.dto.response.B2BUnitListResponse;
import com.skillrat.usermanagement.model.B2BUnitModel;

import java.io.IOException;
import java.util.List;

public interface B2BUnitService {

    B2BUnitDTO createOrUpdate(B2BUnitRequest request) throws IOException;

    Page<B2BUnitListResponse> listAllPaginated(int page, int size);

    B2BUnitDTO getById(Long id);

    List<B2BUnitStatusDTO> getBusinessNameAndApprovalStatusForLoggedInUser();

    Page<B2BUnitDTO> searchByCityAndCategory(String city, String categoryName, String searchTerm, Pageable pageable);

    AddressDTO getAddressByB2BUnitId(Long unitId);

    B2BUnitDTO approveBusiness(Long businessId, Long adminUserId);

    Page<B2BUnitListResponse> listUnapprovedBusinesses(int page, int size);

}
