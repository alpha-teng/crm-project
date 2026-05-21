package com.crm.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FollowUpRequest {
    
    private Long customerId;
    
    private String content;
    
    private String type;
    
    private LocalDate nextFollowUpDate;
}
