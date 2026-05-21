package com.crm.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DealRequest {
    
    private Long customerId;
    
    private Long opportunityId;
    
    private String title;
    
    private BigDecimal amount;
    
    private LocalDate dealDate;
    
    private String status;
    
    private String remark;
}
