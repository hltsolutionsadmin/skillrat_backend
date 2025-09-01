package com.skillrat.usermanagement.services.impl;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.usermanagement.dto.request.BusinessCategoryRequest;
import com.skillrat.usermanagement.model.BusinessCategoryModel;
import com.skillrat.usermanagement.repository.BusinessCategoryRepository;
import com.skillrat.usermanagement.services.BusinessCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // ✅ Lombok generates constructor for final fields
public class BusinessCategoryServiceImpl implements BusinessCategoryService {

    private final BusinessCategoryRepository repository; // injected by constructor

    @Override
    public BusinessCategoryModel createCategory(BusinessCategoryRequest request) {
        if (request == null || request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name must not be empty");
        }

        String name = request.getName().trim();

        if (repository.existsByName(name)) {
            throw new HltCustomerException(ErrorCode.ALREADY_EXISTS);
        }

        BusinessCategoryModel category = new BusinessCategoryModel();
        category.setName(name);

        return repository.save(category);
    }

    @Override
    public List<BusinessCategoryModel> listAll() {
        return repository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new HltCustomerException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        repository.deleteById(id);
    }

    @Override
    public BusinessCategoryModel getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    @Override
    public List<BusinessCategoryModel> searchByName(String keyword) {
        return repository.findByNameContainingIgnoreCase(keyword);
    }

    @Override
    public BusinessCategoryModel getByName(String name) {
        return repository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.CATEGORY_NOT_FOUND));
    }
}
