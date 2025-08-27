package com.skillrat.usermanagement.populator;

import com.skillrat.usermanagement.dto.response.BusinessAttributeResponse;
import com.skillrat.usermanagement.model.BusinessAttributeModel;
import com.skillrat.utils.Populator;

import org.springframework.stereotype.Component;

@Component
public class BusinessAttributePopulator implements Populator<BusinessAttributeModel, BusinessAttributeResponse> {

    @Override
    public void populate(BusinessAttributeModel source, BusinessAttributeResponse target) {
        if (source == null || target == null) {
            return;
        }
        target.setId(source.getId());
        target.setAttributeName(source.getAttributeName());
        target.setAttributeValue(source.getAttributeValue());
    }
}
