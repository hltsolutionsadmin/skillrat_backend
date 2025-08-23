package com.hlt.usermanagement.controllers;

import com.hlt.commonservice.dto.StandardResponse;
import com.hlt.commonservice.user.UserDetailsImpl;
import com.hlt.usermanagement.dto.AddressDTO;
import com.hlt.usermanagement.dto.response.ApiResponse;
import com.hlt.usermanagement.services.AddressService;
import com.hlt.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping("/save")
    public ResponseEntity<AddressDTO> saveOrUpdate(@RequestBody AddressDTO addressDTO) {
        AddressDTO savedAddress = addressService.saveOrUpdateAddress(addressDTO);
        return ResponseEntity.ok(savedAddress);
    }

    @GetMapping("/default")
    public ResponseEntity<StandardResponse<AddressDTO>> getDefaultAddress() {
        UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
        AddressDTO addressDTO = addressService.getDefaultAddress(loggedInUser.getId());
        return ResponseEntity.ok(StandardResponse.single("Default address retrieved successfully", addressDTO));
    }
    @PostMapping("/setdefaultAddress/{addressId}")
    public ResponseEntity<StandardResponse<AddressDTO>> setDefaultAddress(@PathVariable Long addressId) {
        UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
        AddressDTO addressDTO = addressService.setDefaultAddress(loggedInUser.getId(), addressId);
        return ResponseEntity.ok(StandardResponse.single("Default address set successfully.", addressDTO));
    }
    //using feign
    @GetMapping("/{id}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long id) {
        AddressDTO addressDTO = addressService.getAddressById(id);
        return ResponseEntity.ok(addressDTO);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAddress(@PathVariable Long id) {
        try {
            addressService.deleteAddressById(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Address deleted successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.failure("Failed to delete address with id: " + id + ". " + e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<AddressDTO>>> getAllAddresses(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sort) {

        UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
        Pageable pageable = PageRequest.of(page, size);
        Page<AddressDTO> addresses = addressService.getAllAddresses(loggedInUser.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(addresses, "Addresses fetched successfully."));
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<ApiResponse<Page<AddressDTO>>> getAddressesByBusinessId(
            @PathVariable Long businessId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AddressDTO> addresses = addressService.getAddressesByBusinessId(businessId, pageable);
        return ResponseEntity.ok(ApiResponse.success(addresses, "Addresses for business ID " + businessId + " fetched successfully."));
    }
}
