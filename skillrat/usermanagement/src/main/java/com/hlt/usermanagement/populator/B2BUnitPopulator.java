package com.hlt.usermanagement.populator;

import com.hlt.usermanagement.dto.AddressDTO;
import com.hlt.usermanagement.dto.B2BUnitDTO;
import com.hlt.usermanagement.dto.UserDTO;
import com.hlt.usermanagement.dto.response.BusinessAttributeResponse;
import com.hlt.usermanagement.model.B2BUnitModel;
import com.hlt.utils.Populator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class B2BUnitPopulator implements Populator<B2BUnitModel, B2BUnitDTO> {

	@Autowired
	private UserPopulator userPopulator;

	@Autowired
	private AddressPopulator addressPopulator;

	@Autowired
	private BusinessAttributePopulator attributePopulator;

	/**
	 * Converts a model to DTO
	 */
	public B2BUnitDTO toDTO(B2BUnitModel source) {
		if (source == null) return null;
		B2BUnitDTO dto = new B2BUnitDTO();
		populate(source, dto);
		return dto;
	}

	@Override
	public void populate(B2BUnitModel source, B2BUnitDTO target) {
		if (source == null || target == null) return;

		// Basic fields
		target.setId(source.getId());
		target.setBusinessName(source.getBusinessName());
		target.setContactNumber(source.getContactNumber());
		target.setBusinessCode(source.getBusinessCode());
		target.setEmail(source.getEmail());
		target.setEnabled(source.isEnabled());
		target.setTemporarilyClosed(source.getIsTemporarilyClosed() != null && source.getIsTemporarilyClosed());
		target.setBusinessLatitude(source.getBusinessLatitude());
		target.setBusinessLongitude(source.getBusinessLongitude());
		target.setCreationDate(source.getCreationDate());

		// Business type
		if (source.getType() != null) {
			target.setType(source.getType());
		}

		// Category
		if (source.getCategory() != null) {
			target.setCategoryId(source.getCategory().getId());
			target.setCategoryName(source.getCategory().getName());
		}

		// Owner mapping
		if (source.getAdmin() != null) {
			UserDTO userDTO = new UserDTO();
			userPopulator.populate(source.getAdmin(), userDTO);
			target.setAdmin(userDTO);
		}

		// Address mapping
		if (source.getBusinessAddress() != null) {
			AddressDTO addressDTO = new AddressDTO();
			addressPopulator.populate(source.getBusinessAddress(), addressDTO);
			target.setBusinessAddress(addressDTO);
		}

		// Attributes mapping
		Set<BusinessAttributeResponse> attributes = source.getAttributes() != null
				? source.getAttributes().stream()
				.map(attr -> {
					BusinessAttributeResponse dto = new BusinessAttributeResponse();
					attributePopulator.populate(attr, dto);
					return dto;
				})
				.collect(Collectors.toSet())
				: Collections.emptySet();
		target.setAttributes(attributes);

		// Media placeholders
		target.setMediaFiles(Collections.emptyList());
		target.setMediaUrls(Collections.emptyList());
		target.setMediaList(Collections.emptyList());
	}
}
