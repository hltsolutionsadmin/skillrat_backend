package com.skillrat.usermanagement.services;

import java.util.List;

import com.skillrat.usermanagement.dto.request.BusinessCategoryRequest;
import com.skillrat.usermanagement.model.BusinessCategoryModel;

public interface BusinessCategoryService {
    BusinessCategoryModel createCategory(BusinessCategoryRequest request);
    List<BusinessCategoryModel> listAll();
    BusinessCategoryModel getById(Long id);
    void deleteById(Long id);
    List<BusinessCategoryModel> searchByName(String keyword);

    public BusinessCategoryModel getByName(String name);

}
