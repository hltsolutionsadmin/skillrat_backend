package com.skillrat.usermanagement.services.impl;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillrat.usermanagement.model.CustomerLastLoginModel;
import com.skillrat.usermanagement.model.UserModel;
import com.skillrat.usermanagement.repository.CustomerLastLoginRepository;
import com.skillrat.usermanagement.services.CustomerLastLoginService;

@Service
public class CustomerLastLoginServiceImpl implements CustomerLastLoginService {

    @Autowired
    private CustomerLastLoginRepository customerLastLoginRepository;

    @Override
    @Transactional
    public CustomerLastLoginModel save(CustomerLastLoginModel customerLastLoginModel) {
        return customerLastLoginRepository.save(customerLastLoginModel);
    }

    @Override
    public CustomerLastLoginModel findByJtCustomer(UserModel userModel) {
        return customerLastLoginRepository.findByCustomer(userModel);
    }

}
