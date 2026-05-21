package com.crm.dto.response;

import com.crm.entity.Opportunity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class OpportunityResponse {
    
    private Long id;
    
    private Long customerId;
    
    private String title;
    
    private BigDecimal amount;
    
    private String stage;
    
    private Integer probability;
    
    private LocalDate expectedCloseDate;
    
    private String remark;
    
    private LocalDateTime createdAt;
    
    public static OpportunityResponse fromEntity(Opportunity opportunity) {
        if (opportunity == null) {
            return null;
        }
        
        OpportunityResponse response = new OpportunityResponse();
        response.setId(opportunity.getId());
        response.setCustomerId(opportunity.getCustomerId());
        response.setTitle(opportunity.getTitle());
        response.setAmount(opportunity.getAmount());
        response.setStage(opportunity.getStage());
        response.setProbability(opportunity.getProbability());
        response.setExpectedCloseDate(opportunity.getExpectedCloseDate());
        response.setRemark(opportunity.getRemark());
        response.setCreatedAt(opportunity.getCreatedAt());
        return response;
    }
}
