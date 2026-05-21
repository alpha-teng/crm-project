package com.crm.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OpportunityRequest {
    
    private Long customerId;
    
    private String title;
    
    private BigDecimal amount;
    
    private String stage;
    
    private Integer probability;
    
    private LocalDate expectedCloseDate;
    
    private String remark;
}
