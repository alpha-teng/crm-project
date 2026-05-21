package com.crm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.dto.request.CustomerRequest;
import com.crm.dto.response.CustomerResponse;
import com.crm.entity.Customer;
import com.crm.mapper.CustomerMapper;
import com.crm.service.ICustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements ICustomerService {
    
    @Override
    public List<CustomerResponse> getAllCustomers() {
        return list().stream()
                .map(CustomerResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = getById(id);
        if (customer == null) {
            throw new RuntimeException("Customer not found with id: " + id);
        }
        return CustomerResponse.fromEntity(customer);
    }
    
    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        Customer customer = new Customer();
        mapRequestToEntity(request, customer);
        
        save(customer);
        log.info("Created customer with id: {}", customer.getId());
        return CustomerResponse.fromEntity(customer);
    }
    
    @Override
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = getById(id);
        if (customer == null) {
            throw new RuntimeException("Customer not found with id: " + id);
        }
        
        mapRequestToEntity(request, customer);
        
        updateById(customer);
        log.info("Updated customer with id: {}", customer.getId());
        return CustomerResponse.fromEntity(customer);
    }
    
    @Override
    public void deleteCustomer(Long id) {
        if (!removeById(id)) {
            throw new RuntimeException("Customer not found with id: " + id);
        }
        log.info("Deleted customer with id: {}", id);
    }
    
    @Override
    public List<CustomerResponse> searchCustomers(String keyword) {
        return baseMapper.findByNameContainingIgnoreCaseOrCompanyContainingIgnoreCase(keyword, keyword).stream()
                .map(CustomerResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    private void mapRequestToEntity(CustomerRequest request, Customer entity) {
        entity.setName(request.getName());
        entity.setCompany(request.getCompany());
        entity.setPhone(request.getPhone());
        entity.setEmail(request.getEmail());
        entity.setAddress(request.getAddress());
        entity.setLongitude(request.getLongitude());
        entity.setLatitude(request.getLatitude());
        entity.setStatus(request.getStatus());
        entity.setSource(request.getSource());
        entity.setRemark(request.getRemark());
    }
}
