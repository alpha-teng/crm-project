package com.crm.dto.request;

import lombok.Data;

@Data
public class CustomerRequest {
    
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
}
