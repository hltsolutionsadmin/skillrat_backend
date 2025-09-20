package com.skillrat.usermanagement.services;


import com.skillrat.usermanagement.model.CustomerLastLoginModel;
import com.skillrat.usermanagement.model.UserModel;

public interface CustomerLastLoginService {

    CustomerLastLoginModel save(CustomerLastLoginModel customerLastLoginModel);

    CustomerLastLoginModel findByJtCustomer(UserModel userModel);
}
