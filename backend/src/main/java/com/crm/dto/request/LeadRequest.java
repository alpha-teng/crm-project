package com.crm.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LeadRequest {
    
    private Long customerId;
    
    private String source;
    
    private String status;
    
    private Integer score;
    
    private String remark;
}
