package com.crm.dto.response;

import com.crm.entity.Deal;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DealResponse {
    
    private Long id;
    
    private Long customerId;
    
    private Long opportunityId;
    
    private String title;
    
    private BigDecimal amount;
    
    private LocalDate contractDate;
    
    private String status;
    
    private String remark;
    
    private LocalDateTime createdAt;
    
    public static DealResponse fromEntity(Deal deal) {
        if (deal == null) {
            return null;
        }
        
        DealResponse response = new DealResponse();
        response.setId(deal.getId());
        response.setCustomerId(deal.getCustomerId());
        response.setOpportunityId(deal.getOpportunityId());
        response.setTitle(deal.getTitle());
        response.setAmount(deal.getAmount());
        response.setContractDate(deal.getContractDate());
        response.setStatus(deal.getStatus());
        response.setRemark(deal.getRemark());
        response.setCreatedAt(deal.getCreatedAt());
        return response;
    }
}
