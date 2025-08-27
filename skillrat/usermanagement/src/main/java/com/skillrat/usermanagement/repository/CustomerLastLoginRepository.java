package com.skillrat.usermanagement.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillrat.usermanagement.model.CustomerLastLoginModel;
import com.skillrat.usermanagement.model.UserModel;

@Repository
public interface CustomerLastLoginRepository extends JpaRepository<CustomerLastLoginModel, Long> {

    CustomerLastLoginModel findByCustomer(UserModel userModel);
}
