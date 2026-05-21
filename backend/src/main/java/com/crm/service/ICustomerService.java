package com.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.dto.request.CustomerRequest;
import com.crm.dto.response.CustomerResponse;
import com.crm.entity.Customer;

import java.util.List;

public interface ICustomerService extends IService<Customer> {
    
    List<CustomerResponse> getAllCustomers();
    
    CustomerResponse getCustomerById(Long id);
    
    CustomerResponse createCustomer(CustomerRequest request);
    
    CustomerResponse updateCustomer(Long id, CustomerRequest request);
    
    void deleteCustomer(Long id);
    
    List<CustomerResponse> searchCustomers(String keyword);
}
