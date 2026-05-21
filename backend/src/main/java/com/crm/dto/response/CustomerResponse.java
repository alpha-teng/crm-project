package com.crm.dto.response;

import com.crm.entity.Customer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerResponse {
    
    private Long id;
    
    private String name;
    
    private String company;
    
    private String phone;
    
    private String email;
    
    private String address;
    
    private Double longitude;
    
    private Double latitude;
    
    private String status;
    
    private String source;
    
    private String remark;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    public static CustomerResponse fromEntity(Customer customer) {
        if (customer == null) {
            return null;
        }
        
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setName(customer.getName());
        response.setCompany(customer.getCompany());
        response.setPhone(customer.getPhone());
        response.setEmail(customer.getEmail());
        response.setAddress(customer.getAddress());
        response.setLongitude(customer.getLongitude());
        response.setLatitude(customer.getLatitude());
        response.setStatus(customer.getStatus());
        response.setSource(customer.getSource());
        response.setRemark(customer.getRemark());
        response.setCreatedAt(customer.getCreatedAt());
        response.setUpdatedAt(customer.getUpdatedAt());
        return response;
    }
}
